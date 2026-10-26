# Resumo geral do Aquamarine

> **Retrato de 26/10/2026**, pedido pelo Adriano: o que já está feito em todas as fases, o que falta e o plano
> proposto. Cada fato mora no arquivo linkado na linha; em conflito, valem eles ([00-decisoes.md](00-decisoes.md),
> [07-roadmap.md](07-roadmap.md), [10-fase4.md](10-fase4.md)). Atualize este retrato a cada release.

## Em números (release 0.5.0)

| O quê | Quantidade |
|---|---|
| Itens | 54 |
| Blocos | 82 (70 de construção e vidros rachados) |
| Criaturas | 7 (Kraken, Tentáculo, Cria, Ovo, Moréia, Baleia-de-Nereia, Cachalote) |
| Receitas | 159 |
| Trocas da Vila | 31 |
| Encantamentos | 8 |
| Texturas | 197 |
| Sons originais | 66 |
| Código | 169 arquivos Java, cerca de 31,5 mil linhas, 14 arquivos de teste no jogo |
| Commits | 158 |

## Feito

| Fase | Data | O que entrou | Estado |
|---|---|---|---|
| **Entrega 1:** base | 24/10 | Registros, aba no criativo, traduções pt/en, configuração, texturas por script, testes no build e no GitHub. Minério, lingote e bloco Abissais, Pérola Abissal e Cristal de Maré. 5 ferramentas e armadura Abissais com bônus de conjunto. Forja Abissal. Óculos de Mergulho. Barra de vida do boss com faixa de escudo e rachaduras. Chamariz, Altar do Abismo e mensagens de invocação | ✅ |
| **Entrega 2:** Kraken v1 | 24/10 | 3 fases com transição, escolha de alvo, fuga e abandono. Vida e escudo crescem com os jogadores (até 12). Escudo Carapaça com Nós de Maré e dano por tipo de arma. 8 tentáculos e 10 ataques com aviso (Pancada, Agarrão, Agarrão duplo, Tinta, Redemoinho, Crias, Jato, Rugido, Raios, Emboscada). Crias e ovos. Pressão 0 a 4 com Tônico e Boia de Âncora. Aviso falso, tremor de tela, silêncio do oceano. Olho, Tinta e Coração do Kraken, 2 troféus e bônus da primeira vitória. `/aquamarine kraken` e arena de teste | ✅ falta o teste com 2 jogadores reais |
| **Fase 3:** fauna, mergulho e Vila | 24 e 25/10 | Pressão por profundidade, Medidor, Regulador e Traje de Mergulho Profundo com Reserva de Ar. Moréia Tempestuosa (7 partes com colisão própria, Descarga), Escama Elétrica, Bobina Voltaica, Chicote. Baleia-de-Nereia e Cachalote: canto, rota, domar, Sela para 3, fuga do Kraken. Arpão Abissal, Aljava, 5 pontas. 8 encantamentos (7 novos e o Quebra-Carapaça). Propulsor de Maré (tecla V). Porto dos Náufragos: Cúpula, Eclusa, Cais com elevadores de bolhas, Torre, 3 profissões, comércio, reputação, Alerta de Moréia, achado com `/locate`. Criaturas nascendo sozinhas | ✅ falta o teste com 3 jogadores reais |
| **Áudio:** A0 e luta do Kraken | 25/10 | Ferramentas de geração e as 6 amostras aprovadas. 3 trilhas (Tormenta, Fúria, Abismo) em 3 camadas (orquestra com sinos e trombetas, breakcore, glória), stingers e efeitos do Kraken e das crias. A luta muda o mundo: céu e névoa por fase, tempestade com raios, recife morrendo, crateras, onda de choque e clarão dourado na vitória | ✅ falta ouvir a luta no jogo |
| **Atlas** e Kraken v2 | 25/10 | Site com modelos, animações, áudio, texturas e docs, histórico de versões e endereço público fixo. Kraken v2 no jogo com 16 movimentos e 2 vezes maior | ✅ falta o aceite no jogo |
| **Fase 4, 4A** | 25/10 | Cria, Ovo e Tentáculo refeitos com as cores do Kraken; o ovo mostra o filhote dentro | ✅ |
| **Fase 4, 4B** | 26/10 | Oricálcio virou Abissal; mundos antigos convertem os itens sozinhos | ✅ |
| **Fase 4, 4D** | 26/10 | Bigorna de Maré, Mesa de Cartas e Altar de Concha em 3D; troféus viram estatuetas. Texturas novas nas 5 pontas, Bobina, Regulador, Aljava e Propulsor. Boia com sino e lanterna que muda de cor | ✅ |
| **Fase 4, 4E** | 26/10 | 35 blocos aquáticos (Pedra de Maré, Coral Petrificado, Telhas de Concha, Ladrilhos Abissais, Vidro do Mar, luminárias) e 35 vidros rachados; a onda de choque do Kraken racha os vidros | ✅ |
| **Fase 4, 4F** | 26/10 | Neutrônio estilo Avaritia: minério sob os oceanos profundos, cadeia Forja → têmpera a 70 blocos → ferraria, equipamento acima da netherite com bônus na água e céu estrelado animado | ✅ |
| **Fase 4, 4G** | 26/10 | Cavitador de Neutrônio: pulso de cavitação debaixo d'água (atravessa 3 criaturas e implode), arpões fora dela, modelo 3D que acende na carga e partículas próprias | ✅ |
| **Releases** | 25 e 26/10 | 0.4.0 (instalador de clique duplo e pacote de servidor) e 0.5.0 (tudo da Fase 4 até a 4G, `.jar` solto para o CurseForge) | ✅ falta o teste com os amigos |

## O que falta

| # | O que falta | Tipo | Liberado? |
|---|---|---|---|
| 1 | **Progressão no sobrevivência:** o Minério Abissal não aparece no mundo, o Altar do Abismo não tem receita e o Projeto do Chamariz só existe no criativo. Sem comandos ficam travados a Forja, o Arpão, as pontas, o Propulsor, o Chicote, o Kraken e o Neutrônio. Tudo isso ia vir da Cidade Perdida | buraco | precisa do Adriano ([00-decisoes.md](00-decisoes.md), "Em aberto") |
| 2 | **Kraken renovado (ex-4C):** 3 modelos novos para escolher, colisão por parte, cabeça sólida, tentáculos que não brotam do nada, mais movimento ([11-fase6.md](11-fase6.md), 6A; especificação de colisão em [10-fase4.md](10-fase4.md), §3) | Fase 6 | planejada, aguarda o Adriano |
| 3 | **4S:** sons da Fase 4 ([10-fase4.md](10-fase4.md), §10) | Fase 4 | quando o Adriano pedir |
| 4 | Itens da luta: Carga de Pulso, Pilar Ressonante, Amuleto da Corrente (o escudo e o Redemoinho já esperam por eles) | Kraken completo | não |
| 5 | Itens de exploração: Lanterna de Coral, Arco de Coral, Sopa de Tentáculo | Kraken completo | não |
| 6 | Itens pós-boss: Tridente do Kraken, Manto de Tinta, Farol Abissal. Hoje o Olho do Kraken e a Tinta Abissal caem, mas não servem para nada | Kraken completo | não |
| 7 | Áudio A1 a A5: ambiente em camadas, sons de Moréia, baleias, itens, blocos e Vila (hoje do Minecraft), músicas fora da luta, mixagem final ([09-audio.md](09-audio.md)) | áudio | ✅ sim, um lote por vez com aprovação |
| 8 | **Cidade Perdida:** cúpula, Praça, Templo, Biblioteca, Minas, Cratera-arena, baús, Chave de Nereia, 5 livros, Bússola de Maré, Mapa de Nereia ([04-cidade-perdida.md](04-cidade-perdida.md)) | cidade | não |
| 9 | Bioma Fossa de Nereia: água escura, Coral Luminoso, areia azulada, Cristais de Maré | cidade | não |
| 10 | Criaturas da cidade: Sentinela, Sereia Corrompida, Enguia Elétrica, Caranguejo Ermitão (Casco de Prismarinho) e o Walker | cidade | não |
| 11 | Testes com jogadores reais: Entrega 2, Fase 3, Kraken v2, trilhas, 0.5.0 ([07-roadmap.md](07-roadmap.md), "Só o Adriano pode fechar") | aceite | só o Adriano |
| 12 | Aviso no log dos blocos de quebra-cabeça do Porto (`joint="rolling"`); teste de print manual (F2) | correção | a pedido |
| 13 | Branch `estruturador/entrega-1`, worktrees antigas e arquivos grandes do disco | faxina | só o Adriano |
| 14 | **Resto da Fase 6:** colisão e carne de baleia, skins 3D e mesas da Vila, Aurium, Forja Abissal nova, projétil 3D do Cavitador, energia e Coletor de Neutrônio, Mesa de Encantamento Aquática, acessórios, radar, decoração ([11-fase6.md](11-fase6.md), 6B a 6H) | Fase 6 | planejada, aguarda o Adriano |

## Plano (ideia, aguarda o Adriano)

Release `0.N.0` entrega a Fase `N` e não existe Fase 5 ([00-decisoes.md](00-decisoes.md), "Versões e fases"; tabela em
[07-roadmap.md](07-roadmap.md), "Releases e fases"). Ordem recomendada: **0 em paralelo → 1 → 2 → 3 e 4 entre as outras**.
O que não tem fase (etapa 5) ganha número quando o Adriano escolher.

| Etapa | O quê | Aceite | Tamanho | Precisa de |
|---|---|---|---|---|
| 0 | Teste da 0.5.0 com os amigos, pelo roteiro de [relatorio-3q.md](relatorio-3q.md); o que der errado vira 0.5.x | sem crash nem desync | sessão de jogo | o Adriano e os amigos |
| 1 | **Ponte do sobrevivência (0.5.1):** Minério Abissal raro no fundo dos oceanos profundos (mais comum e mais alto que o Neutrônio; quando a Cidade vier, as Minas viram a fonte boa), receita do Altar do Abismo na Forja, Projeto do Chamariz raro nos baús do Porto e numa troca de nível alto do Cartógrafo | teste num mundo gerado: achar o minério, fazer Forja, Altar e Chamariz e invocar o Kraken sem comando | 2 a 3 h | ok do Adriano (muda a decisão "o minério só existe na cidade") |
| 2 | **Fase 6 (0.6.0):** Kraken renovado (principal), fauna, Vila, Aurium, energia aquática, encantamento, acessórios, radar e decoração, em faixas 6A a 6H ([11-fase6.md](11-fase6.md)). Começa pela 6A1: três modelos candidatos no Atlas | cada faixa testada no jogo; o Adriano escolhe o modelo do Kraken | várias sessões | liberação do Adriano (fase ou faixa) e respostas F6-1 a F6-8 |
| 3 | **4S:** sons da Fase 4 (e depois os da 6) em lotes para ouvir, junto com a correção do aviso do Porto | o Adriano aprova cada lote | 2 a 3 h | pedido do Adriano |
| 4 | **Áudio A1 a A5**, entre as outras etapas | prévia aprovada a cada lote | por lote | aprovação por lote |
| 5 | **Sem fase:** *Kraken completo* (linhas 4, 5 e 6 de "O que falta"; os pós-boss dão uso ao Olho e à Tinta) e **Cidade Perdida** em 4 levas, cada uma com release: (a) estrutura, baús, livros, Chave, Bússola e Mapa, com as Minas como fonte do minério; (b) bioma Fossa de Nereia; (c) 4 criaturas no nível dos mobs atuais; (d) Walker | teste de cada item no jogo; `/locate` acha a cidade | várias sessões | o Adriano dar número de fase e liberar |
