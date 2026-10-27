# Onda 3: Bomba, Tanque e fauna

**Estado em 27/10/2026:** retomada pelo Codex, com autorização para assumir as quatro worktrees anteriores. Faixas: 7D, 7G e 7H. Coordenação e base Git: [LEIA-ME.md](LEIA-ME.md). Especificação: [12-industria.md](../12-industria.md).

## Retomada às 12h15

Estados herdados preservados: hidráulica `4ae57fd`, ostra `ff6954c`, caranguejo `c9f4617`, arte hidráulica `b97dd13`. Reunidos em `codex/industria` até `aa6f150`; nenhum desses checkpoints representa faixa pronta. Outras worktrees preservadas até a integração final.

Responsabilidade: completar código, recursos, renderizadores, modelos e testes das cinco faixas autorizadas. Orçamento desta retomada: 3 h para onda 3, 2 h para onda 4; até 6 imagens por ciclo. Caminhos: `industry/`, `farming/`, criatura/escudo, renderizadores e geradores relacionados, registros compartilhados pontuais, testes e estes documentos.

Estado herdado: 7D lógica sem recursos/renderizador/testes; 7G lógica compila e Garrafão gerado, faltam Ostra visual e recursos; 7H entidade/modelo/escudo parciais, faltam renderer e recursos. 7E/7F não iniciadas. Próximo: completar a onda 3 e validar em jogo antes do checkpoint de entrega.

## Critérios de fechamento

- [ ] 7D: Bomba e Tanque, persistência, baldes, capacidade de 64 baldes por bloco e empilhamento vertical sem perda ou duplicação.
- [ ] 7G: Ostra, Pérola e cultivo em linha com Corda e Boia; produção e coleta persistentes após recarregar o save.
- [ ] 7H: Caranguejo com comportamento completo e Escudo de Concha com 672 de durabilidade.
- [ ] Receitas, tradução pt/en, obtenção no JEI e modelos no Atlas.
- [ ] Build, testes direcionados e capturas inspecionadas.
- [ ] Integração validada e checkpoint da onda.

## Retomada

7D primeiro; a receita depende da Pérola de 7G, portanto o aceite de obtenção em sobrevivência exige ambas. O aceite da Fenda em terra cruza a 7E e será encerrado na onda 4.

Transferência confirmada pelo Adriano; sessão anterior encerrada, autonomia reiterada nesta retomada.

## Diário de execução

| Data | Faixa / arquivos | Mudança e commit | Verificação / evidência | Próximo passo |
|---|---|---|---|---|
| 27/10/2026 | Preparação documental | Nenhum código alterado | Especificação e estado atual consultados | Confirmar transferência e escopo |

## Pendências e limites

Sem build ou teste executado por Codex nesta onda. Sem capturas produzidas. Não considerar os checkboxes como entrega até haver evidência registrada.

