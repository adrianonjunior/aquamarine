# CONTEXTO.md: coordenação ao vivo entre agentes

> **Dono de:** quem está fazendo o quê agora e o registro recente de entregas.
> Não repete decisões (ver [docs/00-decisoes.md](docs/00-decisoes.md)), plano de
> marcos (ver [docs/07-roadmap.md](docs/07-roadmap.md)) nem ambiente técnico
> (ver [docs/06-tecnico.md](docs/06-tecnico.md)). Regras: [AGENTS.md](AGENTS.md).
> Registro antigo (Entregas 1 e 2, Fase 3, áudio, Atlas): [docs/historico.md](docs/historico.md).

> **FASE ATUAL (26/10/2026): FASE 4 ENTREGUE no `0.5.0`; FASE 6 LIBERADA.** Entrega 1, Entrega 2 e Fases 3 e 4
> estão implementadas. **Liberado para trabalho:** só a 4S (sons da Fase 4), quando o Adriano pedir, e correções e testes
> do que já existe, a pedido. A Fase 6 ([docs/11-fase6.md](docs/11-fase6.md), release `0.6.0`) foi liberada pelo Adriano em 26/10/2026, em
> entregas; a Cidade Perdida e o Kraken completo estão sem fase. Release `0.N.0` = Fase `N`, sem Fase 5
> ([docs/07-roadmap.md](docs/07-roadmap.md), "Releases e fases").

## Aviso para todos os agentes — 26/10/2026
Leia ao retomar uma sessão.

- Todas as faixas das Entregas 1 e 2 e da Fase 3 estão na `main`, com `build` e a suíte inteira do
  `runClientGameTest` verdes. O que falta é o aceite com jogadores reais e o que só o Adriano julga:
  [docs/07-roadmap.md](docs/07-roadmap.md), "Só o Adriano pode fechar".
- A Fase 4 saiu no `0.5.0`; a 4C (colisão e movimento do Kraken) passou para a Fase 6 (6A). Mapa de tudo: [README.md](README.md),
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

## Registro de handoff
Uma linha por entrega, a mais recente no topo: `data · agente · o que mudou · o que falta`.
- 26/10/2026 · Claude Sonnet 5.5 · **Organização dos .md** (só docs, na `main`, sem commit ainda): convenção **release `0.N.0` = Fase `N`, sem Fase 5** ([docs/00-decisoes.md](docs/00-decisoes.md), "Versões e fases"; tabela em [docs/07-roadmap.md](docs/07-roadmap.md), "Releases e fases"). **Fase 6** planejada e organizada em [docs/11-fase6.md](docs/11-fase6.md) (faixas 6A a 6H, ordem sugerida, aceite) com as perguntas F6-1 a F6-8 em "Em aberto"; a **4C foi absorvida pela 6A** (spec segue em [docs/10-fase4.md](docs/10-fase4.md), §3). Banner de fase único em CONTEXTO (AGENTS.md agora só aponta), índice do README em grupos, plano de [docs/resumo.md](docs/resumo.md) renumerado (a antiga "Fase 5, Kraken completo" e a Cidade Perdida ficaram **sem fase**), registros antigos de CONTEXTO movidos para [docs/historico.md](docs/historico.md), linha velha da Fase 4 tirada de "Em andamento". **Testado:** todos os links relativos dos .md resolvem; Atlas regerado (mostra `11-fase6.md`) e `tools/atlas/tests` verdes. Nenhum arquivo de jogo mudou. **Falta:** o Adriano liberar a Fase 6 (ou a 6A1) e responder F6-1 a F6-8.

- 26/10/2026 · Claude Opus 5.5 · **Resumo geral** em [docs/resumo.md](docs/resumo.md): feito, o que falta e plano proposto em tabelas; achado da progressão no sobrevivência travada registrado em "Em aberto" ([docs/00-decisoes.md](docs/00-decisoes.md)) · falta o Adriano escolher a ordem do plano.

- 26/10/2026 · Claude Opus 5.5 · **Release 0.5.0** no GitHub (aba Releases, `Aquamarine-0.5.0.zip` com instalador e servidor, mais o `aquamarine-0.5.0.jar` solto para CurseForge), com tudo da Fase 4 até a 4G. **Testado:** `build` e a parte `multiplayer` do `runClientGameTest` (servidor dedicado, cliente pela rede). **Falta:** teste com os amigos do Adriano, 4C e 4S.

- 26/10/2026 · Claude Opus 5.5 · **Fase 4: 4B, 4D, 4E, 4F e 4G na `main`** (worktree `aquamarine-claude-kraken`, branch `claude/fase4`; painel em `docs/10-fase4.md`). 4B: Oricálcio → Abissal com apelido dos IDs antigos (`addAlias`). 4D: oficina da Vila, troféus e Boia de Âncora em 3D (`tools/gen_phase4_art.py`, `FacingShapeBlock`), ícones novos das pontas e do mergulho. 4E: 35 blocos aquáticos e 35 vidros rachados (`tools/gen_phase4_building.py` gera também o `ModBuildingBlocks`), a onda de choque do Kraken racha os vidros. 4F: Neutrônio inteiro (minério, Forja, têmpera no fundo, Molde do Kraken, ferraria, bônus na água) no estilo Avaritia (`tools/gen_phase4_neutronium.py`, `NeutroniumArmorRenderer`). 4G: Cavitador (modelo 3D com carga, pulso, arpão, partículas). **Testado:** compilação, testes de unidade e as partes `showcase`, `forge`, `creative`, `gear`, `harbor`, `pressure`, `blocks`, `building` e `neutronium` do `runClientGameTest`. **Não testado:** suíte inteira de uma vez (memória, com o jogo LAN do Adriano aberto) e multiplayer. **Falta:** 4C (partes de colisão e movimento do Kraken) e 4S (sons, depois). Aviso antigo, não é desta fase: o Porto loga `joint="rolling"` inválido nos blocos de quebra-cabeça.

- 25/10/2026 · Claude Opus 5.5 · **Fase 4 aberta e 4A entregue** (branch `claude/fase4`): plano completo em `docs/10-fase4.md` (painel do que já foi feito, ordem 4A a 4G e 4S). **4A:** Cria, Ovo e Tentáculo refeitos por um gerador único (`tools/models/spawnlings/build_model.py`: Java, texturas, `_glow`, UV, `.bbmodel` e `rig.json`), paleta do Kraken adulto, tentáculo em S de seção arredondada (hitbox 2 x 9), Cria com olhos em fenda e barbatanas laterais (hitbox 1,0 x 1,3), ovo em três passadas (o filhote aparece; antes a membrana o escondia), camada de brilho nas três. O Atlas lê o `rig.json` (v2 das três no registro, membrana translúcida no visualizador). **Testado:** compilação, parte `spawnlings` do `runClientGameTest` (capturas), `tools/atlas/tests/test_catalog.py`. **Não testado:** suíte inteira (falta memória com o jogo LAN do Adriano aberto). **Falta:** 4B em diante.

- 25/10/2026 · Codex · Ngrok reconfigurado com o token da segunda conta autorizada pelo Adriano (credencial somente na configuração local do ngrok). Agente e servidor estático ativos; **novo bloqueio: ERR_NGROK_382**, e-mail do administrador da conta ainda não verificado. O endereço atribuído em build/atlas-public-url.txt ainda não serve o Atlas publicamente. Falta o Adriano verificar o e-mail no painel do ngrok; depois conferir novamente o acesso público.
