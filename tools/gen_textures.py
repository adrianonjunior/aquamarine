"""Gera todas as texturas do Aquamarine em pixel art (estilo mods de Minecraft, 16x16).

Uso (na raiz do repositório):
    C:\\Python314\\python tools/gen_textures.py            # gera tudo
    C:\\Python314\\python tools/gen_textures.py --preview  # também salva build/texture_preview.png

Como funciona:
- Cada sprite é uma grade ASCII. Letras MAIÚSCULAS são "regiões" sombreadas sozinhas
  (bisel: luz em cima/esquerda, sombra embaixo/direita) com a rampa de cores da região.
  Minúsculas e dígitos são cores fixas da paleta.
- Depois do sombreamento, todo pixel transparente vizinho de um pixel pintado vira contorno
  (a cor mais escura da rampa do vizinho). Por isso as grades desenham só o "miolo".
- Ruído usa semente fixa: rodar de novo gera os mesmos PNGs.

Paleta do mod (docs/01-visao-e-riscos.md): azul profundo, verde-água, roxo
bioluminescente e dourado gasto do Oricálcio.
"""
import math
import os
import random
import sys

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.normpath(os.path.join(HERE, ".."))
TEX = os.path.join(REPO, "src", "main", "resources", "assets", "aquamarine", "textures")


def hexc(h, a=255):
    h = h.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), a)


# Rampas: [contorno, sombra funda, sombra, base, luz, brilho]
RAMPS = {
    "G": [hexc("3a240e"), hexc("6e4a1a"), hexc("9a6c26"), hexc("c79634"), hexc("e8c25a"), hexc("fff0a8")],  # Oricálcio
    "T": [hexc("0c3436"), hexc("17605e"), hexc("238579"), hexc("33aa96"), hexc("6fdcc4"), hexc("c4fff0")],  # verde-água
    "P": [hexc("1e1036"), hexc("362064"), hexc("50348f"), hexc("7552c8"), hexc("a888ff"), hexc("e6d8ff")],  # roxo
    "B": [hexc("060d18"), hexc("0d1b30"), hexc("142a48"), hexc("1d3d66"), hexc("2c5a8e"), hexc("4a86bf")],  # azul profundo
    "H": [hexc("1f140a"), hexc("3a2614"), hexc("54371d"), hexc("6e4a27"), hexc("8d6234"), hexc("a87b46")],  # cabo
    "S": [hexc("0f2e2a"), hexc("1b4d44"), hexc("2a6e5f"), hexc("3f9179"), hexc("6cbf98"), hexc("b9eccb")],  # escama
}

# Cores fixas (minúsculas e dígitos)
FIXED = {
    ".": (0, 0, 0, 0),
    "k": RAMPS["G"][0], "1": RAMPS["G"][1], "2": RAMPS["G"][2], "3": RAMPS["G"][3], "4": RAMPS["G"][4], "5": RAMPS["G"][5],
    "t": RAMPS["T"][2], "u": RAMPS["T"][3], "v": RAMPS["T"][4], "y": RAMPS["T"][5],
    "p": RAMPS["P"][2], "q": RAMPS["P"][3], "r": RAMPS["P"][4], "s": RAMPS["P"][5],
    "a": RAMPS["B"][1], "b": RAMPS["B"][2], "c": RAMPS["B"][3], "d": RAMPS["B"][4], "e": RAMPS["B"][5],
    "h": RAMPS["H"][2], "j": RAMPS["H"][3], "m": RAMPS["H"][4],
    "w": hexc("ffffff"), "z": hexc("0a0614"),
}
# Contorno usado quando o vizinho é uma cor fixa
FIXED_OUTLINE = {}
for ch in "k12345":
    FIXED_OUTLINE[ch] = RAMPS["G"][0]
for ch in "tuvy":
    FIXED_OUTLINE[ch] = RAMPS["T"][0]
for ch in "pqrs":
    FIXED_OUTLINE[ch] = RAMPS["P"][0]
for ch in "abcde":
    FIXED_OUTLINE[ch] = RAMPS["B"][0]
for ch in "hjm":
    FIXED_OUTLINE[ch] = RAMPS["H"][0]
FIXED_OUTLINE["w"] = RAMPS["T"][0]
FIXED_OUTLINE["z"] = hexc("05030a")


def parse(rows, w=16, h=16):
    grid = [list(r.ljust(w, ".")) for r in rows]
    while len(grid) < h:
        grid.append(["."] * w)
    for y, r in enumerate(grid):
        assert len(r) == w, (y, "".join(r))
    return grid


def render(rows, w=16, h=16, outline=True, bevel_light=4, bevel_shadow=2, base=3):
    """Grade ASCII -> imagem RGBA com bisel por região e contorno automático."""
    g = parse(rows, w, h)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    filled = [[g[y][x] != "." for x in range(w)] for y in range(h)]

    def same(x, y, ch):
        return 0 <= x < w and 0 <= y < h and g[y][x] == ch

    for y in range(h):
        for x in range(w):
            ch = g[y][x]
            if ch == ".":
                continue
            if ch in RAMPS:
                ramp = RAMPS[ch]
                tl = not same(x - 1, y, ch) or not same(x, y - 1, ch)
                br = not same(x + 1, y, ch) or not same(x, y + 1, ch)
                if tl and not br:
                    c = ramp[bevel_light]
                elif br and not tl:
                    c = ramp[bevel_shadow]
                else:
                    c = ramp[base]
                img.putpixel((x, y), c)
            else:
                img.putpixel((x, y), FIXED[ch])
    if outline:
        out = img.copy()
        for y in range(h):
            for x in range(w):
                if filled[y][x]:
                    continue
                for dx, dy in ((0, 1), (1, 0), (0, -1), (-1, 0)):
                    nx, ny = x + dx, y + dy
                    if 0 <= nx < w and 0 <= ny < h and filled[ny][nx]:
                        ch = g[ny][nx]
                        out.putpixel((x, y), RAMPS[ch][0] if ch in RAMPS else FIXED_OUTLINE[ch])
                        break
        img = out
    return img


def save(img, rel):
    path = os.path.join(TEX, rel)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path)
    return img


# ---------------------------------------------------------------- itens: materiais

INGOT = [
    "................",
    "................",
    "................",
    "................",
    "........45......",
    "......4GGG5.....",
    "....4GGGGGGG4...",
    "..4GGGGGGGGGG2..",
    ".5GGGGGGGGGGG2..",
    ".3GGGGGGGGGG2...",
    ".23GGGGGGGG2....",
    "..223GGGGG2.....",
    "....223GG2......",
    "......222.......",
]


def ingot():
    img = render(INGOT)
    # pátina verde-água num canto e brilho no topo
    for x, y, ch in ((9, 11, "t"), (10, 10, "u"), (8, 12, "t"), (7, 5, "5"), (8, 5, "5"), (6, 6, "5")):
        img.putpixel((x, y), FIXED[ch])
    return img


def pearl():
    """Pérola Abissal: esfera escura com reflexo roxo e brilho verde-água na borda."""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    ramp = [hexc("140a26"), hexc("23164a"), hexc("362468"), hexc("52398f"), hexc("7a5cc4"), hexc("ad93f0"), hexc("e8dcff")]
    cx, cy, r = 7.5, 8.0, 5.6
    lx, ly, lz = -0.55, -0.65, 0.52
    for y in range(16):
        for x in range(16):
            dx, dy = (x - cx) / r, (y - cy) / r
            d2 = dx * dx + dy * dy
            if d2 > 1.0:
                continue
            nz = math.sqrt(1 - d2)
            lum = max(0.0, dx * lx + dy * ly + nz * lz)
            idx = min(len(ramp) - 1, 1 + int(lum * 5.2))
            c = ramp[idx]
            rim = dx * 0.7 + dy * 0.7
            if d2 > 0.62 and rim > 0.45:
                c = RAMPS["T"][3] if d2 > 0.8 else RAMPS["T"][2]
            img.putpixel((x, y), c)
    for x, y in ((5, 5), (6, 5), (5, 6)):
        img.putpixel((x, y), hexc("ffffff"))
    img.putpixel((10, 11), hexc("c4fff0"))
    return outline_alpha(img, ramp[0])


def outline_alpha(img, color):
    w, h = img.size
    out = img.copy()
    px = img.load()
    for y in range(h):
        for x in range(w):
            if px[x, y][3]:
                continue
            for dx, dy in ((0, 1), (1, 0), (0, -1), (-1, 0)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < w and 0 <= ny < h and px[nx, ny][3]:
                    out.putpixel((x, y), color)
                    break
    return out


CRYSTAL = [
    "................",
    "........y.......",
    ".......yvu......",
    ".......vvut.....",
    "...y...vvut.....",
    "..yvu..vvut..y..",
    "..vvut.vvut.yvu.",
    "..vvut.vvut.vvut",
    "..vvut.vvut.vvt.",
    "...vut.vvut.vut.",
    "...vutvvvuttvt..",
    "....uuvvvuutt...",
    ".....bcccccb....",
    "....bcdddddcb...",
    ".....aabbbaa....",
]


def crystal():
    img = render(CRYSTAL)
    for x, y in ((8, 2), (8, 4), (3, 6), (13, 7)):
        img.putpixel((x, y), hexc("ffffff"))
    return img


SCALE = [
    "................",
    "................",
    "....SSSSSSSS....",
    "...SSSSSSSSSS...",
    "..SSSSSSSSSSSS..",
    "..SSSSSSSSSSSS..",
    "..SSSSSSSSSSSS..",
    "..SSSSSSSSSSSS..",
    "...SSSSSSSSSS...",
    "...SSSSSSSSSS...",
    "....SSSSSSSS....",
    ".....SSSSSS.....",
    "......SSSS......",
    ".......SS.......",
]


def scale():
    img = render(SCALE)
    ridge = RAMPS["S"][1]
    shine = RAMPS["S"][5]
    # arcos de crescimento (como escama de peixe antiga) e veio central dourado gasto
    for x in range(4, 12):
        img.putpixel((x, 5), ridge)
    for x in range(5, 11):
        img.putpixel((x, 8), ridge)
    for x in range(6, 10):
        img.putpixel((x, 11), ridge)
    for y in range(3, 13):
        img.putpixel((7, y), RAMPS["G"][3] if y % 3 else RAMPS["G"][2])
    for x, y in ((4, 3), (5, 3), (3, 4), (4, 4)):
        img.putpixel((x, y), shine)
    img.putpixel((11, 6), FIXED["r"])
    img.putpixel((10, 9), FIXED["q"])
    return img


# ---------------------------------------------------------------- itens: ferramentas

def put_all(rows, pts, ch):
    for x, y in pts:
        r = list(rows[y])
        r[x] = ch
        rows[y] = "".join(r)


def blank():
    return ["." * 16 for _ in range(16)]


def handle(rows, start, end, colors=("j", "h")):
    """Cabo diagonal de 1 px, de baixo-esquerda para cima-direita, com faixa de couro azul."""
    (x0, y0), (x1, y1) = start, end
    n = x1 - x0
    for i in range(n + 1):
        x, y = x0 + i, y0 - i
        ch = colors[i % 2]
        if i in (2, 3):
            ch = "c" if i == 2 else "b"
        put_all(rows, [(x, y)], ch)


def sword():
    rows = blank()
    # lâmina de 3 px: luz, gume central claro, sombra; ponta brilhante
    put_all(rows, [(14, 1), (15, 0)], "5")
    put_all(rows, [(15, 1)], "3")
    for y in range(2, 10):
        put_all(rows, [(15 - y, y)], "4")
        put_all(rows, [(16 - y, y)], "5" if y % 3 else "4")
        put_all(rows, [(17 - y, y)], "2")
    put_all(rows, [(14, 2), (13, 3), (12, 4)], "5")
    put_all(rows, [(6, 9), (7, 9)], "3")
    # guarda verde-água com pérola no centro
    put_all(rows, [(3, 8), (4, 9), (6, 11), (7, 12)], "u")
    put_all(rows, [(5, 10)], "r")
    # cabo e pomo roxo
    put_all(rows, [(4, 11)], "j")
    put_all(rows, [(3, 12)], "h")
    put_all(rows, [(2, 13)], "c")
    put_all(rows, [(1, 14)], "q")
    return render(rows)


def pickaxe():
    rows = blank()
    handle(rows, (2, 13), (9, 6))
    # cabeça em arco
    head_outer = [(3, 3), (4, 2), (5, 2), (6, 1), (7, 1), (8, 1), (9, 1), (10, 2), (11, 2), (12, 3), (13, 4), (13, 5), (14, 6), (14, 7), (14, 8), (14, 9), (13, 10), (13, 11), (12, 12)]
    head_inner = [(4, 3), (5, 3), (6, 2), (7, 2), (8, 2), (9, 2), (10, 3), (11, 3), (12, 4), (12, 5), (13, 6), (13, 7), (13, 8), (13, 9), (12, 10), (12, 11)]
    put_all(rows, head_outer, "4")
    put_all(rows, head_inner, "2")
    put_all(rows, [(7, 1), (8, 1), (14, 7)], "5")
    put_all(rows, [(3, 3), (12, 12)], "3")
    put_all(rows, [(10, 4), (11, 4), (11, 5), (10, 5)], "3")
    put_all(rows, [(10, 4)], "y")
    return render(rows)


def axe():
    rows = blank()
    handle(rows, (2, 13), (10, 5))
    head = [
        (7, 2), (8, 2), (9, 2),
        (6, 3), (7, 3), (8, 3), (9, 3), (10, 3),
        (5, 4), (6, 4), (7, 4), (8, 4), (9, 4),
        (5, 5), (6, 5), (7, 5), (8, 5),
        (5, 6), (6, 6), (7, 6),
        (6, 7),
    ]
    put_all(rows, head, "3")
    put_all(rows, [(7, 2), (6, 3), (5, 4), (5, 5), (5, 6)], "5")
    put_all(rows, [(8, 2), (7, 3), (6, 4), (6, 5), (6, 6), (6, 7)], "4")
    put_all(rows, [(9, 3), (10, 3), (9, 4), (8, 5), (7, 6)], "2")
    put_all(rows, [(11, 2), (12, 3), (11, 4)], "u")
    put_all(rows, [(12, 2)], "t")
    put_all(rows, [(11, 3)], "r")
    return render(rows)


def shovel():
    rows = blank()
    handle(rows, (2, 13), (9, 6))
    head = [(11, 1), (12, 1), (10, 2), (11, 2), (12, 2), (13, 2), (10, 3), (11, 3), (12, 3), (13, 3), (14, 3),
            (11, 4), (12, 4), (13, 4), (14, 4), (12, 5), (13, 5), (10, 5)]
    put_all(rows, head, "3")
    put_all(rows, [(11, 1), (10, 2), (10, 3), (12, 1)], "5")
    put_all(rows, [(11, 2), (11, 3), (12, 2)], "4")
    put_all(rows, [(14, 3), (14, 4), (13, 5), (12, 5), (13, 4)], "2")
    put_all(rows, [(10, 5), (10, 4)], "u")
    put_all(rows, [(12, 3)], "y")
    return render(rows)


def hoe():
    rows = blank()
    handle(rows, (2, 13), (10, 5))
    put_all(rows, [(7, 2), (8, 2), (9, 2), (10, 2), (11, 2)], "4")
    put_all(rows, [(7, 3), (8, 3), (9, 3), (10, 3), (11, 3)], "2")
    put_all(rows, [(12, 3), (12, 4), (11, 4)], "3")
    put_all(rows, [(7, 2), (8, 2)], "5")
    put_all(rows, [(11, 3)], "u")
    put_all(rows, [(12, 2)], "y")
    return render(rows)


# ---------------------------------------------------------------- itens: armadura

HELMET = [
    "................",
    "................",
    ".......TT.......",
    ".....GGTTGG.....",
    "...GGGGTTGGGG...",
    "..GGGGGGGGGGGG..",
    "..GGGGGGGGGGGG..",
    "..TTTTTTTTTTTT..",
    "..GGG......GGG..",
    "..GGG......GGG..",
    "..GG........GG..",
]

CHEST = [
    "................",
    "..GGG......GGG..",
    ".GGGGG....GGGGG.",
    ".GGGGGGGGGGGGGG.",
    ".GGGGGGTTGGGGGG.",
    "..GGGGTTTTGGGG..",
    "...GGGGTTGGGG...",
    "...GGGGGGGGGG...",
    "...GGGGGGGGGG...",
    "...TTTTTTTTTT...",
    "...GGGGGGGGGG...",
    "...GGGGGGGGGG...",
    "....GGGGGGGG....",
]

LEGS = [
    "................",
    "................",
    "...TTTTTTTTTT...",
    "...GGGGGGGGGG...",
    "...GGGGGGGGGG...",
    "...GGGG..GGGG...",
    "...GGGG..GGGG...",
    "...GGG....GGG...",
    "...GGG....GGG...",
    "...GGG....GGG...",
    "...GGG....GGG...",
    "...TTT....TTT...",
    "...GGG....GGG...",
]

BOOTS = [
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "...TTT....TTT...",
    "...GGG....GGG...",
    "...GGG....GGG...",
    "...GGG....GGG...",
    "..GGGG....GGGG..",
    ".GGGGG....GGGGG.",
    ".GGGGG....GGGGG.",
]


def armor_icon(rows, gem=None):
    img = render(rows)
    if gem:
        for (x, y), ch in gem.items():
            img.putpixel((x, y), FIXED[ch])
    return img


# ---------------------------------------------------------------- óculos

GOGGLE_PAL = {
    ".": (0, 0, 0, 0),
    "O": hexc("14202f"), "B": hexc("e3c36a"), "b": hexc("b08a3c"), "d": hexc("6f5224"),
    "W": hexc("ffffff"), "L": hexc("9af2e8"), "l": hexc("38c2c0"), "m": hexc("1b7c92"), "n": hexc("104a6a"),
    "S": hexc("2f558c"), "s": hexc("1b335c"), "P": hexc("8a5ac8"),
}

GOGGLES_ICON = [
    "................",
    "................",
    "....SSSSSSSS....",
    "..SSsSSSSSSsSS..",
    ".Ss..........sS.",
    ".S............S.",
    ".OOOOO....OOOOO.",
    "OBBBBBO..OBBBBBO",
    "OBLWlBOOOOBLWlBO",
    "OBLllBObbOBLllBO",
    "OblmmbOddOblmmbO",
    "ObmnnbO..ObmnnbO",
    "OdbbbdO..OdbbbdO",
    ".OOOOO....OOOOO.",
]


def from_palette(rows, pal, w, h):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            img.putpixel((x, y), pal[ch])
    return img


def goggles_icon():
    return from_palette(parse(GOGGLES_ICON), GOGGLE_PAL, 16, 16)


def goggles_layer():
    """Camada 'humanoid' 64x32: só a cabeça (cubo 8x8x8, UV padrão)."""
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    p = lambda x, y, c: img.putpixel((x, y), GOGGLE_PAL[c])
    # tira de couro em volta da cabeça (faces direita, frente, esquerda, trás: x 0..31, linhas 11..13)
    for x in range(0, 32):
        p(x, 11, "S")
        p(x, 12, "s")
        p(x, 13, "S")
    front = ["bBBbbBBb", "BLWBBLWB", "BlLBBlLB", "BmnBBmnB", "bddbbddb"]
    for i, row in enumerate(front):
        for j, ch in enumerate(row):
            p(8 + j, 10 + i, ch)
    for fx in (0, 16):  # fivelas de latão nas laterais
        p(fx + 3, 12, "b")
        p(fx + 4, 12, "B")
    p(27, 12, "P")
    p(28, 12, "P")
    return img


# ---------------------------------------------------------------- blocos

def noise_fill(img, box, colors, weights, seed):
    rnd = random.Random(seed)
    x0, y0, x1, y1 = box
    for y in range(y0, y1):
        for x in range(x0, x1):
            img.putpixel((x, y), rnd.choices(colors, weights)[0])


def deep_stone(seed):
    """Pedra das profundezas (base do minério): azul-ardósia com veios."""
    img = Image.new("RGBA", (16, 16))
    cols = [hexc("1a2433"), hexc("222f42"), hexc("2b3a50"), hexc("34465f")]
    noise_fill(img, (0, 0, 16, 16), cols, [2, 5, 4, 1], seed)
    rnd = random.Random(seed + 1)
    # rachaduras escuras horizontais (como ardósia)
    for _ in range(5):
        y = rnd.randrange(16)
        x = rnd.randrange(12)
        for i in range(rnd.randint(2, 5)):
            img.putpixel(((x + i) % 16, y), hexc("121a26"))
    return img


def nugget(img, x, y, shape):
    """Pepita de Oricálcio com bisel (shape: lista de strings com 'G')."""
    sub = render(shape, w=len(shape[0]), h=len(shape), outline=True)
    for j in range(sub.size[1]):
        for i in range(sub.size[0]):
            c = sub.getpixel((i, j))
            if c[3]:
                img.putpixel(((x + i) % 16, (y + j) % 16), c)


def orichalcum_ore():
    img = deep_stone(7)
    nugget(img, 1, 1, ["GG.", "GGG", ".G."])
    nugget(img, 9, 2, [".GG", "GGG"])
    nugget(img, 4, 7, ["GGG", "GGG", ".GG"])
    nugget(img, 11, 9, ["GG", "GG"])
    nugget(img, 1, 12, [".G", "GG"])
    nugget(img, 8, 12, ["GGG", "GG."])
    # pequenos pontos de cristal de maré
    for x, y in ((13, 5), (3, 5), (14, 14)):
        img.putpixel((x, y), RAMPS["T"][4])
    return img


def orichalcum_block():
    img = Image.new("RGBA", (16, 16))
    G = RAMPS["G"]
    noise_fill(img, (0, 0, 16, 16), [G[3], G[3], G[4]], [5, 3, 1], 11)
    for i in range(16):
        img.putpixel((i, 0), G[5])
        img.putpixel((0, i), G[4])
        img.putpixel((i, 15), G[1])
        img.putpixel((15, i), G[2])
    # placa interna rebaixada
    for i in range(2, 14):
        img.putpixel((i, 2), G[2])
        img.putpixel((2, i), G[2])
        img.putpixel((i, 13), G[5])
        img.putpixel((13, i), G[4])
    # onda atlante gravada no centro
    wave = [(3, 8), (4, 7), (5, 6), (6, 6), (7, 7), (7, 8), (8, 9), (9, 9), (10, 8), (11, 7), (12, 6)]
    for x, y in wave:
        img.putpixel((x, y), G[1])
        img.putpixel((x, y + 1), G[4])
    # pátina verde-água nos cantos
    for x, y in ((1, 1), (14, 14), (1, 14), (14, 1), (2, 14), (14, 13)):
        img.putpixel((x, y), RAMPS["T"][2])
    for x, y in ((3, 3), (12, 12)):
        img.putpixel((x, y), RAMPS["T"][3])
    return img


def forge_bricks(seed):
    """Tijolos de pedra abissal (azul profundo) com rejunte."""
    img = Image.new("RGBA", (16, 16))
    B = RAMPS["B"]
    noise_fill(img, (0, 0, 16, 16), [B[2], B[3], B[3], B[4]], [3, 5, 4, 1], seed)
    for y in (0, 4, 8, 12):
        for x in range(16):
            img.putpixel((x, y), B[1])
    for row, y in enumerate((0, 4, 8, 12)):
        off = 0 if row % 2 == 0 else 4
        for x in (off, off + 8):
            for yy in range(y, y + 4):
                img.putpixel((x % 16, yy), B[1])
        # luz no topo de cada tijolo
        for x in range(16):
            if img.getpixel((x, y + 1)) != B[1]:
                img.putpixel((x, y + 1), B[4] if (x + row) % 3 else B[5])
    return img


def forge_side():
    img = forge_bricks(21)
    G, T = RAMPS["G"], RAMPS["T"]
    # cinta de Oricálcio no topo e na base
    for x in range(16):
        img.putpixel((x, 0), G[4])
        img.putpixel((x, 1), G[3])
        img.putpixel((x, 2), G[1])
        img.putpixel((x, 13), G[4])
        img.putpixel((x, 14), G[2])
        img.putpixel((x, 15), G[0])
    for x in (3, 12):
        img.putpixel((x, 1), T[4])
        img.putpixel((x, 14), T[3])
    return img


def forge_front():
    img = forge_side()
    G, P = RAMPS["G"], RAMPS["P"]
    # boca da forja com fogo roxo bioluminescente
    for y in range(4, 12):
        for x in range(4, 12):
            img.putpixel((x, y), G[0])
    fire = [
        "......",
        "..s...",
        ".rsr.s",
        ".rqrrs",
        "rqpqrq",
        "qpzpqp",
    ]
    for j, row in enumerate(fire):
        for i, ch in enumerate(row):
            if ch != ".":
                img.putpixel((5 + i, 5 + j), FIXED[ch])
            else:
                img.putpixel((5 + i, 5 + j), hexc("0a0614"))
    # moldura dourada com bisel
    for i in range(3, 13):
        img.putpixel((i, 3), G[5])
        img.putpixel((i, 12), G[1])
        img.putpixel((3, i), G[4])
        img.putpixel((12, i), G[2])
    img.putpixel((7, 3), RAMPS["T"][4])
    img.putpixel((8, 3), RAMPS["T"][5])
    return img


def forge_top():
    img = Image.new("RGBA", (16, 16))
    B, G, T = RAMPS["B"], RAMPS["G"], RAMPS["T"]
    noise_fill(img, (0, 0, 16, 16), [B[2], B[3], B[3]], [2, 5, 3], 31)
    for i in range(16):
        img.putpixel((i, 0), G[4])
        img.putpixel((0, i), G[3])
        img.putpixel((i, 15), G[1])
        img.putpixel((15, i), G[2])
    # bigorna de Oricálcio no centro, com grade 3x3 gravada
    for y in range(4, 12):
        for x in range(4, 12):
            img.putpixel((x, y), G[3])
    for i in range(4, 12):
        img.putpixel((i, 4), G[5])
        img.putpixel((4, i), G[4])
        img.putpixel((i, 11), G[1])
        img.putpixel((11, i), G[2])
    for i in range(5, 11):
        img.putpixel((i, 7), G[2])
        img.putpixel((7, i), G[2])
        img.putpixel((i, 8), G[4])
        img.putpixel((8, i), G[4])
    img.putpixel((7, 7), T[4])
    img.putpixel((8, 8), T[3])
    for x, y in ((2, 2), (13, 2), (2, 13), (13, 13)):
        img.putpixel((x, y), RAMPS["P"][4])
    return img


def forge_bottom():
    return forge_bricks(41)


# ---------------------------------------------------------------- camadas de armadura (64x32)

HEAD = {"top": (8, 0, 8, 8), "bottom": (16, 0, 8, 8), "right": (0, 8, 8, 8), "front": (8, 8, 8, 8),
        "left": (16, 8, 8, 8), "back": (24, 8, 8, 8)}
BODY = {"top": (20, 16, 8, 4), "bottom": (28, 16, 8, 4), "right": (16, 20, 4, 12), "front": (20, 20, 8, 12),
        "left": (28, 20, 4, 12), "back": (32, 20, 8, 12)}
ARM = {"top": (44, 16, 4, 4), "bottom": (48, 16, 4, 4), "right": (40, 20, 4, 12), "front": (44, 20, 4, 12),
       "left": (48, 20, 4, 12), "back": (52, 20, 4, 12)}
LEG = {"top": (4, 16, 4, 4), "bottom": (8, 16, 4, 4), "right": (0, 20, 4, 12), "front": (4, 20, 4, 12),
       "left": (8, 20, 4, 12), "back": (12, 20, 4, 12)}


def plate(img, box, rows=None, seed=0, trim_rows=(), gem=None):
    """Pinta uma face de armadura: placa de Oricálcio com bisel e textura de metal batido.

    rows: intervalo de linhas (relativo à face) a cobrir; None = face inteira.
    trim_rows: linhas (relativas) pintadas com friso verde-água.
    """
    x0, y0, w, h = box
    G, T = RAMPS["G"], RAMPS["T"]
    r0, r1 = rows if rows else (0, h)
    rnd = random.Random(seed)
    for j in range(r0, r1):
        for i in range(w):
            c = rnd.choices([G[3], G[4], G[2]], [10, 5, 1])[0]
            if j == r0:
                c = G[4]
            if i == 0:
                c = G[4] if j != r1 - 1 else G[2]
            if j == r1 - 1 or i == w - 1:
                c = G[1] if j == r1 - 1 else G[2]
            if j in trim_rows:
                c = T[4] if i % 2 == 0 else T[3]
            img.putpixel((x0 + i, y0 + j), c)
    if gem:
        gx, gy = gem
        img.putpixel((x0 + gx, y0 + gy), RAMPS["P"][4])
        img.putpixel((x0 + gx + 1, y0 + gy), RAMPS["P"][3])
        img.putpixel((x0 + gx, y0 + gy + 1), RAMPS["P"][3])
        img.putpixel((x0 + gx + 1, y0 + gy + 1), RAMPS["P"][2])


def orichalcum_layer():
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    # capacete: cobre a cabeça, com abertura para o rosto na frente
    for k, box in HEAD.items():
        if k == "front":
            plate(img, box, rows=(0, 3), seed=1, trim_rows=(2,), gem=(3, 0))
            x0, y0, _, _ = box
            for j in range(3, 7):  # protetores de bochecha
                for i in (0, 7):
                    img.putpixel((x0 + i, y0 + j), RAMPS["G"][3] if i == 0 else RAMPS["G"][2])
        elif k == "bottom":
            continue
        elif k == "top":
            plate(img, box, seed=2)
            x0, y0, _, _ = box
            for j in range(8):  # crista verde-água no topo
                img.putpixel((x0 + 3, y0 + j), RAMPS["T"][4])
                img.putpixel((x0 + 4, y0 + j), RAMPS["T"][3])
        else:
            plate(img, box, rows=(0, 7), seed=3 + len(k), trim_rows=(2,))
    # peitoral
    for k, box in BODY.items():
        plate(img, box, seed=10 + len(k), trim_rows=(8,) if box[3] == 12 else ())
    fx, fy, _, _ = BODY["front"]
    plate(img, BODY["front"], seed=17, trim_rows=(8,), gem=(3, 3))
    for j in range(1, 7):  # nervura central
        img.putpixel((fx + 3, fy + j), RAMPS["G"][4])
        img.putpixel((fx + 4, fy + j), RAMPS["G"][2])
    img.putpixel((fx + 3, fy + 3), RAMPS["P"][4])
    img.putpixel((fx + 4, fy + 3), RAMPS["P"][3])
    img.putpixel((fx + 3, fy + 4), RAMPS["P"][3])
    img.putpixel((fx + 4, fy + 4), RAMPS["P"][2])
    # ombreiras
    for k, box in ARM.items():
        if k in ("top",):
            plate(img, box, seed=30)
        elif k != "bottom":
            plate(img, box, rows=(0, 6), seed=31 + len(k), trim_rows=(5,))
    # botas (parte de baixo das pernas)
    for k, box in LEG.items():
        if k == "bottom":
            plate(img, box, seed=40)
        elif k != "top":
            plate(img, box, rows=(7, 12), seed=41 + len(k), trim_rows=(7,))
    return img


def orichalcum_leggings_layer():
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    for k, box in BODY.items():
        if k in ("right", "front", "left", "back"):
            plate(img, box, rows=(7, 12), seed=50 + len(k), trim_rows=(7,))
    for k, box in LEG.items():
        if k == "top":
            plate(img, box, seed=60)
        elif k != "bottom":
            plate(img, box, rows=(0, 9), seed=61 + len(k), trim_rows=(6,))
    return img


# ---------------------------------------------------------------- GUI da Forja

def gui_forge():
    img = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    W, H = 176, 166
    face = hexc("b7c6c8")
    light = hexc("f2fbfb")
    dark = hexc("5b6e78")
    edge = hexc("0b1220")
    for y in range(H):
        for x in range(W):
            img.putpixel((x, y), face)
    # contorno arredondado estilo vanilla
    for x in range(2, W - 2):
        img.putpixel((x, 0), edge)
        img.putpixel((x, H - 1), edge)
        img.putpixel((x, 1), light)
        img.putpixel((x, 2), light)
        img.putpixel((x, H - 2), dark)
        img.putpixel((x, H - 3), dark)
    for y in range(2, H - 2):
        img.putpixel((0, y), edge)
        img.putpixel((W - 1, y), edge)
        img.putpixel((1, y), light)
        img.putpixel((2, y), light)
        img.putpixel((W - 2, y), dark)
        img.putpixel((W - 3, y), dark)
    for x, y in ((0, 0), (1, 0), (0, 1), (W - 1, 0), (W - 2, 0), (W - 1, 1), (0, H - 1), (1, H - 1), (0, H - 2),
                 (W - 1, H - 1), (W - 2, H - 1), (W - 1, H - 2)):
        img.putpixel((x, y), (0, 0, 0, 0))
    for x, y in ((1, 1), (W - 2, 1), (1, H - 2), (W - 2, H - 2)):
        img.putpixel((x, y), edge)
    img.putpixel((W - 3, 2), face)
    img.putpixel((2, H - 3), face)

    def slot(x, y, size=18):
        for j in range(size):
            for i in range(size):
                c = hexc("8b8b8b")
                if i == 0 or j == 0:
                    c = hexc("373737")
                if i == size - 1 or j == size - 1:
                    c = hexc("ffffff")
                img.putpixel((x + i, y + j), c)
        img.putpixel((x + size - 1, y), hexc("8b8b8b"))
        img.putpixel((x, y + size - 1), hexc("8b8b8b"))

    for r in range(3):
        for c in range(3):
            slot(29 + c * 18, 16 + r * 18)
    # resultado: moldura maior dourada
    G, T, P = RAMPS["G"], RAMPS["T"], RAMPS["P"]
    for j in range(26):
        for i in range(26):
            c = G[3]
            if i == 0 or j == 0:
                c = G[4]
            if i == 25 or j == 25:
                c = G[1]
            img.putpixel((119 + i, 30 + j), c)
    slot(123, 34)
    for r in range(3):
        for c in range(9):
            slot(7 + c * 18, 83 + r * 18)
    for c in range(9):
        slot(7 + c * 18, 141)
    # seta de Oricálcio com núcleo verde-água
    arrow = [
        "..........G.....",
        "..........GG....",
        "GGGGGGGGGGTGG...",
        "GTTTTTTTTTTTGG..",
        "GTTTTTTTTTTTGG..",
        "GGGGGGGGGGTGG...",
        "..........GG....",
        "..........G.....",
    ]
    arr = render(arrow, w=16, h=8)
    img.paste(arr, (90, 31), arr)
    # ornamento: ondas atlantes entre a grade e o inventário (à direita do rótulo "Inventário")
    for x in range(96, W - 8):
        y = 75 + int(round(1.5 * math.sin(x / 3.0)))
        img.putpixel((x, y), T[2])
        img.putpixel((x, y + 1), T[4] if x % 6 else P[4])
    # cantos com pérolas
    for x, y in ((6, 6), (W - 8, 6)):
        img.putpixel((x, y), P[3])
        img.putpixel((x + 1, y), P[4])
        img.putpixel((x, y + 1), P[2])
        img.putpixel((x + 1, y + 1), P[3])
    return img


# ---------------------------------------------------------------- invocação: Chamariz, Projeto e Altar

LURE = [
    "................",
    ".......45.......",
    "......4..4......",
    ".......34.......",
    ".....234432.....",
    "....3qrssrq2....",
    "....4rsswsr3....",
    "....3rssssq2....",
    "....4qrrrqp3....",
    "....2pqqqpp1....",
    ".....213312.....",
    "......yvvt......",
    ".......vt.......",
    ".......t........",
]

BLUEPRINT = [
    "................",
    ".mjjjjjjjjjjjjm.",
    ".hhhhhhhhhhhhhh.",
    "..dddddddddddd..",
    "..dcvvvvvvvvcd..",
    "..dcvccyyccvcd..",
    "..dcvcyrryccvd..",
    "..dcvccyyccvcd..",
    "..dcvvvvvvvvcd..",
    "..dccccccccccd..",
    "..dcuuuucqrrcd..",
    "..dddddddqqqdd..",
    ".mjjjjjjjjjjjjm.",
    ".hhhhhhhhhhhhhh.",
]


def lure():
    img = render(LURE)
    img.putpixel((7, 6), hexc("ffffff"))
    return img


def altar_side():
    img = forge_bricks(51)
    G, T = RAMPS["G"], RAMPS["T"]
    for x in range(16):  # tampo (linhas 0..3) e topo da base (linha 12)
        img.putpixel((x, 0), G[5])
        img.putpixel((x, 1), G[3])
        img.putpixel((x, 2), G[3] if x % 4 else T[4])
        img.putpixel((x, 3), G[1])
        img.putpixel((x, 12), G[4])
        img.putpixel((x, 15), RAMPS["B"][0])
    return img


def altar_column():
    img = Image.new("RGBA", (16, 16))
    B, G, P = RAMPS["B"], RAMPS["G"], RAMPS["P"]
    flutes = [B[4], B[3], B[2], B[3]]
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), flutes[x % 4])
    for x in range(16):  # anéis de Oricálcio no alto e embaixo da coluna
        img.putpixel((x, 4), G[4])
        img.putpixel((x, 5), G[2])
        img.putpixel((x, 10), G[4])
        img.putpixel((x, 11), G[2])
    for y in (7, 8):  # runas roxas acesas
        for x in (5, 9):
            img.putpixel((x, y), P[4] if y == 7 else P[3])
    return img


def altar_top():
    img = Image.new("RGBA", (16, 16))
    B, G, T, P = RAMPS["B"], RAMPS["G"], RAMPS["T"], RAMPS["P"]
    noise_fill(img, (0, 0, 16, 16), [B[1], B[2], B[2]], [2, 5, 3], 61)
    for i in range(1, 15):
        img.putpixel((i, 1), G[4])
        img.putpixel((1, i), G[4])
        img.putpixel((i, 14), G[2])
        img.putpixel((14, i), G[2])
    # círculo de runas: anel verde-água com pérola roxa no centro
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - 7.5, y - 7.5)
            if 4.6 <= d < 5.6:
                img.putpixel((x, y), T[4] if (x + y) % 3 else T[5])
            elif d < 2.2:
                img.putpixel((x, y), P[4] if x + y < 15 else P[3])
    for x, y in ((7, 7), (6, 6)):
        img.putpixel((x, y), P[5])
    for x, y in ((7, 2), (8, 2), (7, 13), (8, 13), (2, 7), (2, 8), (13, 7), (13, 8)):
        img.putpixel((x, y), T[3])
    return img


# ---------------------------------------------------------------- barra de boss (HUD)

CORAL = [hexc("4a1222"), hexc("8e2a40"), hexc("cf4a60"), hexc("ff8a8a"), hexc("ffc2b8")]

# ponta de coral (lado esquerdo; o direito é espelhado). '1'..'4' = CORAL[1..4]
CORAL_CAP = [
    "..........",
    "..2.......",
    ".232......",
    "..2..3....",
    "..22.32...",
    "...2222...",
    ".3..22....",
    ".22.2.....",
    "..2222....",
    "...222....",
    "...222....",
    "..2222....",
    ".22.2.....",
    ".3..22....",
    "...2222...",
    "..22.32...",
    "..2..3....",
    ".232......",
    "..2.......",
    "..........",
    "..........",
    "..........",
]


def boss_bar():
    """Atlas 256x96 da barra de boss. Posições usadas por AquamarineBossBarHud."""
    img = Image.new("RGBA", (256, 96), (0, 0, 0, 0))
    G, B, T, P = RAMPS["G"], RAMPS["B"], RAMPS["T"], RAMPS["P"]
    px = img.putpixel
    W, H = 220, 22
    x0, y0, x1, y1 = 4, 2, 215, 19  # corpo da moldura
    hole = hexc("050a14")
    # corpo
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            px((x, y), G[3])
    for x in range(x0 + 1, x1):
        px((x, y0), G[0])
        px((x, y1), G[0])
        px((x, y0 + 1), G[5])
        px((x, y1 - 1), G[1])
    for y in range(y0 + 1, y1):
        px((x0, y), G[0])
        px((x1, y), G[0])
        px((x0 + 1, y), G[4])
        px((x1 - 1, y), G[2])
    # metal batido no corpo
    rnd = random.Random(5)
    for _ in range(140):
        x, y = rnd.randrange(x0 + 2, x1 - 1), rnd.randrange(y0 + 2, y1 - 1)
        px((x, y), rnd.choice([G[4], G[2]]))
    # janelas do escudo e da vida (rebaixadas)
    for (wy, wh) in ((4, 4), (9, 8)):
        for y in range(wy - 1, wy + wh + 1):
            for x in range(9, 211):
                edge_top = y == wy - 1 or x == 9
                edge_bot = y == wy + wh or x == 210
                if edge_top and not edge_bot:
                    px((x, y), G[1])
                elif edge_bot and not edge_top:
                    px((x, y), G[5])
                elif not edge_top and not edge_bot:
                    px((x, y), hole)
    # pilares laterais com gema verde-água
    for gx in (5, 211):
        for y, ch in ((9, 4), (10, 5), (11, 4), (12, 3)):
            px((gx + 1, y), T[ch])
            px((gx + 2, y), T[ch - 1])
        px((gx + 1, 9), T[5])
    # crista central com pérola roxa
    for x in range(102, 118):
        px((x, 1), G[0])
        px((x, 2), G[4] if x % 3 else G[5])
    for x, y in ((101, 2), (118, 2)):
        px((x, y), G[0])
    for x in range(107, 113):
        px((x, 0), G[0])
        px((x, 1), G[4])
    for x, y, c in ((109, 1, P[4]), (110, 1, P[3]), (109, 2, P[3]), (110, 2, P[2])):
        px((x, y), c)
    # pontas de coral
    for j, row in enumerate(CORAL_CAP):
        for i, ch in enumerate(row):
            if ch == ".":
                continue
            c = CORAL[int(ch)]
            px((i, j), c)
            px((W - 1 - i, j), c)
    # contorno escuro do coral
    snap = img.copy()
    for j in range(H):
        for i in list(range(0, 12)) + list(range(W - 12, W)):
            if snap.getpixel((i, j))[3]:
                continue
            for dx, dy in ((0, 1), (1, 0), (0, -1), (-1, 0)):
                nx, ny = i + dx, j + dy
                if 0 <= nx < W and 0 <= ny < H and snap.getpixel((nx, ny)) in CORAL[1:]:
                    px((i, j), CORAL[0])
                    break

    # vida (200x8 em v=22): azul profundo com brilho verde-água
    rows = [T[4], B[5], B[4], B[4], B[3], B[3], B[2], B[1]]
    for x in range(200):
        for y, c in enumerate(rows):
            px((x, 22 + y), c)
        k = x % 16
        if k in (3, 4):
            px((x, 22 + 2), T[3])
        if k in (4, 5):
            px((x, 22 + 3), T[2])
        if k == 11:
            px((x, 22 + 5), B[5])
    # fantasma (200x8 em v=30)
    for x in range(200):
        for y in range(8):
            px((x, 30 + y), hexc("eaf6ff") if y < 5 else hexc("b9d4ea"))
    # escudo (200x4 em v=38): cristal
    srows = [hexc("e6fbff"), hexc("9fe6ff"), hexc("5cbcf0"), hexc("2f80c8")]
    for x in range(200):
        for y, c in enumerate(srows):
            px((x, 38 + y), c)
        if x % 10 == 0:
            px((x, 38 + 1), hexc("ffffff"))
        if x % 10 == 1:
            px((x, 38 + 2), hexc("9fe6ff"))
    # rachaduras (3 camadas 200x4 em v=42, 46, 50)
    cracks = [
        [(150, [0, 1, 1, 2, 3]), (151, [2])],
        [(96, [0, 1, 2, 2, 3]), (97, [1]), (58, [0, 1, 2, 3]), (57, [3])],
        [(28, [0, 1, 2, 3]), (29, [1, 2]), (124, [0, 1, 2, 3]), (123, [2]), (178, [0, 1, 2, 3]), (179, [0])],
    ]
    for layer, segs in enumerate(cracks):
        v = 42 + layer * 4
        for cx, ys in segs:
            for n, cy in enumerate(ys):
                xx = cx + (n % 2)
                px((xx, v + cy), hexc("0a2038"))
                if xx + 1 < 200:
                    px((xx + 1, v + cy), hexc("ffffff"))
    # brilho do pulso (220x22 em v=56): aro da moldura em branco, tingido de roxo no jogo
    for y in range(H):
        for x in range(W):
            c = img.getpixel((x, y))
            if c[3] and (c in (G[0], G[5], G[4], CORAL[0]) or y in (y0, y1) or x in (x0, x1)):
                px((x, 56 + y), (255, 255, 255, 200))
    return img


# ---------------------------------------------------------------- principal

# ---------------------------------------------------------------- Pressão (2B)

TONIC = [
    "................",
    "......jmj.......",
    "......cwc.......",
    "......ccc.......",
    ".....cdddc......",
    ".....cdddc......",
    "....cdTTTTc.....",
    "...cdTTTTTTc....",
    "...cTTTTTTTTc...",
    "...cTTuTTTTTc...",
    "...cTTTTTTuTc...",
    "...cTuTTTTTTc...",
    "...cTTTTTuTTc...",
    "....cTTTTTTc....",
    ".....ccccccc....",
]


def tonic():
    """Tônico de Pressão: frasco de vidro com líquido verde-água e gargalo rolhado."""
    return render(TONIC)


def anchor_buoy_block():
    """Boia de Âncora: casco laranja com faixas brancas, argolas de Oricálcio nos cantos."""
    img = Image.new("RGBA", (16, 16))
    orange = [hexc("c2531c"), hexc("e0702a"), hexc("f08a3c")]
    white = [hexc("cfd8dc"), hexc("f1f5f7")]
    dark = hexc("5a2a12")
    rnd = random.Random(88)
    for y in range(16):
        for x in range(16):
            band = (x // 4) % 2 == 0
            base = white[rnd.randint(0, 1)] if band else orange[rnd.randint(0, 2)]
            img.putpixel((x, y), base)
    for i in range(16):
        for p in ((i, 0), (i, 15), (0, i), (15, i)):
            img.putpixel(p, dark)
    for p in ((1, 1), (14, 1), (1, 14), (14, 14), (2, 1), (1, 2), (13, 1), (14, 2), (2, 14), (1, 13), (13, 14), (14, 13)):
        img.putpixel(p, RAMPS["G"][3])
    return img


def pressure_tonic_icon():
    """Ícone do efeito (18x18): gota de pressão sobre fundo azul profundo."""
    img = Image.new("RGBA", (18, 18), (0, 0, 0, 0))
    for y in range(18):
        for x in range(18):
            dx, dy = x - 8.5, y - 8.5
            if dx * dx + dy * dy <= 8.5 * 8.5:
                img.putpixel((x, y), RAMPS["B"][2] if dx * dx + dy * dy < 7.2 * 7.2 else RAMPS["B"][0])
    drop = [(9, 3), (8, 4), (10, 4), (8, 5), (10, 5), (7, 6), (11, 6), (7, 7), (11, 7), (6, 8), (12, 8), (6, 9), (12, 9),
            (6, 10), (12, 10), (7, 11), (11, 11), (8, 12), (9, 12), (10, 12)]
    for p in drop:
        img.putpixel(p, RAMPS["T"][4])
    for y in range(5, 12):
        for x in range(7, 12):
            if img.getpixel((x, y))[:3] == RAMPS["B"][2][:3]:
                img.putpixel((x, y), RAMPS["T"][2])
    img.putpixel((8, 7), RAMPS["T"][5])
    img.putpixel((8, 8), RAMPS["T"][5])
# ---------------------------------------------------------------- recompensas do Kraken (2E)

def kraken_eye():
    """Olho do Kraken: globo azul-profundo com íris roxa e pupila em fenda."""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    B, P, T = RAMPS["B"], RAMPS["P"], RAMPS["T"]
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            dx, dy = x - cx, y - cy
            d = math.hypot(dx, dy)
            if d > 6.4:
                continue
            lum = (-dx - dy) / 9.0
            c = B[3] if lum > 0.15 else B[2] if lum > -0.3 else B[1]
            if d < 4.4:
                c = P[4] if lum > 0.1 else P[3] if lum > -0.3 else P[2]
            if d < 4.4 and abs(dx + 0.5) < 1.0 and abs(dy) < 3.6:
                c = hexc("0a0614")
            if d > 5.4:
                c = T[2] if lum < 0 else T[3]
            img.putpixel((x, y), c)
    for x, y in ((5, 4), (6, 4), (5, 5)):
        img.putpixel((x, y), hexc("ffffff"))
    img.putpixel((9, 10), T[5])
    return outline_alpha(img, B[0])


def abyssal_ink():
    """Tinta Abissal: frasco de vidro escuro com tinta roxa e brilho verde-água."""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    B, P, T, H = RAMPS["B"], RAMPS["P"], RAMPS["T"], RAMPS["H"]
    for y in range(6, 14):  # corpo
        half = 5 if 8 <= y <= 12 else 4
        for x in range(8 - half, 8 + half):
            edge = x in (8 - half, 7 + half)
            img.putpixel((x, y), B[4] if edge and x < 8 else B[2] if edge else (P[2] if y < 9 else P[1]))
    for y in range(3, 6):  # gargalo
        for x in (6, 7, 8, 9):
            img.putpixel((x, y), B[3] if x < 8 else B[2])
    for x in (6, 7, 8, 9):  # rolha
        img.putpixel((x, 2), H[4])
        img.putpixel((x, 3), H[3])
    for x, y in ((4, 8), (4, 9), (5, 7)):
        img.putpixel((x, y), T[4])
    for x, y in ((9, 10), (10, 11)):
        img.putpixel((x, y), P[3])
    img.putpixel((6, 10), P[4])
    return outline_alpha(img, B[0])


HEART = [
    "................",
    "....kk....kk....",
    "...kPPk..kPPk...",
    "..kPPPPkkPPPPk..",
    "..kPPPPPPPPPPk..",
    "..kPPPPPPPPPPk..",
    "..kPPrPPPPPPPk..",
    "...kPPPPPPPPk...",
    "....kPPPPPPk....",
    ".....kPPPPk.....",
    "......kPPk......",
    ".......kk.......",
]


def kraken_heart():
    img = render(HEART)
    G, T = RAMPS["G"], RAMPS["T"]
    for x, y, c in ((5, 5, T[4]), (6, 6, T[4]), (7, 7, T[3]), (8, 8, T[4]), (9, 8, T[3]),
                    (10, 5, G[4]), (9, 6, G[4]), (8, 5, G[3]), (6, 3, G[4]), (11, 4, T[3])):
        img.putpixel((x, y), c)
    img.putpixel((4, 3), hexc("e6d8ff"))
    return img


def kraken_trophy(golden):
    """Textura do troféu: pedra do Abismo com relevo de olho; dourada no modo abissal."""
    img = Image.new("RGBA", (16, 16))
    B, G, P, T = RAMPS["B"], RAMPS["G"], RAMPS["P"], RAMPS["T"]
    ramp = G if golden else B
    noise_fill(img, (0, 0, 16, 16), [ramp[2], ramp[3], ramp[3], ramp[4]], [3, 5, 5, 1], 91 if golden else 90)
    for i in range(16):
        img.putpixel((i, 0), ramp[5] if golden else G[4])
        img.putpixel((i, 15), ramp[1])
        img.putpixel((0, i), ramp[4])
        img.putpixel((15, i), ramp[1])
    for y in range(16):
        for x in range(16):
            dx, dy = x - 7.5, y - 7.5
            d = math.hypot(dx, dy)
            if 3.3 <= d < 4.6:
                img.putpixel((x, y), G[4] if not golden else B[3])
            elif d < 3.3:
                img.putpixel((x, y), P[4] if abs(dx + 0.5) >= 0.9 else hexc("0a0614"))
    img.putpixel((6, 6), hexc("ffffff"))
    return img


def main():
    out = {}
    out["item/orichalcum_ingot"] = ingot()
    out["item/abyssal_pearl"] = pearl()
    out["item/tide_crystal"] = crystal()
    out["item/ancient_scale"] = scale()
    out["item/orichalcum_sword"] = sword()
    out["item/orichalcum_pickaxe"] = pickaxe()
    out["item/orichalcum_axe"] = axe()
    out["item/orichalcum_shovel"] = shovel()
    out["item/orichalcum_hoe"] = hoe()
    out["item/orichalcum_helmet"] = armor_icon(HELMET, {(7, 5): "r", (8, 5): "q"})
    out["item/orichalcum_chestplate"] = armor_icon(CHEST, {(7, 5): "r", (8, 5): "q"})
    out["item/orichalcum_leggings"] = armor_icon(LEGS)
    out["item/orichalcum_boots"] = armor_icon(BOOTS)
    out["item/diving_goggles"] = goggles_icon()
    out["block/orichalcum_ore"] = orichalcum_ore()
    out["block/orichalcum_block"] = orichalcum_block()
    out["block/abyssal_forge_side"] = forge_side()
    out["block/abyssal_forge_front"] = forge_front()
    out["block/abyssal_forge_top"] = forge_top()
    out["block/abyssal_forge_bottom"] = forge_bottom()
    out["item/abyssal_lure"] = lure()
    out["item/lure_blueprint"] = render(BLUEPRINT)
    out["block/abyssal_altar_top"] = altar_top()
    out["block/abyssal_altar_side"] = altar_side()
    out["block/abyssal_altar_column"] = altar_column()
    out["block/abyssal_altar_bottom"] = forge_bricks(71)
    out["item/pressure_tonic"] = tonic()
    out["block/anchor_buoy"] = anchor_buoy_block()
    out["item/kraken_eye"] = kraken_eye()
    out["item/abyssal_ink"] = abyssal_ink()
    out["item/kraken_heart"] = kraken_heart()
    out["block/kraken_trophy"] = kraken_trophy(False)
    out["block/golden_abyss_trophy"] = kraken_trophy(True)
    for rel, img in out.items():
        save(img, rel + ".png")
    layers = [
        save(orichalcum_layer(), "entity/equipment/humanoid/orichalcum.png"),
        save(orichalcum_leggings_layer(), "entity/equipment/humanoid_leggings/orichalcum.png"),
        save(goggles_layer(), "entity/equipment/humanoid/diving_goggles.png"),
    ]
    gui = save(gui_forge(), "gui/container/abyssal_forge.png")
    save(boss_bar(), "gui/boss_bar.png")
    save(pressure_tonic_icon(), "mob_effect/pressure_tonic.png")
    # ícone do mod (128x128) a partir da pérola
    save(pearl().resize((128, 128), Image.NEAREST), os.path.join("..", "icon.png"))

    if "--preview" in sys.argv:
        tiles = list(out.items())
        cols = 7
        s = 8
        sheet = Image.new("RGBA", (cols * 17 * s, ((len(tiles) + cols - 1) // cols) * 17 * s), hexc("3a3f47"))
        for n, (_, img) in enumerate(tiles):
            big = img.resize((16 * s, 16 * s), Image.NEAREST)
            sheet.paste(big, ((n % cols) * 17 * s, (n // cols) * 17 * s), big)
        path = os.path.join(REPO, "build", "texture_preview.png")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        sheet.save(path)
        print("preview:", path)
        # segunda folha: camadas de armadura (4x) e a GUI da forja (2x)
        sheet2 = Image.new("RGBA", (64 * 4 * 3 + 16, 32 * 4 + 166 * 2 + 8), hexc("3a3f47"))
        for n, layer in enumerate(layers):
            big = layer.resize((256, 128), Image.NEAREST)
            sheet2.paste(big, (n * 264, 0), big)
        g = gui.crop((0, 0, 176, 166)).resize((352, 332), Image.NEAREST)
        sheet2.paste(g, (0, 136), g)
        path2 = os.path.join(REPO, "build", "texture_preview_layers.png")
        sheet2.save(path2)
        print("preview:", path2)
    print(len(out) + 4, "texturas geradas")


if __name__ == "__main__":
    main()
