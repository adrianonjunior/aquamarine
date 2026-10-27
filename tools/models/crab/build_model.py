#!/usr/bin/env python3
"""Caranguejo (7H): rig articulado, pixel art 64px e oito clipes. Usa o exportador das criaturas do Claude.
Paleta: casco coral/terracota, juntas vinho, pontas marfim, olhos escuros com reflexo (sem emissão).
Execute com C:/Python314/python tools/models/crab/build_model.py [--register].
"""
from pathlib import Path
import sys, math, json, base64
from PIL import Image, ImageDraw
BASE = Path(__file__).resolve().parent
ROOT = BASE.parents[2]
sys.path.insert(0, str(BASE.parent / "spawnlings"))
import build_model as k

CORAL = [(53,24,29),(91,35,37),(139,49,43),(181,69,45),(220,105,60),(246,151,86)]
JOINT = [(34,21,30),(61,31,40),(95,44,48),(134,62,57),(170,86,66),(204,116,81)]
IVORY = [(79,60,48),(120,93,66),(163,132,89),(203,173,118),(230,208,157),(255,238,195)]
EYE = [(10,17,23),(18,29,35),(28,45,49),(42,65,66),(96,133,124),(224,246,218)]
PALETTES = {"coral":CORAL,"joint":JOINT,"ivory":IVORY,"eye":EYE}

def crab():
    c = k.Creature("crab", "Crab", (64,64), "Caranguejo de praia: casco coral, oito pernas articuladas, pinça assimétrica e concha marfim na garra.", 71)
    c.part("body", None, (0,20,0))
    c.cube("body", (-4.5,-2,-3), (9,3,6), "coral", name="carapace")
    c.cube("body", (-3.5,-3,-2.5), (7,1,5), "coral", name="crown")
    c.cube("body", (-3.5,1,-2.5), (7,1,5), "ivory", name="belly")
    c.cube("body", (-5,-1,-2), (1,1,4), "coral", name="rim_l")
    c.cube("body", (4,-1,-2), (1,1,4), "coral", name="rim_r")
    c.part("mouth", "body", (0,0,-3))
    c.cube("mouth", (-1,-.2,-.5),(2,1,1),"ivory")
    for side,sign in (("l",-1),("r",1)):
        c.part("eye_"+side,"body",(sign*2,-2,-2.5),(0,0,sign*12))
        c.cube("eye_"+side,(-.4,-2,-.4),(.8,2,.8),"joint",name="stalk_"+side)
        c.cube("eye_"+side,(-.75,-3,-.7),(1.5,1.3,1.4),"eye",name="eye_"+side)
        for i in range(4):
            n=f"leg_{side}{i}"
            c.part(n,"body",(sign*4,.25,-2+i*1.3),(0,sign*(25-i*16),sign*-16))
            c.cube(n,(-3 if sign<0 else 0,-.55,-.55),(3,1.1,1.1),"coral")
            c.part(n+"_tip",n,(sign*2.7,0,0),(0,0,sign*48))
            c.cube(n+"_tip",(-2.8 if sign<0 else 0,-.4,-.45),(2.8,.8,.9),"joint")
            c.cube(n+"_tip",(-3.2 if sign<0 else 2.6,-.25,-.3),(.6,.5,.6),"ivory")
        arm="arm_"+side
        c.part(arm,"body",(sign*3.4,0,-2),(0,sign*-25,sign*12))
        c.cube(arm,(-.6,-.5,-3),(1.2,1.1,3.2),"joint")
        c.part("claw_"+side,arm,(0,0,-3),(0,sign*16,0))
        w=2.6 if side=="r" else 2
        c.cube("claw_"+side,(-w/2,-1.4,-2.7),(w,2.5,3),"coral",name="palm_"+side)
        c.cube("claw_"+side,(-w/2,-.9,-4),(.9,1.7,1.8),"ivory",name="fixed_finger_"+side)
        c.part("finger_"+side,"claw_"+side,(w/2,0,-1.8),(0,-20,0))
        c.cube("finger_"+side,(-.8,-.9,-2.2),(.9,1.7,2.5),"coral")
    c.part("held_shell","claw_l",(0,-.5,-3.5),(0,0,-18))
    c.cube("held_shell",(-1.1,-1.6,-.7),(2.2,2,1.4),"ivory",name="held_shell")
    c.cube("held_shell",(-.65,-2,-.5),(1.3,1,1),"ivory",name="shell_peak")

    idle=k.Clip(4,True).sample("body","position",lambda t:(0,.08*math.sin(t*math.tau/4),0))
    for side,sg in (("l",-1),("r",1)):
        idle.sample("eye_"+side,"rotation",lambda t,sg=sg:(-3+4*math.sin(t*math.tau/4+sg*.3),sg*8*math.sin(t*math.tau/4),sg*2))
    idle.sample("mouth","rotation",lambda t:(8*math.sin(t*math.tau*1.5),0,0),24)
    c.clips["idle_eyes"]=idle
    for name,length,amp in (("walk_side",1.1,22),("flee",.55,30)):
        a=k.Clip(length,True)
        a.sample("body","position",lambda t,l=length:(0,.18*(1-math.cos(t*math.tau*2/l)),0))
        a.sample("body","rotation",lambda t,l=length:(0,0,2*math.sin(t*math.tau/l)))
        for side,sg in (("l",-1),("r",1)):
            for i in range(4):
                phase=(i%2)*math.pi+(math.pi if sg<0 else 0)
                a.sample(f"leg_{side}{i}","rotation",lambda t,p=phase,l=length,sg=sg:(0,amp*math.cos(t*math.tau/l+p),sg*12*math.sin(t*math.tau/l+p)))
                a.sample(f"leg_{side}{i}_tip","rotation",lambda t,p=phase,l=length,sg=sg:(0,0,sg*18*max(0,math.sin(t*math.tau/l+p))))
            a.sample("arm_"+side,"rotation",lambda t,sg=sg,l=length:(-8,sg*5*math.sin(t*math.tau/l+.5),0))
        c.clips[name]=a
    a=k.Clip(1.75).rot("arm_r",(0,(0,0,0)),(.35,(-55,-15,35)),(.7,(-50,-10,30)),(1.1,(-60,-5,35)),(1.75,(0,0,0)))
    a.rot("finger_r",(0,(0,0,0)),(.4,(0,-35,0)),(.7,(0,10,0)),(1,(0,-35,0)),(1.3,(0,10,0)),(1.75,(0,0,0)))
    c.clips["claw_wave"]=a
    a=k.Clip(1.2).pos("body",(0,(0,0,0)),(.25,(0,.2,0)),(.7,(0,-3,0)),(1.2,(0,-8,0)))
    b=k.Clip(1.2).pos("body",(0,(0,-8,0)),(.3,(0,-6,0)),(.85,(0,.2,0)),(1.2,(0,0,0)))
    for side,sg in (("l",-1),("r",1)):
        a.rot("eye_"+side,(0,(0,0,0)),(.35,(65,0,sg*18)),(1.2,(65,0,sg*18)))
        b.rot("eye_"+side,(0,(65,0,sg*18)),(.8,(65,0,sg*18)),(1.2,(0,0,0)))
        for i in range(4):
            a.sample(f"leg_{side}{i}","rotation",lambda t,sg=sg,i=i:(0,sg*20*math.sin(t*math.tau*4+i),sg*12*math.sin(t*math.tau*4+i)),24)
        a.rot("arm_"+side,(0,(0,0,0)),(.5,(20,sg*-30,0)),(1.2,(20,sg*-30,0)))
        b.rot("arm_"+side,(0,(20,sg*-30,0)),(.9,(-12,sg*8,0)),(1.2,(0,0,0)))
    c.clips["burrow"],c.clips["emerge"]=a,b
    a=k.Clip(.8).rot("arm_r",(0,(0,0,0)),(.22,(-25,20,-10)),(.4,(12,-18,0)),(.52,(12,-18,0)),(.8,(0,0,0)))
    a.rot("finger_r",(0,(0,0,0)),(.2,(0,-45,0)),(.4,(0,16,0)),(.55,(0,16,0)),(.8,(0,0,0)))
    a.pos("body",(0,(0,0,0)),(.25,(0,.1,.3)),(.4,(0,.1,-.8)),(.8,(0,0,0)))
    c.clips["pinch"]=a
    a=k.Clip(3.2,True).sample("body","position",lambda t:(0,-1+.07*math.sin(t*math.tau/3.2),0))
    for side,sg in (("l",-1),("r",1)):
        a.rot("eye_"+side,(0,(65,0,sg*12)),(3.2,(65,0,sg*12)))
        a.rot("arm_"+side,(0,(15,sg*-15,0)),(3.2,(15,sg*-15,0)))
    c.clips["sleep"]=a
    for a in c.clips.values(): a.finish()
    return c

def paint(c):
    img=Image.new("RGBA",(64,64)); px=img.load()
    for box in c.cubes():
        R=PALETTES[box.material]
        for face,(u,v,w,h) in k.faces(box).items():
            for y in range(h):
                for x in range(w):
                    shade=3+(face=="top")-2*(face=="bottom")
                    if y==0: shade+=1
                    if x==w-1 or y==h-1: shade-=1
                    if box.name in ("carapace","crown"):
                        # Duas linhas de crescimento do casco e pintas fixas, jamais ruído uniforme.
                        if face=="top" and (x+y)%7==1: shade-=1
                        if face=="front" and y==h-2: shade-=1
                        if face=="top" and x in (2,w-3) and y in (1,h-2): shade=5
                    if box.material=="eye":
                        shade=0
                        if face in ("front","top") and x==0 and y==0: shade=5
                    if box.name.startswith("held_shell") or box.name=="shell_peak":
                        if (x+2*y)%3==0: shade-=2
                    if "finger" in box.name and y>h//2: shade-=1
                    px[u+x,v+y]=(*R[max(0,min(5,shade))],255)
    return img

def main():
    c=crab(); k.pack(c); img=paint(c)
    sheet=ROOT/'src/main/resources/assets/aquamarine/textures/entity/crab'; sheet.mkdir(parents=True,exist_ok=True)
    img.save(sheet/'crab.png'); img.save(BASE/'crab.png'); k.uv_guide(c).save(BASE/'uv.png')
    rig=k.rig_json(c); (BASE/'rig.json').write_text(json.dumps(rig,separators=(',',':')),encoding='utf-8')
    bb=k.bbmodel(c)
    bb['textures'][0]['source']='data:image/png;base64,'+base64.b64encode((BASE/'crab.png').read_bytes()).decode()
    # Clipes também no arquivo editável do Blockbench, usando os UUIDs dos grupos do mesmo exportador.
    groups={}
    def walk(nodes):
        for g in nodes:
            if isinstance(g,dict): groups[g['name']]=g['uuid']; walk(g['children'])
    walk(bb['outliner']); bb['animations']=[]
    for name,clip in rig['clips'].items():
        anim={'name':name,'loop':'loop' if clip['loop'] else 'once','length':clip['length'],'animators':{}}
        for track in clip['tracks']:
            bone=track['bone']; a=anim['animators'].setdefault(groups[bone],{'name':bone,'type':'bone','keyframes':[]})
            for t,vec,interp in track['keys']:
                a['keyframes'].append({'channel':track['channel'],'time':t,'interpolation':interp,'data_points':[dict(zip(('x','y','z'),map(str,vec)))]})
        bb['animations'].append(anim)
    (BASE/'crab.bbmodel').write_text(json.dumps(bb,separators=(',',':')),encoding='utf-8')
    java=ROOT/'src/client/java/com/aquamarine/client/entity/crab'; java.mkdir(parents=True,exist_ok=True)
    for suffix,fn in [('Model',k.model_java),('Animations',k.animations_java)]:
        (java/f'Crab{suffix}.java').write_text(fn(c,'crab').replace('tools/models/spawnlings/build_model.py','tools/models/crab/build_model.py'),encoding='utf-8')
    names=list(c.clips)
    state='package com.aquamarine.client.entity.crab;\nimport net.minecraft.client.renderer.entity.state.LivingEntityRenderState;\nimport net.minecraft.world.entity.AnimationState;\npublic final class CrabRenderState extends LivingEntityRenderState {\n'
    state+=''.join(f'    public final AnimationState {n} = new AnimationState();\n' for n in names)+'}\n'
    (java/'CrabRenderState.java').write_text(state,encoding='utf-8')
    if '--register' in sys.argv:
        sys.path.insert(0,str(ROOT/'tools/atlas')); from register_version import register
        uri=lambda path:'data:image/png;base64,'+base64.b64encode(path.read_bytes()).decode()
        register(rig|{'id':'crab','version':'v1','texture':uri(sheet/'crab.png'),'uv':uri(BASE/'uv.png')},'Caranguejo · 7H · coral e marfim')
    print(f'Caranguejo: {len(c.cubes())} cubos / {len(c.parts)} ossos / {len(c.clips)} clipes / 64 px')

if __name__=='__main__': main()
