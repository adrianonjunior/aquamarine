# Prompt do Codex: remodelagem da Corda e da Boia de Cultivo (0.6.1, 7G)

Cole o texto abaixo, inteiro, como primeira mensagem do Codex, numa worktree própria. **Enviar este prompt é a liberação
desta tarefa pelo Adriano.** Escopo único: a **Corda de Cultivo** e a **Boia de Cultivo**. Nada além disso.

---

Você é o agente de **modelagem** do projeto **Aquamarine** (mod Fabric, Minecraft Java 26.3, repositório
`C:\Users\adria\Documents\AQUAMARINE`). Tarefa: **refazer do zero a Corda de Cultivo (`cultivation_rope`) e a Boia de
Cultivo (`cultivation_buoy`)**, hoje feias: são caixas de JSON estático com uma textura 16 px lisa. Elas passam a ser
**máquinas animadas** com design próprio, bonito e **em harmonia com o Garrafão de Cultivo**, no mesmo nível de qualidade
dele e do Gerador de Fenda.

**Foco total:** só estas duas peças. Não analise nem refatore o resto do projeto. Leia apenas: `AGENTS.md` (economia),
`docs/12-industria.md` (só o cabeçalho e a "Bíblia de arte das máquinas"), o docstring de
`tools/models/machines/machine_kit.py`, e como **referência de nível e de método**
`tools/models/machines/cultivation_jar/build_model.py` e `tools/models/machines/vent_generator/build_model.py`.
Registre a tarefa em "Em andamento" do `CONTEXTO.md`. O jogo é recurso exclusivo e o Adriano pode estar com ele
aberto: se o Gradle reclamar de lock, peça para ele fechar.

## 1. Harmonia com o Garrafão (a regra mais importante)
A linha de cultivo é `Garrafão — Corda — Corda — … — Boia`, todos virados para o mesmo lado; a linha corre no eixo
`FACING.getClockWise()` (`CultivationLine.axis`). No modelo, **a linha corre em x** (como os cabos do Garrafão) e a peça
gira com o bloco. O que precisa **casar milímetro a milímetro**:
- **Altura e espessura da corda:** o Garrafão solta dois cabos (`rope_left`, `rope_right`) em `y=11`, altura `1`, `z=7..9`,
  de `x=0..5` e `x=11..16`. A Corda e a Boia usam **o mesmo eixo da linha: centro em `y=11,5` e `z=8`**; a corda
  chega **rente ao limite do bloco (`x=0` e `x=16`)** para não ter degrau nem vão entre peças.
- **Material:** a mesma trama de couro (`LEATHER`, pintor `cord` do Garrafão: cilindro com trama diagonal). **Não invente
  outra corda.** Crie `tools/models/machines/cultivation_common.py` copiando os pintores `brass`, `cord`, `glass` do
  Garrafão e as rampas, e importe nas duas peças novas. **Não regenere nem altere o script do Garrafão.**
- **Linguagem visual:** latão (cintas e anéis), vidro verde-água, couro, rebites a cada 8 px, limo verde do cobre nas juntas.
  Paleta e rampas do `machine_kit.py` (`BRASS`, `COPPER`, `PATINA`, `STEEL`, `GLASS`, `LEATHER`, `IVORY`, `CYAN`, `MAGMA`);
  **sem cores novas.** Folha 128x128 com 32 px por face, `_glow` só no que acende.
- Quando a linha estiver montada (Garrafão, 2 Cordas, Boia), tudo tem que parecer **uma coisa só**, feita pelo mesmo
  artesão.

## 2. Corda de Cultivo (peça modular, sem estado)
Design: não é um bastão. É uma **corda trançada de 3 fios** (fios em caixas escalonadas que se cruzam em meia-volta,
alternando o fio por cima), com um **leve caimento no meio** (a corda desce ~1 px no centro e volta a `y=11,5` nas pontas),
**meia-braçadeira de latão em cada ponta** (1 px de largura; duas cordas lado a lado formam uma braçadeira inteira, e junto do
Garrafão parece uma presilha) e **uma pequena boia de vidro verde-água** (flutuador de linha de cultivo) presa no meio com
uma cinta de latão. Detalhe de uso: fiapos soltos de couro, uma mancha de sal discreta.
- **Animação (`idle`, laço):** balanço suave pela correnteza: ondulação de ±0,4 px e ±1,5° em ondas pelo comprimento, a
  boiazinha de vidro sobe e desce com meio ciclo de atraso. Peças com fases diferentes (movimento secundário). **A onda
  anda ao longo da linha:** o block entity soma à fase `0,3 s` por bloco de distância no eixo da linha, para as cordas
  não balançarem todas juntas.
- **Orçamento:** 25 a 45 cubos; barata de desenhar (muitas cordas numa linha de 64 blocos). Block entity **só no cliente**:
  sem salvar, sem rede, `tick` cliente que atualiza o `MachineMotion`.

## 3. Boia de Cultivo (fim da linha, com estado)
Design: **boia de sinalização** de latão, vidro e marfim, não um cubo. Do chão para cima: anel flutuador **octogonal**
(8 caixas encadeadas) em **marfim** com duas faixas de **cobre**, aro de latão com rebites, um **mastro** central de latão
(7..9 em x e z) com **argola** na altura `y=11,5` onde a **corda amarra** (o mesmo nó de couro do Garrafão), uma
**lanterna de vidro com gaiola de latão** no alto e uma **bandeirola de latão** em haste pequena. Linha de corda curta
(x=0..7) ligando a borda do bloco à argola. Mantenha a silhueta dentro da colisão atual
(`Block.box(2,1,2,14,8,14)` + mastro `7,8,7 a 9,16,9`); se precisar mudar, ajuste a forma em `ModCultivation`.
- **Estados** (`MachineState`: parada, trabalhando, cheia), com clipes nomeados: `idle`, `start`, `run`, `full`, `stop`,
  `harvest`:
  - `idle`: boia parada com **balanço** de ±0,6 px e ±1,5° (ciclo 4 s), lanterna **apagada**, bandeirola **baixa**;
  - `run`: o mesmo balanço, lanterna com **brilho âmbar lento** (pulso de 2 s), bandeirola meia-altura balançando;
  - `full`: balanço maior, lanterna **branca-azulada de pérola** piscando, **bandeirola no alto** tremulando;
  - `harvest`: a boia **afunda e volta** (0,8 s), a lanterna dá um **clarão**, a bandeirola cai e levanta;
  - `start` e `stop`: transição de 1 a 1,5 s entre `idle` e `run`.
- **Orçamento:** 40 a 70 cubos.
- **Java mínimo (copie o padrão do Garrafão, nada além):** `ModCultivation` ganha `ROPE_ENTITY` e `BUOY_ENTITY` (tipos de block
  entity); `CultivationBlock.newBlockEntity` devolve o BE de cada tipo, `getRenderShape` vira `INVISIBLE` para os três e o
  `getTicker` cobre os novos; a **Boia** lê a linha com `CultivationLine.find` **no máximo a cada 40 ticks** e só sincroniza
  quando o estado muda (padrão do `CultivationJarBlockEntity.sync`); `IndustryClient` registra os dois renderizadores com
  `MachineRenderer`, como o do Garrafão. Os `Model` e `Animations` Java saem **gerados** pelo `machine_kit` (não à mão).
  Em `tools/gen_industry_wave3.py` mexa **só** nas linhas da corda e da boia (os JSON estáticos antigos viram só
  partícula/ícone como no Garrafão); depois rode `git status` e confirme que **só** arquivos da corda e da boia mudaram.

## 4. Como modelar com qualidade (o método que importa)
1. **Desenhe antes de codar.** Escreva 8 a 10 linhas: silhueta, 3 detalhes que se leem de longe, material de cada parte,
   e o **porquê** de cada movimento. Cole no handoff.
2. **Muitas peças pequenas, em camadas.** Chanfro = caixas empilhadas mais estreitas, passos de 0,5 px. Nenhuma caixa
   grande e lisa: cada peça tem função (fio, braçadeira, aro, argola, gaiola, haste). Ossos nomeados com pivô no eixo real
   da peça (a boia gira no centro do flutuador, a bandeirola na base da haste).
3. **Um pintor por material, com rampa de 5 a 6 tons**, luz do alto à esquerda: realce no canto superior esquerdo, sombra no
   inferior direito, contorno escuro só onde ajuda a leitura. **Nada de ruído aleatório:** só padrões determinísticos
   (trama do couro, nervuras, costuras). Rebite a cada 8 px, parafuso nos cantos, limo verde nas frestas, 2 ou 3 arranhões.
   **Nenhuma face maior que 8x8 px sem detalhe.** Vidro translúcido com reflexo diagonal. `_glow` só na lanterna e na pérola.
4. **Animação com peso.** Entre 6 e 8 chaves por ciclo (curva suave, não vai-e-volta linear); partes secundárias com
   **atraso de fase** (a bandeirola atrasa em relação ao balanço, a boiazinha de vidro em relação à corda); só use períodos
   que dividem a duração do clipe, para o laço fechar sem salto. Animação existe para contar o estado, não para enfeitar.
5. **Itere olhando.** Rode `C:\Python314\python tools/models/machines/cultivation_rope/build_model.py --preview` (e o da
   boia) e **abra as imagens de `build/machines/`**. Corrija até passar no checklist. Máximo de **4 ciclos de prévia por peça**.
6. **Checklist (os 7 da bíblia de arte, mais a harmonia):** (1) silhueta lê em 3 cantos; (2) sem face grande lisa; (3) rampas
   e paleta respeitadas; (4) clipes em laço sem salto; (5) brilho só onde acende; (6) cabe na colisão; (7) aparece certo na
   aba "Máquinas" e no jogo; (8) **corda e boia casam com o Garrafão** (altura, espessura, trama, latão, extremidades rente).

## 5. Entregas e verificação
- Pastas `tools/models/machines/cultivation_rope/` e `.../cultivation_buoy/` com `build_model.py` (rode com `--preview`
  e `--register`, que grava a versão no Atlas sem apagar as antigas), `.bbmodel`, `rig.json`, textura e `_glow`.
- Atlas: a aba "Máquinas" lê o `rig.json` sozinha; rode `tools/atlas/tests/test_catalog.py` e depois
  `C:\Python314\python tools/atlas/build.py` (o site local já está no ar e o rebuild atualiza as duas portas).
- No jogo: `./gradlew build` (leia só o fim) e a parte do `runClientGameTest` que cobre o cultivo
  (`AQUAMARINE_GAMETEST_ONLY=<parte>`; ache o nome com um `grep` curto em `IndustryWave3GameTest`).
  **No máximo 6 screenshots:** uma **linha completa** (Garrafão, 2 Cordas, Boia) vista do lado, dentro d'água, e a boia
  nos estados `idle`, `run` e `full`. Descreva cada uma em 1 linha.
- Receitas, nomes e lang **não mudam**.

## 6. Regras e orçamento
- Esforço `high` **autorizado só nesta tarefa** (qualidade é o objetivo); sem exploração aberta do repositório. Duas falhas
  iguais: registre no handoff e pare.
- Commits pequenos em português com a coautoria do seu agente; `git pull --rebase=merges` antes do push; só edite os arquivos
  desta tarefa (em arquivos compartilhados, apenas acrescente linhas).
- Ao terminar: apague sua linha de "Em andamento", escreva o handoff em `docs/progresso-0.6.1/faixa-7g.md` (3 linhas: o
  que ficou, o que não testou, o que o Adriano deve olhar primeiro) e entregue as capturas.
