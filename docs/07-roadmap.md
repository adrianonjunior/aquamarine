# Roadmap

> **Dono de:** ordem dos marcos e se cada um foi concluído. Não tem donos nem
> tarefas em andamento (isso fica em [../CONTEXTO.md](../CONTEXTO.md)). Marque o
> checkbox aqui quando um marco terminar.

- [x] Repositório privado com esqueleto Fabric 26.3.
- [x] JDK 25 instalado; build e `runClient` funcionam.
- [ ] Ciclo de screenshot: print no jogo (F2) lido de `run/screenshots`.
- [ ] **Marco 2, Materiais:** minério de Oricálcio, lingote, ferramentas,
  armadura, aba no criativo, receitas, texturas.
- [ ] **Kraken v1 jogável**, já com modelo de alta qualidade (decisão do
  Adriano), barra customizada, escudo e efeito de Pressão, antes da cidade, para
  sentir a luta em multiplayer.
- [ ] **Cidade Perdida v1** (estrutura jigsaw).
- [ ] Kraken completo, depois bioma e polimento.

A ordem do plano oficial põe a cidade antes do Kraken. Aqui o Kraken v1 vem
antes porque é a parte mais arriscada e a mais divertida de ajustar.

## Entrega 1: base estruturada + algo jogável (agente Estruturador)
Liberada para implementação em 24/10/2026. Objetivo: deixar a base do projeto
pronta para vários agentes trabalharem e entregar **algo legal e jogável**
no fim. Itens em ordem de prioridade; cada um termina com build verde,
commit e linha no handoff do `CONTEXTO.md`.

**A. Estrutura (obrigatório, primeiro)**
- [x] Pacotes e registros: `ModItems`, `ModBlocks`, aba criativa "Aquamarine",
  carregamento de `lang/pt_br.json` e `en_us.json`, tags, config
  `config/aquamarine.json` (ver [06-tecnico.md](06-tecnico.md)).
- [x] Pasta `tools/` com scripts Python que **geram as texturas** (pixel art,
  paleta do mod) para poderem ser regeradas.
- [x] GameTest ou teste mínimo rodando no `gradlew build`; CI do GitHub continua verde.

**B. Materiais (Marco 2) e Forja Abissal**
- [x] Minério de Oricálcio (**sem gerar no mundo**; só creative e comandos),
  lingote, bloco, ferramentas, armadura com bônus de conjunto, Forja Abissal e
  receitas, conforme [05-itens.md](05-itens.md). Pérola Abissal e Cristal de
  Maré como itens. Texturas em pixel art.

**C. Óculos de Mergulho funcionando** ✅ (spec em [05-itens.md](05-itens.md)): item
de cabeça com textura e modelo na cabeça, receita e redução de névoa debaixo d'água.

**D. Se sobrar orçamento (extra)**
- [x] Barra de vida customizada (HUD cliente) funcionando com uma entidade de
  teste oculta, com faixa de escudo e rachaduras (spec em [02-kraken.md](02-kraken.md)).
- [x] Chamariz Abissal, Altar e as mensagens de invocação no chat.

**Critério de "algo legal"**: ao abrir o jogo, a aba criativa "Aquamarine"
mostra os itens com texturas próprias, dá para craftar e vestir a armadura e os
óculos, e a Forja Abissal funciona.

**Concluída em 24/10/2026** (A, B, C e D). Conferida no jogo pelo teste de cliente
(`./gradlew runClientGameTest`, screenshots em `build/run/clientGameTest/screenshots/`).
Pendências e próximos passos: `CONTEXTO.md`, registro de handoff.

## Entrega 2: Kraken v1 jogável (preparada, **NÃO liberada**)
Só vale depois de o Adriano liberar explicitamente (ver [../AGENTS.md](../AGENTS.md)).
Objetivo: lutar contra o Kraken inteiro em multiplayer, sem a cidade: o teste
usa um comando de depuração e uma arena de teste, e a invocação real (Altar)
já chama o mesmo gancho (`KrakenSummoning.spawnKraken`). Regras da luta:
[02-kraken.md](02-kraken.md); nada aqui as repete.

Faixas independentes, uma por agente, cada uma em branch própria. A ordem
abaixo é a de dependência: **2A vem primeiro**; 2B e 2C podem rodar em paralelo
depois que o 2A fizer merge; 2D e 2E dependem do 2A.

- [ ] **2A. Kraken base:** entidade com vida/escudo por fase, escala por
  jogadores (`f`), transição de fase, aggro, abandono e fuga; modelo de alta
  qualidade com animações (Blockbench → `EntityModel` + `AnimationDefinition`,
  ver [06-tecnico.md](06-tecnico.md)); ligação com a barra de boss que já existe
  (substitui a entidade de teste); comando de depuração `/aquamarine kraken`
  (invocar, definir fase, matar) e arena de teste montada por GameTest.
- [ ] **2B. Pressão e peso:** efeito por jogador (níveis 0-4), Tônico de Pressão,
  Boia de Âncora, integração com `OrichalcumEffects`, vinheta e som no cliente.
- [ ] **2C. Escudo:** Carapaça Abissal, Nós de Maré (hitboxes), tabela de dano
  por fonte, Arpão, Carga de Pulso, Pilar, Quebra-Carapaça, atordoamento.
- [ ] **2D. Tentáculos e ataques:** os 8 tentáculos e a tabela de ataques com
  avisos visuais e sonoros, por fase.
- [ ] **2E. Extras da invocação e recompensas:** gatilho do aviso falso, tremor
  de tela, silêncio do oceano, drops, troféu e primeira vitória.

**Critério de aceite:** num servidor dedicado de teste (ou `runClient` com
2 clientes), 2+ jogadores invocam o Kraken, enfrentam as 3 fases e vencem em
8 a 12 minutos com equipamento do mod; nenhum crash, nenhum desync da barra.
Pontos que **nenhum agente consegue verificar sozinho** (ritmo, sensação de
dano) vão para o handoff como "a testar pelo Adriano".
