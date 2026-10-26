# AQUAMARINE

Mod Fabric para Minecraft Java 26.3: explore a Cidade Perdida de Nereia, desça ao fundo do mar
sentindo a Pressão e enfrente o Kraken. Código aberto no GitHub; multiplayer é prioridade e toda a
lógica roda no servidor.

Plano oficial: https://claude.ai/artifact/M68jvjvKRddKZMxq3yEQjT (pode estar desatualizado; em
conflito, vale [docs/00-decisoes.md](docs/00-decisoes.md)).

## O que tem no mod
- **Entrega 1:** Abissal (minério, ferramentas, armadura), Forja Abissal, Óculos de Mergulho, aba
  criativa e plugin do JEI.
- **Entrega 2:** o Kraken (3 fases, escudo com Nós de Maré, tentáculos, Crias, barra de boss
  própria), a Pressão, a invocação no Altar do Abismo e as recompensas.
- **Fase 3:** Pressão por profundidade com Medidor, Regulador e Traje de Mergulho Profundo; Moréia
  Tempestuosa e Escama Elétrica; Baleia-de-Nereia (montaria de 3) e Cachalote; Arpão Abissal com
  Aljava e 4 pontas; 7 encantamentos; Propulsor de Maré; a Vila subaquática (Porto dos Náufragos), com
  Cúpula, cinco prédios, Cais com elevadores de bolhas, Torre de Vigia, Mergulhadores em 3 profissões,
  reputação por jogador e o Alerta de Moréia.
- **Fase 4:** Neutrônio estilo Avaritia e o Cavitador, Oricálcio renomeado para Abissal, 35 blocos aquáticos e 35
  vidros rachados, oficina da Vila e troféus em 3D, Boia nova, Cria, Ovo e Tentáculo refeitos. Resumo de tudo, do que
  falta e do plano: [docs/resumo.md](docs/resumo.md).
- **Em andamento (liberada em 26/10/2026):** a Fase 6, release `0.6.0`: Kraken renovado, energia aquática, Aurium,
  acessórios, radar e mais ([docs/11-fase6.md](docs/11-fase6.md)). A Cidade Perdida de Nereia e o Kraken completo
  ficam sem fase por enquanto. Estado de cada faixa e o que só o Adriano pode testar:
  [docs/07-roadmap.md](docs/07-roadmap.md).

## Versões e fases
A release **`0.N.0` entrega a Fase `N`**; correções saem como `0.N.x`. Não existe Fase 5 (o `0.5.0` entregou a
Fase 4; a próxima é a `0.6.0`, da Fase 6). Tabela de releases, fases e especificações:
[docs/07-roadmap.md](docs/07-roadmap.md), "Releases e fases". A regra: [docs/00-decisoes.md](docs/00-decisoes.md).

## Para jogar (sem nada de programação)
Baixe o `.zip` da release (no GitHub, aba Releases), extraia e clique duas vezes em `INSTALAR.bat`: ele cria o
perfil "Aquamarine" no Minecraft Launcher. Para jogar junto há "Abrir para LAN" e a pasta `Servidor` pronta.
Tudo explicado no `LEIA-ME.txt` do pacote. Como gerar uma release nova: [docs/06-tecnico.md](docs/06-tecnico.md),
"Release para jogadores".

## Tecnologias
Java 25, Fabric Loader + Fabric API, Gradle 9 com o plugin Loom, JUnit e Fabric Client GameTests.
Modelos e texturas são gerados por scripts Python (Pillow) em `tools/`. Versões exatas em
`gradle.properties`; ambiente validado em [docs/06-tecnico.md](docs/06-tecnico.md).

## Como rodar

**Portal de assets:** execute `.\tools\atlas\abrir.ps1` para abrir o [Atlas técnico](tools/atlas/README.md)
com modelos 3D, animações, áudio, texturas e documentos. O Kraken v2 do site já está no jogo.

1. Instale o **JDK 25** e aponte o `JAVA_HOME` para ele (o caminho usado nesta máquina está em
   [docs/06-tecnico.md](docs/06-tecnico.md), "Ambiente local").
2. Na raiz do repositório:
   - `./gradlew build`: compila e roda os testes JUnit (consistência de recursos, contas da luta).
     O jar fica em `build/libs/`.
   - `./gradlew runClient`: abre o jogo com o mod (mundo de teste em `run/saves`).
   - `./gradlew runClientGameTest`: abre o jogo sozinho, roda os testes de jogo e grava
     screenshots em `build/run/clientGameTest/screenshots/`. Para uma parte só:
     `AQUAMARINE_GAMETEST_ONLY=moray,whale ./gradlew runClientGameTest` (a parte `worldgen` abre um
     segundo mundo, normal, para conferir a Vila gerada de verdade).
3. Para jogar fora do ambiente de desenvolvimento: copie o jar de `build/libs/` e a Fabric API para a
   pasta `mods/` de uma instalação Fabric 26.3 (cliente e servidor).
4. **Configuração:** `config/aquamarine.json` é criado na primeira execução (aviso falso, Nós de
   Maré, dificuldade, escala por jogadores, quem pilota a baleia, Alerta de Moréia).
5. **Texturas e modelos:** com Python 3 e Pillow, `python tools/gen_textures.py` (Entregas 1 e 2),
   `python tools/gen_phase3_textures.py` (itens, blocos e Mergulhadores da Fase 3),
   `python tools/models/<criatura>/build_model.py` (modelo; a textura vem depois, ver 06-tecnico) e
   `python tools/gen_harbor.py` (as duas peças da Vila: o Porto e a Torre de Vigia).
6. Comandos de teste (operador): `/aquamarine kraken ...`, `/aquamarine moray ...`,
   `/aquamarine whale ...`, `/aquamarine sperm_whale ...`, `/aquamarine harbor alert` e
   `/aquamarine harbor reputation <jogador> [set <pontos>]`. Achar a Vila:
   `/locate structure #aquamarine:harbor`.

## Estrutura de pastas
| Pasta | Conteúdo |
|---|---|
| `src/main` | Código comum e do servidor (`com.aquamarine`: itens, blocos, entidades, Pressão, comandos) e os dados (`resources`: modelos, texturas, lang, receitas, tags, encantamentos) |
| `src/client` | Só do cliente: renderers e modelos das criaturas, HUD, teclas, mixins de tela |
| `src/test` | Testes JUnit (rodam no `build`) |
| `src/gametest` | Testes de jogo com screenshots (`runClientGameTest`) |
| `tools/` | Scripts reproduzíveis de texturas e de modelos (`tools/models/<criatura>/`) |
| `docs/` | Especificação e decisões (índice abaixo) |
| `prompts/` | Prompts já enviados aos agentes de IA (histórico, não são ordens atuais) |

## Por onde começar (agentes)
1. [AGENTS.md](AGENTS.md) (regras) e [CONTEXTO.md](CONTEXTO.md) (quem faz o quê agora e o que está liberado).
2. [docs/00-decisoes.md](docs/00-decisoes.md) (fonte da verdade; seções de **ideia** não são decisão) e
   [docs/06-tecnico.md](docs/06-tecnico.md) (build, ambiente, convenções).
3. A especificação da **fase da sua tarefa** (tabela abaixo) e os marcos dela em [docs/07-roadmap.md](docs/07-roadmap.md).
   Fase "planejada" não está liberada: não escreva código, textura nem modelo dela.

## Índice (cada arquivo é dono do seu assunto)

**Regras e estado do projeto**
| Arquivo | Assunto |
|---|---|
| AGENTS.md | Regras de comportamento e coordenação entre agentes |
| CONTEXTO.md | Fase atual e o que está liberado, tarefas em andamento e registro de entregas |
| docs/00-decisoes.md | Decisões tomadas e perguntas em aberto |
| docs/06-tecnico.md | Implementação, ambiente, build, testes, release e convenções |

**Fases e releases** (a release `0.N.0` entrega a Fase `N`)
| Arquivo | Release | Assunto |
|---|---|---|
| docs/07-roadmap.md | todas | Tabela "Releases e fases" e os marcos de cada fase |
| docs/resumo.md | todas | Retrato do que foi feito, do que falta e do plano proposto |
| docs/08-fase3.md | `0.3.0` | Fase 3: fauna, equipamentos de mergulho e Vila |
| docs/10-fase4.md | `0.5.0` | Fase 4: Kraken vivo, oficina, construção aquática e Neutrônio |
| docs/11-fase6.md | `0.6.0` (planejada) | Fase 6: Kraken renovado, energia aquática, Aurium, acessórios, radar |
| docs/09-audio.md | transversal | Áudio: direção sonora, ambiência, música, lista de efeitos, método e padrões |

**Design do mod (Entregas 1 e 2 e Cidade Perdida)**
| Arquivo | Assunto |
|---|---|
| docs/01-visao-e-riscos.md | Visão do mod, riscos, tom e paleta |
| docs/02-kraken.md | Luta, escudo, Pressão, barra de vida, IA, balanceamento |
| docs/03-invocacao-e-chat.md | Anúncio estilo Terraria, aviso falso |
| docs/04-cidade-perdida.md | Estrutura, bioma, criaturas (incluindo o Walker); **sem fase** |
| docs/05-itens.md | Materiais e itens |

**QA e histórico (somente leitura)**
| Arquivo | Assunto |
|---|---|
| docs/relatorio-2q.md | QA visual da Entrega 2 |
| docs/relatorio-3q.md | QA da Fase 3 e o roteiro do aceite com jogadores reais |
| docs/historico.md | Registro antigo de handoffs, tirado do CONTEXTO.md |
| prompts/*.md | Prompts já enviados aos agentes (histórico; cada um diz quando foi usado); `prompts/audio.md` é o do Áudio v1 |
| tools/atlas/README.md | Site (Atlas) com modelos, animações, áudio, texturas e docs |
