"""Redes da indústria (0.6.1, faixa 7C; docs/12-industria.md): arte e dados do Cabo, Cabo Reforçado, Cano, Cano Reforçado,
Tubo de Ar, Válvula e Visor de cada rede, Chave de Maré e Lingote de Latão. O Java fica em com.aquamarine.network.

    C:\\Python314\\python tools/gen_industry_networks.py [--preview]

Texturas de 32 px desenhadas como uma face de bloco inteira: os modelos usam a UV automática (posição do elemento), então
cada peça pega a área certa da textura na mesma densidade das máquinas (2 px por pixel de bloco). O vidro é translúcido
("force_translucent"). Cores do anel de ligação: azul (energia), verde-água (fluido), cinza-claro (gás).
"""
import os
import sys

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
sys.path.insert(0, os.path.join(HERE, "models", "machines"))
import gen_phase4_building as bld  # noqa: E402
import machine_kit as mk  # noqa: E402
from machine_kit import BRASS, COPPER, CYAN, GLASS, MAGMA, PATINA, RING_ENERGY, RING_FLUID, RING_GAS, STEAM, STEEL  # noqa: E402

NS = "aquamarine"
ASSETS = bld.ASSETS
DATA = bld.DATA
AURIUM = mk.ramp("3b3122", "6f6247", "a99c74", "d3c897", "ece2bb", "fbf6e8")  # Aurium perolado das peças reforçadas
CLEAR = (0, 0, 0, 0)
SIZE = 32


# ================================================================ desenho

class Img:
    def __init__(self, w=SIZE, h=SIZE, fill=CLEAR):
        self.img = Image.new("RGBA", (w, h), fill)
        self.px = self.img.load()
        self.w, self.h = w, h

    def put(self, x, y, c):
        if 0 <= x < self.w and 0 <= y < self.h:
            self.px[x, y] = c

    def rect(self, x0, y0, x1, y1, c):
        for y in range(max(0, y0), min(self.h, y1)):
            for x in range(max(0, x0), min(self.w, x1)):
                self.px[x, y] = c

    def frame(self, x0, y0, x1, y1, light, dark):
        for x in range(x0, x1):
            self.put(x, y0, light)
            self.put(x, y1 - 1, dark)
        for y in range(y0, y1):
            self.put(x0, y, light)
            self.put(x1 - 1, y, dark)


def plate(R, base=3, lo=0, hi=SIZE):
    """Chapa: tom de base, chanfro claro em cima e à esquerda, escuro embaixo e à direita, degradê do alto."""
    t = Img()
    for y in range(SIZE):
        for x in range(SIZE):
            lv = base + (1 if y < 8 else -1 if y > 25 else 0)
            t.put(x, y, R[max(0, min(5, lv))])
    return t


def rivet(t, x, y, R):
    t.put(x, y, R[5])
    t.put(x + 1, y, R[4])
    t.put(x, y + 1, R[3])
    t.put(x + 1, y + 1, R[1])


def coupling(R, accent=None):
    """Luva de ligação (o miolo 7x7 fica nos px 9 a 22): moldura, parafusos nos cantos e uma faixa de cor no meio."""
    t = plate(R)
    t.frame(9, 9, 23, 23, R[5], R[0])
    t.frame(10, 10, 22, 22, R[4], R[1])
    for x, y in ((11, 11), (19, 11), (11, 19), (19, 19)):
        rivet(t, x, y, R)
    if accent:
        for x in range(13, 19):
            t.put(x, 15, accent[3])
            t.put(x, 16, accent[2])
    for x in range(0, SIZE, 8):  # rebites nas bordas (aparecem nos braços largos das válvulas)
        rivet(t, x + 3, 1, R)
        rivet(t, x + 3, SIZE - 3, R)
    return t.img


def glass(R, alpha, streak=True):
    """Vidro translúcido: corpo claro, bordas mais densas a cada 16 px e reflexos em diagonal."""
    t = Img()
    for y in range(SIZE):
        for x in range(SIZE):
            edge = x % 16 in (0, 15) or y % 16 in (0, 15)
            c = R[2] if edge else R[3]
            t.put(x, y, c[:3] + (min(255, alpha + (70 if edge else 0)),))
    if streak:
        for k in range(SIZE):
            for off, a, lv in ((3, 190, 5), (4, 140, 4), (11, 120, 5)):
                x = (off + SIZE - 1 - k) % SIZE
                t.put(x, k, R[lv][:3] + (a,))
    return t.img


def frosted():
    """Vidro fosco do Tubo de Ar: branco-azulado leitoso com faixas leves na horizontal."""
    t = Img()
    for y in range(SIZE):
        for x in range(SIZE):
            lv = 3 + (1 if y % 6 < 2 else 0)
            t.put(x, y, STEAM[lv][:3] + (120 + (40 if x % 16 in (0, 15) else 0),))
    for k in range(SIZE):
        t.put((5 + SIZE - 1 - k) % SIZE, k, STEAM[5][:3] + (200,))
    return t.img


def wire(R):
    """Fio trançado: fios em diagonal alternando três tons."""
    t = Img()
    for y in range(SIZE):
        for x in range(SIZE):
            k = (x + y) % 6
            t.put(x, y, R[4] if k == 0 else R[3] if k < 3 else R[2])
    return t.img


def ring(R, core):
    """Anel de ligação (centro do bloco, raio 8 px): aro na cor da rede, aro de latão e o furo com a cor do conteúdo."""
    t = Img()
    for y in range(SIZE):
        for x in range(SIZE):
            d = ((x - 15.5) ** 2 + (y - 15.5) ** 2) ** 0.5
            light = x + y < 31
            if d < 4.5:
                c = core[3] if (x + y) % 2 else core[4]
            elif d < 6.5:
                c = BRASS[4] if light else BRASS[2]
            elif d < 9.5:
                c = R[4] if light else R[2]
                if 8.5 <= d:
                    c = R[1]
            else:
                c = R[3]  # borda do anel (lados finos da caixa)
            t.put(x, y, c)
    return t.img


def lever():
    t = plate(COPPER, 3)
    for x in range(SIZE):
        t.put(x, 0, COPPER[5])
        t.put(x, SIZE - 1, COPPER[1])
    rivet(t, 15, 15, BRASS)
    return t.img


def window_frame():
    t = plate(BRASS, 3)
    t.frame(0, 0, SIZE, SIZE, BRASS[5], BRASS[0])
    for x in range(4, SIZE, 8):
        rivet(t, x, 2, BRASS)
        rivet(t, x, SIZE - 4, BRASS)
    for p in ((2, 2), (SIZE - 4, SIZE - 4)):
        t.put(p[0], p[1], PATINA[3])
    return t.img


def arc():
    """Quatro quadros de arco elétrico (32 x 32 cada, um embaixo do outro): zigue-zague ciano com miolo branco."""
    t = Img(SIZE, SIZE * 4)
    paths = ((16, (3, -4, 5, -3, 4, -5, 2, -2)), (14, (-3, 5, -4, 2, -5, 4, -2, 3)), (17, (4, 2, -5, -3, 5, -2, -4, 3)),
             (15, (-2, -4, 4, 5, -3, -5, 3, 2)))
    for f, (x0, steps) in enumerate(paths):
        x, y = x0, 0
        for s in steps:
            for k in range(4):
                xi = x + round(s * k / 4)
                for dx, c in ((0, (235, 255, 255, 255)), (-1, CYAN[4][:3] + (190,)), (1, CYAN[4][:3] + (190,)),
                              (-2, CYAN[3][:3] + (90,)), (2, CYAN[3][:3] + (90,))):
                    if t.px[max(0, min(31, xi + dx)), f * SIZE + y + k][3] < c[3]:
                        t.put(xi + dx, f * SIZE + y + k, c)
            x += s
            y += 4
        t.put(x0, f * SIZE, (255, 255, 255, 255))
    return t.img


def wisp():
    """Fiapos de ar (tile na vertical): traços finos e translúcidos, com folga entre eles."""
    t = Img()
    for col, start, length in ((4, 2, 11), (11, 18, 9), (19, 6, 13), (26, 22, 8), (15, 27, 10)):
        for k in range(length):
            y = (start + k) % SIZE
            a = int(200 * (1 - abs(k - length / 2) / (length / 2 + 1)))
            t.put(col, y, STEAM[5][:3] + (a,))
            t.put(col + 1, y, STEAM[3][:3] + (a // 2,))
    return t.img


def port_marks():
    """Marcas do modo do conector (16 px cada): sai (seta âmbar), entra (seta ciano), desligado (X vermelho)."""
    t = Img(48, 16)
    for i, (c, d) in enumerate(((MAGMA[4], -1), (CYAN[4], 1))):
        x0 = 16 * i
        for k in range(6):  # seta: ponta para fora (sai) ou para dentro (entra)
            y = 4 + k if d < 0 else 11 - k
            for x in range(8 - k, 8 + k):
                t.put(x0 + x, y, c)
        t.rect(x0 + 6, 10 if d < 0 else 2, x0 + 10, 14 if d < 0 else 6, c)
        t.frame(x0, 0, x0 + 16, 16, BRASS[4], BRASS[1])
    for k in range(12):
        for dx in (0, 1):
            t.put(34 + k + dx, 2 + k, (210, 40, 40, 255))
            t.put(45 - k - dx, 2 + k, (210, 40, 40, 255))
    t.frame(32, 0, 48, 16, BRASS[4], BRASS[1])
    return t.img


def item_wrench():
    """Chave de Maré, 16 px: boca de latão em cima à direita, cabo de couro descendo para a esquerda."""
    t = Img(16, 16)
    dark = (40, 26, 12, 255)
    leather = mk.LEATHER
    for k in range(9):  # cabo de couro
        x, y = 2 + k, 13 - k
        t.put(x, y, leather[3] if k % 3 else leather[4])
        t.put(x + 1, y, leather[2])
        t.put(x, y + 1, dark)
    for x, y in ((10, 2), (11, 1), (12, 1), (13, 2), (14, 3), (14, 4), (13, 5), (11, 4), (10, 3), (9, 4), (10, 5), (11, 6), (12, 6)):
        t.put(x, y, BRASS[4])
    for x, y in ((12, 2), (13, 3), (12, 3)):
        t.put(x, y, CLEAR)
    for x, y in ((10, 2), (11, 1), (9, 4)):
        t.put(x, y, BRASS[5])
    for x, y in ((14, 4), (13, 5), (12, 6)):
        t.put(x, y, BRASS[2])
    t.put(4, 11, PATINA[3])
    return mk_outline(t.img)


def item_ingot():
    """Lingote de Latão, 16 px, na mesma pose dos lingotes do jogo."""
    t = Img(16, 16)
    for y in range(5, 11):
        for x in range(2 + (10 - y) // 2, 14 - (y - 5) // 2):
            lv = 4 if y == 5 else 3 if y < 8 else 2
            t.put(x, y, BRASS[lv])
    for x in range(5, 12):
        t.put(x, 5, BRASS[5])
    t.put(6, 7, BRASS[5])
    t.put(10, 9, BRASS[1])
    return mk_outline(t.img)


def mk_outline(img, dark=(28, 18, 8, 255)):
    px = img.load()
    out = img.copy()
    po = out.load()
    for y in range(img.height):
        for x in range(img.width):
            if px[x, y][3] == 0 and any(0 <= x + dx < img.width and 0 <= y + dy < img.height and px[x + dx, y + dy][3] > 0
                                        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                po[x, y] = dark
    return out


TEXTURES = {
    "block/conduit/brass_coupling": lambda: coupling(BRASS),
    "block/conduit/aurium_coupling": lambda: coupling(AURIUM, CYAN),
    "block/conduit/steel_coupling": lambda: coupling(STEEL),
    "block/conduit/cable_coupling": lambda: coupling(STEEL, RING_ENERGY),
    "block/conduit/pipe_glass": lambda: glass(GLASS, 72),
    "block/conduit/cable_sheath": lambda: glass(CYAN, 64),
    "block/conduit/tube_glass": frosted,
    "block/conduit/copper_wire": lambda: wire(COPPER),
    "block/conduit/aurium_wire": lambda: wire(AURIUM),
    "block/conduit/ring_energy": lambda: ring(RING_ENERGY, RING_ENERGY),
    "block/conduit/ring_fluid": lambda: ring(RING_FLUID, CYAN),
    "block/conduit/ring_gas": lambda: ring(RING_GAS, STEAM),
    "block/conduit/valve_lever": lever,
    "block/conduit/window_frame": window_frame,
    "block/conduit/arc": arc,
    "block/conduit/air_wisp": wisp,
    "block/conduit/port_marks": port_marks,
    "item/tide_wrench": item_wrench,
    "item/brass_ingot": item_ingot,
}
GLASSY = {"pipe_glass", "cable_sheath", "tube_glass"}


# ================================================================ modelos

def tex(name):
    sprite = f"{NS}:block/conduit/{name}"
    return {"sprite": sprite, "force_translucent": True} if name in GLASSY else sprite


def el(frm, to, faces, texture):
    return {"from": list(frm), "to": list(to), "faces": {f: {"texture": f"#{texture}"} for f in faces}}


SIDES4 = ("east", "west", "up", "down")
ALL6 = ("north", "south", "east", "west", "up", "down")


def mirror_z(e):
    """Mesmo elemento do outro lado do bloco (z vira 16 - z): braço sul a partir do braço norte."""
    f, t = e["from"], e["to"]
    faces = {({"north": "south", "south": "north"}.get(k, k)): v for k, v in e["faces"].items()}
    return {"from": [f[0], f[1], 16 - t[2]], "to": [t[0], t[1], 16 - f[2]], "faces": faces}


# Cada pedaço: luva, conteúdo do braço (vidro e/ou fio) e anel. Cabos levam o fio de cobre dentro da capa de vidro.
PIPES = {
    "aquatic_cable": dict(coupling="cable_coupling", glass="cable_sheath", wire="copper_wire", wire_r=1, ring="ring_energy", sheath=2.5),
    "reinforced_aquatic_cable": dict(coupling="aurium_coupling", glass="cable_sheath", wire="aurium_wire", wire_r=1.5, ring="ring_energy", sheath=2.5),
    "fluid_pipe": dict(coupling="brass_coupling", glass="pipe_glass", ring="ring_fluid", sheath=3),
    "reinforced_fluid_pipe": dict(coupling="aurium_coupling", glass="pipe_glass", ring="ring_fluid", sheath=3),
    "air_tube": dict(coupling="steel_coupling", glass="tube_glass", ring="ring_gas", sheath=3),
}
INLINE = {  # peça em linha: (rede, válvula?)
    "cable_switch": ("aquatic_cable", True), "cable_window": ("aquatic_cable", False),
    "pipe_valve": ("fluid_pipe", True), "pipe_window": ("fluid_pipe", False),
    "air_tube_valve": ("air_tube", True), "air_tube_window": ("air_tube", False),
}


def arm(spec, z0, z1):
    """Braço para o norte de z0 a z1: capa (vidro) e, nos cabos, o fio."""
    s = spec["sheath"]
    out = [el((8 - s, 8 - s, z0), (8 + s, 8 + s, z1), SIDES4, "glass")]
    if "wire" in spec:
        r = spec["wire_r"]
        out.insert(0, el((8 - r, 8 - r, z0), (8 + r, 8 + r, z1), SIDES4, "wire"))
    return out


def textures(spec, extra=None):
    t = {"coupling": tex(spec["coupling"]), "glass": tex(spec["glass"]), "ring": tex(spec["ring"]), "particle": tex(spec["coupling"])}
    if "wire" in spec:
        t["wire"] = tex(spec["wire"])
    t.update(extra or {})
    return t


def pipe_models(name, spec):
    core = [el((4.5, 4.5, 4.5), (11.5, 11.5, 11.5), ALL6, "coupling")]
    port = arm(spec, 1, 4.5) + [el((4, 4, 0), (12, 12, 1), ALL6, "ring")]
    models = {f"conduit/{name}_core": core, f"conduit/{name}_arm": arm(spec, 0, 4.5), f"conduit/{name}_port": port}
    out = {k: {"parent": "minecraft:block/block", "textures": textures(spec), "elements": v} for k, v in models.items()}
    straight = core + arm(spec, 0, 4.5) + [mirror_z(e) for e in arm(spec, 0, 4.5)]
    out[f"conduit/{name}_item"] = {"parent": "minecraft:block/block", "textures": textures(spec), "elements": straight,
                                    "display": {"gui": {"rotation": [30, 45, 0], "scale": [0.75, 0.75, 0.75]}}}
    return out


def inline_models(name, base, valve):
    spec = PIPES[base]
    body = "coupling"
    els = arm(spec, 0, 3.5) + [mirror_z(e) for e in arm(spec, 0, 3.5)]
    els += [el((3.5, 3.5, 2.5), (12.5, 12.5, 3.5), ALL6, "ring"), el((3.5, 3.5, 12.5), (12.5, 12.5, 13.5), ALL6, "ring")]
    extra = {}
    out = {}
    if valve:
        els += [el((4, 4, 3.5), (12, 12, 12.5), ALL6, body), el((7.5, 12, 7.5), (8.5, 14, 8.5), ALL6, body)]
        extra["lever"] = tex("valve_lever")
        for state, lev in (("open", ((7, 14, 3.5), (9, 15, 12.5))), ("closed", ((3.5, 14, 7), (12.5, 15, 9)))):
            out[f"conduit/{name}_{state}"] = {"parent": "minecraft:block/block", "textures": textures(spec, extra),
                                             "elements": els + [el(lev[0], lev[1], ALL6, "lever")]}
    else:
        extra["frame"] = tex("window_frame")
        window = [el((4, 4, 3.5), (12, 12, 12.5), SIDES4, "glass")]
        for x, y in ((3.5, 3.5), (11.5, 3.5), (3.5, 11.5), (11.5, 11.5)):
            window.append(el((x, y, 3.5), (x + 1, y + 1, 12.5), ALL6, "frame"))
        out[f"conduit/{name}"] = {"parent": "minecraft:block/block", "textures": textures(spec, extra), "elements": els + window}
    return out


AXIS_ROT = {"x": {"y": 90}, "y": {"x": 90}, "z": {}}
DIR_ROT = (("north", {}), ("east", {"y": 90}), ("south", {"y": 180}), ("west", {"y": 270}), ("up", {"x": 270}), ("down", {"x": 90}))


# ================================================================ dados

NAMES = {
    "aquatic_cable": ("Cabo Aquático", "Aquatic Cable", "Leva energia entre geradores e máquinas: 256 E/t em cada saída.",
                      "Carries energy between generators and machines: 256 E/t per output."),
    "reinforced_aquatic_cable": ("Cabo Aquático Reforçado", "Reinforced Aquatic Cable", "Fio de Aurium: 2048 E/t em cada saída.",
                                 "Aurium wire: 2048 E/t per output."),
    "cable_switch": ("Interruptor de Cabo", "Cable Switch", "Liga e desliga a passagem da energia com um clique.",
                     "Turns the flow of energy on and off with a click."),
    "cable_window": ("Visor de Cabo", "Cable Window", "Trecho de vidro largo: mostra a carga passando.", "Wide glass section: shows the charge going through."),
    "fluid_pipe": ("Cano", "Pipe", "Leva água ou lava, 1 balde por segundo em cada saída. Não mistura dois líquidos.",
                   "Carries water or lava, 1 bucket per second per output. Never mixes two liquids."),
    "reinforced_fluid_pipe": ("Cano Reforçado", "Reinforced Pipe", "Luvas de Aurium: 4 baldes por segundo em cada saída.",
                              "Aurium couplings: 4 buckets per second per output."),
    "pipe_valve": ("Válvula de Cano", "Pipe Valve", "Abre e fecha o Cano com um clique.", "Opens and closes the Pipe with a click."),
    "pipe_window": ("Visor de Cano", "Pipe Window", "Mostra o líquido passando, ampliado.", "Shows the liquid flowing through, up close."),
    "air_tube": ("Tubo de Ar", "Air Tube", "Leva Ar Comprimido, 2 baldes de ar por segundo em cada saída.",
                 "Carries Compressed Air, 2 buckets of air per second per output."),
    "air_tube_valve": ("Válvula de Tubo de Ar", "Air Tube Valve", "Abre e fecha o Tubo de Ar com um clique.", "Opens and closes the Air Tube with a click."),
    "air_tube_window": ("Visor de Tubo de Ar", "Air Tube Window", "Mostra o ar passando, ampliado.", "Shows the air flowing through, up close."),
}
RECIPES = {
    "fluid_pipe": ("shaped", (["BGB"], {"B": f"{NS}:brass_ingot", "G": "minecraft:glass"}), 4),
    "reinforced_fluid_pipe": ("shaped", ([" P ", "PAP", " P "], {"P": f"{NS}:fluid_pipe", "A": f"{NS}:aurium_ingot"}), 4),
    "air_tube": ("shaped", (["BPB"], {"B": f"{NS}:brass_ingot", "P": "minecraft:glass_pane"}), 4),
    "reinforced_aquatic_cable": ("shaped", ([" C ", "CAC", " C "], {"C": f"{NS}:aquatic_cable", "A": f"{NS}:aurium_ingot"}), 4),
    "pipe_valve": ("shapeless", [f"{NS}:fluid_pipe", "minecraft:copper_ingot"], 1),
    "air_tube_valve": ("shapeless", [f"{NS}:air_tube", "minecraft:copper_ingot"], 1),
    "cable_switch": ("shapeless", [f"{NS}:aquatic_cable", "minecraft:lever"], 1),
    "pipe_window": ("shapeless", [f"{NS}:fluid_pipe", "minecraft:glass"], 1),
    "air_tube_window": ("shapeless", [f"{NS}:air_tube", "minecraft:glass"], 1),
    "cable_window": ("shapeless", [f"{NS}:aquatic_cable", "minecraft:glass"], 1),
    "tide_wrench": ("shaped", (["B B", " B ", " L "], {"B": f"{NS}:brass_ingot", "L": "minecraft:leather"}), 1),
    "brass_ingot": ("shapeless", ["minecraft:copper_ingot", "minecraft:copper_ingot", "minecraft:copper_ingot", "minecraft:gold_nugget"], 3),
}
LANG = {
    "item.aquamarine.tide_wrench": ("Chave de Maré", "Tide Wrench"),
    "tooltip.aquamarine.tide_wrench": ("Clique num conector: sai, entra ou desligado. Agachado: gira a máquina.",
                                       "Click a connector: out, in or off. Sneaking: turns the machine."),
    "item.aquamarine.brass_ingot": ("Lingote de Latão", "Brass Ingot"),
    "tooltip.aquamarine.brass_ingot": ("Cobre com um toque de ouro: base dos canos e das máquinas.",
                                       "Copper with a touch of gold: the base of pipes and machines."),
    "fluid.aquamarine.compressed_air": ("Ar Comprimido", "Compressed Air"),
    "message.aquamarine.wrench.side": ("Lado %s: %s", "%s side: %s"),
    "message.aquamarine.port.out": ("sai", "out"),
    "message.aquamarine.port.in": ("entra", "in"),
    "message.aquamarine.port.off": ("desligado", "off"),
    "message.aquamarine.side.north": ("norte", "North"), "message.aquamarine.side.south": ("sul", "South"),
    "message.aquamarine.side.east": ("leste", "East"), "message.aquamarine.side.west": ("oeste", "West"),
    "message.aquamarine.side.up": ("de cima", "Top"), "message.aquamarine.side.down": ("de baixo", "Bottom"),
}


def main():
    w, bid = bld.write_json, bld.block_id
    images = {rel: fn() for rel, fn in TEXTURES.items()}
    for rel, img in images.items():
        path = os.path.join(ASSETS, "textures", rel + ".png")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        img.save(path)
    for name, spec in PIPES.items():
        for rel, model in pipe_models(name, spec).items():
            w(model, f"models/block/{rel}.json")
        parts = [{"apply": {"model": bid(f"conduit/{name}_core")}}]
        for side, rot in DIR_ROT:
            parts.append({"when": {side: "pipe"}, "apply": {"model": bid(f"conduit/{name}_arm"), **rot}})
            parts.append({"when": {side: "port"}, "apply": {"model": bid(f"conduit/{name}_port"), **rot}})
        w({"multipart": parts}, f"blockstates/{name}.json")
        w(bld.item_def(bid(f"conduit/{name}_item")), f"items/{name}.json")
    for name, (base, valve) in INLINE.items():
        for rel, model in inline_models(name, base, valve).items():
            w(model, f"models/block/{rel}.json")
        variants = {}
        for axis, rot in AXIS_ROT.items():
            if valve:
                for state in ("open", "closed"):
                    variants[f"axis={axis},open={'true' if state == 'open' else 'false'}"] = {"model": bid(f"conduit/{name}_{state}"), **rot}
            else:
                variants[f"axis={axis}"] = {"model": bid(f"conduit/{name}"), **rot}
        w({"variants": variants}, f"blockstates/{name}.json")
        w(bld.item_def(bid(f"conduit/{name}_open" if valve else f"conduit/{name}")), f"items/{name}.json")
    for old in ("aquatic_cable_core", "aquatic_cable_arm", "aquatic_cable_item"):  # modelos do cabo da Fase 6
        path = os.path.join(ASSETS, "models", "block", old + ".json")
        if os.path.exists(path):
            os.remove(path)
    for item in ("tide_wrench", "brass_ingot"):
        w({"parent": "minecraft:item/handheld" if item == "tide_wrench" else "minecraft:item/generated",
           "textures": {"layer0": f"{NS}:item/{item}"}}, f"models/item/{item}.json")
        w(bld.item_def(f"{NS}:item/{item}"), f"items/{item}.json")

    blocks = list(NAMES)
    for name in blocks:
        w(bld.self_drop(name), f"{NS}/loot_table/blocks/{name}.json", DATA)
    for name, (kind, body, count) in RECIPES.items():
        recipe = bld.shaped(name, body[0], body[1], count) if kind == "shaped" else bld.shapeless(name, body, count)
        recipe["category"] = "redstone" if name in NAMES else "misc"
        w(recipe, f"{NS}/recipe/{name}.json", DATA)
    bld.merge_tag("minecraft/tags/block/mineable/pickaxe.json", [f"{NS}:{n}" for n in blocks])

    lang = dict(LANG)
    for name, (pt, en, tip_pt, tip_en) in NAMES.items():
        lang[f"block.{NS}.{name}"] = (pt, en)
        lang[f"tooltip.{NS}.{name}"] = (tip_pt, tip_en)
    bld.merge_lang(lang)

    if "--preview" in sys.argv:
        tiles = [img.resize((img.width * 4, img.height * 4), Image.NEAREST) for img in images.values()]
        sheet = Image.new("RGBA", (sum(t.width + 6 for t in tiles), max(t.height for t in tiles)), (30, 34, 44, 255))
        x = 0
        for t in tiles:
            sheet.alpha_composite(t, (x, 0))
            x += t.width + 6
        sheet.save(os.path.join(HERE, "..", "build", "networks_preview.png"))
    print(f"redes (7C): {len(images)} texturas, {len(blocks)} blocos, {len(RECIPES)} receitas")


if __name__ == "__main__":
    main()
