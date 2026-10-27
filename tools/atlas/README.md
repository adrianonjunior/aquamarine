# Atlas técnico Aquamarine

Portal local em HTML: modelos completos em 3D, animações, áudio, texturas e documentos. Nenhum Java,
modelo ou textura consumido pelo jogo é modificado pelo site. A proposta v2 do Kraken nasceu aqui e foi
**integrada ao jogo** em 25/10/2026 (a pedido do Adriano): o site agora mostra o rig de produção.

## Abrir

Na raiz do projeto, em PowerShell:

```powershell
.\tools\atlas\abrir.ps1
```

O atalho gera o catálogo, inicia o servidor local em segundo plano (sem janelas de terminal) e abre
`http://127.0.0.1:8766`. O botão **Atualizar catálogo**, na biblioteca sonora, relê os recursos.
O áudio gerado (`build/audio`: prévias, espectrogramas e comparações) fica na própria pasta do projeto; para
ler outra pasta de áudio, use `-AudioRoot caminho`. Outra porta: `-Port 8767`. Sem abrir navegador: `-SemAbrir`.

Também funciona abrindo **`build/atlas/index.html` diretamente**, sem internet ou servidor. Esse é
um snapshot: para atualizar, rode o atalho de novo. Pode copiar a pasta `build/atlas` inteira.
Somente a primeira instalação precisa de internet (npm). Depois, todos os scripts, texturas e fontes
são locais. O build usa Python com Pillow, Node e as dependências fixadas no lockfile.

## Usar

- Escolha criatura à esquerda e movimento à direita. O Kraken abre no rig de produção (a v2 integrada).
- **Máquinas** (0.6.1): blocos com modelo e animação próprios (o Gerador de Fenda é o primeiro), com o mesmo rig do
  jogo, UV por face, vidro translúcido e os botões **Parada**, **Funcionando** e **Sobrecarga**, que tocam a sequência
  do jogo (ligar, trabalhar, desligar) com a mesma mistura de clipes. Dados: `catalog.machines()` lê o `rig.json` de
  `tools/models/machines/<máquina>/`.
- **Modelo + animações** alterna versões preservadas. Nenhuma seleção altera o mod.
- Arraste para girar, use a roda para zoom e o botão direito para mover. No celular: um dedo gira,
  pinça aproxima, dois dedos movem. Com foco no canvas: setas giram, `+`/`-` aproximam, `F` enquadra,
  espaço pausa. Enquadrar considera toda a animação para não cortar tentáculos.
- A linha do tempo permite pausar uma pose; há velocidade, repetição e marcador de fim do aviso.
- Os players de áudio mostram duração e posição, deixam arrastar até um trecho e têm saltos de 15 s.
  Os servidores local e público aceitam pedidos HTTP Range, para o navegador buscar bytes do ponto
  solicitado sem carregar o arquivo desde o começo. `preload=metadata` carrega a duração ao abrir.
- Textura, UV, volume neutro, malha, ossos, grade e Sela ajudam na inspeção. A camada contínua do
  Kraken é opcional e corresponde à fase 1. Ela é separada dos clipes.
- Áudio reúne o manifesto e as comparações MusicGen da prévia original, incluindo medidas,
  espectrogramas, licença e download. Laços usam a audição de 3 voltas quando ela existe; caso
  contrário o site informa que o arquivo tem sobra e o player não simula o crossfade.
- Texturas têm busca, filtro por pasta e PNG original. Documentação é uma cópia legível dos `.md`.

## Histórico permanente de modelos

`versions/registry.json` registra cada versão; os JSONs guardam **rig, cubos, textura e keyframes**.
Os modelos atuais são v1, menos o Kraken, que no jogo já é a v2; a v1 dele segue no registro. O seletor lê o
registro completo.
**Nunca sobrescreva uma versão publicada.** Exporte o JSON pelo site, atribua um novo `version` e registre:

```powershell
python tools/atlas/register_version.py --source caminho/kraken-v3.json --label 'Proposta v3 · só no site'
```

O comando recusa IDs já existentes. Regerar o site não modifica esses snapshots. O estudo v2 original
continua em `../models/kraken/kraken_preview_v2.py` (movimentos) e `catalog.py::kraken_proposal` (estrutura),
mas o gerador do jogo agora tem a v2 dele mesmo (`kraken_animations.py`: `refine` e `rig_tracks`;
`build_model.py`: juntas 5 e 6, `jaw_hinge` e abas). Quando o rig de produção já tem `jaw_hinge`, o catálogo usa
o rig e os clipes de produção e não monta proposta. Uma v3 futura se registra sem apagar v1 e v2.
`reference_v1.json` preserva os clipes do Kraken na data desta entrega.

**Candidatas da Fase 6 (6A1):** `v3a` (Lula-colosso), `v3b` (Vampiro-abissal) e `v3c` (Amonita-colosso) saem de
`../models/kraken_v3/build_candidates.py --register` e ficam só no site até o Adriano escolher. Os snapshots podem
trazer três campos opcionais que o visualizador usa: `glow` (camada de brilho, vira luz própria no modo Textura), `uv`
(gabarito da própria versão) e `notes` (descrição). Para comparar por imagem: `node tools/atlas/tests/candidates.mjs v2 v3a
v3b v3c` grava vistas de frente, lado, topo e costas em `build/atlas-qa/` (`ATLAS_POSES=idle:0,slam:0.7` escolhe os quadros).
**No jogo desde a 0.6.0:** a escolha do Adriano foi a v3a como `aquamarine:kraken`, com a v3b e a v3c como
`kraken_vampire` e `kraken_ammonite`. `../models/kraken_v3/build_game.py --register` gravou as versões `v3a-jogo`,
`v3b-jogo` e `v3c-jogo`, com os 16 clipes da luta; o rótulo começa com "No jogo", e o site abre nessa versão e mostra
"NO JOGO" no palco. As candidatas de 6 clipes e a v2 ficam no histórico.

## Gerar e validar

```powershell
npm ci --prefix tools/atlas --no-audit --no-fund
python tools/atlas/build.py
python tools/atlas/tests/test_catalog.py
node tools/atlas/tests/browser.mjs
```

O teste de navegador usa Edge instalado, em modo headless. `ATLAS_URL` permite testar a mesma página
via HTTP; por padrão testa `file://`. Evidências em `build/atlas-qa/` (não versionadas).
Ele percorre todas as versões registradas: são 17 versões e 140 clipes, incluindo os
16 originais do Kraken e a v2 do Tentáculo, da Cria e do Ovo (Fase 4). Revisão visual e proveniência em [DESIGN.md](DESIGN.md).
O build falha em formatos de animação Java desconhecidos para não exibir silenciosamente dados incompletos.
Não é necessário compilar o mod para trabalhar no Atlas.

## Proveniência e limites

- Texturas e mapas UV são cópias dos recursos do próprio projeto; não foram gerados PNGs novos.
- Rig do Kraken: caixas do `.bbmodel`, pose radial e UV cúbico do Java. Baleias/Moréia: geradores do
  projeto. Tentáculo, Cria e Ovo: o `rig.json` que o gerador do jogo (`tools/models/spawnlings/build_model.py`)
  grava junto com o Java. Clipes atuais vêm do Java ou do
  gerador do Kraken, não das aproximações presentes em alguns `.bbmodel` antigos.
- A v2 acrescenta mandíbula, abas e duas juntas por tentáculo, reaproveitando as ilhas da textura
  existente. Ajustes de textura podem ser feitos depois do aceite da estrutura.
- Partículas, IA, deslocamento da entidade, escala do renderer, iluminação e efeitos de combate não
  são reproduzidos. A grade usa pixels do modelo, antes da escala de mundo.
- Mergulhadores são aldeões vanilla; dummy e partes de colisão não têm rig visual próprio. Estão
  documentados, sem inventar modelos ou animações para eles.
- Fontes: [Chakra Petch, Google Fonts](https://github.com/google/fonts/tree/main/ofl/chakrapetch),
  licença OFL em `fonts/OFL.txt`. Three.js 0.186.1, MIT; licença copiada no site.
- O servidor só escuta em `127.0.0.1`. Logs e PID ficam em `build/atlas-server.*`.
