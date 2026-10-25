#!/usr/bin/env python3
"""Gera o modelo da Moréia Tempestuosa (faixa 3B-M1, docs/08-fase3.md seção 3).

Uso (na raiz do repositório): C:\\Python314\\python tools/models/moray/build_model.py
Gera moray.bbmodel e uv.png aqui, MorayModel.java e MorayAnimations.java no pacote do cliente,
copia os templates Java e, no fim, roda tools/gen_creature_textures.py (textura final e brilho),
porque este script sobrescreve o uv.png.

Coordenadas em pixels do modelo do jogo: y para baixo, y = 24 é o chão, a cabeça fica em -z.
A posição da entidade é o centro da cabeça; o corpo (14 blocos) segue para +z. As caixas de
colisão do corpo (MorayEntity.PART_OFFSETS) acompanham os centros dos segmentos daqui.
"""
from __future__ import annotations

import json
import math
import shutil
import subprocess
import sys
import uuid
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[3]
BASE = Path(__file__).resolve().parent
JAVA_ROOT = ROOT / "src/client/java/com/aquamarine/client/entity/moray"
TEXTURE_SCRIPT = ROOT / "tools/gen_creature_textures.py"
SIZE = 512
UUID_NS = uuid.NAMESPACE_URL
CY = 11  # linha central do corpo

# Ossos: (nome, pivô absoluto, pai). A cadeia seg1..tail acumula as rotações e faz a ondulação.
BONES = [
    ("body", (0, CY, 0), "root"),
    ("head", (0, CY, 14), "body"),
    ("jaw", (0, 15, 6), "head"),
    ("seg1", (0, CY, 14), "body"),
    ("seg2", (0, CY, 42), "seg1"),
    ("seg3", (0, CY, 70), "seg2"),
    ("seg4", (0, CY, 98), "seg3"),
    ("seg5", (0, CY, 126), "seg4"),
    ("seg6", (0, CY, 154), "seg5"),
    ("tail", (0, CY, 182), "seg6"),
]

# Caixas: (nome, origem absoluta, dimensões, uv (preenchido pelo empacotador), osso).
BOXES = [
    ("skull", (-11, 3, -16), (22, 13, 30), None, "head"),
    ("snout", (-8, 6, -23), (16, 9, 7), None, "head"),
    ("brow", (-12, 2, -12), (24, 3, 10), None, "head"),
    ("head_crest", (-1, -3, -10), (2, 6, 22), None, "head"),
    ("left_eye", (10, 6, -13), (2, 3, 4), None, "head"),
    ("right_eye", (-12, 6, -13), (2, 3, 4), None, "head"),
    ("left_frill", (11, 4, 4), (2, 9, 8), None, "head"),
    ("right_frill", (-13, 4, 4), (2, 9, 8), None, "head"),
    ("left_fang", (5, 15, -22), (2, 3, 2), None, "head"),
    ("right_fang", (-7, 15, -22), (2, 3, 2), None, "head"),
    ("jaw_plate", (-9, 15, -22), (18, 5, 28), None, "jaw"),
    ("left_low_fang", (4, 13, -21), (2, 2, 2), None, "jaw"),
    ("right_low_fang", (-6, 13, -21), (2, 2, 2), None, "jaw"),
    ("barbel", (-1, 20, -18), (2, 3, 6), None, "jaw"),
]
# Segmentos: (osso, meia largura, altura, nadadeira ventral)
SEGMENTS = [("seg1", 10, 20, False), ("seg2", 9, 18, False), ("seg3", 9, 18, True),
            ("seg4", 8, 16, True), ("seg5", 7, 14, True), ("seg6", 6, 12, True)]
for i, (bone, half, height, ventral) in enumerate(SEGMENTS):
    z = 14 + 28 * i
    top = CY - height // 2
    BOXES.append((f"{bone}_core", (-half, top, z), (2 * half, height, 28), None, bone))
    BOXES.append((f"{bone}_dorsal", (-1, top - 5, z + 2), (2, 5, 24), None, bone))
    BOXES.append((f"{bone}_left_glow", (half, CY - 1, z + 4), (1, 2, 20), None, bone))
    BOXES.append((f"{bone}_right_glow", (-half - 1, CY - 1, z + 4), (1, 2, 20), None, bone))
    if ventral:
        BOXES.append((f"{bone}_ventral", (-1, top + height, z + 4), (2, 3, 20), None, bone))
BOXES += [
    ("tail_core", (-4, 6, 182), (8, 10, 24), None, "tail"),
    ("tail_fin", (-1, -2, 186), (2, 26, 26), None, "tail"),
    ("tail_left_glow", (4, CY - 1, 184), (1, 2, 18), None, "tail"),
    ("tail_right_glow", (-5, CY - 1, 184), (1, 2, 18), None, "tail"),
]

CHAIN = ["seg1", "seg2", "seg3", "seg4", "seg5", "seg6", "tail"]


def wave(duration: float, steps: int, poses) -> dict:
    """Animação em laço amostrada: poses(t) devolve {osso: (x, y, z)} em graus."""
    frames: dict[str, list] = {}
    for s in range(steps + 1):
        t = duration * s / steps
        for bone, angle in poses(2 * math.pi * s / steps).items():
            frames.setdefault(bone, []).append((t, angle))
    return frames


def keys(*frames) -> list:
    return list(frames)


def swim_poses(amplitude: float, head: float):
    def poses(p: float) -> dict:
        out = {"head": (0, head * math.sin(p + 0.9), 0)}
        for i, bone in enumerate(CHAIN):
            out[bone] = (0, (amplitude + 1.6 * i) * math.sin(p - 0.85 * i), 0)
        return out
    return poses


def idle_poses(p: float) -> dict:
    out = swim_poses(3.5, 3)(p)
    out["jaw"] = (5 + 4 * math.sin(p), 0, 0)
    return out


def stunned_poses(p: float) -> dict:
    out = {"body": (0, 0, 24 + 5 * math.sin(p)), "head": (16, 4 * math.sin(p), 0), "jaw": (18, 0, 0)}
    for i, bone in enumerate(CHAIN):
        out[bone] = (3 * math.sin(p - 0.6 * i), 2 * math.sin(p - 0.5 * i), 0)
    return out


def strike() -> dict:
    # Aviso de 1 s: recua em S e arqueia; depois o bote (cabeça para a frente, mandíbula aberta).
    neutral = (0, 0, 0)
    return {
        "head": keys((0, neutral), (0.9, (-26, 0, 0)), (1.05, (6, 0, 0)), (1.4, neutral)),
        "jaw": keys((0, neutral), (0.9, (22, 0, 0)), (1.05, (38, 0, 0)), (1.2, (2, 0, 0)), (1.4, neutral)),
        "seg1": keys((0, neutral), (0.9, (28, 10, 0)), (1.05, (-6, 0, 0)), (1.4, neutral)),
        "seg2": keys((0, neutral), (0.9, (-22, -16, 0)), (1.05, (2, 0, 0)), (1.4, neutral)),
        "seg3": keys((0, neutral), (0.9, (-10, 18, 0)), (1.05, (0, 0, 0)), (1.4, neutral)),
        "seg4": keys((0, neutral), (0.9, (0, -12, 0)), (1.4, neutral)),
    }


def chain() -> dict:
    # Aviso de 1,5 s: o corpo treme cada vez mais e a boca abre; descarga rígida até 1,8 s.
    frames: dict[str, list] = {"head": [], "jaw": []}
    for s in range(16):
        t = 0.1 * s
        frames["head"].append((t, (-14 * t / 1.5, 0, 0)))
        frames["jaw"].append((t, (26 * t / 1.5, 0, 0)))
        for i, bone in enumerate(CHAIN):
            amp = 1.0 + 4.0 * t / 1.5
            frames.setdefault(bone, []).append((t, (0, amp * (1 if (s + i) % 2 else -1), 0)))
    frames["head"] += [(1.6, (-22, 0, 0)), (1.8, (-22, 0, 0)), (2.0, (0, 0, 0))]
    frames["jaw"] += [(1.6, (34, 0, 0)), (1.8, (34, 0, 0)), (2.0, (0, 0, 0))]
    for bone in CHAIN:
        frames[bone] += [(1.6, (0, 0, 0)), (2.0, (0, 0, 0))]
    return frames


def coil() -> dict:
    # Aviso de 1 s (cauda sobe e treme), depois enrola os segmentos de trás até 4,6 s.
    neutral = (0, 0, 0)
    out = {
        "seg6": keys((0, neutral), (1.0, (-16, 6, 0)), (1.6, (0, 42, 0)), (4.6, (0, 42, 0)), (5.0, neutral)),
        "tail": keys((0, neutral), (0.5, (-30, -8, 0)), (0.75, (-30, 8, 0)), (1.0, (-30, -8, 0)),
                     (1.6, (0, 48, 0)), (4.6, (0, 48, 0)), (5.0, neutral)),
        "head": keys((0, neutral), (1.0, (-8, 0, 0)), (1.6, (-4, -14, 0)), (4.6, (-4, -14, 0)), (5.0, neutral)),
        "jaw": keys((0, neutral), (1.6, (16, 0, 0)), (4.6, (16, 0, 0)), (5.0, neutral)),
    }
    for bone, angle in (("seg3", 34), ("seg4", 38), ("seg5", 40)):
        out[bone] = keys((0, neutral), (1.0, neutral), (1.6, (0, angle, 0)), (4.6, (0, angle, 0)), (5.0, neutral))
    return out


def death() -> dict:
    neutral = (0, 0, 0)
    out = {
        "body": keys((0, neutral), (0.8, (0, 0, 60)), (2.0, (0, 0, 175)), (2.5, (0, 0, 180))),
        "head": keys((0, neutral), (0.6, (-20, 0, 0)), (2.5, (10, 0, 0))),
        "jaw": keys((0, neutral), (0.6, (34, 0, 0)), (2.5, (24, 0, 0))),
    }
    for i, bone in enumerate(CHAIN):
        sign = 1 if i % 2 else -1
        out[bone] = keys((0, neutral), (0.6, (0, 14 * sign, 0)), (2.5, (4 * sign, 3 * sign, 0)))
    return out


ANIMS = {
    "idle": (4.0, True, wave(4.0, 8, idle_poses)),
    "swim": (1.6, True, wave(1.6, 8, swim_poses(9, 6))),
    "strike": (1.4, False, strike()),
    "chain": (2.0, False, chain()),
    "coil": (5.0, False, coil()),
    "stunned": (2.4, True, wave(2.4, 6, stunned_poses)),
    "death": (2.5, False, death()),
}


def pack_uv() -> dict[str, tuple[int, int]]:
    """Empacota os retângulos de UV de caixa do jogo, com 2 px de folga (mesmo método da baleia)."""
    rows: list[dict[str, int]] = []
    result = {}
    ordered = sorted(BOXES, key=lambda b: (-(b[2][1] + b[2][2]), -2 * (b[2][0] + b[2][2])))
    for name, _, (dx, dy, dz), _, _ in ordered:
        width, height = 2 * (dx + dz), dy + dz
        candidates = [(row["width"] - row["x"] - width, row) for row in rows
                      if height <= row["height"] and row["x"] + width + 2 <= SIZE]
        if candidates:
            _, row = min(candidates, key=lambda pair: pair[0])
            x, y = row["x"], row["y"]
            row["x"] += width + 2
        else:
            y = sum(row["height"] + 2 for row in rows)
            if y + height + 2 > SIZE or width + 2 > SIZE:
                raise ValueError(f"UV layout exceeds {SIZE}x{SIZE} at {name}")
            row = {"x": width + 2, "y": y, "height": height, "width": SIZE}
            rows.append(row)
            x = 0
        result[name] = (x, y)
    return result


def pivots() -> dict[str, tuple[int, int, int]]:
    return {name: origin for name, origin, _ in BONES} | {"root": (0, 0, 0)}


def stable_id(label: str) -> str:
    return str(uuid.uuid5(UUID_NS, "aquamarine-moray-" + label))


def fmt(v: float) -> str:
    text = f"{v:.2f}".rstrip("0").rstrip(".")
    return "0" if text in ("-0", "") else text


def model_java(uvs: dict) -> str:
    piv = pivots()
    lines = [
        "package com.aquamarine.client.entity.moray;",
        "",
        "import com.aquamarine.Aquamarine;",
        "",
        "import net.minecraft.client.animation.KeyframeAnimation;",
        "import net.minecraft.client.model.EntityModel;",
        "import net.minecraft.client.model.geom.ModelLayerLocation;",
        "import net.minecraft.client.model.geom.ModelPart;",
        "import net.minecraft.client.model.geom.PartPose;",
        "import net.minecraft.client.model.geom.builders.CubeListBuilder;",
        "import net.minecraft.client.model.geom.builders.LayerDefinition;",
        "import net.minecraft.client.model.geom.builders.MeshDefinition;",
        "import net.minecraft.client.model.geom.builders.PartDefinition;",
        "",
        "/** Gerado por tools/models/moray/build_model.py; edite o script, não este arquivo. */",
        "public final class MorayModel extends EntityModel<MorayRenderState> {",
        "\tpublic static final ModelLayerLocation LAYER = new ModelLayerLocation(Aquamarine.id(\"moray\"), \"main\");",
        f"\tpublic static final int TEXTURE_SIZE = {SIZE};",
        "\tprivate final ModelPart body;",
        "\tprivate final KeyframeAnimation " + ", ".join(ANIMS) + ";",
        "",
        "\tpublic MorayModel(ModelPart root) {",
        "\t\tsuper(root);",
        "\t\tbody = root.getChild(\"body\");",
    ]
    for name in ANIMS:
        lines.append(f"\t\t{name} = MorayAnimations.{name.upper()}.bake(root);")
    lines += ["\t}", "", "\tpublic static LayerDefinition createBodyLayer() {",
              "\t\tMeshDefinition mesh = new MeshDefinition();",
              "\t\tPartDefinition root = mesh.getRoot();"]
    for name, origin, parent in BONES:
        parent_var = "root" if parent == "root" else parent
        offset = [origin[i] - piv[parent][i] for i in range(3)]
        cubes = []
        for box, abs_origin, size, _, bone in BOXES:
            if bone != name:
                continue
            u, v = uvs[box]
            local = [abs_origin[i] - origin[i] for i in range(3)]
            cubes.append(f"\n\t\t\t.texOffs({u}, {v}).addBox({', '.join(fmt(c) + 'F' for c in local)}, "
                         f"{', '.join(fmt(c) + 'F' for c in size)})")
        decl = "PartDefinition " + name + " = " if any(p == name for _, _, p in BONES) else ""
        lines.append(f"\t\t{decl}{parent_var}.addOrReplaceChild(\"{name}\", CubeListBuilder.create(){''.join(cubes)},"
                     f"\n\t\t\tPartPose.offset({', '.join(fmt(c) + 'F' for c in offset)}));")
    lines += [f"\t\treturn LayerDefinition.create(mesh, TEXTURE_SIZE, TEXTURE_SIZE);", "\t}", "",
              "\t@Override", "\tpublic void setupAnim(MorayRenderState state) {", "\t\tresetPose();",
              "\t\t// Nada em 3D: o corpo inteiro inclina com o olhar, como o golfinho.",
              "\t\tbody.xRot = state.xRot * ((float) Math.PI / 180.0F);"]
    for name in ANIMS:
        lines.append(f"\t\t{name}.apply(state.{name}, state.ageInTicks);")
    lines += ["\t}", "}", ""]
    return "\n".join(lines)


def animations_java() -> str:
    lines = [
        "package com.aquamarine.client.entity.moray;",
        "",
        "import net.minecraft.client.animation.AnimationChannel;",
        "import net.minecraft.client.animation.AnimationDefinition;",
        "import net.minecraft.client.animation.Keyframe;",
        "import net.minecraft.client.animation.KeyframeAnimations;",
        "",
        "/** Gerado por tools/models/moray/build_model.py; edite o script, não este arquivo. */",
        "public final class MorayAnimations {",
        "\tprivate static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;",
        "\tprivate static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;",
    ]
    for name, (duration, loop, frames) in ANIMS.items():
        lines.append(f"\tpublic static final AnimationDefinition {name.upper()} = AnimationDefinition.Builder"
                     f".withLength({fmt(duration)}F){'.looping()' if loop else ''}")
        for bone, keyframes in frames.items():
            ks = ", ".join(f"k({fmt(t)}F, {fmt(a[0])}F, {fmt(a[1])}F, {fmt(a[2])}F)" for t, a in keyframes)
            lines.append(f"\t\t.addAnimation(\"{bone}\", rot({ks}))")
        lines.append("\t\t.build();")
    lines += [
        "",
        "\tprivate static AnimationChannel rot(Keyframe... frames) {",
        "\t\treturn new AnimationChannel(ROT, frames);",
        "\t}",
        "",
        "\tprivate static Keyframe k(float t, float x, float y, float z) {",
        "\t\treturn new Keyframe(t, KeyframeAnimations.degreeVec(x, y, z), SMOOTH);",
        "\t}",
        "",
        "\tprivate MorayAnimations() {}",
        "}",
        "",
    ]
    return "\n".join(lines)


def bbmodel(uvs: dict) -> dict:
    """Referência para o Blockbench (y para cima, x espelhado), reproduzível pelo script."""
    piv = pivots()
    groups = {"root": {"name": "root", "uuid": stable_id("root"), "origin": [0, 0, 0], "rotation": [0, 0, 0], "children": []}}

    def bb(p):
        return [-p[0], 24 - p[1], p[2]]

    for name, origin, parent in BONES:
        groups[name] = {"name": name, "uuid": stable_id(name), "origin": bb(origin), "rotation": [0, 0, 0], "children": []}
        groups[parent]["children"].append(groups[name])
    elements = []
    for name, origin, size, _, bone in BOXES:
        a = bb((origin[0] + size[0], origin[1] + size[1], origin[2]))
        b = bb((origin[0], origin[1], origin[2] + size[2]))
        elements.append({"name": name, "uuid": stable_id(name), "from": a, "to": b, "origin": bb(piv[bone]),
                         "rotation": [0, 0, 0], "uv_offset": list(uvs[name])})
        groups[bone]["children"].append(stable_id(name))
    animations = []
    for name, (duration, loop, frames) in ANIMS.items():
        animators = {}
        for bone, keyframes in frames.items():
            animators[stable_id(f"anim-{name}-{bone}")] = {"name": bone, "type": "bone", "keyframes": [
                {"channel": "rotation", "data_points": [{"x": str(a[0]), "y": str(a[1]), "z": str(a[2])}], "time": t,
                 "interpolation": "catmullrom", "uuid": stable_id(f"{name}-{bone}-{i}")}
                for i, (t, a) in enumerate(keyframes)]}
        animations.append({"name": name, "uuid": stable_id("animation-" + name), "loop": "loop" if loop else "once",
                           "length": duration, "override": True, "animators": animators})
    return {"meta": {"format_version": "4.10", "model_format": "modded_entity", "box_uv": True},
            "name": "Moréia Tempestuosa", "model_identifier": "moray", "resolution": {"width": SIZE, "height": SIZE},
            "elements": elements, "outliner": [groups["root"]],
            "textures": [{"name": "uv.png", "id": stable_id("texture"), "path": "uv.png", "width": SIZE, "height": SIZE,
                          "uv_width": SIZE, "uv_height": SIZE, "particle": True, "layers": []}],
            "animations": animations}


def main() -> None:
    uvs = pack_uv()
    (BASE / "moray.bbmodel").write_text(json.dumps(bbmodel(uvs), indent=1), encoding="utf-8", newline="\n")
    uv = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    draw = ImageDraw.Draw(uv)
    palette = [(30, 74, 110, 255), (46, 112, 140, 255), (88, 170, 190, 255), (150, 220, 230, 255)]
    for i, (name, _, (dx, dy, dz), _, _) in enumerate(BOXES):
        x, y = uvs[name]
        draw.rectangle((x, y, x + 2 * (dx + dz) - 1, y + dy + dz - 1), fill=palette[i % len(palette)], outline=(230, 240, 200, 255))
    uv.save(BASE / "uv.png")
    JAVA_ROOT.mkdir(parents=True, exist_ok=True)
    (JAVA_ROOT / "MorayModel.java").write_text(model_java(uvs), encoding="utf-8", newline="\n")
    (JAVA_ROOT / "MorayAnimations.java").write_text(animations_java(), encoding="utf-8", newline="\n")
    for src in sorted((BASE / "java").glob("*.java.template")):
        shutil.copyfile(src, JAVA_ROOT / src.name.removesuffix(".template"))
    print("Gerados moray.bbmodel, uv.png e as classes Java da Moréia.")
    if TEXTURE_SCRIPT.exists():
        subprocess.run([sys.executable, str(TEXTURE_SCRIPT), "moray"], check=True)


if __name__ == "__main__":
    main()
