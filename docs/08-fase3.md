# Fase 3: fauna nova, equipamentos de mergulho e a Vila

> ## ⛔ EXECUÇÃO BLOQUEADA (preparação liberada em 24/10/2026)
> **Preparar** (specs, contrato, prompts, checklist) está liberado. **Não inicie Tasks,
> worktrees nem agentes, e não produza código, texturas ou modelos** antes de duas
> condições: (1) a **Entrega 2 completa**, com todas as faixas marcadas em
> [07-roadmap.md](07-roadmap.md) e o aceite cumprido; (2) o **Adriano liberar a Fase 3
> explicitamente**, enviando o [prompt do orquestrador](../prompts/orquestrador-fase3.md).

> **Dono de:** a especificação de tudo que é da Fase 3. As **decisões** moram em
> [00-decisoes.md](00-decisoes.md) (linhas "Fase 3: ..."); as **faixas, ondas e
> orçamentos** em [07-roadmap.md](07-roadmap.md). Regras da luta: [02-kraken.md](02-kraken.md).
> Itens e criaturas das fases anteriores: [05-itens.md](05-itens.md) e
> [04-cidade-perdida.md](04-cidade-perdida.md). Números são **valores iniciais a testar**
> (ajuste aqui, sem nova decisão). Papéis e arquivos de cada agente:
> [06-tecnico.md](06-tecnico.md), "Contrato de modelos e entidades"; economia: [../AGENTS.md](../AGENTS.md).

## Como encaixa no que já existe
| Novo | Relação com o existente |
|---|---|
| Pressão ambiental | Mesmo efeito **Pressão** da luta ([02-kraken.md](02-kraken.md)), com uma segunda fonte. O nível aplicado é o **maior** das fontes, não a soma, e as contramedidas valem para as duas |
| Propulsor de Maré | Item **anterior ao boss**. O **Manto de Tinta** ([05-itens.md](05-itens.md)) segue como versão de luxo pós-boss |
| Munições do Arpão | Extensão do **Arpão de Oricálcio**. Os números contra o escudo entram na tabela de dano de [02-kraken.md](02-kraken.md) ao implementar (faixa 3C) |
| Moréia Tempestuosa | **Nova** criatura; a Enguia Elétrica comum da cidade continua |
| Escama Elétrica | Material novo; não substitui a Escama Antiga |
| Vila | Complementa a Cidade Perdida; vende o que as outras criam. Nunca vende Oricálcio, armaduras de boss, Arpão nem Quebra-Carapaça (regra do Walker) |

## 1. Pressão por profundidade
- **Fontes:** `ambiental` (profundidade) e `kraken` ([02-kraken.md](02-kraken.md)).
  Nível final = `max(ambiental, kraken)` após as contramedidas.
- **Pré-requisito técnico (tarefa 3A, primeira):** separar as fontes na implementação da
  Entrega 2 sem mudar o comportamento da luta; só então somar a fonte ambiental.
- **Níveis ambientais** (profundidade abaixo do nível do mar):

  | Profundidade | Nível máximo |
  |---|---|
  | 0 a 30 | 0 |
  | 30 a 50 | 1 |
  | 50 a 70 | 2 |
  | 70 ou mais | 3 |

  Sobe e desce nas mesmas regras de tempo da luta; **nunca chega a 4** sozinha
  (o 4 é exclusivo do Kraken).
- **Contramedidas:** armadura de Oricálcio e Tônico de Pressão (já existem), mais:
  - **Medidor de Profundidade** (item): mostra no HUD profundidade e nível de Pressão.
    Receita: bússola + vidro + cobre + 1 Cristal.
  - **Regulador de Pressão** (mão secundária): −2 níveis ambientais; gasta 1 de
    durabilidade por 10 s ativo (durabilidade 600). Receita: 2 Lingotes de Cobre + 1 Pérola
    + 1 Cristal + 1 Escama Antiga. Um Propulsor e um Regulador competem pela mão secundária.
- **Multiplayer:** por jogador, servidor autoritativo; nenhuma sincronização extra além do efeito.

## 2. Traje de Mergulho Profundo (armadura de tempo submerso)
- 4 peças: capacete, peitoral, calça, botas. Material: Escama Antiga, Pérola Abissal e
  Casco de Prismarinho (forja comum, bancada de armadura). Defesa 2/6/5/2.
- **Reserva de Ar:** cada peça guarda +45 s de fôlego; conjunto completo: +180 s. Funciona
  como ar extra consumido depois do ar normal; **não** dá Respiração Aquática infinita.
- **Isolamento:** −40% de dano elétrico por peça (−100% de choque de Descarga no conjunto).
- **Pressão ambiental:** −1 nível com 2 peças ou mais, −2 com o conjunto completo.
- Sem o bônus de nado do Oricálcio. Misturar peças: cada uma vale sozinha; o bônus do
  conjunto exige as 4. O Capacete aceita Fôlego Profundo (Seção 7).
- Camada `humanoid` 64x32 e item como as armaduras de [05-itens.md](05-itens.md).

## 3. Moréia Tempestuosa e a Escama Elétrica
- **Hostil, ~14 blocos de comprimento**, hitbox segmentada (técnica do Ender Dragon, como
  os Nós de Maré). Vive em fossas e em águas sob tempestade, profundidade ≥ 25.
- **Vida:** 150, escalando com `f = 1 + 0,3 × (n − 1)` ([02-kraken.md](02-kraken.md))
  para `n` jogadores num raio de 48 (máximo em 12). Dano não escala.
- **Ataques com aviso** (regra de clareza):

  | Ataque | Aviso | Efeito | Resposta |
  |---|---|---|---|
  | Bote | Recua e arqueia 1 s | 10 de dano | Desviar de lado |
  | Descarga em Cadeia | Faíscas azuis crescentes por 1,5 s | 8 de dano e Lentidão a **todos na água** num raio de 12 | Sair da água, barco, bloco seco, Isolamento |
  | Enrolar | Cauda brilha 1 s | Prende; 3 de dano por segundo, 4 s | Atacar o rabo (hitbox da cauda, 30 de vida) |
- **Fraquezas:** o rabo (dano ×1,5); fica atordoada 3 s ao tomar Choque dos seus próprios raios.
  Imune a dano elétrico.
- **Drops:** 2 + `n ÷ 2` **Escamas Elétricas** (máximo 6), afetadas por Saque, mais 40 XP.
- **Itens que a Escama cria:**

  | Item | Receita | Efeito |
  |---|---|---|
  | Bobina Voltaica | 1 Escama Elétrica + 1 Lingote de Cobre + 1 Cristal (bancada) | Bateria consumível: 1 Bobina = 6 cargas do Propulsor |
  | Chicote de Moréia | 3 Bobinas + 2 Lingotes de Oricálcio + 1 vareta (Forja Abissal) | Arma de corpo a corpo: 7 de dano, ataque rápido, alcance 3,5; **corrente** nos alvos molhados a 4 blocos (50% do dano); durabilidade 450 |

## 4. Propulsor de Maré
- **Mão secundária**; só funciona **debaixo d'água ou na superfície**. Tecla própria
  (padrão `V`, configurável): segura para mirar, solta para disparar; o servidor valida.
- **Efeito:** impulso de ~16 blocos em 1,2 s na direção do olhar; recarga de 3 s.
  Cada impulso gasta 1 carga; **1 Bobina Voltaica = 6 cargas** (guardadas no item).
- **Uso no boss:** cobre a janela de aviso dos ataques (1,2 a 2 s). Não atravessa o
  escudo do Kraken nem tira a Pressão.
- **Receita:** 1 Concha de Náutilo + 2 Lingotes de Oricálcio + 1 Cristal + 1 Bobina.
- Encantamento próprio: **Sobrecarga** (Seção 7).
- **Visual:** rastro de bolhas e luz verde-água; som de turbina abafada.

## 5. Arpão com munições
- **Aljava de Arpões** (item): guarda até 4 tipos de ponta; alterna com `R` (configurável).
  Receita: 2 couro + 3 Escamas Antigas + 1 Pérola.
- Pontas (feitas com 1 ponta simples de Oricálcio + ingrediente, bancada comum):

  | Ponta | Ingrediente | Efeito |
  |---|---|---|
  | Veneno | Olho de aranha | Veneno I por 10 s; inútil em mortos-vivos |
  | Fogo | Pó de Blaze | Queima 5 s **mesmo debaixo d'água** ("Chama Abissal"), 2 de dano por segundo |
  | Wither | Caveira de Wither esqueleto (1 por 8 pontas) | Wither I por 8 s |
  | Choque | Bobina Voltaica (1 por 4 pontas) | Descarga em cadeia nos alvos molhados a 6 blocos (50% do dano) |
- **Contra o escudo do Kraken** (a ser levada à tabela de dano ao implementar): veneno e
  wither **não afetam** o Kraken; fogo +10% do dano base; **choque +40%** do dano base
  (melhor opção); todas mantêm o dano triplo do Arpão e prendem o tentáculo por 3 s.
- Compatível com Lealdade e os encantamentos de Arpão da Seção 7.

## 6. Baleias
- **Baleia-de-Nereia** (passiva, 200 de vida, ~16 blocos, bioluminescente): nada em grupos de
  1 a 3 em águas com profundidade ≥ 40. **Canta** de longe (som e partículas); seguir o
  canto indica a Cidade Perdida (rota para a mais próxima, sem substituir a Bússola).
- **Cachalote** (neutra, 140 de vida): vive em fossas; só reage se atacada, com **onda
  sonora** que empurra e dá 6 de dano num cone de 8 blocos. Tem cicatrizes de tentáculo.
- **Montaria (só a Baleia-de-Nereia):**
  - Domar: oferecer 4 a 6 bacalhaus/salmões crus (efeito de coração, como cavalos).
  - **Sela de Baleia** (item): 4 couro + 2 Escamas Antigas + 1 Pérola. Sem sela não se monta.
  - Carrega **3 jogadores** (1 piloto + 2 passageiros); pilota na direção do olhar e
    avanço; velocidade ~9 blocos por segundo; sobe sozinha para respirar a cada 3 minutos
    (os passageiros ganham Respiração Aquática dentro de uma bolha de ar).
  - A bolha reduz a Pressão ambiental em 1 nível enquanto monta. Não atravessa a arena.
  - Cuidado em multiplayer: a baleia domada pertence a um jogador, mas **qualquer um** pode
    ser passageiro; só o dono pilota (configurável).
- **Fora da luta:** as baleias **fogem a 48 blocos do Kraken** e nunca entram na arena
  (decidido em [00-decisoes.md](00-decisoes.md)).
- Matar uma baleia selvagem **não tem consequência**; dá apenas 20 XP e couro. Baleia
  domada que morre deixa a sela.
- **Limites de servidor:** no máximo 2 grupos por chunk de 16x16 chunks; baleias domadas
  não somem sozinhas.

## 7. Encantamentos novos
| Encantamento | Níveis | Em quê | Efeito |
|---|---|---|---|
| Fôlego Profundo | 3 | Capacete | +30% de ar por nível |
| Aclimatação | 2 | Armadura | −1 nível de Pressão por nível |
| Correnteza | 3 | Calça e botas | +10% de velocidade de nado por nível |
| Isolamento | 3 | Armadura | −15% de dano elétrico por nível |
| Fisgada | 2 | Arpão | Puxa o alvo; recolhe 40% mais rápido por nível |
| Perfurante | 3 | Arpão | Atravessa +1 alvo por nível |
| Sobrecarga | 2 | Propulsor | +2 cargas por Bobina por nível |
- **Origem:** só livros em baús (Cidade, Vila) e o **Encantador de Fossa** da Vila; **não**
  aparecem na mesa de encantamento comum. O **Quebra-Carapaça** continua só em baús.
- Cada encantamento tem nome e descrição em `lang/pt_br.json` e `en_us.json`.

## 8. Vila subaquática: o Porto dos Náufragos (redesenhada)
**Ideia:** não é só um conjunto de casas: é um **porto vivo** onde a Pressão se desfaz e o
jogador se prepara. É a base multiplayer entre a superfície e a Cidade Perdida.

**Estrutura** (~80x80 blocos, 25 a 40 de profundidade, espaçada ~700 blocos; bem longe da
Cidade Perdida; peças geradas por script do Claude em `tools/`, ver [00-decisoes.md](00-decisoes.md)):
- **Eclusa** (entrada): câmara de duas portas de vidro de coral; ao passar, a **Pressão
  zera** e o jogador recebe ar. É também o refúgio contra a Moréia.
- **Cúpula Central:** praça com **Sino do Porto**, bancos e árvores de coral; sem hostis dentro.
- **Cais** (anel externo): docas, escadas até a superfície, vagas para barcos e a baleia domada.
- **Oficina** (Ferreiro de Maré), **Mercado** (Cartógrafo) e **Santuário** (Encantador de
  Fossa): cada um um prédio com 3 a 6 trabalhadores.
- **Estufa de algas:** comida e bolhas; **Dormitórios** com camas para o grupo.
- **Torre de Vigia** (acima da cúpula, na superfície): vê tempestades e a Moréia.
- Os baús da Vila são normais (cada jogador usa o seu; nada compartilhado por script).

**Habitantes:** os **Mergulhadores** (aldeões com escafandro, em skin própria sobre o modelo
vanilla), em 3 profissões novas com blocos de trabalho próprios (Bigorna de Maré, Mesa de
Cartas Náuticas, Altar de Concha). Nunca vendem Oricálcio, armaduras de boss, o Arpão
nem Quebra-Carapaça. Moeda: **esmeralda** (itens comuns) e **Pérola Abissal** (raros).

| Profissão | Vende (por nível) | Compra |
|---|---|---|
| Ferreiro de Maré | N1: Aljava, ponta de Veneno; N2: ponta de Fogo; N3: Medidor, Regulador; N4: ponta de Choque; N5: Sela de Baleia | Escama Antiga, Lingote de Cobre |
| Cartógrafo | N1: Mapa da Vila; N2: Mapa de Fossas de Moréia; N3: Bússola de Maré; N4: Mapa de Nereia | Pérola, Cristal |
| Encantador de Fossa | N1: Fôlego Profundo I; N2: Aclimatação I, Correnteza I; N3: Isolamento I, Fisgada I; N4: Perfurante I, Sobrecarga I; N5: níveis II e III | Âmbar-cinza, Bobina Voltaica, Escama Elétrica |
- **Reputação:** por jogador, sobe ao vender e ao defender a Vila; abre os níveis de comércio.

**Evento "Alerta de Moréia"** (opcional, uma vez a cada 3 dias de jogo, só em noite de
tempestade): o Sino toca; uma Moréia ataca a Cúpula; quem ajudar ganha reputação e 2 a
4 Escamas Elétricas. Falhar não destrói a Vila. Pode ser desligado na config (`harborAlert`).

## Ordem e faixas
Etapas e faixas (Claude e Codex, dependências, aceite e orçamentos) estão em
[07-roadmap.md](07-roadmap.md), seção "Fase 3". Resumo: 3A Pressão e Traje; 3B Moréia,
Baleias e Bobina; 3C Munições e encantamentos; 3D Propulsor; 3E Vila; 3Q QA final.

## Aceite da Fase 3
Um grupo de 3 jogadores, num servidor de teste, faz o ciclo completo: desce até 70 blocos
sentindo a Pressão, usa Medidor e Regulador, derrota uma Moréia e recebe Escamas, cria a
Bobina, o Propulsor e a Munição de Choque, monta uma Baleia domada, negocia com os
Mergulhadores e usa tudo contra o Kraken, sem crash e sem desync.
