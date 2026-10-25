# Relatório 3Q: QA da Fase 3

> **Dono de:** o resultado da faixa 3Q (QA da Fase 3) e o roteiro do aceite com jogadores reais. Feito em
> 25/10/2026 pelo agente único de fechamento (Claude Opus 5.5), com um cliente só: o que precisa de 3
> jogadores reais fica para o Adriano (lista em [07-roadmap.md](07-roadmap.md)). Aceite oficial:
> [08-fase3.md](08-fase3.md), "Aceite da Fase 3".

## Cobertura do aceite
Cada passo do ciclo do aceite e o que o prova hoje (`runClientGameTest`, salvo indicação).

| Passo do aceite | Prova | Resultado |
|---|---|---|
| Descer até 70 blocos sentindo a Pressão | `DepthGameTest`: níveis 1 a 3 por tempo a 70 m, teto 3 sem o Kraken, maior das fontes com ele | Verde |
| Usar Medidor e Regulador | `DepthGameTest`: HUD do Medidor (print 27), Regulador baixa o ambiente de 3 para 1 e gasta durabilidade | Verde |
| Derrotar uma Moréia e receber Escamas | `MorayGameTest`: 3 ataques com aviso, Descarga só na água, drops por jogador | Verde |
| Criar a Bobina, o Propulsor e a Munição de Choque | JUnit `ResourceConsistencyTest.phase3AcceptanceChainHasRecipes` (Escama → Bobina → Propulsor e ponta de Choque) e `GearGameTest` (Propulsor gasta a Bobina; Choque no Kraken) | Verde |
| Montar uma Baleia domada com 3 a bordo | `WhaleGameTest`: sem sela não monta; dono pilota; mais 2 passageiros; o quarto não cabe | Verde (passageiros são porcos: o `FakePlayer` da Fabric não monta) |
| Negociar com os Mergulhadores | `HarborGameTest`: 9 Mergulhadores, trocas de todos os níveis, nada proibido, reputação abre os níveis (cliente e servidor), troca soma reputação e reabre ofertas | Verde |
| Achar a Vila num mundo normal | `WorldgenGameTest`: `/locate structure #aquamarine:harbor` num mundo normal (semente `aquamarine`), Cúpula seca, Torre na superfície, elevadores até a superfície, Eclusa zera a Pressão, Cartógrafo vende o Mapa de Fossas | Verde (prints 37 e 38) |
| Alerta de Moréia | `HarborGameTest`: o comando toca o Sino, solta a Moréia, quem a vence ganha 2 a 4 Escamas e +50 | Verde |
| Usar tudo contra o Kraken | `GearGameTest`: veneno não pega, fogo e choque somam no escudo, o Arpão prende o tentáculo; `AttackGameTest` e testes do Kraken da Entrega 2 | Verde |
| Sem crash e sem desync com 3 jogadores | Não dá para testar com um cliente | **Só o Adriano** |

## Achados e correções (25/10/2026)
| Prioridade | Achado | Correção |
|---|---|---|
| P1 | Num mundo gerado de verdade, a Cúpula enchia de água (260 blocos): bancos e baús da peça saíam alagados porque ocupavam o lugar da água do mar, e vazavam para o ar. Os testes antigos usavam `/place` num mundo plano sem mar e nunca viram. | `"override_liquid_settings": "ignore_waterlogging"` nos pools do Porto; o `WorldgenGameTest` confere a Cúpula seca. |
| P1 | As colunas de bolhas do Cais não se formavam de forma confiável: a decoração do oceano põe algas em cima da areia das almas, e uma das colunas nem começou. | `harbor/HarborElevators` refaz as colunas (e tira as plantas) com jogador no Porto; o teste confere a coluna até a superfície. |
| P2 | Na Estufa da v1 as algas estavam soltas no ar da Cúpula; alga é bloco de água e vazaria. | Algas em tanques de vidro fechados. |
| P3 | O `runClientGameTest` cria o mundo com as configurações "consistentes" da Fabric, que **desligam as estruturas**: nenhum teste via a Vila gerada. | Parte nova `worldgen`, num segundo mundo normal com estruturas ligadas. |

## Roteiro do aceite com jogadores reais (Adriano)
Servidor dedicado ou LAN, 3 jogadores, mundo novo (na semente `aquamarine` há um Porto a uns 600 blocos
do spawn, em x -430, z -440).
1. `/locate structure #aquamarine:harbor` e ir até a Torre de Vigia; descer pelo elevador que desce (magma)
   e entrar pela Eclusa: a Pressão zera no ar.
2. Negociar: o nível na tela é o de cada jogador; as ofertas acima aparecem esgotadas. Conferir que um
   jogador com mais reputação vê mais ofertas no mesmo Mergulhador que outro.
3. Sair, descer até 70 blocos com o Medidor na mão e o Regulador na outra; sentir a Pressão.
4. Achar uma Moréia (fossas ou tempestade; ou `/aquamarine moray`), vencê-la; criar a Bobina, o Propulsor
   (Forja) e a ponta de Choque; usar o Propulsor (tecla V) e trocar a ponta (R).
5. Domar uma Baleia-de-Nereia, pôr a Sela e montar os 3; o dono pilota.
6. Noite de tempestade no Porto (`/weather thunder`, `/time set night`): o Alerta de Moréia toca o Sino.
7. Invocar o Kraken e usar tudo contra ele. Anotar: crash, desync (barra, posições, montaria), ritmo,
   dano e legibilidade dos avisos.

## Limites deste QA
Um cliente só; montaria testada no servidor com porcos; teclas R e V testadas pelo código (não pela mão);
surgimento natural de Moréias e baleias não forçado em mundo normal; sons ainda vanilla.
