# Histórico de handoffs (Entregas 1 e 2, Fase 3, áudio, Atlas, Fase 4 e release 0.5.0)

> **Dono de:** o registro antigo de entregas, tirado do [CONTEXTO.md](../CONTEXTO.md) em 25 e 26/10/2026 (a última leva, da
> Fase 4 e da `0.5.0`, saiu na release `0.6.0`) para
> ele ficar curto. **Somente leitura:** descreve o que cada agente fez e viu naquele momento; não é estado
> atual (estado: [CONTEXTO.md](../CONTEXTO.md) e [07-roadmap.md](07-roadmap.md)). Duplicatas de merge foram
> removidas; a ordem é a do registro original (mais recente no topo, com alguns blocos fora de ordem).


- 26/10/2026 · Claude Sonnet 5.5 · **Organização dos .md** (só docs, na `main`, sem commit ainda): convenção **release `0.N.0` = Fase `N`, sem Fase 5** ([docs/00-decisoes.md](00-decisoes.md), "Versões e fases"; tabela em [docs/07-roadmap.md](07-roadmap.md), "Releases e fases"). **Fase 6** planejada e organizada em [docs/11-fase6.md](11-fase6.md) (faixas 6A a 6H, ordem sugerida, aceite) com as perguntas F6-1 a F6-8 em "Em aberto"; a **4C foi absorvida pela 6A** (spec segue em [docs/10-fase4.md](10-fase4.md), §3). Banner de fase único em CONTEXTO (AGENTS.md agora só aponta), índice do README em grupos, plano de [docs/resumo.md](resumo.md) renumerado (a antiga "Fase 5, Kraken completo" e a Cidade Perdida ficaram **sem fase**), registros antigos de CONTEXTO movidos para [docs/historico.md](historico.md), linha velha da Fase 4 tirada de "Em andamento". **Testado:** todos os links relativos dos .md resolvem; Atlas regerado (mostra `11-fase6.md`) e `tools/atlas/tests` verdes. Nenhum arquivo de jogo mudou. **Falta:** o Adriano liberar a Fase 6 (ou a 6A1) e responder F6-1 a F6-8.

- 26/10/2026 · Claude Opus 5.5 · **Resumo geral** em [docs/resumo.md](resumo.md): feito, o que falta e plano proposto em tabelas; achado da progressão no sobrevivência travada registrado em "Em aberto" ([docs/00-decisoes.md](00-decisoes.md)) · falta o Adriano escolher a ordem do plano.

- 26/10/2026 · Claude Opus 5.5 · **Release 0.5.0** no GitHub (aba Releases, `Aquamarine-0.5.0.zip` com instalador e servidor, mais o `aquamarine-0.5.0.jar` solto para CurseForge), com tudo da Fase 4 até a 4G. **Testado:** `build` e a parte `multiplayer` do `runClientGameTest` (servidor dedicado, cliente pela rede). **Falta:** teste com os amigos do Adriano, 4C e 4S.

- 26/10/2026 · Claude Opus 5.5 · **Fase 4: 4B, 4D, 4E, 4F e 4G na `main`** (worktree `aquamarine-claude-kraken`, branch `claude/fase4`; painel em `docs/10-fase4.md`). 4B: Oricálcio → Abissal com apelido dos IDs antigos (`addAlias`). 4D: oficina da Vila, troféus e Boia de Âncora em 3D (`tools/gen_phase4_art.py`, `FacingShapeBlock`), ícones novos das pontas e do mergulho. 4E: 35 blocos aquáticos e 35 vidros rachados (`tools/gen_phase4_building.py` gera também o `ModBuildingBlocks`), a onda de choque do Kraken racha os vidros. 4F: Neutrônio inteiro (minério, Forja, têmpera no fundo, Molde do Kraken, ferraria, bônus na água) no estilo Avaritia (`tools/gen_phase4_neutronium.py`, `NeutroniumArmorRenderer`). 4G: Cavitador (modelo 3D com carga, pulso, arpão, partículas). **Testado:** compilação, testes de unidade e as partes `showcase`, `forge`, `creative`, `gear`, `harbor`, `pressure`, `blocks`, `building` e `neutronium` do `runClientGameTest`. **Não testado:** suíte inteira de uma vez (memória, com o jogo LAN do Adriano aberto) e multiplayer. **Falta:** 4C (partes de colisão e movimento do Kraken) e 4S (sons, depois). Aviso antigo, não é desta fase: o Porto loga `joint="rolling"` inválido nos blocos de quebra-cabeça.

- 25/10/2026 · Claude Opus 5.5 · **Fase 4 aberta e 4A entregue** (branch `claude/fase4`): plano completo em `docs/10-fase4.md` (painel do que já foi feito, ordem 4A a 4G e 4S). **4A:** Cria, Ovo e Tentáculo refeitos por um gerador único (`tools/models/spawnlings/build_model.py`: Java, texturas, `_glow`, UV, `.bbmodel` e `rig.json`), paleta do Kraken adulto, tentáculo em S de seção arredondada (hitbox 2 x 9), Cria com olhos em fenda e barbatanas laterais (hitbox 1,0 x 1,3), ovo em três passadas (o filhote aparece; antes a membrana o escondia), camada de brilho nas três. O Atlas lê o `rig.json` (v2 das três no registro, membrana translúcida no visualizador). **Testado:** compilação, parte `spawnlings` do `runClientGameTest` (capturas), `tools/atlas/tests/test_catalog.py`. **Não testado:** suíte inteira (falta memória com o jogo LAN do Adriano aberto). **Falta:** 4B em diante.

- 25/10/2026 · Codex · Ngrok reconfigurado com o token da segunda conta autorizada pelo Adriano (credencial somente na configuração local do ngrok). Agente e servidor estático ativos; **novo bloqueio: ERR_NGROK_382**, e-mail do administrador da conta ainda não verificado. O endereço atribuído em build/atlas-public-url.txt ainda não serve o Atlas publicamente. Falta o Adriano verificar o e-mail no painel do ngrok; depois conferir novamente o acesso público.

- 25/10/2026 · Claude (orquestrador, Codex+Claude via CLI) · **UV do ovo, da Cria e do tentáculo do Kraken corrigidos na branch `claude/blocos-textura` (commit `5388c6e`, worktree `../aquamarine-orq-blocos`, NÃO mergeada).** Causa: `build_model.py` desenhava um retângulo por peça em vez da rede box-UV (faces transparentes/sobrepostas); regenerados texturas, `.bbmodel`, templates e os 3 `*Model.java` (geometria e animações iguais), com `tools/models/spawnlings/check_uv.py` validando. `KrakenEggRenderer` passava `-1` como cor de contorno (halo branco) e overlay 0, sem a inversão de entidade. Os 10 blocos foram revisados no jogo: sem defeito. Faltam: aceite visual do Adriano com água/animações reais, merge na `main` e novo release (jogo com LAN não foi tocado). Cenas `AQUAMARINE_GAMETEST_ONLY=blocks` e `spawnlings` no GameTest.

- 25/10/2026 · Claude Opus 5.5 · **Release 0.4.0 para teste multiplayer** (GitHub, aba Releases, com o `Aquamarine-0.4.0.zip`). Auditoria de rede: pacotes registrados dos dois lados, sem estado estático compartilhado; **corrigido** a baleia montada que não andava (o dono e a regra da config só existiam no servidor; a montaria anda no cliente do piloto). Parte `multiplayer` nova no `runClientGameTest` (servidor dedicado de verdade com o cliente pela rede). Pacote com instalador de clique duplo sem Java (perfil próprio no Minecraft Launcher), a Fabric API certa e a pasta `Servidor` pronta (**`white-list=false`**: o 26.3 liga a lista por padrão). **Testado:** o pacote de servidor ligado de verdade com um jogador entrando e o Kraken atacando sem erros, o instalador numa `.minecraft` falsa e a suíte do jogo. Gerar a próxima: `tools/release/montar_release.py` (docs/06-tecnico.md, "Release para jogadores"). **Falta:** o teste com os amigos do Adriano.

- 25/10/2026 · Claude Opus 5.5 · **Luta do Kraken (Áudio A-K), Kraken v2 no jogo e 2 vezes maior, criaturas do oceano.** Música por síntese em camadas (Tormenta, Fúria, Abismo: base orquestral com sinos, breakcore com bateria sintetizada e picotada, glória com trombetas) misturada ao vivo pelo cliente conforme a luta (respiro, glória por momento, troca de fase com golpe), stingers de vitória e recuo, 29 efeitos no lugar dos sons vanilla; céu por fase, tempestade e raios, recife morto, crateras e onda de choque (com `mob_griefing` e as opções `krakenStorm`/`krakenWorldDamage`). Kraken v2 do Codex integrado ao gerador (7 juntas, mandíbula, abas, 16 movimentos) e escala 8 (~41 x 25 blocos), com arena, Nós, ataques e Pressão acompanhando; o Atlas mostra o rig de produção. Moréia e baleias voltaram a nascer sozinhas (o padrão de Mob recusava nascer na água) com vagas próprias. **Testado:** JUnit, testes do Atlas e `runClientGameTest` nas partes kraken, visual, attack, worldfx e spawn (densidade medida: ~4 baleias e 1 Moréia perto do jogador no oceano fundo). Prévia de áudio: `build/audio-preview/index.html` na worktree `aquamarine-claude-audio`. **Falta:** o Adriano ouvir e lutar no jogo (portão 4) e o A1 (ambiência).

- 25/10/2026 · Codex · Players de áudio do Atlas agora mostram duração, barra arrastável para buscar qualquer posição e saltos de 15 s. Os servidores local (8766) e público estático (8767) aceitam Range HTTP, pedido parcial que permite ao navegador carregar do trecho escolhido; metadados carregados ao abrir. **Verificado nos dois servidores:** pedido bytes 1000–1999 retornou HTTP 206 e exatamente 1.000 bytes. Portal refeito com 72 áudios. Nenhuma mudança no jogo. Handoff em [ferramentas do Atlas](../tools/atlas/README.md).

- 25/10/2026 · Codex · Compartilhamento do Atlas por ngrok solicitado: agente instalado em LocalAppData/Programs/ngrok e autenticação configurada fora do repositório. Servidor estático separado pronto em 127.0.0.1:8767, sem API de atualização (PID em build/atlas-public.pid). **Bloqueio:** ERR_NGROK_334; domínio da conta já serve “Central Ecommerce”, não o Atlas. Túnel existente preservado. Falta liberar o domínio ou usar outra conta; nenhum link público do Atlas foi criado.

- 25/10/2026 · Codex · **Atlas técnico entregue na main**, retomado do agente anterior: sete rigs, 45 clipes no catálogo e oito versões preservadas (61 clipes contando a v1 do Kraken); Kraken v2 com mandíbula, abas e sete juntas por braço, exclusivo do site. Áudio original e entregas em andamento do Claude importados em leitura, 80 texturas e documentos no mesmo portal dark. Abrir com `tools/atlas/abrir.ps1` ou `build/atlas/index.html`; servidor local em http://127.0.0.1:8766. **Testado:** seis testes do catálogo, todas as versões no Edge, desktop/celular, áudio decodificado e atualização via HTTP. Nenhum arquivo de jogo alterado. **Falta:** avaliação artística e audição do Adriano; integração do Kraken v2 no jogo não autorizada. Uso e limites: [tools/atlas/README.md](../tools/atlas/README.md).

- 25/10/2026 · Claude Opus 5.5 (áudio) · **Áudio v1, lote A0 entregue** (portão 1 aberto). Pipeline em `tools/audio/` (síntese original por código, finalização com medidas, laços circulares com 2 s de sobra cruzada, prévia HTML, manifesto) e as 6 amostras: `ambient/depth_2`, `entity/whale/song1`, `entity/kraken/roar1`, `item/thruster/fire1`, `music/harbor`, `music/stinger_summon`. **Medido:** loudness nos alvos, pico real ≤ −2,8 dBTP, sem saturação, emendas ≈1 (inaudíveis). **Não avaliado:** a qualidade subjetiva (agentes não ouvem). **Falta:** o aval do Adriano em `build/audio-preview/index.html`; na mesma página, 3 peças do MusicGen local (as melhores de 6 tentativas) para comparar com a síntese; as outras 3 vieram fracas (ruído, só grave, fina). Os sons ainda não estão registrados (`sounds.json`, `ModSounds`): isso é o A1.

- 25/10/2026 · Claude Opus 5.5 (agente único de fechamento, sem Orca) · **Fase 3 fechada nas faixas.**
  3E v2: reputação por jogador (XP das trocas; níveis 10/70/150/250; Mergulhadores com todas as trocas e
  as acima do nível esgotadas, recusadas no servidor), Alerta de Moréia (`harbor/HarborAlert`), Mapa de
  Fossas de Moréia (função `aquamarine:biome_map`), `gen_harbor.py` v2 (80 x 80, cinco prédios, 9
  Mergulhadores, Cais com 6 docas e 2 elevadores de bolhas) e a Torre de Vigia por jigsaw na superfície;
  `/aquamarine harbor alert|reputation`. 3Q (feita por mim, embora marcada para o Codex): parte nova
  `worldgen` do GameTest num mundo normal achou e corrigiu a **Cúpula alagada** em mundo gerado
  (`ignore_waterlogging`) e os **elevadores que não se formavam** (`HarborElevators`); relatório em
  [docs/relatorio-3q.md](relatorio-3q.md). Extras: itens da Fase 3 nas abas vanilla, troféus e blocos
  da Vila na vitrine, JUnit da cadeia de receitas. Organização: todos os .md revistos (decisões, roadmap,
  técnico, itens, Fase 3, README, AGENTS), prompts marcados como histórico, handoffs antigos em
  [docs/historico.md](historico.md) sem duplicatas. **Testado:** `./gradlew build` e a suíte inteira
  do `runClientGameTest` verdes (52 screenshots; prints 35 a 38 conferidos). **Não decidido (Adriano):**
  sons próprios e o que fazer com a branch `estruturador/entrega-1` e as worktrees antigas
  ([00-decisoes.md](00-decisoes.md), "Em aberto"). **Só o Adriano testa:** lista em
  [07-roadmap.md](07-roadmap.md), "Só o Adriano pode fechar"; roteiro em relatorio-3q.

- 24/10/2026 · Claude Opus 5.5 (agente único de fechamento) · **Fechamento entregue na `main`** (3E v1 `8b0cfa5`, docs e revisão no commit seguinte). 3E: Porto dos Náufragos gerado por script (`tools/gen_harbor.py`), 3 profissões com blocos de trabalho e trocas em dados, Eclusa, baús. Revisão: sem TODO/FIXME reais (os "~10" eram a palavra "todo"), sem segredos nem caminhos pessoais versionados, `.gitignore` cobre `__pycache__/`, `*.pyc`, `*.tmp` e `mods/`, versão 0.3.0. **Testado:** `build` e a suíte inteira do `runClientGameTest` (build verde; suíte inteira verde em todas as partes até o Cachalote, que falhou só na rodada completa (jogador fora do cone) e foi corrigido no teste (verde isolado); rodada completa final **verde** (49 screenshots)). **Falta (3E):** reputação por jogador, Alerta de Moréia, Cais, Torre de Vigia, mapas de Fossas/Nereia e Bússola de Maré. **Falta (3Q e só o Adriano):** aceite com 3 clientes reais, multiplayer da Entrega 2, ritmo/dano do Kraken, montaria pilotada, Propulsor e teclas R/V na mão, legibilidade dos avisos (slam, Descarga, Enrolar), Vila e criaturas surgindo num mundo normal (`/locate structure aquamarine:harbor`). **Branches:** todas mergeadas na `main` exceto `estruturador/entrega-1` (6 commits fora); nenhuma apagada, aguarda o Adriano.

- 24/10/2026 · Claude Opus 5.5 (agente único de fechamento) · **3A a 3D na `main`** (`405447b`, `2731476`, `183eca9`). 3A validada: o GameTest achou receitas do Traje que derrubavam o carregamento do mundo e o teste de profundidade que nunca rodou (mundo plano sem mar); corrigidos. 3B: Moréia (modelo por script, 7 partes de colisão, 3 ataques com aviso), Baleia-de-Nereia (domar, Sela, 3 a bordo) e Cachalote, texturas por script. 3C: Arpão de Oricálcio (arremesso do tridente), Aljava, 4 pontas, 7 encantamentos. 3D: Propulsor de Maré. Ícones ilegíveis redesenhados a pedido do Adriano. **Testado:** `build` e `runClientGameTest` (suíte inteira verde até a 3B; `gear` verde isolado). **Não testado:** multiplayer real, montaria pilotada por um jogador de verdade (só o servidor), tecla V/R no jogo, surgimento natural das criaturas no mundo normal. **Falta:** 3E (Vila), 3Q, revisão final. **Como retomar:** ver a linha "Em andamento" e `prompts/fechamento-final.md`.

- 24/10/2026 · Orquestrador · **Agentes do Orca parados a pedido do Adriano** (3A `ctx_e6a27537c07c`, 3B-M2 `ctx_ed1dda7e6bf4`, 3B-M1 `ctx_2a4c060d5656`). **Salvo:** 3A na branch `claude/3A` (`d98ed00`, não mergeada); 3B-M2 na `main` (`176dbb0`); 3B-M1 sem trabalho (disco cheio). Disco C: 22 GB livres após limpar `Temp`, `CrashDumps` e cache de build/imagens do Docker (volumes intactos). **Quem continua:** um Opus 5.5 único, com `prompts/fechamento-final.md` (termina a Fase 3 e faz a revisão/limpeza/README). Pendências só do Adriano: aceite multiplayer real da Entrega 2, ritmo/dano, legibilidade do slam.

- 25/10/2026 · Codex Astra (3B-M2) · **Pronto para integração:** views (`WhaleView`, `SpermWhaleView`), modelos, animações, renderers genéricos, scripts `.bbmodel`/UV e placeholders temporários na `main` (`9184dc6`, `53c4629`, `a64193d`). `./gradlew build --quiet` passou; os modelos têm 34/32 cubos, rigs de 8/7 ossos e 5/4 animações. **Não testado no jogo:** spawn, animações e visibilidade da Sela, pois entidades/comandos só chegam em 3B-W; visual a verificar após 3B-W. **Próximo:** 3B-W integrar as entidades/views; então rodar `runClientGameTest`, registrar até 6 screenshots e substituir os UVs com a textura 3B-T.

- 24/10/2026 · Orquestrador · **Entrega 2 completa nas faixas** (2A–2Q aceitas; 2Q-fix `64a8724`, build verde). **Pendente com o Adriano:** aceite multiplayer real (2 clientes, 3 fases), sensação de dano/ritmo e legibilidade do aviso do slam com a câmera normal. **Fase 3 liberada** (`ccee430`) por pedido do Adriano. **Onda 1 rodando (mesma Run `run_a6bda8ca6927`):** 3A (Claude, `ctx_e6a27537c07c`, terminal/worktree 2B, branch `claude/3A`), 3B-M2 (Codex medium, `ctx_ed1dda7e6bf4`, worktree 2D-M, branch `codex/3B-M2`), 3B-M1 (Codex high, `ctx_2a4c060d5656`, worktree nova 3B-M1). **Próximo:** quando 3B-M1 mergear as views, iniciar 3B-C (deps 3A, 3B-M1); quando 3B-M2 mergear, 3B-W (deps 3A, 3B-M2); depois 3B-T, 3C, 3D, 3E, 3Q.

- 24/10/2026 · Claude Sonnet 5.5 (2Q-fix) · P1: os retângulos ciano eram as partículas de pó `TEAL_LIGHT` (tamanho 1,6, opacas, em anel ao redor da câmera no Rugido/Jato); tamanho 0,5 em `KrakenEffects`, sem retângulo no print `22b_kraken_jato_rugido_raios_impacto`. P2: o aviso da Pancada passou de anel de tinta escura para `END_ROD` + `GLOW` (`KrakenAttacks.warn`, só visual). Testado: `build` e `runClientGameTest`. Não conferido: legibilidade em câmera comum de jogo (o print do teste enquadra o chão).

- 24/10/2026 · Orquestrador · 2P aceita (Kraken 32 x 20, 2A-T aceita), Opus entregou o Kraken vivo (`6325941`, locomoção, ondulação, avisos maiores) e a 2Q (Codex) publicou `docs/relatorio-2q.md` (`0ab8f06`): 2 achados em `KrakenEffects.java` (P1 retângulos ciano em waterJet/roar/bolts; P2 aviso do slam ilegível), enviados ao Opus (`aquamarine-45`) para correção. Build verde na main. **Falta para fechar a Entrega 2:** correção dos 2 achados e o **aceite multiplayer real com 2 clientes e as 3 fases (só o Adriano)**. Fase 3 só depois (`prompts/orquestrador-fase3.md`).

- 24/10/2026 · Codex Astra (2Q) · QA visual registrado em [relatorio-2q.md](relatorio-2q.md): GameTest terminou sem falha/crash; capturas cobrem ataques em grupos, rachaduras 2/3 e nado. Há dois achados de efeitos/legibilidade para Tasks do Claude; FakePlayer simulou segundo jogador no servidor, mas multiplayer real e sincronização da barra entre clientes ficam para validação do Adriano.

- 24/10/2026 · Claude Opus 5.5 (Kraken vivo, pedido do Adriano via orquestrador) · **Mergeado na main** (`6325941`). Locomoção no servidor (`entity/boss/KrakenMovement`): circula o centro, avança contra o alvo de aggro e recua, preso a 12 blocos de onde surgiu (cabe na arena de 64 com o corpo de 32), velocidade por fase (fase 3 +30%), altura por fase (sobe na 2, desce na 3); para atordoado, em transição ou com `ai off`, e anda devagar durante um aviso; centro salvo no mundo. Tentáculos ociosos acompanham o corpo (`KrakenAttacks.follow`); dano, escudo, ataques e regras intocados. Modelo: respiração, onda contínua da raiz à ponta dos tentáculos, cabeça oscilando, braçadas e inclinação ao nadar, tudo mais forte a cada fase (procedural em `KrakenModel.alive`). Avisos dos ataques 30-40% mais amplos; rastro de bolhas ao nadar. **Atenção:** o `MODEL_SCALE = 4` da 2P foi para o template (`tools/models/kraken/java/KrakenRenderer.java.template`): edite o template, não o `.java` gerado. **Testado:** `./gradlew build` (JUnit `KrakenMovementTest`) e `runClientGameTest` inteiro verdes; 8 fotos do teste visual conferidas (Kraken grande, poses legíveis, rachaduras nas 3 fases, Kraken deslocado após nadar). **Não testado:** multiplayer; locomoção numa Cratera real (colisão com terreno irregular); o movimento contínuo só aparece em jogo, não em foto.

- 24/10/2026 · Claude Sonnet 5.5 (2P) · Fecha o Kraken: mergeei a branch do Opus (16 animações, partículas/sons por animação em `KrakenEffects`, rachaduras e brilho, `KrakenVisualGameTest`) com a main. Tamanho final **32 x 20** (Adriano pediu maior que 24 x 16): hitbox em `ModEntities.KRAKEN`, `clientTrackingRange` 48, `MODEL_SCALE` 4, Nós de Maré ×4 (`TideNodes.WORLD_SCALE`), anel de tentáculos 18, nasce 24 abaixo da superfície, arena de teste 129 x 64, e o Kraken não empurra mais jogadores (`doPush` vazio). Regras e dano intactos (raios de Tinta/Redemoinho/Rugido são do centro do Kraken e não foram mexidos). **Testado:** `build` e a suíte inteira do `runClientGameTest` (inclui o teste visual; a IA do `AttackGameTest` usa vida alta para o jogador sobreviver); 2A-T aceita (foto `26_kraken_rachaduras_fases_1_2_3` com as 3 fases e rachaduras visíveis). **Não testado:** multiplayer; spawn real no mar com o novo tamanho (mede 20 de altura, água funda exigida é 30); calibragem de Rugido/Tinta/Redemoinho para o corpo maior. **Próximo:** locomoção/movimento do corpo (Opus) e a 2Q.

- 24/10/2026 · Orquestrador · **Fechamento da Entrega 2 (pedido do Adriano: terminar a Entrega 2 e então iniciar a Fase 3).** Fase 3 preparada em `25630e7`. **Rodando:** 2P (Claude, dispatch `ctx_ee6ab56345b5`, terminal da worktree 2B): mergeia `opus/kraken-anim` com a main, hitbox/escala 24 x 16 (ou ~32 x 20 se ainda parecer pequeno), aceite da 2A-T, merge na main. **Depois:** Opus 5.5 (sessão `aquamarine-45`) faz o Kraken bem mais vivo (idle/swim procedural, avisos com amplitude, partículas, locomoção no servidor), 2Q (Codex, 2 clientes) e o aceite multiplayer (só o Adriano julga). **Só então** o orquestrador da Fase 3 (`prompts/orquestrador-fase3.md`, checklist em docs/07-roadmap.md). O Adriano pediu o Kraken "maior e se mexendo muito mais" porque estava estático nos testes dele.

- 24/10/2026 · Claude Sonnet 5.5 (2D-T) · Texturas do tentáculo, da Cria e do ovo geradas por `tools/gen_spawnlings_texture.py` sobre os `uv.png` do Codex (paleta mais clara e bioluminescente: ciano/roxo/magenta, contorno escuro, pontos luminosos). Se `tools/models/spawnlings/build_model.py` rodar de novo, ele sobrescreve os PNGs: rode o script depois. **Testado:** `build` e `runClientGameTest` (3 close-ups `23_textura_ovo`, `24_textura_cria`, `25_textura_tentaculo`). **Não conferido bem:** nos prints o Kraken ocupa o centro e as criaturas ficam pequenas/fora de eixo; a leitura das cores está boa, mas falta um enquadramento melhor (afastar o Kraken do teste) para avaliar os detalhes de pixel. Próximo: 2A-T e 2Q.

- 24/10/2026 · Claude Opus 5.5 (animações/partículas do Kraken) · **Parcial, sem merge na main.** Branch `opus/kraken-anim` (`eeb9fc5`, publicada; já com a 2D-C, ainda sem 2D-C.2/2D-M). Pronto: 16 animações como dados em `tools/models/kraken/kraken_animations.py` (antecipação, tremor até o tempo de aviso, golpe, retorno; `build_model.py` gera `KrakenAnimations.java` e o `.bbmodel` e roda a textura, sem sobrescrever `kraken.png`); rachaduras em cascas sobre o manto + `kraken_glow.png` emissiva (vistas nas fases 1, 2 e 3); `KrakenRenderer.MODEL_SCALE` para o tamanho 24 x 16, giro de 180° (o modelo olhava para trás) e luz mínima 9; `client/KrakenEffects` (partículas e sons por animação, rachaduras e carapaça, a partir do estado sincronizado); `KrakenVisualGameTest` (7 fotos). Corrigido também o `rewards()`, que já falhava na main (o Kraken do Altar sai com `discard`). **Testado:** `./gradlew build` verde; teste visual verde rodando sozinho (prints conferidos). **Não testado:** a suíte inteira com o teste visual no fim; a última rodada parou com `IOException: Espaço insuficiente no disco` (C: com 0 MB livres, causa fora desta faixa). **Próximo:** liberar disco, mergear a main de novo, `./gradlew build runClientGameTest` e mergear na main.

- 24/10/2026 · Orquestrador · 2D-C.2 e 2D-M aceitas na main (`6c9062c`/`cf79253`, build verde): tentáculo, Cria e ovo (entidades, views, renderers do Codex, `/aquamarine kraken tentacle|young|egg` com `anim`). **Visto pelo Codex:** criaturas pequenas e de baixo contraste sob a água (placeholders de UV). **Rodando:** 2D-T (Claude, dispatch `ctx_25e0c5168c80`, texturas finais) e o Opus 5.5 fora do Orca (animações/partículas do Kraken). **Falta:** aceite parcial da 2A-T (rachaduras), faixa de tamanho 24 x 16, 2Q (QA com 2 clientes, Codex), multiplayer real. Aberto: tentáculo criado por comando não tem dono e não some com o Kraken.

- 24/10/2026 · Codex Astra · **2D-M entregue** nos commits `31f3620` (modelos/views/renderers) e `26f6fa5` (UVs como placeholders provisórios) na `main`. Tentáculo, Cria e ovo aparecem pelos comandos; `compileJava`/`compileClientJava` e `runClientGameTest` concluídos, com comandos de spawn e animação executados. Conferi as capturas `21_criaturas_comando` e `22_criaturas_anim`: a cena aquática mostra os modelos a distância, com leitura discreta sob a água; a 2D-T deve substituir os placeholders e melhorar o contraste. Próximo: 2D-T aplicar as texturas finais.

- 24/10/2026 · Claude Sonnet 5.5 (2D-C.2) · `TentacleEntity` e `YoungKrakenEntity` implementam `TentacleView`/`YoungKrakenView` (animações sincronizadas por `AnimationSync`; os ataques disparam `rise`/`slam`/`grab`/`release`, a Cria dispara `hatch`/`attack`/`death`), novo `KrakenEggEntity` (`KrakenEggView`: pulsa no aviso das Crias e choca no golpe), renderers do Codex registrados no lugar dos `NoopRenderer`, comandos `/aquamarine kraken tentacle|young|egg` (cria à frente) e `... anim <nome>`. **Testado:** `build` e `runClientGameTest` (contagens por comando e 2 screenshots: as três criaturas aparecem). **Não conferido:** a aparência/animações do ponto de vista do Codex (2D-M faz os screenshots); tentáculo criado por comando não tem dono (não some com o Kraken). Também: `KrakenWorldData.forgetHeart` (só testes) para o teste de recompensas da 2E não depender do Kraken do Altar.

- 24/10/2026 · Orquestrador · **Estado:** Codex atualizado para 0.164.0 (o 0.163 falhava no IPC do Orca). 2D-C aceita na main (`ec9c4fb`, build verde: 8 tentáculos, Cria, 10 ataques). 2D-M: modelos/views na main (`31f3620`), screenshots pendentes (dispatch `ctx_35368d517fda`, Codex medium, worktree 2D-M); autorizado copiar `uv.png` como textura provisória.
  **Rodando:** 2D-C.2 (Claude, dispatch `ctx_fb3903c4ff13`, terminal da worktree 2B): entidades implementam as views do Codex (pacote `com.aquamarine.entity`), ovo, renderers reais no lugar de `NoopRenderer`, comandos `/aquamarine kraken tentacle|young|egg`. Depois: Codex fecha 2D-M com screenshots; iniciar 2D-T (texturas), a faixa de tamanho 24 x 16 + rachaduras visíveis (pendência da 2A-T) e a 2Q. 2A-T aceita parcial.
  Pedido do Adriano: um Opus 5.5 (fora do Orca) pode fazer animações/partículas/avisos do Kraken, só Entrega 2; deve declarar "jogo" e não tocar a lógica de ataques nem `tools/models/spawnlings`. Fase 3 segue bloqueada.

- 24/10/2026 · Claude Sonnet 5.5 (faixa 2D-C) · 8 `TentacleEntity` (60 de vida, cortado volta em 30 s; os 8 cortados expõem o Kraken), `YoungKrakenEntity` (Cria) e os 10 ataques com aviso (partículas, som e a animação do contrato) em `entity/boss/KrakenAttacks` (+ `KrakenAttackType`); sorteio automático por fase/simultâneos `2 + n÷3`; `/aquamarine kraken attack <nome>` e `ai on|off`. Valores adotados em `docs/02-kraken.md`. Ajuste na Pressão: o afundar agora é um modificador do atributo `GRAVITY` (somar velocidade no servidor acumulava sem parar).
  **Testado:** JUnit `KrakenAttackTypeTest`; `runClientGameTest` (`AttackGameTest`): os 8 tentáculos, o dano de Pancada (12), Agarrão (6/s, cortar solta, Tônico solta em ~2,5 s), Jato (10), Raios (14 fora da água, 0 submerso), Emboscada (10), Agarrão Duplo parcial, Tinta (armadura completa protege), Redemoinho (puxa), Crias (3), Rugido (quebra vidro e derruba a Boia), 8 cortados expõem, IA automática; 3 screenshots (avisos de Pancada e Jato conferidos).
  **Não testado:** multiplayer (simultâneos > 2, alvo por aggro), difícil abissal, Kraken salvo e recarregado com tentáculos, barcos (Pancada/Rugido), Amuleto da Corrente (item não existe). **Pendente de outras faixas:** os renderers de `TentacleEntity` e `YoungKrakenEntity` são `NoopRenderer` até a 2D-M/2D-T (as entidades são invisíveis; o aviso é só por partículas); animações `slam`/`grab`... já disparam no `KrakenView`. Os testes antigos desligam a IA (`attacks().setEnabled(false)`).

- 24/10/2026 · Orquestrador · **Retomada.** 2A-T entregue **parcial** (textura por `tools/gen_kraken_texture.py` na main, build verde; aceite visual NÃO cumprido: rachaduras `crack_*` não aparecem, Kraken pequeno e escuro; `build_model.py` sobrescreve `kraken.png`, rodar o script da textura depois).
  Fica para o polimento de tamanho 24 x 16 (Codex: rachaduras maiores/emissivas). **2D-C iniciada** (Claude, terminal da worktree 2B, dispatch `ctx_731e88465b7b`).
  **2D-M (Codex) BLOQUEADA:** o Codex 0.163 falha com erro de IPC do Orca (`unknown field result_buffered_for_ns`) e não consegue chamar o `orca`; precisa atualizar o Codex (`npm install -g @openai/codex`, decisão do Adriano) e reenviar a tarefa no terminal `term_cca07849...` (worktree 2D-M, task `task_c82174f95651`).
  Bloqueia também 2D-T, o polimento de tamanho (parte do modelo) e a 2Q. Revisado o commit `e3dd518` do Codex (só docs; Fase 3 bloqueada, sem contradição).

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

- 24/10/2026 · Claude Sonnet 5.5 (2A-T) · Textura do Kraken por `tools/gen_kraken_texture.py` (box UV do `KrakenModel`, 128x128), sem mudar o renderer; teste de cliente fotografa as 3 fases. **Atenção:** `build_model.py` do Codex sobrescreve `kraken.png` com o uv.png; rode o meu script depois. **Não aceito ainda:** as rachaduras (geometria `crack_*` de 1 px do Codex) não aparecem nos prints, o Kraken é pequeno (~2 blocos) e escuro na água; deve melhorar com o tamanho final 24x16 e/ou rachaduras maiores/emissivas (pedir ao Codex).

## Aviso antigo (24/10/2026)

Leia este aviso ao retomar uma sessão; ele registra a organização da documentação,
sem iniciar, cancelar ou atribuir tarefas a outros agentes.

- A fonte dos marcos é [docs/07-roadmap.md](07-roadmap.md). Entrega 1 está
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
- A [Fase 3](08-fase3.md) foi liberada em 24/10/2026 e fechada por um agente único
  até a 3E v1; o que falta está no handoff abaixo e em [07-roadmap.md](07-roadmap.md).
- Ao voltar à própria worktree, sincronize com a `main` preservando alterações
  locais; só marque um marco após o aceite e registre pendências no handoff.
