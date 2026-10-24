# O Kraken

Base do plano: 1.500 de vida, 3 fases (Tormenta, Fúria, Abismo), barra de
escudo "Carapaça Abissal" (400 / 600 / 800), 8 tentáculos de 60 de vida.

## Ideias para o escudo
- **Visual:** a carapaça é uma camada de cristal azul sobre o corpo. Rachaduras
  aparecem em 75%, 50% e 25% do escudo, para o jogador sentir progresso mesmo
  sem olhar a barra.
- **Quebra:** quando chega a zero, a carapaça explode em estilhaços (partículas
  + som de vidro grave), o Kraken grita e fica atordoado 8 s. Esse momento tem
  que ser *muito* satisfatório, é o "pico" da luta.
- **Pontos fracos no escudo:** em vez de dano em qualquer lugar, talvez 3 a 4
  "nós" brilhantes no escudo que levam dano extra. Dá alvo ao jogador e
  recompensa mira. (Pergunta aberta: vale a complexidade?)
- **Regeneração:** 20 s sem dano → regenera. Na fase 3, regenera sozinho a
  cada 45 s. Mostrar a regeneração com brilho subindo na barra.

## Ideias para a IA
- Ciclo de ataques com peso aleatório, mas sem repetir o mesmo ataque 2 vezes
  seguidas.
- O Kraken escolhe alvo: quem deu mais dano no escudo recentemente (aggro).
  Em multiplayer isso cria papéis: um "tanque" chama atenção, outro quebra
  escudo.
- Tentáculos cortados param aquele ataque por 30 s. Ideia: se o jogador
  cortar os 8, o Kraken fica exposto por um tempo extra (recompensa por jogar
  agressivo).

## Ataques (além dos do plano)
- **Maré Negra:** a água ao redor fica escura (tinta) e só a armadura de
  Oricálcio completa enxerga.
- **Rugido Abissal:** onda de choque que tira o jogador de barcos e quebra
  blocos de 1 camada.

## Balanceamento, primeiros números para testar
- Alvo de tempo de luta: 6 a 10 minutos para um jogador bem preparado.
- Dano de tentáculo: ~12 (6 corações) com netherite = o jogador aguenta uns
  4 a 5 golpes sem curar.
- Ajustar depois de jogar; esses números são chute.

## Dificuldade configurável
- `normal`: um pouco acima do Warden (decisão do Adriano).
- `abissal` (modo mestre): mais vida, escudo regenera mais rápido, drop
  exclusivo (ex.: troféu dourado ou cosmético). *Engolir foi cortado.*

## Peso do abismo (decisão: superfície + fundo)
- Fase 1 na superfície; fases 2-3 puxam a luta para o fundo.
- Efeito **Pressão**: nível que sobe com o tempo perto do Kraken. Cada nível
  aumenta a gravidade na água e afunda o jogador (velocidade vertical para
  baixo), embaça a tela e abafa o som. Chega ao fundo = arena da Cratera.
- Contramedidas: Tônico de Pressão, armadura de Oricálcio, Amuleto da
  Corrente, Boia de Âncora (reduzem ou resetam o nível).
- Multiplayer: Pressão por jogador; o aggro continua escolhendo o alvo.

## Barra de vida customizada
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
