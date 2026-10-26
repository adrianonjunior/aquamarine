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
- Código atual: Entregas 1 e 2 e a Fase 3 (ver "Como adicionar conteúdo" abaixo). Versão do mod em
  `gradle.properties`: 0.3.0 desde a Fase 3.

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
- Heredoc com Python (`python - <<EOF`) funciona no Git Bash, mas já travou em outros
  terminais desta máquina: se travar, escreva o script em arquivo.
- Na 26.3 várias APIs mudaram de nome: telas usam `GuiGraphicsExtractor` e
  `extractBackground`; `Minecraft.gui.screen()` em vez de `minecraft.screen`;
  `ContainerInput` em vez de `ClickType`; `Player.drop(stack, false, Prediction.PREDICTED)`.

## Como testar
- `./gradlew build`: compila e roda os testes JUnit de `src/test` (consistência
  de recursos: modelos, texturas, lang pt/en, receitas, camadas de equipamento;
  e a config). Item novo sem textura ou sem nome quebra o build.
- `./gradlew runClientGameTest`: abre o jogo sozinho (`src/gametest`), cria um
  mundo plano (as configurações "consistentes" da Fabric: semente 1, **sem estruturas**), monta as cenas, confere por código a Forja e o bônus da armadura
  e grava screenshots em `build/run/clientGameTest/screenshots/`. Não precisa
  controlar o PC; qualquer agente pode rodar e ler os PNGs. Não roda no CI.
  Para iterar numa parte só: `AQUAMARINE_GAMETEST_ONLY=moray,whale ./gradlew runClientGameTest`
  (nomes nos `enabled(...)` de `AquamarineClientGameTest`); sem a variável roda tudo.
  O `build` não compila `src/gametest`: use `./gradlew compileGametestJava` antes.
- A parte `worldgen` (`WorldgenGameTest`, a última) abre um **segundo mundo, normal**, com semente
  fixa e estruturas ligadas, e confere o que só o mundo gerado prova: `/locate` do Porto, Cúpula seca,
  Torre de Vigia, elevadores, Eclusa e Mapa de Fossas. O `/place structure` do mundo plano não registra
  a estrutura nem aplica a decoração do oceano, então não pega esses erros.
- A parte `spawn` (`OceanSpawnGameTest`) abre outro mundo normal, para num oceano fundo com o surgimento
  de mobs ligado, acelera o tempo com `/tick sprint` e conta baleias e Moréias (média e máximo no log,
  linha "Densidade do oceano"). A parte `worldfx` (`KrakenWorldGameTest`) confere recife morto e crateras;
  a música por fase, o céu e a tempestade são conferidos na parte `kraken`.
- A parte `multiplayer` (`MultiplayerGameTest`) sobe um **servidor dedicado de verdade** e conecta o cliente pela
  rede (API `worldBuilder().createServer()` do teste de cliente da Fabric): entrada com registros sincronizados,
  música da luta por fase no cliente, céu, chuva, pacote do servidor (Pressão), pacote do cliente (Propulsor),
  vitória e a baleia andando com o piloto. Pega o que o singleplayer esconde.
- Partes da Fase 6: `energy` (geradores, cabo, Coletor, Mesa de Encantamento Aquática, mesas da Vila e Radar) e `krakens`
  (o `/summon` dos três Krakens novos, com capturas). A `forge` precisa rodar junto com a `showcase`, que coloca a Forja; a
  `moray` falha se rodar logo depois da `showcase` (o jogador fica de armadura e o choque cai).
- `C:\Python314\python tools/gen_textures.py --preview` regera as texturas e
  grava folhas de prévia em `build/texture_preview*.png`.

## Release para jogadores
Pacote para quem só tem o Minecraft: `./gradlew build` e depois `C:\Python314\python tools/release/montar_release.py`
(versões lidas do `gradle.properties`). A versão do mod é a da release `0.N.0` da fase ([00-decisoes.md](00-decisoes.md),
"Versões e fases"). A Team Reborn Energy (Fase 6) vai dentro do jar do mod (`include` no `build.gradle`); o pacote não muda.
Sai `build/release/Aquamarine-<versão>.zip` com:
- `INSTALAR.bat` → `arquivos/instalar.ps1`: faz o que o instalador oficial do Fabric faz, **sem Java** (grava o perfil
  de versão do Fabric Loader, baixado do meta.fabricmc.net ou a cópia do pacote, e um `.jar` vazio em `versions/`),
  cria o perfil "Aquamarine <versão>" no `launcher_profiles.json` com **pasta própria** (`.minecraft/aquamarine`,
  4 GB) e copia os mods; guarda a lista de perfis antiga em `launcher_profiles.json.antes-do-aquamarine`;
- `mods/`: o jar do mod e a Fabric API exata do `gradle.properties`;
- `Servidor/`: `INICIAR-SERVIDOR.bat` → `iniciar-servidor.ps1` acha um Java 25 (do sistema, do `JAVA_HOME` ou o que
  o Minecraft Launcher baixou), pede a EULA uma vez, copia os mods do pacote e abre o lançador de servidor da Fabric
  (que baixa o servidor do Minecraft); `server.properties` com **`white-list=false`** (o 26.3 cria servidores com a
  lista ligada) e `allow-flight=true` (os empurrões do Kraken não derrubam ninguém por "voar");
- `LEIA-ME.txt` e `Servidor/LEIA-ME-SERVIDOR.txt` para o jogador, em português.
Modelos em `tools/release/modelo/` (`{{VERSAO}}`, `{{MC}}`, `{{LOADER}}`, `{{ICONE}}`): `.bat` só em ASCII com CRLF,
`.ps1` e `.txt` em UTF-8 com BOM (o PowerShell 5.1 do Windows só lê acentos assim). Antes de publicar: a parte
`multiplayer` do `runClientGameTest`, o instalador numa `.minecraft` falsa (`instalar.ps1 -MinecraftDir <pasta>`) e o
pacote de servidor ligado de verdade com um cliente entrando (`runClient --args="--quickPlayMultiplayer localhost:<porta>"`,
com `skipMultiplayerWarning:true` no `run/options.txt` e `online-mode=false` só no servidor de teste).

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
- **Efeitos Abissais:** `item/AbyssalEffects` (`armorPieces`,
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
- **Bioma novo:** além do JSON em `data/aquamarine/worldgen/biome/<nome>.json`,
  crie a tag `data/minecraft/tags/worldgen/biome/aquamarine_<nome>.json` com
  `aquamarine:<nome>`; assim `/locate biome #aquamarine_<nome>` funciona (sem
  namespace o jogo usa `minecraft`). O teste do `build` exige a tag.
  Hoje existe só `#aquamarine_fossa_de_nereia`, provisória: aponta para
  `#minecraft:is_deep_ocean` até o bioma Fossa de Nereia existir; quem criar o
  bioma troca o valor por `aquamarine:fossa_de_nereia`.
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

## Estrutura de pacotes
```
com.aquamarine            (src/main: comum e servidor)
  block      blocos (Forja, Altar, Boia, troféus, blocos de trabalho da Vila)
  command    /aquamarine kraken | moray | whale | sperm_whale | harbor
  config     config/aquamarine.json
  effect     efeitos (Tônico de Pressão)
  entity     criaturas da Fase 3, views do modelo, profissões e estrutura da Vila (ModVillagers)
  entity/boss  Kraken: fases, escudo, ataques, locomoção, barra, recompensas
  event      invocação, aviso falso, estado salvo do Kraken
  harbor     Porto dos Náufragos: reputação, trocas por nível, Alerta de Moréia, elevadores, mapa de bioma
  item       itens (Abissal, Arpão, Aljava, Propulsor, Traje, Óculos...)
  menu       tela da Forja Abissal
  mixin      ganchos no jogo (tridente, aldeão, troca)
  pressure   Pressão (estado puro + gerente no servidor)
  recipe     receita da Forja
com.aquamarine.client     (src/client: renderers, modelos, HUD, teclas, JEI)
```
A Cidade Perdida (`worldgen`) entra quando for liberada.

## Como fazer cada parte (decidido)
- **Barra de boss:** customizada. O servidor continua usando `ServerBossEvent`
  para controlar quem vê o boss; o cliente cancela a barra vanilla desse evento
  (mixin no renderer da barra de boss ou callback da Fabric API de HUD) e
  desenha a barra própria com `GuiGraphics`. Escudo, fase e dano recente vêm de
  `SynchedEntityData` ou de packet próprio.
- **Pressão (implementada, faixa 2B):** `pressure/PressureState` (contas puras, JUnit),
  `PressureManager` (tick no servidor; nível em atributos e empuxo; `PressurePayload` ao
  próprio jogador; o afundar é um modificador do atributo `GRAVITY`), `client/pressure/PressureHud` (vinheta e batimentos), `effect/ModEffects`
  (Tônico), `block/AnchorBuoyBlock` + `item/AnchorBuoyItem` (Boia; `collapse` é o gancho para
  o Kraken destruir a plataforma). Plano original: nível 0 a 4 por jogador, atualizado por
  tick perto do Kraken (regras em [02-kraken.md](02-kraken.md)); ajusta a velocidade
  vertical e aplica os efeitos de cada nível.
- **Escudo:** campos `shield` e `maxShield` no Kraken, sincronizados com
  `SynchedEntityData`. O dano é interceptado em `hurtServer`: com escudo > 0, o
  dano vai para o escudo, multiplicado conforme a fonte (tabela em
  [02-kraken.md](02-kraken.md)). Os Nós de Maré são hitboxes filhas (como as partes do
  Ender Dragon).
- **Tentáculos (implementados, 2D-C):** `entity/TentacleEntity` e `entity/boss/KrakenAttacks`; entidades independentes com referência ao "dono" (Kraken);
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
  jogadores no cálculo, padrão 12), `naturalWalkers` (liga ou desliga o Walker), `harborAlert` (Alerta de
  Moréia da Vila), `krakenStorm` (tempestade e raios na luta) e `krakenWorldDamage` (marcas da luta no mundo;
  também precisa da regra `mob_griefing`).
- **Dados do mundo:** `SavedData` para "Kraken derrotado", "jogador já recebeu a
  Chave" e "jogador já recebeu o Coração".
- **Multiplayer:** todo cálculo de luta no servidor (aggro, Pressão, escala por
  jogadores); o cliente só renderiza e mostra HUD.
- **Desempenho:** 8 tentáculos + Crias + partículas + tempestade; testar cedo,
  já com os modelos finais.

## Ferramentas
- IntelliJ IDEA Community com o plugin Minecraft Development.
- Blockbench para modelos e texturas.
- Git + GitHub: repositório público `adrianonjunior/aquamarine` (conta `adrianonjunior`).
- Testes no jogo: `runClient` do Loom; comandos `/summon aquamarine:kraken`
  e `/kill` para iterar rápido.
- Testes automáticos: Fabric GameTests para o escudo (absorve o dano e quebra
  no zero), a escala por jogadores e a Pressão.

## Contrato de modelos e entidades (Entrega 2)
Dono desta regra: aqui. Existe para o **Claude** (lógica e texturas) e o **Codex**
(modelos e animações) trabalharem em paralelo sem pisar nos mesmos arquivos.
Divisão de papéis: [00-decisoes.md](00-decisoes.md). **"Arquivos do Codex" e "do Claude" dizem quem
edita quando os dois trabalham ao mesmo tempo; quando um agente só faz as duas partes (pedido do
Adriano), ele edita os dois lados, seguindo o mesmo contrato.**

**Método de modelagem (decidido):** o Codex escreve o modelo por **script**
(`tools/models/<criatura>/build_model.py` gera `<criatura>.bbmodel`), exporta as
classes Java e confere no jogo por screenshot. O Blockbench, se estiver
instalado, só serve para conferir e ajustar; o resultado precisa ser
reproduzível pelo script.

**Arquivos do Codex** (ninguém mais edita): `tools/models/**` e, no cliente,
`com.aquamarine.client.entity.<criatura>.*` (`<Criatura>Model`,
`<Criatura>Animations`, `<Criatura>Renderer`, estado de render).
**Arquivos do Claude:** a entidade, a IA, os dados, os sons, o resto do código e
as texturas (`tools/gen_textures.py` e `assets/aquamarine/textures/entity/**`).

**Ponte entre os dois:** a interface `com.aquamarine.entity.boss.KrakenView`
(código comum, criada pelo Codex no **primeiro commit da faixa 2A-M**, mergeada na
`main` em cerca de 30 minutos). A entidade do Claude a implementa; o renderer do
Codex só lê dela. Conteúdo mínimo:
- `AnimationState` por animação, com estes nomes: `idle`, `swim`, `emerge`,
  `stunned`, `phaseChange`, `death`, e um por aviso de ataque de
  [02-kraken.md](02-kraken.md): `slam`, `grab`, `ink`, `whirlpool`, `spawnYoung`,
  `waterJet`, `roar`, `bolts`, `ambush`, `doubleGrab`;
- `int phase()` (1 a 3) e `float shieldFraction()` (0 a 1) para rachaduras e brilho.

**Mesma regra para o tentáculo e a Cria** (faixa 2D-M): `TentacleView` e
`YoungKrakenView` seguem o formato acima, com as animações que a tabela de ataques exigir.

**Entregáveis do modelo, em caminhos fixos:** `tools/models/<criatura>/uv.png`
(gabarito de UV, entregue cedo), textura final em
`assets/aquamarine/textures/entity/<criatura>/<criatura>.png` (pintada pelo Claude
sobre o UV), classes Java no pacote do cliente acima. O renderer do Codex começa
**sem textura final**: usa o gabarito até a faixa de textura terminar.

**Mudança de contrato:** só com aval do Adriano; o novo valor entra aqui, antes do código.

**Kraken desde a Fase 6 (`0.6.0`):** o desenho do jogo sai de `tools/models/kraken_v3/build_game.py` (`KrakenV3aModel`,
`KrakenV3bModel`, `KrakenV3cModel` e as `Animations`, com os 16 clipes acima e as mesmas `AnimationState` da
`KrakenView`); `KrakenRenderer.Variant` liga cada tipo ao seu desenho. `tools/models/kraken/` guarda a v2 (fora do jogo).

## Contrato de modelos e entidades (Fase 3)
Dono desta regra: aqui. Vale a mesma divisão e o mesmo método da Entrega 2 (seção acima:
script gera o modelo, Codex nos arquivos de modelo, Claude na lógica e nas texturas);
só os nomes novos e as lições abaixo são específicos. Em execução desde 24/10/2026
([07-roadmap.md](07-roadmap.md)).

| Criatura | Faixa do modelo | View (código comum) | Animações (`AnimationState`) | Pasta e pacote |
|---|---|---|---|---|
| Moréia Tempestuosa | 3B-M1 | `com.aquamarine.entity.MorayView` | `idle`, `swim`, `strike` (Bote), `chain` (Descarga em Cadeia), `coil` (Enrolar), `stunned`, `death`; `float chargeFraction()` para as faíscas | `tools/models/moray/`, `com.aquamarine.client.entity.moray` |
| Baleia-de-Nereia | 3B-M2 | `com.aquamarine.entity.WhaleView` | `idle`, `swim`, `sing`, `surface`, `death`; `boolean saddled()` para a Sela | `tools/models/whale/`, `com.aquamarine.client.entity.whale` |
| Cachalote | 3B-M2 | `com.aquamarine.entity.SpermWhaleView` | `idle`, `swim`, `sonic` (onda sonora), `death` | `tools/models/sperm_whale/`, `com.aquamarine.client.entity.sperm_whale` |

- **Pacote das views:** `com.aquamarine.entity` (plano), como as do tentáculo, da Cria e do
  ovo. A `KrakenView` ficou em `entity.boss` por história; não mova.
- **Hitbox segmentada da Moréia** (técnica do Ender Dragon, como os Nós de Maré): o modelo
  é do Codex; as partes de colisão (cabeça, corpo, rabo com 30 de vida) são do Claude.
- Textura final: `assets/aquamarine/textures/entity/<criatura>/<criatura>.png`, pintada pelo
  Claude (3B-T) sobre o `uv.png`; o Codex copia o `uv.png` como placeholder só com aval do orquestrador.
- **Lições da Entrega 2 (todas as faixas):** (1) o `build_model.py` sobrescreve os PNGs, então
  o script de textura roda **depois** dele; (2) quem cria a entidade implementa a view e troca
  o `NoopRenderer` pelo renderer do Codex, na mesma faixa de lógica; (3) criaturas pequenas e
  escuras sob a água não leem: pense em cores claras/bioluminescentes e em enquadramento dos
  screenshots; (4) o `runClientGameTest` precisa de câmera acima do chão e de arena aquática;
  (5) o recurso "jogo" é exclusivo, e o disco C: precisa de espaço livre para builds.
- **Lições da Fase 3:** (1) o jogo só trata como multiparte o Ender Dragon; o corpo da Moréia
  são entidades comuns `MorayPartEntity` (não salvas, posição calculada da dona no cliente e no
  servidor, dano repassado com `is()` apontando para a dona); (2) o `/fill` aceita no máximo
  32.768 blocos: arenas grandes de teste vão em camadas; (3) mob criado longe do jogador some pelo
  despawn: nos testes, `setPersistenceRequired()`; (4) o `FakePlayer` da Fabric não monta nada;
  (5) cor de texto no HUD é ARGB: sem `0xFF` no alfa o texto some.
- **Lições do áudio e da luta (25/10/2026):** (1) **criatura aquática que não estende `WaterAnimal`
  precisa sobrescrever `checkSpawnObstruction`** (o padrão de `Mob` recusa nascer com o corpo em líquido:
  Moréia e baleias nunca nasciam sozinhas); (2) cada jogador tem só 5 vagas de `WATER_CREATURE` (no 26.3
  o náutilo pesa 5 no oceano) e 70 de `MONSTER` (disputadas com as cavernas embaixo do mar); Moréia e baleias
  usam `UNDERGROUND_WATER_CREATURE`, quase livre no oceano (a lula-brilhante só nasce 33 blocos abaixo do mar); (3) regras do jogo no 26.3 têm nome novo (`spawn_mobs`, `spawn_monsters`,
  `mob_griefing`); a chuva sobe 1% por tick, então "está chovendo" só vale uns 20 ticks depois:
  teste o pedido em `server.getWeatherData()`; (4) **música em camadas**: o `FabricSoundInstance.getAudioStream`
  deixa a instância fornecer o próprio `AudioStream` (o evento da `sounds.json` aponta para
  `fabric-sound-api-v1:empty` com `"stream": true`); o `JOrbisAudioStream` do jogo decodifica em float e
  respeita o fim exato do Vorbis, então camadas do mesmo tamanho voltam juntas; o canal do jogo enfileira
  4 blocos do tamanho que o fluxo devolver; (5) cores do céu, névoa, água, nuvens e luz vêm do
  `EnvironmentAttributeProbe.getValue` (atributos `visual/*`): um mixin no retorno tinge tudo de uma vez.
- **Lições do multiplayer (release 0.4.0):** (1) **montaria anda no cliente do piloto** (`isLocalClientAuthoritative`):
  tudo o que `getControllingPassenger` usa tem de estar sincronizado; a baleia decidia pelo dono, que só o servidor
  conhecia, e ninguém a movia (nem no singleplayer); agora dono e regra da config vão em `SynchedEntityData`;
  (2) servidor novo do 26.3 nasce com `white-list=true`; (3) o JEI só lê receitas do servidor integrado (no
  multiplayer as da Forja não aparecem nele, sem erro).
- **Kraken v2 e tamanho (25/10/2026):** o modelo do jogo saiu de `tools/models/kraken/build_model.py` (rodar com
  um Python que tenha Pillow; ele copia os templates de `java/`, gera o `KrakenAnimations.java` e refaz a textura):
  7 juntas por braço (`arm_N_segment_0` a `_6`; as duas últimas finas), mandíbula e bico na `jaw_hinge` e as abas
  `mantle_fin_left/right`. O `KrakenAnimations` tem **um método por clipe**: a v2 tem ~8.700 chaves, e tudo no
  inicializador estático passa dos 64 KB de bytecode por método da JVM. O tamanho mora em `KrakenStats`
  (`MODEL_SCALE` 8 e `MODEL_DROP` 5,4); renderer, partículas (`KrakenEffects`) e Nós de Maré leem dali, e o
  renderer alarga a caixa de corte para os braços erguidos não sumirem.
- **Corpo da Moréia:** `MorayEntity.PART_OFFSETS` (blocos atrás da cabeça) acompanha os segmentos
  de `tools/models/moray/build_model.py`; quem mudar um muda o outro.
- **Vila (3E):** a peça do Porto sai de `tools/gen_harbor.py` (NBT de estrutura escrito à mão);
  no 26.3 a paleta do NBT usa `id` e `properties` (minúsculas), não `Name`/`Properties`. Mergulhador
  sem XP perde a profissão antes de achar o bloco de trabalho: o NBT dá `Xp: 1`. O `/place structure`
  só põe blocos e entidades, sem registrar a estrutura (a Eclusa e o `/locate` só valem em mundo
  gerado). Trocas em `data/aquamarine/villager_trade/` (modificadores usam `type`, não `function`).
- **Vila v2 (25/10/2026):** (1) a peça precisa de `"override_liquid_settings": "ignore_waterlogging"` no
  pool: sem isso, num mundo gerado, bancos e baús saem alagados (estavam no lugar da água do mar) e
  enchem a Cúpula; (2) a decoração do oceano roda depois das estruturas e põe algas no Cais, que cortam
  as colunas de bolhas, e as colunas nem sempre começam sozinhas: `harbor/HarborElevators` refaz as
  colunas com jogador no Porto, a partir de `HarborElevators.BASES`, que tem de bater com as bases que
  o `gen_harbor.py` imprime; (3) a Torre de Vigia é um filho do jigsaw com projeção `terrain_matching`:
  o jogo a põe na altura da superfície (`WORLD_SURFACE_WG`), e o bloco jigsaw do pai fica na última
  camada da peça para o filho ser checado fora da caixa do pai; (4) as trocas por reputação são
  mixins (`VillagerMixin`, `AbstractVillagerMixin`, `MerchantContainerMixin`, `ServerPlayerMixin`) e
  o nível de cada oferta fica num attachment da Fabric (`HarborTrades.OFFER_LEVELS`); (5) água solta
  dentro de uma peça com ar (algas, que são blocos de água) vaza: plantas aquáticas só em tanque fechado.
- **Texturas das criaturas da Fase 3:** `tools/gen_creature_textures.py` pinta sobre o layout de UV
  lido do próprio `build_model.py` (rode depois dele); o da Moréia já chama o de textura no fim.
