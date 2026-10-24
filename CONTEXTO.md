# CONTEXTO.md: coordenação ao vivo entre agentes

> **Dono de:** quem está fazendo o quê agora e o histórico de entregas.
> Não repete decisões (ver [docs/00-decisoes.md](docs/00-decisoes.md)), plano de
> marcos (ver [docs/07-roadmap.md](docs/07-roadmap.md)) nem ambiente técnico
> (ver [docs/06-tecnico.md](docs/06-tecnico.md)). Regras: [AGENTS.md](AGENTS.md).

> **FASE ATUAL: IMPLEMENTAÇÃO LIBERADA SOMENTE PARA A "ENTREGA 1" (Estruturador).** Fora dela, não implemente nada (código, texturas,
> modelos) até o Adriano liberar explicitamente.

## Em andamento
Registre aqui antes de começar e apague a linha ao terminar. "Recurso" = o que
não pode ser usado por dois agentes ao mesmo tempo (jogo aberto, Gradle).

| Tarefa | Agente | Recurso | Desde |
|---|---|---|---|
| Entrega 1 (ver docs/07-roadmap.md). Branch `estruturador/entrega-1`, worktree `../aquamarine-estruturador-entrega-1`. Plano (início 12:30): leitura e plano 15 min (até 12:45) · A estrutura 60 min (até 13:45) · B materiais e Forja 90 min (até 15:15) · C óculos 60 min (até 16:15) · D extras 45 min (até 17:00) · reserva build/correção/handoff 30 min (até 17:30). Bloco que estourar o dobro: corta escopo e segue. | Estruturador (Claude Opus 5.5) | jogo, Gradle (na worktree) | 24/10/2026 12:30 |

## Registro de handoff
Uma linha por entrega, a mais recente no topo: `data · agente · o que mudou · o que falta`.

- 24/10/2026 · Claude · Repositório criado, docs organizados, JDK 25 instalado,
  AGENTS.md e CONTEXTO.md criados. Um óculos foi implementado por engano e
  removido a pedido. Falta: decidir o que sai do planejamento primeiro.
