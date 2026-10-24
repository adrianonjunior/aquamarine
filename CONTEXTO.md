# CONTEXTO.md: estado atual do projeto

Atualize este arquivo ao terminar qualquer tarefa. Regras de trabalho com
vários agentes: [AGENTS.md](AGENTS.md).

## O projeto em 5 linhas
- **Aquamarine**: mod Fabric (Minecraft Java 26.3) de fim de jogo no oceano.
- Explora-se a **Cidade Perdida de Nereia** (materiais: Oricálcio, Pérola
  Abissal, Cristal de Maré, Escama Antiga) para enfrentar o **Kraken**.
- Kraken: 3 fases (Tormenta, Fúria, Abismo), escudo "Carapaça Abissal",
  8 tentáculos, efeito de **Pressão** que afunda o jogador. Luta na superfície
  e no fundo. Dificuldade: um pouco acima do Warden. Multiplayer: prioridade alta.
- Barra de vida **customizada e bonita** (não a vanilla).
- Repositório privado `adrianonjunior/aquamarine`; uso só com amigos.

## Decisões que não podem ser esquecidas
Fonte da verdade: [docs/00-decisoes.md](docs/00-decisoes.md). Resumo:
- Itens da cidade úteis, não obrigatórios. "Engolir" **cortado**. "Aviso falso" mantido.
- **Todas as texturas são feitas pelo Claude em pixel art** (estilo mods de
  Minecraft); o Adriano não fará nenhuma, mas pode trocar depois.
- **Modelagem inicial de altíssima qualidade** (não protótipos de cubos).
- **Walker**: zumbi pacífico noturno das profundezas que **troca itens** (detalhes em aberto).
- **Óculos de mergulho**: item que melhora a visão dentro da água (em desenvolvimento).
- **Codex Astra** cuida das tarefas de controle do PC (jogo, screenshots,
  Blockbench), por decisão do Adriano.

## Ambiente (validado em 24/10/2026)
- JDK 25 instalado (Microsoft OpenJDK 25.0.4). `gradlew build` e `runClient` funcionam.
- Fabric Loader 0.20.1, Fabric API 0.165.0+26.3, Loom 1.19.x, Gradle 9.8.2.
- Mundo de teste criado pelo Adriano (modo singleplayer, em `run/saves`).
- Python 3.14 + Pillow em `C:\Python314\python`.
- `genSources` falha com o jogo aberto (jar travado); rodar com o jogo fechado.
- Código atual: `Aquamarine`, `AquamarineClient`, `item/ModItems` (Óculos de Mergulho), `event/DivingGogglesHandler`.

## Quadro de tarefas
Status: `livre` | `em andamento` | `revisão` | `feito`. Campo "Recurso" = jogo/Gradle em uso.

| Tarefa | Dono | Status | Recurso | Notas |
|---|---|---|---|---|
| Óculos de mergulho (item, textura, efeito na água) | Claude | revisão | jogo | Código, textura (`tools/gen_goggles.py`), receita e lang prontos; build OK. Falta testar no jogo: `/give @s aquamarine:diving_goggles`, entrar na água, conferir visão e modelo na cabeça (F2). Seguimento: reduzir a névoa da água (mixin cliente) 
| Texturas provisórias dos 3 materiais (Oricálcio, Pérola, Cristal) | Claude | livre | | Pixel art via script |
| Ciclo de screenshot (F2 → `run/screenshots` → leitura) | Codex Astra | livre | jogo | Validar que os PNGs saem e que dá para lerem |
| Gerar sources do Minecraft (`genSources`) | qualquer | livre | Gradle, jogo fechado | Facilita consultar a API |
| Kraken protótipo (modelo de alta qualidade, barra customizada, escudo, Pressão) | Claude | livre | | Depois dos materiais |
| Walker (definir trocas e comportamento) | Claude + Adriano | livre | | Perguntas em aberto em docs/00-decisoes.md |
| Pesquisar plugin/MCP confiável de Blockbench | Codex Astra | livre | | Antes de instalar qualquer coisa |

## Perguntas ainda abertas (ver docs/00-decisoes.md)
- O que o Walker vende e aceita; o que acontece com ele de dia.
- Pontos fracos brilhantes no escudo do Kraken: vale a complexidade?
- Dano exato dos itens da cidade contra o escudo (hoje: sem eles, 25%).

## Registro de handoff
Uma linha por entrega, a mais recente no topo: `data · agente · o que mudou · o que falta`.

- 24/10/2026 · Claude · Óculos de Mergulho implementados (item equipável na cabeça, visão noturna discreta só submerso, receita couro+vidro+cobre, textura pixel art gerada por script). Falta: teste no jogo e névoa da água.
- 24/10/2026 · Claude · Repo criado e docs reorganizados em `docs/`; JDK 25
  instalado; build e runClient OK; AGENTS.md e CONTEXTO.md criados. Falta:
  óculos de mergulho e primeira textura.
