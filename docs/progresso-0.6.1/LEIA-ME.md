# Progresso temporário da indústria 0.6.1

Criado em 27/10/2026 a pedido do Adriano. Estes documentos guardam checkpoints, evidências e instruções de retomada; as regras de jogo continuam em [12-industria.md](../12-industria.md), marcos em [07-roadmap.md](../07-roadmap.md) e ownership em [CONTEXTO.md](../../CONTEXTO.md).

## Ponto de partida

- Main limpa na inspeção: `505691f`; 7A, 7B e 7C integradas.
- Referência visual aprovada: Gerador de Fenda do Claude, `tools/models/machines/vent_generator/` e contrato em [06-tecnico.md](../06-tecnico.md).
- Há reserva das ondas 3 a 6 para Claude em CONTEXTO.md; transferência para Codex solicitada ao Adriano e ainda sem resposta. Nenhuma implementação iniciada por Codex.
- A expressão “fases 3 e 4” diverge da lista de ondas 3 a 6. Escopo proposto: a lista detalhada até 0.6.1, aguardando a mesma confirmação.

## Ondas

- [Onda 3](onda-3.md): Bomba, Tanque, Ostra, cultivo, Caranguejo e Escudo.
- [Onda 4](onda-4.md): geradores com fluidos, Compressor e Pulmão.
- [Onda 5](onda-5.md): dez máquinas e telas.
- [Onda 6](onda-6.md): QA, pacote e release.

## Disciplina de checkpoints

1. Confirmar ownership e trabalhar em branch/worktree própria dentro de `.worktrees/`.
2. Registrar arquivos e orçamento da faixa antes de implementar; até seis capturas por ciclo visual.
3. Commits pequenos em português com coautoria, preservando estados intermediários identificados como incompletos.
4. Em cada onda, registrar commits, comandos, resultados, imagens inspecionadas, limitações e próximo passo. Só fechar após os critérios de aceite.
5. Ao integrar cada onda, preservar um commit de integração identificável; anotar o hash aqui. Para desfazer depois, usar reversão dos commits da onda em branch de revisão, sem reset destrutivo da main ou trabalho alheio.
6. Não remover estes documentos enquanto forem necessários para retomar; encerrar com handoff e links para as evidências definitivas.

## Uso do Codex

A janela de cinco horas é uma cota de uso compartilhada da conta, não o tamanho do contexto da conversa. Na consulta inicial: 7% da janela de cinco horas e 1% da semanal usados. Consultar novamente nas passagens de onda e antes de trechos longos; salvar progresso após cada entrega para sobreviver a interrupções ou compactação. Não há garantia de caber toda a execução numa janela.

## Checkpoints

| Estado | Commit | Evidência |
|---|---|---|
| Base antes das ondas 3 a 6 | `505691f` | main limpa; 7C registrada no handoff |
| Preparação documental | commit que introduz este arquivo | documentos por onda; implementação não iniciada |

