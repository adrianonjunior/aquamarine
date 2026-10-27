"""Manual do Aquamarine (pedido de 27/10/2026): arte da tela e do item, modelo do item, receita e textos da interface.

A tela (ManualScreen) usa textures/gui/manual/book.png: o livro aberto (304 x 196) e, a partir de y = 216, as peças
(passo, pérola das dicas, casa de receita, seta, casa do resultado, setas de página e a aba que o código tinge).
Os prints ficam em textures/gui/manual/*.png (tools/gen_manual_shots.py) e o texto em assets/aquamarine/manual/*.json.
Pixel art determinística: sem ruído aleatório, as fibras do couro e do papel seguem padrões fixos.
"""
from pathlib import Path
import json
from PIL import Image
import gen_phase4_building as b

ROOT = Path(__file__).resolve().parents[1]
A = ROOT / 'src/main/resources/assets/aquamarine'
D = ROOT / 'src/main/resources/data/aquamarine'


def hexc(h, a=255):
    h = h.lstrip('#')
    return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4)) + (a,)


LEATHER = [hexc(c) for c in ('0a1a20', '102a33', '163844', '1d4653', '275a68', '3a7484')]
BRASS = [hexc(c) for c in ('261b0d', '4a3618', '735a2a', '9c7d3b', 'c2a255', 'e2cb85')]
PAPER = [hexc(c) for c in ('a8916a', 'c9b48a', 'dccaa2', 'e8d9b6', 'f1e6ca', 'f8f0dc')]
TEAL = [hexc(c) for c in ('0b3442', '105469', '177e96', '35b4c8', '84e2ec', 'd4fbff')]
INK = hexc('3b2f22')


def mix(a, b2, t):
    return tuple(round(a[i] + (b2[i] - a[i]) * t) for i in range(3)) + (255,)


def rect(img, x0, y0, x1, y1, c):
    for y in range(y0, y1):
        for x in range(x0, x1):
            img.putpixel((x, y), c)


def leather(img, x0, y0, x1, y1):
    """Couro azul-petróleo com fibra diagonal e borda gasta mais clara."""
    for y in range(y0, y1):
        for x in range(x0, x1):
            n = ((x * 7 + y * 13) % 11 == 0) + ((x * 3 - y * 5) % 17 == 0)
            base = LEATHER[2] if n == 0 else LEATHER[1] if n == 1 else LEATHER[3]
            img.putpixel((x, y), base)
    for x in range(x0, x1):
        img.putpixel((x, y0), LEATHER[0])
        img.putpixel((x, y1 - 1), LEATHER[0])
        img.putpixel((x, y0 + 1), LEATHER[4])
        img.putpixel((x, y1 - 2), LEATHER[1])
    for y in range(y0, y1):
        img.putpixel((x0, y), LEATHER[0])
        img.putpixel((x1 - 1, y), LEATHER[0])
        img.putpixel((x0 + 1, y), LEATHER[3])
        img.putpixel((x1 - 2, y), LEATHER[1])
    # costura em volta, a 3 px da borda
    for x in range(x0 + 4, x1 - 4, 3):
        img.putpixel((x, y0 + 3), LEATHER[4])
        img.putpixel((x, y1 - 4), LEATHER[3])
    for y in range(y0 + 4, y1 - 4, 3):
        img.putpixel((x0 + 3, y), LEATHER[4])
        img.putpixel((x1 - 4, y), LEATHER[3])


def corner(img, x, y, fx, fy):
    """Canto de latão em L com dois rebites (fx, fy = espelhamento)."""
    shape = [(i, j) for i in range(14) for j in range(14) if i < 4 or j < 4]
    for i, j in shape:
        px, py = x + (i if fx > 0 else -i), y + (j if fy > 0 else -j)
        edge = i == 0 or j == 0 or (i == 3 and j >= 3) or (j == 3 and i >= 3) or i == 13 or j == 13
        shade = BRASS[1] if edge else BRASS[4] if (i + j) < 6 else BRASS[3]
        img.putpixel((px, py), shade)
    for i, j in ((1, 9), (9, 1)):
        px, py = x + (i if fx > 0 else -i), y + (j if fy > 0 else -j)
        img.putpixel((px, py), BRASS[5])
        img.putpixel((px + (1 if fx > 0 else -1), py), BRASS[2])
        img.putpixel((px, py + (1 if fy > 0 else -1)), BRASS[2])


def page(img, x0, y0, x1, y1, spine_left):
    """Pergaminho: mais escuro perto da lombada e nas bordas, fibra horizontal clara."""
    w = x1 - x0
    for y in range(y0, y1):
        for x in range(x0, x1):
            d_spine = (x - x0) if spine_left else (x1 - 1 - x)
            d_edge = min(y - y0, y1 - 1 - y, (x1 - 1 - x) if spine_left else (x - x0))
            t = 1.0
            if d_spine < 8:
                t -= (8 - d_spine) * 0.09
            if d_edge < 3:
                t -= (3 - d_edge) * 0.08
            base = mix(PAPER[2], PAPER[4], max(0.0, min(1.0, t)))
            if (y * 5 + (x // 9) * 3) % 23 == 0 and 8 < d_spine:
                base = mix(base, PAPER[5], 0.5)
            img.putpixel((x, y), base)
    # ondinha clara no pé da página (marca d'água do mod)
    cx = x0 + w // 2
    for i in range(-14, 15):
        yy = y1 - 8 + (1 if (i // 3) % 2 == 0 else 0)
        img.putpixel((cx + i, yy), mix(PAPER[3], PAPER[2], 0.5))


def book():
    img = Image.new('RGBA', (512, 256), (0, 0, 0, 0))
    W, H = 304, 196
    leather(img, 0, 0, W, H)
    # bloco de folhas: degraus de papel embaixo e nas laterais (espessura)
    for k, c in enumerate((PAPER[1], PAPER[3], PAPER[1], PAPER[3])):
        rect(img, 9 - k // 2, 8 + k, 151, 189 + k // 2, c)
        rect(img, 153, 8 + k, 295 + k // 2, 189 + k // 2, c)
    page(img, 10, 7, 151, 188, spine_left=False)
    page(img, 153, 7, 294, 188, spine_left=True)
    # lombada: vinco escuro e costura
    for y in range(6, 190):
        img.putpixel((151, y), PAPER[0])
        img.putpixel((152, y), mix(PAPER[0], LEATHER[2], 0.4))
        if y % 6 == 0:
            img.putpixel((151, y), BRASS[2])
    for c, (x, y) in zip((0, 1, 2, 3), ((0, 0), (W - 1, 0), (0, H - 1), (W - 1, H - 1))):
        corner(img, x, y, 1 if x == 0 else -1, 1 if y == 0 else -1)
    # fita marcadora saindo do pé da lombada
    for y in range(184, 196):
        for x in range(149, 155):
            if y > 192 and (x == 151 or x == 152) and y - 192 > abs(x - 151.5) * 2:
                continue
            img.putpixel((x, y), TEAL[2] if x in (149, 154) else TEAL[3] if x < 152 else TEAL[2])
    sprites(img)
    return img


def disc(img, x0, y0, size, fill, edge, light):
    r = size / 2
    for j in range(size):
        for i in range(size):
            dx, dy = i + 0.5 - r, j + 0.5 - r
            d = (dx * dx + dy * dy) ** 0.5
            if d <= r:
                c = edge if d > r - 1.2 else light if dx + dy < -r * 0.6 else fill
                img.putpixel((x0 + i, y0 + j), c)


def slot(img, x0, y0, s, frame=False):
    for j in range(s):
        for i in range(s):
            if frame and (i < 2 or j < 2 or i >= s - 2 or j >= s - 2):
                c = BRASS[1] if i == 0 or j == 0 or i == s - 1 or j == s - 1 else BRASS[4] if i + j < s else BRASS[3]
            elif i == 0 or j == 0 or (frame and (i == 2 or j == 2)):
                c = PAPER[0]
            elif i == s - 1 or j == s - 1:
                c = PAPER[5]
            else:
                c = PAPER[2]
            img.putpixel((x0 + i, y0 + j), c)


def arrow(img, x0, y0, w, h, left, colors):
    """Seta de latão: haste e ponta, com luz em cima."""
    mid = h // 2
    head = h // 2 + 1
    for j in range(h):
        for i in range(w):
            ii = w - 1 - i if left else i
            in_head = ii >= w - head * 1 and abs(j - mid) <= (w - 1 - ii)
            in_shaft = ii < w - head and abs(j - mid) <= h // 5
            if in_head or in_shaft:
                c = colors[2] if j < mid else colors[1]
                if (in_head and abs(j - mid) == (w - 1 - ii)) or (in_shaft and abs(j - mid) == h // 5):
                    c = colors[0]
                img.putpixel((x0 + i, y0 + j), c)


def sprites(img):
    disc(img, 0, 216, 12, BRASS[4], BRASS[1], BRASS[5])           # passo
    disc(img, 14, 216, 6, hexc('e9e4d6'), hexc('9fb4c4'), hexc('ffffff'))  # pérola das dicas
    slot(img, 32, 216, 18)                                         # casa de receita
    arrow(img, 52, 216, 22, 15, False, (BRASS[1], BRASS[3], BRASS[4]))      # seta da receita
    slot(img, 76, 216, 26, frame=True)                             # casa do resultado
    arrow(img, 104, 216, 18, 12, True, (BRASS[1], BRASS[3], BRASS[5]))     # página anterior
    arrow(img, 104, 230, 18, 12, True, (TEAL[0], TEAL[2], TEAL[4]))
    arrow(img, 124, 216, 18, 12, False, (BRASS[1], BRASS[3], BRASS[5]))    # próxima página
    arrow(img, 124, 230, 18, 12, False, (TEAL[0], TEAL[2], TEAL[4]))
    # aba de capítulo: fita clara (o código tinge com a cor do capítulo), ponta arredondada à direita
    for j in range(18):
        for i in range(30):
            if i >= 27 and (j < 2 or j > 15) or i == 29 and (j < 4 or j > 13):
                continue
            edge = j == 0 or j == 17 or i == 29 or (i >= 27 and (j == 2 or j == 15))
            v = 120 if edge else 255 - (j * 4 if j > 9 else 0) - (12 if j < 2 else 0)
            img.putpixel((148 + i, 216 + j), (v, v, v, 255))


def icon():
    """Ícone 16 px: livro de couro azul-petróleo, cantos de latão, pérola na capa e folhas à mostra."""
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for y in range(2, 15):
        for x in range(2, 13):
            img.putpixel((x, y), LEATHER[2] if (x + y) % 5 else LEATHER[3])
        img.putpixel((2, y), LEATHER[0])
        img.putpixel((12, y), LEATHER[1])
        img.putpixel((3, y), LEATHER[4])
    for x in range(2, 13):
        img.putpixel((x, 1), LEATHER[0])
        img.putpixel((x, 15), LEATHER[0])
        img.putpixel((x, 2), LEATHER[4])
    for y in range(3, 15):
        img.putpixel((13, y), PAPER[4] if y % 2 else PAPER[2])
        img.putpixel((14, y), PAPER[1])
    img.putpixel((13, 2), LEATHER[0])
    img.putpixel((14, 15), LEATHER[0])
    for x, y in ((11, 2), (12, 2), (12, 3), (11, 14), (12, 14), (12, 13)):
        img.putpixel((x, y), BRASS[4])
    # moldura de latão e a pérola no meio
    for x in range(5, 11):
        img.putpixel((x, 5), BRASS[3])
        img.putpixel((x, 11), BRASS[2])
    for y in range(5, 12):
        img.putpixel((5, y), BRASS[3])
        img.putpixel((10, y), BRASS[2])
    for x, y, c in ((7, 7, 'ffffff'), (8, 7, 'd4fbff'), (7, 8, 'd4fbff'), (8, 8, '84e2ec'), (8, 9, '35b4c8'), (7, 9, '84e2ec'),
                    (6, 8, '84e2ec'), (9, 8, '35b4c8')):
        img.putpixel((x, y), hexc(c))
    # fita marcadora
    img.putpixel((9, 15), TEAL[3])
    img.putpixel((9, 14), TEAL[2])
    return img


def write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + '\n', encoding='utf-8', newline='\n')


def main():
    (A / 'textures/gui/manual').mkdir(parents=True, exist_ok=True)
    book().save(A / 'textures/gui/manual/book.png', optimize=True)
    icon().save(A / 'textures/item/manual.png', optimize=True)
    write(A / 'models/item/manual.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': 'aquamarine:item/manual'}})
    write(A / 'items/manual.json', {'model': {'type': 'minecraft:model', 'model': 'aquamarine:item/manual'}})
    write(D / 'recipe/manual.json', b.shapeless('manual', ['minecraft:book', 'minecraft:prismarine_shard']))
    b.merge_lang({
        'item.aquamarine.manual': ('Manual do Aquamarine', 'Aquamarine Manual'),
        'tooltip.aquamarine.manual': ('Passo a passo de tudo da indústria aquática, com prints.',
                                      'Step-by-step guide to the aquatic industry, with pictures.'),
        'tooltip.aquamarine.manual.2': ('Botão direito para abrir. Perdeu? Digite /aquamarine help.',
                                        'Right-click to open. Lost it? Type /aquamarine help.'),
        'manual.aquamarine.index': ('Sumário', 'Contents'),
        'manual.aquamarine.chapter': ('Capítulo %s', 'Chapter %s'),
        'manual.aquamarine.zoom': ('Clique para ampliar', 'Click to enlarge'),
        'manual.aquamarine.no_image': ('(imagem em breve)', '(picture coming soon)'),
        'manual.aquamarine.no_recipe': ('Receita no JEI: passe o mouse no item e aperte R.', 'Recipe in JEI: hover the item and press R.'),
        'manual.aquamarine.crafting': ('Na bancada de trabalho', 'At a crafting table'),
        'manual.aquamarine.station': ('Em: %s', 'At: %s'),
        'manual.aquamarine.shapeless': (', em qualquer ordem', ', in any order'),
        'commands.aquamarine.help.given': ('Você recebeu o Manual do Aquamarine. Botão direito com ele na mão abre de novo.',
                                           'You got the Aquamarine Manual. Right-click it to open it again.'),
        'commands.aquamarine.help.open': ('Manual do Aquamarine aberto (ele já está no seu inventário).',
                                          'Aquamarine Manual opened (it is already in your inventory).'),
        'commands.aquamarine.help.player': ('Só um jogador pode abrir o Manual.', 'Only a player can open the Manual.'),
    })
    print('Manual: arte, item, receita e textos da interface gerados')


if __name__ == '__main__':
    main()
