# Áudio: ambientação, efeitos e música

> **Dono de:** a direção sonora e a lista de tudo que tem som no mod, o método de produção e
> as regras de integração. **Decisões** em [00-decisoes.md](00-decisoes.md) (linha "Áudio");
> **faixas e lotes** em [07-roadmap.md](07-roadmap.md), "Áudio v1"; papéis e economia em
> [../AGENTS.md](../AGENTS.md). Números são valores iniciais a testar. Quem produz segue o
> prompt [../prompts/audio.md](../prompts/audio.md).

## Direção sonora (estilo Calamity, original)
O mod alterna **opressão** e **acolhimento**, como as trilhas de Calamity (Terraria): o
oceano é lindo e perigoso, e o som é o que avisa a diferença.
- **Palavras-chave:** pressão, vazio, grandiosidade, beleza bioluminescente, perigo
  épico (coral, drones graves, percussão profunda, sinos de vidro, ruído de casco de metal).
- **Acolhimento** (superfície de dia, Vila): harmonias abertas (lídio/dórico), vidro,
  marimba suave, bolhas, madeira; **dread** (profundezas, noite, Kraken): frígio/menor
  com semitons, drones, coro sintético, graves que se sentem no peito.
- **Dinâmica por camadas:** o som do mundo **responde ao jogador**: profundidade, nível de
  Pressão, hora do dia, tempestade, perto de estrutura, em luta.
- **Tudo original:** nada de amostras de jogos, filmes ou bancos sem licença livre; nenhuma
  melodia copiada de Calamity ou de outro jogo.
- Arquivos `.ogg` com **nomes fixos**: o Adriano pode trocar qualquer um depois (como as texturas).

## Ambiência: camadas contínuas (cliente)
Camadas em loop com fade, controladas por um `AmbienceController` no cliente (sem rede).

| Camada | Quando | Caráter |
|---|---|---|
| Superfície calma | Na superfície ou a menos de 10 blocos, de dia | Ondas, vento, gaivotas distantes; **conforto** |
| Maré cinzenta | Superfície à noite ou em tempestade | Vento grave, ondas pesadas, trovão longe |
| Mergulho raso | Debaixo d'água até 30 blocos | Abafado, bolhas, luz que "canta" |
| Profundeza 1 a 3 | Nível ambiental da Pressão 1 a 3 | Drone grave que sobe, rangidos de casco, gemidos longínquos |
| Coração da Pressão | Pressão ≥ 3 | Batimento lento e baixo, filtro abafado |
| Fossa de Nereia | Dentro do bioma | Brilho cristalino, canto de baleia distante, sinos de cristal |
| Cidade Perdida | Dentro da estrutura | Reverb de ruína, gotas, sino distante, sussurros de coro |
| Bolsão de ar | Câmara de ar da cidade | Silêncio abafado e respiração da cúpula |
| Vila / Porto dos Náufragos | Dentro da Vila | Calor: bolhas, madeira, harpa de vidro, Sino do Porto |
| Eclusa | Ao passar a eclusa | Vazão e chiado de ar; a Pressão cede |
| Kraken: tempestade | Em luta (fases 1 a 3) | Vento, trovão, mar revolto, cresce por fase |

Também: **sons de humor** (estalos, ecos curtos aleatórios) por bioma, e o **silêncio do
oceano** da invocação ([03-invocacao-e-chat.md](03-invocacao-e-chat.md)) continua barrando as camadas novas.

## Música (decidida; só ambiente e temas, sem copiar ninguém)
Reproduzida pelo cliente por situação. Cada faixa é um loop ou peça de 2 a 3 minutos;
depois de tocar, silêncio de 1 a 3 minutos (padrão do jogo).

| Tema | Onde | Caráter |
|---|---|---|
| Superfície | Mar de dia | Aberto, vidro e cordas suaves |
| Maré Cinzenta | Noite e tempestade | Tensão baixa, drones, ondas sonoras |
| Profundezas | Pressão ≥ 2 | Escuro, grave, coro distante |
| Fossa de Nereia | Bioma | Bioluminescente, cristal, etéreo |
| Cidade Perdida | Estrutura | Melancólico e imponente, coro sintético, sinos |
| Porto dos Náufragos | Vila | Caloroso, seguro, harpa de vidro |
| Kraken: Tormenta | Fase 1 | Marcha surda, ostinato, motivo do Kraken; o breakcore abre o filtro; trombetas no fim |
| Kraken: Fúria | Fase 2 | Graves duplos, coro gritando, breakcore inteiro, tema heroico das trombetas |
| Kraken: Abismo | Fase 3 | Riff distorcido, sinos de catedral, breakcore no máximo; vira ré maior com o tema do Porto |
| Invocação | Sequência de 10 s | Silêncio e um acorde que cresce (stinger) |
| Vitória | Kraken derrotado | Stinger de alívio e glória |
| Retorno | "O Kraken retorna às profundezas..." | Stinger curto e frio |

A luta usa **camadas sincronizadas** (base + breakcore + glória) que entram pelo estado da luta e uma
trilha por fase; o tema anterior cai com fade sob o golpe da troca, nunca corta seco. Detalhes em
"Luta do Kraken", abaixo.

## Efeitos (lista de substituição dos sons vanilla)
Hoje o código usa sons do jogo no lugar destes; o produtor faz um **audit** (`grep
"SoundEvents\."` em `src/`) e troca cada um por um evento do mod, mantendo a lista.
- **Kraken:** rugido (3 intensidades), pancada, agarrão (guincho agudo), nuvem de tinta,
  redemoinho, jato de água, rugido abissal, raios guiados, emboscada, estilhaço e
  quebra da Carapaça (vidro grave), Nós de Maré, atordoamento, troca de fase, morte.
- **Crias, ovos e tentáculos:** chiado, estalo, deslizar, corte.
- **Moréia:** crepitar elétrico, bote, descarga em cadeia, enrolar.
- **Baleias:** canto longo (2 variantes), respiração na superfície, cachalote (cliques e
  onda sonora), sela e domar.
- **Walker:** gemido lento e arrastar de passos, em tom triste, não agressivo.
- **Criaturas da cidade (planejadas):** Sentinela, Sereia (canto), Enguia, Caranguejo
  Ermitão, Cria. Os arquivos já entram; os eventos ficam prontos para quando existirem.
- **Itens e blocos:** Forja Abissal, Altar, Chamariz, Óculos (por/tirar), Propulsor
  (carregar e disparar), Arpão (cada ponta), Bobina, Medidor, Regulador, Boia de Âncora,
  Tônico, Sela, armadura Abissal e Traje ao equipar.
- **Interface:** quebra de escudo na barra, mensagens da invocação, conquista de troféu,
  comércio da Vila, Sino do Porto, Alerta de Moréia.
- Cada som frequente tem **3 variações** (mais pitch aleatório na `sounds.json`).

## Produção (local e grátis: decidido)
Máquina do Adriano: RTX 3050 (6 GB), ffmpeg instalado, Python 3.14 sem scipy.
1. **Síntese por script** (`tools/audio/`): osciladores, FM, subtrativa, ruído filtrado,
   envelopes, reverb por resposta ao impulso gerada, coro por filtros de formante, percussão
   sintética, sequenciamento de notas em código. Reproduzível por semente.
2. **IA local opcional**, só para textura e matéria-prima (AudioGen/MusicGen pequenos,
   Stable Audio Open), se couber na GPU; clipes curtos, **sempre curados e pós-processados**.
   Conferir a licença dos pesos antes de redistribuir modelos ou áudio gerado por IA.
3. **Skills externas** (ex.: `build-sfx`, `audiocraft-audio-generation`): ler o `SKILL.md` e
   o código antes de instalar; só instalar se funcionar offline e sem custo.
4. **Pós-produção com ffmpeg:** EQ, compressão, reverb por convolução, loop sem emenda
   (crossfade ≥ 1 s), normalização (`loudnorm`), codificação `.ogg`.
- **Os agentes não ouvem.** A verificação automática é: loudness (`ebur128`), pico (≤ −1 dBFS),
  ausência de clipping e de silêncio longo, emenda do loop e **espectrograma** (imagem que
  o agente lê). A qualidade subjetiva é **do Adriano**, em audições com portões (abaixo).
  Um gerador de prévia (`build/audio-preview/index.html`, local) lista tudo com players.

## Pipeline (lote A0, 25/10/2026)
Em `tools/audio/` (ambientes Python locais em `tools/audio/.venv*`, fora do git):
- `dsp.py`: a biblioteca de síntese (osciladores sem aliasing, ruídos, filtros fixos e variáveis,
  formantes de voz e coro, síntese modal de sinos, vidro, marimba e casco, Karplus-Strong, bolhas de
  Minnaert, reverb por convolução com IR gerada, compressor, limitador e camadas **circulares** para
  laços perfeitos).
- `master.py`: alvo de loudness por tipo, pico real, `.ogg`, medidas (`ebur128`), espectrograma,
  emenda do laço e `manifest.json` (script, semente, medidas e licença de cada som).
- `a0.py`: as 6 amostras da prova de qualidade; `preview.py --open`: a prévia em
  `build/audio-preview/index.html`.
- `musicgen_alt.py` + `alt_finish.py`: peças de IA local (MusicGen estéreo pequeno, na GPU) só para
  comparar; não entram no mod sem aval.
- Rodar: `tools/audio/.venv/Scripts/python tools/audio/a0.py` (todas) ou com nomes (`roar harbor`).

## Luta do Kraken (entregue em 25/10/2026)
Pedido do Adriano depois de aprovar o A0 (decisão em [00-decisoes.md](00-decisoes.md), "Áudio: luta do Kraken").
Tudo sintetizado por `tools/audio/instruments.py` (instrumentos), `kraken_music.py` (trilhas e stingers) e
`kraken_sfx.py` (efeitos; também escreve `assets/aquamarine/sounds.json`).

**Trilhas** (ré menor com o mi bemol frígio do Kraken; uma por fase, em laço): Tormenta (160 bpm, 56
compassos, 84 s), Fúria (172 bpm, 64 compassos, 89 s) e Abismo (180 bpm, 72 compassos, 96 s). Cada uma tem
**três camadas** com o mesmo número de amostras (`sounds/music/kraken/<trilha>_<camada>.ogg`):
- `base`: orquestra (cordas em ostinato, metais graves com o **motivo do Kraken**, coro), **sinos** (catedral,
  tubulares e os sinos de vidro do Porto), baixo, taikos, tímpanos, impactos e a tempestade de fundo;
- `breaks`: **breakcore** com bateria **sintetizada** (bumbo, caixa, chimbal, condução; padrões próprios, nada
  de amostra), picotada em 16 avos: gagueiras, trechos invertidos, mudança de tom, parada de fita e esmagado;
- `glory`: **trombetas** (naipe de 3, com o "bico" do lábio e o brilho que abre com a força), trompas e coro
  heroico. No Abismo, as trombetas tocam o tema do Porto em ré maior: a esperança contra o abismo.

**Como toca** (cliente; regras em `sound/KrakenMusicPlan`, controle em `client/music/KrakenMusic`):
| Situação | O que acontece |
|---|---|
| Invocação (silêncio do oceano) | Stinger da invocação (lote A0) |
| Kraken vivo a até 72 blocos | Entra a trilha da fase atual; a música normal do jogo fica calada |
| Troca de fase | A trilha cai em 0,5 s, toca o golpe de transição (3,6 s) e a trilha nova entra no fim da subida |
| 12 s sem combate (fases 1 e 2) | **Respiro**: o breakcore sai; volta no próximo golpe, dano ou ataque |
| Glória | Fase 1: 35%, fase 2: 60%, fase 3: 85%; +30% abaixo da metade da vida da fase; **100% com a Carapaça quebrada ou o jogador com menos de 30% de vida** |
| Kraken morre | A trilha cai e toca a **vitória** (fanfarra, tema do Porto, sinos repicando); céu dourado |
| Kraken some vivo por perto | Stinger do **recuo** ("retorna às profundezas") |
| Jogador morre ou passa de 112 blocos | A trilha some em 3 s; volta ao renascer perto |

**Implementação:** cada trilha é um `LayeredMusicInstance` (categoria Música) cujo evento aponta para o som
vazio da Fabric em fluxo; o `LayeredMusicStream` decodifica as três camadas juntas e soma com os ganhos ao vivo.
Camadas em canais separados do jogo não serviriam: cada canal começa dezenas de ms depois do outro e o
breakcore sairia do tempo da orquestra. O laço é **nativo** (sem a sobra de 2 s): as camadas voltam ao
começo na mesma amostra, e a emenda cai num impacto. Blocos de 0,5 s: mudança de camada chega em até ~2 s;
os fades imediatos usam o volume da instância.

**Efeitos** (eventos em `sound/ModSounds`, arquivos e variações em `sounds.json`, legendas `subtitles.aquamarine.*`):
Kraken (ronco, ferido, morte, rugido, emergir, recuar, aviso e golpe de cada ataque, onda de choque, raios,
erupção, Carapaça rachando, partindo e se refazendo), tentáculos (ferido, cortado), Crias (chiado, ferida,
morte) e ovo chocando. Os sons vanilla do Kraken saíram do código.

## Padrões técnicos
- Formato `.ogg` (Vorbis). **Efeitos posicionais em mono**; música e camadas globais em estéreo.
- **Laços** (camadas de ambiência e temas simples): a mixagem é circular e o `.ogg` leva **2 s a mais**, iguais ao começo;
  o tocador do jogo começa a próxima volta 2 s antes do fim e cruza esse trecho (as bordas do
  Vorbis estragam um laço "colado"). O manifesto guarda `loop_overlap_s`.
- Alvos: ambiência −26 a −24 LUFS; efeitos com pico em −3 dBFS; música −18 LUFS integrado.
- **Orçamento total de áudio:** até 70 MB. Efeitos q4; música q3.
- Registro: `ModSounds` (eventos), `assets/aquamarine/sounds.json`, `subtitles.*` em
  `lang/pt_br.json` e `en_us.json`. Arquivos em `assets/aquamarine/sounds/<categoria>/`.
- Configuração em `config/aquamarine.json`: `ambienceVolume`, `musicVolume`, `ambienceEnabled`,
  `musicEnabled`. Respeitam as categorias de volume do jogo.
- **Multiplayer:** música e camadas são **só do cliente** (nada na rede); efeitos de criaturas e
  ataques saem do servidor pelo mecanismo de som do jogo. O silêncio da invocação vale para todos.
- Gatilhos de música no cliente (por bioma, profundidade, Pressão, luta): o produtor
  verifica a API do 26.3 e prefere o mecanismo nativo do jogo; se não servir, uma
  `SoundInstance` própria com fade. O resultado é documentado em [06-tecnico.md](06-tecnico.md).

## Portões de audição do Adriano
1. **Prova de qualidade (antes de produzir em massa):** 6 amostras (1 ambiente, 1 efeito de
   criatura, 1 do Kraken, 1 de item, 1 tema de música, 1 stinger). Só segue com o aval.
2. **Lote de ambiência e efeitos:** prévia em `build/audio-preview/`.
3. **Lote de música:** prévia com todos os temas.
4. **Aceite no jogo:** o Adriano caminha pelo mundo e luta contra o Kraken.
Cada portão termina com "aprovado" ou **lista de ajustes** do Adriano.
