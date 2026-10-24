# Notas técnicas

> **Dono de:** ambiente, build, convenções e as decisões técnicas de implementação. Tudo **decidido**; a base (Entrega 1) está implementada.

## Base
- Minecraft Java 26.3 "Wilderness Bound" (15/09/2026), Fabric Loader +
  Fabric API, Java 25, Gradle 9.x, plugin `net.fabricmc.fabric-loom`.
- Desde o 26.1 o jogo não é ofuscado: nomes oficiais da Mojang, Yarn não é
  mais usado. Dependências com `implementation`, mod depende de `fabric-api`.
- Esqueleto criado a partir do `fabric-example-mod` oficial (mod id
  `aquamarine`, pacote `com.aquamarine`). Versões: loader 0.20.1, Fabric API
  0.165.0+26.3, Loom 1.19.x, Gradle 9.8.2.
- Código atual: a base da Entrega 1 (ver "Como adicionar conteúdo" abaixo).

## Ambiente local (validado em 24/10/2026)
- **JDK 25 obrigatório**: Microsoft OpenJDK 25.0.4 instalado em
  `C:\Program Files\Microsoft\jdk-25.0.4.101-hotspot` (usar como `JAVA_HOME`;
  o JDK 21 também está na máquina e não serve).
- `./gradlew build` e `./gradlew runClient` funcionam. Mundo de teste em `run/saves`.
- `genSources` falha com o jogo aberto (jar travado); rodar com o jogo fechado.
- **Python:** `C:\Python314\python` com Pillow (o alias `python3` do Windows
  Store não funciona). Não usar `-I`: ele esconde o Pillow.
- Screenshots do jogo (F2): `run/screenshots/`; qualquer agente pode ler os PNGs.
- API do Minecraft 26.3 usa nomes oficiais da Mojang; consulte com `javap` nos
  jars de `~/.gradle/caches/fabric-loom/minecraftMaven/` ou com `genSources`.
  O `genSources` grava os fontes em
  `.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-*-sources.jar` da
  pasta onde rodou; descompacte num diretório temporário e use `grep`.
- No Git Bash, `javap` do Windows precisa de caminhos `cygpath -w` no classpath.
- Heredoc com Python (`python - <<EOF`) trava neste ambiente: escreva o script
  em arquivo.
- Na 26.3 várias APIs mudaram de nome: telas usam `GuiGraphicsExtractor` e
  `extractBackground`; `Minecraft.gui.screen()` em vez de `minecraft.screen`;
  `ContainerInput` em vez de `ClickType`; `Player.drop(stack, false, Prediction.PREDICTED)`.

## Como testar
- `./gradlew build`: compila e roda os testes JUnit de `src/test` (consistência
  de recursos: modelos, texturas, lang pt/en, receitas, camadas de equipamento;
  e a config). Item novo sem textura ou sem nome quebra o build.
- `./gradlew runClientGameTest`: abre o jogo sozinho (`src/gametest`), cria um
  mundo plano, monta as cenas, confere por código a Forja e o bônus da armadura
  e grava screenshots em `build/run/clientGameTest/screenshots/`. Não precisa
  controlar o PC; qualquer agente pode rodar e ler os PNGs. Não roda no CI.
- `C:\Python314\python tools/gen_textures.py --preview` regera as texturas e
  grava folhas de prévia em `build/texture_preview*.png`.

## Como adicionar conteúdo (estrutura da Entrega 1)
- **Item:** campo em `item/ModItems` com `register(...)` (entra sozinho na aba
  criativa "Aquamarine"); `items/<nome>.json`, `models/item/<nome>.json`, textura
  em `tools/gen_textures.py` e nome nos dois `lang`.
- **Bloco:** `block/ModBlocks` + `ModItems.registerBlock`; blockstate, modelo,
  loot table em `data/aquamarine/loot_table/blocks/` e tags de ferramenta.
- **Receita da Forja:** `data/aquamarine/recipe/<nome>.json` com
  `"type": "aquamarine:abyssal_forging"` (resto igual ao `crafting_shaped`).
- **Tags:** chaves em `ModTags`; JSON em `data/aquamarine/tags/`.
- **Config:** campos públicos em `config/AquamarineConfig` (gravados em
  `config/aquamarine.json`; campos novos entram sozinhos no arquivo).
- **Efeitos de Oricálcio:** `item/OrichalcumEffects` (`armorPieces`,
  `hasFullSet`, `PRESSURE_REDUCTION_PER_PIECE` para a Pressão).
- **Descrição do item:** `tooltip.aquamarine.<item>` (e `.2`, `.3`) nos dois
  `lang`; aparece no tooltip e na página de informação do JEI
  (`client/ItemDescriptions`). O teste do `build` exige a descrição de todo item.
- **JEI (opcional):** `client/compat/jei/AquamarineJeiPlugin` (entrypoint
  `jei_mod_plugin`): categoria "Forja Abissal", a Forja como estação também da
  bancada comum e as descrições. Versão em `gradle.properties` (`jei_version`);
  o JEI completo só entra no `runClient`/testes (`localRuntime`). As receitas
  vêm do servidor integrado: em servidor dedicado a categoria fica vazia.
- **Aba criativa:** abas de mod ficam na página 2 do criativo; por isso
  `ModCreativeTab` também põe os itens nas abas vanilla.
- **Invocação:** `event/KrakenSummoning` (`start`, `hasDeepWater`, gancho
  `spawnKraken`) e `block/AbyssalAltarBlock`.
- **Óculos:** `client/DivingGogglesVision` + `client/mixin/WaterFogEnvironmentMixin`.
- **Barra de boss:** o boss cria um `entity/boss/AquamarineBossBar`, chama
  `addPlayer`/`removePlayer` em `startSeenByPlayer`/`stopSeenByPlayer` e
  `update(vida, vidaMax, escudo, escudoMax, fase, regenerando)` a cada tick. O
  cliente (`client/bossbar/AquamarineBossBarHud` + `BossHealthOverlayMixin`)
  troca a barra vanilla desse evento pela própria; sem o mod no cliente, aparece
  a vanilla. Exemplo completo: `entity/BossDummy` (entidade de teste oculta,
  `/summon aquamarine:boss_dummy`, `/damage @e[type=aquamarine:boss_dummy] 40`).

## Convenções de código e arte
- `src/main` = comum/servidor; `src/client` = só cliente (HUD, renderers).
- Nada de texto fixo: tudo em `Component.translatable` + `lang/pt_br.json` e
  `en_us.json`; nomes em português no jogo.
- Texturas: pixel art estilo mods de Minecraft, 16x16 para itens e blocos,
  paleta do mod (ver [01-visao-e-riscos.md](01-visao-e-riscos.md)), geradas por
  script em `tools/` para poderem ser regeradas.
- Modelos de alta qualidade desde a primeira versão (ver [00-decisoes.md](00-decisoes.md)).
- Todo item novo responde "o que faz na luta ou na exploração?" (ver [05-itens.md](05-itens.md)).

## Estrutura de pacotes planejada
```
com.aquamarine
  entity/kraken   boss, tentáculos, fases, escudo
  event           invocação e mensagens no chat
  item            arpão, carga de pulso
  block           Pilar Ressonante, Forja Abissal
  worldgen        Cidade Perdida, depois o bioma
```

## Como fazer cada parte (decidido)
- **Barra de boss:** customizada. O servidor continua usando `ServerBossEvent`
  para controlar quem vê o boss; o cliente cancela a barra vanilla desse evento
  (mixin no renderer da barra de boss ou callback da Fabric API de HUD) e
  desenha a barra própria com `GuiGraphics`. Escudo, fase e dano recente vêm de
  `SynchedEntityData` ou de packet próprio.
- **Pressão:** efeito de status próprio, nível 0 a 4 por jogador, atualizado por
  tick perto do Kraken (regras em [02-kraken.md](02-kraken.md)); ajusta a velocidade
  vertical e aplica os efeitos de cada nível.
- **Escudo:** campos `shield` e `maxShield` no Kraken, sincronizados com
  `SynchedEntityData`. O dano é interceptado em `hurtServer`: com escudo > 0, o
  dano vai para o escudo, multiplicado conforme a fonte (tabela em
  [02-kraken.md](02-kraken.md)). Os Nós de Maré são hitboxes filhas (como as partes do
  Ender Dragon).
- **Tentáculos:** entidades independentes com referência ao "dono" (Kraken);
  mais fácil de testar que partes do mesmo bounding box.
- **Animações e modelos:** Blockbench exportando para o formato nativo do jogo
  (`AnimationDefinition` + `EntityModel`), **sem GeckoLib**, para não depender
  de biblioteca extra. Modelos de alta qualidade desde a primeira versão.
- **Tags de item:** `aquamarine:shield_breakers` (60% de dano no escudo) e
  `aquamarine:metal_armor` (Enguia Elétrica causa 1,5x). Ajustáveis por outros mods.
- **Bioma:** primeiro tentar **Biolith**; se não houver versão para 26.3,
  **TerraBlender**; se nenhuma existir, a Cidade e a Cratera ganham água e névoa
  próprias dentro do volume da estrutura (sem bioma de verdade), e o bioma
  entra quando houver biblioteca.
- **Configuração** (arquivo JSON próprio, `config/aquamarine.json`):
  `falseWarning` (aviso falso, padrão ligado), `weakPoints` (Nós de Maré, padrão
  ligado), `difficulty` (`normal` ou `abissal`), `scalingCap` (máximo de
  jogadores no cálculo, padrão 12), `naturalWalkers` (liga ou desliga o Walker).
- **Dados do mundo:** `SavedData` para "Kraken derrotado", "jogador já recebeu a
  Chave" e "jogador já recebeu o Coração".
- **Multiplayer:** todo cálculo de luta no servidor (aggro, Pressão, escala por
  jogadores); o cliente só renderiza e mostra HUD.
- **Desempenho:** 8 tentáculos + Crias + partículas + tempestade; testar cedo,
  já com os modelos finais.

## Ferramentas
- IntelliJ IDEA Community com o plugin Minecraft Development.
- Blockbench para modelos e texturas.
- Git + GitHub: repositório privado `adrianonjunior/aquamarine` (conta `adrianonjunior`).
- Testes no jogo: `runClient` do Loom; comandos `/summon aquamarine:kraken`
  e `/kill` para iterar rápido.
- Testes automáticos: Fabric GameTests para o escudo (absorve o dano e quebra
  no zero), a escala por jogadores e a Pressão.
