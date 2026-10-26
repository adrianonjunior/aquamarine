#!/usr/bin/env python3
"""Cria, Ovo e tentáculo do Kraken, refeitos na Fase 4 (docs/10-fase4.md): geometria, textura com camada de
brilho, animações, o modelo e as animações em Java, o .bbmodel e o rig que o site (Atlas) lê. Tudo sai daqui:
mudar um bicho é mudar este arquivo e rodar

    C:\\Python314\\python tools/models/spawnlings/build_model.py

Convenções do jogo: coordenadas do modelo Java (y para baixo; pés da entidade em y = 24; frente em -z); box-UV
padrão do Minecraft (topo e base em cima; lados, frente e costas embaixo); rotação em graus; posição de animação
com y para cima (o posVec do jogo inverte o y); escala multiplica (1 = normal). Os renderers (escritos à mão)
desenham a textura e, por cima, a camada de brilho `<bicho>_glow.png` com luz própria.
"""
from __future__ import annotations

import json
import math
import random
import uuid
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[3]
BASE = Path(__file__).resolve().parent
JAVA = ROOT / "src/client/java/com/aquamarine/client/entity"
TEXTURES = ROOT / "src/main/resources/assets/aquamarine/textures/entity"
C, L = "catmullrom", "linear"
GUTTER = 1


# ================================================================ animações

class Clip:
    def __init__(self, length: float, loop: bool = False):
        self.length = length
        self.loop = loop
        self.tracks: dict[tuple[str, str], dict[float, tuple[tuple[float, float, float], str]]] = {}

    def keys(self, bone, channel, *keys):
        track = self.tracks.setdefault((bone, channel), {})
        for key in keys:
            t, value = key[0], key[1]
            interp = key[2] if len(key) > 2 else C
            if isinstance(value, (int, float)):
                value = (value, value, value) if channel == "scale" else (value, 0.0, 0.0)
            track[round(min(max(t, 0.0), self.length), 4)] = (tuple(round(float(v), 4) for v in value), interp)
        return self

    def rot(self, bone, *keys):
        return self.keys(bone, "rotation", *keys)

    def pos(self, bone, *keys):
        return self.keys(bone, "position", *keys)

    def scale(self, bone, *keys):
        return self.keys(bone, "scale", *keys)

    def sample(self, bone, channel, fn, samples=16):
        """Curva periódica amostrada (fecha o laço exatamente no primeiro valor)."""
        return self.keys(bone, channel, *[(self.length * k / samples, fn(self.length * k / samples)) for k in range(samples + 1)])

    def finish(self):
        for (bone, channel), track in self.tracks.items():
            rest = (1.0, 1.0, 1.0) if channel == "scale" else (0.0, 0.0, 0.0)
            if min(track) > 0:
                track[0.0] = (rest, C)
            if self.loop:
                track[self.length] = track[min(track)]
            elif max(track) < self.length:
                track[self.length] = (rest, C)
        return self


def tremble(t0, t1, base, amp, step=0.07):
    out, t, sign = [], t0, 1
    while t <= t1 + 1e-6:
        out.append((t, tuple(base[i] + sign * amp[i] for i in range(3)), L))
        t, sign = t + step, -sign
    return out


# ================================================================ materiais (rampas do escuro ao claro)

MATERIALS = {
    "skin": [(4, 10, 24), (8, 22, 44), (12, 34, 64), (20, 50, 88), (32, 70, 112), (52, 96, 140), (84, 130, 170)],
    "belly": [(26, 18, 56), (44, 32, 84), (64, 48, 112), (88, 68, 144), (116, 94, 176), (150, 128, 206)],
    "sucker": [(36, 20, 66), (62, 40, 104), (96, 68, 148), (134, 104, 190), (176, 148, 224)],
    "ridge": [(6, 36, 40), (12, 62, 64), (22, 96, 92), (40, 140, 124), (84, 196, 172)],
    "horn": [(14, 12, 16), (30, 26, 32), (50, 44, 52), (76, 68, 76), (110, 100, 106)],
    "eye": [(6, 30, 34), (20, 80, 86), (60, 160, 160), (150, 230, 224), (230, 255, 250)],
    "membrane": [(14, 52, 64), (22, 80, 92), (36, 112, 122), (60, 150, 156), (110, 196, 196), (180, 236, 232)],
    "embryo": [(8, 10, 30), (16, 22, 52), (28, 36, 80), (44, 56, 110), (66, 80, 140)],
    "root": [(12, 20, 30), (22, 34, 46), (34, 52, 66), (52, 74, 88), (78, 104, 116)],
}
GLOW_CYAN = (110, 255, 236, 255)
GLOW_SOFT = (70, 210, 220, 255)
GLOW_EYE = (200, 255, 246, 255)


# ================================================================ modelo

class Part:
    def __init__(self, name, parent, pivot, rot=(0.0, 0.0, 0.0)):
        self.name, self.parent = name, parent
        self.pivot, self.rot = tuple(float(v) for v in pivot), tuple(float(v) for v in rot)
        self.cubes: list[Cube] = []


class Cube:
    def __init__(self, part, origin, size, material, inflate=0.0, pattern="plain", alpha=255, feature=None, name=None,
                 face_material=None, tint=0, hollow=None):
        self.part, self.origin, self.size = part, tuple(float(v) for v in origin), tuple(float(v) for v in size)
        self.material, self.inflate, self.pattern, self.alpha, self.feature = material, inflate, pattern, alpha, feature
        self.face_material = face_material or {}  # face -> material (barriga, olho)
        self.tint = tint  # desloca a rampa (+ mais claro): degradê da raiz à ponta
        self.hollow = hollow or {}  # face -> borda pintada em px (0 = face toda vazia)
        self.name = name
        self.uv = None

    def net(self):
        w, h, d = (max(1, math.ceil(round(v, 4))) for v in self.size)
        return w, h, d, 2 * (w + d), h + d


class Creature:
    def __init__(self, key, java_class, tex_size, description, seed):
        self.key, self.java_class, self.tex_size, self.description = key, java_class, tex_size, description
        self.parts: dict[str, Part] = {}
        self.clips: dict[str, Clip] = {}
        self.extra_java = ""
        self.rng = random.Random(seed)

    def part(self, name, parent, pivot, rot=(0, 0, 0)):
        assert name not in self.parts, name
        assert parent is None or parent in self.parts, parent
        self.parts[name] = Part(name, parent, pivot, rot)
        return name

    def cube(self, part, origin, size, material, **kw):
        cube = Cube(part, origin, size, material, **kw)
        cube.name = cube.name or f"{part}_{len(self.parts[part].cubes)}"
        self.parts[part].cubes.append(cube)
        return cube

    def cubes(self):
        return [c for p in self.parts.values() for c in p.cubes]

    def children(self, name):
        return [p for p in self.parts.values() if p.parent == name]

    def absolute(self, name):
        p = self.parts[name]
        if p.parent is None:
            return p.pivot
        a = self.absolute(p.parent)
        return tuple(a[i] + p.pivot[i] for i in range(3))


# ================================================================ UV e textura

def pack(creature):
    """Prateleiras: maiores primeiro, 1 pixel livre entre as redes."""
    W, H = creature.tex_size
    cubes = sorted(creature.cubes(), key=lambda c: (-c.net()[4], -c.net()[3]))
    x = y = shelf = 0
    for c in cubes:
        nw, nh = c.net()[3], c.net()[4]
        if x + nw > W:
            x, y, shelf = 0, y + shelf + GUTTER, 0
        if y + nh > H:
            raise SystemExit(f"{creature.key}: as redes de UV não cabem em {W}x{H}")
        c.uv = (x, y)
        x += nw + GUTTER
        shelf = max(shelf, nh)


def faces(c):
    w, h, d, _, _ = c.net()
    u, v = c.uv
    return {"top": (u + d, v, w, d), "bottom": (u + d + w, v, w, d), "side_a": (u, v + d, d, h),
            "front": (u + d, v + d, w, h), "side_b": (u + d + w, v + d, d, h), "back": (u + 2 * d + w, v + d, w, h)}


def paint(creature):
    W, H = creature.tex_size
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    glow = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    px, gx = img.load(), glow.load()
    rng = creature.rng
    for c in creature.cubes():
        for face, (x0, y0, w, h) in faces(c).items():
            ramp = MATERIALS[c.face_material.get(face, c.material)]
            top = len(ramp) - 1
            shade = {"top": 1, "bottom": -2}.get(face, 0) + c.tint
            ring = c.hollow.get(face)
            for j in range(h):
                for i in range(w):
                    if ring is not None and ring <= i < w - ring and ring <= j < h - ring:
                        continue
                    level = top // 2 + shade
                    if face not in ("top", "bottom") and h > 2:
                        level += 1 if j < h * 0.3 else (-1 if j > h * 0.75 else 0)
                    if rng.random() < 0.14:
                        level += rng.choice((-1, 1))
                    if i in (0, w - 1) or j in (0, h - 1):
                        level -= 1
                    px[x0 + i, y0 + j] = ramp[max(0, min(top, level))] + (c.alpha,)
            if ring is None:
                pattern(c, face, x0, y0, w, h, px, gx, rng, ramp)
    return img, glow


def pattern(c, face, x0, y0, w, h, px, gx, rng, ramp):
    top = len(ramp) - 1
    kind = c.pattern
    if kind == "spots" and face in ("back", "side_a", "side_b", "top") and w > 2 and h > 2:
        for _ in range(max(1, w * h // 26)):
            cx, cy = rng.randrange(1, w - 1), rng.randrange(1, h - 1)
            rx, ry = rng.choice((1, 1, 2)), rng.choice((1, 1, 2))
            for j in range(-ry, ry + 1):
                for i in range(-rx, rx + 1):
                    if (i / rx) ** 2 + (j / ry) ** 2 <= 1.05 and 0 < cx + i < w - 1 and 0 < cy + j < h - 1:
                        px[x0 + cx + i, y0 + cy + j] = ramp[1 + rng.choice((0, 0, 1))] + (c.alpha,)
        if rng.random() < 0.8:
            bx, by = rng.randrange(1, w - 1), rng.randrange(1, h - 1)
            gx[x0 + bx, y0 + by] = GLOW_SOFT
            px[x0 + bx, y0 + by] = ramp[top] + (c.alpha,)
    elif kind == "bands" and face not in ("top", "bottom") and h > 3:
        for j in (0, 1):
            for i in range(1, w - 1):
                px[x0 + i, y0 + j] = ramp[1] + (c.alpha,)
        if face == "back" and w > 2:
            for j in range(2, h - 1, 3):
                gx[x0 + w // 2, y0 + j] = GLOW_SOFT
    elif kind == "sucker" and face == "front" and w >= 2 and h >= 2:
        for j in range(h):
            for i in range(w):
                edge = i in (0, w - 1) or j in (0, h - 1)
                px[x0 + i, y0 + j] = ramp[top - 1 if edge else 1] + (c.alpha,)
        gx[x0 + w // 2, y0 + h // 2] = GLOW_CYAN
        if w > 3:
            gx[x0 + w // 2 - 1, y0 + h // 2] = GLOW_SOFT
    elif kind == "eye" and face == c.feature:
        for j in range(h):
            for i in range(w):
                ring = i in (0, w - 1) or j in (0, h - 1)
                px[x0 + i, y0 + j] = (ramp[1] if ring else ramp[3]) + (255,)
                gx[x0 + i, y0 + j] = (0, 0, 0, 0) if ring else GLOW_EYE
        # Pupila em fenda horizontal, como a dos cefalópodes, e um ponto de brilho.
        for i in range(2, w - 2):
            px[x0 + i, y0 + h // 2] = (4, 8, 10, 255)
            gx[x0 + i, y0 + h // 2] = (0, 0, 0, 0)
        px[x0 + 1, y0 + 1] = ramp[top] + (255,)
    elif kind == "veins" and face != "bottom":
        for _ in range(max(1, (w + h) // 6)):
            x, y = rng.randrange(w), rng.randrange(h)
            for _ in range(rng.randint(3, 7)):
                if 0 <= x < w and 0 <= y < h:
                    px[x0 + x, y0 + y] = ramp[1] + (min(255, c.alpha + 40),)
                    if rng.random() < 0.1:
                        gx[x0 + x, y0 + y] = GLOW_SOFT
                x += rng.choice((-1, 0, 1))
                y += rng.choice((0, 1, 1))
    elif kind == "embryo" and face == "front" and w >= 3 and h >= 3:
        for i in (1, w - 2):
            px[x0 + i, y0 + 1] = (230, 255, 250, 255)
            gx[x0 + i, y0 + 1] = GLOW_CYAN
    elif kind == "row" and face == "front" and h >= 3:
        # Fileira de ventosas na parte de dentro dos braços.
        s = MATERIALS["sucker"]
        cx = (w - 1) // 2
        for j in range(1, h - 1, 2):
            px[x0 + cx, y0 + j] = s[3] + (c.alpha,)
            if w >= 3:
                px[x0 + cx + 1, y0 + j] = s[2] + (c.alpha,)
            if j % 4 == 1:
                gx[x0 + cx, y0 + j] = GLOW_SOFT
    elif kind == "horn" and face != "bottom" and h > 1:
        for i in range(w):
            px[x0 + i, y0] = ramp[top] + (255,)


def uv_guide(creature):
    W, H = creature.tex_size
    img = Image.new("RGBA", (W, H), (24, 42, 64, 255))
    px = img.load()
    for c in creature.cubes():
        for _, (x0, y0, w, h) in faces(c).items():
            for j in range(h):
                for i in range(w):
                    edge = i in (0, w - 1) or j in (0, h - 1)
                    px[x0 + i, y0 + j] = (191, 219, 226, 255) if edge else (44, 86, 104, 255)
    return img


# ================================================================ saída: Java, .bbmodel e rig do Atlas

def fmt(v):
    text = f"{v:.4f}".rstrip("0").rstrip(".")
    return (text if text not in ("-0", "") else "0") + "F"


def java_const(name):
    out = ""
    for ch in name:
        out += ("_" + ch) if ch.isupper() else ch.upper()
    return out


def model_java(cr, package):
    var = {name: "part_" + name for name in cr.parts}
    lines = []

    def emit(name, parent_var):
        p = cr.parts[name]
        builder = "CubeListBuilder.create()"
        for c in p.cubes:
            x, y, z = c.origin
            w, h, d = c.size
            builder += (f"\n\t\t\t.texOffs({c.uv[0]}, {c.uv[1]}).addBox({fmt(x)}, {fmt(y)}, {fmt(z)}, {fmt(w)}, {fmt(h)}, {fmt(d)}, "
                        f"new CubeDeformation({fmt(c.inflate)}))")
        rx, ry, rz = (math.radians(v) for v in p.rot)
        px, py, pz = p.pivot
        lines.append(f"\t\tPartDefinition {var[name]} = {parent_var}.addOrReplaceChild(\"{name}\", {builder},")
        lines.append(f"\t\t\tPartPose.offsetAndRotation({fmt(px)}, {fmt(py)}, {fmt(pz)}, {fmt(rx)}, {fmt(ry)}, {fmt(rz)}));")
        for child in cr.children(name):
            emit(child.name, var[name])

    for top in [p.name for p in cr.parts.values() if p.parent is None]:
        emit(top, "root")
    clips = list(cr.clips)
    fields = "\n".join(f"\tprivate final KeyframeAnimation {n};" for n in clips)
    bakes = "\n".join(f"\t\tthis.{n} = {cr.java_class}Animations.{java_const(n)}.bake(root);" for n in clips)
    applies = "\n".join(f"\t\tthis.{n}.apply(state.{n}, state.ageInTicks);" for n in clips)
    body = "\n".join(lines)
    return f"""package com.aquamarine.client.entity.{package};

import com.aquamarine.Aquamarine;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * {cr.description}
 * GERADO por tools/models/spawnlings/build_model.py: não edite à mão (mude o gerador e rode de novo).
 */
public final class {cr.java_class}Model extends EntityModel<{cr.java_class}RenderState> {{
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id("{cr.key}"), "main");
{fields}

	public {cr.java_class}Model(ModelPart root) {{
		super(root);
{bakes}
	}}

	public static LayerDefinition createBodyLayer() {{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
{body}
		return LayerDefinition.create(mesh, {cr.tex_size[0]}, {cr.tex_size[1]});
	}}

	@Override
	public void setupAnim({cr.java_class}RenderState state) {{
		this.resetPose();
{applies}
	}}
}}
"""


def animations_java(cr, package):
    vec = {"rotation": "rot", "position": "pos", "scale": "scale"}
    target = {"rotation": "ROT", "position": "POS", "scale": "SCALE"}
    interp = {C: "SMOOTH", L: "LINEAR"}
    out = [f"package com.aquamarine.client.entity.{package};", "",
           "import net.minecraft.client.animation.AnimationChannel;",
           "import net.minecraft.client.animation.AnimationDefinition;",
           "import net.minecraft.client.animation.Keyframe;",
           "import net.minecraft.client.animation.KeyframeAnimations;",
           "import org.joml.Vector3f;", "",
           "/** GERADO por tools/models/spawnlings/build_model.py: não edite à mão. */",
           f"public final class {cr.java_class}Animations {{",
           "\tprivate static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;",
           "\tprivate static final AnimationChannel.Target POS = AnimationChannel.Targets.POSITION;",
           "\tprivate static final AnimationChannel.Target SCALE = AnimationChannel.Targets.SCALE;",
           "\tprivate static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;",
           "\tprivate static final AnimationChannel.Interpolation LINEAR = AnimationChannel.Interpolations.LINEAR;", ""]
    for name in cr.clips:
        out.append(f"\tpublic static final AnimationDefinition {java_const(name)} = {name}();")
    out.append("")
    for name, clip in cr.clips.items():
        out.append(f"\tprivate static AnimationDefinition {name}() {{")
        out.append(f"\t\treturn AnimationDefinition.Builder.withLength({fmt(clip.length)}){'.looping()' if clip.loop else ''}")
        for (bone, channel), track in clip.tracks.items():
            keys = [f"k({fmt(t)}, {vec[channel]}({fmt(v[0])}, {fmt(v[1])}, {fmt(v[2])}), {interp[m]})"
                    for t, (v, m) in sorted(track.items())]
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
            f"\tprivate {cr.java_class}Animations() {{}}", "}", ""]
    return "\n".join(out)


def clips_data(cr):
    return {name: {"length": c.length, "loop": c.loop,
                   "tracks": [{"bone": b, "channel": ch, "keys": [[t, list(v), m] for t, (v, m) in sorted(k.items())]}
                              for (b, ch), k in c.tracks.items()]} for name, c in cr.clips.items()}


def rig_json(cr):
    bones = [{"name": p.name, "parent": p.parent or "root", "position": list(p.pivot), "rotation": list(p.rot)}
             for p in cr.parts.values()]
    cubes = [{"name": c.name, "bone": c.part, "position": list(c.origin), "size": list(c.size), "uv": list(c.uv),
              "inflate": c.inflate} for c in cr.cubes()]
    return {"bones": bones, "cubes": cubes, "resolution": list(cr.tex_size), "clips": clips_data(cr)}


def bbmodel(cr):
    counter = [0]

    def uid():
        counter[0] += 1
        return str(uuid.uuid5(uuid.NAMESPACE_URL, f"aquamarine:{cr.key}:{counter[0]}"))

    groups, elements, roots = {}, [], []
    for p in cr.parts.values():
        groups[p.name] = {"name": p.name, "uuid": uid(), "origin": list(cr.absolute(p.name)), "rotation": list(p.rot), "children": []}
    for p in cr.parts.values():
        (groups[p.parent]["children"] if p.parent else roots).append(groups[p.name])
    for c in cr.cubes():
        a = cr.absolute(c.part)
        frm = [a[i] + c.origin[i] for i in range(3)]
        to = [frm[i] + c.size[i] for i in range(3)]
        e = {"name": c.name, "uuid": uid(), "from": frm, "to": to, "inflate": c.inflate, "origin": list(a), "rotation": [0, 0, 0],
             "box_uv": True, "uv_offset": list(c.uv),
             "faces": {f: {"uv": [0, 0, 0, 0], "texture": 0} for f in ("north", "east", "south", "west", "up", "down")}}
        elements.append(e)
        groups[c.part]["children"].append(e["uuid"])
    return {"meta": {"format_version": "4.10", "model_format": "modded_entity", "box_uv": True},
            "name": cr.java_class, "model_identifier": cr.key, "resolution": {"width": cr.tex_size[0], "height": cr.tex_size[1]},
            "elements": elements, "outliner": roots,
            "textures": [{"name": f"{cr.key}.png", "id": "0", "path": f"{cr.key}.png", "width": cr.tex_size[0],
                          "height": cr.tex_size[1]}]}


def write(cr, package):
    pack(cr)
    img, glow = paint(cr)
    folder = TEXTURES / cr.key
    folder.mkdir(parents=True, exist_ok=True)
    img.save(folder / f"{cr.key}.png", optimize=True)
    glow.save(folder / f"{cr.key}_glow.png", optimize=True)
    out = BASE / cr.key
    out.mkdir(parents=True, exist_ok=True)
    uv_guide(cr).save(out / "uv.png", optimize=True)
    (out / f"{cr.key}.bbmodel").write_text(json.dumps(bbmodel(cr), separators=(",", ":")), encoding="utf-8")
    (out / "rig.json").write_text(json.dumps(rig_json(cr), separators=(",", ":")), encoding="utf-8")
    jdir = JAVA / package
    (jdir / f"{cr.java_class}Model.java").write_text(model_java(cr, package), encoding="utf-8", newline="\n")
    (jdir / f"{cr.java_class}Animations.java").write_text(animations_java(cr, package), encoding="utf-8", newline="\n")
    print(f"{cr.key}: {len(cr.parts)} partes, {len(cr.cubes())} cubos, {len(cr.clips)} animações, textura {cr.tex_size[0]}x{cr.tex_size[1]}")


# ================================================================ tentáculo de ataque

# (altura, meia-largura, curva em repouso em graus): afina devagar e faz um S, como um braço vivo.
TENTACLE_SEGMENTS = [(11, 11, 0), (10, 10.2, 3), (10, 9.5, 4), (10, 8.8, 4), (9, 8.1, 2), (9, 7.4, -2),
                     (9, 6.7, -4), (9, 6.0, -5), (8, 5.3, -4), (8, 4.6, -2), (8, 3.9, 3), (7, 3.2, 6)]
NOTCH = 0.64  # as duas caixas cruzadas têm 64% da largura: os cantos recuados arredondam a seção


def round_body(cr, part, y, h, r, name, front="belly", tint=0, pattern="spots"):
    """Seção quase redonda com duas caixas cruzadas (quadrado de cantos recuados); a de frente leva a barriga."""
    n = r * NOTCH
    cr.cube(part, (-r, y, -n), (2 * r, h, 2 * n), "skin", pattern=pattern, name=f"{name}_wide", tint=tint)
    cr.cube(part, (-n, y, -r), (2 * n, h, 2 * r), "skin", pattern=pattern, name=f"{name}_deep", tint=tint,
            face_material={"front": front} if front else None)
    return n


def tentacle():
    cr = Creature("tentacle", "Tentacle", (256, 256),
                  "Tentáculo de ataque do Kraken: um braço de ~8 blocos que sai da água fazendo um S, de seção arredondada, "
                  "com ventosas em zigue-zague que brilham na parte de dentro, espinhos nas costas e ponta em gancho.", 41)
    cr.part("base", None, (0, 24, 0))
    parent, py = "base", 0
    n = len(TENTACLE_SEGMENTS)
    for i, (h, r, bend) in enumerate(TENTACLE_SEGMENTS):
        name = f"seg_{i}"
        cr.part(name, parent, (0, py, 0), (bend, 0, 0))
        notch = round_body(cr, name, -h - 1, h + 1, r, name, tint=(i * 2) // n)
        size = 3 if r >= 7 else 2
        for k, side in enumerate((-1, 1)):
            frac = 0.32 if (i + k) % 2 == 0 else 0.74  # duas fileiras desencontradas
            cr.cube(name, (side * notch * 0.5 - size / 2, -h * frac - size / 2, -r - 1), (size, size, 1), "sucker",
                    pattern="sucker", name=f"{name}_sucker_{'l' if side < 0 else 'r'}")
        if 1 <= i <= 10:
            cr.cube(name, (-0.5, -h * 0.85, r - 1), (1, h * 0.55, 3), "ridge", pattern="bands", name=f"{name}_spine")
        parent, py = name, -h
    cr.part("tip", parent, (0, py, 0), (10, 0, 0))
    round_body(cr, "tip", -8, 8, 2.6, "tip", tint=1)
    cr.cube("tip", (-1, -6, -3.6), (2, 2, 1), "sucker", pattern="sucker", name="tip_sucker")
    cr.part("hook", "tip", (0, -8, 0), (-34, 0, 0))
    cr.cube("hook", (-1.5, -6, -1.5), (3, 6, 3), "skin", name="hook_body", tint=1)
    cr.part("hook_end", "hook", (0, -6, 0), (-46, 0, 0))
    cr.cube("hook_end", (-1, -4, -1), (2, 4, 2), "horn", pattern="horn", name="hook_claw")

    segs = [f"seg_{i}" for i in range(n)] + ["tip"]
    # Balanço contínuo: onda que sobe da raiz à ponta, em dois eixos, por cima da curva em S de repouso.
    idle = Clip(2.4, True)
    for i, s in enumerate(segs):
        idle.sample(s, "rotation", lambda t, i=i: ((1.6 + 0.7 * i) * math.sin(2 * math.pi * t / 2.4 - 0.45 * i), 0,
                                                    (1.2 + 0.55 * i) * math.cos(2 * math.pi * t / 2.4 - 0.4 * i + 0.7)), 12)
    idle.sample("hook", "rotation", lambda t: (6 * math.sin(2 * math.pi * t / 2.4 - 5), 0, 0), 12)
    cr.clips["idle"] = idle.finish()
    # Sobe da água enrolado, desenrola, arqueia para trás e treme de tensão até o golpe (aviso de 1,5 s).
    rise = Clip(1.5)
    rise.pos("base", (0, (0, -120, 0)), (0.55, (0, 4, 0)), (0.75, (0, -2, 0)), (1.0, (0, 0, 0)), (1.5, (0, 0, 0)))
    for i, s in enumerate(segs):
        rise.rot(s, (0, (20, 0, 0)), (0.5, (8, 0, 0)), (0.9, (-5, 0, 0)), *tremble(1.0, 1.5, (-5, 0, 0), (1.2 + 0.15 * i, 0, 0.8)))
    cr.clips["rise"] = rise.finish()
    # Chicote: a raiz puxa e a ponta chega depois; bate no chão, quica e volta.
    slam = Clip(1.35)
    for i, s in enumerate(segs):
        lag = 0.014 * i
        slam.rot(s, (0, (-5, 0, 0)), (0.06 + lag, (-9, 0, 0)), (0.2 + lag, (12, 0, 0), L), (0.32 + lag, (10, 0, 0)),
                 (0.42 + lag, (11.5, 0, 0)), (0.75, (8, 0, 0)), (1.35, (0, 0, 0)))
    slam.scale("seg_0", (0, 1), (0.2, (1.1, 0.94, 1.1), L), (0.4, 1))
    cr.clips["slam"] = slam.finish()
    # Agarrão: o meio para a frente, a ponta enrola e aperta em pulsos.
    grab = Clip(1.6)
    for i, s in enumerate(segs):
        curl = 3 if i < 4 else 22
        grab.rot(s, (0, (0, 0, 0)), (0.35, (curl * 0.6, 0, 0)), (0.6, (curl, 0, 0)), (0.9, (curl + 5, 0, 0)),
                 (1.1, (curl, 0, 0)), (1.3, (curl + 5, 0, 0)), (1.6, (curl, 0, 0)))
    for s in segs[4:]:
        grab.scale(s, (0, 1), (0.9, (0.9, 1, 0.9)), (1.1, 1), (1.3, (0.9, 1, 0.9)), (1.6, 1))
    cr.clips["grab"] = grab.finish()
    # Solta: desenrola e afunda.
    release = Clip(1.1)
    for i, s in enumerate(segs):
        release.rot(s, (0, (22 if i >= 4 else 3, 0, 0)), (0.4, (-5, 0, 0)), (1.1, (6, 0, 0)))
    release.pos("base", (0, (0, 0, 0)), (0.35, (0, 3, 0)), (1.1, (0, -110, 0)))
    cr.clips["release"] = release.finish()
    return cr


# ================================================================ Cria do Kraken

ARM_SEGMENTS = ((4.5, 2.4), (4.0, 1.9), (3.5, 1.5), (3.0, 1.1))  # (comprimento, largura)
FEEDER_SEGMENTS = ((5.0, 1.5), (5.0, 1.3), (4.5, 1.1), (4.0, 1.0), (3.5, 0.9))


def young_kraken():
    cr = Creature("young_kraken", "YoungKraken", (128, 128),
                  "Cria do Kraken: Kraken em miniatura com manto cônico e barbatanas na ponta, olhos grandes que brilham, "
                  "bico, seis braços com ventosas e dois tentáculos de caça longos com clava.", 52)
    cr.part("body", None, (0, 13, 0))
    # Manto mais estreito que a cabeça (proporção de filhote) e bem inclinado para trás.
    cr.part("mantle", "body", (0, -3, 2), (40, 0, 0))
    round_body(cr, "mantle", -8, 8, 4, "mantle_shell")
    round_body(cr, "mantle", -11.5, 4, 3, "mantle_upper", tint=1)
    round_body(cr, "mantle", -14, 3, 1.8, "mantle_tip", tint=1, pattern="plain")
    # Barbatanas nos lados do manto, varridas para trás (no topo pareciam orelhas).
    for side, name in ((-1, "fin_left"), (1, "fin_right")):
        cr.part(name, "mantle", (side * 3.2, -7, 0.5), (0, side * -25, side * -8))
        cr.cube(name, (0 if side > 0 else -3.5, -2.5, -0.5), (3.5, 4, 1), "skin", pattern="bands", name=f"{name}_web", tint=1)
        cr.cube(name, (0 if side > 0 else -2, -3.5, -0.5), (2, 1, 1), "skin", name=f"{name}_cap", tint=1)
    cr.part("head", "body", (0, 0, -1))
    round_body(cr, "head", -4.5, 7, 4.5, "head", front=None)
    for side, name in ((-1, "eye_left"), (1, "eye_right")):
        feature = "side_a" if side < 0 else "side_b"
        cr.cube("head", (4.5 if side > 0 else -5.5, -3.5, -3), (1, 5, 6), "skin", pattern="eye", feature=feature,
                face_material={feature: "eye"}, name=name)
    cr.part("beak", "head", (0, 2.5, -2.5))
    cr.cube("beak", (-1.5, 0, -2), (3, 2, 2.5), "horn", pattern="horn", name="beak_upper")
    cr.part("beak_lower", "beak", (0, 2, -0.5))
    cr.cube("beak_lower", (-1, 0, -1.5), (2, 1, 2), "horn", pattern="horn", name="beak_jaw")
    arms = []
    for k in range(6):
        a = math.radians(30 + 60 * k)
        root = f"arm_{k}"
        cr.part(root, "head", (math.sin(a) * 2.8, 2.5, math.cos(a) * 2.8), (34, math.degrees(a), 0))
        parent, prev_len = root, 0
        for j, (length, w) in enumerate(ARM_SEGMENTS):
            seg = f"arm_{k}_{j}"
            cr.part(seg, parent, (0, prev_len, 0), (0 if j == 0 else 10 + 2 * j, 0, 0))
            cr.cube(seg, (-w / 2, 0, -w / 2), (w, length + 0.4, w), "skin", pattern="row", name=f"{seg}_body",
                    face_material={"front": "belly"}, tint=1 if j >= 2 else 0)
            parent, prev_len = seg, length
            arms.append(seg)
    feeders = []
    for side in (-1, 1):
        root = f"feeder_{'l' if side < 0 else 'r'}"
        cr.part(root, "head", (side * 1.5, 2.5, -2.4), (40, 180 - side * 16, 0))
        parent, prev_len = root, 0
        for j, (length, w) in enumerate(FEEDER_SEGMENTS):
            seg = f"{root}_{j}"
            cr.part(seg, parent, (0, prev_len, 0), (0 if j == 0 else 13, 0, 0))
            cr.cube(seg, (-w / 2, 0, -w / 2), (w, length + 0.3, w), "skin", name=f"{seg}_body", tint=1 if j >= 2 else 0)
            parent, prev_len = seg, length
            feeders.append(seg)
        club = f"{root}_club"
        cr.part(club, parent, (0, prev_len, 0), (10, 0, 0))
        cr.cube(club, (-1.3, 0, -1.1), (2.6, 3.2, 2.2), "skin", pattern="row", name=f"{club}_pad",
                face_material={"front": "belly"}, tint=1)
        feeders.append(club)

    idle = Clip(2.2, True)
    idle.sample("body", "position", lambda t: (0, 0.7 * math.sin(2 * math.pi * t / 2.2), 0), 8)
    idle.sample("mantle", "scale", lambda t: (1 + 0.04 * math.sin(2 * math.pi * t / 2.2), 1 - 0.03 * math.sin(2 * math.pi * t / 2.2),
                                              1 + 0.04 * math.sin(2 * math.pi * t / 2.2)), 8)
    for side, fin in ((-1, "fin_left"), (1, "fin_right")):
        idle.sample(fin, "rotation", lambda t, s=side: (0, 0, s * 14 * math.sin(2 * math.pi * t / 1.1)), 8)
    for k in range(6):
        for j in range(len(ARM_SEGMENTS)):
            idle.sample(f"arm_{k}_{j}", "rotation", lambda t, k=k, j=j: ((5 + 4 * j) * math.sin(2 * math.pi * t / 2.2 - 0.7 * j + k * 1.05),
                                                                          (3 + 2 * j) * math.cos(2 * math.pi * t / 2.2 - 0.6 * j + k), 0), 8)
    for n, seg in enumerate(feeders):
        idle.sample(seg, "rotation", lambda t, n=n: (6 * math.sin(2 * math.pi * t / 2.2 - n * 0.45), 0, 0), 8)
    cr.clips["idle"] = idle.finish()

    swim = Clip(1.2, True)
    swim.rot("body", (0, (-24, 0, 0)), (1.2, (-24, 0, 0)))
    swim.scale("mantle", (0, 1), (0.3, (0.86, 1.1, 0.86)), (0.6, (1.06, 0.96, 1.06)), (1.2, 1))
    swim.pos("body", (0, (0, 0, 0)), (0.3, (0, 0, -1.2)), (0.6, (0, 0, -0.4)), (1.2, (0, 0, 0)))
    for k in range(6):
        for j in range(len(ARM_SEGMENTS)):
            swim.rot(f"arm_{k}_{j}", (0, (8, 0, 0)), (0.3, (-22 + 4 * j, 0, 0)), (0.6, (18, 0, 0)), (1.2, (8, 0, 0)))
    for seg in feeders:
        swim.rot(seg, (0, (4, 0, 0)), (0.3, (-12, 0, 0)), (0.7, (10, 0, 0)), (1.2, (4, 0, 0)))
    for side, fin in ((-1, "fin_left"), (1, "fin_right")):
        swim.sample(fin, "rotation", lambda t, s=side: (0, 0, s * 26 * math.sin(2 * math.pi * t / 0.4)), 12)
    cr.clips["swim"] = swim.finish()

    stunned = Clip(1.1, True)
    stunned.rot("body", (0, (0, 0, -18)), (0.55, (0, 0, 18)), (1.1, (0, 0, -18)))
    stunned.rot("head", (0, (10, 0, 0)), (0.55, (16, 0, 0)), (1.1, (10, 0, 0)))
    for seg in arms + feeders:
        stunned.rot(seg, (0, (22, 0, 0)), (0.55, (30, 0, 0)), (1.1, (22, 0, 0)))
    cr.clips["stunned"] = stunned.finish()

    hatch = Clip(1.8)
    hatch.scale("body", (0, 0.25), (0.6, (0.7, 0.5, 0.7)), (1.0, (1.15, 1.2, 1.15)), (1.3, (0.95, 0.95, 0.95)), (1.8, 1))
    hatch.pos("body", (0, (0, -4, 0)), (1.0, (0, 1.5, 0)), (1.8, (0, 0, 0)))
    for seg in arms + feeders:
        hatch.rot(seg, (0, (-60, 0, 0)), (0.8, (-40, 0, 0)), (1.2, (18, 0, 0)), (1.8, (0, 0, 0)))
    hatch.rot("body", (0, (0, 0, 0)), *tremble(1.2, 1.6, (0, 0, 0), (0, 0, 8), 0.08), (1.8, (0, 0, 0)))
    cr.clips["hatch"] = hatch.finish()

    attack = Clip(1.25)
    attack.scale("mantle", (0, 1), (0.42, (1.14, 1.06, 1.14)), (0.55, (0.88, 0.94, 0.88), L), (1.25, 1))
    attack.pos("body", (0, (0, 0, 0)), (0.42, (0, 0, 1.5)), (0.56, (0, 0, -4.5), L), (0.8, (0, 0, -2)), (1.25, (0, 0, 0)))
    attack.rot("beak_lower", (0, (0, 0, 0)), (0.45, (0, 0, 0)), (0.55, (32, 0, 0), L), (0.75, (5, 0, 0)), (0.85, (28, 0, 0)),
               (1.25, (0, 0, 0)))
    for seg in arms:
        attack.rot(seg, (0, (0, 0, 0)), (0.42, (40, 0, 0)), (0.56, (-34, 0, 0), L), (0.9, (-10, 0, 0)), (1.25, (0, 0, 0)))
    for seg in feeders:
        attack.rot(seg, (0, (0, 0, 0)), (0.42, (-30, 0, 0)), (0.56, (44, 0, 0), L), (0.9, (12, 0, 0)), (1.25, (0, 0, 0)))
    cr.clips["attack"] = attack.finish()

    death = Clip(1.7)
    death.rot("body", (0, (0, 0, 0)), *tremble(0.05, 0.5, (0, 0, 0), (8, 0, 10), 0.06), (1.4, (0, 0, 175)), (1.7, (0, 0, 180)))
    death.pos("body", (0, (0, 0, 0)), (0.5, (0, 1, 0)), (1.7, (0, -5, 0)))
    for seg in arms + feeders:
        death.rot(seg, (0, (0, 0, 0)), (0.5, (20, 0, 0)), (1.7, (-70, 0, 0)))
    cr.clips["death"] = death.finish()
    return cr


# ================================================================ Ovo do Kraken

def kraken_egg():
    cr = Creature("kraken_egg", "KrakenEgg", (128, 128),
                  "Ovo do Kraken: bolsa alta de membrana translúcida com um filhote enrolado e aceso dentro, raízes que "
                  "prendem no fundo e quatro pétalas no topo que abrem na eclosão.", 63)
    cr.part("anchor", None, (0, 24, 0))
    for k in range(5):
        a = math.radians(36 + 72 * k)
        root = f"root_{k}"
        cr.part(root, "anchor", (math.sin(a) * 3, 0, math.cos(a) * 3), (-8, math.degrees(a), 0))
        cr.cube(root, (-0.6, -1.2, 0), (1.2, 1.2, 5), "root", name=f"{root}_body")
        cr.part(f"{root}_end", root, (0, 0, 5), (-12, 18 if k % 2 else -18, 0))
        cr.cube(f"{root}_end", (-0.4, -0.9, 0), (0.8, 0.9, 3.5), "root", name=f"{root}_end_body")
    # O filhote fica fora da bolsa na árvore: o renderizador desenha raízes e filhote opacos, depois o brilho e por
    # último a membrana translúcida (sem isso a membrana escondia o filhote).
    cr.part("embryo", "anchor", (0, -8.5, 0))
    cr.cube("embryo", (-2.5, -2.5, -2.5), (5, 5, 5), "embryo", pattern="embryo", name="embryo_head", inflate=0.2)
    cr.cube("embryo", (-2, -6, -1), (4, 4, 4), "embryo", name="embryo_mantle", tint=1)
    stubs = []
    for k in range(4):
        a = math.radians(45 + 90 * k)
        stub = f"embryo_arm_{k}"
        cr.part(stub, "embryo", (math.sin(a) * 1.6, 2.5, math.cos(a) * 1.6), (50, math.degrees(a), 0))
        cr.cube(stub, (-0.6, 0, -0.6), (1.2, 3, 1.2), "embryo", name=f"{stub}_body", tint=1)
        stubs.append(stub)
    cr.part("sac", "anchor", (0, 0, 0))
    # Formato de ovo; as faces que ficam dentro da bolsa são vazadas (só a borda exposta é pintada).
    membrane = dict(pattern="veins", alpha=118)
    cr.cube("sac", (-3.5, -2, -3.5), (7, 2, 7), "membrane", name="sac_base", hollow={"top": 0}, **membrane)
    cr.cube("sac", (-5, -7, -5), (10, 5, 10), "membrane", name="sac_low", hollow={"top": 0, "bottom": 2}, **membrane)
    cr.cube("sac", (-5.5, -12, -5.5), (11, 5, 11), "membrane", name="sac_mid", hollow={"top": 1, "bottom": 1}, **membrane)
    cr.cube("sac", (-4.5, -16, -4.5), (9, 4, 9), "membrane", name="sac_high", hollow={"bottom": 0}, **membrane)
    # Tampa em quatro quartos com dobradiça no canto de fora: abrem na diagonal, como uma flor.
    petals = []
    for k, (sx, sz) in enumerate(((1, 1), (1, -1), (-1, -1), (-1, 1))):
        name = f"petal_{k}"
        cr.part(name, "sac", (sx * 4.5, -16, sz * 4.5))
        cr.cube(name, (0 if sx < 0 else -4.5, -2, 0 if sz < 0 else -4.5), (4.5, 2, 4.5), "membrane", name=f"{name}_leaf",
                **membrane)
        petals.append((name, -sz, sx))

    pulse = Clip(2.0, True)
    pulse.scale("sac", (0, 1), (0.18, (1.06, 1.04, 1.06)), (0.3, (0.99, 1, 0.99)), (0.45, (1.04, 1.03, 1.04)), (0.7, (1, 1, 1)), (2.0, 1))
    pulse.sample("embryo", "rotation", lambda t: (0, 14 * math.sin(2 * math.pi * t / 2.0), 4 * math.sin(4 * math.pi * t / 2.0)), 8)
    pulse.scale("embryo", (0, 1), (0.2, (1.08, 1.08, 1.08)), (0.5, 1), (2.0, 1))
    for k, stub in enumerate(stubs):
        pulse.sample(stub, "rotation", lambda t, k=k: (10 * math.sin(2 * math.pi * t / 2.0 + k * 1.6), 0, 0), 8)
    for name, rx, rz in petals:
        pulse.rot(name, (0, (0, 0, 0)), (0.2, (rx * 3, 0, rz * 3)), (0.6, (0, 0, 0)), (2.0, (0, 0, 0)))
    for k in range(5):
        pulse.sample(f"root_{k}_end", "rotation", lambda t, k=k: (4 * math.sin(2 * math.pi * t / 2.0 + k), 0, 0), 8)
    cr.clips["pulse"] = pulse.finish()

    hatch = Clip(1.8)
    hatch.scale("sac", (0, 1), (0.12, (1.12, 1.08, 1.12)), (0.24, (0.96, 0.98, 0.96)), (0.36, (1.14, 1.1, 1.14)),
                (0.48, (0.95, 0.97, 0.95)), (0.6, (1.16, 1.12, 1.16)), (1.0, (1.05, 0.9, 1.05)), (1.4, (1.1, 0.35, 1.1)),
                (1.8, (1.1, 0.25, 1.1)))
    for name, rx, rz in petals:
        hatch.rot(name, (0, (0, 0, 0)), (0.7, (rx * 6, 0, rz * 6)), (1.0, (rx * 62, 0, rz * 62), L),
                  (1.15, (rx * 55, 0, rz * 55)), (1.8, (rx * 58, 0, rz * 58)))
    hatch.pos("embryo", (0, (0, 0, 0)), (0.6, (0, 1, 0)), (1.1, (0, 5, 0)), (1.4, (0, 9, 0)), (1.8, (0, 10, 0)))
    hatch.scale("embryo", (0, 1), (1.1, (1.1, 1.1, 1.1)), (1.5, (1.3, 1.3, 1.3)), (1.8, (1.2, 1.2, 1.2)))
    hatch.rot("embryo", (0, (0, 0, 0)), *tremble(0.1, 0.9, (0, 0, 0), (6, 10, 0), 0.08), (1.8, (0, 0, 0)))
    for stub in stubs:
        hatch.rot(stub, (0, (0, 0, 0)), (1.1, (-30, 0, 0)), (1.5, (-50, 0, 0)), (1.8, (-40, 0, 0)))
    cr.clips["hatch"] = hatch.finish()
    return cr


CREATURES = {"tentacle": (tentacle, "tentacle"), "young_kraken": (young_kraken, "young_kraken"),
             "kraken_egg": (kraken_egg, "kraken_egg")}

if __name__ == "__main__":
    for key, (build, package) in CREATURES.items():
        write(build(), package)
