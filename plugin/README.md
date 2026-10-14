# bootstrap-agent-architecture (v3.0.0 — núcleo Codex-first e adaptadores opcionais)

## Princípio da v3

O núcleo do projeto é pequeno e compartilhável: `AGENTS.md`, estado persistente,
decisões e validação. Claude Code, Cursor, Antigravity e Codex usam adaptadores
somente quando estão realmente ativos. `ai-memory` e hooks são extensões opcionais.

Use o nível mínimo adequado:

- mudança pequena: implementar e validar;
- mudança média: task e validação;
- mudança arquitetural: opções, ADR, task, implementação e validação.

Plugin universal de skills para Claude Code e Codex, com oito skills:

| Skill | Comando | O que faz | Fase |
|---|---|---|---|
| `bootstrap-init` | `/bootstrap-agent-architecture:bootstrap-init` | Inicializa a arquitetura completa num projeto novo (roteadores, `.agents/`, adaptadores, docs). | 1 |
| `bootstrap-add-role` | `/bootstrap-agent-architecture:bootstrap-add-role` | Adiciona um papel especializado, replicado nos três adaptadores; pergunta sobre MCP para papéis de risco. | 1 + 4 |
| `bootstrap-audit` | `/bootstrap-agent-architecture:bootstrap-audit` | Auditoria local: teste de sanidade, compliance de formato, guardrail anti-vazamento (na sessão), índice de ADRs. | 1 |
| `bootstrap-plan` | `/bootstrap-agent-architecture:bootstrap-plan` | Ingestão (GitHub Projects ou texto colado) → 3 opções de solução → ADR → tasks em `backlog/` → snapshot opcional. | 2 |
| `bootstrap-handoff` | `/bootstrap-agent-architecture:bootstrap-handoff` | Pausa manual da task ativa: registra estado, `git diff`, próximos passos e "não refazer". | 2 |
| `bootstrap-complete` | `/bootstrap-agent-architecture:bootstrap-complete` | Checa o DoD (código → doc → testes com evidência) antes de mover a task para `completed/`. | 2 |
| `bootstrap-quarantine` | `/bootstrap-agent-architecture:bootstrap-quarantine` | Analisa uma skill externa contra a Constituição/ADRs e gera `relatorio-aderencia.md`; nunca ativa sem aprovação. | 4 |
| `bootstrap-install-hook` | `/bootstrap-agent-architecture:bootstrap-install-hook` | Instala (opt-in) um hook de `pre-commit` real com o guardrail anti-vazamento, testado nesta entrega. | 4 |

## O que esta versão entrega

**Fase 1** — plugin, skills, Constituição + teste de sanidade, índice de ADRs, guardrail anti-vazamento (sessão), compliance de formato e estrutura de `.agents/quarantine/`.

**Fase 2** — ciclo de vida completo da task: `bootstrap-plan`, `bootstrap-handoff`, `bootstrap-complete`.

**Fase 4** — `bootstrap-add-role` agora pergunta sobre MCP para papéis de risco (banco/infra), com fallback automático para `Bash` + veto-list quando não há MCP disponível; `bootstrap-quarantine` automatiza a geração do relatório de aderência de skills externas (a ativação continua exigindo aprovação humana, por design); `bootstrap-install-hook` instala o guardrail anti-vazamento como hook de `pre-commit` real — **testado de ponta a ponta nesta sessão** num repositório git descartável: bloqueou uma chave AWS e um `STRIPE_SECRET_KEY` colado por engano, deixou passar um commit limpo (incluindo código que só menciona as palavras "secret"/"token" sem atribuir valor, para confirmar que não há falso positivo óbvio), e confirmou que `--no-verify` continua pulando o hook (comportamento esperado do Git, documentado para o usuário).

**Fase 3** foi absorvida pela Fase 1. A v3 também adiciona `.agents/roles/` como fonte canônica dos papéis e torna `ai-memory` explicitamente opcional.

**v3.0.0 — documentação viva e núcleo enxuto.** A pasta `docs/` continua sendo mantida pelo ciclo de vida das tasks, mas não é exigida para toda alteração pequena:

- **Frontmatter de estado em todo doc** (`estado: planejado | real | divergente`, `fonte`, `ultima-revisao`) — legitima documentar projetos ainda sem código (greenfield, `planejado`) e formaliza divergências (`divergente`) em vez de correções silenciosas.
- **`bootstrap-init` preenche o núcleo inteiro de `docs/`** (não só `architecture/`): visão geral real em cada subpasta (`domain/`, `modules/`, `api/`, `data/`, `integrations/`, `diagrams/`); arquivos por módulo/endpoint nascem sob demanda, não no bootstrap.
- **Extensões opcionais detectadas e sugeridas** (`ai/`, `product/`, `ui/`, `quality/`, `security/`, `roadmap/`) — criadas só com confirmação do usuário e sempre declaradas no mapa do `docs/README.md`.
- **`bootstrap-complete` fecha o ciclo**: o DoD passou a exigir que a doc da área tocada pela task seja atualizada (carimbo de `ultima-revisao`, flip `planejado → real`, ou marcação `divergente`).
- **`bootstrap-audit` vigia a defasagem**: checa o frontmatter, lista os `divergente` pendentes e sinaliza docs `real` cuja `fonte` mudou no Git depois da última revisão.
- **Governança de `docs/`**: tabela de fontes primárias (nunca duplicar doc externa existente), nenhum arquivo solto na raiz de `docs/`, e diagramas com `status`/`fonte` declarados + índice obrigatório em catálogos grandes.

## Limitações que continuam valendo (documentadas nas próprias skills)

- `bootstrap-install-hook` só ativa em `.git/hooks/` — é um hook Git, não um lifecycle hook do Codex; cada dev que clonar o repositório precisa ativá-lo na própria máquina.
- O guardrail (sessão ou hook) é baseado em padrões `grep` conhecidos — reduz risco, não elimina.
- `bootstrap-quarantine` gera o relatório automaticamente, mas a ativação da skill importada continua exigindo aprovação humana — nunca é automática.
- MCP para papéis de risco depende de existir um servidor MCP configurado para o recurso específico — `bootstrap-add-role` pergunta e cai para `Bash` com veto-list se não houver.

## Instalação local

### Claude Code

```bash
/plugin marketplace add /caminho/absoluto/para/bootstrap-agent-architecture-plugin
/plugin install bootstrap-agent-architecture@victor-bootstrap
```

Se já tinha uma versão anterior instalada, depois de atualizar os arquivos:

```bash
/plugin marketplace update victor-bootstrap
/plugin update bootstrap-agent-architecture@victor-bootstrap
```

Depois de instalar/atualizar, os comandos aparecem como `/bootstrap-agent-architecture:<skill>`. Rode `/reload-plugins` se não aparecerem de imediato.

Para testar sem instalar (desenvolvimento):

```bash
### Codex

O pacote inclui o manifesto do Codex em `.codex-plugin/plugin.json` e reutiliza diretamente as
skills em `skills/`. O marketplace raiz (`.claude-plugin/marketplace.json`) também é compatível
com o Codex. A partir da raiz do repositório, configure e instale assim:

```bash
codex plugin marketplace add C:\caminho\absoluto\para\bootstrap-agent-architecture-plugin
codex plugin add bootstrap-agent-architecture@victor-bootstrap
```

No Codex desktop, a instalação também pode ser feita pela aba de plugins; no Codex CLI, abra o
navegador com `/plugins`. Inicie uma nova sessão depois da instalação.

O formato de plugin do Codex não usa os comandos slash do Claude. As mesmas skills ficam
disponíveis como capacidades do Codex quando o fluxo descrito na respectiva `SKILL.md` for
solicitado.

### Antigravity (AGY)

O pacote inclui o manifesto nativo do Antigravity em `plugin.json` e reutiliza diretamente as skills em `skills/`.

Para carregar no Antigravity:
1. Copie ou aponte a pasta `plugin` para a sua pasta de plugins/skills de workspace (ex.: `.agents/plugins/bootstrap-agent-architecture` ou registrando em `plugins.json`).
2. O Antigravity descobre as oito skills automaticamente através do `SKILL.md` de cada pasta.
3. No projeto de destino, o Antigravity lê nativamente o `AGENTS.md` e o roteador `GEMINI.md`, respeitando as regras e o hub de estado em `.agents/`.

### Memória Compartilhada opcional com ai-memory (Fabio Akita)

Se o projeto precisar compartilhar contexto fora dos arquivos versionados entre **Antigravity**, **Codex** e **Claude Code**:
1. Instale o [`ai-memory`](https://github.com/akitaonrails/ai-memory) no seu ambiente.
2. Adicione o servidor MCP ao `mcp_config.json` (usado por Antigravity e adaptável a Claude/Codex):
   ```json
   {
     "mcpServers": {
       "ai-memory": {
         "command": "ai-memory",
         "args": ["mcp"]
       }
     }
   }
   ```
3. O template `AGENTS.md` já instrui os agentes a consultarem e consolidarem memória a cada início/fim de sessão.
