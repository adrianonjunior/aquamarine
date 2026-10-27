#!/usr/bin/env python3
"""Gera o painel do Coletor com instrumentos de latão, aço e vidro; sem ruído aleatório."""
from pathlib import Path
import re
import sys

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "tools/models/machines"))
import machine_kit as k  # noqa: E402

OUT = ROOT / "src/main/resources/assets/aquamarine/textures/gui/container/neutronium_collector.png"
MENU = ROOT / "src/main/java/com/aquamarine/menu/NeutroniumCollectorMenu.java"
W, H = 220, 224
SHEET = 256

# Grade funcional: todas as caixas têm moldura própria e texto com 3 px de respiro.
HEADER = (8, 6, 211, 20)
ENERGY = (6, 25, 65, 96)
CORE = (70, 25, 145, 96)
STOCK = (150, 25, 213, 96)
STATUS = (6, 101, 213, 127)
INVENTORY = (6, 131, 213, 220)
CORE_FRAMES_Y = 232
CORE_FRAME_SIZE = 24


def java_constant(name):
    text = MENU.read_text(encoding="utf-8")
    match = re.search(rf"public static final int {name}\s*=\s*(\d+);", text)
    if not match:
        raise ValueError(f"constante {name} não encontrada em {MENU}")
    return int(match.group(1))


def bevel(draw, box, ramp, level=2):
    """Placa chanfrada, iluminada no alto/esquerda e sombreada embaixo/direita."""
    x0, y0, x1, y1 = box
    draw.rectangle(box, fill=ramp[level])
    draw.line((x0, y0, x1, y0), fill=ramp[min(5, level + 2)])
    draw.line((x0, y0, x0, y1), fill=ramp[min(5, level + 1)])
    draw.line((x0, y1, x1, y1), fill=ramp[max(0, level - 2)])
    draw.line((x1, y0, x1, y1), fill=ramp[max(0, level - 1)])
    if x1 - x0 > 5 and y1 - y0 > 5:
        draw.line((x0 + 2, y0 + 2, x1 - 2, y0 + 2), fill=ramp[min(5, level + 1)])


def inset(draw, box, ramp, level=1):
    x0, y0, x1, y1 = box
    draw.rectangle(box, fill=ramp[max(0, level - 1)])
    draw.line((x0, y0, x1, y0), fill=ramp[0])
    draw.line((x0, y0, x0, y1), fill=ramp[0])
    draw.line((x0, y1, x1, y1), fill=ramp[min(5, level + 2)])
    draw.line((x1, y0, x1, y1), fill=ramp[min(5, level + 1)])


def ribs(draw, box, ramp, step=8):
    """Quebra superfícies grandes em chapas prensadas, sem textura aleatória."""
    x0, y0, x1, y1 = box
    for x in range(x0 + step, x1, step):
        draw.line((x, y0 + 2, x, y1 - 2), fill=ramp[1])
        if x + 1 < x1:
            draw.line((x + 1, y0 + 2, x + 1, y1 - 2), fill=ramp[3])


def rivet(draw, x, y, ramp=k.BRASS):
    draw.point((x, y), fill=ramp[5])
    draw.point((x + 1, y), fill=ramp[3])
    draw.point((x, y + 1), fill=ramp[2])
    draw.point((x + 1, y + 1), fill=ramp[0])


def rivet_edge(draw, box, step=8):
    x0, y0, x1, y1 = box
    for x in range(x0 + 4, x1 - 2, step):
        rivet(draw, x, y0 + 2)
        rivet(draw, x, y1 - 3)


def plaque(draw, box):
    bevel(draw, box, k.BRASS, 3)
    x0, y0, x1, y1 = box
    draw.rectangle((x0 + 2, y0 + 2, x1 - 2, y1 - 2), fill=k.BRASS[4])
    draw.line((x0 + 2, y1 - 2, x1 - 2, y1 - 2), fill=k.BRASS[1])


def instrument(draw, box):
    bevel(draw, box, k.BRASS, 2)
    x0, y0, x1, y1 = box
    inset(draw, (x0 + 3, y0 + 3, x1 - 3, y1 - 3), k.STEEL, 2)
    ribs(draw, (x0 + 3, y0 + 3, x1 - 3, y1 - 3), k.STEEL)
    rivet(draw, x0 + 2, y0 + 2)
    rivet(draw, x1 - 3, y0 + 2)
    rivet(draw, x0 + 2, y1 - 3)
    rivet(draw, x1 - 3, y1 - 3)


def glass(draw, box):
    x0, y0, x1, y1 = box
    inset(draw, box, k.STEEL, 1)
    draw.rectangle((x0 + 2, y0 + 2, x1 - 2, y1 - 2), fill=k.GLASS[1])
    for y in range(y0 + 3, y1 - 1):
        level = min(4, 1 + (y - y0) * 3 // max(1, y1 - y0))
        draw.line((x0 + 3, y, x1 - 3, y), fill=k.GLASS[level])
    draw.line((x0 + 4, y1 - 3, x0 + 9, y0 + 3), fill=k.GLASS[5])
    if x1 - x0 > 18:
        draw.line((x0 + 10, y1 - 3, x0 + 15, y0 + 3), fill=k.GLASS[4])


def slot_well(draw, slot_x, slot_y):
    """Moldura 18×18: item real ocupa slot_x..+15, exatamente como Slot do Menu."""
    x0, y0 = slot_x - 1, slot_y - 1
    draw.rectangle((x0, y0, x0 + 17, y0 + 17), fill=k.BRASS[1])
    draw.line((x0, y0, x0 + 17, y0), fill=k.BRASS[5])
    draw.line((x0, y0, x0, y0 + 17), fill=k.BRASS[4])
    draw.line((x0, y0 + 17, x0 + 17, y0 + 17), fill=k.BRASS[0])
    draw.line((x0 + 17, y0, x0 + 17, y0 + 17), fill=k.BRASS[0])
    draw.rectangle((slot_x, slot_y, slot_x + 15, slot_y + 15), fill=k.STEEL[1])
    draw.line((slot_x + 1, slot_y + 1, slot_x + 14, slot_y + 1), fill=k.STEEL[0])
    draw.line((slot_x + 1, slot_y + 14, slot_x + 14, slot_y + 14), fill=k.STEEL[3])


def core_frame(image, frame):
    """Esfera estelar pixelada: oito pulsações determinísticas, sem a antiga cruz branca."""
    ox = frame * CORE_FRAME_SIZE
    oy = CORE_FRAMES_Y
    pixels = image.load()
    radius = (7, 7, 8, 8, 9, 8, 8, 7)[frame]
    cx = cy = 11.5
    for py in range(CORE_FRAME_SIZE):
        for px in range(CORE_FRAME_SIZE):
            dx, dy = px - cx, py - cy
            distance = dx * dx + dy * dy
            if distance > radius * radius:
                continue
            edge = distance ** 0.5 / radius
            light = max(0.0, 1.0 - ((dx + 3.0) ** 2 + (dy + 3.0) ** 2) ** 0.5 / (radius * 1.55))
            level = max(1, min(5, round((1.0 - edge) * 3 + light * 2)))
            pixels[ox + px, oy + py] = k.CYAN[level]
    draw = ImageDraw.Draw(image)
    # Reflexos e estrelas deslocam um pixel entre quadros e mantêm volume esférico.
    stars = ((8, 7), (13, 9), (10, 13), (15, 14))
    for index, (sx, sy) in enumerate(stars):
        if (frame + index) % 3:
            draw.point((ox + sx + (frame & 1), oy + sy), fill=k.STEAM[5])
    ray = 1 + (1 if frame in (3, 4, 5) else 0)
    draw.line((ox + 12, oy + 1, ox + 12, oy + ray + 1), fill=k.CYAN[5])
    draw.line((ox + 12, oy + 22 - ray, ox + 12, oy + 22), fill=k.CYAN[4])
    draw.point((ox + 2, oy + 11), fill=k.CYAN[4])
    draw.point((ox + 21, oy + 12), fill=k.CYAN[5])


def draw_panel(image):
    d = ImageDraw.Draw(image)
    bevel(d, (0, 0, W - 1, H - 1), k.BRASS, 2)
    bevel(d, (3, 3, W - 4, H - 4), k.STEEL, 2)
    ribs(d, (4, 4, W - 5, H - 5), k.STEEL)
    rivet_edge(d, (3, 3, W - 4, H - 4))

    plaque(d, HEADER)
    for box in (ENERGY, CORE, STOCK):
        instrument(d, box)
    plaque(d, (9, 28, 62, 41))
    plaque(d, (73, 28, 142, 41))
    plaque(d, (151, 28, 212, 41))

    # Energia: tubo alto com graduação e duas leituras externas.
    glass(d, (10, 43, 30, 92))
    for y in range(48, 90, 7):
        d.line((24, y, 28, y), fill=k.CYAN[5])
        d.point((23, y), fill=k.CYAN[2])
    inset(d, (31, 50, 64, 84), k.BRASS, 2)
    d.line((33, 67, 62, 67), fill=k.BRASS[4])
    rivet(d, 32, 51); rivet(d, 61, 81)

    # Núcleo: visor largo, anel dinâmico desenhado pela Screen e duas plaquinhas.
    glass(d, (73, 44, 142, 77))
    inset(d, (73, 79, 105, 93), k.BRASS, 2)
    inset(d, (108, 79, 142, 93), k.BRASS, 2)
    d.point((110, 81), fill=k.CYAN[5]); d.point((111, 81), fill=k.CYAN[3])

    # Estoque: bandeja de vidro, slot real grande e contador independente.
    glass(d, (154, 44, 209, 76))
    output_x = java_constant("OUTPUT_SLOT_X")
    output_y = java_constant("OUTPUT_SLOT_Y")
    bevel(d, (output_x - 6, output_y - 5, output_x + 21, output_y + 20), k.BRASS, 2)
    slot_well(d, output_x, output_y)
    inset(d, (154, 79, 210, 93), k.BRASS, 2)

    # Estado em duas linhas; o ícone colorido é aplicado ao vivo pela Screen.
    bevel(d, STATUS, k.BRASS, 2)
    inset(d, (9, 104, 210, 124), k.STEEL, 2)
    inset(d, (11, 106, 24, 122), k.STEEL, 1)
    d.line((27, 105, 27, 123), fill=k.BRASS[4])
    d.line((28, 105, 28, 123), fill=k.BRASS[0])

    # Inventário: placa nominal, fundo prensado e quatro fileiras de poços de latão.
    bevel(d, INVENTORY, k.BRASS, 2)
    inset(d, (9, 134, 210, 217), k.STEEL, 2)
    ribs(d, (9, 134, 210, 217), k.STEEL)
    plaque(d, (10, 133, 85, 143))
    inv_x = java_constant("PLAYER_INVENTORY_X")
    inv_y = java_constant("PLAYER_INVENTORY_Y")
    for row in range(3):
        for col in range(9):
            slot_well(d, inv_x + col * 18, inv_y + row * 18)
    for col in range(9):
        slot_well(d, inv_x + col * 18, inv_y + 58)

    # Pátina/sal só nas frestas onde latão, aço e vidro se encontram.
    for x, y in ((7, 96), (64, 94), (146, 94), (211, 94), (7, 127), (211, 128)):
        d.point((x, y), fill=k.PATINA[3])
        d.point((x + (1 if x < W // 2 else -1), y), fill=k.PATINA[1])


def main():
    image = Image.new("RGBA", (SHEET, SHEET), (0, 0, 0, 0))
    draw_panel(image)
    for frame in range(8):
        core_frame(image, frame)
    OUT.parent.mkdir(parents=True, exist_ok=True)
    image.save(OUT, optimize=True)
    print(OUT)


if __name__ == "__main__":
    main()
