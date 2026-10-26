"""Texturas do tentáculo, da Cria e do ovo do Kraken (128x128), pintadas sobre os gabaritos de UV
do Codex em tools/models/spawnlings/<criatura>/uv.png.

Uso (na raiz do repositório):
    python tools/gen_spawnlings_texture.py

Escreve src/main/resources/assets/aquamarine/textures/entity/<criatura>/<criatura>.png.
ATENÇÃO: tools/models/spawnlings/build_model.py copia o uv.png por cima desses arquivos; depois de
rodá-lo, rode este script de novo.

Como funciona: o gabarito tem a rede box-UV inteira de cada cubo (largura 2*(w+d), altura h+d),
com contorno creme nos cubos grandes e preenchimento que indica o "grupo" da peça (A azul-petróleo
escuro, B verde-água claro, C roxo); redes não se sobrepõem. Cada pixel
recebe a rampa do grupo mais próximo; a borda vira contorno escuro, o canto superior esquerdo de
cada face ganha luz e o resto leva ruído e pontos bioluminescentes. As cores são mais claras que as
do Kraken para ler de longe debaixo d'água.
"""
import os
import random
from collections import deque

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.normpath(os.path.join(HERE, ".."))
UV_DIR = os.path.join(HERE, "models", "spawnlings")
OUT_DIR = os.path.join(REPO, "src", "main", "resources", "assets", "aquamarine", "textures", "entity")

BG = (24, 42, 64, 255)
CREAM = {(225, 208, 146, 255), (235, 230, 200, 255)}
FAMILY = {(27, 110, 132, 255): "A", (44, 166, 163, 255): "B", (105, 75, 144, 255): "C"}


def c(h):
    h = h.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), 255)


# Rampas [contorno, sombra, base, luz, brilho]
RAMPS = {
    "tentacle": {
        "A": [c("0a3a42"), c("14707a"), c("27a6a8"), c("5fd9c8"), c("c4fff0")],
        "B": [c("0e4a50"), c("1d8c90"), c("3fc9bb"), c("8cf0dc"), c("e6fff8")],
        "C": [c("2a1a5c"), c("5236a0"), c("8a62e0"), c("b896ff"), c("f0e6ff")],
    },
    "young_kraken": {
        "A": [c("23125a"), c("4a2f9a"), c("7d58dc"), c("a98cff"), c("e6dcff")],
        "B": [c("0e4a50"), c("1d8c90"), c("3fc9bb"), c("8cf0dc"), c("e6fff8")],
        "C": [c("5a1a5c"), c("9a3a9a"), c("d070d8"), c("f0a8f4"), c("fff0ff")],
    },
    "kraken_egg": {
        "A": [c("0c4a60"), c("2380a0"), c("5cc0dc"), c("a4ecf8"), c("f0ffff")],
        "B": [c("0e5a58"), c("22a090"), c("5cdcc0"), c("b0fff0"), c("ffffff")],
        "C": [c("4a1a78"), c("8038c8"), c("b878ff"), c("e0b8ff"), c("fff0ff")],
    },
}
SPOT_CHANCE = {"tentacle": 0.07, "young_kraken": 0.06, "kraken_egg": 0.05}


def paint(name):
    uv = Image.open(os.path.join(UV_DIR, name, "uv.png")).convert("RGBA")
    w, h = uv.size
    src = uv.load()
    out = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    dst = out.load()
    rnd = random.Random(sum(map(ord, name)))
    ramps = RAMPS[name]

    def used(x, y):
        return 0 <= x < w and 0 <= y < h and src[x, y] != BG and src[x, y][3] > 0

    # família do pixel: preenchimento direto, ou a mais próxima (borda e diagonal creme)
    fam = {}
    queue = deque()
    for y in range(h):
        for x in range(w):
            f = FAMILY.get(src[x, y])
            if f:
                fam[(x, y)] = f
                queue.append((x, y))
    while queue:
        x, y = queue.popleft()
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            n = (x + dx, y + dy)
            if n not in fam and used(*n):
                fam[n] = fam[(x, y)]
                queue.append(n)

    def border(x, y):
        return src[x, y] in CREAM and any(not used(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))

    for y in range(h):
        for x in range(w):
            if not used(x, y):
                continue
            ramp = ramps[fam.get((x, y), "A")]
            if border(x, y):
                dst[x, y] = ramp[0]
                continue
            # luz no canto superior esquerdo de cada face, sombra no inferior direito
            top_left = border(x - 1, y) if used(x - 1, y) else True
            top_left = top_left or (border(x, y - 1) if used(x, y - 1) else True)
            bottom_right = (border(x + 1, y) if used(x + 1, y) else True) or (border(x, y + 1) if used(x, y + 1) else True)
            if top_left and not bottom_right:
                col = ramp[3]
            elif bottom_right and not top_left:
                col = ramp[1]
            else:
                col = ramp[2] if rnd.random() < 0.8 else ramp[3 if rnd.random() < 0.5 else 1]
            if rnd.random() < SPOT_CHANCE[name]:
                col = ramp[4]
            dst[x, y] = col
    path = os.path.join(OUT_DIR, name, name + ".png")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    out.save(path)
    return out


def main():
    imgs = [paint(n) for n in ("tentacle", "young_kraken", "kraken_egg")]
    if "--preview" in os.sys.argv:
        sheet = Image.new("RGBA", (3 * 390, 390), (58, 63, 71, 255))
        for n, im in enumerate(imgs):
            big = im.resize((384, 384), Image.NEAREST)
            sheet.paste(big, (n * 390, 0), big)
        path = os.path.join(REPO, "build", "spawnlings_preview.png")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        sheet.save(path)
        print("preview:", path)
    print("3 texturas geradas")


if __name__ == "__main__":
    main()
