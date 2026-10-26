"""Fauna da Fase 6, faixa 6B2 (docs/11-fase6.md): Carne de Baleia crua e cozida.
Grava os ícones, os modelos de item, as receitas (fornalha, defumador e fogueira), a tag de carne e os nomes. A queda
fica no código das baleias (só as selvagens, F6-6).
    C:\\Python314\\python tools/gen_phase6_fauna.py
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import gen_phase4_art as p4  # noqa: E402
import gen_phase4_building as bld  # noqa: E402
from gen_phase4_art import Tex, outline  # noqa: E402

NS, DATA = p4.NS, bld.DATA
RAW = p4.ramp("4a1020", "7a2034", "a8384a", "c95a64", "e48a8a", "f6c0b8")
COOKED = p4.ramp("2e1608", "4e2810", "71401c", "94602e", "b98048", "d8a866")
FAT = p4.ramp("c8bca8", "e6dccb", "fbf5ea")


def steak(ramp, seed, fat_ramp):
    """Bife largo de baleia: lombo com uma faixa de gordura e marmoreio."""
    t = Tex(seed)
    for cx, cy, r in ((7.5, 8.5, 5.5), (10.5, 7, 4), (5, 7, 3.5)):
        t.disc(cx, cy, r, ramp[2])
    for y in range(16):
        for x in range(16):
            if t.get(x, y)[3]:
                t.put(x, y, t.rng.choice([ramp[1], ramp[2], ramp[2], ramp[3]]))
    for x in range(3, 14):  # faixa de gordura em cima
        y = 4 + (x % 5 == 0)
        if t.get(x, y)[3]:
            t.put(x, y, fat_ramp[1])
            t.put(x, y + 1, fat_ramp[0])
    for _ in range(6):
        x, y = t.rng.randrange(4, 12), t.rng.randrange(7, 13)
        if t.get(x, y)[3]:
            t.put(x, y, ramp[4])
    return outline(t.img, ramp[0])


def main():
    p4.write_png(steak(RAW, 951, FAT), "item/raw_whale_meat")
    p4.write_png(steak(COOKED, 952, COOKED[4:]), "item/cooked_whale_meat")
    w = bld.write_json
    for name in ("raw_whale_meat", "cooked_whale_meat"):
        w({"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/{name}"}}, f"models/item/{name}.json")
        w(bld.item_def(f"{NS}:item/{name}"), f"items/{name}.json")
    for kind, time, xp in (("smelting", 200, 0.35), ("smoking", 100, 0.35), ("campfire_cooking", 600, 0.35)):
        w({"type": f"minecraft:{kind}", "category": "food", "ingredient": f"{NS}:raw_whale_meat", "result": {"id": f"{NS}:cooked_whale_meat"},
           "experience": xp, "cookingtime": time}, f"{NS}/recipe/cooked_whale_meat_from_{kind}.json", DATA)
    bld.merge_tag("minecraft/tags/item/meat.json", [f"{NS}:raw_whale_meat", f"{NS}:cooked_whale_meat"])
    bld.merge_lang({
        f"item.{NS}.raw_whale_meat": ("Carne de Baleia Crua", "Raw Whale Meat"),
        f"item.{NS}.cooked_whale_meat": ("Carne de Baleia Cozida", "Cooked Whale Meat"),
        f"tooltip.{NS}.raw_whale_meat": ("Cai das baleias selvagens; Saque aumenta.", "Dropped by wild whales; Looting adds more."),
        f"tooltip.{NS}.cooked_whale_meat": ("Alimento forte: 10 de fome e muita saturação.", "Hearty food: 10 hunger and high saturation."),
    })
    print("fase 6 (6B2): carne de baleia crua e cozida")


if __name__ == "__main__":
    main()
