"""Texturas da Fase 3A: Medidor de Profundidade, Regulador de Pressão e Traje de Mergulho Profundo.

Uso (na raiz do repositório):
    C:\\Python314\\python tools/gen_phase3_textures.py            # gera tudo
    C:\\Python314\\python tools/gen_phase3_textures.py --preview  # também salva build/phase3_preview.png

Reaproveita as funções de tools/gen_textures.py (mesma paleta e mesmo sombreamento). O Traje usa a
rampa verde-escama "S" no lugar do dourado do Oricálcio, para as duas armaduras não se confundirem.
"""
import os
import sys

from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import gen_textures as gt  # noqa: E402

GAUGE = [
    "................",
    ".....GGGGGG.....",
    "...GGGGGGGGGG...",
    "..GGGuuuuuuGGG..",
    "..GGuuvvvvuuGG..",
    ".GGuuvvyyvvuuGG.",
    ".GGuuvvvvvvuuGG.",
    ".GGuuvvvyvvuuGG.",
    ".GGuuvvvvvvuuGG.",
    "..GGuuvvvvuuGG..",
    "..GGGuuuuuuGGG..",
    "...GGGGGGGGGG...",
    ".....GGGGGG.....",
    "......hhhh......",
    "................",
    "................",
]
GAUGE_NEEDLE = {(8, 4): "z", (8, 5): "z", (7, 6): "z", (7, 7): "z", (6, 8): "r", (9, 8): "r"}

REGULATOR = [
    "................",
    "......SSSS......",
    ".....SSSSSS.....",
    "......TTTT......",
    ".....GGGGGG.....",
    "....SSSSSSSS....",
    "....SSuuuuSS....",
    "....SSuvvuSS....",
    "....SSuuuuSS....",
    "....SSuvvuSS....",
    "....SSuuuuSS....",
    "....SSSSSSSS....",
    ".....SSSSSS.....",
    "......hhhh......",
    "................",
    "................",
]


def suit_icon(rows, gem=None):
    return gt.armor_icon([r.replace("G", "S") for r in rows], gem)


def suit_layers():
    """Camadas 64x32 do Traje: as placas do Oricálcio repintadas com a rampa de escama."""
    saved = gt.RAMPS["G"]
    gt.RAMPS["G"] = gt.RAMPS["S"]
    try:
        return gt.orichalcum_layer(), gt.orichalcum_leggings_layer()
    finally:
        gt.RAMPS["G"] = saved


def main():
    out = {
        "item/depth_gauge": gt.armor_icon(GAUGE, GAUGE_NEEDLE),
        "item/pressure_regulator": gt.render(REGULATOR),
        "item/deep_diving_helmet": suit_icon(gt.HELMET, {(7, 5): "y", (8, 5): "v"}),
        "item/deep_diving_chestplate": suit_icon(gt.CHEST, {(7, 5): "y", (8, 5): "v"}),
        "item/deep_diving_leggings": suit_icon(gt.LEGS),
        "item/deep_diving_boots": suit_icon(gt.BOOTS),
    }
    for rel, img in out.items():
        gt.save(img, rel + ".png")
    body, legs = suit_layers()
    gt.save(body, "entity/equipment/humanoid/deep_diving.png")
    gt.save(legs, "entity/equipment/humanoid_leggings/deep_diving.png")

    if "--preview" in sys.argv:
        scale = 8
        tiles = list(out.values()) + [body, legs]
        width = sum(t.width * scale + 8 for t in tiles)
        sheet = Image.new("RGBA", (width, 64 * scale), (22, 36, 56, 255))
        x = 0
        for t in tiles:
            sheet.paste(t.resize((t.width * scale, t.height * scale), Image.NEAREST), (x, 0))
            x += t.width * scale + 8
        os.makedirs(os.path.join(gt.REPO, "build"), exist_ok=True)
        sheet.save(os.path.join(gt.REPO, "build", "phase3_preview.png"))


if __name__ == "__main__":
    main()
