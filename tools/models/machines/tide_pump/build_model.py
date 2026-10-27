#!/usr/bin/env python3
"""Bomba de Maré: pistão exposto, volante de cobre e sucção gradeada. 7D."""
import sys
from pathlib import Path
BASE=Path(__file__).resolve().parent
sys.path.insert(0,str(BASE.parent))
import machine_kit as k
import hydraulic_art as h

def build():
    m=k.Machine("tide_pump","TidePump","Bomba de Maré",
        "Bomba de pistão com volante exposto de cobre, corpo de aço, sucção gradeada e mostrador de vazão. Latão, vidro e vapor; 32 px por face.")
    h.base(m)
    # A bomba é alta à esquerda; à direita, volante com raio real e contrapeso.
    m.box("pump_shoe","works",(2,2,6),(5,1,5),h.brass,same={"back":"front","left":"front","right":"front","bottom":"top"})
    m.box("barrel","works",(2.5,3,6.5),(4,5,4),h.ref.p_housing,
          same={"back":"front","left":"front","right":"front"})
    for i,y in enumerate((3,7.5)):
        m.box(f"barrel_band_{i}","works",(2, y, 6),(5,.5,5),h.brass,
              same={"back":"front","left":"front","right":"front","bottom":"top"},uv_from="barrel_band_0" if i else None)
    m.bone("piston","works",(4.5,8,8.5))
    m.box("piston_rod","piston",(4,7.5,8),(1,5,1),h.ref.p_rod,
          same={"back":"front","left":"front","right":"front","bottom":"top"})
    m.box("piston_head","piston",(3,12,7),(3,1,3),h.ref.p_head,
          same={"back":"front","left":"front","right":"front","bottom":"top"})
    for i,x in enumerate((1.5,7)):
        m.box(f"guide_{i}","works",(x,3,8),(0.5,11,1),h.brass,
              same={"back":"front","right":"left","bottom":"top"},uv_from="guide_0" if i else None)
    m.box("crosshead","works",(1,14,7.5),(7,1,2),h.steel,same={"back":"front","right":"left","bottom":"top"})
    m.box("bearing","works",(9,2,7.5),(3,5,3),h.steel,same={"back":"front","right":"left"})
    m.box("axle","works",(10,6,3),(1,1,6),h.ref.p_rod,same={"back":"front","right":"left","bottom":"top"})
    h.wheel(m,(10.5,6.5,4),3.5)
    m.box("counterweight","flywheel",(11.5,5,3.25),(1.5,1.5,.5),h.brass,same={"back":"front","right":"left","bottom":"top"})
    m.bone("rod_link","works",(10.5,6.5,6))
    m.box("connecting_rod","rod_link",(4.5,6,5.75),(6,1,.5),h.brass,same={"back":"front","right":"left","bottom":"top"})
    m.box("suction_elbow","works",(2,2,2),(4,3,3),h.copper,same={"back":"front","right":"left"})
    m.box("intake_grate","works",(1.5,2,1),(5,3,1),h.grille,same={"back":"front","right":"left","bottom":"top"})
    m.box("suction_riser","works",(3,4,3),(2,2,4),h.copper,same={"back":"front","right":"left","bottom":"top"})
    m.box("delivery_riser","works",(4,6,11),(2,2,4),h.copper,same={"back":"front","right":"left","bottom":"top"})
    h.port(m,"water_out",(5,7,15.5),"fluid",(0,180,0))
    h.port(m,"energy_in",(14.5,3.5,10),"energy",(0,90,0))
    h.gauge(m,(10.5,11,10))
    m.box("dial_stem","works",(10,7,11),(1,3,1),h.copper,same={"back":"front","left":"front","right":"front","bottom":"top"})
    m.box("status_lamp","works",(7.5,2,1),(2,1,.5),h.lamp,same={"back":"front","right":"left","bottom":"top"})
    h.motion(m)
    for name in ("run","start","stop"):
        c=m.clips[name]
        h.sine(c,"rod_link","rotation",9,2)
    m.descriptions={"idle":"Bomba parada; pistão em repouso.","start":"Volante ganha giro e mostrador sobe em 1,6 s.",
        "run":"Um curso por segundo; volante gira e biela acompanha o pistão.","stop":"Freio gradual, última oscilação da haste.","full":"Saída cheia, giro interrompido e pressão conservada."}
    return m

if __name__=="__main__":
    m=build()
    k.write(m,BASE,preview_shots=h.shots(m) if "--preview" in sys.argv else None,
            register=("v2","No jogo · Bomba de Maré · repouso e funcionamento") if "--register" in sys.argv else None)
