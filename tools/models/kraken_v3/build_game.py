#!/usr/bin/env python3
"""Os três Krakens da Fase 6 no jogo (docs/11-fase6.md, 6A; escolha do Adriano em 26/10/2026): v3a Lula-colosso é o
`aquamarine:kraken`, v3b Vampiro-abissal e v3c Amonita-colosso entram como `aquamarine:kraken_vampire` e
`aquamarine:kraken_ammonite`.

    C:\\Python314\\python tools/models/kraken_v3/build_game.py              # Java e texturas do jogo
    C:\\Python314\\python tools/models/kraken_v3/build_game.py --register   # e registra no Atlas (v3a-jogo, v3b-jogo, v3c-jogo)

Parte dos mesmos rigs e texturas de `build_candidates.py` e completa os 16 clipes do contrato da `KrakenView`
(docs/06-tecnico.md): aos 6 do Atlas somam-se emerge, stunned, phaseChange, ink, whirlpool, spawnYoung, waterJet, bolts,
ambush e doubleGrab. Grava `KrakenV3{a,b,c}Model` e `...Animations` no pacote do cliente do Kraken e as texturas em
`textures/entity/kraken/v3{a,b,c}.png` (com `_glow`). O movimento contínuo por cima dos clipes fica em `KrakenV3Motion`.
"""
from __future__ import annotations

import math
import sys
from pathlib import Path

BASE = Path(__file__).resolve().parent
sys.path.insert(0, str(BASE))
import build_candidates as bc  # noqa: E402
from build_candidates import Clip, L, bm, tremble  # noqa: E402

ROOT = bc.ROOT
JAVA = ROOT / "src/client/java/com/aquamarine/client/entity/kraken"
TEXTURES = ROOT / "src/main/resources/assets/aquamarine/textures/entity/kraken"


# ================================================================ os 10 clipes que faltavam

def clip_emerge(r):
    c = Clip(2.5)
    c.pos(r.body, (0, (0, -40, 0)), (1.4, (0, 2, 0)), (1.8, (0, -1, 0)), (2.5, (0, 0, 0)))
    c.rot(r.body, (0, (30, 0, 0)), (1.4, (-8, 0, 0)), (2.5, (0, 0, 0)))
    for arm in r.arms:
        for j, seg in enumerate(arm):
            c.rot(seg, (0, (-30, 0, 0)), (1.2, (-10, 0, 0)), (1.7 + 0.03 * j, (30, 0, 0)), (2.5, (0, 0, 0)))
    for m in r.breathe:
        c.scale(m, (0, (0.8, 1.2, 0.8)), (1.5, (1.15, 0.95, 1.15)), (2.5, 1))
    return c.finish()


def clip_stunned(r):
    T = 1.4
    c = Clip(T, True)
    c.rot(r.body, (0, (6, 0, -10)), (0.7 * 1, (10, 0, 10)), (T, (6, 0, -10)))
    c.rot(r.jaw, (0, (18, 0, 0)), (T, (18, 0, 0)))
    for arm in r.arms:
        for j, seg in enumerate(arm):
            c.rot(seg, (0, (18 + 2 * j, 0, 0)), (0.7, (26 + 2 * j, 0, 0)), (T, (18 + 2 * j, 0, 0)))
    for lid, off in r.lids:
        c.pos(lid, (0, (off[0] * 0.6, 0, 0)), (T, (off[0] * 0.6, 0, 0)))
    for fin, side in r.fins:
        c.rot(fin, (0, (0, -side * 20, 0)), (T, (0, -side * 20, 0)))
    return c.finish()


def clip_phase_change(r):
    c = Clip(3.0)
    c.rot(r.body, (0, (0, 0, 0)), (0.6, (-16, 0, 0)), (1.6, (-16, 180, 0), L), (2.4, (-8, 360, 0), L), (3.0, (0, 360, 0)))
    c.rot(r.jaw, (0, (0, 0, 0)), (0.6, (40, 0, 0)), *tremble(0.7, 2.4, (40, 0, 0), (4, 0, 0), 0.08), (3.0, (0, 0, 0)))
    for arm in r.arms:
        for j, seg in enumerate(arm):
            c.rot(seg, (0, (0, 0, 0)), (0.6, (44 + 3 * j, 0, 0)), *tremble(0.7, 2.4, (44 + 3 * j, 0, 0), (3, 0, 3), 0.07), (3.0, (0, 0, 0)))
    for m in r.breathe:
        c.scale(m, (0, 1), (0.6, (1.2, 1.1, 1.2)), (1.4, (0.9, 1.05, 0.9)), (2.2, (1.2, 1.1, 1.2)), (3.0, 1))
    return c.finish()


def clip_ink(r):
    c = Clip(1.8)
    c.pos(r.body, (0, (0, 0, 0)), (0.5, (0, 1, -1)), (0.75, (0, 0, 4), L), (1.8, (0, 0, 0)))
    for m in r.breathe:
        c.scale(m, (0, 1), (0.5, (1.15, 1.05, 1.15)), (0.75, (0.78, 1.12, 0.78), L), (1.2, (1.04, 1, 1.04)), (1.8, 1))
    c.rot(r.jaw, (0, (0, 0, 0)), (0.6, (30, 0, 0)), (1.1, (30, 0, 0)), (1.8, (0, 0, 0)))
    for arm in r.arms:
        for seg in arm:
            c.rot(seg, (0, (0, 0, 0)), (0.75, (-18, 0, 0), L), (1.8, (0, 0, 0)))
    return c.finish()


def clip_whirlpool(r):
    c = Clip(3.0)
    c.rot(r.body, (0, (0, 0, 0)), (0.5, (0, 30, 0)), (1.5, (0, 270, 0), L), (2.5, (0, 630, 0), L), (3.0, (0, 720, 0)))
    for arm in r.arms:
        for j, seg in enumerate(arm):
            c.rot(seg, (0, (0, 0, 0)), (0.5, (50 + 4 * j, 0, 0)), (2.5, (50 + 4 * j, 0, 0)), (3.0, (0, 0, 0)))
    for feeder in r.feeders:
        for seg in feeder:
            c.rot(seg, (0, (0, 0, 0)), (0.5, (40, 0, 0)), (2.5, (40, 0, 0)), (3.0, (0, 0, 0)))
    return c.finish()


def clip_spawn_young(r):
    c = Clip(2.4)
    c.rot(r.body, (0, (0, 0, 0)), (0.4, (12, 0, 0)), (2.0, (12, 0, 0)), (2.4, (0, 0, 0)))
    for m in r.breathe:
        c.scale(m, (0, 1), *[(0.4 + 0.5 * k + d, s) for k in range(3) for d, s in ((0, (1.18, 0.92, 1.18)), (0.25, (0.9, 1.06, 0.9)))],
                (2.4, 1))
    for arm in r.arms:
        for seg in arm:
            c.rot(seg, (0, (0, 0, 0)), (0.4, (20, 0, 0)), (2.0, (20, 0, 0)), (2.4, (0, 0, 0)))
    return c.finish()


def clip_water_jet(r):
    c = Clip(2.2)
    c.rot(r.body, (0, (0, 0, 0)), (0.7, (-10, 0, 0)), (1.0, (6, 0, 0), L), (1.8, (6, 0, 0)), (2.2, (0, 0, 0)))
    c.pos(r.body, (0, (0, 0, 0)), (0.7, (0, 0, -1.5)), (1.0, (0, 0, 3), L), (1.8, (0, 0, 2)), (2.2, (0, 0, 0)))
    c.rot(r.jaw, (0, (0, 0, 0)), (0.7, (10, 0, 0)), (0.9, (45, 0, 0), L), *tremble(1.0, 1.8, (45, 0, 0), (3, 0, 0), 0.06), (2.2, (0, 0, 0)))
    for m in r.breathe:
        c.scale(m, (0, 1), (0.7, (1.2, 1.08, 1.2)), (1.0, (0.8, 1.1, 0.8), L), (1.8, (0.85, 1.05, 0.85)), (2.2, 1))
    for arm in r.arms:
        for seg in arm:
            c.rot(seg, (0, (0, 0, 0)), (0.9, (-14, 0, 0)), (1.8, (-14, 0, 0)), (2.2, (0, 0, 0)))
    return c.finish()


def clip_bolts(r):
    c = Clip(2.0)
    for arm in r.arms:
        for j, seg in enumerate(arm):
            c.rot(seg, (0, (0, 0, 0)), (0.5, (-26 - 3 * j, 0, 0)), *tremble(0.55, 1.6, (-26 - 3 * j, 0, 0), (3, 0, 3), 0.05), (2.0, (0, 0, 0)))
    c.rot(r.body, (0, (0, 0, 0)), *tremble(0.5, 1.6, (-6, 0, 0), (1.5, 1.5, 1.5), 0.05), (2.0, (0, 0, 0)))
    for fin, side in r.fins:
        c.rot(fin, (0, (0, 0, 0)), (0.5, (0, side * 30, 0)), (1.6, (0, side * 30, 0)), (2.0, (0, 0, 0)))
    return c.finish()


def clip_ambush(r):
    c = Clip(1.8)
    c.pos(r.body, (0, (0, 0, 0)), (0.6, (0, -8, 0)), (0.85, (0, 4, -3), L), (1.2, (0, 1, -1)), (1.8, (0, 0, 0)))
    for arm in r.arms:
        for j, seg in enumerate(arm):
            c.rot(seg, (0, (0, 0, 0)), (0.6, (40, 0, 0)), (0.85 + 0.02 * j, (-30, 0, 0), L), (1.3, (-8, 0, 0)), (1.8, (0, 0, 0)))
    c.rot(r.jaw, (0, (0, 0, 0)), (0.6, (30, 0, 0)), (0.85, (0, 0, 0), L), (1.8, (0, 0, 0)))
    return c.finish()


def clip_double_grab(r):
    c = bc.clip_grab(r)
    c2 = Clip(c.length)
    c2.tracks = dict(c.tracks)
    for k in (0, len(r.arms) - 1):
        for j, seg in enumerate(r.arms[k]):
            c2.tracks[(seg, "rotation")] = {}
            c2.rot(seg, (0, (0, 0, 0)), (0.4, (20, 0, 0)), (0.6 + 0.015 * j, (-12, 0, 0), L), (1.3, (16, 0, 0)), (2.0, (0, 0, 0)))
    return c2.finish()


EXTRA = {"emerge": clip_emerge, "stunned": clip_stunned, "phaseChange": clip_phase_change, "ink": clip_ink,
         "whirlpool": clip_whirlpool, "spawnYoung": clip_spawn_young, "waterJet": clip_water_jet, "bolts": clip_bolts,
         "ambush": clip_ambush, "doubleGrab": clip_double_grab}
ORDER = ("idle", "swim", "emerge", "stunned", "phaseChange", "death", "slam", "grab", "ink", "whirlpool", "spawnYoung",
         "waterJet", "roar", "bolts", "ambush", "doubleGrab")


# ================================================================ posição na caixa da entidade

def rest_extent_y(cr):
    """Menor e maior y (px do modelo, y para baixo) dos cantos dos cubos na pose de repouso."""
    def rot(v, deg):
        x, y, z = v
        rx, ry, rz = (math.radians(a) for a in deg)
        y, z = y * math.cos(rx) - z * math.sin(rx), y * math.sin(rx) + z * math.cos(rx)
        x, z = x * math.cos(ry) + z * math.sin(ry), -x * math.sin(ry) + z * math.cos(ry)
        x, y = x * math.cos(rz) - y * math.sin(rz), x * math.sin(rz) + y * math.cos(rz)
        return x, y, z

    def to_root(name, v):
        p = cr.parts[name]
        v = rot(v, p.rot)
        v = tuple(v[i] + p.pivot[i] for i in range(3))
        return to_root(p.parent, v) if p.parent else v

    ys = []
    for c in cr.cubes():
        for dx in (0, c.size[0]):
            for dy in (0, c.size[1]):
                for dz in (0, c.size[2]):
                    ys.append(to_root(c.part, (c.origin[0] + dx, c.origin[1] + dy, c.origin[2] + dz))[1])
    return min(ys), max(ys)


# ================================================================ Java

def path(cr, name):
    out = []
    while name:
        out.append(name)
        name = cr.parts[name].parent
    return ", ".join(f"\"{n}\"" for n in reversed(out))


def game_java(cr, rig, version):
    cls = f"KrakenV3{version[-1]}"
    cr.java_class, cr.key = cls, f"kraken_{version}"
    model = bm.model_java(cr, "kraken").replace(f"{cls}RenderState", "KrakenRenderState")
    model = model.replace("GERADO por tools/models/spawnlings/build_model.py", "GERADO por tools/models/kraken_v3/build_game.py")
    arms = ",\n\t\t\t".join("{" + ", ".join(f"KrakenV3Motion.part(root, {path(cr, s)})" for s in arm) + "}" for arm in rig.arms)
    breathe = ", ".join(f"KrakenV3Motion.part(root, {path(cr, m)})" for m in (rig.breathe or [rig.head]))
    model = model.replace("\t\tsuper(root);\n", f"""\t\tsuper(root);
\t\tthis.motion = new KrakenV3Motion.Parts(KrakenV3Motion.part(root, {path(cr, rig.head)}),
\t\t\tnew ModelPart[] {{{breathe}}},
\t\t\tnew ModelPart[][] {{
\t\t\t{arms}}});
""", 1)
    model = model.replace("\tpublic static final ModelLayerLocation LAYER", "\tprivate final KrakenV3Motion.Parts motion;\n\tpublic static final ModelLayerLocation LAYER", 1)
    lines = model.split("\n")
    end = max(i for i, l in enumerate(lines) if l.startswith("\t\tthis.") and ".apply(state." in l)
    lines.insert(end + 1, "\t\tKrakenV3Motion.alive(state, this.motion);")
    model = "\n".join(lines)
    anims = bm.animations_java(cr, "kraken").replace("GERADO por tools/models/spawnlings/build_model.py",
                                                       "GERADO por tools/models/kraken_v3/build_game.py")
    (JAVA / f"{cls}Model.java").write_text(model, encoding="utf-8", newline="\n")
    (JAVA / f"{cls}Animations.java").write_text(anims, encoding="utf-8", newline="\n")
    return cls


def build(version):
    factory, _ = bc.CANDIDATES[version]
    cr, rig = factory()
    clips = {**{n: fn(rig) for n, fn in bc.CLIPS.items()}, **{n: fn(rig) for n, fn in EXTRA.items()}}
    cr.clips = {n: clips[n] for n in ORDER}
    for size in ((256, 256), (256, 384), (256, 512)):
        cr.tex_size = size
        try:
            bm.pack(cr)
            break
        except SystemExit:
            continue
    img, glow = bm.paint(cr)
    TEXTURES.mkdir(parents=True, exist_ok=True)
    img.save(TEXTURES / f"{version}.png", optimize=True)
    glow.save(TEXTURES / f"{version}_glow.png", optimize=True)
    lo, hi = rest_extent_y(cr)
    cls = game_java(cr, rig, version)
    # Ponta mais baixa nos pés da entidade: y_mundo = 12 - DROP - y_px / 2 (escala 8; ver KrakenRenderer).
    drop = 12.008 - hi / 2
    if REGISTER:
        sys.path.insert(0, str(ROOT / "tools/atlas"))
        from register_version import register
        snapshot = bm.rig_json(cr) | {"id": "kraken", "version": f"{version}-jogo", "texture": bc.data_uri(img), "glow": bc.data_uri(glow),
                                       "uv": bc.data_uri(bm.uv_guide(cr)), "notes": cr.description + " No jogo, com os 16 clipes da luta."}
        register(snapshot, LABELS[version])
    print(f"{version}: {cls}, {len(cr.parts)} ossos, {len(cr.clips)} clipes, y {lo:.1f}..{hi:.1f} px, queda {drop:.2f}, altura "
          f"{(hi - lo) / 2:.1f} blocos")
    return drop


LABELS = {"v3a": "No jogo · v3a Lula-colosso (aquamarine:kraken), 16 clipes",
          "v3b": "No jogo · v3b Vampiro-abissal (aquamarine:kraken_vampire), 16 clipes",
          "v3c": "No jogo · v3c Amonita-colosso (aquamarine:kraken_ammonite), 16 clipes"}
REGISTER = "--register" in sys.argv

if __name__ == "__main__":
    for v in bc.CANDIDATES:
        build(v)
