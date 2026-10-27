#!/usr/bin/env python3
"""Kit das máquinas animadas da 0.6.1 (faixa 7B; docs/12-industria.md, "Bíblia de arte das máquinas").

Cada máquina tem um `tools/models/machines/<máquina>/build_model.py` que descreve ossos, caixas, pintura e clipes com
este kit; rodar o script grava tudo de uma vez:

  - a folha de 128 x 128 px (32 px por face de bloco) e a `_glow` em `textures/block/machine/`;
  - `<Máquina>Model.java` (ossos e cubos do jogo com UV por face) e `<Máquina>Animations.java` no pacote
    `com.aquamarine.client.machine`;
  - `<máquina>.bbmodel` (Blockbench, UV por face), `rig.json` (Atlas, aba "Máquinas") e `uv.png` (gabarito);
  - o modelo JSON do item (pose de repouso) e, com `--preview`, uma prévia em `build/machines/`.

**Coordenadas de projeto (D):** x da esquerda para a direita de quem olha a frente da máquina, y para cima a partir do
chão e z da frente para trás, em pixels do bloco (0 a 16). O kit converte para o modelo Java (y para baixo, frente em -z,
chão em y = 24), o mesmo das criaturas. Rotações em graus, em torno dos eixos do modelo Java: +z abaixa o lado direito,
+x levanta o fundo de uma tampa e abaixa a frente; posição de animação com y para cima.

**UV por face (decisão da 7B):** o box-UV das criaturas gasta 128 x 64 px num cubo inteiro a 32 px por face e não cabe
na folha de 128; aqui cada face visível ganha o seu retângulo, e faces iguais podem dividir o mesmo. O lado de fora de
cada face é desenhado como quem olha para ela (topo com a frente embaixo e a esquerda à esquerda).
"""
from __future__ import annotations

import json
import math
import random
import sys
import uuid
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[3]
sys.path.insert(0, str(ROOT / "tools/models/spawnlings"))
import build_model as bm  # noqa: E402  (Clip, tremble e o formato dos keyframes)

Clip, tremble, C, L = bm.Clip, bm.tremble, bm.C, bm.L
JAVA = ROOT / "src/client/java/com/aquamarine/client/machine"
ASSETS = ROOT / "src/main/resources/assets/aquamarine"
SHEET = 128     # folha da máquina, em px
DENSITY = 2     # px por unidade do modelo: 16 unidades = 1 bloco = 32 px
GUTTER = 1
FACES = ("top", "bottom", "left", "right", "front", "back")
FACE_ID = {f: i for i, f in enumerate(FACES)}  # mesma ordem de MachineParts.java


# ================================================================ rampas (do escuro ao claro, luz do alto à esquerda)

def ramp(*hexes):
    return [tuple(int(h[i:i + 2], 16) for i in (0, 2, 4)) + (255,) for h in hexes]


BRASS = ramp("261b0d", "4a3618", "735a2a", "9c7d3b", "c2a255", "e2cb85")    # latão quente, ouro envelhecido
COPPER = ramp("341a10", "62301c", "93502c", "bf7244", "dd9764", "f2bf8f")
PATINA = ramp("17382f", "245a49", "347d63", "4fa083", "7fc6a9", "b9e6d2")   # limo verde do cobre nas juntas
STEEL = ramp("101318", "1c222b", "2b3440", "3e4a5a", "5a6a7e", "8798ad")    # aço escuro azulado
ROCK = ramp("0d0b10", "1a171e", "29252e", "39343f", "4a4451", "625a69")     # pedra escura de fenda
GLASS = ramp("10302f", "18484a", "236664", "3a8a84", "6cb8ad", "bfe9e0")    # vidro verde-água
LEATHER = ramp("1e1009", "3a2012", "56321c", "734629", "925f3b", "b07f57")  # couro das vedações
MAGMA = ramp("4a1305", "84260a", "c2440e", "ec7117", "ffa53a", "ffdc82")    # âmbar e laranja
CYAN = ramp("0b3442", "105469", "177e96", "35b4c8", "84e2ec", "d4fbff")     # água e energia
STEAM = ramp("7d95a8", "9fb4c4", "bed0dc", "dbe7ef", "eef5f9", "ffffff")    # vapor e ar
IVORY = ramp("5e5646", "877d66", "ada186", "cdc2a2", "e6dcbd", "f7f0d9")    # fundo dos mostradores
RING_ENERGY = ramp("0d2350", "16388a", "2357c7", "3f7ff0", "7fb0ff", "c7dcff")  # anel de ligação: energia
RING_FLUID = ramp("0f3d36", "176054", "22866f", "3aae8f", "73d3b3", "bdf2df")   # anel de ligação: fluido
RING_GAS = ramp("4a5058", "6a727c", "8e97a1", "b2bac3", "d0d6dc", "eef1f4")     # anel de ligação: gás
GLOW_HOT = (255, 214, 130, 255)
GLOW_AMBER = (255, 160, 60, 255)
GLOW_DEEP = (236, 100, 30, 255)
CLEAR = (0, 0, 0, 0)


def clamp(v, lo, hi):
    return max(lo, min(hi, v))


def mix(a, b, t):
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(4))


# ================================================================ geometria

class Bone:
    def __init__(self, name, parent, pivot, rot):
        self.name, self.parent = name, parent
        self.pivot = tuple(float(v) for v in pivot)  # D (absoluto)
        self.rot = tuple(float(v) for v in rot)

    @property
    def java(self):
        x, y, z = self.pivot
        return (x - 8.0, 24.0 - y, z - 8.0)


class Box:
    def __init__(self, machine, name, bone, at, size, paint, hide, glass, uv_from, same):
        self.machine, self.name, self.bone = machine, name, bone
        self.at = tuple(float(v) for v in at)
        self.size = tuple(float(v) for v in size)
        for v in self.size:
            assert v > 0 and abs(v * DENSITY - round(v * DENSITY)) < 1e-6, f"{name}: tamanho {self.size} fora da grade de meio px"
        self.paint, self.glass, self.uv_from = paint, glass, uv_from
        self.faces = [f for f in FACES if f not in hide]
        self.same = dict(same or {})
        self.part = bone + "_glass" if glass else bone
        self.uv: dict[str, tuple[int, int, int, int]] = {}

    def texels(self, face):
        w, h, d = self.size
        dims = {"front": (w, h), "back": (w, h), "left": (d, h), "right": (d, h), "top": (w, d), "bottom": (w, d)}[face]
        return tuple(int(round(v * DENSITY)) for v in dims)

    @property
    def java(self):
        """Canto mínimo no modelo Java, absoluto."""
        (x, y, z), (w, h, d) = self.at, self.size
        return (x - 8.0, 24.0 - (y + h), z - 8.0)


class Machine:
    def __init__(self, key, java_class, name, description):
        self.key, self.java_class, self.name, self.description = key, java_class, name, description
        self.bones: dict[str, Bone] = {}
        self.boxes: list[Box] = []
        self.clips: dict[str, Clip] = {}
        self.states: dict[str, dict] = {}
        self.descriptions: dict[str, str] = {}

    def bone(self, name, parent, pivot, rot=(0, 0, 0)):
        assert name not in self.bones and (parent is None or parent in self.bones), name
        self.bones[name] = Bone(name, parent, pivot, rot)
        return name

    def box(self, name, bone, at, size, paint=None, hide=(), glass=False, uv_from=None, same=None):
        assert bone in self.bones, bone
        assert all(b.name != name for b in self.boxes), name
        b = Box(self, name, bone, at, size, paint, hide, glass, uv_from, same)
        self.boxes.append(b)
        return b

    def find(self, name):
        return next(b for b in self.boxes if b.name == name)

    # Ossos de verdade do jogo: os da máquina mais um "<osso>_glass" por osso com vidro (passada translúcida).
    def parts(self):
        out = []
        for bone in self.bones.values():
            out.append((bone.name, bone.parent, bone))
            if any(b.glass and b.bone == bone.name for b in self.boxes):
                out.append((bone.name + "_glass", bone.name, None))
        return out

    def glass_parts(self):
        return sorted({b.part for b in self.boxes if b.glass})

    def pivot_java(self, part):
        bone = self.bones.get(part) or self.bones[part.removesuffix("_glass")]
        return bone.java

    def local_offset(self, part, parent):
        """Pivô do osso relativo ao pai (osso de vidro fica no pivô do dono)."""
        if part.endswith("_glass") and part not in self.bones:
            return (0.0, 0.0, 0.0)
        a = self.bones[part].java
        if parent is None:
            return a
        p = self.pivot_java(parent)
        return tuple(a[i] - p[i] for i in range(3))

    def box_local(self, b):
        a, p = b.java, self.pivot_java(b.part)
        return tuple(a[i] - p[i] for i in range(3))


# ================================================================ UV: um retângulo por face, empacotado em prateleiras

def pack(m):
    owners = [(b, f) for b in m.boxes if not b.uv_from for f in b.faces if f not in b.same]
    owners.sort(key=lambda bf: (-bf[0].texels(bf[1])[1], -bf[0].texels(bf[1])[0], bf[0].name, bf[1]))
    x = y = shelf = 0
    for b, f in owners:
        w, h = b.texels(f)
        if x + w > SHEET:
            x, y, shelf = 0, y + shelf + GUTTER, 0
        if y + h > SHEET:
            raise SystemExit(f"{m.key}: as faces não cabem em {SHEET}x{SHEET} (parou em {b.name}/{f})")
        b.uv[f] = (x, y, w, h)
        x += w + GUTTER
        shelf = max(shelf, h)
    for b in m.boxes:
        if b.uv_from:
            src = m.find(b.uv_from)
            assert src.size == b.size, f"{b.name}: uv_from {src.name} com outro tamanho"
            b.uv = dict(src.uv)
        for f in b.same:
            g = f
            while g in b.same:  # a face pode apontar para outra que também empresta
                g = b.same[g]
            assert b.texels(f) == b.texels(g), f"{b.name}: {f} e {g} têm tamanhos diferentes"
            if not b.uv_from:
                b.uv[f] = b.uv[g]
    used = sum(w * h for b, f in owners for (_, _, w, h) in [b.uv[f]])
    return used, y + shelf


# ================================================================ pintura

class Canvas:
    """Uma face na folha. (0, 0) é o canto de cima à esquerda de quem olha a face de fora."""

    def __init__(self, img, glow, rect, face, box):
        self.px, self.gx = img.load(), glow.load()
        self.x0, self.y0, self.w, self.h = rect
        self.face, self.box = face, box
        self.rng = random.Random(f"{box.machine.key}:{box.name}:{face}")

    @property
    def side(self):
        return self.face not in ("top", "bottom")

    def ok(self, x, y):
        return 0 <= x < self.w and 0 <= y < self.h

    def put(self, x, y, c):
        if self.ok(x, y):
            self.px[self.x0 + x, self.y0 + y] = c

    def get(self, x, y):
        return self.px[self.x0 + x, self.y0 + y] if self.ok(x, y) else CLEAR

    def lit(self, x, y, c):
        if self.ok(x, y):
            self.gx[self.x0 + x, self.y0 + y] = c

    def blend(self, x, y, c, t):
        if self.ok(x, y):
            self.put(x, y, mix(self.get(x, y), c, t))

    def fill(self, c):
        for y in range(self.h):
            for x in range(self.w):
                self.put(x, y, c)

    def rect(self, x0, y0, x1, y1, c):
        for y in range(max(0, y0), min(self.h, y1)):
            for x in range(max(0, x0), min(self.w, x1)):
                self.put(x, y, c)


def plate(c, R, base=3, grad=True, bevel=True):
    """Chapa de metal: degradê suave do alto, chanfro claro em cima e à esquerda, escuro embaixo e à direita."""
    top = len(R) - 1
    for y in range(c.h):
        for x in range(c.w):
            lv = base
            if grad and c.h >= 4 and c.side:
                lv += 1 if y < c.h * 0.22 else (-1 if y >= c.h * 0.8 else 0)
            elif grad and not c.side and c.w >= 6 and c.h >= 6:
                lv += 1 if x + y < (c.w + c.h) * 0.2 else (-1 if x + y > (c.w + c.h) * 0.85 else 0)
            if bevel:
                if (y == 0 and c.h > 2) or (x == 0 and c.w > 2):
                    lv += 1
                if (y == c.h - 1 and c.h > 2) or (x == c.w - 1 and c.w > 2):
                    lv -= 1
            c.put(x, y, R[clamp(lv, 0, top)])


def cylinder(c, R, base=3, light=0.3):
    """Peça redonda vista de lado: faixa de luz a ~30% da esquerda, bordas escurecendo (a coluna lê como cilindro)."""
    top = len(R) - 1
    if not c.side:
        plate(c, R, base, grad=False)
        for y in range(c.h):
            for x in range(c.w):
                dx, dy = (x + 0.5) / c.w - 0.5, (y + 0.5) / c.h - 0.5
                if dx * dx + dy * dy > 0.2:
                    c.put(x, y, R[clamp(base - 1, 0, top)])
        return
    for x in range(c.w):
        t = (x + 0.5) / c.w
        v = math.cos((t - light) * math.pi * 0.95)
        lv = base - 1 + round(2.2 * max(-0.5, v))
        if x == 0 or x == c.w - 1:
            lv -= 1
        for y in range(c.h):
            c.put(x, y, R[clamp(lv + (1 if y == 0 and c.h > 3 else 0), 0, top)])


def rivet(c, x, y, R):
    c.put(x, y, R[5])
    c.put(x + 1, y, R[4])
    c.put(x, y + 1, R[3])
    c.put(x + 1, y + 1, R[1])


def rivets(c, R, step=8, inset=1, edges="tblr"):
    """Rebites a cada 8 px nas bordas (cantos ficam para os parafusos)."""
    if "t" in edges or "b" in edges:
        for x in range(step // 2, c.w - 2, step):
            if "t" in edges:
                rivet(c, x, inset, R)
            if "b" in edges and c.h > 6:
                rivet(c, x, c.h - inset - 2, R)
    if "l" in edges or "r" in edges:
        for y in range(step // 2, c.h - 2, step):
            if "l" in edges:
                rivet(c, inset, y, R)
            if "r" in edges and c.w > 6:
                rivet(c, c.w - inset - 2, y, R)


def bolt(c, x, y, R):
    """Parafuso de canto, 3 x 3 com fenda."""
    for dx, dy, lv in ((0, 0, 4), (1, 0, 4), (2, 0, 3), (0, 1, 4), (1, 1, 2), (2, 1, 2), (0, 2, 3), (1, 2, 2), (2, 2, 1)):
        c.put(x + dx, y + dy, R[lv])
    c.put(x + 1, y + 1, R[0])


def corner_bolts(c, R, inset=1):
    if c.w >= 8 and c.h >= 8:
        for x, y in ((inset, inset), (c.w - inset - 3, inset), (inset, c.h - inset - 3), (c.w - inset - 3, c.h - inset - 3)):
            bolt(c, x, y, R)


def seam_h(c, y, R, x0=0, x1=None):
    for x in range(x0, c.w if x1 is None else x1):
        c.put(x, y, R[1])
        c.put(x, y + 1, R[4])


def seam_v(c, x, R, y0=0, y1=None):
    for y in range(y0, c.h if y1 is None else y1):
        c.put(x, y, R[1])
        c.put(x + 1, y, R[4])


def scratches(c, R, n, length=(2, 4)):
    """Arranhões curtos na diagonal (desgaste), sempre do tom mais claro."""
    for _ in range(n):
        x, y = c.rng.randrange(1, max(2, c.w - 2)), c.rng.randrange(1, max(2, c.h - 2))
        for k in range(c.rng.randint(*length)):
            c.put(x + k, y - k // 2, R[5] if k % 2 == 0 else R[4])


def patina(c, spots, size=2):
    """Limo verde do cobre: manchas pequenas nas juntas, com um ponto claro."""
    for x, y in spots:
        for dy in range(-size + 1, size):
            for dx in range(-size + 1, size):
                if abs(dx) + abs(dy) < size:
                    c.put(x + dx, y + dy, PATINA[2 if (dx + dy) % 2 else 3])
        c.put(x, y, PATINA[4])


def stain(c, cx, cy, r, color, t=0.45):
    """Mancha de óleo ou sal misturada à cor de baixo."""
    for y in range(cy - r, cy + r + 1):
        for x in range(cx - r, cx + r + 1):
            d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
            if d <= r:
                c.blend(x, y, color, t * (1 - d / (r + 1)))


def outline(c, color, sides="tblr"):
    for x in range(c.w):
        if "t" in sides:
            c.put(x, 0, color)
        if "b" in sides:
            c.put(x, c.h - 1, color)
    for y in range(c.h):
        if "l" in sides:
            c.put(0, y, color)
        if "r" in sides:
            c.put(c.w - 1, y, color)


def disc_mask(w, h, inset=0.0):
    """Pixels dentro de um círculo que ocupa o retângulo (para mostradores, anéis e o volante)."""
    out = set()
    rx, ry = w / 2 - inset, h / 2 - inset
    for y in range(h):
        for x in range(w):
            dx, dy = (x + 0.5 - w / 2) / rx, (y + 0.5 - h / 2) / ry
            if dx * dx + dy * dy <= 1.0:
                out.add((x, y))
    return out


DIGITS = {  # numerais 3 x 5 dos mostradores
    "0": ("111", "101", "101", "101", "111"), "1": ("010", "110", "010", "010", "111"),
    "2": ("111", "001", "111", "100", "111"), "3": ("111", "001", "011", "001", "111"),
    "4": ("101", "101", "111", "001", "001"), "5": ("111", "100", "111", "001", "111"),
    "6": ("111", "100", "111", "101", "111"), "7": ("111", "001", "010", "010", "010"),
    "8": ("111", "101", "111", "101", "111"), "9": ("111", "101", "111", "001", "111"),
}
MINI = {  # numerais 2 x 3, para mostradores pequenos
    "0": ("11", "11", "11"), "1": ("01", "01", "01"), "2": ("11", "01", "10"), "3": ("11", "01", "11"),
    "4": ("10", "11", "01"), "5": ("01", "10", "11"), "6": ("10", "11", "11"), "8": ("11", "00", "11"),
}


def text(c, x, y, s, color, font=None):
    font = font or DIGITS
    for ch in s:
        rows = font[ch]
        for j, row in enumerate(rows):
            for i, bit in enumerate(row):
                if bit == "1":
                    c.put(x + i, y + j, color)
        x += len(rows[0]) + 1


# ================================================================ folha final

def paint(m):
    img = Image.new("RGBA", (SHEET, SHEET), CLEAR)
    glow = Image.new("RGBA", (SHEET, SHEET), CLEAR)
    owned = []
    for b in m.boxes:
        if b.uv_from:
            continue
        for f in b.faces:
            if f in b.same:
                continue
            c = Canvas(img, glow, b.uv[f], f, b)
            if b.paint:
                b.paint(c)
            else:
                plate(c, STEEL)
            owned.append(b.uv[f])
    extrude(img, owned)
    return img, glow


def extrude(img, rects):
    """Repete a borda de cada face no vão vizinho: nada de preto transparente vazando nas bordas."""
    px = img.load()
    for x0, y0, w, h in rects:
        for x in range(x0, x0 + w):
            for y_src, y_dst in ((y0, y0 - 1), (y0 + h - 1, y0 + h)):
                if 0 <= y_dst < SHEET and px[x, y_dst][3] == 0:
                    px[x, y_dst] = px[x, y_src]
        for y in range(y0, y0 + h):
            for x_src, x_dst in ((x0, x0 - 1), (x0 + w - 1, x0 + w)):
                if 0 <= x_dst < SHEET and px[x_dst, y][3] == 0:
                    px[x_dst, y] = px[x_src, y]


def uv_guide(m):
    colors = {"top": (210, 190, 90, 255), "bottom": (120, 110, 70, 255), "front": (90, 190, 210, 255),
              "back": (60, 110, 130, 255), "left": (170, 120, 200, 255), "right": (120, 90, 150, 255)}
    img = Image.new("RGBA", (SHEET, SHEET), (16, 28, 40, 255))
    px = img.load()
    for b in m.boxes:
        if b.uv_from:
            continue
        for f in b.faces:
            if f in b.same:
                continue
            x0, y0, w, h = b.uv[f]
            for y in range(h):
                for x in range(w):
                    edge = x in (0, w - 1) or y in (0, h - 1)
                    px[x0 + x, y0 + y] = (235, 240, 230, 255) if edge else colors[f]
    return img


# ================================================================ saída: Java

def fmt(v):
    return bm.fmt(v)


def model_java(m):
    lines = []
    for name, parent, bone in m.parts():
        x, y, z = m.local_offset(name, parent)
        rx, ry, rz = (math.radians(v) for v in (bone.rot if bone else (0, 0, 0)))
        parent_s = f"\"{parent}\"" if parent else "null"
        lines.append(f"\t\tp.part(\"{name}\", {parent_s}, {fmt(x)}, {fmt(y)}, {fmt(z)}, {fmt(rx)}, {fmt(ry)}, {fmt(rz)});")
    for b in m.boxes:
        x, y, z = m.box_local(b)
        w, h, d = b.size
        faces = ", ".join(f"{FACE_ID[f]}, {b.uv[f][0]}, {b.uv[f][1]}, {b.uv[f][2]}, {b.uv[f][3]}" for f in b.faces)
        lines.append(f"\t\tp.box(\"{b.part}\", {fmt(x)}, {fmt(y)}, {fmt(z)}, {fmt(w)}, {fmt(h)}, {fmt(d)}, new int[] {{{faces}}}); // {b.name}")
    glass = ", ".join(f"\"{g}\"" for g in m.glass_parts())
    body = "\n".join(lines)
    return f"""package com.aquamarine.client.machine;

import com.aquamarine.Aquamarine;
import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;

/**
 * {m.name}: {m.description}
 * GERADO por tools/models/machines/{m.key}/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class {m.java_class}Model {{
	public static final Identifier TEXTURE = Aquamarine.id("textures/block/machine/{m.key}.png");
	public static final Identifier GLOW = Aquamarine.id("textures/block/machine/{m.key}_glow.png");
	/** Partes de vidro: vão só na passada translúcida. */
	public static final Set<String> GLASS = Set.of({glass});

	public static ModelPart create() {{
		MachineParts p = new MachineParts({SHEET}, {SHEET});
{body}
		return p.bake();
	}}

	private {m.java_class}Model() {{}}
}}
"""


def animations_java(m):
    vec = {"rotation": "rot", "position": "pos", "scale": "scale"}
    target = {"rotation": "ROT", "position": "POS", "scale": "SCALE"}
    interp = {C: "SMOOTH", L: "LINEAR"}
    out = ["package com.aquamarine.client.machine;", "",
           "import java.util.LinkedHashMap;",
           "import java.util.Map;",
           "import net.minecraft.client.animation.AnimationChannel;",
           "import net.minecraft.client.animation.AnimationDefinition;",
           "import net.minecraft.client.animation.Keyframe;",
           "import net.minecraft.client.animation.KeyframeAnimations;",
           "import org.joml.Vector3f;", "",
           f"/** Clipes de {m.name}. GERADO por tools/models/machines/{m.key}/build_model.py: não edite à mão. */",
           f"public final class {m.java_class}Animations {{",
           "\tprivate static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;",
           "\tprivate static final AnimationChannel.Target POS = AnimationChannel.Targets.POSITION;",
           "\tprivate static final AnimationChannel.Target SCALE = AnimationChannel.Targets.SCALE;",
           "\tprivate static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;",
           "\tprivate static final AnimationChannel.Interpolation LINEAR = AnimationChannel.Interpolations.LINEAR;", "",
           "\t/** Clipes pelo nome do contrato (idle, start, run, stop, overload...). */",
           "\tpublic static final Map<String, AnimationDefinition> CLIPS = clips();", ""]
    out.append("\tprivate static Map<String, AnimationDefinition> clips() {")
    out.append("\t\tMap<String, AnimationDefinition> clips = new LinkedHashMap<>();")
    for name in m.clips:
        out.append(f"\t\tclips.put(\"{name}\", {name}());")
    out += ["\t\treturn clips;", "\t}", ""]
    for name, clip in m.clips.items():
        out.append(f"\tprivate static AnimationDefinition {name}() {{")
        out.append(f"\t\treturn AnimationDefinition.Builder.withLength({fmt(clip.length)}){'.looping()' if clip.loop else ''}")
        for (bone, channel), track in clip.tracks.items():
            keys = [f"k({fmt(t)}, {vec[channel]}({fmt(v[0])}, {fmt(v[1])}, {fmt(v[2])}), {interp[mode]})"
                    for t, (v, mode) in sorted(track.items())]
            out.append(f"\t\t\t.addAnimation(\"{bone}\", new AnimationChannel({target[channel]},")
            for i, key in enumerate(keys):
                out.append(f"\t\t\t\t{key}{',' if i < len(keys) - 1 else '))'}")
        out.append("\t\t\t.build();")
        out.append("\t}")
        out.append("")
    out += ["\tprivate static Keyframe k(float time, Vector3f vector, AnimationChannel.Interpolation interpolation) {",
            "\t\treturn new Keyframe(time, vector, interpolation);", "\t}", "",
            "\tprivate static Vector3f rot(float x, float y, float z) {", "\t\treturn KeyframeAnimations.degreeVec(x, y, z);", "\t}", "",
            "\tprivate static Vector3f pos(float x, float y, float z) {", "\t\treturn KeyframeAnimations.posVec(x, y, z);", "\t}", "",
            "\tprivate static Vector3f scale(float x, float y, float z) {", "\t\treturn KeyframeAnimations.scaleVec(x, y, z);", "\t}", "",
            f"\tprivate {m.java_class}Animations() {{}}", "}", ""]
    return "\n".join(out)


# ================================================================ saída: Atlas, Blockbench e item

def clips_data(m):
    return {name: {"length": c.length, "loop": c.loop,
                   "tracks": [{"bone": b, "channel": ch, "keys": [[t, list(v), mode] for t, (v, mode) in sorted(k.items())]}
                              for (b, ch), k in c.tracks.items()]} for name, c in m.clips.items()}


def rig_json(m):
    bones = []
    for name, parent, bone in m.parts():
        bones.append({"name": name, "parent": parent or "root", "position": list(m.local_offset(name, parent)),
                      "rotation": list(bone.rot if bone else (0, 0, 0))})
    cubes = []
    for b in m.boxes:
        faces = {f: [b.uv[f][0], b.uv[f][1], b.uv[f][0] + b.uv[f][2], b.uv[f][1] + b.uv[f][3]] for f in b.faces}
        cubes.append({"name": b.name, "bone": b.part, "position": list(m.box_local(b)), "size": list(b.size), "faces": faces,
                      "glass": b.glass})
    return {"id": m.key, "name": m.name, "kind": "machine", "description": m.description, "bones": bones, "cubes": cubes,
            "resolution": [SHEET, SHEET], "clips": clips_data(m), "states": m.states, "clipDescriptions": m.descriptions}


def to_block(p):
    """Ponto do modelo Java para o bloco virado ao norte (JSON e Blockbench): x espelha, y sobe a partir do chão."""
    x, y, z = p
    return (8.0 - x, 24.0 - y, 8.0 + z)


JSON_FACE = {"top": "up", "bottom": "down", "front": "north", "back": "south", "left": "east", "right": "west"}


def item_json(m, texture, particle):
    """Modelo do item: a máquina parada, com a mesma folha (o bloco no mundo é desenhado pelo renderizador)."""
    elements = []
    for b in m.boxes:
        x, y, z = b.java  # pose de repouso sem a rotação dos ossos (o JSON só gira 22,5 em 22,5 graus)
        w, h, d = b.size
        lo, hi = to_block((x + w, y + h, z)), to_block((x, y, z + d))
        frm, to = [min(lo[i], hi[i]) for i in range(3)], [max(lo[i], hi[i]) for i in range(3)]
        faces = {}
        for f in b.faces:
            u0, v0, fw, fh = b.uv[f]
            k = 16 / SHEET
            uv = [u0 * k, v0 * k, (u0 + fw) * k, (v0 + fh) * k]
            face = {"uv": [round(v, 4) for v in uv], "texture": "#machine"}
            if f in ("top", "bottom"):
                face["rotation"] = 180
            faces[JSON_FACE[f]] = face
        elements.append({"name": b.name, "from": [round(v, 4) for v in frm], "to": [round(v, 4) for v in to], "faces": faces})
    return {"parent": "minecraft:block/block", "textures": {"machine": texture, "particle": particle}, "elements": elements}


def bbmodel(m):
    counter = [0]

    def uid():
        counter[0] += 1
        return str(uuid.uuid5(uuid.NAMESPACE_URL, f"aquamarine:machine:{m.key}:{counter[0]}"))

    groups, roots, elements = {}, [], []
    for name, parent, bone in m.parts():
        origin = to_block(m.pivot_java(name))
        rot = bone.rot if bone else (0, 0, 0)
        groups[name] = {"name": name, "uuid": uid(), "origin": list(origin), "rotation": [-rot[0], -rot[1], rot[2]], "children": []}
        (groups[parent]["children"] if parent else roots).append(groups[name])
    for b in m.boxes:
        x, y, z = b.java
        w, h, d = b.size
        lo, hi = to_block((x + w, y + h, z)), to_block((x, y, z + d))
        bb_faces = {"north": "front", "south": "back", "east": "left", "west": "right", "up": "top", "down": "bottom"}
        faces = {}
        for bbf, f in bb_faces.items():
            if f in b.faces:
                u0, v0, fw, fh = b.uv[f]
                faces[bbf] = {"uv": [u0, v0, u0 + fw, v0 + fh], "texture": 0, **({"rotation": 180} if f in ("top", "bottom") else {})}
            else:
                faces[bbf] = {"uv": [0, 0, 0, 0], "texture": None}
        e = {"name": b.name, "uuid": uid(), "from": [min(lo[i], hi[i]) for i in range(3)], "to": [max(lo[i], hi[i]) for i in range(3)],
             "origin": list(to_block(m.pivot_java(b.part))), "rotation": [0, 0, 0], "box_uv": False, "faces": faces}
        elements.append(e)
        groups[b.part]["children"].append(e["uuid"])
    animations = []
    for name, clip in m.clips.items():
        animators = {}
        for (bone, channel), track in clip.tracks.items():
            keys = []
            for t, (v, mode) in sorted(track.items()):
                if channel == "rotation":
                    v = (-v[0], -v[1], v[2])
                elif channel == "position":
                    v = (-v[0], v[1], v[2])
                keys.append({"channel": channel, "data_points": [{"x": v[0], "y": v[1], "z": v[2]}], "time": t,
                             "interpolation": "catmullrom" if mode == C else "linear", "uuid": uid()})
            animators.setdefault(groups[bone]["uuid"], {"name": bone, "type": "bone", "keyframes": []})["keyframes"] += keys
        animations.append({"name": name, "uuid": uid(), "loop": "loop" if clip.loop else "once", "length": clip.length,
                           "override": False, "animators": animators})
    return {"meta": {"format_version": "4.10", "model_format": "free", "box_uv": False}, "name": m.java_class,
            "model_identifier": m.key, "resolution": {"width": SHEET, "height": SHEET}, "elements": elements, "outliner": roots,
            "textures": [{"name": f"{m.key}.png", "id": "0", "path": f"{m.key}.png", "width": SHEET, "height": SHEET,
                          "uv_width": SHEET, "uv_height": SHEET}], "animations": animations}


# ================================================================ prévia em software (só para quem desenha; não vai ao jogo)

def sample(track, t):
    keys = sorted(track.items())
    if t <= keys[0][0]:
        return keys[0][1][0]
    if t >= keys[-1][0]:
        return keys[-1][1][0]
    i = max(j for j in range(len(keys)) if keys[j][0] <= t)
    (t0, (a, _)), (t1, (b, mode)) = keys[i], keys[i + 1]
    f = (t - t0) / (t1 - t0)
    if mode == L:
        return tuple(a[k] + (b[k] - a[k]) * f for k in range(3))
    p, q = keys[max(0, i - 1)][1][0], keys[min(len(keys) - 1, i + 2)][1][0]
    return tuple(0.5 * (2 * a[k] + (-p[k] + b[k]) * f + (2 * p[k] - 5 * a[k] + 4 * b[k] - q[k]) * f * f
                        + (-p[k] + 3 * a[k] - 3 * b[k] + q[k]) * f * f * f) for k in range(3))


def pose_matrices(m, layers):
    """Matriz 4x4 de cada osso no espaço do modelo (unidades), como o ModelPart do jogo: translada, gira ZYX, escala."""
    import numpy as np
    delta = {}
    for clip, t, w in layers:
        for (bone, channel), track in clip.tracks.items():
            v = sample(track, t)
            d = delta.setdefault(bone, {"rotation": [0, 0, 0], "position": [0, 0, 0], "scale": [0, 0, 0]})
            for k in range(3):
                d[channel][k] += w * ((v[k] - 1) if channel == "scale" else v[k])
    out = {}
    for name, parent, bone in m.parts():
        off = list(m.local_offset(name, parent))
        rot = list(bone.rot if bone else (0, 0, 0))
        sc = [1.0, 1.0, 1.0]
        d = delta.get(name)
        if d:
            rot = [rot[k] + d["rotation"][k] for k in range(3)]
            off = [off[0] + d["position"][0], off[1] - d["position"][1], off[2] + d["position"][2]]
            sc = [1 + d["scale"][k] for k in range(3)]
        rx, ry, rz = (math.radians(v) for v in rot)
        cx, sx, cy, sy, cz, sz = math.cos(rx), math.sin(rx), math.cos(ry), math.sin(ry), math.cos(rz), math.sin(rz)
        Rx = np.array([[1, 0, 0], [0, cx, -sx], [0, sx, cx]])
        Ry = np.array([[cy, 0, sy], [0, 1, 0], [-sy, 0, cy]])
        Rz = np.array([[cz, -sz, 0], [sz, cz, 0], [0, 0, 1]])
        M = np.eye(4)
        M[:3, :3] = Rz @ Ry @ Rx @ np.diag(sc)
        M[:3, 3] = off
        out[name] = (out[parent] @ M) if parent else M
    return out


def render_view(m, img, glow, layers, yaw, pitch, size=360, scale=None, tint=(1, 1, 1)):
    import numpy as np
    tex = np.asarray(img, dtype=np.float32) / 255
    gtex = np.asarray(glow, dtype=np.float32) / 255
    mats = pose_matrices(m, layers)
    cy_, sy_, cp, sp = math.cos(yaw), math.sin(yaw), math.cos(pitch), math.sin(pitch)
    # Mundo do bloco (x leste, y cima): do modelo Java x' = -x, y' = 24 - y; câmera ortográfica.
    right = np.array([cy_, 0, -sy_])
    up = np.array([-sy_ * sp, cp, -cy_ * sp])
    fwd = np.array([sy_ * cp, sp, cy_ * cp])
    scale = scale or size / 26.0
    color = np.zeros((size, size, 3), np.float32) + np.array([0.035, 0.1, 0.12])
    depth = np.full((size, size), 1e9, np.float32)
    light_dir = np.array([-0.45, 0.8, -0.4])
    light_dir /= np.linalg.norm(light_dir)
    corners = {  # vértices de cada face como no ModelPart.Cube (ordem com a UV v0=(u2,v1) v1=(u1,v1) v2=(u1,v2) v3=(u2,v2))
        "top": (5, 4, 0, 1), "bottom": (2, 3, 7, 6), "left": (0, 4, 7, 3), "front": (1, 0, 3, 2), "right": (5, 1, 2, 6),
        "back": (4, 5, 6, 7)}
    glass_quads = []

    def project(p):
        world = np.array([-p[0], 24 - p[1], p[2]])
        return np.array([size / 2 + world @ right * scale, size * 0.62 - (world @ up) * scale, -(world @ fwd)])

    def raster(P, uv, normal, alpha_mode):
        nonlocal color, depth
        for tri in ((0, 1, 2), (0, 2, 3)):
            a, b, c = (P[i] for i in tri)
            ta, tb, tc = (uv[i] for i in tri)
            xmin, xmax = int(max(0, min(a[0], b[0], c[0]))), int(min(size - 1, max(a[0], b[0], c[0])) + 1)
            ymin, ymax = int(max(0, min(a[1], b[1], c[1]))), int(min(size - 1, max(a[1], b[1], c[1])) + 1)
            if xmax <= xmin or ymax <= ymin:
                continue
            ys, xs = np.mgrid[ymin:ymax, xmin:xmax] + 0.5
            den = (b[1] - c[1]) * (a[0] - c[0]) + (c[0] - b[0]) * (a[1] - c[1])
            if abs(den) < 1e-9:
                continue
            w0 = ((b[1] - c[1]) * (xs - c[0]) + (c[0] - b[0]) * (ys - c[1])) / den
            w1 = ((c[1] - a[1]) * (xs - c[0]) + (a[0] - c[0]) * (ys - c[1])) / den
            w2 = 1 - w0 - w1
            inside = (w0 >= -1e-4) & (w1 >= -1e-4) & (w2 >= -1e-4)
            if not inside.any():
                continue
            z = w0 * a[2] + w1 * b[2] + w2 * c[2]
            u = w0 * ta[0] + w1 * tb[0] + w2 * tc[0]
            v = w0 * ta[1] + w1 * tb[1] + w2 * tc[1]
            iu = np.clip((u * SHEET).astype(int), 0, SHEET - 1)
            iv = np.clip((v * SHEET).astype(int), 0, SHEET - 1)
            texel = tex[iv, iu]
            gl = gtex[iv, iu]
            shade = 0.55 + 0.45 * max(0.0, float(normal @ light_dir))
            rgb = texel[..., :3] * shade * np.array(tint) + gl[..., :3] * gl[..., 3:4] * 0.9
            region_d = depth[ymin:ymax, xmin:xmax]
            region_c = color[ymin:ymax, xmin:xmax]
            if alpha_mode == "glass":
                ok = inside & (z < region_d) & (texel[..., 3] > 0.01)
                al = texel[..., 3:4]
                region_c[ok] = (region_c * (1 - al) + rgb * al)[ok]
            else:
                ok = inside & (z < region_d) & (texel[..., 3] > 0.5)
                region_c[ok] = rgb[ok]
                region_d[ok] = z[ok]

    for b in m.boxes:
        M = mats[b.part]
        x, y, z = m.box_local(b)
        w, h, d = b.size
        verts = [(x, y, z), (x + w, y, z), (x + w, y + h, z), (x, y + h, z), (x, y, z + d), (x + w, y, z + d), (x + w, y + h, z + d), (x, y + h, z + d)]
        vw = [(M @ np.array([*v, 1.0]))[:3] for v in verts]
        for f in b.faces:
            ids = corners[f]
            P = [project(vw[i]) for i in ids]
            e1, e2 = vw[ids[1]] - vw[ids[0]], vw[ids[2]] - vw[ids[0]]
            n = np.cross(e1, e2)
            n_world = np.array([-n[0], -n[1], n[2]])
            n_world = n_world / (np.linalg.norm(n_world) + 1e-9)
            if n_world @ fwd <= 0:
                continue  # face de costas para a câmera
            u0, v0, fw, fh = b.uv[f]
            u1, v1, u2, v2 = u0 / SHEET, v0 / SHEET, (u0 + fw) / SHEET, (v0 + fh) / SHEET
            uv = [(u2, v1), (u1, v1), (u1, v2), (u2, v2)]
            if b.glass:
                glass_quads.append((sum(p[2] for p in P) / 4, P, uv, n_world))
            else:
                raster(P, uv, n_world, "solid")
    for _, P, uv, n in sorted(glass_quads, key=lambda q: -q[0]):
        raster(P, uv, n, "glass")
    out = Image.fromarray((np.clip(color, 0, 1) * 255).astype("uint8"), "RGB")
    return out


def preview(m, img, glow, path, shots):
    """Folha de prévia: cada entrada é (rótulo, camadas [(clipe, t, peso)], yaw, pitch)."""
    tiles = [render_view(m, img, glow, layers, yaw, pitch) for _, layers, yaw, pitch in shots]
    w, h = tiles[0].size
    cols = min(4, len(tiles))
    rows = (len(tiles) + cols - 1) // cols
    sheet = Image.new("RGB", (cols * w, rows * h), (9, 25, 31))
    for i, t in enumerate(tiles):
        sheet.paste(t, ((i % cols) * w, (i // cols) * h))
    path.parent.mkdir(parents=True, exist_ok=True)
    sheet.save(path)
    return path


# ================================================================ gravação

def write(m, base, *, preview_shots=None, register=None, particle=None):
    used, height = pack(m)
    img, glow = paint(m)
    tex_dir = ASSETS / "textures/block/machine"
    tex_dir.mkdir(parents=True, exist_ok=True)
    img.save(tex_dir / f"{m.key}.png", optimize=True)
    glow.save(tex_dir / f"{m.key}_glow.png", optimize=True)
    base.mkdir(parents=True, exist_ok=True)
    uv_guide(m).save(base / "uv.png", optimize=True)
    (base / f"{m.key}.bbmodel").write_text(json.dumps(bbmodel(m), separators=(",", ":")), encoding="utf-8")
    rig = rig_json(m)
    (base / "rig.json").write_text(json.dumps(rig, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")
    JAVA.mkdir(parents=True, exist_ok=True)
    (JAVA / f"{m.java_class}Model.java").write_text(model_java(m), encoding="utf-8", newline="\n")
    (JAVA / f"{m.java_class}Animations.java").write_text(animations_java(m), encoding="utf-8", newline="\n")
    item = item_json(m, f"aquamarine:block/machine/{m.key}", particle or f"aquamarine:block/machine/{m.key}")
    (ASSETS / "models/block").mkdir(parents=True, exist_ok=True)
    (ASSETS / f"models/block/{m.key}_item.json").write_text(json.dumps(item, indent=1) + "\n", encoding="utf-8", newline="\n")
    print(f"{m.key}: {len(m.bones)} ossos, {len(m.boxes)} cubos, {len(m.clips)} clipes, "
          f"folha {SHEET}x{SHEET} com {used} px de faces ({100 * used / SHEET ** 2:.0f}%), até a linha {height}")
    if preview_shots:
        out = preview(m, img, glow, ROOT / "build/machines" / f"{m.key}_preview.png", preview_shots)
        print(f"prévia: {out}")
    if register:
        import base64
        import io
        sys.path.insert(0, str(ROOT / "tools/atlas"))
        from register_version import register as reg

        def uri(im):
            buf = io.BytesIO()
            im.save(buf, "PNG", optimize=True)
            return "data:image/png;base64," + base64.b64encode(buf.getvalue()).decode()
        version, label = register
        reg(rig | {"version": version, "texture": uri(img), "glow": uri(glow), "uv": uri(uv_guide(m)),
                   "notes": m.description}, label)
        print(f"Atlas: {m.key}/{version} registrada")
    return img, glow
