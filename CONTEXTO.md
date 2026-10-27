# CONTEXTO.md: coordenação ao vivo entre agentes

> **Dono de:** quem está fazendo o quê agora e o registro recente de entregas.
> Não repete decisões (ver [docs/00-decisoes.md](docs/00-decisoes.md)), plano de
> marcos (ver [docs/07-roadmap.md](docs/07-roadmap.md)) nem ambiente técnico
> (ver [docs/06-tecnico.md](docs/06-tecnico.md)). Regras: [AGENTS.md](AGENTS.md).
> Registro antigo (Entregas 1 e 2, Fase 3, áudio, Atlas): [docs/historico.md](docs/historico.md).

> **FASE ATUAL (26/10/2026): FASE 6 PUBLICADA na `0.6.0`.** Entregas 1 e 2 e Fases 3, 4 e 6 estão no jogo.
> **Liberado para trabalho:** a `0.6.1` nas faixas 7A e 7B, até o portão do Gerador de Fenda
> ([docs/12-industria.md](docs/12-industria.md)); o resto da Fase 6 numa `0.6.x` (6A2 e 6A4 sobre a v3a, 6C1; a 6G1
> espera o Accessories para o 26.3), os sons numa subversão própria quando o Adriano pedir, e correções e testes do que já existe. A
> Cidade Perdida e o Kraken completo estão sem fase. Release `0.N.0` = Fase `N`, sem Fase 5
> ([docs/07-roadmap.md](docs/07-roadmap.md), "Releases e fases").

## Aviso para todos os agentes — 26/10/2026
Leia ao retomar uma sessão.

- Todas as faixas das Entregas 1 e 2 e da Fase 3 estão na `main`, com `build` e a suíte inteira do
  `runClientGameTest` verdes. O que falta é o aceite com jogadores reais e o que só o Adriano julga:
  [docs/07-roadmap.md](docs/07-roadmap.md), "Só o Adriano pode fechar".
- A Fase 4 saiu no `0.5.0` e a Fase 6 no `0.6.0` (três Krakens novos: a Lula-colosso é o principal). Mapa de tudo: [README.md](README.md),
  "Índice"; retrato do que falta: [docs/resumo.md](docs/resumo.md).
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
| 0.6.1: 7A, 7B e portão do Gerador de Fenda (worktree `aquamarine-claude-industria`, branch `claude/industria`) | Claude Opus 5.5 | Gradle da worktree; jogo de teste próprio (não a instância LAN do Adriano) | 26/10/2026 |

## Registro de handoff
Uma linha por entrega, a mais recente no topo: `data · agente · o que mudou · o que falta`.
- 26/10/2026 · Claude Opus 5.5 · **Release 0.6.0 (Fase 6) publicada no GitHub** (aba Releases, com o `.zip` e o `.jar`), com os três Krakens novos no jogo: a v3a Lula-colosso é o `aquamarine:kraken`, a v3b e a v3c são `kraken_vampire` e `kraken_ammonite` no `/summon` (`tools/models/kraken_v3/build_game.py`, 16 clipes, `KrakenRenderer.Variant`; Atlas com `v3a-jogo`, `v3b-jogo` e `v3c-jogo`, abrindo na que está no jogo). `version=0.6.0`, pacote por `tools/release/montar_release.py` (a Team Reborn Energy vai dentro do jar). Docs da release organizados (decisões, roadmap, README, resumo, técnico; handoffs da Fase 4 foram para [docs/historico.md](docs/historico.md)). **Testado:** `build`; partes `krakens` (nova), `kraken`, `attack`, `multiplayer`, e as das entregas; testes do Atlas. **Não testado:** o instalador numa `.minecraft` falsa (a sessão não pôde rodar o `.ps1`) e o pacote de servidor ligado com um cliente. **Falta:** 6A2, 6A4 e 6C1 (`0.6.x`), 6G1 quando sair o Accessories, os sons e o teste com os amigos.
- 26/10/2026 · Claude Opus 5.5 · **Fase 6, E4 a E6 na `main` (menos a 6C1 e a 6G1).** 6E: energia da Team Reborn Energy 5.0.0 (dentro do jar): Turbina de Correnteza, Gerador de Fenda, Boia de Ondas, Cabo Aquático e Coletor de Neutrônio com o Pó Estelar (8 pós + Cristal de Maré = 1 Poeira, na Forja) (`com.aquamarine.energy`, `tools/gen_phase6_energy.py`). 6F1: Mesa de Encantamento Aquática (tela do jogo, só encantamentos da água). 6C2: Bigorna de Maré, Mesa de Cartas e Altar de Concha abrem bigorna, cartografia e rebolo. 6B2: Carne de Baleia. 6G2: Radar Aquático (HUD). 6G1 pulada: o Accessories não tem versão para o 26.3. **Testado:** `build`; partes `energy` (nova), `whale`, `kraken` e `multiplayer`. **Não testado:** o Coletor num oceano fundo de verdade (o teste libera o lugar à força) e a energia com máquinas de outros mods. **Falta:** 6C1 (skins 3D dos Mergulhadores), a escolha do Kraken (F6-1b) e, depois dela, 6A2 e 6A4; 6G1 quando sair o Accessories.
- 26/10/2026 · Claude Opus 5.5 · **Fase 6, E3 (6D1 e 6H) na `main`.** Aurium inteiro (perolado, minério sob os oceanos profundos, Forja, Brilho Perolado, entra no Lingote Instável no lugar do Abissal; `tools/gen_phase6_aurium.py`) e 14 blocos de construção e decoração (`tools/gen_phase6_building.py` → `ModDecorBlocks`). **Testado:** `build` e partes `building` (vitrine e Poder do Conduíte do conjunto), `neutronium`, `kraken` e `multiplayer`. **Não testado:** o minério num mundo gerado (a parte `worldgen` não roda o Aurium) e o visual de cada peça de perto. **Falta:** E4 (energia) em diante.
- 26/10/2026 · Claude Opus 5.5 · **Fase 6, E2 (6B1, 6D2, 6D3 e 6F2) na `main`.** 6B1: baleia e cachalote com cabeça, fim do corpo e rabo (`WhalePartEntity`, cabeça sólida). 6D2: Forja Abissal 3D (`tools/gen_phase6_art.py`). 6D3: arpão em voo com modelo e rastro por ponta e para o Cavitador (`HarpoonRenderer`, dado sincronizado no `ThrownTridentMixin`). 6F2: descrições dos 8 encantamentos no livro e no JEI. **Testado:** `build`; partes `showcase`+`forge`, `moray`, `whale`, `gear`, `neutronium`, `kraken` e `multiplayer`. **Não testado:** a montaria de 3 com amigos depois das partes novas (o teste automático com 3 passa). Aviso: a parte `forge` depende da `showcase`, e a `moray` falha se rodar logo depois dela (o jogador fica de armadura). **Falta:** E3 em diante.
- 26/10/2026 · Claude Opus 5.5 · **Fase 6, E1 (6A1 e 6A3) na `main`** (branch `claude/fase6`). 6A1: três candidatas do Kraken só no Atlas, `kraken/v3a` Lula-colosso, `v3b` Vampiro-abissal e `v3c` Amonita-colosso (gerador `tools/models/kraken_v3/build_candidates.py`, textura, `_glow`, rig no contrato e 6 clipes; o jogo não mudou); o Atlas acende o brilho e compara versões (`tools/atlas/tests/candidates.mjs`). 6A3: tentáculos nascem do leito ou sobem do fundo com aviso (tinta, bolhas, chão rompido) e afundam antes de voltar para casa. **Testado:** `build`, partes `attack`, `kraken` e `multiplayer` do `runClientGameTest`, `tools/atlas/tests` (catálogo e navegador). **Não testado:** o visual da subida no jogo com o Adriano (a captura `18b` só mostra a arena por baixo). **Falta:** o Adriano escolher o Kraken (F6-1b em [docs/00-decisoes.md](docs/00-decisoes.md)); depois 6A2 e 6A4.
