#!/usr/bin/env python3
"""Build the reproducible Kraken Blockbench reference and export Java model sources."""

from __future__ import annotations

import json
import shutil
import uuid
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[3]
MODEL_DIR = Path(__file__).resolve().parent
TEMPLATE_DIR = MODEL_DIR / "java"
JAVA_DIR = ROOT / "src/client/java/com/aquamarine/client/entity/kraken"
GAME_TEXTURE = ROOT / "src/main/resources/assets/aquamarine/textures/entity/kraken/kraken.png"
JAVA_FILES = ("KrakenModel.java", "KrakenAnimations.java", "KrakenRenderState.java", "KrakenRenderer.java")
SIZE = 128
UUID_COUNTER = 0

ANIMATIONS = {
    "idle": (3.0, True), "swim": (1.4, True), "emerge": (1.8, False),
    "stunned": (1.1, True), "phaseChange": (2.2, False), "death": (2.4, False),
    "slam": (1.25, False), "grab": (1.55, False), "ink": (1.35, False),
    "whirlpool": (2.0, True), "spawnYoung": (1.6, False), "waterJet": (1.15, False),
    "roar": (1.7, False), "bolts": (1.5, False), "ambush": (1.3, False),
    "doubleGrab": (1.7, False),
}

ANIMATION_POSES = {
    "idle": {"arm_0_segment_1": ((0, 0, 0), (-3, 0, 0), (3, 0, 0), (0, 0, 0)),
             "arm_4_segment_1": ((0, 0, 0), (3, 0, 0), (-3, 0, 0), (0, 0, 0))},
    "swim": {"mantle": ((-2, 0, 0), (4, 0, 0), (-2, 0, 0), (-2, 0, 0)),
             "arm_1_segment_0": ((-9, 0, 0), (12, 0, 0), (-9, 0, 0), (-9, 0, 0))},
    "emerge": {"mantle": ((0, 0, 0), (0, -8, 0), (0, -8, 0), (0, 0, 0)),
               "head": ((8, 0, 0), (-10, 0, 0), (-10, 0, 0), (0, 0, 0))},
    "stunned": {"mantle": ((0, -7, 4), (0, 7, -4), (0, 7, -4), (0, -7, 4)),
                "arm_0_segment_0": ((-22, 0, 0), (10, 0, 0), (10, 0, 0), (-22, 0, 0))},
    "phaseChange": {"mantle": ((-12, 0, 0), (15, 0, 0), (15, 0, 0), (0, 0, 0)),
                    "arm_0_segment_0": ((0, 0, 0), (-28, 0, 0), (-28, 0, 0), (0, 0, 0))},
    "death": {"mantle": ((0, 0, 0), (25, 0, 32), (25, 0, 32), (80, 0, 90)),
              "head": ((0, 0, 0), (-25, 0, -32), (-25, 0, -32), (-45, 0, -50))},
    "slam": {"arm_0_segment_0": ((0, 0, 0), (-48, 0, 0), (42, 0, 0), (0, 0, 0)),
             "arm_0_segment_1": ((0, 0, 0), (-16.8, 0, 0), (23.1, 0, 0), (0, 0, 0))},
    "grab": {"arm_1_segment_0": ((0, 0, 0), (-32, 0, 0), (34, 0, 0), (0, 0, 0)),
             "arm_1_segment_1": ((0, 0, 0), (-11.2, 0, 0), (18.7, 0, 0), (0, 0, 0))},
    "ink": {"mantle": ((0, 0, 0), (0, 0, 0), (3, 0, 0), (0, 0, 0)),
            "head": ((12, 0, 0), (-22, 0, 0), (-22, 0, 0), (0, 0, 0))},
    "whirlpool": {"arm_0_segment_0": ((-12, -12, 0), (-12, 18, 0), (-12, 18, 0), (-12, 24, 0)),
                  "arm_4_segment_0": ((-12, -12, 0), (-12, 18, 0), (-12, 18, 0), (-12, 24, 0))},
    "spawnYoung": {"arm_4_segment_0": ((0, 0, 0), (-7, 0, 0), (-20, 0, 0), (0, 0, 0)),
                   "arm_5_segment_0": ((0, 0, 0), (-9, 0, 0), (-26, 0, 0), (0, 0, 0))},
    "waterJet": {"head": ((8, 0, 0), (-6.3, 0, 0), (-18, 0, 0), (0, 0, 0)),
                 "arm_1_segment_0": ((0, 0, 0), (2.8, 0, 0), (8, 0, 0), (0, 0, 0))},
    "roar": {"head": ((14, 0, 0), (-9.8, 0, 0), (-28, 0, 0), (0, 0, 0)),
             "arm_0_segment_0": ((0, 0, 0), (-8.4, 0, 0), (-24, 0, 0), (0, 0, 0))},
    "bolts": {"head": ((0, -8, 0), (0, 5.6, 0), (0, 16, 0), (0, -8, 0)),
              "arm_2_segment_0": ((0, 0, 0), (-7.7, 0, 0), (-22, 0, 0), (0, 0, 0))},
    "ambush": {"mantle": ((0, 0, 0), (0, 0, 0), (6, 0, 0), (0, 0, 0)),
               "arm_0_segment_0": ((28, 0, 0), (-18.2, 0, 0), (-52, 0, 0), (0, 0, 0))},
    "doubleGrab": {"arm_1_segment_0": ((0, 0, 0), (-30, 0, 0), (38, 0, 0), (0, 0, 0)),
                   "arm_1_segment_1": ((0, 0, 0), (-12, 0, 0), (24, 0, 0), (0, 0, 0)),
                   "arm_5_segment_0": ((0, 0, 0), (-30, 0, 0), (38, 0, 0), (0, 0, 0)),
                   "arm_5_segment_1": ((0, 0, 0), (-12, 0, 0), (24, 0, 0), (0, 0, 0))},
}

ANIMATION_POSITIONS = {
    "emerge": {"mantle": ((0, 0, 0), (0, -2.8, 0), (0, -8, 0), (0, 0, 0))},
    "ink": {"mantle": ((0, 0, 0), (0, 0, 0), (3, 0, 0), (0, 0, 0))},
    "waterJet": {"head": ((0, 0, 0), (0, 1.05, 0), (0, 3, 0), (0, 0, 0))},
    "roar": {"mantle": ((0, 0, 0), (0, -0.875, 0), (0, -2.5, 0), (0, 0, 0))},
    "ambush": {"mantle": ((0, 0, 0), (0, 0, 0), (6, 0, 0), (0, 0, 0))},
}


def uid() -> str:
    global UUID_COUNTER
    UUID_COUNTER += 1
    return str(uuid.uuid5(uuid.NAMESPACE_URL, f"aquamarine:kraken:{UUID_COUNTER}"))


def box(name: str, origin: tuple[float, float, float], bounds: tuple[float, float, float, float, float, float],
        uv: tuple[int, int], group: str) -> dict:
    ox, oy, oz = origin
    x, y, z, w, h, d = bounds
    x1, y1 = uv
    return {
        "name": name, "uuid": uid(), "from": [ox + x, oy + y, oz + z],
        "to": [ox + x + w, oy + y + h, oz + z + d], "autouv": 0,
        "color": 0, "origin": [ox, oy, oz], "rotation": [0, 0, 0], "shade": True,
        "faces": {
            face: {"uv": [x1, y1, x1 + max(1, int(w)), y1 + max(1, int(h))], "texture": 0,
                   "rotation": 0, "tint": 0}
            for face in ("north", "east", "south", "west", "up", "down")
        },
        "_group": group,
    }


def generate_uv() -> None:
    im = Image.new("RGBA", (SIZE, SIZE), (18, 28, 40, 255))
    draw = ImageDraw.Draw(im)
    for p in range(0, SIZE + 1, 8):
        c = (39, 56, 70, 255) if p % 16 else (57, 76, 90, 255)
        draw.line((p, 0, p, SIZE), fill=c)
        draw.line((0, p, SIZE, p), fill=c)
    islands = [(4, 4, 76, 32), (78, 4, 124, 26), (4, 34, 52, 56),
               (54, 34, 104, 56), (106, 30, 124, 56)]
    islands += [(4 + i * 24, 60, 24 + i * 24, 82) for i in range(5)]
    islands += [(4 + i * 24, 86, 24 + i * 24, 124) for i in range(5)]
    for i, rect in enumerate(islands):
        if i == 0:
            draw.rectangle((rect[0] + 1, rect[1] + 1, rect[2] - 1, rect[3] - 1), fill=(30, 62, 77, 255))
        draw.rectangle(rect, outline=(191, 219, 226, 255), width=1)
    im.save(MODEL_DIR / "uv.png", optimize=True)
    GAME_TEXTURE.parent.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(MODEL_DIR / "uv.png", GAME_TEXTURE)


def create_model() -> None:
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
        box("crack_75", (-4, -36, -1), (-.5, -.6, -6, 1, .5, 12), (0, 118), "mantle"),
        box("crack_50", (2, -36, 0), (-5, -.6, -.5, 10, .5, 1), (16, 118), "mantle"),
        box("crack_25", (4, -36, -1), (-.5, -.6, -5, 1, .5, 10), (28, 118), "mantle"),
        box("head_shell", (0, -15, 1), (-7, -7, -8, 14, 13, 16), (0, 60), "head"),
        box("jaw", (0, -15, 1), (-5, 2, 7, 10, 3, 5), (60, 60), "head"),
    ])
    group("mantle", (0, -24, -1))
    head_group = group("head", (0, -15, 1))
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
        import math
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

    textures = [{"name": "uv.png", "id": uid(), "path": "uv.png", "width": SIZE, "height": SIZE,
                 "uv_width": SIZE, "uv_height": SIZE, "particle": True, "layers": []}]
    animations = []
    for name, (length, looping) in ANIMATIONS.items():
        animators = {}
        channels = [("rotation", bone, poses) for bone, poses in ANIMATION_POSES[name].items()]
        channels.extend(("position", bone, poses) for bone, poses in ANIMATION_POSITIONS.get(name, {}).items())
        for channel, bone, poses in channels:
            animator_id = uid()
            frames = []
            for time, pose in zip((0.0, length * 0.27, length * 0.58, length), poses):
                frames.append({"channel": channel, "data_points": [{"x": str(pose[0]), "y": str(pose[1]), "z": str(pose[2])}],
                               "time": time, "interpolation": "catmullrom", "uuid": uid()})
            animators[animator_id] = {"name": bone, "type": "bone", "keyframes": frames}
        animations.append({"name": name, "uuid": uid(), "loop": "loop" if looping else "once",
                           "length": length, "override": True, "animators": animators})
    model = {
        "meta": {"format_version": "4.10", "model_format": "free", "box_uv": False,
                 "visible_box": [5, 4, 5], "variable_placeholders": ""},
        "name": "Aquamarine Kraken", "model_identifier": "aquamarine_kraken",
        "visible_box": [5, 4, 5], "variable_placeholders": "",
        "resolution": {"width": SIZE, "height": SIZE}, "elements": elements,
        "outliner": [groups["root"]], "textures": textures, "animations": animations,
    }
    (MODEL_DIR / "kraken.bbmodel").write_text(json.dumps(model, separators=(",", ":")), encoding="utf-8")


def export_java() -> None:
    JAVA_DIR.mkdir(parents=True, exist_ok=True)
    for filename in JAVA_FILES:
        source = TEMPLATE_DIR / f"{filename}.template"
        if not source.exists():
            raise FileNotFoundError(f"Missing Java source template: {source}")
        shutil.copyfile(source, JAVA_DIR / filename)


def main() -> None:
    generate_uv()
    create_model()
    export_java()
    print("Generated kraken.bbmodel, uv.png, and Kraken Java model sources.")


if __name__ == "__main__":
    main()
