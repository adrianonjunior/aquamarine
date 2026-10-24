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
| Agentes | Vários em paralelo (regras em [../AGENTS.md](../AGENTS.md)). Codex Astra cuida das tarefas de controle do PC (jogo, screenshots, Blockbench). |
| Fase do projeto | **Implementação liberada só para a Entrega 1** (agente Estruturador) em 24/10/2026, por pedido explícito do Adriano. O restante continua em planejamento até nova liberação. |
| Escopo da v1 | **Núcleo + cidade completa:** materiais, Kraken completo (barra customizada, Pressão, invocação), Cidade Perdida com bioma, criaturas e Walker já bem acabados. |
| Como se joga | Servidor dedicado Fabric com **5 ou mais jogadores**. Kraken com mais vida, mais tentáculos simultâneos e escala dinâmica de dificuldade pelo número de jogadores. |
| Decisões criativas | Delegadas ao Claude. Só perguntar ao Adriano o que muda escopo, prazo ou direção geral. |
| Plano oficial (claude.ai) | Deve ser mantido atualizado a partir deste arquivo. |
| Walker | Pacífico, só à noite no oceano profundo; troca Pérola Abissal e Escama Antiga por suprimentos (Tônico, Boia, Bússola, Lanterna, Mapa), **nunca** por itens da cidade; some ao amanhecer (detalhes em [04-cidade-perdida.md](04-cidade-perdida.md)). |
| Óculos de Mergulho | Efeito só visual (menos névoa debaixo d'água), sem visão noturna; não enxergam através da Nuvem de Tinta; ocupam o slot da cabeça (detalhes em [05-itens.md](05-itens.md)). |
| Pontos fracos do escudo | Sim, 4 "Nós de Maré" com dano 2x, que mudam de lugar por fase e aumentam com o número de jogadores (detalhes em [02-kraken.md](02-kraken.md)). |
| Dano dos itens no escudo | Sem item especial 25%; ferramentas de Oricálcio 60%; Arpão dano triplo; Carga de Pulso 100 fixo; Pilar 5% a cada 15 s (tabela em [02-kraken.md](02-kraken.md)). |
| Escala por jogadores | Vida e escudo × `1 + 0,3 × (n − 1)`, máximo em 12 jogadores; dano do Kraken não escala. Substitui a regra antiga "+50% por jogador extra" (fórmula em [02-kraken.md](02-kraken.md)). |
| Design da v1 | **Fechado em 24/10/2026** em todos os arquivos de `docs/` (Kraken, invocação, cidade, itens, técnico). Falta só a liberação do Adriano para implementar. |

Os números das linhas acima são valores iniciais a testar no jogo; ajustes de
balanceamento não precisam de nova decisão, só de atualização em `02-kraken.md`.

## Em aberto
Nada no momento. Novas dúvidas entram aqui.
