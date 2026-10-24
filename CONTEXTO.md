# CONTEXTO.md: coordenação ao vivo entre agentes

> **Dono de:** quem está fazendo o quê agora e o histórico de entregas.
> Não repete decisões (ver [docs/00-decisoes.md](docs/00-decisoes.md)), plano de
> marcos (ver [docs/07-roadmap.md](docs/07-roadmap.md)) nem ambiente técnico
> (ver [docs/06-tecnico.md](docs/06-tecnico.md)). Regras: [AGENTS.md](AGENTS.md).

> **FASE ATUAL: ENTREGA 1 CONCLUÍDA; ENTREGA 2 (Kraken v1) LIBERADA em 24/10/2026.** Nada além delas está liberado: não implemente
> nada (código, texturas, modelos) até o Adriano liberar explicitamente o próximo passo.

## Atualização para todos os agentes — 24/10/2026
Leia este aviso ao retomar uma sessão; ele registra a organização da documentação,
sem iniciar, cancelar ou atribuir tarefas a outros agentes.

- A fonte dos marcos é [docs/07-roadmap.md](docs/07-roadmap.md). Entrega 1 está
  concluída; na Entrega 2, 2A-M, 2A-C, 2B, 2C e 2E foram aceitas na `main`.
  As demais faixas e o aceite multiplayer da Entrega 2 ainda não foram concluídos.
- O último handoff do orquestrador registrou **2A-T e 2D-M em execução**. Isso é
  histórico, não confirmação de atividade neste instante. Antes de assumir uma
  faixa, confira a tabela "Em andamento" e a coordenação existente no Orca.
- O renderer real do Kraken já está registrado no cliente; a textura atual ainda
  é o gabarito UV. Na 2A-T, confira o trabalho existente e foque a textura final
  e a verificação visual das fases, sem refazer o renderer.
- A 2C foi aceita com os ganchos de dano do escudo; Arpão, Carga de Pulso e
  Pilar ainda constam como pendentes no handoff da faixa. Não trate os ganchos
  como itens jogáveis já entregues.
- A [Fase 3](docs/08-fase3.md) está documentada para planejamento e **bloqueada**.
  Sua especificação e as faixas do roadmap não autorizam Tasks, worktrees,
  código, texturas ou modelos antes das condições de liberação nas
  [decisões](docs/00-decisoes.md).
- Ao voltar à própria worktree, sincronize com a `main` preservando alterações
  locais; só marque um marco após o aceite e registre pendências no handoff.

## Em andamento
Registre aqui antes de começar e apague a linha ao terminar. "Recurso" = o que
não pode ser usado por dois agentes ao mesmo tempo (jogo aberto, Gradle).

| Tarefa | Agente | Recurso | Desde |
|---|---|---|---|
| 2D-M (onda 2) | Codex | jogo + Gradle | 24/10/2026 |
| Animações, partículas e avisos do Kraken (Entrega 2; autorizada pelo Adriano no lugar do Codex) | Claude Opus 5.5 (worktree `aquamarine-opus-kraken`, branch `opus/kraken-anim`) | jogo (runClientGameTest) | 24/10/2026 |

## Registro de handoff

- 24/10/2026 · Orquestrador · **Retomada.** 2A-T entregue **parcial** (textura por `tools/gen_kraken_texture.py` na main, build verde; aceite visual NÃO cumprido: rachaduras `crack_*` não aparecem, Kraken pequeno e escuro; `build_model.py` sobrescreve `kraken.png`, rodar o script da textura depois).
  Fica para o polimento de tamanho 24 x 16 (Codex: rachaduras maiores/emissivas). **2D-C iniciada** (Claude, terminal da worktree 2B, dispatch `ctx_731e88465b7b`).
  **2D-M (Codex) BLOQUEADA:** o Codex 0.163 falha com erro de IPC do Orca (`unknown field result_buffered_for_ns`) e não consegue chamar o `orca`; precisa atualizar o Codex (`npm install -g @openai/codex`, decisão do Adriano) e reenviar a tarefa no terminal `term_cca07849...` (worktree 2D-M, task `task_c82174f95651`).
  Bloqueia também 2D-T, o polimento de tamanho (parte do modelo) e a 2Q. Revisado o commit `e3dd518` do Codex (só docs; Fase 3 bloqueada, sem contradição).
Uma linha por entrega, a mais recente no topo: `data · agente · o que mudou · o que falta`.

- 24/10/2026 · Codex (organização documental) · Reconciliou marcos da Entrega 1 e das faixas 2B/2E com o histórico da `main`; corrigiu textos de liberação e da 2A-T, incorporou a documentação planejada da Fase 3 e deixou o aviso "Atualização para todos os agentes" acima. **Sem executar agentes ou build.** Permanecem pendentes as faixas desmarcadas no roadmap e o aceite multiplayer da Entrega 2; a Fase 3 segue bloqueada.

- 24/10/2026 · Orquestrador · **Atualização (limite de uso atingido):** 2B e 2E aceitas e validadas na main (build verde, 473adaf).
  **Ainda rodando:** 2A-T (Claude, terminal da worktree 2A-C, dispatch `ctx_bb7771a97cfe`) e 2D-M (Codex, dispatch `ctx_2776644f41a3`).
  **Próximo orquestrador:** `check --wait`; iniciar a **2D-C** (deps 2A-C, 2B, 2C já mergeadas; reaproveitar o terminal Claude ocioso
  `term_a1116273...` da worktree 2B; usar ModEffects.hasTonic e AnchorBuoyBlock.collapse); depois 2D-T (dep. 2D-M), faixa de tamanho 24 x 16 e 2Q.
  Itens pendentes de 2B/2E: multiplayer, som abafado, troféus no jogo, itens Arpão/Carga de Pulso/Pilar (2C).

- 24/10/2026 · Claude Sonnet 5.5 (faixa 2B) · Pressão 0-4 (`pressure/PressureState` puro + `PressureManager` no servidor, nível enviado por `PressurePayload`), `PressureHud` (vinheta azul no 2, escura e pulsante no 4, batimentos do 3), Tônico de Pressão (item de beber + efeito `ModEffects.PRESSURE_TONIC`, devolve o frasco), Boia de Âncora (`AnchorBuoyItem`/`AnchorBuoyBlock`: 3x3 por 90 s, recarga de 30 s, `AnchorBuoyBlock.collapse` para o Kraken destruir), `OrichalcumEffects.pressureReduction`, texturas por script, receitas e lang pt/en.
  **Testado:** `./gradlew build` (JUnit `PressureStateTest`) e `runClientGameTest` (`PressureGameTest`: níveis 1-4 por tempo, nado lento, ataque lento, dano do nível 4, nível no cliente, Boia, armadura completa, Tônico; 2 screenshots, vinheta do nível 4 conferida).
  **Não testado:** multiplayer, fase 3 em jogo (6 s, só no JUnit), Tônico dado por poção de água real na receita (aceita qualquer `minecraft:potion`), Kraken a destruir a Boia. **Para a 2D-C:** soltura de agarrões 50% mais rápida usa `ModEffects.hasTonic(jogador)`; o Kraken derruba a Boia com `AnchorBuoyBlock.collapse(level, pos)`. Som é o batimento vanilla (`WARDEN_HEARTBEAT`), sem som abafado nem bolhas.

- 24/10/2026 · Orquestrador (Claude Sonnet 5.5) · **Entrega 2 em andamento** (Run `run_a6bda8ca6927`, Orca;
  liberação em `e1d894b`). **Aceitas e validadas na main (build verde):** 2A-M (modelo, UV, 16 animações,
  renderer; QA visual em arena aquática), 2A-C (`KrakenEntity`, `ModEntities.KRAKEN`, `/aquamarine kraken [arena|anim <nome>]`),
  2C (tabela de dano do escudo, Nós de Maré, atordoamento). **Rodando:** 2B, 2E (Claude, worktrees `2B`/`2E`),
  2A-T (Claude, reaproveitou o terminal da worktree `2A-C`), 2D-M (Codex medium, worktree `2D-M`;
  tarefa reenviada por `terminal send` com preâmbulo, pois a original se perdeu). **Pendentes:** 2D-C (dep. 2A-C, 2B, 2C),
  2D-T (dep. 2D-M), 2Q (todas) e a faixa de polimento do tamanho (Kraken final 24 x 16 blocos, ver docs/00-decisoes.md).
  **Abertos da 2C:** itens Arpão, Carga de Pulso e Pilar (só existem os ganchos), visual dos nós (`nodeCenters()`),
  acerto em nó por raio do olhar não testado no jogo. **Lições do Orca:** Claude novo em worktree nova para no aviso
  Bypass Permissions e o Codex no aviso de trust/atualização: o Adriano aceita; depois reenviar a spec com
  `dispatch --dry-run --return-preamble` (trocar `ctx_dryrun` pelo dispatch real) + `terminal send --enter`.
  Terminais ociosos reaproveitáveis: Claude `term_5882b258...` (2A-C), Codex `term_4019b92f...` (2A-M);
  dispatches retidos (`worker-release` deu `retained`). A árvore principal tem edições de outro agente
  (README, docs/00-decisoes, docs/07-roadmap, docs/08-fase3.md): não mexer; validar em worktree temporária.
  **Próximo:** esperar `worker_done` de 2B, 2E, 2A-T e 2D-M (`check --wait`), validar cada um, liberar 2D-C quando 2B e 2C
  estiverem mergeadas, depois 2D-T, 2Q e o relatório final.

- 24/10/2026 · Claude Sonnet 5.5 (faixa 2E) · Aviso falso, efeitos de tela, drops, troféus e primeira vitória. `FalseWarning` (gatilho: tempestade à noite, jogador em oceano profundo, 8% por noite, 3 dias entre avisos; checa a cada 5 s) e `KrakenWorldData` (`SavedData`: quem já tem o Coração + controle do aviso); `SummonEffectPayload` leva ao cliente o silêncio do oceano (0 s), o tremor (5 s, `CameraMixin`) e o escurecer (20 s finais do aviso falso, véu no HUD); `KrakenRewards` entrega, a cada jogador da luta, Olho, 2 a 4 Tinta, 1.000 de XP, Troféu (Dourado no `abissal`) e Coração só na primeira vitória; itens `kraken_eye`, `abyssal_ink`, `kraken_heart`, blocos `kraken_trophy` e `golden_abyss_trophy` (texturas por `tools/gen_textures.py`). **Testado:** `./gradlew build` (JUnit do gatilho) e `runClientGameTest` (3 mortes: primeira vitória, segunda sem Coração, abissal com Dourado; XP; gatilho com sorte, noite repetida, 3 dias de espera, config desligada; sem Kraken no aviso falso; silêncio, tremor e escurecer no cliente; 2 screenshots). **Não testado:** o aspecto dos troféus no mundo (o screenshot pegou só água; modelo conferido só por JUnit de recursos), o som real do silêncio (só o estado), o aviso falso disparando sozinho em oceano profundo real e multiplayer. **Falta:** sons próprios; alvo da recompensa por jogador pode ganhar regra de distribuição se o Adriano preferir loot compartilhado.

- 24/10/2026 · Codex Astra (2A-M.2) · Verificação visual do Kraken no `runClientGameTest` em arena aquática, com gabarito UV e câmera acima do chão; `slam` e `grab` dispararam por comando, teste passou. **Pronto:** modelo e poses visíveis; **não testado nesta faixa:** textura final e combate completo; **próximo passo:** 2A-T aplicar a textura definitiva.
  - `tools/models/kraken/qa/01_kraken_geral.png`: silhueta completa e oito tentáculos aparecem sob a água, com gabarito escuro.
  - `tools/models/kraken/qa/02_kraken_slam.png`: tentáculo do ataque `slam` aberto à esquerda da imagem.
  - `tools/models/kraken/qa/03_kraken_grab.png`: tentáculo do ataque `grab` erguido à direita da imagem.
- 24/10/2026 · Claude Sonnet 5.5 (2C) · Escudo: tabela de dano por fonte (`ShieldDamage`, JUnit), Nós de Maré (`TideNodes`, 4 a 8, por fase, opção `weakPoints`), `KrakenEntity.hitShield(...)` para Pulso/Pilar, `expose(ticks)` (+25%, sem regenerar), `canAct()`, tags `shield_breakers`/`harpoons` e encantamento `shell_breaker`; teste de cliente confere absorve, quebra, regenera e a tabela (verde). **Não feito:** itens Arpão, Carga de Pulso e Pilar (só os ganchos), visual dos nós (usar `nodeCenters()`), acerto em nó por raio do olhar não testado no jogo. **Próximo passo:** 2D-C chama `canAct()`/`expose()`; itens chamam `hitShield`.

- 24/10/2026 · Codex Astra (2A-M) · `KrakenView`, gabarito UV, `.bbmodel` com 191 cubos e 16 clips, classes do modelo/animações/renderer e registro do modelo; `compileJava compileClientJava` passou, `/aquamarine kraken` criou a entidade e os 16 comandos responderam `Animation <nome> played.`
  **Visual não verificado:** no mundo de QA florestal o Kraken surgiu em y=57 sob o terreno; as 9 capturas autorizadas foram usadas sem obter uma vista do modelo. **Próximo passo:** repetir a vista geral e poses em mundo aquático/câmera acima do chão; deixar 2A-M sem marcar até conferir a silhueta.

- 24/10/2026 · Claude Sonnet 5.5 (2A-C.2) · `/aquamarine kraken anim <nome>` dispara as 16 animações do contrato no Kraken mais próximo (sincronizado por dados da entidade); testado no teste de cliente (estado no servidor), **não** conferido visualmente.

- 24/10/2026 · Claude Sonnet 5.5 (faixa 2A-C) · `KrakenEntity` (`entity/`),
  `KrakenStats` e `AggroTracker` (`entity/boss/`, contas puras com JUnit), `KrakenCommand`
  (`/aquamarine kraken` e `/aquamarine kraken arena`, nível 2), `KrakenSummoning.spawnKraken`
  (surge 10 blocos abaixo da água funda mais próxima) e renderer provisório (`NoopRenderer`).
  Pronto: vida/escudo por fase, escala `f`, transição de 3 s, quebra do escudo com atordoamento de 8 s,
  regeneração (20 s sem dano, 10%/s; fase 3 a cada 45 s), aggro de 10 s, fuga (30 s, +10%) e
  abandono (3 min), barra com a vida real. **Detalhe:** o atributo de vida do jogo para em 1024,
  então a vida do Kraken usa o atributo como régua de 0 a 1000; use `realHealth()`/`realMaxHealth()`
  e dê dano por `hurtServer`. `setNoAi(true)` impede `customServerAiStep`: o Kraken não usa.
  **Testado:** `./gradlew build` (JUnit) e `runClientGameTest` (fases, escudo, transição,
  regeneração, aggro, escala com 5 jogadores, 3 screenshots; o modelo ainda é invisível).
  **Não testado:** multiplayer, fuga/abandono (só no código), difícil `abissal`, Kraken salvo e recarregado.
  **Falta:** `KrakenEntity` já implementa `KrakenView` (idle, stunned e phaseChange ligados no cliente;
  os ataques ficam para a 2D-C; troque o `NoopRenderer` pelo renderer do Codex na 2A-T); multiplicadores
  de dano por fonte (2C), ataques (2D), movimento.

- 24/10/2026 14:15 · Estruturador (Claude Opus 5.5) · A pedido do Adriano:
  biomas do mod achados com `/locate biome #aquamarine_<bioma>` (tag
  `minecraft:aquamarine_<bioma>`, exigida pelo teste). Só existe
  `#aquamarine_fossa_de_nereia`, provisória (oceanos profundos). **Falta:** quem
  criar o bioma Fossa de Nereia troca o valor da tag por `aquamarine:fossa_de_nereia`.

- 24/10/2026 14:05 · Estruturador (Claude Opus 5.5) · A pedido do Adriano: JEI
  31.16.0.90 no ambiente de dev, plugin com a categoria "Forja Abissal" e
  descrição curta de cada item (tooltip + informação do JEI), itens também nas
  abas vanilla (a aba "Aquamarine" fica na página 2 do criativo). Testado no
  teste de cliente (screenshots do tooltip e da categoria). **Falta:** quando a
  Pressão e a Nuvem de Tinta existirem, acrescentar esses efeitos às descrições
  da armadura e dos óculos; receitas da Forja no JEI em servidor dedicado.

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
- 24/10/2026 · Codex Astra (2A-M) · `KrakenView`, gabarito UV, `.bbmodel` com 191 cubos e 16 clips, classes do modelo/animações/renderer e registro do modelo; `compileJava compileClientJava` passou, `/aquamarine kraken` criou a entidade e os 16 comandos responderam `Animation <nome> played.`
  **Visual não verificado:** no mundo de QA florestal o Kraken surgiu em y=57 sob o terreno; as 9 capturas autorizadas foram usadas sem obter uma vista do modelo. **Próximo passo:** repetir a vista geral e poses em mundo aquático/câmera acima do chão; deixar 2A-M sem marcar até conferir a silhueta.

- 24/10/2026 · Claude Sonnet 5.5 (2A-T) · Textura do Kraken por `tools/gen_kraken_texture.py` (box UV do `KrakenModel`, 128x128), sem mudar o renderer; teste de cliente fotografa as 3 fases. **Atenção:** `build_model.py` do Codex sobrescreve `kraken.png` com o uv.png; rode o meu script depois. **Não aceito ainda:** as rachaduras (geometria `crack_*` de 1 px do Codex) não aparecem nos prints, o Kraken é pequeno (~2 blocos) e escuro na água; deve melhorar com o tamanho final 24x16 e/ou rachaduras maiores/emissivas (pedir ao Codex).

- 24/10/2026 · Claude Sonnet 5.5 (2A-C.2) · `/aquamarine kraken anim <nome>` dispara as 16 animações do contrato no Kraken mais próximo (sincronizado por dados da entidade); testado no teste de cliente (estado no servidor), **não** conferido visualmente.

- 24/10/2026 · Claude Sonnet 5.5 (faixa 2A-C) · `KrakenEntity` (`entity/`),
  `KrakenStats` e `AggroTracker` (`entity/boss/`, contas puras com JUnit), `KrakenCommand`
  (`/aquamarine kraken` e `/aquamarine kraken arena`, nível 2), `KrakenSummoning.spawnKraken`
  (surge 10 blocos abaixo da água funda mais próxima) e renderer provisório (`NoopRenderer`).
  Pronto: vida/escudo por fase, escala `f`, transição de 3 s, quebra do escudo com atordoamento de 8 s,
  regeneração (20 s sem dano, 10%/s; fase 3 a cada 45 s), aggro de 10 s, fuga (30 s, +10%) e
  abandono (3 min), barra com a vida real. **Detalhe:** o atributo de vida do jogo para em 1024,
  então a vida do Kraken usa o atributo como régua de 0 a 1000; use `realHealth()`/`realMaxHealth()`
  e dê dano por `hurtServer`. `setNoAi(true)` impede `customServerAiStep`: o Kraken não usa.
  **Testado:** `./gradlew build` (JUnit) e `runClientGameTest` (fases, escudo, transição,
  regeneração, aggro, escala com 5 jogadores, 3 screenshots; o modelo ainda é invisível).
  **Não testado:** multiplayer, fuga/abandono (só no código), difícil `abissal`, Kraken salvo e recarregado.
  **Falta:** `KrakenEntity` já implementa `KrakenView` (idle, stunned e phaseChange ligados no cliente;
  os ataques ficam para a 2D-C; troque o `NoopRenderer` pelo renderer do Codex na 2A-T); multiplicadores
  de dano por fonte (2C), ataques (2D), movimento.

- 24/10/2026 14:15 · Estruturador (Claude Opus 5.5) · A pedido do Adriano:
  biomas do mod achados com `/locate biome #aquamarine_<bioma>` (tag
  `minecraft:aquamarine_<bioma>`, exigida pelo teste). Só existe
  `#aquamarine_fossa_de_nereia`, provisória (oceanos profundos). **Falta:** quem
  criar o bioma Fossa de Nereia troca o valor da tag por `aquamarine:fossa_de_nereia`.

- 24/10/2026 14:05 · Estruturador (Claude Opus 5.5) · A pedido do Adriano: JEI
  31.16.0.90 no ambiente de dev, plugin com a categoria "Forja Abissal" e
  descrição curta de cada item (tooltip + informação do JEI), itens também nas
  abas vanilla (a aba "Aquamarine" fica na página 2 do criativo). Testado no
  teste de cliente (screenshots do tooltip e da categoria). **Falta:** quando a
  Pressão e a Nuvem de Tinta existirem, acrescentar esses efeitos às descrições
  da armadura e dos óculos; receitas da Forja no JEI em servidor dedicado.

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
