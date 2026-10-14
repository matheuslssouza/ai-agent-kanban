#!/usr/bin/env node

/**
 * Kanban MCP Server (Model Context Protocol)
 * Connects AI tools (Claude, Cursor, Codex, Antigravity) to the local Spring Boot Kanban API.
 * Uses JSON-RPC 2.0 over standard I/O (stdio).
 */

const readline = require('readline');

const BASE_URL = process.env.KANBAN_API_URL || 'http://localhost:8085';

const TOOLS = [
  {
    name: 'create_task',
    description: 'Create a new task in the Kanban board (similar to a GitHub Project card)',
    inputSchema: {
      type: 'object',
      properties: {
        title: { type: 'string', description: 'Title of the task' },
        description: { type: 'string', description: 'Markdown description of the task' },
        priority: { type: 'string', enum: ['LOW', 'MEDIUM', 'HIGH', 'URGENT'], description: 'Priority level' },
        status: { type: 'string', enum: ['BACKLOG', 'TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'], description: 'Initial status' },
        labels: { type: 'array', items: { type: 'string' }, description: 'Tags/labels for the task' },
        assignee: { type: 'string', description: 'Assignee name or agent' }
      },
      required: ['title']
    }
  },
  {
    name: 'mark_task_done',
    description: 'Mark a task card as DONE and move it to the Done column',
    inputSchema: {
      type: 'object',
      properties: {
        taskId: { type: 'integer', description: 'The unique ID of the task to mark as done' }
      },
      required: ['taskId']
    }
  },
  {
    name: 'move_task',
    description: 'Move a task card to another column and position in the Kanban board',
    inputSchema: {
      type: 'object',
      properties: {
        taskId: { type: 'integer', description: 'The ID of the task to move' },
        targetStatus: { type: 'string', enum: ['BACKLOG', 'TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'], description: 'Target column status' },
        targetPosition: { type: 'integer', description: '0-based position in column' }
      },
      required: ['taskId', 'targetStatus']
    }
  },
  {
    name: 'list_tasks',
    description: 'List all tasks on the board, optionally filtered by status or search keyword',
    inputSchema: {
      type: 'object',
      properties: {
        status: { type: 'string', enum: ['BACKLOG', 'TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'] },
        query: { type: 'string', description: 'Search keyword' }
      }
    }
  },
  {
    name: 'get_board_state',
    description: 'Get full board hierarchy with all columns and current cards',
    inputSchema: {
      type: 'object',
      properties: {}
    }
  },
  {
    name: 'delete_task',
    description: 'Delete a task card by its ID',
    inputSchema: {
      type: 'object',
      properties: {
        taskId: { type: 'integer', description: 'ID of the task to delete' }
      },
      required: ['taskId']
    }
  }
];

async function handleToolCall(name, args) {
  try {
    switch (name) {
      case 'create_task': {
        const res = await fetch(`${BASE_URL}/api/tasks`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(args)
        });
        const data = await res.json();
        return {
          content: [{ type: 'text', text: `Created task #${data.id}: "${data.title}" [Status: ${data.status}]` }]
        };
      }

      case 'mark_task_done': {
        const res = await fetch(`${BASE_URL}/api/tasks/${args.taskId}/done`, {
          method: 'PATCH'
        });
        const data = await res.json();
        return {
          content: [{ type: 'text', text: `Task #${data.id} marked as DONE!` }]
        };
      }

      case 'move_task': {
        const res = await fetch(`${BASE_URL}/api/tasks/${args.taskId}/move`, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            targetStatus: args.targetStatus,
            targetPosition: args.targetPosition || 0
          })
        });
        const data = await res.json();
        return {
          content: [{ type: 'text', text: `Moved task #${data.id} to status ${data.status}` }]
        };
      }

      case 'list_tasks': {
        const params = new URLSearchParams();
        if (args.status) params.append('status', args.status);
        if (args.query) params.append('query', args.query);
        const res = await fetch(`${BASE_URL}/api/tasks?${params.toString()}`);
        const data = await res.json();
        return {
          content: [{ type: 'text', text: JSON.stringify(data, null, 2) }]
        };
      }

      case 'get_board_state': {
        const res = await fetch(`${BASE_URL}/api/boards/default`);
        const data = await res.json();
        return {
          content: [{ type: 'text', text: JSON.stringify(data, null, 2) }]
        };
      }

      case 'delete_task': {
        await fetch(`${BASE_URL}/api/tasks/${args.taskId}`, { method: 'DELETE' });
        return {
          content: [{ type: 'text', text: `Task #${args.taskId} deleted.` }]
        };
      }

      default:
        throw new Error(`Unknown tool: ${name}`);
    }
  } catch (error) {
    return {
      isError: true,
      content: [{ type: 'text', text: `Error executing ${name}: ${error.message}` }]
    };
  }
}

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
  terminal: false
});

rl.on('line', async (line) => {
  if (!line.trim()) return;

  try {
    const request = JSON.parse(line);
    const id = request.id;

    if (request.method === 'initialize') {
      const response = {
        jsonrpc: '2.0',
        id,
        result: {
          protocolVersion: '2024-11-05',
          capabilities: {
            tools: {}
          },
          serverInfo: {
            name: 'kanban-mcp-server',
            version: '1.0.0'
          }
        }
      };
      process.stdout.write(JSON.stringify(response) + '\n');
      return;
    }

    if (request.method === 'notifications/initialized') {
      return;
    }

    if (request.method === 'tools/list') {
      const response = {
        jsonrpc: '2.0',
        id,
        result: {
          tools: TOOLS
        }
      };
      process.stdout.write(JSON.stringify(response) + '\n');
      return;
    }

    if (request.method === 'tools/call') {
      const { name, arguments: args } = request.params;
      const result = await handleToolCall(name, args || {});
      const response = {
        jsonrpc: '2.0',
        id,
        result
      };
      process.stdout.write(JSON.stringify(response) + '\n');
      return;
    }

    // Default response for unhandled methods
    const response = {
      jsonrpc: '2.0',
      id,
      error: {
        code: -32601,
        message: `Method not found: ${request.method}`
      }
    };
    process.stdout.write(JSON.stringify(response) + '\n');
  } catch (err) {
    const errorResponse = {
      jsonrpc: '2.0',
      id: null,
      error: {
        code: -32700,
        message: 'Parse error: ' + err.message
      }
    };
    process.stdout.write(JSON.stringify(errorResponse) + '\n');
  }
});

console.error('[kanban-mcp-server] Running on stdio. Forwarding to ' + BASE_URL);
