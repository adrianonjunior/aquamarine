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
- A v1 usa **sons próprios** (rugido, estilhaço do escudo, avisos de ataque,
  caminhada do Walker), sem trilha própria.
- Música de boss própria só no polimento, depois da v1.

## Implementação
- Evento de invocação no servidor com contador de ticks (0, 100, 200).
- `SavedData` do mundo guarda se o Kraken foi derrotado e se o jogador já
  recebeu o Coração.
