---
description: Gerencia e sincroniza o quadro Kanban estilo GitHub Projects na porta 8085 com as tarefas do projeto.
globs: ["**/*.md", "backend/**/*", "frontend/**/*", "mcp-server/**/*"]
---

# Regra: Kanban Manager (Antigravity)

Quando o usuário pedir para gerenciar, listar, criar, mover ou concluir tarefas no Kanban local:
1. Verifique se o backend Spring Boot está ativo em `http://localhost:8085/api/boards/default`.
2. Para consultar cartões: faça requisições GET para `http://localhost:8085/api/tasks` ou use o endpoint de resumo do board.
3. Para criar um cartão: envie POST para `http://localhost:8085/api/tasks` com título, prioridade, status, labels e responsável.
4. Para mover cartões entre colunas (Backlog, Todo, In Progress, In Review, Done): envie PATCH para `http://localhost:8085/api/tasks/{id}/move`.
5. Para marcar uma tarefa como feita: envie PATCH para `http://localhost:8085/api/tasks/{id}/done`.
6. Para comandos livres em linguagem natural: utilize o endpoint do Copilot `POST /api/ai/command`.
7. Nunca delete tarefas sem confirmação explícita do usuário.
