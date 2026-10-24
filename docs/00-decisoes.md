# Decisões (fonte da verdade)

> **Dono de:** decisões tomadas e perguntas em aberto. É o único arquivo que
> lista isso. Se qualquer outro arquivo (ou o plano oficial no claude.ai)
> contradisser, **este vale**.

Decisões do Adriano, 24/10/2026.

## Decididas
| Assunto | Decisão |
|---|---|
| Onde a luta acontece | Superfície **e** fundo, por fase. Efeito de **peso** que afunda o jogador (detalhes em [02-kraken.md](02-kraken.md)). |
| Itens da cidade | Úteis, **não obrigatórios**. Sem eles o jogador não teria motivo para explorar a cidade. |
| Dificuldade do Kraken | Um pouco mais difícil que o Warden. |
| Multiplayer | Prioridade alta (balanceamento e aggro pensados para vários jogadores). |
| Estilo visual | Texturas 16x16 estilo vanilla; Kraken "minecraftiano" com silhueta forte e detalhes bioluminescentes (escolha delegada ao Claude). |
| Texturas | **Todas** feitas pelo Claude em pixel art estilo mods de Minecraft. O Adriano não fará nenhuma, mas pode trocar depois. |
| Modelagem | Desde a primeira versão, de **altíssima qualidade**. Nada de protótipo de cubos. |
| Engolir | **Cortado.** Não implementar. |
| Aviso falso | Mantido: mensagens de invocação aparecem no chat, mas o Kraken não vem. Desligável na config. |
| Barra de vida | Barra de boss **customizada e bonita** (detalhes em [02-kraken.md](02-kraken.md)). |
| Nomes dos itens | Português no jogo, com tradução inglesa. |
| Publicação | Só com amigos por enquanto. Repositório privado `adrianonjunior/aquamarine`. |
| Walker | Mob aceito: zumbi pacífico, só à noite, nas profundezas, que troca itens (detalhes em [04-cidade-perdida.md](04-cidade-perdida.md)). |
| Óculos de Mergulho | Item aceito: melhora a visão dentro da água (detalhes em [05-itens.md](05-itens.md)). |
| Agentes | Vários em paralelo (regras em [../AGENTS.md](../AGENTS.md)). Codex Astra cuida das tarefas de controle do PC (jogo, screenshots, Blockbench). |
| Fase do projeto | **Planejamento.** Nada é implementado até o Adriano liberar explicitamente com palavras suas (não vale deduzir de outra instrução). |
| Escopo da v1 | **Núcleo + cidade completa:** materiais, Kraken completo (barra customizada, Pressão, invocação), Cidade Perdida com bioma, criaturas e Walker já bem acabados. |
| Como se joga | Servidor dedicado Fabric com **5 ou mais jogadores**. Kraken com mais vida, mais tentáculos simultâneos e escala dinâmica de dificuldade pelo número de jogadores. |
| Decisões criativas | Delegadas ao Claude. Só perguntar ao Adriano o que muda escopo, prazo ou direção geral. |
| Plano oficial (claude.ai) | Deve ser mantido atualizado a partir deste arquivo. |

## Em aberto
- **Walker:** o que vende e aceita; o que acontece com ele de dia.
- **Pontos fracos brilhantes no escudo do Kraken:** vale a complexidade?
- **Dano dos itens da cidade no escudo:** hoje, sem eles o escudo leva 25% do dano.
- **Óculos de Mergulho:** qual efeito (visão noturna, menos névoa na água, ou os dois).
