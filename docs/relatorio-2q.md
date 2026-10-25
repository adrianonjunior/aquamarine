# Relatório 2Q — QA visual do Kraken

**Data:** 24/10/2026 · **Base:** `abd589b` após `git pull --rebase origin main` · **Ambiente:** `runClientGameTest`, arena aquática, câmera acima do chão.

## Achados

| Prioridade | Arquivo provável | Evidência e reprodução |
|---|---|---|
| P1 — investigar | `src/client/java/com/aquamarine/client/KrakenEffects.java` | `0035_22_kraken_jato_rugido_raios.png` mostra grandes retângulos ciano opacos cobrindo a cena durante o grupo `waterJet`/`roar`/`bolts`. Reproduzir em arena aquática com `/aquamarine kraken attack waterJet`, `roar` e `bolts`, avaliando cada efeito isoladamente; a captura atual combina os três, então ainda é necessário localizar qual efeito os produz. Dono provável: Claude (efeitos/partículas). |
| P2 — legibilidade | `src/client/java/com/aquamarine/client/KrakenEffects.java` | Em `0026_18_ataque_pancada_aviso.png`, o aviso de `slam` fica escuro e pouco distinguível na água; aparecem principalmente as pontas dos tentáculos no enquadramento. Reproduzir `/aquamarine kraken arena` e `/aquamarine kraken attack slam` com câmera acima do chão. Dono provável: Claude (aviso/efeito); confirmar após reprodução isolada. |

## Cobertura e limites

- `runClientGameTest --quiet` terminou com código 0, sem falha de GameTest ou crash observados. O log registra falhas de autenticação de perfil/Realms (HTTP 401), sem relação demonstrada com a luta.
- O corpo grande do Kraken foi reconhecível nos enquadramentos da arena, e ovo/Cria/tentáculo aparecem nas capturas de teste. Esse enquadramento de trio não valida legibilidade do modelo em distância de jogo livre.
- As capturas cobrem os 10 ataques em grupos, conforme os próprios cenários da suíte: slam (`0026`, `0033`), grab e double grab (`0033`), ink, whirlpool e spawnYoung (`0034`), waterJet, roar e bolts (`0035`), ambush (`0036`). Portanto, não há uma imagem independente por ataque; as imagens de grupos também dificultam atribuir efeitos sobrepostos.
- Rachaduras das fases 2 e 3 foram visíveis em `0020` e `0021`; no quadro combinado `0039`, as três fases aparecem juntas, mas a fase 1 não ficou legível o bastante para validar suas rachaduras individualmente. `0040` mostra o Kraken nadando na arena; não houve comparação temporal dedicada para medir o quanto os tentáculos acompanham o corpo.
- Uma checagem temporária com `FakePlayer` foi compilada e executada dentro da suíte para simular um segundo jogador no servidor; passou sem falha reportada. Isso não cria uma segunda conexão cliente: multiplayer real, sincronização da barra entre clientes, latência e desync continuam **não cobertos** e precisam do teste do Adriano com dois clientes.
- Foram abertas 12 screenshots no total, dentro do limite; as do grupo de golpes mostram três Krakens de teste lado a lado, com barras sobrepostas e modelos laterais cortados pelo enquadramento. Esse arranjo é do cenário visual da suíte e não foi tratado como defeito de gameplay.

## Próximo passo

O orquestrador abrirá Tasks para o Claude investigar os dois achados acima. O aceite multiplayer da Entrega 2 permanece pendente até o Adriano validar uma sessão real com dois clientes e as três fases.
