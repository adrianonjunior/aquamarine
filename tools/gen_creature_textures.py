"""Texturas das criaturas da Fase 3 (faixa 3B-T): Moréia Tempestuosa, Baleia-de-Nereia e Cachalote.

Uso (na raiz do repositório):
    C:\\Python314\\python tools/gen_creature_textures.py              # as três
    C:\\Python314\\python tools/gen_creature_textures.py moray        # só uma (moray, whale, sperm_whale)

Pinta cada caixa sobre o layout de UV do tools/models/<criatura>/build_model.py (lido do próprio
script, então rode este DEPOIS dele: o build_model sobrescreve os PNGs). Cada face recebe um
degradê dorso→barriga, manchas por sementes fixas e detalhes por nome de caixa. A Moréia também
ganha a camada que brilha (moray_glow.png: listras, olhos, cristas).
Layout de UV de caixa do jogo: [topo][base] na primeira faixa; [lado][frente][lado][trás] na segunda.
"""
import importlib.util
import os
import random
import sys

from PIL import Image

sys.dont_write_bytecode = True  # sem __pycache__ no repositório
REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(REPO, "src", "main", "resources", "assets", "aquamarine", "textures", "entity")


def load_model(name):
    path = os.path.join(REPO, "tools", "models", name, "build_model.py")
    spec = importlib.util.spec_from_file_location(f"aq_model_{name}", path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def mix(a, b, t):
    t = max(0.0, min(1.0, t))
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3)) + (255,)


def faces(u, v, dx, dy, dz):
    """Retângulos (nome, x, y, largura, altura) das seis faces de uma caixa."""
    return [
        ("top", u + dz, v, dx, dz),
        ("bottom", u + dz + dx, v, dx, dz),
        ("side", u, v + dz, dz, dy),
        ("front", u + dz, v + dz, dx, dy),
        ("side", u + dz + dx, v + dz, dz, dy),
        ("back", u + 2 * dz + dx, v + dz, dx, dy),
    ]


class Skin:
    """Material de pele: cores do dorso e da barriga, manchas e granulação."""

    def __init__(self, back, belly, spot, spot_density=0.0, spot_size=(1, 3), grain=10, stripe=None):
        self.back, self.belly, self.spot = back, belly, spot
        self.spot_density, self.spot_size, self.grain, self.stripe = spot_density, spot_size, grain, stripe

    def paint(self, img, rng, face, x0, y0, w, h):
        px = img.load()
        for y in range(h):
            for x in range(w):
                if face == "top":
                    t = 0.0
                elif face == "bottom":
                    t = 1.0
                else:
                    t = (y + 0.5) / h
                    t = t * t * (3 - 2 * t)  # suave: dorso escuro, barriga clara
                c = mix(self.back, self.belly, t)
                n = rng.randint(-self.grain, self.grain)
                px[x0 + x, y0 + y] = tuple(max(0, min(255, c[i] + n)) for i in range(3)) + (255,)
        if self.stripe and face in ("side", "front", "back") and h >= 6:
            row = int(h * 0.45)
            for x in range(w):
                if (x // 3) % 3 != 2:
                    px[x0 + x, y0 + row] = self.stripe
        spots = int(w * h * self.spot_density)
        for _ in range(spots):
            sx, sy = rng.randrange(w), rng.randrange(h)
            r = rng.randint(*self.spot_size)
            if face == "bottom" or (face != "top" and sy > h * 0.7):
                continue
            for yy in range(sy - r, sy + r + 1):
                for xx in range(sx - r, sx + r + 1):
                    if 0 <= xx < w and 0 <= yy < h and (xx - sx) ** 2 + (yy - sy) ** 2 <= r * r:
                        px[x0 + xx, y0 + yy] = self.spot


class Flat:
    """Cor quase lisa (olhos, dentes, brilho), com borda opcional."""

    def __init__(self, color, edge=None, grain=6):
        self.color, self.edge, self.grain = color, edge, grain

    def paint(self, img, rng, face, x0, y0, w, h):
        px = img.load()
        for y in range(h):
            for x in range(w):
                edge = self.edge and (x in (0, w - 1) or y in (0, h - 1)) and w > 2 and h > 2
                c = self.edge if edge else self.color
                n = rng.randint(-self.grain, self.grain)
                px[x0 + x, y0 + y] = tuple(max(0, min(255, c[i] + n)) for i in range(3)) + (255,)


class Fin:
    """Nadadeira: raios claros ao longo do comprimento sobre a cor de base."""

    def __init__(self, base, ray, tip):
        self.base, self.ray, self.tip = base, ray, tip

    def paint(self, img, rng, face, x0, y0, w, h):
        px = img.load()
        for y in range(h):
            for x in range(w):
                c = self.base
                if face in ("side", "front", "back"):
                    if y == 0:
                        c = self.tip
                    elif (x + y // 2) % 4 == 0:
                        c = self.ray
                n = rng.randint(-6, 6)
                px[x0 + x, y0 + y] = tuple(max(0, min(255, c[i] + n)) for i in range(3)) + (255,)


def paint_model(name, material_for, glow_for=None):
    model = load_model(name)
    size = model.SIZE
    uvs = model.pack_uv()
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    glow = Image.new("RGBA", (size, size), (0, 0, 0, 0)) if glow_for else None
    for index, (box, _origin, (dx, dy, dz), _uv, _bone) in enumerate(model.BOXES):
        u, v = uvs[box]
        rng = random.Random(f"{name}:{box}:{index}")
        material = material_for(box)
        for face, x, y, w, h in faces(u, v, dx, dy, dz):
            if w > 0 and h > 0:
                material.paint(img, rng, face, x, y, w, h)
        if glow is not None:
            glow_material = glow_for(box)
            if glow_material is not None:
                for face, x, y, w, h in faces(u, v, dx, dy, dz):
                    if w > 0 and h > 0:
                        glow_material(glow, img, face, x, y, w, h)
    folder = os.path.join(OUT, name)
    os.makedirs(folder, exist_ok=True)
    img.save(os.path.join(folder, f"{name}.png"))
    if glow is not None:
        glow.save(os.path.join(folder, f"{name}_glow.png"))
    print(f"textura {name}: {size}x{size}, {len(model.BOXES)} caixas")


# ---------------------------------------------------------------- Moréia Tempestuosa

MORAY_SKIN = Skin(back=(38, 66, 104), belly=(156, 184, 196), spot=(92, 140, 178), spot_density=0.035, spot_size=(1, 2),
                  stripe=None)
MORAY_HEAD = Skin(back=(34, 60, 98), belly=(150, 178, 192), spot=(96, 146, 184), spot_density=0.03, spot_size=(1, 2))
MORAY_GLOW = Flat((130, 240, 255), edge=(70, 190, 230))
MORAY_EYE = Flat((255, 236, 120), edge=(40, 20, 10))
MORAY_FANG = Flat((236, 232, 214), grain=4)
MORAY_FIN = Fin(base=(30, 52, 86), ray=(70, 120, 160), tip=(120, 220, 245))
MORAY_JAW = Skin(back=(140, 70, 80), belly=(170, 196, 204), spot=(140, 70, 80))


def moray_material(box):
    if "glow" in box:
        return MORAY_GLOW
    if "eye" in box:
        return MORAY_EYE
    if "fang" in box:
        return MORAY_FANG
    if any(k in box for k in ("dorsal", "ventral", "tail_fin", "crest", "frill", "barbel")):
        return MORAY_FIN
    if box == "jaw_plate":
        return MORAY_JAW
    if box in ("skull", "snout", "brow"):
        return MORAY_HEAD
    return MORAY_SKIN


def copy_pixels(glow, img, face, x, y, w, h):
    src = img.load()
    dst = glow.load()
    for yy in range(h):
        for xx in range(w):
            dst[x + xx, y + yy] = src[x + xx, y + yy]


def fin_edge(glow, img, face, x, y, w, h):
    if face in ("side", "front", "back"):
        dst = glow.load()
        for xx in range(w):
            dst[x + xx, y] = (130, 240, 255, 255)


def moray_glow(box):
    if "glow" in box or "eye" in box:
        return copy_pixels
    if any(k in box for k in ("dorsal", "tail_fin", "crest", "frill")):
        return fin_edge
    return None


# ---------------------------------------------------------------- Baleia-de-Nereia e Cachalote

WHALE_SKIN = Skin(back=(40, 78, 112), belly=(196, 220, 222), spot=(150, 220, 230), spot_density=0.004, spot_size=(1, 2), grain=8,
                  stripe=None)
WHALE_GLOW = Flat((120, 236, 220), edge=(60, 170, 170))
WHALE_EYE = Flat((170, 250, 240), edge=(20, 50, 60))
WHALE_FIN = Fin(base=(36, 70, 104), ray=(70, 120, 150), tip=(140, 230, 230))
SADDLE = Skin(back=(110, 70, 38), belly=(84, 52, 28), spot=(200, 160, 80), spot_density=0.01, grain=8)
SADDLE_METAL = Flat((214, 176, 84), edge=(120, 90, 40))


def whale_material(box):
    if "glow" in box or "luminous" in box:
        return WHALE_GLOW
    if "eye" in box:
        return WHALE_EYE
    if "saddle" in box:
        return SADDLE_METAL if "arch" in box else SADDLE
    if any(k in box for k in ("fin", "fluke", "pectoral", "dorsal", "sail", "spur")):
        return WHALE_FIN
    if "blowhole" in box:
        return Flat((24, 46, 70))
    return WHALE_SKIN


SPERM_SKIN = Skin(back=(58, 62, 72), belly=(120, 124, 132), spot=(150, 152, 160), spot_density=0.006, spot_size=(1, 2), grain=9)
SPERM_SCAR = Flat((196, 190, 184), edge=(140, 130, 126))
SPERM_EYE = Flat((30, 26, 24), edge=(140, 140, 150))
SPERM_FIN = Fin(base=(52, 56, 66), ray=(84, 88, 100), tip=(120, 124, 134))
SPERM_JAW = Skin(back=(206, 204, 198), belly=(180, 178, 172), spot=(150, 150, 150), spot_density=0.004)


def sperm_whale_material(box):
    if "scar" in box or "rake" in box:
        return SPERM_SCAR
    if "eye" in box:
        return SPERM_EYE
    if "jaw" in box:
        return SPERM_JAW
    if any(k in box for k in ("fin", "fluke", "pectoral", "dorsal")):
        return SPERM_FIN
    if "blowhole" in box:
        return Flat((30, 32, 38))
    return SPERM_SKIN


def main():
    names = [a for a in sys.argv[1:] if not a.startswith("-")] or ["moray", "whale", "sperm_whale"]
    for name in names:
        if name == "moray":
            paint_model("moray", moray_material, moray_glow)
        elif name == "whale":
            paint_model("whale", whale_material)
        elif name == "sperm_whale":
            paint_model("sperm_whale", sperm_whale_material)
        else:
            raise SystemExit(f"criatura desconhecida: {name}")


if __name__ == "__main__":
    main()
