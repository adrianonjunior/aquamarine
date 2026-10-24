# Prompt dos agentes da Entrega 2 (Kraken v1)

**Não use antes de o Adriano liberar a Entrega 2.** Cole o texto abaixo como
primeira mensagem do agente e troque `<FAIXA>` por 2A, 2B, 2C, 2D ou 2E
(ver "Entrega 2" em `docs/07-roadmap.md`). Um agente por faixa.

---

Você é um agente do projeto **Aquamarine** (mod Fabric, Minecraft Java 26.3,
repositório `C:\Users\adria\Documents\AQUAMARINE`, GitHub privado
`adrianonjunior/aquamarine`). Você não conhece o projeto; o repositório tem tudo.
Sua faixa: **<FAIXA>** da "Entrega 2: Kraken v1". Não implemente outras faixas.

## Leitura (uma vez, no máximo 15 min)
1. `AGENTS.md` (há **vários agentes ao mesmo tempo**) e `CONTEXTO.md`
   (o handoff da Entrega 1 diz o que existe e o que não foi testado).
2. `docs/00-decisoes.md`, `docs/06-tecnico.md`.
3. `docs/07-roadmap.md`, seção "Entrega 2": sua faixa e dependências.
   Se a faixa depende do 2A e ele ainda não foi mergeado na `main`, **pare** e
   avise o Adriano; não invente um Kraken paralelo.
4. `docs/02-kraken.md` (dono das regras da luta) e, se sua faixa exigir,
   `docs/05-itens.md`. Não leia o resto.
5. Código existente a reaproveitar: `entity/BossDummy`, `entity/boss/` (barra de
   vida), `item/OrichalcumEffects`, `event/KrakenSummoning` (`spawnKraken` é o
   gancho), `config/AquamarineConfig`.

## Como trabalhar
- Registre sua faixa em "Em andamento" do `CONTEXTO.md` com um plano por bloco
  e estimativa **antes** de codar. Worktree e branch próprios
  (`<agente>/kraken-<faixa>`). Janela de cerca de 5 h: commit e push a cada
  marco verde (no máximo ~40 min sem commit), handoff no `CONTEXTO.md`, pare de
  abrir frentes ao passar de 70-80% do orçamento e deixe o próximo passo exato.
- Números e regras vivem em `docs/02-kraken.md`: se algo precisar mudar,
  mude lá no mesmo commit, nunca só no código. Ajustes de valor inicial não
  exigem pergunta; mudança de design, sim.
- Valores configuráveis em `config/aquamarine.json`; textos em `lang/*.json`
  (pt_br e en_us); sem texto fixo no código.
- Multiplayer é prioridade: toda lógica no servidor, estado visível sincronizado
  (`SynchedEntityData` ou packet), nada que dependa de estar no cliente local.
- Texturas e modelos são seus (pixel art, paleta do mod, qualidade alta, scripts
  em `tools/`). Controle do PC e Blockbench aberto são do Codex Astra.
- Teste: `./gradlew build` e `./gradlew runClientGameTest` (screenshots em
  `build/run/clientGameTest/screenshots/`). O que não puder testar vai para o
  handoff, dito exatamente.
- Jogo aberto trava o Gradle: declare o recurso "jogo" em "Em andamento".

## Ao terminar
Merge na `main` com build verde (`git pull --rebase` antes), marque sua faixa em
`docs/07-roadmap.md`, apague sua linha de "Em andamento" e escreva o handoff:
pronto, não testado, próximo passo.
