"""Arte da Fase 6 (docs/11-fase6.md) que não é bloco de construção nem criatura.
6D3: textura do arpão em voo (`HarpoonModel`), uma por ponta e uma para o Cavitador, nas mesmas posições de UV.
6D2: a Forja Abissal vira modelo 3D no molde da Bigorna de Maré (4D): lareira com brasas acesas atrás de uma grade,
pilares e bancada dourados com a grade 3x3 gravada, cadinho de metal fundido e chaminé com anéis de latão. Grava as
texturas, o modelo e o estado de bloco; o ID, as receitas e a tela não mudam. Usa os pincéis e as rampas da 4D.
    C:\\Python314\\python tools/gen_phase6_art.py            # gera tudo
    C:\\Python314\\python tools/gen_phase6_art.py --preview  # e salva build/phase6_art_preview.png
"""
import json
import os
import sys

from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import gen_phase4_art as p4  # noqa: E402
from gen_phase4_art import BRASS, DEEP, GOLD, IRON, NS, ORANGE, STONE, Model, Tex  # noqa: E402

EMBER = p4.ramp("1a0603", "4a1206", "8a2a0a", "c8501a", "f08a2c", "ffc25a", "fff0b0")


# ================================================================ texturas da Forja Abissal (6D2)

def forge_brick():
    """Tijolos abissais escuros, desencontrados, com argamassa funda e pontos de brilho ciano."""
    t = Tex(601)
    t.noise((0, 0, 16, 16), [DEEP[2], DEEP[3], DEEP[3], DEEP[4]], [2, 3, 3, 1])
    for row in range(4):
        y = row * 4
        t.hline(0, 15, y + 3, DEEP[0])
        off = 0 if row % 2 == 0 else 4
        for x in range(off, 16 + off, 8):
            t.vline(x % 16, y, y + 2, DEEP[0])
            t.hline(x % 16 + 1, min(15, x % 16 + 6), y, DEEP[5])
    t.scatter(3, [p4.CYAN_SOFT], (1, 1, 15, 15))
    return t.img


def forge_top():
    """Bancada: chapa escura com a grade 3x3 incrustada em ouro e borda biselada."""
    t = Tex(602)
    t.noise((0, 0, 16, 16), [DEEP[3], DEEP[4], DEEP[3]], [3, 1, 2])
    t.bevel((0, 0, 16, 16), GOLD[4], GOLD[1])
    for i in (5, 10):
        t.hline(2, 13, i, GOLD[3])
        t.vline(i, 2, 13, GOLD[3])
    t.bevel((2, 2, 14, 14), GOLD[2], GOLD[4])
    for x, y in ((3, 3), (7, 7), (12, 12)):
        t.put(x, y, p4.CYAN_SOFT)
    return t.img


def forge_trim():
    """Faixa dourada lavrada (lados da bancada e pilares)."""
    t = Tex(603)
    t.noise((0, 0, 16, 16), [GOLD[2], GOLD[3], GOLD[3]], [1, 3, 2])
    for y in (0, 15):
        t.hline(0, 15, y, GOLD[1])
    for x in range(1, 16, 4):
        t.put(x, 7, GOLD[5])
        t.put(x + 1, 8, GOLD[1])
    return t.img


def forge_base():
    """Pedra do pé, com cracas."""
    t = Tex(604)
    t.noise((0, 0, 16, 16), [STONE[0], STONE[1], STONE[2]], [2, 3, 1])
    t.hline(0, 15, 0, STONE[3])
    for x, y in ((3, 10), (11, 6), (7, 13)):
        t.rect((x, y, x + 2, y + 2), p4.BARNACLE[2])
        t.put(x, y, p4.BARNACLE[4])
    return t.img


def forge_embers(frames=3):
    """Brasas da lareira (animadas): fundo negro, carvões que pulsam do vermelho ao amarelo."""
    sheet = Image.new("RGBA", (16, 16 * frames))
    for f in range(frames):
        t = Tex(610 + f)
        t.noise((0, 0, 16, 16), [EMBER[0], EMBER[1], EMBER[2]], [3, 2, 1])
        for y in range(9, 16):
            t.noise((0, y, 16, y + 1), [EMBER[2], EMBER[3], EMBER[4], EMBER[5]], [2, 3, 2, 1 + (y - 9) // 2])
        for _ in range(6):
            x, y = t.rng.randrange(1, 15), t.rng.randrange(4, 12)
            t.put(x, y, EMBER[6] if t.rng.random() < 0.4 else EMBER[5])
        sheet.paste(t.img, (0, 16 * f))
    return sheet


def forge_molten(frames=2):
    """Metal fundido do cadinho (animado)."""
    sheet = Image.new("RGBA", (16, 16 * frames))
    for f in range(frames):
        t = Tex(620 + f)
        t.noise((0, 0, 16, 16), [EMBER[4], EMBER[5], EMBER[5], EMBER[6]], [2, 3, 2, 1])
        t.scatter(5, [p4.CYAN_SOFT, (255, 255, 230, 255)])
        sheet.paste(t.img, (0, 16 * f))
    return sheet


def forge_metal():
    """Ferro do cadinho e da grade, com a boca dourada."""
    t = Tex(630)
    t.noise((0, 0, 16, 16), [IRON[2], IRON[3], IRON[3], IRON[4]], [1, 3, 2, 1])
    t.hline(0, 15, 0, GOLD[4])
    t.hline(0, 15, 1, GOLD[2])
    for x in (2, 8, 13):
        t.put(x, 6, IRON[6])
    return t.img


def forge_pipe():
    """Chaminé de latão com pátina e rebites."""
    t = Tex(640)
    for x in range(16):
        c = BRASS[1 + min(4, (x * 5) // 16 + (1 if 5 <= x <= 9 else 0))]
        t.vline(x, 0, 15, c)
    t.scatter(6, [p4.PATINA[2], p4.PATINA[3]])
    for y in (2, 13):
        for x in (3, 12):
            t.put(x, y, BRASS[5])
    return t.img


# ================================================================ arpão em voo (6D3)

# Redes de UV do HarpoonModel (x0, y0, x1, y1) e o material de cada peça.
HARPOON_PARTS = {"shaft": (0, 0, 4, 23), "grip": (4, 0, 12, 6), "collar": (4, 6, 16, 11), "blade": (4, 11, 12, 18),
                 "point": (12, 11, 16, 14), "barb_l": (16, 0, 20, 4), "barb_r": (20, 0, 24, 4), "fin_a": (16, 6, 26, 11),
                 "fin_b": (16, 12, 26, 20)}
HARPOON_STYLES = {
    "abyssal": {"blade": GOLD[1:], "trim": GOLD, "fin": p4.NAVY},
    "poison": {"blade": p4.ramp("1d3a12", "2f6a1c", "4f9a2a", "7ccf3f", "b8f27a"), "trim": IRON, "fin": p4.ramp("0c1a08", "15300f", "1f4716", "2c5f1f")},
    "fire": {"blade": ORANGE, "trim": GOLD, "fin": p4.ramp("2a0804", "4e1206", "7a1e0a", "a8300e")},
    "wither": {"blade": p4.ramp("0c0a10", "1d1824", "2e2638", "4a3d5a", "6e5e86"), "trim": p4.WHITE, "fin": DEEP},
    "shock": {"blade": p4.ramp("0c3a48", "157089", "22a6c4", "6fe0f0", "fff6a0"), "trim": BRASS, "fin": p4.NAVY},
    "cavitator": {"blade": p4.ramp("05060c", "0d1430", "1b2f6a", "3f7fd0", "9ff6ff"), "trim": p4.ramp("0d2f3a", "15586a", "2a8ea4", "6fd8e8", "c8fbff"), "fin": DEEP},
}


def harpoon_texture(style, seed):
    s = HARPOON_STYLES[style]
    t = Tex(seed, 32, 32)

    def fill(part, colors, light_col=True):
        x0, y0, x1, y1 = HARPOON_PARTS[part]
        for y in range(y0, y1):
            for x in range(x0, x1):
                c = colors[min(len(colors) - 1, max(0, len(colors) // 2 + t.rng.choice((-1, 0, 0, 1))))]
                t.put(x, y, c)
            if light_col:
                t.put(x0, y, colors[-1])

    fill("shaft", IRON[1:5])
    x0, y0, x1, y1 = HARPOON_PARTS["shaft"]
    for y in range(y0 + 4, y1, 5):  # anéis da cor da ponta ao longo da haste
        t.hline(x0, x1 - 1, y, s["trim"][3])
    fill("grip", p4.WOOD[2:6])
    x0, y0, x1, y1 = HARPOON_PARTS["grip"]
    for y in range(y0 + 1, y1, 2):
        t.hline(x0, x1 - 1, y, p4.WOOD[1])
    fill("collar", s["trim"][1:])
    blade = s["blade"]
    x0, y0, x1, y1 = HARPOON_PARTS["blade"]
    for y in range(y0, y1):  # lâmina: clara na ponta, escura no colar
        for x in range(x0, x1):
            k = len(blade) - 1 - (y - y0) * (len(blade) - 1) // max(1, y1 - y0 - 1)
            t.put(x, y, blade[max(0, k - (1 if x % 4 == 3 else 0))])
    fill("point", blade[-2:])
    fill("barb_l", blade[1:])
    fill("barb_r", blade[1:])
    fill("fin_a", s["fin"], light_col=False)
    fill("fin_b", s["fin"], light_col=False)
    if style == "cavitator":
        t.scatter(10, [p4.CYAN, (255, 255, 255, 255)], (0, 0, 26, 23))
    return t.img


def abyssal_forge():
    m = Model({"brick": "abyssal_forge_brick", "top": "abyssal_forge_worktop", "trim": "abyssal_forge_trim",
               "base": "abyssal_forge_base", "ember": "abyssal_forge_embers", "molten": "abyssal_forge_molten",
               "metal": "abyssal_forge_metal", "pipe": "abyssal_forge_pipe"}, "brick")
    m.box((0, 0, 0), (16, 2, 16), "base")
    m.box((1, 2, 1), (15, 11, 15), "brick")
    for x, z in ((0.5, 0.5), (13.5, 0.5), (0.5, 13.5), (13.5, 13.5)):  # pilares dourados
        m.box((x, 2, z), (x + 2, 11, z + 2), "trim")
    # Boca da lareira: moldura dourada, brasas acesas no fundo e três barras de ferro na frente.
    m.box((4, 4, 0.98), (12, 9, 1.0), "ember", faces=("north",), uv={"north": [0, 3, 16, 13]}, light=15)
    m.box((3, 3, 0.4), (13, 4, 1), "trim")
    m.box((3, 9, 0.4), (13, 10, 1), "trim")
    m.box((3, 4, 0.4), (4, 9, 1), "trim")
    m.box((12, 4, 0.4), (13, 9, 1), "trim")
    for x in (5.8, 7.6, 9.4):
        m.box((x, 4, 0.6), (x + 0.8, 9, 0.9), "metal", uv={f: [4, 4, 5, 9] for f in ("north", "south", "east", "west")})
    m.box((0, 11, 0), (16, 13, 16), {"*": "trim", "up": "top"})
    # Cadinho no canto de trás, com o metal fundido aceso; chaminé no outro canto.
    m.box((2, 13, 9), (7, 16, 14), "metal")
    m.box((2.5, 15.9, 9.5), (6.5, 15.95, 13.5), "molten", faces=("up",), uv={"up": [0, 0, 8, 8]}, light=15)
    m.box((11, 13, 10), (14, 22, 13), "pipe")
    for y in (15, 19.5):
        m.box((10.5, y, 9.5), (14.5, y + 1, 13.5), "metal")
    m.box((10, 22, 9), (15, 23, 14), {"*": "metal", "up": "brick"})
    return m


def main():
    textures = {"block/abyssal_forge_brick": forge_brick(), "block/abyssal_forge_worktop": forge_top(),
                "block/abyssal_forge_trim": forge_trim(), "block/abyssal_forge_base": forge_base(),
                "block/abyssal_forge_embers": forge_embers(), "block/abyssal_forge_molten": forge_molten(),
                "block/abyssal_forge_metal": forge_metal(), "block/abyssal_forge_pipe": forge_pipe()}
    for i, style in enumerate(HARPOON_STYLES):
        textures[f"entity/harpoon/{style}"] = harpoon_texture(style, 650 + i)
    for rel, img in textures.items():
        p4.write_png(img, rel)
    for name, frametime in (("abyssal_forge_embers", 6), ("abyssal_forge_molten", 12)):
        with open(os.path.join(p4.ASSETS, "textures", "block", f"{name}.png.mcmeta"), "w", encoding="utf-8", newline="\n") as f:
            json.dump({"animation": {"frametime": frametime, "interpolate": True}}, f)
            f.write("\n")
    p4.write_json(abyssal_forge().json(), "models/block/abyssal_forge.json")
    p4.write_json(p4.facing_states("abyssal_forge"), "blockstates/abyssal_forge.json")
    # As faces da forja antiga (cubo) saíram com o modelo 3D.
    for old in ("front", "side", "top", "bottom"):
        path = os.path.join(p4.ASSETS, "textures", "block", f"abyssal_forge_{old}.png")
        if os.path.exists(path):
            os.remove(path)

    if "--preview" in sys.argv:
        scale, pad = 6, 6
        sheet = Image.new("RGBA", (len(textures) * (16 * scale + pad), 16 * scale), (30, 34, 44, 255))
        for i, img in enumerate(textures.values()):
            sheet.alpha_composite(img.crop((0, 0, 16, 16)).resize((16 * scale, 16 * scale), Image.NEAREST), (i * (16 * scale + pad), 0))
        os.makedirs(os.path.join(p4.REPO, "build"), exist_ok=True)
        sheet.save(os.path.join(p4.REPO, "build", "phase6_art_preview.png"))
    print(f"fase 6 (6D2, 6D3): {len(textures)} texturas, Forja Abissal 3D e arpão em voo")


if __name__ == "__main__":
    main()
