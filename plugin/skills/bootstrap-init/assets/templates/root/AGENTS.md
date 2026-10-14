# Guia de Agentes — <NOME_DO_PROJETO>

Este arquivo segue o padrão aberto `AGENTS.md` e é a fonte de verdade curta para qualquer agente operando neste repositório. Ele deve ser versionado, salvo decisão explícita em contrário. Adaptadores de Claude, Cursor e Antigravity apontam para ele; não duplique aqui todas as regras das outras ferramentas.

## Projeto

<Descrição de 2-3 linhas: o que o projeto faz e sua stack real. Ex.: "Plataforma de X com backend <STACK_BACKEND> em `<PASTA_BACKEND>/` e frontend <STACK_FRONTEND> em `<PASTA_FRONTEND>/`.">

## Fontes de verdade

- Contexto vivo: `.agents/context/CONTEXT.md`
- Estado de trabalho: `.agents/tasks/` e `.agents/handoffs/`
- Decisões arquiteturais: `.agents/decisions/` (índice em `.agents/decisions/README.md`)
- Constituição e teste de sanidade: `.agents/test-onboarding.md`
- Memória compartilhada opcional: `.agents/memory/` e `ai-memory` (ver seção abaixo)
- Documentação real do produto (versionada ou não — ver decisão do projeto): `docs/`

## Memória compartilhada opcional (`ai-memory` — Fabio Akita)

Se `ai-memory` estiver configurado neste ambiente, use-o como complemento para memória entre ferramentas. Caso contrário, use os arquivos versionados deste repositório como fonte de continuidade.
- Consulte memória somente quando a task depender de histórico externo ao repositório.
- Registre aprendizados duradouros em ADRs ou documentação, não em memória por padrão.
- Atualize `.agents/handoffs/`; consolide no `ai-memory` apenas se ele estiver ativo.

## Leitura orientada pelo escopo

1. Leia este arquivo.
2. Para uma task média ou grande, leia `.agents/context/CONTEXT.md` e a task ativa relevante.
3. Leia o agente/regra especializada apenas quando a área da mudança exigir.
4. Consulte `ai-memory` apenas se estiver configurado e se o histórico for relevante.
   - Antigravity: `.agents/rules/<papel>.md`
   - Claude Code: `.claude/agents/<papel>.md`
   - Codex: `.codex/agents/<papel>.toml` (somente se houver delegação específica)
   - Cursor: `.cursor/rules/<papel>.mdc`
5. Para uma mudança arquitetural, releia a Constituição e os ADRs relacionados.
6. <Se houver uma iniciativa/feature específica em andamento, aponte para a documentação dela aqui.>

## Auditoria local

Antes de um commit ou handoff relevante, rode `bootstrap-audit`. Para commits manuais, `bootstrap-install-hook` instala um hook Git separado e opcional. Ele não é um hook do harness do Codex.

## Ciclo de vida de uma task

Mudança pequena: implemente diretamente e valide. Mudança média: crie uma task. Mudança arquitetural: use `bootstrap-plan` (opções → ADR → task), depois mova para `active/`; use `bootstrap-handoff` se pausar e `bootstrap-complete` ao concluir. Nenhuma skill deve impor o ritual máximo a uma mudança simples.

## Mapa do repositório

<Preencha com as pastas reais do projeto. Exemplo de formato:>

- `<PASTA_BACKEND>` — <o que é>
- `<PASTA_FRONTEND>` — <o que é>
- `docs/` — documentação real do produto
- `<outras pastas relevantes>`

## Papéis especializados (agentes)

<Liste somente os papéis que possuem fronteira técnica real ou operação de risco — não crie personas por padrão.>

- `<papel-1>`: Antigravity (`.agents/rules/<papel-1>.md`) | Claude (`.claude/agents/<papel-1>.md`) | Codex (`.codex/agents/<papel-1>.toml`) | Cursor (`.cursor/rules/<papel-1>.mdc`) — <responsabilidade>
- `<papel-2>`: Antigravity (`.agents/rules/<papel-2>.md`) | Claude (`.claude/agents/<papel-2>.md`) | Codex (`.codex/agents/<papel-2>.toml`) | Cursor (`.cursor/rules/<papel-2>.mdc`) — <responsabilidade>

## Regras globais

<Liste as regras que valem para qualquer papel/agente neste projeto — mantenha curto; detalhe vai em `.agents/rules/global.md` ou `.claude/rules/global.md`. Exemplos do tipo de regra a incluir (adapte à sua stack):>

- Não duplique regra de negócio entre camadas.
- Não altere contratos públicos (endpoints, schemas) silenciosamente.
- Não amplie o escopo de uma task sem registrar em `.agents/tasks/`.
- Registre decisões arquiteturais relevantes em `.agents/decisions/`.
- Ao alternar entre agentes de IA, preserve o handoff em `.agents/handoffs/`; sincronize o `ai-memory` somente se ele estiver configurado.
- <Regras técnicas específicas da stack: ex. ORM proibido, padrão de autenticação obrigatório, etc.>

## Comandos reais

```bash
# <Substitua pelos comandos reais de build/test/lint do projeto — nunca invente>
<comando de build>
<comando de test>
<comando de lint>
```

## Critérios de conclusão

- Critérios de aceitação da task verificados.
- Build/testes executados conforme o escopo alterado.
- Riscos e pendências declarados.
- Handoff preenchido em `.agents/handoffs/` e memória atualizada quando houver continuação em outra sessão/ferramenta.
