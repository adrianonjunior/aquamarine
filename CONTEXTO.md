# CONTEXTO.md: estado atual do projeto

Atualize este arquivo ao terminar qualquer tarefa. Regras de trabalho com
vários agentes: [AGENTS.md](AGENTS.md).

> **FASE ATUAL: PLANEJAMENTO.** Não implemente nada (código, texturas,
> modelos) até o Adriano liberar explicitamente.

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
- **Óculos de mergulho**: item que melhora a visão dentro da água (**planejado**, ver docs/05-itens.md).
- **Codex Astra** cuida das tarefas de controle do PC (jogo, screenshots,
  Blockbench), por decisão do Adriano.

## Ambiente (validado em 24/10/2026)
- JDK 25 instalado (Microsoft OpenJDK 25.0.4). `gradlew build` e `runClient` funcionam.
- Fabric Loader 0.20.1, Fabric API 0.165.0+26.3, Loom 1.19.x, Gradle 9.8.2.
- Mundo de teste criado pelo Adriano (modo singleplayer, em `run/saves`).
- Python 3.14 + Pillow em `C:\Python314\python`.
- `genSources` falha com o jogo aberto (jar travado); rodar com o jogo fechado.
- Código atual: só o esqueleto (`Aquamarine`, `AquamarineClient`). Nada implementado; o óculos foi removido de propósito (estamos em planejamento).

## Quadro de tarefas
Status: `livre` | `em andamento` | `revisão` | `feito`. Campo "Recurso" = jogo/Gradle em uso.

| Tarefa | Dono | Status | Recurso | Notas |
|---|---|---|---|---|
| Óculos de mergulho (item que melhora visão na água) | Claude | planejado | | Só descrito em docs/05-itens.md; implementar quando o Adriano liberar |
| Texturas dos materiais (Oricálcio, Pérola, Cristal) | Claude | planejado | | Só quando a implementação for liberada |
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

- 24/10/2026 · Claude · Repo criado e docs reorganizados em `docs/`; JDK 25
  instalado; build e runClient OK; AGENTS.md e CONTEXTO.md criados. Óculos
  implementado por engano e removido a pedido (só planejamento). Falta:
  decidir o que sai do planejamento primeiro.
