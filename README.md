# AQUAMARINE

Mod Fabric para Minecraft Java 26.3: explore a Cidade Perdida de Nereia e
enfrente o Kraken. Repositório privado, uso com amigos (multiplayer é
prioridade).

Plano oficial: https://claude.ai/artifact/M68jvjvKRddKZMxq3yEQjT

O plano oficial acima pode estar desatualizado: em conflito, vale
[docs/00-decisoes.md](docs/00-decisoes.md).

## Por onde começar
- **Agentes:** [AGENTS.md](AGENTS.md) (regras, vários agentes em paralelo),
  depois [CONTEXTO.md](CONTEXTO.md) (quem faz o quê agora).
- **Todos:** [docs/00-decisoes.md](docs/00-decisoes.md) é a fonte da verdade.
  Os demais arquivos de `docs/` detalham um assunto cada e trazem um aviso de
  status; seções de **ideia** não são decisão.

## Índice (cada arquivo é dono do seu assunto)
| Arquivo | Assunto |
|---|---|
| AGENTS.md | Regras de comportamento e coordenação entre agentes |
| CONTEXTO.md | Tarefas em andamento e registro de entregas |
| docs/00-decisoes.md | Decisões tomadas e perguntas em aberto |
| docs/01-visao-e-riscos.md | Visão do mod, riscos, tom e paleta |
| docs/02-kraken.md | Luta, escudo, Pressão, barra de vida, IA, balanceamento |
| docs/03-invocacao-e-chat.md | Anúncio estilo Terraria, aviso falso |
| docs/04-cidade-perdida.md | Estrutura, bioma, criaturas (incluindo o Walker) |
| docs/05-itens.md | Materiais e itens (incluindo o Óculos de Mergulho) |
| docs/06-tecnico.md | Implementação, ambiente, build e convenções |
| docs/07-roadmap.md | Ordem dos marcos, progresso e faixas das Entregas 1 e 2; Fase 3 planejada |
| docs/08-fase3.md | Fase 3 (bloqueada até a Entrega 2 completa): especificação de Pressão por profundidade, Moréia, baleias, Propulsor, munições, encantamentos e Vila |
| prompts/estruturador.md | Prompt para o agente que inicia a implementação |
| prompts/orquestrador.md | Prompt histórico de arranque da Entrega 2; para retomar, leia CONTEXTO.md |
| prompts/kraken-v1.md | Corpo das Tasks dos agentes Claude (Entrega 2) |
| prompts/kraken-modelagem.md | Corpo das Tasks dos agentes Codex (modelagem e QA) |
| prompts/fase3-claude.md | Corpo das Tasks do Claude na Fase 3 (**bloqueado**, ver 07-roadmap) |
| prompts/fase3-modelagem.md | Corpo das Tasks do Codex na Fase 3 (**bloqueado**) |
| prompts/orquestrador-fase3.md | Prompt do orquestrador da Fase 3; **enviá-lo libera a fase** (só com a Entrega 2 completa) |
