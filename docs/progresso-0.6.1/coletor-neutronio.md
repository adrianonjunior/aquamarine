# Coletor de Neutrônio — desenho e handoff

## Desenho antes do código
1. Silhueta: altar-industrial simétrico de 3×2×3, baixo e largo, com a esfera central elevada como foco visível em três quartos.
2. Leitura distante 1: duas bocas-funil de latão na frente e atrás formam uma ampulheta horizontal que “puxa” a água para o centro.
3. Leitura distante 2: a câmara octogonal de vidro ocupa o vão central, atravessada por três anéis giroscópicos de latão.
4. Leitura distante 3: a bandeja frontal de vidro exibe de zero a dezesseis grãos perolados sem abrir a tela.
5. Captação: grades de aço azulado, lábios chanfrados de latão e braços tubulares com limo nas juntas deixam a entrada pesada e náutica.
6. Compressão: vidro verde-água com reflexo diagonal envolve um núcleo branco-azulado estrelado, frio e perolado.
7. Distribuição: bobinas laterais, cabos de cobre patinado e arcos ciano conduzem visualmente cada borda da máquina ao núcleo.
8. Instrumentação: dois mostradores marfim, rebites regulares e uma lâmpada tricolor eliminam superfícies mudas e explicam o estado.
9. Movimento principal: os anéis aceleram em eixos diferentes e o núcleo cresce com o progresso, fazendo a compressão ser legível sem texto.
10. Movimento secundário: pó aspirado atrasa meio compasso em relação aos anéis; os arcos correm depois, dando peso e causalidade.
11. Coleta: um pulso comprime o núcleo, freia os anéis e solta um grão que cai até a bandeja, com começo e fim sem salto.
12. Tela: painel de latão e vidro organiza energia à esquerda, núcleo/progresso no centro e estoque/estado à direita; o erro explica exatamente o requisito ausente.

## Handoff final
- **O que ficou:** Coletor 3×2×3 completo (profundidade em constante), 17 partes waterlogged com energia/menu por qualquer face, BER único animado, tela viva, drop único com pó e migração do bloco antigo — monta se houver espaço ou quebra preservando o pó se estiver obstruído.
- **O que não foi testado:** não houve captura no idioma pt-BR (a composição foi dimensionada para duas linhas); o teste opcional `npm test --prefix tools/atlas` ainda acusa `neutronium_collector/invalid: pose`, mesmo com o pulso vermelho da v3 presente no rig, e foi interrompido após duas ocorrências iguais. `test_catalog.py`, build do Atlas, Gradle e GameTest `energy` passaram.
- **O que o Adriano olha primeiro:** as oito imagens abaixo, começando por `modelo-02-run-inicio.png` e `tela-03-run.png`; no jogo, confirme principalmente o enquadramento do item grande na aba Máquinas e a leitura submersa da câmara.

## Capturas finais (8)
- [`modelo-01-idle.png`](capturas/coletor-neutronio/modelo-01-idle.png) — altar largo, anéis em repouso, núcleo pequeno e bandeja vazia.
- [`modelo-02-run-inicio.png`](capturas/coletor-neutronio/modelo-02-run-inicio.png) — núcleo ciano ativo, anéis cruzados e fluxo visual das bobinas para a câmara.
- [`modelo-03-run-fim.png`](capturas/coletor-neutronio/modelo-03-run-fim.png) — pulso de coleta com compressão forte e grão em trânsito para a bandeja.
- [`modelo-04-full.png`](capturas/coletor-neutronio/modelo-04-full.png) — núcleo estável e bandeja perolada cheia, mantendo a silhueta legível.
- [`tela-01-local-invalido.png`](capturas/coletor-neutronio/tela-01-local-invalido.png) — energia/progresso zerados e motivo de profundidade exibido em vermelho.
- [`tela-02-idle.png`](capturas/coletor-neutronio/tela-02-idle.png) — local válido sem energia, com capacidade, consumo e tempo restantes ao vivo.
- [`tela-03-run.png`](capturas/coletor-neutronio/tela-03-run.png) — barra carregada, núcleo em 50%, tempo restante e consumo de 64 E/t.
- [`tela-04-full.png`](capturas/coletor-neutronio/tela-04-full.png) — estoque 16/16, núcleo estável e instrução de retirada do Pó Estelar.
