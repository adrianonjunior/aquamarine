# CONTEXTO.md: coordenação ao vivo entre agentes

> **Dono de:** quem está fazendo o quê agora e o histórico de entregas.
> Não repete decisões (ver [docs/00-decisoes.md](docs/00-decisoes.md)), plano de
> marcos (ver [docs/07-roadmap.md](docs/07-roadmap.md)) nem ambiente técnico
> (ver [docs/06-tecnico.md](docs/06-tecnico.md)). Regras: [AGENTS.md](AGENTS.md).

> **FASE ATUAL: ENTREGA 1 CONCLUÍDA (24/10/2026).** Nada além dela está liberado: não implemente
> nada (código, texturas, modelos) até o Adriano liberar explicitamente o próximo passo.

## Em andamento
Registre aqui antes de começar e apague a linha ao terminar. "Recurso" = o que
não pode ser usado por dois agentes ao mesmo tempo (jogo aberto, Gradle).

| Tarefa | Agente | Recurso | Desde |
|---|---|---|---|
| JEI 31.16.0.90 (só no ambiente de dev) + plugin da Forja e descrições dos itens (pedido do Adriano). Branch `estruturador/jei-descricoes`, worktree `../aquamarine-estruturador-entrega-1`. | Estruturador (Claude Opus 5.5) | jogo, Gradle | 24/10/2026 13:50 |

## Registro de handoff
Uma linha por entrega, a mais recente no topo: `data · agente · o que mudou · o que falta`.

- 24/10/2026 13:20 · Estruturador (Claude Opus 5.5) · **Entrega 1 concluída e
  mergeada na `main`** (A, B, C e D; CI verde). Pronto: registros, aba
  "Aquamarine", config, tags, lang pt/en; Oricálcio (minério, lingote, bloco,
  5 ferramentas com Maré, armadura com bônus de nado), Pérola, Cristal, Escama;
  Forja Abissal (tipo de receita próprio + tela); Óculos de Mergulho (névoa 1,5x,
  água mais clara); barra de boss customizada + entidade de teste
  `aquamarine:boss_dummy`; Projeto do Chamariz, Chamariz Abissal, Altar do Abismo
  e mensagens de invocação; 30 texturas por `tools/gen_textures.py`.
  **Testado:** `./gradlew build` (7 testes JUnit) e `./gradlew runClientGameTest`
  (abre o jogo sozinho, confere Forja, receita exclusiva, bônus de conjunto,
  escudo da barra e altar, e grava 14 screenshots que foram revisados).
  **Não testado:** multiplayer/servidor dedicado; velocidade real de nado do
  bônus (+0,33 de `water_movement_efficiency`, calibrar); Maré minerando
  debaixo d'água; armadura em bebês (sem camada `humanoid_baby`).
  **Falta (fora do escopo):** gatilho do aviso falso, tremor de tela e silêncio
  do oceano na invocação (ver docs/03), o Kraken em si (`KrakenSummoning.spawnKraken`
  é o gancho), Nuvem de Tinta e Pressão (usar `OrichalcumEffects`).
  **Próximo passo:** o Adriano abrir o jogo (`./gradlew runClient`, aba
  "Aquamarine") e liberar o próximo marco; o cliente antigo aberto desde 12:00
  pela pasta principal não tem este código.

- 24/10/2026 12:55 · Estruturador · Checkpoint na branch `estruturador/entrega-1`
  (build verde, 7 testes JUnit): blocos A, B e C escritos (registros, config, aba,
  tags, lang, Oricálcio completo, Forja Abissal, Óculos, texturas por
  `tools/gen_textures.py`). Falta: testar no jogo, docs e merge na `main`.

- 24/10/2026 · Claude · Repositório criado, docs organizados, JDK 25 instalado,
  AGENTS.md e CONTEXTO.md criados. Um óculos foi implementado por engano e
  removido a pedido. Falta: decidir o que sai do planejamento primeiro.
