# O Kraken

> **Dono de:** tudo sobre a luta: fases, escudo, ataques, IA, Pressão, barra de
> vida, escala por jogadores e recompensas. Tudo aqui está **decidido**; números
> são valores iniciais a testar (ajustar aqui, sem nova decisão). Ideias
> descartadas ficam em "Descartado" no fim. Decisões gerais em [00-decisoes.md](00-decisoes.md).

## Arena
- A luta acontece na **Cratera**, uma bacia aberta da Fossa de Nereia ao lado da
  Cidade Perdida: 120 blocos de largura e pelo menos 40 de profundidade, com
  plataformas de ruína no fundo.
- O Chamariz Abissal, montado no Altar da Praça Central, só funciona se houver
  água aberta com 30 blocos de profundidade num raio de 48 blocos; senão a
  mensagem "As águas são rasas demais..." aparece e o Chamariz não é gasto.
- O Kraken surge dentro dessa água e a arena é o raio de 32 blocos ao redor
  dele (a mesma regra de "sair da arena" do plano).

## Fases
| Fase | Vida | Escudo | Onde se luta | Pressão máxima |
|---|---|---|---|---|
| 1. Tormenta | 100% a 66% | 400 | Superfície: ele fica submerso, só os tentáculos saem; jogadores em barcos, plataformas e Boias de Âncora | 1 |
| 2. Fúria | 66% a 33% | 600 | Mistura: ele emerge, a cabeça vira o ponto fraco; combate na superfície e em mergulhos curtos | 2 |
| 3. Abismo | 33% a 0% | 800, regenera a cada 45 s | Fundo da Cratera: tempestade permanente, ataques 30% mais rápidos | 4 |

Vida total base: 1.500 (500 por fase). Escudo base por fase: a coluna acima.
O escudo se refaz no início de cada fase. A transição de fase dura 3 s de
invulnerabilidade com mensagem no chat e som de rugido.

## Pressão (o peso do abismo)
Efeito próprio, **por jogador**, nível de 0 a 4.
- **Sobe** 1 nível a cada 10 s submerso a menos de 24 blocos do Kraken (a cada
  6 s na fase 3). **Desce** 1 nível a cada 5 s fora dessa condição ou em
  superfície.
- **Efeitos:** nível 1: afunda 20% mais rápido; nível 2: nado lento e vinheta
  azul; nível 3: afunda com força e perde velocidade de ataque; nível 4: é
  puxado até o fundo, tela escura nas bordas e 1 coração de dano a cada 2 s.
- **Contramedidas:** cada peça de armadura de Oricálcio reduz o ganho em 15%
  (conjunto completo: 60% mais lento); Tônico de Pressão reduz o máximo em 2
  níveis por 5 minutos; pisar numa Boia de Âncora zera o nível; o Amuleto da
  Corrente não afeta a Pressão, só o puxão do redemoinho.
- O efeito é visível para o próprio jogador (vinheta, bolhas, som abafado).

## Escudo "Carapaça Abissal"
- **Visual:** camada de cristal azul sobre o corpo; rachaduras em 75%, 50% e 25%.
- **Quebra:** a carapaça explode em estilhaços (partículas e som de vidro
  grave), o Kraken grita e fica atordoado 8 s: é o pico da luta, e só então a
  vida leva dano.
- **Regeneração:** 20 s sem dano ao escudo regenera 10% por segundo (cancelado por
  qualquer dano); na fase 3 também regenera sozinho a cada 45 s, com brilho
  subindo na barra.

### Nós de Maré (pontos fracos do escudo)
- 4 nós de cristal, cada um uma hitbox pequena (mesma técnica das partes do
  Ender Dragon). Dano no nó vale **2x** no escudo.
- A cada fase os nós mudam de lugar (fase 1 nas costas, fase 2 nos flancos,
  fase 3 sob o corpo, exigindo mergulhar) e piscam antes de trocar.
- Cada jogador acima de 4 acrescenta meio nó (1 nó a cada 2 jogadores), no
  máximo 8.
- Opção na configuração para desligar os nós (dano uniforme).

### Dano no escudo por fonte
Sem item especial o escudo recebe **25%** do dano: dá para vencer sem os itens
da cidade, mas é bem mais demorado. Valores do plano oficial mantidos.

| Fonte | Dano no escudo |
|---|---|
| Armas comuns (espada, machado, arco, besta, tridente) | 25% do dano |
| Ferramentas e armas de Oricálcio (tag `aquamarine:shield_breakers`) | 60% do dano |
| Arpão de Oricálcio | dano triplo (300% do dano base); prende o tentáculo por 3 s |
| Carga de Pulso | 100 de dano fixo no escudo, em área de 4 blocos |
| Pilar Ressonante | com Cristais de Maré, pulso a cada 15 s que tira 5% do escudo; até 3 ativos |
| Encantamento Quebra-Carapaça | +10% por nível (até 3) sobre qualquer fonte |
| Acerto em um Nó de Maré | multiplica por 2 o valor acima |

## Tentáculos
- 8 entidades separadas, 60 de vida cada (não escalam com jogadores).
- Cortar um tentáculo o tira do ciclo por 30 s (depois cresce de novo).
- Se os 8 estiverem cortados ao mesmo tempo, o Kraken fica exposto: o escudo
  para de regenerar e leva +25% de dano por 10 s.
- Ataques simultâneos: `2 + n ÷ 3` (arredondado para baixo, máximo 8), com `n`
  jogadores na arena.

## Ataques e avisos
Todo ataque forte tem **aviso visual e sonoro** antes do golpe (regra de
clareza). Dano base com netherite: 4 a 5 golpes até curar.

| Ataque | Fase | Aviso | Efeito | Resposta |
|---|---|---|---|---|
| Pancada | 1-3 | Tentáculo se ergue com sombra na água por 1,5 s | 12 de dano e onda que vira barcos | Desviar do círculo de sombra |
| Agarrão | 1-3 | Tentáculo brilha roxo por 1,5 s e emite som agudo | Prende e arrasta ao fundo (6 de dano por segundo) | Cortar o tentáculo, Tônico de Pressão (solta 50% mais rápido) |
| Nuvem de Tinta | 1-3 | Água escurece em espiral por 2 s | Cegueira numa nuvem de 16 blocos por 12 s | Armadura de Oricálcio completa enxerga; óculos não |
| Redemoinho | 2-3 | Som grave por 2 s e espiral de partículas | Puxa quem está num raio de 16 blocos | Amuleto da Corrente, sair do raio |
| Crias de Kraken | 2-3 | Ovos brilham 2 s | Invoca `3 + n ÷ 2` Crias (4 de dano cada, 8 de vida) | Matar rápido; têm pouca vida |
| Jato de Água | 2-3 | Fileira de bolhas por 1,2 s | 10 de dano em linha de 24 blocos | Sair da linha |
| Rugido Abissal | 2-3 | Corpo se contrai por 1,5 s | Onda de choque que quebra blocos de 1 camada e derruba de barcos (6 de dano) | Estar no fundo ou fora de barcos |
| Raios Guiados | 3 | Céu escurece e uma marca azul no jogador por 2 s | 14 de dano, segue o alvo | Ficar submerso; a marca expira |
| Emboscada por Baixo | 3 | Bolhas no fundo por 2 s | Tentáculos emergem do chão em área de 6 blocos | Sair da área |
| Agarrão Duplo | 3 | Dois tentáculos brilham juntos | Prende com dois, 10 de dano por segundo | Cortar um para soltar parcialmente |

## IA
- **Escolha do ataque:** peso aleatório entre os ataques liberados na fase, sem
  repetir o mesmo ataque duas vezes seguidas; ataques do tipo "área" têm
  intervalo mínimo de 8 s entre si.
- **Alvo (aggro):** quem causou mais dano ao escudo nos últimos 10 s (decai
  com o tempo). Em multiplayer isso cria papéis: um chama atenção, outro
  quebra o escudo.
- **Fuga e abandono:** se nenhum jogador estiver na arena (32 blocos) por 30 s,
  o Kraken mergulha e recupera 10% da vida; se continuar assim por 3 minutos, a
  luta termina com "O Kraken retorna às profundezas..." (sem drops).
- **Contra "torres" e trapaças:** quebra barcos e blocos frágeis (vidro,
  folhas) como no plano; o Rugido Abissal quebra blocos de 1 camada. Escudo
  imune a flechas comuns acima de 64 blocos de distância.
- **Mensagens de fase** no chat: "A carapaça do Kraken se parte!", "O Kraken
  se enfurece!", "O abismo se abre...".

## Barra de vida customizada
- Barra própria no HUD (cliente), no lugar da barra de boss do vanilla:
  moldura de coral e oricálcio dourado, preenchimento azul-profundo com brilho
  verde-água.
- Escudo "Carapaça Abissal" como segunda faixa de cristal azul sobre a vida,
  com rachaduras em 75/50/25% e explosão de estilhaços ao quebrar.
- Marcadores de fase (3 divisões) e nome "KRAKEN" com a fase atual.
- Dano recente aparece como faixa clara que escoa ("barra fantasma").
- Pulso roxo bioluminescente na fase 3 e na regeneração do escudo.
- Texturas em `assets/aquamarine/textures/gui/`; vida e escudo sincronizados
  por packet próprio ou `SynchedEntityData`.

## Escala por número de jogadores (servidor com 5 ou mais)
Fator `f = 1 + 0,3 × (n − 1)`, com `n` jogadores na arena e máximo em `n = 12`.
Substitui a regra antiga do plano ("+50% por jogador extra").
- Vida e escudo máximos de cada fase × `f`.
- Dano que o Kraken causa **não** escala.
- Se jogadores saem no meio da luta, `f` só diminui entre fases.
- Alvo de duração para um grupo bem preparado: **8 a 12 minutos**.

## Dificuldade
- `normal`: um pouco acima do Warden (decisão do Adriano).
- `abissal` (modo mestre): vida e escudo × 1,5 sobre `f`, regeneração do
  escudo a cada 30 s na fase 3, e um troféu dourado exclusivo.

## Recompensas
Olho do Kraken, Tinta Abissal, Coração do Kraken (garantido na primeira
vitória de cada jogador), 1.000 de XP e um troféu. O Coração libera os itens
pós-boss (lista em [05-itens.md](05-itens.md)).

**Como é entregue (2E, valores iniciais):** a recompensa é **pessoal**, entregue direto
no inventário (o que não couber cai aos pés) de cada jogador que estava na arena (32 blocos)
ou causou dano e está a até 128 blocos na morte. Cada um recebe 1 Olho, 2 a 4 Tinta Abissal,
1.000 de XP (cada um, não dividido), 1 Troféu do Kraken (no modo `abissal`, o Troféu Dourado do
Abismo no lugar) e, se for a primeira vitória dele (`KrakenWorldData`), o Coração.
Código: `entity/boss/KrakenRewards`; a mensagem "O Kraken foi derrotado!" vai a todos a 128 blocos.

## Invocação
Detalhes em [03-invocacao-e-chat.md](03-invocacao-e-chat.md).

## Descartado
- **Engolir** (ataque de engolir o jogador): cortado pelo Adriano.
- **Pontos fracos fixos** sem mudança de posição: substituídos pelos Nós de Maré.
