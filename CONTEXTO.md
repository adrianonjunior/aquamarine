# CONTEXTO.md: coordenação ao vivo entre agentes

> **Dono de:** quem está fazendo o quê agora e o registro recente de entregas.
> Não repete decisões (ver [docs/00-decisoes.md](docs/00-decisoes.md)), plano de
> marcos (ver [docs/07-roadmap.md](docs/07-roadmap.md)) nem ambiente técnico
> (ver [docs/06-tecnico.md](docs/06-tecnico.md)). Regras: [AGENTS.md](AGENTS.md).
> Registro antigo (Entregas 1 e 2): [docs/historico.md](docs/historico.md).

> **FASE ATUAL: ENTREGA 1, ENTREGA 2 E FASE 3 IMPLEMENTADAS** (faixas fechadas em 25/10/2026). Nada além
> está liberado: a próxima etapa (Cidade Perdida) só começa quando o Adriano liberar.

## Aviso para todos os agentes — 25/10/2026
Leia ao retomar uma sessão.

- Todas as faixas das Entregas 1 e 2 e da Fase 3 estão na `main`, com `build` e a suíte inteira do
  `runClientGameTest` verdes. O que falta é o aceite com jogadores reais e o que só o Adriano julga:
  [docs/07-roadmap.md](docs/07-roadmap.md), "Só o Adriano pode fechar".
- A divisão Claude/Codex é **preferencial**: a pedido do Adriano, qualquer agente faz qualquer faixa
  ([docs/00-decisoes.md](docs/00-decisoes.md), linha "Agentes"). Os arquivos de `prompts/` são histórico.
- Nenhum agente do Orca está trabalhando; a Run antiga (`run_a6bda8ca6927`) só tem terminais retidos,
  sem tarefa viva.

## Em andamento
Registre aqui antes de começar e apague a linha ao terminar. "Recurso" = o que
não pode ser usado por dois agentes ao mesmo tempo (jogo aberto, Gradle).

| Tarefa | Agente | Recurso | Desde |
|---|---|---|---|
| Áudio v1, lote A0 (pipeline em `tools/audio/` e prova de qualidade de 6 amostras; depois para no portão do Adriano) | Claude Opus 5.5 (worktree `aquamarine-claude-audio`) | GPU (MusicGen local), jogo só no fim | 25/10/2026 |

## Registro de handoff
Uma linha por entrega, a mais recente no topo: `data · agente · o que mudou · o que falta`.

- 25/10/2026 · Claude Opus 5.5 (agente único de fechamento, sem Orca) · **Fase 3 fechada nas faixas.**
  3E v2: reputação por jogador (XP das trocas; níveis 10/70/150/250; Mergulhadores com todas as trocas e
  as acima do nível esgotadas, recusadas no servidor), Alerta de Moréia (`harbor/HarborAlert`), Mapa de
  Fossas de Moréia (função `aquamarine:biome_map`), `gen_harbor.py` v2 (80 x 80, cinco prédios, 9
  Mergulhadores, Cais com 6 docas e 2 elevadores de bolhas) e a Torre de Vigia por jigsaw na superfície;
  `/aquamarine harbor alert|reputation`. 3Q (feita por mim, embora marcada para o Codex): parte nova
  `worldgen` do GameTest num mundo normal achou e corrigiu a **Cúpula alagada** em mundo gerado
  (`ignore_waterlogging`) e os **elevadores que não se formavam** (`HarborElevators`); relatório em
  [docs/relatorio-3q.md](docs/relatorio-3q.md). Extras: itens da Fase 3 nas abas vanilla, troféus e blocos
  da Vila na vitrine, JUnit da cadeia de receitas. Organização: todos os .md revistos (decisões, roadmap,
  técnico, itens, Fase 3, README, AGENTS), prompts marcados como histórico, handoffs antigos em
  [docs/historico.md](docs/historico.md) sem duplicatas. **Testado:** `./gradlew build` e a suíte inteira
  do `runClientGameTest` verdes (52 screenshots; prints 35 a 38 conferidos). **Não decidido (Adriano):**
  sons próprios e o que fazer com a branch `estruturador/entrega-1` e as worktrees antigas
  ([00-decisoes.md](docs/00-decisoes.md), "Em aberto"). **Só o Adriano testa:** lista em
  [07-roadmap.md](docs/07-roadmap.md), "Só o Adriano pode fechar"; roteiro em relatorio-3q.

- 24/10/2026 · Claude Opus 5.5 (agente único de fechamento) · **Fechamento entregue na `main`** (3E v1 `8b0cfa5`, docs e revisão no commit seguinte). 3E: Porto dos Náufragos gerado por script (`tools/gen_harbor.py`), 3 profissões com blocos de trabalho e trocas em dados, Eclusa, baús. Revisão: sem TODO/FIXME reais (os "~10" eram a palavra "todo"), sem segredos nem caminhos pessoais versionados, `.gitignore` cobre `__pycache__/`, `*.pyc`, `*.tmp` e `mods/`, versão 0.3.0. **Testado:** `build` e a suíte inteira do `runClientGameTest` (build verde; suíte inteira verde em todas as partes até o Cachalote, que falhou só na rodada completa (jogador fora do cone) e foi corrigido no teste (verde isolado); rodada completa final **verde** (49 screenshots)). **Falta (3E):** reputação por jogador, Alerta de Moréia, Cais, Torre de Vigia, mapas de Fossas/Nereia e Bússola de Maré. **Falta (3Q e só o Adriano):** aceite com 3 clientes reais, multiplayer da Entrega 2, ritmo/dano do Kraken, montaria pilotada, Propulsor e teclas R/V na mão, legibilidade dos avisos (slam, Descarga, Enrolar), Vila e criaturas surgindo num mundo normal (`/locate structure aquamarine:harbor`). **Branches:** todas mergeadas na `main` exceto `estruturador/entrega-1` (6 commits fora); nenhuma apagada, aguarda o Adriano.

- 24/10/2026 · Claude Opus 5.5 (agente único de fechamento) · **3A a 3D na `main`** (`405447b`, `2731476`, `183eca9`). 3A validada: o GameTest achou receitas do Traje que derrubavam o carregamento do mundo e o teste de profundidade que nunca rodou (mundo plano sem mar); corrigidos. 3B: Moréia (modelo por script, 7 partes de colisão, 3 ataques com aviso), Baleia-de-Nereia (domar, Sela, 3 a bordo) e Cachalote, texturas por script. 3C: Arpão de Oricálcio (arremesso do tridente), Aljava, 4 pontas, 7 encantamentos. 3D: Propulsor de Maré. Ícones ilegíveis redesenhados a pedido do Adriano. **Testado:** `build` e `runClientGameTest` (suíte inteira verde até a 3B; `gear` verde isolado). **Não testado:** multiplayer real, montaria pilotada por um jogador de verdade (só o servidor), tecla V/R no jogo, surgimento natural das criaturas no mundo normal. **Falta:** 3E (Vila), 3Q, revisão final. **Como retomar:** ver a linha "Em andamento" e `prompts/fechamento-final.md`.

- 24/10/2026 · Orquestrador · **Agentes do Orca parados a pedido do Adriano** (3A `ctx_e6a27537c07c`, 3B-M2 `ctx_ed1dda7e6bf4`, 3B-M1 `ctx_2a4c060d5656`). **Salvo:** 3A na branch `claude/3A` (`d98ed00`, não mergeada); 3B-M2 na `main` (`176dbb0`); 3B-M1 sem trabalho (disco cheio). Disco C: 22 GB livres após limpar `Temp`, `CrashDumps` e cache de build/imagens do Docker (volumes intactos). **Quem continua:** um Opus 5.5 único, com `prompts/fechamento-final.md` (termina a Fase 3 e faz a revisão/limpeza/README). Pendências só do Adriano: aceite multiplayer real da Entrega 2, ritmo/dano, legibilidade do slam.

- 25/10/2026 · Codex Astra (3B-M2) · **Pronto para integração:** views (`WhaleView`, `SpermWhaleView`), modelos, animações, renderers genéricos, scripts `.bbmodel`/UV e placeholders temporários na `main` (`9184dc6`, `53c4629`, `a64193d`). `./gradlew build --quiet` passou; os modelos têm 34/32 cubos, rigs de 8/7 ossos e 5/4 animações. **Não testado no jogo:** spawn, animações e visibilidade da Sela, pois entidades/comandos só chegam em 3B-W; visual a verificar após 3B-W. **Próximo:** 3B-W integrar as entidades/views; então rodar `runClientGameTest`, registrar até 6 screenshots e substituir os UVs com a textura 3B-T.
