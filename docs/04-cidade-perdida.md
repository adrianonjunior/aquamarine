# Cidade Perdida de Nereia, bioma e criaturas

> **Dono de:** a estrutura, o loot, o bioma Fossa de Nereia, as criaturas
> (incluindo o Walker) e o texto dos livros de história. Tudo **decidido**;
> números são valores iniciais a testar. Decisões gerais em
> [00-decisoes.md](00-decisoes.md). Itens e receitas em [05-itens.md](05-itens.md).

## Estrutura
- Grande e rara, no oceano profundo, dentro do bioma Fossa de Nereia. Espaçamento
  entre cidades: cerca de 1.300 blocos (média), tamanho de uns 160x160 blocos.
- Fica sob uma **cúpula de vidro rachada**: visual forte de longe e explica os
  **bolsões de ar** em alguns prédios (salas seladas com ar para descanso).
- **Progressão:** a borda é fácil (baús comuns, poucas Escamas); o centro é
  perigoso (Sentinelas e a Sereia Corrompida). O jogador entra aos poucos.
- Como achar: **Mapa de Nereia** (Cartógrafo, naufrágios, Walker) ou **Bússola
  de Maré**. Sem eles a cidade é muito difícil de achar.
- Estrutura jigsaw (template pools e NBT) primeiro; bioma depois (ver
  [06-tecnico.md](06-tecnico.md)).

## Partes da cidade
| Parte | O que tem |
|---|---|
| Praça Central | Altar do Abismo, onde o Chamariz Abissal é montado |
| Templo Afundado | Cofre com a Chave de Nereia e o Projeto do Chamariz; guardado por Sentinelas |
| Forja Abissal | Bancada para itens de Oricálcio e da luta, mais baús de ferramentas |
| Biblioteca em ruínas | Livros de história com dicas do Kraken; livros do Quebra-Carapaça |
| Minas de Oricálcio | Único lugar com o minério |
| Cratera | Bacia aberta ao lado da cidade: a arena do Kraken (ver [02-kraken.md](02-kraken.md)) |

## Loot
| Local | Conteúdo |
|---|---|
| Baús da borda | Escama Antiga (1 a 2), cobre, ferro, fragmentos de prismarinho, livros comuns |
| Baús do meio | Escama Antiga (2 a 4), Cristal de Maré (1 a 3), Lanterna de Coral, 10% de chance de 1 Lingote de Oricálcio |
| Cofre do Templo | Chave de Nereia (1 por jogador e mundo) e Projeto do Chamariz |
| Biblioteca | 1 a 2 livros de história; 15% de chance de livro Quebra-Carapaça I ou II |
| Templo (baú raro) | 5% de chance de livro Quebra-Carapaça III |
| Forja | 1 Forja Abissal pronta e Lingotes de Oricálcio (1 a 3) |
| Minas | Veias de Oricálcio por todas as galerias |

## Bioma Fossa de Nereia
- Água mais escura (cor da água e da névoa próprias).
- **Coral Luminoso:** novo coral que emite luz (nível 10).
- **Areia azulada** e **Cristais de Maré** crescendo no chão (como ametista,
  luz 5, dropam Cristal de Maré).
- Partículas de bolhas subindo e som ambiente grave.

## Criaturas
| Criatura | Vida | Comportamento | Drop |
|---|---|---|---|
| Sentinela Afundada | 80 | Golem de prismarinho, lento, 14 de dano; fica parado como estátua até alguém chegar a 8 blocos | Pérola Abissal (1 a 2) |
| Sereia Corrompida | 30 | Ataque à distância (6 de dano) e canto que puxa o jogador por 8 blocos (recarga de 10 s) | Fragmentos de prismarinho, Escama (rara) |
| Enguia Elétrica | 24 | Rápida, 5 de dano e Lentidão II por 4 s; dano 1,5x contra quem usa armadura de metal | Nada de especial |
| Caranguejo Ermitão | 40 | Passivo, defende o território (6 de dano) com concha de prismarinho | Casco de Prismarinho |
| Cria de Kraken | 8 | Invocada na luta (4 de dano); não aparece na natureza | Tentáculo de Cria |

Modelos detalhados e de alta qualidade, com animações próprias, desde a primeira
versão.

## Livros de história (5 títulos)
1. **"A Fundação de Nereia":** a cidade foi criada pelas Nereidas para estudar o mar.
2. **"O Despertar"**: o Kraken surgiu e ameaçou a cúpula.
3. **"Cristais de Maré":** Nereia selou o Kraken com cristais; por isso o cristal enfraquece a carapaça.
4. **"Diário do Ferreiro":** como forjar Oricálcio e o Arpão, e que a água escura pesa no corpo (Pressão).
5. **"O Último Aviso":** o Kraken tem pontos fracos nos nós de cristal da carapaça e odeia a luz do coral.

## Walker
O **Walker** é o único habitante pacífico das profundezas: um morto-vivo
afogado que caminha devagar pelo fundo carregando uma Lanterna de Coral. Dá um
motivo para ir ao fundo à noite e uma segunda fonte de suprimentos, sem
substituir a cidade.

**Aparição**
- Só **à noite**, em oceano profundo (coluna d'água de 25 blocos ou mais), com
  mais frequência na Fossa de Nereia e ao redor da cidade.
- No máximo 1 a cada 64 blocos; ao amanhecer afunda numa nuvem de bolhas e some
  (sem drops).
- Luz própria (a lanterna) e som grave e lento, para ser achado de longe.

**Comportamento**
- Pacífico: nunca ataca. Se levar dano, solta a lanterna, foge nadando para o
  fundo e não negocia mais naquela noite. Matá-lo não dá nada e toca um som de
  lamento para todos por perto.
- Vida 20. Não sofre Pressão e não se afoga.

**Troca** (clique direito, como aldeão; novo estoque a cada noite; 1 a 3 usos
por troca; estoque independente por jogador)
- **Aceita:** Pérola Abissal (principal), Escama Antiga e fragmentos de
  prismarinho.
- **Vende:** Tônico de Pressão, Boia de Âncora, Bússola de Maré, Lanterna de
  Coral, Mapa de Nereia e, raramente, livros de história.
- **Nunca vende:** Oricálcio, armaduras, Arpão, Carga de Pulso nem Pilar
  Ressonante: esses vêm só da cidade.
- 3 a 5 trocas por noite; a Bússola e o Mapa são raros (no máximo 1 por noite).

**Visual:** silhueta de zumbi afogado, pele azul-esverdeada, algas e conchas nos
ombros, lanterna de coral na mão; caminhada lenta e balanço da lanterna.

## Descartado
- Cidade sem cúpula (a cúpula é decisão visual e de jogabilidade).
- Vender itens de luta no Walker.
