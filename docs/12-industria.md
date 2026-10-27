# Fase 0.6.1: Indústria aquática (ideia, planejada)

> **Dono de:** a especificação da `0.6.1` (fazendas, mobs, redes de transporte, máquinas, arte e animação das máquinas, aba
> "Máquinas" do Atlas). **Decisões** em [00-decisoes.md](00-decisoes.md) (linhas "0.6.1"); **faixas e ondas** em
> [07-roadmap.md](07-roadmap.md), "0.6.1"; regras de agentes em [../AGENTS.md](../AGENTS.md). Números são valores iniciais a testar.

> **ESTADO: PLANEJADA, NÃO LIBERADA.** Nenhum agente escreve código, texturas ou modelos daqui até o Adriano liberar a fase
> ou uma faixa explicitamente.

> **FOCO DO AGENTE (pedido do Adriano).** Quem trabalhar nesta fase foca **só nesta etapa**: leia este arquivo e a faixa que
> recebeu, use o próprio mod como **referência** (por exemplo `AnchorBuoyBlock`, `tools/gen_phase6_energy.py`, `tools/models/`),
> e **não** gaste tempo checando nem analisando como o resto do projeto funciona. Se algo não está aqui, decida pequeno e siga
> (decisões criativas pequenas são suas); só pergunte o que muda escopo.

> **LIBERDADE CRIATIVA (pedido do Adriano).** Tudo que este plano **não decidiu** (receitas, valores, nomes de itens extras,
> detalhes de aparência, ideias de itens ou máquinas que combinem com a indústria) fica **a critério do agente**, sempre dentro do
> tema latão, vidro e vapor e dos nomes do dia a dia. Registre cada escolha em 1 linha no handoff do `CONTEXTO.md` e, se virar
> regra do jogo, em `docs/12-industria.md`. Só **pergunte** o que muda escopo, a progressão ou o balanceamento da luta do Kraken.

## Painel
| Faixa | O que | Estado | Depende de |
|---|---|---|---|
| 7A | Água dentro dos blocos (waterlogged) em todo bloco do mod | planejada | nada |
| 7B | Pipeline de máquinas animadas, bíblia de arte e aba "Máquinas" do Atlas | planejada | nada |
| 7C | Redes: Cano de fluido, Tubo de Ar, cabos novos, Chave de Maré, conteúdo visível | planejada | 7A, 7B |
| 7D | Bomba de Maré, Tanque d'Água e exceção dos geradores | planejada | 7C |
| 7E | Gerador de Fenda refeito (animado) e geradores com fluido | planejada | 7B, 7C, 7D |
| 7F | Compressor de Ar e Pulmão de Ferro (gás + regulação) | planejada | 7C, 7E |
| 7G | Ostra, pérolas e Fazenda de Ostras | planejada | 7A, 7B |
| 7H | Caranguejo e Escudo de Concha | planejada | 7B |
| 7I | Fornalha a Vapor, Prensa de Maré e todas as máquinas de fabricação e geração animadas | planejada | 7B, 7E |
| 7J | Telas das máquinas (medidores animados e lados) | planejada | 7C, 7D |
| 7Q | QA e release `0.6.1` | planejada | todas |

Os rótulos 7A a 7Q só organizam as faixas: **a fase se chama 0.6.1** e não existe Fase 7.

## Premissas
- **`0.N.x` não é só correção:** pode trazer ajustes e melhorias em geral (decisão do Adriano em 26/10/2026,
  [00-decisoes.md](00-decisoes.md), "Versões e fases"). A `0.6.1` leva conteúdo novo; as pendências da Fase 6 (6A2, 6A4, 6C1)
  ficam para a `0.6.2`, e os sons seguem em subversão própria.
- Foco do Adriano: **indústria aquática** com farms, energia e mecanismos técnicos estilo Mekanism; "uma das partes mais
  daora do mod". Ficar só debaixo d'água não pode ser obrigatório: o Tanque d'Água traz a água para a terra.
- Reaproveita: Team Reborn Energy 5.0.0 (energia), **Fabric Transfer API** (fluidos, já no Fabric API 0.165.0),
  `com.aquamarine.energy`, `tools/gen_phase6_energy.py` e o pipeline de modelos por script (`tools/models/`).
- **Latão:** o mod ainda **não tem item de latão**, mas a arte dele já é usada em `gen_phase4_art.py` (`BRASS`). A 7C cria o
  **Lingote de Latão** (receita pequena a decidir pela faixa, com cobre do jogo) e a **Placa de Latão** (Prensa, 7I).

- **Nomes do dia a dia (pedido do Adriano):** itens, blocos e textos usam palavras que qualquer jogador conhece (Tubo de Ar, não "duto";
  Prensa, Bomba, Compressor, Fornalha; nada de "centrífuga", "flange" ou "manômetro" no jogo). Vale para `lang/*.json`, descrições e este plano.

## Onde cada máquina funciona (decidido)
| Ambiente | Máquinas | Regra |
|---|---|---|
| Terra e água | Cabo, Cano, Tubo de Ar, Tanque d'Água, Pulmão de Ferro, Compressor de Ar | sem restrição |
| Só água | Turbina de Correnteza, Boia de Ondas, Coletor de Neutrônio, Garrafão de Cultivo, Bomba de Maré | exige água em volta (a Bomba, fonte encostada); o Coletor mantém também oceano profundo e 20 blocos abaixo do nível do mar |
| Só terra | Fornalha a Vapor, Prensa de Maré | não funcionam com o bloco alagado nem com água encostada (mostram o aviso na tela) |
| Gerador de Fenda | modo mundo: só água; modo fluido: terra e água | o modo fluido recebe água e magma pelo Cano |

## 7A. Água dentro dos blocos
**Pedido.** Bloco colocado na água não pode quebrar a fonte nem barrar a água: deve funcionar "como cerca".
**Hoje.** Só a Boia de Âncora implementa `SimpleWaterloggedBlock` (`AnchorBuoyBlock`, é o modelo a copiar).
- Todo bloco do mod com forma menor que o cubo inteiro ganha `waterlogged`: máquinas, geradores, cabo, Forja, mesas, troféus,
  Altar, decoração e os novos. Fazer **um ajudante ou base comum** (colocação, `getFluidState`, `updateShape` com tick do
  fluido, `canPlaceLiquid`), não copiar em cada classe. Blocos de cubo inteiro ficam como estão.
- Em água **fonte** ao colocar, o bloco nasce `waterlogged=true`; água corrente passa pelos vizinhos; quebrar devolve a fonte.
- Os blockstates dos scripts aceitam a propriedade nova; mundos antigos abrem com `waterlogged=false`.
- **Aceite:** teste que coloca cada bloco numa fonte (a fonte continua e o fluxo passa); parte `waterlog` e `build` verdes.

## 7B. Pipeline de máquinas animadas, bíblia de arte e Atlas
**Pedido.** Modelo estilizado e animação própria para tudo de fabricação e geração; **aba no site só para blocos customizados
com animações próprias**; o Gerador de Fenda refeito aparece lá.
**Hoje.** Modelos de energia são JSON estático com variante "aceso"; o Atlas só mostra entidades (`tools/atlas/catalog.py`).
- **Pipeline:** `tools/models/machines/<máquina>/build_model.py` gera o `.bbmodel`, as animações e o Java, no contrato dos
  modelos de entidade ([06-tecnico.md](06-tecnico.md), "Contrato de modelos e entidades"); renderização por
  `BlockEntityRenderer` com `ModelPart` e `AnimationDefinition`; estado da máquina (parada, ligando, funcionando, sobrecarga,
  cheia) vem do block entity, sincronizado só quando muda. Luz própria das peças acesas como na `CreatureGlowLayer`
  (`<nome>_glow.png`), adaptada ao bloco.
- **Atlas:** seção nova **"Máquinas"** em `tools/atlas/index.html`, com o mesmo visualizador, lista de animações e botão de
  estado (parada, funcionando, sobrecarga); o `catalog.py` ganha o tipo "máquina" (rig, clipes, textura, estados). Testes do
  Atlas cobrem a aba.
- **Aceite:** uma máquina de prova animada no jogo e na aba, mesmo rig nos dois; `build` e testes do Atlas verdes.
- **Portão de aprovação visual (como o A0 do áudio):** a **primeira máquina é o Gerador de Fenda**. O agente entrega capturas,
  a aba do Atlas e o `.bbmodel`, **para e espera o aval do Adriano** antes de produzir as outras. O aval vira a referência de
  nível das demais.

## Bíblia de arte das máquinas (vale para texturas, modelos e animações)
**Tema decidido: latão, vidro e vapor submarino** (steampunk náutico). Combina com a Forja Abissal, o Cano de vidro e latão e o
Pulmão de Ferro. Meta: **qualidade gráfica máxima**, a parte mais bonita do mod.

**Textura e escala**
- **32 px por face nas máquinas** (exceção declarada ao padrão 16x16, só para as máquinas animadas, os Canos, os Tubos de Ar e o
  Garrafão); itens, blocos de construção, decoração e mobs seguem as regras deles (itens 16 px; mobs 64 px por folha).
  Cada máquina tem **uma folha própria** (`textures/block/machine/<nome>.png`, até 128x128) e uma `<nome>_glow.png` para o que brilha.
- **Rampa de cor:** 5 a 6 tons por material, luz vinda do alto à esquerda, contorno escuro **só onde ajuda a leitura**, sem ruído
  aleatório de pixel. Escreva a rampa no topo do script (como `COPPER` e `BRASS` em `gen_phase4_art.py`).
- **Paleta fixa:** latão quente (ouro envelhecido), cobre com limo verde do cobre nas juntas, aço escuro azulado, vidro verde-água
  translúcido, couro marrom das foles e vedações, **âmbar/laranja** para magma e brasas, **ciano** para água e energia,
  **branco-azulado** para vapor e ar. **Brilho próprio só no que realmente acende** (magma, mostradores, núcleo, lâmpadas).
- **Detalhes obrigatórios que dão qualidade:** rebites a cada 8 px, parafusos nos cantos, juntas e anel de ligaçãos, letreiros e
  numerais nos mostradores, desgaste (arranhões, limo verde do cobre, manchas de óleo e sal), vidro com reflexo diagonal, tampas com
  alças. **Nada de face lisa maior que 8x8 px sem detalhe.**

**Modelos**
- Bloco de **1x1x1** (Tanque 1x1x1 empilhável; nada maior nesta fase). Modelo detalhado, **entre 30 e 70 cubos** por máquina,
  com partes móveis separadas em **ossos nomeados** (`root`, `hélice`, `piston_l`, `needle`...), pivôs no eixo real da peça.
- Silhueta forte e legível de longe; **colisão no formato** (como a Forja da 6D2); todas com `waterlogged` (7A).
- Lado "frente" reconhecível, gira para quem coloca (como `FacingShapeBlock`); conectores visíveis onde ligam Cabos, Canos e
  Tubos de Ar (anel de anel de ligação).

**Animações (padrão)**
- Cada máquina tem clipes nomeados: `idle`, `start` (1 a 2 s), `run` (laço), `stop` (1 s) e, onde faz sentido, `overload`
  (vibra e brilha) e `full`/`empty`. Movimento **mecânico com peso**: aceleração e freio suaves (não linear), peças
  secundárias com atraso, nunca tudo no mesmo ritmo. Velocidade do `run` acompanha a produção (0,5x a 2x).
- **Partículas** sempre que couber: bolhas na água, vapor em terra, faíscas e brasas; sem exagero (orçamento: no máximo
  2 partículas por tick por máquina).
- **Conteúdo visível:** o líquido do Tanque, do Cano e da Fenda usa a **textura do próprio fluido** (vanilla ou do mod), com
  altura proporcional e onda leve na superfície; gás com fiapos translúcidos; energia com arcos.

**Mobs (ostra e caranguejo):** folha de **64 px** como os outros mobs, brilho em `_glow` onde couber, rig e clipes no contrato.

**Entregáveis de arte por máquina:** script gerador, `.bbmodel`, textura e `_glow`, clipes, entrada no Atlas, captura do estado
`idle` e do `run`, e linha na tabela abaixo. **Checklist de qualidade antes de entregar:** (1) silhueta lê em 3 cantos;
(2) nenhuma face grande lisa; (3) rampa e paleta respeitadas; (4) clipes em laço sem salto; (5) brilho só onde acende;
(6) cabe na colisão; (7) aparece certo na aba "Máquinas" e no jogo.

## 7C. Redes de transporte e Chave de Maré
**Pedido.** Cabo de mais qualidade e **mais variações**; ver o conteúdo passando: magma se for magma, água se for água, vento
se for vento. **Decidido:** energia, fluidos e gás (itens saem por funil e baú do jogo).
- **Cabo** (energia): modelo refeito, duas camadas (Básico 256 E/t e Reforçado de Aurium com mais vazão), capas de
  vidro com arcos elétricos animados quando passa carga, juntas e anel de ligaçãos.
- **Cano** (fluido): vidro e latão com o líquido **visível por dentro** (textura e cor do fluido, fluxo animado no sentido
  real); dois níveis de vazão. Usa a Fabric Transfer API (`FluidStorage.SIDED`).
- **Tubo de Ar** (gás: Ar Comprimido, o "vento"): tubo translúcido com fiapos animados no sentido do fluxo. O gás é **fluido próprio
  sem bloco nem balde**, registrado só para a Transfer API (o agente confirma a técnica no 26.3 e documenta).
- Ligam sozinhos nos vizinhos, formam rede com distribuição justa; o conteúdo e o sentido vão ao cliente só quando mudam.
- **Variações visuais:** cada rede tem peça reta, cotovelo, T e cruz, **válvula** (liga e desliga com o mouse) e
  **visor de vidro** (mostra o conteúdo ampliado). Os três tipos se distinguem pela cor do anel de ligação: azul (energia), verde-água
  (fluido), cinza-claro (gás).
- **Chave de Maré (item):** clique direito em um conector cicla **entra, sai, desligado** (cores e anel visíveis, como o anel de ligação);
  agachado gira a máquina. Textura 16 px de latão com cabo de couro.
- **Bloqueio por conteúdo:** Cano e Tubo de Ar só aceitam um tipo de fluido de cada vez; **misturar não funciona** (o segundo é recusado).
- **Quebrar com fluido (decidido):** Cano de **água** solta uma fonte pequena no lugar; **magma e gás somem** sem queimar nem
  explodir.
- **Aceite:** testes de rede (fluido e gás andam, não vazam, não misturam); capturas com água, magma e ar; a Chave muda os
  lados e o visual acompanha.

## 7D. Bomba de Maré e Tanque d'Água
**Pedido.** Tanque que dá água em grandes quantidades por tubos (puxa e entrega), com exceção à regra de profundidade.
- **Bomba de Maré** (só água): gasta energia, puxa de uma **fonte de água encostada** sem esgotar o oceano (regra da fonte infinita
  do jogo) e empurra para o Cano. Pistão grande grande, volante e tubo de sucção com bolhas.
- **Tanque d'Água** (terra e água): guarda 64 baldes (valor inicial), enche por Bomba, Cano ou balde, entrega a Canos e às
  máquinas que pedem água; **empilha na vertical** somando capacidade. Cada tanque guarda um tipo (água, lava ou magma) e mostra o nível
  e o líquido por fora.
- **Exceção (decidida):** os **geradores** funcionam em qualquer lugar com água e magma vindos do Tanque; o **Coletor de
  Neutrônio segue exigindo** oceano profundo e 20 blocos abaixo do nível do mar.
- **Aceite:** Gerador de Fenda gerando em terra firme só com Tanque e Cano; Coletor recusando a terra com o mesmo Tanque.

## 7E. Geradores renovados
**Pedido.** Refazer o modelo do gerador de magma (Gerador de Fenda) com animações próprias e colocar no site.
- **Gerador de Fenda** (`vent_generator`; **primeira máquina, portão de aprovação da 7B**): caixa de pedra escura rachada, câmara de vidro com magma
  borbulhante, dois pistões em contratempo, válvula, chaminé que solta vapor (terra) ou bolhas (água), grade de ventilação com
  brilho. Dois modos: **mundo** (magma embaixo e água em cima, como hoje) e **fluido** (água e magma pelo Cano, conectores
  visíveis). Mantém ID, receita e 32 E/t como base.
- Turbina de Correnteza e Boia de Ondas: tratamento da 7I (fichas abaixo); a Fenda sai antes.
- **Aceite:** captura da Fenda em cada estado; a aba "Máquinas" mostra a Fenda animada; parte `energy` verde.

## 7F. Compressor de Ar e Pulmão de Ferro
**Pedido.** Regulador de pressão em bloco ("iron lung") que regula geradores e armazena pressão.
**Hoje.** O **Regulador de Pressão** é item da mão secundária ([08-fase3.md](08-fase3.md)); o Pulmão é um bloco de outra função.
- **Compressor de Ar** (terra e água): gasta energia e produz **Ar Comprimido** para o Tubo de Ar.
- **Pulmão de Ferro** (terra e água): guarda o Ar Comprimido em tanque grande; **regula os geradores ligados** (com pressão e
  água, rendem mais e estáveis, +25% como valor inicial; sem pressão perdem potência); nunca explode nem destrói bloco.
- **Fora da 0.6.1:** proteger o jogador da Pressão numa área.
- **Aceite:** o gás enche e esvazia pelo Tubo de Ar; a regulação muda a saída do gerador; capturas e aba "Máquinas".

## 7G. Ostra, pérolas e Fazenda de Ostras
**Pedido.** Mob "ostra"; algumas dropam pérola, úteis em crafts; fazenda estilo vida real com grandes garrafas conectadas nos oceanos.
- **Ostra (mob):** fica no fundo, fecha a concha quando alguém chega perto e abre devagar; **dropa Pérola de Ostra** com
  chance baixa (15% inicial); só em oceano morno e raso; passiva. Rig: duas valvas com dobradiça, manto, pérola que brilha
  quando madura. Clipes: `idle` (valvas entreabertas, respira), `close` (0,3 s), `open` (1,5 s), `pearl_glow`.
- **Pérola de Ostra (item, decidido):** **núcleo de máquinas e do Aurium**: componente da Bomba, do Tanque, do Compressor e do
  Pulmão, e reagente do refino do Aurium (Aurium Bruto + Pérola na Forja ou na Prensa). A Concha do Caranguejo também usa a Pérola (7H).
- **Fazenda de Ostras (linha de cultivo):** o **Garrafão de Cultivo** (só água) é um garrafão de vidro com uma ostra dentro,
  **ligado ao vizinho por Corda de Cultivo** (linha de boias), formando a linha. Cada garrafão ligado e com água em volta produz
  uma pérola a cada 8 min (+10% por garrafão vizinho até um teto). Uma **Boia de Cultivo** na ponta mostra a produção e recolhe
  as pérolas. Precisa só de água limpa (sem bloco de magma em volta). Arte: vidro grosso com rolha de latão, bolhas subindo,
  ostra abrindo e fechando dentro, brilho quando a pérola está pronta.
- **Aceite:** ostras nascem no bioma certo; a linha com N garrafões produz; funciona com o save recarregado.

## 7H. Caranguejo e Escudo de Concha
**Pedido.** Mob encontrado de vez em quando nas praias, com mecânica de vida real; a concha serve de base para um escudo mais forte.
- **Caranguejo:** anda de lado, cava a areia e some na toca quando ameaçado (volta depois), belisca se encurralado, mais ativo
  à noite; segura a **concha**, que dropa (**Concha de Caranguejo**). Raro, em praias. Pode ser alimentado com peixe cru.
  Rig: casco largo, 8 pernas, 2 garras, olhos em haste. Clipes: `walk_side` (laço), `claw_wave`, `burrow` (1,2 s), `emerge`,
  `pinch`, `flee`, `idle_eyes`, `sleep`. Folha 64 px, `_glow` só se houver detalhe luminoso.
- **Escudo de Concha (decidido):** o **escudo do jogo retexturizado** e mais forte: **672 de durabilidade** (o dobro), recarga menor
  quando o machado o desativa e **menos empurrão** de baleia, sem poder novo. Receita: Concha de Caranguejo + Pérola de Ostra + Escudo.
  Item 16 px, com a concha pintada no centro.
- **Aceite:** nasce só em praia, foge para a toca, belisca encurralado; o escudo bloqueia como o vanilla com os números acima.

## 7I. Todas as máquinas de fabricação e geração, animadas
**Pedido.** Tudo ligado a fabricação e geração com animações e modelos estilizados.
- **Novas máquinas:** **Fornalha a Vapor** (só terra; funde mais rápido com água do Cano) e **Prensa de Maré** (só terra; faz
  Placas de Latão, Aurium e Abissal para as receitas da indústria). Usam a mesma tela da 7J.
- **Já existentes que ganham modelo e animação de bloco:** Turbina de Correnteza, Boia de Ondas, Coletor de Neutrônio, Forja
  Abissal, Mesa de Encantamento Aquática, Bigorna de Maré, Mesa de Cartas, Altar de Concha.
- **Novas da 0.6.1 nas outras faixas:** Gerador de Fenda (7E), Bomba e Tanque (7D), Compressor e Pulmão (7F), Garrafão (7G).
- Cada uma entra na aba "Máquinas".

### Fichas de arte e animação
| Máquina | Ambiente | Forma e peças móveis | Clipes (estado) | Partículas e luz |
|---|---|---|---|---|
| Gerador de Fenda | Fenda mundo: água; fluido: ambos | caixa de pedra rachada, câmara de vidro com magma, 2 pistões, válvula, chaminé | `idle`, `start`, `run` (pistões em contratempo, magma pulsa), `overload` (vibra e fica vermelho) | vapor ou bolhas na chaminé; brilho âmbar |
| Turbina de Correnteza | água | carcaça octogonal de latão, hélice de 6 pás de cobre, eixo, mostrador com agulha | `idle`, `spin` (velocidade acompanha as faces com correnteza) | bolhas na saída; mostrador acende |
| Boia de Ondas | água (superfície) | boia, mastro, lanterna de vidro, mola e pistão | `bob` (balanço de 6 graus, 3 s; mais forte na chuva e na tempestade), `light_on` | faíscas fracas; lanterna acende |
| Coletor de Neutrônio | água (oceano profundo) | câmara selada de latão e Aurium, núcleo estelar flutuando, 3 braços que giram | `idle`, `run` (núcleo gira, braços giram), `collect` (um pó cai), `full` | pó estelar caindo; brilho perolado |
| Bomba de Maré | água | pistão grande grande, volante, tubo de sucção, grade de entrada | `idle`, `pump` (pistão grande 1 s por ciclo) | bolhas na sucção |
| Tanque d'Água | ambos | cilindro de vidro e latão, tampa, mostrador, escada; nível dinâmico | `idle`, `fill`, `empty` (nível sobe e desce suave) | superfície com onda; gotas ao encher |
| Compressor de Ar | ambos | motor, cilindro, volante, tubulação | `idle`, `run` (pistão e volante), `full` | pulsos de ar |
| Pulmão de Ferro | ambos | grande fole deitado de latão e couro com janela de vidro, mostrador de pressão | `idle`, `breathe` (fole sobe e desce com o nível), `relief` (válvula solta jato) | jato de ar branco-azulado |
| Fornalha a Vapor | terra | caldeira, chaminé, porta com brasas, medidor de pressão, apito | `idle`, `run` (brasas pulsam, medidor de pressão sobe), `whistle` | fumaça e vapor; brilho âmbar |
| Prensa de Maré | terra | prensa hidráulica: placa desce e sobe sobre bigorna | `idle`, `press` (ciclo 1,6 s com impacto), `done` | vapor lateral; faísca no impacto |
| Garrafão de Cultivo | água | garrafão de vidro com ostra dentro, rolha de latão, corda | `idle`, `ready` (pérola brilha), `harvest` | bolhas subindo |
| Forja Abissal | terra e água | já tem modelo 3D; ganha fole, martelo e brasas animados | `idle`, `forge` (fole e martelo) | brasas, bolhas ou fumaça |
| Mesa de Encantamento Aquática | ambos | concha com pérola, livro | `idle` (pérola gira), `enchant` (livro abre) | brilho cintilante |
| Bigorna de Maré | ambos | bigorna de latão e martelo | `idle`, `strike` (martelo cai) | faíscas |
| Mesa de Cartas | ambos | mesa com mapa, bússola, compasso | `idle` (agulha), `unroll` (mapa desenrola) | nenhuma |
| Altar de Concha | ambos | concha grande com pérola | `idle`, `open` (concha abre) | brilho |

- **Aceite:** todas animam no jogo e na aba; capturas por máquina (`idle` e `run`); sem regressão das receitas.

## 7J. Telas das máquinas
**Pedido.** Telas com medidores animados, e Chave de Maré para os lados (7C).
- Tela de cada máquina em painel de **latão** (estilo da bíblia de arte), com **medidor de energia** (raios), **medidor de
  fluido** (textura do fluido, subida suave), **medidor de gás** (bolhas), **progresso** e uma **aba de lados** com os 6 lados e
  a configuração entra, sai e desligado.
- **Aceite:** abrem todas as telas das máquinas com inventário (Bomba, Tanque, Compressor, Pulmão, Fornalha, Prensa), com os medidores
  acompanhando o conteúdo; capturas por tela.

## Ondas sugeridas (para o orquestrador, quando liberado)
1. **7A e 7B** em paralelo; **portão do Gerador de Fenda** (aval do Adriano). 2. **7C**. 3. **7D, 7G e 7H** em paralelo.
4. **7E e 7F**. 5. **7I e 7J**. 6. **7Q**. Jogo e Gradle são recursos exclusivos; uma worktree por faixa.

## Em aberto (nada trava o plano)
- Receita do Lingote de Latão, da Pérola de Ostra como núcleo e do Escudo de Concha: **o agente decide** (liberdade criativa);
  só pergunta se mudar a progressão.
- Valores iniciais acima (testar no jogo).
