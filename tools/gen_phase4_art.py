"""Arte da Fase 4, faixa 4D (docs/10-fase4.md, §4 e §5).

Bigorna de Maré, Mesa de Cartas Náuticas, Altar de Concha, os dois troféus e a Boia de Âncora viram modelos 3D
(peças com giro livre, que o 26.3 aceita) com texturas próprias; as pontas do Arpão, a Bobina Voltaica, o Regulador
de Pressão, a Aljava e o Propulsor ganham ícones novos. Grava texturas, modelos, estados de bloco e definições de item.

    C:\\Python314\\python tools/gen_phase4_art.py            # gera tudo
    C:\\Python314\\python tools/gen_phase4_art.py --preview  # e salva build/phase4_art_preview.png
"""
import json
import math
import os
import random
import sys

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.normpath(os.path.join(HERE, ".."))
ASSETS = os.path.join(REPO, "src", "main", "resources", "assets", "aquamarine")
NS = "aquamarine"


def hexc(h, a=255):
    h = h.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), a)


def ramp(*hexes):
    return [hexc(h) for h in hexes]


IRON = ramp("14181c", "1f262c", "2c353d", "3b4650", "4e5c68", "6a7a87", "8d9eab")
PATINA = ramp("0d2f2d", "164a45", "1f655c", "2c8576", "46a892", "7dd3bd")
BARNACLE = ramp("3e3a35", "6d665d", "948b7e", "bdb3a3", "e3dbcc")
CORAL = ramp("5a1530", "8f2849", "c4416a", "ef6f93", "ffa7c0")
WOOD = ramp("1c110a", "2c1b10", "3f2817", "553720", "6d482a", "8a5f38", "a6774a")
BRASS = ramp("3e2a0c", "6b4c18", "9a7228", "c79a3c", "e9c463", "fff0a8")
PARCH = ramp("6e5f43", "96845f", "b9a67c", "d6c59a", "ece0bb", "fbf3d9")
SEA = ramp("173e63", "1f5584", "2b6a9c", "4a8cc0", "7db4e0")
STONE = ramp("0b1d1b", "122b28", "1a3b36", "234c45", "2f6056", "3f7a6c", "5a9886")
SHELL = ramp("4e2238", "7e3e5a", "b06a82", "d8949f", "f2bdbd", "ffe4dc")
NAVY = [(4, 10, 24, 255), (8, 22, 44, 255), (12, 34, 64, 255), (20, 50, 88, 255), (32, 70, 112, 255),
        (52, 96, 140, 255), (84, 130, 170, 255)]
PURPLE = [(36, 20, 66, 255), (62, 40, 104, 255), (96, 68, 148, 255), (134, 104, 190, 255), (176, 148, 224, 255)]
GOLD = ramp("3a240e", "6e4a1a", "9a6c26", "c79634", "e8c25a", "fff0a8")
DEEP = ramp("08090e", "10131b", "181d28", "222937", "2d3648", "3b475e")
ORANGE = ramp("4e1806", "8a2e0e", "c2531c", "e0702a", "f5913f", "ffb56a")
WHITE = ramp("5e6a70", "8e9ba1", "b9c5ca", "dde5e8", "f5f9fa")
DECK = ramp("10201f", "17302e", "21403d", "2c524e", "3a6762", "4d7f78")
CYAN = (110, 255, 236, 255)
CYAN_SOFT = (70, 210, 220, 255)
CLEAR = (0, 0, 0, 0)


# ================================================================ pincéis

class Tex:
    """Textura com pincéis simples; tudo determinístico pela semente."""

    def __init__(self, seed, w=16, h=16):
        self.img = Image.new("RGBA", (w, h), CLEAR)
        self.px = self.img.load()
        self.rng = random.Random(seed)
        self.w, self.h = w, h

    def put(self, x, y, c):
        if 0 <= x < self.w and 0 <= y < self.h:
            self.px[x, y] = c

    def get(self, x, y):
        return self.px[x, y] if 0 <= x < self.w and 0 <= y < self.h else CLEAR

    def noise(self, box, colors, weights=None):
        x0, y0, x1, y1 = box
        for y in range(y0, y1):
            for x in range(x0, x1):
                self.put(x, y, self.rng.choices(colors, weights or [1] * len(colors))[0])

    def rect(self, box, c):
        x0, y0, x1, y1 = box
        for y in range(y0, y1):
            for x in range(x0, x1):
                self.put(x, y, c)

    def hline(self, x0, x1, y, c):
        for x in range(x0, x1 + 1):
            self.put(x, y, c)

    def vline(self, x, y0, y1, c):
        for y in range(y0, y1 + 1):
            self.put(x, y, c)

    def bevel(self, box, light, dark):
        x0, y0, x1, y1 = box
        self.hline(x0, x1 - 1, y0, light)
        self.vline(x0, y0, y1 - 1, light)
        self.hline(x0, x1 - 1, y1 - 1, dark)
        self.vline(x1 - 1, y0, y1 - 1, dark)

    def disc(self, cx, cy, r, c):
        for y in range(self.h):
            for x in range(self.w):
                if (x + 0.5 - cx) ** 2 + (y + 0.5 - cy) ** 2 <= r * r:
                    self.put(x, y, c)

    def scatter(self, n, colors, box=None):
        x0, y0, x1, y1 = box or (0, 0, self.w, self.h)
        for _ in range(n):
            self.put(self.rng.randrange(x0, x1), self.rng.randrange(y0, y1), self.rng.choice(colors))


def outline(img, dark=(14, 10, 14, 255)):
    """Contorno de 1 px em volta das partes opacas (o estilo dos ícones do jogo)."""
    px = img.load()
    w, h = img.size
    out = img.copy()
    po = out.load()
    for y in range(h):
        for x in range(w):
            if px[x, y][3]:
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < w and 0 <= ny < h and px[nx, ny][3] == 255:
                    po[x, y] = dark
                    break
    return out


# ================================================================ texturas dos blocos

def anvil_metal():
    t = Tex(401)
    t.noise((0, 0, 16, 16), IRON[2:5], [3, 5, 2])
    for x in t.rng.sample(range(16), 6):  # escorridos de pátina descendo do alto
        for y in range(t.rng.randrange(2, 9)):
            t.put(x, y, PATINA[2 + (y < 2)])
    for x, y in ((2, 3), (13, 3), (2, 12), (13, 12)):  # rebites
        t.put(x, y, IRON[6])
        t.put(x + 1, y + 1, IRON[1])
    t.hline(0, 15, 0, IRON[5])
    t.hline(0, 15, 15, IRON[1])
    return t.img


def anvil_top():
    t = Tex(402)
    t.noise((0, 0, 16, 16), IRON[4:6], [4, 1])
    for x in range(16):  # ondas gravadas no tampo
        y = 5 + round(1.4 * math.sin(x / 2.3))
        t.put(x, y, IRON[2])
        t.put(x, y + 6, IRON[2])
        t.put(x, y + 1, IRON[6])
    t.rect((7, 7, 9, 9), PATINA[4])
    t.bevel((0, 0, 16, 16), IRON[6], IRON[2])
    return t.img


def anvil_base():
    t = Tex(403)
    t.noise((0, 0, 16, 16), IRON[1:4], [2, 4, 2])
    t.noise((0, 12, 16, 16), PATINA[1:4])
    for _ in range(9):  # cracas
        x, y = t.rng.randrange(1, 15), t.rng.randrange(1, 14)
        t.put(x, y, BARNACLE[3])
        t.put(x + 1, y, BARNACLE[2])
        t.put(x, y + 1, BARNACLE[1])
        t.put(x + 1, y + 1, BARNACLE[0])
    t.scatter(5, CORAL[2:4], (0, 9, 16, 16))
    return t.img


def anvil_rune():
    """Runa de maré acesa por cima do tampo (o resto é transparente)."""
    t = Tex(404)
    for x in range(3, 13):
        y = 8 + round(1.6 * math.sin((x - 3) / 1.6))
        t.put(x, y, CYAN if x % 3 else CYAN_SOFT)
    t.put(7, 4, CYAN)
    t.put(8, 4, CYAN)
    t.put(7, 12, CYAN_SOFT)
    t.put(8, 12, CYAN_SOFT)
    return t.img


def coral_sprig(seed, colors=CORAL):
    """Raminho de coral para as placas cruzadas (fundo transparente)."""
    t = Tex(seed)

    def branch(x, y, dx, n, c):
        for i in range(n):
            t.put(round(x), y - i, colors[c])
            t.put(round(x) + 1, y - i, colors[c - 1])
            x += dx

    branch(7, 15, 0.0, 9, 2)
    branch(7, 10, -0.6, 6, 3)
    branch(8, 9, 0.7, 6, 3)
    branch(5, 7, -0.2, 4, 4)
    branch(11, 6, 0.3, 3, 4)
    for x, y in ((3, 2), (4, 4), (12, 2), (10, 1), (6, 1)):
        t.put(x, y, colors[4])
    return t.img


def wood_planks(seed, vertical=True):
    t = Tex(seed)
    t.noise((0, 0, 16, 16), WOOD[2:5], [2, 4, 2])
    for k in range(0, 16, 4):  # emendas das tábuas
        for i in range(16):
            t.put(k if vertical else i, i if vertical else k, WOOD[1])
    for _ in range(14):  # veios
        x, y = t.rng.randrange(16), t.rng.randrange(16)
        for i in range(t.rng.randint(2, 4)):
            t.put(x if vertical else x + i, y + i if vertical else y, WOOD[5])
    return t.img


def chart_front():
    t = Tex(411)
    t.img.paste(wood_planks(412), (0, 0))
    for y0 in (2, 9):  # duas gavetas com puxador de latão
        t.rect((2, y0, 14, y0 + 5), WOOD[3])
        t.bevel((2, y0, 14, y0 + 5), WOOD[5], WOOD[1])
        t.rect((7, y0 + 2, 9, y0 + 3), BRASS[4])
        t.put(7, y0 + 3, BRASS[2])
        t.put(8, y0 + 3, BRASS[2])
    return t.img


def chart_top():
    t = Tex(413)
    t.img.paste(wood_planks(414, vertical=False), (0, 0))
    t.bevel((0, 0, 16, 16), WOOD[5], WOOD[1])
    for x, y in ((0, 0), (14, 0), (0, 14), (14, 14)):  # cantoneiras de latão
        t.rect((x, y, x + 2, y + 2), BRASS[3])
        t.put(x + (x == 0), y + (y == 0), BRASS[5])
    return t.img


def chart_map():
    """Carta náutica no canto (0,0)-(11,10): mar, ilha, rota pontilhada e rosa dos ventos."""
    t = Tex(415)
    t.noise((0, 0, 11, 10), PARCH[3:6], [2, 4, 1])
    t.noise((0, 0, 6, 10), SEA[2:4], [3, 1])
    for y in range(10):  # costa recortada
        edge = 5 + round(1.2 * math.sin(y * 1.3))
        t.put(edge, y, PARCH[1])
        t.put(edge - 1, y, SEA[4])
    t.rect((2, 2, 4, 4), PARCH[4])
    t.put(2, 2, (88, 140, 70, 255))
    for i, (x, y) in enumerate(((1, 8), (2, 7), (3, 6), (5, 5), (6, 4), (7, 3))):
        if i % 2 == 0:
            t.put(x, y, (176, 40, 40, 255))
    cx, cy = 8, 7  # rosa dos ventos
    for dx, dy, c in ((0, -2, PARCH[0]), (0, 2, PARCH[0]), (-2, 0, PARCH[0]), (2, 0, PARCH[0]), (0, -1, (176, 40, 40, 255)),
                      (0, 0, PARCH[0])):
        t.put(cx + dx, cy + dy, c)
    t.bevel((0, 0, 11, 10), PARCH[5], PARCH[1])
    # mostrador da bússola em (12,0)-(15,3)
    t.rect((12, 0, 15, 3), PARCH[5])
    t.put(13, 0, (176, 40, 40, 255))
    t.put(13, 1, PARCH[0])
    t.put(13, 2, WHITE[1])
    return t.img


def scroll_paper():
    t = Tex(416)
    t.noise((0, 0, 16, 16), PARCH[2:5], [2, 4, 2])
    for x in range(16):
        t.put(x, 0, PARCH[1])
        t.put(x, 15, PARCH[1])
    t.rect((6, 0, 8, 16), (150, 34, 40, 255))  # fita vermelha
    t.vline(6, 0, 15, (110, 22, 30, 255))
    # pontas enroladas em espiral no canto (10,10)-(13,13)
    for (x, y), c in zip(((10, 10), (11, 10), (12, 10), (12, 11), (12, 12), (11, 12), (10, 12), (10, 11), (11, 11)),
                         (PARCH[4], PARCH[3], PARCH[2], PARCH[1], PARCH[1], PARCH[2], PARCH[3], PARCH[2], PARCH[0])):
        t.put(x, y, c)
    return t.img


def brass_tex(seed):
    t = Tex(seed)
    t.noise((0, 0, 16, 16), BRASS[2:5], [2, 4, 2])
    t.scatter(6, [BRASS[5]])
    t.hline(0, 15, 0, BRASS[5])
    t.hline(0, 15, 15, BRASS[1])
    t.rect((12, 12, 15, 15), (170, 230, 240, 255))  # lente da luneta
    t.put(12, 12, (230, 255, 255, 255))
    return t.img


def ink_and_quill():
    t = Tex(417)
    t.noise((0, 0, 8, 8), [(20, 26, 48, 255), (30, 44, 80, 255)], [3, 1])  # vidro do tinteiro
    t.put(1, 1, (140, 170, 220, 255))
    for y in range(16):  # pena (8..15): haste e barbas
        for x in range(9, 15):
            d = abs(x - 11.5)
            if y < 13 and d <= 1.6 + 0.12 * y:
                t.put(x, y, WHITE[3] if (x + y) % 3 else WHITE[2])
        t.put(11, y, WHITE[1])
    return t.img


def altar_stone():
    t = Tex(421)
    t.noise((0, 0, 16, 16), STONE[2:5], [2, 4, 2])
    for y in (5, 10):  # fiadas
        t.hline(0, 15, y, STONE[1])
    for x in (4, 12):
        t.vline(x, 0, 4, STONE[1])
        t.vline(x, 11, 15, STONE[1])
    for x in (0, 8):
        t.vline(x, 6, 9, STONE[1])
    for i in range(5):  # concha em relevo no meio
        t.put(6 + i, 8 - abs(i - 2), STONE[6])
        t.put(6 + i, 9, STONE[5])
    t.hline(0, 15, 0, GOLD[4])
    t.hline(0, 15, 1, GOLD[2])
    return t.img


def altar_top():
    t = Tex(422)
    t.noise((0, 0, 16, 16), STONE[3:6], [3, 4, 1])
    for y in range(16):
        for x in range(16):
            d = math.hypot(x + 0.5 - 8, y + 0.5 - 8)
            if 5.6 <= d < 6.5:
                t.put(x, y, GOLD[4] if (x + y) % 2 else GOLD[3])
            elif 6.5 <= d < 7.2:
                t.put(x, y, STONE[1])
    t.bevel((0, 0, 16, 16), STONE[6], STONE[1])
    return t.img


def shell_tex():
    t = Tex(423)
    for y in range(16):
        for x in range(16):
            ang = math.atan2(y - 16, x - 8)
            rib = int((ang + math.pi) * 9) % 2
            dist = math.hypot(x - 8, y - 16)
            level = 2 + rib + (dist > 9) + (dist > 13)
            t.put(x, y, SHELL[min(5, level)])
    for x in range(16):
        t.put(x, 0, SHELL[1])
    return t.img


def pearl_tex():
    t = Tex(424)
    for y in range(16):
        for x in range(16):
            d = math.hypot(x + 0.5 - 6, y + 0.5 - 6)
            t.put(x, y, (240, 255, 255, 255) if d < 3 else (200, 245, 250, 255) if d < 7 else (150, 220, 235, 255))
    t.put(4, 4, (255, 255, 255, 255))
    return t.img


def glow_bulb():
    t = Tex(425)
    t.noise((0, 0, 16, 16), [CYAN, CYAN_SOFT, (180, 255, 245, 255)], [3, 2, 1])
    return t.img


def pedestal(seed, stone, trim, inlay=None):
    t = Tex(seed)
    t.noise((0, 0, 16, 16), stone[2:5], [2, 4, 2])
    t.hline(0, 15, 0, trim[5])
    t.hline(0, 15, 1, trim[3])
    t.hline(0, 15, 2, trim[1])
    t.hline(0, 15, 15, trim[2])
    for x in range(16):  # ondas gravadas
        y = 8 + round(1.3 * math.sin(x / 1.7))
        t.put(x, y, stone[1])
        t.put(x, y - 1, stone[5])
    if inlay:
        for x in (3, 8, 13):
            t.put(x, 11, inlay)
            t.put(x, 12, CYAN_SOFT)
    return t.img


def plaque(golden):
    t = Tex(431 + golden)
    face = GOLD if not golden else ramp("2a3242", "364055", "46536c", "5d6f8e", "7e93b5", "b8c8e6")
    t.noise((0, 0, 16, 16), face[3:5], [3, 1])
    t.bevel((0, 0, 16, 16), face[5], face[1])
    # emblema: tentáculo enrolado
    for x, y in ((5, 9), (6, 8), (7, 7), (8, 7), (9, 8), (9, 9), (8, 10), (7, 9)):
        t.put(x, y, face[1])
    return t.img


def kraken_skin(seed, ramp_, spot, glow):
    t = Tex(seed)
    t.noise((0, 0, 16, 16), ramp_[2:5], [2, 4, 2])
    for _ in range(10):  # manchas
        x, y = t.rng.randrange(16), t.rng.randrange(16)
        t.put(x, y, spot)
        t.put(x + 1, y, spot)
    for _ in range(7):
        t.put(t.rng.randrange(16), t.rng.randrange(16), glow)
    for x in range(16):  # ventosas na parte de baixo (linhas 12..15)
        if x % 3 == 1:
            t.put(x, 13, PURPLE[4] if ramp_ is NAVY else GOLD[5])
            t.put(x, 14, PURPLE[2] if ramp_ is NAVY else GOLD[2])
    return t.img


def eye_tex(gem):
    """Olho do troféu: íris acesa com pupila em fenda horizontal (canto 0,0-4,3)."""
    t = Tex(441 + gem)
    iris = (110, 255, 236, 255) if not gem else (130, 230, 255, 255)
    t.rect((0, 0, 4, 3), iris)
    t.hline(1, 2, 1, (4, 10, 14, 255))
    t.put(0, 0, (230, 255, 250, 255))
    t.rect((0, 4, 16, 16), iris)
    return t.img


def crystal_tex():
    t = Tex(451)
    for y in range(16):
        for x in range(16):
            t.put(x, y, (120, 240, 255, 255) if (x + y) % 5 else (220, 255, 255, 255))
    for x in range(16):
        t.put(x, x, (60, 180, 220, 255))
    return t.img


def deck_tex():
    t = Tex(461)
    t.noise((0, 0, 16, 16), DECK[2:5], [2, 4, 2])
    for y in (0, 5, 10, 15):  # tábuas na direção x
        t.hline(0, 15, y, DECK[0])
    for y in (2, 7, 12):
        for x in (1, 14):
            t.put(x, y, BRASS[3])  # pregos
    for y in range(16):  # amarração de corda nas pontas
        if y % 5 in (2, 3):
            t.put(0, y, (178, 150, 102, 255))
            t.put(15, y, (150, 124, 82, 255))
    t.scatter(10, [DECK[5], (90, 120, 110, 255)])
    return t.img


def float_tex():
    t = Tex(462)
    t.noise((0, 0, 16, 16), ORANGE[2:5], [2, 4, 2])
    t.rect((0, 6, 16, 10), WHITE[3])
    t.hline(0, 15, 6, WHITE[1])
    t.hline(0, 15, 9, WHITE[1])
    for y in (1, 14):
        t.hline(0, 15, y, IRON[3])
        t.hline(0, 15, y + (1 if y == 1 else -1), IRON[5])
    return t.img


def stripes_tex():
    t = Tex(463)
    for y in range(16):
        for x in range(16):
            band = (y // 3) % 2 == 0
            base = ORANGE[3] if band else WHITE[3]
            if x in (0, 15):
                base = ORANGE[1] if band else WHITE[1]
            t.put(x, y, base)
    t.scatter(12, [ORANGE[4], WHITE[4]])
    return t.img


def metal_tex():
    t = Tex(464)
    t.noise((0, 0, 16, 16), WHITE[1:3], [2, 3])
    t.hline(0, 15, 0, WHITE[4])
    t.hline(0, 15, 15, WHITE[0])
    for x in range(0, 16, 4):  # grades da gaiola da lanterna
        t.vline(x, 0, 15, IRON[2])
    return t.img


def lamp_frames(color):
    """Lanterna da Boia: 2 quadros (aceso e apagado) para piscar; a cor diz o tempo que resta."""
    img = Image.new("RGBA", (16, 32), CLEAR)
    on, off = color
    t = Tex(465)
    t.noise((0, 0, 16, 16), [on, tuple(min(255, v + 60) for v in on[:3]) + (255,)], [4, 1])
    img.paste(t.img, (0, 0))
    t2 = Tex(466)
    t2.noise((0, 0, 16, 16), [off, tuple(max(0, v - 30) for v in off[:3]) + (255,)], [4, 1])
    img.paste(t2.img, (0, 16))
    return img


# ================================================================ modelos

class Model:
    """Modelo de bloco com peças; sem 'uv' explícito o jogo projeta pela posição da peça."""

    def __init__(self, textures, particle, ao=False):
        self.textures = {k: v if ":" in v else f"{NS}:block/{v}" for k, v in textures.items()}
        self.textures["particle"] = self.textures[particle]
        self.elements = []
        self.ao = ao

    def box(self, frm, to, tex, faces=None, uv=None, rot=None, light=0, cull=None):
        """tex: chave de textura ou dict face -> chave ('*' = resto)."""
        faces = faces or ("north", "south", "east", "west", "up", "down")
        el = {"from": [round(v, 4) for v in frm], "to": [round(v, 4) for v in to], "faces": {}}
        for f in faces:
            key = tex if isinstance(tex, str) else tex.get(f, tex.get("*"))
            if key is None:
                continue
            face = {"texture": "#" + key}
            if uv and f in uv:
                face["uv"] = uv[f]
            if cull and f in cull:
                face["cullface"] = f
            el["faces"][f] = face
        if rot:
            origin, angles = rot
            r = {"origin": [round(v, 4) for v in origin]}
            if isinstance(angles, tuple) and len(angles) == 2:
                r["axis"], r["angle"] = angles
            else:
                r["x"], r["y"], r["z"] = (round(a, 3) for a in angles)
            el["rotation"] = r
        if light:
            el["light_emission"] = light
        self.elements.append(el)
        return el

    def plane(self, center, size, tex, yaw, light=0):
        """Placa fina vertical (raminho), girada em torno do eixo y."""
        cx, cy, cz = center
        w, h = size
        self.box((cx - w / 2, cy, cz), (cx + w / 2, cy + h, cz), tex, faces=("north", "south"),
                 uv={"north": [0, 0, 16, 16], "south": [0, 0, 16, 16]}, rot=((cx, cy, cz), ("y", yaw)), light=light)

    def json(self, parent="minecraft:block/block"):
        data = {"parent": parent, "textures": self.textures, "elements": self.elements}
        if not self.ao:
            data["ambientocclusion"] = False
        return data


def tide_anvil():
    m = Model({"metal": "tide_anvil_metal", "top": "tide_anvil_top", "base": "tide_anvil_base",
               "rune": "tide_anvil_rune", "coral": "tide_anvil_coral", "barnacle": "tide_anvil_base"}, "metal")
    m.box((2, 0, 2), (14, 4, 14), {"*": "base", "up": "metal"})
    m.box((4, 4, 3), (12, 5, 13), "metal")
    m.box((6, 5, 4), (10, 10, 12), "metal")
    m.box((3, 10, 0), (13, 16, 16), {"*": "metal", "up": "top"})
    m.box((4, 16, 1.5), (12, 16.05, 14.5), "rune", faces=("up",), uv={"up": [3, 2, 13, 14]}, light=12)
    m.box((3.5, 15, -1.5), (12.5, 16, 0), "metal")  # bico (chifre) da bigorna, afinando
    m.box((5, 15.25, -3), (11, 16, -1.5), "metal")
    m.box((6.5, 15.5, -4), (9.5, 16, -3), "metal")
    for (x, z) in ((2.5, 3), (12, 12), (3, 12.5), (12.5, 3.5)):  # cracas no pé
        m.box((x, 4, z), (x + 1.2, 4.9, z + 1.2), "barnacle", uv={f: [10, 2, 12, 4] for f in ("north", "south", "east", "west", "up")})
    m.plane((12.6, 4, 12.6), (5, 6), "coral", 45)
    m.plane((12.6, 4, 12.6), (5, 6), "coral", -45)
    return m


def chart_table():
    m = Model({"wood": "chart_table_wood", "front": "chart_table_front", "top": "chart_table_top",
               "map": "chart_table_map", "scroll": "chart_table_scroll", "brass": "chart_table_brass",
               "ink": "chart_table_ink"}, "wood")
    m.box((1, 0, 1), (15, 11, 15), {"*": "wood", "north": "front"})
    m.box((0, 11, 0), (16, 13, 16), "top")
    m.box((1.5, 13, 1.5), (12.5, 13.1, 11.5), "map", faces=("up",), uv={"up": [0, 0, 11, 10]})
    m.box((12.6, 13, 1.5), (14.8, 15.2, 13), {"*": "scroll", "east": "scroll", "west": "scroll"},
          uv={"east": [10, 10, 13, 13], "west": [10, 10, 13, 13], "north": [9.5, 9.5, 12, 12], "south": [9.5, 9.5, 12, 12]})
    m.box((1.8, 13, 12.4), (9.8, 14.6, 14.4), "scroll", rot=((5.8, 13, 13.4), ("y", -12)),
          uv={"east": [10, 10, 13, 13], "west": [10, 10, 13, 13]})
    m.box((10.4, 13, 12.2), (12.2, 14.8, 14.2), "ink", uv={f: [0, 0, 2, 2] for f in ("north", "south", "east", "west", "up")})
    m.box((11.1, 14.4, 12.9), (11.5, 18.2, 13.6), "ink", faces=("north", "south", "east", "west"),
          uv={f: [9, 0, 13, 13] for f in ("north", "south", "east", "west")}, rot=((11.3, 14.4, 13.2), (16, 0, 22)))
    m.box((4.5, 13.1, 6.4), (11.5, 14.3, 7.6), "brass", rot=((8, 13.1, 7), ("y", 28)))  # luneta
    m.box((11.2, 13.0, 6.2), (12.0, 14.5, 7.8), "brass", rot=((8, 13.1, 7), ("y", 28)),
          uv={"east": [12, 12, 15, 15], "west": [12, 12, 15, 15]})
    m.box((7.6, 13.1, 8.6), (10.4, 13.9, 11.4), {"*": "brass", "up": "map"}, uv={"up": [12, 0, 15, 3]})  # bússola
    return m


def shell_altar():
    m = Model({"stone": "shell_altar_stone", "top": "shell_altar_top", "shell": "shell_altar_shell",
               "pearl": "shell_altar_pearl", "coral": "shell_altar_coral", "glow": "shell_altar_glow"}, "stone")
    m.box((1, 0, 1), (15, 3, 15), {"*": "stone", "up": "top"})
    m.box((3, 3, 3), (13, 9, 13), "stone")
    m.box((2, 9, 2), (14, 10.5, 14), {"*": "stone", "up": "top"})
    for x, z in ((3, 3), (12, 3), (3, 12), (12, 12)):  # anêmonas acesas nos cantos
        m.box((x, 10.5, z), (x + 1, 11.5, z + 1), "glow", light=10)
    # concha: valva de baixo em leque (três gomos) e a de cima aberta para trás, na dobradiça
    m.box((5, 10.5, 5), (11, 11.6, 11.5), "shell", uv={"up": [2, 4, 14, 16]})
    m.box((4, 10.5, 6), (5, 11.3, 11), "shell")
    m.box((11, 10.5, 6), (12, 11.3, 11), "shell")
    m.box((7, 10.5, 11.2), (9, 12, 12.6), "shell")
    hinge = (8, 11.6, 11.6)
    m.box((5, 11.6, 5.6), (11, 12.4, 11.6), "shell", rot=(hinge, ("x", -58)), uv={"down": [2, 4, 14, 16], "up": [2, 4, 14, 16]})
    m.box((4.2, 11.6, 6.6), (5, 12.2, 11.4), "shell", rot=(hinge, ("x", -58)))
    m.box((11, 11.6, 6.6), (11.8, 12.2, 11.4), "shell", rot=(hinge, ("x", -58)))
    m.box((6.8, 11.5, 7.0), (9.2, 13.9, 9.4), "pearl", light=15)
    m.plane((2.4, 3, 2.4), (4, 5), "coral", 45)
    m.plane((13.6, 3, 13.4), (4, 5), "coral", -45)
    return m


def trophy(golden):
    pre = "golden_abyss_trophy" if golden else "kraken_trophy"
    m = Model({"pedestal": f"{pre}_pedestal", "plaque": f"{pre}_plaque", "skin": f"{pre}_skin", "eye": f"{pre}_eye",
               "crystal": f"{pre}_crystal"}, "pedestal")
    m.box((1, 0, 1), (15, 2, 15), "pedestal")
    m.box((3, 2, 3), (13, 4, 13), "pedestal")
    m.box((5, 2.4, 2.9), (11, 3.6, 3.0), "plaque", faces=("north",), uv={"north": [3, 6, 13, 11]})
    if golden:  # cristais nos cantos do pedestal
        for x, z, yaw in ((1.6, 1.6, 20), (13.4, 1.6, -25), (1.6, 13.4, -15), (13.4, 13.4, 30)):
            m.box((x - 0.5, 2, z - 0.5), (x + 0.5, 4.6, z + 0.5), "crystal", rot=((x, 2, z), (8, yaw, 10)), light=12)
    # Kraken: cabeça, manto inclinado para trás, barbatanas e oito tentáculos que se enrolam no pedestal
    m.box((5, 4.6, 4.6), (11, 9.6, 10.4), "skin")
    m.box((5.4, 6.6, 4.5), (6.9, 8.1, 4.6), "eye", faces=("north",), uv={"north": [0, 0, 4, 3]}, light=14)
    m.box((9.1, 6.6, 4.5), (10.6, 8.1, 4.6), "eye", faces=("north",), uv={"north": [0, 0, 4, 3]}, light=14)
    lean = ((8, 9.2, 8.2), ("x", 24))
    m.box((5.5, 9.2, 5.6), (10.5, 14.2, 10.6), "skin", rot=lean)
    m.box((6.4, 14.0, 6.5), (9.6, 15.8, 9.7), "skin", rot=lean)
    m.box((7.2, 15.6, 7.3), (8.8, 16.6, 8.9), "skin", rot=lean)
    m.box((4.2, 12.0, 8.0), (5.6, 14.6, 10.2), "skin", rot=lean)
    m.box((10.4, 12.0, 8.0), (11.8, 14.6, 10.2), "skin", rot=lean)
    under = {"*": "skin", "down": "skin"}
    for k in range(8):
        yaw = 22.5 + 45 * k
        o = (8, 4, 7.5)
        # raiz descendo da cabeça, trecho deitado no pedestal e ponta que sobe enrolando
        m.box((7.3, 4, 9.6), (8.7, 6.2, 11.0), under, rot=(o, (0, yaw, 0)), uv={"down": [0, 12, 2, 15]})
        m.box((7.4, 4, 10.6), (8.6, 5.2, 13.2), under, rot=(o, (0, yaw, 0)), uv={"down": [0, 12, 2, 15]})
        m.box((7.6, 4.2, 12.8), (8.4, 6.4, 13.6), "skin", rot=(o, (-18, yaw, 0)))
    return m


def anchor_buoy(center, stage=0):
    m = Model({"deck": "anchor_buoy_deck", "float": "anchor_buoy_float", "stripes": "anchor_buoy_stripes",
               "metal": "anchor_buoy_metal", "lamp": f"anchor_buoy_lamp_{('green', 'yellow', 'red')[stage]}",
               "bell": "chart_table_brass"}, "deck")
    m.box((0, 13, 0), (16, 16, 16), "deck")
    for x, z in ((1, 1), (10, 1), (1, 10), (10, 10)):  # flutuadores debaixo do deque
        m.box((x, 6, z), (x + 5, 13, z + 5), "float")
    if center:
        m.box((4, 16, 4), (12, 18, 12), "stripes")
        m.box((5, 18, 5), (11, 22, 11), "stripes")
        m.box((7, 22, 7), (9, 27, 9), "metal")
        m.box((9, 25, 7.7), (11.8, 25.6, 8.3), "metal")  # braço do sino
        m.box((10.4, 23.0, 7.2), (12.0, 25.0, 8.8), "bell")
        m.box((6, 27, 6), (10, 27.6, 10), "metal")
        m.box((6.6, 27.6, 6.6), (9.4, 30, 9.4), "lamp", light=15)
        for x, z in ((6, 6), (9.4, 6), (6, 9.4), (9.4, 9.4)):  # grade da lanterna
            m.box((x, 27.6, z), (x + 0.6, 30, z + 0.6), "metal")
        m.box((6, 30, 6), (10, 30.6, 10), "metal")
        m.box((7.4, 30.6, 7.4), (8.6, 31.4, 8.6), "metal")
    return m


# ================================================================ ícones de item

def sprite():
    return Tex(0)


def harpoon_tip(kind):
    """Ponta farpada na diagonal (cabo embaixo à esquerda); cada munição muda metal, encaixe e efeito."""
    metals = {"abyssal": GOLD, "poison": ramp("10200e", "1e3a1a", "2f5a28", "4a7d3a", "73a85a", "a8d88a"),
              "fire": ramp("101828", "1c2a44", "2a4064", "3e5c8a", "6a8cc0", "b8d8ff"),
              "wither": ramp("0a080c", "151018", "221a26", "322838", "4a3e52", "6e6276"),
              "shock": ramp("3a1a0c", "6a3418", "9a5228", "c87a3c", "eaa860", "fff0c0")}
    met = metals[kind]
    t = Tex(500 + len(kind))
    ax, ay = 2.5, 13.5  # base do cabo
    ux, uy = 1 / math.sqrt(2), -1 / math.sqrt(2)  # direção da ponta
    vx, vy = -uy, ux
    L = 15.0

    def inside(u, v):
        a = abs(v)
        if u < 0 or u > L:
            return None
        if 7.4 <= u:  # lâmina: triângulo até a ponta
            return "blade" if a <= 2.3 * (L - u) / (L - 7.4) + 0.2 else None
        if 4.6 <= u and 1.0 < a <= 3.5:  # farpas: ganchos voltados para trás, separados do pescoço
            inner = 1.0 + (u - 4.6) * 0.5  # borda de dentro sobe do pescoço
            outer = 3.5 - (7.4 - u) * 0.15
            return "barb" if a >= inner - 0.1 and a <= outer and a <= 1.0 + (7.4 - 4.6 - (u - 4.6)) * 0 + 2.4 and                 u >= 7.4 - (a - 1.0) * 1.15 else None
        if u >= 4.6:  # pescoço entre as farpas
            return "blade" if a <= 1.0 else None
        if u >= 2.8:  # encaixe
            return "socket" if a <= 1.2 else None
        return "shaft" if a <= 0.75 else None

    for y in range(16):
        for x in range(16):
            px, py = x + 0.5 - ax, y + 0.5 - ay
            u, v = px * ux + py * uy, px * vx + py * vy
            part = inside(u, v)
            if part == "shaft":
                t.put(x, y, WOOD[4] if v < 0 else WOOD[3])
            elif part == "socket":
                band = {"abyssal": PATINA[3], "poison": (80, 150, 60, 255), "fire": (60, 120, 200, 255),
                        "wither": (215, 205, 190, 255), "shock": (200, 110, 50, 255)}[kind]
                t.put(x, y, band if int(u * 2) % 2 == 0 else met[2])
            elif part in ("barb", "blade"):
                if abs(v) < 0.5:
                    c = met[5]  # aresta central
                elif v < 0:
                    c = met[4]
                else:
                    c = met[2]
                t.put(x, y, c)
    img = outline(t.img, met[0])
    t.img, t.px = img, img.load()
    if kind == "abyssal":
        t.put(13, 2, CYAN)
        t.put(12, 3, (190, 255, 245, 255))
    elif kind == "poison":
        for x, y in ((5, 9), (5, 10), (5, 11), (9, 12), (9, 13), (10, 6)):
            t.put(x, y, (130, 230, 60, 255) if y % 2 else (90, 190, 40, 255))
    elif kind == "fire":
        for x, y, c in ((14, 1, (220, 250, 255, 255)), (13, 0, (120, 210, 255, 255)), (15, 3, (80, 160, 255, 255)),
                        (11, 1, (80, 160, 255, 255)), (15, 0, (120, 210, 255, 255)), (12, 0, (60, 120, 230, 255)),
                        (10, 3, (120, 210, 255, 255)), (14, 5, (60, 120, 230, 255))):
            t.put(x, y, c)
    elif kind == "wither":
        for x, y in ((3, 8), (2, 7), (4, 6), (11, 13), (12, 14), (13, 12)):
            t.put(x, y, (60, 56, 66, 170))
        t.put(5, 10, (240, 236, 226, 255))
        t.put(6, 9, (240, 236, 226, 255))
    elif kind == "shock":
        for x, y, c in ((10, 1, (255, 242, 120, 255)), (11, 0, (255, 255, 200, 255)), (9, 2, (160, 250, 255, 255)),
                        (15, 5, (255, 242, 120, 255)), (14, 6, (160, 250, 255, 255)), (15, 7, (255, 255, 200, 255))):
            t.put(x, y, c)
    return t.img


def voltaic_coil():
    t = Tex(520)
    copper = ramp("4e2410", "7a3a1c", "a8522a", "d06e36", "f0a060")
    for y in range(3, 13):  # núcleo de cristal entre as espiras
        for x in range(5, 11):
            t.put(x, y, (90, 220, 230, 255) if (x + y) % 2 else (150, 245, 250, 255))
    for y in range(3, 13):  # espiras na diagonal
        for x in range(4, 12):
            if (x + 2 * y) % 4 in (0, 1):
                edge = x in (4, 11)
                t.put(x, y, copper[1] if edge else copper[3 + ((x + 2 * y) % 4 == 0)])
    for y in (1, 2, 13, 14):  # tampas de ferro
        for x in range(4, 12):
            t.put(x, y, IRON[5] if y in (1, 13) else IRON[3])
    t.put(4, 1, CLEAR)
    t.put(11, 1, CLEAR)
    t.put(4, 14, CLEAR)
    t.put(11, 14, CLEAR)
    for x, y in ((6, 0), (9, 0)):  # bornes
        t.put(x, y, IRON[6])
    t.rect((12, 5, 14, 10), PATINA[3])  # escama elétrica presa de lado
    t.put(13, 6, PATINA[5])
    t.put(12, 8, PATINA[4])
    img = outline(t.img)
    t.img, t.px = img, img.load()
    for x, y, c in ((2, 4, (255, 242, 120, 255)), (1, 5, (160, 250, 255, 255)), (2, 10, (160, 250, 255, 255)),
                    (15, 3, (255, 242, 120, 255)), (14, 12, (160, 250, 255, 255))):
        t.put(x, y, c)
    return t.img


def pressure_regulator():
    t = Tex(530)
    t.rect((7, 12, 9, 16), BRASS[2])  # cano de saída
    t.put(7, 14, BRASS[4])
    t.disc(8, 8, 5.6, BRASS[3])
    t.disc(8, 8, 4.4, WHITE[4])
    for a in range(0, 360, 45):  # marcas do mostrador
        r = math.radians(a)
        t.put(round(8 + 3.6 * math.cos(r) - 0.5), round(8 + 3.6 * math.sin(r) - 0.5), IRON[3])
    for i in range(4):  # ponteiro vermelho
        t.put(7 + i // 2, 8 - i, (200, 40, 40, 255))
    t.put(7, 7, IRON[1])
    t.rect((6, 0, 10, 2), (180, 36, 36, 255))  # volante da válvula
    t.put(6, 0, (230, 80, 70, 255))
    t.rect((7, 2, 9, 3), BRASS[1])
    for x, y in ((3, 4), (12, 4), (3, 11), (12, 11)):
        t.put(x, y, BRASS[5])
    img = outline(t.img, BRASS[0])
    return img


def harpoon_quiver():
    t = Tex(540)
    leather = ramp("2a160c", "4a2a16", "6a3e20", "8a5530", "a86e40")
    for y in range(16):
        for x in range(16):
            u = (x - y) / math.sqrt(2)  # através do tubo
            v = (x + y - 15) / math.sqrt(2)  # ao longo do tubo (+ = boca, no alto à direita)
            if abs(u) <= 2.3 and -7.0 <= v <= 3.4:
                c = leather[4] if u < -1.2 else leather[3] if u < 0.4 else leather[2] if u < 1.5 else leather[1]
                if abs(v + 4.6) < 0.55 or abs(v - 1.4) < 0.55:
                    c = PATINA[3] if u < 0.5 else PATINA[2]
                if v > 2.6:
                    c = leather[0]  # boca do tubo
                t.put(x, y, c)
    for x, y in ((10, 4), (11, 3), (12, 2)):  # hastes de madeira saindo da boca
        t.put(x, y, WOOD[5])
    for x, y in ((9, 3), (11, 1), (13, 3)):  # pontas douradas
        t.put(x, y, GOLD[4])
        t.put(x + 1, y - 1, GOLD[5])
    img = outline(t.img)
    t.img, t.px = img, img.load()
    for x, y in ((1, 9), (1, 10), (2, 11), (3, 12)):  # alça solta
        t.put(x, y, leather[0])
    return t.img


def tide_thruster():
    t = Tex(550)
    cream = ramp("5a3c24", "9c7450", "cfa878", "ecd2a8", "fff3dc")
    stripe = ramp("6a2412", "9a3a1c")
    cx, cy = 6.5, 8.5
    for y in range(16):
        for x in range(16):
            dx, dy = (x + 0.5 - cx) / 1.05, y + 0.5 - cy
            r = math.hypot(dx, dy)
            if r > 6.3:
                continue
            ang = math.atan2(dy, dx)
            c = cream[3] if dy > 0 else cream[4] if r < 4.5 else cream[3]
            if r < 2.6:
                c = cream[2]  # umbigo da concha
            if r < 1.2:
                c = cream[1]
            if dy < -0.5 and r > 3.4 and int(ang * 2.6 + r * 0.55) % 2 == 0:
                c = stripe[1] if r > 4.4 else stripe[0]  # listras de fogo do dorso
            if abs(r - 6.0) < 0.5 or (abs(r - 2.8) < 0.4 and dx > -0.5):
                c = cream[1]  # borda e volta da espiral
            t.put(x, y, c)
    for i in range(90):  # volta da espiral, do umbigo para fora
        th = i * 0.12
        rr = 0.9 * math.exp(0.16 * th)
        if rr > 5.6:
            break
        t.put(round(cx + 1.05 * rr * math.cos(th + 2.2) - 0.5), round(cy + rr * math.sin(th + 2.2) - 0.5), cream[0])
    t.rect((12, 6, 15, 11), BRASS[3])  # bocal de latão na abertura
    t.vline(12, 6, 10, BRASS[1])
    t.hline(12, 14, 6, BRASS[5])
    t.rect((15, 7, 16, 10), CYAN)
    img = outline(t.img, cream[0])
    t.img, t.px = img, img.load()
    t.put(15, 8, (230, 255, 252, 255))
    return t.img


def buoy_icon():
    t = Tex(560)
    t.rect((3, 10, 13, 13), DECK[3])  # deque
    t.hline(3, 12, 10, DECK[5])
    t.rect((5, 6, 11, 10), ORANGE[3])  # casco listrado
    t.hline(5, 10, 7, WHITE[3])
    t.hline(5, 10, 8, WHITE[3])
    t.vline(7, 2, 5, WHITE[1])
    t.vline(8, 2, 5, WHITE[2])
    t.rect((6, 0, 10, 2), (120, 255, 140, 255))  # lanterna verde
    t.put(6, 0, (220, 255, 220, 255))
    t.rect((10, 3, 12, 5), BRASS[3])  # sino
    t.put(10, 3, BRASS[5])
    t.rect((3, 13, 6, 15), ORANGE[2])  # flutuadores
    t.rect((10, 13, 13, 15), ORANGE[2])
    return outline(t.img)


# ================================================================ escrita

def write_png(img, rel):
    path = os.path.join(ASSETS, "textures", rel + ".png")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path)
    return img


def write_json(data, rel):
    path = os.path.join(ASSETS, rel)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write("\n")


def facing_states(model):
    return {"variants": {f"facing={d}": {"model": f"{NS}:block/{model}", **({"y": y} if y else {})}
                         for d, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270))}}


def main():
    textures = {
        "block/tide_anvil_metal": anvil_metal(), "block/tide_anvil_top": anvil_top(), "block/tide_anvil_base": anvil_base(),
        "block/tide_anvil_rune": anvil_rune(), "block/tide_anvil_coral": coral_sprig(405),
        "block/chart_table_wood": wood_planks(410), "block/chart_table_front": chart_front(), "block/chart_table_top": chart_top(),
        "block/chart_table_map": chart_map(), "block/chart_table_scroll": scroll_paper(), "block/chart_table_brass": brass_tex(418),
        "block/chart_table_ink": ink_and_quill(),
        "block/shell_altar_stone": altar_stone(), "block/shell_altar_top": altar_top(), "block/shell_altar_shell": shell_tex(),
        "block/shell_altar_pearl": pearl_tex(), "block/shell_altar_coral": coral_sprig(426), "block/shell_altar_glow": glow_bulb(),
        "block/kraken_trophy_pedestal": pedestal(432, DEEP, GOLD), "block/kraken_trophy_plaque": plaque(False),
        "block/kraken_trophy_skin": kraken_skin(434, NAVY, NAVY[1], CYAN), "block/kraken_trophy_eye": eye_tex(False),
        "block/kraken_trophy_crystal": crystal_tex(),
        "block/golden_abyss_trophy_pedestal": pedestal(436, DEEP, GOLD, inlay=CYAN), "block/golden_abyss_trophy_plaque": plaque(True),
        "block/golden_abyss_trophy_skin": kraken_skin(437, GOLD, GOLD[2], GOLD[5]), "block/golden_abyss_trophy_eye": eye_tex(True),
        "block/golden_abyss_trophy_crystal": crystal_tex(),
        "block/anchor_buoy_deck": deck_tex(), "block/anchor_buoy_float": float_tex(), "block/anchor_buoy_stripes": stripes_tex(),
        "block/anchor_buoy_metal": metal_tex(),
        "item/abyssal_harpoon_tip": harpoon_tip("abyssal"), "item/poison_harpoon_tip": harpoon_tip("poison"),
        "item/fire_harpoon_tip": harpoon_tip("fire"), "item/wither_harpoon_tip": harpoon_tip("wither"),
        "item/shock_harpoon_tip": harpoon_tip("shock"), "item/voltaic_coil": voltaic_coil(),
        "item/pressure_regulator": pressure_regulator(), "item/harpoon_quiver": harpoon_quiver(),
        "item/tide_thruster": tide_thruster(), "item/anchor_buoy": buoy_icon(),
    }
    for rel, img in textures.items():
        write_png(img, rel)
    # Lanternas da Boia: verde pisca devagar, amarela mais rápido, vermelha depressa.
    lamps = {"green": (((120, 255, 140, 255), (20, 70, 30, 255)), 20), "yellow": (((255, 220, 80, 255), (80, 64, 16, 255)), 10),
             "red": (((255, 80, 70, 255), (80, 18, 16, 255)), 4)}
    for name, (colors, frametime) in lamps.items():
        write_png(lamp_frames(colors), f"block/anchor_buoy_lamp_{name}")
        with open(os.path.join(ASSETS, "textures", "block", f"anchor_buoy_lamp_{name}.png.mcmeta"), "w", encoding="utf-8", newline="\n") as f:
            json.dump({"animation": {"frametime": frametime}}, f)
            f.write("\n")

    for name, model in (("tide_anvil", tide_anvil()), ("chart_table", chart_table()), ("shell_altar", shell_altar()),
                        ("kraken_trophy", trophy(False)), ("golden_abyss_trophy", trophy(True))):
        write_json(model.json(), f"models/block/{name}.json")
        write_json(facing_states(name), f"blockstates/{name}.json")
        write_json({"model": {"type": "minecraft:model", "model": f"{NS}:block/{name}"}}, f"items/{name}.json")
    write_json(anchor_buoy(False).json(), "models/block/anchor_buoy_deck.json")
    variants = {}
    for stage, color in enumerate(("green", "yellow", "red")):
        write_json(anchor_buoy(True, stage).json(), f"models/block/anchor_buoy_center_{color}.json")
    for wl in ("false", "true"):
        for stage in range(3):
            variants[f"center=false,stage={stage},waterlogged={wl}"] = {"model": f"{NS}:block/anchor_buoy_deck"}
            variants[f"center=true,stage={stage},waterlogged={wl}"] = {
                "model": f"{NS}:block/anchor_buoy_center_{('green', 'yellow', 'red')[stage]}"}
    write_json({"variants": variants}, "blockstates/anchor_buoy.json")
    write_json({"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/anchor_buoy"}}, "models/item/anchor_buoy.json")
    write_json({"model": {"type": "minecraft:model", "model": f"{NS}:item/anchor_buoy"}}, "items/anchor_buoy.json")
    for old in ("models/block/anchor_buoy.json",):
        path = os.path.join(ASSETS, old)
        if os.path.exists(path):
            os.remove(path)

    if "--preview" in sys.argv:
        scale, pad = 6, 6
        tiles = list(textures.items())
        cols = 9
        rows = (len(tiles) + cols - 1) // cols
        sheet = Image.new("RGBA", (cols * (16 * scale + pad), rows * (16 * scale + pad)), (30, 34, 44, 255))
        for i, (rel, img) in enumerate(tiles):
            x, y = (i % cols) * (16 * scale + pad), (i // cols) * (16 * scale + pad)
            sheet.alpha_composite(img.crop((0, 0, 16, 16)).resize((16 * scale, 16 * scale), Image.NEAREST), (x, y))
        os.makedirs(os.path.join(REPO, "build"), exist_ok=True)
        sheet.save(os.path.join(REPO, "build", "phase4_art_preview.png"))
    print(f"fase 4 (4D): {len(textures)} texturas, 6 modelos de bloco, 3 lanternas animadas")


if __name__ == "__main__":
    main()
