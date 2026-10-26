# Fase 6: Kraken renovado, energia aquática e conforto do mergulhador (ideia)

> ## 🚧 LIBERADA em 26/10/2026 (organizada no mesmo dia)
> Ideias do Adriano para a release **0.6.0** (a Fase 5 não existe; a regra está em [00-decisoes.md](00-decisoes.md),
> "Versões e fases"). Liberada "o quanto o contexto de 5 horas suportar", em entregas organizadas
> ([00-decisoes.md](00-decisoes.md), "Fase 6 (respostas)"). Seções marcadas **(ideia)** são propostas do agente, não decisão.

> **Dono de:** a especificação de tudo que é da Fase 6. A **decisão** de que a fase existe e as **perguntas em aberto**
> moram em [00-decisoes.md](00-decisoes.md) (linha "Fase 6" e "Em aberto", códigos F6-1 a F6-8). Os marcos ficam em
> [07-roadmap.md](07-roadmap.md) e quem está fazendo o quê, em [../CONTEXTO.md](../CONTEXTO.md). Os números são
> valores iniciais a testar no jogo.

## Painel

| # | Faixa | Estado | Depende de | Pergunta |
|---|---|---|---|---|
| **6A** | **Kraken renovado (principal):** 3 modelos novos para escolher, colisão por parte, tentáculos que não brotam do nada, movimento dinâmico. **Absorve a antiga 4C** | **6A1 e 6A3 feitas** (26/10); 6A2 e 6A4 esperam a escolha | 6A2 e 6A4 esperam a escolha do modelo (6A1) | F6-1 |
| 6B | Fauna: colisão da baleia e do cachalote, carne de baleia | **6B1 e 6B2 feitas** (26/10) | nenhuma | F6-6 |
| 6C | Vila: skins 3D dos Mergulhadores, funções das mesas de trabalho | **6C2 feita** (26/10); 6C1 falta | nenhuma | F6-8 |
| 6D | Materiais e armas: Aurium, Forja Abissal nova, projétil 3D do Cavitador | **6D1, 6D2 e 6D3 feitas** (26/10) | nenhuma | F6-2, F6-7 |
| 6E | Energia aquática: geradores e Coletor de Neutrônio | **feita** (26/10) | 6D1 (se o Aurium entrar na cadeia) | F6-2, F6-3 |
| 6F | Encantamento: Mesa de Encantamento Aquática e descrições melhores | **feita** (26/10) | nenhuma | |
| 6G | Equipamento: slot de acessórios (mod Accessories) e Radar | **6G2 feita**; 6G1 sem Accessories para o 26.3 | 6G2 espera o formato do Radar | F6-4, F6-5 |
| 6H | Construção: mais decoração e blocos | **feita** (26/10) | 6D1 (a família do Aurium) | |

Sons: nenhum som novo nesta fase; ficam para uma subversão própria, futura ([00-decisoes.md](00-decisoes.md), linha "Sons (adiados)").

## Ordem sugerida (ideia)
1. **Já no começo:** 6A1 (gera o portão de escolha do Adriano) e as faixas pequenas e independentes (6A3, 6B1, 6D2, 6D3,
   6F2). Quem escolhe o modelo do Kraken destrava o resto da 6A.
2. **Depois da escolha:** 6A2 e 6A4 sobre o modelo escolhido.
3. **Sistemas novos, em sequência:** 6D1 (Aurium) → 6E (energia, depois o Coletor) → 6F1 → 6G → 6C → 6B2 → 6H.
4. **Release 0.6.0** quando as faixas liberadas fecharem: `version=0.6.0` no `gradle.properties`, pacote por
   `tools/release/montar_release.py` ([06-tecnico.md](06-tecnico.md), "Release para jogadores").

## 6A. Kraken renovado (principal)
**Pedido.** Fazer 3 modelos novos, "muito bem feitos, com texturização excelente", no estilo do filhote de Kraken, e pôr
todos no site para escolher o melhor; o modelo antigo não é apagado. Arrumar colisão e hitboxes. Arrumar os tentáculos
que "estão brotando no meio do mar do nada". Fazer o mob se mexer mais e ser mais dinâmico.

**Hoje.** O Kraken v2 está no jogo na escala 8 (~41 x 25 blocos) com **uma hitbox só** (36 x 24) e quase parado
([10-fase4.md](10-fase4.md), §3). Os 8 tentáculos são entidades separadas (60 de vida, hitbox 2 x 9) postas num anel de
28 blocos em volta, **sem ligação visível com o corpo** ([02-kraken.md](02-kraken.md), "Tentáculos"). O gerador é
`tools/models/kraken/` (`build_model.py`, `kraken_animations.py`) e o Atlas já guarda versões sem apagar as antigas
(`tools/atlas/register_version.py`; [tools/atlas/README.md](../tools/atlas/README.md)).

- **6A1 · Três modelos candidatos (ideia).**
  - Três silhuetas diferentes (a decidir ao desenhar), todas com a paleta e a linguagem da Cria v2 de [10-fase4.md](10-fase4.md),
    §1 (azul-marinho, acentos roxos, bioluminescência ciano, olhos em fenda) e textura com camada de brilho.
  - Registradas no Atlas como versões novas do Kraken (por exemplo `kraken-v3a`, `v3b`, `v3c`), **sem tocar no jogo**. Cada
    uma traz rig, textura e ao menos os clipes de base (parado, nado, uma pancada), lado a lado com a v2.
  - Para a escolhida poder entrar no jogo sem refazer os ataques, o rig segue o contrato de clipes de
    [06-tecnico.md](06-tecnico.md), "Contrato de modelos e entidades (Entrega 2)".
  - **Aceite:** as três aparecem no Atlas e o Adriano escolhe uma. A escolhida vira o modelo do jogo pelo gerador; a v2 e
    as outras ficam no histórico.
  - **Feito (26/10/2026).** Gerador `tools/models/kraken_v3/build_candidates.py` (molde da Cria v2: mesmas rampas, box-UV,
    camada `_glow` e formato de clipe), registrado no Atlas como `kraken/v3a`, `v3b` e `v3c`:
    - **v3a · Lula-colosso:** corpo deitado como torpedo, manto longo com faixas de fotóforos e colar aceso, barbatana em
      ponta de flecha, coroa de 8 braços para a frente, 2 tentáculos de caça com clava de ganchos, olhos enormes.
    - **v3b · Vampiro-abissal:** cúpula alta e larga, duas barbatanas de orelha que remam, 8 braços em guarda-chuva ligados
      por membrana, espinhos nas costas e lanternas acesas nas pontas, 2 filamentos de caça.
    - **v3c · Amonita-colosso:** concha em espiral de 18 câmaras nas costas (costelas, sutura acesa, espinhos na quilha,
      borda de nácar que brilha), capuz duro sobre a cabeça, 8 braços em leque e 2 tentáculos de caça.
    - Os três rigs têm os ossos do contrato de clipes (corpo, cabeça, mandíbula, manto que respira, barbatanas, pálpebras
      escondidas no olho, 8 braços de 6 juntas, 2 tentáculos de caça com clava); 6 clipes cada: `idle` (com piscada),
      `swim`, `slam`, `roar`, `grab` e `death`. Os outros 10 da `KrakenView` saem quando a escolhida entrar no jogo.
    - O Atlas acende o `_glow` no modo Textura e usa o UV de cada versão; `tools/atlas/tests/candidates.mjs` grava vistas de
      frente, lado, topo e costas para comparar. Testado: `tools/atlas/tests` (catálogo e navegador, 14 versões e 92 clipes).
- **6A2 · Colisão e hitboxes (antiga 4C, parte 1).** A especificação continua em [10-fase4.md](10-fase4.md), §3 (partes por
  cabeça, núcleo, manto e braços; dano por parte; cabeça e manto sólidos). Só começa **depois de 6A1**, porque as partes
  seguem as juntas do rig escolhido.
- **6A3 · Tentáculos que brotam do nada (ideia).**
  - *Hipótese a confirmar:* o anel de 28 blocos põe tentáculos soltos na água, longe de qualquer parte do Kraken.
  - Primeiro reproduzir e capturar a cena (parte `kraken` do `runClientGameTest`). Depois, uma de duas saídas ou ambas:
    (a) a raiz do tentáculo nasce de algo visível (leito, ou o corpo do Kraken); (b) a subida é anunciada (bolhas, sombra) e
    animada, em vez de aparecer.
  - **Aceite:** em captura, nenhum tentáculo surge sem raiz visível nem aviso; os ataques e o ciclo de corte seguem iguais.
  - **Feito (26/10/2026), hipótese confirmada:** o tentáculo do ataque era posto 1 bloco abaixo do jogador, no meio da água, e
    o que voltava do ataque era teletransportado para a casa no anel e reaparecia de repente. Agora (`KrakenAttacks`):
    (a) a raiz pousa no leito quando ele está perto (até 8 blocos sob o alvo, 16 sob a casa) e o chão se rompe em
    partículas do próprio bloco; (b) sem leito perto, uma sombra de tinta e uma coluna de bolhas sobem do fundo antes do
    clipe `rise`, e a raiz ferve durante o aviso; (c) depois do ataque o tentáculo afunda onde está (`release`, 1,1 s) e
    só então volta para casa, subindo de novo com aviso; os ociosos acompanham o corpo pousados no leito. Testado: parte
    `attack` do `runClientGameTest` (asserção nova: o tentáculo do agarrão nasce do leito; captura `18b_tentaculo_raiz`).
- **6A4 · Movimento dinâmico (antiga 4C, parte 2).** Manto que respira, braços que ondulam, cabeça que acompanha o alvo, órbita,
  investidas curtas e mergulho para reaparecer em outro ponto ([10-fase4.md](10-fase4.md), §3, "Movimento constante" e
  "Deslocamento"). Depende de 6A1.
- **Aceite da 6A:** os de 6A1 a 6A4 mais os de §3 (flecha na cabeça tira mais que no braço; quem está dentro do volume sai em
  até 1 s; pelo menos 3 mudanças de posição por minuto) e a parte `multiplayer` verde.

## 6B. Fauna
**Pedido.** Baleia e cachalote: colisão melhor, "caixas na cabeça e no rabo". Adicionar carne de baleia.

**Hoje.** As duas têm uma hitbox só (`AbstractWhaleEntity`). A Moréia já tem 7 partes de colisão (`MorayPartEntity`).
- **6B1 · Colisão por partes (ideia).** O mesmo mecanismo da Moréia, com três partes (cabeça, corpo, rabo), dano igual em todas
  (dano por parte é opcional). Não pode quebrar a Sela, a montaria de 3 nem a fuga do Kraken. **Aceite:** flecha na cabeça e no
  rabo acerta; o jogador não atravessa a cabeça; as partes `whale` e `moray` do `runClientGameTest` seguem verdes.
  **Feito (26/10/2026):** `WhalePartEntity` (sem salvar, recriada pela baleia) com cabeça, fim do corpo e rabo à frente e atrás
  da caixa, que fica como o meio do corpo (os modelos têm ~18 blocos). Dano igual; quem está montado não fere a própria
  baleia; clicar numa parte é clicar na baleia (Sela e montaria); a cabeça é sólida e empurra para fora quem ficou dentro.
  Testado na parte `whale` (3 partes nas duas espécies, flecha acerta cabeça e rabo, jogador posto na cabeça sai) e `moray`.
- **6B2 · Carne de Baleia (ideia).** Crua e cozida (fornalha, defumador e fogueira), dropada pela Baleia-de-Nereia e pelo
  Cachalote (Saque aumenta), alimento forte. Texturas por script. Regra do que acontece com baleia domada: F6-6.
  **Feito (26/10/2026):** `tools/gen_phase6_fauna.py` (ícones, receitas de fornalha, defumador e fogueira, tag `minecraft:meat`).
  Crua 3 de fome; cozida 10 de fome e saturação 0,9 (o bife é 8). Cai 3 a 5, mais até 1 por nível de Saque, da Baleia-de-Nereia
  **selvagem** (a domada não dá, F6-6) e do Cachalote. Testado na parte `whale`.

## 6C. Vila
**Pedido.** Melhorar as skins dos aldeões "com assets em 3D". Adicionar funcionalidade às mesas dos aldeões.

**Hoje.** Os Mergulhadores têm skin própria sobre o modelo vanilla, em 3 profissões ([08-fase3.md](08-fase3.md), §8). As mesas
são a Bigorna de Maré, a Mesa de Cartas Náuticas e o Altar de Concha, em 3D desde a 4D, e **só servem de bloco de trabalho**.
- **6C1 · Skins 3D (ideia).** Camadas 3D sobre o aldeão, como a armadura faz: capacete de escafandro, cilindro de ar, mangueiras
  e uma peça por profissão; mais a skin base melhorada. **Aceite:** captura das 3 profissões de frente e de lado; as trocas e a
  reputação seguem iguais.
- **6C2 · Funções das mesas (ideia, F6-8).**
  - *Bigorna de Maré* (Ferreiro): reparar e renomear o equipamento do mod sem o custo crescente da bigorna comum.
  - *Mesa de Cartas Náuticas* (Cartógrafo): copiar e ampliar os mapas do mod e marcar locais.
  - *Altar de Concha* (Encantador): trocar o encantamento de um livro aquático ou desencantar. Relação com a 6F1: F6-8.
  - **Feito (26/10/2026):** as três viraram `WorkstationBlock` (mesmo ID, modelo e posto de trabalho) e abrem as telas do jogo:
    a Bigorna de Maré abre a bigorna (no equipamento do mod o custo de reparo não cresce, `AnvilMenuMixin`, e ela não se
    desgasta), a Mesa de Cartas Náuticas abre a cartografia (copiar, ampliar e travar mapas) e o Altar de Concha abre o rebolo
    (desencantar). `WorkstationMenusMixin` deixa as telas abertas perto delas. Trocar o encantamento de um livro ficou de fora.
    Testado na parte `energy` (as três abrem e continuam abertas; reparo na Bigorna de Maré sem aumento de custo).

## 6D. Materiais e armas
**Pedido.** Melhorar o design da Forja Abissal. Novo minério intermediário, o **Aurium**, mais forte que o Abissal e mais fraco
que o Neutrônio. O Cavitador de Neutrônio atirar "uma sprite personalizada 3D em vez de um tridente comum".
- **6D1 · Aurium (ideia, F6-2).** Família completa como as outras: minério, bruto, lingote, bloco, ferramentas, armadura com bônus
  de conjunto na água (menor que o do Neutrônio), texturas por script, receitas na Forja. Referências para calibrar:

  | Material | Armadura | Escudo do Kraken | Observação |
  |---|---|---|---|
  | Abissal | 3/7/5/3 | 60% | levemente abaixo da netherite; dourado |
  | **Aurium** | entre os dois | ~70% | cor própria, que não confunda com o dourado Abissal nem com o ciano Neutrônio |
  | Neutrônio | 4/9/7/4 | 80% | acima da netherite; ciano; bônus fortes na água |

  **Feito (26/10/2026)** por `tools/gen_phase6_aurium.py` (texturas, dados, receitas, tags, mundo, nomes) e o Java em
  `ModMaterials`, `ModItems`, `ModBlocks`, `AuriumEffects` e `AuriumOre` (decisões em [00-decisoes.md](00-decisoes.md),
  linha "Aurium (6D1)"):
  - **Cor:** pérola leitosa com reflexos de opala rosa e ciano; os ícones e a armadura usam a silhueta das peças Abissais.
  - **Números:** armadura 3/8/6/3, resistência 3, repulsão 0,1, durabilidade 45; ferramentas com 2300 usos, velocidade 9,2,
    espada com 8 de dano, mineram debaixo d'água sem perda (como as Abissais) e ferem o escudo do Kraken a 70%.
  - **Brilho Perolado** (conjunto completo, submerso): Poder do Conduíte e o nado rápido do conjunto Abissal.
  - **Fonte:** Minério de Aurium na ardósia sob o leito dos oceanos profundos (y −48 a 0, veios de 4, 3 tentativas por pedaço,
    metade some se encostar no ar), que dá Aurium Bruto (Fortuna; Toque Suave dá o minério); fornalha e alto-forno dão o
    lingote. Ferramentas e armadura na Forja Abissal, com os desenhos das Abissais.
  - **Cadeia do Neutrônio:** o Lingote Instável pede 4 Lingotes de Aurium no lugar dos 4 Abissais.
  - Testado: `build` (inclui o escudo a 70%) e partes `neutronium` (receitas) e `building` (o conjunto dá Poder do Conduíte na
    água; vitrine `construcao_02_fase6`).
- **6D2 · Forja Abissal nova (ideia).** Modelo 3D como a Bigorna de Maré da 4D (bloco que vira para quem coloca, colisão no
  formato), com brasas e luz própria, chaminé ou tubo e bolhas. Mantém ID, receitas e a tela. **Aceite:** captura e a parte
  `forge` do `runClientGameTest` verde.
  **Feito (26/10/2026):** `tools/gen_phase6_art.py` gera o modelo e 8 texturas (tijolos abissais, bancada dourada com a grade
  3x3, pilares, brasas e metal fundido animados, cadinho, chaminé de latão). `AbyssalForgeBlock` virou `FacingShapeBlock`
  (luz 11, colisão no formato); a boca solta brasas e a chaminé, bolhas debaixo d'água ou fumaça no seco. Captura
  `05b_forja_3d`; a parte `forge` precisa rodar junto com a `showcase`, que coloca a Forja.
- **6D3 · Projétil 3D (ideia, F6-7).** Hoje o arpão em voo é o tridente do jogo (`ThrownTridentMixin`; o Cavitador fora d'água lança
  arpão). Proposta: entidade e renderer próprios com modelo 3D por ponta e rastro de partículas. **Aceite:** captura do
  projétil em voo por ponta; as partes `gear` e `neutronium` verdes.
  **Feito (26/10/2026), pela proposta F6-7:** o arremesso continua o do tridente (Lealdade, Fisgada e Perfurante iguais); só o
  desenho muda. O `ThrownTridentMixin` guarda o desenho num dado sincronizado (`HarpoonStyleHolder`: Arpão sem ponta, as 4
  pontas e o Cavitador) e o `HarpoonRenderer` substitui o do tridente: desenha o comum para tridentes do jogo e o
  `HarpoonModel` (haste, punho, colar, lâmina com farpas, aletas) com textura por desenho (`tools/gen_phase6_art.py`). Rastro:
  bolhas na água e uma partícula por ponta. Captura `neutronio_03b_arpoes`; partes `gear`, `neutronium` e `multiplayer` verdes.

## 6E. Energia aquática
**Pedido.** Geradores de energia aquáticos. Um **Coletor de Neutrônio** que coleta pó de neutrônio usando os geradores; o pó é
"bem lento e difícil de transformar em neutrônio normal".

**Hoje.** O mod **não tem sistema de energia**; a Bobina Voltaica é só uma bateria de item.
- **6E1 · Geradores (ideia, F6-3).** Três fontes naturais da água: turbina de correnteza (água corrente), gerador de fenda (magma
  e colunas de bolhas debaixo d'água) e boia de ondas (na superfície, melhor em tempestade). Transmissão simples (cabos ou
  blocos vizinhos), a detalhar. A unidade pode ser a da Team Reborn Energy, para conversar com outros mods: **F6-3**.
- **6E2 · Coletor de Neutrônio (ideia, F6-3).** Bloco submerso, só no fundo dos oceanos profundos (onde o minério existe,
  [10-fase4.md](10-fase4.md), §8), ligado a geradores. Taxa muito baixa. O que ele entrega e como o pó vira Neutrônio:
  F6-3 (é a Poeira que já existe, ou um pó novo que pede uma etapa cara?).
- **Feito (26/10/2026), 6E1 e 6E2.** Energia da **Team Reborn Energy 5.0.0** (pede Minecraft 26.1+, vai dentro do jar; os blocos
  expõem `EnergyStorage.SIDED` e conversam com outros mods). Java em `com.aquamarine.energy`; arte e dados em
  `tools/gen_phase6_energy.py`. Clique com a mão vazia mostra a carga.
  - **Turbina de Correnteza:** 4 E/t por lado com água corrente (até 24). **Gerador de Fenda:** 32 E/t com magma embaixo e água
    em cima. **Boia de Ondas:** 4 E/t na superfície (água embaixo, céu em cima), o dobro na chuva, quatro vezes na tempestade.
    Guardam 20 000 E e empurram até 128 E/t para os vizinhos; acendem quando geram.
  - **Cabo Aquático:** liga sozinho nos vizinhos com energia, iguala a carga entre cabos e entrega 256 E/t às máquinas.
  - **Coletor de Neutrônio:** só trabalha em oceano profundo, com água em cima e 20 blocos abaixo do nível do mar; gasta 64 E/t
    e junta 1 **Pó Estelar de Neutrônio** a cada 5 minutos (guarda até 16; clique para pegar). Conversão cara na Forja:
    8 Pós + 1 Cristal de Maré = 1 Poeira de Neutrônio. Receita do Coletor com Poeira, Aurium, Bobinas e um Conduíte.
  - Testado na parte `energy` (nova): os três geradores produzem no lugar certo, a tempestade quadruplica a Boia, o cabo leva a
    energia da Fenda ao Coletor, o Coletor recusa o mundo raso e, com o lugar liberado só no teste, junta um Pó; todos aparecem
    na API da Team Reborn. Não testado: com máquinas de outro mod.

## 6F. Encantamento
**Pedido.** Mesa de encantamento aquática, "com apenas encantamentos aquáticos". Melhorar as descrições dos encantamentos.

**Hoje.** 8 encantamentos do mod ([08-fase3.md](08-fase3.md), §7), cada um com uma chave `.desc` de uma linha em
`lang/*.json`.
- **6F1 · Mesa de Encantamento Aquática (ideia).** Bloco com tela de mesa que oferece só os encantamentos aquáticos (os do mod
  mais os do jogo que são da água, por uma tag `aquamarine:aquatic_enchantments`). Custo em material do mod em vez de lápis (a
  detalhar). Os mesmos encantamentos continuam à venda com o Encantador de Fossa.
  **Feito (26/10/2026):** usa a tela e as regras da mesa do jogo (lápis, estantes, níveis); o `EnchantmentMenuMixin` troca o
  sorteio pela tag `aquamarine:aquatic_enchantments` (os 8 do mod e Respiração, Afinidade Aquática, Passos Profundos, Passos
  Gelados, Empalamento, Correnteza, Lealdade, Canalização, Sorte do Mar e Isca) e mantém a tela aberta perto dela. O custo
  continua em lápis (o material do mod fica para depois, se o Adriano quiser). Modelo 3D com concha e pérola acesa em
  `tools/gen_phase6_art.py`. Testado na parte `energy` (abre, continua aberta e só oferece encantamentos da tag).
- **6F2 · Descrições melhores (ideia).** Cada `.desc` ganha: o que faz, quanto por nível e onde vale (peça ou arma), em pt-BR e
  en-US, e aparece no JEI (`client/compat/jei`).
  **Aceite:** os 8 revisados nos dois idiomas; o teste de consistência de recursos do `build` verde.
  **Feito (26/10/2026):** as 8 chaves `enchantment.aquamarine.<id>.desc` (a Quebra-Carapaça não tinha) dizem o que faz, quanto
  por nível, o máximo e onde vale, com os números do código. `EnchantmentDescriptions` mostra a linha no tooltip dos livros
  encantados e o JEI ganha a página de informação de cada livro, em todos os níveis. `build` verde.

## 6G. Equipamento
**Pedido.** Um slot extra para os Óculos de Mergulho, compatível com o mod **Accessories** (CurseForge). Usar ali também "a
bússola de pressão", a aljava (quiver), o regulador de pressão "e outros". Um **radar** que identifica mobs do mod e estruturas.

**Hoje.** Os Óculos disputam o slot da cabeça com o capacete ([05-itens.md](05-itens.md)). A bússola que existe é a **de Maré**
(planejada para a Cidade Perdida); o que mede pressão é o **Medidor de Pressão** (F6-4 pergunta se é esse).
- **6G1 · Slot de acessórios (ideia, F6-4). Pulada em 26/10/2026:** o Accessories não tem versão para o 26.3 (a mais nova é
  para o 1.21.10, no Modrinth e no Maven da wispforest). Fica para quando sair; nada muda no mod.
  Texto original: Integração **opcional** com o Accessories (slots e itens por dado): os Óculos num slot
  próprio, mais Medidor de Pressão, Aljava e Regulador de Pressão. Sem o Accessories o mod funciona como hoje. Antes de
  qualquer código, **confirmar que existe versão do Accessories para o 26.3** (uma busca em 26/10/2026 não achou).
- **6G2 · Radar (ideia, F6-5).** Item de uso ativo (ou acessório) que mostra num HUD os mobs do mod por perto (Kraken, Moréia,
  baleias, cachalote) e as estruturas conhecidas (o Porto; a Cidade Perdida quando existir). Gasta uma Bobina Voltaica, como o
  Propulsor, ou energia da 6E. Não substitui a Bússola de Maré nem o Mapa de Nereia, que apontam um destino só.
  **Feito (26/10/2026), Radar Aquático:** usar gasta uma Bobina Voltaica (o criativo não gasta) e liga por 60 s; acha o Porto
  mais perto na hora (até 48 pedaços). O HUD (`RadarHud`) mostra num quadro no canto de cima, com a frente para cima, o Kraken,
  tentáculos, Crias e ovos, Moréias, Baleias-de-Nereia e Cachalotes a até 64 blocos e o Porto na borda com a distância.
  Funciona com o radar em qualquer espaço do inventário. Testado na parte `energy` (liga, gasta uma Bobina e o HUD aparece na
  captura `energia_00_geradores`).

## 6H. Construção e decoração
**Pedido.** Mais itens de decoração e blocos de construção.

**Hoje.** 35 blocos aquáticos e 35 vidros rachados da 4E ([10-fase4.md](10-fase4.md), §6 e §7), gerados por
`tools/gen_phase4_building.py`.
- **6H (ideia).** Gerador novo, no mesmo molde (`tools/gen_phase6_building.py`). Pontos de partida: a família do Aurium (como
  os Ladrilhos Abissais), peças de navio e naufrágio (lemes, âncoras, redes, cordas, barris, lanternas de navio), estátuas,
  estandartes e aquários. Lista e tamanho a fechar com o Adriano ao liberar a faixa.
- **Feito (26/10/2026)** por `tools/gen_phase6_building.py`, que também gera o `ModDecorBlocks` (14 blocos):
  - Família do Aurium: Ladrilhos de Aurium (8 com 8 ladrilhos de ardósia e 1 lingote), escada, laje e muro (bancada e
    cortador), Ladrilho de Aurium Talhado (concha em leque) e Lampião de Aurium (luz 15).
  - Peças 3D que viram para quem as coloca, com colisão no formato: Lanterna de Navio (luz 15), Leme de Navio (na parede),
    Âncora Decorativa, Rolo de Corda, Barril de Carga, Rede de Pesca (sem colisão, com boias), Aquário (areia, coral e peixes,
    luz 8) e Estátua do Kraken (tentáculo de pedra com o olho aceso).
  - Estandartes ficaram de fora: os do jogo já servem. Testado: `build` e captura `construcao_02_fase6`.

## Aceite da Fase 6
- `./gradlew build` e a parte do `runClientGameTest` de cada faixa verdes; a parte `multiplayer` verde.
- As três candidatas do Kraken no Atlas e a escolha do Adriano registrada em [00-decisoes.md](00-decisoes.md).
- Site (Atlas) regerado a cada entrega; docs, roadmap e handoff atualizados.
- Só o Adriano fecha: o ritmo do Kraken renovado, o visual das três candidatas e o teste com jogadores reais.

## Fora da Fase 6
Cidade Perdida, Kraken completo (itens da luta, de exploração e pós-boss), áudio A1 a A5 e 4S: **sem fase e sem número**,
listados em [resumo.md](resumo.md), "O que falta".
