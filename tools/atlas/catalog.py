"""Adaptadores explícitos dos rigs do mod; coordenadas Java (Y para baixo), rotações em graus.

Não usa as animações simplificadas dos .bbmodel antigos: lê os keyframes Java efetivamente usados.
As caixas grandes vêm dos geradores, as do Kraken do .bbmodel com a pose radial do Java.
"""
from __future__ import annotations
import base64
import copy
import importlib.util
import json
import math
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
MODELS = ROOT / "tools/models"
CLIENT = ROOT / "src/client/java/com/aquamarine/client/entity"
ASSETS = ROOT / "src/main/resources/assets/aquamarine"
NAMES = {"kraken": "Kraken", "tentacle": "Tentáculo", "young_kraken": "Cria de Kraken",
         "kraken_egg": "Ovo de Kraken", "moray": "Moréia Tempestuosa", "whale": "Baleia-de-Nereia",
         "sperm_whale": "Cachalote"}
# Tentáculo, Cria e Ovo saem de tools/models/spawnlings/build_model.py, que grava o Java do jogo e um rig.json
# com as mesmas caixas, UV e clipes: o site lê esse arquivo em vez de copiar o modelo à mão.
SPAWNLINGS = ("tentacle", "young_kraken", "kraken_egg")
CLASSES = {"kraken": "Kraken", "tentacle": "Tentacle", "young_kraken": "YoungKraken",
           "kraken_egg": "KrakenEgg", "moray": "Moray", "whale": "Whale", "sperm_whale": "SpermWhale"}


def module(path):
    name = "atlas_" + path.parent.name + "_" + path.stem
    spec = importlib.util.spec_from_file_location(name, path)
    mod = importlib.util.module_from_spec(spec)
    sys.modules[name] = mod
    spec.loader.exec_module(mod)
    return mod


def image_data(path):
    return "data:image/png;base64," + base64.b64encode(path.read_bytes()).decode()


def bone(name, parent="root", pos=(0, 0, 0), rot=(0, 0, 0)):
    return {"name": name, "parent": parent, "position": list(pos), "rotation": list(rot)}


def cube(name, parent, pos, size, uv, inflate=0):
    return {"name": name, "bone": parent, "position": list(pos), "size": list(size), "uv": list(uv), "inflate": inflate}


def track(b, keys, channel="rotation"):
    return {"bone": b, "channel": channel, "keys": [[t, list(v), "catmullrom"] for t, v in keys]}


def clips_data(clips):
    return {name: {"length": c.length, "loop": c.loop,
                   "tracks": [{"bone": b, "channel": ch, "keys": [[t, list(v), mode] for t, (v, mode) in sorted(keys.items())]}
                              for (b, ch), keys in c.tracks.items()]} for name, c in clips.items()}


def java_animations(kind):
    source = (CLIENT / kind / (CLASSES[kind] + "Animations.java")).read_text(encoding="utf-8")
    result = {}
    num = lambda s: float(s.strip().rstrip("Ff"))
    for name, body in re.findall(r"public static final AnimationDefinition (\w+)\s*=\s*(.*?);", source, re.S):
        name = name.lower()
        simple = re.match(r'clip\(([^,]+),\s*(true|false),\s*([^,]+),\s*([^,]+),(.*)\)', body, re.S)
        tracks = []
        if simple:
            duration, loop, windup, peak, names = simple.groups()
            duration, windup, peak = map(num, (duration, windup, peak))
            for i, b in enumerate(re.findall(r'"([^"]+)"', names)):
                sign = 1 if i % 2 == 0 else -1
                tracks.append(track(b, [(0, (0, 0, 0)), (duration * .28, (0, 0, windup * sign)),
                                        (duration * .58, (0, 0, peak * sign)), (duration, (0, 0, 0))]))
            loop = loop == "true"
        else:
            length = re.search(r"withLength\(([^)]+)\)", body)
            if not length:
                raise ValueError(f"Formato Java desconhecido: {kind}/{name}")
            duration, loop = num(length[1]), ".looping()" in body
            for match in re.finditer(r'\.addAnimation\("([^"]+)",\s*rot\((.*?)(?=\.addAnimation|\.build|$)', body, re.S):
                keys = [list(map(num, key.split(","))) for key in re.findall(r"\bk\(([^)]+)\)", match[2])]
                tracks.append(track(match[1], [(v[0], v[1:]) for v in keys]))
        if not tracks:
            raise ValueError(f"Sem canais: {kind}/{name}")
        result[name] = {"length": duration, "loop": loop, "tracks": tracks}
    return result


def large_rig(kind):
    m = module(MODELS / kind / "build_model.py")
    uvs = m.pack_uv()
    bones, cubes = [], []
    if kind == "moray":
        pivots = {n: p for n, p, _ in m.BONES} | {"root": (0, 0, 0)}
        for n, p, parent in m.BONES:
            bones.append(bone(n, parent, [p[i] - pivots[parent][i] for i in range(3)]))
        for n, p, size, _, b in m.BOXES:
            cubes.append(cube(n, b, [p[i] - pivots[b][i] for i in range(3)], size, uvs[n]))
    else:
        # BOXES das baleias excluem o offset global de body; os filhos usam pivôs relativos.
        offsets = {"root": (0, 0, 0)}
        for n, p, parent in m.BONES:
            offsets[n] = (0, 0, 0) if parent == "root" else tuple(offsets[parent][i] + p[i] for i in range(3))
            bones.append(bone(n, parent, p))
        for n, p, size, _, b in m.BOXES:
            cubes.append(cube(n, b, [p[i] - offsets[b][i] for i in range(3)], size, uvs[n]))
    return bones, cubes, m.SIZE


def kraken_rig():
    model = json.loads((MODELS / "kraken/kraken.bbmodel").read_text())
    elements = {e["uuid"]: e for e in model["elements"]}
    bones, cubes = [], []

    def walk(node, parent="root", origin=(0, 0, 0)):
        n, p = node["name"], node["origin"]
        if n == "root":
            p = [0, 0, 0]  # raiz virtual do Blockbench não existe no Java
        else:
            rotation = [0, 0, 0]
            if re.fullmatch(r"arm_\d", n):
                arm = int(n[-1])
                rotation = [math.degrees(-.18 if arm % 2 == 0 else -.34), arm * 45, 0]
            elif re.search(r"_segment_[56]$", n) or n.startswith("mantle_fin_"):
                rotation = list(node["rotation"])  # partes da v2: o .bbmodel já traz a pose
            elif "_segment_" in n and not n.endswith("_0"):
                rotation = [math.degrees(-.12), 0, 0]
            bones.append(bone(n, parent, [p[i] - origin[i] for i in range(3)], rotation))
        for child in node["children"]:
            if isinstance(child, dict):
                walk(child, n, p)
            else:
                e = elements[child]
                cubes.append(cube(e["name"], n, [e["from"][i] - p[i] for i in range(3)],
                                  [e["to"][i] - e["from"][i] for i in range(3)],
                                  e["faces"]["north"]["uv"][:2], e.get("inflate", 0)))
    walk(model["outliner"][0])
    return bones, cubes, [128, 256]


def kraken_proposal(bones, cubes, clips):
    """Rig v2 apenas do Atlas: pontas flexíveis, mandíbula e abas laterais do manto.

    UVs reaproveitam as ilhas da textura original. A baseline fica intocada.
    """
    bones, cubes = copy.deepcopy(bones), copy.deepcopy(cubes)
    bones.append(bone("jaw_hinge", "head", (0, 2, 7)))
    for c in cubes:
        if c["name"] in ("jaw", "beak"):
            c["bone"] = "jaw_hinge"
            c["position"] = [c["position"][i] - (0, 2, 7)[i] for i in range(3)]
    for side in (-1, 1):
        n = "mantle_fin_left" if side < 0 else "mantle_fin_right"
        bones.append(bone(n,"mantle",(side*9,-3,-2),(0,0,side*12)))
        cubes.append(cube(n,n,(-5 if side<0 else 0,-1,-7),(5,2,16),(80,0)))
        cubes.append(cube(n+"_edge",n,(-8 if side<0 else 4,0,-4),(4,1.5,10),(80,22)))
    for i in range(8):
        for j, width, length, z in ((5,1.05,5.2,6),(6,.65,4.0,4.5)):
            n = f"arm_{i}_segment_{j}"
            bones.append(bone(n,f"arm_{i}_segment_{j-1}",(0,0,z),(-5,0,0)))
            cubes.append(cube(n,n,(-width/2,-width/2,0),(width,width,length),(72,92)))
            cubes.append(cube(n+"_sucker",n,(-.4,width/2,1.2),(.8,.5,.8),(96,92)))
    for name, clip in clips.items():
        duration = clip["length"]
        for i in range(8):
            previous = next((t for t in clip["tracks"] if t["bone"]==f"arm_{i}_segment_4" and t["channel"]=="rotation"),None)
            for j in (5,6):
                # Mesmo gesto, amplitude menor, continuação da curva; preserva extremos e laços.
                if previous:
                    keys = [[t, [v*.72 for v in values], mode] for t, values, mode in previous["keys"]]
                else:
                    keys = [[duration*k/16,[4*math.sin(2*math.pi*k/16-i*.7-j*.5),0,0],"catmullrom"] for k in range(17)]
                clip["tracks"].append({"bone":f"arm_{i}_segment_{j}","channel":"rotation","keys":keys})
        w = {"roar":1.5,"ink":2,"waterJet":1.2,"spawnYoung":2,"phaseChange":1.1}.get(name)
        if w:
            keys = [(0,(0,0,0)),(w*.65,(-7,0,0)),(w+.08,(-28 if name=="roar" else -18,0,0)),(duration,(0,0,0))]
        else:
            keys = [(0,(0,0,0)),(duration*.5,(-3,0,0)),(duration,(0,0,0))]
        clip["tracks"].append(track("jaw_hinge",keys))
        for side in (-1,1):
            n = "mantle_fin_left" if side < 0 else "mantle_fin_right"
            clip["tracks"].append(track(n,[(0,(0,0,0)),(duration*.3,(0,0,side*8)),
                                          (duration*.65,(0,0,-side*6)),(duration,(0,0,0))]))
    return {"bones":bones,"cubes":cubes}


def models():
    sys.path.insert(0, str(MODELS / "kraken"))
    anim = module(MODELS / "kraken/kraken_preview_v2.py")
    production = module(MODELS / "kraken/kraken_animations.py")
    result = []
    for kind, name in NAMES.items():
        rig = json.loads((MODELS/"spawnlings"/kind/"rig.json").read_text(encoding="utf-8")) if kind in SPAWNLINGS else None
        bones, cubes, size = (rig["bones"], rig["cubes"], rig["resolution"]) if rig else             kraken_rig() if kind == "kraken" else large_rig(kind)
        folder = MODELS / kind if kind in ("kraken","moray","whale","sperm_whale") else MODELS / "spawnlings" / kind
        # A v2 entrou no jogo em 25/10/2026: com o rig de produção já articulado, ele e os clipes de produção
        # são o Kraken atual, e a proposta separada deixa de existir (a v1 segue no histórico de versões).
        integrated = kind == "kraken" and any(b["name"] == "jaw_hinge" for b in bones)
        clips = (clips_data(production.build_all()) if integrated else clips_data(anim.build_all())) if kind == "kraken" \
            else rig["clips"] if rig else java_animations(kind)
        proposal = kraken_proposal(bones,cubes,clips) if kind=="kraken" and not integrated else None
        ids = {b["name"] for b in (proposal["bones"] if proposal else bones)} | {"root"}
        for clip in clips.values():
            assert all(t["bone"] in ids for t in clip["tracks"]), kind
        result.append({"id":kind,"name":name,"bones":bones,"cubes":cubes,"translucent":kind == "kraken_egg",
                       "proposal":proposal,"resolution":[size,size] if isinstance(size,int) else size,
                       "texture":image_data(ASSETS/f"textures/entity/{kind}/{kind}.png"),
                       "uvTexture":image_data(folder/"uv.png"),"clips":clips,
                       "source":str((CLIENT/kind/(CLASSES[kind]+"Animations.java")).relative_to(ROOT)).replace("\\","/"),
                       "warnings":anim.WARN if kind == "kraken" else {},
                       "baseline":json.loads((MODELS/"kraken/reference_v1.json").read_text()) if kind == "kraken" else None})
    return result
