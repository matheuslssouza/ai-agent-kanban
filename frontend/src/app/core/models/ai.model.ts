import { Task, TaskPriority, TaskStatus } from './task.model';

export interface AiCommandRequest {
  action?: string;
  prompt?: string;
  taskId?: number;
  title?: string;
  description?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  labels?: string[];
  assignee?: string;
  columnId?: number;
  position?: number;
}

export interface AiCommandResponse {
  success: boolean;
  message: string;
  actionExecuted: string;
  data: any;
}

export interface McpTool {
  name: string;
  description: string;
  inputSchema: any;
}
