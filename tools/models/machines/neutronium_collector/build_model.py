#!/usr/bin/env python3
"""Coletor de Neutrônio 3×2×3: altar industrial de latão, vidro e energia estelar.

    C:\\Python314\\python tools/models/machines/neutronium_collector/build_model.py [--preview] [--register]

O controlador fica no centro da base. Coordenadas do kit: x esquerda→direita, y chão→alto e z frente→fundo;
o volume completo vai de -16 a 32 em x/z e de 0 a 32 em y. A folha de 512 px é exclusiva desta máquina.
"""
from __future__ import annotations

import math
import sys
from pathlib import Path

BASE = Path(__file__).resolve().parent
sys.path.insert(0, str(BASE.parent))
import machine_kit as k  # noqa: E402
from machine_kit import (BRASS, COPPER, CYAN, GLASS, IVORY, PATINA, STEAM, STEEL, Clip, L)  # noqa: E402

PEARL = k.ramp("172336", "273d59", "426887", "73a7bc", "b9dbe0", "f3ffff")
STAR = k.ramp("18233f", "294b76", "397aa0", "67bed0", "b8f2f3", "ffffff")
RED = k.ramp("360b0d", "651318", "98202a", "cf3840", "f06b70", "ffc2c0")
AMBER = k.ramp("4b2408", "76400d", "a86216", "dc8b24", "f6bd54", "ffe2a0")


def metal(c, ramp=BRASS, level=3):
    k.plate(c, ramp, level)
    if c.side:
        k.rivets(c, ramp, 8, 0, "t")
    else:
        k.corner_bolts(c, STEEL)
        k.rivets(c, ramp, 8, 1)
    if c.w >= 12 and c.h >= 6:
        k.scratches(c, ramp, 4)


def brass(c):
    metal(c, BRASS, 3)
    if c.side and c.w > 4:
        k.patina(c, ((2, c.h - 2), (c.w - 3, c.h - 2)), 1)


def copper(c):
    metal(c, COPPER, 3)
    if c.side:
        k.patina(c, ((1, 1), (c.w // 2, c.h - 1)), 2)


def steel(c):
    metal(c, STEEL, 3)
    if c.w > 10:
        k.seam_v(c, c.w // 2, STEEL, 1, c.h)


def grate(c):
    k.plate(c, STEEL, 2)
    if c.face in ("front", "back", "left", "right"):
        for x in range(2, c.w - 1, 4):
            for y in range(1, c.h - 1):
                c.put(x, y, STEEL[0])
                if x + 1 < c.w:
                    c.put(x + 1, y, STEEL[4] if y % 4 else PATINA[3])
        k.rivets(c, BRASS, 8, 0, "t")


def glass(c):
    c.fill((*GLASS[2][:3], 34))
    for y in range(c.h):
        for x in range(c.w):
            edge = x in (0, c.w - 1) or y in (0, c.h - 1)
            if edge:
                c.put(x, y, (*GLASS[1 if x + y > c.w else 4][:3], 145))
            elif x + y in (4, 5, c.w - 4):
                c.put(x, y, (*GLASS[5][:3], 115))
    if c.w > 10 and c.h > 8:
        for x, y in ((c.w - 3, 2), (c.w - 5, 4), (3, c.h - 3)):
            c.put(x, y, (*STEAM[5][:3], 165))


def neutron(c):
    cx, cy = (c.w - 1) / 2, (c.h - 1) / 2
    for y in range(c.h):
        for x in range(c.w):
            d = math.hypot((x - cx) / max(1, c.w), (y - cy) / max(1, c.h))
            band = k.clamp(5 - int(d * 10), 1, 5)
            col = STAR[band]
            c.put(x, y, col)
            c.lit(x, y, PEARL[min(5, band + 1)])
    for x, y in ((0, 0), (c.w - 1, 1), (1, c.h - 1)):
        c.put(x, y, PEARL[5])
        c.lit(x, y, PEARL[5])


def stardust(c):
    for y in range(c.h):
        for x in range(c.w):
            col = PEARL[5] if x + y < max(c.w, c.h) else PEARL[3]
            c.put(x, y, col)
            c.lit(x, y, PEARL[5] if (x + y) % 2 == 0 else PEARL[4])


def arc(c):
    for y in range(c.h):
        for x in range(c.w):
            col = CYAN[5] if (x + y) % 3 == 0 else CYAN[3]
            c.put(x, y, col)
            c.lit(x, y, CYAN[5])


def lamp(ramp):
    def paint(c):
        k.plate(c, BRASS, 2)
        for y in range(1, c.h - 1):
            for x in range(1, c.w - 1):
                c.put(x, y, ramp[4 if x + y < c.w else 3])
                c.lit(x, y, ramp[5])
    return paint


def dial(c):
    k.plate(c, BRASS, 3)
    if c.face != "front":
        return
    disc = k.disc_mask(c.w, c.h)
    inner = k.disc_mask(c.w, c.h, 1.0)
    for y in range(c.h):
        for x in range(c.w):
            if (x, y) not in disc:
                c.put(x, y, k.CLEAR)
            elif (x, y) in inner:
                c.put(x, y, IVORY[4 if y < c.h / 2 else 3])
    for i in range(7):
        a = math.radians(-140 + i * 46)
        x = round((c.w - 1) / 2 + 3.5 * math.cos(a))
        y = round((c.h - 1) / 2 + 3.5 * math.sin(a))
        c.put(x, y, CYAN[3] if i < 5 else AMBER[3])
    k.text(c, 2, c.h - 6, "64", STEEL[0])


def ring(c):
    k.plate(c, BRASS, 4)
    if c.side:
        for x in range(0, c.w, 4):
            c.put(x, 0, BRASS[5])
            if c.h > 1:
                c.put(x, 1, PATINA[3])


def tray_glass(c):
    glass(c)
    if c.face == "front":
        for x in range(3, c.w - 2, 8):
            c.put(x, c.h - 2, BRASS[4])


def build():
    m = k.Machine("neutronium_collector", "NeutroniumCollector", "Coletor de Neutrônio",
                  "altar industrial 3×2×3 com bocas de captação, câmara giroscópica de vidro, núcleo estelar, arcos de energia e bandeja visível.",
                  sheet=512, item_scale=0.27, preview_scale=5.5)
    m.bone("frame", None, (8, 0, 8))
    m.bone("capture", "frame", (8, 7, -4))
    m.bone("chamber", "frame", (8, 17, 8))
    m.bone("core", "chamber", (8, 19, 8))
    m.bone("charge", "core", (8, 19, 8))
    m.bone("ring_y", "chamber", (8, 19, 8))
    m.bone("ring_x", "chamber", (8, 19, 8), (0, 0, 58))
    m.bone("ring_z", "chamber", (8, 19, 8), (58, 0, 0))
    m.bone("orbit", "core", (8, 19, 8))
    m.bone("tray", "frame", (8, 4, -8))
    m.bone("drop", "core", (8, 19, 1))
    m.bone("needle", "frame", (-7.5, 16, -5.2), (0, 0, -105))
    for name, pivot in (("lamp_red", (23, 26, -5)), ("lamp_amber", (23, 26, -2)), ("lamp_blue", (23, 26, 1))):
        m.bone(name, "frame", pivot)
    for i in range(8):
        a = 2 * math.pi * i / 8
        m.bone(f"orbit_dust_{i}", "orbit", (8 + 7 * math.cos(a), 19 + (i % 2) * 1.5, 8 + 7 * math.sin(a)))
    for i in range(8):
        m.bone(f"arc_{i}", "frame", (8, 15, 8))
    for i in range(16):
        x = 2.5 + (i % 8) * 1.55
        z = -10.5 + (i // 8) * 2.1
        m.bone(f"tray_dust_{i}", "tray", (x, 4, z))

    # Fundação 3×3: placas chanfradas, frisos e pilares; a repetição modular reforça a escala sem faces vazias.
    for zi, z in enumerate((-15.5, 0.5, 16.5)):
        for xi, x in enumerate((-15.5, 0.5, 16.5)):
            idx = zi * 3 + xi
            m.box(f"base_{idx}", "frame", (x, 0, z), (15, 2, 15), steel,
                  hide=("bottom",), same={"back": "front", "right": "left"}, uv_from=None if idx == 0 else "base_0")
            m.box(f"deck_{idx}", "frame", (x + .5, 2, z + .5), (14, 1, 14), brass,
                  same={"bottom": "top", "back": "front", "right": "left"}, uv_from=None if idx == 0 else "deck_0")
    for i, x in enumerate((-15, -7, 1, 9, 17, 25)):
        m.box(f"front_rail_{i}", "frame", (x, 3, -15.5), (6, 2, 1), brass,
              same={"back": "front"}, uv_from=None if i == 0 else "front_rail_0")
        m.box(f"back_rail_{i}", "frame", (x, 3, 30.5), (6, 2, 1), brass,
              same={"back": "front"}, uv_from="front_rail_0")
    for i, z in enumerate((-9, -1, 7, 15, 23)):
        m.box(f"left_rail_{i}", "frame", (-15.5, 3, z), (1, 2, 6), brass,
              same={"right": "left"}, uv_from=None if i == 0 else "left_rail_0")
        m.box(f"right_rail_{i}", "frame", (30.5, 3, z), (1, 2, 6), brass,
              same={"right": "left"}, uv_from="left_rail_0")
    for i, (x, z) in enumerate(((-14, -14), (29, -14), (-14, 29), (29, 29), (-1, -14), (16, -14), (-1, 29), (16, 29))):
        m.box(f"post_{i}", "frame", (x, 3, z), (2, 11, 2), copper,
              same={"back": "front", "right": "left"}, uv_from=None if i == 0 else "post_0")
        m.box(f"post_cap_{i}", "frame", (x - .5, 14, z - .5), (3, 1, 3), brass,
              same={"bottom": "top", "back": "front", "right": "left"}, uv_from=None if i == 0 else "post_cap_0")
    # Oito tampas de manutenção quebram o convés em módulos legíveis e mostram a direção do fluxo até o centro.
    for i, (x, z) in enumerate(((-11, -11), (5, -11), (21, -11), (-11, 5), (21, 5),
                                (-11, 21), (5, 21), (21, 21))):
        m.box(f"service_panel_{i}", "frame", (x, 3.05, z), (6, .5, 6), grate,
              hide=("bottom",), same={"back": "front", "right": "left"},
              uv_from=None if i == 0 else "service_panel_0")

    # Duas bocas de captação na frente: grade, lábios em degraus e braços que convergem para a câmara.
    for side, x in (("l", -12), ("r", 20)):
        for j, (dx, y, z, w, h, d) in enumerate(((0, 3, -15, 8, 2, 2), (-1, 5, -14, 10, 2, 2),
                                                   (0, 7, -13, 8, 2, 2), (1, 9, -12, 6, 1, 2))):
            m.box(f"funnel_{side}_{j}", "capture", (x + dx, y, z), (w, h, d), brass,
                  same={"back": "front"}, uv_from=None if side == "l" else f"funnel_l_{j}")
        m.box(f"funnel_grate_{side}", "capture", (x + 1, 4, -15.6), (6, 5, .5), grate, hide=("back",),
              uv_from=None if side == "l" else "funnel_grate_l")
        m.box(f"arm_low_{side}", "capture", (x + 3, 6, -10), (2, 2, 14), copper,
              same={"back": "front", "right": "left"}, uv_from=None if side == "l" else "arm_low_l")
        m.box(f"arm_high_{side}", "capture", (x + 3, 11, 2), (2, 7, 2), copper,
              same={"back": "front", "right": "left"}, uv_from=None if side == "l" else "arm_high_l")
        m.box(f"intake_chute_{side}", "capture", (x + 1, 4, -8), (6, 1, 10), grate,
              hide=("bottom",), same={"back": "front", "right": "left"},
              uv_from=None if side == "l" else "intake_chute_l")
    m.box("intake_bridge", "capture", (-7, 11, 3), (30, 2, 3), brass, same={"back": "front"})

    # Câmara octogonal: oito vidros estreitos em volta do núcleo, cintas, costelas e teto escalonado.
    m.box("chamber_floor", "chamber", (-2, 13, -2), (20, 2, 20), brass,
          same={"bottom": "top", "back": "front", "right": "left"})
    m.box("chamber_step", "chamber", (0, 15, 0), (16, 1, 16), steel,
          same={"bottom": "top", "back": "front", "right": "left"})
    panes = [(-1, 16, -2, 18, 10, .5), (-1, 16, 17.5, 18, 10, .5), (-2, 16, -1, .5, 10, 18),
             (17.5, 16, -1, .5, 10, 18), (-1.5, 16, -1.5, 3, 10, .5), (14.5, 16, -1.5, 3, 10, .5),
             (-1.5, 16, 17, 3, 10, .5), (14.5, 16, 17, 3, 10, .5)]
    for i, (x, y, z, w, h, d) in enumerate(panes):
        m.box(f"glass_{i}", "chamber", (x, y, z), (w, h, d), glass, glass=True,
              uv_from=None if i in (0, 2, 4) else "glass_0" if i in (1,) else "glass_2" if i == 3 else "glass_4")
    for i, (x, z) in enumerate(((-2, -2), (16, -2), (-2, 16), (16, 16), (7, -2), (7, 16), (-2, 7), (16, 7))):
        size = (2, 11, 2) if i < 4 else (2, 11, 2)
        m.box(f"rib_{i}", "chamber", (x, 15, z), size, brass,
              same={"back": "front", "right": "left"}, uv_from=None if i == 0 else "rib_0")
    m.box("chamber_lid", "chamber", (-2, 26, -2), (20, 2, 20), brass,
          same={"bottom": "top", "back": "front", "right": "left"})
    m.box("chamber_crown", "chamber", (1, 28, 1), (14, 2, 14), steel,
          same={"bottom": "top", "back": "front", "right": "left"})

    # Três anéis giroscópicos de oito segmentos, todos com pivô no núcleo.
    ring_segments = [((-10, -.75, -10), (8, 1.5, 1.5)), ((2, -.75, -10), (8, 1.5, 1.5)),
                     ((-10, -.75, 8.5), (8, 1.5, 1.5)), ((2, -.75, 8.5), (8, 1.5, 1.5)),
                     ((-10, -.75, -8.5), (1.5, 1.5, 8)), ((-10, -.75, .5), (1.5, 1.5, 8)),
                     ((8.5, -.75, -8.5), (1.5, 1.5, 8)), ((8.5, -.75, .5), (1.5, 1.5, 8))]
    for bone in ("ring_y", "ring_x", "ring_z"):
        for i, (at, size) in enumerate(ring_segments):
            name = f"{bone}_{i}"
            m.box(name, bone, (8 + at[0], 19 + at[1], 8 + at[2]), size, ring,
                  same={"bottom": "top", "back": "front", "right": "left"},
                  uv_from=None if bone == "ring_y" and i == 0 else "ring_y_0" if i in (0, 1, 2, 3) else None)

    # Núcleo estrelado em camadas e poeira orbitando.
    for name, at, size in (("core_center", (5, 16, 5), (6, 6, 6)), ("core_x", (1, 18, 6), (14, 2, 4)),
                           ("core_y", (6, 12, 6), (4, 14, 4)), ("core_z", (6, 18, 1), (4, 2, 14)),
                           ("core_hi", (4, 15, 4), (8, 8, 8)), ("core_glint_l", (2, 17, 7), (3, 4, 2)),
                           ("core_glint_r", (11, 17, 7), (3, 4, 2))):
        m.box(name, "charge", at, size, neutron, same={"back": "front", "right": "left"})
    for i in range(8):
        p = m.bones[f"orbit_dust_{i}"].pivot
        m.box(f"orbit_dust_{i}", f"orbit_dust_{i}", (p[0] - .75, p[1] - .75, p[2] - .75), (1.5, 1.5, 1.5), stardust,
              same={"back": "front", "right": "left", "bottom": "top"}, uv_from=None if i == 0 else "orbit_dust_0")

    # Bobinas, cabos, medidores e arcos de carga nas quatro bordas.
    for i, (x, z) in enumerate(((-11, 5), (25, 5), (-11, 16), (25, 16))):
        m.box(f"coil_body_{i}", "frame", (x, 6, z), (2, 8, 5), copper,
              same={"back": "front", "right": "left"}, uv_from=None if i == 0 else "coil_body_0")
        for j in range(3):
            m.box(f"coil_band_{i}_{j}", "frame", (x - .5, 7 + j * 2.5, z - .5), (3, 1, 6), brass,
                  same={"back": "front", "right": "left"}, uv_from=None if i == 0 and j == 0 else "coil_band_0_0")
    for i, (x, z, w, d) in enumerate(((-9, 8, 13, 1), (12, 8, 13, 1), (7.5, -8, 1, 14), (7.5, 18, 1, 12))):
        m.box(f"cable_{i}", "frame", (x, 12, z), (w, 1, d), copper,
              same={"bottom": "top", "back": "front", "right": "left"})
    for i, (x, y, z, w, h, d) in enumerate(((-7, 14, 7, 10, .5, .5), (13, 14, 7, 10, .5, .5),
                                               (7.5, 14, -3, .5, .5, 10), (7.5, 14, 13, .5, .5, 10),
                                               (-4, 17, 7, 8, .5, .5), (12, 17, 7, 8, .5, .5),
                                               (7.5, 17, 0, .5, .5, 8), (7.5, 17, 12, .5, .5, 8))):
        m.box(f"arc_{i}", f"arc_{i}", (x, y, z), (w, h, d), arc,
              same={"bottom": "top", "back": "front", "right": "left"})
    for i, (x, z, w, d) in enumerate(((-7, 6.5, 7, .5), (16, 6.5, 7, .5), (7.75, -7, .5, 7), (7.75, 16, .5, 7))):
        m.box(f"deck_arc_{i}", "frame", (x, 3.6, z), (w, .5, d), arc,
              same={"bottom": "top", "back": "front", "right": "left"})
    m.box("gauge_left", "frame", (-12, 16, -5.5), (9, 9, 1), dial, hide=("back",))
    m.box("gauge_right", "frame", (18, 16, -5.5), (9, 9, 1), dial, hide=("back",), uv_from="gauge_left")
    m.box("needle", "needle", (-7.75, 15.5, -6), (.5, 4, .5), arc,
          same={"back": "front", "right": "left"})
    for name, x, paint in (("lamp_red", 21.5, lamp(RED)), ("lamp_amber", 21.5, lamp(AMBER)), ("lamp_blue", 21.5, lamp(STAR))):
        z = {"lamp_red": -6.5, "lamp_amber": -3.5, "lamp_blue": -.5}[name]
        m.box(name, name, (x, 24.5, z), (3, 3, 1), paint, hide=("back",))

    # Bandeja frontal: vidro e dezesseis grãos que aparecem pelo estoque sincronizado.
    m.box("tray_floor", "tray", (0, 3, -13), (16, 1, 10), brass,
          same={"bottom": "top", "back": "front", "right": "left"})
    m.box("tray_front", "tray", (0, 4, -13), (16, 4, 1), tray_glass, glass=True, hide=("back",))
    m.box("tray_back", "tray", (0, 4, -4), (16, 4, 1), tray_glass, glass=True, hide=("front",))
    m.box("tray_left", "tray", (0, 4, -12), (1, 4, 8), tray_glass, glass=True, hide=("right",))
    m.box("tray_right", "tray", (15, 4, -12), (1, 4, 8), tray_glass, glass=True, hide=("left",))
    for i in range(16):
        p = m.bones[f"tray_dust_{i}"].pivot
        m.box(f"tray_dust_{i}", f"tray_dust_{i}", (p[0] - .55, 4.1 + (i % 3) * .35, p[2] - .55), (1, 1, 1), stardust,
              same={"back": "front", "right": "left", "bottom": "top"}, uv_from=None if i == 0 else "tray_dust_0")
    m.box("drop", "drop", (7.25, 18, .25), (1.5, 1.5, 1.5), stardust,
          same={"back": "front", "right": "left", "bottom": "top"}, uv_from="orbit_dust_0")

    m.states = {
        "idle": {"label": "Parado", "loop": "idle"},
        "run": {"label": "Funcionando", "enter": "start", "loop": "run", "leave": "stop"},
        "full": {"label": "Cheio", "loop": "full"},
    }
    m.descriptions = {
        "idle": "Anéis pesados em repouso, núcleo mínimo e lâmpada âmbar; o vidro ainda reflete a água.",
        "start": "As bobinas carregam em sequência, os três eixos vencem a inércia e a energia converge no núcleo.",
        "run": "Anéis em eixos diferentes aceleram, arcos ciano avançam e o pó espirala para a compressão.",
        "full": "Núcleo branco-azulado estável, bandeja cheia e lâmpada azul-branca pulsando em aviso.",
        "stop": "Os eixos freiam em tempos diferentes e o brilho retorna ao ponto frio de repouso.",
        "collect": "Pulso de compressão, breve freio e um grão perolado cai na bandeja.",
    }
    return m


def ring_keys(c, bone, axis, turns=1.0, lag=0.0):
    keys = []
    for i in range(9):
        t = c.length * i / 8
        v = [0.0, 0.0, 0.0]
        v[axis] = 360 * turns * i / 8 + lag * math.sin(math.pi * i / 4)
        keys.append((t, tuple(v), L))
    c.rot(bone, *keys)


def lamp_pose(c, red=.01, amber=.01, blue=.01):
    c.scale("lamp_red", (0, red), (c.length, red))
    c.scale("lamp_amber", (0, amber), (c.length, amber))
    c.scale("lamp_blue", (0, blue), (c.length, blue))


def clips(m):
    idle = Clip(4, True)
    idle.scale("core", (0, 1), (1.1, 1.04), (2.2, .98), (3.2, 1.03), (4, 1))
    idle.rot("orbit", (0, (0, 0, 0)), (4, (0, 18, 0)))
    idle.rot("needle", (0, (0, 0, 0)), (1.4, (0, 0, 1.5)), (2.8, (0, 0, -1)), (4, (0, 0, 0)))
    idle.scale("drop", (0, 0), (4, 0))
    lamp_pose(idle, amber=1)
    m.clips["idle"] = idle.finish()

    start = Clip(1.6)
    start.scale("core", (0, 1), (.45, 1.03), (1.1, .98), (1.6, 1))
    start.rot("ring_y", (0, (0, 0, 0)), (.35, (0, 4, 0)), (.8, (0, 22, 0)), (1.2, (0, 58, 0)), (1.6, (0, 92, 0)))
    start.rot("ring_x", (0, (0, 0, 0)), (.5, (0, -3, 0)), (1.0, (0, -25, 0)), (1.6, (0, -86, 0)))
    start.rot("ring_z", (0, (0, 0, 0)), (.6, (0, 0, 2)), (1.15, (0, 0, 30)), (1.6, (0, 0, 78)))
    start.rot("needle", (0, (0, 0, 0)), (.7, (0, 0, 25)), (1.25, (0, 0, 132)), (1.45, (0, 0, 118)), (1.6, (0, 0, 122)))
    start.scale("drop", (0, 0), (1.6, 0))
    lamp_pose(start, amber=1, blue=1)
    m.clips["start"] = start.finish()

    run = Clip(2.4, True)
    ring_keys(run, "ring_y", 1, 1.0, 4)
    ring_keys(run, "ring_x", 1, -1.25, -3)
    ring_keys(run, "ring_z", 2, .75, 2)
    run.rot("orbit", *[(i * .3, (0, i * 45, 0), L) for i in range(9)])
    run.scale("core", (0, 1), (.3, 1.05), (.6, .98), (.9, 1.04), (1.2, 1), (1.5, 1.06), (1.8, .99), (2.1, 1.03), (2.4, 1))
    run.rot("needle", (0, (0, 0, 120)), (.4, (0, 0, 128)), (.8, (0, 0, 116)), (1.2, (0, 0, 126)), (1.6, (0, 0, 119)), (2.0, (0, 0, 127)), (2.4, (0, 0, 120)))
    for i in range(8):
        phase = i * .3
        run.scale(f"arc_{i}", (0, .05), (phase % 2.4, .05), ((phase + .18) % 2.4, 1.25), ((phase + .42) % 2.4, .05), (2.4, .05))
        run.pos(f"orbit_dust_{i}", (0, (0, 0, 0)), (.6, (0, .5, 0)), (1.2, (0, -.25, 0)), (1.8, (0, .4, 0)), (2.4, (0, 0, 0)))
    run.scale("drop", (0, 0), (2.4, 0))
    lamp_pose(run, blue=1)
    m.clips["run"] = run.finish()

    stop = Clip(1.25)
    stop.rot("ring_y", (0, (0, 95, 0)), (.35, (0, 145, 0)), (.75, (0, 172, 0)), (1.05, (0, 180, 0)), (1.25, (0, 180, 0)))
    stop.rot("ring_x", (0, (0, -90, 0)), (.4, (0, -132, 0)), (.8, (0, -154, 0)), (1.25, (0, -160, 0)))
    stop.rot("ring_z", (0, (0, 0, 80)), (.35, (0, 0, 116)), (.8, (0, 0, 130)), (1.25, (0, 0, 132)))
    stop.scale("core", (0, 1), (.25, 1.08), (.65, .98), (1.25, 1))
    stop.rot("needle", (0, (0, 0, 120)), (.45, (0, 0, 78)), (.95, (0, 0, -5)), (1.25, (0, 0, 0)))
    stop.scale("drop", (0, 0), (1.25, 0))
    lamp_pose(stop, amber=1, blue=1)
    m.clips["stop"] = stop.finish()

    full = Clip(2, True)
    ring_keys(full, "ring_y", 1, .18)
    ring_keys(full, "ring_x", 1, -.14)
    ring_keys(full, "ring_z", 2, .1)
    full.scale("core", (0, 1.12), (.5, 1.16), (1, 1.12), (1.5, 1.16), (2, 1.12))
    full.scale("lamp_blue", (0, .35), (.2, 1.2), (.65, .55), (1.0, 1.2), (1.45, .35), (2, .35))
    full.scale("lamp_red", (0, .01), (2, .01)); full.scale("lamp_amber", (0, .01), (2, .01))
    full.scale("drop", (0, 0), (2, 0))
    m.clips["full"] = full.finish()

    collect = Clip(1.2)
    collect.scale("core", (0, 1), (.18, 1.5), (.36, .78), (.62, 1.12), (1.2, 1))
    collect.rot("ring_y", (0, (0, 0, 0)), (.18, (0, 55, 0)), (.48, (0, 72, 0)), (.82, (0, 104, 0)), (1.2, (0, 160, 0)))
    collect.rot("ring_x", (0, (0, 0, 0)), (.18, (0, -48, 0)), (.5, (0, -64, 0)), (1.2, (0, -145, 0)))
    collect.rot("ring_z", (0, (0, 0, 0)), (.2, (0, 0, 42)), (.5, (0, 0, 55)), (1.2, (0, 0, 138)))
    collect.scale("drop", (0, 0), (.28, 0), (.34, 1), (.9, 1), (.96, 0), (1.2, 0))
    collect.pos("drop", (0, (0, 0, 0)), (.34, (0, 0, 0)), (.58, (0, -7, -2)), (.82, (0, -13, -5)), (.96, (0, -15, -7)), (1.2, (0, -15, -7)))
    lamp_pose(collect, blue=1)
    m.clips["collect"] = collect.finish()

    progress = Clip(1)
    progress.scale("charge", (0, .28, L), (1, 1, L))
    for i in range(8):
        progress.pos(f"orbit_dust_{i}", (0, (0, 0, 0), L), (1, (0, -i * .04, 0), L))
    m.clips["progress"] = progress.finish()

    stock = Clip(1)
    for i in range(16):
        threshold = (i + 1) / 16
        stock.scale(f"tray_dust_{i}", (0, .01, L), (max(0, threshold - .05), .01, L), (threshold, 1, L), (1, 1, L))
    m.clips["stock"] = stock.finish()

    invalid = Clip(1)
    invalid.scale("lamp_red", (0, 1.5, L), (.15, 2.2, L), (.32, 1.7, L), (.5, 2.4, L),
                  (.68, 1.6, L), (.85, 2.1, L), (1, 1.5, L))
    invalid.scale("lamp_amber", (0, .01, L), (1, .01, L))
    invalid.scale("lamp_blue", (0, .01, L), (1, .01, L))
    m.clips["invalid"] = invalid.finish()


def shots(m):
    fl = math.radians(140)
    return [
        ("idle", [(m.clips["idle"], 1.0, 1), (m.clips["progress"], 0.0, 1), (m.clips["stock"], 0.0, 1)], fl, math.radians(22)),
        ("run começo", [(m.clips["run"], .3, 1), (m.clips["progress"], .12, 1), (m.clips["stock"], .18, 1)], fl, math.radians(22)),
        ("run fim", [(m.clips["run"], 1.65, 1), (m.clips["progress"], .92, 1), (m.clips["stock"], .55, 1)], math.radians(155), math.radians(18)),
        ("full", [(m.clips["full"], .25, 1), (m.clips["progress"], 1, 1), (m.clips["stock"], 1, 1)], fl, math.radians(24)),
        ("inválido", [(m.clips["idle"], 2, 1), (m.clips["invalid"], 1, 1)], math.radians(130), math.radians(18)),
        ("coleta", [(m.clips["collect"], .65, 1), (m.clips["progress"], 1, 1), (m.clips["stock"], .62, 1)], math.radians(180), math.radians(10)),
    ]


if __name__ == "__main__":
    machine = build()
    clips(machine)
    k.write(machine, BASE, preview_shots=shots(machine) if "--preview" in sys.argv else None,
            register=("v3", "No jogo · Coletor 3×2×3, progresso separado e alerta pulsante") if "--register" in sys.argv else None,
            particle="aquamarine:block/machine/neutronium_collector")
