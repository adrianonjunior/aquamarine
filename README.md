# AQUAMARINE

Mod Fabric para Minecraft Java 26.3: explore a Cidade Perdida de Nereia e
enfrente o Kraken. Repositório privado, uso com amigos (multiplayer é
prioridade).

Plano oficial: https://claude.ai/artifact/M68jvjvKRddKZMxq3yEQjT

## Agentes
Leia [AGENTS.md](AGENTS.md) (regras, vários agentes em paralelo) e [CONTEXTO.md](CONTEXTO.md) (estado atual e quadro de tarefas).

## Como ler a documentação
1. **Comece por [docs/00-decisoes.md](docs/00-decisoes.md)**: é a fonte da
   verdade. Se algo contradisser, ele vale.
2. Os outros arquivos misturam coisas decididas e **ideias**. Cada um tem um
   aviso de status no topo e seções marcadas.
3. Estado do trabalho: [docs/07-roadmap.md](docs/07-roadmap.md).

| Arquivo | Assunto |
|---|---|
| docs/00-decisoes.md | Decisões tomadas e perguntas ainda abertas |
| docs/01-visao-e-riscos.md | Visão do mod, o que o faz bom, riscos, tom |
| docs/02-kraken.md | Luta, escudo, Pressão, barra de vida, IA, balanceamento |
| docs/03-invocacao-e-chat.md | Anúncio estilo Terraria, aviso falso |
| docs/04-cidade-perdida.md | Estrutura, bioma, criaturas |
| docs/05-itens.md | Materiais, itens e ideias extras |
| docs/06-tecnico.md | Implementação no Fabric 26.3, versões, ferramentas |
| docs/07-roadmap.md | Ordem de trabalho e progresso |

## Código
`src/main` (servidor/comum) e `src/client` (HUD, renderers), pacote
`com.aquamarine`. Requer **JDK 25**. Rodar: `gradlew runClient`.

Última atualização: 24/10/2026.
