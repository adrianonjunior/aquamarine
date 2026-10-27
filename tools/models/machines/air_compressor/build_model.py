#!/usr/bin/env python3
"""Compressor de Ar (0.6.1, 7F): motor de aço com aletas, volante de cobre na frente, cilindro de latão com cruzeta,
reservatório de ar atrás com válvula de alívio. Latão, vidro e vapor; 32 px por face.

Portas: energia à esquerda (motor), Ar Comprimido atrás (reservatório). O bocal da válvula (13.5, 15.6, 12.5) é o ponto
das baforadas em PneumaticBlockEntity.clientTick.
"""
import sys
from pathlib import Path
BASE = Path(__file__).resolve().parent
sys.path.insert(0, str(BASE.parent))
import machine_kit as k
import hydraulic_art as h

ROUND = {"back": "front", "right": "left", "bottom": "top"}


def fins(c):
    """Aletas de refrigeração do motor: ranhuras horizontais escuras com fio de luz em cima."""
    h.steel(c)
    if c.face in ("left", "right", "top"):
        for y in range(2, c.h - 2, 3):
            for x in range(1, c.w - 1):
                c.put(x, y, k.STEEL[0])
                c.put(x, y - 1, k.STEEL[4])


def receiver(c):
    """Reservatório: latão rebitado com placa de aviso de pressão."""
    k.cylinder(c, k.BRASS, 3)
    k.rivets(c, k.BRASS, 6, 0)
    if c.face == "front" and c.w >= 10:
        # Placa marfim de aviso com a faixa de pressão em âmbar.
        c.rect(2, 2, c.w - 3, 5, k.IVORY[4])
        c.rect(3, 3, c.w - 3, 4, k.MAGMA[3])
    k.patina(c, ((1, c.h - 2),), 1)


def build():
    m = k.Machine("air_compressor", "AirCompressor", "Compressor de Ar",
        "Motor de aço com aletas, volante de cobre, cilindro de latão com cruzeta e reservatório de ar com válvula de alívio. "
        "Latão, vidro e vapor; 32 px por face.")
    h.base(m)
    # Motor deitado no eixo z, à esquerda e atrás; o eixo atravessa até o volante na frente.
    m.box("motor", "works", (2, 5, 8), (5, 6, 6), fins, same={"back": "front", "bottom": "top"})
    for i, z in enumerate((8.5, 12.5)):
        m.box(f"motor_band_{i}", "works", (1.75, 4.75, z), (5.5, 6.5, .5), h.brass, same=ROUND,
              uv_from="motor_band_0" if i else None)
    m.box("motor_foot", "works", (2.5, 2, 9), (4, 3, 4), h.steel, same={"back": "front", "right": "left"})
    m.box("motor_cap", "works", (2.5, 5.5, 14), (4, 5, .5), h.ref.p_flange, same={"back": "front", "right": "left", "bottom": "top"})
    m.box("axle", "works", (4, 7.5, 3.5), (1, 1, 4.5), h.ref.p_rod, same=ROUND)
    h.wheel(m, (4.5, 8, 3), 3.5)
    m.box("counterweight", "flywheel", (5.5, 4.75, 2.25), (1.5, 1.5, .5), h.brass, same=ROUND)
    # Cilindro vertical de latão à direita; o pistão sobe da cabeça e a cruzeta corre entre as guias.
    m.box("cylinder", "works", (9.5, 2, 3.5), (5, 7, 5), h.ref.p_housing, same={"back": "front", "right": "left"})
    for i, y in enumerate((3, 7.5)):
        m.box(f"cylinder_band_{i}", "works", (9.25, y, 3.25), (5.5, .5, 5.5), h.brass, same=ROUND,
              uv_from="cylinder_band_0" if i else None)
    m.box("cylinder_head", "works", (9, 9, 3), (6, 1, 6), h.steel, same=ROUND)
    m.bone("piston", "works", (12, 10, 6))
    m.box("piston_rod", "piston", (11.5, 10, 5.5), (1, 3, 1), h.ref.p_rod, same=ROUND)
    m.box("crosshead", "piston", (10.25, 13, 5), (3.5, 1, 2), h.steel, same=ROUND)
    for i, x in enumerate((9.5, 14)):
        m.box(f"guide_{i}", "works", (x, 10, 5.5), (.5, 5, 1), h.brass, same=ROUND, uv_from="guide_0" if i else None)
    m.box("guide_cap", "works", (9.5, 15, 5.25), (5, .5, 1.5), h.copper, same=ROUND)
    # Biela: do moente do volante até a cruzeta; balança com o volante.
    m.bone("rod_link", "works", (4.5, 8, 2))
    m.box("connecting_rod", "rod_link", (4.5, 7.75, 1.75), (6.5, .5, .5), h.brass, same=ROUND)
    # Reservatório de ar atrás, à direita, ligado ao cilindro por um tubo de cobre.
    m.box("receiver", "works", (8.5, 10, 9.5), (6, 4.5, 5.5), receiver, same={"back": "front", "right": "left", "bottom": "top"})
    for i, x in enumerate((9, 13.5)):
        m.box(f"receiver_hoop_{i}", "works", (x, 9.75, 9.25), (.5, 5, 6), h.brass, same=ROUND,
              uv_from="receiver_hoop_0" if i else None)
    m.box("receiver_leg", "works", (10.5, 2, 10.5), (2, 8, 2), h.steel, same={"back": "front", "right": "left"})
    m.box("delivery", "works", (11.25, 8.5, 8.5), (1.5, 1.5, 1.5), h.copper, same=ROUND)
    m.box("relief_body", "works", (13, 14.5, 12), (1, 1, 1), h.copper, same=ROUND)
    m.bone("relief_cap", "works", (13.5, 15.5, 12))
    m.box("relief_lid", "relief_cap", (12.75, 15.5, 11.75), (1.5, .5, 1.5), h.brass, same=ROUND)
    m.box("intake", "works", (14.5, 4, 4.5), (1, 3, 3), h.grille, same={"back": "front", "right": "left", "bottom": "top"})
    h.gauge(m, (12, 6, 3))
    m.box("status_lamp", "works", (7, 2, 1), (2, 1, .5), h.lamp, same=ROUND)
    h.port(m, "energy_in", (1.5, 8, 11), "energy", (0, -90, 0))
    h.port(m, "air_out", (11.5, 12.25, 15.5), "gas", (0, 180, 0))

    h.motion(m, cycle=.8)
    for name in ("run", "start", "stop"):
        h.sine(m.clips[name], "rod_link", "rotation", 10, 2)
    full = m.clips["full"]
    full.pos("relief_cap", (0, (0, 0, 0), k.L), (.2, (0, .5, 0), k.L), (.35, (0, .3, 0), k.L), (.6, (0, .55, 0), k.L),
             (1.2, (0, 0, 0), k.L)).finish()
    m.descriptions = {"idle": "Motor parado; agulha em repouso.", "start": "Volante pega giro e a agulha sobe em 1,6 s.",
        "run": "Um curso a cada 0,8 s: volante gira, biela balança e a cruzeta sobe e desce.",
        "stop": "Volante perde giro e o pistão assenta.",
        "full": "Reservatório cheio: o motor para e a válvula de alívio pulsa."}
    m.states = {"idle": {"label": "Parado", "enter": "stop", "loop": "idle"},
                "run": {"label": "Comprimindo", "enter": "start", "loop": "run"},
                "full": {"label": "Cheio", "loop": "full"}}
    return m


if __name__ == "__main__":
    m = build()
    k.write(m, BASE, preview_shots=h.shots(m) if "--preview" in sys.argv else None,
            register=("v1", "No jogo · Compressor de Ar · motor, cilindro e reservatório") if "--register" in sys.argv else None)
