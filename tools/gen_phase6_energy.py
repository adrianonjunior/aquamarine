"""Energia aquática da Fase 6, faixas 6E1 e 6E2 (docs/11-fase6.md): arte e dados.
Turbina de Correnteza, Gerador de Fenda, Boia de Ondas, Cabo Aquático e Coletor de Neutrônio, mais o Pó Estelar de
Neutrônio. Grava texturas, modelos (aceso e apagado), estados, loot, receitas, tags e nomes. O Java fica em
com.aquamarine.energy (ModEnergy).
    C:\\Python314\\python tools/gen_phase6_energy.py [--preview]
"""
import os
import sys

from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import gen_phase4_art as p4  # noqa: E402
import gen_phase4_building as bld  # noqa: E402
from gen_phase4_art import BRASS, DEEP, IRON, Model, Tex, outline  # noqa: E402
from gen_phase6_art import EMBER  # noqa: E402

NS, DATA, REPO = p4.NS, bld.DATA, p4.REPO
COPPER = p4.ramp("3a1e12", "6a3620", "9a5530", "c27546", "e09a66", "f4c08e")


def copper():
    t = Tex(901)
    t.noise((0, 0, 16, 16), [COPPER[2], COPPER[3], COPPER[3], COPPER[4]], [1, 3, 2, 1])
    t.bevel((0, 0, 16, 16), COPPER[5], COPPER[1])
    t.scatter(6, [p4.PATINA[2], p4.PATINA[3]])
    return t.img


def blade():
    t = Tex(902)
    for y in range(16):
        t.hline(0, 15, y, BRASS[2 + (y % 4 == 0) + (y < 4)])
    return t.img


def vent_rock():
    t = Tex(903)
    t.noise((0, 0, 16, 16), [DEEP[1], DEEP[2], DEEP[3]], [2, 3, 1])
    x, y = 3, 0
    for _ in range(16):  # rachadura de magma
        t.put(x, y, EMBER[3])
        x = max(1, min(14, x + t.rng.choice((-1, 0, 1))))
        y += 1
    return t.img


def vent_glow():
    t = Tex(904)
    t.noise((0, 0, 16, 16), [EMBER[3], EMBER[4], EMBER[5]], [2, 3, 1])
    t.disc(8, 8, 4, EMBER[6])
    return t.img


def coil():
    t = Tex(905)
    for y in range(16):
        t.hline(0, 15, y, p4.CYAN_SOFT if y % 3 else (240, 255, 255, 255))
    return t.img


def cable():
    """Bainha de vidro do mar com o fio de cobre no meio."""
    t = Tex(906)
    t.rect((0, 0, 16, 16), (90, 170, 180, 255))
    t.noise((0, 5, 16, 11), [COPPER[3], COPPER[4]], [2, 1])
    t.hline(0, 15, 0, (150, 220, 226, 255))
    t.hline(0, 15, 15, (40, 100, 112, 255))
    return t.img


def crystal():
    t = Tex(907)
    t.noise((0, 0, 16, 16), [(110, 230, 255, 255), (170, 245, 255, 255), (60, 180, 220, 255)], [3, 2, 1])
    t.vline(7, 0, 15, (240, 255, 255, 255))
    return t.img


def stardust():
    """Pó Estelar: um montinho escuro com pontos de estrela."""
    t = Tex(908)
    for cx, cy, r in ((8, 10, 5), (6, 8, 3), (10, 8, 3)):
        t.disc(cx, cy, r, (14, 20, 44, 255))
    for x, y in ((6, 9), (9, 7), (11, 11), (7, 12), (8, 10), (5, 11)):
        t.put(x, y, t.rng.choice([(220, 255, 255, 255), p4.CYAN, (150, 240, 255, 255)]))
    return outline(t.img, (4, 6, 14, 255))


TEXTURES = {"block/energy_copper": copper, "block/energy_blade": blade, "block/energy_vent_rock": vent_rock,
            "block/energy_vent_glow": vent_glow, "block/energy_coil": coil, "block/aquatic_cable": cable,
            "block/energy_crystal": crystal, "item/neutronium_stardust": stardust}


def turbine(on):
    m = Model({"copper": "energy_copper", "blade": "energy_blade", "brass": "abyssal_forge_pipe"}, "copper")
    m.box((1, 0, 1), (15, 2, 15), "copper")
    m.box((1, 14, 1), (15, 16, 15), "copper")
    for x, z in ((1, 1), (13, 1), (1, 13), (13, 13)):
        m.box((x, 2, z), (x + 2, 14, z + 2), "copper")
    m.box((6.5, 6.5, 0.5), (9.5, 9.5, 15.5), "brass")
    for angle in (22.5, -67.5):
        m.box((7.2, 1.5, 7.4), (8.8, 14.5, 8.6), "blade", rot=((8, 8, 8), ("z", angle)))
    return m


def vent(on):
    m = Model({"rock": "energy_vent_rock", "glow": "energy_vent_glow"}, "rock")
    m.box((1, 0, 1), (15, 10, 15), "rock")
    m.box((3, 10, 3), (13, 14, 13), "rock")
    m.box((5, 14, 5), (11, 16, 11), {"*": "rock", "up": "glow"}, light=15 if on else 0)
    for x0, z0, x1, z1 in ((4, 0.9, 6, 1), (10, 15, 12, 15.1)):
        m.box((x0, 2, z0), (x1, 9, z1), "glow", faces=("north", "south"), light=15 if on else 0)
    return m


def buoy(on):
    m = Model({"float": "anchor_buoy_float", "stripes": "anchor_buoy_stripes", "metal": "anchor_buoy_metal", "coil": "energy_coil"}, "float")
    m.box((3, 0, 3), (13, 6, 13), "float")
    m.box((2.8, 2.5, 2.8), (13.2, 4, 13.2), "stripes", faces=("north", "south", "east", "west"))
    m.box((4, 6, 4), (12, 7, 12), "metal")
    m.box((7.5, 7, 7.5), (8.5, 13, 8.5), "metal")
    m.box((6.5, 13, 6.5), (9.5, 16, 9.5), "coil", light=15 if on else 0)
    return m


def collector(on):
    m = Model({"aurium": "aurium_block", "core": "neutronium_block", "crystal": "energy_crystal"}, "aurium")
    m.box((0, 0, 0), (16, 3, 16), "aurium")
    m.box((2, 3, 2), (14, 12, 14), "core", light=8)
    for x, z in ((1, 1), (13, 1), (1, 13), (13, 13)):
        m.box((x, 3, z), (x + 2, 12, z + 2), "aurium")
    m.box((3, 12, 3), (13, 14, 13), "aurium")
    m.box((6, 14, 6), (10, 18, 10), "crystal", light=15 if on else 4)
    return m


GENERATORS = {"current_turbine": turbine, "vent_generator": vent, "wave_buoy": buoy, "neutronium_collector": collector}
NAMES = {
    "current_turbine": ("Turbina de Correnteza", "Current Turbine", "Gera 4 E/t por lado com água corrente (até 24).",
                        "Makes 4 E/t per side touching flowing water (up to 24)."),
    "vent_generator": ("Gerador de Fenda", "Vent Generator", "Gera 32 E/t com magma embaixo e água em cima.",
                       "Makes 32 E/t with magma below and water above."),
    "wave_buoy": ("Boia de Ondas", "Wave Buoy", "Na superfície, 4 E/t; o dobro na chuva e quatro vezes na tempestade.",
                  "At the surface, 4 E/t; double in rain, four times in a storm."),
    "aquatic_cable": ("Cabo Aquático", "Aquatic Cable", "Leva a energia entre geradores e máquinas (256 E/t).",
                      "Carries energy between generators and machines (256 E/t)."),
    "neutronium_collector": ("Coletor de Neutrônio", "Neutronium Collector",
                             "No fundo dos oceanos profundos, gasta 64 E/t e junta 1 Pó Estelar a cada 5 minutos.",
                             "At the bottom of deep oceans, uses 64 E/t and gathers 1 Stardust every 5 minutes."),
}
RECIPES = {
    "current_turbine": (["CPC", "PVP", "CPC"], {"C": "minecraft:copper_ingot", "P": "minecraft:prismarine_shard", "V": f"{NS}:voltaic_coil"}, 1),
    "vent_generator": (["III", "VMV", "III"], {"I": "minecraft:iron_ingot", "V": f"{NS}:voltaic_coil", "M": "minecraft:magma_block"}, 1),
    "wave_buoy": (["IVI", "PPP"], {"I": "minecraft:iron_ingot", "V": f"{NS}:voltaic_coil", "P": "minecraft:prismarine"}, 1),
    "aquatic_cable": (["PCP"], {"P": "minecraft:prismarine_shard", "C": "minecraft:copper_ingot"}, 6),
    "neutronium_collector": (["NAN", "VXV", "NAN"], {"N": f"{NS}:neutronium_dust", "A": f"{NS}:aurium_ingot", "V": f"{NS}:voltaic_coil",
                                                     "X": "minecraft:conduit"}, 1),
}


def main():
    w, bid = bld.write_json, bld.block_id
    images = {rel: fn() for rel, fn in TEXTURES.items()}
    for rel, img in images.items():
        p4.write_png(img, rel)
    for name, builder in GENERATORS.items():
        for on in (False, True):
            w(builder(on).json(), f"models/block/{name}{'_on' if on else ''}.json")
        variants = {}
        for d, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
            for lit in ("false", "true"):
                variants[f"facing={d},lit={lit}"] = {"model": bid(name + ("_on" if lit == "true" else "")), **({"y": y} if y else {})}
        w({"variants": variants}, f"blockstates/{name}.json")
        w(bld.item_def(bid(name)), f"items/{name}.json")
    # Cabo: núcleo e um braço por lado ligado (multipart).
    w({"parent": "minecraft:block/block", "textures": {"c": bid("aquatic_cable"), "particle": bid("aquatic_cable")},
       "elements": [{"from": [5, 5, 5], "to": [11, 11, 11], "faces": {f: {"texture": "#c"} for f in ("north", "south", "east", "west", "up", "down")}}]},
      "models/block/aquatic_cable_core.json")
    w({"parent": "minecraft:block/block", "textures": {"c": bid("aquatic_cable"), "particle": bid("aquatic_cable")},
       "elements": [{"from": [6, 6, 0], "to": [10, 10, 5], "faces": {f: {"texture": "#c"} for f in ("north", "east", "west", "up", "down")}}]},
      "models/block/aquatic_cable_arm.json")
    parts = [{"apply": {"model": bid("aquatic_cable_core")}}]
    for side, rot in (("north", {}), ("east", {"y": 90}), ("south", {"y": 180}), ("west", {"y": 270}), ("up", {"x": 270}), ("down", {"x": 90})):
        parts.append({"when": {side: "true"}, "apply": {"model": bid("aquatic_cable_arm"), **rot}})
    w({"multipart": parts}, "blockstates/aquatic_cable.json")
    w({"parent": "minecraft:block/block", "textures": {"c": bid("aquatic_cable"), "particle": bid("aquatic_cable")},
       "elements": [{"from": [5, 5, 0], "to": [11, 11, 16], "faces": {f: {"texture": "#c"} for f in ("north", "south", "east", "west", "up", "down")}}],
       "display": {"gui": {"rotation": [30, 45, 0], "scale": [0.8, 0.8, 0.8]}}}, "models/block/aquatic_cable_item.json")
    w(bld.item_def(bid("aquatic_cable_item")), "items/aquatic_cable.json")
    w({"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/neutronium_stardust"}}, "models/item/neutronium_stardust.json")
    w(bld.item_def(f"{NS}:item/neutronium_stardust"), "items/neutronium_stardust.json")

    blocks = list(GENERATORS) + ["aquatic_cable"]
    for name in blocks:
        w(bld.self_drop(name), f"{NS}/loot_table/blocks/{name}.json", DATA)
        pattern, key, count = RECIPES[name]
        w(bld.shaped(name, pattern, key, count), f"{NS}/recipe/{name}.json", DATA)
    # O pó do Coletor vira Poeira de Neutrônio na Forja, caro: 8 pós e um Cristal de Maré.
    w({"type": f"{NS}:abyssal_forging", "category": "misc", "key": {"S": f"{NS}:neutronium_stardust", "C": f"{NS}:tide_crystal"},
       "pattern": ["SSS", "SCS", "SSS"], "result": {"id": f"{NS}:neutronium_dust"}}, f"{NS}/recipe/neutronium_dust_from_stardust.json", DATA)
    bld.merge_tag("minecraft/tags/block/mineable/pickaxe.json", [f"{NS}:{n}" for n in blocks if n != "wave_buoy"])
    bld.merge_tag("minecraft/tags/block/mineable/axe.json", [f"{NS}:wave_buoy"])
    bld.merge_tag("minecraft/tags/block/needs_diamond_tool.json", [f"{NS}:neutronium_collector"])

    lang = {}
    for name, (pt, en, tip_pt, tip_en) in NAMES.items():
        lang[f"block.{NS}.{name}"] = (pt, en)
        lang[f"tooltip.{NS}.{name}"] = (tip_pt, tip_en)
    lang[f"item.{NS}.neutronium_stardust"] = ("Pó Estelar de Neutrônio", "Neutronium Stardust")
    lang[f"tooltip.{NS}.neutronium_stardust"] = ("Junta-se no Coletor de Neutrônio.", "Gathered by the Neutronium Collector.")
    lang[f"tooltip.{NS}.neutronium_stardust.2"] = ("8 Pós e 1 Cristal de Maré na Forja dão 1 Poeira de Neutrônio.",
                                                   "8 Stardust and 1 Tide Crystal in the Forge make 1 Neutronium Dust.")
    lang[f"message.{NS}.energy.generator"] = ("Energia: %s / %s E · gerando %s E/t", "Energy: %s / %s E · making %s E/t")
    lang[f"message.{NS}.energy.collector"] = ("Energia: %s / %s E · pó %s%% · %s", "Energy: %s / %s E · dust %s%% · %s")
    lang[f"message.{NS}.energy.collector.ok"] = ("lugar bom", "good spot")
    lang[f"message.{NS}.energy.collector.bad"] = ("precisa de oceano profundo, água em cima e 20 blocos abaixo do mar",
                                                  "needs deep ocean, water above and 20 blocks below sea level")
    bld.merge_lang(lang)

    if "--preview" in sys.argv:
        scale, pad = 6, 6
        sheet = Image.new("RGBA", (len(images) * (16 * scale + pad), 16 * scale), (30, 34, 44, 255))
        for i, img in enumerate(images.values()):
            sheet.alpha_composite(img.crop((0, 0, 16, 16)).resize((16 * scale, 16 * scale), Image.NEAREST), (i * (16 * scale + pad), 0))
        sheet.save(os.path.join(REPO, "build", "phase6_energy_preview.png"))
    print(f"fase 6 (6E): {len(images)} texturas, {len(blocks)} blocos de energia")


if __name__ == "__main__":
    main()
