# Itens

> **Dono de:** lista final de materiais, itens, receitas e estatísticas. Tudo
> **decidido**; números são valores iniciais a testar (ajustar aqui).
> **Implementados (Entrega 1):** Oricálcio (minério, lingote, bloco, ferramentas,
> armadura), Pérola Abissal, Cristal de Maré e Escama Antiga como itens, Forja
> Abissal, Óculos de Mergulho, Chamariz Abissal (e Projeto) e Altar do Abismo.
> O resto ainda é planejamento. Regra: todo item
> responde "o que faz na luta ou na exploração?". Decisões gerais em
> [00-decisoes.md](00-decisoes.md).

## Materiais
| Material | Origem | Usado em |
|---|---|---|
| Minério de Oricálcio | Minas de Oricálcio (só na cidade) | Lingote de Oricálcio |
| Lingote de Oricálcio | Fundir o minério | Ferramentas, armadura, Arpão, Pilar, Forja |
| Pérola Abissal | Sentinelas Afundadas (1 a 2, afetada por Saque); também Walker | Chamariz, Amuleto, Bússola |
| Cristal de Maré | Cristais na Fossa de Nereia (minerar com picareta) | Cargas de Pulso, Pilar, Tônico, Arpão |
| Escama Antiga | Baús da cidade | Armadura, Amuleto da Corrente |
| Casco de Prismarinho | Caranguejo Ermitão | Amuleto da Corrente |
| Chave de Nereia | Cofre do Templo (uma por jogador e mundo) | Abre o cofre do Projeto do Chamariz |
| Projeto do Chamariz | Cofre do Templo | Montar o Chamariz Abissal |
| Tentáculo de Cria | Crias de Kraken | Sopa de Tentáculo |
| Olho do Kraken, Tinta Abissal | Recompensa do Kraken | Itens pós-boss |
| Coração do Kraken | Recompensa do Kraken (primeira vitória de cada jogador) | Itens pós-boss |
| Escama Elétrica | Moréia Tempestuosa (2 + jogadores ÷ 2, máximo 6, afetada por Saque) | Bobina Voltaica, Chicote de Moréia (Fase 3B) |

## Bancada: Forja Abissal
Receitas de Oricálcio e de itens da luta usam a **Forja Abissal**. Existem
algumas prontas na cidade e ela também pode ser feita: 2 Blocos de Oricálcio +
2 Escamas Antigas + 1 fornalha + 1 bloco de obsidiana (bancada comum).
A Forja tem grade 3x3 e também aceita as receitas da bancada comum; as receitas
dela usam o tipo `aquamarine:abyssal_forging` (JSON igual ao `crafting_shaped`).

## Itens da luta e da exploração
Todas as receitas abaixo são na Forja Abissal, exceto quando indicado.

| Item | Receita | Efeito |
|---|---|---|
| Chamariz Abissal | Projeto do Chamariz + 4 Pérolas + 2 Cristais + 1 Lingote; usado no Altar do Abismo | Invoca o Kraken; consumido ao invocar (implementado; o Projeto por enquanto só no criativo) |
| Arpão de Oricálcio | 3 Lingotes + 1 Cristal + 1 vareta (implementado na 3C, arremesso do tridente) | Arremessável; dano triplo no escudo; prende o tentáculo por 3 s; volta com Lealdade ou é recolhido |
| Carga de Pulso | 1 Cristal + 1 pólvora + 1 Pérola (rende 2) | Granada submarina; 100 de dano fixo no escudo em área de 4 blocos |
| Pilar Ressonante | 4 Cristais + 2 Lingotes + 1 bloco de prismarinho | Bloco; perto da arena, a cada 15 s tira 5% do escudo; até 3 ativos |
| Amuleto da Corrente | 3 Escamas + 2 Cascos + 1 Pérola | Na mão secundária: imunidade ao puxão do Redemoinho |
| Tônico de Pressão | 1 garrafa d'água + 1 Cristal + 2 algas (bancada comum) | 5 min: máximo da Pressão −2 e solta de agarrões 50% mais rápido |
| Boia de Âncora | 1 barril + 2 lingotes de cobre + 1 cristal de prismarinho (bancada comum) | Bloco que forma uma plataforma 3x3 por 90 s na superfície; zera a Pressão de quem pisa; o Kraken pode destruí-la; recarga de 30 s por jogador |
| Medidor de Profundidade | bússola + painel de vidro + lingote de cobre + 1 Cristal (sem forma, bancada comum) | Na mão (qualquer uma), o HUD mostra a profundidade e o nível de Pressão (Fase 3A) |
| Regulador de Pressão | 2 lingotes de cobre + 1 Pérola + 1 Cristal + 1 Escama Antiga (sem forma) | Mão secundária: Pressão da profundidade −2 níveis; 1 de durabilidade (600) a cada 10 s ativo; não afeta a Pressão do Kraken (3A) |
| Traje de Mergulho Profundo (4 peças) | Escama Antiga + Pérola Abissal + estilhaço de prismarinho (bancada comum; o Casco de Prismarinho ainda não existe) | Defesa 2/6/5/2. +45 s de Reserva de Ar por peça (180 s com o conjunto), gasta depois do fôlego; Pressão da profundidade −1 nível com 2 peças e −2 com 4. Isolamento: −40% de dano elétrico por peça (imune com o conjunto) |
| Bobina Voltaica | 1 Escama Elétrica + 1 lingote de cobre + 1 Cristal (sem forma, bancada comum) | Bateria: 6 cargas do Propulsor; vira ponta de Choque do Arpão (3B) |
| Chicote de Moréia | 3 Bobinas + 2 Lingotes de Oricálcio + 1 vareta | 7 de dano, ataque rápido (2/s), alcance 3,5; corrente elétrica nos alvos molhados a 4 blocos (50% do dano); durabilidade 450 (3B) |
| Sela de Baleia | 4 couro + 2 Escamas Antigas + 1 Pérola (bancada comum) | Numa Baleia-de-Nereia domada: montaria de 3 jogadores; a baleia deixa a sela ao morrer (3B) |
| Aljava de Arpões | 3 couro + 2 Escamas Antigas + 1 Pérola (bancada comum) | Guarda até 64 pontas de cada tipo; usar recolhe as pontas do inventário, agachado devolve; R troca a ponta; o Arpão gasta 1 por arremesso (3C) |
| Ponta de Oricálcio | 1 Lingote de Oricálcio + 1 pederneira, rende 4 (bancada comum) | Base das pontas especiais (3C) |
| Pontas de Veneno, Fogo, Wither e Choque | Ponta + olho de aranha / pó de blaze (1 cada); 8 pontas + 1 caveira de wither esqueleto (rende 8); 4 pontas + 1 Bobina (rende 4) | Veneno I 10 s; Chama Abissal 5 s (2/s, até debaixo d'água); Wither I 8 s; descarga nos alvos molhados a 6 blocos (50%). No Kraken: tabela de [02-kraken.md](02-kraken.md) (3C) |
| Propulsor de Maré | 1 concha de náutilo + 2 Lingotes + 1 Cristal + 1 Bobina | Mão secundária, tecla V (segura e solta): impulso de ~16 blocos em 1,2 s, recarga 3 s, só na água; 1 Bobina = 6 cargas (+2 por nível de Sobrecarga), gasta do inventário quando acabam (3D) |
| Bússola de Maré | 1 bússola + 1 Pérola + 1 Cristal (bancada comum) | Aponta para a Cidade Perdida mais próxima |
| Mapa de Nereia | Cartógrafo (nível 3, por esmeraldas e uma bússola), naufrágios ou Walker | Marca a Cidade Perdida mais próxima |
| Lanterna de Coral | 1 lanterna + 2 corais + 1 Cristal (bancada comum) | Luz 15 debaixo d'água; afasta a Nuvem de Tinta num raio de 4 blocos |
| Arco de Coral | 3 corais + 3 linhas + 1 Pérola (bancada comum) | Atira normalmente debaixo d'água, sem perda de velocidade |
| Sopa de Tentáculo | 2 Tentáculos + 1 tigela + 1 alga (bancada comum) | Respiração Aquática por 60 s |
| Óculos de Mergulho | Couro + painel de vidro + cobre (bancada comum), ver abaixo | Menos névoa debaixo d'água |
| Encantamento Quebra-Carapaça | Só em livros de baús da Biblioteca e do Templo (níveis 1 a 3) | +10% de dano em escudos de boss por nível |

## Armadura e ferramentas de Oricálcio
- **Armadura** (levemente abaixo da netherite): capacete 5 Lingotes + 1 Escama;
  peitoral 7 + 2 (8 + 2 não cabe na grade 3x3); calças 7 + 2; botas 4 + 1.
  Defesa 3/7/5/3, resistência 2, durabilidade 35 (multiplicador), resistência a
  repulsão 0,05.
- **Bônus do conjunto completo:** nado 25% mais rápido (atributo
  `water_movement_efficiency` +0,33, a calibrar no jogo), enxerga através da
  Nuvem de Tinta e a Pressão sobe 60% mais devagar (15% por peça; a Nuvem e a
  Pressão leem `OrichalcumEffects` quando existirem).
- **Ferramentas e arma** (picareta, machado, pá, enxada, espada): nível um pouco
  abaixo da netherite (durabilidade 1800, velocidade 8,5, minera o mesmo que
  diamante), espada com 7 de dano; minerar debaixo d'água sem a
  penalidade da água ("Maré"). Todas na tag `aquamarine:shield_breakers`
  (60% de dano no escudo).
- Capacete de Oricálcio e Óculos de Mergulho disputam o slot da cabeça.

## Óculos de Mergulho
- Efeito só **visual e de cliente**: aumenta bastante o alcance de visão debaixo
  d'água e deixa a água mais clara enquanto equipados. Sem visão noturna.
- Não enxergam através da **Nuvem de Tinta**: só o conjunto de Oricálcio.
- Visual: armação de latão envelhecido, lentes verde-água, tira azul de couro.

## Itens pós-boss (feitos com o Coração do Kraken)
| Item | Receita | Efeito |
|---|---|---|
| Tridente do Kraken | Tridente + Coração + 2 Olhos + 4 Lingotes | Arremessado, lança tentáculos que puxam inimigos; 11 de dano corpo a corpo |
| Manto de Tinta | Coração + 6 Tintas + 2 Escamas + 1 couro | No peito: nadar como elytra debaixo d'água, com impulso de tinta (recarga de 4 s) |
| Farol Abissal | Coração + 3 Pérolas + 4 cristais de prismarinho + 1 Olho | Bloco tipo conduto: alcance de 96 blocos, dá Respiração Aquática, visão clara, nado rápido e imunidade à Pressão |
| Troféu do Kraken | Recompensa | Bloco decorativo, cabeça do Kraken na parede |
| Troféu Dourado do Abismo | Recompensa do modo `abissal` | Bloco decorativo exclusivo |

## Nomes e texto
Todos os itens em português no jogo, com tradução em inglês; nada de texto fixo
no código.

## Descartado
- Ferramentas de Oricálcio como "só efeito Maré sem dano de escudo" (agora têm 60%).
- Itens puramente decorativos além dos troféus (ficam para depois do lançamento).
