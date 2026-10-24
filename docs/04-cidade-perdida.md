# Cidade Perdida de Nereia

> **Status:** "Partes" = plano oficial (decidido). "Ideias extras", bioma e criaturas novas = **ideias** ainda não decididas.

Estrutura grande e rara no oceano profundo, dentro do bioma Fossa de Nereia.
Único lugar com os materiais que enfraquecem o Kraken.

## Partes (do plano)
Praça Central (Altar do Abismo), Templo Afundado (Chave de Nereia), Forja
Abissal, Biblioteca em ruínas, Minas de Oricálcio.

## Ideias extras
- **Como achar a cidade:** um "Mapa de Nereia" vendido pelo Cartógrafo ou
  achado em naufrágios, igual ao mapa do tesouro. Sem isso a cidade é quase
  impossível de achar.
- **Respirar lá embaixo:** bolsões de ar dentro de alguns prédios (salas
  seladas com ar), para dar descanso no meio da exploração.
- **Domo quebrado:** a cidade fica dentro de uma cúpula de vidro rachada.
  Visual muito forte de longe e explica por que tem ar em algumas partes.
- **Progressão dentro da cidade:** a borda é fácil (baús, Escamas Antigas);
  o centro é perigoso (Sentinelas). O jogador vai entrando aos poucos.
- **Livros de história:** contam que Nereia selou o Kraken com Cristais de
  Maré, e por isso o cristal enfraquece o escudo. A história explica a
  mecânica, que é o ideal.
- **Arena perto da cidade:** a Fossa pode ter uma área aberta grande, a
  "Cratera", que é o lugar natural para a luta.

## Bioma Fossa de Nereia
- Água mais escura (cor de água e névoa próprias do bioma).
- Coral brilhante novo (emite luz) e areia azulada.
- Cristais de Maré crescendo no chão, como ametista.
- Partículas de bolhas subindo.

## Criaturas
- **Sentinela Afundada:** golem de prismarinho, lento, bate forte, dropa
  Pérola Abissal. Fica parado até o jogador chegar perto (parece estátua).
- **Sereia Corrompida:** ataque à distância, canto que puxa o jogador.
- **Enguia Elétrica:** rápida, causa lentidão; ideia: dano maior se o
  jogador estiver de armadura de metal.
- Ideia nova: **Caranguejo Ermitão gigante** que usa uma concha de prismarinho;
  passivo, mas defende o território. Pode dropar material para o Amuleto.

## Técnico
- Estrutura jigsaw (template pools + NBT de estruturas) primeiro, sem bioma.
- Bioma depois com Biolith ou TerraBlender quando tiver versão para 26.3.

## Walker — Decidido (números e trocas iniciais, a testar)
O **Walker** é o único habitante pacífico das profundezas: um morto-vivo
afogado que caminha devagar pelo fundo carregando uma Lanterna de Coral. Ele
dá ao jogador um motivo para ir ao fundo à noite e uma segunda fonte de
suprimentos, sem substituir a cidade.

**Aparição**
- Só **à noite**, em oceano profundo (coluna d'água de 25 blocos ou mais) e
  com mais frequência na Fossa de Nereia e ao redor da Cidade Perdida.
- No máximo 1 por vez a cada 64 blocos; some ao amanhecer: afunda em uma nuvem
  de bolhas e desaparece (não deixa drops).
- Luz própria (a lanterna) e som grave e lento, para ser achado de longe.

**Comportamento**
- Pacífico: nunca ataca. Se levar dano, solta a lanterna, foge nadando para o
  fundo e não negocia mais naquela noite. Matá-lo não dá nada e toca um som de
  lamento para todos por perto.
- Vida 20 (10 corações). Não é atingido por Pressão e não se afoga.

**Troca** (clique direito, como um aldeão; novo estoque a cada noite, cada
troca com 1 a 3 usos, estoque independente por jogador no multiplayer)
- **Aceita como pagamento:** Pérola Abissal (principal), Escama Antiga e
  fragmentos de prismarinho.
- **Vende:** Tônico de Pressão, Boia de Âncora, Bússola de Maré, Lanterna de
  Coral, Mapa de Nereia e, raramente, livros de história da cidade.
- **Nunca vende:** Oricálcio, armaduras, Arpão, Carga de Pulso nem Pilar
  Ressonante. Esses vêm só da cidade, para preservar o motivo de explorá-la.
- Cada noite oferece 3 a 5 trocas sorteadas; a Bússola de Maré e o Mapa de
  Nereia são raros (no máximo 1 deles por noite).

**Arte e modelo:** silhueta de zumbi afogado, pele azul-esverdeada, algas e
conchas nos ombros, lanterna de coral na mão; modelo detalhado, com animação
de caminhada lenta e balanço da lanterna.
