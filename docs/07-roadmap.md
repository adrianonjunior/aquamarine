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
- [ ] Pacotes e registros: `ModItems`, `ModBlocks`, aba criativa "Aquamarine",
  carregamento de `lang/pt_br.json` e `en_us.json`, tags, config
  `config/aquamarine.json` (ver [06-tecnico.md](06-tecnico.md)).
- [ ] Pasta `tools/` com scripts Python que **geram as texturas** (pixel art,
  paleta do mod) para poderem ser regeradas.
- [ ] GameTest ou teste mínimo rodando no `gradlew build`; CI do GitHub continua verde.

**B. Materiais (Marco 2) e Forja Abissal**
- [ ] Minério de Oricálcio (**sem gerar no mundo**; só creative e comandos),
  lingote, bloco, ferramentas, armadura com bônus de conjunto, Forja Abissal e
  receitas, conforme [05-itens.md](05-itens.md). Pérola Abissal e Cristal de
  Maré como itens. Texturas em pixel art.

**C. Óculos de Mergulho funcionando** (spec em [05-itens.md](05-itens.md)): item
de cabeça com textura e modelo na cabeça, receita e redução de névoa debaixo d'água.

**D. Se sobrar orçamento (extra)**
- [ ] Barra de vida customizada (HUD cliente) funcionando com uma entidade de
  teste oculta, com faixa de escudo e rachaduras (spec em [02-kraken.md](02-kraken.md)).
- [ ] Chamariz Abissal, Altar e as mensagens de invocação no chat.

**Critério de "algo legal"**: ao abrir o jogo, a aba criativa "Aquamarine"
mostra os itens com texturas próprias, dá para craftar e vestir a armadura e os
óculos, e a Forja Abissal funciona.
