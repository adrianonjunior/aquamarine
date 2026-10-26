"""Blocos de construção aquáticos e vidros rachados da Fase 4, faixa 4E (docs/10-fase4.md, §6 e §7).

Uma lista só (FAMILIES e GLASSES) gera tudo: texturas, modelos, estados, itens, loot, receitas, tags, nomes nos dois
idiomas e a classe Java de registro (ModBuildingBlocks). As tabelas de estado de escada, muro e vidraça vêm do jogo
(tools/vanilla_state_tables.json, só o formato).

    C:\\Python314\\python tools/gen_phase4_building.py [--preview]
"""
import json
import math
import os
import random
import sys

from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import gen_phase4_art as art  # noqa: E402
from gen_phase4_art import Tex, Model, hexc, ramp, outline, CLEAR, CYAN, CYAN_SOFT  # noqa: E402

REPO, NS = art.REPO, art.NS
ASSETS = art.ASSETS
DATA = os.path.join(REPO, "src", "main", "resources", "data")
JAVA = os.path.join(REPO, "src", "main", "java", "com", "aquamarine", "block", "ModBuildingBlocks.java")
STATES = json.load(open(os.path.join(os.path.dirname(os.path.abspath(__file__)), "vanilla_state_tables.json"), encoding="utf-8"))

TIDE = ramp("172224", "212f31", "2c3c3e", "384a4b", "485d5d", "5f7673", "7a948f")
VEIN = ramp("1f655c", "2c8576", "46a892", "7dd3bd")
CORALP = ramp("3c2e33", "55444a", "6e5a60", "887278", "a38c90", "c2aeae")
PORE = ramp("8f2849", "c4416a", "ef9ab0")
SHINGLE = ramp("5a4234", "86684f", "b08d6e", "d4b392", "ecd5b6", "fbeedb")
ABY = ramp("070a12", "0e1422", "151e33", "1d2a45", "273859", "33476e")
GOLDR = art.GOLD
IRONR = art.IRON


# ================================================================ texturas

def stone_noise(seed, pal, weights=(2, 4, 3, 1)):
    t = Tex(seed)
    t.noise((0, 0, 16, 16), pal[1:5], list(weights))
    return t


def veins(t, seed, count=3, pal=VEIN):
    rng = random.Random(seed)
    for _ in range(count):
        y = rng.uniform(2, 14)
        phase, amp, freq = rng.uniform(0, 6), rng.uniform(0.8, 2.2), rng.uniform(0.3, 0.7)
        for x in range(16):
            yy = round(y + amp * math.sin(x * freq + phase))
            if rng.random() < 0.85:
                t.put(x, yy % 16, pal[rng.choice((1, 2, 2, 3))])


def tidestone():
    t = stone_noise(601, TIDE)
    veins(t, 602, 3)
    return t.img


def polished_tidestone():
    t = Tex(603)
    t.noise((0, 0, 16, 16), TIDE[3:5], [3, 2])
    veins(t, 604, 1)
    t.bevel((0, 0, 16, 16), TIDE[6], TIDE[1])
    t.bevel((1, 1, 15, 15), TIDE[5], TIDE[2])
    return t.img


def bricks(seed, pal, mortar, glints=None, cracked=False, rows=4):
    t = Tex(seed)
    t.noise((0, 0, 16, 16), pal[2:5], [2, 4, 2])
    h = 16 // rows
    for r in range(rows):
        y0 = r * h
        t.hline(0, 15, y0, mortar)
        off = 0 if r % 2 == 0 else 4
        for x in range(off, 16 + off, 8):
            t.vline(x % 16, y0, y0 + h - 1, mortar)
        for x in range(16):  # luz no alto de cada tijolo
            if t.get(x, y0 + 1) != mortar:
                t.put(x, y0 + 1, pal[5])
    if glints:
        t.scatter(5, glints)
    if cracked:
        rng = random.Random(seed + 1)
        for _ in range(3):
            x, y = rng.randrange(2, 14), rng.randrange(2, 14)
            for _ in range(rng.randint(4, 7)):
                t.put(x, y, pal[0])
                x += rng.choice((-1, 0, 1))
                y += rng.choice((-1, 1))
    return t.img


def chiseled_tidestone():
    """Tridente entalhado no meio da Pedra de Maré Polida (sulco escuro com a luz embaixo)."""
    t = Tex(611)
    t.img.paste(polished_tidestone(), (0, 0))
    carve = [(8, y) for y in range(5, 13)] + [(6, 4), (6, 5), (6, 6), (7, 6), (9, 6), (10, 6), (10, 5), (10, 4), (8, 3), (8, 4)]
    carve += [(7, 12), (9, 12), (8, 13)]
    for x, y in carve:
        t.put(x, y, TIDE[1])
    for x, y in carve:
        if (x + 1, y + 1) not in carve:
            t.put(x + 1, y + 1, TIDE[6])
    t.put(8, 3, VEIN[3])
    t.put(6, 4, VEIN[2])
    t.put(10, 4, VEIN[2])
    return t.img


def pillar_side():
    t = Tex(612)
    t.noise((0, 0, 16, 16), TIDE[3:5], [3, 2])
    for x in (2, 5, 10, 13):  # caneluras
        t.vline(x, 2, 13, TIDE[1])
        t.vline(x + 1, 2, 13, TIDE[5])
    for y in (0, 1, 14, 15):
        t.hline(0, 15, y, TIDE[6] if y in (0, 14) else TIDE[2])
    t.hline(0, 15, 1, VEIN[1])
    t.hline(0, 15, 14, VEIN[1])
    return t.img


def pillar_top():
    t = Tex(613)
    for y in range(16):
        for x in range(16):
            d = math.hypot(x + 0.5 - 8, y + 0.5 - 8)
            t.put(x, y, TIDE[2 + int(d) % 3] if d < 7.5 else TIDE[1])
    t.disc(8, 8, 1.6, VEIN[2])
    return t.img


def coral_bricks():
    img = bricks(621, CORALP, CORALP[0], glints=PORE)
    t = Tex(622)
    t.img, t.px = img, img.load()
    t.scatter(18, [CORALP[1], CORALP[2]])  # poros
    return t.img


def coral_tiles():
    t = Tex(623)
    t.noise((0, 0, 16, 16), CORALP[2:5], [2, 4, 2])
    for k in (0, 8):
        t.hline(0, 15, k, CORALP[0])
        t.vline(k, 0, 15, CORALP[0])
        t.hline(0, 15, k + 1, CORALP[5])
    t.scatter(16, [CORALP[1], PORE[0], PORE[1]])
    return t.img


def shell_shingles():
    t = Tex(631)
    for row in range(4):  # fileiras de telhas em escama
        y0 = row * 4
        off = 0 if row % 2 == 0 else 2
        for x in range(16):
            local = (x + off) % 4
            for dy in range(4):
                edge = dy == 3 or (dy == 2 and local in (0, 3))
                c = SHINGLE[1] if edge else SHINGLE[4] if dy == 0 else SHINGLE[3]
                if local == 1 and dy == 1:
                    c = SHINGLE[5]
                t.put(x, y0 + dy, c)
    t.scatter(6, [(236, 170, 176, 255)])  # nácar rosado
    return t.img


def abyssal_tiles(chiseled=False):
    t = Tex(641 + chiseled)
    t.noise((0, 0, 16, 16), ABY[2:5], [2, 4, 2])
    if chiseled:
        t.bevel((0, 0, 16, 16), GOLDR[4], GOLDR[1])
        for y in range(16):  # olho do Kraken
            for x in range(16):
                d = math.hypot(x + 0.5 - 8, (y + 0.5 - 8) * 1.6)
                if 3.2 <= d < 4.4:
                    t.put(x, y, GOLDR[4] if y < 8 else GOLDR[2])
                elif d < 3.2:
                    t.put(x, y, CYAN if abs(x + 0.5 - 8) > 0.9 else ABY[0])
        t.put(6, 7, (230, 255, 252, 255))
    else:
        for k in (0, 8):
            t.hline(0, 15, k, GOLDR[2])
            t.vline(k, 0, 15, GOLDR[2])
        for x, y in ((0, 0), (8, 0), (0, 8), (8, 8)):
            t.put(x, y, GOLDR[5])
        for x, y in ((3, 4), (12, 3), (4, 12), (11, 11)):
            t.put(x, y, ABY[5])
    return t.img


def sea_glass():
    t = Tex(651)
    for y in range(16):
        for x in range(16):
            t.put(x, y, (70, 170, 160, 150) if (x * 7 + y * 3) % 11 else (120, 210, 200, 165))
    t.bevel((0, 0, 16, 16), (190, 245, 235, 220), (40, 120, 116, 220))
    for i in range(4):
        t.put(3 + i, 3 + i, (220, 255, 250, 190))
    return t.img


def pane_edge(color):
    t = Tex(652)
    for y in range(16):
        t.put(7, y, color)
        t.put(8, y, tuple(max(0, v - 30) for v in color[:3]) + (color[3],))
    return t.img


def jellyfish_frames():
    """Lanterna de Água-viva: geleia violeta com a água-viva acesa dentro; 4 quadros pulsando."""
    img = Image.new("RGBA", (16, 64), CLEAR)
    for f in range(4):
        t = Tex(661)
        pulse = (0, 18, 36, 18)[f]
        t.noise((0, 0, 16, 16), [(40, 30, 90, 255), (54, 40, 116, 255), (66, 52, 140, 255)], [3, 4, 2])
        t.bevel((0, 0, 16, 16), (120, 110, 200, 255), (24, 18, 60, 255))
        for y in range(3, 9):  # sino da água-viva
            for x in range(4, 12):
                if (x + 0.5 - 8) ** 2 / 16 + (y + 0.5 - 8) ** 2 / 25 <= 1:
                    t.put(x, y, (min(255, 150 + pulse), min(255, 230 + pulse // 2), 255, 255))
        for x in (5, 7, 9, 11):  # tentáculos
            for y in range(9, 14):
                if (y + x + f) % 3:
                    t.put(x + (y % 2) - 1, y, CYAN if y < 12 else CYAN_SOFT)
        img.paste(t.img, (0, 16 * f))
    return img


def lamp_tex():
    t = Tex(671)
    t.noise((0, 0, 16, 16), art.BRASS[2:5], [2, 4, 2])
    t.rect((0, 8, 8, 16), (226, 250, 255, 255))  # pérola
    t.disc(3, 11, 2.2, (255, 255, 255, 255))
    t.put(2, 10, (255, 255, 255, 255))
    t.rect((8, 8, 16, 16), art.IRON[3])  # corrente
    for y in range(8, 16, 2):
        t.put(11, y, art.IRON[5])
        t.put(12, y + 1, art.IRON[5])
    return t.img


def grate_tex():
    t = Tex(681)
    for y in range(16):
        for x in range(16):
            bar = x % 4 in (0, 3) or y % 4 in (0, 3)
            t.put(x, y, (IRONR[4] if (x + y) % 2 else IRONR[3]) if bar else CLEAR)
    for x, y in ((0, 0), (12, 4), (4, 12), (8, 8)):  # cracas
        t.put(x, y, art.BARNACLE[3])
        t.put(x + 1, y, art.BARNACLE[2])
    t.bevel((0, 0, 16, 16), IRONR[5], IRONR[1])
    return t.img


# ---------------------------------------------------------------- vidros rachados

DYES = [("white", (249, 255, 254), "Branco", "Branca", "White"), ("orange", (249, 128, 29), "Laranja", "Laranja", "Orange"),
        ("magenta", (199, 78, 189), "Magenta", "Magenta", "Magenta"),
        ("light_blue", (58, 179, 218), "Azul-Claro", "Azul-Clara", "Light Blue"),
        ("yellow", (254, 216, 61), "Amarelo", "Amarela", "Yellow"), ("lime", (128, 199, 31), "Verde-Limão", "Verde-Limão", "Lime"),
        ("pink", (243, 139, 170), "Rosa", "Rosa", "Pink"), ("gray", (71, 79, 82), "Cinza", "Cinza", "Gray"),
        ("light_gray", (157, 157, 151), "Cinza-Claro", "Cinza-Clara", "Light Gray"), ("cyan", (22, 156, 156), "Ciano", "Ciano", "Cyan"),
        ("purple", (137, 50, 184), "Roxo", "Roxa", "Purple"), ("blue", (60, 68, 170), "Azul", "Azul", "Blue"),
        ("brown", (131, 84, 50), "Marrom", "Marrom", "Brown"), ("green", (94, 124, 22), "Verde", "Verde", "Green"),
        ("red", (176, 46, 38), "Vermelho", "Vermelha", "Red"), ("black", (29, 29, 33), "Preto", "Preta", "Black")]


def crack_mask(seed):
    """Rachaduras finas saindo de um ponto de impacto, com um anel partido em volta e um furinho no meio.
    Devolve {(x, y): força 0..1}; força 2 = furo."""
    rng = random.Random(seed)
    cx, cy = 10.5, 5.5
    mask = {(10, 5): 2, (11, 5): 2, (10, 6): 2}
    rays = []
    for k in range(6):
        ang = k * 2 * math.pi / 6 + rng.uniform(-0.4, 0.4)
        x, y, pts = cx, cy, []
        length = rng.randint(5, 12)
        for step in range(length):
            ang += rng.uniform(-0.55, 0.55)
            x += math.cos(ang) * 0.9
            y += math.sin(ang) * 0.9
            p = (int(x), int(y))
            if not (1 <= p[0] < 15 and 1 <= p[1] < 15):
                break
            pts.append(p)
            mask[p] = max(mask.get(p, 0), 1.0 - step / (length + 2))
            if step > 2 and rng.random() < 0.18:  # galho curto
                bx, by, bang = x, y, ang + rng.choice((-1, 1)) * rng.uniform(0.7, 1.2)
                for i in range(rng.randint(2, 4)):
                    bx += math.cos(bang)
                    by += math.sin(bang)
                    q = (int(bx), int(by))
                    if 1 <= q[0] < 15 and 1 <= q[1] < 15:
                        mask[q] = max(mask.get(q, 0), 0.45)
        rays.append(pts)
    for i in range(0, 360, 14):  # anel partido a ~3 px do impacto
        if rng.random() < 0.55:
            a = math.radians(i)
            q = (int(cx + 3.0 * math.cos(a)), int(cy + 3.0 * math.sin(a)))
            if 1 <= q[0] < 15 and 1 <= q[1] < 15:
                mask[q] = max(mask.get(q, 0), 0.7)
    return mask


MASK = crack_mask(700)


def glass_tex(rgb, kind):
    """kind: clear, stained ou tinted. Moldura e brilho próprios do mod mais as rachaduras."""
    t = Tex(701)
    r, g, b = rgb
    if kind == "clear":
        inner, frame, crack = (0, 0, 0, 0), (200, 226, 232, 255), (236, 250, 255, 235)
    elif kind == "tinted":
        inner, frame, crack = (44, 34, 52, 205), (70, 58, 82, 235), (150, 136, 170, 240)
    else:
        inner = (r, g, b, 105)
        frame = (min(255, r + 30), min(255, g + 30), min(255, b + 30), 190)
        crack = (min(255, r + 120), min(255, g + 120), min(255, b + 120), 230)
    t.rect((0, 0, 16, 16), inner)
    t.bevel((0, 0, 16, 16), frame, frame)
    for i in range(3):  # brilho diagonal
        t.put(2 + i, 4 - i, (255, 255, 255, 150) if kind == "clear" else crack[:3] + (120,))
    for (x, y), force in MASK.items():
        if force >= 2:
            t.put(x, y, CLEAR)
        else:
            t.put(x, y, crack[:3] + (round(crack[3] * (0.45 + 0.55 * force)),))
    return t.img


# ================================================================ famílias

def fam(base, pt, en, tex, extra=(), shapes=("stairs", "slab", "wall"), tool=True, recipe=None, cut_from=None):
    return {"base": base, "pt": pt, "en": en, "tex": tex, "shapes": shapes, "tool": tool, "recipe": recipe,
            "cut_from": cut_from, "extra": extra}


# (id, nome pt, nome en, textura ou dict de faces, tipo)
BASE_BLOCKS = [
    ("tidestone", "Pedra de Maré", "Tidestone", tidestone, "stone"),
    ("polished_tidestone", "Pedra de Maré Polida", "Polished Tidestone", polished_tidestone, "stone"),
    ("tidestone_bricks", "Tijolos de Pedra de Maré", "Tidestone Bricks",
     lambda: bricks(605, TIDE, TIDE[0], glints=VEIN[2:4]), "stone"),
    ("cracked_tidestone_bricks", "Tijolos de Pedra de Maré Rachados", "Cracked Tidestone Bricks",
     lambda: bricks(605, TIDE, TIDE[0], glints=VEIN[2:4], cracked=True), "stone"),
    ("chiseled_tidestone", "Pedra de Maré Talhada", "Chiseled Tidestone", chiseled_tidestone, "stone"),
    ("tidestone_pillar", "Pilar de Pedra de Maré", "Tidestone Pillar", None, "pillar"),
    ("petrified_coral_bricks", "Tijolos de Coral Petrificado", "Petrified Coral Bricks", coral_bricks, "stone"),
    ("petrified_coral_tiles", "Ladrilhos de Coral Petrificado", "Petrified Coral Tiles", coral_tiles, "stone"),
    ("shell_shingles", "Telhas de Concha", "Shell Shingles", shell_shingles, "stone"),
    ("abyssal_tiles", "Ladrilhos Abissais", "Abyssal Tiles", lambda: abyssal_tiles(False), "stone"),
    ("chiseled_abyssal_tiles", "Ladrilho Abissal Talhado", "Chiseled Abyssal Tiles", lambda: abyssal_tiles(True), "stone"),
    ("sea_glass", "Vidro do Mar", "Sea Glass", sea_glass, "glass"),
    ("jellyfish_lantern", "Lanterna de Água-viva", "Jellyfish Lantern", None, "jelly"),
    ("pearl_lamp", "Lampião de Pérola", "Pearl Lamp", None, "lamp"),
    ("naval_grate", "Grade Naval", "Naval Grate", grate_tex, "grate"),
]
# variantes de formato: (id da base, quais)
SHAPED = {"tidestone": ("stairs", "slab", "wall"), "polished_tidestone": ("stairs", "slab", "wall"),
          "tidestone_bricks": ("stairs", "slab", "wall"), "petrified_coral_bricks": ("stairs", "slab", "wall"),
          "petrified_coral_tiles": ("stairs", "slab"), "shell_shingles": ("stairs", "slab"),
          "abyssal_tiles": ("stairs", "slab", "wall")}
# o que sai de cada base na serra (além das próprias variantes)
CUTS = {"tidestone": ["polished_tidestone", "tidestone_bricks", "chiseled_tidestone", "tidestone_pillar"],
        "polished_tidestone": ["tidestone_bricks", "chiseled_tidestone", "tidestone_pillar"],
        "petrified_coral_bricks": ["petrified_coral_tiles"], "abyssal_tiles": ["chiseled_abyssal_tiles"]}
TOOLTIPS = {
    "tidestone": ("Pedra do litoral com veios de maré.", "Rock from the shore with tidal veins."),
    "polished_tidestone": ("Pedra de Maré lisa, com moldura.", "Smoothed Tidestone with a frame."),
    "tidestone_bricks": ("Tijolos para muralhas de porto.", "Bricks for harbor walls."),
    "cracked_tidestone_bricks": ("Tijolos gastos pela maré.", "Bricks worn down by the tide."),
    "chiseled_tidestone": ("Ondas e tridente talhados.", "Carved waves and trident."),
    "tidestone_pillar": ("Coluna com caneluras.", "Fluted column."),
    "petrified_coral_bricks": ("Coral morto compactado em tijolos.", "Dead coral packed into bricks."),
    "petrified_coral_tiles": ("Ladrilhos porosos de coral.", "Porous coral tiles."),
    "shell_shingles": ("Telhas em escama para telhados.", "Scale-shaped tiles for roofs."),
    "abyssal_tiles": ("Ardósia com filetes de metal Abissal.", "Slate with Abyssal metal inlays."),
    "chiseled_abyssal_tiles": ("O olho do Kraken em ouro.", "The Kraken's eye in gold."),
    "sea_glass": ("Vidro fosco cor de água rasa.", "Frosted glass the color of shallow water."),
    "sea_glass_pane": ("Vidraça fosca cor de água rasa.", "Frosted pane the color of shallow water."),
    "jellyfish_lantern": ("Luz 15; a água-viva pulsa dentro.", "Light 15; the jellyfish pulses inside."),
    "pearl_lamp": ("Luz 15; pendura no teto como lampião.", "Light 15; hangs from ceilings like a lantern."),
    "naval_grate": ("Grade de ferro com cracas; aceita água.", "Iron grate with barnacles; holds water."),
}
SHAPE_PT = {"stairs": "Escadas de {}", "slab": "Laje de {}", "wall": "Muro de {}"}
SHAPE_EN = {"stairs": "{} Stairs", "slab": "{} Slab", "wall": "{} Wall"}


def shape_name(base_pt, base_en, shape):
    """Como o jogo: 'Escadas de Tijolos de Pedra' e 'Stone Brick Stairs' (o plural vira singular no inglês)."""
    en_base = base_en
    for plural in ("Bricks", "Tiles", "Shingles"):
        if en_base.endswith(plural):
            en_base = en_base[:-1]
    return SHAPE_PT[shape].format(base_pt), SHAPE_EN[shape].format(en_base)


# ================================================================ escrita

def write_png(img, rel):
    return art.write_png(img, rel)


def write_json(data, rel, root=ASSETS):
    path = os.path.join(root, rel)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write("\n")


def block_id(name):
    return f"{NS}:block/{name}"


def item_def(model):
    return {"model": {"type": "minecraft:model", "model": model}}


def self_drop(name):
    return {"type": "minecraft:block", "pools": [{"condition": {"type": "minecraft:survives_explosion"},
            "entries": [{"type": "minecraft:item", "name": f"{NS}:{name}"}], "rolls": 1}], "random_sequence": f"{NS}:blocks/{name}"}


def slab_drop(name):
    return {"type": "minecraft:block", "pools": [{"entries": [{"type": "minecraft:item", "modifier": [
        {"type": "minecraft:set_count", "condition": {"type": "minecraft:match_block", "blocks": f"{NS}:{name}", "state": {"type": "double"}},
         "count": 2}, {"type": "minecraft:explosion_decay"}], "name": f"{NS}:{name}"}], "rolls": 1}], "random_sequence": f"{NS}:blocks/{name}"}


def silk_drop(name):
    return {"type": "minecraft:block", "pools": [{"condition": "minecraft:tool/can_silk_touch",
            "entries": [{"type": "minecraft:item", "name": f"{NS}:{name}"}], "rolls": 1}], "random_sequence": f"{NS}:blocks/{name}"}


def shaped(result, pattern, key, count=1, group=None):
    data = {"type": "minecraft:crafting_shaped", "category": "building", "key": key, "pattern": pattern,
            "result": {"count": count, "id": result if ":" in result else f"{NS}:{result}"}}
    if group:
        data["group"] = group
    return data


def shapeless(result, ingredients, count=1):
    return {"type": "minecraft:crafting_shapeless", "category": "building", "ingredients": ingredients,
            "result": {"count": count, "id": result if ":" in result else f"{NS}:{result}"}}


def cutting(ingredient, result, count=1):
    return {"type": "minecraft:stonecutting", "ingredient": f"{NS}:{ingredient}", "result": {"count": count, "id": f"{NS}:{result}"}}


def smelting(ingredient, result):
    return {"type": "minecraft:smelting", "category": "blocks", "cookingtime": 200, "experience": 0.1,
            "ingredient": f"{NS}:{ingredient}", "result": {"id": f"{NS}:{result}"}}


def templated(table, name):
    return json.loads(json.dumps(STATES[table]).replace("@ID@", block_id(name)))


def merge_tag(rel, values):
    path = os.path.join(DATA, rel)
    data = json.load(open(path, encoding="utf-8")) if os.path.exists(path) else {"values": []}
    for v in values:
        if v not in data["values"]:
            data["values"].append(v)
    write_json(data, rel, DATA)


def merge_lang(entries):
    for lang, idx in (("pt_br", 0), ("en_us", 1)):
        path = os.path.join(ASSETS, "lang", f"{lang}.json")
        data = json.load(open(path, encoding="utf-8"))
        for key, pair in entries.items():
            data[key] = pair[idx]
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
            f.write("\n")


def java_const(name):
    return name.upper()


def main():
    textures, java_blocks, lang = {}, [], {}
    pick, stairs_tag, slabs_tag, walls_tag, impermeable = [], [], [], [], []
    building_items, glass_items = [], []

    def add_lang(name, pt, en, tip=None):
        lang[f"block.{NS}.{name}"] = (pt, en)
        tip = tip or TOOLTIPS.get(name) or ("Bloco de construção aquático.", "Aquatic building block.")
        lang[f"tooltip.{NS}.{name}"] = tip

    for name, pt, en, painter, kind in BASE_BLOCKS:
        # ---- texturas e modelo da base
        if kind == "pillar":
            textures[f"block/{name}_side"] = pillar_side()
            textures[f"block/{name}_top"] = pillar_top()
            write_json({"parent": "minecraft:block/cube_column", "textures": {"end": block_id(f"{name}_top"), "side": block_id(f"{name}_side")}},
                       f"models/block/{name}.json")
            write_json({"parent": "minecraft:block/cube_column_horizontal",
                        "textures": {"end": block_id(f"{name}_top"), "side": block_id(f"{name}_side")}}, f"models/block/{name}_horizontal.json")
            write_json({"variants": {"axis=x": {"model": block_id(f"{name}_horizontal"), "x": 90, "y": 90}, "axis=y": {"model": block_id(name)},
                                     "axis=z": {"model": block_id(f"{name}_horizontal"), "x": 90}}}, f"blockstates/{name}.json")
            java_blocks.append((name, "RotatedPillarBlock::new", "Blocks.QUARTZ_PILLAR", "building", "MapColor.WARPED_WART_BLOCK"))
        elif kind == "jelly":
            textures[f"block/{name}"] = jellyfish_frames()
            with open(os.path.join(ASSETS, "textures", "block", f"{name}.png.mcmeta"), "w", encoding="utf-8", newline="\n") as f:
                json.dump({"animation": {"frametime": 8, "interpolate": True}}, f)
                f.write("\n")
            write_json({"parent": "minecraft:block/cube_all", "textures": {"all": block_id(name)}}, f"models/block/{name}.json")
            write_json({"variants": {"": {"model": block_id(name)}}}, f"blockstates/{name}.json")
            java_blocks.append((name, "Block::new", "Blocks.SEA_LANTERN", "building", None))
        elif kind == "lamp":
            textures[f"block/{name}"] = lamp_tex()
            for hanging in (False, True):
                m = Model({"lamp": name}, "lamp")
                dy = 1 if hanging else 0
                m.box((5, dy, 5), (11, dy + 1, 11), "lamp", uv={f: [0, 0, 6, 1] for f in ("north", "south", "east", "west")})
                m.box((6, dy + 1, 6), (10, dy + 6, 10), "lamp", uv={f: [0, 8, 4, 13] for f in ("north", "south", "east", "west")},
                      light=15)
                for x, z in ((5.4, 5.4), (9.8, 5.4), (5.4, 9.8), (9.8, 9.8)):
                    m.box((x, dy + 1, z), (x + 0.8, dy + 6, z + 0.8), "lamp", uv={f: [2, 2, 3, 7] for f in ("north", "south", "east", "west")})
                m.box((5, dy + 6, 5), (11, dy + 7, 11), "lamp")
                m.box((7, dy + 7, 7), (9, dy + 8.5, 9), "lamp")
                if hanging:
                    m.box((7.5, dy + 8.5, 8), (8.5, 16, 8), "lamp", faces=("north", "south"),
                          uv={"north": [11, 8, 12, 15], "south": [11, 8, 12, 15]}, rot=((8, 12, 8), ("y", 45)))
                    m.box((7.5, dy + 8.5, 8), (8.5, 16, 8), "lamp", faces=("north", "south"),
                          uv={"north": [11, 8, 12, 15], "south": [11, 8, 12, 15]}, rot=((8, 12, 8), ("y", -45)))
                write_json(m.json(), f"models/block/{name}{'_hanging' if hanging else ''}.json")
            write_json({"variants": {f"hanging={h},waterlogged={w}": {"model": block_id(name + ("_hanging" if h == "true" else ""))}
                                     for h in ("false", "true") for w in ("false", "true")}}, f"blockstates/{name}.json")
            java_blocks.append((name, "LanternBlock::new", "Blocks.LANTERN", "building", None))
            lang[f"block.{NS}.{name}"] = (pt, en)
            lang[f"tooltip.{NS}.{name}"] = TOOLTIPS[name]
            pick.append(f"{NS}:{name}")
            write_json(self_drop(name), f"{NS}/loot_table/blocks/{name}.json", DATA)
            write_json(item_def(block_id(name)), f"items/{name}.json")
            building_items.append(name)
            continue
        else:
            textures[f"block/{name}"] = painter()
            tex = block_id(name)
            all_tex = {"force_translucent": True, "sprite": tex} if kind == "glass" else tex
            write_json({"parent": "minecraft:block/cube_all", "textures": {"all": all_tex}}, f"models/block/{name}.json")
            write_json({"variants": {"": {"model": block_id(name)}}}, f"blockstates/{name}.json")
            factory = {"stone": "Block::new", "glass": "p -> new TransparentBlock(p) {}", "grate": "p -> new WaterloggedTransparentBlock(p) {}"}[kind]
            source = {"stone": "Blocks.STONE_BRICKS", "glass": "Blocks.GLASS", "grate": "Blocks.IRON_BARS"}[kind]
            java_blocks.append((name, factory, source, "building", None))
            if kind == "grate":
                write_json({"variants": {"waterlogged=false": {"model": block_id(name)}, "waterlogged=true": {"model": block_id(name)}}},
                           f"blockstates/{name}.json")
        add_lang(name, pt, en)
        write_json(item_def(block_id(name)), f"items/{name}.json")
        write_json(silk_drop(name) if kind == "glass" else self_drop(name), f"{NS}/loot_table/blocks/{name}.json", DATA)
        building_items.append(name)
        if kind in ("stone", "pillar", "grate"):
            pick.append(f"{NS}:{name}")
        if kind == "glass":
            impermeable.append(f"{NS}:{name}")
            # vidraça
            pane = f"{name}_pane"
            textures[f"block/{pane}_top"] = pane_edge((160, 230, 222, 220))
            for part in ("post", "side", "side_alt", "noside", "noside_alt"):
                textures_json = {"pane": {"force_translucent": True, "sprite": block_id(name)}}
                if part in ("post", "side", "side_alt"):
                    textures_json["edge"] = {"force_translucent": True, "sprite": block_id(f"{pane}_top")}
                write_json({"parent": f"minecraft:block/template_glass_pane_{part}", "textures": textures_json}, f"models/block/{pane}_{part}.json")
            write_json(templated("pane_clear", pane), f"blockstates/{pane}.json")
            write_json({"parent": "minecraft:item/generated", "textures": {"layer0": block_id(name)}}, f"models/item/{pane}.json")
            write_json(item_def(f"{NS}:item/{pane}"), f"items/{pane}.json")
            write_json(silk_drop(pane), f"{NS}/loot_table/blocks/{pane}.json", DATA)
            add_lang(pane, "Vidraça do Mar", "Sea Glass Pane")
            java_blocks.append((pane, "p -> new IronBarsBlock(p) {}", "Blocks.GLASS_PANE", "building", None))
            building_items.append(pane)

        # ---- escadas, lajes e muros
        for shape in SHAPED.get(name, ()):
            sid = f"{name}_{'stairs' if shape == 'stairs' else shape}"
            sid = sid.replace("_bricks_", "_brick_").replace("_tiles_", "_tile_").replace("_shingles_", "_shingle_")
            spt, sen = shape_name(pt, en, shape)
            if shape == "stairs":
                for suffix, parent in (("", "stairs"), ("_inner", "inner_stairs"), ("_outer", "outer_stairs")):
                    write_json({"parent": f"minecraft:block/{parent}", "textures": {"bottom": block_id(name), "side": block_id(name),
                                "top": block_id(name)}}, f"models/block/{sid}{suffix}.json")
                write_json(templated("stairs", sid), f"blockstates/{sid}.json")
                java_blocks.append((sid, f"p -> new StairBlock({java_const(name)}.defaultBlockState(), p) {{}}", java_const(name), "building", None))
                stairs_tag.append(f"{NS}:{sid}")
            elif shape == "slab":
                for suffix, parent in (("", "slab"), ("_top", "slab_top")):
                    write_json({"parent": f"minecraft:block/{parent}", "textures": {"bottom": block_id(name), "side": block_id(name),
                                "top": block_id(name)}}, f"models/block/{sid}{suffix}.json")
                write_json({"variants": {"type=bottom": {"model": block_id(sid)}, "type=double": {"model": block_id(name)},
                                         "type=top": {"model": block_id(f"{sid}_top")}}}, f"blockstates/{sid}.json")
                java_blocks.append((sid, "SlabBlock::new", java_const(name), "building", None))
                slabs_tag.append(f"{NS}:{sid}")
            else:
                for suffix, parent in (("_post", "template_wall_post"), ("_side", "template_wall_side"),
                                       ("_side_tall", "template_wall_side_tall"), ("_inventory", "wall_inventory")):
                    write_json({"parent": f"minecraft:block/{parent}", "textures": {"wall": block_id(name)}}, f"models/block/{sid}{suffix}.json")
                write_json(templated("wall", sid), f"blockstates/{sid}.json")
                java_blocks.append((sid, "WallBlock::new", java_const(name), "building", None))
                walls_tag.append(f"{NS}:{sid}")
            write_json(item_def(block_id(sid + ("_inventory" if shape == "wall" else ""))), f"items/{sid}.json")
            write_json(slab_drop(sid) if shape == "slab" else self_drop(sid), f"{NS}/loot_table/blocks/{sid}.json", DATA)
            add_lang(sid, spt, sen, (f"{SHAPE_PT[shape].split(' ')[0]} de {pt}.", f"{sen}."))
            pick.append(f"{NS}:{sid}")
            building_items.append(sid)

    # ---------------------------------------------------------------- vidros rachados
    glasses = [("cracked_glass", "Blocks.GLASS", "p -> new TransparentBlock(p) {}", glass_tex((0, 0, 0), "clear"), "Vidro Rachado",
                "Cracked Glass", True),
               ("cracked_tinted_glass", "Blocks.TINTED_GLASS", "TintedGlassBlock::new", glass_tex((0, 0, 0), "tinted"), "Vidro Fosco Rachado",
                "Cracked Tinted Glass", False)]
    for dye, rgb, pt_m, pt_f, en in DYES:
        glasses.append((f"cracked_{dye}_stained_glass", f"Blocks.STAINED_GLASS.pick(DyeColor.{dye.upper()})",
                        f"p -> new StainedGlassBlock(DyeColor.{dye.upper()}, p)", glass_tex(rgb, "stained"), f"Vidro {pt_m} Rachado",
                        f"Cracked {en} Stained Glass", True))
    cracked_map = []
    for name, source, factory, img, pt, en, translucent in glasses:
        textures[f"block/{name}"] = img
        tex = {"force_translucent": True, "sprite": block_id(name)} if translucent else block_id(name)
        write_json({"parent": "minecraft:block/cube_all", "textures": {"all": tex}}, f"models/block/{name}.json")
        write_json({"variants": {"": {"model": block_id(name)}}}, f"blockstates/{name}.json")
        write_json(item_def(block_id(name)), f"items/{name}.json")
        write_json(silk_drop(name), f"{NS}/loot_table/blocks/{name}.json", DATA)
        add_lang(name, pt, en, ("Vidro quase quebrando; a onda de choque do Kraken racha os vidros da arena.",
                                "Glass about to give way; the Kraken's shockwave cracks the glass around the arena."))
        java_blocks.append((name, factory, source, "glass", None))
        cracked_map.append((source, java_const(name)))
        impermeable.append(f"{NS}:{name}")
        glass_items.append(name)
        if name != "cracked_tinted_glass":  # vidro fumê não tem vidraça no jogo
            pane = f"{name}_pane"
            stained = name.startswith("cracked_") and name.endswith("_stained_glass")
            edge_rgb = (200, 226, 232, 255) if not stained else tuple(min(255, v + 30) for v in DYES[[d[0] for d in DYES].index(
                name[len("cracked_"):-len("_stained_glass")])][1]) + (200,)
            textures[f"block/{pane}_top"] = pane_edge(edge_rgb)
            for part in ("post", "side", "side_alt", "noside", "noside_alt"):
                textures_json = {"pane": {"force_translucent": True, "sprite": block_id(name)}}
                if part in ("post", "side", "side_alt"):
                    textures_json["edge"] = {"force_translucent": True, "sprite": block_id(f"{pane}_top")}
                write_json({"parent": f"minecraft:block/template_glass_pane_{part}", "textures": textures_json}, f"models/block/{pane}_{part}.json")
            write_json(templated("pane" if stained else "pane_clear", pane), f"blockstates/{pane}.json")
            write_json({"parent": "minecraft:item/generated", "textures": {"layer0": block_id(name)}}, f"models/item/{pane}.json")
            write_json(item_def(f"{NS}:item/{pane}"), f"items/{pane}.json")
            write_json(silk_drop(pane), f"{NS}/loot_table/blocks/{pane}.json", DATA)
            if stained:
                dye = name[len("cracked_"):-len("_stained_glass")]
                d = DYES[[x[0] for x in DYES].index(dye)]
                ppt, pen = f"Vidraça {d[3]} Rachada", f"Cracked {d[4]} Stained Glass Pane"
                pfactory, psource = f"p -> new StainedGlassPaneBlock(DyeColor.{dye.upper()}, p)", f"Blocks.STAINED_GLASS_PANE.pick(DyeColor.{dye.upper()})"
            else:
                ppt, pen, pfactory, psource = "Vidraça Rachada", "Cracked Glass Pane", "p -> new IronBarsBlock(p) {}", "Blocks.GLASS_PANE"
            add_lang(pane, ppt, pen, ("Vidraça quase quebrando.", "A glass pane about to give way."))
            java_blocks.append((pane, pfactory, psource, "glass", None))
            cracked_map.append((psource, java_const(pane)))
            glass_items.append(pane)
            write_json(shaped(pane, ["###", "###"], {"#": f"{NS}:{name}"}, 16), f"{NS}/recipe/{pane}.json", DATA)
        vanilla = name[len("cracked_"):]
        write_json(shapeless(name, [f"minecraft:{vanilla}", "minecraft:flint"]), f"{NS}/recipe/{name}.json", DATA)

    # ---------------------------------------------------------------- receitas das famílias
    recipes = {
        "tidestone": shaped("tidestone", ["###", "#P#", "###"], {"#": "#minecraft:stone_crafting_materials", "P": "minecraft:prismarine_shard"}, 8),
        "polished_tidestone": shaped("polished_tidestone", ["##", "##"], {"#": f"{NS}:tidestone"}, 4),
        "tidestone_bricks": shaped("tidestone_bricks", ["##", "##"], {"#": f"{NS}:polished_tidestone"}, 4),
        "chiseled_tidestone": shaped("chiseled_tidestone", ["#", "#"], {"#": f"{NS}:polished_tidestone_slab"}),
        "tidestone_pillar": shaped("tidestone_pillar", ["#", "#"], {"#": f"{NS}:polished_tidestone"}, 2),
        "petrified_coral_bricks": shaped("petrified_coral_bricks", ["##", "##"], {"#": f"#{NS}:dead_coral_blocks"}, 4),
        "petrified_coral_tiles": shaped("petrified_coral_tiles", ["##", "##"], {"#": f"{NS}:petrified_coral_bricks"}, 4),
        "shell_shingles": shaped("shell_shingles", ["BBB", "BNB", "BBB"], {"B": "minecraft:brick", "N": "minecraft:nautilus_shell"}, 8),
        "abyssal_tiles": shaped("abyssal_tiles", ["###", "#I#", "###"], {"#": "minecraft:deepslate_tiles", "I": f"{NS}:abyssal_ingot"}, 8),
        "chiseled_abyssal_tiles": shaped("chiseled_abyssal_tiles", ["#", "#"], {"#": f"{NS}:abyssal_tile_slab"}),
        "sea_glass": shaped("sea_glass", ["###", "#P#", "###"], {"#": "minecraft:glass", "P": "minecraft:prismarine_crystals"}, 8),
        "sea_glass_pane": shaped("sea_glass_pane", ["###", "###"], {"#": f"{NS}:sea_glass"}, 16),
        "jellyfish_lantern": shaped("jellyfish_lantern", ["CGC", "GSG", "CGC"], {"C": "minecraft:prismarine_crystals",
                                    "G": "minecraft:glow_ink_sac", "S": f"{NS}:sea_glass"}, 2),
        "pearl_lamp": shapeless("pearl_lamp", ["minecraft:lantern", f"{NS}:abyssal_pearl"], 2),
        "naval_grate": shapeless("naval_grate", ["minecraft:iron_bars", "minecraft:iron_bars", "minecraft:iron_bars",
                                                 "minecraft:iron_bars", "minecraft:copper_ingot"], 4),
    }
    for name, data in recipes.items():
        write_json(data, f"{NS}/recipe/{name}.json", DATA)
    write_json(smelting("tidestone_bricks", "cracked_tidestone_bricks"), f"{NS}/recipe/cracked_tidestone_bricks.json", DATA)
    for name, shapes in SHAPED.items():
        for shape in shapes:
            sid = f"{name}_{shape}".replace("_bricks_", "_brick_").replace("_tiles_", "_tile_").replace("_shingles_", "_shingle_")
            pattern = {"stairs": (["#  ", "## ", "###"], 4), "slab": (["###"], 6), "wall": (["###", "###"], 6)}[shape]
            write_json(shaped(sid, pattern[0], {"#": f"{NS}:{name}"}, pattern[1]), f"{NS}/recipe/{sid}.json", DATA)
            write_json(cutting(name, sid, 2 if shape == "slab" else 1), f"{NS}/recipe/{sid}_from_{name}_stonecutting.json", DATA)
    for base, outs in CUTS.items():
        for out in outs:
            write_json(cutting(base, out), f"{NS}/recipe/{out}_from_{base}_stonecutting.json", DATA)
            for shape in SHAPED.get(out, ()):
                sid = f"{out}_{shape}".replace("_bricks_", "_brick_").replace("_tiles_", "_tile_").replace("_shingles_", "_shingle_")
                write_json(cutting(base, sid, 2 if shape == "slab" else 1), f"{NS}/recipe/{sid}_from_{base}_stonecutting.json", DATA)

    # ---------------------------------------------------------------- tags
    merge_tag(f"{NS}/tags/item/dead_coral_blocks.json", [f"minecraft:dead_{c}_coral_block" for c in ("tube", "brain", "bubble", "fire", "horn")])
    merge_tag("minecraft/tags/block/mineable/pickaxe.json", pick)
    merge_tag("minecraft/tags/block/stairs.json", stairs_tag)
    merge_tag("minecraft/tags/item/stairs.json", stairs_tag)
    merge_tag("minecraft/tags/block/slabs.json", slabs_tag)
    merge_tag("minecraft/tags/item/slabs.json", slabs_tag)
    merge_tag("minecraft/tags/block/walls.json", walls_tag)
    merge_tag("minecraft/tags/item/walls.json", walls_tag)
    merge_tag("minecraft/tags/block/impermeable.json", impermeable)

    for rel, img in textures.items():
        write_png(img, rel)
    merge_lang(lang)
    write_java(java_blocks, cracked_map)

    if "--preview" in sys.argv:
        scale, pad, cols = 5, 4, 12
        tiles = [img.crop((0, 0, 16, 16)) for img in textures.values()]
        rows = (len(tiles) + cols - 1) // cols
        sheet = Image.new("RGBA", (cols * (16 * scale + pad), rows * (16 * scale + pad)), (70, 110, 70, 255))
        for i, img in enumerate(tiles):
            sheet.alpha_composite(img.resize((16 * scale, 16 * scale), Image.NEAREST),
                                  ((i % cols) * (16 * scale + pad), (i // cols) * (16 * scale + pad)))
        sheet.save(os.path.join(REPO, "build", "phase4_building_preview.png"))
    print(f"fase 4 (4E): {len(java_blocks)} blocos, {len(textures)} texturas")


def write_java(blocks, cracked_map):
    lines = []
    for name, factory, source, group, _ in blocks:
        props = f"BlockBehaviour.Properties.ofFullCopy({source})"
        if name == "jellyfish_lantern":
            props += ".lightLevel(state -> 15).mapColor(MapColor.COLOR_PURPLE)"
        elif name == "pearl_lamp":
            props += ".lightLevel(state -> 15)"
        elif name.startswith("abyssal_tile") or name == "chiseled_abyssal_tiles":
            props = "BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)"
        elif name.startswith("shell_shingle"):
            props = "BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)"
        elif name.startswith("petrified_coral"):
            props = "BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK)"
        elif "tidestone" in name and source in ("Blocks.STONE_BRICKS", "Blocks.QUARTZ_PILLAR"):
            props = "BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.WARPED_WART_BLOCK)"
        elif not source.startswith("Blocks."):  # escada/laje/muro copiam a própria base
            props = f"BlockBehaviour.Properties.ofFullCopy({source})"
        if name == "tidestone_pillar":
            props = "BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.WARPED_WART_BLOCK)"
        lines.append(f"\tpublic static final Block {java_const(name)} = add({'GLASS' if group == 'glass' else 'BUILDING'}, \"{name}\", {factory},\n"
                     f"\t\t{props});")
    cracked = "\n".join(f"\t\tCRACKED.put({src}, {dst});" for src, dst in cracked_map)
    body = "\n".join(lines)
    text = f"""package com.aquamarine.block;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Blocos de construção aquáticos e vidros rachados da Fase 4 (docs/10-fase4.md, §6 e §7).
 * GERADO por tools/gen_phase4_building.py: não edite à mão (mude a lista no gerador e rode de novo).
 */
public final class ModBuildingBlocks {{
	private static final List<Block> BUILDING = new ArrayList<>();
	private static final List<Block> GLASS = new ArrayList<>();
	private static final Map<Block, Block> CRACKED = new HashMap<>();

{body}

	static {{
{cracked}
	}}

	private static Block add(List<Block> group, String name, Function<BlockBehaviour.Properties, Block> factory,
		BlockBehaviour.Properties properties) {{
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Aquamarine.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
		group.add(block);
		return block;
	}}

	/** A versão rachada de um vidro ou vidraça do jogo, ou {{@code null}}. */
	public static Block cracked(Block glass) {{
		return CRACKED.get(glass);
	}}

	/** Blocos de construção, na ordem da aba criativa. */
	public static List<Block> building() {{
		return Collections.unmodifiableList(BUILDING);
	}}

	/** Vidros e vidraças rachados, na ordem da aba criativa. */
	public static List<Block> glass() {{
		return Collections.unmodifiableList(GLASS);
	}}

	public static void register() {{
		// Os campos estáticos registram os blocos; aqui entram os itens, depois dos do ModItems.
		BUILDING.forEach(block -> ModItems.registerBlock(block, new Item.Properties()));
		GLASS.forEach(block -> ModItems.registerBlock(block, new Item.Properties()));
	}}

	private ModBuildingBlocks() {{}}
}}
"""
    with open(JAVA, "w", encoding="utf-8", newline="\n") as f:
        f.write(text)


if __name__ == "__main__":
    main()
