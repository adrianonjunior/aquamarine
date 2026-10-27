#!/usr/bin/env python3
"""Pulmão de Ferro (0.6.1, 7F): vaso de ferro deitado em berços, com cintas de latão, visor de nível com boia acesa,
mostrador de pressão e válvula de alívio em cima; na ponta direita, o fole de couro e latão que enche com o nível.
Latão, vidro e vapor; 32 px por face.

O clipe `level` (1 s, fora dos estados) vai de vazio (0 s) a cheio (1 s): PneumaticRenderer o soma com o tempo igual ao
nível. A válvula de alívio solta o jato em (6, 14.6, 8) (PneumaticBlockEntity.clientTick).
"""
import sys
from pathlib import Path
BASE = Path(__file__).resolve().parent
sys.path.insert(0, str(BASE.parent))
import machine_kit as k
import hydraulic_art as h

ROUND = {"back": "front", "right": "left", "bottom": "top"}


def vessel(c):
    """Chapas de ferro rebitadas, com costura no meio e escorrido de ferrugem embaixo dos rebites."""
    k.plate(c, k.STEEL, 3)
    k.rivets(c, k.STEEL, 5, 1)
    if c.w > 10:
        k.seam_v(c, c.w // 2, k.STEEL)
    if c.face in ("front", "back"):
        for x in (3, c.w - 4):
            k.stain(c, x, c.h - 3, 1, k.COPPER[2], .35)
    k.scratches(c, k.STEEL, 2)


def fold(c):
    """Dobra de couro do fole: nas faces finas (a aresta da dobra), couro claro com vinco; nas largas, couro com costura."""
    if min(c.w, c.h) <= 1:
        for y in range(c.h):
            for x in range(c.w):
                c.put(x, y, k.LEATHER[4] if (x + y) % 4 else k.LEATHER[5])
        return
    k.plate(c, k.LEATHER, 3, bevel=False)
    for x in range(1, c.w - 1, 2):
        c.put(x, 1, k.LEATHER[5])
        c.put(x, c.h - 2, k.LEATHER[5])


def float_mark(c):
    """Boia do visor: disco ciano aceso (lê de longe)."""
    k.plate(c, k.CYAN, 3)
    for y in range(c.h):
        for x in range(c.w):
            c.lit(x, y, k.CYAN[4] if (x + y) % 3 else k.CYAN[5])


def build():
    m = k.Machine("iron_lung", "IronLung", "Pulmão de Ferro",
        "Vaso de ferro deitado com cintas de latão, visor de nível com boia acesa, mostrador de pressão, válvula de alívio "
        "e um fole de couro na ponta que enche com o ar guardado. Latão, vidro e vapor; 32 px por face.")
    h.base(m)
    for i, x in enumerate((2, 8)):
        m.box(f"saddle_{i}", "works", (x, 2, 4), (2.5, 2, 8), h.steel, same={"back": "front", "right": "left"},
              uv_from="saddle_0" if i else None)
    # Vaso de perfil octogonal: miolo alto e duas lajes mais estreitas em cima e embaixo (lê como cilindro deitado).
    m.box("vessel", "works", (1.5, 5, 3.5), (9.5, 6, 9), vessel, same={"back": "front"})
    m.box("vessel_top", "works", (1.5, 11, 4.5), (9.5, 1, 7), vessel, same={"bottom": "top", "back": "front"})
    m.box("vessel_bottom", "works", (1.5, 4, 4.5), (9.5, 1, 7), vessel, uv_from="vessel_top")
    for i, x in enumerate((1, 5.5, 10.5)):
        m.box(f"hoop_{i}", "works", (x, 4.75, 3.25), (.5, 6.5, 9.5), h.brass, same=ROUND, uv_from="hoop_0" if i else None)
        m.box(f"hoop_cap_{i}", "works", (x, 3.75, 4.25), (.5, 8.5, 7.5), h.brass, same=ROUND,
              uv_from="hoop_cap_0" if i else None)
    m.box("end_cap", "works", (.5, 5.5, 5), (.5, 5, 6), h.ref.p_flange, same=ROUND)
    # Visor de nível: tubo de vidro na frente com a boia que sobe com o ar guardado.
    m.box("sight_top", "works", (7.5, 11, 2.5), (2, .5, 1.5), h.brass, same=ROUND)
    m.box("sight_bottom", "works", (7.5, 4.5, 2.5), (2, .5, 1.5), h.brass, same=ROUND, uv_from="sight_top")
    m.box("sight_glass", "works", (7.75, 5, 2.75), (1.5, 6, 1), h.glass, glass=True, same={"back": "front", "right": "left"})
    m.bone("float", "works", (8.5, 5.5, 3.25))
    m.box("float_mark", "float", (8, 5, 3), (1, 1, .5), float_mark, same=ROUND)
    h.gauge(m, (4, 8, 3))
    # Válvula de alívio em cima: corpo, mola e tampa que pula no clipe full.
    m.box("relief_body", "works", (5, 12, 7), (2, 1.5, 2), h.brass, same=ROUND)
    m.box("relief_spring", "works", (5.5, 13.5, 7.5), (1, .5, 1), h.copper, same=ROUND)
    m.bone("relief_cap", "works", (6, 14, 8))
    m.box("relief_lid", "relief_cap", (4.75, 14, 6.75), (2.5, .5, 2.5), h.brass, same=ROUND)
    m.box("relief_stem", "relief_cap", (5.75, 14.5, 7.75), (.5, .5, .5), h.ref.p_rod, same=ROUND)
    # Fole na ponta direita: dobras de couro entre cintas de latão; escala no x a partir do vaso.
    m.bone("bellows", "works", (11, 8, 8))
    # Sanfona de couro: dobra de fora (larga) e de dentro (estreita) alternadas; um arame de latão no meio.
    for i in range(4):
        x = 11 + i
        m.box(f"bellows_fold_{i}", "bellows", (x, 3.75, 3.75), (.5, 8.5, 8.5), fold, same=ROUND,
              uv_from="bellows_fold_0" if i else None)
        m.box(f"bellows_pleat_{i}", "bellows", (x + .5, 5, 5), (.5, 6, 6), fold, same=ROUND,
              uv_from="bellows_pleat_0" if i else None)
    m.box("bellows_wire", "bellows", (13, 3.5, 3.5), (.5, 9, 9), h.brass, same=ROUND)
    m.bone("bellows_plate", "works", (15.25, 8, 8))
    m.box("bellows_board", "bellows_plate", (15, 3.5, 3.5), (.5, 9, 9), h.ref.p_flange, same=ROUND)
    m.box("bellows_handle", "bellows_plate", (15.5, 7, 6), (.5, 2, 4), h.copper, same=ROUND)
    m.box("status_lamp", "works", (11.5, 2, 1), (2, 1, .5), h.lamp, same=ROUND)
    h.port(m, "air_in", (1, 8, 8), "gas", (0, -90, 0))
    h.port(m, "air_out", (6, 8, 13), "gas", (0, 180, 0))

    def breath(clip, amp):
        h.sine(clip, "bellows", "scale", amp, 0, base=1)
        h.sine(clip, "bellows_plate", "position", amp * 4, 0)

    idle = k.Clip(4, True)
    h.sine(idle, "needle", "rotation", .25, 2)
    breath(idle, .02)
    m.clips["idle"] = idle.finish()
    run = k.Clip(2.4, True)
    h.sine(run, "needle", "rotation", 4, 2, 0, 95)
    breath(run, .08)
    h.sine(run, "float", "position", .15, 1)
    m.clips["run"] = run.finish()
    start = k.Clip(1.6)
    start.rot("needle", (0, (0, 0, 0)), (.8, (0, 0, 40)), (1.6, (0, 0, 95)))
    m.clips["start"] = start.finish()
    stop = k.Clip(1)
    stop.rot("needle", (0, (0, 0, 95)), (.6, (0, 0, 10)), (1, (0, 0, 0)))
    m.clips["stop"] = stop.finish()
    full = k.Clip(1.2, True)
    full.pos("relief_cap", (0, (0, 0, 0), k.L), (.1, (0, .6, 0), k.L), (.25, (0, .35, 0), k.L), (.4, (0, .7, 0), k.L),
             (.6, (0, .4, 0), k.L), (.8, (0, .65, 0), k.L), (1.2, (0, 0, 0), k.L))
    h.sine(full, "needle", "rotation", 1.5, 2, 0, 172)
    breath(full, .015)
    m.clips["full"] = full.finish()
    level = k.Clip(1)
    level.scale("bellows", (0, (.5, 1, 1), k.L), (1, (1, 1, 1), k.L))
    level.pos("bellows_plate", (0, (-2, 0, 0), k.L), (1, (0, 0, 0), k.L))
    level.pos("float", (0, (0, 0, 0), k.L), (1, (0, 4.5, 0), k.L))
    m.clips["level"] = level.finish()
    m.descriptions = {"idle": "Pulmão em repouso; o fole quase não mexe.", "start": "A agulha sobe com a pressão.",
        "run": "Respira: o fole enche e esvazia devagar enquanto o ar entra ou sai.", "stop": "A agulha volta ao zero.",
        "full": "Cheio: a válvula de alívio pula e solta o jato.",
        "level": "Nível (fora dos estados): o fole abre e a boia sobe do vazio ao cheio."}
    m.states = {"idle": {"label": "Parado", "enter": "stop", "loop": "idle"},
                "run": {"label": "Respirando", "enter": "start", "loop": "run"},
                "full": {"label": "Cheio (alívio)", "loop": "full"}}
    return m


def shots(m):
    import math
    lv = m.clips["level"]
    return [("vazio", [(m.clips["idle"], 0, 1), (lv, 0, 1)], math.radians(135), math.radians(24)),
            ("cheio", [(m.clips["full"], .4, 1), (lv, 1, 1)], math.radians(155), math.radians(15)),
            ("trás", [(m.clips["run"], .6, 1), (lv, .5, 1)], math.radians(-40), math.radians(28)),
            ("perfil", [(m.clips["idle"], 0, 1), (lv, .5, 1)], math.radians(90), math.radians(12))]


if __name__ == "__main__":
    m = build()
    k.write(m, BASE, preview_shots=shots(m) if "--preview" in sys.argv else None,
            register=("v1", "No jogo · Pulmão de Ferro · nível, respiração e alívio") if "--register" in sys.argv else None)
