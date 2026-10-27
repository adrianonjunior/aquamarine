#!/usr/bin/env python3
"""Corda de Cultivo: trança modular, presilhas de latão e pequena ampola que segue a correnteza."""
from __future__ import annotations

import math
import sys
from pathlib import Path

BASE = Path(__file__).resolve().parent
sys.path.insert(0, str(BASE.parent))
import machine_kit as k  # noqa: E402
from cultivation_common import BRASS, GLASS, IVORY, LEATHER, STEAM, brass, cord, glass  # noqa: E402
from machine_kit import Clip  # noqa: E402


def salt(c):
    cord(c)
    for x, y in ((1, 0), (2, 0), (3, 1)):
        if c.ok(x, y):
            c.put(x, y, STEAM[4] if (x + y) % 2 else IVORY[4])


def float_glass(c):
    glass(c)
    if c.face in ("front", "back"):
        for x, y in ((1, 1), (2, 1), (2, 2)):
            if c.ok(x, y):
                c.put(x, y, (*GLASS[5][:3], 105))


def wave(clip, bone, amplitude, phase, rotation=1.5):
    pos, rot = [], []
    for i in range(9):
        t = clip.length * i / 8
        angle = 2 * math.pi * i / 8 + phase
        pos.append((t, (0, amplitude * math.sin(angle), 0)))
        rot.append((t, (0, 0, rotation * math.sin(angle))))
    clip.pos(bone, *pos).rot(bone, *rot)


def build():
    m = k.Machine("cultivation_rope", "CultivationRope", "Corda de Cultivo",
        "Três fios de couro em meia-volta, presilhas modulares de latão e uma ampola central atrasada pela correnteza.")
    m.bone("root", None, (8, 11.5, 8))

    # O eixo termina exatamente em y=11,5 e z=8; a catenária baixa um pixel no meio.
    sag = (0.0, -0.25, -0.55, -0.85, -0.85, -0.55, -0.25, 0.0)
    first = {}
    for i in range(8):
        x = i * 2
        m.bone(f"wave_{i}", "root", (x + 1, 11.5 + sag[i], 8))
        for strand in range(3):
            angle = (i * math.pi / 2) + strand * 2 * math.pi / 3
            y = 11.25 + sag[i] + 0.32 * math.cos(angle)
            z = 7.75 + 0.48 * math.sin(angle)
            name = f"strand_{i}_{strand}"
            source = first.get(strand)
            m.box(name, f"wave_{i}", (x, round(y * 2) / 2, round(z * 2) / 2), (2, 0.5, 0.5), cord,
                  same={"back": "front", "bottom": "top", "right": "left"}, uv_from=source)
            first.setdefault(strand, name)

    # Cada bloco carrega meia presilha: dois vizinhos formam uma abraçadeira inteira.
    for side, x in (("l", 0.0), ("r", 15.5)):
        m.box(f"cuff_{side}_top", "root", (x, 12, 7), (0.5, 0.5, 2), brass,
              same={"bottom": "top", "back": "front"}, uv_from="cuff_l_top" if side == "r" else None)
        m.box(f"cuff_{side}_bottom", "root", (x, 10.5, 7), (0.5, 0.5, 2), brass,
              same={"bottom": "top", "back": "front"}, uv_from="cuff_l_bottom" if side == "r" else None)
        m.box(f"cuff_{side}_front", "root", (x, 11, 6.75), (0.5, 1, 0.5), brass,
              same={"right": "left"}, uv_from="cuff_l_front" if side == "r" else None)
        m.box(f"cuff_{side}_back", "root", (x, 11, 8.75), (0.5, 1, 0.5), brass,
              same={"right": "left"}, uv_from="cuff_l_back" if side == "r" else None)

    # Ampola pequena: vidro em duas camadas, cinta e fiapos, pendurada no vale da trança.
    m.bone("float", "root", (8, 10.4, 8))
    m.box("float_cap", "float", (7.25, 10.0, 7.25), (1.5, 0.5, 1.5), brass,
          same={"back": "front", "right": "left", "bottom": "top"})
    m.box("float_glass", "float", (6.5, 8.0, 6.5), (3, 2, 3), float_glass, glass=True,
          same={"back": "front", "right": "left", "left": "front"})
    m.box("float_tip", "float", (7, 7.5, 7), (2, 0.5, 2), salt,
          same={"back": "front", "right": "left", "bottom": "top"})
    m.box("float_band", "float", (6.25, 8.75, 6.25), (3.5, 0.5, 3.5), brass,
          same={"back": "front", "right": "left", "bottom": "top"})
    m.box("fibre_l", "float", (6.75, 9.7, 7.25), (0.5, 1, 0.5), cord,
          same={"back": "front", "right": "left"})
    m.box("fibre_r", "float", (8.75, 9.7, 8.25), (0.5, 1, 0.5), cord,
          same={"back": "front", "right": "left"}, uv_from="fibre_l")

    idle = Clip(4.0, True)
    for i in range(8):
        envelope = math.sin(math.pi * (i + 0.5) / 8)
        wave(idle, f"wave_{i}", 0.4 * envelope, i * math.pi / 8, 1.5 * envelope)
    wave(idle, "float", 0.32, math.pi, 2.2)
    m.clips["idle"] = idle.finish()
    m.states = {"idle": {"label": "Correnteza", "loop": "idle"}}
    m.descriptions = {"idle": "A onda percorre a trança; a ampola responde meio ciclo depois e as pontas continuam presas."}
    return m


if __name__ == "__main__":
    machine = build()
    idle = machine.clips["idle"]
    shots = [("lado", [(idle, 0.0, 1)], math.radians(180), math.radians(4)),
             ("três quartos", [(idle, 1.0, 1)], math.radians(145), math.radians(18)),
             ("onda", [(idle, 2.0, 1)], math.radians(205), math.radians(10))]
    k.write(machine, BASE, preview_shots=shots if "--preview" in sys.argv else None,
            register=("v1", "No jogo · Corda de Cultivo animada") if "--register" in sys.argv else None)

