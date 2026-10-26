"""Construção e decoração da Fase 6, faixa 6H (docs/11-fase6.md), no molde de gen_phase4_building.py.
Família do Aurium (ladrilhos com escada, laje e muro, ladrilho talhado e lampião) e peças de navio e naufrágio em 3D que
viram para quem as coloca: lanterna de navio, leme, âncora, rolo de corda, barril de carga, rede de pesca, aquário e
estátua do Kraken. Grava texturas, modelos, estados, loot, receitas, tags, nomes e o Java (ModDecorBlocks, gerado).
    C:\\Python314\\python tools/gen_phase6_building.py [--preview]
"""
import os
import sys

from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import gen_phase4_art as p4  # noqa: E402
import gen_phase4_building as bld  # noqa: E402
import gen_phase6_aurium as aur  # noqa: E402
from gen_phase4_art import BRASS, CORAL, DEEP, IRON, ORANGE, PARCH, STONE, WOOD, Model, Tex  # noqa: E402

NS, ASSETS, DATA, REPO = p4.NS, p4.ASSETS, bld.DATA, p4.REPO
PEARL, OPAL = aur.PEARL, aur.OPAL
ROPE = p4.ramp("4a3820", "6e5634", "927548", "b39660", "d2b680")
GLASS = (190, 236, 240, 70)


# ================================================================ texturas

def aurium_tiles():
    t = Tex(801)
    t.noise((0, 0, 16, 16), [PEARL[2], PEARL[3], PEARL[3], PEARL[4]], [2, 3, 3, 1])
    for k in (0, 8):
        t.hline(0, 15, k, PEARL[1])
        t.vline(k, 0, 15, PEARL[1])
        t.hline(1, 7, k + 1, PEARL[5])
        t.hline(9, 15, k + 1, PEARL[5])
    for x, y in ((4, 4), (12, 12), (12, 4), (4, 12)):
        t.put(x, y, t.rng.choice(OPAL))
    return t.img


def chiseled_aurium_tiles():
    """Leque de concha entalhado no meio, com a borda de opala."""
    t = Tex(802)
    t.noise((0, 0, 16, 16), [PEARL[2], PEARL[3]], [1, 2])
    t.bevel((0, 0, 16, 16), PEARL[5], PEARL[1])
    for i, x in enumerate(range(4, 13, 2)):
        t.vline(x, 5 + abs(8 - x) // 2, 12, PEARL[1])
        t.vline(x + 1, 5 + abs(8 - x) // 2, 12, PEARL[4])
    t.hline(4, 12, 12, PEARL[1])
    t.hline(5, 11, 4, OPAL[0])
    t.put(8, 3, OPAL[1])
    return t.img


def aurium_lamp():
    t = Tex(803)
    t.rect((0, 0, 16, 16), PEARL[3])
    t.bevel((0, 0, 16, 16), PEARL[5], PEARL[1])
    t.bevel((1, 1, 15, 15), PEARL[1], PEARL[4])
    for y in range(2, 14):
        for x in range(2, 14):
            t.put(x, y, OPAL[(x + y) // 3 % 2] if (x + y) % 5 else (255, 255, 255, 255))
    t.hline(2, 13, 7, PEARL[2])
    t.vline(7, 2, 13, PEARL[2])
    return t.img


def brass():
    t = Tex(810)
    t.noise((0, 0, 16, 16), [BRASS[2], BRASS[3], BRASS[3], BRASS[4]], [1, 3, 2, 1])
    t.scatter(5, [p4.PATINA[2], p4.PATINA[3]])
    return t.img


def lantern_glass():
    """Vidro quente da lanterna, aceso."""
    t = Tex(811)
    t.noise((0, 0, 16, 16), [(255, 214, 140, 255), (255, 236, 180, 255), (255, 196, 110, 255)], [2, 2, 1])
    t.disc(8, 9, 3, (255, 250, 220, 255))
    t.vline(5, 0, 15, BRASS[1])
    t.vline(10, 0, 15, BRASS[1])
    return t.img


def wood(seed, vertical=False):
    t = Tex(seed)
    for i in range(16):
        c = WOOD[2 + (i * 7 + seed) % 4]
        (t.vline(i, 0, 15, c) if vertical else t.hline(0, 15, i, c))
    t.scatter(6, [WOOD[1]])
    return t.img


def iron():
    t = Tex(813)
    t.noise((0, 0, 16, 16), [IRON[2], IRON[3], IRON[3], IRON[4]], [1, 3, 2, 1])
    t.scatter(4, [ORANGE[1], ORANGE[2]])  # ferrugem
    return t.img


def rope():
    t = Tex(814)
    for y in range(16):
        for x in range(16):
            t.put(x, y, ROPE[1 + ((x + y * 2) // 2) % 4])
    return t.img


def barrel_top():
    t = Tex(815)
    t.rect((0, 0, 16, 16), WOOD[3])
    for r, c in ((7.5, WOOD[1]), (6.5, WOOD[4]), (3.5, WOOD[2])):
        t.disc(8, 8, r, c)
    t.bevel((0, 0, 16, 16), IRON[4], IRON[2])
    return t.img


def net():
    """Rede de pesca: malha de corda com vazio entre os fios (recorte)."""
    t = Tex(816)
    for i in range(0, 16, 4):
        t.hline(0, 15, i, ROPE[2])
        t.vline(i, 0, 15, ROPE[3])
    return t.img


def cork():
    t = Tex(817)
    t.noise((0, 0, 16, 16), [ORANGE[3], ORANGE[4], ORANGE[2]], [3, 2, 1])
    return t.img


def aq_glass():
    """Vidro do aquário: quase transparente, com moldura e um reflexo diagonal."""
    t = Tex(818)
    t.rect((0, 0, 16, 16), GLASS)
    t.bevel((0, 0, 16, 16), IRON[5], IRON[3])
    for i in range(3, 9):
        t.put(i, 12 - i, (240, 255, 255, 140))
    return t.img


def sand():
    t = Tex(819)
    t.noise((0, 0, 16, 16), [PARCH[2], PARCH[3], PARCH[3], PARCH[4]], [1, 3, 2, 1])
    t.scatter(4, [CORAL[3], (240, 240, 240, 255)])
    return t.img


def fish():
    t = Tex(820)
    t.rect((0, 0, 16, 16), ORANGE[4])
    for x in (4, 9, 13):
        t.vline(x, 0, 15, (245, 245, 245, 255))
    t.put(2, 6, (10, 10, 10, 255))
    return t.img


def statue_stone():
    t = Tex(821)
    t.noise((0, 0, 16, 16), [STONE[2], STONE[3], STONE[3], STONE[4]], [1, 3, 2, 1])
    t.scatter(5, [STONE[5]])
    return t.img


def statue_gem():
    t = Tex(822)
    t.rect((0, 0, 16, 16), p4.CYAN_SOFT)
    t.disc(8, 8, 4, p4.CYAN)
    return t.img


TEXTURES = {
    "aurium_tiles": aurium_tiles, "chiseled_aurium_tiles": chiseled_aurium_tiles, "aurium_lamp": aurium_lamp,
    "decor_brass": brass, "decor_lantern_glass": lantern_glass, "decor_wood": lambda: wood(812),
    "decor_staves": lambda: wood(823, vertical=True), "decor_iron": iron, "decor_rope": rope, "decor_barrel_top": barrel_top,
    "decor_net": net, "decor_cork": cork, "decor_aquarium_glass": aq_glass, "decor_sand": sand, "decor_fish": fish,
    "decor_coral": lambda: p4.coral_sprig(824), "decor_statue_stone": statue_stone, "decor_statue_gem": statue_gem,
    "decor_deep": lambda: aurium_like_deep(),
}


def aurium_like_deep():
    t = Tex(825)
    t.noise((0, 0, 16, 16), [DEEP[2], DEEP[3], DEEP[4]], [2, 3, 1])
    t.bevel((0, 0, 16, 16), DEEP[5], DEEP[0])
    return t.img


# ================================================================ modelos 3D (frente para o norte)

def ship_lantern():
    m = Model({"brass": "decor_brass", "glass": "decor_lantern_glass"}, "brass")
    m.box((4, 0, 4), (12, 2, 12), "brass")
    m.box((5, 2, 5), (11, 10, 11), "glass", light=15)
    for x, z in ((4.5, 4.5), (10.5, 4.5), (4.5, 10.5), (10.5, 10.5)):
        m.box((x, 2, z), (x + 1, 10, z + 1), "brass")
    m.box((4, 10, 4), (12, 12, 12), "brass")
    m.box((5.5, 12, 5.5), (10.5, 13, 10.5), "brass")
    m.box((7, 13, 7.5), (9, 15, 8.5), "brass")
    return m, (4, 0, 4, 12, 15, 12)


def ship_wheel():
    """Leme preso na parede de trás: aro, oito raios com punhos e o cubo de ferro."""
    m = Model({"wood": "decor_wood", "iron": "decor_iron"}, "wood")
    z0, z1 = 13.5, 15
    m.box((2, 13, z0), (14, 14, z1), "wood")
    m.box((2, 2, z0), (14, 3, z1), "wood")
    m.box((2, 3, z0), (3, 13, z1), "wood")
    m.box((13, 3, z0), (14, 13, z1), "wood")
    m.box((7.5, -1, z0 + 0.2), (8.5, 17, z1 - 0.2), "wood")
    m.box((-1, 7.5, z0 + 0.2), (17, 8.5, z1 - 0.2), "wood")
    for angle in (45, -45):
        m.box((7.5, 0.5, z0 + 0.3), (8.5, 15.5, z1 - 0.3), "wood", rot=((8, 8, 14.25), ("z", angle)))
    m.box((6.5, 6.5, 12.5), (9.5, 9.5, 16), "iron")
    return m, (0, 0, 12.5, 16, 16, 16)


def decorative_anchor():
    m = Model({"iron": "decor_iron", "wood": "decor_wood"}, "iron")
    m.box((7, 3, 7), (9, 14, 9), "iron")
    m.box((6.5, 14, 7.5), (9.5, 16, 8.5), "iron")
    m.box((2.5, 1, 7), (13.5, 3, 9), "iron")
    m.box((2, 3, 7), (4, 6, 9), "iron")
    m.box((12, 3, 7), (14, 6, 9), "iron")
    m.box((1, 5, 6.5), (4.5, 6.5, 9.5), "iron", rot=((2.5, 5.5, 8), ("z", 30)))
    m.box((11.5, 5, 6.5), (15, 6.5, 9.5), "iron", rot=((13.5, 5.5, 8), ("z", -30)))
    m.box((3, 11, 7.5), (13, 12.5, 8.5), "wood")
    m.box((7.5, 0, 7.5), (8.5, 1, 8.5), "iron")
    return m, (1, 0, 6, 15, 16, 10)


def rope_coil():
    m = Model({"rope": "decor_rope"}, "rope")
    for y0, y1, a, b in ((0, 2, 3, 13), (2, 3.8, 3.8, 12.2), (3.8, 5.2, 4.8, 11.2)):
        w = 2 if y0 == 0 else 1.8
        m.box((a, y0, a), (b, y1, a + w), "rope")
        m.box((a, y0, b - w), (b, y1, b), "rope")
        m.box((a, y0, a + w), (a + w, y1, b - w), "rope")
        m.box((b - w, y0, a + w), (b, y1, b - w), "rope")
    m.box((10, 0, 1), (11.5, 1.2, 4), "rope", rot=((10.75, 0.6, 2.5), ("y", -30)))
    return m, (3, 0, 3, 13, 6, 13)


def cargo_barrel():
    m = Model({"staves": "decor_staves", "top": "decor_barrel_top", "iron": "decor_iron", "rope": "decor_rope"}, "staves")
    m.box((2, 0, 2), (14, 14, 14), {"*": "staves", "up": "top", "down": "top"})
    for y in (2, 11):
        m.box((1.8, y, 1.8), (14.2, y + 1, 14.2), "iron", faces=("north", "south", "east", "west"))
    m.box((7.5, 0, 1.6), (8.5, 14.2, 14.4), "rope", faces=("north", "south", "up"))
    return m, (2, 0, 2, 14, 14, 14)


def fishing_net():
    m = Model({"net": "decor_net", "cork": "decor_cork"}, "net")
    m.box((0, 0, 0), (16, 0.5, 16), "net", faces=("up", "down"))
    for x, z in ((1.5, 1.5), (13, 2), (2, 13), (12.5, 12.5), (7.5, 7)):
        m.box((x, 0.2, z), (x + 1.5, 1.2, z + 1.5), "cork")
    return m, (0, 0, 0, 16, 1, 16)


def aquarium():
    m = Model({"deep": "decor_deep", "glass": "decor_aquarium_glass", "sand": "decor_sand", "coral": "decor_coral",
               "fish": "decor_fish", "brass": "decor_brass"}, "deep")
    m.box((0, 0, 0), (16, 2, 16), "deep")
    m.box((1, 2, 1), (15, 3, 15), "sand", faces=("up",))
    m.box((0, 2, 0), (16, 14, 0.5), "glass", faces=("north", "south"))
    m.box((0, 2, 15.5), (16, 14, 16), "glass", faces=("north", "south"))
    m.box((0, 2, 0.5), (0.5, 14, 15.5), "glass", faces=("east", "west"))
    m.box((15.5, 2, 0.5), (16, 14, 15.5), "glass", faces=("east", "west"))
    for x0, z0, x1, z1 in ((0, 0, 16, 1), (0, 15, 16, 16), (0, 1, 1, 15), (15, 1, 16, 15)):
        m.box((x0, 14, z0), (x1, 15, z1), "brass")
    m.plane((5, 3, 10), (6, 8), "coral", 45)
    m.plane((5, 3, 10), (6, 8), "coral", -45)
    m.plane((11, 3, 5), (5, 6), "coral", 30)
    m.box((7, 8, 6), (10, 9.5, 7), "fish", uv={f: [0, 4, 6, 8] for f in ("north", "south", "east", "west", "up", "down")})
    m.box((9.5, 8.2, 6.2), (11, 9.3, 6.8), "fish", uv={f: [12, 4, 16, 8] for f in ("north", "south", "east", "west")})
    m.box((4, 11, 9), (6.5, 12.2, 9.8), "fish", uv={f: [0, 4, 6, 8] for f in ("north", "south", "east", "west", "up", "down")})
    return m, (0, 0, 0, 16, 15, 16)


def kraken_statue():
    """Pedestal de Pedra de Maré com um tentáculo de pedra que sobe em espiral e um olho aceso na base."""
    m = Model({"stone": "decor_statue_stone", "gem": "decor_statue_gem", "pearl": "aurium_tiles"}, "stone")
    m.box((1, 0, 1), (15, 3, 15), {"*": "stone", "up": "pearl"})
    m.box((2, 3, 2), (14, 4, 14), "stone")
    segs = ((6, 4, 6, 10, 8, 10, 0), (6.5, 8, 6.5, 9.5, 11, 9.5, 18), (7, 11, 6, 9.5, 13.5, 8.5, 36), (7.4, 13.5, 4.8, 9, 15, 6.4, 60))
    for x0, y0, z0, x1, y1, z1, ang in segs:
        m.box((x0, y0, z0), (x1, y1, z1), "stone", rot=(((x0 + x1) / 2, y0, (z0 + z1) / 2), ("x", -ang)) if ang else None)
    m.box((7.4, 5, 5.7), (8.6, 6.2, 6), "gem", faces=("north",), light=10)
    return m, (1, 0, 1, 15, 16, 15)


# (id, pt, en, dica pt, dica en, construtor do modelo, fonte das propriedades, extra das propriedades, ferramenta)
DECOR = [
    ("ship_lantern", "Lanterna de Navio", "Ship Lantern", "Luz 15; a lanterna de bronze dos conveses.", "Light 15; the bronze deck lantern.",
     ship_lantern, "Blocks.LANTERN", ".lightLevel(state -> 15)", "pickaxe"),
    ("ship_wheel", "Leme de Navio", "Ship Wheel", "Decoração de parede: o leme de um naufrágio.", "Wall decoration: a shipwreck's helm.",
     ship_wheel, "Blocks.OAK_PLANKS", "", "axe"),
    ("decorative_anchor", "Âncora Decorativa", "Decorative Anchor", "Âncora de ferro com o cepo de madeira.",
     "Iron anchor with a wooden stock.", decorative_anchor, "Blocks.IRON_BARS", "", "pickaxe"),
    ("rope_coil", "Rolo de Corda", "Rope Coil", "Corda enrolada no chão do convés.", "Rope coiled on the deck.",
     rope_coil, "Blocks.HAY_BLOCK", "", None),
    ("cargo_barrel", "Barril de Carga", "Cargo Barrel", "Barril de porão com aros de ferro e corda.", "Hold barrel with iron hoops and rope.",
     cargo_barrel, "Blocks.BARREL", "", "axe"),
    ("fishing_net", "Rede de Pesca", "Fishing Net", "Rede estendida no chão, com boias de cortiça.", "Net spread on the floor, with cork floats.",
     fishing_net, "Blocks.MOSS_CARPET", ".noCollision()", None),
    ("aquarium", "Aquário", "Aquarium", "Tanque com areia, coral e peixes; luz 8.", "Tank with sand, coral and fish; light 8.",
     aquarium, "Blocks.GLASS", ".lightLevel(state -> 8)", "pickaxe"),
    ("kraken_statue", "Estátua do Kraken", "Kraken Statue", "Tentáculo de pedra com o olho aceso; luz 4.",
     "Stone tentacle with a glowing eye; light 4.", kraken_statue, "Blocks.STONE_BRICKS", ".lightLevel(state -> 4)", "pickaxe"),
]
DECOR_RECIPES = {
    "ship_lantern": ([" N ", "NLN", " C "], {"N": "minecraft:iron_nugget", "L": "minecraft:lantern", "C": "minecraft:copper_ingot"}, 1),
    "ship_wheel": (["SPS", "PIP", "SPS"], {"S": "minecraft:stick", "P": "#minecraft:planks", "I": "minecraft:iron_ingot"}, 1),
    "decorative_anchor": ([" I ", "III", "I I"], {"I": "minecraft:iron_ingot"}, 1),
    "rope_coil": (["SSS", "S S", "SSS"], {"S": "minecraft:string"}, 1),
    "cargo_barrel": (["S", "B", "S"], {"S": "minecraft:string", "B": "minecraft:barrel"}, 1),
    "fishing_net": (["S S", " S ", "S S"], {"S": "minecraft:string"}, 2),
    "aquarium": (["GGG", "GCG", "GSG"], {"G": "minecraft:glass", "C": "minecraft:tube_coral", "S": "minecraft:sand"}, 1),
    "kraken_statue": ([" T ", "TET", "TTT"], {"T": f"{NS}:tidestone", "E": "minecraft:prismarine_crystals"}, 1),
}


# ================================================================ saída

def write_java(entries):
    lines = []
    for const, group, name, factory, props in entries:
        lines.append(f"\tpublic static final Block {const} = add({group}, \"{name}\", {factory},\n\t\t{props});")
    body = "\n".join(lines)
    text = f"""package com.aquamarine.block;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Construção e decoração da Fase 6 (6H, docs/11-fase6.md): família do Aurium e peças de navio, naufrágio e aquário.
 * GERADO por tools/gen_phase6_building.py: não edite à mão (mude a lista no gerador e rode de novo).
 */
public final class ModDecorBlocks {{
	private static final List<Block> BUILDING = new ArrayList<>();
	private static final List<Block> DECOR = new ArrayList<>();

{body}

	private static Block add(List<Block> group, String name, Function<BlockBehaviour.Properties, Block> factory,
		BlockBehaviour.Properties properties) {{
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Aquamarine.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
		group.add(block);
		return block;
	}}

	/** Blocos de construção (aba de construção), na ordem da aba. */
	public static List<Block> building() {{
		return Collections.unmodifiableList(BUILDING);
	}}

	/** Peças de decoração (aba de blocos funcionais), na ordem da aba. */
	public static List<Block> decor() {{
		return Collections.unmodifiableList(DECOR);
	}}

	public static void register() {{
		// Os campos estáticos registram os blocos; aqui entram os itens, depois dos do ModItems.
		BUILDING.forEach(block -> ModItems.registerBlock(block, new Item.Properties()));
		DECOR.forEach(block -> ModItems.registerBlock(block, new Item.Properties()));
	}}

	private ModDecorBlocks() {{}}
}}
"""
    path = os.path.join(REPO, "src", "main", "java", "com", "aquamarine", "block", "ModDecorBlocks.java")
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(text)


def main():
    w, bid = bld.write_json, bld.block_id
    images = {}
    for name, fn in TEXTURES.items():
        images[name] = fn()
        p4.write_png(images[name], f"block/{name}")
    lang, java, pick, axe, recipes = {}, [], [], [], {}
    stairs_tag, slabs_tag, walls_tag = [], [], []
    tiles = "aurium_tiles"
    tile_props = "BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES).mapColor(MapColor.QUARTZ)"

    def cube(name, pt, en, tip_pt, tip_en, props):
        w({"parent": "minecraft:block/cube_all", "textures": {"all": bid(name)}}, f"models/block/{name}.json")
        w({"variants": {"": {"model": bid(name)}}}, f"blockstates/{name}.json")
        w(bld.item_def(bid(name)), f"items/{name}.json")
        w(bld.self_drop(name), f"{NS}/loot_table/blocks/{name}.json", DATA)
        lang[f"block.{NS}.{name}"] = (pt, en)
        lang[f"tooltip.{NS}.{name}"] = (tip_pt, tip_en)
        java.append((bld.java_const(name), "BUILDING", name, "Block::new", props))
        pick.append(f"{NS}:{name}")

    cube(tiles, "Ladrilhos de Aurium", "Aurium Tiles", "Ardósia clara com filetes de Aurium.", "Pale slate with Aurium inlays.", tile_props)
    for shape, sid in (("stairs", "aurium_tile_stairs"), ("slab", "aurium_tile_slab"), ("wall", "aurium_tile_wall")):
        if shape == "stairs":
            for suffix, parent in (("", "stairs"), ("_inner", "inner_stairs"), ("_outer", "outer_stairs")):
                w({"parent": f"minecraft:block/{parent}", "textures": {"bottom": bid(tiles), "side": bid(tiles), "top": bid(tiles)}},
                  f"models/block/{sid}{suffix}.json")
            w(bld.templated("stairs", sid), f"blockstates/{sid}.json")
            java.append((bld.java_const(sid), "BUILDING", sid, "p -> new StairBlock(AURIUM_TILES.defaultBlockState(), p) {}",
                         "BlockBehaviour.Properties.ofFullCopy(AURIUM_TILES)"))
            stairs_tag.append(f"{NS}:{sid}")
            recipes[sid] = bld.shaped(sid, ["#  ", "## ", "###"], {"#": f"{NS}:{tiles}"}, 4)
            pt, en = "Escadas de Ladrilhos de Aurium", "Aurium Tile Stairs"
        elif shape == "slab":
            for suffix, parent in (("", "slab"), ("_top", "slab_top")):
                w({"parent": f"minecraft:block/{parent}", "textures": {"bottom": bid(tiles), "side": bid(tiles), "top": bid(tiles)}},
                  f"models/block/{sid}{suffix}.json")
            w({"variants": {"type=bottom": {"model": bid(sid)}, "type=double": {"model": bid(tiles)}, "type=top": {"model": bid(f"{sid}_top")}}},
              f"blockstates/{sid}.json")
            java.append((bld.java_const(sid), "BUILDING", sid, "SlabBlock::new", "BlockBehaviour.Properties.ofFullCopy(AURIUM_TILES)"))
            slabs_tag.append(f"{NS}:{sid}")
            recipes[sid] = bld.shaped(sid, ["###"], {"#": f"{NS}:{tiles}"}, 6)
            pt, en = "Laje de Ladrilhos de Aurium", "Aurium Tile Slab"
        else:
            for suffix, parent in (("_post", "template_wall_post"), ("_side", "template_wall_side"),
                                   ("_side_tall", "template_wall_side_tall"), ("_inventory", "wall_inventory")):
                w({"parent": f"minecraft:block/{parent}", "textures": {"wall": bid(tiles)}}, f"models/block/{sid}{suffix}.json")
            w(bld.templated("wall", sid), f"blockstates/{sid}.json")
            java.append((bld.java_const(sid), "BUILDING", sid, "WallBlock::new", "BlockBehaviour.Properties.ofFullCopy(AURIUM_TILES)"))
            walls_tag.append(f"{NS}:{sid}")
            recipes[sid] = bld.shaped(sid, ["###", "###"], {"#": f"{NS}:{tiles}"}, 6)
            pt, en = "Muro de Ladrilhos de Aurium", "Aurium Tile Wall"
        w(bld.item_def(bid(sid + ("_inventory" if shape == "wall" else ""))), f"items/{sid}.json")
        w(bld.slab_drop(sid) if shape == "slab" else bld.self_drop(sid), f"{NS}/loot_table/blocks/{sid}.json", DATA)
        lang[f"block.{NS}.{sid}"] = (pt, en)
        lang[f"tooltip.{NS}.{sid}"] = (f"{pt}.", f"{en}.")
        pick.append(f"{NS}:{sid}")
        recipes[f"{sid}_from_{tiles}_stonecutting"] = bld.cutting(tiles, sid, 2 if shape == "slab" else 1)
    cube("chiseled_aurium_tiles", "Ladrilho de Aurium Talhado", "Chiseled Aurium Tiles", "Uma concha em leque, com borda de opala.",
         "A fan shell with an opal rim.", tile_props)
    cube("aurium_lamp", "Lampião de Aurium", "Aurium Lamp", "Luz 15; o brilho perolado do Aurium.", "Light 15; Aurium's pearly glow.",
         "BlockBehaviour.Properties.ofFullCopy(Blocks.SEA_LANTERN).mapColor(MapColor.QUARTZ).lightLevel(state -> 15)")
    recipes[tiles] = bld.shaped(tiles, ["###", "#I#", "###"], {"#": "minecraft:deepslate_tiles", "I": f"{NS}:aurium_ingot"}, 8)
    recipes[f"chiseled_aurium_tiles_from_{tiles}_stonecutting"] = bld.cutting(tiles, "chiseled_aurium_tiles")
    recipes["aurium_lamp"] = bld.shaped("aurium_lamp", ["TTT", "TGT", "TTT"], {"T": f"{NS}:{tiles}", "G": "minecraft:glowstone"}, 4)

    for name, pt, en, tip_pt, tip_en, builder, source, extra, tool in DECOR:
        model, shape = builder()
        w(model.json(), f"models/block/{name}.json")
        w(p4.facing_states(name), f"blockstates/{name}.json")
        w(bld.item_def(bid(name)), f"items/{name}.json")
        w(bld.self_drop(name), f"{NS}/loot_table/blocks/{name}.json", DATA)
        lang[f"block.{NS}.{name}"] = (pt, en)
        lang[f"tooltip.{NS}.{name}"] = (tip_pt, tip_en)
        box = ", ".join(f"{v:g}" for v in shape)
        java.append((bld.java_const(name), "DECOR", name, f"p -> new FacingShapeBlock(p, Block.box({box}))",
                     f"BlockBehaviour.Properties.ofFullCopy({source}){extra}.noOcclusion()"))
        if tool == "pickaxe":
            pick.append(f"{NS}:{name}")
        elif tool == "axe":
            axe.append(f"{NS}:{name}")
        pattern, key, count = DECOR_RECIPES[name]
        recipes[name] = bld.shaped(name, pattern, key, count)
    for name, data in recipes.items():
        w(data, f"{NS}/recipe/{name}.json", DATA)
    for rel, values in (("minecraft/tags/block/mineable/pickaxe.json", pick), ("minecraft/tags/block/mineable/axe.json", axe),
                        ("minecraft/tags/block/stairs.json", stairs_tag), ("minecraft/tags/block/slabs.json", slabs_tag),
                        ("minecraft/tags/block/walls.json", walls_tag), ("minecraft/tags/item/stairs.json", stairs_tag),
                        ("minecraft/tags/item/slabs.json", slabs_tag), ("minecraft/tags/item/walls.json", walls_tag)):
        bld.merge_tag(rel, values)
    bld.merge_lang(lang)
    write_java(java)

    if "--preview" in sys.argv:
        scale, pad = 6, 6
        sheet = Image.new("RGBA", (len(images) * (16 * scale + pad), 16 * scale), (30, 34, 44, 255))
        for i, img in enumerate(images.values()):
            sheet.alpha_composite(img.crop((0, 0, 16, 16)).resize((16 * scale, 16 * scale), Image.NEAREST), (i * (16 * scale + pad), 0))
        sheet.save(os.path.join(REPO, "build", "phase6_building_preview.png"))
    print(f"fase 6 (6H): {len(images)} texturas, {len(java)} blocos (ModDecorBlocks)")


if __name__ == "__main__":
    main()
