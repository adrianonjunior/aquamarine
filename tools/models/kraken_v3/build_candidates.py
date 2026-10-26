#!/usr/bin/env python3
"""Três candidatos a Kraken novo (Fase 6, faixa 6A1; docs/11-fase6.md): só para o site (Atlas), sem tocar no jogo.

    C:\\Python314\\python tools/models/kraken_v3/build_candidates.py            # gera as três pastas
    C:\\Python314\\python tools/models/kraken_v3/build_candidates.py --register # e registra no Atlas (v3a, v3b, v3c)

Usa o molde da Cria v2 (`tools/models/spawnlings/build_model.py`): mesmas convenções de eixo, box-UV, rampas de cor,
camada de brilho `_glow` e formato de clipe. Cada candidato grava em `tools/models/kraken_v3/<versão>/` a textura, o
brilho, o gabarito de UV, o `.bbmodel` e o `rig.json`; o snapshot do Atlas vai para `build/kraken_v3/` e, com `--register`,
para `tools/atlas/versions/`. O gerador do jogo
(`tools/models/kraken/`) não muda: a escolhida do Adriano entra no jogo depois, pela 6A2/6A4.

Contrato de clipes (docs/06-tecnico.md, "Contrato de modelos e entidades"): os três rigs têm os ossos que os 16 clipes
da `KrakenView` usam (corpo, cabeça, mandíbula, manto que respira, barbatanas, pálpebras, 8 braços de 6 juntas e 2
tentáculos de caça com clava). Aqui saem os de base para escolher: idle, swim, slam, roar, grab e death.
"""
from __future__ import annotations

import argparse
import base64
import io
import json
import math
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
BASE = Path(__file__).resolve().parent
sys.path.insert(0, str(ROOT / "tools/models/spawnlings"))
import build_model as bm  # noqa: E402
from build_model import Clip, Creature, L, tremble  # noqa: E402

NOTCH = bm.NOTCH
bm.MATERIALS.update({
    "shell": [(8, 12, 26), (14, 22, 42), (22, 34, 60), (34, 50, 82), (50, 70, 106), (72, 96, 132), (104, 130, 162),
              (146, 170, 196)],
    "web": [(10, 8, 28), (20, 14, 46), (32, 22, 68), (46, 32, 92), (64, 46, 118), (88, 66, 148)],
    "nacre": [(30, 24, 70), (52, 48, 110), (80, 84, 150), (110, 130, 186), (150, 190, 220), (200, 236, 240)],
})
GLOW_CYAN, GLOW_SOFT, GLOW_EYE = bm.GLOW_CYAN, bm.GLOW_SOFT, bm.GLOW_EYE
_base_pattern = bm.pattern


# ================================================================ pintura (padrões novos sobre os da Cria)

def _spots(c, x0, y0, w, h, px, gx, rng, ramp, density=22, glow=120):
    """Manchas de cromatóforo (roxas e escuras) e fotóforos esparsos que brilham."""
    if w < 3 or h < 3:
        return
    purple = bm.MATERIALS["belly"]
    for _ in range(max(1, w * h // density)):
        cx, cy = rng.randrange(1, w - 1), rng.randrange(1, h - 1)
        r = rng.choice((1, 1, 1, 2))
        color = purple[rng.choice((1, 2))] if rng.random() < 0.45 else ramp[max(0, len(ramp) // 2 - 2)]
        for j in range(-r, r + 1):
            for i in range(-r, r + 1):
                if i * i + j * j <= r * r + 0.5 and 0 < cx + i < w - 1 and 0 < cy + j < h - 1:
                    px[x0 + cx + i, y0 + cy + j] = color + (c.alpha,)
    for _ in range(w * h // glow):
        if rng.random() < 0.75:
            bx, by = rng.randrange(1, w - 1), rng.randrange(1, h - 1)
            gx[x0 + bx, y0 + by] = GLOW_SOFT
            px[x0 + bx, y0 + by] = ramp[-1] + (c.alpha,)


def pattern(c, face, x0, y0, w, h, px, gx, rng, ramp):
    kind, a = c.pattern, c.alpha
    top = len(ramp) - 1
    fmat = c.face_material.get(face)
    if kind in ("skin2", "arm"):
        if kind == "arm" and face == "front" and w >= 2 and h >= 3:
            # Ventosas em duas fileiras desencontradas, com o centro aceso de vez em quando.
            s = bm.MATERIALS["sucker"]
            cols = (0, w - 2) if w >= 4 else ((w - 2) // 2,)
            for n, j in enumerate(range(1, h - 2, 3)):
                cx = cols[n % len(cols)]
                for dj in (0, 1):
                    for di in (0, 1):
                        if cx + di < w:
                            px[x0 + cx + di, y0 + j + dj] = s[3 if (di + dj) % 2 == 0 else 2] + (a,)
                if n % 3 == 0:
                    gx[x0 + cx, y0 + j] = GLOW_SOFT
            return
        if fmat == "belly":
            for _ in range(max(1, w * h // 12)):
                px[x0 + rng.randrange(w), y0 + rng.randrange(h)] = ramp[top - 1] + (a,)
            return
        if face != "bottom" and fmat != "eye":
            _spots(c, x0, y0, w, h, px, gx, rng, ramp)
    elif kind == "lamp":
        for j in range(h):
            for i in range(w):
                edge = i in (0, w - 1) or j in (0, h - 1)
                px[x0 + i, y0 + j] = bm.MATERIALS["eye"][2 if edge else 4] + (255,)
                gx[x0 + i, y0 + j] = GLOW_SOFT if edge and w > 2 and h > 2 else GLOW_CYAN
    elif kind == "photo" and face != "bottom":
        # Faixa de fotóforos: um ponto aceso a cada 2 px no eixo mais longo da face.
        if h >= w:
            for j in range(0, h, 2):
                gx[x0 + w // 2, y0 + j] = GLOW_CYAN
                px[x0 + w // 2, y0 + j] = bm.MATERIALS["eye"][3] + (a,)
        else:
            for i in range(0, w, 2):
                gx[x0 + i, y0 + h // 2] = GLOW_CYAN
                px[x0 + i, y0 + h // 2] = bm.MATERIALS["eye"][3] + (a,)
    elif kind == "eye2" and face == c.feature:
        eye = bm.MATERIALS["eye"]
        cx, cy = (w - 1) / 2, (h - 1) / 2
        rx, ry = w / 2, h / 2
        for j in range(h):
            for i in range(w):
                d = math.hypot((i - cx) / rx, (j - cy) / ry)
                if d > 0.9:
                    px[x0 + i, y0 + j] = bm.MATERIALS["skin"][1] + (255,)
                    gx[x0 + i, y0 + j] = (0, 0, 0, 0)
                elif d > 0.72:
                    px[x0 + i, y0 + j] = eye[1] + (255,)
                    gx[x0 + i, y0 + j] = GLOW_SOFT
                else:
                    px[x0 + i, y0 + j] = eye[4 if d < 0.38 else 3] + (255,)
                    gx[x0 + i, y0 + j] = GLOW_EYE
        # Pupila em fenda horizontal (cefalópode), um pouco mais grossa nos olhos grandes, e um reflexo.
        half = 1 if h >= 7 else 0
        for j in range(round(cy - half), round(cy + half) + (0 if h % 2 else 1)):
            for i in range(round(cx - w * 0.34), round(cx + w * 0.34) + 1):
                if 0 <= i < w and 0 <= j < h:
                    px[x0 + i, y0 + j] = (4, 8, 12, 255)
                    gx[x0 + i, y0 + j] = (0, 0, 0, 0)
        hx, hy = round(cx - rx * 0.42), round(cy - ry * 0.48)
        px[x0 + hx, y0 + hy] = (236, 255, 252, 255)
        gx[x0 + hx, y0 + hy] = GLOW_EYE
    elif kind == "plates":
        if face in ("top", "bottom"):
            return
        # Costelas de crescimento a cada 3 px; sutura acesa em zigue-zague nos lados; quilha clara nas costas.
        for j in range(1, h - 1):
            for i in range(1, w - 1):
                if j % 3 == 0:
                    px[x0 + i, y0 + j] = ramp[1] + (a,)
                elif j % 3 == 1:
                    px[x0 + i, y0 + j] = ramp[min(top, top - 2 + (1 if i % 5 == 0 else 0))] + (a,)
        if face in ("side_a", "side_b") and w >= 5:
            for j in range(h):
                i = w // 2 + (1 if (j // 2) % 2 == 0 else -1)
                px[x0 + i, y0 + j] = bm.MATERIALS["ridge"][3] + (a,)
                if j % 2 == 0:
                    gx[x0 + i, y0 + j] = GLOW_SOFT
        if face == "back" and w >= 3:
            for j in range(h):
                px[x0 + w // 2, y0 + j] = ramp[top] + (a,)
    elif kind == "web" and face not in ("top", "bottom"):
        # Membrana: nervuras claras que se abrem da raiz e borda com pontos de luz.
        for k in range(1, w - 1, 3):
            x = k
            for j in range(h):
                if 0 <= x < w:
                    px[x0 + x, y0 + j] = ramp[top - 1] + (a,)
                if j % 4 == 3:
                    x += rng.choice((0, 1))
        for j in range(0, h, 2):
            gx[x0 + w - 1, y0 + j] = GLOW_SOFT
            px[x0 + w - 1, y0 + j] = ramp[top] + (a,)
    elif kind == "hooks" and face == "front" and w >= 2:
        horn = bm.MATERIALS["horn"]
        for j in range(1, h - 1, 2):
            for i in range(j % 4 // 2, w, 2):
                px[x0 + i, y0 + j] = horn[4] + (a,)
            gx[x0 + w // 2, y0 + j] = GLOW_SOFT
    else:
        _base_pattern(c, face, x0, y0, w, h, px, gx, rng, ramp)


bm.pattern = pattern  # bm.paint procura `pattern` no módulo na hora de pintar


# ================================================================ peças

def section(cr, part, y, h, r, name, belly=None, tint=0, pattern="skin2", material="skin", depth=1.0):
    """Seção quase redonda (duas caixas cruzadas, cantos recuados), como `round_body` da Cria; `belly` escolhe a
    face clara (no manto deitado ela não é a da frente)."""
    n = r * NOTCH
    fm = {belly: "belly"} if belly else None
    cr.cube(part, (-r, y, -n * depth), (2 * r, h, 2 * n * depth), material, pattern=pattern, name=f"{name}_wide",
            tint=tint, face_material=fm)
    cr.cube(part, (-n, y, -r * depth), (2 * n, h, 2 * r * depth), material, pattern=pattern, name=f"{name}_deep",
            tint=tint, face_material=fm)


def chain(cr, parent, root, pivot, rot, segs, bends, *, tip=None, spikes=False, webs=None):
    """Braço de juntas que crescem para +y. Devolve os nomes das juntas (a raiz `root` fica fora: ela mira o braço)."""
    cr.part(root, parent, pivot, rot)
    names, prev, p = [], 0.0, root
    for j, (length, w) in enumerate(segs):
        seg = f"{root}_{j}"
        cr.part(seg, p, (0, prev, 0), (bends[j], 0, 0))
        cr.cube(seg, (-w / 2, 0, -w / 2), (w, length + 0.4, w), "skin", pattern="arm", name=f"{seg}_body",
                face_material={"front": "belly"}, tint=(2 * j) // len(segs))
        if spikes and 1 <= j < len(segs) - 1:
            cr.cube(seg, (-0.4, length * 0.3, w / 2 - 0.3), (0.8, 1.2, 1.6), "horn", pattern="horn", name=f"{seg}_spike")
        if webs and j < len(webs):
            cr.cube(seg, (w / 2 - 0.2, 0, -0.4), (webs[j], length + 0.4, 0.8), "web", pattern="web", name=f"{seg}_web")
        names.append(seg)
        p, prev = seg, length
    if tip == "lamp":
        cr.cube(p, (-0.8, prev, -0.8), (1.6, 1.6, 1.6), "eye", pattern="lamp", name=f"{root}_lamp")
    return names


def club(cr, parent, name, length, w, prev):
    cr.part(name, parent, (0, prev, 0), (12, 0, 0))
    cr.cube(name, (-w / 2, 0, -w * 0.35), (w, length, w * 0.7), "skin", pattern="hooks", name=f"{name}_pad",
            face_material={"front": "belly"}, tint=1)
    cr.cube(name, (-w * 0.3, length, -w * 0.25), (w * 0.6, 1.6, w * 0.5), "horn", pattern="horn", name=f"{name}_hook")
    return name


def eyes(cr, part, x, y, z, w, h, depth=1.5):
    """Olho de cada lado (face de fora acesa, pupila em fenda) e uma pálpebra escondida dentro dele: a pálpebra
    sai 0,8 px para fora e cobre o olho quando o clipe pisca."""
    lids = []
    for side, n in ((-1, "left"), (1, "right")):
        feature = "side_b" if side > 0 else "side_a"
        x0 = x if side > 0 else -x - depth
        cr.cube(part, (x0, y, z), (depth, h, w), "skin", pattern="eye2", feature=feature,
                face_material={feature: "eye"}, name=f"eye_{n}")
        lid = cr.part(f"lid_{n}", part, (0, 0, 0))
        cr.cube(lid, (x0 + 0.3, y + 0.25, z + 0.25), (depth - 0.6, h - 0.5, w - 0.5), "skin", pattern="plain",
                tint=-1, name=f"lid_{n}_skin")
        lids.append((lid, (side * 0.8, 0, 0)))
    return lids


class Rig:
    """O que os clipes genéricos precisam saber de cada candidato."""

    def __init__(self, **kw):
        self.body, self.head, self.jaw = "body", "head", "jaw"
        self.breathe, self.sway, self.fins, self.arms, self.feeders, self.clubs, self.lids = [], [], [], [], [], [], []
        self.swim_pitch = 0.0
        self.__dict__.update(kw)


# ================================================================ candidato A: Lula-colosso

def colossal():
    cr = Creature("kraken", "Kraken", (256, 256),
                  "Candidata A, Lula-colosso: corpo deitado como um torpedo, manto longo com faixas de fotóforos e "
                  "barbatana em ponta de flecha, coroa de 8 braços para a frente e 2 tentáculos de caça com clava de "
                  "ganchos; olhos enormes com fotóforos embaixo.", 63)
    cr.part("body", None, (0, 0, 0))
    cr.part("head", "body", (0, 0, 0))
    section(cr, "head", -6, 12, 7.5, "head", belly="bottom")
    section(cr, "head", -8, 2, 5.5, "head_crown", tint=1)
    for side, n in ((-1, "left"), (1, "right")):
        cr.cube("head", (7.3 if side > 0 else -8.5, -4.8, -5.6), (1.2, 1.6, 9.6), "skin", pattern="plain", tint=-1,
                name=f"brow_{n}")
        cr.cube("head", (7.2 if side > 0 else -8.2, 4.4, -4), (1, 1, 6), "eye", pattern="lamp", name=f"photophore_{n}")
    lids = eyes(cr, "head", 7.3, -3, -5, 8, 7)
    cr.part("beak", "head", (0, 1.5, -7.5))
    cr.cube("beak", (-2, -1.5, -3), (4, 3, 3), "horn", pattern="horn", name="beak_upper")
    cr.part("jaw", "beak", (0, 1.5, -0.5))
    cr.cube("jaw", (-1.5, 0, -2.5), (3, 1.8, 2.8), "horn", pattern="horn", name="beak_jaw")
    # Coroa de braços na frente da cabeça, apontando para a frente (rotação em x) e abertos em leque (em z).
    arm_segs = ((6.5, 4.2), (6, 3.6), (5.5, 3), (5, 2.5), (4.5, 2), (4, 1.5))
    arms = []
    for k in range(8):
        psi = 22.5 + 45 * k
        a = math.radians(psi)
        arms.append(chain(cr, "head", f"arm_{k}", (-math.sin(a) * 4.2, 1.5 + math.cos(a) * 4.2, -6.5), (-72, 0, psi),
                          arm_segs, (0, 4, 5, 7, 9, 12)))
    feeders, clubs = [], []
    for side, n in ((-1, "l"), (1, "r")):
        segs = [(4.5, 1.9 - 0.1 * j) for j in range(7)]
        names = chain(cr, "head", f"feeder_{n}", (side * 1.5, 3.8, -7), (-80, 0, side * 8), segs, (0, 2, 2, 3, 3, 4, 4))
        feeders.append(names)
        clubs.append(club(cr, names[-1], f"feeder_{n}_club", 7, 3.2, 4.5))
    # Manto deitado para trás, um pouco para cima; a barriga clara fica embaixo (face de trás no eixo do manto).
    cr.part("mantle", "body", (0, -4, 5), (-76, 0, 0))
    section(cr, "mantle", -1.5, 2, 8.8, "collar", material="ridge", pattern="photo")
    section(cr, "mantle", -12, 12, 8.2, "mantle_a", belly="back")
    section(cr, "mantle", -22, 10, 7.6, "mantle_b", belly="back")
    for side, n in ((-1, "left"), (1, "right")):
        for y, r, tag in ((-11, 8.2, "a"), (-21, 7.6, "b")):
            cr.cube("mantle", (r - 0.3 if side > 0 else -r - 0.3, y, -0.5), (0.6, 9, 1), "eye", pattern="lamp",
                    name=f"stripe_{tag}_{n}")
    cr.part("mantle_tip", "mantle", (0, -22, 0))
    section(cr, "mantle_tip", -8, 8, 6.4, "mantle_c", belly="back", tint=1)
    section(cr, "mantle_tip", -13, 5, 4.4, "mantle_d", tint=1)
    section(cr, "mantle_tip", -16, 3, 2.4, "mantle_e", tint=1, pattern="plain")
    fins = []
    for side, n in ((-1, "left"), (1, "right")):
        fin = cr.part(f"fin_{n}", "mantle_tip", (side * 4, -9, 0))
        for x, y, w, h, tag in ((0, -10, 6, 10, "root"), (6, -8, 4, 6, "mid"), (10, -6, 2.5, 3, "tip")):
            cr.cube(fin, (x if side > 0 else -x - w, y, -0.6), (w, h, 1.2), "skin", pattern="web", tint=1,
                    name=f"fin_{n}_{tag}")
        fins.append((fin, side))
    rig = Rig(breathe=["mantle"], sway=["mantle_tip"], fins=fins, arms=arms, feeders=feeders, clubs=clubs, lids=lids)
    return cr, rig


# ================================================================ candidato B: Vampiro-abissal

def vampire():
    cr = Creature("kraken", "Kraken", (256, 256),
                  "Candidata B, Vampiro-abissal: manto em cúpula alta com duas barbatanas de orelha que remam, olhos "
                  "na frente da cabeça e 8 braços abertos em guarda-chuva, ligados por membrana, com espinhos nas "
                  "costas e lanternas acesas nas pontas; 2 filamentos longos de caça.", 64)
    cr.part("body", None, (0, 0, 0))
    cr.part("head", "body", (0, 0, 0))
    section(cr, "head", -5, 10, 7, "head")
    section(cr, "head", 5, 2, 5.5, "head_base", belly="bottom")
    for side, n in ((-1, "left"), (1, "right")):
        cr.cube("head", (6.4 if side > 0 else -8.2, -4.6, -5.6), (1.8, 1.4, 7), "ridge", pattern="bands",
                name=f"brow_{n}")
    lids = eyes(cr, "head", 6.8, -3, -5.2, 6, 6, depth=1.6)
    cr.part("beak", "head", (0, 7, 0))
    cr.cube("beak", (-1.8, 0, -1.8), (3.6, 2, 3.6), "horn", pattern="horn", name="beak_upper")
    cr.part("jaw", "beak", (0, 2, 0))
    cr.cube("jaw", (-1.2, 0, -1.2), (2.4, 1.5, 2.4), "horn", pattern="horn", name="beak_jaw")
    cr.part("mantle", "head", (0, -5, 0.5), (-10, 0, 0))
    section(cr, "mantle", -3, 3, 9, "dome_a")
    section(cr, "mantle", -7, 4, 12, "dome_b")
    section(cr, "mantle", -11, 4, 13, "dome_c")
    cr.part("mantle_tip", "mantle", (0, -11, 0))
    section(cr, "mantle_tip", -4, 4, 12, "dome_d", tint=1)
    section(cr, "mantle_tip", -7, 3, 9.5, "dome_e", tint=1)
    section(cr, "mantle_tip", -9, 2, 6, "dome_f", tint=1)
    section(cr, "mantle_tip", -10, 1, 2.5, "dome_g", tint=1, pattern="plain")
    fins = []
    for side, n in ((-1, "left"), (1, "right")):
        ear = cr.part(f"ear_{n}", "mantle", (side * 12.5, -9, 0), (0, 0, side * 20))
        cr.cube(ear, (0 if side > 0 else -8, -4.5, -0.6), (8, 9, 1.2), "skin", pattern="web", tint=1, name=f"ear_{n}_blade")
        cr.cube(ear, (8 if side > 0 else -11, -3, -0.6), (3, 6, 1.2), "skin", pattern="web", tint=1, name=f"ear_{n}_edge")
        cr.cube("mantle", (side * 12.6 - 1, -10, -1), (2, 2, 2), "eye", pattern="lamp", name=f"ear_{n}_lamp")
        fins.append((ear, side))
    # Guarda-chuva: braços inclinados 40 graus para fora, a membrana de cada um alcança o vizinho (+x local).
    arm_segs = ((5, 3.6), (5, 3.2), (4.5, 2.8), (4.5, 2.4), (4, 2), (3.5, 1.6))
    tilt = 52
    webs, rho = [], 5.0
    for length, w in arm_segs[:4]:
        mid = rho + math.sin(math.radians(tilt)) * length / 2
        webs.append(max(1.0, 2 * mid * math.sin(math.radians(22.5)) - w))
        rho += math.sin(math.radians(tilt)) * length
    arms = []
    for k in range(8):
        phi = 22.5 + 45 * k
        a = math.radians(phi)
        arms.append(chain(cr, "head", f"arm_{k}", (math.sin(a) * 5, 6, math.cos(a) * 5), (tilt, phi, 0), arm_segs,
                          (0, 3, 5, 7, 10, 14), tip="lamp", spikes=True, webs=webs))
    feeders, clubs = [], []
    for side, n in ((-1, "l"), (1, "r")):
        segs = [(4, 1.0 - 0.05 * j) for j in range(8)]
        names = chain(cr, "head", f"feeder_{n}", (side * 2.5, 6, -1.5), (28, 180 - side * 18, 0), segs,
                      (0, 4, 4, 4, 3, 3, 2, 2))
        feeders.append(names)
        clubs.append(club(cr, names[-1], f"feeder_{n}_club", 3, 1.6, 4))
    rig = Rig(breathe=["mantle"], sway=["mantle_tip"], fins=fins, arms=arms, feeders=feeders, clubs=clubs, lids=lids,
              swim_pitch=34)
    return cr, rig


# ================================================================ candidato C: Amonita-colosso

WHORLS = 18


def ammonite():
    cr = Creature("kraken", "Kraken", (256, 256),
                  "Candidata C, Amonita-colosso: concha em espiral nas costas, com costelas, sutura acesa e espinhos "
                  "na quilha (casa com o escudo do Kraken), capuz duro sobre a cabeça, borda de nácar que brilha na "
                  "boca da concha e 8 braços em leque para baixo com 2 tentáculos de caça.", 65)
    cr.part("body", None, (0, 0, 0))
    cr.part("head", "body", (0, 0, 0))
    section(cr, "head", -5, 10, 7, "head", belly="bottom")
    lids = eyes(cr, "head", 6.8, -3, -3.5, 5, 5, depth=1.6)
    cr.part("hood", "head", (0, -5, 1), (8, 0, 0))
    cr.cube("hood", (-8, -3, -9), (16, 3, 14), "shell", pattern="plates", name="hood_cap")
    cr.cube("hood", (-6.5, -1.5, -10), (13, 2.5, 2), "shell", pattern="plates", tint=1, name="hood_lip")
    cr.cube("hood", (-1, -4, -8), (2, 1.2, 12), "ridge", pattern="photo", name="hood_keel")
    cr.part("beak", "head", (0, 2.5, -7))
    cr.cube("beak", (-1.8, -1, -2.5), (3.6, 2.5, 3), "horn", pattern="horn", name="beak_upper")
    cr.part("jaw", "beak", (0, 1.5, -0.5))
    cr.cube("jaw", (-1.4, 0, -2), (2.8, 1.6, 2.4), "horn", pattern="horn", name="beak_jaw")
    # Concha: espiral logarítmica de 18 câmaras; a primeira aponta para trás e cada uma vira 21 graus para cima,
    # passando por cima da cabeça. A seção fica deslocada para fora (+z local) para as voltas de dentro encaixarem.
    cr.part("shell", "body", (0, 3, 5), (-90, 0, 0))
    parent, prev = "shell", 0.0
    whorls = []
    for j in range(WHORLS):
        length, depth, width = 5.6 * 0.91 ** j, 17 * 0.925 ** j, 15 * 0.925 ** j
        name = f"whorl_{j}"
        cr.part(name, parent, (0, -prev, 0), (0 if j == 0 else 21, 0, 0))
        # As câmaras se sobrepõem 1,2 px nas duas pontas: sem isso a volta de fora abre cunhas e vira engrenagem.
        cr.cube(name, (-width / 2, -length - 1.2, -depth * 0.15), (width, length + 2.4 if j else length + 1.2, depth), "shell",
                pattern="plates", tint=(j * 3) // WHORLS - 1, name=f"{name}_chamber")
        if 1 <= j <= 13 and j % 3 == 1:
            cr.cube(name, (-0.7, -length * 0.6, depth * 0.85 - 0.3), (1.4, 1.4, 2.2), "horn", pattern="horn",
                    name=f"{name}_spike")
        if j == 0:
            cr.cube(name, (-width / 2 - 0.6, -1.2, -depth * 0.15 - 0.6), (width + 1.2, 1.2, depth + 1.2), "nacre",
                    pattern="photo", name="aperture_rim")
        whorls.append(name)
        parent, prev = name, length
    arm_segs = ((5.5, 3.4), (5, 3), (4.5, 2.6), (4.5, 2.2), (4, 1.8), (3.5, 1.4))
    arms = []
    for k in range(8):
        psi = -84 + 24 * k
        a = math.radians(psi)
        arms.append(chain(cr, "head", f"arm_{k}", (-math.sin(a) * 3.5, 4 + math.cos(a) * 2.2, -4.5), (-30, 0, psi),
                          arm_segs, (0, 4, 6, 8, 10, 13), spikes=True))
    feeders, clubs = [], []
    for side, n in ((-1, "l"), (1, "r")):
        segs = [(4.2, 1.7 - 0.08 * j) for j in range(7)]
        names = chain(cr, "head", f"feeder_{n}", (side * 1.6, 3, -6.5), (-55, 0, side * 10), segs, (0, 3, 3, 4, 4, 5, 5))
        feeders.append(names)
        clubs.append(club(cr, names[-1], f"feeder_{n}_club", 5, 2.8, 4.2))
    rig = Rig(breathe=["hood"], sway=whorls[:1], arms=arms, feeders=feeders, clubs=clubs, lids=lids, swim_pitch=-8)
    return cr, rig


CANDIDATES = {"v3a": (colossal, "Candidata v3a · Lula-colosso (só no site)"),
              "v3b": (vampire, "Candidata v3b · Vampiro-abissal (só no site)"),
              "v3c": (ammonite, "Candidata v3c · Amonita-colosso (só no site)")}


# ================================================================ clipes (os mesmos para os três rigs)

def wave(t, length, cycles=1, phase=0.0):
    return math.sin(2 * math.pi * cycles * t / length + phase)


def clip_idle(r):
    T = 3.2
    c = Clip(T, True)
    c.sample(r.body, "position", lambda t: (0, 1.4 * wave(t, T), 0.6 * wave(t, T, 1, 1.2)), 12)
    c.sample(r.body, "rotation", lambda t: (2 * wave(t, T, 1, 0.8), 3 * wave(t, T, 1, 2.1), 1.6 * wave(t, T, 1, 0.3)), 12)
    c.sample(r.head, "rotation", lambda t: (1.5 * wave(t, T, 2), 2.5 * wave(t, T, 1, 1.5), 0), 12)
    for m in r.breathe:
        c.sample(m, "scale", lambda t: (1 + 0.05 * wave(t, T), 1 - 0.03 * wave(t, T), 1 + 0.05 * wave(t, T)), 12)
    for i, m in enumerate(r.sway):
        c.sample(m, "rotation", lambda t, i=i: (4 * wave(t, T, 1, -0.6 * i), 0, 2 * wave(t, T, 1, 1 - 0.5 * i)), 12)
    for fin, side in r.fins:
        c.sample(fin, "rotation", lambda t, s=side: (0, s * 14 * wave(t, T, 2), s * 6 * wave(t, T, 2, 0.6)), 16)
    for k, arm in enumerate(r.arms):
        for j, seg in enumerate(arm):
            c.sample(seg, "rotation", lambda t, k=k, j=j: ((3 + 2.6 * j) * wave(t, T, 1, -0.75 * j + 0.785 * k), 0,
                                                            (2 + 1.4 * j) * wave(t, T, 1, -0.6 * j + 1.3 * k + 0.5)), 12)
    for k, feeder in enumerate(r.feeders):
        for j, seg in enumerate(feeder):
            c.sample(seg, "rotation", lambda t, k=k, j=j: ((2 + 1.2 * j) * wave(t, T, 1, -0.5 * j + 2 * k), 0,
                                                            1.5 * wave(t, T, 1, -0.4 * j + k)), 12)
    for lid, off in r.lids:
        c.pos(lid, (0, (0, 0, 0)), (0.62 * T, (0, 0, 0)), (0.655 * T, off, L), (0.7 * T, off), (0.74 * T, (0, 0, 0), L))
    c.rot(r.jaw, (0, (0, 0, 0)), (0.3 * T, (8, 0, 0)), (0.42 * T, (0, 0, 0)), (0.5 * T, (6, 0, 0)), (0.6 * T, (0, 0, 0)))
    return c.finish()


def clip_swim(r):
    T = 1.6
    c = Clip(T, True)
    p = r.swim_pitch
    c.rot(r.body, (0, (p, 0, 0)), (T, (p, 0, 0)))
    c.pos(r.body, (0, (0, 0, 0)), (0.35 * T, (0, 0.6, -2.5)), (0.7 * T, (0, 0, -0.8)), (T, (0, 0, 0)))
    for m in r.breathe:
        c.scale(m, (0, 1), (0.35 * T, (0.86, 1.08, 0.86)), (0.65 * T, (1.07, 0.96, 1.07)), (T, 1))
    for i, m in enumerate(r.sway):
        c.sample(m, "rotation", lambda t, i=i: (5 * wave(t, T, 1, -0.8 - 0.4 * i), 0, 0), 8)
    for fin, side in r.fins:
        c.sample(fin, "rotation", lambda t, s=side: (0, s * 26 * wave(t, T, 2), s * 8 * wave(t, T, 2, 0.5)), 16)
    for arm in r.arms:
        for j, seg in enumerate(arm):
            lag = 0.02 * j
            c.rot(seg, (0, (6, 0, 0)), (0.35 * T + lag, (-16 + 2 * j, 0, 0)), (0.68 * T + lag, (16 + 2 * j, 0, 0)), (T, (6, 0, 0)))
    for feeder in r.feeders:
        for j, seg in enumerate(feeder):
            c.rot(seg, (0, (-4, 0, 0)), (0.4 * T + 0.02 * j, (-10, 0, 0)), (0.75 * T + 0.02 * j, (4, 0, 0)), (T, (-4, 0, 0)))
    return c.finish()


def clip_slam(r):
    c = Clip(2.2)
    c.rot(r.body, (0, (0, 0, 0)), (0.75, (-18, 0, 0)), (0.82, (-19, 0, 0)), (0.98, (14, 0, 0), L), (1.2, (8, 0, 0)),
          (1.6, (2, 0, 0)))
    c.pos(r.body, (0, (0, 0, 0)), (0.75, (0, 3, 1.5)), (0.98, (0, -2.5, -5), L), (1.2, (0, -1.5, -3)), (1.8, (0, 0, 0)))
    c.rot(r.jaw, (0, (0, 0, 0)), (0.7, (24, 0, 0)), (0.98, (0, 0, 0), L), (1.3, (6, 0, 0)), (1.6, (0, 0, 0)))
    for arm in r.arms:
        for j, seg in enumerate(arm):
            lag, up = 0.022 * j, 34 + 4 * j
            c.rot(seg, (0, (0, 0, 0)), (0.6, (up, 0, 0)), *tremble(0.66, 0.84, (up, 0, 0), (2, 0, 1.5), 0.045),
                  (0.98 + lag, (-24 - 2 * j, 0, 0), L), (1.15 + lag, (-8, 0, 0)), (1.4 + lag, (-14, 0, 0)), (2.2, (0, 0, 0)))
    for feeder in r.feeders:
        for j, seg in enumerate(feeder):
            c.rot(seg, (0, (0, 0, 0)), (0.7, (-20, 0, 0)), (0.98 + 0.02 * j, (26, 0, 0), L), (1.4, (6, 0, 0)))
    for m in r.breathe:
        c.scale(m, (0, 1), (0.75, (1.1, 1.04, 1.1)), (0.98, (0.88, 1.12, 0.88), L), (1.3, (1.04, 0.98, 1.04)), (2.2, 1))
    for fin, side in r.fins:
        c.rot(fin, (0, (0, 0, 0)), (0.75, (0, side * 30, 0)), (0.98, (0, -side * 20, 0), L), (1.5, (0, 0, 0)))
    for m in r.sway:
        c.rot(m, (0, (0, 0, 0)), (0.75, (-8, 0, 0)), (0.98, (10, 0, 0), L), (1.5, (0, 0, 0)))
    return c.finish()


def clip_roar(r):
    c = Clip(2.4)
    c.rot(r.body, (0, (0, 0, 0)), (0.5, (-12, 0, 0)), *tremble(0.6, 1.9, (-14, 0, 0), (1.2, 1.2, 1.5), 0.06), (2.4, (0, 0, 0)))
    c.rot(r.head, (0, (0, 0, 0)), (0.5, (-6, 0, 0)), (1.9, (-8, 0, 0)), (2.4, (0, 0, 0)))
    c.rot(r.jaw, (0, (0, 0, 0)), (0.45, (10, 0, 0)), (0.6, (38, 0, 0), L), *tremble(0.65, 1.9, (38, 0, 0), (3, 0, 0), 0.08),
          (2.2, (0, 0, 0)))
    for arm in r.arms:
        for j, seg in enumerate(arm):
            c.rot(seg, (0, (0, 0, 0)), (0.5, (-10, 0, 0)), (0.7, (36 + 3 * j, 0, 0), L),
                  *tremble(0.75, 1.9, (36 + 3 * j, 0, 0), (2.5, 0, 2), 0.07), (2.4, (0, 0, 0)))
    for feeder in r.feeders:
        for j, seg in enumerate(feeder):
            c.rot(seg, (0, (0, 0, 0)), (0.7, (-26, 0, 0), L), *tremble(0.75, 1.9, (-26, 0, 0), (2, 0, 2), 0.07), (2.4, (0, 0, 0)))
    for m in r.breathe:
        c.scale(m, (0, 1), (0.5, (1.14, 1.06, 1.14)), (1.9, (1.1, 1.04, 1.1)), (2.4, 1))
    for fin, side in r.fins:
        c.rot(fin, (0, (0, 0, 0)), (0.7, (0, side * 34, side * 10)), *tremble(0.75, 1.9, (0, side * 34, side * 10), (0, 3, 2), 0.07),
              (2.4, (0, 0, 0)))
    return c.finish()


def clip_grab(r):
    c = Clip(2.0)
    c.pos(r.body, (0, (0, 0, 0)), (0.4, (0, 0, 1.5)), (0.6, (0, 0, -3.5), L), (1.4, (0, 0, -1)), (2.0, (0, 0, 0)))
    for feeder in r.feeders:
        for j, seg in enumerate(feeder):
            c.rot(seg, (0, (0, 0, 0)), (0.4, (22, 0, 0)), (0.6 + 0.015 * j, (-6, 0, 0), L), (1.0, (-4, 0, 0)),
                  (1.3, (16, 0, 0)), (1.7, (10, 0, 0)), (2.0, (0, 0, 0)))
    for cl in r.clubs:
        c.rot(cl, (0, (0, 0, 0)), (0.55, (-30, 0, 0)), (0.8, (48, 0, 0), L), *tremble(0.85, 1.3, (48, 0, 0), (4, 0, 0), 0.08),
              (2.0, (0, 0, 0)))
    for arm in r.arms:
        for j, seg in enumerate(arm):
            c.rot(seg, (0, (0, 0, 0)), (0.5, (10, 0, 0)), (0.8, (-10 - 2 * j, 0, 0)), (1.4, (-6, 0, 0)), (2.0, (0, 0, 0)))
    c.rot(r.jaw, (0, (0, 0, 0)), (1.1, (0, 0, 0)), (1.25, (30, 0, 0)), (1.4, (0, 0, 0), L), (2.0, (0, 0, 0)))
    for m in r.breathe:
        c.scale(m, (0, 1), (0.4, (1.08, 1.02, 1.08)), (0.6, (0.9, 1.08, 0.9), L), (1.2, 1))
    return c.finish()


def clip_death(r):
    T = 2.6
    c = Clip(T)
    c.rot(r.body, (0, (0, 0, 0)), *tremble(0.05, 0.7, (0, 0, 0), (6, 4, 8), 0.06), (1.4, (20, 0, 80)), (2.4, (28, 0, 160)),
          (T, (30, 0, 165)))
    c.pos(r.body, (0, (0, 0, 0)), (0.6, (0, 2, 0)), (T, (0, -18, 0)))
    for arm in r.arms:
        for j, seg in enumerate(arm):
            c.rot(seg, (0, (0, 0, 0)), (0.6, (24, 0, 0)), (T, (-30 - 4 * j, 0, 0)))
    for feeder in r.feeders:
        for j, seg in enumerate(feeder):
            c.rot(seg, (0, (0, 0, 0)), (0.6, (-10, 0, 0)), (T, (-40, 0, 0)))
    for m in r.breathe:
        c.scale(m, (0, 1), (0.7, (1.1, 1.05, 1.1)), (T, (0.82, 0.9, 0.82)))
    for fin, side in r.fins:
        c.rot(fin, (0, (0, 0, 0)), (0.7, (0, side * 20, 0)), (T, (0, -side * 10, side * 20)))
    c.rot(r.jaw, (0, (0, 0, 0)), (0.6, (30, 0, 0)), (T, (20, 0, 0)))
    for lid, off in r.lids:
        c.pos(lid, (0, (0, 0, 0)), (2.0, (0, 0, 0)), (2.4, off, L), (T, off))
    return c.finish()


CLIPS = {"idle": clip_idle, "swim": clip_swim, "slam": clip_slam, "roar": clip_roar, "grab": clip_grab, "death": clip_death}


# ================================================================ saída

def data_uri(img):
    buf = io.BytesIO()
    img.save(buf, "PNG", optimize=True)
    return "data:image/png;base64," + base64.b64encode(buf.getvalue()).decode("ascii")


def build(version):
    factory, label = CANDIDATES[version]
    cr, rig = factory()
    for name, fn in CLIPS.items():
        cr.clips[name] = fn(rig)
    for size in ((256, 256), (256, 384), (256, 512)):
        cr.tex_size = size
        try:
            bm.pack(cr)
            break
        except SystemExit:
            continue
    img, glow = bm.paint(cr)
    uv = bm.uv_guide(cr)
    out = BASE / version
    out.mkdir(parents=True, exist_ok=True)
    img.save(out / "kraken.png", optimize=True)
    glow.save(out / "kraken_glow.png", optimize=True)
    uv.save(out / "uv.png", optimize=True)
    (out / "kraken.bbmodel").write_text(json.dumps(bm.bbmodel(cr), separators=(",", ":")), encoding="utf-8")
    rig_data = bm.rig_json(cr)
    (out / "rig.json").write_text(json.dumps(rig_data, separators=(",", ":")), encoding="utf-8")
    snapshot = rig_data | {"id": "kraken", "version": version, "texture": data_uri(img), "glow": data_uri(glow),
                           "uv": data_uri(uv), "notes": cr.description}
    staging = ROOT / "build/kraken_v3"  # o snapshot só vira arquivo versionado pelo register_version (tools/atlas/versions)
    staging.mkdir(parents=True, exist_ok=True)
    (staging / f"kraken-{version}.json").write_text(json.dumps(snapshot, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")
    print(f"{version}: {len(cr.parts)} ossos, {len(cr.cubes())} cubos, {len(cr.clips)} clipes, textura {cr.tex_size[0]}x{cr.tex_size[1]}")
    return snapshot, label


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--register", action="store_true", help="registra as versões novas no Atlas (não sobrescreve)")
    parser.add_argument("versions", nargs="*", default=list(CANDIDATES))
    args = parser.parse_args()
    built = [build(v) for v in args.versions]
    if args.register:
        sys.path.insert(0, str(ROOT / "tools/atlas"))
        from register_version import register
        for snapshot, label in built:
            register(snapshot, label)
            print(f"registrada: kraken/{snapshot['version']}")
