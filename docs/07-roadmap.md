# Roadmap

> **Dono de:** ordem dos marcos e se cada um foi concluído. Não tem donos nem
> tarefas em andamento (isso fica em [../CONTEXTO.md](../CONTEXTO.md)). Marque o
> checkbox aqui quando um marco terminar.

- [x] Repositório privado com esqueleto Fabric 26.3.
- [x] JDK 25 instalado; build e `runClient` funcionam.
- [ ] Ciclo **manual** de screenshot: print no jogo (F2) lido de `run/screenshots`.
  Capturas automatizadas do `runClientGameTest` já foram verificadas na Entrega 1.
- [x] **Marco 2, Materiais:** minério de Oricálcio, lingote, ferramentas,
  armadura, aba no criativo, receitas, texturas.
- [ ] **Kraken v1 jogável**, já com modelo de alta qualidade (decisão do
  Adriano), barra customizada, escudo e efeito de Pressão, antes da cidade, para
  sentir a luta em multiplayer.
- [ ] **Cidade Perdida v1** (estrutura jigsaw).
- [ ] Kraken completo, depois bioma e polimento.

A ordem do plano oficial põe a cidade antes do Kraken. Aqui o Kraken v1 vem
antes porque é a parte mais arriscada e a mais divertida de ajustar.

## Entrega 1: base estruturada + algo jogável (agente Estruturador)
Liberada para implementação em 24/10/2026. Objetivo: deixar a base do projeto
pronta para vários agentes trabalharem e entregar **algo legal e jogável**
no fim. Itens em ordem de prioridade; cada um termina com build verde,
commit e linha no handoff do `CONTEXTO.md`.

**A. Estrutura (obrigatório, primeiro)**
- [x] Pacotes e registros: `ModItems`, `ModBlocks`, aba criativa "Aquamarine",
  carregamento de `lang/pt_br.json` e `en_us.json`, tags, config
  `config/aquamarine.json` (ver [06-tecnico.md](06-tecnico.md)).
- [x] Pasta `tools/` com scripts Python que **geram as texturas** (pixel art,
  paleta do mod) para poderem ser regeradas.
- [x] GameTest ou teste mínimo rodando no `gradlew build`; CI do GitHub continua verde.

**B. Materiais (Marco 2) e Forja Abissal**
- [x] Minério de Oricálcio (**sem gerar no mundo**; só creative e comandos),
  lingote, bloco, ferramentas, armadura com bônus de conjunto, Forja Abissal e
  receitas, conforme [05-itens.md](05-itens.md). Pérola Abissal e Cristal de
  Maré como itens. Texturas em pixel art.

**C. Óculos de Mergulho funcionando** ✅ (spec em [05-itens.md](05-itens.md)): item
de cabeça com textura e modelo na cabeça, receita e redução de névoa debaixo d'água.

**D. Se sobrar orçamento (extra)**
- [x] Barra de vida customizada (HUD cliente) funcionando com uma entidade de
  teste oculta, com faixa de escudo e rachaduras (spec em [02-kraken.md](02-kraken.md)).
- [x] Chamariz Abissal, Altar e as mensagens de invocação no chat.

**Critério de "algo legal"**: ao abrir o jogo, a aba criativa "Aquamarine"
mostra os itens com texturas próprias, dá para craftar e vestir a armadura e os
óculos, e a Forja Abissal funciona.

**Concluída em 24/10/2026** (A, B, C e D). Conferida no jogo pelo teste de cliente
(`./gradlew runClientGameTest`, screenshots em `build/run/clientGameTest/screenshots/`).
Pendências e próximos passos: `CONTEXTO.md`, registro de handoff.

## Entrega 2: Kraken v1 jogável (**liberada em 24/10/2026; em andamento**)
O Adriano liberou esta entrega ao enviar o
[prompt do orquestrador](../prompts/orquestrador.md). Objetivo: lutar contra o Kraken inteiro em multiplayer, sem a cidade;
o teste usa um comando de depuração e uma arena de teste, e o Altar já chama o
gancho `KrakenSummoning.spawnKraken`. Regras da luta: [02-kraken.md](02-kraken.md).
Papéis e contrato de arquivos: [06-tecnico.md](06-tecnico.md). Regras de economia:
[../AGENTS.md](../AGENTS.md).

Cada linha é uma **Task** do orquestrador. "Dep." = só começa depois do merge
validado das faixas listadas. Orçamento = tempo / screenshots (ver economia).

| Onda | Faixa | Agente | Dep. | Entrega e aceite observável | Orçamento |
|---|---|---|---|---|---|
| 1 | **2A-M** | Codex (`high`) | nenhuma | Primeiro commit: `KrakenView` + `uv.png` na `main` em ~30 min. Depois o modelo completo e animações do contrato. Aceite: modelo aparece no jogo com o gabarito e todas as animações disparam por comando | 3 h / 6 |
| 1 | **2A-C** | Claude | nenhuma | `KrakenEntity` (vida e escudo por fase, escala `f`, transição, aggro, fuga, abandono), ligação com a barra existente, `spawnKraken`, `/aquamarine kraken`, arena de teste. Aceite: GameTest de fases e escala verde; barra mostra a vida real | 3 h / 3 |
| 2 | **2A-T** | Claude | 2A-M, 2A-C | Textura pixel art do Kraken por script, usando o renderer real já registrado. Aceite: screenshot das 3 fases com rachaduras | 1 h / 4 |
| 2 | **2B** | Claude | 2A-C | Pressão (0-4), Tônico, Boia de Âncora, `OrichalcumEffects`, vinheta e som. Aceite: GameTest dos níveis e contramedidas | 2 h / 2 |
| 2 | **2C** | Claude | 2A-C | Carapaça, Nós de Maré, tabela de dano por fonte, armas, atordoamento. Aceite: GameTest do escudo (absorve, quebra, regenera) | 2 h / 2 |
| 2 | **2D-M** | Codex (`medium`) | 2A-M | Modelos e animações do tentáculo, da Cria e dos ovos (`TentacleView`, `YoungKrakenView`). Aceite: aparecem no jogo por comando | 2 h / 4 |
| 3 | **2D-C** | Claude | 2A-C, 2B, 2C | 8 tentáculos e os 10 ataques com avisos. Aceite: cada ataque dispara por comando com seu aviso; GameTest de dano | 3 h / 3 |
| 3 | **2D-T** | Claude | 2D-M | Texturas do tentáculo, da Cria e dos ovos. Aceite: screenshot de cada | 1 h / 3 |
| 3 | **2E** | Claude | 2A-C | Aviso falso, tremor de tela, silêncio do oceano, drops, troféu, primeira vitória. Aceite: GameTest dos drops e do gatilho | 2 h / 2 |
| 4 | **2Q** | Codex (`medium`) | todas | QA visual com 2 clientes: invocar, 3 fases, 1 screenshot por ataque. Aceite: relatório de bugs por prioridade; não corrige código, abre Task para o dono | 2 h / 12 (até 6 por ciclo) |

- [x] 2A-M · [x] 2A-C · [ ] 2A-T · [x] 2B · [x] 2C · [x] 2D-M · [x] 2D-C · [x] 2D-T · [x] 2E · [ ] 2Q

**Aceite da Entrega 2:** 2 ou mais jogadores invocam o Kraken e vencem as 3
fases em 8 a 12 minutos com equipamento do mod, sem crash e sem desync da barra.
Ritmo e sensação de dano só o Adriano julga: vão ao relatório como "a testar pelo Adriano".


## Fase 3: fauna, equipamentos de mergulho e Vila (**PREPARADA; execução bloqueada**)
**Preparação liberada em 24/10/2026; execução bloqueada.** Nenhum agente inicia Tasks,
worktrees ou implementação desta seção antes de (1) a Entrega 2 estar completa (todas as
faixas acima marcadas, aceite cumprido) e (2) o Adriano liberar a Fase 3 enviando o
[prompt do orquestrador](../prompts/orquestrador-fase3.md). Especificação:
[08-fase3.md](08-fase3.md). Contrato de arquivos: [06-tecnico.md](06-tecnico.md), "Contrato
de modelos e entidades (Fase 3)". Corpos das Tasks: [fase3-claude.md](../prompts/fase3-claude.md)
e [fase3-modelagem.md](../prompts/fase3-modelagem.md). Economia: [../AGENTS.md](../AGENTS.md).
Cada linha é uma Task; "Dep." = só depois do merge validado.

| Onda | Faixa | Agente | Dep. | Entrega e aceite observável | Orçamento |
|---|---|---|---|---|---|
| 1 | **3A** | Claude | nenhuma | Separa as fontes da Pressão (sem mudar a luta), fonte ambiental, Medidor, Regulador, Reserva de Ar e Traje com texturas. Aceite: GameTest dos níveis por profundidade e do max das fontes; testes da Entrega 2 seguem verdes | 3 h / 3 |
| 1 | **3B-M1** | Codex (`high`) | nenhuma | Modelo e animações da Moréia (hitbox segmentada, ataques do contrato). Aceite: aparece e anima por comando | 3 h / 6 |
| 1 | **3B-M2** | Codex (`medium`) | nenhuma | Modelos e animações da Baleia-de-Nereia e do Cachalote, com a Sela visível. Aceite: aparecem e animam por comando | 3 h / 6 |
| 2 | **3B-C** | Claude | 3A, 3B-M1 | Moréia (IA, ataques, escala, drops), Escama Elétrica, Bobina Voltaica, Chicote de Moréia. Aceite: GameTest dos drops e da Descarga (só atinge quem está na água) | 3 h / 3 |
| 2 | **3B-W** | Claude | 3A, 3B-M2 | Baleias: spawn, canto e rota, domar, Sela, montaria com 3 jogadores, fuga do Kraken. Aceite: GameTest de montar e de passageiros | 3 h / 3 |
| 2 | **3B-T** | Claude | 3B-M1, 3B-M2 | Texturas da Moréia, Baleia e Cachalote por script. Aceite: screenshot de cada | 1 h / 3 |
| 3 | **3C** | Claude | 3A, 3B-C | Aljava e 4 pontas do Arpão, efeitos contra o Kraken (linhas na tabela de [02-kraken.md](02-kraken.md)), 7 encantamentos. Aceite: GameTests de cada ponta e encantamento | 3 h / 3 |
| 3 | **3D** | Claude | 3A, 3B-C | Propulsor de Maré: tecla, carga por Bobina, impulso validado no servidor, Sobrecarga. Aceite: GameTest do impulso e das cargas | 2 h / 3 |
| 4 | **3E** | Claude | 3A, 3B-C, 3B-W, 3C, 3D | Vila: peças por script, Eclusa, profissões, comércio, reputação, Alerta de Moréia. Aceite: `/locate` a encontra e o comércio vende os itens da tabela | 4 h / 6 |
| 5 | **3Q** | Codex (`medium`) | todas | QA com 3 clientes seguindo o aceite de [08-fase3.md](08-fase3.md). Relatório de bugs; não corrige código | 2 h / 12 (até 6 por ciclo) |

- [ ] 3A · [ ] 3B-M1 · [ ] 3B-M2 · [ ] 3B-C · [ ] 3B-W · [ ] 3B-T · [ ] 3C · [ ] 3D · [ ] 3E · [ ] 3Q

**Checklist de liberação (o orquestrador confere antes de criar a Run; nada aqui é feito antes):**
- [ ] Entrega 2 completa: todas as faixas marcadas (inclui 2A-T com rachaduras visíveis e 2Q) e o aceite multiplayer cumprido.
- [ ] Faixa de tamanho do Kraken (24 x 16, ver [00-decisoes.md](00-decisoes.md)) aplicada.
- [ ] `main` limpa, `./gradlew build` e `runClientGameTest` verdes, "Em andamento" do `CONTEXTO.md` vazio.
- [ ] Codex atualizado e conseguindo chamar o `orca` (o 0.163 falhava no IPC); disco C: com pelo menos 10 GB livres.
- [ ] Terminais Claude e Codex aceitos no Orca (Bypass Permissions e trust), para os workers não pararem nos avisos.
- [ ] Adriano enviou o prompt do orquestrador da Fase 3 (a liberação explícita).
