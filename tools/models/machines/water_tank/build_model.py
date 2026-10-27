#!/usr/bin/env python3
"""Tanque d'Água: vidro octogonal, anéis empilháveis e escada. 7D.

Líquido é dinâmico no renderer. Volume interno: y=2..14, centro x/z=8;
seção octogonal união de x=4..12,z=2..14 e x=2..14,z=4..12.
Não contém cubos de água/lava falsos. Cada vidro fica em chamber_glass.
"""
import sys
from pathlib import Path
BASE=Path(__file__).resolve().parent
sys.path.insert(0,str(BASE.parent))
import machine_kit as k
import hydraulic_art as h

def build():
    m=k.Machine("water_tank","WaterTank","Tanque d'Água",
        "Tanque de vidro octogonal empilhável com aro de latão, escada, escala de 64 baldes e tampa selada. O líquido vem do conteúdo real do bloco.")
    m.bone("root",None,(8,0,8))
    m.bone("works","root",(8,0,8))
    m.bone("chamber","works",(8,8,8))
    # Base/tampa em três faixas contíguas: sem planos coplanares sobrepostos.
    for tier,y in enumerate((0,15)):
        for i,(x,z,w,d) in enumerate(((4,1,8,2),(1,3,14,10),(4,13,8,2))):
            m.box(f"rim_{tier}_{i}","works",(x,y,z),(w,1,d),h.brass,
                  same={"back":"front","right":"left","bottom":"top"},uv_from=f"rim_0_{i}" if tier else None)
    # Quatro faces retas e quatro cantos de 45 graus formam o casco de vidro.
    for i,(x,z,angle) in enumerate(((8,1.75,0),(14.25,8,90),(8,14.25,180),(1.75,8,270))):
        bone=f"panel_{i}"
        m.bone(bone,"chamber",(x,8,z),(0,angle,0))
        m.box(bone,bone,(x-4,1,z-.25),(8,14,.5),h.glass,glass=True,
              hide=("top","bottom","left","right"),same={"back":"front"},uv_from="panel_0" if i else None)
    for i,(x,z,angle) in enumerate(((12.5,3.5,-45),(12.5,12.5,-135),(3.5,12.5,-225),(3.5,3.5,-315))):
        bone=f"corner_{i}"
        m.bone(bone,"chamber",(x,8,z),(0,angle,0))
        m.box(bone,bone,(x-1.75,1,z-.25),(3.5,14,.5),h.glass,glass=True,
              hide=("top","bottom","left","right"),same={"back":"front"},uv_from="corner_0" if i else None)
    for i,(x,z) in enumerate(((3.5,1.5),(12,1.5),(3.5,14),(12,14))):
        m.box(f"brace_{i}","works",(x,1,z),(.5,14,.5),h.brass,
              same={"back":"front","left":"front","right":"front","bottom":"top"},uv_from="brace_0" if i else None)
    # Escada no lado direito, dentro da caixa de um bloco.
    for i,z in enumerate((5,10)):
        m.box(f"ladder_rail_{i}","works",(15,1,z),(.5,14,.5),h.brass,
              same={"back":"front","left":"front","right":"front","bottom":"top"},uv_from="brace_0")
    for i,y in enumerate((2,5,8,11,14)):
        m.box(f"rung_{i}","works",(15,y,5),(.5,.5,5.5),h.copper,
              same={"back":"front","right":"left","bottom":"top"},uv_from="rung_0" if i else None)
    m.box("capacity_scale","works",(2.5,2,1),(1.5,12,.5),h.scale_plate,same={"back":"front","right":"left"})
    h.port(m,"water_port",(8,3,0.5))
    h.port(m,"water_back",(8,3,15.5),rot=(0,180,0))
    h.gauge(m,(8,12,1))
    m.bone("valve","works",(8,15.5,8))
    m.box("lid_handle_l","works",(6,15,7),(.5,1,2),h.brass,same={"back":"front","right":"left","bottom":"top"})
    m.box("lid_handle_r","works",(9.5,15,7),(.5,1,2),h.brass,same={"back":"front","right":"left","bottom":"top"},uv_from="lid_handle_l")
    m.box("lid_handle","valve",(6,15.5,7),(4,.5,.5),h.brass,same={"back":"front","right":"left","bottom":"top"})
    m.box("level_lamp","works",(11,13,1),(1,1,.5),h.lamp,same={"back":"front","right":"left","bottom":"top"})
    idle=k.Clip(4,True); h.sine(idle,"needle","rotation",.5,2)
    m.clips["idle"]=idle.finish()
    run=k.Clip(3,True); h.sine(run,"needle","rotation",2,2,0,80)
    m.clips["run"]=run.finish()
    m.clips["start"]=k.Clip(1.5).rot("needle",(0,(0,0,0)),(.8,(0,0,88)),(1.5,(0,0,80))).finish()
    m.clips["stop"]=k.Clip(1).rot("needle",(0,(0,0,80)),(.7,(0,0,-3)),(1,(0,0,0))).finish()
    m.clips["full"]=k.Clip(3,True).rot("needle",(0,(0,0,165)),(3,(0,0,165))).finish()
    m.clips["fill"]=m.clips["run"]
    m.clips["empty"]=m.clips["stop"]
    m.states={"idle":{"clip":"idle"},"run":{"clip":"run"},"full":{"clip":"full"}}
    m.descriptions={"idle":"Tanque selado; nível real desenhado pelo renderer.","run":"Agulha acompanha o fluxo de entrada.",
        "fill":"Fluxo suave; o líquido real sobe.","empty":"Fluxo encerrado; o líquido real desce.","full":"Indicador no limite de capacidade."}
    return m

if __name__=="__main__":
    m=build()
    k.write(m,BASE,preview_shots=h.shots(m) if "--preview" in sys.argv else None,
            register=("v1","Tanque d'Água · v1, vidro e latão") if "--register" in sys.argv else None)
