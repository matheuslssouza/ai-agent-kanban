# 📋 AI Agent Kanban Dashboard

> **Dashboard de Kanban local, gratuito e integrado a IA (via MCP e REST) para acompanhamento e planejamento autônomo de agentes de IA.**
> Interface moderna para gestão visual de tarefas, construído como um monólito limpo em camadas (**Spring Boot + Angular**), sem custos de assinaturas na nuvem e com controle pessoal total sobre as tarefas e sprints de desenvolvimento dos agentes.

---

## 💡 Motivação & Contexto

Ao desenvolver projetos utilizando múltiplos agentes de inteligência artificial (como **Claude Code**, **Codex**, **Cursor**, **Antigravity**), o custo e a complexidade de manter ferramentas de gestão de projetos externas (Jira, Linear, ferramentas pagas com rate limits) tornam-se um gargalo desnecessário.

Este projeto foi criado como um **painel Kanban local independente para agentes de IA**:
- 🆓 **100% Gratuito e Local:** Roda inteiramente na sua máquina (banco H2 persistido em arquivo local, sem dependência de Docker externo ou nuvem).
- 🤖 **Construído para Agentes de IA:** Inclui um servidor **MCP (Model Context Protocol)** e endpoints REST otimizados para que os próprios agentes leiam, criem, movam e concluam tarefas de forma autônoma.
- 🎯 **Planejamento Pessoal & Visibilidade:** Permite visualizar exatamente o que cada agente implementou, o que está em revisão e o que está no backlog, com suporte a notas e documentação completa em Markdown dentro de cada cartão.
- 🔄 **Sincronização com `.agents/tasks/`:** Lê e sincroniza automaticamente tarefas mantidas em arquivos Markdown locais sem modificar seus arquivos originais.

---

## 🏛️ Decisões de Arquitetura

O sistema adota uma **Arquitetura em Camadas Limpa (Clean Layered Architecture)** com padrão monólito:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        FRONTEND (Angular 19)                           │
│   • SCSS Centralizado (src/styles/)   • Reusable Components (Inline TS)│
│   • Page Components (templateUrl)     • Native HTML5 Drag & Drop       │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTP / REST (JSON)
┌───────────────────────────────────▼────────────────────────────────────┐
│                  BACKEND (Spring Boot 3.3 / Java 21)                   │
│   • Controller Interfaces & Impl      • Service Interfaces & Impl      │
│   • Repository Interfaces (JPA)       • Domain Entities & Enums        │
│   • H2 Database (Arquivo local)       • OpenAPI 3 / Swagger UI         │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ stdio / JSON-RPC 2.0
┌───────────────────────────────────▼────────────────────────────────────┐
│                    MCP SERVER (Node.js - Stdio)                        │
│   • Claude Desktop   • Cursor   • Codex   • Antigravity AI Agents     │
└────────────────────────────────────────────────────────────────────────┘
```

### 1. Decisões do Backend (Java 21 + Spring Boot 3.3)
- **Todas as camadas possuem interfaces explícitas:**
  - `TaskControllerApi`, `BoardControllerApi`, `AiIntegrationControllerApi`
  - `TaskService`, `TaskColumnService`, `BoardService`, `AiIntegrationService`
  - `TaskRepository`, `TaskColumnRepository`, `BoardRepository`
- **Banco de Dados Local H2:** Configurado para persistir em `./data/kanbandb` com zero dependências externas.
- **Porta Dedicada 8085:** Evita colisões comuns com Docker ou outros serviços em portas padrão (8080).
- **Entrega Monolítica:** O backend entrega tanto a API REST quanto os arquivos estáticos compilados do Angular no caminho raiz `/`.

### 2. Decisões do Frontend (Angular 19)
- **Modularização Estrita:**
  - `core/`: Modelos de dados e serviços HTTP (`TaskService`, `BoardService`, `AiService`).
  - `shared/components/`: Componentes reutilizáveis (Badges, CardItem, ColumnHeader, QuickAdd, FilterBar, AiPromptBar, McpInspector).
  - `features/kanban/`: Páginas e modais (`KanbanPageComponent`, `TaskDetailModalComponent`).
- **HTML dentro do TypeScript para Reutilizáveis:**
  - Todos os componentes em `shared/components/` utilizam `template: \`...\`` embutido no TypeScript para coesão e reutilização ágil.
- **Páginas com `templateUrl`:**
  - Páginas e componentes de visão complexa utilizam arquivos `.html` separados via `templateUrl`.
- **SCSS Centralizado na pasta `src/styles/`:**
  - Variáveis do tema GitHub (`_variables.scss`), mixins de layout (`_mixins.scss`), estilos globais do GitHub (`_github-theme.scss`), layout de colunas (`_kanban.scss`), modais (`_modal.scss`) e barra de IA (`_ai-bar.scss`), unificados em `styles.scss`.
- **Visualizador Completo de Markdown:**
  - Cada task possui aba de **Leitura de Observações**, permitindo visualizar documentações completas de dezenas de milhares de caracteres com formatação limpa.

---

## 📁 Estrutura do Projeto

```
.
├── backend/                                   # 🍃 Spring Boot 3.3.4 (Java 21)
│   ├── pom.xml                                # Dependências Maven
│   ├── src/main/java/com/kanban/
│   │   ├── domain/                            # Entidades e Enums
│   │   │   ├── entity/                        # Board, TaskColumn, Task
│   │   │   └── enums/                         # TaskStatus, TaskPriority
│   │   ├── repository/                        # Interfaces Spring Data JPA
│   │   │   ├── BoardRepository.java
│   │   │   ├── TaskColumnRepository.java
│   │   │   └── TaskRepository.java
│   │   ├── service/                           # Interfaces & Implementações
│   │   │   ├── TaskService.java / impl/TaskServiceImpl.java
│   │   │   ├── TaskColumnService.java / impl/TaskColumnServiceImpl.java
│   │   │   ├── BoardService.java / impl/BoardServiceImpl.java
│   │   │   └── AiIntegrationService.java / impl/AiIntegrationServiceImpl.java
│   │   ├── controller/                        # Interfaces OpenAPI & Controllers
│   │   │   ├── TaskControllerApi.java / TaskController.java
│   │   │   ├── BoardControllerApi.java / BoardController.java
│   │   │   └── AiIntegrationControllerApi.java / AiIntegrationController.java
│   │   ├── dto/                               # Objetos de Transferência (DTOs)
│   │   └── config/                            # Cors, WebMvc, DataInitializer, OpenApi
│   └── src/main/resources/
│       ├── application.yml                    # Configurações do H2 e porta 8085
│       └── static/                            # SPA Angular compilado para o monólito
│
├── frontend/                                  # 🅰️ Angular 19 (Modularizado)
│   ├── src/styles/                            # 🎨 SCSS Centralizado
│   │   ├── _variables.scss                    # Paleta Dark/Light GitHub
│   │   ├── _mixins.scss                       # Mixins de flex, botões, scrollbar
│   │   ├── _github-theme.scss                 # Estilos do tema GitHub Projects
│   │   ├── _kanban.scss                       # Grid de colunas e cards arrastáveis
│   │   ├── _modal.scss                        # Modais e gavetas de edição
│   │   └── _ai-bar.scss                       # Barra de Copilot e gaveta MCP
│   ├── src/app/
│   │   ├── core/                              # Modelos e Serviços HTTP
│   │   ├── shared/components/                 # Reutilizáveis (HTML no TypeScript)
│   │   │   ├── card-item.component.ts
│   │   │   ├── column-header.component.ts
│   │   │   ├── badge.component.ts
│   │   │   ├── quick-add.component.ts
│   │   │   ├── filter-bar.component.ts
│   │   │   ├── ai-prompt-bar.component.ts
│   │   │   └── mcp-inspector.component.ts
│   │   └── features/kanban/                   # Páginas (templateUrl)
│   │       ├── pages/kanban-page/
│   │       └── components/task-detail-modal/
│
├── mcp-server/                                # 🤖 Model Context Protocol (MCP)
│   ├── index.js                               # Servidor stdio JSON-RPC 2.0
│   ├── sync-tasks.js                          # Script de sincronização de tasks locais
│   └── package.json
│
├── .agents/                                   # 🧠 Integração com a Arquitetura de Agentes
│   ├── roles/kanban-manager.md                # Papel canônico do agente
│   └── rules/kanban-manager.md                # Regra Antigravity / IDE
│
├── plugin/skills/bootstrap-kanban/            # 📦 Skill empacotada para o Marketplace
│   └── SKILL.md
│
├── start-all.bat                              # Inicia Monólito (Backend + Frontend)
├── start-backend.bat                          # Inicia apenas o Spring Boot
└── start-frontend.bat                         # Inicia apenas o Angular Dev Server
```

---

## 📡 Listagem Completa de Endpoints REST

A API roda por padrão em **`http://localhost:8085`**. A documentação interativa Swagger está disponível em `http://localhost:8085/swagger-ui.html`.

### 1. Tarefas (`/api/tasks`)

| Método | Endpoint | Descrição | Parâmetros / Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/tasks` | Lista todas as tarefas do quadro | Query params: `query` (busca textual), `status` (`BACKLOG`, `TODO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`), `columnId` |
| `GET` | `/api/tasks/{id}` | Busca uma tarefa específica por ID | `id` (path) |
| `POST` | `/api/tasks` | Cria uma nova tarefa | JSON: `title` (obrigatório), `description`, `status`, `priority`, `assignee`, `labels`, `dueDate`, `columnId` |
| `PUT` | `/api/tasks/{id}` | Atualiza todos os dados de uma tarefa | `id` (path), JSON com os campos atualizados |
| `PATCH` | `/api/tasks/{id}/move` | Move a tarefa para outra coluna e posição | JSON: `targetStatus` ou `targetColumnId`, `targetPosition` (inteiro) |
| `PATCH` | `/api/tasks/{id}/done` | Marca uma tarefa imediatamente como `DONE` | `id` (path) |
| `DELETE` | `/api/tasks/{id}` | Exclui uma tarefa do quadro | `id` (path) |

#### Exemplo de criação de tarefa:
```http
POST /api/tasks HTTP/1.1
Host: localhost:8085
Content-Type: application/json

{
  "title": "[TASK-013] Implementar cache para rotas territoriais",
  "description": "## Contexto\nReduzir o tempo de resposta das consultas espaciais.\n\n## Critérios de Aceitação\n- Cache de 1 hora para feições estáticas.",
  "status": "TODO",
  "priority": "HIGH",
  "assignee": "backend + dados",
  "labels": ["performance", "backend", "cache"],
  "dueDate": "2026-10-20"
}
```

#### Exemplo de movimentação de cartão:
```http
PATCH /api/tasks/2/move HTTP/1.1
Host: localhost:8085
Content-Type: application/json

{
  "targetStatus": "IN_PROGRESS",
  "targetPosition": 0
}
```

---

### 2. Quadros e Colunas (`/api/boards`)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/api/boards/default` | Retorna o quadro padrão completo com todas as colunas, cartões ordenados e contadores |
| `GET` | `/api/boards` | Lista todos os quadros cadastrados |
| `GET` | `/api/boards/{id}` | Retorna detalhes de um quadro específico |
| `GET` | `/api/boards/{id}/columns` | Lista as colunas configuradas de um quadro |

---

### 3. Integração com Inteligência Artificial (`/api/ai`)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/ai/command` | Processa comandos em linguagem natural ou chamadas estruturadas de IA |
| `GET` | `/api/ai/tools` | Retorna o catálogo de ferramentas no padrão Model Context Protocol |
| `GET` | `/api/ai/mcp-manifest` | Retorna o manifesto JSON do servidor MCP |

#### Exemplo de comando em linguagem natural:
```http
POST /api/ai/command HTTP/1.1
Host: localhost:8085
Content-Type: application/json

{
  "prompt": "Marcar tarefa 2 como feita"
}
```
*Resposta:*
```json
{
  "success": true,
  "message": "Tarefa #2 marcada como CONCLUÍDA / DONE com sucesso!",
  "actionExecuted": "mark_done",
  "data": { "id": 2, "status": "DONE" }
}
```

---

## 🤖 Integração com Model Context Protocol (MCP)

O projeto conta com uma implementação oficial do **Model Context Protocol** baseada em `stdio` (`JSON-RPC 2.0`), localizada em [`mcp-server/index.js`](file:///c:/Users/tizo/Downloads/bootstrap-agent-architecture-plugin-main/bootstrap-agent-architecture-plugin-main/mcp-server/index.js).

### 🛠️ Ferramentas MCP Disponíveis para Agentes:

1. **`create_task`**: Cria uma nova tarefa com título, descrição Markdown, prioridade, status, labels e responsável.
2. **`mark_task_done`**: Marca um item como concluído e o move para a coluna `Done`.
3. **`move_task`**: Move um cartão entre colunas (`BACKLOG`, `TODO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`) e define sua posição.
4. **`list_tasks`**: Consulta tarefas com filtros por status ou termo de busca.
5. **`get_board_state`**: Retorna a visão completa do quadro, colunas e contadores.
6. **`delete_task`**: Remove uma tarefa pelo ID.

### ⚙️ Configuração nos Clientes de IA

#### Claude Desktop (`claude_desktop_config.json`):
```json
{
  "mcpServers": {
    "kanban-projects": {
      "command": "node",
      "args": [
        "c:/Users/tizo/Downloads/bootstrap-agent-architecture-plugin-main/bootstrap-agent-architecture-plugin-main/mcp-server/index.js"
      ],
      "env": {
        "KANBAN_API_URL": "http://localhost:8085"
      }
    }
  }
}
```

#### Cursor / Codex / Antigravity:
Basta executar `node mcp-server/index.js` ou utilizar o papel especializado configurado em [`.agents/roles/kanban-manager.md`](file:///c:/Users/tizo/Downloads/bootstrap-agent-architecture-plugin-main/bootstrap-agent-architecture-plugin-main/.agents/roles/kanban-manager.md).

---

## 🔄 Sincronização com `.agents/tasks/`

Para ler automaticamente tasks mantidas em pastas Markdown (como `.agents/tasks/backlog/`, `active/` e `completed/`) e refletir no quadro Kanban com as descrições completas:

```bash
node mcp-server/sync-tasks.js "c:/caminho/do/projeto/.agents/tasks" http://localhost:8085
```

O script é **idempotente**: identifica tasks já existentes pelo ID ou título, atualiza o status se houve mudança e preserva 100% das observações e critérios de aceitação.

---

## 🚀 Como Executar

### Pré-requisitos
- **Java 21 LTS**
- **Node.js 18+** e **npm**

### Executando em 1 clique (Windows)
- Clique duplo em **[`start-all.bat`](file:///c:/Users/tizo/Downloads/bootstrap-agent-architecture-plugin-main/bootstrap-agent-architecture-plugin-main/start-all.bat)** para iniciar o backend e o frontend juntos.
- Ou execute separadamente:
  - **[`start-backend.bat`](file:///c:/Users/tizo/Downloads/bootstrap-agent-architecture-plugin-main/bootstrap-agent-architecture-plugin-main/start-backend.bat)**: Sobe o Spring Boot na porta `8085`.
  - **[`start-frontend.bat`](file:///c:/Users/tizo/Downloads/bootstrap-agent-architecture-plugin-main/bootstrap-agent-architecture-plugin-main/start-frontend.bat)**: Sobe o Angular com live-reload na porta `4200`.

### 🌐 Endereços Locais
- **Aplicação Completa (Monólito)**: [http://localhost:8085/](http://localhost:8085/)
- **Swagger UI**: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)
- **Console H2**: [http://localhost:8085/h2-console](http://localhost:8085/h2-console) (JDBC URL: `jdbc:h2:file:./data/kanbandb`)
- **Frontend em Desenvolvimento**: [http://localhost:4200/](http://localhost:4200/)
