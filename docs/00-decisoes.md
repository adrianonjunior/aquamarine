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
| Agentes | Vários em paralelo, rodando no **Orca** (regras em [../AGENTS.md](../AGENTS.md)). Divisão fixa: **Claude** = código, texturas em pixel art e decisões criativas; **Codex** = modelagem 3D, animações, testes visuais, screenshots e controle do PC; um **Codex orquestrador** coordena as faixas pelo Orca ([../prompts/orquestrador.md](../prompts/orquestrador.md)). |
| Modelagem 3D | O Codex modela por script (`.bbmodel` gerado, exportado para Java), conferindo no jogo; Blockbench só para ajustes. Contrato com o Claude em [06-tecnico.md](06-tecnico.md). Decidido em 24/10/2026. |
| Tamanho do Kraken | **Bem maior no produto final** (pedido do Adriano, 24/10/2026). Hoje é provisório (hitbox 8 x 6 em `ModEntities.KRAKEN`); durante o desenvolvimento pode ficar pequeno para testar. **Tamanho final (decidido pelo orquestrador a pedido do Adriano; ele avisa se ficar pequeno):** cerca de 24 blocos de largura e 16 de altura, modelo e hitbox (3x o atual), **Aplicado na 2P** (24/10/2026): `ModEntities.KRAKEN` sized 32 x 20 (a pedido do Adriano, em vez de 24 x 16), `clientTrackingRange` 48, `KrakenRenderer.MODEL_SCALE` 4, Nós de Maré e anel de tentáculos ×4, nasce 24 blocos abaixo da superfície. |
| Fase do projeto | **Implementação liberada só para a Entrega 1** (agente Estruturador) e para a **Entrega 2** (Kraken v1), esta em 24/10/2026 por pedido explícito do Adriano. O restante continua em planejamento até nova liberação. |
| Fase 3 | **Preparação liberada em 24/10/2026** (pedido do Adriano): specs, contrato de arquivos, prompts e checklist de liberação. **Execução bloqueada**: Tasks, worktrees, agentes, código, texturas e modelos só depois da Entrega 2 completa e da liberação explícita do Adriano. Plano em [08-fase3.md](08-fase3.md); prontidão em [07-roadmap.md](07-roadmap.md).
| Fase 3: escopo | Baleias (com montaria), Moréia Tempestuosa e Escama Elétrica, Propulsor de Maré, Vila subaquática, Pressão por profundidade, munições do Arpão (veneno, fogo, wither, choque), encantamentos novos, Traje de Mergulho Profundo. Especificação em [08-fase3.md](08-fase3.md); faixas em [07-roadmap.md](07-roadmap.md). Respostas do Adriano em 24/10/2026. |
| Fase 3: Pressão | **Efeito único** com duas fontes (ambiental e Kraken); vale o **maior** nível, não soma (decisão delegada ao Claude). A separação das fontes é a primeira tarefa da Fase 3 (3A), sem alterar a Entrega 2. |
| Fase 3: Propulsor | Vestido na **mão secundária**; consome Bobina Voltaica; ativado por tecla própria. O Manto de Tinta continua como versão pós-boss. |
| Fase 3: Escama Elétrica | Cria a **Bobina Voltaica** (bateria do Propulsor e munição de Choque) **e** o Chicote de Moréia. |
| Fase 3: Baleias | Têm **montaria** (Sela de Baleia). Matar uma baleia **não traz consequência**. |
| Fase 3: Vila | Aldeões com **profissões novas** (Mergulhadores), redesenhada em [08-fase3.md](08-fase3.md). |
| Fase 3: armadura | "Durar mais" = **tempo debaixo d'água** (Reserva de Ar), não durabilidade. |
| Fase 3: munições | Só as pedidas: veneno, fogo, wither e choque. Âncora e Explosiva ficam fora. |
| Fase 3: ordem | 3A a 3E, como em [07-roadmap.md](07-roadmap.md). |
| Fase 3: estruturas | As peças da Vila (e, por coerência, da Cidade Perdida) são **geradas por script** pelo Claude em `tools/`, reproduzíveis e versionadas; o Codex só confere no jogo (decisão delegada ao Claude, 24/10/2026). |
| Fase 3: baleias e o Kraken | As baleias **fogem a 48 blocos do Kraken** e nunca entram na arena (confirmado pelo Adriano em 24/10/2026). |
| Escopo da v1 | **Núcleo + cidade completa:** materiais, Kraken completo (barra customizada, Pressão, invocação), Cidade Perdida com bioma, criaturas e Walker já bem acabados. |
| Como se joga | Servidor dedicado Fabric com **5 ou mais jogadores**. Kraken com mais vida, mais tentáculos simultâneos e escala dinâmica de dificuldade pelo número de jogadores. |
| Decisões criativas | Delegadas ao Claude. Só perguntar ao Adriano o que muda escopo, prazo ou direção geral. |
| Plano oficial (claude.ai) | Deve ser mantido atualizado a partir deste arquivo. |
| Walker | Pacífico, só à noite no oceano profundo; troca Pérola Abissal e Escama Antiga por suprimentos (Tônico, Boia, Bússola, Lanterna, Mapa), **nunca** por itens da cidade; some ao amanhecer (detalhes em [04-cidade-perdida.md](04-cidade-perdida.md)). |
| Óculos de Mergulho | Efeito só visual (menos névoa debaixo d'água), sem visão noturna; não enxergam através da Nuvem de Tinta; ocupam o slot da cabeça (detalhes em [05-itens.md](05-itens.md)). |
| Pontos fracos do escudo | Sim, 4 "Nós de Maré" com dano 2x, que mudam de lugar por fase e aumentam com o número de jogadores (detalhes em [02-kraken.md](02-kraken.md)). |
| Dano dos itens no escudo | Sem item especial 25%; ferramentas de Oricálcio 60%; Arpão dano triplo; Carga de Pulso 100 fixo; Pilar 5% a cada 15 s (tabela em [02-kraken.md](02-kraken.md)). |
| Escala por jogadores | Vida e escudo × `1 + 0,3 × (n − 1)`, máximo em 12 jogadores; dano do Kraken não escala. Substitui a regra antiga "+50% por jogador extra" (fórmula em [02-kraken.md](02-kraken.md)). |
| Achar biomas | Todo bioma do mod é achado com `/locate biome #aquamarine_<bioma>` (pedido do Adriano, 24/10/2026). Como fazer em [06-tecnico.md](06-tecnico.md). |
| Design da v1 | **Fechado em 24/10/2026** em todos os arquivos de `docs/` (Kraken, invocação, cidade, itens, técnico). O andamento da implementação está em [07-roadmap.md](07-roadmap.md). |

Os números das linhas acima são valores iniciais a testar no jogo; ajustes de
balanceamento não precisam de nova decisão, só de atualização em `02-kraken.md`.

## Em aberto
Nada no momento. Novas dúvidas entram aqui.
