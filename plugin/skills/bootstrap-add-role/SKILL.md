---
name: bootstrap-add-role
description: Adiciona um papel especializado somente quando há uma fronteira técnica real ou uma operação de risco. Mantém um corpo canônico e gera adaptadores apenas para as ferramentas usadas no projeto. Use ao pedir um especialista em uma área concreta.
---

# bootstrap-add-role — criar ou atualizar um papel especializado

## Entradas necessárias

- Nome do papel e seu objetivo principal — se o usuário não deu isso pronto, pergunte: **"Qual o nome do novo papel e seu objetivo principal?"** Peça também, se não estiver implícito: o que esse papel pode fazer sozinho, o que ele deve recusar/perguntar antes de fazer, e se ele tem poder de veto sobre algo específico (comum em papéis de banco de dados/infra).
- Quais adaptadores este projeto usa de fato — leia `AGENTS.md`/`CLAUDE.md`/`GEMINI.md` existentes ou pergunte, se for a primeira vez. Nunca crie adaptadores para ferramentas que o projeto não usa.

## Procedimento

1. **Confira duplicidade.** Verifique se já existe um papel com responsabilidade sobreposta em `.claude/agents/`. Papéis demais com fronteiras confusas são piores que poucos papéis bem definidos — se houver sobreposição real, sugira ajustar o papel existente em vez de criar um novo.

2. **Se o papel envolve uma operação de risco (banco de dados, deploy, infraestrutura), pergunte sobre MCP antes de decidir as ferramentas**: "Existe um servidor MCP configurado neste ambiente para este recurso (ex.: um MCP de Postgres/MySQL com escopo de leitura de schema)? Se sim, qual?"
   - **Se sim**: liste esse servidor MCP em `tools:` no lugar de (ou junto com) `Bash`, e registre como regra obrigatória do papel que operações destrutivas (`DROP`, `DELETE` sem `WHERE`, etc.) devem passar por esse escopo restrito, nunca por um `Bash` genérico.
   - **Se não existir ou o usuário não souber**: siga com `Bash` e a lista explícita de "o que não pode fazer sem perguntar antes" (mecanismo padrão desta arquitetura) — não bloqueie a criação do papel por falta de MCP, e não invente um servidor que não existe.

3. **Escreva o conteúdo canônico em `.agents/roles/<papel>.md`** (ou no diretório canônico já adotado pelo projeto), a partir do template de papel. O corpo deve responder:
   - **Arquitetura confirmada** — citando pastas/namespaces/arquivos reais do projeto, nunca em abstrato.
   - **Regras obrigatórias, numeradas** — com o "porquê" quando não for óbvio.
   - **Referências de código reais** — 2-3 exemplos concretos, não pseudocódigo.
   - **O que pode fazer** e **o que não pode fazer sem perguntar antes** — a segunda lista é a mais importante para prevenir dano; inclua aqui qualquer poder de veto declarado pelo usuário.

4. **Frontmatter do Claude Code** — inclua os campos nativos, escolhidos pelo peso real do papel, não por padrão fixo:
   ```yaml
   ---
   name: <papel>
   description: <uma frase objetiva: quando usar, e o que este papel explicitamente NÃO faz>
   tools: <lista mínima necessária — um papel de auditoria/veto normalmente não precisa de Edit/Write; se o passo 2 confirmou um MCP, liste-o aqui>
   model: <sonnet para papéis que fazem trabalho complexo/orquestração; haiku (ou modelo leve equivalente) para papéis simples, repetitivos ou efêmeros>
   memory: project   # se este papel se beneficia de lembrar contexto entre sessões
   ---
   ```
   `model`, `memory` e `skills:` são campos específicos do Claude Code. Não os trate como contrato universal entre ferramentas.

5. **Replique para Codex, se usado para delegação específica**: `.codex/agents/<papel>.toml`, copiando o corpo canônico para `developer_instructions = """ ... """`, preservando o Markdown. Não crie este arquivo apenas para duplicar regras globais já presentes em `AGENTS.md`.

6. **Replique para Cursor, se usado**: `.cursor/rules/<papel>.mdc`, copiando o mesmo corpo, com frontmatter `description` (idêntica à do Claude Code), `globs` inferidos dos caminhos reais que este papel edita (ex.: `["src/billing/**/*"]`), e `alwaysApply: false`.

7. **Replique para Antigravity, se usado**: `.agents/rules/<papel>.md`, a partir de `assets/templates/antigravity/rules/_role-template.md`, preservando o corpo e incluindo frontmatter YAML com `description:` (idêntica à do Claude Code) e `globs:` (se aplicável ao escopo de arquivos). O Antigravity carrega a regra automaticamente de acordo com o contexto ou on-demand.

8. **Confira identidade de conteúdo.** Faça um diff visual rápido entre o papel canônico e os adaptadores ativos — a única divergência aceitável é a casca (frontmatter/formato específico de cada agente). Se o papel usar ferramentas via MCP, declare-as na configuração da ferramenta que realmente as utiliza.

9. **Atualize os roteadores.** Adicione o novo papel à lista de "Papéis especializados" em `AGENTS.md`, `CLAUDE.md` e `GEMINI.md`.

10. **Considere atualizar `test-onboarding.md`.** Se este papel introduz uma regra que vale a pena checar na seção "Perguntas de sanidade" (`.agents/test-onboarding.md`), proponha uma pergunta nova — não é obrigatório para papéis simples.

11. **Reporte.** Confirme ao usuário: nome do papel, em quais adaptadores foi criado (Antigravity, Claude, Codex, Cursor), o modelo escolhido e por quê, se algum poder de veto foi registrado, e se o acesso a um recurso de risco ficou via MCP restrito ou via `Bash` com veto-list.

## Regras que valem sempre

- Nunca copie nome de papel, regra ou conteúdo de outro projeto — cada papel é escrito com referências reais deste projeto.
- Nunca deixe um `<PLACEHOLDER>` sem preencher no arquivo final.
- Nunca crie o adaptador de uma ferramenta que o projeto não usa.
- Um papel com poder de veto (ex.: banco de dados, deploy) deve ter isso explícito na lista "o que não pode fazer sem perguntar antes" — não deixe implícito.
