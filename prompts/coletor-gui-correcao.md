# Prompt do Codex: correção da tela do Coletor de Neutrônio

Cole o texto abaixo, inteiro, como primeira mensagem do Codex, numa worktree própria (anexe a captura do Adriano, se puder).
**Enviar este prompt é a liberação desta tarefa pelo Adriano.** Escopo único: a **tela** do Coletor.

---

Você é o agente de **interface** do projeto **Aquamarine** (mod Fabric, Minecraft Java 26.3, repositório
`C:\Users\adria\Documents\AQUAMARINE`). Tarefa: **corrigir a tela do Coletor de Neutrônio**, que está bugada e feia no jogo.
Só a tela: **não mexa no modelo 3D, na estrutura 3x2x3, nas regras nem na energia.**

**Foco total:** leia apenas `AGENTS.md` (economia) e estes arquivos: `src/client/java/com/aquamarine/client/screen/NeutroniumCollectorScreen.java`,
`src/main/java/com/aquamarine/menu/NeutroniumCollectorMenu.java` (só as posições dos slots e os dados), `tools/gen_collector_gui.py`,
`textures/gui/container/neutronium_collector.png`, as chaves `gui.aquamarine.collector.*` dos dois `lang`, e como referência de
qualidade visual o painel da Forja Abissal e as máquinas do `machine_kit`. Registre a tarefa em "Em andamento" do `CONTEXTO.md`.
Se o Gradle der lock, peça ao Adriano para fechar o jogo.

## 1. Defeitos vistos na captura do Adriano (corrija todos)
1. **Título "Coletor de Neutrônio" colado e cortado pela moldura de cima** (`titleLabelY=5` cai em cima da faixa de aço).
2. **Os rótulos "Energia", "Compressão" e "Pó guardado" invadem as bordas dos painéis** (desenhados em `y=15`, os painéis de
   vidro começam em `y=18 a 23`). **"Pó guardado" é mais largo que o painel (47 px) e vaza para fora da moldura.**
3. **O texto "Falta 5:00" (aparece como "Falta 500") fica em cima da borda inferior do painel central**, em cinza escuro sobre
   verde escuro, quase ilegível, e encosta na faixa de energia.
4. **Barra de energia minúscula e perdida** num painel de vidro quase vazio; sem enchimento visível quando a carga é 0.
5. **O "núcleo animado" é uma cruz branca chapada** (duas caixas e um quadrado com degradê) com pontinhos soltos em volta:
   não parece um núcleo estrelado nem um anel de progresso.
6. **O slot de saída é um quadradinho escuro pequeno e solto** dentro do painel de estoque; a textura e a posição real do slot
   da `Menu` precisam coincidir pixel a pixel.
7. **A linha de estado quebra em 2 linhas** ("Local inválido: precisa de oceano / profundo") e a 2ª linha estoura a caixa de
   18 px; o texto também perdeu a informação "20 blocos abaixo do nível do mar".
8. **Manchas de limo soltas** (por exemplo `(8,110)` e `(210,71)`) que parecem pixels com defeito; e o rótulo "Inventário"
   sumiu.
9. No geral: **blocos de cor chapada, sem profundidade nem a qualidade das máquinas**; margens irregulares; o painel de
   inventário é só um retângulo dourado com slots escuros.

## 2. O que fazer (redesenhe a tela com uma grade e limites seguros)
- **Defina uma grade em constantes** (margem externa 8 px, faixas de moldura que **ninguém** invade, caixas nomeadas:
  `HEADER`, `ENERGY`, `CORE`, `STOCK`, `STATUS`, `INVENTORY`). Texto **só dentro das caixas**, com 3 px de folga; use
  `font.width(...)` e **encurte, reduza a escala ou quebre** o texto para caber. Cada rótulo vai numa **plaquinha de latão
  própria** desenhada na textura (em vez de solto em cima do vidro). O título fica numa placa de cabeçalho, centralizado.
- **Estética: latão, vidro e vapor**, a mesma da máquina. Textura gerada por `tools/gen_collector_gui.py` com as rampas e
  pintores do `machine_kit` (5 a 6 tons, luz do alto à esquerda, rebites a cada 8 px, chanfro, **sem ruído aleatório**,
  nenhuma área grande lisa). Remova as manchas soltas; se usar limo ou sal, só em frestas e cantos, de forma deliberada.
- **Energia:** um **tubo de vidro vertical grande** (mais alto e largo que o atual), com marcações de graduação e
  **líquido de energia ciano** que sobe e tem raios correndo; mostra "vazio" de forma bonita quando a carga é 0; o valor
  em E e a porcentagem ficam **fora** do tubo, numa plaquinha.
- **Núcleo:** troque a cruz por um **núcleo desenhado em pixel art**: esfera estrelada com anel de progresso de 24 a 32
  marcas que acendem com o andamento; use uma **faixa de quadros** (spritesheet, 8 quadros) gerada pelo script e escolhida
  pelo tempo de jogo, com a pulsação mais forte quando está funcionando, **apagado e menor quando parado**, e um pulso quando
  completa. A porcentagem fica **dentro da plaquinha de baixo**, legível; o "tempo restante" vai numa plaquinha própria
  (formato `5:00`, confira se o `lang` imprime os dois pontos).
- **Estoque de pó:** um **slot grande e bem emoldurado** (18x18 reais, alinhado ao da `Menu`) com a bandeja de vidro em volta e
  o contador "x / 16" numa plaquinha; o texto do título dessa caixa curto o bastante ("Pó" ou "Pó Estelar") para caber nos
  dois idiomas.
- **Estado:** caixa de **2 linhas** (altura de 24 px) com **ícone colorido** (âmbar sem energia, vermelho local inválido, azul
  funcionando, branco cheio). Texto **curto**; o detalhe completo, como "precisa de oceano profundo, 20 blocos abaixo do
  nível do mar" (e quanto falta), vai no **tooltip** da caixa. Nunca estoura a caixa em pt-BR nem en-US.
- **Inventário:** mantenha as posições dos slots da `Menu`, mas com **moldura de latão e fundo de aço com profundidade**;
  restaure o rótulo "Inventário" dentro de uma plaquinha.
- Cores de texto com contraste real (claro sobre escuro, escuro sobre latão); **sombra de texto só onde ajuda**.
- Tooltips continuam, nas caixas novas (confira as áreas de `isHovering` depois de mover tudo).

## 3. Método
1. **Desenhe antes de codar:** esboço em texto das caixas (coordenadas e tamanhos) no handoff.
2. **Fonte da verdade das posições:** as constantes do script e as da `Screen`/`Menu` devem vir do **mesmo lugar** (ou ser
   conferidas por um teste), para a textura e os slots nunca mais divergirem.
3. **Teste de limites:** um teste simples (unitário ou no GameTest) que, para cada texto de cada estado, nos dois idiomas,
   confirma `font.width <= largura da caixa`. Falhou, encurte o texto, não a fonte.
4. **Itere olhando:** rode a captura (`coletor_tela_*` em `EnergyGameTest`) e **abra as imagens**; máximo de **3 ciclos**.
   Confira também nas escalas de interface **2, 3 e 4 e automática**, sem texto cortado.
5. **Checklist:** (1) nada de texto sobre moldura ou borda; (2) rótulos e números cabem nas caixas em pt-BR e en-US;
   (3) slots coincidem com a textura; (4) núcleo e energia animam e leem bem; (5) cada estado (`sem energia`, `local
   inválido`, `funcionando`, `cheio`) tem aparência e cor próprias; (6) sem área grande lisa; (7) combina com a máquina e
   com a Forja Abissal.

## 4. Entregas e regras
- Arquivos: `NeutroniumCollectorScreen.java`, `tools/gen_collector_gui.py`, a textura (e a faixa de quadros), as chaves de
  `lang` estritamente necessárias (acrescente, não reordene) e o teste de limites. Rode o script para regenerar os PNGs.
- `./gradlew build` (leia só o fim) e a captura do GameTest. **No máximo 6 screenshots:** `funcionando`, `sem energia`,
  `local inválido`, `cheio`, mais uma em escala 4 e uma em en-US. Descreva cada uma em 1 linha.
- Esforço `high` autorizado só nesta tarefa; sem exploração aberta; duas falhas iguais, registre e pare. Commits pequenos em
  português com a coautoria do seu agente; `git pull --rebase=merges` antes do push; só os arquivos desta tarefa.
- Ao terminar: apague sua linha de "Em andamento" e escreva 3 linhas no handoff do `CONTEXTO.md` (o que ficou, o que não
  testou, o que o Adriano olha primeiro).
