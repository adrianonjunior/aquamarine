# AGENTS.md: regras para agentes (leia antes de mexer em qualquer coisa)

Projeto: mod Fabric **Aquamarine** (Minecraft Java 26.3). Contexto, decisões e
estado atual: [CONTEXTO.md](CONTEXTO.md). Decisões de design: [docs/00-decisoes.md](docs/00-decisoes.md).

> **FASE ATUAL: PLANEJAMENTO.** Só documente e planeje. Não escreva código,
> texturas ou modelos do mod até o Adriano liberar explicitamente (ver
> CONTEXTO.md). Na dúvida, pergunte antes de implementar.

## ⚠️ Vários agentes rodam ao mesmo tempo
O Adriano mantém **diversos agentes simultâneos** (Claude, Codex Astra, outros)
neste mesmo projeto. Trabalhe assumindo que outro agente está editando arquivos
agora. Regras:

1. **Uma tarefa, um dono.** Antes de começar, registre a tarefa em
   `CONTEXTO.md` → "Quadro de tarefas" (status `em andamento` + seu nome). Não
   pegue tarefa que já tem dono.
2. **Branch e worktree próprios.** Não trabalhe direto na `main`. Use
   `git worktree add ../aquamarine-<agente>-<tarefa> -b <agente>/<tarefa>`.
   Isso também evita disputar a pasta `build/` e `.gradle/`.
3. **Só edite os arquivos da sua tarefa.** Arquivos compartilhados (registro
   de itens, `lang/*.json`, `fabric.mod.json`, mixins, `CONTEXTO.md`) → faça
   mudanças pequenas e pontuais, só acrescentando linhas; nunca reformate ou
   reordene.
4. **Commits pequenos, `git pull --rebase` antes de `git push`.** Nunca
   `push --force` e nunca `reset --hard` em trabalho que não é seu.
5. **Não apague nem sobrescreva arquivo de outro agente** sem verificar o que
   está nele (git log/blame).
6. **Ao terminar:** atualize o quadro de tarefas e `docs/07-roadmap.md`, e
   deixe uma linha em "Registro de handoff" do `CONTEXTO.md`.

### Recursos que não se compartilham (um por vez)
- **Jogo aberto (`runClient`)**: trava jars; enquanto houver um Minecraft
  aberto, `genSources` e outros builds podem falhar com "process has a lock".
  Feche o jogo antes de rodar Gradle pesado, e **só um agente usa o jogo por
  vez** (peça a vez no quadro de tarefas, campo "Recurso").
- **Gradle na mesma pasta**: dois builds na mesma árvore se atrapalham. Use
  worktrees separadas.
- **Mundo de teste e `run/`**: compartilhados na árvore principal; não apague
  saves nem `run/` sem avisar.
- **Mesma conta GitHub**: use `adrianonjunior`; confira `gh auth status`.

## Quem faz o quê
| Agente | Responsabilidade |
|---|---|
| **Claude** | Design, código do mod (itens, entidades, HUD, worldgen), **todas as texturas em pixel art e os modelos** (o Adriano não fará nenhuma), docs. |
| **Codex Astra** | Tarefas de **controle do PC**, por decisão do Adriano: abrir o jogo, rodar testes visuais, tirar e analisar screenshots, operar Blockbench e afins. Devolve resultados (prints, logs) para os outros agentes lerem. |
| Outros | Registrar-se no quadro de tarefas antes de começar. |

## Build e execução
- **JDK 25 obrigatório**: `JAVA_HOME="C:\Program Files\Microsoft\jdk-25.0.4.101-hotspot"`.
- Build: `./gradlew build`. Jogo: `./gradlew runClient`. Mundo de teste já existe.
- Python 3.14 com Pillow em `C:\Python314\python` (o alias `python3` do
  Windows Store não funciona). Use para gerar texturas.
- Screenshots do jogo (F2): `run/screenshots/`. Qualquer agente pode ler os PNGs.
- A API do Minecraft 26.3 usa **nomes oficiais da Mojang** (sem Yarn, sem
  ofuscação). Consulte com `javap` nos jars em
  `~/.gradle/caches/fabric-loom/minecraftMaven/` ou com `genSources` (jogo fechado).

## Padrões do projeto
- Mod id `aquamarine`, pacote `com.aquamarine`. `src/main` = comum/servidor,
  `src/client` = só cliente (HUD, renderers).
- **Nada de texto fixo**: tudo em `Component.translatable` + `lang/pt_br.json`
  e `en_us.json`. Nomes em português no jogo.
- **Texturas**: pixel art estilo Minecraft, 16x16 para itens e blocos, paleta
  do mod (azul profundo, verde-água, roxo bioluminescente, dourado gasto).
  Geradas por script em `tools/` para poderem ser regeradas.
- **Modelos**: qualidade **altíssima** desde o início; proibido entregar
  "dois cubos se mexendo". Kraken e criaturas com silhueta forte, muitos
  detalhes, animação própria.
- Todo item novo deve responder: "o que faz na luta ou na exploração?"
- Docs: seguem o aviso de status de cada arquivo. Decisão nova entra em
  `docs/00-decisoes.md`, nunca só em outro arquivo.
- Idioma dos docs e commits: português.
- Commits terminam com a linha de coautoria do agente que os fez.
