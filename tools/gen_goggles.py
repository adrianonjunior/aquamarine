"""Gera as texturas dos Óculos de Mergulho (ícone 16x16 e camada de armadura 64x32).

Uso: C:\\Python314\\python tools/gen_goggles.py
"""
import os
from PIL import Image

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources",
                    "assets", "aquamarine", "textures")

PAL = {
    ".": (0, 0, 0, 0),
    "O": (0x14, 0x20, 0x30, 255),  # contorno
    "B": (0xE3, 0xC3, 0x6A, 255),  # latão claro
    "b": (0xB0, 0x8A, 0x3C, 255),  # latão médio
    "d": (0x6F, 0x52, 0x24, 255),  # latão escuro
    "W": (0xFF, 0xFF, 0xFF, 255),  # brilho
    "L": (0x9A, 0xF2, 0xE8, 255),  # lente clara
    "l": (0x38, 0xC2, 0xC0, 255),  # lente média
    "m": (0x1B, 0x7C, 0x92, 255),  # lente funda
    "n": (0x10, 0x4A, 0x6A, 255),  # lente sombra
    "S": (0x2F, 0x55, 0x8C, 255),  # tira
    "s": (0x1B, 0x33, 0x5C, 255),  # tira escura
    "P": (0x8A, 0x5A, 0xC8, 255),  # roxo bioluminescente
}

LENS = [  # 7x8 por lente
    ".OOOOO.",
    "OBBBBBO",
    "OBLWlBO",
    "OBLllBO",
    "OblmmbO",
    "ObmnnbO",
    "OdbbbdO",
    ".OOOOO.",
]
BRIDGE = ["..", "..", "..", "bb", "dd", "..", "..", ".."]


def icon():
    rows = [""] * 16
    # tira em arco no topo, lentes embaixo (linhas 4..11), reflexo roxo na tira
    arc = [
        "....SSSSSSSS....",
        "..SSsSSSSSSsSS..",
        ".Ss..........sS.",
        ".S............S.",
    ]
    rows[:4] = arc
    for i in range(8):
        rows[4 + i] = LENS[i] + BRIDGE[i] + LENS[i]
    rows[12:] = ["................"] * 4
    # tiras laterais ligando as lentes ao arco
    rows[3] = ".S............S."
    img = Image.new("RGBA", (16, 16))
    for y, row in enumerate(rows):
        assert len(row) == 16, (y, row)
        for x, ch in enumerate(row):
            img.putpixel((x, y), PAL[ch])
    # centraliza verticalmente (conteúdo ocupa linhas 0..11)
    out = Image.new("RGBA", (16, 16))
    out.paste(img, (0, 2))
    return out


def armor_layer():
    """Camada 'humanoid' (64x32): só a cabeça (cubo 8x8x8 com UV padrão do vanilla)."""
    img = Image.new("RGBA", (64, 32), PAL["."])
    px = lambda x, y, c: img.putpixel((x, y), PAL[c])

    # Faces da cabeça: direita (0,8), frente (8,8), esquerda (16,8), trás (24,8); cada 8x8.
    # Altura dos olhos: linhas 11..13 da textura (y 3..5 dentro da face).
    # Tira de couro em volta (linhas 11..13), exceto onde ficam as lentes.
    for x in range(0, 32):
        px(x, 11, "S")
        px(x, 12, "s")
        px(x, 13, "S")
    # Frente (x 8..15): armação de latão com duas lentes 2x3.
    front = [
        "bBBbbBBb",  # linha 10: topo da armação
        "BLWBBLWB",  # 11
        "BlLBBlLB",  # 12
        "BmnBBmnB",  # 13
        "bddbbddb",  # 14: base da armação
    ]
    for i, row in enumerate(front):
        for j, ch in enumerate(row):
            px(8 + j, 10 + i, ch)
    # Fivelas de latão nas laterais (rosto de lado) e detalhe roxo atrás.
    for fx in (0, 16):
        px(fx + 6, 12, "b")
        px(fx + 7, 12, "B")
    px(24 + 3, 12, "P")
    px(24 + 4, 12, "P")
    # Contorno sutil acima/abaixo da tira nas laterais e trás
    for x in list(range(0, 8)) + list(range(16, 32)):
        px(x, 10, "O") if x not in (8,) else None
        px(x, 14, "O")
    return img


if __name__ == "__main__":
    os.makedirs(os.path.join(ROOT, "item"), exist_ok=True)
    os.makedirs(os.path.join(ROOT, "entity", "equipment", "humanoid"), exist_ok=True)
    icon().save(os.path.join(ROOT, "item", "diving_goggles.png"))
    armor_layer().save(os.path.join(ROOT, "entity", "equipment", "humanoid", "diving_goggles.png"))
    print("texturas geradas")
