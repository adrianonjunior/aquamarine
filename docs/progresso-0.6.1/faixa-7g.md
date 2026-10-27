# Progresso temporário 7G

Iniciada em 27/10/2026. Dono: Codex, faixa Ostra. Worktree `codex-ostra`, branch `codex/ostra`.

Escopo: entidade Ostra, item `oyster_pearl`, Garrafão/Corda/Boia de Cultivo, arte, receitas, refino do Aurium e JEI; teste direcionado de produção, persistência e criatura.
Orçamento inicial: 3 horas; máximo 6 capturas por ciclo. Referência: Fenda aprovada e contrato de máquinas/criaturas.

Estado: **feita** em 27/10/2026; fechamento em [onda-3.md](onda-3.md).

## Desenho do refazimento da Corda e da Boia
1. A Corda lê como uma linha fina e flexível; três fios escalonados cruzam em meia-volta e caem um pixel no centro.
2. As pontas voltam ao eixo do Garrafão em y=11,5 e z=8; cada bloco leva meia presilha, fechada pelo vizinho.
3. A ampola central é o detalhe distante da Corda: vidro verde-água, cinta de latão, dois fiapos e sal seco na ponta.
4. A onda perde amplitude nas extremidades para preservar a emenda; a ampola responde meio ciclo depois e dá peso à água.
5. A Boia lê primeiro como anel octogonal baixo e largo, depois como mastro fino e lanterna alta.
6. Marfim ocupa a massa flutuante, cobre marca as duas faixas, latão arma o aro e couro forma a amarração.
7. Rebites, limo nas juntas e arranhões ficam nos pintores determinísticos; nenhuma placa grande depende de ruído.
8. A lanterna troca o núcleo âmbar pelo perolado; a bandeirola baixa, média ou alta comunica idle, run e full de longe.
9. O anel gira no centro do flutuador, a bandeirola gira na base e o mergulho da coleta termina alinhado ao run.

- **Ficou:** Corda de 38 cubos e Boia de 60 cubos, modelos/rigs/texturas 128 px, seis clipes da Boia, BEs e renderizadores; Atlas, `build` e `industry3` verdes.
- **Não testei:** multiplayer real nem uma linha longa em oceano gerado; o teste cobriu Garrafão + 2 Cordas + Boia, água, estados e coleta.
- **Adriano olha primeiro:** `cultivo_linha_agua.png` para o encaixe da linha e depois `cultivo_boia_full/run/idle.png` para lanterna e bandeirola.
