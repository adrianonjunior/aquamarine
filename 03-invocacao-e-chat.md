# Invocação e mensagens no chat (estilo Terraria)

## Sequência (do plano)
| Tempo | Mensagem | Cor | Efeito |
|---|---|---|---|
| 0 s | "As águas ficam estranhamente calmas..." | cinza, itálico | sons do oceano param |
| 5 s | "Algo enorme se move nas profundezas..." | azul | tela treme, bolhas |
| 10 s | **"O Kraken despertou!"** | roxo, negrito | tempestade, barra de boss, ataque surpresa |

Fim: **"O Kraken foi derrotado!"** ou *"O Kraken retorna às profundezas..."*

## Ideias extras
- **Mensagens de fase:** ao quebrar cada escudo/mudar de fase, uma linha no
  chat: "A carapaça do Kraken se parte!", "O Kraken se enfurece!",
  "O abismo se abre...". No Terraria isso ajuda muito a entender a luta.
- **Título na tela** além do chat: "O KRAKEN" grande no centro na mensagem
  final (comando de título do vanilla, `ClientboundSetTitleTextPacket`).
- **Tradução:** todas as mensagens em `lang/pt_br.json` e `en_us.json` usando
  `Component.translatable`, nunca texto fixo no código.
- **Música:** uma faixa de boss própria tocando enquanto ele está vivo.
- **Aviso falso (mantido):** às vezes, em noites de tempestade no oceano
  profundo, as mensagens aparecem como se o Kraken fosse surgir, mas ele não
  vem. Cria tensão. (Desligável na config.)

## Implementação (rascunho)
- Um "evento de invocação" no servidor com contador de ticks (0, 100, 200).
- Mensagens enviadas a jogadores num raio de 128 blocos.
- Guardar no `SavedData` do mundo se o Kraken já foi derrotado (para o drop
  garantido do Coração e para a aparição natural).
