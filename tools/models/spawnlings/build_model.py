#!/usr/bin/env python3
"""Generate editable Blockbench models and UV guides for the Kraken's offspring."""
from __future__ import annotations

import json
import math
import shutil
import uuid
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[3]
BASE = Path(__file__).resolve().parent
UUID_COUNTER = 0
SIZE = 128
JAVA_TEMPLATES = BASE / "java"
JAVA_ROOT = ROOT / "src/client/java/com/aquamarine/client/entity"


def uid() -> str:
    global UUID_COUNTER
    UUID_COUNTER += 1
    return str(uuid.uuid5(uuid.NAMESPACE_URL, f"aquamarine-spawnlings-{UUID_COUNTER}"))


def box(name, origin, dims, uv, group):
    x, y, z = origin
    dx, dy, dz = dims
    return {"name": name, "uuid": uid(), "from": [x, y, z], "to": [x + dx, y + dy, z + dz],
            "origin": list(origin), "rotation": [0, 0, 0], "uv_offset": list(uv), "_group": group}


def make_model(name, identifier, size, bones, parts, animations):
    global UUID_COUNTER
    UUID_COUNTER = 0
    groups = {"root": {"name": "root", "uuid": uid(), "origin": [0, 24, 0],
                       "rotation": [0, 0, 0], "children": []}}
    for bone, origin, parent in bones:
        groups[bone] = {"name": bone, "uuid": uid(), "origin": list(origin), "rotation": [0, 0, 0], "children": []}
        groups[parent]["children"].append(groups[bone])
    for p in parts:
        groups[p.pop("_group")]["children"].append(p["uuid"])
    anims = []
    for aname, length, loop, poses in animations:
        animator = {}
        for bone, radians in poses.items():
            aid = uid()
            keys = []
            for t, angle in zip((0, length * .28, length * .62, length), radians):
                keys.append({"channel": "rotation", "data_points": [{"x": "0", "y": "0", "z": str(angle)}],
                             "time": t, "interpolation": "catmullrom", "uuid": uid()})
            animator[aid] = {"name": bone, "type": "bone", "keyframes": keys}
        anims.append({"name": aname, "uuid": uid(), "loop": "loop" if loop else "once",
                      "length": length, "override": True, "animators": animator})
    data = {"meta": {"format_version": "4.10", "model_format": "free", "box_uv": True,
                      "visible_box": [4, 4, 4], "variable_placeholders": ""},
            "name": name, "model_identifier": identifier, "visible_box": [4, 4, 4],
            "variable_placeholders": "", "resolution": {"width": SIZE, "height": SIZE},
            "elements": parts, "outliner": [groups["root"]],
            "textures": [{"name": "uv.png", "id": uid(), "path": "uv.png", "width": SIZE,
                          "height": SIZE, "uv_width": SIZE, "uv_height": SIZE, "particle": True, "layers": []}],
            "animations": anims}
    folder = BASE / identifier
    folder.mkdir(parents=True, exist_ok=True)
    (folder / f"{identifier}.bbmodel").write_text(json.dumps(data, separators=(",", ":")), encoding="utf-8")
    uv = Image.new("RGBA", (SIZE, SIZE), (24, 42, 64, 255))
    draw = ImageDraw.Draw(uv)
    palette = [(27, 110, 132, 255), (44, 166, 163, 255), (105, 75, 144, 255), (190, 151, 73, 255)]
    for n, p in enumerate(parts):
        off = p["uv_offset"]
        dims = [int(max(2, min(24, abs(p["to"][i] - p["from"][i])))) for i in range(3)]
        w, h = max(dims[0], dims[2]) * 2, max(dims[1], dims[2]) * 2
        x, y = off
        x = max(0, min(SIZE - w - 1, x)); y = max(0, min(SIZE - h - 1, y))
        draw.rectangle((x, y, x + w, y + h), fill=palette[n % len(palette)], outline=(225, 208, 146, 255))
        draw.line((x, y, x + w, y + h), fill=(235, 230, 200, 255))
    uv.save(folder / "uv.png")


def tentacle():
    bones, parts = [], []
    parent = "root"
    for i in range(7):
        bone = f"segment_{i}"
        bones.append((bone, (0, -i * 4, 0), parent))
        r = 2.5 - i * .23
        parts.append(box(bone, (-r, -4, -r), (r * 2, 4.3, r * 2), (i * 12, 0), bone))
        for j in range(3):
            parts.append(box(f"sucker_{i}_{j}", (-r + .35, -3.8, -r - .6),
                             (max(.65, r - .4), .55, .8), (i * 12, 16 + j * 4), bone))
        parent = bone
    parts.extend([box("barb_left", (-1.4, -27, -.3), (1, 3, .8), (96, 0), parent),
                  box("barb_right", (.4, -27, -.3), (1, 3, .8), (104, 0), parent),
                  box("anchor_hook", (-1.5, -29, -.5), (3, 2, 1), (112, 0), parent)])
    anims = [("idle", 2.4, True, {f"segment_{i}": [0, (-1)**i * 10, (-1)**i * 4, 0] for i in range(1, 7)}),
             ("slam", 1.35, False, {"segment_1": [0, -34, 52, 0], "segment_2": [0, -22, 38, 0],
                                     "segment_3": [0, 8, -16, 0]}),
             ("grab", 1.6, False, {"segment_2": [0, -14, 28, 0], "segment_3": [0, -25, 42, 0],
                                    "segment_4": [0, 8, -12, 0]}),
             ("release", 1.1, False, {"segment_3": [0, 20, -10, 0], "segment_4": [0, 16, -8, 0]})]
    make_model("Aquamarine Tentacle", "tentacle", SIZE, bones, parts, anims)


def young():
    bones = [("mantle", (0, -12, -1), "root"), ("head", (0, -7, 1), "root")]
    parts = [box("mantle_shell", (-5, -17, -7), (10, 8, 13), (0, 0), "mantle"),
             box("mantle_ridge", (-3, -19, -5), (6, 3, 9), (32, 0), "mantle"),
             box("head", (-4, -11, -5), (8, 7, 11), (0, 32), "head"),
             box("beak", (-1.5, -6, 5), (3, 2, 2), (40, 32), "head"),
             box("eye_left", (-3.5, -9, 5), (2, 2, 1), (48, 32), "head"),
             box("eye_right", (1.5, -9, 5), (2, 2, 1), (56, 32), "head")]
    for i in range(6):
        angle = math.tau * i / 6
        x, z = math.sin(angle) * 3.1, math.cos(angle) * 2.8
        parent = "root"
        for j in range(3):
            bone = f"arm_{i}_{j}"
            bones.append((bone, (x, -7, z + j * 2), parent))
            taper = 1.55 - j * .32
            parts.append(box(f"arm_{i}_segment_{j}", (-taper / 2, -taper / 2, 0),
                             (taper, taper, 3), (64 + j * 12, i * 12), bone))
            parts.append(box(f"sucker_{i}_{j}", (-.45, taper / 2, .8), (.9, .5, 1.1),
                             (100 + j * 8, i * 8), bone))
            parent = bone
    anims = [("idle", 2.2, True, {**{f"arm_{i}_1": [0, (-1)**i * 10, 0, 0] for i in range(6)}, "mantle": [0, 3, -2, 0]}),
             ("swim", 1.2, True, {**{f"arm_{i}_0": [0, (-1)**i * 20, (-1)**i * 8, 0] for i in range(6)}, "mantle": [0, -5, 4, 0]}),
             ("hatch", 1.8, False, {"mantle": [0, 10, -15, 0], "head": [0, -8, 14, 0],
                                     "arm_0_0": [0, -20, 30, 0], "arm_3_0": [0, 20, -30, 0]}),
             ("attack", 1.25, False, {"head": [0, -16, 30, 0], "arm_0_0": [0, -24, 46, 0],
                                       "arm_3_0": [0, -24, 46, 0]})]
    make_model("Aquamarine Young Kraken", "young_kraken", SIZE, bones, parts, anims)


def egg():
    bones = [("shell", (0, -6, 0), "root"), ("cap", (0, -9, 0), "shell"),
             ("tendril_left", (-2, -5, -2), "shell"), ("tendril_right", (2, -5, 2), "shell")]
    parts = [box("egg_body", (-4, -12, -4), (8, 12, 8), (0, 0), "shell"),
             box("egg_band", (-4.3, -8, -4.3), (8.6, 2, 8.6), (24, 0), "shell"),
             box("crown", (-2.8, -15, -2.8), (5.6, 4, 5.6), (48, 0), "cap"),
             box("biolume_window", (-1.5, -7, -4.5), (3, 3, .6), (64, 0), "shell"),
             box("tendril_l", (-.5, -4, -.5), (1, 6, 1), (72, 0), "tendril_left"),
             box("tendril_r", (-.5, -4, -.5), (1, 6, 1), (80, 0), "tendril_right")]
    anims = [("pulse", 2.0, True, {"shell": [0, 3, -2, 0], "cap": [0, -8, 5, 0]}),
             ("hatch", 1.8, False, {"cap": [0, 18, -8, 0], "tendril_left": [0, -16, 22, 0],
                                     "tendril_right": [0, 16, -22, 0]})]
    make_model("Aquamarine Kraken Egg", "kraken_egg", SIZE, bones, parts, anims)


def export_java():
    for template in JAVA_TEMPLATES.glob("*/*.java.template"):
        package = template.parent.name
        destination = JAVA_ROOT / package / template.name.removesuffix(".template")
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(template, destination)


if __name__ == "__main__":
    tentacle()
    young()
    egg()
    export_java()
    print("Generated tentacle, young kraken, and egg Blockbench models with UV guides.")
