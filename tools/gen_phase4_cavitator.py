"""Cavitador de Neutrônio, faixa 4G (docs/10-fase4.md, §9): modelo 3D, estágios de carga, ícone, partículas e dados.

O modelo é feito de peças ao longo de x (boca do cano em x=0, coronha em x=16, topo em +z, empunhadura em -z) e
girado 45° na mesma diagonal do sprite da besta; assim as poses de mão da besta seguram a arma na horizontal. Na mão
aparece o 3D (com o núcleo acendendo e as bobinas ganhando luz a cada estágio da carga); no inventário, no chão e na
moldura, o ícone 2D animado.

    C:\\Python314\\python tools/gen_phase4_cavitator.py [--preview]
"""
import json
import math
import os
import sys

from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import gen_phase4_art as art  # noqa: E402
import gen_phase4_building as bld  # noqa: E402
import gen_phase4_neutronium as neu  # noqa: E402
from gen_phase4_art import Tex, hexc, outline, CLEAR  # noqa: E402

NS, ASSETS, DATA, TEX = art.NS, art.ASSETS, bld.DATA, neu.TEX
FRAMES = neu.FRAMES
ROT = {"origin": [8, 8, 8], "axis": "z", "angle": -45}  # a diagonal do sprite da besta (frente no alto à esquerda)
CROSSBOW_DISPLAY = {
    "thirdperson_righthand": {"rotation": [-90, 0, -60], "translation": [2, 0.1, -3], "scale": [0.9, 0.9, 0.9]},
    "thirdperson_lefthand": {"rotation": [-90, 0, 30], "translation": [2, 0.1, -3], "scale": [0.9, 0.9, 0.9]},
    "firstperson_righthand": {"rotation": [-90, 0, -55], "translation": [1.13, 3.2, 1.13], "scale": [0.68, 0.68, 0.68]},
    "firstperson_lefthand": {"rotation": [-90, 0, 35], "translation": [1.13, 3.2, 1.13], "scale": [0.68, 0.68, 0.68]},
    "head": {"rotation": [0, 0, 0], "translation": [0, 10, -6], "scale": [0.8, 0.8, 0.8]},
}


# ================================================================ texturas das peças

def hull_frames():
    return [neu.Sky(901, density=0.15).render(f) for f in range(FRAMES)]


def frame_tex():
    t = Tex(902)
    t.noise((0, 0, 16, 16), neu.HANDLE[1:4], [3, 4, 2])
    for k in (0, 5, 10, 15):  # filetes ciano
        t.hline(0, 15, k, (60, 210, 240, 255) if k in (0, 15) else (30, 120, 150, 255))
    for x in range(0, 16, 4):
        t.put(x, 7, (150, 245, 255, 255))
    return t.img


def core_frames(stage):
    """Núcleo que pulsa; cada estágio da carga acende mais."""
    base = 0.45 + 0.18 * stage
    frames = []
    for f in range(FRAMES):
        t = Tex(903 + stage)
        pulse = base + 0.12 * math.sin(2 * math.pi * f / FRAMES)
        for y in range(16):
            for x in range(16):
                d = math.hypot(x + 0.5 - 8, y + 0.5 - 8) / 8
                k = max(0.0, min(1.0, pulse * (1.25 - d)))
                t.put(x, y, (int(40 + 200 * k * k), int(120 + 135 * k), int(150 + 105 * k), 255))
        frames.append(t.img)
    return frames


def brass_tex():
    t = Tex(906)
    t.noise((0, 0, 16, 16), art.BRASS[2:5], [2, 4, 2])
    t.scatter(8, [art.BRASS[5]])
    return t.img


def grip_tex():
    t = Tex(907)
    t.noise((0, 0, 16, 16), [(14, 16, 22, 255), (20, 24, 32, 255), (26, 30, 40, 255)], [2, 4, 2])
    for y in range(0, 16, 2):  # sulcos
        t.hline(0, 15, y, (8, 10, 14, 255))
    return t.img


def tip_tex():
    t = Tex(908)
    t.noise((0, 0, 16, 16), art.GOLD[2:5], [2, 4, 2])
    t.hline(0, 15, 0, art.GOLD[5])
    t.put(0, 8, (110, 255, 236, 255))
    return t.img


# ================================================================ modelo 3D

def model(stage):
    tex = {"hull": f"{NS}:item/cavitator_hull", "frame": f"{NS}:item/cavitator_frame", "core": f"{NS}:item/cavitator_core_{stage}",
           "brass": f"{NS}:item/cavitator_brass", "grip": f"{NS}:item/cavitator_grip", "tip": f"{NS}:item/cavitator_tip"}
    els = []

    def box(frm, to, t, light=0):
        faces = {f: {"texture": "#" + t} for f in ("north", "south", "east", "west", "up", "down")}
        el = {"from": [round(v, 3) for v in frm], "to": [round(v, 3) for v in to], "rotation": ROT, "faces": faces}
        if light:
            el["light_emission"] = light
        els.append(el)

    coil_light = (0, 6, 11, 15)[stage]
    # boca do cano (emissor) e o núcleo aceso dentro dela
    box((0, 6, 6), (1.2, 10, 10), "frame")
    box((-0.15, 6.8, 6.8), (0.6, 9.2, 9.2), "core", 15)
    # cano arredondado (cruz de duas peças)
    box((1, 6.6, 7.4), (9, 9.4, 8.6), "hull")
    box((1, 7.4, 6.6), (9, 8.6, 9.4), "hull")
    # três bobinas de neutrônio
    for x in (2.2, 4.4, 6.6):
        box((x, 6.0, 6.0), (x + 1.0, 10.0, 10.0), "frame", coil_light)
        box((x + 0.3, 5.9, 7.6), (x + 0.7, 10.1, 8.4), "core", 15)
    # câmara do núcleo com janelas acesas dos dois lados
    box((8.5, 5.5, 5.5), (12, 10.5, 10.5), "hull")
    box((9, 5.35, 7), (11.5, 5.5, 9), "core", 15)
    box((9, 10.5, 7), (11.5, 10.65, 9), "core", 15)
    box((8.3, 6.2, 6.2), (8.6, 9.8, 9.8), "frame")
    box((11.9, 6.2, 6.2), (12.2, 9.8, 9.8), "frame")
    # mangueiras de latão da câmara até o cano
    box((6.8, 9.4, 8.8), (8.8, 10.0, 9.4), "brass")
    box((6.8, 6.0, 8.8), (8.8, 6.6, 9.4), "brass")
    # trilho em cima com o arpão carregado (o modo de fora d'água)
    box((1.6, 7.6, 10.0), (9, 8.4, 10.6), "frame")
    box((-1.8, 7.55, 10.05), (1.6, 8.45, 10.85), "tip")
    box((-2.6, 7.75, 10.25), (-1.8, 8.25, 10.65), "tip")
    # barbatanas: dorsal e as duas estabilizadoras
    box((9, 7.6, 10.5), (12.4, 8.4, 12.2), "frame")
    box((9.5, 4.2, 7.5), (12.2, 5.5, 8.5), "frame")
    box((9.5, 10.5, 7.5), (12.2, 11.8, 8.5), "frame")
    # empunhadura, guarda-mato e gatilho
    box((10.4, 7.0, 1.8), (12.2, 9.0, 5.6), "grip")
    box((8.6, 7.6, 4.6), (10.4, 8.4, 5.2), "frame")
    box((9.5, 7.8, 5.2), (9.9, 8.2, 6.2), "brass")
    # coronha e a sapata
    box((12, 6.5, 6.5), (15.4, 9.5, 9.5), "hull")
    box((15.4, 6.0, 6.0), (16.4, 10.0, 10.0), "grip")
    return {"textures": {**tex, "particle": tex["hull"]}, "elements": els, "display": CROSSBOW_DISPLAY}


# ================================================================ ícone 2D

def icon_frames():
    """Perfil do Cavitador na diagonal: coronha embaixo à direita, boca no alto à esquerda, como a besta."""
    sky = neu.Sky(910, density=0.16)
    ux, uy = -1 / math.sqrt(2), -1 / math.sqrt(2)  # da coronha para a boca (em pixels: para cima e para a esquerda)
    vx, vy = -uy, ux
    ox, oy = 13.5, 13.0

    def part(x, y):
        px, py = x + 0.5 - ox, y + 0.5 - oy
        u, v = px * ux + py * uy, px * vx + py * vy
        if 0 <= u < 3 and abs(v) <= 1.6:
            return "stock"
        if 3 <= u < 7.5 and abs(v) <= 2.2:
            return "chamber"
        if 4.5 <= u < 6.2 and 2.2 < v <= 5.0:
            return "grip"
        if 7.5 <= u < 14.5:
            ring = any(abs(u - c) < 0.55 for c in (8.8, 10.6, 12.4))
            if abs(v) <= (1.9 if ring else 1.0):
                return "ring" if ring else "barrel"
        if 14.5 <= u < 15.8 and abs(v) <= 1.8:
            return "muzzle"
        if 7.0 <= u < 17.2 and -2.6 <= v < -1.6:
            return "harpoon"
        return None

    frames = []
    for f in range(FRAMES):
        cp = sky.render(f).load()
        t = Tex(911)
        for y in range(16):
            for x in range(16):
                p = part(x, y)
                if p in ("stock", "chamber", "barrel"):
                    t.put(x, y, cp[x, y])
                elif p == "ring":
                    t.put(x, y, (70, 220, 245, 255) if (x + y + f) % 3 else (180, 255, 255, 255))
                elif p == "grip":
                    t.put(x, y, neu.HANDLE[2])
                elif p == "muzzle":
                    glow = 0.75 + 0.25 * math.sin(2 * math.pi * f / FRAMES)
                    t.put(x, y, (int(150 * glow), int(255 * glow), int(255 * glow), 255))
                elif p == "harpoon":
                    t.put(x, y, art.GOLD[4])
        img = outline(t.img, (8, 26, 40, 255))
        frames.append(img)
    return frames


# ================================================================ partículas

def particle_textures():
    out = {}
    for i, r in enumerate((1.4, 2.0, 2.6, 3.2)):  # bolha que cresce
        t = Tex(920 + i, 8, 8)
        for y in range(8):
            for x in range(8):
                d = math.hypot(x + 0.5 - 4, y + 0.5 - 4)
                if abs(d - r) < 0.55:
                    t.put(x, y, (220, 255, 255, 230))
                elif d < r - 0.55:
                    t.put(x, y, (140, 230, 255, 50))
        t.put(3, 2, (255, 255, 255, 255))
        out[f"cavitation_bubble_{i}"] = t.img
    for i, arm in enumerate((3, 2, 1)):  # centelha em cruz que encolhe
        t = Tex(930 + i, 8, 8)
        for k in range(-arm, arm + 1):
            a = 255 if abs(k) < 2 else 170
            t.put(4 + k, 4, (180, 250, 255, a))
            t.put(4, 4 + k, (180, 250, 255, a))
        t.put(4, 4, (255, 255, 255, 255))
        out[f"neutron_spark_{i}"] = t.img
    t = Tex(940, 16, 16)
    for y in range(16):
        for x in range(16):
            d = math.hypot(x + 0.5 - 8, y + 0.5 - 8)
            if 5.4 <= d < 7.6:
                a = int(255 * (1 - abs(d - 6.5) / 1.1))
                t.put(x, y, (150, 245, 255, max(0, a)))
    out["shock_ring"] = t.img
    t = Tex(950, 4, 4)
    t.rect((1, 1, 3, 3), (200, 255, 255, 255))
    for x, y in ((0, 1), (3, 2), (1, 0), (2, 3)):
        t.put(x, y, (120, 230, 255, 140))
    out["charge_mote"] = t.img
    return out


# ================================================================ escrita

def main():
    w = bld.write_json
    neu.write_anim(hull_frames(), "item/cavitator_hull")
    for stage in range(4):
        neu.write_anim(core_frames(stage), f"item/cavitator_core_{stage}", 2)
    art.write_png(frame_tex(), "item/cavitator_frame")
    art.write_png(brass_tex(), "item/cavitator_brass")
    art.write_png(grip_tex(), "item/cavitator_grip")
    art.write_png(tip_tex(), "item/cavitator_tip")
    icon = neu.write_anim(icon_frames(), "item/neutronium_cavitator")
    w({"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/neutronium_cavitator"}}, "models/item/neutronium_cavitator.json")
    for stage in range(4):
        w(model(stage), f"models/item/neutronium_cavitator_3d{'' if stage == 0 else f'_charge_{stage}'}.json")
    flat = {"type": "minecraft:model", "model": f"{NS}:item/neutronium_cavitator"}
    hand = {"type": "minecraft:condition", "property": "minecraft:using_item",
            "on_false": {"type": "minecraft:model", "model": f"{NS}:item/neutronium_cavitator_3d"},
            "on_true": {"type": "minecraft:range_dispatch", "property": "minecraft:use_duration", "scale": 0.05,
                        "entries": [{"threshold": 0.2, "model": {"type": "minecraft:model", "model": f"{NS}:item/neutronium_cavitator_3d_charge_2"}},
                                    {"threshold": 0.6, "model": {"type": "minecraft:model", "model": f"{NS}:item/neutronium_cavitator_3d_charge_3"}}],
                        "fallback": {"type": "minecraft:model", "model": f"{NS}:item/neutronium_cavitator_3d_charge_1"}}}
    w({"model": {"type": "minecraft:select", "property": "minecraft:display_context",
                 "cases": [{"when": ["gui", "ground", "fixed"], "model": flat}], "fallback": hand}}, "items/neutronium_cavitator.json")

    for name, img in particle_textures().items():
        art.write_png(img, f"particle/{name}")
    for name, frames in (("cavitation_bubble", 4), ("neutron_spark", 3), ("shock_ring", 0), ("charge_mote", 0)):
        textures = [f"{NS}:{name}_{i}" for i in range(frames)] if frames else [f"{NS}:{name}"]
        w({"textures": textures}, f"particles/{name}.json")

    w({"type": f"{NS}:abyssal_forging", "category": "equipment",
       "key": {"C": f"{NS}:voltaic_coil", "I": f"{NS}:neutronium_ingot", "K": f"{NS}:kraken_heart", "H": f"{NS}:abyssal_harpoon",
               "R": f"{NS}:pressure_regulator"},
       "pattern": ["CIC", "IKI", "HR "], "result": {"id": f"{NS}:neutronium_cavitator"}}, f"{NS}/recipe/neutronium_cavitator.json", DATA)
    w({"message_id": f"{NS}.cavitation", "exhaustion": 0.1, "scaling": "when_caused_by_living_non_player"},
      f"{NS}/damage_type/cavitation.json", DATA)
    for rel in (f"{NS}/tags/item/neutronium_tools.json", f"{NS}/tags/item/neutronium_dense.json", "minecraft/tags/item/enchantable/durability.json"):
        bld.merge_tag(rel, [f"{NS}:neutronium_cavitator"])
    bld.merge_lang({
        f"item.{NS}.neutronium_cavitator": ("Cavitador de Neutrônio", "Neutronium Cavitator"),
        f"tooltip.{NS}.neutronium_cavitator": ("Segure e solte. Debaixo d'água: pulso de cavitação (10 de dano, 32 blocos, atravessa 3).",
                                               "Hold and release. Underwater: a cavitation pulse (10 damage, 32 blocks, pierces 3)."),
        f"tooltip.{NS}.neutronium_cavitator.2": ("Fora d'água: lança arpões com a ponta escolhida na Aljava.",
                                                 "Out of water: fires harpoons with the tip selected in the Quiver."),
        f"tooltip.{NS}.neutronium_cavitator.3": ("80% de dano no escudo do Kraken.", "Deals 80% damage to the Kraken's shield."),
        f"death.attack.{NS}.cavitation": ("%1$s implodiu numa bolha de cavitação", "%1$s imploded in a cavitation bubble"),
        f"death.attack.{NS}.cavitation.player": ("%1$s implodiu no pulso de %2$s", "%1$s was imploded by %2$s's pulse"),
    })

    if "--preview" in sys.argv:
        tiles = [icon] + [Image.open(os.path.join(TEX, f"particle/{n}.png")).convert("RGBA").resize((16, 16), Image.NEAREST)
                          for n in ("cavitation_bubble_2", "neutron_spark_0", "shock_ring", "charge_mote")]
        sheet = Image.new("RGBA", (len(tiles) * 104, 100), (40, 60, 80, 255))
        for i, t in enumerate(tiles):
            sheet.alpha_composite(t.resize((96, 96), Image.NEAREST), (i * 104, 2))
        sheet.save(os.path.join(art.REPO, "build", "phase4_cavitator_preview.png"))
    print("fase 4 (4G): Cavitador com 4 modelos 3D, ícone animado e 4 partículas")


if __name__ == "__main__":
    main()
