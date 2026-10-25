"""Gera a estrutura do Porto dos Náufragos (Vila subaquática, faixa 3E) como NBT de estrutura.

Uso (na raiz do repositório):
    C:\\Python314\\python tools/gen_harbor.py

Grava duas peças em src/main/resources/data/aquamarine/structure/harbor/:
- porto.nbt (80 x 20 x 80, no fundo do oceano profundo; docs/08-fase3.md, seção 8): plataforma de
  prismarinho; Cúpula Central de vidro cheia de ar (sem Pressão) com o Sino do Porto e bancos; cinco
  prédios de vidro ligados a ela por corredores (Oficina do Ferreiro de Maré, Mercado do Cartógrafo,
  Santuário do Encantador de Fossa, Dormitórios e Estufa de algas), cada oficina com bloco de trabalho,
  baú e 3 Mergulhadores; a Eclusa com duas portas ao norte; e o Cais no anel externo, com 6 docas,
  postes de amarrar a baleia e dois elevadores de bolhas (areia das almas sobe, magma desce) até a
  superfície. No alto da peça, um bloco jigsaw puxa a Torre de Vigia.
- torre.nbt (9 x 12 x 9): a Torre de Vigia, que o jigsaw põe na superfície do mar acima da Cúpula
  (projeção "terrain_matching"), com farol e para-raios.
Tudo reproduzível: mude aqui e rode de novo.
"""
import gzip
import io
import math
import os
import struct

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT_DIR = os.path.join(REPO, "src", "main", "resources", "data", "aquamarine", "structure", "harbor")
DATA_VERSION = 5023  # Minecraft 26.3 (version.json, world_version); a paleta usa "id" e "properties"

SIZE_XZ = 80
HEIGHT = 20
C = 40  # centro em x e z
DOME_R = 13
ANNEX_R = 6
ANNEX_D = 22  # distância do centro da Cúpula ao centro de cada prédio
CORRIDOR_HALF = 2  # meia largura do corredor (interior de 3 blocos)
CORRIDOR_TOP = 4  # teto do corredor (interior de 3 blocos de altura)
INNER_R = 28  # até aqui o piso é de tijolos; daí até PLATFORM_R é o Cais
PLATFORM_R = 38
LOOT = "aquamarine:chests/harbor"

# Prédios: (ângulo em graus, papel). Norte (z menor) é 270 graus, onde fica a Eclusa.
ANNEXES = (
    (330, "smith"),
    (30, "market"),
    (90, "dorm"),
    (150, "sanctuary"),
    (210, "greenhouse"),
)
WORKSHOPS = {
    "smith": ("aquamarine:tide_anvil", "aquamarine:tide_smith"),
    "market": ("aquamarine:chart_table", "aquamarine:nautical_cartographer"),
    "sanctuary": ("aquamarine:shell_altar", "aquamarine:abyss_enchanter"),
}
DOCKS = (0, 60, 120, 180, 240, 300)
# Mudou os elevadores? Atualize as bases em HarborElevators.BASES (o script imprime as posições).
ELEVATORS = (0, 180)


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


class Piece:
    """Uma peça de estrutura: blocos (x, y, z) -> (nome, propriedades, nbt) e entidades."""

    def __init__(self, size_xz, height):
        self.size_xz, self.height = size_xz, height
        self.blocks = {}
        self.entities = []

    def put(self, x, y, z, name, props=None, nbt=None):
        if 0 <= x < self.size_xz and 0 <= y < self.height and 0 <= z < self.size_xz:
            self.blocks[(x, y, z)] = ("minecraft:" + name if ":" not in name else name, props or {}, nbt)

    def get(self, x, y, z):
        return self.blocks.get((x, y, z), ("",))[0]

    def save(self, name):
        palette, index, block_list = [], {}, []
        for pos in sorted(self.blocks, key=lambda p: (p[1], p[2], p[0])):
            block_name, props, nbt = self.blocks[pos]
            key = (block_name, tuple(sorted(props.items())))
            if key not in index:
                index[key] = len(palette)
                entry = {"id": block_name}
                if props:
                    entry["properties"] = dict(sorted(props.items()))
                palette.append(entry)
            block = {"pos": ints(list(pos)), "state": i(index[key])}
            if nbt:
                block["nbt"] = nbt
            block_list.append(block)
        write_nbt(os.path.join(OUT_DIR, name), {
            "DataVersion": i(DATA_VERSION),
            "size": ints([self.size_xz, self.height, self.size_xz]),
            "palette": compounds(palette),
            "blocks": compounds(block_list),
            "entities": compounds(self.entities),
        })
        return len(block_list), len(palette), len(self.entities)


def jigsaw(name, target, pool, final_state, orientation):
    props = {"orientation": orientation}
    nbt = {"id": "minecraft:jigsaw", "name": name, "target": target, "pool": pool, "final_state": final_state,
           "joint": "rolling", "placement_priority": i(0), "selection_priority": i(0)}
    return props, nbt


# ---------------------------------------------------------------- Porto (peça do fundo)

def polar(radius, degrees, cx=C, cz=C):
    a = math.radians(degrees)
    return int(round(cx + radius * math.cos(a))), int(round(cz + radius * math.sin(a)))


def annex_center(angle):
    a = math.radians(angle)
    return C + ANNEX_D * math.cos(a), C + ANNEX_D * math.sin(a)


def in_sphere(x, y, z, cx, cz, r):
    return y >= 1 and math.sqrt((x - cx) ** 2 + (y - 1) ** 2 + (z - cz) ** 2) <= r


def in_corridor(x, y, z, angle):
    """Corredor reto do centro da Cúpula até o prédio, ao longo do raio."""
    if not 1 <= y <= CORRIDOR_TOP:
        return False
    a = math.radians(angle)
    ux, uz = math.cos(a), math.sin(a)
    along = (x - C) * ux + (z - C) * uz
    across = -(x - C) * uz + (z - C) * ux
    return DOME_R - 3 <= along <= ANNEX_D - ANNEX_R + 2 and abs(across) <= CORRIDOR_HALF + 0.01


def inside(x, y, z):
    if in_sphere(x, y, z, C, C, DOME_R):
        return True
    for angle, _ in ANNEXES:
        ax, az = annex_center(angle)
        if in_sphere(x, y, z, ax, az, ANNEX_R) or in_corridor(x, y, z, angle):
            return True
    return False


def build_harbor():
    p = Piece(SIZE_XZ, HEIGHT)
    # Plataforma: tijolos de prismarinho por dentro, prismarinho escuro no Cais.
    for x in range(SIZE_XZ):
        for z in range(SIZE_XZ):
            r = math.hypot(x - C, z - C)
            if r <= PLATFORM_R:
                p.put(x, 0, z, "prismarine_bricks" if r <= INNER_R else "dark_prismarine")
    # Cúpula, corredores e prédios: casca de vidro estanque (vizinho de face fora) e ar por dentro.
    for x in range(SIZE_XZ):
        for z in range(SIZE_XZ):
            for y in range(1, HEIGHT):
                if not inside(x, y, z):
                    continue
                shell = any(not inside(x + dx, y + dy, z + dz)
                            for dx, dy, dz in ((1, 0, 0), (-1, 0, 0), (0, 1, 0), (0, 0, 1), (0, 0, -1)))
                p.put(x, y, z, "glass" if shell else "air")
    light_domes(p)
    plaza(p)
    for angle, role in ANNEXES:
        ax, az = annex_center(angle)
        if role in WORKSHOPS:
            workshop(p, angle, ax, az, *WORKSHOPS[role])
        elif role == "dorm":
            dorm(p, angle, ax, az)
        else:
            greenhouse(p, angle, ax, az)
    airlock(p)
    quay(p)
    # Jigsaw no topo da peça (fora da casca): puxa a Torre de Vigia para a superfície.
    props, nbt = jigsaw("aquamarine:dome_top", "aquamarine:tower_base", "aquamarine:harbor/tower", "minecraft:water", "up_north")
    p.put(C, HEIGHT - 1, C, "jigsaw", props, nbt)
    return p


def light_domes(p):
    """Lanternas do mar na casca de cada cúpula e no piso em volta do Sino."""
    domes = [(C, C, DOME_R, 45)] + [(*annex_center(a), ANNEX_R, 90) for a, _ in ANNEXES]
    for cx, cz, r, step in domes:
        for angle in range(0, 360, step):
            x, z = polar(r - 0.5, angle, cx, cz)
            for y in range(3, 6):
                if p.get(x, y, z) == "minecraft:glass":
                    p.put(x, y, z, "sea_lantern")
                    break
    for angle in range(0, 360, 45):
        x, z = polar(6, angle + 22)
        p.put(x, 0, z, "sea_lantern")


def plaza(p):
    """Praça da Cúpula Central: Sino do Porto no centro, bancos virados para ele."""
    p.put(C, 1, C, "bell", {"attachment": "floor", "facing": "north", "powered": "false"})
    for facing, (dx, dz) in (("south", (0, -3)), ("north", (0, 3)), ("east", (-3, 0)), ("west", (3, 0))):
        for side in (-1, 1):
            sx, sz = (C + dx + side, C + dz) if dx == 0 else (C + dx, C + dz + side)
            p.put(sx, 1, sz, "dark_oak_stairs", {"facing": facing, "half": "bottom", "shape": "straight", "waterlogged": "false"})


def workshop(p, angle, ax, az, block, profession):
    """Oficina: bloco de trabalho e baú no fundo do prédio e 3 Mergulhadores em volta do centro."""
    x, z = polar(3, angle, ax, az)
    p.put(x, 1, z, block)
    cx, cz = polar(3, angle + 50, ax, az)
    p.put(cx, 1, cz, "chest", {"facing": "north", "type": "single", "waterlogged": "false"},
          {"id": "minecraft:chest", "LootTable": LOOT})
    for offset in (100, 180, 260):
        vx, vz = polar(2, angle + offset, ax, az)
        villager(p, vx, 1, vz, profession)


def axes(angle):
    """Eixo (x ou z) e sentido mais perto do raio do prédio, e o eixo perpendicular a ele."""
    a = math.radians(angle)
    if abs(math.cos(a)) >= abs(math.sin(a)):
        return (1 if math.cos(a) > 0 else -1, 0), (0, 1)
    return (0, 1 if math.sin(a) > 0 else -1), (1, 0)


def dorm(p, angle, ax, az):
    """Dormitórios: 6 camas dos dois lados do corredor de entrada e um baú no fundo."""
    bx, bz = int(round(ax)), int(round(az))
    (rx, rz), (px, pz) = axes(angle)
    facing = {(1, 0): "east", (-1, 0): "west", (0, 1): "south", (0, -1): "north"}
    for along in (-2, 0, 2):
        for side in (-1, 1):
            head = (bx + rx * along + px * side * 4, bz + rz * along + pz * side * 4)
            foot = (bx + rx * along + px * side * 3, bz + rz * along + pz * side * 3)
            props = {"facing": facing[(px * side, pz * side)], "occupied": "false"}
            p.put(head[0], 1, head[1], "cyan_bed", dict(props, part="head"))
            p.put(foot[0], 1, foot[1], "cyan_bed", dict(props, part="foot"))
    p.put(bx + rx * 4, 1, bz + rz * 4, "chest", {"facing": facing[(-rx, -rz)], "type": "single", "waterlogged": "false"},
          {"id": "minecraft:chest", "LootTable": LOOT})


def greenhouse(p, angle, ax, az):
    """Estufa: dois tanques de vidro fechados (água, algas e pepinos-do-mar) dos dois lados da entrada e
    um baú no fundo. Fechados, a água das algas não vaza para o ar."""
    bx, bz = int(round(ax)), int(round(az))
    (rx, rz), (px, pz) = axes(angle)
    for side in (-1, 1):
        tx, tz = bx + px * side * 3, bz + pz * side * 3
        for x in range(tx - 2, tx + 3):
            for z in range(tz - 2, tz + 3):
                for y in range(1, 5):
                    wall = abs(x - tx) == 2 or abs(z - tz) == 2 or y == 4
                    p.put(x, y, z, "glass" if wall else "water")
        p.put(tx, 0, tz, "sand")
        p.put(tx, 1, tz, "kelp_plant")
        p.put(tx, 2, tz, "kelp_plant")
        p.put(tx, 3, tz, "kelp", {"age": "20"})
        for dx, dz in ((-1, -1), (1, 1)):
            p.put(tx + dx, 1, tz + dz, "sea_pickle", {"pickles": "3", "waterlogged": "true"})
    p.put(bx + rx * 4, 1, bz + rz * 4, "chest", {"facing": "north", "type": "single", "waterlogged": "false"},
          {"id": "minecraft:chest", "LootTable": LOOT})


def airlock(p):
    """Eclusa (norte): túnel de prismarinho com porta de fora e porta de dentro; o ar entre elas não
    deixa a água entrar."""
    outer, inner = C - DOME_R - 10, C - DOME_R - 6
    for z in range(outer, C - DOME_R + 3):
        for x in range(C - 2, C + 3):
            for y in range(0, 5):
                wall = x in (C - 2, C + 2) or y in (0, 4)
                p.put(x, y, z, "prismarine_bricks" if wall else "air")
    for z in (outer, inner):
        for x in (C - 1, C + 1):
            for y in (1, 2, 3):
                p.put(x, y, z, "prismarine_bricks")
        p.put(C, 3, z, "prismarine_bricks")
        p.put(C, 1, z, "spruce_door", {"facing": "south", "half": "lower", "hinge": "left", "open": "false", "powered": "false"})
        p.put(C, 2, z, "spruce_door", {"facing": "south", "half": "upper", "hinge": "left", "open": "false", "powered": "false"})
    p.put(C, 1, (outer + inner) // 2, "sea_lantern")


def quay(p):
    """Cais: anel de lanternas, 6 docas com postes de amarrar (baleia e barcos) e os elevadores de bolhas."""
    for angle in range(0, 360, 15):
        x, z = polar(INNER_R + 1.5, angle)
        p.put(x, 0, z, "sea_lantern")
    fence = {"east": "false", "north": "false", "south": "false", "west": "false", "waterlogged": "true"}
    for angle in DOCKS:
        a = math.radians(angle)
        for step in range(INNER_R + 3, PLATFORM_R + 1):
            for side in (-1, 0, 1):
                x = int(round(C + step * math.cos(a) - side * math.sin(a)))
                z = int(round(C + step * math.sin(a) + side * math.cos(a)))
                p.put(x, 1, z, "prismarine_bricks")
        for side in (-2, 2):
            x = int(round(C + PLATFORM_R * math.cos(a) - side * math.sin(a)))
            z = int(round(C + PLATFORM_R * math.sin(a) + side * math.cos(a)))
            p.put(x, 1, z, "prismarine_bricks")
            p.put(x, 2, z, "dark_oak_fence", fence)
            p.put(x, 3, z, "sea_lantern")
    # Elevadores (ao lado das docas leste e oeste): areia das almas sobe, magma desce. As colunas de
    # bolhas se formam sozinhas até a superfície, qualquer que seja a profundidade.
    for angle in ELEVATORS:
        a = math.radians(angle + 8)
        x, z = int(round(C + 34 * math.cos(a))), int(round(C + 34 * math.sin(a)))
        p.put(x, 0, z, "soul_sand")
        dx, dz = (0, 1) if abs(math.cos(a)) > 0.5 else (1, 0)
        p.put(x + 2 * dx, 0, z + 2 * dz, "magma_block")
        for fx, fz in ((x - dz, z - dx), (x + dz, z + dx), (x + 2 * dx - dz, z + 2 * dz - dx), (x + 2 * dx + dz, z + 2 * dz + dx)):
            p.put(fx, 1, fz, "prismarine_wall", {"east": "none", "north": "none", "south": "none", "up": "true",
                                                 "waterlogged": "true", "west": "none"})


def villager(p, x, y, z, profession):
    p.entities.append({
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


# ---------------------------------------------------------------- Torre de Vigia (peça da superfície)

TOWER_XZ = 9
TOWER_H = 12
T = 4  # centro da torre


def build_tower():
    """Torre de Vigia: deque flutuante 9 x 9 na linha d'água, torre 5 x 5 com escada, varanda e farol."""
    p = Piece(TOWER_XZ, TOWER_H)
    # Deque na linha d'água (y = 0) com um buraco no meio para subir nadando.
    for x in range(TOWER_XZ):
        for z in range(TOWER_XZ):
            if max(abs(x - T), abs(z - T)) <= 4 and (x, z) != (T, T):
                p.put(x, 0, z, "dark_prismarine")
    props, nbt = jigsaw("aquamarine:tower_base", "minecraft:empty", "minecraft:empty", "minecraft:air", "down_north")
    p.put(T, 0, T, "jigsaw", props, nbt)
    # Corpo da torre: paredes 5 x 5 de tijolos, janelas, porta ao sul e escada por dentro.
    for y in range(1, 7):
        for x in range(T - 2, T + 3):
            for z in range(T - 2, T + 3):
                edge = abs(x - T) == 2 or abs(z - T) == 2
                corner = abs(x - T) == 2 and abs(z - T) == 2
                if not edge:
                    p.put(x, y, z, "air")
                elif corner or y in (1, 6) or not (y in (3, 4) and (x == T or z == T)):
                    p.put(x, y, z, "prismarine_bricks")
                else:
                    p.put(x, y, z, "glass_pane", {"east": "false", "north": "false", "south": "false",
                                                  "waterlogged": "false", "west": "false"})
    p.put(T, 1, T + 2, "spruce_door", {"facing": "north", "half": "lower", "hinge": "left", "open": "false", "powered": "false"})
    p.put(T, 2, T + 2, "spruce_door", {"facing": "north", "half": "upper", "hinge": "left", "open": "false", "powered": "false"})
    for y in range(1, 8):
        p.put(T, y, T - 1, "ladder", {"facing": "south", "waterlogged": "false"})
    # Varanda (y = 7) com parapeito e alçapão para a escada.
    for x in range(T - 3, T + 4):
        for z in range(T - 3, T + 4):
            if (x, z) == (T, T - 1):
                continue
            p.put(x, 7, z, "dark_prismarine")
            if max(abs(x - T), abs(z - T)) == 3:
                p.put(x, 8, z, "prismarine_wall", {"east": "none", "north": "none", "south": "none", "up": "true",
                                                   "waterlogged": "false", "west": "none"})
    p.put(T, 8, T - 1, "air")
    # Farol: lanterna do mar com vidro, teto e para-raios (a torre vê as tempestades).
    p.put(T, 8, T, "sea_lantern")
    for dx, dz in ((1, 0), (-1, 0), (0, 1)):
        p.put(T + dx, 8, T + dz, "glass")
    p.put(T, 9, T, "sea_lantern")
    for x in range(T - 1, T + 2):
        for z in range(T - 1, T + 2):
            p.put(x, 10, z, "dark_prismarine_slab", {"type": "bottom", "waterlogged": "false"})
    p.put(T, 11, T, "lightning_rod", {"facing": "up", "powered": "false", "waterlogged": "false"})
    return p


def main():
    harbor = build_harbor()
    blocks, states, divers = harbor.save("porto.nbt")
    print(f"porto.nbt: {blocks} blocos, {states} estados, {divers} Mergulhadores")
    bases = sorted(pos for pos, block in harbor.blocks.items() if block[0] in ("minecraft:soul_sand", "minecraft:magma_block"))
    print(f"bases dos elevadores (HarborElevators.BASES): {bases}")
    blocks, states, _ = build_tower().save("torre.nbt")
    print(f"torre.nbt: {blocks} blocos, {states} estados")


if __name__ == "__main__":
    main()
