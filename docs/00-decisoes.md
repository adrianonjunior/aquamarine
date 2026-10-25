# Decisões (fonte da verdade)

> **Dono de:** decisões tomadas e perguntas em aberto. É o único arquivo que
> lista isso. Se qualquer outro arquivo (ou o plano oficial no claude.ai)
> contradisser, **este vale**.

Decisões do Adriano de 24/10/2026, com as atualizações datadas em cada linha.

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
| Agentes | Vários em paralelo, normalmente no **Orca** (regras em [../AGENTS.md](../AGENTS.md)). Divisão **preferencial**: **Claude** = código, texturas em pixel art e decisões criativas; **Codex** = modelagem 3D, animações, testes visuais, screenshots e controle do PC; um orquestrador pode coordenar as faixas pelo Orca. **Desde 25/10/2026 a divisão não é exclusiva:** quando o Adriano pede, qualquer agente faz uma faixa marcada para o outro (inclusive modelos, QA visual e a 3Q). Um documento dizer "o Codex fará" não impede ninguém. |
| Modelagem 3D | Modelos feitos por script (`.bbmodel` gerado, exportado para Java), conferindo no jogo; Blockbench só para ajustes. Contrato de arquivos em [06-tecnico.md](06-tecnico.md). Decidido em 24/10/2026; quem modela pode ser qualquer agente (linha "Agentes"). |
| Tamanho do Kraken | **32 x 20 blocos** (largura x altura), modelo e hitbox, a pedido do Adriano (24/10/2026; ele avisa se ficar pequeno). Aplicado na faixa 2P: `ModEntities.KRAKEN` 32 x 20, `clientTrackingRange` 48, `KrakenRenderer.MODEL_SCALE` 4, Nós de Maré e anel de tentáculos ×4, nasce 24 blocos abaixo da superfície. |
| Fase do projeto | **Liberadas e implementadas:** Entrega 1, Entrega 2 (Kraken v1) e Fase 3, todas por pedido explícito do Adriano em 24/10/2026; a Fase 3 foi fechada nas faixas em 25/10/2026. **Todo o resto** (Cidade Perdida, bioma Fossa de Nereia, Walker, itens da cidade) continua em planejamento até nova liberação. |
| Fase 3 | **Execução liberada em 24/10/2026** por pedido explícito do Adriano ("termina a fase 2 e começa a fase 3"), com a Entrega 2 completa nas faixas (2A–2Q aceitas, build verde). **Pendência herdada:** o aceite multiplayer real da Entrega 2 (2 clientes, 3 fases) é do Adriano e segue aberto. Plano em [08-fase3.md](08-fase3.md); faixas em [07-roadmap.md](07-roadmap.md). |
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
| Fase 3: detalhes de execução | Decididos pelo agente único em 24/10/2026 (decisões criativas pequenas): o **Arpão** usa o arremesso do tridente do jogo com o nosso item (em voo aparece o tridente; Lealdade e encantamentos de tridente valem); a **Aljava** recolhe as pontas do inventário ao ser usada e devolve agachado; a **ponta simples** sai de 1 lingote de Oricálcio + 1 pederneira (rende 4); o **Propulsor** recarrega sozinho gastando uma Bobina do inventário quando as cargas acabam; **Aclimatação** vale só para a Pressão da profundidade (não esvazia a da luta); **Correnteza** usa a eficiência de movimento na água do jogo (+10% por nível). |
| Fase 3: Vila (v2) | Decidido pelo agente único em 25/10/2026 (decisões criativas pequenas, dentro de [08-fase3.md](08-fase3.md)): a **reputação** de cada jogador é a soma da XP das trocas com os Mergulhadores (mais o Alerta de Moréia) e abre os níveis de comércio nos mesmos limites da XP dos aldeões vanilla; por isso os Mergulhadores já nascem com as trocas de **todos** os níveis e a tela mostra o nível e a barra do jogador. O **Mapa de Fossas de Moréia** aponta o bioma da tag `#aquamarine_fossa_de_nereia` (hoje o oceano profundo). A **Torre de Vigia** é uma segunda peça que o jigsaw põe na superfície acima da Cúpula; as **"escadas até a superfície"** do Cais são elevadores de bolhas (areia das almas sobe, magma desce), que funcionam em qualquer profundidade. Valores em [08-fase3.md](08-fase3.md), seção 8. |
| Fase 3: o que fica para a Cidade Perdida | A **Bússola de Maré** e o **Mapa de Nereia** apontam a Cidade Perdida, que ainda não existe: ficam para a fase da Cidade Perdida (o Cartógrafo vende os outros mapas). Decidido em 25/10/2026, pelo mesmo motivo da linha de trocas da v1 em [08-fase3.md](08-fase3.md). |
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
| Áudio | **Ambientação e música próprias**, estilo Calamity, tudo original ([09-audio.md](09-audio.md)): camadas de ambiente por profundidade, Pressão, hora e local, temas de música (bioma, Cidade, Vila e um por fase do Kraken) e efeitos que substituem os sons vanilla. Produção **local e grátis** (síntese por script, IA local opcional, ffmpeg); o Adriano aprova por **portões de audição**. Decidido em 25/10/2026; muda a antiga regra "sem música de boss na v1". |
| Áudio: luta do Kraken | Pedido do Adriano em 25/10/2026, depois de **aprovar as 6 amostras do A0** ("gostei muito das criações"): a luta do Kraken vem antes da ambiência. **3 trilhas** (Tormenta, Fúria, Abismo; uma por fase) em **3 camadas sincronizadas** (base orquestral com o motivo do Kraken e **sinos**; **breakcore** com bateria sintetizada e picotada, sem amostra nenhuma; **glória** com **trombetas**, trompas e coro), misturadas ao vivo pelo cliente conforme a luta; stingers de troca de fase, vitória e recuo; os efeitos do Kraken, dos tentáculos, das Crias e dos ovos trocam os sons vanilla. Música **por síntese**: a IA local não gera camadas sincronizadas nem laços exatos. Detalhes em [09-audio.md](09-audio.md), "Luta do Kraken". |
| A luta do Kraken muda o mundo | Pedido do Adriano em 25/10/2026: **céu, névoa, água e luz tingidos por fase** (só no cliente; clarão dourado na vitória), **tempestade** durante a luta (chuva na fase 1, trovoada a partir da 2) com raios em volta da arena, **recife morrendo e algas arrancadas** perto do Kraken, **crateras** das Pancadas e das Emboscadas e **onda de choque** na troca de fase. Toda destruição precisa da regra `mob_griefing` (vale também para o Rugido) e da opção `krakenWorldDamage`; a tempestade, da `krakenStorm`. Valores em [02-kraken.md](02-kraken.md), "A luta muda o mundo". |
| Criaturas do oceano | Correção de 25/10/2026 ("difícil de encontrá-los"): Moréia, Baleia e Cachalote **nunca nasciam sozinhos** (o jogo recusa mob comum nascendo dentro d'água; agora a regra é a dos animais aquáticos) e a baleia pedia água abaixo do fundo da maioria dos oceanos. Nova densidade: baleias onde a coluna d'água tem 14 blocos ou mais (oceano fundo peso 3, oceano comum peso 1, até 6 numa área); Cachalote peso 2 nas fossas; Moréia peso 3, a 12 blocos ou mais de fundura em coluna de 18, 1 em 4 nas fossas ou 1 em 2 sob tempestade, **uma a cada 80 blocos**. Medido em `runClientGameTest` (parte `spawn`); detalhes em [08-fase3.md](08-fase3.md). |
| Design da v1 | **Fechado em 24/10/2026** em todos os arquivos de `docs/` (Kraken, invocação, cidade, itens, técnico). O andamento da implementação está em [07-roadmap.md](07-roadmap.md). |

Os números das linhas acima são valores iniciais a testar no jogo; ajustes de
balanceamento não precisam de nova decisão, só de atualização em `02-kraken.md`.

## Em aberto
Perguntas que só o Adriano responde (o resto está decidido):
- **Branch `estruturador/entrega-1`** (6 commits fora da `main`) e as worktrees antigas das faixas: apagar ou guardar?
