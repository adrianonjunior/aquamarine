"""Gera build/atlas, portátil e offline. Não modifica nenhum asset ou Java do mod.

python tools/atlas/build.py --audio-root ../aquamarine-claude-audio
Instalação única da dependência de build: npm ci --prefix tools/atlas
"""
from __future__ import annotations
import argparse
import json
import shutil
import subprocess
from datetime import datetime, timezone
from pathlib import Path

from catalog import ROOT, ASSETS, models, image_data

BASE = Path(__file__).resolve().parent
OUT = ROOT / "build/atlas"


def copy_asset(path, relative):
    target = OUT / relative
    target.parent.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(path, target)
    return relative.replace("\\", "/")


def audio_catalog(audio_root=None):
    # A worktree de áudio é opt-in; um snapshot da main funciona sem ela.
    roots = [ROOT] + ([Path(audio_root).resolve()] if audio_root else [])
    entries = {}
    for root in roots:
        path = root / "tools/audio/manifest.json"
        if not path.exists():
            continue
        for name, entry in json.loads(path.read_text(encoding="utf-8")).items():
            e = dict(entry)
            slug = name.replace("/", "_")
            e.setdefault("description",name.rsplit("/",1)[-1].replace("_"," · "))
            if "layers" in e:
                # Música adaptativa: o manifesto registra camadas e mixagens, sem um file único.
                auditions = e.get("audition",{})
                e["mixes"] = []
                for key, relative in auditions.items():
                    mix = root / relative
                    if mix.exists():
                        e["mixes"].append({"id":key,"src":copy_asset(mix,f"audio/{slug}_{key}.ogg")})
                e["available"] = bool(e["mixes"])
                if e["available"]:
                    e["src"] = next((mix["src"] for mix in e["mixes"] if mix["id"]=="full"),e["mixes"][0]["src"])
                e["file"] = " + ".join(e["layers"].values())
                e["audition"] = True
                e["playback_note"] = "Mixagem adaptativa de audição: 2 voltas completas. Compare as combinações abaixo; cada camada também está no catálogo."
                spec = root / "build/audio/spectrograms" / (slug + ".png")
                if spec.exists():
                    e["spectrogram"] = copy_asset(spec,f"audio/{slug}.png")
                entries[name] = e
                continue
            if not e.get("file"):
                e["available"] = False
                e["file"] = name
                entries[name] = e
                continue
            file = root / e["file"]
            if not file.exists():
                e["available"] = False
                entries[name] = e
                continue
            preview = root / "build/audio/audition" / (slug + "_3x.ogg")
            e["audition"] = bool(e.get("loop") and preview.exists())
            e["src"] = copy_asset(preview if e["audition"] else file, f"audio/{slug}.ogg")
            e["download"] = copy_asset(file, f"audio/original/{slug}.ogg")
            e["available"] = True
            if e.get("loop") and not e.get("loop_overlap_s") and not e["audition"]:
                e["playback_note"] = "Camada em laço circular, sem sobra para crossfade. A duração corresponde a uma volta."
            spec = root / "build/audio/spectrograms" / (slug + ".png")
            if spec.exists():
                e["spectrogram"] = copy_asset(spec, f"audio/{slug}.png")
            entries[name] = e
        # Preserva as comparações MusicGen do site anterior, com a licença e a origem.
        alt_path = root / "build/audio/alt/alt.json"
        if alt_path.exists():
            alt = json.loads(alt_path.read_text(encoding="utf-8"))
            picks_path = alt_path.with_name("picks.json")
            picks = json.loads(picks_path.read_text(encoding="utf-8")) if picks_path.exists() else alt
            for name in picks:
                e = dict(alt[name])
                file = alt_path.parent / (name + ".ogg")
                if not file.exists():
                    continue
                e.update(name="comparacao/"+name,kind="comparison",description=name,available=True,
                         listen_for=str(picks[name]) if isinstance(picks[name],str) else "Comparação MusicGen; não integrada ao mod.",
                         src=copy_asset(file,f"audio/alt_{name}.ogg"),file=str(file.relative_to(root)).replace("\\","/"))
                spec = file.with_suffix(".png")
                if spec.exists():
                    e["spectrogram"] = copy_asset(spec,f"audio/alt_{name}.png")
                entries[e["name"]] = e
    return list(entries.values())


def texture_catalog():
    from PIL import Image
    textures = []
    for path in sorted((ASSETS / "textures").rglob("*.png")):
        rel = path.relative_to(ASSETS).as_posix()
        with Image.open(path) as im:
            size = list(im.size)
        textures.append({"path": rel, "name": path.stem, "group":path.relative_to(ASSETS/"textures").parts[0],
                         "size":size,"src":copy_asset(path,rel)})
    return textures


def build(audio_root=None, bundle=True):
    OUT.mkdir(parents=True, exist_ok=True)
    data = {"models":models(),"audio":audio_catalog(audio_root),"textures":texture_catalog(),
            "generated":datetime.now(timezone.utc).isoformat(),"docs":[]}
    registry = json.loads((BASE/"versions/registry.json").read_text(encoding="utf-8"))
    for m in data["models"]:
        m["versions"] = []
        for record in registry:
            if record["id"] == m["id"]:
                version = json.loads((BASE/"versions"/record["file"]).read_text(encoding="utf-8"))
                version.update(label=record["label"],date=record["date"])
                m["versions"].append(version)
    for path in sorted((ROOT/"docs").glob("*.md")):
        text = path.read_text(encoding="utf-8")
        data["docs"].append({"name":text.splitlines()[0].lstrip("# "),"file":path.name,"text":text})
    # Script clássico + data URIs: funciona ao abrir index.html direto, sem CORS nem CDN.
    (OUT/"data.js").write_text("window.ATLAS_DATA="+json.dumps(data,ensure_ascii=False,separators=(",",":")).replace("</","<\\/")+";",encoding="utf-8")
    for name in ("index.html","style.css"):
        shutil.copyfile(BASE/name,OUT/name)
    shutil.copytree(BASE/"fonts",OUT/"fonts",dirs_exist_ok=True)
    if bundle:
        exe = BASE/"node_modules/@esbuild/win32-x64/esbuild.exe"
        command = [str(exe)] if exe.exists() else ["node",str(BASE/"node_modules/esbuild/bin/esbuild")]
        subprocess.run(command+[str(BASE/"app.js"),"--bundle","--format=iife","--minify",f"--outfile={OUT/'app.js'}"],check=True,cwd=BASE)
        copy_asset(BASE/"node_modules/three/LICENSE","licenses/three.txt")
    print(f"Atlas: {len(data['models'])} rigs, {sum(len(m['clips']) for m in data['models'])} clipes, "
          f"{len(data['audio'])} áudios, {len(data['textures'])} texturas. {OUT/'index.html'}")
    return data


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--audio-root",type=Path)
    args = parser.parse_args()
    build(args.audio_root)
