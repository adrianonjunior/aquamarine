#!/usr/bin/env python3
"""Gerador de Fenda refeito (0.6.1, faixas 7B e 7E; docs/12-industria.md): a primeira máquina animada e o portão de
aprovação visual. Caixa de pedra escura rachada com magma nas fendas e na grade, câmara de vidro com magma borbulhante,
dois pistões em contratempo ligados por um balancim, volante da válvula, mostrador e chaminé que solta vapor (terra) ou
bolhas (água). Anéis de ligação: azul atrás (energia), verde-água nos lados (água à esquerda, magma à direita).

    C:\\Python314\\python tools/models/machines/vent_generator/build_model.py [--preview] [--register]

Coordenadas de projeto do kit (machine_kit.py): x da esquerda para a direita de quem olha a frente, y do chão para
cima, z da frente para trás, em pixels do bloco.
"""
from __future__ import annotations

import math
import sys
from pathlib import Path

BASE = Path(__file__).resolve().parent
sys.path.insert(0, str(BASE.parent))
import machine_kit as k  # noqa: E402
from machine_kit import (BRASS, COPPER, GLASS, GLOW_AMBER, GLOW_DEEP, GLOW_HOT, LEATHER, MAGMA, PATINA,  # noqa: E402
                         RING_ENERGY, RING_FLUID, ROCK, STEAM, STEEL, CYAN, Clip, L, cylinder, plate)

STROKE = 1.0                                  # curso dos pistões (px), para cima e para baixo
ARM = 5.75                                    # do eixo do balancim ao centro de cada pistão
THETA = math.degrees(math.atan(STROKE / ARM))  # inclinação do balancim no fim do curso
RUN = 1.2                                     # um ciclo do run a 1x (WaterGeneratorBlockEntity.VENT_STROKE = RUN / 2)
NEEDLE_REST = -110.0                          # ponteiro no zero; +130 trabalhando, +205 na sobrecarga


# ================================================================ pintura de cada peça

def p_plinth(c):
    plate(c, STEEL, 2)
    if c.side:
        k.rivets(c, STEEL, 8, 0, "t")
    else:
        k.rivets(c, STEEL, 8, 0)
        k.corner_bolts(c, STEEL, 0)


def p_stone(c):
    """Cantaria de pedra escura: fiadas de 4 px, juntas desencontradas, chanfro do alto à esquerda e rachaduras de magma."""
    c.fill(ROCK[0])
    course = 4
    offset = {"front": 2, "back": 7, "left": 5, "right": 0}.get(c.face, 0)
    widths = (10, 9, 11, 9, 10, 12, 9)
    for row in range((c.h + course - 1) // course):
        y0 = row * course
        x = -((offset + 5 * row) % 10)
        i = row * 3 + offset
        while x < c.w:
            bw = widths[i % len(widths)]
            tone = 2 + (i % 3 == 0) - (i % 4 == 1)
            for yy in range(y0 + 1, min(c.h, y0 + course)):
                for xx in range(max(0, x + 1), min(c.w, x + bw)):
                    lv = tone
                    if yy == y0 + 1 or xx == x + 1:
                        lv += 1
                    if yy == y0 + course - 1 or xx == x + bw - 1:
                        lv -= 1
                    c.put(xx, yy, ROCK[k.clamp(lv, 0, 5)])
            c.put(x + 1, y0 + 1, ROCK[5])
            x += bw
            i += 1
    # Rachaduras: caminho fixo em degraus (nada de ruído), longe das cantoneiras; núcleo de magma, borda queimada.
    paths = {"front": ((7, "dRdRdLdddd"), (20, "dLddRdLddd")), "back": ((10, "dRRdLddRddd"), (21, "ddLdLdddd")),
             "left": ((9, "dRdRdLdddd"),), "right": ((16, "dLdLddRddd"),)}.get(c.face, ())
    for x, steps in paths:
        y, cells = 0, []
        for s in steps:
            cells.append((x, y))
            x, y = (x + 1, y) if s == "R" else (x - 1, y) if s == "L" else (x, y + 1)
        for x, y in cells:
            for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)):
                if c.ok(nx, ny) and (nx, ny) not in cells:
                    c.put(nx, ny, ROCK[0])
        for i, (x, y) in enumerate(cells):
            deep = 0 < i < len(cells) - 1
            c.put(x, y, MAGMA[4] if deep else MAGMA[2])
            c.lit(x, y, GLOW_AMBER if deep else GLOW_DEEP)


def p_deck(c):
    """Convés de aço com friso de latão: a cor quente fica nas peças que se mexem."""
    if not c.side:
        plate(c, STEEL, 2)
        k.seam_v(c, 14, STEEL, 0, c.h)
        k.seam_h(c, 14, STEEL, 0, c.w)
        k.rivets(c, BRASS, 8, 1)
        k.corner_bolts(c, BRASS)
        k.stain(c, 26, 26, 3, (24, 16, 8, 255), 0.6)    # óleo pingado do volante
        k.stain(c, 5, 12, 2, (24, 16, 8, 255), 0.45)    # e do pistão esquerdo
        k.stain(c, 22, 4, 3, STEAM[2], 0.22)            # sal seco
        k.scratches(c, STEEL, 6)
    else:
        for x in range(c.w):
            c.put(x, 0, BRASS[4] if x % 8 != 4 else BRASS[5])
            c.put(x, 1, BRASS[2] if x % 8 != 4 else BRASS[3])
        k.patina(c, ((3, 1), (c.w - 6, 1)), 1)


def p_grate(c):
    if c.face != "front":
        plate(c, STEEL, 2)
        return
    plate(c, STEEL, 3)
    for slot in (1, 3):
        for x in range(1, c.w - 1):
            if x % 4 == 0:  # montantes
                c.put(x, slot, STEEL[4])
                continue
            heat = 1 - abs(x - (c.w - 1) / 2) / (c.w / 2)
            c.put(x, slot, MAGMA[5] if heat > 0.6 else MAGMA[4] if heat > 0.25 else MAGMA[3])
            c.lit(x, slot, GLOW_HOT if heat > 0.6 else GLOW_AMBER if heat > 0.25 else GLOW_DEEP)
    for x in range(1, c.w - 1):
        c.put(x, 2, STEEL[4] if x % 4 else STEEL[5])


def p_guard(c):
    plate(c, BRASS, 3)
    if c.side:
        k.rivet(c, 0, 1, BRASS)
        k.rivet(c, 0, c.h - 3, BRASS)
        k.patina(c, ((1, c.h - 1),), 1)


def ring_face(R, core):
    def paint(c):
        if c.face not in ("front", "left", "right", "back") or min(c.w, c.h) < 5:
            plate(c, R, 2)
            return
        disc = k.disc_mask(c.w, c.h)
        inner = k.disc_mask(c.w, c.h, 1.2)
        hole = k.disc_mask(c.w, c.h, 2.0)
        for y in range(c.h):
            for x in range(c.w):
                if (x, y) not in disc:
                    c.put(x, y, k.CLEAR)
                elif (x, y) not in inner:
                    c.put(x, y, R[4] if x + y < c.w - 1 else R[2])
                elif (x, y) not in hole:
                    c.put(x, y, BRASS[3] if x + y < c.w else BRASS[2])
                else:
                    c.put(x, y, core[3] if (x + y) % 2 else core[4])
        return
    return paint


def p_collar(c):
    plate(c, COPPER, 3)
    if c.side:
        k.rivets(c, COPPER, 6, 0, "t")
        k.patina(c, ((1, 1), (c.w - 2, 1), (c.w // 2, 1)), 1)
    else:
        k.patina(c, ((1, 1), (c.w - 2, c.h - 2)), 2)


def p_glass(c):
    for y in range(c.h):
        for x in range(c.w):
            edge = y in (0, c.h - 1)
            col = GLASS[2] if edge else GLASS[3]
            c.put(x, y, col[:3] + ((150,) if edge else (64,)))
    for j in range(c.h):  # reflexo diagonal, duas faixas paralelas subindo para a direita
        x = 2 + (c.h - 1 - j)
        c.put(x, j, GLASS[5][:3] + (200,))
        c.put(x + 1, j, GLASS[4][:3] + (150,))
        c.put(x + 5, j, GLASS[5][:3] + (120,))
    for x, y in ((c.w - 3, 1), (c.w - 5, 2), (4, 1)):  # vapor condensado no alto
        c.put(x, y, STEAM[4][:3] + (170,))


def p_gasket(c):
    plate(c, LEATHER, 2, grad=False)
    if c.side:
        for x in range(0, c.w, 3):
            c.put(x, 0, LEATHER[4])  # costura


def p_lid(c):
    if c.side:
        plate(c, BRASS, 3)
        k.rivets(c, BRASS, 8, 0, "t")
        return
    plate(c, BRASS, 3)
    cx = cy = (c.w - 1) / 2
    for y in range(c.h):
        for x in range(c.w):
            d = math.hypot(x - cx, y - cy)
            if 5.6 <= d < 6.6:
                c.put(x, y, BRASS[1] if (x + y) > c.w - 1 else BRASS[2])
            elif 6.6 <= d < 7.4:
                c.put(x, y, BRASS[4])
    for a in range(6):
        ang = math.pi / 6 + a * math.pi / 3
        bx, by = round(cx + 6.1 * math.cos(ang)), round(cy + 6.1 * math.sin(ang))
        c.put(bx, by, STEEL[4])
        c.put(bx + 1, by + 1, STEEL[1])
    k.rivets(c, BRASS, 8, 0)
    k.scratches(c, BRASS, 3)
    k.patina(c, ((1, 1), (c.w - 2, 1)), 1)


def p_post(c):
    if c.side:
        cylinder(c, BRASS, 3)
        for x in range(c.w):
            c.put(x, c.h // 2, BRASS[5] if x == 0 else BRASS[3])
            c.put(x, c.h // 2 + 1, BRASS[1])
    else:
        plate(c, BRASS, 4, grad=False)


def p_magma(c):
    """Magma vivo: lava clara com placas de crosta escura e veios quentes; o que é lava acende na _glow."""
    for y in range(c.h):
        for x in range(c.w):
            hot = not c.side or y == 0
            c.put(x, y, MAGMA[4] if hot else MAGMA[3 if y < 2 else 2])
            c.lit(x, y, GLOW_HOT if hot else (GLOW_AMBER if y < 2 else GLOW_DEEP))
    crusts = ((1, 1, 4, 3), (7, 0, 4, 2), (8, 4, 3, 4), (0, 6, 3, 4), (5, 8, 4, 3), (4, 4, 2, 2)) if not c.side \
        else ((0, 1, 3, 2), (5, 2, 4, 2), (10, 1, 2, 2))
    for x0, y0, w, h in crusts:
        for y in range(y0, y0 + h):
            for x in range(x0, x0 + w):
                if c.ok(x, y):
                    border = x in (x0, x0 + w - 1) or y in (y0, y0 + h - 1)
                    c.put(x, y, MAGMA[2] if border else MAGMA[1])
                    c.lit(x, y, GLOW_DEEP if border else k.CLEAR)
        c.put(x0 + 1, y0, MAGMA[3])  # luz do alto na borda da placa
    if not c.side:
        for x, y in ((6, 6), (3, 5), (10, 9), (7, 3)):
            c.put(x, y, MAGMA[5])
            c.lit(x, y, GLOW_HOT)


def p_blob(c):
    c.fill(MAGMA[4])
    c.put(0, 0, MAGMA[5])
    for y in range(c.h):
        for x in range(c.w):
            c.lit(x, y, GLOW_HOT)


def p_housing(c):
    cylinder(c, STEEL, 3)
    for x in range(c.w):
        c.put(x, 2, STEEL[1])  # junta do cilindro
    c.put(1, 4, PATINA[2])
    k.stain(c, c.w - 2, c.h - 2, 1, (30, 20, 10, 255), 0.5)


def p_band(c):
    if c.side:
        for x in range(c.w):
            c.put(x, 0, BRASS[5] if x % 3 == 1 else BRASS[4] if x < c.w - 2 else BRASS[3])
    else:
        plate(c, BRASS, 3, grad=False)
        m = c.w // 2
        for y in range(m - 1, m + 1):
            for x in range(m - 1, m + 1):
                c.put(x, y, STEEL[0])  # furo da haste
        c.put(m - 2, m - 2, BRASS[5])


def p_rod(c):
    for y in range(c.h):
        for x in range(c.w):
            c.put(x, y, STEEL[5] if x == 0 else STEEL[3])
        if y % 5 == 4:
            c.put(0, y, STEEL[4])


def p_head(c):
    plate(c, BRASS, 3)
    if c.side:
        c.put(c.w // 2, 0, STEEL[4])
        c.put(c.w // 2, 1, STEEL[1])
    else:
        k.bolt(c, c.w // 2 - 1, c.h // 2 - 1, STEEL)


def p_beam(c):
    if c.face in ("front", "back"):
        for x in range(c.w):
            c.put(x, 0, BRASS[4])
            c.put(x, 1, BRASS[2])
        for x in (3, 4, c.w - 5, c.w - 4):  # pinos sobre as cabeças dos pistões
            c.put(x, 0, STEEL[4])
            c.put(x, 1, STEEL[2])
        for x in range(8, c.w - 8, 4):
            c.put(x, 0, BRASS[5])
    elif c.side:
        plate(c, BRASS, 3, grad=False)
    else:
        plate(c, BRASS, 3)
        k.scratches(c, BRASS, 2)


def p_steel_block(c):
    plate(c, STEEL, 3)


def p_pivot(c):
    c.fill(STEEL[3])
    c.put(0, 0, STEEL[5])
    c.put(c.w - 1, c.h - 1, STEEL[1])


def p_chimney(c):
    if not c.side:
        c.fill(COPPER[2])
        for y in range(1, c.h - 1):
            for x in range(1, c.w - 1):
                c.put(x, y, ROCK[0])
        return
    cylinder(c, COPPER, 3)
    for y in range(5):  # fuligem perto da boca
        for x in range(c.w):
            c.blend(x, y, ROCK[0], 0.5 - 0.1 * y)
    for x, y0, n in ((1, 7, 3), (3, 7, 2), (2, 15, 2)):  # limo escorrendo das cintas
        for y in range(y0, y0 + n):
            c.put(x, y, PATINA[2] if y > y0 else PATINA[3])


def p_band_small(c):
    if c.side:
        for x in range(c.w):
            c.put(x, 0, BRASS[4] if x < c.w - 1 else BRASS[2])
        c.put(1, 0, BRASS[5])
    else:
        plate(c, BRASS, 3, grad=False)
        for y in range(1, c.h - 1):
            for x in range(1, c.w - 1):
                c.put(x, y, COPPER[2])


def p_mouth(c):
    """Boca da chaminé: anel de latão com fuligem e o furo escuro no meio."""
    if c.side:
        plate(c, BRASS, 3, grad=False)
        for x in range(c.w):
            c.blend(x, 0, ROCK[0], 0.35)
        return
    plate(c, BRASS, 3, grad=False)
    for y in range(1, c.h - 1):
        for x in range(1, c.w - 1):
            c.put(x, y, ROCK[0] if 1 < x < c.w - 2 and 1 < y < c.h - 2 else ROCK[2])


def p_cap(c):
    if c.face == "bottom":
        c.fill(ROCK[1])
        k.outline(c, COPPER[2])
        return
    plate(c, BRASS, 3)
    if c.face == "top":
        for x in range(c.w):
            c.put(x, 0, BRASS[1])  # dobradiça, no fundo
        for x in (1, c.w - 2):
            c.put(x, 0, STEEL[4])
        c.put(c.w // 2, c.h - 2, BRASS[5])  # puxador na frente


def p_pipe(c):
    if c.face in ("front", "back"):
        for x in range(c.w):
            c.put(x, 0, COPPER[4])
            c.put(x, 1, COPPER[2])
        c.put(1, 0, PATINA[3])
    else:
        plate(c, COPPER, 3, grad=False)


def p_flange(c):
    plate(c, BRASS, 3)
    if c.side and c.h >= 3:
        k.rivet(c, 0, 0, BRASS)


def p_steel_round(c):
    cylinder(c, STEEL, 3)


def p_wheel(c):
    """Volante da válvula com aro, quatro raios e cubo; o resto é vazado (recorte)."""
    if c.side:
        for x in range(c.w):
            c.put(x, 0, COPPER[4] if x < c.w // 2 else COPPER[3])
        return
    disc, inner = k.disc_mask(c.w, c.h), k.disc_mask(c.w, c.h, 1.0)
    m = c.w // 2
    for y in range(c.h):
        for x in range(c.w):
            if (x, y) in disc and (x, y) not in inner:
                c.put(x, y, COPPER[4] if x + y < c.w else COPPER[2])
            elif (x, y) in inner and (x == m or y == m):
                c.put(x, y, COPPER[3])
            else:
                c.put(x, y, k.CLEAR)
    c.put(m, m, BRASS[5])


def p_dial(c):
    """Mostrador: aro de latão, fundo claro iluminado, escala de 0 a 8 e faixa âmbar no fim."""
    if c.face != "front":
        plate(c, BRASS, 2)
        return
    disc, face = k.disc_mask(c.w, c.h), k.disc_mask(c.w, c.h, 1.0)
    cx = cy = (c.w - 1) / 2
    for y in range(c.h):
        for x in range(c.w):
            if (x, y) not in disc:
                c.put(x, y, k.CLEAR)
            elif (x, y) not in face:
                c.put(x, y, BRASS[4] if x + y < c.w - 1 else BRASS[2])
            else:
                c.put(x, y, k.IVORY[4] if y < cy else k.IVORY[3])
                c.lit(x, y, k.IVORY[5][:3] + (60,))
    for i in range(9):  # marcas da escala, de -110 a +110 graus
        ang = math.radians(-110 + i * 27.5 - 90)
        x, y = round(cx + 3.4 * math.cos(ang)), round(cy + 3.4 * math.sin(ang))
        if i >= 7:
            c.put(x, y, MAGMA[3])
            c.lit(x, y, GLOW_AMBER)
        else:
            c.put(x, y, STEEL[1])
    k.text(c, 2, 6, "0", STEEL[2], k.MINI)
    k.text(c, 6, 6, "8", MAGMA[2], k.MINI)
    c.put(round(cx), round(cy), STEEL[1])


def p_needle(c):
    for y in range(c.h):
        for x in range(c.w):
            c.put(x, y, STEEL[1] if y > 0 else MAGMA[4])
    c.lit(0, 0, GLOW_AMBER)


def p_handle(c):
    plate(c, BRASS, 4, grad=False)


# ================================================================ modelo

def build():
    m = k.Machine("vent_generator", "VentGenerator", "Gerador de Fenda",
                  "caixa de pedra rachada com magma nas fendas, câmara de vidro com magma borbulhante, pistões em contratempo "
                  "num balancim, volante da válvula, mostrador e chaminé; 32 E/t com magma embaixo e água em cima.")
    m.bone("frame", None, (8, 0, 8))
    m.bone("works", "frame", (8, 6, 8))
    m.bone("chamber", "works", (8, 6, 7))
    m.bone("magma", "chamber", (8, 7, 7))
    for i, (x, z) in enumerate(((6.5, 5.5), (9.0, 8.0), (7.5, 8.5)), 1):
        m.bone(f"blob_{i}", "magma", (x, 9, z))
    for side, x in (("l", 2.25), ("r", 13.75)):
        m.bone(f"piston_{side}", "works", (x, 10.5, 6.75))
    m.bone("beam", "works", (8, 14, 6.75))
    m.bone("chimney", "works", (2.25, 6, 12.75))
    m.bone("chimney_cap", "chimney", (2.25, 15, 14.5))
    m.bone("valve", "works", (14, 7.75, 2))
    m.bone("gauge", "works", (2.75, 6, 1.25))
    m.bone("needle", "gauge", (2.75, 9.5, 0.75), (0, 0, NEEDLE_REST))

    # Base de pedra: rodapé de aço, cantaria, convés de latão, cantoneiras, grade acesa e anéis de ligação.
    m.box("plinth", "frame", (0.5, 0, 0.5), (15, 1, 15), p_plinth, hide=("bottom",), same={"back": "front", "right": "left"})
    m.box("stone", "frame", (1, 1, 1), (14, 4, 14), p_stone, hide=("top", "bottom"))
    m.box("deck", "frame", (0.5, 5, 0.5), (15, 1, 15), p_deck, same={"bottom": "top", "back": "front", "right": "left"})
    m.box("grate", "frame", (4, 1.5, 0.5), (8, 2.5, 0.5), p_grate, hide=("back",))
    for i, (x, z) in enumerate(((0.75, 0.75), (13.75, 0.75), (0.75, 13.75), (13.75, 13.75))):
        m.box(f"guard_{i}", "frame", (x, 1, z), (1.5, 4, 1.5), p_guard, hide=("bottom", "top"),
              same={"back": "front", "right": "left", "left": "front"}, uv_from=None if i == 0 else "guard_0")
    m.box("ring_energy", "frame", (6.5, 1.5, 15), (3, 3, 0.5), ring_face(RING_ENERGY, RING_ENERGY), hide=("front",))
    m.box("ring_water", "frame", (0.5, 1.5, 6.5), (0.5, 3, 3), ring_face(RING_FLUID, CYAN), hide=("right",))
    m.box("ring_magma", "frame", (15, 1.5, 6.5), (0.5, 3, 3), ring_face(RING_FLUID, MAGMA), hide=("left",))

    # Câmara de vidro com o magma, gaxeta de couro, tampa com alça e a gaiola de latão.
    m.box("collar", "chamber", (4, 6, 3), (8, 1, 8), p_collar, hide=("bottom",), same={"back": "front", "right": "left", "left": "front"})
    m.box("glass", "chamber", (4.5, 7, 3.5), (7, 4, 7), p_glass, hide=("top", "bottom"), glass=True,
          same={"back": "front", "left": "front", "right": "front"})
    m.box("gasket", "chamber", (4.25, 11, 3.25), (7.5, 0.5, 7.5), p_gasket, hide=("top", "bottom"),
          same={"back": "front", "left": "front", "right": "front"})
    m.box("lid", "chamber", (3.5, 11.5, 2.5), (9, 1, 9), p_lid, same={"bottom": "top", "back": "front", "left": "front", "right": "front"})
    m.box("handle_l", "chamber", (6.5, 12.5, 3.5), (0.5, 0.5, 0.5), p_handle, hide=("bottom", "top"))
    m.box("handle_r", "chamber", (9, 12.5, 3.5), (0.5, 0.5, 0.5), p_handle, hide=("bottom", "top"), uv_from="handle_l")
    m.box("handle_bar", "chamber", (6.5, 13, 3.5), (3, 0.5, 0.5), p_handle, same={"back": "front"})
    for i, (x, z) in enumerate(((4, 3), (11, 3), (4, 10), (11, 10))):
        m.box(f"post_{i}", "chamber", (x, 7, z), (1, 4, 1), p_post, hide=("bottom", "top"),
              same={"back": "front", "left": "front", "right": "front"}, uv_from=None if i == 0 else "post_0")
    m.box("magma", "magma", (5, 7, 4), (6, 2, 6), p_magma, hide=("bottom",), same={"back": "front", "left": "front", "right": "front"})
    for i, (x, z) in enumerate(((6.5, 5.5), (9.0, 8.0), (7.5, 8.5)), 1):
        m.box(f"blob_{i}", f"blob_{i}", (x - 0.5, 9, z - 0.5), (1, 1, 1), p_blob, hide=("bottom",),
              same={"back": "front", "left": "front", "right": "front"}, uv_from=None if i == 1 else "blob_1")

    # Pistões: cilindro de aço com cintas de latão; a haste e a cabeça sobem e descem.
    for side, x in (("l", 2.25), ("r", 13.75)):
        twin = side == "r"
        m.box(f"band_low_{side}", "works", (x - 1.75, 6, 5), (3.5, 0.5, 3.5), p_band, hide=("bottom",),
              same={"back": "front", "left": "front", "right": "front"}, uv_from="band_low_l" if twin else None)
        m.box(f"housing_{side}", "works", (x - 1.5, 6.5, 5.25), (3, 3.5, 3), p_housing, hide=("top", "bottom"),
              same={"back": "front", "left": "front", "right": "front"}, uv_from="housing_l" if twin else None)
        m.box(f"band_top_{side}", "works", (x - 1.75, 10, 5), (3.5, 0.5, 3.5), p_band,
              same={"back": "front", "left": "front", "right": "front", "bottom": "top"}, uv_from="band_top_l" if twin else None)
        m.box(f"rod_{side}", f"piston_{side}", (x - 0.5, 9, 6.25), (1, 3.5, 1), p_rod, hide=("top", "bottom"),
              same={"back": "front", "left": "front", "right": "front"}, uv_from="rod_l" if twin else None)
        m.box(f"head_{side}", f"piston_{side}", (x - 1, 12.5, 5.75), (2, 1, 2), p_head,
              same={"back": "front", "left": "front", "right": "front", "bottom": "top"}, uv_from="head_l" if twin else None)

    # Balancim sobre um poste no meio da tampa, com o pino na frente.
    m.box("beam_post", "works", (7.25, 12.5, 6), (1.5, 1, 1.5), p_steel_block, hide=("bottom", "top"),
          same={"back": "front", "left": "front", "right": "front"})
    m.box("beam", "beam", (1.5, 13.5, 6), (13, 1, 1.5), p_beam, same={"back": "front", "bottom": "top", "right": "left"})
    m.box("beam_pin", "beam", (7.5, 13.5, 5.5), (1, 1, 0.5), p_pivot, hide=("back",), same={"left": "right"})

    # Chaminé de cobre no canto de trás, com cintas, tampa de dobradiça e o cano que vem da tampa da câmara.
    m.box("chimney", "chimney", (1, 6, 11.5), (2.5, 8, 2.5), p_chimney, hide=("bottom", "top"),
          same={"back": "front", "left": "front", "right": "front"})
    m.box("chimney_band", "chimney", (0.75, 9, 11.25), (3, 0.5, 3), p_band_small,
          same={"back": "front", "left": "front", "right": "front", "bottom": "top"})
    m.box("chimney_mouth", "chimney", (0.75, 14, 11.25), (3, 1, 3), p_mouth, hide=("bottom",),
          same={"back": "front", "left": "front", "right": "front"})
    m.box("chimney_cap", "chimney_cap", (0.5, 15, 11), (3.5, 0.5, 3.5), p_cap, same={"back": "front", "left": "front", "right": "front"})
    m.box("pipe", "works", (3.5, 11.5, 11.5), (3, 1, 1), p_pipe, hide=("left", "right", "front"), same={"bottom": "top"})
    m.box("flange", "works", (6, 11.25, 11.5), (1, 1.5, 1.5), p_flange, hide=("front",), same={"right": "left"})

    # Válvula de volante na frente, à direita; mostrador num poste na frente, à esquerda.
    m.box("valve_stub", "works", (13.25, 6, 1.25), (1.5, 1.5, 1.5), p_steel_round, hide=("bottom", "top"),
          same={"back": "front", "left": "front", "right": "front"})
    m.box("wheel", "valve", (12, 7.5, 0), (4, 0.5, 4), p_wheel, same={"bottom": "top", "back": "front", "left": "front", "right": "front"})
    m.box("gauge_post", "gauge", (2.25, 6, 0.75), (1, 1, 1), p_steel_block, hide=("bottom", "top"),
          same={"back": "front", "left": "front", "right": "front"})
    m.box("dial", "gauge", (0.25, 7, 0.75), (5, 5, 1), p_dial, same={"bottom": "top"})
    m.box("needle", "needle", (2.5, 9.5, 0.25), (0.5, 2, 0.5), p_needle, hide=("back", "bottom"),
          same={"left": "right"})
    clips(m)
    m.states = {
        "idle": {"label": "Parada", "enter": "stop", "loop": "idle"},
        "run": {"label": "Funcionando", "enter": "start", "loop": "run"},
        "overload": {"label": "Sobrecarga", "enter": None, "loop": "overload", "tint": "#ff6a4a"},
    }
    m.descriptions = {
        "idle": "Parada: o magma respira devagar, uma bolha estoura de vez em quando e o ponteiro descansa no zero.",
        "start": "Ligando: o volante abre a válvula, o ponteiro sobe com um tranco e os pistões pegam embalo até o primeiro golpe.",
        "run": "Funcionando: pistões em contratempo balançam o balancim, o magma pulsa a cada golpe e a chaminé solta vapor ou bolhas.",
        "stop": "Desligando: a válvula fecha, os pistões perdem força em balanços cada vez menores e o ponteiro cai.",
        "overload": "Sobrecarga: sem cabo para levar a energia, a máquina treme, o ponteiro encosta no fim da escala e o magma ferve.",
    }
    return m


# ================================================================ clipes

def wave(c, bone, channel, amp, n=8, length=None, phase=0.0, axis=1, base=0.0):
    """Cosseno amostrado em n pontos (catmull-rom fecha o laço sem salto)."""
    length = length or c.length
    keys = []
    for i in range(n + 1):
        t = length * i / n
        v = [0.0, 0.0, 0.0]
        v[axis] = base + amp * math.cos(2 * math.pi * i / n + phase)
        keys.append((t, tuple(v)))
    c.keys(bone, channel, *keys)


def blob_cycle(c, bone, t0, grow, peak=1.25):
    """Bolha de magma: cresce, incha, estoura (linear) e some até o próximo ciclo."""
    T = c.length
    pts = [(t0, 0.0, L), (t0 + grow * 0.75, 1.0, L), (t0 + grow, peak), (t0 + grow + 0.05, 0.0, L)]
    keys = []
    for p in pts:
        t = p[0] % T
        keys.append((t, p[1], p[2] if len(p) > 2 else k.C))
    c.scale(bone, *keys)


def clip_idle(m):
    c = Clip(4.0, True)
    c.scale("magma", (0, (1, 1, 1)), (2.0, (1.01, 1.04, 1.01)), (4.0, (1, 1, 1)))
    c.scale("blob_1", (0, 0.0, L), (4.0, 0.0, L))
    c.scale("blob_3", (0, 0.0, L), (4.0, 0.0, L))
    c.scale("blob_2", (0, 0.0, L), (1.4, 0.0, L), (2.6, 0.8), (3.0, 1.15), (3.05, 0.0, L), (4.0, 0.0, L))
    c.rot("needle", (0, (0, 0, 0)), (1.3, (0, 0, 0.8)), (2.4, (0, 0, -0.6)), (4.0, (0, 0, 0)))
    return c.finish()


def run_pose_at_zero():
    """Valores do run em t = 0 (o start termina neles para a troca não pular)."""
    run = clip_run()
    return {key: track[0.0][0] for key, track in run.tracks.items()}


def clip_run(length=RUN):
    c = Clip(length, True)
    wave(c, "piston_l", "position", STROKE)
    wave(c, "piston_r", "position", -STROKE)
    wave(c, "beam", "rotation", THETA, axis=2)
    # Peças secundárias com atraso: o magma pulsa a cada golpe e a tampa da chaminé abre logo depois dele.
    wave(c, "magma", "scale", 0.05, n=8, phase=-0.6 + math.pi, axis=1, base=1.12)
    for (b, ch), track in list(c.tracks.items()):
        if b == "magma" and ch == "scale":
            for t, (v, mode) in list(track.items()):
                track[t] = ((1.03, v[1], 1.03), mode)
    T = length
    c.rot("chimney_cap", (0, (-4, 0, 0)), (0.12 * T / 1.2, (-24, 0, 0)), (0.32 * T / 1.2, (-7, 0, 0)), (0.6 * T / 1.2, (-4, 0, 0)),
          (0.72 * T / 1.2, (-24, 0, 0)), (0.92 * T / 1.2, (-7, 0, 0)))
    c.rot("needle", (0, (0, 0, 130)), (0.3 * T / 1.2, (0, 0, 133)), (0.6 * T / 1.2, (0, 0, 128)), (0.9 * T / 1.2, (0, 0, 132)))
    c.rot("valve", (0, (0, 270, 0)), (T, (0, 270, 0)))
    blob_cycle(c, "blob_1", 0.0, 0.45)
    blob_cycle(c, "blob_2", 0.42, 0.38, 1.1)
    blob_cycle(c, "blob_3", 0.8, 0.32, 1.3)
    return c.finish()


def clip_start(m):
    end = run_pose_at_zero()
    c = Clip(1.6)
    c.rot("valve", (0, (0, 0, 0)), (0.15, (0, 20, 0)), (0.7, (0, 250, 0)), (0.85, (0, 270, 0)), (1.6, (0, 270, 0)))
    c.rot("needle", (0, (0, 0, 0)), (0.45, (0, 0, 10)), (1.05, (0, 0, 142)), (1.35, (0, 0, 127)), (1.6, end[("needle", "rotation")]))
    piston = ((0, 0.0), (0.5, 0.0), (0.88, -0.55), (1.22, 0.25), (1.6, STROKE))
    c.pos("piston_l", *[(t, (0, v, 0)) for t, v in piston])
    c.pos("piston_r", *[(t, (0, -v, 0)) for t, v in piston])
    c.rot("beam", *[(t, (0, 0, THETA * v / STROKE)) for t, v in piston])
    c.scale("magma", (0, (1, 1, 1)), (0.6, (1.01, 1.05, 1.01)), (1.6, end[("magma", "scale")]))
    c.rot("chimney_cap", (0, (0, 0, 0)), (1.2, (0, 0, 0)), (1.36, (-26, 0, 0)), (1.6, end[("chimney_cap", "rotation")]))
    for i in (1, 2, 3):
        c.scale(f"blob_{i}", (0, 0.0, L), (0.8, 0.0, L), (1.6, end[(f"blob_{i}", "scale")]))
    return c.finish()


def clip_stop(m):
    c = Clip(1.4)
    piston = ((0, 0.0), (0.35, 0.55), (0.75, -0.25), (1.1, 0.06), (1.4, 0.0))
    c.pos("piston_l", *[(t, (0, v, 0)) for t, v in piston])
    c.pos("piston_r", *[(t, (0, -v, 0)) for t, v in piston])
    c.rot("beam", *[(t, (0, 0, THETA * v / STROKE)) for t, v in piston])
    c.rot("valve", (0, (0, 270, 0)), (0.2, (0, 255, 0)), (0.95, (0, 10, 0)), (1.1, (0, 0, 0)))
    c.rot("needle", (0, (0, 0, 130)), (0.3, (0, 0, 120)), (0.95, (0, 0, -8)), (1.15, (0, 0, 3)), (1.4, (0, 0, 0)))
    c.scale("magma", (0, (1.03, 1.12, 1.03)), (1.4, (1, 1, 1)))
    c.rot("chimney_cap", (0, (-4, 0, 0)), (0.2, (-26, 0, 0)), (0.55, (-3, 0, 0)), (0.7, (0, 0, 0)))
    for i in (1, 2, 3):
        c.scale(f"blob_{i}", (0, 0.6), (0.3, 0.0, L), (1.4, 0.0, L))
    return c.finish()


def clip_overload(m):
    T = 0.5
    c = Clip(T, True)
    c.pos("works", *k.tremble(0, T, (0, 0, 0), (0.12, 0.05, 0.1), 0.05))
    wave(c, "piston_l", "position", 0.7, n=8)
    wave(c, "piston_r", "position", -0.7, n=8)
    wave(c, "beam", "rotation", THETA * 0.7, axis=2)
    c.rot("needle", *k.tremble(0, T, (0, 0, 205), (0, 0, 4), 0.05))
    c.rot("valve", *k.tremble(0, T, (0, 270, 0), (0, 2, 0), 0.05))
    c.scale("magma", (0, (1.04, 1.2, 1.04)), (0.12, (1.06, 1.28, 1.06)), (0.25, (1.03, 1.16, 1.03)), (0.37, (1.06, 1.27, 1.06)))
    c.rot("chimney_cap", (0, (-30, 0, 0)), (0.12, (-38, 0, 0)), (0.25, (-24, 0, 0)), (0.37, (-36, 0, 0)))
    blob_cycle(c, "blob_1", 0.0, 0.16, 1.3)
    blob_cycle(c, "blob_2", 0.17, 0.14, 1.2)
    blob_cycle(c, "blob_3", 0.33, 0.12, 1.4)
    return c.finish()


def clips(m):
    m.clips["idle"] = clip_idle(m)
    m.clips["start"] = clip_start(m)
    m.clips["run"] = clip_run()
    m.clips["stop"] = clip_stop(m)
    m.clips["overload"] = clip_overload(m)


def shots(m):
    run, idle, over = m.clips["run"], m.clips["idle"], m.clips["overload"]
    fl, br = math.radians(135), math.radians(-45)
    return [("idle", [(idle, 0.0, 1)], fl, math.radians(24)),
            ("run 0", [(run, 0.0, 1)], fl, math.radians(24)),
            ("run 0.3", [(run, 0.3, 1)], math.radians(165), math.radians(12)),
            ("overload", [(over, 0.1, 1)], fl, math.radians(24)),
            ("trás", [(run, 0.6, 1)], br, math.radians(24)),
            ("lado", [(run, 0.6, 1)], math.radians(90), math.radians(8)),
            ("cima", [(run, 0.0, 1)], fl, math.radians(62)),
            ("frente", [(idle, 0.0, 1)], math.radians(180), math.radians(4))]


if __name__ == "__main__":
    machine = build()
    k.write(machine, BASE, preview_shots=shots(machine) if "--preview" in sys.argv else None,
            register=("v1", "No jogo · v1, Gerador de Fenda animado (0.6.1)") if "--register" in sys.argv else None,
            particle="aquamarine:block/energy_vent_rock")
