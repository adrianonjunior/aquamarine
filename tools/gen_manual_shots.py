"""Prints do Manual do Aquamarine: pega as capturas `manual_<nome>` do teste de cliente (parte `manualshots`), corta em
16:9 e reduz para 320 x 180 em textures/gui/manual/<nome>.png. Também monta build/manual_prints.png (folha de conferência).

Uso: ./gradlew runClientGameTest com AQUAMARINE_GAMETEST_ONLY=manualshots; depois python tools/gen_manual_shots.py.
"""
from pathlib import Path
import re
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
SHOTS = ROOT / 'build/run/clientGameTest/screenshots'
OUT = ROOT / 'src/main/resources/assets/aquamarine/textures/gui/manual'
W, H = 320, 180


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    found = {}
    for path in sorted(SHOTS.glob('*.png')):
        m = re.match(r'\d+_manual_(.+)\.png$', path.name)
        if m:
            found[m.group(1)] = path
    sheet = Image.new('RGB', (W * 3 + 8, (H + 4) * ((len(found) + 2) // 3) + 4), (20, 24, 28))
    for i, (name, path) in enumerate(sorted(found.items())):
        im = Image.open(path).convert('RGB')
        w, h = im.size
        cw, ch = (w, round(w * 9 / 16)) if w * 9 <= h * 16 else (round(h * 16 / 9), h)
        box = ((w - cw) // 2, (h - ch) // 2, (w - cw) // 2 + cw, (h - ch) // 2 + ch)
        small = im.crop(box).resize((W, H), Image.LANCZOS)
        small.save(OUT / f'{name}.png', optimize=True)
        sheet.paste(small, (4 + (i % 3) * (W + 2), 4 + (i // 3) * (H + 4)))
    sheet.save(ROOT / 'build/manual_prints.png')
    print(f'{len(found)} prints: {", ".join(sorted(found))}')


if __name__ == '__main__':
    main()
