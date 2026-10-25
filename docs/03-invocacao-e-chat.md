# Invocação e mensagens no chat

> **Dono de:** a sequência de invocação, as mensagens do chat, o aviso falso e
> o som e a música da luta. Tudo **decidido**. Decisões gerais em
> [00-decisoes.md](00-decisoes.md).

## Sequência de invocação
O Chamariz Abissal é usado no Altar do Abismo (ver [02-kraken.md](02-kraken.md) para as
condições de água). Mensagens enviadas aos jogadores num raio de 128 blocos.

| Tempo | Mensagem | Cor | Efeito |
|---|---|---|---|
| 0 s | "As águas ficam estranhamente calmas..." | cinza, itálico | sons do oceano param |
| 5 s | "Algo enorme se move nas profundezas..." | azul | tela treme, bolhas |
| 10 s | **"O Kraken despertou!"** | roxo, negrito | tempestade, barra de boss, ataque surpresa |

Fim: **"O Kraken foi derrotado!"** ou *"O Kraken retorna às profundezas..."*
Mensagens de fase e transições: ver [02-kraken.md](02-kraken.md).

- **Título na tela** "O KRAKEN" grande no centro na mensagem final de
  invocação (comando de título do vanilla).
- Todas as mensagens em `lang/pt_br.json` e `en_us.json` com
  `Component.translatable`.

## Aviso falso
Algo no chat que parece o Kraken, mas não é.
- Só em noite de tempestade, com jogador em oceano profundo.
- 8% de chance por noite elegível, no máximo uma vez a cada 3 dias de jogo.
- A sequência das três mensagens aparece igual à real (sem barra de boss).
  Segue o céu escurecendo por 20 s e a mensagem em cinza: *"As águas voltam ao
  silêncio..."*
- Ligado por padrão; desligável na configuração.

## Som e música
- Sons próprios, ambientação e **música** (inclusive temas do Kraken por fase e stingers da
  invocação, da vitória e do retorno) são especificados em [09-audio.md](09-audio.md), que é o
  dono. Hoje o código ainda usa sons vanilla até o Áudio v1 ser entregue.

## Implementação
- **Feito (Entrega 1):** `event/KrakenSummoning` com as mensagens de 0, 5 e 10 s
  (raio 128), bolhas, título "O KRAKEN", tempestade e o modo falso (as três
  mensagens + "As águas voltam ao silêncio..." 20 s depois); o Altar do Abismo
  checa a água funda e não gasta o Chamariz se faltar. `spawnKraken` é o gancho
  onde o Kraken entra.
- **Feito (2E):** `event/FalseWarning` (gatilho: tempestade à noite, jogador na água de
  oceano profundo, 8% de sorte uma vez por noite, mínimo de 3 dias entre avisos, config
  `falseWarning`; checa a cada 5 s no overworld; contas puras com JUnit) e
  `event/KrakenWorldData` (`SavedData` no overworld). `SummonEffectPayload` leva ao cliente
  o **silêncio do oceano** (0 s: barra sons novos da categoria ambiente e para os que tocam),
  o **tremor de tela** (5 s: desvio de até 1,2° na câmera) e o **céu escurecendo** (os 20 s
  finais do aviso falso: véu azul-escuro no HUD, 55% de opacidade no máximo). Lado do
  cliente: `SummonEffects` e os mixins `CameraMixin` e `SoundEngineMixin`.
- **Falta:** sons próprios.
- Evento de invocação no servidor com contador de ticks (0, 100, 200).
- `KrakenWorldData` guarda quem já recebeu o Coração e o controle do aviso falso
  (se o Kraken foi derrotado não é guardado: nada o usa na v1).
