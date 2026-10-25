# Revisão do Atlas — 25/10/2026

Direção: observatório de criaturas, com o modelo completo no centro. Azul abissal
`#09191f`, painéis `#10232b`, texto `#e2eeec`, acento água-marinha `#6ee4cf` e
âmbar `#e5bf81` para propostas. Títulos em Chakra Petch; controles em Segoe UI.
Catálogo à esquerda, modelo e transporte ao centro, movimentos à direita.
Em telas estreitas, criaturas em faixa horizontal e movimentos abaixo do modelo.

Revisão visual: desktop 1440 px e celular 390 px. Corrigido o enquadramento:
a câmera considera cantos reais dos cubos durante o movimento, aproximando o
modelo sem depender da caixa global superdimensionada dos tentáculos.
Áudio mantém players nativos, filtros e medidas separadas da audição.

Veredito: pronto para a revisão criativa do Adriano. Os controles e o catálogo
passaram nos testes de navegador, com oito versões e 61 clipes exercitados
(45 atuais/propostos + 16 da referência v1 do Kraken). Seis testes de catálogo
verificam hierarquia, tempos, laços, isolamento da proposta e áudio adaptativo.
Evidências locais em `build/atlas-qa/`; sem erros JavaScript nessa execução.

Proveniência visual: todos os PNGs exibidos vêm dos recursos e mapas UV do
próprio mod. Os snapshots incorporam as texturas existentes; a proposta v2
reutiliza suas ilhas UV. Não há arte raster externa ou imagem gerada nesta entrega.
Licenças das fontes e do Three.js acompanham o site.

Limites: revisão em navegador, sem execução do jogo; a audição e a aprovação
artística cabem ao Adriano. Histórico preservado em `versions/`; novas propostas
devem ganhar novos identificadores, sem substituir os arquivos anteriores.
