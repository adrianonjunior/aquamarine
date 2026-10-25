#!/usr/bin/env python3
"""Gera o modelo editável e o gabarito UV da Baleia-de-Nereia."""
from __future__ import annotations

import json
import shutil
import uuid
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[3]
BASE = Path(__file__).resolve().parent
SIZE = 1024
JAVA_ROOT = ROOT / "src/client/java/com/aquamarine/client/entity/whale"
UUID_NS = uuid.NAMESPACE_URL

# Eixo Z aponta para a cabeça. Cada entrada é (nome, origem, dimensões, UV, osso).
BOXES = [
    ("main_mantle", (-40,-29,-96), (80,58,192), (0,0), "body"),
    ("belly_keel", (-30,20,-75), (60,20,145), (0,80), "body"),
    ("back_ridge", (-24,-39,-57), (48,12,102), (0,160), "body"),
    ("tail_peduncle", (-15,-10,72), (30,28,52), (96,160), "tail"),
    ("left_fluke", (-81,-4,110), (69,8,45), (0,196), "tail"),
    ("right_fluke", (12,-4,110), (69,8,45), (96,196), "tail"),
    ("fluke_keel", (-18,3,116), (36,13,28), (176,196), "tail"),
    ("rostrum", (-34,-22,-135), (68,38,53), (144,0), "head"),
    ("lower_jaw", (-30,7,-130), (60,20,49), (144,64), "jaw"),
    ("chin_plate", (-23,20,-115), (46,8,28), (144,112), "jaw"),
    ("left_brow", (-42,-28,-127), (16,12,33), (0,232), "head"),
    ("right_brow", (26,-28,-127), (16,12,33), (32,232), "head"),
    ("left_eye_socket", (-37,-22,-128), (6,8,10), (64,232), "head"),
    ("right_eye_socket", (31,-22,-128), (6,8,10), (80,232), "head"),
    ("left_luminous_eye", (-38,-21,-130), (2,4,5), (96,232), "head"),
    ("right_luminous_eye", (36,-21,-130), (2,4,5), (112,232), "head"),
    ("blowhole_rim", (-11,-33,-107), (22,5,14), (128,232), "head"),
    ("blowhole", (-6,-36,-104), (12,3,8), (160,232), "head"),
    ("left_gill_plate", (-41,-7,-36), (5,28,63), (192,0), "body"),
    ("right_gill_plate", (36,-7,-36), (5,28,63), (192,64), "body"),
    ("left_pectoral", (-103,13,-44), (77,11,78), (0,112), "left_fin"),
    ("left_fin_keel", (-96,24,-38), (67,5,61), (96,112), "left_fin"),
    ("right_pectoral", (26,13,-44), (77,11,78), (0,80), "right_fin"),
    ("right_fin_keel", (29,24,-38), (67,5,61), (96,80), "right_fin"),
    ("dorsal_sail", (-11,-54,26), (22,28,31), (192,128), "dorsal"),
    ("dorsal_spur", (-6,-62,43), (12,10,13), (224,128), "dorsal"),
    ("glow_left_gill", (-43,-4,-25), (3,5,51), (192,160), "body"),
    ("glow_right_gill", (40,-4,-25), (3,5,51), (224,160), "body"),
    ("saddle_pad", (-24,-37,-20), (48,8,42), (160,112), "saddle"),
    ("saddle_seat", (-20,-49,-13), (40,12,25), (160,160), "saddle"),
    ("saddle_front_arch", (-21,-50,-20), (42,4,5), (160,208), "saddle"),
    ("saddle_back_arch", (-21,-50,14), (42,4,5), (208,208), "saddle"),
    ("left_saddle_strap", (-28,-33,-21), (4,27,6), (208,224), "saddle"),
    ("right_saddle_strap", (24,-33,-21), (4,27,6), (224,224), "saddle"),
]
BONES = [("body",(0,-40,0),"root"),("head",(0,0,-72),"body"),("jaw",(0,0,-72),"body"),
         ("left_fin",(-31,11,-36),"body"),("right_fin",(31,11,-36),"body"),
         ("tail",(0,0,75),"body"),("dorsal",(0,-36,22),"body"),("saddle",(0,-29,-8),"body")]
ANIMS = {
    "idle": (3.2, True, {"body": [(0,0,0),(0,-1.5,0),(0,0,0),(0,1.5,0)], "tail": [(0,0,0),(0,0,3),(0,0,-3),(0,0,0)], "left_fin": [(0,0,0),(0,0,-3),(0,0,2),(0,0,0)], "right_fin": [(0,0,0),(0,0,3),(0,0,-2),(0,0,0)]}),
    "swim": (1.6, True, {"body": [(0,0,-2),(0,0,3),(0,0,-3),(0,0,-2)], "tail": [(0,-8,0),(0,16,0),(0,-17,0),(0,-8,0)], "left_fin": [(0,0,-4),(0,0,13),(0,0,-9),(0,0,-4)], "right_fin": [(0,0,4),(0,0,-13),(0,0,9),(0,0,4)]}),
    "sing": (2.4, False, {"head": [(0,0,0),(0,0,-7),(0,0,4),(0,0,0)], "jaw": [(0,0,0),(0,0,10),(0,0,-3),(0,0,0)], "body": [(0,0,0),(0,-2,0),(0,3,0),(0,0,0)], "left_fin": [(0,0,0),(0,0,-14),(0,0,8),(0,0,0)], "right_fin": [(0,0,0),(0,0,14),(0,0,-8),(0,0,0)]}),
    "surface": (2.0, False, {"body": [(0,0,0),(0,-10,0),(0,-18,0),(0,0,0)], "head": [(0,0,0),(0,9,0),(0,14,0),(0,0,0)], "tail": [(0,0,0),(18,0,0),(30,0,0),(0,0,0)], "left_fin": [(0,0,0),(0,0,-12),(0,0,-20),(0,0,0)], "right_fin": [(0,0,0),(0,0,12),(0,0,20),(0,0,0)]}),
    "death": (2.3, False, {"body": [(0,0,0),(0,0,-6),(0,0,12),(0,0,18)], "left_fin": [(0,0,0),(0,0,-18),(0,0,-35),(0,0,-48)], "right_fin": [(0,0,0),(0,0,18),(0,0,35),(0,0,48)], "tail": [(0,0,0),(0,0,12),(0,0,28),(0,0,35)]}),
}

def pack_uv() -> dict[str, tuple[int, int]]:
    """Pack vanilla box-UV rectangles with a two-pixel gutter."""
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

def stable_id(label: str) -> str:
    return str(uuid.uuid5(UUID_NS, "aquamarine-whale-" + label))

def main() -> None:
    uvs = pack_uv()
    groups = {"root": {"name":"root","uuid":stable_id("root"),"origin":[0,0,0],"rotation":[0,0,0],"children":[]}}
    bone_offsets = {"root": (0,0,0)}
    for bone, origin, parent in BONES:
        bone_offsets[bone] = (0,0,0) if parent == "root" else tuple(bone_offsets[parent][i] + origin[i] for i in range(3))
        groups[bone] = {"name":bone,"uuid":stable_id(bone),"origin":list(origin),"rotation":[0,0,0],"children":[]}
        groups[parent]["children"].append(groups[bone])
    elements = []
    for name, origin, size, _uv, bone in BOXES:
        x,y,z = origin; dx,dy,dz = size
        uid = stable_id(name)
        local = [origin[i] - bone_offsets[bone][i] for i in range(3)]
        elements.append({"name":name,"uuid":uid,"from":local,"to":[local[0]+dx,local[1]+dy,local[2]+dz],"origin":local,"rotation":[0,0,0],"uv_offset":list(uvs[name])})
        groups[bone]["children"].append(uid)
    animations = []
    for name,(duration,loop,poses) in ANIMS.items():
        animators = {}
        for bone, frames in poses.items():
            keys = []
            for idx,(time,angle) in enumerate(zip((0,duration*.28,duration*.62,duration),frames)):
                keys.append({"channel":"rotation","data_points":[{"x":str(angle[0]),"y":str(angle[1]),"z":str(angle[2])}],"time":time,"interpolation":"catmullrom","uuid":stable_id(f"{name}-{bone}-{idx}")})
            animators[stable_id(f"anim-{name}-{bone}")]={"name":bone,"type":"bone","keyframes":keys}
        animations.append({"name":name,"uuid":stable_id("animation-"+name),"loop":"loop" if loop else "once","length":duration,"override":True,"animators":animators})
    model={"meta":{"format_version":"4.10","model_format":"free","box_uv":True,"visible_box":[16,16,16],"variable_placeholders":""},"name":"Baleia-de-Nereia","model_identifier":"whale","visible_box":[16,16,16],"variable_placeholders":"","resolution":{"width":SIZE,"height":SIZE},"elements":elements,"outliner":[groups["root"]],"textures":[{"name":"uv.png","id":stable_id("texture"),"path":"uv.png","width":SIZE,"height":SIZE,"uv_width":SIZE,"uv_height":SIZE,"particle":True,"layers":[]}],"animations":animations}
    BASE.mkdir(parents=True, exist_ok=True)
    (BASE/"whale.bbmodel").write_text(json.dumps(model,indent=2),encoding="utf-8")
    uv=Image.new("RGBA",(SIZE,SIZE),(11,34,57,255)); draw=ImageDraw.Draw(uv)
    palette=[(14,68,97,255),(22,103,124,255),(40,144,148,255),(98,205,186,255),(150,233,204,255),(131,95,52,255),(202,162,83,255)]
    for i,(name,_,(dx,dy,dz),_,_) in enumerate(BOXES):
        x,y=uvs[name]; color=palette[i%len(palette)]
        draw.rectangle((x,y,x+2*(dx+dz)-1,y+dy+dz-1),fill=color,outline=(220,220,190,255))
        draw.line((x,y,x+2*(dx+dz)-1,y+dy+dz-1),fill=(220,245,220,255),width=2)
    uv.save(BASE/"uv.png")
    constants = [f"    public static final int SIZE = {SIZE};"]
    constants += [f"    public static final int {name.upper()}_U = {uvs[name][0]}, {name.upper()}_V = {uvs[name][1]};" for name, *_ in BOXES]
    (JAVA_ROOT/"WhaleUv.java").write_text("package com.aquamarine.client.entity.whale;\n\npublic final class WhaleUv {\n"+"\n".join(constants)+"\n    private WhaleUv() {}\n}\n",encoding="utf-8")
    for src in sorted((BASE/"java").glob("*.java.template")):
        JAVA_ROOT.mkdir(parents=True,exist_ok=True)
        shutil.copyfile(src,JAVA_ROOT/src.name.removesuffix(".template"))
    print("Generated whale.bbmodel, uv.png and Whale Java classes.")

if __name__ == "__main__":
    main()
