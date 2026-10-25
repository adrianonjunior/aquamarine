"""Alternativa de IA local para a música (docs/09-audio.md, "Produção", item 2): MusicGen estéreo
pequeno na GPU, só como matéria-prima para o Adriano comparar com a síntese. Nada daqui entra no
mod sem o aval dele. Pesos: facebook/musicgen-stereo-small, licença CC-BY-NC 4.0 (uso privado ok).

Uso (ambiente com torch): tools/audio/.venv-ai/Scripts/python tools/audio/musicgen_alt.py
Grava WAV cru em build/audio/alt/raw/; o acabamento é do alt_finish.py (ambiente de síntese).
"""
import json
import os
import sys

import numpy as np
import soundfile as sf
import torch
from transformers import AutoProcessor, MusicgenForConditionalGeneration

REPO = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
RAW = os.path.join(REPO, "build", "audio", "alt", "raw")
MODEL = "facebook/musicgen-stereo-small"
SECONDS = 30

PIECES = {
    "harbor_ai": (611, "warm cozy underwater harbor town theme, glass harmonica melody, soft marimba arpeggios, "
                       "gentle string pad, lydian mode, 76 bpm, peaceful and safe, fantasy game soundtrack, high quality"),
    "harbor_ai_b": (612, "gentle fantasy village music, celesta and marimba melody, warm strings, harp arpeggios, "
                         "major key, slow 76 bpm, calm and cozy, orchestral video game soundtrack, clear melody, high quality"),
    "harbor_ai_c": (613, "peaceful orchestral town theme, flute melody over pizzicato strings and harp, marimba, "
                         "lydian, 76 bpm, warm and safe, nintendo style fantasy game music, high quality"),
    "kraken_storm_ai": (811, "epic dark orchestral boss battle, deep taiko drums, low brass, ominous male choir, "
                             "stormy ocean, phrygian mode, 100 bpm, intense cinematic fantasy game soundtrack, high quality"),
    "kraken_storm_ai_b": (812, "epic orchestral boss battle music, staccato strings ostinato, powerful brass stabs, "
                               "dark choir, big taiko and timpani hits, minor key, 120 bpm, terraria calamity style, high quality"),
    "kraken_storm_ai_c": (813, "intense heroic dark battle theme, fast string runs, french horns melody, choir, war drums, "
                               "cymbal crashes, d minor, 120 bpm, cinematic orchestral video game boss music, high quality"),
}


def main():
    os.makedirs(RAW, exist_ok=True)
    processor = AutoProcessor.from_pretrained(MODEL)
    model = MusicgenForConditionalGeneration.from_pretrained(MODEL, torch_dtype=torch.float16).to("cuda")
    sr = model.config.audio_encoder.sampling_rate
    meta_path = os.path.join(RAW, "meta.json")
    meta = json.load(open(meta_path, encoding="utf-8")) if os.path.exists(meta_path) else {}
    only = set(sys.argv[1:])
    for name, (seed, prompt) in PIECES.items():
        if only and name not in only:
            continue
        torch.manual_seed(seed)
        inputs = processor(text=[prompt], padding=True, return_tensors="pt").to("cuda")
        with torch.no_grad():
            audio = model.generate(**inputs, do_sample=True, guidance_scale=3.5, max_new_tokens=SECONDS * 50)
        data = audio[0].float().cpu().numpy()
        sf.write(os.path.join(RAW, name + ".wav"), data.T, sr, subtype="PCM_24")
        meta[name] = {"seed": seed, "prompt": prompt, "model": MODEL, "license": "CC-BY-NC 4.0 (pesos do MusicGen)",
                      "sample_rate": sr, "channels": int(data.shape[0])}
        print(name, data.shape, sr)
    with open(os.path.join(RAW, "meta.json"), "w", encoding="utf-8") as f:
        json.dump(meta, f, ensure_ascii=False, indent=2)


if __name__ == "__main__":
    main()
