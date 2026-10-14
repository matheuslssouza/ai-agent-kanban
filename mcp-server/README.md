# 🤖 Kanban MCP Server (Model Context Protocol)

Este servidor MCP permite que assistentes e agentes de inteligência artificial (como **Claude Code**, **Claude Desktop**, **Cursor**, **Codex**, **Antigravity**) controlem diretamente o seu Kanban local através do protocolo padrão da Anthropic / Model Context Protocol.

## 🚀 Como configurar no seu cliente de IA

### No Claude Desktop (`claude_desktop_config.json`):

```json
{
  "mcpServers": {
    "kanban-projects": {
      "command": "node",
      "args": [
        "c:/Users/tizo/Downloads/bootstrap-agent-architecture-plugin-main/bootstrap-agent-architecture-plugin-main/mcp-server/index.js"
      ],
      "env": {
        "KANBAN_API_URL": "http://localhost:8080"
      }
    }
  }
}
```

### No Cursor / Antigravity / Codex:
Basta apontar o comando para executar `node ./mcp-server/index.js`.

---

## 🛠 Ferramentas disponíveis para a IA:

| Ferramenta | Descrição | Parâmetros |
| :--- | :--- | :--- |
| `create_task` | Cria nova tarefa no quadro | `title`, `description`, `priority`, `status`, `labels`, `assignee` |
| `mark_task_done` | Marca uma tarefa como feita e move para Done | `taskId` |
| `move_task` | Move uma tarefa para outra coluna e posição | `taskId`, `targetStatus`, `targetPosition` |
| `list_tasks` | Lista tarefas com filtro por status ou busca | `status`, `query` |
| `get_board_state`| Retorna todas as colunas e cartões | *(nenhum)* |
| `delete_task` | Exclui uma tarefa pelo ID | `taskId` |

---

## ⚡ Testando diretamente via CLI:
```bash
node mcp-server/index.js
```
