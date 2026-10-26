"""Neutrônio da Fase 4, faixa 4F (docs/10-fase4.md, §8): texturas no estilo Avaritia e todos os dados.

Os itens usam a silhueta das peças Abissais com o metal trocado por um céu estrelado que corre (8 quadros), contorno
ciano e brilho nas arestas; o minério e o bloco cintilam; a armadura ganha a camada base e o céu que escorre por
cima (textures/entity/neutronium_armor_swirl.png, desenhado pelo NeutroniumArmorLayer). Grava também modelos,
estados, loot, receitas, tags, geração no mundo, equipamento e nomes.

    C:\\Python314\\python tools/gen_phase4_neutronium.py [--preview]
"""
import json
import math
import os
import random
import sys

from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import gen_phase4_art as art  # noqa: E402
import gen_phase4_building as bld  # noqa: E402
from gen_phase4_art import Tex, hexc, ramp, outline, CLEAR  # noqa: E402

NS, ASSETS, DATA = art.NS, art.ASSETS, bld.DATA
TEX = os.path.join(ASSETS, "textures")
FRAMES = 8
SPACE = [(3, 4, 11), (5, 8, 22), (8, 13, 36), (12, 20, 52)]
NEBULA_A, NEBULA_B = (20, 120, 150), (70, 30, 120)
STAR = [(150, 240, 255), (220, 255, 255), (255, 255, 255)]
RIM, RIM_DARK = (60, 230, 255, 255), (10, 70, 96, 255)
HANDLE = [(10, 12, 22, 255), (18, 22, 38, 255), (28, 34, 56, 255), (40, 50, 80, 255), (58, 72, 110, 255), (80, 100, 150, 255)]
GOLD = [hexc(h) for h in ("3a240e", "6e4a1a", "9a6c26", "c79634", "e8c25a", "fff0a8")]
WOOD = [hexc(h) for h in ("1f140a", "3a2614", "54371d", "6e4a27", "8d6234", "a87b46")]


# ================================================================ céu estrelado

class Sky:
    """Campo de estrelas que se repete a cada FRAMES quadros: nebulosa lenta, estrelas que piscam e algumas que correm."""

    def __init__(self, seed, w=16, h=16, density=0.11):
        rng = random.Random(seed)
        self.w, self.h = w, h
        self.stars = [(rng.randrange(w), rng.randrange(h), rng.uniform(0, 2 * math.pi), rng.choice((0, 0, 1, 2)))
                      for _ in range(int(w * h * density))]
        self.drift = [(rng.randrange(w), rng.randrange(h)) for _ in range(max(1, w * h // 90))]
        self.phase = rng.uniform(0, 6.28)

    def color(self, x, y, f):
        t = f / FRAMES
        n = 0.5 + 0.25 * math.sin(x * 0.55 + y * 0.31 + self.phase + t * 2 * math.pi) + 0.25 * math.sin(x * 0.21 - y * 0.47 + 1.3)
        base = SPACE[min(3, int(n * 3.2))]
        tint = NEBULA_A if (x * 3 + y * 5) % 7 < 4 else NEBULA_B
        k = max(0.0, n - 0.55) * 0.55
        return tuple(min(255, int(base[i] + tint[i] * k)) for i in range(3)) + (255,)

    def render(self, f, mask=None):
        img = Image.new("RGBA", (self.w, self.h), CLEAR)
        px = img.load()
        for y in range(self.h):
            for x in range(self.w):
                if mask is None or mask(x, y):
                    px[x, y] = self.color(x, y, f)
        t = f / FRAMES
        for x, y, ph, kind in self.stars:
            b = 0.5 + 0.5 * math.sin(2 * math.pi * t + ph)
            if (mask is None or mask(x, y)) and b > 0.35:
                c = STAR[min(2, int(b * 2.9))]
                px[x, y] = c + (255,)
                if kind == 2 and b > 0.85:  # cintilar em cruz
                    for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                        nx, ny = x + dx, y + dy
                        if 0 <= nx < self.w and 0 <= ny < self.h and (mask is None or mask(nx, ny)):
                            px[nx, ny] = STAR[0] + (255,)
        for x0, y0 in self.drift:  # estrelas que correm na diagonal e voltam no mesmo ponto
            x, y = (x0 + f * 2) % self.w, (y0 + f) % self.h
            if mask is None or mask(x, y):
                px[x, y] = STAR[1] + (255,)
        return img


def strip(frames):
    """Quadros empilhados na vertical, como o jogo lê texturas animadas."""
    w, h = frames[0].size
    out = Image.new("RGBA", (w, h * len(frames)), CLEAR)
    for i, f in enumerate(frames):
        out.paste(f, (0, h * i))
    return out


def write_anim(img_frames, rel, frametime=3):
    path = os.path.join(TEX, rel + ".png")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    strip(img_frames).save(path)
    with open(path + ".mcmeta", "w", encoding="utf-8", newline="\n") as f:
        json.dump({"animation": {"frametime": frametime, "interpolate": False}}, f)
        f.write("\n")
    return img_frames[0]


# ================================================================ itens a partir das peças Abissais

def cosmic_icon(src_rel, seed):
    """Troca o metal Abissal pelo céu, o cabo por metal escuro e o contorno por aro ciano."""
    src = Image.open(os.path.join(TEX, src_rel + ".png")).convert("RGBA")
    spx = src.load()
    w, h = src.size

    def kind(x, y):
        c = spx[x, y]
        if c[3] == 0:
            return None
        if c == GOLD[0]:
            return "rim"
        if c in GOLD:
            return ("metal", GOLD.index(c))
        if c in WOOD:
            return ("handle", WOOD.index(c))
        return "gem"

    kinds = {(x, y): kind(x, y) for y in range(h) for x in range(w)}
    sky = Sky(seed, w, h, density=0.16)
    frames = []
    for f in range(FRAMES):
        cosmic = sky.render(f)
        cp = cosmic.load()
        img = Image.new("RGBA", (w, h), CLEAR)
        px = img.load()
        for (x, y), k in kinds.items():
            if k is None:
                continue
            if k == "rim":
                # aro: ciano onde encosta no fundo, escuro por dentro; pulsa devagar
                edge = any(kinds.get((x + dx, y + dy)) is None for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
                glow = 0.75 + 0.25 * math.sin(2 * math.pi * f / FRAMES + (x + y) * 0.4)
                px[x, y] = tuple(int(v * glow) for v in RIM[:3]) + (255,) if edge else RIM_DARK
            elif k == "gem":
                px[x, y] = (200, 255, 255, 255)
            elif k[0] == "handle":
                px[x, y] = HANDLE[min(5, k[1])]
            else:
                level = k[1]
                c = cp[x, y]
                if level >= 5:  # brilho das arestas
                    px[x, y] = (170, 250, 255, 255)
                else:
                    lift = (level - 2) * 10
                    px[x, y] = tuple(max(0, min(255, c[i] + lift)) for i in range(3)) + (255,)
        frames.append(img)
    return frames


def raw_frames():
    rng = random.Random(801)
    blobs = [(7.5, 8.5, 5.2), (5.2, 6.0, 3.0), (10.5, 6.5, 3.2), (9.5, 11.0, 3.0), (5.0, 11.0, 2.6)]

    def inside(x, y):
        return any((x + 0.5 - bx) ** 2 + (y + 0.5 - by) ** 2 <= r * r for bx, by, r in blobs)

    cracks = set()
    for _ in range(4):
        x, y = rng.uniform(4, 11), rng.uniform(4, 12)
        ang = rng.uniform(0, 6.28)
        for _ in range(6):
            cracks.add((int(x), int(y)))
            ang += rng.uniform(-0.6, 0.6)
            x += math.cos(ang)
            y += math.sin(ang)
    sky = Sky(802)
    crust = [(18, 21, 30, 255), (26, 30, 44, 255), (36, 42, 60, 255), (50, 58, 82, 255)]
    frames = []
    for f in range(FRAMES):
        cp = sky.render(f).load()
        t = Tex(803)
        for y in range(16):
            for x in range(16):
                if not inside(x, y):
                    continue
                if (x, y) in cracks:
                    t.put(x, y, cp[x, y] if (x + y + f) % 3 else (120, 245, 255, 255))
                else:
                    shade = 2 if inside(x - 1, y - 1) else 3
                    t.put(x, y, crust[shade - (1 if not inside(x + 1, y + 1) else 0) - (t.rng.random() < 0.2)])
        frames.append(outline(t.img, (6, 8, 14, 255)))
    return frames


def dust_frames():
    rng = random.Random(811)
    grains = [(x, y) for y in range(7, 15) for x in range(16) if abs(x + 0.5 - 8) <= (y - 6) * 0.95 and rng.random() < 0.9]
    frames = []
    for f in range(FRAMES):
        t = Tex(812)
        for x, y in grains:
            t.put(x, y, (16, 30, 54, 255) if (x * 7 + y * 3) % 5 else (30, 60, 96, 255))
        for i, (x, y) in enumerate(grains):
            if (i * 7 + f * 3) % 11 == 0:
                t.put(x, y, (170, 250, 255, 255))
            elif (i * 5 + f) % 17 == 0:
                t.put(x, y, (255, 255, 255, 255))
        for k in range(3):  # pó subindo
            t.put((4 + k * 4 + f) % 16, 5 - (f + k) % 4, (120, 230, 255, 180))
        frames.append(outline(t.img, (6, 10, 20, 255)))
    return frames


def unstable_frames():
    base = cosmic_icon("item/abyssal_ingot", 821)
    rng = random.Random(822)
    cracks = []
    for _ in range(3):
        x, y = rng.uniform(3, 12), rng.uniform(6, 10)
        pts = []
        ang = rng.uniform(-0.5, 0.5)
        for _ in range(7):
            pts.append((int(x), int(y)))
            ang += rng.uniform(-0.8, 0.8)
            x += math.cos(ang)
            y += math.sin(ang) * 0.6
        cracks.append(pts)
    out = []
    for f, img in enumerate(base):
        px = img.load()
        for k, pts in enumerate(cracks):
            hot = (f + k * 3) % 4 != 0  # pisca: instável
            for x, y in pts:
                if 0 <= x < 16 and 0 <= y < 16 and px[x, y][3]:
                    px[x, y] = (255, 255, 255, 255) if hot and (x + f) % 2 else (190, 120, 255, 255)
        out.append(img)
    return out


def template_frames():
    shape = [(x, y) for y in range(1, 15) for x in range(2, 14) if not (y < 3 and x > 9) and not (y > 12 and x < 5)]
    sky = Sky(831)
    frames = []
    for f in range(FRAMES):
        cp = sky.render(f).load()
        t = Tex(832)
        for x, y in shape:
            t.put(x, y, (24, 30, 46, 255) if (x + y) % 4 else (30, 38, 58, 255))
        for y in range(16):  # gravura: estrela de nêutrons com dois jatos
            for x in range(16):
                d = math.hypot(x + 0.5 - 8, y + 0.5 - 8)
                if d < 2.2 or (abs(x + 0.5 - 8) < 0.6 and 3 <= y <= 13 and (x, y) in set(shape)):
                    t.put(x, y, cp[x, y] if d >= 1.2 else (200, 255, 255, 255))
                elif 3.4 <= d < 4.0 and (x, y) in set(shape):
                    t.put(x, y, (60, 200, 240, 255) if (x + y + f) % 2 else (30, 110, 150, 255))
        frames.append(outline(t.img, (8, 10, 18, 255)))
    return frames


def block_frames():
    sky = Sky(841, density=0.14)
    frames = []
    for f in range(FRAMES):
        img = sky.render(f)
        t = Tex(842)
        t.img, t.px = img, img.load()
        glow = 0.8 + 0.2 * math.sin(2 * math.pi * f / FRAMES)
        rim = tuple(int(v * glow) for v in RIM[:3]) + (255,)
        t.bevel((0, 0, 16, 16), rim, RIM_DARK)
        for x, y in ((1, 1), (14, 1), (1, 14), (14, 14)):
            t.put(x, y, (200, 255, 255, 255))
        frames.append(t.img)
    return frames


def ore_frames():
    rng = random.Random(851)
    base = Tex(852)
    slate = [(22, 22, 28, 255), (32, 32, 40, 255), (44, 44, 54, 255), (58, 58, 70, 255)]
    base.noise((0, 0, 16, 16), slate, [2, 4, 3, 1])
    for y in (4, 10):  # estratos da ardósia
        for x in range(16):
            if rng.random() < 0.7:
                base.put(x, y, slate[0])
    clusters = [(4, 4, 1.7), (11, 7, 2.1), (6, 12, 1.5)]
    sky = Sky(853)
    frames = []
    for f in range(FRAMES):
        cp = sky.render(f).load()
        img = base.img.copy()
        px = img.load()
        for cx, cy, r in clusters:
            for y in range(16):
                for x in range(16):
                    d = abs(x + 0.5 - cx) + abs(y + 0.5 - cy)
                    if d <= r:
                        px[x, y] = cp[x, y]
                    elif d <= r + 0.9:
                        px[x, y] = RIM_DARK if (x + y + f) % 3 else RIM
        frames.append(img)
    return frames


# ================================================================ armadura

def equipment_layer(src_rel):
    """Camada base: as placas Abissais viram céu estrelado parado com arestas ciano; a camada que escorre vem por cima."""
    src = Image.open(os.path.join(TEX, src_rel)).convert("RGBA")
    px = src.load()
    w, h = src.size
    sky = Sky(870, w, h, density=0.07).render(0).load()
    out = src.copy()
    po = out.load()
    for y in range(h):
        for x in range(w):
            c = px[x, y]
            if c[3] == 0:
                continue
            if c in GOLD:
                level = GOLD.index(c)
                if level >= 5:
                    po[x, y] = (150, 245, 255, 255)
                elif level == 0:
                    po[x, y] = RIM_DARK
                else:
                    k = sky[x, y]
                    po[x, y] = tuple(min(255, k[i] + (level - 1) * 8) for i in range(3)) + (255,)
            elif c in WOOD:
                po[x, y] = HANDLE[WOOD.index(c)]
            else:
                lum = sum(c[:3]) / 3
                po[x, y] = (int(lum * 0.3), int(lum * 0.75), int(min(255, lum * 0.95 + 30)), c[3])
    return out


def swirl_texture():
    """Céu que escorre pela armadura (o jogo soma a cor: preto some, as estrelas brilham)."""
    rng = random.Random(861)
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 255))
    px = img.load()
    for y in range(64):
        for x in range(64):
            n = 0.5 + 0.5 * math.sin(x * 0.19 + y * 0.11) * math.sin(x * 0.07 - y * 0.23)
            if n > 0.78:  # nebulosa fraca: o brilho fica nas estrelas e as placas continuam à vista
                k = (n - 0.78) * 4
                px[x, y] = (int(4 + 12 * k), int(16 + 40 * k), int(26 + 56 * k), 255)
    for _ in range(150):
        x, y = rng.randrange(64), rng.randrange(64)
        b = rng.choice((0, 0, 1, 2))
        px[x, y] = (STAR[b][0], STAR[b][1], STAR[b][2], 255)
        if b == 2:
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                px[(x + dx) % 64, (y + dy) % 64] = (60, 160, 200, 255)
    return img


# ================================================================ dados

TOOLS = ("sword", "pickaxe", "axe", "shovel", "hoe")
ARMOR = ("helmet", "chestplate", "leggings", "boots")
NAMES = {
    "neutronium_ore": ("Minério de Neutrônio", "Neutronium Ore",
                       ["Só sob o leito dos oceanos profundos, entre y −60 e −32, e nunca exposto ao ar.",
                        "Pede picareta Abissal ou de netherite."],
                       ["Only beneath deep ocean floors, between y −60 and −32, never exposed to air.",
                        "Needs an Abyssal or netherite pickaxe."]),
    "raw_neutronium": ("Neutrônio Bruto", "Raw Neutronium", ["Matéria de estrela morta.", "Na Forja Abissal, com um Cristal de Maré, vira Poeira."],
                       ["Dead star matter.", "In the Abyssal Forge, with a Tide Crystal, it becomes Dust."]),
    "neutronium_dust": ("Poeira de Neutrônio", "Neutronium Dust",
                        ["4 Poeiras, 4 Lingotes Abissais e 1 Escama Antiga na Forja dão um Lingote Instável."],
                        ["4 Dust, 4 Abyssal Ingots and 1 Ancient Scale in the Forge make an Unstable Ingot."]),
    "unstable_neutronium_ingot": ("Lingote de Neutrônio Instável", "Unstable Neutronium Ingot",
                                  ["Afunda na água. 20 s submerso a 70 blocos abaixo do nível do mar (Pressão 3) o estabilizam."],
                                  ["Sinks in water. 20 s submerged 70 blocks below sea level (Pressure 3) stabilize it."]),
    "neutronium_ingot": ("Lingote de Neutrônio", "Neutronium Ingot",
                         ["Com o Molde de Neutrônio, transforma peças Abissais na ferraria."],
                         ["With the Neutronium Template, upgrades Abyssal gear at the smithing table."]),
    "neutronium_block": ("Bloco de Neutrônio", "Neutronium Block", ["Nove Lingotes de Neutrônio. Base de sinalizador."],
                         ["Nine Neutronium Ingots. Works as a beacon base."]),
    "neutronium_upgrade_smithing_template": ("Molde de Neutrônio", "Neutronium Smithing Template",
                                             ["Cai do Kraken na primeira vitória de cada jogador.", "7 diamantes e 1 Poeira copiam o molde."],
                                             ["Dropped by the Kraken on each player's first victory.", "7 diamonds and 1 Dust copy it."]),
    "neutronium_sword": ("Espada de Neutrônio", "Neutronium Sword", ["9 de dano; +3 debaixo d'água.", "80% de dano no escudo do Kraken."],
                         ["9 damage; +3 underwater.", "Deals 80% damage to the Kraken's shield."]),
    "neutronium_pickaxe": ("Picareta de Neutrônio", "Neutronium Pickaxe", ["Minera debaixo d'água sem perder velocidade.",
                           "80% de dano no escudo do Kraken."], ["Mines underwater at full speed.", "Deals 80% damage to the Kraken's shield."]),
    "neutronium_axe": ("Machado de Neutrônio", "Neutronium Axe", ["Minera debaixo d'água sem perder velocidade.",
                       "80% de dano no escudo do Kraken."], ["Mines underwater at full speed.", "Deals 80% damage to the Kraken's shield."]),
    "neutronium_shovel": ("Pá de Neutrônio", "Neutronium Shovel", ["Minera debaixo d'água sem perder velocidade.",
                          "80% de dano no escudo do Kraken."], ["Mines underwater at full speed.", "Deals 80% damage to the Kraken's shield."]),
    "neutronium_hoe": ("Enxada de Neutrônio", "Neutronium Hoe", ["Minera debaixo d'água sem perder velocidade.",
                       "80% de dano no escudo do Kraken."], ["Mines underwater at full speed.", "Deals 80% damage to the Kraken's shield."]),
    "neutronium_helmet": ("Capacete de Neutrônio", "Neutronium Helmet", ["Debaixo d'água: respiração aquática e visão noturna."],
                          ["Underwater: water breathing and night vision."]),
    "neutronium_chestplate": ("Peitoral de Neutrônio", "Neutronium Chestplate",
                              ["Imune à Pressão da profundidade; a do Kraken sobe 75% mais devagar."],
                              ["Immune to depth Pressure; the Kraken's rises 75% slower."]),
    "neutronium_leggings": ("Calças de Neutrônio", "Neutronium Leggings", ["Debaixo d'água: Graça do Golfinho."],
                            ["Underwater: Dolphin's Grace."]),
    "neutronium_boots": ("Botas de Neutrônio", "Neutronium Boots", ["Passos Profundos III embutido."], ["Built-in Depth Strider III."]),
}
SET_PT = "Conjunto completo (Singularidade Abissal): regeneração e +20% de dano submerso; imune à Tinta e ao Redemoinho."
SET_EN = "Full set (Abyssal Singularity): regeneration and +20% damage underwater; immune to the Ink and the Whirlpool."


def recipes():
    out = {
        "neutronium_dust": {"type": f"{NS}:abyssal_forging", "category": "misc", "key": {"R": f"{NS}:raw_neutronium", "C": f"{NS}:tide_crystal"},
                            "pattern": ["R", "C"], "result": {"count": 2, "id": f"{NS}:neutronium_dust"}},
        "unstable_neutronium_ingot": {"type": f"{NS}:abyssal_forging", "category": "misc",
                                      "key": {"D": f"{NS}:neutronium_dust", "I": f"{NS}:abyssal_ingot", "S": f"{NS}:ancient_scale"},
                                      "pattern": ["DID", "ISI", "DID"], "result": {"id": f"{NS}:unstable_neutronium_ingot"}},
        "neutronium_block": bld.shaped("neutronium_block", ["###", "###", "###"], {"#": f"{NS}:neutronium_ingot"}),
        "neutronium_ingot_from_block": bld.shapeless("neutronium_ingot", [f"{NS}:neutronium_block"], 9),
        "neutronium_upgrade_smithing_template": bld.shaped("neutronium_upgrade_smithing_template", ["#S#", "#D#", "###"],
                                                           {"#": "minecraft:diamond", "S": f"{NS}:neutronium_upgrade_smithing_template",
                                                            "D": f"{NS}:neutronium_dust"}, 2),
    }
    for piece in TOOLS + ARMOR:
        out[f"neutronium_{piece}_smithing"] = {"type": "minecraft:smithing_transform", "addition": f"{NS}:neutronium_ingot",
                                               "base": f"{NS}:abyssal_{piece}", "result": {"id": f"{NS}:neutronium_{piece}"},
                                               "template": f"{NS}:neutronium_upgrade_smithing_template"}
    return out


def main():
    previews = {}
    for piece in TOOLS + ARMOR:
        previews[f"neutronium_{piece}"] = write_anim(cosmic_icon(f"item/abyssal_{piece}", 700 + len(piece)), f"item/neutronium_{piece}")
    previews["neutronium_ingot"] = write_anim(cosmic_icon("item/abyssal_ingot", 790), "item/neutronium_ingot")
    previews["raw_neutronium"] = write_anim(raw_frames(), "item/raw_neutronium")
    previews["neutronium_dust"] = write_anim(dust_frames(), "item/neutronium_dust", 4)
    previews["unstable_neutronium_ingot"] = write_anim(unstable_frames(), "item/unstable_neutronium_ingot", 2)
    previews["neutronium_upgrade_smithing_template"] = write_anim(template_frames(), "item/neutronium_upgrade_smithing_template")
    previews["neutronium_block"] = write_anim(block_frames(), "block/neutronium_block")
    previews["neutronium_ore"] = write_anim(ore_frames(), "block/neutronium_ore", 4)
    body = equipment_layer("entity/equipment/humanoid/abyssal.png")
    legs = equipment_layer("entity/equipment/humanoid_leggings/abyssal.png")
    body.save(os.path.join(TEX, "entity/equipment/humanoid/neutronium.png"))
    legs.save(os.path.join(TEX, "entity/equipment/humanoid_leggings/neutronium.png"))
    swirl_texture().save(os.path.join(TEX, "entity/neutronium_armor_swirl.png"))

    w = bld.write_json
    for name in ("neutronium_ore", "neutronium_block"):
        w({"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/{name}"}}, f"models/block/{name}.json")
        w({"variants": {"": {"model": f"{NS}:block/{name}"}}}, f"blockstates/{name}.json")
        w(bld.item_def(f"{NS}:block/{name}"), f"items/{name}.json")
    w(bld.self_drop("neutronium_block"), f"{NS}/loot_table/blocks/neutronium_block.json", DATA)
    w({"type": "minecraft:block", "pools": [{"condition": {"type": "minecraft:survives_explosion"},
       "entries": [{"type": "minecraft:item", "name": f"{NS}:raw_neutronium"}], "rolls": 1}], "random_sequence": f"{NS}:blocks/neutronium_ore"},
      f"{NS}/loot_table/blocks/neutronium_ore.json", DATA)
    for name in ["raw_neutronium", "neutronium_dust", "unstable_neutronium_ingot", "neutronium_ingot",
                 "neutronium_upgrade_smithing_template"] + [f"neutronium_{p}" for p in TOOLS + ARMOR]:
        parent = "minecraft:item/handheld" if any(name.endswith(t) for t in TOOLS) else "minecraft:item/generated"
        w({"parent": parent, "textures": {"layer0": f"{NS}:item/{name}"}}, f"models/item/{name}.json")
        w(bld.item_def(f"{NS}:item/{name}"), f"items/{name}.json")
    w({"layers": {"humanoid": [{"texture": f"{NS}:neutronium"}], "humanoid_leggings": [{"texture": f"{NS}:neutronium"}]}},
      "equipment/neutronium.json")
    for name, data in recipes().items():
        w(data, f"{NS}/recipe/{name}.json", DATA)
    # Blocos soltos que somem se encostarem no ar, como os detritos ancestrais (formato do 26.3: worldgen/feature).
    w({"type": "minecraft:scattered_ore", "discard_chance_on_air_exposure": 1.0, "size": 2, "targets": [
        {"state": f"{NS}:neutronium_ore", "target": {"predicate_type": "minecraft:tag_match", "tag": "minecraft:deepslate_ore_replaceables"}}]},
      f"{NS}/worldgen/feature/ore_neutronium.json", DATA)
    w({"feature": f"{NS}:ore_neutronium", "placement": [{"type": "minecraft:count", "count": 1}, {"type": "minecraft:in_square"},
       {"type": "minecraft:height_range", "height": {"type": "minecraft:uniform", "max_inclusive": {"absolute": -32},
                                                       "min_inclusive": {"absolute": -60}}}, {"type": "minecraft:biome"}]},
      f"{NS}/worldgen/placed_feature/ore_neutronium.json", DATA)

    gear = [f"{NS}:neutronium_{p}" for p in TOOLS + ARMOR]
    tools = [f"{NS}:neutronium_{p}" for p in TOOLS]
    armor = [f"{NS}:neutronium_{p}" for p in ARMOR]
    every = [f"{NS}:{n}" for n in NAMES]
    for rel, values in ((f"{NS}/tags/item/neutronium_tool_materials.json", [f"{NS}:neutronium_ingot"]),
                        (f"{NS}/tags/item/repairs_neutronium_armor.json", [f"{NS}:neutronium_ingot"]),
                        (f"{NS}/tags/item/neutronium_tools.json", tools), (f"{NS}/tags/item/neutronium_dense.json", every),
                        (f"{NS}/tags/item/metal_armor.json", armor),
                        (f"{NS}/tags/damage_type/neutronium_immune.json", ["#minecraft:is_fire", "#minecraft:is_explosion"]),
                        ("minecraft/tags/block/mineable/pickaxe.json", [f"{NS}:neutronium_ore", f"{NS}:neutronium_block"]),
                        ("minecraft/tags/block/needs_diamond_tool.json", [f"{NS}:neutronium_ore", f"{NS}:neutronium_block"]),
                        ("minecraft/tags/block/incorrect_for_diamond_tool.json", [f"{NS}:neutronium_ore", f"{NS}:neutronium_block"]),
                        ("minecraft/tags/block/beacon_base_blocks.json", [f"{NS}:neutronium_block"]),
                        ("minecraft/tags/item/beacon_payment_items.json", [f"{NS}:neutronium_ingot"]),
                        ("minecraft/tags/item/swords.json", [f"{NS}:neutronium_sword"]),
                        ("minecraft/tags/item/pickaxes.json", [f"{NS}:neutronium_pickaxe"]),
                        ("minecraft/tags/item/axes.json", [f"{NS}:neutronium_axe"]),
                        ("minecraft/tags/item/shovels.json", [f"{NS}:neutronium_shovel"]),
                        ("minecraft/tags/item/hoes.json", [f"{NS}:neutronium_hoe"]),
                        ("minecraft/tags/item/head_armor.json", [f"{NS}:neutronium_helmet"]),
                        ("minecraft/tags/item/chest_armor.json", [f"{NS}:neutronium_chestplate"]),
                        ("minecraft/tags/item/leg_armor.json", [f"{NS}:neutronium_leggings"]),
                        ("minecraft/tags/item/foot_armor.json", [f"{NS}:neutronium_boots"]),
                        ("minecraft/tags/item/trimmable_armor.json", armor),
                        ("minecraft/tags/item/enchantable/durability.json", gear)):
        bld.merge_tag(rel, values)

    lang = {}
    for name, (pt, en, tips_pt, tips_en) in NAMES.items():
        kind = "block" if name in ("neutronium_ore", "neutronium_block") else "item"
        lang[f"{kind}.{NS}.{name}"] = (pt, en)
        if name.endswith(ARMOR):
            tips_pt, tips_en = tips_pt + [SET_PT], tips_en + [SET_EN]
        for i, (tp, te) in enumerate(zip(tips_pt, tips_en)):
            lang[f"tooltip.{NS}.{name}" + ("" if i == 0 else f".{i + 1}")] = (tp, te)
    key = f"item.{NS}.smithing_template.neutronium_upgrade."
    lang[key + "applies_to"] = ("Equipamento Abissal", "Abyssal Equipment")
    lang[key + "ingredients"] = ("Lingote de Neutrônio", "Neutronium Ingot")
    lang[key + "base_slot_description"] = ("Coloque uma arma, ferramenta ou armadura Abissal", "Add Abyssal armor, weapon, or tool")
    lang[key + "additions_slot_description"] = ("Coloque um Lingote de Neutrônio", "Add Neutronium Ingot")
    bld.merge_lang(lang)

    if "--preview" in sys.argv:
        scale = 6
        tiles = list(previews.values()) + [body.crop((0, 0, 64, 32)), legs.crop((0, 0, 64, 32))]
        width = sum(t.width * scale + 6 for t in tiles)
        sheet = Image.new("RGBA", (width, 32 * scale), (60, 70, 80, 255))
        x = 0
        for t in tiles:
            sheet.alpha_composite(t.resize((t.width * scale, t.height * scale), Image.NEAREST), (x, 0))
            x += t.width * scale + 6
        sheet.save(os.path.join(art.REPO, "build", "phase4_neutronium_preview.png"))
    print(f"fase 4 (4F): {len(previews)} texturas animadas, armadura e camada de estrelas")


if __name__ == "__main__":
    main()
