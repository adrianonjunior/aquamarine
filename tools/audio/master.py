"""Finalização dos sons do Aquamarine (docs/09-audio.md, "Padrões técnicos"): alvo de loudness por
tipo, pico real, codificação .ogg, medidas (ffmpeg ebur128), espectrograma, emenda do laço e manifesto.

Os agentes não ouvem: estas medidas e o espectrograma são a verificação automática; a qualidade
subjetiva é do Adriano, nos portões de audição.
"""
import json
import os
import re
import subprocess

import numpy as np
import soundfile as sf
from scipy import signal

import dsp

REPO = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
SOUNDS = os.path.join(REPO, "src", "main", "resources", "assets", "aquamarine", "sounds")
BUILD = os.path.join(REPO, "build", "audio")
MANIFEST = os.path.join(os.path.dirname(os.path.abspath(__file__)), "manifest.json")

# Alvos (docs/09-audio.md): ambiência -26 a -24 LUFS; música -18 LUFS; efeitos com pico em -3 dBFS.
TARGETS = {"ambience": -25.0, "music": -18.0}
QUALITY = {"sfx": 4, "ambience": 4, "music": 3}
TRUE_PEAK_MAX = -1.0
# Laços levam os primeiros LOOP_OVERLAP segundos repetidos no fim: o tocador do jogo começa a próxima
# volta antes do fim e cruza esse trecho (as bordas do Vorbis caem onde o volume é quase zero).
LOOP_OVERLAP = 2.0


def ffmpeg(*args):
    return subprocess.run(["ffmpeg", "-hide_banner", "-nostats", *args], capture_output=True, text=True,
                          encoding="utf-8", errors="replace")


def measure(path):
    """Loudness integrado (LUFS), faixa (LRA), pico real (dBTP) e trechos de silêncio."""
    res = ffmpeg("-i", path, "-af", "ebur128=peak=true,silencedetect=noise=-60dB:d=1.5", "-f", "null", "-")
    err = res.stderr
    summary = err[err.rfind("Summary:"):]
    get = lambda key: float(re.search(key + r":\s+(-?[\d.]+|-inf)", summary).group(1).replace("-inf", "-120"))
    silences = [float(x) for x in re.findall(r"silence_duration: ([\d.]+)", err)]
    return {"lufs": get("I"), "lra": get("LRA"), "true_peak": get("Peak"), "silence_s": round(sum(silences), 2)}


def true_peak_db(x):
    up = signal.resample_poly(x if x.ndim == 1 else x.T, 4, 1, axis=0)
    return 20 * np.log10(np.abs(up).max() + 1e-12)


def _write_wav(path, x):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    sf.write(path, x.T if x.ndim == 2 else x, dsp.SR, subtype="PCM_24")


def finish(x, kind, peak_db=-3.0):
    """Leva ao alvo do tipo: efeitos por pico; ambiência e música por loudness integrado."""
    if kind == "sfx":
        x = dsp.normalize_peak(x, peak_db)
    else:
        tmp = os.path.join(BUILD, "_measure.wav")
        for _ in range(3):
            _write_wav(tmp, x)
            lufs = measure(tmp)["lufs"]
            x = x * dsp.db(TARGETS[kind] - lufs)
            x = dsp.limit(x, -1.5)
    while true_peak_db(x) > TRUE_PEAK_MAX - 0.2:
        x = dsp.limit(x * dsp.db(-0.5), -1.5)
    return x


def seam(x):
    """Emenda do laço: salto na volta comparado ao passo típico, e distância espectral fim→começo
    comparada à de janelas vizinhas no meio (perto de 1 = emenda inaudível)."""
    m = x.mean(axis=0) if x.ndim == 2 else x
    step = np.median(np.abs(np.diff(m))) + 1e-9
    jump = abs(m[0] - m[-1]) / step
    w = dsp.n_of(0.1)

    def spec(seg):
        return np.log(np.abs(np.fft.rfft(seg * np.hanning(len(seg)))) + 1e-6)

    across = np.linalg.norm(spec(m[-w:]) - spec(m[:w]))
    mid = len(m) // 2
    typical = np.median([np.linalg.norm(spec(m[k - w:k]) - spec(m[k:k + w])) for k in range(mid - 5 * w, mid + 5 * w, w)])
    return {"seam_jump": round(float(jump), 2), "seam_spectral": round(float(across / (typical + 1e-9)), 2)}


def crossfaded_lap(decoded, n, xf):
    """Uma volta como o tocador a ouve: o fim (sobra) cruza linearmente com o começo."""
    lap = decoded[:n].copy()
    lin = np.linspace(0, 1, xf)[:, None]
    lap[:xf] = decoded[:xf] * lin + decoded[n:n + xf] * (1 - lin)
    return lap


def audition(decoded, n, xf, laps=3):
    """Arquivo de prévia com várias voltas cruzadas, para ouvir as emendas como no jogo."""
    lap = crossfaded_lap(decoded, n, xf)
    out = np.concatenate([decoded[:n]] + [lap] * (laps - 1) + [decoded[n:n + xf] * np.linspace(1, 0, xf)[:, None]])
    return out


def export(name, x, kind, script, seed, description, listen_for, loop=False, license_="original (síntese por código)",
           mono=None):
    """Finaliza, grava master WAV (build/) e .ogg (assets/), mede, faz espectrograma e atualiza o manifesto."""
    mono = (kind == "sfx") if mono is None else mono
    if mono and x.ndim == 2:
        x = x.mean(axis=0)
    if not mono and x.ndim == 1:
        x = np.stack([x, x])
    x = finish(x, kind)
    n_loop = x.shape[-1]
    master_seam = seam(x) if loop else None
    if loop:
        x = np.concatenate([x, x[..., :dsp.n_of(LOOP_OVERLAP)]], axis=-1)
    wav = os.path.join(BUILD, "masters", name + ".wav")
    ogg = os.path.join(SOUNDS, name + ".ogg")
    png = os.path.join(BUILD, "spectrograms", name.replace("/", "_") + ".png")
    _write_wav(wav, x)
    os.makedirs(os.path.dirname(ogg), exist_ok=True)
    os.makedirs(os.path.dirname(png), exist_ok=True)
    enc = ffmpeg("-y", "-i", wav, "-ac", "1" if mono else "2", "-c:a", "libvorbis", "-q:a", str(QUALITY[kind]), ogg)
    if enc.returncode:
        raise RuntimeError(enc.stderr[-800:])
    ffmpeg("-y", "-i", ogg, "-lavfi", "showspectrumpic=s=960x360:legend=1:scale=log:fscale=log:color=intensity", png)
    stats = measure(ogg)
    decoded, _ = sf.read(ogg, always_2d=True)
    entry = {
        "name": name, "file": os.path.relpath(ogg, REPO).replace("\\", "/"), "kind": kind,
        "channels": 1 if mono else 2, "seconds": round(n_loop / dsp.SR, 2), "loop": loop,
        "bytes": os.path.getsize(ogg), "script": script, "seed": seed, "license": license_,
        "clipping_samples": int((np.abs(decoded) >= 0.999).sum()),
        "description": description, "listen_for": listen_for, **stats,
    }
    if loop:
        xf = dsp.n_of(LOOP_OVERLAP)
        entry["loop_overlap_s"] = LOOP_OVERLAP
        entry["master_seam"] = master_seam
        entry.update(seam(crossfaded_lap(decoded, n_loop, xf).T))
        lap_wav = os.path.join(BUILD, "audition", name.replace("/", "_") + "_3x.wav")
        os.makedirs(os.path.dirname(lap_wav), exist_ok=True)
        sf.write(lap_wav, audition(decoded, n_loop, xf), dsp.SR, subtype="PCM_24")
        ffmpeg("-y", "-i", lap_wav, "-c:a", "libvorbis", "-q:a", "5", lap_wav[:-4] + ".ogg")
    manifest = load_manifest()
    manifest[name] = entry
    with open(MANIFEST, "w", encoding="utf-8") as f:
        json.dump(dict(sorted(manifest.items())), f, ensure_ascii=False, indent=2)
        f.write("\n")
    print(f"{name}: {entry['seconds']} s, {stats['lufs']} LUFS, pico {stats['true_peak']} dBTP, "
          f"{entry['bytes'] // 1024} KB" + (f", emenda {entry['seam_jump']}/{entry['seam_spectral']}" if loop else ""))
    return entry


def load_manifest():
    if os.path.exists(MANIFEST):
        with open(MANIFEST, encoding="utf-8") as f:
            return json.load(f)
    return {}
