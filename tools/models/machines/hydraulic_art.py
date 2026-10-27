"""Peças de oficina das faixas 7D/7F. Paleta e densidade do Gerador de Fenda aprovado.

Rampas de seis tons: BRASS=261b0d..e2cb85, COPPER=341a10..f2bf8f,
STEEL=101318..8798ad, GLASS=10302f..bfe9e0, LEATHER=1e1009..b07f57.
Nenhum ruído aleatório: chapas, juntas, rebites e desgaste têm localização funcional.
"""
import importlib.util
import math
from pathlib import Path
import machine_kit as k

_spec = importlib.util.spec_from_file_location("vent_art_reference", Path(__file__).parent / "vent_generator/build_model.py")
ref = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(ref)

def steel(c):
    k.plate(c, k.STEEL, 3)
    k.rivets(c, k.STEEL, 8, 1)
    k.corner_bolts(c, k.BRASS, 1)
    if c.w > 12:
        k.seam_v(c, c.w // 2, k.STEEL)
    k.scratches(c, k.STEEL, 3)

def brass(c):
    k.cylinder(c, k.BRASS, 3)
    k.rivets(c, k.BRASS, 8, 0)
    k.patina(c, ((1, c.h - 2), (c.w - 2, 1)), 1)

def copper(c):
    k.cylinder(c, k.COPPER, 3)
    k.rivets(c, k.BRASS, 8, 0)
    k.patina(c, ((1, c.h - 2),), 1)

def glass(c):
    if c.face not in ("front", "back"):
        c.fill(k.GLASS[4][:3] + (80,))
        return
    c.fill(k.GLASS[2][:3] + (28,))
    for y in range(c.h):
        for x in range(c.w):
            if x in (0, c.w-1) or y in (0, c.h-1):
                c.put(x, y, k.GLASS[3][:3] + (110,))
            elif (x + y) % 21 in (3, 4):
                c.put(x, y, k.GLASS[5][:3] + (105 if (x+y)%21 == 3 else 55,))
    for x, y in ((2, 3), (c.w-3, 6), (3, c.h-4)):
        c.put(x, y, k.STEAM[4][:3] + (110,))

def gasket(c):
    k.plate(c, k.LEATHER, 2)
    for x in range(1, c.w, 3):
        c.put(x, 1, k.LEATHER[4])

def grille(c):
    steel(c)
    if c.face == "front":
        for x in range(2, c.w-1, 3):
            for y in range(2, c.h-2):
                c.put(x, y, k.STEEL[0]); c.put(x+1, y, k.STEEL[4])

def scale_plate(c):
    k.plate(c, k.IVORY, 4)
    if c.face == "front":
        for y in range(2, c.h-1, 4):
            for x in range(1, 4 if y % 8 == 2 else 3):
                c.put(x, y, k.STEEL[1])
        k.text(c, 2, 2, "64", k.STEEL[1])

def lamp(c):
    k.plate(c, k.CYAN, 3)
    for y in range(1, c.h-1):
        for x in range(1, c.w-1):
            c.lit(x, y, k.CYAN[4])

def base(m):
    m.bone("root", None, (8, 0, 8))
    m.bone("works", "root", (8, 3, 8))
    m.box("deck", "root", (1, 1, 1), (14, 1, 14), steel,
          same={"back":"front", "right":"left", "bottom":"top"})
    for i, (x,z) in enumerate(((1,1),(12,1),(1,12),(12,12))):
        m.box(f"foot_{i}", "root", (x,0,z), (3,1,3), gasket,
              same={"back":"front","left":"front","right":"front","bottom":"top"},
              uv_from="foot_0" if i else None)

def port(m, name, at, kind="fluid", rot=(0,0,0)):
    """Porta de 3x3 no plano XY; centro absoluto e rotação apontam para a rede."""
    x,y,z=at
    m.bone(name,"works",at,rot)
    colors = {"fluid":k.RING_FLUID,"energy":k.RING_ENERGY,"gas":k.RING_GAS}
    m.box(name+"_ring",name,(x-1.5,y-1.5,z-0.5),(3,3,1),ref.ring_face(colors[kind], k.STEEL[0]),
          same={"back":"front","right":"left"})
    m.box(name+"_neck",name,(x-1,y-1,z+0.5),(2,2,1),brass,
          same={"back":"front","right":"left","bottom":"top"})

def gauge(m, at, name="needle"):
    x,y,z=at
    m.box(name+"_dial","works",(x-2,y-2,z),(4,4,0.5),ref.p_dial,
          same={"back":"front","right":"left"})
    m.bone(name,"works",(x,y,z-0.125))
    m.box(name+"_pointer",name,(x-0.25,y-0.25,z-0.5),(0.5,1.5,0.5),ref.p_pivot,
          same={"back":"front","right":"left","bottom":"top"})

def wheel(m, at, radius=3, name="flywheel"):
    x,y,z=at
    m.bone(name,"works",at)
    for i in range(8):
        bone=f"{name}_rim_{i}"
        m.bone(bone,name,at,(0,0,i*45))
        m.box(bone,bone,(x-1.25,y+radius-0.5,z-0.5),(2.5,1,1),copper,
              same={"back":"front","right":"left","bottom":"top"},uv_from=f"{name}_rim_0" if i else None)
    for i in range(4):
        bone=f"{name}_spoke_{i}"
        m.bone(bone,name,at,(0,0,i*45))
        m.box(bone,bone,(x-0.25,y-radius+0.5,z-0.25),(0.5,2*radius-1,0.5),brass,
              same={"back":"front","left":"front","right":"front","bottom":"top"},uv_from=f"{name}_spoke_0" if i else None)
    m.box(name+"_hub",name,(x-0.5,y-0.5,z-1),(1,1,2),ref.p_head,
          same={"back":"front","right":"left","bottom":"top"})

def sine(clip,bone,channel,amplitude,axis=1,phase=0,base=0,steps=12):
    points=[]
    for i in range(steps+1):
        v=[0,0,0] if channel!="scale" else [1,1,1]
        v[axis]=base+amplitude*math.sin(2*math.pi*i/steps+phase)
        points.append((clip.length*i/steps,tuple(v),k.L))
    getattr(clip,{"rotation":"rot","position":"pos","scale":"scale"}[channel])(bone,*points)

def motion(m, piston="piston", cycle=1.0, wheel_name="flywheel"):
    m.clips["idle"]=k.Clip(3,True).finish()
    run=k.Clip(cycle,True)
    sine(run,piston,"position",0.75)
    run.rot(wheel_name,(0,(0,0,0),k.L),(cycle,(0,0,360),k.L))
    sine(run,"needle","rotation",3,2,0,105)
    m.clips["run"]=run.finish()
    start=k.Clip(1.6)
    start.rot(wheel_name,(0,(0,0,0)),(.5,(0,0,15)),(1,(0,0,130)),(1.6,(0,0,360)))
    start.pos(piston,(0,(0,0,0)),(.5,(0,0,0)),(1,(0,-.5,0)),(1.35,(0,.4,0)),(1.6,(0,0,0)))
    start.rot("needle",(0,(0,0,0)),(.8,(0,0,20)),(1.3,(0,0,112)),(1.6,(0,0,105)))
    m.clips["start"]=start.finish()
    stop=k.Clip(1)
    stop.rot(wheel_name,(0,(0,0,0)),(.25,(0,0,75)),(.6,(0,0,140)),(1,(0,0,180)))
    stop.pos(piston,(0,(0,0,0)),(.3,(0,.3,0)),(.7,(0,-.1,0)),(1,(0,0,0)))
    stop.rot("needle",(0,(0,0,105)),(.4,(0,0,65)),(.85,(0,0,-4)),(1,(0,0,0)))
    m.clips["stop"]=stop.finish()
    full=k.Clip(3,True)
    sine(full,"needle","rotation",1,2,0,170)
    m.clips["full"]=full.finish()
    m.states={"idle":{"clip":"idle"},"run":{"clip":"run"},"full":{"clip":"full"}}

def shots(m):
    return [("repouso",[(m.clips["idle"],0,1)],math.radians(135),math.radians(24)),
            ("trabalhando",[(m.clips["run"],.23,1)],math.radians(155),math.radians(15)),
            ("trás",[(m.clips["run"],.5,1)],math.radians(-40),math.radians(28)),
            ("perfil",[(m.clips["idle"],0,1)],math.radians(90),math.radians(12))]
