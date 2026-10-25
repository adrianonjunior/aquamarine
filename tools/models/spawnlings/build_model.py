#!/usr/bin/env python3
"""Generate editable Blockbench models and UV guides for the Kraken's offspring.

Every cube gets the standard Minecraft box-UV net (width 2*(w+d), height h+d; top/bottom above,
then right/front/left/back) and the nets of one creature are shelf-packed into the texture without
overlapping. Fractional cube sizes are rounded up for the footprint. The same offsets are written to
the .bbmodel and to the UV block of each Java model template (between the UV:BEGIN / UV:END markers).
Afterwards run tools/gen_spawnlings_texture.py (it paints over uv.png) and tools/models/spawnlings/check_uv.py.
"""
from __future__ import annotations

import json
import math
import re
import shutil
import uuid
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[3]
BASE = Path(__file__).resolve().parent
UUID_COUNTER = 0
SIZE = 128
GUTTER = 1  # transparent pixels kept between two nets
JAVA_TEMPLATES = BASE / "java"
JAVA_ROOT = ROOT / "src/client/java/com/aquamarine/client/entity"
RESOURCE_TEXTURES = ROOT / "src/main/resources/assets/aquamarine/textures/entity"

BG = (24, 42, 64, 255)
# Familia da peca (A/B/C), lida por gen_spawnlings_texture.py para escolher a rampa de cores.
FAMILY = {"A": (27, 110, 132, 255), "B": (44, 166, 163, 255), "C": (105, 75, 144, 255)}
CREAM = (225, 208, 146, 255)
CREAM_FACE = (235, 230, 200, 255)

# Constante Java -> nomes dos elementos do .bbmodel, na ordem do vetor. O 3o item diz se e um int[] unico.
JAVA_UV = {
    "tentacle": [
        ("SEGMENT_UV", [f"segment_{i}" for i in range(7)], False),
        ("SUCKER_UV", [f"sucker_{i}_{j}" for i in range(7) for j in range(3)], False),
        ("HOOK_UV", ["anchor_hook"], True),
    ],
    "young_kraken": [
        ("MANTLE_UV", ["mantle_shell"], True),
        ("RIDGE_UV", ["mantle_ridge"], True),
        ("HEAD_UV", ["head"], True),
        ("BEAK_UV", ["beak"], True),
        ("EYE_UV", ["eye_left", "eye_right"], False),
        ("ARM_UV", [f"arm_{i}_segment_{j}" for i in range(6) for j in range(3)], False),
        ("SUCKER_UV", [f"sucker_{i}_{j}" for i in range(6) for j in range(3)], False),
    ],
    "kraken_egg": [
        ("BODY_UV", ["egg_body"], True),
        ("BAND_UV", ["egg_band"], True),
        ("CROWN_UV", ["crown"], True),
        ("WINDOW_UV", ["biolume_window"], True),
        ("TENDRIL_UV", ["tendril_l", "tendril_r"], False),
    ],
}
JAVA_MODEL = {"tentacle": "TentacleModel", "young_kraken": "YoungKrakenModel", "kraken_egg": "KrakenEggModel"}


def uid() -> str:
    global UUID_COUNTER
    UUID_COUNTER += 1
    return str(uuid.uuid5(uuid.NAMESPACE_URL, f"aquamarine-spawnlings-{UUID_COUNTER}"))


def box(name, origin, dims, group, family):
    x, y, z = origin
    dx, dy, dz = dims
    return {"name": name, "uuid": uid(), "from": [x, y, z], "to": [x + dx, y + dy, z + dz],
            "origin": list(origin), "rotation": [0, 0, 0], "uv_offset": [0, 0],
            "_group": group, "_family": family}


def net_dims(part):
    """(w, h, d) of the cube rounded up to whole pixels."""
    return tuple(max(1, math.ceil(round(abs(part["to"][i] - part["from"][i]), 4))) for i in range(3))


def net_size(part):
    w, h, d = net_dims(part)
    return 2 * (w + d), h + d


def pack(sizes, size):
    """Shelf packing: tallest nets first, left to right, new shelf when the row is full."""
    order = sorted(range(len(sizes)), key=lambda i: (-sizes[i][1], -sizes[i][0], i))
    pos = {}
    x = y = shelf = 0
    for i in order:
        w, h = sizes[i]
        if x + w > size:
            x, y, shelf = 0, y + shelf + GUTTER, 0
        if y + h > size or w > size:
            raise SystemExit(f"nets do not fit in {size}x{size}")
        pos[i] = (x, y)
        x += w + GUTTER
        shelf = max(shelf, h)
    return [pos[i] for i in range(len(sizes))]


def draw_net(draw, part):
    """Fill the whole net rectangle with the part family colour; cream outline on chunky cubes."""
    x, y = part["uv_offset"]
    w, h, d = net_dims(part)
    nw, nh = net_size(part)
    draw.rectangle((x, y, x + nw - 1, y + nh - 1), fill=FAMILY[part["_family"]])
    if min(w, h, d) < 2:
        return
    # face dividers first (top and bottom above; right, front, left, back below), then the outer ring
    for fx, fy, fw, fh in ((d, 0, w, d), (d + w, 0, w, d), (0, d, d, h), (d, d, w, h),
                           (d + w, d, d, h), (2 * d + w, d, w, h)):
        draw.rectangle((x + fx, y + fy, x + fx + fw - 1, y + fy + fh - 1), outline=CREAM_FACE)
    draw.rectangle((x, y, x + nw - 1, y + nh - 1), outline=CREAM)


def make_model(name, identifier, size, bones, parts, animations):
    global UUID_COUNTER
    UUID_COUNTER = 0
    groups = {"root": {"name": "root", "uuid": uid(), "origin": [0, 24, 0],
                       "rotation": [0, 0, 0], "children": []}}
    for bone, origin, parent in bones:
        groups[bone] = {"name": bone, "uuid": uid(), "origin": list(origin), "rotation": [0, 0, 0], "children": []}
        groups[parent]["children"].append(groups[bone])
    for p, off in zip(parts, pack([net_size(p) for p in parts], size)):
        p["uv_offset"] = list(off)
    for p in parts:
        groups[p["_group"]]["children"].append(p["uuid"])
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
    uv = Image.new("RGBA", (size, size), BG)
    draw = ImageDraw.Draw(uv)
    for p in parts:
        draw_net(draw, p)
    layout = {p["name"]: tuple(p["uv_offset"]) for p in parts}
    for p in parts:
        del p["_group"], p["_family"]
    data = {"meta": {"format_version": "4.10", "model_format": "free", "box_uv": True,
                      "visible_box": [4, 4, 4], "variable_placeholders": ""},
            "name": name, "model_identifier": identifier, "visible_box": [4, 4, 4],
            "variable_placeholders": "", "resolution": {"width": size, "height": size},
            "elements": parts, "outliner": [groups["root"]],
            "textures": [{"name": "uv.png", "id": uid(), "path": "uv.png", "width": size,
                          "height": size, "uv_width": size, "uv_height": size, "particle": True, "layers": []}],
            "animations": anims}
    folder = BASE / identifier
    folder.mkdir(parents=True, exist_ok=True)
    (folder / f"{identifier}.bbmodel").write_text(json.dumps(data, separators=(",", ":")), encoding="utf-8")
    uv_path = folder / "uv.png"
    uv.save(uv_path)
    texture_path = RESOURCE_TEXTURES / identifier / f"{identifier}.png"
    texture_path.parent.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(uv_path, texture_path)
    patch_java_template(identifier, layout)
    print(f"{identifier}: {len(parts)} cubes packed into {size}x{size}")


def uv_block(identifier, layout):
    lines = []
    for const, names, scalar in JAVA_UV[identifier]:
        pairs = ["{%d, %d}" % layout[n] for n in names]
        if scalar:
            lines.append(f"    private static final int[] {const} = {pairs[0]};")
        else:
            lines.append(f"    private static final int[][] {const} = {{")
            for k in range(0, len(pairs), 6):
                lines.append("        " + ", ".join(pairs[k:k + 6]) + ("," if k + 6 < len(pairs) else ""))
            lines.append("    };")
    return "\n".join(lines)


def patch_java_template(identifier, layout):
    path = JAVA_TEMPLATES / identifier / f"{JAVA_MODEL[identifier]}.java.template"
    text = path.read_bytes().decode("utf-8").replace("\r\n", "\n")
    text, n = re.subn(r"(    // UV:BEGIN[^\n]*\n).*?(    // UV:END)",
                      lambda m: m.group(1) + uv_block(identifier, layout) + "\n" + m.group(2), text, flags=re.S)
    if n != 1:
        raise SystemExit(f"{path}: UV:BEGIN/UV:END markers not found")
    path.write_bytes(text.replace("\n", "\r\n").encode("utf-8"))


def tentacle():
    bones, parts = [], []
    parent = "root"
    for i in range(7):
        bone = f"segment_{i}"
        bones.append((bone, (0, -i * 4, 0), parent))
        r = 2.5 - i * .23
        parts.append(box(bone, (-r, -4, -r), (r * 2, 4.3, r * 2), bone, "A"))
        for j in range(3):
            parts.append(box(f"sucker_{i}_{j}", (-r + .35, -3.8, -r - .6),
                             (max(.65, r - .4), .55, .8), bone, "B"))
        parent = bone
    parts.extend([box("barb_left", (-1.4, -27, -.3), (1, 3, .8), parent, "C"),
                  box("barb_right", (.4, -27, -.3), (1, 3, .8), parent, "C"),
                  box("anchor_hook", (-1.5, -29, -.5), (3, 2, 1), parent, "C")])
    anims = [("idle", 2.4, True, {f"segment_{i}": [0, (-1)**i * 10, (-1)**i * 4, 0] for i in range(1, 7)}),
             ("slam", 1.35, False, {"segment_1": [0, -34, 52, 0], "segment_2": [0, -22, 38, 0],
                                     "segment_3": [0, 8, -16, 0]}),
             ("grab", 1.6, False, {"segment_2": [0, -14, 28, 0], "segment_3": [0, -25, 42, 0],
                                    "segment_4": [0, 8, -12, 0]}),
             ("release", 1.1, False, {"segment_3": [0, 20, -10, 0], "segment_4": [0, 16, -8, 0]})]
    make_model("Aquamarine Tentacle", "tentacle", SIZE, bones, parts, anims)


def young():
    bones = [("mantle", (0, -12, -1), "root"), ("head", (0, -7, 1), "root")]
    parts = [box("mantle_shell", (-5, -17, -7), (10, 8, 13), "mantle", "A"),
             box("mantle_ridge", (-3, -19, -5), (6, 3, 9), "mantle", "C"),
             box("head", (-4, -11, -5), (8, 7, 11), "head", "A"),
             box("beak", (-1.5, -6, 5), (3, 2, 2), "head", "C"),
             box("eye_left", (-3.5, -9, 5), (2, 2, 1), "head", "B"),
             box("eye_right", (1.5, -9, 5), (2, 2, 1), "head", "B")]
    for i in range(6):
        angle = math.tau * i / 6
        x, z = math.sin(angle) * 3.1, math.cos(angle) * 2.8
        parent = "root"
        for j in range(3):
            bone = f"arm_{i}_{j}"
            bones.append((bone, (x, -7, z + j * 2), parent))
            taper = 1.55 - j * .32
            parts.append(box(f"arm_{i}_segment_{j}", (-taper / 2, -taper / 2, 0),
                             (taper, taper, 3), bone, "A"))
            parts.append(box(f"sucker_{i}_{j}", (-.45, taper / 2, .8), (.9, .5, 1.1), bone, "C"))
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
    parts = [box("egg_body", (-4, -12, -4), (8, 12, 8), "shell", "A"),
             box("egg_band", (-4.3, -8, -4.3), (8.6, 2, 8.6), "shell", "C"),
             box("crown", (-2.8, -15, -2.8), (5.6, 4, 5.6), "cap", "B"),
             box("biolume_window", (-1.5, -7, -4.5), (3, 3, .6), "shell", "B"),
             box("tendril_l", (-.5, -4, -.5), (1, 6, 1), "tendril_left", "C"),
             box("tendril_r", (-.5, -4, -.5), (1, 6, 1), "tendril_right", "C")]
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
