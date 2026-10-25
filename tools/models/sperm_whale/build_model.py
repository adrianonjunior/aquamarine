#!/usr/bin/env python3
"""Gera o modelo editável e o gabarito UV do Cachalote."""
from __future__ import annotations

import json
import shutil
import uuid
from pathlib import Path

from PIL import Image, ImageDraw

ROOT=Path(__file__).resolve().parents[3]
BASE=Path(__file__).resolve().parent
SIZE=1024
JAVA_ROOT=ROOT/"src/client/java/com/aquamarine/client/entity/sperm_whale"
NS=uuid.NAMESPACE_URL
BOXES=[
    ("torpedo_mantle",(-35,-28,-79),(70,56,158),(0,0),"body"),
    ("belly_keel",(-26,19,-61),(52,18,123),(0,64),"body"),
    ("shoulder_hump",(-38,-39,-29),(76,20,74),(0,112),"body"),
    ("back_ridge",(-16,-44,3),(32,8,52),(112,112),"body"),
    ("square_spermaceti_head",(-39,-34,-124),(78,61,55),(96,0),"head"),
    ("forehead_boss",(-34,-42,-119),(68,11,42),(96,64),"head"),
    ("lower_jaw",(-24,25,-119),(48,19,94),(176,0),"jaw"),
    ("jaw_keel",(-18,39,-97),(36,7,61),(176,64),"jaw"),
    ("left_eye_socket",(-40,-4,-108),(6,9,12),(0,160),"head"),
    ("right_eye_socket",(34,-4,-108),(6,9,12),(32,160),"head"),
    ("left_eye_glint",(-41,-3,-110),(2,4,6),(64,160),"head"),
    ("right_eye_glint",(39,-3,-110),(2,4,6),(80,160),"head"),
    ("offset_blowhole_rim",(-19,-45,-106),(22,5,16),(96,160),"head"),
    ("offset_blowhole",(-13,-48,-103),(12,3,9),(144,160),"head"),
    ("left_pectoral",(-91,12,-44),(63,12,68),(0,184),"left_fin"),
    ("left_fin_edge",(-84,23,-38),(52,5,49),(96,184),"left_fin"),
    ("right_pectoral",(28,12,-44),(63,12,68),(0,224),"right_fin"),
    ("right_fin_edge",(32,23,-38),(52,5,49),(96,224),"right_fin"),
    ("tail_peduncle",(-13,-7,63),(26,23,49),(160,112),"tail"),
    ("left_fluke",(-71,-2,100),(58,8,42),(160,160),"tail"),
    ("right_fluke",(13,-2,100),(58,8,42),(160,200),"tail"),
    ("fluke_keel",(-14,5,107),(28,12,23),(208,160),"tail"),
    ("dorsal_fin",(-8,-53,31),(16,22,29),(208,200),"dorsal"),
    ("scar_left_1",(-38,-5,-2),(3,4,27),(128,112),"body"),
    ("scar_left_2",(-38,1,18),(3,4,22),(144,112),"body"),
    ("scar_left_3",(-38,6,35),(3,3,16),(160,112),"body"),
    ("scar_right_1",(35,-4,4),(3,4,25),(176,112),"body"),
    ("scar_right_2",(35,2,24),(3,4,20),(192,112),"body"),
    ("scar_right_3",(35,7,40),(3,3,13),(208,112),"body"),
    ("jaw_rake_left",(-22,39,-45),(4,1,36),(112,160),"jaw"),
    ("jaw_rake_mid",(-2,39,-45),(4,1,36),(128,160),"jaw"),
    ("jaw_rake_right",(18,39,-45),(4,1,36),(144,160),"jaw"),
]
BONES=[("body",(0,-46,0),"root"),("head",(0,0,-60),"body"),("jaw",(0,0,-50),"body"),
       ("left_fin",(-25,10,-35),"body"),("right_fin",(25,10,-35),"body"),
       ("tail",(0,0,65),"body"),("dorsal",(0,-38,24),"body")]
ANIMS={
 "idle":(3.4,True,{"body":[(0,0,0),(0,-1,0),(0,1,0),(0,0,0)],"tail":[(0,0,0),(0,0,2),(0,0,-2),(0,0,0)],"head":[(0,0,0),(1,0,0),(-1,0,0),(0,0,0)]}),
 "swim":(1.5,True,{"body":[(0,0,-2),(0,0,3),(0,0,-3),(0,0,-2)],"tail":[(0,-8,0),(0,17,0),(0,-18,0),(0,-8,0)],"left_fin":[(0,0,-3),(0,0,11),(0,0,-8),(0,0,-3)],"right_fin":[(0,0,3),(0,0,-11),(0,0,8),(0,0,3)]}),
 "sonic":(1.8,False,{"head":[(0,0,0),(0,-7,0),(0,8,0),(0,0,0)],"jaw":[(0,0,0),(0,0,5),(0,0,-14),(0,0,0)],"body":[(0,0,0),(0,-3,0),(0,6,0),(0,0,0)],"tail":[(0,0,0),(0,0,-9),(0,0,18),(0,0,0)],"left_fin":[(0,0,0),(0,0,-8),(0,0,14),(0,0,0)],"right_fin":[(0,0,0),(0,0,8),(0,0,-14),(0,0,0)]}),
 "death":(2.2,False,{"body":[(0,0,0),(0,0,-9),(0,0,16),(0,0,22)],"head":[(0,0,0),(0,0,8),(0,0,-10),(0,0,-16)],"left_fin":[(0,0,0),(0,0,-15),(0,0,-32),(0,0,-45)],"right_fin":[(0,0,0),(0,0,15),(0,0,32),(0,0,45)],"tail":[(0,0,0),(0,0,14),(0,0,28),(0,0,38)]})
}

def pack_uv()->dict[str,tuple[int,int]]:
    rows=[]; result={}
    ordered=sorted(BOXES,key=lambda b:(-(b[2][1]+b[2][2]),-2*(b[2][0]+b[2][2])))
    for name,_,(dx,dy,dz),_,_ in ordered:
        width,height=2*(dx+dz),dy+dz
        fits=[(row["width"]-row["x"]-width,row) for row in rows
              if height<=row["height"] and row["x"]+width+2<=SIZE]
        if fits:
            _,row=min(fits,key=lambda candidate:candidate[0]); x,y=row["x"],row["y"]; row["x"]+=width+2
        else:
            y=sum(row["height"]+2 for row in rows)
            if y+height+2>SIZE or width+2>SIZE: raise ValueError(f"UV layout exceeds {SIZE}x{SIZE} at {name}")
            rows.append({"x":width+2,"y":y,"height":height,"width":SIZE}); x=0
        result[name]=(x,y)
    return result

def uid(s:str)->str: return str(uuid.uuid5(NS,"aquamarine-sperm-whale-"+s))

def main()->None:
    uvs=pack_uv()
    groups={"root":{"name":"root","uuid":uid("root"),"origin":[0,0,0],"rotation":[0,0,0],"children":[]}}
    bone_offsets={"root":(0,0,0)}
    for bone,origin,parent in BONES:
        bone_offsets[bone]=(0,0,0) if parent=="root" else tuple(bone_offsets[parent][i]+origin[i] for i in range(3))
        groups[bone]={"name":bone,"uuid":uid(bone),"origin":list(origin),"rotation":[0,0,0],"children":[]}; groups[parent]["children"].append(groups[bone])
    elements=[]
    for name,origin,size,_uv,bone in BOXES:
        x,y,z=origin; dx,dy,dz=size; eid=uid(name); local=[origin[i]-bone_offsets[bone][i] for i in range(3)]
        elements.append({"name":name,"uuid":eid,"from":local,"to":[local[0]+dx,local[1]+dy,local[2]+dz],"origin":local,"rotation":[0,0,0],"uv_offset":list(uvs[name])}); groups[bone]["children"].append(eid)
    animations=[]
    for name,(duration,loop,poses) in ANIMS.items():
        animators={}
        for bone,frames in poses.items():
            keys=[]
            for i,(time,angle) in enumerate(zip((0,duration*.28,duration*.62,duration),frames)):
                keys.append({"channel":"rotation","data_points":[{"x":str(angle[0]),"y":str(angle[1]),"z":str(angle[2])}],"time":time,"interpolation":"catmullrom","uuid":uid(f"{name}-{bone}-{i}")})
            animators[uid(f"anim-{name}-{bone}")]={"name":bone,"type":"bone","keyframes":keys}
        animations.append({"name":name,"uuid":uid("anim-"+name),"loop":"loop" if loop else "once","length":duration,"override":True,"animators":animators})
    model={"meta":{"format_version":"4.10","model_format":"free","box_uv":True,"visible_box":[12,12,12],"variable_placeholders":""},"name":"Aquamarine Cachalote","model_identifier":"sperm_whale","visible_box":[12,12,12],"variable_placeholders":"","resolution":{"width":SIZE,"height":SIZE},"elements":elements,"outliner":[groups["root"]],"textures":[{"name":"uv.png","id":uid("texture"),"path":"uv.png","width":SIZE,"height":SIZE,"uv_width":SIZE,"uv_height":SIZE,"particle":True,"layers":[]}],"animations":animations}
    BASE.mkdir(parents=True,exist_ok=True); (BASE/"sperm_whale.bbmodel").write_text(json.dumps(model,indent=2),encoding="utf-8")
    uv=Image.new("RGBA",(SIZE,SIZE),(15,31,47,255)); draw=ImageDraw.Draw(uv)
    palette=[(17,51,73,255),(24,78,91,255),(46,112,119,255),(101,174,164,255),(140,194,185,255),(92,69,49,255),(174,140,84,255)]
    for i,(name,_,(dx,dy,dz),_,_) in enumerate(BOXES):
        x,y=uvs[name]
        draw.rectangle((x,y,x+2*(dx+dz)-1,y+dy+dz-1),fill=palette[i%len(palette)],outline=(219,205,170,255))
        draw.line((x,y,x+2*(dx+dz)-1,y+dy+dz-1),fill=(225,231,202,255),width=2)
    uv.save(BASE/"uv.png")
    constants=[f"    public static final int SIZE = {SIZE};"]
    constants += [f"    public static final int {name.upper()}_U = {uvs[name][0]}, {name.upper()}_V = {uvs[name][1]};" for name,*_ in BOXES]
    (JAVA_ROOT/"SpermWhaleUv.java").write_text("package com.aquamarine.client.entity.sperm_whale;\n\npublic final class SpermWhaleUv {\n"+"\n".join(constants)+"\n    private SpermWhaleUv() {}\n}\n",encoding="utf-8")
    for src in sorted((BASE/"java").glob("*.java.template")):
        JAVA_ROOT.mkdir(parents=True,exist_ok=True); shutil.copyfile(src,JAVA_ROOT/src.name.removesuffix(".template"))
    print("Generated sperm_whale.bbmodel, uv.png and SpermWhale Java classes.")

if __name__=="__main__": main()
