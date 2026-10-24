#!/usr/bin/env python3
"""Build the reproducible Kraken Blockbench reference and export Java model sources.

Uso (na raiz do repositório): python tools/models/kraken/build_model.py
Gera kraken.bbmodel e uv.png aqui, KrakenAnimations.java a partir de kraken_animations.py, copia os
templates Java e, no fim, roda tools/gen_kraken_texture.py (textura final e a camada que brilha).
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

sys.dont_write_bytecode = True  # sem __pycache__ no repositório
import kraken_animations  # noqa: E402

ROOT = Path(__file__).resolve().parents[3]
MODEL_DIR = Path(__file__).resolve().parent
TEMPLATE_DIR = MODEL_DIR / "java"
JAVA_DIR = ROOT / "src/client/java/com/aquamarine/client/entity/kraken"
JAVA_FILES = ("KrakenModel.java", "KrakenRenderState.java", "KrakenRenderer.java")
TEXTURE_SCRIPT = ROOT / "tools/gen_kraken_texture.py"
SIZE = 128
HEIGHT = 256
UUID_COUNTER = 0

# Cascas das rachaduras (crack_75/50/25): cobrem coroa, elevação e placa do manto, um pouco infladas.
CRACK_INFLATE = 0.12
CRACK_SHELLS = (
    ("crack_75", (-5, -12, -6, 10, 4, 12), (0, 128)),
    ("crack_50", (-8, -9, -9, 16, 5, 18), (48, 128)),
    ("crack_25", (-10, -5, -12, 20, 8, 24), (0, 152)),
)


def uid() -> str:
    global UUID_COUNTER
    UUID_COUNTER += 1
    return str(uuid.uuid5(uuid.NAMESPACE_URL, f"aquamarine:kraken:{UUID_COUNTER}"))


def box(name: str, origin: tuple[float, float, float], bounds: tuple[float, float, float, float, float, float],
        uv: tuple[int, int], group: str, inflate: float = 0.0) -> dict:
    ox, oy, oz = origin
    x, y, z, w, h, d = bounds
    x1, y1 = uv
    return {
        "name": name, "uuid": uid(), "from": [ox + x, oy + y, oz + z],
        "to": [ox + x + w, oy + y + h, oz + z + d], "autouv": 0, "inflate": inflate,
        "color": 0, "origin": [ox, oy, oz], "rotation": [0, 0, 0], "shade": True,
        "faces": {
            face: {"uv": [x1, y1, x1 + max(1, int(w)), y1 + max(1, int(h))], "texture": 0,
                   "rotation": 0, "tint": 0}
            for face in ("north", "east", "south", "west", "up", "down")
        },
        "_group": group,
    }


def generate_uv() -> None:
    """Gabarito de UV (referência); a textura do jogo vem de tools/gen_kraken_texture.py."""
    im = Image.new("RGBA", (SIZE, HEIGHT), (18, 28, 40, 255))
    draw = ImageDraw.Draw(im)
    for p in range(0, HEIGHT + 1, 8):
        c = (39, 56, 70, 255) if p % 16 else (57, 76, 90, 255)
        draw.line((p, 0, p, HEIGHT), fill=c)
        draw.line((0, p, SIZE, p), fill=c)
    islands = [(4, 4, 76, 32), (78, 4, 124, 26), (4, 34, 52, 56),
               (54, 34, 104, 56), (106, 30, 124, 56)]
    islands += [(4 + i * 24, 60, 24 + i * 24, 82) for i in range(5)]
    islands += [(4 + i * 24, 86, 24 + i * 24, 124) for i in range(5)]
    for _, (x, y, z, w, h, d), (u, v) in CRACK_SHELLS:
        islands.append((u, v, u + 2 * (d + w) - 1, v + d + h - 1))
    for i, rect in enumerate(islands):
        if i == 0:
            draw.rectangle((rect[0] + 1, rect[1] + 1, rect[2] - 1, rect[3] - 1), fill=(30, 62, 77, 255))
        draw.rectangle(rect, outline=(191, 219, 226, 255), width=1)
    im.save(MODEL_DIR / "uv.png", optimize=True)


def create_model(clips: dict) -> None:
    global UUID_COUNTER
    UUID_COUNTER = 0
    groups: dict[str, dict] = {}
    elements: list[dict] = []

    def group(name: str, origin: tuple[float, float, float], parent: str = "root") -> dict:
        node = {"name": name, "uuid": uid(), "origin": list(origin), "rotation": [0, 0, 0], "children": []}
        groups[name] = node
        (groups[parent]["children"] if parent in groups else outliner).append(node)
        return node

    outliner: list[dict] = []
    groups["root"] = {"name": "root", "uuid": uid(), "origin": [0, 24, 0], "rotation": [0, 0, 0], "children": outliner}
    elements.extend([
        box("mantle_plate", (0, -24, -1), (-10, -5, -12, 20, 8, 24), (0, 0), "mantle"),
        box("mantle_rise", (0, -24, -1), (-8, -9, -9, 16, 5, 18), (0, 34), "mantle"),
        box("mantle_crown", (0, -24, -1), (-5, -12, -6, 10, 4, 12), (48, 34), "mantle"),
        box("mantle_ridge_left", (-7, -27, -1), (-1, -3, -10, 2, 2, 20), (80, 0), "mantle"),
        box("mantle_ridge_right", (7, -27, -1), (-1, -3, -10, 2, 2, 20), (80, 22), "mantle"),
    ])
    for name, bounds, uv in CRACK_SHELLS:
        elements.append(box(name, (0, -24, -1), bounds, uv, "mantle", CRACK_INFLATE))
    elements.extend([
        box("head_shell", (0, -15, 1), (-7, -7, -8, 14, 13, 16), (0, 60), "head"),
        box("jaw", (0, -15, 1), (-5, 2, 7, 10, 3, 5), (60, 60), "head"),
    ])
    group("mantle", (0, -24, -1))
    group("head", (0, -15, 1))
    for side in (-1, 1):
        side_name = "left" if side < 0 else "right"
        elements.append(box(f"eye_socket_{side_name}", (side * 4.2, -16, 8.2), (-2, -2, 0, 4, 4, 2), (80, 48), "head"))
        elements.append(box(f"eye_glow_{side_name}", (side * 4.2, -16, 10), (-1, -1, 0, 2, 2, 1), (96, 48), "head"))
        for gill in range(4):
            elements.append(box(f"gill_{side_name}_{gill}", (side * (5 + gill * .35), -13, -3 + gill * 2),
                                (0, 0, 0, 1, 4, 1), (104, 48), "head"))
    elements.append(box("beak", (0, -11, 11), (-2, 0, 0, 4, 3, 4), (112, 56), "head"))

    for arm_index in range(8):
        angle = 360.0 * arm_index / 8.0
        group_name = f"arm_{arm_index}"
        # The model exporter groups arms separately; Java applies the radial yaw and droop pose.
        radians = math.radians(angle)
        origin = (math.sin(radians) * 8, -10, math.cos(radians) * 7)
        group(group_name, origin)
        elements.append(box(f"arm_{arm_index}_root", origin, (-2, -1, -2, 4, 4, 5), (0, 92), group_name))
        for joint in range(5):
            joint_name = f"arm_{arm_index}_segment_{joint}"
            joint_origin = (origin[0], origin[1], origin[2] + joint * 6)
            group(joint_name, joint_origin, group_name if joint == 0 else f"arm_{arm_index}_segment_{joint - 1}")
            taper = max(1.4, 3.3 - joint * .38)
            elements.append(box(joint_name, joint_origin, (-taper / 2, -taper / 2, 0, taper, taper, 7),
                                (24 + (joint % 3) * 24, 92), joint_name))
            for sucker in range(3):
                elements.append(box(f"arm_{arm_index}_{joint}_sucker_{sucker}",
                                    (joint_origin[0], joint_origin[1] + taper / 2, joint_origin[2] + 1 + sucker * 2),
                                    (-1, 0, -1, 2, 1, 2), (96, 92), joint_name))

    for item in elements:
        outliner_group = item.pop("_group")
        groups[outliner_group]["children"].append(item["uuid"])

    textures = [{"name": "uv.png", "id": uid(), "path": "uv.png", "width": SIZE, "height": HEIGHT,
                 "uv_width": SIZE, "uv_height": HEIGHT, "particle": True, "layers": []}]
    animations = []
    for name, clip in clips.items():
        animators = {}
        for (bone, channel), track in clip.tracks.items():
            target = groups.get(bone)
            animator_id = target["uuid"] if target else uid()
            animator = animators.setdefault(animator_id, {"name": bone, "type": "bone", "keyframes": []})
            for time in sorted(track):
                (x, y, z), interp = track[time]
                animator["keyframes"].append({"channel": channel, "data_points": [{"x": str(x), "y": str(y), "z": str(z)}],
                                              "time": time, "interpolation": interp, "uuid": uid()})
        animations.append({"name": name, "uuid": uid(), "loop": "loop" if clip.loop else "once",
                           "length": clip.length, "override": True, "animators": animators})
    model = {
        "meta": {"format_version": "4.10", "model_format": "free", "box_uv": False,
                 "visible_box": [5, 4, 5], "variable_placeholders": ""},
        "name": "Aquamarine Kraken", "model_identifier": "aquamarine_kraken",
        "visible_box": [5, 4, 5], "variable_placeholders": "",
        "resolution": {"width": SIZE, "height": HEIGHT}, "elements": elements,
        "outliner": [groups["root"]], "textures": textures, "animations": animations,
    }
    (MODEL_DIR / "kraken.bbmodel").write_text(json.dumps(model, separators=(",", ":")), encoding="utf-8")


def java_name(name: str) -> str:
    """phaseChange -> PHASE_CHANGE."""
    out = ""
    for ch in name:
        out += ("_" + ch) if ch.isupper() else ch.upper()
    return out


def fmt(v: float) -> str:
    text = f"{v:.4f}".rstrip("0").rstrip(".")
    return (text if text not in ("-0", "") else "0") + "F"


def animations_java(clips: dict) -> str:
    vec = {"rotation": "rot", "position": "pos", "scale": "scale"}
    target = {"rotation": "ROT", "position": "POS", "scale": "SCALE"}
    interp = {kraken_animations.C: "SMOOTH", kraken_animations.L: "LINEAR"}
    lines = [
        "package com.aquamarine.client.entity.kraken;",
        "",
        "import net.minecraft.client.animation.AnimationChannel;",
        "import net.minecraft.client.animation.AnimationDefinition;",
        "import net.minecraft.client.animation.Keyframe;",
        "import net.minecraft.client.animation.KeyframeAnimations;",
        "",
        "import org.joml.Vector3f;",
        "",
        "/**",
        " * GERADO por tools/models/kraken/build_model.py a partir de kraken_animations.py: não edite à mão.",
        " * Clipes do contrato KrakenView. Ataques: antecipação e tremor até o fim do aviso ({@code *_IMPACT},",
        " * em segundos, igual ao tempo de aviso de docs/02-kraken.md), golpe e retorno.",
        " */",
        "public final class KrakenAnimations {",
        "\tprivate static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;",
        "\tprivate static final AnimationChannel.Target POS = AnimationChannel.Targets.POSITION;",
        "\tprivate static final AnimationChannel.Target SCALE = AnimationChannel.Targets.SCALE;",
        "\tprivate static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;",
        "\tprivate static final AnimationChannel.Interpolation LINEAR = AnimationChannel.Interpolations.LINEAR;",
        "",
    ]
    for name, seconds in kraken_animations.WARN.items():
        lines.append(f"\tpublic static final float {java_name(name)}_IMPACT = {fmt(seconds)};")
    lines.append("")
    for name, clip in clips.items():
        lines.append(f"\tpublic static final AnimationDefinition {java_name(name)} = "
                     f"AnimationDefinition.Builder.withLength({fmt(clip.length)}){'.looping()' if clip.loop else ''}")
        for (bone, channel), track in clip.tracks.items():
            keys = []
            for time in sorted(track):
                (x, y, z), mode = track[time]
                keys.append(f"k({fmt(time)}, {vec[channel]}({fmt(x)}, {fmt(y)}, {fmt(z)}), {interp[mode]})")
            lines.append(f"\t\t.addAnimation(\"{bone}\", new AnimationChannel({target[channel]},")
            for i, key in enumerate(keys):
                lines.append(f"\t\t\t{key}{',' if i < len(keys) - 1 else '))'}")
        lines.append("\t\t.build();")
        lines.append("")
    lines += [
        "\t/** Duração do clipe em segundos (as partículas sabem quando ele acabou). */",
        "\tpublic static float lengthSeconds(String name) {",
        "\t\treturn switch (name) {",
    ]
    for name, clip in clips.items():
        lines.append(f"\t\t\tcase \"{name}\" -> {fmt(clip.length)};")
    lines += [
        "\t\t\tdefault -> 0.0F;",
        "\t\t};",
        "\t}",
        "",
        "\tprivate static Keyframe k(float time, Vector3f vector, AnimationChannel.Interpolation interpolation) {",
        "\t\treturn new Keyframe(time, vector, interpolation);",
        "\t}",
        "",
        "\tprivate static Vector3f rot(float x, float y, float z) {",
        "\t\treturn KeyframeAnimations.degreeVec(x, y, z);",
        "\t}",
        "",
        "\tprivate static Vector3f pos(float x, float y, float z) {",
        "\t\treturn KeyframeAnimations.posVec(x, y, z);",
        "\t}",
        "",
        "\tprivate static Vector3f scale(float x, float y, float z) {",
        "\t\treturn KeyframeAnimations.scaleVec(x, y, z);",
        "\t}",
        "",
        "\tprivate KrakenAnimations() {}",
        "}",
        "",
    ]
    return "\n".join(lines)


def export_java(clips: dict) -> None:
    JAVA_DIR.mkdir(parents=True, exist_ok=True)
    for filename in JAVA_FILES:
        source = TEMPLATE_DIR / f"{filename}.template"
        if not source.exists():
            raise FileNotFoundError(f"Missing Java source template: {source}")
        shutil.copyfile(source, JAVA_DIR / filename)
    (JAVA_DIR / "KrakenAnimations.java").write_text(animations_java(clips), encoding="utf-8", newline="\n")


def main() -> None:
    clips = kraken_animations.build_all()
    generate_uv()
    create_model(clips)
    export_java(clips)
    subprocess.run([sys.executable, str(TEXTURE_SCRIPT)], check=True)
    print("Generated kraken.bbmodel, uv.png, Kraken Java model sources and textures.")


if __name__ == "__main__":
    main()
