---
name: bootstrap-kanban
description: Controla e sincroniza o quadro Kanban local (Spring Boot + Angular estilo GitHub Projects) via REST API (porta 8085) ou Model Context Protocol (MCP). Use para consultar estado do quadro, criar tasks, mover cartões entre colunas (Backlog, Todo, In Progress, In Review, Done) e marcar tarefas como concluídas.
---

# bootstrap-kanban — Gerenciamento e Sincronização de Kanban Local

Esta skill permite que agentes de IA e desenvolvedores interajam programaticamente com a aplicação local de Kanban (estilo GitHub Projects), que roda como um monólito Spring Boot + Angular na porta `8085`.

---

## 🧭 Configuração do Ambiente

- **URL Base da API**: `http://localhost:8085/api`
- **Interface Web do Kanban**: `http://localhost:8085/`
- **Swagger UI Interativo**: `http://localhost:8085/swagger-ui.html`
- **Servidor MCP stdio**: `node mcp-server/index.js` (no diretório do Kanban)

---

## 🛠️ Modos de Operação

### Modo 1: Chamadas MCP (Model Context Protocol)

Se o seu cliente de IA suportar MCP (Claude Code, Cursor, Codex, Antigravity), use as seguintes ferramentas:

1. `create_task(title, description, priority, status, labels, assignee)`
2. `move_task(taskId, targetStatus, targetPosition)`
3. `mark_task_done(taskId)`
4. `list_tasks(status, query)`
5. `get_board_state()`
6. `delete_task(taskId)`

---

### Modo 2: API REST Direta (HTTP / JSON)

#### 1. Consultar estado completo do quadro:
```http
GET http://localhost:8085/api/boards/default
```

#### 2. Criar uma nova tarefa:
```http
POST http://localhost:8085/api/tasks
Content-Type: application/json

{
  "title": "[TASK-013] Implementar cache Redis para consultas territoriais",
  "description": "Reduzir tempo de resposta da rota de análise territorial.",
  "status": "TODO",
  "priority": "HIGH",
  "labels": ["performance", "backend", "cache"],
  "assignee": "backend + dados"
}
```

#### 3. Mover cartão de coluna ou posição:
```http
PATCH http://localhost:8085/api/tasks/{id}/move
Content-Type: application/json

{
  "targetStatus": "IN_PROGRESS",
  "targetPosition": 0
}
```

#### 4. Marcar tarefa como concluída (Done):
```http
PATCH http://localhost:8085/api/tasks/{id}/done
```

#### 5. Executar comando em linguagem natural via IA Copilot:
```http
POST http://localhost:8085/api/ai/command
Content-Type: application/json

{
  "prompt": "Mover tarefa 2 para In Progress"
}
```

---

## 🔄 Regras de Sincronização com `.agents/tasks/`

Quando o projeto utiliza o padrão `.agents/tasks/`:
- Arquivos em `.agents/tasks/backlog/` correspondem à coluna **Backlog** no Kanban.
- Arquivos em `.agents/tasks/active/` correspondem às colunas **In Progress** ou **In Review** no Kanban.
- Arquivos em `.agents/tasks/completed/` correspondem à coluna **Done** no Kanban.

Quando uma task muda de estado nos arquivos locais:
1. Ao iniciar o trabalho: Mova o arquivo para `active/` e envie `PATCH /api/tasks/{id}/move` com `targetStatus: "IN_PROGRESS"`.
2. Ao concluir e passar pelos testes/gates: Mova o arquivo para `completed/` e envie `PATCH /api/tasks/{id}/done`.

---

## 🛑 O que NÃO fazer sem perguntar antes

1. Nunca exclua uma tarefa (`DELETE /api/tasks/{id}`) sem autorização explícita do usuário.
2. Nunca marque uma tarefa como `DONE` sem que os critérios de aceitação e testes tenham sido verificados.
3. Não modifique as colunas estruturais do quadro sem consentimento.
