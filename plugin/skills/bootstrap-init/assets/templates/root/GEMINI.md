# Instruções para Antigravity — <NOME_DO_PROJETO>

Arquivo local de roteamento para Antigravity. A fonte principal unificada de verdade é o `AGENTS.md` na raiz.

Leia nesta ordem:
1. `AGENTS.md` (regras mestras, stack, comandos e convenções)
2. `.agents/context/CONTEXT.md` (contexto vivo da branch e iniciativa)
3. Task ativa em `.agents/tasks/active/` (se houver)
4. Memória compartilhada via `ai-memory`, somente se configurado e relevante para a task
5. Regras de papéis especializados em `.agents/rules/`

Papéis especializados / Regras: `.agents/rules/` (<liste os nomes dos papéis deste projeto>)
Skills do projeto: `.agents/skills/` (ou `skills/`)
Decisões e Governança: `.agents/decisions/` e `.agents/test-onboarding.md`

Não trate este arquivo como documentação completa. Siga as orientações em `AGENTS.md` e registre o progresso em `.agents/tasks/` e `.agents/handoffs/`.
