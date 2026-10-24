# O Kraken

> **Status:** seções marcadas "Decidido" valem; o resto é **ideia** (não decidida). Números de balanceamento são chutes a testar. Decisões em [00-decisoes.md](00-decisoes.md).

Base do plano: 1.500 de vida, 3 fases (Tormenta, Fúria, Abismo), barra de
escudo "Carapaça Abissal" (400 / 600 / 800), 8 tentáculos de 60 de vida.

## Ideias para o escudo (ideias)
- **Visual:** a carapaça é uma camada de cristal azul sobre o corpo. Rachaduras
  aparecem em 75%, 50% e 25% do escudo, para o jogador sentir progresso mesmo
  sem olhar a barra.
- **Quebra:** quando chega a zero, a carapaça explode em estilhaços (partículas
  + som de vidro grave), o Kraken grita e fica atordoado 8 s. Esse momento tem
  que ser *muito* satisfatório, é o "pico" da luta.
- **Pontos fracos no escudo:** decididos, ver "Nós de Maré" abaixo.
- **Regeneração:** 20 s sem dano → regenera. Na fase 3, regenera sozinho a
  cada 45 s. Mostrar a regeneração com brilho subindo na barra.

## Ideias para a IA (ideias)
- Ciclo de ataques com peso aleatório, mas sem repetir o mesmo ataque 2 vezes
  seguidas.
- O Kraken escolhe alvo: quem deu mais dano no escudo recentemente (aggro).
  Em multiplayer isso cria papéis: um "tanque" chama atenção, outro quebra
  escudo.
- Tentáculos cortados param aquele ataque por 30 s. Ideia: se o jogador
  cortar os 8, o Kraken fica exposto por um tempo extra (recompensa por jogar
  agressivo).

## Ataques extras (ideias)
- **Maré Negra:** a água ao redor fica escura (tinta) e só a armadura de
  Oricálcio completa enxerga.
- **Rugido Abissal:** onda de choque que tira o jogador de barcos e quebra
  blocos de 1 camada.

## Balanceamento: números iniciais (chutes)
- Alvo de tempo de luta: 6 a 10 minutos para um jogador bem preparado.
- Dano de tentáculo: ~12 (6 corações) com netherite = o jogador aguenta uns
  4 a 5 golpes sem curar.
- Ajustar depois de jogar; esses números são chute.

## Dificuldade — Decidido: um pouco acima do Warden
- `normal`: um pouco acima do Warden (decisão do Adriano).
- `abissal` (modo mestre): mais vida, escudo regenera mais rápido, drop
  exclusivo (ex.: troféu dourado ou cosmético).
## Peso do abismo — Decidido (superfície + fundo)
- Fase 1 na superfície; fases 2-3 puxam a luta para o fundo.
- Efeito **Pressão**: nível que sobe com o tempo perto do Kraken. Cada nível
  aumenta a gravidade na água e afunda o jogador (velocidade vertical para
  baixo), embaça a tela e abafa o som. Chega ao fundo = arena da Cratera.
- Contramedidas: Tônico de Pressão, armadura de Oricálcio, Amuleto da
  Corrente, Boia de Âncora (reduzem ou resetam o nível).
- Multiplayer: Pressão por jogador; o aggro continua escolhendo o alvo.

## Barra de vida customizada — Decidido
- Barra própria desenhada no HUD (cliente), no lugar da barra de boss vanilla:
  moldura de coral/oricálcio dourado, preenchimento azul-profundo com brilho
  verde-água.
- Escudo "Carapaça Abissal" como segunda faixa de cristal azul sobre a vida,
  com rachaduras em 75/50/25% e explosão de estilhaços ao quebrar.
- Marcadores de fase (3 divisões) e nome "KRAKEN" com a fase atual.
- Dano recente aparece como faixa clara que escoa (efeito "barra fantasma").
- Pulso roxo bioluminescente na fase 3 e na regeneração do escudo.
- Texturas em `assets/aquamarine/textures/gui/`; vida e escudo sincronizados
  por packet próprio ou `SynchedEntityData`.

## Nós de Maré (pontos fracos do escudo) — Decidido
- 4 "nós" de cristal brilhante na carapaça, cada um uma hitbox pequena
  (mesma técnica das partes do Ender Dragon). Dano no nó vale **2x** no escudo.
- A cada fase os nós mudam de posição (fase 1 nas costas, fase 2 nos flancos,
  fase 3 sob o corpo, exigindo mergulhar). Piscam antes de trocar.
- Por jogador acima de 4, entra 1 nó extra a cada 2 jogadores (máximo 8), para
  grupos grandes terem alvos para todos.
- Opção na configuração para desligar os nós (dano uniforme no escudo).
- Projéteis comuns contam, mas com o mesmo multiplicador de arma; o Arpão
  recebe bônus adicional ao acertar um nó.

## Dano no escudo por item — Decidido (números iniciais, a testar)
Sem item especial, o escudo recebe **25%** do dano: dá para vencer sem os itens
da cidade, mas é bem mais demorado e a regeneração pune quem para de bater.

| Fonte de dano | Dano no escudo |
|---|---|
| Armas comuns (espada, machado, arco, besta, tridente) | 25% |
| Ferramentas e armas de Oricálcio (tag `aquamarine:shield_breakers`) | 60% |
| Arpão de Oricálcio | 100% (150% em um nó) |
| Carga de Pulso | 60 de dano fixo no escudo, em área |
| Pilar Ressonante | 15 por segundo no escudo, alcance de 12 blocos; até 3 ativos |
| Encantamento Quebra-Carapaça | +10% por nível, até o nível 3, sobre qualquer item |

O dano na vida do Kraken só acontece com o escudo em zero (como no plano).

## Escala por número de jogadores — Decidido (servidor com 5 ou mais)
Fator `f = 1 + 0,3 × (n − 1)`, com `n` jogadores num raio de 96 blocos e máximo
em `n = 12`.
- Vida e escudo máximos (por fase) multiplicados por `f`.
- Ataques simultâneos de tentáculo: `2 + n ÷ 3` (arredondado para baixo), no
  máximo 8.
- Dano que o Kraken causa não escala, para a luta não virar punição só pelo
  tamanho do grupo.
- Se jogadores saem do raio no meio da luta, o fator só diminui entre fases,
  nunca no meio de uma.
- Dificuldade `abissal` aplica um multiplicador próprio sobre `f`.
