"""Prévia local dos sons (docs/09-audio.md): build/audio-preview/index.html com players, medidas,
espectrograma e o que ouvir em cada som. Abre direto no navegador (arquivo local, sem servidor).

Uso: tools/audio/.venv/Scripts/python tools/audio/preview.py [--open]
"""
import html
import os
import shutil
import sys

from master import BUILD, REPO, load_manifest

OUT = os.path.join(REPO, "build", "audio-preview")

KIND = {"ambience": "Ambiência", "sfx": "Efeito", "music": "Música"}
TARGET = {"ambience": "−26 a −24 LUFS", "sfx": "pico −3 dBFS", "music": "−18 LUFS"}


def card(entry):
    name = entry["name"]
    audio = "files/" + name.replace("/", "_") + ".ogg"
    spec = "files/" + name.replace("/", "_") + ".png"
    shutil.copyfile(os.path.join(REPO, entry["file"]), os.path.join(OUT, audio))
    if entry.get("loop"):
        # Laço: o player toca 3 voltas cruzadas como o tocador do jogo fará (o .ogg do mod tem a sobra).
        audio = "files/" + name.replace("/", "_") + "_3x.ogg"
        shutil.copyfile(os.path.join(BUILD, "audition", name.replace("/", "_") + "_3x.ogg"), os.path.join(OUT, audio))
    png = os.path.join(BUILD, "spectrograms", name.replace("/", "_") + ".png")
    if os.path.exists(png):
        shutil.copyfile(png, os.path.join(OUT, spec))
    seam = ""
    if entry.get("loop"):
        seam = (f"<li>Emenda do laço (como no jogo, cruzando {entry['loop_overlap_s']} s): salto {entry['seam_jump']} · "
                f"espectral {entry['seam_spectral']} (≈1 = inaudível). O player toca 3 voltas.</li>")
    peak_ok = "ok" if entry["true_peak"] <= -1.0 else "ALTO"
    return f"""
<article>
  <header><span class="kind">{KIND[entry['kind']]}</span><h2>{html.escape(entry['description'])}</h2>
  <code>{html.escape(entry['file'])}</code></header>
  <audio controls preload="none" src="{audio}"></audio>
  <p class="listen"><b>Ouça:</b> {html.escape(entry['listen_for'])}</p>
  <ul class="stats">
    <li>{entry['seconds']} s · {'mono' if entry['channels'] == 1 else 'estéreo'} · {entry['bytes'] // 1024} KB</li>
    <li>Loudness {entry['lufs']} LUFS (alvo {TARGET[entry['kind']]}) · LRA {entry['lra']} LU</li>
    <li>Pico real {entry['true_peak']} dBTP ({peak_ok}) · amostras saturadas: {entry['clipping_samples']}</li>
    <li>Silêncio longo: {entry['silence_s']} s</li>{seam}
    <li>Origem: {html.escape(entry['license'])} · {html.escape(entry['script'])}, semente {entry['seed']}</li>
  </ul>
  <img src="{spec}" alt="Espectrograma de {html.escape(name)}" loading="lazy">
</article>"""


def alt_cards():
    """Seção à parte: peças de IA local (MusicGen) só para comparar com a síntese."""
    path = os.path.join(BUILD, "alt", "alt.json")
    if not os.path.exists(path):
        return ""
    import json
    with open(path, encoding="utf-8") as f:
        alt = json.load(f)
    picks_path = os.path.join(BUILD, "alt", "picks.json")
    picks = json.load(open(picks_path, encoding="utf-8")) if os.path.exists(picks_path) else {k: "" for k in alt}
    items = []
    for name, why in picks.items():
        e = alt[name]
        shutil.copyfile(os.path.join(BUILD, "alt", name + ".ogg"), os.path.join(OUT, "files", name + ".ogg"))
        shutil.copyfile(os.path.join(BUILD, "alt", name + ".png"), os.path.join(OUT, "files", name + ".png"))
        items.append(f"""
<article class="alt">
  <header><span class="kind">Comparação: IA local</span><h2>{html.escape(name)}{(" · " + html.escape(why)) if why else ""}</h2>
  <code>{html.escape(e['prompt'])}</code></header>
  <audio controls preload="none" src="files/{name}.ogg"></audio>
  <ul class="stats"><li>{e['seconds']} s · {e['lufs']} LUFS · pico {e['true_peak']} dBTP · {e['bytes'] // 1024} KB</li>
  <li>{html.escape(e['model'])}, semente {e['seed']} · licença dos pesos: {html.escape(e['license'])}</li></ul>
  <img src="files/{name}.png" alt="Espectrograma de {html.escape(name)}" loading="lazy">
</article>""")
    return ("<h2 class=\"section\">Comparação: música por IA local (não entra no mod sem o seu aval)</h2>"
            "<p class=\"intro\">Gerado na sua GPU com o MusicGen pequeno, sem internet depois do download do modelo. "
            "Serve para você decidir se a música segue por síntese, por IA local pós-produzida ou misturando as duas.</p>"
            + "".join(items))


def build():
    os.makedirs(os.path.join(OUT, "files"), exist_ok=True)
    manifest = load_manifest()
    order = {"ambience": 0, "sfx": 1, "music": 2}
    entries = sorted(manifest.values(), key=lambda e: (order[e["kind"]], e["name"]))
    total = sum(e["bytes"] for e in entries)
    cards = "\n".join(card(e) for e in entries)
    page = f"""<!doctype html>
<html lang="pt-BR"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Aquamarine: prévia de áudio</title>
<style>
:root {{ --bg:#06141c; --card:#0c2230; --ink:#d8eef2; --muted:#8fb3bd; --accent:#46d6c8; }}
body {{ margin:0; background:var(--bg); color:var(--ink); font:16px/1.5 system-ui, sans-serif; }}
main {{ max-width:1000px; margin:0 auto; padding:24px 16px 64px; }}
h1 {{ color:var(--accent); margin:0 0 4px; }}
.intro {{ color:var(--muted); }}
article {{ background:var(--card); border-radius:12px; padding:16px 18px; margin:18px 0; }}
article h2 {{ font-size:1.1rem; margin:4px 0; }}
.kind {{ color:var(--accent); font-size:.8rem; text-transform:uppercase; letter-spacing:.08em; }}
code {{ color:var(--muted); font-size:.85rem; }}
audio {{ width:100%; margin:10px 0 4px; }}
.listen {{ margin:6px 0; }}
.stats {{ color:var(--muted); font-size:.9rem; margin:6px 0 10px; padding-left:18px; }}
img {{ width:100%; border-radius:8px; }}
.section {{ color:var(--accent); margin-top:40px; }}
article.alt {{ border:1px dashed #2f5d6b; }}
</style></head><body><main>
<h1>Aquamarine: prévia de áudio</h1>
<p class="intro">{len(entries)} sons, {total // 1024} KB. Tudo original, gerado por <code>tools/audio/</code>.
Use fones ou caixas boas; as medidas embaixo de cada player são a checagem automática, a opinião é sua.
Os laços tocam 3 voltas seguidas, cruzadas como no jogo: preste atenção nas voltas.</p>
{cards}
{alt_cards()}
</main></body></html>
"""
    path = os.path.join(OUT, "index.html")
    with open(path, "w", encoding="utf-8") as f:
        f.write(page)
    print(path)
    return path


if __name__ == "__main__":
    p = build()
    if "--open" in sys.argv:
        os.startfile(p)
