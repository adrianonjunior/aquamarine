# Roadmap

> **Dono de:** ordem dos marcos e se cada um foi concluído. Não tem donos nem
> tarefas em andamento (isso fica em [../CONTEXTO.md](../CONTEXTO.md)). Marque o
> checkbox aqui quando um marco terminar.

- [x] Repositório público com esqueleto Fabric 26.3.
- [x] JDK 25 instalado; build e `runClient` funcionam.
- [ ] Ciclo **manual** de screenshot: print no jogo (F2) lido de `run/screenshots`.
  Capturas automatizadas do `runClientGameTest` já foram verificadas na Entrega 1.
- [x] **Marco 2, Materiais:** minério de Oricálcio, lingote, ferramentas,
  armadura, aba no criativo, receitas, texturas.
- [ ] **Kraken v1 jogável**, já com modelo de alta qualidade (decisão do
  Adriano), barra customizada, escudo e efeito de Pressão, antes da cidade, para
  sentir a luta em multiplayer. *Faixas prontas (Entrega 2); falta o aceite multiplayer real, que é do Adriano.*
- [ ] **Fase 3** (fauna, equipamentos de mergulho e Vila). *Faixas 3A a 3E prontas e 3Q feita até onde um
  cliente alcança; falta o aceite com 3 jogadores reais, que é do Adriano.*
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

## Entrega 2: Kraken v1 jogável (**liberada em 24/10/2026; faixas concluídas, aceite multiplayer com o Adriano**)
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
| 4 | **2Q** | Codex (`medium`) | todas | QA visual: invocar, 3 fases, screenshots dos ataques. Aceite: relatório de bugs por prioridade; não corrige código, abre Task para o dono | 2 h / 12 (até 6 por ciclo) |

- [x] 2A-M · [x] 2A-C · [x] 2A-T · [x] 2B · [x] 2C · [x] 2D-M · [x] 2D-C · [x] 2D-T · [x] 2E · [x] 2Q

**Aceite da Entrega 2:** 2 ou mais jogadores invocam o Kraken e vencem as 3
fases em 8 a 12 minutos com equipamento do mod, sem crash e sem desync da barra.
Ritmo e sensação de dano só o Adriano julga: vão ao relatório como "a testar pelo Adriano".


## Fase 3: fauna, equipamentos de mergulho e Vila (**liberada em 24/10/2026; faixas fechadas em 25/10/2026**)
**Liberada em 24/10/2026** pelo Adriano (as faixas da Entrega 2 estão aceitas; o aceite
multiplayer real dela segue com ele). Execução conduzida pelo orquestrador
([prompt](../prompts/orquestrador-fase3.md)). Especificação:
[08-fase3.md](08-fase3.md). Contrato de arquivos: [06-tecnico.md](06-tecnico.md), "Contrato
de modelos e entidades (Fase 3)". Corpos das Tasks: [fase3-claude.md](../prompts/fase3-claude.md)
e [fase3-modelagem.md](../prompts/fase3-modelagem.md). Economia: [../AGENTS.md](../AGENTS.md).
Cada linha é uma Task; "Dep." = só depois do merge validado. "Agente" é o preferencial: qualquer
agente pode fazer a faixa quando o Adriano pedir ([00-decisoes.md](00-decisoes.md), linha "Agentes").

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

- [x] 3A · [x] 3B-M1 · [x] 3B-M2 · [x] 3B-C · [x] 3B-W · [x] 3B-T · [x] 3C · [x] 3D · [x] 3E (v2: reputação, Alerta, Cais, Torre, prédios) · [x] 3Q (um cliente; [relatório](relatorio-3q.md))
- [ ] **Aceite da Fase 3 com 3 jogadores reais** (só o Adriano; roteiro em [relatorio-3q.md](relatorio-3q.md))

Desde 24/10/2026 à noite as faixas seguiram com **um agente único**, sem Run do Orca
([prompt](../prompts/fechamento-final.md); a 3E v2 e a 3Q em 25/10/2026). Evidência de cada faixa:
GameTests `DepthGameTest` (3A), `MorayGameTest` (3B-C), `WhaleGameTest` (3B-W), `GearGameTest` (3C,
3D), `HarborGameTest` e `WorldgenGameTest` (3E e a parte da 3Q no mundo gerado), JUnit `HarborTest`, e
as screenshots 27 e 30 a 38 do `runClientGameTest`.

**Só o Adriano pode fechar** (Entrega 2 e Fase 3): o aceite com jogadores reais (2 clientes nas 3 fases
do Kraken; 3 clientes no ciclo da Fase 3, sem crash nem desync), o ritmo e o dano da luta, a montaria
pilotada por um jogador de verdade, o Propulsor e as teclas R e V na mão, a legibilidade dos avisos
(slam, Descarga, Enrolar) e o surgimento natural das criaturas num mundo normal.

## Áudio v1: ambientação, efeitos e música (**liberado ao enviar `prompts/audio.md`**)
Especificação, lista de sons, método e padrões: [09-audio.md](09-audio.md). Um agente (qualquer
um, divisão Claude/Codex só preferencial) ou vários em worktrees próprias; sempre **lotes
pequenos com aprovação do Adriano** (agentes não ouvem). Arquivos novos em `tools/audio/` e
`assets/aquamarine/sounds/`; mudanças em código compartilhado só acrescentam.

| Lote | Conteúdo | Aceite observável | Portão do Adriano |
|---|---|---|---|
| A0 | Pipeline (`tools/audio/`, prévia HTML, medidores) e **prova de qualidade** de 6 amostras | Amostras geradas, medidas e na prévia | Aprova as 6 |
| A1 | Ambiência em camadas, `AmbienceController`, config, `ModSounds`, `sounds.json` | Camadas trocam por profundidade, hora, Pressão e local | Prévia |
| A2 | Efeitos de Kraken, tentáculos, Crias, Moréia, baleias, Walker e criaturas planejadas; troca dos sons vanilla | `grep` sem `SoundEvents.` vanilla fora da lista de exceções | Prévia |
| A3 | Efeitos de itens, blocos, interface, Vila e invocação | Todos os eventos da lista de [09-audio.md](09-audio.md) com legendas | Prévia |
| A4 | Música: temas, stems do Kraken e stingers, gatilhos no cliente | Temas tocam na situação certa; luta com camadas por fase | Prévia + jogo |
| A5 | Mixagem final, orçamento de 70 MB, verificação em `runClientGameTest` | Medidores verdes e build verde | Aceite no jogo |

- [x] A0 (aprovado pelo Adriano em 25/10/2026) · [ ] A1 · [ ] A2 · [ ] A3 · [ ] A4 · [ ] A5
- [x] **A-K, luta do Kraken** (adiantada a pedido do Adriano, 25/10/2026): 3 trilhas em camadas, stingers, efeitos do
  Kraken e das crias, céu por fase, tempestade e destruição do ambiente ([09-audio.md](09-audio.md), "Luta do Kraken";
  [02-kraken.md](02-kraken.md), "A luta muda o mundo"). Cobre a parte do Kraken de A2 e A4. Falta o portão 4 (luta no jogo).
- [x] **Criaturas do oceano nascendo sozinhas** (correção de 25/10/2026; [00-decisoes.md](00-decisoes.md)).
- [x] **Release 0.4.0 para teste multiplayer** (25/10/2026): instalador, pacote de servidor e teste em servidor
  dedicado. Falta o teste com os amigos do Adriano.

## Atlas técnico e estudo do Kraken (25/10/2026)

- [x] Portal HTML local unificado: sete rigs, suas animações, áudio existente, texturas e documentação.
- [x] Kraken v2 exclusivo da prévia, com 16 movimentos e estrutura articulada; referência v1 preservada.
- [x] Histórico de oito versões, exportação, atualização do áudio em produção e verificação no navegador.
- [x] Kraken v2 **integrado ao jogo** a pedido do Adriano (25/10/2026), junto com o Kraken 2 vezes maior
  ([00-decisoes.md](00-decisoes.md), "Tamanho do Kraken"). Falta o aceite no jogo.

Uso, limites e evidências: [Atlas técnico](../tools/atlas/README.md).
