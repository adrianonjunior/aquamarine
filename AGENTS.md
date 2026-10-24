# AGENTS.md: regras de comportamento para agentes

> **Dono de:** como os agentes trabalham juntos. Não repete decisões
> ([docs/00-decisoes.md](docs/00-decisoes.md)), estado e tarefas atuais
> ([CONTEXTO.md](CONTEXTO.md)) nem build, ambiente e convenções de código
> ([docs/06-tecnico.md](docs/06-tecnico.md)). **Leia os três antes de mexer em qualquer coisa.**

> **FASE ATUAL: IMPLEMENTAÇÃO LIBERADA SOMENTE PARA A "ENTREGA 1" (agente Estruturador, ver docs/07-roadmap.md).** Todo o resto continua em planejamento: não escreva código,
> texturas ou modelos do mod até o Adriano liberar explicitamente. Na dúvida,
> pergunte antes de implementar.

## ⚠️ Vários agentes rodam ao mesmo tempo
O Adriano mantém **diversos agentes simultâneos** neste mesmo projeto. Trabalhe
assumindo que outro agente está editando arquivos agora.

1. **Uma tarefa, um dono.** Registre a tarefa na tabela "Em andamento" de
   `CONTEXTO.md` antes de começar; não pegue tarefa que já tem dono.
2. **Branch e worktree próprios** para trabalho de código:
   `git worktree add ../aquamarine-<agente>-<tarefa> -b <agente>/<tarefa>`.
   Isso evita disputar `build/` e `.gradle/`. Docs pequenos podem ir direto na `main`.
3. **Só edite os arquivos da sua tarefa.** Arquivos compartilhados (registro de
   itens, `lang/*.json`, `fabric.mod.json`, mixins, `CONTEXTO.md`): mudanças
   pequenas, só acrescentando linhas; nunca reformate ou reordene.
4. **Commits pequenos; `git pull --rebase` antes de `git push`.** Nunca
   `push --force` nem `reset --hard` em trabalho que não é seu.
5. **Não apague nem sobrescreva arquivo de outro agente** sem ver o que há nele
   (`git log`, `git blame`).
6. **Ao terminar:** apague sua linha de "Em andamento", marque o marco em
   `docs/07-roadmap.md` e acrescente uma linha ao "Registro de handoff".

### Recursos de uso exclusivo (um agente por vez)
- **Jogo aberto (`runClient`)**: trava jars; com ele aberto, `genSources` e
  outros builds podem falhar com "process has a lock". Feche antes de rodar
  Gradle pesado e declare o recurso "jogo" em "Em andamento".
- **Gradle na mesma pasta**: dois builds na mesma árvore se atrapalham. Use worktrees.
- **Mundo de teste e `run/`**: compartilhados; não apague saves sem avisar.

### Quem faz o quê
| Agente | Responsabilidade |
|---|---|
| **Claude** | Design, código do mod, texturas em pixel art, modelos, docs. |
| **Codex Astra** | Controle do PC, por decisão do Adriano: abrir o jogo, testes visuais, screenshots, Blockbench. Devolve prints e logs para os outros lerem. |
| Outros | Registrar-se em "Em andamento" antes de começar. |

## Regras de documentação
- **Cada fato mora em um único arquivo.** Os outros apenas linkam para ele.
  Donos: decisões e perguntas em aberto → `docs/00-decisoes.md`; ambiente,
  versões e convenções → `docs/06-tecnico.md`; marcos → `docs/07-roadmap.md`;
  tarefas em andamento e handoff → `CONTEXTO.md`; índice → `README.md`.
- Decisão nova entra primeiro em `docs/00-decisoes.md`. Se contradisser outro
  arquivo, corrija o outro no mesmo commit.
- Seções de ideia dizem "(ideia)" no título; nunca misture ideia com decisão na mesma seção.
- Idioma de docs e commits: português. Commits terminam com a linha de
  coautoria do agente que os fez.
