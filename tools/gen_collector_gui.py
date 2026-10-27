#!/usr/bin/env python3
"""Gera o painel do Coletor de Neutrônio com as rampas/pintores do machine_kit; sem ruído aleatório."""
from pathlib import Path
import sys
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "tools/models/machines"))
import machine_kit as k  # noqa: E402

OUT = ROOT / "src/main/resources/assets/aquamarine/textures/gui/container/neutronium_collector.png"
W, H = 220, 202


def rect(draw, box, ramp, level=3, outline=1):
    x0, y0, x1, y1 = box
    draw.rectangle(box, fill=ramp[level], outline=ramp[max(0, level - outline)])
    draw.line((x0 + 1, y0 + 1, x1 - 1, y0 + 1), fill=ramp[min(5, level + 2)])
    draw.line((x0 + 1, y1 - 1, x1 - 1, y1 - 1), fill=ramp[max(0, level - 2)])


def rivets(draw, box):
    x0, y0, x1, y1 = box
    for x, y in ((x0 + 3, y0 + 3), (x1 - 3, y0 + 3), (x0 + 3, y1 - 3), (x1 - 3, y1 - 3)):
        draw.point((x, y), fill=k.BRASS[5]); draw.point((x + 1, y + 1), fill=k.BRASS[0])


def glass(draw, box):
    x0, y0, x1, y1 = box
    draw.rectangle(box, fill=(*k.GLASS[1][:3], 255), outline=k.BRASS[1])
    for y in range(y0 + 2, y1):
        tone = k.GLASS[min(4, 1 + (y - y0) * 3 // max(1, y1 - y0))]
        draw.line((x0 + 2, y, x1 - 2, y), fill=tone)
    draw.line((x0 + 4, y1 - 3, x0 + 14, y0 + 3), fill=k.GLASS[5])
    draw.line((x0 + 8, y1 - 3, x0 + 18, y0 + 3), fill=k.GLASS[4])


def slot(draw, x, y):
    draw.rectangle((x, y, x + 17, y + 17), fill=k.STEEL[0])
    draw.line((x + 1, y + 1, x + 16, y + 1), fill=k.STEEL[4])
    draw.line((x + 1, y + 1, x + 1, y + 16), fill=k.STEEL[4])
    draw.rectangle((x + 2, y + 2, x + 15, y + 15), fill=k.STEEL[2])


def main():
    image = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    d = ImageDraw.Draw(image)
    rect(d, (0, 0, W - 1, H - 1), k.BRASS, 2)
    rect(d, (3, 3, W - 4, H - 4), k.STEEL, 2)
    rect(d, (6, 6, W - 7, 116), k.BRASS, 3)
    rivets(d, (6, 6, W - 7, 116))
    # Energia, núcleo e estoque: três instrumentos com pesos visuais diferentes.
    glass(d, (13, 23, 54, 84)); rect(d, (17, 27, 32, 80), k.STEEL, 1)
    for y in range(31, 80, 7): d.line((20, y, 29, y), fill=k.CYAN[3])
    rect(d, (62, 18, 152, 86), k.STEEL, 1); glass(d, (66, 22, 148, 82))
    for a in range(0, 360, 45):
        import math
        x = 107 + round(33 * math.cos(math.radians(a))); y = 52 + round(24 * math.sin(math.radians(a)))
        d.rectangle((x - 1, y - 1, x + 1, y + 1), fill=k.BRASS[5])
    rect(d, (160, 20, 207, 82), k.BRASS, 2); glass(d, (164, 24, 203, 78))
    slot(d, 177, 46)
    # Faixa de estado e inventário vanilla integrado ao mesmo painel.
    rect(d, (10, 88, W - 11, 98), k.STEEL, 1)
    rect(d, (10, 100, W - 11, 116), k.STEEL, 1)
    rect(d, (6, 118, W - 7, H - 7), k.BRASS, 2)
    for row in range(3):
        for col in range(9): slot(d, 28 + col * 18, 119 + row * 18)
    for col in range(9): slot(d, 28 + col * 18, 177)
    # Parafusos a cada 8 px e marcas de limo/sal nas juntas.
    for x in range(11, W - 10, 8):
        d.point((x, 7), fill=k.BRASS[5]); d.point((x + 1, 8), fill=k.BRASS[0])
    for x, y in ((8, 110), (44, 114), (154, 7), (210, 71)):
        d.rectangle((x, y, x + 2, y + 1), fill=k.PATINA[3])
    OUT.parent.mkdir(parents=True, exist_ok=True)
    image.save(OUT, optimize=True)
    print(OUT)


if __name__ == "__main__": main()
