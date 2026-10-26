"""Aurium da Fase 6, faixa 6D1 (docs/11-fase6.md): o metal entre o Abissal e o Neutrônio.
Cor própria, clara e leitosa (pérola com reflexos de opala rosa e ciano), para não confundir com o dourado Abissal nem
com o ciano do Neutrônio. Os ícones, o bloco, o minério e a armadura usam a silhueta das peças Abissais com o metal
trocado. Grava texturas, modelos, estados, loot, receitas (Forja Abissal), tags, geração no mundo e nomes; o Java
(materiais, itens, bônus) fica em ModMaterials, ModItems, ModBlocks, AuriumEffects e AuriumOre.
    C:\\Python314\\python tools/gen_phase6_aurium.py [--preview]
"""
import json
import os
import random
import sys

from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import gen_phase4_art as art  # noqa: E402
import gen_phase4_building as bld  # noqa: E402
from gen_phase4_art import Tex, outline  # noqa: E402

NS, ASSETS, DATA = art.NS, art.ASSETS, bld.DATA
TEX = os.path.join(ASSETS, "textures")
# Ouro Abissal → pérola; verde-água → opala rosa; gema roxa → opala ciano.
RECOLOR = {
    (58, 36, 14): (52, 46, 68), (110, 74, 26): (120, 112, 140), (154, 108, 38): (162, 156, 182),
    (199, 150, 52): (208, 204, 224), (232, 194, 90): (236, 232, 246), (255, 240, 168): (255, 253, 255),
    (35, 133, 121): (178, 112, 160), (51, 170, 150): (226, 156, 202), (111, 220, 196): (250, 206, 236),
    (12, 52, 54): (70, 44, 72), (168, 136, 255): (140, 226, 236), (117, 82, 200): (80, 170, 190),
}
PEARL = [(52, 46, 68, 255), (120, 112, 140, 255), (162, 156, 182, 255), (208, 204, 224, 255), (236, 232, 246, 255),
         (255, 253, 255, 255)]
OPAL = [(250, 206, 236, 255), (190, 244, 248, 255)]
TOOLS = ("sword", "pickaxe", "axe", "shovel", "hoe")
ARMOR = ("helmet", "chestplate", "leggings", "boots")


def recolor(rel, seed, flip=False):
    """Troca o metal Abissal pelo Aurium e espalha uns reflexos de opala no claro."""
    img = Image.open(os.path.join(TEX, rel)).convert("RGBA")
    if flip:
        img = img.transpose(Image.Transpose.FLIP_LEFT_RIGHT)
    px = img.load()
    rng = random.Random(seed)
    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            new = RECOLOR.get((r, g, b))
            if new:
                if new in ((236, 232, 246), (255, 253, 255)) and rng.random() < 0.18:
                    new = rng.choice(OPAL)[:3]
                px[x, y] = new + (a,)
    return img


def raw_aurium():
    """Pepita bruta: pedra leitosa irregular, com veios de opala."""
    t = Tex(661)
    for cx, cy, r in ((7.5, 8.5, 5.2), (10, 6, 3.4), (5, 6, 3)):
        t.disc(cx, cy, r, PEARL[2])
    for y in range(16):
        for x in range(16):
            if t.get(x, y)[3]:
                t.put(x, y, t.rng.choice([PEARL[1], PEARL[2], PEARL[2], PEARL[3]]))
    for x, y in ((6, 6), (9, 8), (7, 11), (11, 5)):
        t.put(x, y, PEARL[5])
        t.put(x + 1, y, t.rng.choice(OPAL))
    return outline(t.img, PEARL[0])


NAMES = {
    "aurium_ore": ("Minério de Aurium", "Aurium Ore",
                   ["Raro, na ardósia sob o leito dos oceanos profundos (y −48 a 0).", "Pede picareta de diamante ou melhor."],
                   ["Rare, in the deepslate beneath deep ocean floors (y −48 to 0).", "Needs a diamond pickaxe or better."]),
    "raw_aurium": ("Aurium Bruto", "Raw Aurium", ["Funda na fornalha para virar lingote."], ["Smelt it into an ingot."]),
    "aurium_ingot": ("Lingote de Aurium", "Aurium Ingot",
                     ["Metal leitoso entre o Abissal e o Neutrônio.", "Entra no Lingote de Neutrônio Instável, na Forja."],
                     ["Milky metal between Abyssal and Neutronium.", "Goes into the Unstable Neutronium Ingot, at the Forge."]),
    "aurium_block": ("Bloco de Aurium", "Aurium Block", ["Nove Lingotes de Aurium. Base de sinalizador."],
                     ["Nine Aurium Ingots. Works as a beacon base."]),
    "aurium_sword": ("Espada de Aurium", "Aurium Sword", ["8 de dano.", "70% de dano no escudo do Kraken."],
                     ["8 damage.", "Deals 70% damage to the Kraken's shield."]),
    "aurium_pickaxe": ("Picareta de Aurium", "Aurium Pickaxe", ["Minera debaixo d'água sem perder velocidade.",
                       "70% de dano no escudo do Kraken."], ["Mines underwater at full speed.", "Deals 70% damage to the Kraken's shield."]),
    "aurium_axe": ("Machado de Aurium", "Aurium Axe", ["Minera debaixo d'água sem perder velocidade.",
                   "70% de dano no escudo do Kraken."], ["Mines underwater at full speed.", "Deals 70% damage to the Kraken's shield."]),
    "aurium_shovel": ("Pá de Aurium", "Aurium Shovel", ["Minera debaixo d'água sem perder velocidade.",
                      "70% de dano no escudo do Kraken."], ["Mines underwater at full speed.", "Deals 70% damage to the Kraken's shield."]),
    "aurium_hoe": ("Enxada de Aurium", "Aurium Hoe", ["Minera debaixo d'água sem perder velocidade.",
                   "70% de dano no escudo do Kraken."], ["Mines underwater at full speed.", "Deals 70% damage to the Kraken's shield."]),
    "aurium_helmet": ("Capacete de Aurium", "Aurium Helmet", ["Defesa 3, resistência 3."], ["Armor 3, toughness 3."]),
    "aurium_chestplate": ("Peitoral de Aurium", "Aurium Chestplate", ["Defesa 8, resistência 3."], ["Armor 8, toughness 3."]),
    "aurium_leggings": ("Calças de Aurium", "Aurium Leggings", ["Defesa 6, resistência 3."], ["Armor 6, toughness 3."]),
    "aurium_boots": ("Botas de Aurium", "Aurium Boots", ["Defesa 3, resistência 3."], ["Armor 3, toughness 3."]),
}
SET_PT = "Conjunto completo (Brilho Perolado): submerso, Poder do Conduíte e nado mais rápido."
SET_EN = "Full set (Pearl Glow): underwater, Conduit Power and faster swimming."


def recipes():
    ingot = f"{NS}:aurium_ingot"
    out = {
        "aurium_ingot_from_smelting": {"type": "minecraft:smelting", "category": "misc", "group": "aurium_ingot",
                                       "ingredient": f"{NS}:raw_aurium", "result": {"id": ingot}, "experience": 1.5, "cookingtime": 200},
        "aurium_ingot_from_blasting": {"type": "minecraft:blasting", "category": "misc", "group": "aurium_ingot",
                                       "ingredient": f"{NS}:raw_aurium", "result": {"id": ingot}, "experience": 1.5, "cookingtime": 100},
        "aurium_ingot_from_ore_smelting": {"type": "minecraft:smelting", "category": "misc", "group": "aurium_ingot",
                                           "ingredient": f"{NS}:aurium_ore", "result": {"id": ingot}, "experience": 1.5, "cookingtime": 200},
        "aurium_block": bld.shaped("aurium_block", ["###", "###", "###"], {"#": ingot}),
        "aurium_ingot_from_block": bld.shapeless("aurium_ingot", [f"{NS}:aurium_block"], 9),
    }
    # Ferramentas e armadura na Forja Abissal, com os mesmos desenhos das Abissais.
    for piece in TOOLS + ARMOR:
        path = os.path.join(DATA, NS, "recipe", f"abyssal_{piece}.json")
        data = json.load(open(path, encoding="utf-8"))
        data["key"] = {k: (ingot if v == f"{NS}:abyssal_ingot" else v) for k, v in data["key"].items()}
        data["result"] = {"id": f"{NS}:aurium_{piece}"}
        out[f"aurium_{piece}"] = data
    return out


def main():
    previews = {}
    for piece in TOOLS + ARMOR:
        previews[f"aurium_{piece}"] = recolor(f"item/abyssal_{piece}.png", 670 + len(piece))
    previews["aurium_ingot"] = recolor("item/abyssal_ingot.png", 690)
    previews["raw_aurium"] = raw_aurium()
    previews["aurium_ore"] = recolor("block/abyssal_ore.png", 692, flip=True)
    previews["aurium_block"] = recolor("block/abyssal_block.png", 693, flip=True)
    for name, img in previews.items():
        kind = "block" if name in ("aurium_ore", "aurium_block") else "item"
        img.save(os.path.join(TEX, kind, f"{name}.png"))
    body = recolor("entity/equipment/humanoid/abyssal.png", 694)
    legs = recolor("entity/equipment/humanoid_leggings/abyssal.png", 695)
    body.save(os.path.join(TEX, "entity/equipment/humanoid/aurium.png"))
    legs.save(os.path.join(TEX, "entity/equipment/humanoid_leggings/aurium.png"))

    w = bld.write_json
    for name in ("aurium_ore", "aurium_block"):
        w({"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/{name}"}}, f"models/block/{name}.json")
        w({"variants": {"": {"model": f"{NS}:block/{name}"}}}, f"blockstates/{name}.json")
        w(bld.item_def(f"{NS}:block/{name}"), f"items/{name}.json")
    w(bld.self_drop("aurium_block"), f"{NS}/loot_table/blocks/aurium_block.json", DATA)
    # Minério: Toque Suave dá o bloco; sem ele, Aurium Bruto com Fortuna.
    w({"type": "minecraft:block", "random_sequence": f"{NS}:blocks/aurium_ore", "pools": [{"rolls": 1, "bonus_rolls": 0, "entries": [
        {"type": "minecraft:alternatives", "children": [
            {"type": "minecraft:item", "name": f"{NS}:aurium_ore", "conditions": [{"condition": "minecraft:match_tool", "predicate": {
                "predicates": {"minecraft:enchantments": [{"enchantments": "minecraft:silk_touch", "levels": {"min": 1}}]}}}]},
            {"type": "minecraft:item", "name": f"{NS}:raw_aurium", "functions": [
                {"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"},
                {"function": "minecraft:explosion_decay"}]}]}]}]},
      f"{NS}/loot_table/blocks/aurium_ore.json", DATA)
    for name in ["raw_aurium", "aurium_ingot"] + [f"aurium_{p}" for p in TOOLS + ARMOR]:
        parent = "minecraft:item/handheld" if any(name.endswith(t) for t in TOOLS) else "minecraft:item/generated"
        w({"parent": parent, "textures": {"layer0": f"{NS}:item/{name}"}}, f"models/item/{name}.json")
        w(bld.item_def(f"{NS}:item/{name}"), f"items/{name}.json")
    w({"layers": {"humanoid": [{"texture": f"{NS}:aurium"}], "humanoid_leggings": [{"texture": f"{NS}:aurium"}]}},
      "equipment/aurium.json")
    for name, data in recipes().items():
        w(data, f"{NS}/recipe/{name}.json", DATA)
    # Veios pequenos na ardósia sob o leito dos oceanos profundos; metade some se encostar na água ou no ar.
    w({"type": "minecraft:ore", "discard_chance_on_air_exposure": 0.5, "size": 4, "targets": [
        {"state": f"{NS}:aurium_ore", "target": {"predicate_type": "minecraft:tag_match", "tag": "minecraft:deepslate_ore_replaceables"}}]},
      f"{NS}/worldgen/feature/ore_aurium.json", DATA)
    w({"feature": f"{NS}:ore_aurium", "placement": [{"type": "minecraft:count", "count": 3}, {"type": "minecraft:in_square"},
       {"type": "minecraft:height_range", "height": {"type": "minecraft:uniform", "max_inclusive": {"absolute": 0},
                                                       "min_inclusive": {"absolute": -48}}}, {"type": "minecraft:biome"}]},
      f"{NS}/worldgen/placed_feature/ore_aurium.json", DATA)

    gear = [f"{NS}:aurium_{p}" for p in TOOLS + ARMOR]
    tools = [f"{NS}:aurium_{p}" for p in TOOLS]
    armor = [f"{NS}:aurium_{p}" for p in ARMOR]
    for rel, values in ((f"{NS}/tags/item/aurium_tool_materials.json", [f"{NS}:aurium_ingot"]),
                        (f"{NS}/tags/item/repairs_aurium_armor.json", [f"{NS}:aurium_ingot"]),
                        (f"{NS}/tags/item/aurium_tools.json", tools),
                        (f"{NS}/tags/item/metal_armor.json", armor),
                        ("minecraft/tags/block/mineable/pickaxe.json", [f"{NS}:aurium_ore", f"{NS}:aurium_block"]),
                        ("minecraft/tags/block/needs_diamond_tool.json", [f"{NS}:aurium_ore", f"{NS}:aurium_block"]),
                        ("minecraft/tags/block/beacon_base_blocks.json", [f"{NS}:aurium_block"]),
                        ("minecraft/tags/item/beacon_payment_items.json", [f"{NS}:aurium_ingot"]),
                        ("minecraft/tags/item/swords.json", [f"{NS}:aurium_sword"]),
                        ("minecraft/tags/item/pickaxes.json", [f"{NS}:aurium_pickaxe"]),
                        ("minecraft/tags/item/axes.json", [f"{NS}:aurium_axe"]),
                        ("minecraft/tags/item/shovels.json", [f"{NS}:aurium_shovel"]),
                        ("minecraft/tags/item/hoes.json", [f"{NS}:aurium_hoe"]),
                        ("minecraft/tags/item/head_armor.json", [f"{NS}:aurium_helmet"]),
                        ("minecraft/tags/item/chest_armor.json", [f"{NS}:aurium_chestplate"]),
                        ("minecraft/tags/item/leg_armor.json", [f"{NS}:aurium_leggings"]),
                        ("minecraft/tags/item/foot_armor.json", [f"{NS}:aurium_boots"]),
                        ("minecraft/tags/item/trimmable_armor.json", armor),
                        ("minecraft/tags/item/enchantable/durability.json", gear)):
        bld.merge_tag(rel, values)

    lang = {}
    for name, (pt, en, tips_pt, tips_en) in NAMES.items():
        kind = "block" if name in ("aurium_ore", "aurium_block") else "item"
        lang[f"{kind}.{NS}.{name}"] = (pt, en)
        if name.endswith(ARMOR):
            tips_pt, tips_en = tips_pt + [SET_PT], tips_en + [SET_EN]
        for i, (tp, te) in enumerate(zip(tips_pt, tips_en)):
            lang[f"tooltip.{NS}.{name}" + ("" if i == 0 else f".{i + 1}")] = (tp, te)
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
        os.makedirs(os.path.join(art.REPO, "build"), exist_ok=True)
        sheet.save(os.path.join(art.REPO, "build", "phase6_aurium_preview.png"))
    print(f"fase 6 (6D1): {len(previews)} texturas do Aurium e a armadura")


if __name__ == "__main__":
    main()
