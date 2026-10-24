"""Textura em pixel art do Kraken (128x128), pintada sobre o mapeamento "box UV" do KrakenModel.

Uso (na raiz do repositório):
    python tools/gen_kraken_texture.py

Escreve src/main/resources/assets/aquamarine/textures/entity/kraken/kraken.png.
ATENÇÃO: tools/models/kraken/build_model.py copia o uv.png por cima desse arquivo; depois de
rodá-lo, rode este script de novo.

O modelo do Codex (KrakenModel.java) usa o desdobramento de caixa do jogo: para uma caixa
w x h x d em (u, v): topo (u+d, v), base (u+d+w, v), esquerda (u, v+d), frente (u+d, v+d),
direita (u+d+w, v+d), costas (u+2d+w, v+d). A lista CUBES repete as caixas do Java.
As rachaduras são geometria (crack_75/50/25, mostradas conforme o escudo cai) e só precisam
de uma cor viva; a textura funciona em qualquer escala porque cada face é pintada por
padrão repetido e não por posição absoluta.
"""
import math
import os
import random

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.normpath(os.path.join(HERE, "..", "src", "main", "resources", "assets", "aquamarine",
                                    "textures", "entity", "kraken", "kraken.png"))
SIZE = 128
rng = random.Random(7)


def c(h):
    h = h.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), 255)


# Rampas [escura, sombra, base, luz, brilho] (paleta do mod: azul profundo, verde-água, roxo)
BLUE = [c("060d18"), c("0d1b30"), c("142a48"), c("1d3d66"), c("2c5a8e")]
TEAL = [c("0c3436"), c("17605e"), c("238579"), c("33aa96"), c("6fdcc4")]
PURP = [c("1e1036"), c("362064"), c("50348f"), c("7552c8"), c("a888ff")]
GOLD = [c("3a240e"), c("6e4a1a"), c("9a6c26"), c("c79634"), c("e8c25a")]
CRACK = c("c4fff0")

# (u, v, w, h, d, estilo) = addBox do KrakenModel.java
CUBES = [
    (0, 0, 20, 8, 24, "shell"), (0, 34, 16, 5, 18, "shell"), (48, 34, 10, 4, 12, "plate"),
    (80, 0, 2, 2, 20, "ridge"), (80, 22, 2, 2, 20, "ridge"),
    (0, 60, 14, 13, 16, "head"), (60, 60, 10, 3, 5, "jaw"),
    (80, 48, 4, 4, 2, "socket"), (96, 48, 2, 2, 1, "eye"), (104, 48, 1, 4, 1, "gill"),
    (112, 56, 4, 3, 4, "beak"),
    (0, 92, 4, 4, 5, "arm"), (24, 92, 3.3, 3.3, 7, "arm"), (48, 92, 2.92, 2.92, 7, "arm"),
    (72, 92, 2.54, 2.54, 7, "arm"), (96, 92, 2, 1, 2, "sucker"),
    (0, 118, 1, .5, 12, "crack"), (16, 118, 10, .5, 1, "crack"), (28, 118, 1, .5, 10, "crack"),
]

im = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
px = im.load()


def put(x, y, col):
    if 0 <= x < SIZE and 0 <= y < SIZE:
        px[x, y] = col


def pick(ramp, level):
    return ramp[max(0, min(len(ramp) - 1, level))]


def face(x0, y0, fw, fh, kind, style):
    """Pinta um retângulo de face. kind: top, bottom, side."""
    light = {"top": 1, "side": 0, "bottom": -1}[kind]
    for j in range(fh):
        for i in range(fw):
            x, y = x0 + i, y0 + j
            edge = i == 0 or j == 0 or i == fw - 1 or j == fh - 1
            if style == "crack":
                put(x, y, CRACK if (i + j) % 5 else TEAL[4])
            elif style == "eye":
                put(x, y, TEAL[4] if not edge else TEAL[3])
            elif style == "socket":
                put(x, y, BLUE[0] if edge else PURP[1])
            elif style == "beak":
                put(x, y, pick(GOLD, 2 + light + (1 if j < fh // 2 else 0) - (1 if edge else 0)))
            elif style == "gill":
                put(x, y, pick(PURP, 1 + (j % 2) * 2))
            elif style == "sucker":
                put(x, y, pick(TEAL, 3 + light) if (i + j) % 2 else pick(TEAL, 2 + light))
            elif style == "arm":
                lvl = 2 + light + (1 if (i // 2 + j // 3) % 2 else 0) + (rng.random() < .1)
                put(x, y, pick(PURP if (i + j) % 7 else BLUE, lvl - (1 if edge else 0)))
            elif style == "ridge":
                put(x, y, pick(TEAL, 2 + light + (1 if (j % 4 < 2) else 0)))
            elif style == "jaw":
                put(x, y, pick(BLUE, 1 + light + (j % 2)))
            else:
                # casco: placas azuis em escama (tijolo 4x3) com junta escura e brilho teal raro
                row = j // 3
                col = (i + (2 if row % 2 else 0)) % 4
                joint = col == 0 or j % 3 == 0
                lvl = 2 + light - (1 if joint else 0) + (1 if rng.random() < .12 else 0)
                base = BLUE
                if style == "head" and rng.random() < .08:
                    base = PURP
                col_ = pick(base, lvl)
                if style == "plate" and rng.random() < .06:
                    col_ = pick(TEAL, 3)
                if edge:
                    col_ = pick(base, max(0, lvl - 1))
                put(x, y, col_)


def box(u, v, w, h, d, style):
    W, H, D = math.ceil(w), math.ceil(h), math.ceil(d)
    face(u + D, v, W, D, "top", style)
    face(u + D + W, v, W, D, "bottom", style)
    face(u, v + D, D, H, "side", style)
    face(u + D, v + D, W, H, "side", style)
    face(u + D + W, v + D, D, H, "side", style)
    face(u + 2 * D + W, v + D, W, H, "side", style)


for cube in CUBES:
    box(*cube)

os.makedirs(os.path.dirname(OUT), exist_ok=True)
im.save(OUT, optimize=True)
print("Textura do Kraken gravada em", OUT)
