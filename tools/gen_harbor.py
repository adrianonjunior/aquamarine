"""Gera a estrutura do Porto dos Náufragos (Vila subaquática, faixa 3E) como NBT de estrutura.

Uso (na raiz do repositório):
    C:\\Python314\\python tools/gen_harbor.py

Grava src/main/resources/data/aquamarine/structure/harbor/porto.nbt. A peça fica no fundo do
oceano profundo (docs/08-fase3.md, seção 8): plataforma de prismarinho, Cúpula Central de vidro
cheia de ar (sem Pressão), Eclusa com duas portas, as três oficinas (Bigorna de Maré, Mesa de
Cartas Náuticas, Altar de Concha) com baús e 2 Mergulhadores cada, dormitório e estufa de algas.
Tudo reproduzível: mude aqui e rode de novo.
"""
import gzip
import io
import math
import os
import struct

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(REPO, "src", "main", "resources", "data", "aquamarine", "structure", "harbor", "porto.nbt")
DATA_VERSION = 5023  # Minecraft 26.3 (version.json, world_version); a paleta usa "id" e "properties"

SIZE_XZ = 48
HEIGHT = 16
C = 24  # centro em x e z
DOME_R = 13
PLATFORM_R = 23
LOOT = "aquamarine:chests/harbor"


# ---------------------------------------------------------------- NBT mínimo

class Tag:
    def __init__(self, kind, value, item_kind=None):
        self.kind, self.value, self.item_kind = kind, value, item_kind


def i(v):
    return Tag(3, v)


def b(v):
    return Tag(1, v)


def ints(values):
    return Tag(9, [i(v) for v in values], 3)


def doubles(values):
    return Tag(9, [Tag(6, float(v)) for v in values], 6)


def compounds(values):
    return Tag(9, values, 10)


def _payload(out, tag):
    k, v = tag.kind, tag.value
    if k == 1:
        out.write(struct.pack(">b", v))
    elif k == 3:
        out.write(struct.pack(">i", v))
    elif k == 6:
        out.write(struct.pack(">d", v))
    elif k == 8:
        data = v.encode("utf-8")
        out.write(struct.pack(">H", len(data)) + data)
    elif k == 9:
        out.write(struct.pack(">bi", tag.item_kind if v else 0, len(v)))
        for item in v:
            _payload(out, _wrap(item))
    elif k == 10:
        for name, child in v.items():
            child = _wrap(child)
            out.write(struct.pack(">b", child.kind))
            _payload(out, Tag(8, name))
            _payload(out, child)
        out.write(b"\x00")
    else:
        raise ValueError(k)


def _wrap(value):
    if isinstance(value, Tag):
        return value
    if isinstance(value, str):
        return Tag(8, value)
    if isinstance(value, dict):
        return Tag(10, value)
    raise TypeError(value)


def write_nbt(path, root):
    out = io.BytesIO()
    out.write(struct.pack(">b", 10))
    _payload(out, Tag(8, ""))
    _payload(out, Tag(10, root))
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "wb") as f:
        f.write(gzip.compress(out.getvalue(), mtime=0))


# ---------------------------------------------------------------- construção

blocks = {}  # (x, y, z) -> (nome, propriedades, nbt do bloco)
entities = []


def put(x, y, z, name, props=None, nbt=None):
    if 0 <= x < SIZE_XZ and 0 <= y < HEIGHT and 0 <= z < SIZE_XZ:
        blocks[(x, y, z)] = ("minecraft:" + name if ":" not in name else name, props or {}, nbt)


def dist(x, y, z):
    return math.sqrt((x - C) ** 2 + (y - 1) ** 2 + (z - C) ** 2)


def inside_dome(x, y, z):
    return y >= 1 and dist(x, y, z) <= DOME_R


def polar(radius, degrees):
    a = math.radians(degrees)
    return int(round(C + radius * math.cos(a))), int(round(C + radius * math.sin(a)))


def build():
    # Plataforma: prismarinho escuro na borda, tijolos no resto.
    for x in range(SIZE_XZ):
        for z in range(SIZE_XZ):
            r = math.hypot(x - C, z - C)
            if r <= PLATFORM_R:
                put(x, 0, z, "dark_prismarine" if r > PLATFORM_R - 2 else "prismarine_bricks")
    # Cúpula: casca de vidro estanque (vizinho de face fora do raio) e ar por dentro.
    for x in range(SIZE_XZ):
        for z in range(SIZE_XZ):
            for y in range(1, HEIGHT):
                if not inside_dome(x, y, z):
                    continue
                shell = any(not inside_dome(x + dx, y + dy, z + dz)
                            for dx, dy, dz in ((1, 0, 0), (-1, 0, 0), (0, 1, 0), (0, 0, 1), (0, 0, -1)))
                put(x, y, z, "glass" if shell else "air")
    # Luz: lanternas do mar na casca e no piso.
    for angle in range(0, 360, 45):
        x, z = polar(DOME_R - 0.5, angle)
        for y in range(4, 7):
            if blocks.get((x, y, z), ("",))[0] == "minecraft:glass":
                put(x, y, z, "sea_lantern")
                break
        x, z = polar(6, angle + 22)
        put(x, 0, z, "sea_lantern")
    # Praça: Sino do Porto no centro e bancos virados para ele.
    put(C, 1, C, "bell", {"attachment": "floor", "facing": "north", "powered": "false"})
    for facing, (dx, dz) in (("south", (0, -3)), ("north", (0, 3)), ("east", (-3, 0)), ("west", (3, 0))):
        put(C + dx, 1, C + dz, "dark_oak_stairs", {"facing": facing, "half": "bottom", "shape": "straight", "waterlogged": "false"})
    # Oficinas: bloco de trabalho, baú e 2 Mergulhadores de cada profissão.
    for angle, block, profession in ((0, "aquamarine:tide_anvil", "aquamarine:tide_smith"),
                                     (120, "aquamarine:chart_table", "aquamarine:nautical_cartographer"),
                                     (240, "aquamarine:shell_altar", "aquamarine:abyss_enchanter")):
        x, z = polar(9, angle)
        put(x, 1, z, block)
        cx, cz = polar(10, angle + 12)
        put(cx, 1, cz, "chest", {"facing": "north", "type": "single", "waterlogged": "false"},
            {"id": "minecraft:chest", "LootTable": LOOT})
        for offset in (-14, 14):
            vx, vz = polar(6.5, angle + offset)
            villager(vx, 1, vz, profession)
    # Dormitório: 4 camas (cabeceira ao norte) e um baú.
    for x in (26, 28, 30, 32):
        put(x, 1, 31, "cyan_bed", {"facing": "north", "occupied": "false", "part": "head"})
        put(x, 1, 32, "cyan_bed", {"facing": "north", "occupied": "false", "part": "foot"})
    put(29, 1, 34, "chest", {"facing": "north", "type": "single", "waterlogged": "false"}, {"id": "minecraft:chest", "LootTable": LOOT})
    # Estufa de algas: tanque 3x3 com algas, cercado de prismarinho.
    gx, gz = polar(8, 300)
    for x in range(gx - 2, gx + 3):
        for z in range(gz - 2, gz + 3):
            edge = abs(x - gx) == 2 or abs(z - gz) == 2
            put(x, 1, z, "prismarine" if edge else "kelp", None if edge else {"age": "20"})
    # Eclusa (norte): túnel de prismarinho com porta de fora e porta de dentro; o ar entre elas
    # não deixa a água entrar.
    for z in range(5, C - DOME_R + 3):
        for x in range(C - 2, C + 3):
            for y in range(0, 5):
                wall = x in (C - 2, C + 2) or y in (0, 4)
                put(x, y, z, "prismarine_bricks" if wall else "air")
    for z, half_props in ((5, "outer"), (9, "inner")):
        for x in (C - 1, C + 1):
            for y in (1, 2, 3):
                put(x, y, z, "prismarine_bricks")
        put(C, 3, z, "prismarine_bricks")
        put(C, 1, z, "spruce_door", {"facing": "south", "half": "lower", "hinge": "left", "open": "false", "powered": "false"})
        put(C, 2, z, "spruce_door", {"facing": "south", "half": "upper", "hinge": "left", "open": "false", "powered": "false"})
    put(C, 1, 7, "sea_lantern")


def villager(x, y, z, profession):
    entities.append({
        "pos": doubles([x + 0.5, y, z + 0.5]),
        "blockPos": ints([x, y, z]),
        "nbt": {
            "id": "minecraft:villager",
            "VillagerData": {"type": "minecraft:plains", "profession": profession, "level": i(1)},
            "PersistenceRequired": b(1),
            # Com XP > 0 o jogo não tira a profissão antes de ele achar o bloco de trabalho.
            "Xp": i(1),
        },
    })


def main():
    build()
    palette, index, block_list = [], {}, []
    for pos in sorted(blocks, key=lambda p: (p[1], p[2], p[0])):
        name, props, nbt = blocks[pos]
        key = (name, tuple(sorted(props.items())))
        if key not in index:
            index[key] = len(palette)
            entry = {"id": name}
            if props:
                entry["properties"] = dict(sorted(props.items()))
            palette.append(entry)
        block = {"pos": ints(list(pos)), "state": i(index[key])}
        if nbt:
            block["nbt"] = nbt
        block_list.append(block)
    root = {
        "DataVersion": i(DATA_VERSION),
        "size": ints([SIZE_XZ, HEIGHT, SIZE_XZ]),
        "palette": compounds(palette),
        "blocks": compounds(block_list),
        "entities": compounds(entities),
    }
    write_nbt(OUT, root)
    print(f"porto.nbt: {len(block_list)} blocos, {len(palette)} estados, {len(entities)} Mergulhadores")


if __name__ == "__main__":
    main()
