# Prompt do Codex: Coletor de Neutrônio 3x2, animado, com tela própria (0.6.1)

Cole o texto abaixo, inteiro, como primeira mensagem do Codex, numa worktree própria. **Enviar este prompt é a liberação
desta tarefa pelo Adriano.** Escopo único: o **Coletor de Neutrônio** (modelo, estrutura de vários blocos e tela).

---

Você é o agente de **modelagem e interface** do projeto **Aquamarine** (mod Fabric, Minecraft Java 26.3, repositório
`C:\Users\adria\Documents\AQUAMARINE`). Tarefa: **refazer o Coletor de Neutrônio (`neutronium_collector`)** como uma
máquina grande de **3 de largura x 2 de altura x 3 de profundidade** (os 3 de profundidade são decisão do plano; deixe numa
constante), com **modelo animado, design próprio e criativo** e uma **tela personalizada** (progresso e dados). O jogador tem de
**ver o Neutrônio sendo processado e gerado pela energia**, na máquina e na tela. Qualidade no nível do Garrafão de Cultivo e do
Gerador de Fenda.

**Foco total:** só o Coletor. Não analise nem refatore o resto. Leia apenas: `AGENTS.md` (economia), o cabeçalho e a "Bíblia
de arte das máquinas" de `docs/12-industria.md`, o docstring de `tools/models/machines/machine_kit.py`, e como **referência de
nível e método** `tools/models/machines/vent_generator/build_model.py` e `.../cultivation_jar/build_model.py`. Java de
partida: `com.aquamarine.energy` (`NeutroniumCollectorBlock`, `NeutroniumCollectorBlockEntity`, `WaterEnergy`, `ModEnergy`),
`Waterlogging` (base comum de água da 7A) e, para a tela, `menu/AbyssalForgeMenu`, `menu/ModMenus` e
`client/screen/AbyssalForgeScreen`. Registre a tarefa em "Em andamento" do `CONTEXTO.md`. O jogo é recurso exclusivo: se o
Gradle reclamar de lock, peça ao Adriano para fechar.

## 1. O que a máquina faz hoje (não mude as regras)
Só em oceano profundo, com água em cima e 20 blocos abaixo do nível do mar; gasta 64 E/t; junta 1 Pó Estelar a cada 6000
ticks (guarda até 16); capacidade 100 000 E, entrada de 512 E/t. Receita, nome e `lang` ficam como estão.

## 2. Estrutura de vários blocos (3x2x3)
- **Controlador** no bloco do **centro da base**; os outros 17 blocos são **partes** (um bloco de parte, sem item). Colocar a
  máquina exige o espaço todo livre (ou com água) e vira para quem coloca; **quebrar qualquer parte quebra tudo e solta um
  item só** (com o pó guardado). Todas as partes são `waterlogged` (use a base da 7A), com colisão e seleção no formato do
  modelo, parte por parte.
- A energia entra **por qualquer parte** (a `EnergyStorage.SIDED` das partes repassa ao controlador); o Cabo Aquático liga
  em qualquer parte (confira `AquaticCableBlock.connects` e o que usar de `NeutroniumCollectorBlock`). **A tela abre clicando
  em qualquer parte.**
- **Coletores antigos (1 bloco) nos mundos salvos:** ao carregar, o controlador tenta criar as partes; se o espaço não
  couber, **quebra e solta o item** (com o pó). Registre isso no handoff.
- O desenho (BER) é **um só, no controlador**, com a caixa de renderização aumentada para a máquina inteira (senão some quando
  o controlador sai da tela). Dados e animação vêm do `MachineView`/`MachineMotion`, sincronizados só quando muda (padrão do
  Garrafão).

## 3. Design do modelo (seja criativo; esta é a máquina mais importante do mod)
Tema: **latão, vidro e vapor** com a identidade do **Neutrônio** (estrelado, frio, perolado). Paleta só do `machine_kit`
(`BRASS`, `STEEL`, `GLASS`, `CYAN`, `IVORY`, `PATINA`...); o brilho do núcleo é `_glow`. Folha de textura: **32 px por face**.
O kit tem folha 128x128 fixa e esta máquina não cabe; **parametrize a folha** (256 ou 512) **sem mudar a saída das outras
máquinas** (só acrescente; rode uma outra máquina antes e depois e confira que o PNG não mudou).

Ideia-base (você pode melhorar): um **reator de coleta** em três andares de leitura:
1. **Captação (embaixo):** boca larga de latão com grade na frente e dois braços funil que "puxam" a água; pó estelar
   entra pelos funis.
2. **Câmara de compressão (centro, a peça principal):** uma **esfera de vidro** (octógono de caixas) com um **núcleo
   estrelado** que cresce e fica mais denso conforme o progresso, **dois ou três anéis de latão** em eixos diferentes que
   aceleram, e **partículas de pó (cubinhos) orbitando**.
3. **Bandeja de saída (frente, embaixo à direita):** o Pó Estelar pronto cai numa bandeja de vidro; fica acumulado, e a
   quantidade se vê.
Detalhes: **cabos e bobinas de energia** que acendem em ciano enquanto entra carga (arco que corre dos 4 pontos de entrada ao
núcleo), manômetros, tubulação de latão e rebites, vidro com reflexo, uma **lâmpada de aviso** (vermelha quando o lugar não
serve, âmbar parada, azul-branca funcionando). Silhueta forte e simétrica; sem face grande lisa.
- **Orçamento:** 120 a 200 cubos, ossos nomeados com pivô no eixo real (anéis no centro do núcleo, funis na base).
- **Estados** (`MachineState`: `IDLE`, `RUNNING`, `FULL`), clipes `idle`, `start`, `run`, `full`, `stop` e `collect`:
  - `idle` (sem energia ou lugar inválido): anéis parados, núcleo pequeno e **apagado**, lâmpada âmbar (ou vermelha se o
    lugar não serve: dado extra sincronizado);
  - `run`: **o progresso é visível**: núcleo cresce e brilha com o andamento (use o andamento 0 a 100 % do servidor), anéis
    aceleram com ele, pó orbita mais rápido e é sugado dos funis, arcos de energia correndo;
  - `collect`: ao completar, **pulso de luz** no núcleo, anéis desaceleram, **um grão de pó cai na bandeja**;
  - `full` (16 pós): núcleo estável e brilhante, bandeja cheia, lâmpada piscando.
- **Partículas** com parcimônia (no máximo 2 por tick): bolhas subindo da câmara, brilho de `END_ROD` no pulso.
- **Animação com peso:** 6 a 8 chaves por ciclo, movimento secundário atrasado, laços sem salto.

## 4. A tela personalizada (ao clicar)
`MenuType` e `Screen` novos (copie o padrão do `AbyssalForgeMenu`, `ModMenus` e do registro no cliente). Visual **latão e vidro**,
textura de GUI gerada por script (`tools/gen_collector_gui.py`, com os pintores do `machine_kit`), sem imagem copiada.
Mostra, **sempre ao vivo**:
- **Barra de energia** (raios correndo, números "atual / capacidade" e consumo em E/t);
- **Progresso do próximo pó** em anel ou barra em torno de um **núcleo animado** (quadros desenhados por tempo de jogo, não
  precisa de `.mcmeta`), com porcentagem e **tempo restante** estimado;
- **Pó Estelar guardado** (x / 16) e um **slot de saída** para tirar o pó (ou botão "Coletar", como achar melhor para o inventário);
- **Estado e motivo** em texto e cor: "Funcionando", "Sem energia", "Local inválido: precisa de oceano profundo, 20 blocos
  abaixo do nível do mar" (com o que falta), "Cheio";
- dicas ao passar o mouse (tooltips) em cada medidor; textos em pt-BR e en-US nos dois `lang`.
Cuidado: o `ContainerData` do jogo manda inteiros curtos; **não mande 100 000 de energia direto**. Mande em partes ou como
porcentagem mais o valor calculado, ou use o mecanismo estendido do Fabric (confira como o `AbyssalForgeMenu` e o projeto
fazem). Sincronize só o necessário e só enquanto a tela está aberta. Clicar abre a tela, agachado ou não; retire a mensagem
de overlay antiga e o "pegar pó com a mão vazia" (agora é o slot ou botão da tela).

## 5. Como modelar com qualidade (o método que importa)
1. **Desenhe antes de codar:** 10 a 12 linhas com silhueta, 3 detalhes que se leem de longe, material de cada parte e o **porquê**
   de cada movimento. Cole no handoff.
2. **Muitas peças pequenas, em camadas;** chanfro com caixas empilhadas, passos de 0,5 px. Nada grande e liso: cada peça tem
   função. Ossos nomeados com pivô no eixo real.
3. **Um pintor por material, rampa de 5 a 6 tons**, luz do alto à esquerda, contorno só onde ajuda; sem ruído aleatório
   (padrões determinísticos); rebite a cada 8 px, limo verde nas frestas, poucos arranhões; **nenhuma face maior que 8x8 px
   sem detalhe**; vidro translúcido com reflexo; `_glow` só no que acende (núcleo, bobinas ativas, lâmpada, pó).
4. **Itere olhando:** rode o `build_model.py` com `--preview` e **abra as imagens de `build/machines/`**; máximo de **4 ciclos
   de prévia**. Na tela, máximo de **3 ciclos** de captura.
5. **Checklist:** (1) silhueta lê em 3 cantos; (2) sem face grande lisa; (3) paleta e rampas; (4) laços sem salto; (5) brilho
   só onde acende; (6) colisão e seleção casam com o modelo; (7) aparece certo na aba "Máquinas" do Atlas (confira o
   **enquadramento**: a máquina é maior que 1 bloco) e no jogo; (8) combina com a Fenda e o Garrafão; (9) a **tela** não corta
   texto em pt-BR e en-US.

## 6. Entregas e verificação
- Pasta `tools/models/machines/neutronium_collector/` com `build_model.py` (rode com `--preview` e `--register`, que grava no
  Atlas sem apagar versões), `.bbmodel`, `rig.json`, textura e `_glow`; `tools/gen_collector_gui.py` e a textura da tela.
- Atlas: `tools/atlas/tests/test_catalog.py` e depois `C:\Python314\python tools/atlas/build.py` (o site local atualiza).
- Jogo: `./gradlew build` (leia só o fim) e a parte `energy` do `runClientGameTest` (ajuste o `EnergyGameTest` do Coletor para a
  estrutura nova, **mínimo necessário**; mantenha o `forceValidSpot`). Testes obrigatórios: coloca a estrutura, quebrar uma
  parte quebra tudo e solta um item, energia entra por uma parte de fora, a tela abre e os dados batem, o coletor antigo migra.
- **No máximo 8 screenshots:** 4 do modelo (de fora em `idle`, `run` no começo do progresso, `run` perto do fim e `full`)
  e 4 da tela (`idle`, `run`, local inválido, `full`). Descreva cada uma em 1 linha.

## 7. Regras e orçamento
- Esforço `high` **autorizado só nesta tarefa**; sem exploração aberta do repositório. Duas falhas iguais: registre e pare.
- Commits pequenos em português com a coautoria do seu agente; `git pull --rebase=merges` antes do push; só edite os arquivos
  desta tarefa (em arquivos compartilhados, apenas acrescente linhas). Decisão de design nova que mude a progressão do Neutrônio
  vai ao Adriano; o resto é criativo e seu.
- Ao terminar: apague sua linha de "Em andamento", escreva 3 linhas no handoff do `CONTEXTO.md` (o que ficou, o que não
  testou, o que o Adriano olha primeiro) e entregue as capturas.
