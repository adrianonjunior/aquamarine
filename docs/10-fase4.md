# Fase 4: Kraken vivo, oficina renovada, construção aquática e o Neutrônio

> ## 🚧 EM ANDAMENTO desde 25/10/2026
> Liberada pelo Adriano em 25/10/2026 ("isso será a fase 4. estou deixando você bem livre para gerir").
> O painel abaixo diz o que já está pronto. Cada entrega regera o site (Atlas), que mostra este arquivo.

> **Dono de:** a especificação de tudo que é da Fase 4. A **decisão** de abrir a fase e as escolhas de
> design moram em [00-decisoes.md](00-decisoes.md) (linha "Fase 4"). Os marcos ficam em [07-roadmap.md](07-roadmap.md),
> e quem está fazendo o quê, em [../CONTEXTO.md](../CONTEXTO.md). Os números são valores iniciais a testar no jogo.

## Painel

| # | Item | Estado | Onde |
|---|---|---|---|
| 4A | Cria, Ovo e Tentáculo do Kraken: modelos e texturas refeitos | ✅ **feito** (25/10/2026) | §1 |
| 4B | Oricálcio vira **Abissal** (minério, lingote, ferramentas, armadura, pontas, arpão) | ✅ **feito** (26/10/2026) | §2 |
| 4C | Kraken: hitbox por tentáculo e na cabeça, cabeça sólida, muito mais movimento | a fazer | §3 |
| 4D | Retexturas da oficina, das pontas, dos itens de mergulho e dos troféus; rework da Boia de Âncora | ✅ **feito** (26/10/2026) | §4, §5 |
| 4E | Blocos de construção aquáticos e vidros quase quebrados para todos os vidros | ✅ **feito** (26/10/2026) | §6, §7 |
| 4F | **Neutrônio**: minério novo, cadeia de processos, equipamento com bônus na água, visual estilo Avaritia | ✅ **feito** (26/10/2026) | §8 |
| 4G | **Cavitador de Neutrônio**: arma de longo alcance debaixo d'água com modelo 3D e partículas | a fazer | §9 |
| 4S | Sons de tudo acima | **depois**, a pedido do Adriano ("não faça ainda") | §10 |

Ordem: 4A já entrou; 4B vem antes do resto porque o nome novo aparece em quase tudo que vem depois.
4C, 4D e 4E podem andar juntas; 4F depende de 4B; 4G depende de 4F.

## Pedido original (25/10/2026, organizado)
- Corrigir a textura da Cria, do Ovo e do Tentáculo (capturas no Atlas). Depois: "o modelo não foi bem feito, faça de novo".
- Kraken: hitboxes melhores, "o ideal é colocar individualmente por tentáculos e melhorar a da cabeça"; "fazer ele se
  mexer muito mais, no momento ele é estático"; "dá pra entrar na cabeça do Kraken e ficar lá, quero impedir isso".
- Refazer as texturas: Bigorna de Maré, Mesa de Cartas Náuticas, Altar de Concha, pontas Abissais (todas), Bobina
  Voltaica, Regulador de Pressão, Aljava de Arpões, Propulsor de Maré e troféus. Rework na Boia de Âncora.
- Blocos de construção estilo aquático; variações de vidros quase quebrados para todos os vidros.
- Oricálcio passa a se chamar **Abissal** (abyssal ore, abyssal ingot...).
- Minério novo, **Neutrônio**, azul ciano: série de processos de grande dificuldade, melhor que netherite, vários bônus
  na água. Depois: "dê um ar mais sobrenatural pro neutronium, estilo Avaritia mod, a armadura e variáveis também".
- Arma subaquática de Neutrônio que dá dano a distância debaixo d'água ("em cima da água ela funciona como lançador de",
  frase cortada: **lançador de arpões**, confirmado pelo Adriano em 26/10/2026); modelagem 3D de
  qualidade impecável, partículas e efeitos com maestria.
- Sons de qualidade para tudo isso, mas só depois.

## 1. Cria, Ovo e Tentáculo (4A, feito)
Um gerador só, `tools/models/spawnlings/build_model.py`, monta as três criaturas e grava tudo junto: o Java do modelo
e das animações (um método por clipe), as texturas (`<criatura>.png` e `<criatura>_glow.png`), o mapa UV, o `.bbmodel`
e um `rig.json`, que o Atlas lê (o site mostra o modelo que está no jogo, sem cópia à mão). Os moldes antigos saíram.
- **Paleta do Kraken adulto** (azul-marinho, acentos roxos, bioluminescência ciano): as três eram roxas e destoavam.
- **Tentáculo:** braço de ~8 blocos em S com 12 segmentos que afinam devagar. A seção é arredondada: duas caixas
  cruzadas com os cantos recuados. Tem ventosas em zigue-zague acesas na parte de dentro, espinhos nas costas e ponta
  em gancho. Hitbox 2 x 9.
- **Cria:** proporção de filhote, com cabeça maior que o manto e manto cônico inclinado para trás. As barbatanas ficam
  nos lados (no topo pareciam orelhas). Os olhos grandes têm pupila em fenda horizontal, como os de um polvo. Tem seis
  braços de 4 juntas com ventosas e dois tentáculos de caça que avançam com clava. Hitbox 1,0 x 1,3 (era 0,9 x 0,9,
  menor que o corpo).
- **Ovo:** bolsa alta em forma de ovo, com membrana translúcida e veias discretas. Dentro há um filhote enrolado (olhos
  acesos, quatro bracinhos) e no topo uma tampa em quatro pétalas que abrem na diagonal ao eclodir. O renderizador
  desenha em três passadas: filhote opaco, brilho e membrana. Antes a membrana escondia o filhote.
- Atlas: as três estão registradas como **v2** (a v1 segue no histórico de versões).
- Testado no jogo pela parte `spawnlings` do `runClientGameTest` (capturas de frente, costas e de perto).

## 2. Oricálcio → Abissal (4B, feito)
- Nomes: Minério Abissal, Lingote Abissal, Bloco Abissal, Espada/Picareta/Machado/Pá/Enxada Abissal, Capacete/Peitoral/
  Calças/Botas Abissais, Arpão Abissal, Ponta Abissal. Em inglês: Abyssal Ore, Abyssal Ingot etc.
- IDs: `orichalcum_*` virou `abyssal_*` (não colide com `abyssal_forge` nem com `abyssal_altar`). Os IDs antigos ficaram
  como **apelido** de registro (`addAlias` da Fabric em `ModItems` e `ModBlocks`): os mundos da release 0.4.0, o mundo
  LAN de teste incluído, abrem com os itens e os blocos já Abissais.
- Mudaram também classes, texturas (renomeadas), receitas, loot, tags, descrições e docs vivos (o histórico guarda o
  nome antigo). A cor **continua dourada**: o ciano é do Neutrônio, e os dois níveis não se confundem. A ponta ganha
  textura nova em §4.
- Teste: a parte `forge` do `runClientGameTest` lê um item salvo como `aquamarine:orichalcum_pickaxe` e confere que
  ele abre como Picareta Abissal, e que `orichalcum_ore` aponta para o Minério Abissal.

## 3. Kraken: partes, cabeça sólida e movimento (4C)
Hoje o Kraken é uma caixa só (36 x 24 blocos). Dá para ficar dentro do modelo, na cabeça, sem ser atingido. E ele
quase não se desloca.
- **Partes de colisão** (o mesmo mecanismo das 7 partes da Moréia): núcleo, cabeça, manto e 8 braços x 3 partes.
  Cada parte segue a pose animada: o servidor calcula a posição das juntas com os mesmos clipes do jogo. A entidade
  principal deixa de ser clicável; quem acerta é a parte.
- **Dano por parte** (inicial): cabeça x1,25 (olhos), núcleo e manto x1, braços x0,75. Um braço que leva muito dano
  recua (animação de recuo). As regras do escudo não mudam.
- **Cabeça e manto sólidos:** o jogador pisa neles, mas não entra. Quem estiver dentro do volume é empurrado para fora.
- **Movimento constante:** o manto respira e os braços ondulam por baixo de qualquer ataque. A cabeça acompanha o
  alvo, e os olhos piscam.
- **Deslocamento:** órbita lenta subindo e descendo, giro suave para encarar o alvo, investidas curtas (cerca de 8
  blocos) entre ataques e mergulho para reaparecer em outro ponto da arena.
- Aceite (GameTest): flecha na cabeça causa mais dano que no braço; um jogador posto dentro da cabeça sai em até 1 s;
  o Kraken muda de posição pelo menos 3 vezes por minuto de luta. Nada disso pode quebrar a parte `multiplayer`.

## 4. Retexturas (4D, feito)
Pixel art do mod, legível em 16 x 16 e com silhueta clara. Quando a forma do bloco pede (uma bigorna não é um cubo),
o modelo também ganha forma.
- **Blocos de trabalho da Vila:** Bigorna de Maré (corpo de bigorna, ferro com cracas e corais), Mesa de Cartas
  Náuticas (tampo com carta, rosa dos ventos e compasso), Altar de Concha (base de pedra com concha aberta e pérola).
- **Pontas** (todas): Abissal, Veneno, Fogo, Wither e Choque. Mesma silhueta de ponta de arpão; cada efeito tem cor e
  detalhe próprios.
- **Itens de mergulho:** Bobina Voltaica, Regulador de Pressão, Aljava de Arpões e Propulsor de Maré.
- **Troféus:** Troféu do Kraken e Troféu Dourado do Abismo como estatuetas 3D num pedestal, no lugar do cubo texturizado.
- **Como foi feito:** `tools/gen_phase4_art.py` grava texturas, modelos (peças com giro livre, que o 26.3 aceita),
  estados e ícones; os blocos usam `FacingShapeBlock` (viram para quem coloca e colidem no formato do modelo). Os
  geradores antigos deixaram de escrever esses arquivos. Conferido na parte `blocks` do `runClientGameTest`.

## 5. Boia de Âncora: rework (4D, feito)
- Modelo 3D de boia: casco com faixas vermelhas e brancas, sino, corrente e lanterna no topo. A lanterna pisca e muda
  de cor conforme o tempo restante (verde, amarelo, vermelho).
- A plataforma 3 x 3 passa a ser de tábuas sobre flutuadores, com a água em volta (o bloco aceita água dentro).
  O meio tem a boia e colide também no mastro; o sino toca quando a lanterna muda de cor (som provisório do jogo).
- Quando o Kraken a destrói, há efeito visível (estilhaços e espirro d'água).
- Regras iguais às de hoje ([05-itens.md](05-itens.md)): 90 s, Pressão zerada, recarga de 30 s.

## 6. Blocos de construção aquáticos (4E, feito)
Cada família tem bloco, escada, laje e muro, além de polido, cinzelado ou pilar quando faz sentido. Saem de
materiais do oceano mais os do mod. Lista inicial:
- **Pedra de Maré:** pedra com veios verde-azulados, em versões bruta, polida, tijolos, tijolos rachados, cinzelada com
  ondas e pilar.
- **Coral petrificado:** tijolos e ladrilhos de coral morto compactado.
- **Telhas de concha** para telhados.
- **Ladrilho Abissal:** escuro com filetes de metal Abissal, a família de luxo.
- **Vidro do mar:** vidro fosco em cores de água.
- **Luzes:** Lanterna de Água-viva e Lampião de Pérola.
- **Grade naval:** treliça de ferro com cracas.
- **Como ficou:** 35 blocos.
  - Pedra de Maré: bruta, polida, tijolos, tijolos rachados, talhada com tridente e pilar.
  - Escadas, lajes e muros: da Pedra de Maré bruta, polida e em tijolos, dos tijolos de coral e dos Ladrilhos
    Abissais. Os ladrilhos de coral e as telhas têm só escadas e lajes.
  - Vidro do Mar com vidraça. A Lanterna de Água-viva tem luz 15 e pulsa. O Lampião de Pérola pendura no teto.
  - Receitas:
    - Pedra de Maré: 8 pedras + 1 fragmento de prismarinho; o resto da família sai pela serra.
    - Tijolos de Coral Petrificado: 4 blocos de coral morto.
    - Telhas de Concha: 8 tijolos + 1 concha de náutilo.
    - Ladrilhos Abissais: 8 ladrilhos de ardósia + 1 Lingote Abissal.
    - Vidro do Mar: 8 vidros + 1 cristal de prismarinho.
    - Lanterna de Água-viva: bolsas de tinta brilhante, cristais e Vidro do Mar.
    - Lampião de Pérola: lampião + Pérola Abissal, rende 2.
    - Grade Naval: 4 barras de ferro + 1 lingote de cobre.
  - Tudo é gerado por `tools/gen_phase4_building.py`, que também grava o `ModBuildingBlocks`.

## 7. Vidros quase quebrados (4E, feito)
- Uma variante rachada para **todos os vidros**: vidro, 16 tingidos e fumê (18 blocos), mais os painéis (17).
- Receita: vidro + pederneira (sem forma) rende 1 vidro rachado.
- Na luta, a onda de choque do Kraken racha os vidros da arena (complementa a destruição do ambiente).
- Desenho: textura própria de cada cor (moldura, brilho e as mesmas rachaduras finas saindo de um ponto de impacto,
  com um furinho no meio). Não há cópia das texturas do Minecraft.
- Na luta: `KrakenWorldEffects.crackGlass` racha os vidros do anel entre 14 e 22 blocos da onda de choque, mantendo
  as conexões das vidraças. Conferido pela parte `building` do `runClientGameTest`.

## 8. Neutrônio (4F, feito)
Material de fim de jogo, azul-ciano, acima da netherite. Fica **mais difícil** que ela e pede uma **série de etapas**.
Visual **estilo Avaritia**: um céu estrelado que corre dentro do metal, brilho próprio e partículas de estrela.
1. **Minério de Neutrônio:** só sob o leito dos oceanos profundos, na ardósia entre y −60 e −32. É muito raro e
   nunca fica exposto (blocos soltos como os detritos ancestrais, 1 tentativa por pedaço de mundo). Minerar pede
   picareta Abissal ou de netherite (o diamante não minera) e é lento (dureza 50, resiste a explosões). O minério tem luz fraca e cintila. Rende Neutrônio Bruto
   (sem Fortuna).
2. **Poeira de Neutrônio:** Forja Abissal, 1 Neutrônio Bruto + 1 Cristal de Maré, rende 2.
3. **Lingote de Neutrônio Instável:** Forja Abissal, 4 Poeiras + 4 Lingotes Abissais + 1 Escama Antiga.
4. **Têmpera:** o lingote instável, jogado na água com Pressão nível 3, se estabiliza em 20 s, com efeito visível.
   Sai o **Lingote de Neutrônio**. Fora d'água ele não estabiliza.
5. **Molde de Neutrônio** (modelo de ferraria): cai do Kraken, garantido na primeira vitória de cada jogador. Copiar
   pede 7 diamantes + 1 Poeira + o molde.
6. **Ferraria:** molde + peça Abissal + Lingote de Neutrônio dá a peça de Neutrônio (ferramentas e armadura).
- **Números iniciais** (netherite entre parênteses): armadura 4/9/7/4 (3/8/6/3), resistência 4 (3), repulsão 0,15
  (0,1), durabilidade 1,6 vez a da netherite. Espada 9 (8) e +3 de dano debaixo d'água. Escudo do Kraken a 80% (o
  Abissal está a 60%). Picareta com velocidade 10 (9) e nenhuma perda debaixo d'água. Não queima e não explode.
- **Bônus de cada peça na água:**
  - capacete: respiração aquática e visão noturna submersa;
  - peitoral: imune à Pressão da profundidade, e a do Kraken sobe 75% mais devagar (o conjunto Abissal dá 60%);
  - calças: Graça do Golfinho submerso;
  - botas: Passos Profundos III embutido.
- **Conjunto completo, Singularidade Abissal:** submerso, regeneração I e +20% de dano; imune à Nuvem de Tinta e ao
  Redemoinho do Kraken (o afogamento já some com o capacete), com aura de faíscas e brilho.
- Todo item da família afunda na água (matéria densa) e não queima nem explode.
- **Visual:** texturas animadas (o céu estrelado corre), partes com luz própria e, na armadura, uma segunda camada de
  energia ciano que escorre. A família inteira segue o mesmo visual: minério, bruto, poeira, lingotes, bloco,
  ferramentas, armadura e o Cavitador.
- **Como foi feito:**
  - `tools/gen_phase4_neutronium.py` gera as texturas animadas (8 quadros; os itens usam a silhueta das peças
    Abissais), a camada da armadura, os dados, as receitas, as tags, a geração no mundo e os nomes.
  - O `NeutroniumArmorRenderer` desenha a armadura com a camada estrelada e o céu que escorre por cima, com luz
    própria. `NeutroniumEffects` cuida dos bônus e `NeutroniumTempering` da têmpera e do afundar.
  - Conferido pela parte `neutronium` do `runClientGameTest`, com receitas, ferraria, Molde, minério, afundar,
    têmpera, bônus e Pressão.

## 9. Cavitador de Neutrônio (4G)
- **Debaixo d'água:** segurar carrega (0,6 s) e soltar dispara um **pulso de cavitação** (a bolha que implode, como a
  do camarão-pistola). O pulso corre rápido pela água com rastro de bolhas, atravessa até 3 criaturas e implode no
  impacto com um anel de choque de 2 blocos. Dano inicial: 10 carregado, 5 rápido. Alcance de 32 blocos. Fora d'água,
  o pulso se desfaz.
- **Fora d'água:** **lançador de arpões**, um arpão pesado com o efeito da ponta escolhida na Aljava. Gasta
  durabilidade, sem munição, com recarga de 1 s.
- **Modelo 3D:** cano com bobinas de Neutrônio, câmara central com o núcleo aceso e anéis que giram mais rápido
  conforme a carga. Poses próprias para primeira e terceira pessoa, inventário, chão e moldura.
- **Partículas:** bolhas de cavitação, centelhas de estrela, um anel de choque que se expande e pontos de luz que
  convergem para a boca do cano enquanto carrega.
- **Receita** (Forja Abissal): 3 Lingotes de Neutrônio + 1 Arpão Abissal + 1 Coração do Kraken + 2 Bobinas Voltaicas
  + 1 Regulador de Pressão.

## 10. Sons (4S, depois)
Por pedido do Adriano, só depois do resto e com a mesma régua de [09-audio.md](09-audio.md) (tudo original).
Lista a fazer:
- passos e impactos nas partes do Kraken;
- Boia: sino e lanterna;
- blocos aquáticos e vidro rachando;
- Neutrônio: minério cintilando, têmpera, armadura e bônus;
- Cavitador: carga, pulso, implosão e arpão.
