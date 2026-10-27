# Onda 4: Geradores com fluidos e ar

**Estado em 27/10/2026: fechada** pelo Claude (o Codex não chegou a começar). Resultado em [12-industria.md](../12-industria.md), 7E e 7F ("Como ficou" e "Modo fluido"). Faixas: 7E e 7F. Coordenação e base Git: [LEIA-ME.md](LEIA-ME.md). Especificação: [12-industria.md](../12-industria.md).

## Critérios de fechamento

- [x] 7E: Fenda preserva modo mundo e recebe água/lava pelo Cano no modo fluido, inclusive em terra.
- [x] 7F: Compressor produz Ar Comprimido; Pulmão armazena e regula geradores com +25% nas condições previstas.
- [x] Fluxos e consumo transacionais, persistência e sincronização com cliente.
- [x] Coletor continua recusando terra e locais fora da profundidade/bioma exigidos.
- [x] Arte e animações próprias do Compressor e Pulmão, Atlas e capturas inspecionadas.
- [x] Build, testes direcionados, integração e checkpoint da onda.

## Retomada

Depende da onda 3. Testar o circuito completo Bomba → Tanque → Cano → Fenda e Compressor → Tubo → Pulmão → gerador.

Antes de começar: reler o aviso atual do CONTEXTO.md, verificar ownership, estado Git e avanços dos outros agentes. Registrar arquivos e orçamento por faixa. A confirmação de transferência do Claude ainda está pendente.

## Diário de execução

| Data | Faixa / arquivos | Mudança e commit | Verificação / evidência | Próximo passo |
|---|---|---|---|---|
| 27/10/2026 | Preparação documental | Nenhum código alterado | Especificação e estado atual consultados | Confirmar transferência e escopo |
| 27/10/2026 | 7E e 7F (Claude) | Fenda com tanques e regulação; `PneumaticBlockEntity`, modelos `air_compressor` e `iron_lung` | parte `industry4` verde, 4 capturas `onda4_*` vistas | integrar |

## Pendências e limites

Não testado: Turbina e Boia reguladas (mesmo código da Fenda), linha de ar muito longa e o Pulmão debaixo d'água (as bolhas trocam o vapor, como na Fenda).

