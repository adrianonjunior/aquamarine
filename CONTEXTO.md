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
| Publicar e verificar link ngrok do Atlas de amostra | Codex | Servidor Atlas 8767 e túnel ngrok | 25/10/2026 |
| Áudio v1 (A0 aprovado): **luta do Kraken** (3 trilhas adaptativas, stingers, sons de combate) e **correção do spawn das criaturas do oceano** | Claude Opus 5.5 (worktree `aquamarine-claude-audio`, branch `claude/kraken-audio`) | Gradle na worktree; jogo só no teste final | 25/10/2026 |

## Registro de handoff
Uma linha por entrega, a mais recente no topo: `data · agente · o que mudou · o que falta`.

- 25/10/2026 · Codex · Players de áudio do Atlas agora mostram duração, barra arrastável para buscar qualquer posição e saltos de 15 s. Os servidores local (8766) e público estático (8767) aceitam Range HTTP, pedido parcial que permite ao navegador carregar do trecho escolhido; metadados carregados ao abrir. **Verificado nos dois servidores:** pedido bytes 1000–1999 retornou HTTP 206 e exatamente 1.000 bytes. Portal refeito com 72 áudios. Nenhuma mudança no jogo. Handoff em [ferramentas do Atlas](tools/atlas/README.md).

- 25/10/2026 · Codex · Ngrok reconfigurado com o token da segunda conta autorizada pelo Adriano (credencial somente na configuração local do ngrok). Agente e servidor estático ativos; **novo bloqueio: ERR_NGROK_382**, e-mail do administrador da conta ainda não verificado. O endereço atribuído em build/atlas-public-url.txt ainda não serve o Atlas publicamente. Falta o Adriano verificar o e-mail no painel do ngrok; depois conferir novamente o acesso público.

- 25/10/2026 · Codex · Compartilhamento do Atlas por ngrok solicitado: agente instalado em LocalAppData/Programs/ngrok e autenticação configurada fora do repositório. Servidor estático separado pronto em 127.0.0.1:8767, sem API de atualização (PID em build/atlas-public.pid). **Bloqueio:** ERR_NGROK_334; domínio da conta já serve “Central Ecommerce”, não o Atlas. Túnel existente preservado. Falta liberar o domínio ou usar outra conta; nenhum link público do Atlas foi criado.

- 25/10/2026 · Codex · **Atlas técnico entregue na main**, retomado do agente anterior: sete rigs, 45 clipes no catálogo e oito versões preservadas (61 clipes contando a v1 do Kraken); Kraken v2 com mandíbula, abas e sete juntas por braço, exclusivo do site. Áudio original e entregas em andamento do Claude importados em leitura, 80 texturas e documentos no mesmo portal dark. Abrir com `tools/atlas/abrir.ps1` ou `build/atlas/index.html`; servidor local em http://127.0.0.1:8766. **Testado:** seis testes do catálogo, todas as versões no Edge, desktop/celular, áudio decodificado e atualização via HTTP. Nenhum arquivo de jogo alterado. **Falta:** avaliação artística e audição do Adriano; integração do Kraken v2 no jogo não autorizada. Uso e limites: [tools/atlas/README.md](tools/atlas/README.md).

- 25/10/2026 · Claude Opus 5.5 (áudio) · **Áudio v1, lote A0 entregue** (portão 1 aberto). Pipeline em `tools/audio/` (síntese original por código, finalização com medidas, laços circulares com 2 s de sobra cruzada, prévia HTML, manifesto) e as 6 amostras: `ambient/depth_2`, `entity/whale/song1`, `entity/kraken/roar1`, `item/thruster/fire1`, `music/harbor`, `music/stinger_summon`. **Medido:** loudness nos alvos, pico real ≤ −2,8 dBTP, sem saturação, emendas ≈1 (inaudíveis). **Não avaliado:** a qualidade subjetiva (agentes não ouvem). **Falta:** o aval do Adriano em `build/audio-preview/index.html`; na mesma página, 3 peças do MusicGen local (as melhores de 6 tentativas) para comparar com a síntese; as outras 3 vieram fracas (ruído, só grave, fina). Os sons ainda não estão registrados (`sounds.json`, `ModSounds`): isso é o A1.

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
