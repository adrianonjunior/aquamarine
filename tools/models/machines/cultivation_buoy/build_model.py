#!/usr/bin/env python3
"""Boia de Cultivo: flutuador octogonal, mastro com argola, lanterna sinalizadora e bandeirola."""
from __future__ import annotations

import math
import sys
from pathlib import Path

BASE = Path(__file__).resolve().parent
sys.path.insert(0, str(BASE.parent))
import machine_kit as k  # noqa: E402
from cultivation_common import (BRASS, COPPER, CYAN, GLASS, IVORY, LEATHER, MAGMA, PATINA, STEAM, STEEL,
                                brass, cord, glass)  # noqa: E402
from machine_kit import Clip  # noqa: E402


def ivory(c):
    k.plate(c, IVORY, 3)
    if c.side:
        for x in range(2, c.w, 8):
            c.put(x, 0, IVORY[5])
        k.stain(c, max(1, c.w - 4), max(1, c.h - 2), 2, STEAM[4], 0.35)
    if c.w > 8:
        k.scratches(c, IVORY, 2)


def copper_band(c):
    k.plate(c, COPPER, 3)
    k.rivets(c, BRASS, 8, 0)
    if c.side:
        k.patina(c, ((2, 0), (max(1, c.w - 3), 0)), 1)


def mast(c):
    k.cylinder(c, BRASS, 3)
    if c.side:
        for y in range(3, c.h, 8):
            c.put(0, y, BRASS[5])
            c.put(c.w - 1, y, PATINA[2])


def cage(c):
    k.plate(c, BRASS, 3)
    if c.side:
        k.rivets(c, BRASS, 8, 0, "t")


def amber(c):
    c.fill(MAGMA[4])
    for y in range(c.h):
        for x in range(c.w):
            c.lit(x, y, k.GLOW_AMBER if (x + y) % 3 else k.GLOW_HOT)


def pearl_light(c):
    c.fill(CYAN[4])
    for y in range(c.h):
        for x in range(c.w):
            c.lit(x, y, STEAM[5] if (x + y) % 3 else CYAN[5])


def flag(c):
    k.plate(c, BRASS, 3)
    if c.face in ("front", "back"):
        for x in range(2, c.w, 4):
            c.put(x, 0, BRASS[5])
            if c.h > 2:
                c.put(x + 1, c.h - 1, BRASS[1])
        k.scratches(c, BRASS, 2)
        k.patina(c, ((1, c.h - 1),), 1)


def loop_wave(clip, bone, amp_y, amp_z, phase=0.0):
    pos, rot = [], []
    for i in range(9):
        t = clip.length * i / 8
        a = 2 * math.pi * i / 8 + phase
        pos.append((t, (0, amp_y * math.sin(a), 0)))
        rot.append((t, (amp_z * 0.35 * math.sin(a + 0.4), 0, amp_z * math.sin(a))))
    clip.pos(bone, *pos).rot(bone, *rot)


def signal_pose(clip, amber_scale, pearl_scale, flag_height):
    clip.scale("amber", (0, amber_scale), (clip.length, amber_scale))
    clip.scale("pearl", (0, pearl_scale), (clip.length, pearl_scale))
    clip.pos("flag", (0, (0, flag_height, 0)), (clip.length, (0, flag_height, 0)))
    return clip


def build():
    m = k.Machine("cultivation_buoy", "CultivationBuoy", "Boia de Cultivo",
        "Anel octogonal de marfim e cobre, argola alinhada à trama, lanterna de dois sinais e bandeirola de latão.")
    m.bone("root", None, (8, 4, 8))

    # Oito segmentos reais formam o octógono. Cobre embaixo e em cima, marfim no corpo e aro rebitado no meio.
    segments = (("n", (8, 3), 0, 6), ("s", (8, 13), 0, 6), ("w", (3, 8), 90, 6), ("e", (13, 8), 90, 6),
                ("nw", (4.5, 4.5), 45, 5), ("ne", (11.5, 4.5), -45, 5),
                ("sw", (4.5, 11.5), -45, 5), ("se", (11.5, 11.5), 45, 5))
    sources = {}
    for name, (cx, cz), angle, length in segments:
        bone = f"ring_{name}"
        m.bone(bone, "root", (cx, 4, cz), (0, angle, 0))
        x, z = cx - length / 2, cz - 1
        kind = "long" if length == 6 else "diag"
        for layer, y, h, paint in (("copper_low", 2, 0.5, copper_band), ("ivory", 2.5, 3, ivory),
                                    ("brass", 4.25, 0.5, brass), ("copper_high", 5.5, 0.5, copper_band)):
            box = f"{layer}_{name}"
            key = (layer, kind)
            m.box(box, bone, (x, y, z), (length, h, 2), paint,
                  same={"back": "front", "bottom": "top", "right": "left"}, uv_from=sources.get(key))
            sources.setdefault(key, box)

    m.bone("mast", "root", (8, 4, 8))
    m.box("mast_foot", "mast", (6.5, 5.5, 6.5), (3, 1, 3), brass,
          same={"back": "front", "right": "left", "bottom": "top"})
    m.box("mast_low", "mast", (7, 6.5, 7), (2, 5, 2), mast,
          same={"back": "front", "right": "left"})
    m.box("mast_high", "mast", (7.25, 11.5, 7.25), (1.5, 3, 1.5), mast,
          same={"back": "front", "right": "left"})

    # A trama chega rente a x=0 e prende na argola no mesmo eixo do Garrafão: y=11,5, z=8.
    for i, (x, y, length) in enumerate(((0, 11.0, 2), (2, 11.0, 2), (4, 10.9, 1.5), (5.5, 11.0, 1.5))):
        source = "lead_0" if i == 1 else "lead_2" if i == 3 else None
        m.box(f"lead_{i}", "root", (x, y, 7), (length, 1, 2), cord,
              same={"back": "front", "bottom": "top", "right": "left"}, uv_from=source)
    m.box("eye_top", "mast", (6.25, 12.25, 7.25), (2.5, 0.5, 1.5), brass,
          same={"back": "front", "bottom": "top"})
    m.box("eye_bottom", "mast", (6.25, 10.25, 7.25), (2.5, 0.5, 1.5), brass,
          same={"back": "front", "bottom": "top"}, uv_from="eye_top")
    m.box("eye_left", "mast", (6.25, 10.75, 7.25), (0.5, 1.5, 1.5), brass,
          same={"back": "front", "right": "left"})
    m.box("knot", "mast", (5.75, 10.75, 6.75), (1.5, 1.5, 2.5), cord,
          same={"back": "front", "right": "left"})

    # Lanterna: base e teto chanfrados, quatro montantes, quatro lâminas de vidro e dois núcleos alternados por estado.
    m.bone("lantern", "mast", (8, 13.75, 8))
    m.box("lantern_base", "lantern", (5.75, 13, 5.75), (4.5, 0.5, 4.5), cage,
          same={"back": "front", "right": "left", "bottom": "top"})
    m.box("lantern_step", "lantern", (6.25, 13.5, 6.25), (3.5, 0.5, 3.5), brass,
          same={"back": "front", "right": "left", "bottom": "top"})
    m.box("lantern_roof", "lantern", (5.75, 15.5, 5.75), (4.5, 0.5, 4.5), cage,
          same={"back": "front", "right": "left", "bottom": "top"}, uv_from="lantern_base")
    m.box("lantern_cap", "lantern", (6.5, 15.5, 6.5), (3, 0.5, 3), brass,
          same={"back": "front", "right": "left", "bottom": "top"})
    for i, (x, z) in enumerate(((6, 6), (9.5, 6), (6, 9.5), (9.5, 9.5))):
        m.box(f"cage_{i}", "lantern", (x, 14, z), (0.5, 1.5, 0.5), cage,
              same={"back": "front", "right": "left"}, uv_from="cage_0" if i else None)
    for name, at, size in (("front", (6.5, 14, 5.9), (3, 1.5, 0.5)), ("back", (6.5, 14, 9.6), (3, 1.5, 0.5)),
                           ("left", (5.9, 14, 6.5), (0.5, 1.5, 3)), ("right", (9.6, 14, 6.5), (0.5, 1.5, 3))):
        m.box(f"glass_{name}", "lantern", at, size, glass, glass=True,
              same={"back": "front", "right": "left"})
    m.bone("amber", "lantern", (8, 14.75, 8))
    m.box("amber_core", "amber", (7, 14, 7), (2, 1.5, 2), amber,
          same={"back": "front", "right": "left", "bottom": "top"})
    m.bone("pearl", "lantern", (8, 14.75, 8))
    m.box("pearl_core", "pearl", (7, 14, 7), (2, 1.5, 2), pearl_light,
          same={"back": "front", "right": "left", "bottom": "top"})

    m.bone("flag", "mast", (8, 13, 8))
    m.box("flag_pole", "mast", (7.5, 11.5, 7.5), (0.5, 4, 0.5), mast,
          same={"back": "front", "right": "left"})
    m.box("flag_root", "flag", (8.5, 13, 7.5), (3, 1.5, 0.5), flag,
          same={"back": "front", "bottom": "top"})
    m.box("flag_tip", "flag", (11.5, 13.5, 7.5), (2.5, 1, 0.5), flag,
          same={"back": "front", "bottom": "top"})

    idle = signal_pose(Clip(4.0, True), 0.0, 0.0, -1.5)
    loop_wave(idle, "root", 0.6, 1.5)
    idle.rot("flag", (0, (0, 0, -5)), (1, (0, 0, 3)), (2, (0, 0, -5)), (3, (0, 0, -11)), (4, (0, 0, -5)))
    m.clips["idle"] = idle.finish()

    start = Clip(1.2)
    start.pos("flag", (0, (0, -1.5, 0)), (0.35, (0, -1.2, 0)), (1.0, (0, 0.1, 0)), (1.2, (0, 0, 0)))
    start.scale("amber", (0, 0.0), (0.45, 0.0), (0.9, 1.12), (1.2, 1.0)).scale("pearl", (0, 0.0), (1.2, 0.0))
    start.pos("root", (0, (0, 0, 0)), (0.45, (0, 0.25, 0)), (0.8, (0, -0.12, 0)), (1.2, (0, 0, 0)))
    m.clips["start"] = start.finish()

    run = signal_pose(Clip(2.0, True), 1.0, 0.0, 0.0)
    loop_wave(run, "root", 0.45, 1.1)
    run.rot("flag", (0, (0, 0, -5)), (0.5, (0, 0, 4)), (1, (0, 0, -5)), (1.5, (0, 0, -12)), (2, (0, 0, -5)))
    run.scale("amber", (0, 0.96), (0.5, 1.05), (1, 0.96), (1.5, 0.88), (2, 0.96))
    m.clips["run"] = run.finish()

    full = signal_pose(Clip(2.0, True), 0.0, 1.0, 1.5)
    loop_wave(full, "root", 0.8, 2.2)
    full.rot("flag", (0, (0, 0, -8)), (0.25, (0, 0, 8)), (0.5, (0, 0, -12)), (0.75, (0, 0, 10)),
             (1, (0, 0, -8)), (1.25, (0, 0, 8)), (1.5, (0, 0, -12)), (1.75, (0, 0, 10)), (2, (0, 0, -8)))
    full.scale("pearl", (0, 0.45), (0.25, 1.15), (0.5, 0.7), (0.75, 1.0), (1, 0.35),
               (1.25, 1.2), (1.5, 0.65), (1.75, 1.05), (2, 0.45))
    m.clips["full"] = full.finish()

    stop = Clip(1.2)
    stop.pos("flag", (0, (0, 0, 0)), (0.55, (0, -1.7, 0)), (0.8, (0, -1.35, 0)), (1.2, (0, -1.5, 0)))
    stop.scale("amber", (0, 1.0), (0.5, 0.75), (0.9, 0.0), (1.2, 0.0)).scale("pearl", (0, 0.0), (1.2, 0.0))
    stop.pos("root", (0, (0, 0, 0)), (0.45, (0, -0.15, 0)), (1.2, (0, 0, 0)))
    m.clips["stop"] = stop.finish()

    harvest = Clip(0.8)
    harvest.pos("root", (0, (0, 0, 0)), (0.18, (0, -1.8, 0)), (0.42, (0, -2.1, 0)),
                (0.62, (0, 0.35, 0)), (0.8, (0, 0, 0)))
    harvest.pos("flag", (0, (0, 1.5, 0)), (0.22, (0, -1.4, 0)), (0.52, (0, 0.55, 0)), (0.8, (0, 0, 0)))
    harvest.rot("flag", (0, (0, 0, -8)), (0.22, (0, 0, -28)), (0.52, (0, 0, 12)), (0.8, (0, 0, -5)))
    harvest.scale("amber", (0, 0.0), (0.8, 0.0)).scale("pearl", (0, 0.4), (0.16, 1.45), (0.38, 0.15), (0.8, 0.0))
    m.clips["harvest"] = harvest.finish()

    m.states = {"idle": {"label": "Parada", "enter": "stop", "loop": "idle"},
                "run": {"label": "Cultivando", "enter": "start", "loop": "run"},
                "full": {"label": "Pérola pronta", "loop": "full"}}
    m.descriptions = {
        "idle": "Flutua devagar, com a lanterna apagada e a bandeirola recolhida.",
        "start": "A lanterna âmbar aquece enquanto a bandeirola sobe até a marca de trabalho.",
        "run": "O sinal âmbar respira em dois segundos e a bandeirola acompanha a correnteza.",
        "full": "O sinal perolado pisca, a bandeirola sobe e o anel acusa o peso da linha.",
        "stop": "O sinal apaga e a bandeirola desce com uma pequena recuperação.",
        "harvest": "A coleta puxa a boia para baixo; a lanterna dá um clarão e a bandeirola cai e volta.",
    }
    return m


if __name__ == "__main__":
    machine = build()
    shots = [("idle", [(machine.clips["idle"], 0.8, 1)], math.radians(140), math.radians(22)),
             ("run", [(machine.clips["run"], 0.5, 1)], math.radians(150), math.radians(18)),
             ("full", [(machine.clips["full"], 0.25, 1)], math.radians(215), math.radians(16)),
             ("harvest", [(machine.clips["harvest"], 0.24, 1)], math.radians(180), math.radians(10))]
    k.write(machine, BASE, preview_shots=shots if "--preview" in sys.argv else None,
            register=("v1", "No jogo · Boia de Cultivo animada") if "--register" in sys.argv else None)

