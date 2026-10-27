#!/usr/bin/env python3
"""Garrafão de Cultivo: vidro espesso, gaiola de latão, nó de corda e uma ostra viva. 7G."""
import math
import sys
from pathlib import Path
BASE = Path(__file__).resolve().parent
sys.path.insert(0, str(BASE.parent))
import machine_kit as k
from machine_kit import Clip, BRASS, GLASS, LEATHER, IVORY, STEEL, CYAN

# Rampas orgânicas: concha cinza-violeta com borda marfim e manto rosado; sem ruído aleatório.
SHELL = k.ramp('282532', '48424c', '716875', '9c919d', 'c2b7bd', 'e3d9d7')
MANTLE = k.ramp('583d4a', '805365', 'aa7585', 'ce9eaa', 'eac5ca', 'f7e1dc')
PEARL = k.ramp('445768', '69818d', 'a0bcc0', 'cadbd4', 'f2ede0', 'ffffff')

def brass(c):
    k.plate(c, BRASS, 3)
    k.rivets(c, BRASS, 8, 0)
    k.patina(c, ((1, c.h-2),), 1)
    if c.w > 8 and c.h > 4: k.scratches(c, BRASS, 2)

def cord(c):
    k.cylinder(c, LEATHER, 3)
    for y in range(c.h):
        for x in range(c.w):
            if (x+y) % 5 == 0: c.put(x,y,LEATHER[1])
            elif (x+y) % 5 == 1: c.put(x,y,LEATHER[5])

def glass(c):
    c.fill((*GLASS[2][:3], 35))
    for y in range(c.h):
        for x in range(c.w):
            if x == 0 or y == 0: c.put(x,y,(*GLASS[5][:3],155))
            elif x == c.w-1 or y == c.h-1: c.put(x,y,(*GLASS[0][:3],140))
            elif (x+y) in (5,6,7,18): c.put(x,y,(*GLASS[5][:3],70))
    if c.h > 10:
        for y in range(5,c.h-2,4): c.put(c.w-2,y,(*IVORY[4][:3],180))

def shell(c):
    k.plate(c, SHELL, 3)
    for y in range(c.h):
        for x in range(c.w):
            rib = (x + (y//3)) % 4
            if rib == 0: c.put(x,y,SHELL[1])
            elif rib == 1: c.put(x,y,SHELL[4])
    for x in range(c.w): c.put(x,c.h-1,IVORY[3 if x%3 else 5])

def mantle(c):
    k.plate(c, MANTLE, 3)
    for x in range(0,c.w,3): c.put(x,0,MANTLE[5])

def pearl(c):
    k.plate(c, PEARL, 3)
    c.put(0,0,PEARL[5]); c.lit(0,0,PEARL[5])
    if c.w > 1: c.lit(1,0,PEARL[4])

def label(c):
    k.plate(c, IVORY, 4)
    k.corner_bolts(c, BRASS, 0)
    if c.face == 'front':
        k.text(c, 3, 2, '01', STEEL[0])

def build():
    m=k.Machine('cultivation_jar','CultivationJar','Garrafão de Cultivo',
        'Vidro verde-água em gaiola de latão, corda trançada, ostra articulada e pérola pronta com brilho próprio.')
    m.bone('frame',None,(8,0,8))
    # Base chanfrada, cinco cintas e quatro tirantes com parafusos.
    m.box('foot','frame',(3,0,3),(10,1,10),brass,hide=('top',))
    m.box('bottom','frame',(2.5,1,2.5),(11,1,11),brass,hide=('bottom','top'))
    for y in (2,10):
        for i,(at,size) in enumerate([((2,y,2),(12,1,1)),((2,y,13),(12,1,1)),((2,y,3),(1,1,10)),((13,y,3),(1,1,10))]):
            m.box(f'band_{y}_{i}','frame',at,size,brass)
    for x in (2.5,12.5):
        for z in (2.5,12.5):
            m.box(f'brace_{x}_{z}','frame',(x,3,z),(1,7,1),brass)
    # Thin panes leave the living oyster readable through the glass pass.
    for name,at,size in [('front',(3,3,2.5),(10,7,.5)),('back',(3,3,13),(10,7,.5)),
        ('left',(2.5,3,3),(.5,7,10)),('right',(13,3,3),(.5,7,10)),('shoulder',(4,10.5,4),(8,1,8))]:
        m.box('glass_'+name,'frame',at,size,glass,glass=True)
    m.box('neck','frame',(5,11.5,5),(6,2,6),glass,glass=True)
    m.box('neck_band','frame',(4.5,13,4.5),(7,1,7),brass)
    m.box('stopper','frame',(5,14,5),(6,1,6),cord)
    m.box('cap','frame',(4.5,15,4.5),(7,.5,7),brass)
    m.box('handle_l','frame',(6,15.5,7),(1,.5,2),brass)
    m.box('handle_r','frame',(9,15.5,7),(1,.5,2),brass)
    m.box('handle','frame',(7,15.5,7),(2,.5,2),brass)
    m.box('rope_left','frame',(0,11,7),(5,1,2),cord)
    m.box('rope_right','frame',(11,11,7),(5,1,2),cord)
    m.box('knot','frame',(3.5,10.5,6.5),(2,2,3),cord)
    m.box('label','frame',(5,1.2,1.5),(6,2,.5),label)
    # The oyster sits in a copper cradle, with upper valve pivot at the rear hinge.
    m.bone('oyster', 'frame', (8,2,8))
    m.box('lower_rim','oyster',(4.5,2,4.5),(7,1,7),shell)
    m.box('lower_cup','oyster',(5,3,5),(6,1,6),mantle)
    m.bone('lid','oyster',(8,4,10.5))
    m.box('upper_shell','lid',(4.5,4,4.5),(7,1,6),shell)
    m.box('upper_crown','lid',(5.5,5,5.5),(5,1,4),shell)
    m.bone('pearl','oyster',(8,4.5,7))
    m.box('pearl','pearl',(7,4,6),(2,2,2),pearl)
    for i,(x,z) in enumerate(((5,7),(10,9),(7,10))):
        m.bone(f'bubble_{i}','frame',(x,7,z))
        m.box(f'bubble_{i}',f'bubble_{i}',(x,7,z),(.5,.5,.5),glass,glass=True)
    idle=Clip(4,True).rot('lid',(0,(-8,0,0)),(2,(-11,0,0)),(4,(-8,0,0)))
    idle.scale('pearl',(0,.1),(4,.1))
    for i in range(3): idle.scale(f'bubble_{i}',(0,0),(4,0))
    m.clips['idle']=idle.finish()
    run=Clip(4,True).rot('lid',(0,(-20,0,0)),(2,(-25,0,0)),(4,(-20,0,0)))
    run.scale('pearl',(0,.15),(2,.2),(4,.15))
    for i in range(3):
        run.pos(f'bubble_{i}',(0,(0,0,0)),(1+i*.3,(0,2,0)),(2.5+i*.3,(0,4,0)),(4,(0,0,0)))
        run.scale(f'bubble_{i}',(0,0),(1,1),(2.7,1),(3.1,0),(4,0))
    m.clips['run']=run.finish()
    m.clips['start']=Clip(1.5).rot('lid',(0,(-8,0,0)),(.6,(-12,0,0)),(1.5,(-20,0,0))).scale('pearl',(0,.1),(1.5,.15)).finish()
    m.clips['stop']=Clip(1).rot('lid',(0,(-20,0,0)),(.7,(-7,0,0)),(1,(-8,0,0))).scale('pearl',(0,.15),(1,.1)).finish()
    full=Clip(4,True).rot('lid',(0,(-30,0,0)),(2,(-33,0,0)),(4,(-30,0,0)))
    full.scale('pearl',(0,1),(2,1.08),(4,1)).rot('pearl',(0,(0,0,0)),(2,(0,15,0)),(4,(0,0,0)))
    for i in range(3): full.scale(f'bubble_{i}',(0,0),(4,0))
    m.clips['full']=full.finish()
    m.clips['ready']=full.finish()
    m.clips['harvest']=Clip(1).scale('pearl',(0,1),(.4,1.15),(.65,.1),(1,.1)).rot('lid',(0,(-30,0,0)),(1,(-20,0,0))).finish()
    m.states={'idle':{'clip':'idle'},'run':{'clip':'run'},'full':{'clip':'full'}}
    return m

if __name__=='__main__':
    m=build()
    shots=[(name,[(m.clips[clip],t,1)],math.radians(yaw),math.radians(pitch)) for name,clip,t,yaw,pitch in
        [('idle','idle',0,135,20),('run','run',1.4,150,15),('ready','full',1,135,24),('back','run',1,315,24)]]
    k.write(m,BASE,preview_shots=shots if '--preview' in sys.argv else None,
        register=('v1','No jogo · Garrafão de Cultivo') if '--register' in sys.argv else None)
