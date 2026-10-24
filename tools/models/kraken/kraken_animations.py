"""As 16 animações do Kraken (contrato KrakenView), como dados. build_model.py gera daqui o .bbmodel e o
KrakenAnimations.java, então mudar uma animação é mudar este arquivo e rodar o gerador.

Convenções do modelo (conferidas no jogo):
- rotação x positiva levanta o que aponta para +z (braço sobe, manto inclina para trás);
- posição y positiva sobe (o posVec do jogo inverte o y);
- escala é multiplicador (1 = normal).
Os braços são radiais: arm_0 na frente (+z), arm_2 à direita, arm_4 atrás, arm_6 à esquerda.

Ritmo dos ataques: aviso (antecipação + tremor de tensão) até o tempo de aviso de docs/02-kraken.md,
golpe logo depois e retorno. WARN guarda esse tempo; o Java o exporta para as partículas.
"""
from __future__ import annotations

import math

C, L = "catmullrom", "linear"
ARMS = range(8)
BODY = ["mantle", "head"] + [f"arm_{i}" for i in ARMS]

# Tempo do aviso (s) de cada ataque = docs/02-kraken.md, tabela "Ataques e avisos". O golpe vem em seguida.
WARN = {"slam": 1.5, "grab": 1.5, "ink": 2.0, "whirlpool": 2.0, "spawnYoung": 2.0, "waterJet": 1.2,
        "roar": 1.5, "bolts": 2.0, "ambush": 2.0, "doubleGrab": 1.5}


def seg(arm: int, joint: int) -> str:
    return f"arm_{arm}_segment_{joint}"


class Clip:
    def __init__(self, length: float, loop: bool = False):
        self.length = length
        self.loop = loop
        # (osso, canal) -> {tempo: (vetor, interpolação)}
        self.tracks: dict[tuple[str, str], dict[float, tuple[tuple[float, float, float], str]]] = {}

    def keys(self, bone: str, channel: str, *keys) -> "Clip":
        """keys: (t, valor) ou (t, valor, interp); valor é número (só x; escala uniforme) ou (x, y, z)."""
        track = self.tracks.setdefault((bone, channel), {})
        for key in keys:
            t, value = key[0], key[1]
            interp = key[2] if len(key) > 2 else C
            if isinstance(value, (int, float)):
                value = (value, value, value) if channel == "scale" else (value, 0.0, 0.0)
            track[round(min(max(t, 0.0), self.length), 4)] = (tuple(float(v) for v in value), interp)
        return self

    def rot(self, bone, *keys):
        return self.keys(bone, "rotation", *keys)

    def pos(self, bone, *keys):
        return self.keys(bone, "position", *keys)

    def scale(self, bone, *keys):
        return self.keys(bone, "scale", *keys)

    def body_pos(self, *keys):
        """Move o corpo inteiro (manto, cabeça e os 8 braços são filhos da raiz)."""
        for bone in BODY:
            self.pos(bone, *keys)
        return self

    def finish(self) -> "Clip":
        """Garante chave no início e no fim: clipes únicos voltam ao repouso, laços fecham no primeiro valor."""
        for (bone, channel), track in self.tracks.items():
            rest = (1.0, 1.0, 1.0) if channel == "scale" else (0.0, 0.0, 0.0)
            times = sorted(track)
            if times[0] > 0:
                track[0.0] = (rest, C)
            if self.loop:
                track.setdefault(self.length, track[min(track)])
            elif max(track) < self.length:
                track[self.length] = (rest, C)
        return self


def tremble(t0: float, t1: float, base, amp, step: float = 0.08, interp: str = L) -> list:
    """Tremor de tensão: alterna base+amp e base-amp entre t0 e t1 (base e amp: número ou vetor)."""
    def vec(v):
        return v if isinstance(v, tuple) else (v, 0.0, 0.0)
    b, a = vec(base), vec(amp)
    out, t, sign = [], t0, 1
    while t <= t1 + 1e-6:
        out.append((t, tuple(b[i] + sign * a[i] for i in range(3)), interp))
        t, sign = t + step, -sign
    return out


def scale_tremble(t0, t1, base, amp, step=0.08):
    out, t, sign = [], t0, 1
    while t <= t1 + 1e-6:
        out.append((t, base + sign * amp, L))
        t, sign = t + step, -sign
    return out


def wave(clip: Clip, bone: str, channel: str, period: float, amp: float, phase: float, axis: int = 0,
         offset: float = 0.0, samples: int = 8):
    """Senoide amostrada (laço suave com catmull-rom)."""
    keys = []
    for k in range(samples + 1):
        t = clip.length * k / samples
        v = [0.0, 0.0, 0.0]
        v[axis] = offset + amp * math.sin(2 * math.pi * t / period + phase)
        keys.append((t, tuple(v)))
    clip.keys(bone, channel, *keys)


# ---------------------------------------------------------------- estados

def idle() -> Clip:
    c = Clip(4.0, loop=True)
    c.body_pos((0, (0, 0, 0)), (2.0, (0, -0.8, 0)), (4.0, (0, 0, 0)))
    c.scale("mantle", (0, 1.0), (2.0, (1.03, 1.06, 1.03)), (4.0, 1.0))
    c.rot("head", (0, 0), (2.0, 2.5), (4.0, 0))
    for i in ARMS:
        for j, amp in ((1, 5.0), (2, 7.0), (3, 9.0)):
            wave(c, seg(i, j), "rotation", 4.0, amp, i * math.pi / 4 + j * 0.7)
    return c


def swim() -> Clip:
    c = Clip(1.4, loop=True)
    c.scale("mantle", (0, 1.0), (0.45, (0.9, 1.08, 0.9)), (1.4, 1.0))
    for i in ARMS:
        c.rot(seg(i, 0), (0, 14), (0.5, -20), (1.4, 14))
        c.rot(seg(i, 2), (0, 8), (0.3, 10), (0.75, -16), (1.4, 8))
    return c


def emerge() -> Clip:
    c = Clip(2.2)
    c.body_pos((0, (0, -18, 0)), (1.3, (0, 1.8, 0)), (1.7, (0, -0.4, 0)), (2.2, (0, 0, 0)))
    c.rot("head", (0, -18), (1.3, 14), (2.2, 0))
    for i in ARMS:
        c.rot(seg(i, 0), (0, -35), (0.9, -10), (1.3, 30), (2.2, 0))
        c.rot(seg(i, 2), (0, 0), (1.3, -12), (1.6, 10), (2.2, 0))
    return c


def stunned() -> Clip:
    c = Clip(1.2, loop=True)
    c.rot("mantle", (0, (0, 0, -9)), (0.6, (0, 0, 9)), (1.2, (0, 0, -9)))
    c.rot("head", (0, (14, 0, 7)), (0.6, (14, 0, -7)), (1.2, (14, 0, 7)))
    c.body_pos((0, (0, -1.2, 0)), (0.6, (0, -1.8, 0)), (1.2, (0, -1.2, 0)))
    for i in ARMS:
        sway = 4 if i % 2 else -4
        c.rot(seg(i, 0), (0, -26), (0.6, -26 + sway), (1.2, -26))
        for j in (1, 2, 3):
            c.rot(seg(i, j), (0, -10), (0.6, -10 - sway), (1.2, -10))
    return c


def phase_change() -> Clip:
    """3 s, a transição de fase: contrai e treme, explode para fora (rugido) e volta."""
    c = Clip(3.0)
    c.scale("mantle", (0, 1.0), (0.8, (0.86, 0.9, 0.86)), *scale_tremble(0.85, 1.05, 0.86, 0.03),
            (1.2, (1.22, 1.18, 1.22), L), (1.9, 1.15), (3.0, 1.0))
    c.body_pos((0, (0, 0, 0)), (0.8, (0, -1.6, 0)), (1.2, (0, 1.2, 0), L), (3.0, (0, 0, 0)))
    c.rot("head", (0, 0), (0.8, 10), (1.2, -24, L), (1.9, -20), (3.0, 0))
    for i in ARMS:
        c.rot(seg(i, 0), (0, 0), (0.8, -24), *tremble(0.85, 1.05, -24, 4), (1.2, 48, L), (1.9, 40), (3.0, 0))
        for j in (1, 2, 3):
            c.rot(seg(i, j), (0, 0), (0.8, -18), (1.2, 12, L), (1.9, 6), (3.0, 0))
    return c


def death() -> Clip:
    c = Clip(3.2)
    c.rot("mantle", (0, 0), (0.7, (0, 0, 6)), (1.2, (-10, 0, 0)), (3.2, (20, 0, 70)))
    c.rot("head", (0, 0), (0.7, -20), (1.2, -10), (3.2, (35, 0, 40)))
    c.body_pos((0, (0, 0, 0)), (1.2, (0, 1.0, 0)), (3.2, (0, -10, 0)))
    for i in ARMS:
        jit = 22 if i % 2 else -22
        c.rot(seg(i, 0), (0, 0), (0.15, jit, L), (0.3, -jit, L), (0.45, jit, L), (0.6, -jit, L),
              (1.0, 36), (1.6, -34), (3.2, -42))
        for j in (1, 2, 3):
            c.rot(seg(i, j), (0, 0), (1.0, 8), (1.6, -14), (3.2, -16))
    return c


# ---------------------------------------------------------------- ataques

def slam() -> Clip:
    """Pancada: o tentáculo da direita (de lado, legível de frente) sobe alto, curvado como chicote, treme no topo e desce de uma vez."""
    w = WARN["slam"]
    c = Clip(w + 0.9)
    c.rot(seg(2, 0), (0, 0), (0.2, -10), (w - 0.35, 78), *tremble(w - 0.3, w - 0.05, 78, 4, 0.07),
          (w + 0.1, -42, L), (w + 0.3, -34), (w + 0.9, 0))
    for j, curl in ((1, 18), (2, 24), (3, 30), (4, 34)):
        c.rot(seg(2, j), (0, 0), (w - 0.35, curl), (w + 0.1, -curl * 0.6, L), (w + 0.35, -curl * 0.3), (w + 0.9, 0))
    c.rot("mantle", (0, 0), (w - 0.35, 7), (w + 0.1, -9, L), (w + 0.9, 0))
    c.rot("head", (0, 0), (w - 0.35, 9), (w + 0.1, -12, L), (w + 0.9, 0))
    c.body_pos((0, (0, 0, 0)), (w + 0.1, (0, 0, 0)), (w + 0.18, (0, -0.8, 0), L), (w + 0.45, (0, 0, 0)))
    return c


def grab() -> Clip:
    """Agarrão: o tentáculo da esquerda serpenteia de lado (movimento horizontal), estala para a frente e se enrola."""
    w = WARN["grab"]
    c = Clip(w + 0.9)
    c.rot(seg(6, 0), (0, 0), (0.5, (22, -12, 0)), (w - 0.2, (26, -34, 0)), (w + 0.12, (4, 32, 0), L),
          (w + 0.5, (0, 28, 0)), (w + 0.9, 0))
    for j in (1, 2, 3, 4):
        snake = [(0.25 + k * 0.25, (0, (16 if (k + j) % 2 else -16) * (0.6 + j * 0.15), 0)) for k in range(int(w / 0.25))]
        c.rot(seg(6, j), (0, 0), *snake, (w + 0.15, (-30 - j * 6, 0, 0), L), (w + 0.5, (-36 - j * 6, 0, 0)), (w + 0.9, 0))
    c.rot("head", (0, 0), (w - 0.2, (0, -10, 0)), (w + 0.15, (0, 8, 0)), (w + 0.9, 0))
    return c


def double_grab() -> Clip:
    """Agarrão Duplo: dois tentáculos simétricos abrem como pinças, serpenteiam juntos e fecham."""
    w = WARN["doubleGrab"]
    c = Clip(w + 0.9)
    for arm, side in ((1, 1), (7, -1)):
        c.rot(seg(arm, 0), (0, 0), (0.5, (30, -18 * side, 0)), (w - 0.2, (38, -36 * side, 0)),
              (w + 0.12, (8, 26 * side, 0), L), (w + 0.5, (4, 22 * side, 0)), (w + 0.9, 0))
        for j in (1, 2, 3, 4):
            snake = [(0.25 + k * 0.25, (0, (14 if k % 2 else -14) * side, 0)) for k in range(int(w / 0.25))]
            c.rot(seg(arm, j), (0, 0), *snake, (w + 0.15, (-32 - j * 6, 0, 0), L), (w + 0.5, (-38 - j * 6, 0, 0)),
                  (w + 0.9, 0))
    c.rot("mantle", (0, 0), (w - 0.2, 6), (w + 0.12, -6, L), (w + 0.9, 0))
    return c


def ink() -> Clip:
    """Nuvem de Tinta: o manto incha devagar com os braços abertos em guarda-chuva e se espreme de uma vez."""
    w = WARN["ink"]
    c = Clip(w + 0.8)
    c.scale("mantle", (0, 1.0), (w - 0.4, (1.2, 1.14, 1.2)), *scale_tremble(w - 0.35, w - 0.05, 1.22, 0.03),
            (w + 0.08, (0.8, 0.86, 0.8), L), (w + 0.35, 0.92), (w + 0.8, 1.0))
    c.rot("head", (0, 0), (w - 0.4, -12), (w + 0.08, 6, L), (w + 0.8, 0))
    c.body_pos((0, (0, 0, 0)), (w, (0, -0.6, 0)), (w + 0.12, (0, 2.0, 0), L), (w + 0.8, (0, 0, 0)))
    for i in ARMS:
        c.rot(seg(i, 0), (0, 0), (w - 0.4, 32), (w + 0.08, -16, L), (w + 0.8, 0))
        c.rot(seg(i, 1), (0, 0), (w - 0.4, -10), (w + 0.08, 6, L), (w + 0.8, 0))
    return c


def whirlpool() -> Clip:
    """Redemoinho (laço): o manto gira e uma onda corre pelos 8 braços em volta do corpo."""
    c = Clip(WARN["whirlpool"], loop=True)
    c.rot("mantle", (0, (0, 0, 0), L), (0.5, (0, 90, 0), L), (1.0, (0, 180, 0), L), (1.5, (0, 270, 0), L),
          (2.0, (0, 360, 0), L))
    for i in ARMS:
        ph = -i * math.pi / 4
        c.keys(seg(i, 0), "rotation", *[(t, (18 + 10 * math.sin(2 * math.pi * t / 2.0 + ph + math.pi / 2),
                                             26 * math.sin(2 * math.pi * t / 2.0 + ph), 0))
                                        for t in [k * 0.25 for k in range(9)]])
        wave(c, seg(i, 2), "rotation", 2.0, 22, ph - 0.9, axis=1)
    return c


def spawn_young() -> Clip:
    """Crias: os braços de trás sobem e mostram a barriga, o manto estremece em espasmos e expele os ovos."""
    w = WARN["spawnYoung"]
    c = Clip(w + 0.8)
    for arm in (3, 4, 5):
        c.rot(seg(arm, 0), (0, 0), (0.8, 46), (w + 0.1, 30), (w + 0.8, 0))
        c.rot(seg(arm, 1), (0, 0), (0.8, 14), (w + 0.8, 0))
    c.pos("mantle", (0, (0, 0, 0)), *tremble(0.8, w - 0.05, (0, 0, 0), (0.7, 0, 0), 0.1), (w + 0.8, (0, 0, 0)))
    c.scale("mantle", (0, 1.0), (1.0, (1.1, 1.06, 1.1)), (1.3, 1.0), (1.7, (1.13, 1.08, 1.13)),
            (w + 0.08, (0.9, 0.84, 0.9), L), (w + 0.8, 1.0))
    c.rot("head", (0, 0), (0.8, -10), (w + 0.08, 4, L), (w + 0.8, 0))
    return c


def water_jet() -> Clip:
    """Jato de Água: a cabeça recua e aspira (manto enche), os braços da frente se abrem e ela dispara."""
    w = WARN["waterJet"]
    c = Clip(w + 0.8)
    c.rot("head", (0, 0), (0.45, 18), (w - 0.15, -8), *tremble(w - 0.12, w - 0.02, -8, 2.5, 0.05),
          (w + 0.06, -14, L), (w + 0.8, 0))
    c.pos("head", (0, (0, 0, 0)), (w - 0.15, (0, 0, -2.6)), (w + 0.06, (0, 0, 3.2), L), (w + 0.35, (0, 0, 1)),
          (w + 0.8, (0, 0, 0)))
    c.scale("mantle", (0, 1.0), (w - 0.15, (1.16, 1.1, 1.16)), (w + 0.06, (0.88, 0.9, 0.88), L), (w + 0.8, 1.0))
    c.body_pos((0, (0, 0, 0)), (w + 0.06, (0, 0, 0)), (w + 0.25, (0, 0, -2.0)), (w + 0.8, (0, 0, 0)))
    for arm, side in ((0, 0), (1, 1), (7, -1)):
        spread = (22, 0, 0) if side == 0 else (6, -30 * side, 0)
        c.rot(seg(arm, 0), (0, 0), (0.5, spread), (w + 0.4, spread), (w + 0.8, 0))
    return c


def roar() -> Clip:
    """Rugido Abissal: o corpo inteiro se contrai e treme (braços enrolados para dentro), depois explode."""
    w = WARN["roar"]
    c = Clip(w + 1.0)
    c.scale("mantle", (0, 1.0), (w - 0.5, (0.8, 0.86, 0.8)), *scale_tremble(w - 0.45, w - 0.05, 0.8, 0.035, 0.06),
            (w + 0.08, (1.26, 1.2, 1.26), L), (w + 0.4, 1.18), (w + 1.0, 1.0))
    c.body_pos((0, (0, 0, 0)), (w - 0.5, (0, -2.2, 0)), (w + 0.08, (0, 1.0, 0), L), (w + 1.0, (0, 0, 0)))
    c.rot("head", (0, 0), (w - 0.5, 16), *tremble(w - 0.45, w - 0.05, (16, 0, 0), (0, 0, 3), 0.06),
          (w + 0.08, -26, L), (w + 0.4, -22), (w + 1.0, 0))
    for i in ARMS:
        c.rot(seg(i, 0), (0, 0), (w - 0.5, -28), *tremble(w - 0.45, w - 0.05, -28, 3, 0.06),
              (w + 0.08, 42, L), (w + 0.4, 36), (w + 1.0, 0))
        for j in (1, 2, 3):
            c.rot(seg(i, j), (0, 0), (w - 0.5, -22), (w + 0.08, 4, L), (w + 1.0, 0))
    return c


def bolts() -> Clip:
    """Raios Guiados: a cabeça olha o céu e quatro braços sobem retos como antenas, com as pontas faiscando."""
    w = WARN["bolts"]
    c = Clip(w + 0.8)
    c.rot("head", (0, 0), (0.8, 30), (w + 0.05, 34), (w + 0.8, 0))
    for arm in (0, 2, 4, 6):
        c.rot(seg(arm, 0), (0, 0), (1.0, 82), (w + 0.05, 82), (w + 0.2, 70, L), (w + 0.8, 0))
        for j in (1, 2):
            c.rot(seg(arm, j), (0, 0), (1.0, 6), (w + 0.8, 0))
        c.rot(seg(arm, 3), (0, 0), (1.0, 0), *tremble(1.05, w, 0, (12, 8, 0), 0.1), (w + 0.8, 0))
    for arm in (1, 3, 5, 7):
        c.rot(seg(arm, 0), (0, 0), (1.0, -14), (w + 0.8, 0))
    c.scale("mantle", (0, 1.0), (w, 1.0), (w + 0.06, 1.12, L), (w + 0.25, 1.0), (w + 0.8, 1.0))
    c.body_pos((0, (0, 0, 0)), (w, (0, 0, 0)), (w + 0.06, (0, 1.0, 0), L), (w + 0.3, (0, 0, 0)))
    return c


def ambush() -> Clip:
    """Emboscada por Baixo: o corpo se agacha e todos os braços apontam e cavam para baixo, depois perfuram."""
    w = WARN["ambush"]
    c = Clip(w + 0.8)
    c.body_pos((0, (0, 0, 0)), (1.4, (0, -3.0, 0)), (w, (0, -3.2, 0)), (w + 0.1, (0, -4.6, 0), L), (w + 0.8, (0, 0, 0)))
    c.rot("mantle", (0, 0), (1.4, -6), (w + 0.8, 0))
    c.rot("head", (0, 0), (1.4, -14), (w + 0.8, 0))
    for i in ARMS:
        c.rot(seg(i, 0), (0, 0), (1.2, -52), *tremble(1.25, w - 0.05, -52, 5, 0.12), (w + 0.1, -76, L),
              (w + 0.4, -70), (w + 0.8, 0))
        for j in (1, 2, 3):
            c.rot(seg(i, j), (0, 0), (1.2, -10), (w + 0.1, 2, L), (w + 0.8, 0))
    return c


BUILDERS = {
    "idle": idle, "swim": swim, "emerge": emerge, "stunned": stunned, "phaseChange": phase_change,
    "death": death, "slam": slam, "grab": grab, "ink": ink, "whirlpool": whirlpool,
    "spawnYoung": spawn_young, "waterJet": water_jet, "roar": roar, "bolts": bolts, "ambush": ambush,
    "doubleGrab": double_grab,
}


def build_all() -> dict[str, Clip]:
    return {name: builder().finish() for name, builder in BUILDERS.items()}
