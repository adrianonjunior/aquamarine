# Prompt do Opus 5.5 (agente único): fechar a Fase 3 e deixar o projeto organizado

Cole o texto abaixo, inteiro, como primeira mensagem de **um** Opus 5.5 (esforço alto), aberto na pasta
principal do repositório. Escrito pelo orquestrador em 24/10/2026, quando o Adriano mandou parar os
agentes do Orca e concluir tudo com **um único agente**. Este arquivo não repete o plano: o plano mora
em `docs/07-roadmap.md`, a spec em `docs/08-fase3.md`, as decisões em `docs/00-decisoes.md`.

---

Você é o **agente único** de fechamento do projeto **Aquamarine** (mod Fabric, Minecraft Java 26.3,
repositório `C:\Users\adria\Documents\AQUAMARINE`, GitHub privado `adrianonjunior/aquamarine`). Acumula os
papéis de Claude (código, texturas por script, decisões pequenas) e de Codex (modelos e animações por script,
QA visual). Não há outros agentes rodando: todos foram parados. **Missão: terminar a Fase 3 e deixar o projeto
revisado, limpo, organizado e documentado**, sem quebrar a Entrega 1 e a Entrega 2. Fale português; commits
em português terminando com `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.

## 0. Regras que valem acima de tudo
- Leia `AGENTS.md` (economia de contexto; vários agentes: hoje só você), `CONTEXTO.md` (aviso "Atualização" e os
  handoffs recentes), `docs/06-tecnico.md` (ambiente, `JAVA_HOME=C:\Program Files\Microsoft\jdk-25.0.4.101-hotspot`,
  contratos de modelos e entidades da Entrega 2 e da Fase 3). Leia só o necessário de cada faixa.
- **Cada fato mora em um arquivo**; decisão nova entra primeiro em `docs/00-decisoes.md`. Mudança de design:
  pare e pergunte ao Adriano. Ajuste de valor inicial: faça e registre.
- Servidor manda; toda lógica no servidor; multiplayer é prioridade. Textos em `lang/pt_br.json` e `en_us.json`;
  config em `config/aquamarine.json`.
- Nunca `push --force` nem `reset --hard`; `git pull --rebase` antes do `push`; nada de apagar o que não é seu
  sem `git log`/`git blame`. **Nada irreversível sem perguntar** (apagar branches, arquivos de outras pessoas, mundos de teste).
- O **jogo** (`runClientGameTest`) é um por vez; o disco C: precisa de >=10 GB livres (confira `df -h /c`; ele já
  encheu duas vezes). Compile em silêncio (`./gradlew build -q`) e leia só o fim. No máximo 6 screenshots por ciclo.
- Registre o andamento no "Registro de handoff" de `CONTEXTO.md` a cada marco (pronto / falta / como retomar). Ao
  passar de 70% do contexto, pare, registre e avise o Adriano.

## 1. Estado atual (24/10/2026, verificado pelo orquestrador)
**Entrega 1** concluída. **Entrega 2 (Kraken v1)**: todas as faixas 2A-M, 2A-C, 2A-T, 2B, 2C, 2D-M, 2D-C, 2D-T, 2E, 2Q
aceitas na `main`, build verde. Kraken final 32 x 20 (`MODEL_SCALE` 4), 16 animações, locomoção no servidor,
tentáculos, Cria, ovos, Pressão do Kraken, escudo e Nós de Maré, drops/troféus/aviso falso, relatório `docs/relatorio-2q.md`
(P1 e P2 corrigidos em `64a8724`). **Pendências da Entrega 2 que só o Adriano pode fechar:** aceite multiplayer real
(2 clientes, 3 fases), ritmo e dano da luta, legibilidade do aviso do slam com a câmera normal.

**Fase 3** (liberada pelo Adriano em 24/10/2026; `docs/08-fase3.md`), por faixa:

| Faixa | Dono anterior | Estado |
|---|---|---|
| 3A Pressão por profundidade, Medidor, Regulador, Reserva de Ar, Traje | Claude (worktree 2B) | **Feito na branch `claude/3A` (`d98ed00`, publicada), NÃO mergeada na `main` e não validada pelo orquestrador.** Valide (build + `runClientGameTest`), faça merge/rebase com a `main` e feche. |
| 3B-M1 Moréia (modelo/animações/views) | Codex high | **Não feito.** Parou por disco cheio sem commit (só `CONTEXTO.md` alterado na worktree `3B-M1`). Faça do zero. |
| 3B-M2 Baleia-de-Nereia e Cachalote (modelo/animações/views) | Codex medium | **Feito e na `main`** (`53c4629`..`176dbb0`: `WhaleView`, `SpermWhaleView`, renderers, UV provisório). Falta ver no jogo (precisa das entidades). |
| 3B-C Moréia (IA, ataques, hitbox segmentada, drops), Escama Elétrica, Bobina, Chicote | Claude | Não iniciado. Depende de 3A e 3B-M1. |
| 3B-W Baleias (spawn, canto, domar, Sela, montaria de 3, fuga do Kraken a 48 blocos) | Claude | Não iniciado. Depende de 3A e 3B-M2. |
| 3B-T Texturas Moréia, Baleia, Cachalote | Claude | Não iniciado. |
| 3C Aljava e 4 pontas do Arpão, linhas na tabela de dano, 7 encantamentos | Claude | Não iniciado. |
| 3D Propulsor de Maré (tecla, carga por Bobina, impulso validado no servidor, Sobrecarga) | Claude | Não iniciado. |
| 3E Vila subaquática (peças por script, Eclusa, profissões, comércio, reputação, Alerta de Moréia) | Claude | Não iniciado. |
| 3Q QA final (3 clientes) | Codex | Não iniciado. |

Aceite da Fase 3: ver "Aceite da Fase 3" em `docs/08-fase3.md`.

Itens abertos herdados da Entrega 2 (resolva se couber, senão registre): itens **Arpão, Carga de Pulso e Pilar**
da 2C só existem como ganchos (a 3C os usa); sons próprios (2E); troféus no mundo sem screenshot; tentáculo criado por
comando não tem dono e não some com o Kraken; armadura em bebês; descrições do JEI e aba criativa para os itens novos.

## 2. Plano de execução (nesta ordem; um commit verde por marco)
1. **Diagnóstico (30 min):** `git status`, `git log --oneline -15`, `./gradlew build -q`, `grep` de TODO/FIXME/HACK
   (há ~10 nos arquivos: `KrakenEffects`, `KrakenVisualGameTest`, `BossDummy`, `KrakenEntity`, `ModCreativeTab`,
   `AbyssalForgeResultSlot`, `ResourceConsistencyTest`, `tools/gen_textures.py`, `kraken_animations.py`). Apresente ao
   Adriano um diagnóstico curto e o plano; siga sem esperar, salvo dúvida de design.
2. **Fechar a 3A:** valide a branch `claude/3A`, resolva conflitos com a `main`, build e GameTests verdes, mergeie.
3. **3B-M1 e 3B-C (Moréia), depois 3B-W (baleias) e 3B-T (texturas):** modelos por script como o Kraken
   (`tools/models/<criatura>/build_model.py` gera `.bbmodel`, `uv.png` e as classes Java do cliente; textura final por
   script **depois** do `build_model.py`); entidade implementa a view e troca o `NoopRenderer` na mesma faixa; comandos
   `/aquamarine ...` de spawn e animação para teste. Câmera acima do chão em arena aquática nos screenshots; criaturas
   precisam de contraste e volume sob a água.
4. **3C, 3D, 3E** como no roadmap (peças da Vila geradas por script em `tools/`, reproduzíveis).
5. **3Q:** simule o ciclo do aceite; o que exigir 3 clientes reais vai para a lista "só o Adriano".
6. **Revisão geral e organização final (seção 3 abaixo).**
7. **Relatório final** (máx. 20 linhas) ao Adriano: por faixa, resultado e evidência; o que não foi testado; o que só
   ele pode julgar (multiplayer real, ritmo, montaria, Propulsor, legibilidade dos avisos); bugs abertos.

## 3. Revisão geral, limpeza e organização (extra pedido pelo Adriano)
Atue como **Arquiteto de Software Sênior e Especialista em Qualidade de Código** e execute, de forma estruturada,
depois que as faixas estiverem funcionando (ou por área, a cada marco):

1. **Varredura e auditoria (code review):** códigos mortos, comentários desnecessários, variáveis não usadas,
   redundâncias; falhas óbvias de segurança e tratamento de erro (validar no servidor todo pacote de cliente: Propulsor,
   montaria, comércio, comandos; permissões dos comandos `/aquamarine`); padronize formatação, indentação e nomes
   no estilo dos arquivos vizinhos. Não refatore por gosto: só o que reduz risco ou duplicação real.
2. **Pendências (TODO/FIXME/HACK):** varra, implemente a solução definitiva de cada uma ou registre por quê não;
   liste funcionalidades incompletas que ainda impedem fechar o escopo.
3. **Estrutura de pastas e arquivos:** avalie `src/main`, `src/client`, `src/gametest`, `src/test`, `tools/`, `docs/`,
   `prompts/`; mantenha a organização modular (assets, lógica, cliente, ferramentas). Confira o `.gitignore`
   (`build/`, `.gradle/`, `run/`, `logs/`, `mods/`, screenshots, `__pycache__`, `*.tmp`): nada gerado ou local no Git.
   Liste o que mover/descartar; **pergunte antes de apagar** qualquer arquivo versionado de outro agente.
4. **Documentação:** deixe o `README.md` com visão geral, tecnologias (Fabric, Java 25, Gradle/Loom), instruções passo a
   passo de instalar, configurar e rodar (`./gradlew build`, `runClient`, `runClientGameTest`, `JAVA_HOME`), estrutura
   de pastas resumida e links para `docs/`. Atualize `docs/07-roadmap.md` (marque as faixas), `docs/05-itens.md`
   (itens novos), `docs/02-kraken.md` (linhas de dano das munições) e o `CONTEXTO.md` (registre o handoff final).
5. **Prontidão para commit/GitHub:** `main` limpa, `./gradlew build` e `runClientGameTest` verdes, sem segredos
   (`.env`, tokens, caminhos pessoais), branches antigas listadas (`estruturador/*`, `codex/3B-M2`, `opus/kraken-anim`,
   `claude/3A`: **pergunte antes de apagar**), versão em `gradle.properties`. Liste arquivos a manter e a descartar.

## 4. Critério de pronto
`./gradlew build` e `./gradlew runClientGameTest` verdes na `main`; roadmap com todas as faixas da Fase 3 marcadas;
README e docs coerentes (cada fato em um único arquivo); `.gitignore` correto; relatório final entregue com a
lista do que só o Adriano pode julgar. Se algo da Fase 3 não couber no seu contexto, **pare em um marco verde**,
registre exatamente o que falta e avise: é melhor entregar menos, estável, do que muito, quebrado.
