"""Proposta v2 EXCLUSIVA DO ATLAS; sem importacao pelo gerador do mod."""
from kraken_animations import *

def organic_idle() -> Clip:
    """Massa suspensa: respiração lenta e onda viajante até a ponta, em dois eixos."""
    c = Clip(4.0, True)
    c.body_pos((0, (0, 0, 0)), (1.6, (0, .6, 0)), (3, (0, -.6, 0)), (4, (0, 0, 0)))
    c.scale("mantle", (0, 1), (1.6, (1.035, .97, 1.035)), (3, (.98, 1.035, .98)), (4, 1))
    c.rot("head", (0, (1, 0, 0)), (1.6, (3, -2, 0)), (3, (-2, 2, 0)), (4, (1, 0, 0)))
    for i in ARMS:
        for j in range(5):
            phase = i * math.pi / 4 - j * .8
            keys = []
            for k in range(17):
                t = k / 4
                a = 2 * math.pi * t / 4 + phase
                keys.append((t, ((2 + j * 1.6) * math.sin(a),
                                 (1 + j * .65) * math.cos(a), .6 * math.sin(a))))
            c.rot(seg(i, j), *keys)
    return c


def jet_swim() -> Clip:
    """Propulsão: contrai o manto, empurra e desliza; pontas chegam depois da raiz."""
    c = Clip(1.4, True)
    c.scale("mantle", (0, (1.02, .98, 1.02)), (.36, (1.1, .92, 1.1)),
            (.55, (.88, 1.12, .88)), (.95, (.96, 1.04, .96)), (1.4, (1.02, .98, 1.02)))
    c.rot("head", (0, 0), (.4, 5), (.65, -6), (1.4, 0))
    for i in ARMS:
        for j in range(5):
            # Amostra periódica com atraso espacial: continuidade inclusive na emenda.
            keys = []
            for k in range(29):
                t = k * .05
                a = 2 * math.pi * t / 1.4 - j * .6 + (i % 2) * .16
                stroke = math.sin(a) + .28 * math.sin(2 * a)
                keys.append((t, ((13 - j * 1.5) * stroke, (1.5 + j) * math.sin(a - .7), 0)))
            c.rot(seg(i, j), *keys)
    return c


def refine(name: str, c: Clip) -> Clip:
    """V2: sobreposição orgânica e recuperação em cadeia, sem alterar tempos da luta.

    O gesto principal continua nos ossos originais; só os braços livres recebem contrapeso.
    Sem multiplicar o giro de 360° do redemoinho (o amplificador antigo quebrava o laço).
    """
    if name == "idle":
        return organic_idle().finish()
    if name == "swim":
        return jet_swim().finish()
    if name == "whirlpool":
        c = Clip(2, True)
        for i in ARMS:
            for j in range(5):
                keys = []
                for k in range(17):
                    t = k / 8
                    a = math.pi * t - i * math.pi / 4 - j * .65
                    keys.append((t, (16 + 11 * math.sin(a), 18 * math.cos(a), 3 * math.sin(a))))
                c.rot(seg(i, j), *keys)
        # O corpo torce e volta, em vez de girar o manto separado da cabeça.
        for bone in ("mantle", "head"):
            wave(c, bone, "rotation", 2, 7, 0, axis=1, samples=16)
        return c.finish()
    # Gestos com impacto: cascata de energia, seguida de retorno amortecido.
    if name in WARN:
        w = WARN[name]
        active = {"slam": {2}, "grab": {6}, "doubleGrab": {1, 7},
                  "bolts": {0, 2, 4, 6}, "spawnYoung": {3, 4, 5},
                  "waterJet": {0, 1, 7}}.get(name, set(ARMS))
        for i in ARMS:
            for j in range(5):
                bone = seg(i, j)
                track = c.tracks.get((bone, "rotation"))
                lag = j * .035 + (i % 2) * .025
                if track:
                    # Conserva o aviso; propaga o golpe da raiz à ponta e segura o fim neutro.
                    moved = {}
                    for t, (v, interp) in sorted(track.items()):
                        nt = min(c.length - .12, t + lag) if w < t < c.length else t
                        moved[round(nt, 4)] = (v, interp)
                    c.tracks[(bone, "rotation")] = moved
                else:
                    # Braços livres sustentam a massa e reagem ao recuo.
                    strength = (3.5 + j * 1.3) * (1 if i in active else .65)
                    side = 1 if i % 2 else -1
                    c.rot(bone, (0, 0), (w * .55, (strength, side * 2, 0)),
                          (w, (strength * 1.25, side * 3, 0)),
                          (w + .15 + lag, (-strength, -side * 3, 0), L),
                          (c.length - .14, (strength * .12, 0, 0)), (c.length, 0))
        # Recuo lateral do golpe unilateral: a silhueta inteira comunica a pancada.
        if name in ("slam", "grab", "doubleGrab"):
            side = -1 if name == "grab" else 1
            c.rot("mantle", (0, 0), (w * .7, (5, -4 * side, -4 * side)),
                  (w, (7, -5 * side, -6 * side)), (w + .12, (-7, 5 * side, 7 * side), L),
                  (w + .5, (2, -1 * side, -2 * side)), (c.length, 0))
    elif name == "emerge":
        for i in ARMS:
            for j in range(1, 5):
                lag = j * .075 + (i % 2) * .05
                c.tracks.pop((seg(i, j), "rotation"), None)
                c.rot(seg(i, j), (0, -15), (.75 + lag, -22),
                      (1.25 + lag, 15 - j), (1.7 + lag, -5), (c.length, 0))
    elif name == "stunned":
        # Pontas moles, cabeça pendendo: exaustão em vez de sacudir todo o corpo rigidamente.
        c.tracks.pop(("mantle", "rotation"), None)
        c.rot("mantle", (0, (3, 0, -3)), (.6, (4, 0, 3)), (1.2, (3, 0, -3)))
        for i in ARMS:
            wave(c, seg(i, 4), "rotation", c.length, 5, i * .8, offset=-12, samples=12)
    elif name == "phaseChange":
        for i in ARMS:
            c.rot(seg(i, 4), (0, 0), (.8, -28), (1.2 + i * .025, 22, L),
                  (1.65 + i * .025, -12), (2.3, 4), (3, 0))
    elif name == "death":
        # Última contração curta, depois peso morto com queda sequencial das pontas.
        for i in ARMS:
            for j in range(1, 5):
                c.tracks.pop((seg(i, j), "rotation"), None)
                lag = j * .12 + (i % 3) * .08
                c.rot(seg(i, j), (0, 0), (.6 + lag, 12), (1.35 + lag, -20),
                      (2.2 + lag, -10), (3.2, -16 - j * 2))
    return c.finish()


def build_all() -> dict[str, Clip]:
    clips = {name: builder().finish() for name, builder in BUILDERS.items()}
    return {name: refine(name, amplify(clip) if name in WARN and name != "whirlpool" else clip)
            for name, clip in clips.items()}
