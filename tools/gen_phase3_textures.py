"""Texturas de itens da Fase 3: Medidor, Regulador e Traje (3A); Escama Elétrica, Bobina Voltaica e
Chicote de Moréia (3B).

Uso (na raiz do repositório):
    C:\\Python314\\python tools/gen_phase3_textures.py            # gera tudo
    C:\\Python314\\python tools/gen_phase3_textures.py --preview  # também salva build/phase3_preview.png

Reaproveita as funções de tools/gen_textures.py (mesma paleta e mesmo sombreamento). O Traje usa a
rampa verde-escama "S" no lugar do dourado do metal Abissal, para as duas armaduras não se confundirem.
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

# Fase 3B: rampas novas (elétrico e cobre) para a Escama, a Bobina e o Chicote.
gt.RAMPS["E"] = [gt.hexc("0a1f3a"), gt.hexc("123a66"), gt.hexc("1d5d94"), gt.hexc("2f86bf"), gt.hexc("62bde6"), gt.hexc("c6f4ff")]
gt.RAMPS["R"] = [gt.hexc("3a0c10"), gt.hexc("6a1820"), gt.hexc("982630"), gt.hexc("c43c40"), gt.hexc("e86a60"), gt.hexc("ffb0a0")]
gt.RAMPS["C"] = [gt.hexc("3a1a0c"), gt.hexc("6a3018"), gt.hexc("9a4a24"), gt.hexc("c56a36"), gt.hexc("e8955a"), gt.hexc("ffd2a8")]

WHIP = [
    "................",
    ".........EEEE...",
    "........EEEEEE..",
    ".......EE....EE.",
    "......EE......E.",
    ".....EE.......v.",
    "....EE........y.",
    "...GG...........",
    "..hGG...........",
    ".hhh............",
    "hhh.............",
    "hh..............",
    "................",
    "................",
    "................",
    "................",
]


def electric_scale():
    """A Escama Antiga em azul elétrico, com um raio amarelo no veio."""
    img = gt.render([r.replace("S", "E") for r in gt.SCALE])
    for x, y in ((8, 3), (7, 4), (7, 5), (8, 6), (8, 7), (7, 8), (7, 9), (8, 10), (8, 11)):
        img.putpixel((x, y), gt.RAMPS["G"][5] if y % 2 else gt.RAMPS["G"][4])
    for x, y in ((4, 3), (5, 3), (3, 4)):
        img.putpixel((x, y), gt.RAMPS["E"][5])
    return img


def moray_whip():
    img = gt.render(WHIP)
    for x, y in ((15, 4), (13, 7), (15, 7)):
        img.putpixel((x, y), gt.RAMPS["T"][5])
    return img


# Sela de Baleia: assento de couro com arcos e correias de escama.
SADDLE = [
    "................",
    "................",
    "................",
    "...HH......HH...",
    "...HHH....HHH...",
    "..HHHHHHHHHHHH..",
    "..HHHHHHHHHHHH..",
    "..HHHHHHHHHHHH..",
    "...HHHHHHHHHH...",
    "....S......S....",
    "....S......S....",
    "....S......S....",
    "...SSS....SSS...",
    "................",
    "................",
    "................",
]


def whale_saddle():
    img = gt.render(SADDLE)
    for x, y, ch in ((7, 6, "q"), (8, 6, "r"), (4, 12, "4"), (11, 12, "4"), (5, 4, "m"), (10, 4, "m")):
        img.putpixel((x, y), gt.FIXED[ch])
    return img


# Fase 3C: Arpão, Aljava e pontas; 3D: Propulsor de Maré.
HARPOON = [
    "............55..",
    "...........5445.",
    "..........54GG4.",
    "...........GGG5.",
    "..........GGG.5.",
    ".........hhG....",
    "........hh......",
    ".......hh.......",
    "......hh........",
    ".....hh.........",
    "....hh..........",
    "...hh...........",
    "..GG............",
    ".GGG............",
    "..G.............",
    "................",
]

def harpoon():
    img = gt.render(HARPOON)
    for x, y in ((13, 1), (12, 2)):
        img.putpixel((x, y), gt.RAMPS["T"][5])
    return img


def diver_overlay(coat, accent):
    """Escafandro de latão com visor aberto (o rosto aparece) e casaco da profissão."""
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    px = img.load()
    brass, dark = gt.RAMPS["G"][3], gt.RAMPS["G"][1]
    # Chapéu do aldeão (32,0) 8x10x8: o capacete inteiro, rebites mais escuros.
    for x in range(32, 64):
        for y in range(0, 18):
            if y < 8 and not (40 <= x < 56):
                continue
            px[x, y] = dark if (x + y) % 5 == 0 else brass
    for x in range(42, 46):
        for y in range(11, 15):
            px[x, y] = (0, 0, 0, 0)
    for x in range(41, 47):
        for y in (10, 15):
            px[x, y] = gt.RAMPS["T"][4]
    for y in range(10, 16):
        px[41, y] = px[46, y] = gt.RAMPS["T"][4]
    # Casaco (camada do manto, 0,38 8x20x6): cor da profissão com cinto e detalhe.
    for x in range(0, 28):
        for y in range(38, 64):
            if y < 44 and not (6 <= x < 22):
                continue
            px[x, y] = coat if (x + y) % 7 else tuple(max(0, c - 25) for c in coat[:3]) + (255,)
    for x in range(0, 28):
        px[x, 50] = gt.RAMPS["H"][2]
    px[9, 50] = px[10, 50] = gt.RAMPS["G"][4]
    for x, y in ((7, 46), (12, 47), (8, 54)):
        px[x, y] = accent
    return img


# Capacete de mergulho clássico: cúpula verde-escama, visor redondo com aro de latão e gola.
DIVE_HELMET = [
    "................",
    ".....SSSSSS.....",
    "...SSSSSSSSSS...",
    "..SSSSGGGGSSSS..",
    ".SSSSGvvvvGSSSS.",
    ".SSSGvyyvvvGSSS.",
    ".SSSGvyvvvvGSSS.",
    ".SSSGvvvvvuGSSS.",
    ".SSSSGvvvuGSSSS.",
    "..SSSSGGGGSSSS..",
    "..SSSSSSSSSSSS..",
    ".GGGGGGGGGGGGGG.",
    ".G4G4G4G4G4G4GG.",
    "................",
    "................",
    "................",
]


def suit_icon(rows, gem=None):
    return gt.armor_icon([r.replace("G", "S") for r in rows], gem)


def suit_layers():
    """Camadas 64x32 do Traje: as placas do metal Abissal repintadas com a rampa de escama."""
    saved = gt.RAMPS["G"]
    gt.RAMPS["G"] = gt.RAMPS["S"]
    try:
        return gt.abyssal_layer(), gt.abyssal_leggings_layer()
    finally:
        gt.RAMPS["G"] = saved


def main():
    out = {
        "item/depth_gauge": gt.armor_icon(GAUGE, GAUGE_NEEDLE),
        "item/deep_diving_helmet": gt.render(DIVE_HELMET),
        "item/deep_diving_chestplate": suit_icon(gt.CHEST, {(7, 5): "y", (8, 5): "v"}),
        "item/deep_diving_leggings": suit_icon(gt.LEGS),
        "item/deep_diving_boots": suit_icon(gt.BOOTS),
        "item/electric_scale": electric_scale(),
        "item/moray_whip": moray_whip(),
        "item/whale_saddle": whale_saddle(),
        "item/abyssal_harpoon": harpoon(),
        }
    for name, coat, accent in (("tide_smith", gt.hexc("2a6e5f"), gt.RAMPS["G"][4]),
                               ("nautical_cartographer", gt.hexc("2c5a8e"), gt.FIXED["y"]),
                               ("abyss_enchanter", gt.hexc("50348f"), gt.FIXED["s"])):
        out[f"entity/villager/profession/{name}"] = diver_overlay(coat, accent)
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
