#!/usr/bin/env python3
"""Verifica a UV do tentaculo, da Cria e do ovo do Kraken.

Uso (na raiz do repositorio, depois de build_model.py e gen_spawnlings_texture.py):
    python tools/models/spawnlings/check_uv.py

Para cada cubo do .bbmodel (box-UV do Minecraft, com as dimensoes reais, fracionarias ou nao):
  1. as 6 faces estao dentro da imagem;
  2. a rede (dimensoes arredondadas para cima) nao se sobrepoe a de outro cubo, nem as faces de um
     cubo tocam pixels de outro;
  3. nenhum pixel das 6 faces e transparente nem a cor de fundo do gabarito no PNG final;
  4. os texOffs do Java real (bloco UV:BEGIN/UV:END) sao os mesmos do .bbmodel, o Java real e o
     template sao identicos e LayerDefinition.create usa o tamanho da textura.
Sai com codigo 1 se algo falhar.
"""
from __future__ import annotations

import json
import math
import re
import sys
from pathlib import Path

from PIL import Image

BASE = Path(__file__).resolve().parent
sys.path.insert(0, str(BASE))
import build_model as bm  # noqa: E402

EPS = 1e-6


def faces(u, v, w, h, d):
    """(nome, x0, y0, x1, y1) das 6 faces, como em ModelPart.Cube do Minecraft."""
    return [("top", u + d, v, u + d + w, v + d), ("bottom", u + d + w, v, u + 2 * w + d, v + d),
            ("right", u, v + d, u + d, v + d + h), ("front", u + d, v + d, u + d + w, v + d + h),
            ("left", u + d + w, v + d, u + 2 * d + w, v + d + h),
            ("back", u + 2 * d + w, v + d, u + 2 * d + 2 * w, v + d + h)]


def pixels(x0, y0, x1, y1):
    """Pixels que a face cobre ao ser amostrada (conservador para limites fracionarios)."""
    return [(x, y) for y in range(math.floor(y0 + EPS), math.ceil(y1 - EPS))
            for x in range(math.floor(x0 + EPS), math.ceil(x1 - EPS))]


def check(identifier, errors):
    bb = json.loads((BASE / identifier / f"{identifier}.bbmodel").read_text(encoding="utf-8"))
    width, height = bb["resolution"]["width"], bb["resolution"]["height"]
    png = bm.RESOURCE_TEXTURES / identifier / f"{identifier}.png"
    img = Image.open(png).convert("RGBA")
    if img.size != (width, height):
        errors.append(f"{identifier}: PNG {img.size} != resolution {(width, height)}")
    px = img.load()
    owner, rects, offsets = {}, [], {}
    for el in bb["elements"]:
        name, (u, v) = el["name"], el["uv_offset"]
        offsets[name] = (u, v)
        w, h, d = (round(abs(el["to"][i] - el["from"][i]), 4) for i in range(3))
        if u != int(u) or v != int(v):
            errors.append(f"{identifier}/{name}: uv_offset {u},{v} nao e inteiro")
        cw, ch, cd = (max(1, math.ceil(x)) for x in (w, h, d))
        rects.append((name, u, v, u + 2 * (cw + cd), v + ch + cd))
        for face, x0, y0, x1, y1 in faces(u, v, w, h, d):
            if x0 < 0 or y0 < 0 or x1 > width or y1 > height:
                errors.append(f"{identifier}/{name}/{face}: fora da imagem ({x0},{y0})-({x1},{y1})")
                continue
            for x, y in pixels(x0, y0, x1, y1):
                if owner.setdefault((x, y), name) != name:
                    errors.append(f"{identifier}/{name}/{face}: pixel {x},{y} tambem e de {owner[(x, y)]}")
                    break
                r, g, b, a = px[x, y]
                if a == 0 or (r, g, b, a) == bm.BG:
                    errors.append(f"{identifier}/{name}/{face}: pixel {x},{y} transparente/fundo {px[x, y]}")
                    break
    for i, (n1, ax0, ay0, ax1, ay1) in enumerate(rects):
        if ax1 > width or ay1 > height:
            errors.append(f"{identifier}/{n1}: rede ({ax0},{ay0})-({ax1},{ay1}) passa da imagem")
        for n2, bx0, by0, bx1, by1 in rects[i + 1:]:
            if ax0 < bx1 and bx0 < ax1 and ay0 < by1 and by0 < ay1:
                errors.append(f"{identifier}: redes {n1} e {n2} se sobrepoem")
    # Java real
    model = bm.JAVA_MODEL[identifier]
    real = bm.JAVA_ROOT / identifier / f"{model}.java"
    template = bm.JAVA_TEMPLATES / identifier / f"{model}.java.template"
    if real.read_bytes() != template.read_bytes():
        errors.append(f"{identifier}: {real.name} difere do template")
    src = real.read_text(encoding="utf-8")
    block = re.search(r"UV:BEGIN.*?UV:END", src, re.S)
    if not block:
        errors.append(f"{real.name}: bloco UV:BEGIN/UV:END ausente")
        return offsets
    for const, names, scalar in bm.JAVA_UV[identifier]:
        m = re.search(rf"{const} = (.*?);", block.group(0), re.S)
        if not m:
            errors.append(f"{real.name}: constante {const} ausente")
            continue
        java = [tuple(map(int, p)) for p in re.findall(r"\{(\d+),\s*(\d+)\}", m.group(1))]
        expected = [tuple(offsets[n]) for n in names]
        if java != expected:
            errors.append(f"{real.name}: {const} {java} != bbmodel {expected}")
    m = re.search(r"LayerDefinition\.create\(mesh,\s*(\d+),\s*(\d+)\)", src)
    if not m or (int(m.group(1)), int(m.group(2))) != (width, height):
        errors.append(f"{real.name}: LayerDefinition.create nao usa {width}x{height}")
    if "texOffs(" in src and re.search(r"texOffs\(\s*\d", src):
        errors.append(f"{real.name}: ainda ha texOffs com numero literal")
    print(f"{identifier}: {len(bb['elements'])} cubos, {len(owner)} pixels de face, "
          f"{width}x{height}, ok={not any(e.startswith(identifier) for e in errors)}")
    return offsets


def main():
    errors = []
    for identifier in ("tentacle", "young_kraken", "kraken_egg"):
        check(identifier, errors)
    for e in errors:
        print("ERRO:", e)
    print("FALHOU" if errors else "OK: todas as faces dentro da imagem, sem sobreposicao, sem pixel vazio")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
