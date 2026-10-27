"""Ostra: duas valvas estriadas, manto rosado e pérola; folha 64px, rig e clipes do jogo/Atlas."""
from pathlib import Path
import sys, math, json, base64
from PIL import Image
BASE=Path(__file__).resolve().parent
ROOT=BASE.parents[2]
sys.path.insert(0,str(BASE.parent/'spawnlings'))
import build_model as k

SHELL=[(39,42,43),(66,66,62),(99,94,80),(139,130,107),(181,172,144),(225,216,185)]
NACRE=[(65,62,78),(100,92,115),(143,132,154),(185,175,190),(220,218,222),(250,248,226)]
FLESH=[(59,37,48),(95,51,63),(136,76,82),(182,111,113),(217,154,145),(242,194,169)]
PEARL=[(65,96,111),(99,142,154),(145,182,185),(189,216,209),(226,240,216),(255,254,232)]

def build():
    c=k.Creature('oyster','Oyster',(64,64),'Ostra de águas mornas: valvas estriadas de pedra e nácar, manto coral e pérola luminosa.',84)
    c.part('body',None,(0,23,0))
    c.part('lid','body',(0,-1,3))
    for part,upper in [('body',False),('lid',True)]:
        z=-3 if not upper else -6
        y=-1.5 if not upper else -.5
        c.cube(part,(-4,y,z),(8,1.5,6),'shell',name=part+'_rim')
        c.cube(part,(-3,y-1 if upper else y+1,z+.5),(6,1,5),'shell',name=part+'_crown')
        c.cube(part,(-3,y-.2 if not upper else y+1.2,z+.5),(6,.5,5),'nacre',name=part+'_lining')
        for i in range(5):
            c.cube(part,(-3.6+i*1.5,y-1.3 if upper else y+1.7,z+1),(0.5,.4,4),'shell',name=f'{part}_rib{i}')
    c.cube('body',(-2.5,-1.9,-2),(5,.6,4),'flesh',name='mantle')
    c.part('pearl','body',(0,-2.4,-.5))
    c.cube('pearl',(-.75,-.75,-.75),(1.5,1.5,1.5),'pearl',name='pearl')
    c.cube('body',(-1,-1,2.5),(2,1.2,1),'shell',name='hinge')
    c.clips['idle']=k.Clip(4,True).sample('lid','rotation',lambda t:(-25+3*math.sin(t*math.tau/4),0,0))
    c.clips['close']=k.Clip(.3).rot('lid',(0,(-25,0,0)),(.12,(-12,0,0)),(.3,(0,0,0)))
    c.clips['open']=k.Clip(1.5).rot('lid',(0,(0,0,0)),(.5,(-5,0,0)),(1.5,(-25,0,0)))
    c.clips['pearl_glow']=k.Clip(3,True).sample('pearl','scale',lambda t:(1+.06*math.sin(t*math.tau/3),)*3)
    return c

def main():
    c=build(); k.pack(c)
    img=Image.new('RGBA',(64,64)); glow=Image.new('RGBA',(64,64))
    palettes={'shell':SHELL,'nacre':NACRE,'flesh':FLESH,'pearl':PEARL}
    for box in c.cubes():
        pal=palettes[box.material]
        for face,(u,v,w,h) in k.faces(box).items():
            for y in range(h):
                for x in range(w):
                    shade=3+(face=='top')-(face=='bottom')
                    if y==0: shade+=1
                    if x==w-1 or y==h-1: shade-=1
                    if box.material=='shell' and (x+2*y)%4==0: shade-=2
                    if box.material=='nacre' and (x+y)%4==0: shade+=1
                    color=(*pal[max(0,min(5,shade))],255)
                    img.putpixel((u+x,v+y),color)
                    if box.material=='pearl': glow.putpixel((u+x,v+y),color)
    folder=ROOT/'src/main/resources/assets/aquamarine/textures/entity/oyster'; folder.mkdir(parents=True,exist_ok=True)
    for suffix,im in [('',img),('_glow',glow)]: im.save(folder/f'oyster{suffix}.png')
    img.save(BASE/'oyster.png'); k.uv_guide(c).save(BASE/'uv.png')
    rig=k.rig_json(c); (BASE/'rig.json').write_text(json.dumps(rig,separators=(',',':')),encoding='utf-8')
    bb=k.bbmodel(c); bb['textures'][0]['source']='data:image/png;base64,'+base64.b64encode((BASE/'oyster.png').read_bytes()).decode()
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
    (BASE/'oyster.bbmodel').write_text(json.dumps(bb,separators=(',',':')),encoding='utf-8')
    java=ROOT/'src/client/java/com/aquamarine/client/entity/oyster'; java.mkdir(parents=True,exist_ok=True)
    for suffix,fn in [('Model',k.model_java),('Animations',k.animations_java)]:
        code=fn(c,'oyster')
        if suffix=='Model':
            code=code.replace('this.resetPose();','''this.resetPose();
        this.root.getChild("body").getChild("lid").xRot = (float)Math.toRadians(-25 + 3*Math.sin(state.ageInTicks*Math.PI/40)) * state.openness;
        this.root.getChild("body").getChild("pearl").visible = state.pearl;''')
        (java/f'Oyster{suffix}.java').write_text(code,encoding='utf-8')
    if '--register' in sys.argv:
        sys.path.insert(0,str(ROOT/'tools/atlas')); from register_version import register
        uri=lambda p:'data:image/png;base64,'+base64.b64encode(p.read_bytes()).decode()
        register(rig|{'id':'oyster','version':'v1','texture':uri(folder/'oyster.png'),'glow':uri(folder/'oyster_glow.png'),'uv':uri(BASE/'uv.png')},'Ostra · nácar e pérola')
    print(f'Ostra: {len(c.cubes())} cubos, 4 clipes, 64px')
if __name__=='__main__': main()
