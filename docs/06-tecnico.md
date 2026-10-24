# Notas técnicas

> **Status:** rascunho de implementação. Barra de boss customizada e efeito de Pressão = **decididos**; o resto = abordagem sugerida.

## Base
- Minecraft Java 26.3 "Wilderness Bound" (15/09/2026), Fabric Loader +
  Fabric API, Java 25, Gradle 9.x, plugin `net.fabricmc.fabric-loom`.
- Desde o 26.1 o jogo não é ofuscado: nomes oficiais da Mojang, Yarn não é
  mais usado. Dependências com `implementation`, mod depende de `fabric-api`.
- **Pendente:** instalar JDK 25 e rodar `gradlew build` / `gradlew runClient`
  pela primeira vez.
- Esqueleto criado a partir do `fabric-example-mod` oficial (mod id
  `aquamarine`, pacote `com.aquamarine`), já no repositório privado
  `adrianonjunior/aquamarine`. Versões: loader 0.20.1, Fabric API
  0.165.0+26.3, Loom 1.19-SNAPSHOT, Gradle 9.8.2.
- **JDK 25 obrigatório** (a máquina tinha só o 21 em 24/10/2026).

## Estrutura de pacotes planejada
```
com.aquamarine
  entity/kraken   boss, tentáculos, fases, escudo
  event           invocação e mensagens no chat
  item            arpão, carga de pulso
  block           Pilar Ressonante, Forja Abissal
  worldgen        Cidade Perdida, depois o bioma
```

## Como fazer cada parte (rascunho)
- **Barra de boss:** customizada (decisão do Adriano). O servidor continua
  usando `ServerBossEvent` para controlar quem vê o boss; o cliente cancela a
  barra vanilla desse evento (mixin no renderer de boss bar ou callback da
  Fabric API de HUD) e desenha a barra própria com `GuiGraphics`. Escudo,
  fase e dano recente vêm de `SynchedEntityData`/packet próprio.
- **Pressão (peso):** efeito de status próprio aplicado por tick perto do
  Kraken; ajusta a velocidade vertical do jogador e o nível sobe com o tempo.
- **Escudo:** campo no Kraken (`shield`, `maxShield`), sincronizado com
  `SynchedEntityData`. Interceptar o dano em `hurtServer`: se escudo > 0,
  o dano vai para o escudo (multiplicado conforme a arma).
- **Tentáculos:** entidades filhas ligadas ao Kraken (como as partes do
  Ender Dragon, ou entidades independentes com referência ao "dono").
  Começar com entidades independentes, é mais fácil de testar.
- **Animações:** GeckoLib + Blockbench, se houver versão para 26.3. Se não,
  modelo vanilla (`EntityModel`) com animação simples primeiro.
- **Tags de arma:** uma tag de item `aquamarine:shield_breakers` para dizer
  quais itens dão dano cheio no escudo. Fácil de ajustar e de outros mods
  usarem.
- **Config:** um arquivo simples (ex.: JSON próprio ou biblioteca de config
  quando definirmos) para aparição natural, aviso falso, dificuldade.
- **Dados do mundo:** `SavedData` para "Kraken já derrotado" e "jogador já
  entrou na cidade".

## Ferramentas
- IntelliJ IDEA Community com o plugin Minecraft Development.
- Blockbench para modelos e texturas.
- Git + GitHub (quando o repositório estiver pronto).
- Testes no jogo: `runClient` do Loom; comandos `/summon aquamarine:kraken`
  e `/kill` para iterar rápido.
- Testes automáticos: Fabric tem GameTests; dá para testar coisas simples
  (escudo absorve dano, quebra no zero).
