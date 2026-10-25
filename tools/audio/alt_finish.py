"""Acabamento das peças de IA local (musicgen_alt.py) para comparação na prévia: 48 kHz, limpeza
de graves e agudos, um pouco de sala, loudness da música (-18 LUFS) e .ogg em build/audio/alt/.
Não entra no mod: é só para o Adriano comparar com a síntese no portão A0.

Uso: tools/audio/.venv/Scripts/python tools/audio/alt_finish.py
"""
import json
import os

import numpy as np
import soundfile as sf
from scipy import signal

import dsp
import master

ALT = os.path.join(master.BUILD, "alt")


def main():
    raw = os.path.join(ALT, "raw")
    with open(os.path.join(raw, "meta.json"), encoding="utf-8") as f:
        meta = json.load(f)
    out = {}
    for name, info in meta.items():
        x, sr = sf.read(os.path.join(raw, name + ".wav"), always_2d=True)
        x = signal.resample_poly(x, dsp.SR // 1000, sr // 1000, axis=0).T
        x = np.stack([dsp.eq(dsp.hp(ch, 35, 2), "highshelf", 9000, 0.7, -2.0) for ch in x])
        r = dsp.rng(info["seed"])
        x = dsp.reverb(x, dsp.impulse_response(2.5, r, rt60_low=2.2, rt60_high=1.0, dark=9000), wet=0.15)
        x = dsp.fade(x, 0.05, 2.5)
        x = master.finish(x, "music")
        wav = os.path.join(ALT, name + ".wav")
        ogg = os.path.join(ALT, name + ".ogg")
        png = os.path.join(ALT, name + ".png")
        sf.write(wav, x.T, dsp.SR, subtype="PCM_24")
        master.ffmpeg("-y", "-i", wav, "-c:a", "libvorbis", "-q:a", "5", ogg)
        master.ffmpeg("-y", "-i", ogg, "-lavfi", "showspectrumpic=s=960x360:legend=1:scale=log:fscale=log:color=intensity", png)
        out[name] = {**info, **master.measure(ogg), "seconds": round(x.shape[-1] / dsp.SR, 2), "bytes": os.path.getsize(ogg)}
        print(name, out[name]["lufs"], out[name]["true_peak"])
    with open(os.path.join(ALT, "alt.json"), "w", encoding="utf-8") as f:
        json.dump(out, f, ensure_ascii=False, indent=2)


if __name__ == "__main__":
    main()
