"""Áudio v1, luta do Kraken: os efeitos (docs/09-audio.md, "Efeitos"). Tudo sintetizado, mono e
posicional, com semente fixa; também escreve assets/aquamarine/sounds.json (efeitos e música).

Uso (na raiz do repositório):
    tools/audio/.venv/Scripts/python tools/audio/kraken_sfx.py [evento ...]   (sem nomes: todos)
"""
import json
import os
import sys

import numpy as np

import dsp
from dsp import n_of, time
from master import REPO, export

SCRIPT = "tools/audio/kraken_sfx.py"


# ================================================================ motores

def voice(dur, f0_pts, env_pts, vowels, r, scale=0.42, scream=0.5, rough=0.55, grit=0.12, churn=0.4, sub=0.6):
    """Voz do Kraken (a do rugido do lote A0, com parâmetros): trato enorme, grito nos médios e água."""
    n = n_of(dur)
    f0 = dsp.curve(n, f0_pts, "exp") * (1 + 0.025 * dsp.smooth_noise(n, 28, r))
    rough_mod = 1 + rough * dsp.osc(dsp.curve(n, [(0, 22), (dur, 30)]), n, "tri")
    src = (dsp.osc(f0, n, "saw") + 0.5 * dsp.osc(f0 / 2, n, "saw", phase0=0.3) + 0.2 * dsp.osc(f0 * 1.5, n, "saw")) * rough_mod
    env = dsp.curve(n, env_pts)
    out = dsp.saturate(dsp.morph_formants(src, vowels, scale=scale) * 2.2, 2.5) * env
    out += dsp.morph_formants(dsp.pink(n, r), vowels, scale=scale * 1.2) * env * 0.35
    out += (0.8 * dsp.osc(f0 / 2, n) + 0.3 * dsp.osc(31.0, n)) * env * sub
    if scream:
        sc = dsp.osc(f0 * 4, n, "saw") * (1 + 0.6 * dsp.osc(26, n, "tri"))
        sc = dsp.morph_formants(sc, vowels, scale=0.8)
        out += dsp.bp(dsp.saturate(sc * 2.0, 2.0), 350, 3400) * env ** 1.3 * scream
    if grit:
        out += dsp.bp(dsp.fm(f0 * 3, 1.41, 3.0, n), 600, 2600) * env * grit
    if churn:
        out += dsp.bubbles(n, r, rate_per_s=80, radius_mm=(6, 22)) * env * churn
    out = dsp.hp(out, 32, 2)
    out = dsp.eq(dsp.eq(out, "peak", 130, 1.0, -4.0), "peak", 1300, 0.9, 3.0)
    return dsp.compress(out, -16, 3.0, 0.004, 0.2)


def splash(n, r, size=1.0, start=0.0):
    """Explosão de água: estalo molhado, massa de água, borrifo e gotas caindo."""
    out = np.zeros(n)
    s0 = n_of(start)
    m = n - s0
    slap = dsp.bp(dsp.white(m, r), 500, 4500) * dsp.decay(m, 0.018 * size, 0.0005)
    mass = dsp.lp(dsp.pink(m, r), 2600, 2) * dsp.decay(m, 0.35 * size, 0.004)
    spray = dsp.hp(dsp.white(m, r), 2500) * dsp.decay(m, 0.6 * size, 0.01) * 0.35
    drops = dsp.bubbles(m, r, rate_per_s=160 * size, radius_mm=(1.0, 5.0)) * dsp.curve(m, [(0, 0), (0.1, 1), (m / dsp.SR, 0)])
    out[s0:] += 1.0 * slap + 0.9 * mass + spray + 0.5 * drops
    return out


def thump(n, r, f_hi=90, f_lo=30, tau=0.25, start=0.0):
    out = np.zeros(n)
    s0 = n_of(start)
    m = n - s0
    out[s0:] = dsp.osc(dsp.curve(m, [(0, f_hi), (min(0.3, m / dsp.SR), f_lo)], "exp"), m) * dsp.decay(m, tau, 0.002)
    return out


def whoosh(n, r, lo, hi, env_pts, q=1.2):
    f = dsp.curve(n, [(0, lo), (n / dsp.SR, hi)], "exp")
    return dsp.sweep(dsp.pink(n, r), "bp", f, q) * dsp.curve(n, env_pts)


def squelch(n, r, rate=14.0, lo=200, hi=1600):
    """Viscoso: estalos úmidos com ressonâncias que escorregam."""
    t = time(n)
    f = lo + (hi - lo) * (0.5 + 0.5 * dsp.smooth_noise(n, rate, r))
    x = dsp.sweep(dsp.white(n, r), "bp", f, 6.0)
    pops = dsp.bubbles(n, r, rate_per_s=rate * 3, radius_mm=(3, 12)) * 0.6
    return (x * (0.6 + 0.4 * np.abs(dsp.smooth_noise(n, rate, r))) + pops) * np.sin(np.pi * np.clip(t / (n / dsp.SR), 0, 1)) ** 0.5


def crackle(n, r, density=60.0, lo=1500, hi=9000):
    clicks = (r.random(n) < density / dsp.SR) * r.uniform(-1, 1, n)
    return dsp.bp(signal_ring(clicks, r), lo, hi) * 3


def signal_ring(x, r):
    return dsp.lp(x, 12000, 2)


def thunder(n, r, start=0.0, size=1.0):
    out = np.zeros(n)
    s0 = n_of(start)
    m = n - s0
    crack = dsp.hp(dsp.white(m, r), 1500) * dsp.decay(m, 0.03, 0.0003) * 1.2
    roll = dsp.lp(dsp.brown(m, r), 600, 2) * dsp.decay(m, 0.9 * size, 0.02) * (0.6 + 0.4 * np.abs(dsp.smooth_noise(m, 6, r)))
    out[s0:] = crack + roll * 1.4
    return out


def glass(n, r, f=420.0, tau=0.5, partials=10, start=0.0, gain=1.0):
    out = np.zeros(n)
    s0 = n_of(start)
    m = n - s0
    ratios = np.sort(r.uniform(1.0, 7.5, partials))
    x = dsp.modal(m, [f * k for k in ratios], [tau * (1.2 - k / 9) for k in ratios], r.uniform(0.3, 1.0, partials), r, strike=0.0004)
    out[s0:] = x * gain
    return out


def shatter(n, r, count=60, span=1.4, start=0.0):
    out = np.zeros(n)
    for _ in range(count):
        t0 = start + span * r.random() ** 1.8
        if t0 >= n / dsp.SR - 0.05:
            continue
        m = n_of(0.25)
        x = dsp.modal(m, [r.uniform(2200, 9000) * k for k in (1, 1.63, 2.41)], [0.05, 0.03, 0.02], [1, 0.5, 0.3], r, strike=0.0002)
        dsp.place(out, x * r.uniform(0.2, 0.7) * (1 - (t0 - start) / (span + 0.3)), n_of(t0))
    return out


def room(x, r, seconds=2.5, wet=0.3, dark=6000):
    ir = dsp.impulse_response(seconds, r, rt60_low=seconds * 0.9, rt60_high=seconds * 0.3, predelay=0.02, dark=dark)
    return dsp.mono_reverb(x, ir, wet=wet)


def done(name, x, seed, dur, description, listen, under=6000):
    x = dsp.underwater(x, under, 0.35)
    x = dsp.fade(dsp.fit(x, n_of(dur)), 0.002, min(0.6, dur * 0.25))
    return export(name, x, "sfx", SCRIPT, seed, description, listen)


# ================================================================ Kraken

def ambient(k, seed):
    r = dsp.rng(seed)
    d = r.uniform(2.6, 3.4)
    lo = r.uniform(30, 38)
    hi = lo * r.uniform(1.25, 1.5)
    x = voice(d, [(0, lo), (d * 0.4, hi), (d, lo * 0.9)], [(0, 0), (0.4, 0.7), (d * 0.6, 0.8), (d, 0)],
              [(0, "u"), (d * 0.5, "o"), (d, "u")], r, scale=0.4, scream=0.12, churn=0.6, sub=0.5)
    x = room(x, r, 3.5, 0.35, 3500)
    return done(f"entity/kraken/ambient{k}", x, seed, d + 1.2, "Kraken: ronco baixo de presença.", "Um gigante respirando no fundo; nada de rugido.")


def hurt(k, seed):
    r = dsp.rng(seed)
    d = r.uniform(0.9, 1.3)
    f = r.uniform(70, 95)
    x = voice(d, [(0, f * 0.6), (0.1, f), (d, f * 0.55)], [(0, 0), (0.05, 1), (d * 0.5, 0.7), (d, 0)],
              [(0, "a"), (d * 0.6, "o"), (d, "u")], r, scale=0.46, scream=0.75, churn=0.3)
    n = len(x)
    x += squelch(n, r, 22) * dsp.decay(n, 0.15, 0.002) * 0.5
    x = room(x, r, 2.5, 0.28, 4500)
    return done(f"entity/kraken/hurt{k}", x, seed, d + 0.9, "Kraken: ferido.", "Dor e raiva curtas; tem que dar vontade de bater de novo.")


def death(seed=2101):
    r = dsp.rng(seed)
    d = 5.6
    x = voice(d, [(0, 50), (0.4, 92), (1.6, 88), (3.0, 60), (4.6, 34), (d, 24)], [(0, 0), (0.15, 1), (2.0, 0.95), (4.0, 0.6), (d, 0)],
              [(0, "o"), (0.5, "a"), (2.4, "a"), (3.8, "o"), (d, "u")], r, scale=0.4, scream=0.8, rough=0.8, churn=0.7)
    n = len(x)
    x += splash(n, r, 2.2, start=4.2) * 0.9
    x += thump(n, r, 70, 24, 0.9, start=4.2) * 1.2
    x += dsp.bubbles(n, r, rate_per_s=120, radius_mm=(8, 30)) * dsp.curve(n, [(0, 0), (3.5, 0.2), (5.0, 1.0), (d, 0.5)]) * 0.6
    x = room(x, r, 5.0, 0.35, 3500)
    return done("entity/kraken/death", x, seed, d + 1.6, "Kraken: morte (lamento que cai e o corpo afundando).",
                "Tragédia de um titã: o lamento quebra, cai e o corpo bate na água.")


def roar2(seed=2102):
    r = dsp.rng(seed)
    d = 4.2
    x = voice(d, [(0, 40), (0.3, 66), (1.4, 82), (2.6, 70), (d, 38)], [(0, 0), (0.1, 0.9), (0.6, 1.0), (2.8, 0.9), (d, 0)],
              [(0, "u"), (0.5, "a"), (2.6, "a"), (d, "o")], r, scale=0.4, scream=0.6, rough=0.65, churn=0.5)
    n = len(x)
    x += thump(n, r, 80, 28, 0.2) * 1.2
    x = room(x, r, 5.0, 0.32, 3500)
    return done("entity/kraken/roar2", x, seed, d + 1.4, "Kraken: rugido (variação).", "Ameaça que cresce no meio; par do rugido do lote A0.")


def emerge(seed=2201):
    r = dsp.rng(seed)
    d = 4.0
    n = n_of(d)
    swell = dsp.lp(dsp.brown(n, r), 300, 2) * dsp.curve(n, [(0, 0), (1.2, 1.0), (1.4, 0.4), (d, 0)]) * 1.2
    x = swell + splash(n, r, 2.5, start=1.25) * 1.3 + thump(n, r, 70, 26, 0.6, start=1.25) * 1.3
    x += dsp.hp(dsp.pink(n, r), 3000) * dsp.curve(n, [(0, 0), (1.3, 0), (1.5, 0.5), (d, 0)]) * 0.4
    x += dsp.bubbles(n, r, rate_per_s=220, radius_mm=(2, 14)) * dsp.curve(n, [(0, 0.3), (1.2, 1), (d, 0.1)]) * 0.5
    x = room(x, r, 3.0, 0.3, 5000)
    return done("entity/kraken/emerge", x, seed, d + 1.0, "Kraken: emerge (o mar sobe e explode).",
                "O mar incha e arrebenta; peso de prédio saindo d'água.", under=9000)


def retreat(seed=2202):
    r = dsp.rng(seed)
    d = 5.0
    n = n_of(d)
    gurgle = dsp.bubbles(n, r, rate_per_s=140, radius_mm=(10, 40)) * dsp.curve(n, [(0, 0), (0.6, 1), (d, 0)])
    rumble = dsp.lp(dsp.brown(n, r), 220, 2) * dsp.curve(n, [(0, 0), (0.8, 1), (d, 0)]) * 1.1
    groan = voice(3.0, [(0, 46), (1.5, 38), (3.0, 26)], [(0, 0), (0.6, 0.6), (3.0, 0)], [(0, "o"), (3.0, "u")], r,
                  scale=0.38, scream=0.1, churn=0.0) * 0.6
    x = gurgle * 0.8 + rumble
    dsp.place(x, groan, n_of(0.8))
    x = room(x, r, 5.0, 0.45, 2500)
    return done("entity/kraken/retreat", x, seed, d + 1.6, "Kraken: recua para as profundezas.", "Afundando devagar, frio e distante.", under=3500)


def slam_warn(k, seed):
    r = dsp.rng(seed)
    d = r.uniform(0.9, 1.2)
    n = n_of(d)
    pour = whoosh(n, r, 300, 2200, [(0, 0), (d * 0.8, 1), (d, 0.3)], 1.0)
    creak = dsp.creak(n, r, base=r.uniform(90, 130), rate=(10, 40)) * 0.6
    drip = dsp.bubbles(n, r, rate_per_s=90, radius_mm=(1, 6)) * 0.4
    x = pour + creak + drip + thump(n, r, 60, 40, 0.5) * dsp.curve(n, [(0, 0), (d, 1)]) * 0.4
    x = room(x, r, 2.0, 0.25)
    return done(f"entity/kraken/slam_warn{k}", x, seed, d + 0.6, "Kraken: tentáculo se erguendo (aviso da Pancada).",
                "Água escorrendo de algo enorme subindo: dá tempo de fugir.")


def slam(k, seed):
    r = dsp.rng(seed)
    d = 2.2
    n = n_of(d)
    x = thump(n, r, r.uniform(85, 100), 28, 0.3) * 1.6 + splash(n, r, 1.6) * 1.2
    x += dsp.saturate(dsp.lp(dsp.white(n, r), 900, 2) * dsp.decay(n, 0.05, 0.0008) * 3, 2.0)
    debris = np.zeros(n)
    for _ in range(18):
        t0 = 0.05 + 0.5 * r.random() ** 1.5
        m = n_of(0.08)
        dsp.place(debris, dsp.bp(dsp.white(m, r), 600, 3000) * dsp.decay(m, 0.012, 0.0003) * r.uniform(0.2, 0.6), n_of(t0))
    x += debris
    x = room(x, r, 2.4, 0.25, 5000)
    return done(f"entity/kraken/slam{k}", x, seed, d + 0.8, "Kraken: Pancada do tentáculo.", "Golpe que treme o chão e explode a água.", under=8000)


def grab_warn(k, seed):
    r = dsp.rng(seed)
    d = r.uniform(0.8, 1.0)
    f = r.uniform(320, 420)
    x = voice(d, [(0, f * 0.8), (0.12, f * 1.4), (d, f * 1.1)], [(0, 0), (0.05, 1), (d * 0.7, 0.8), (d, 0)],
              [(0, "e"), (d * 0.4, "i"), (d, "e")], r, scale=0.9, scream=1.0, rough=0.4, sub=0.0, churn=0.1, grit=0.2)
    x = room(x, r, 1.8, 0.25, 8000)
    return done(f"entity/kraken/grab_warn{k}", x, seed, d + 0.6, "Kraken: guincho agudo (aviso do agarrão).",
                "Agudo e arrepiante: avisa que um tentáculo vai agarrar.", under=11000)


def grab(k, seed):
    r = dsp.rng(seed)
    d = 1.4
    n = n_of(d)
    x = squelch(n, r, 9, 180, 1200) + dsp.creak(n, r, base=r.uniform(100, 140), rate=(25, 70)) * 0.7
    x += thump(n, r, 70, 45, 0.15) * 0.8
    x = room(x, r, 1.5, 0.2)
    return done(f"entity/kraken/grab{k}", x, seed, d + 0.4, "Kraken: tentáculo agarra e aperta.", "Aperto viscoso e músculo rangendo.")


def ink(k, seed):
    r = dsp.rng(seed)
    d = 1.8
    n = n_of(d)
    blurp = dsp.bubbles(n, r, rate_per_s=120, radius_mm=(10, 35)) * dsp.decay(n, 0.4, 0.01) * 1.2
    cloud = dsp.bp(dsp.pink(n, r), 600, 4500) * dsp.curve(n, [(0, 0), (0.08, 1), (d, 0)]) * 0.6
    x = blurp + cloud + thump(n, r, 55, 35, 0.25) * 0.7
    x = room(x, r, 2.0, 0.3, 4000)
    return done(f"entity/kraken/ink{k}", x, seed, d + 0.5, "Kraken: nuvem de tinta.", "Jorro grosso que escurece a água.", under=5000)


def whirlpool(seed=2401):
    r = dsp.rng(seed)
    d = 4.6
    n = n_of(d)
    t = time(n)
    swirl = np.zeros(n)
    for k in range(3):
        f = 500 + 350 * np.sin(2 * np.pi * (0.9 + 0.3 * k) * t * (1 + t / d) + k * 2.1)
        swirl += dsp.sweep(dsp.pink(n, r), "bp", f, 2.0) * (0.6 + 0.4 * np.sin(2 * np.pi * (1.3 + 0.2 * k) * t + k))
    whistle = dsp.osc(dsp.curve(n, [(0, 180), (d, 420)], "exp"), n) * 0.06
    rumble = dsp.lp(dsp.brown(n, r), 180, 2) * 0.8
    env = dsp.curve(n, [(0, 0), (1.5, 0.7), (3.8, 1.0), (d, 0)])
    x = (swirl * 0.7 + whistle + rumble) * env + dsp.bubbles(n, r, rate_per_s=200, radius_mm=(2, 12)) * env * 0.5
    x = room(x, r, 3.0, 0.35, 4500)
    return done("entity/kraken/whirlpool", x, seed, d + 0.8, "Kraken: redemoinho se formando.", "Água girando e puxando, cada vez mais forte.", under=5000)


def spawn_young(k, seed):
    r = dsp.rng(seed)
    d = 1.6
    n = n_of(d)
    x = np.zeros(n)
    for j in range(4):
        m = n_of(0.35)
        dsp.place(x, squelch(m, r, 18, 250, 1800) * r.uniform(0.6, 1.0), n_of(0.05 + j * 0.3 + r.uniform(0, 0.08)))
    x = room(x, r, 1.6, 0.25)
    return done(f"entity/kraken/spawn_young{k}", x, seed, d + 0.4, "Kraken: bota os ovos das Crias.", "Bolsas viscosas caindo; nojento no bom sentido.")


def water_jet_charge(seed=2501):
    r = dsp.rng(seed)
    d = 1.4
    n = n_of(d)
    suck = whoosh(n, r, 2400, 400, [(0, 0.1), (d * 0.9, 1), (d, 0.6)], 1.4)
    gurgle = dsp.bubbles(n, r, rate_per_s=200, radius_mm=(4, 16)) * dsp.curve(n, [(0, 0.2), (d, 1)])
    tone = dsp.osc(dsp.curve(n, [(0, 60), (d, 140)], "exp"), n) * dsp.curve(n, [(0, 0), (d, 0.4)])
    x = suck + gurgle * 0.6 + tone
    x = room(x, r, 1.6, 0.2)
    return done("entity/kraken/water_jet_charge", x, seed, d + 0.3, "Kraken: puxa água (aviso do Jato e da Tinta).",
                "Sucção que cresce: alguma coisa vai sair com força.")


def water_jet(k, seed):
    r = dsp.rng(seed)
    d = 1.6
    n = n_of(d)
    hiss = dsp.hp(dsp.white(n, r), 1200) * dsp.curve(n, [(0, 0), (0.03, 1), (0.9, 0.7), (d, 0)])
    core = dsp.bp(dsp.pink(n, r), 300, 2000) * dsp.curve(n, [(0, 0), (0.02, 1), (d, 0)]) * 0.8
    x = hiss * 0.7 + core + thump(n, r, 90, 40, 0.15) + dsp.bubbles(n, r, rate_per_s=300, radius_mm=(1, 6)) * 0.4
    x = room(x, r, 1.8, 0.2, 7000)
    return done(f"entity/kraken/water_jet{k}", x, seed, d + 0.4, "Kraken: Jato d'água.", "Pressão de mangueira gigante.", under=9000)


def roar_blast(seed=2601):
    r = dsp.rng(seed)
    d = 3.4
    n = n_of(d)
    boom = thump(n, r, 70, 22, 1.0) * 1.8
    blast = dsp.lp(dsp.pink(n, r), 1800, 2) * dsp.decay(n, 0.5, 0.003) * 1.4
    rumble = dsp.lp(dsp.brown(n, r), 260, 2) * dsp.decay(n, 1.5, 0.02)
    x = boom + blast + rumble + splash(n, r, 1.8) * 0.6
    x = room(x, r, 3.5, 0.3, 4000)
    return done("entity/kraken/roar_blast", x, seed, d + 0.8, "Kraken: onda de choque do Rugido Abissal.", "Deslocamento de ar e água; o peito sente.")


def bolts_charge(seed=2701):
    r = dsp.rng(seed)
    d = 1.9
    n = n_of(d)
    density = dsp.curve(n, [(0, 20), (d, 260)], "exp")
    clicks = (r.random(n) < density / dsp.SR) * r.uniform(-1, 1, n)
    crack = dsp.bp(clicks, 1500, 9000) * 3
    buzz = dsp.bp(dsp.fm(dsp.curve(n, [(0, 90), (d, 240)], "exp"), 2.0, 4.0, n), 200, 3000) * dsp.curve(n, [(0, 0), (d, 0.6)])
    x = crack + buzz
    x = room(x, r, 1.6, 0.2, 9000)
    return done("entity/kraken/bolts_charge", x, seed, d + 0.3, "Kraken: raios se acumulando (aviso dos Raios Guiados).",
                "Estalos que aceleram: mergulhe!", under=12000)


def bolt(k, seed):
    r = dsp.rng(seed)
    d = 2.8
    n = n_of(d)
    zap = dsp.bp(dsp.fm(dsp.curve(n, [(0, 2400), (0.1, 300)], "exp"), 2.3, 5.0, n), 400, 9000) * dsp.decay(n, 0.08, 0.0004)
    x = zap * 0.8 + thunder(n, r, 0.02, 1.2) + crackle(n, r, 120) * dsp.decay(n, 0.3, 0.001) * 0.5
    x = room(x, r, 3.0, 0.3, 7000)
    return done(f"entity/kraken/bolt{k}", x, seed, d + 0.5, "Kraken: raio guiado caindo.", "Estalo seco e trovão rolando.", under=12000)


def ambush_warn(seed=2801):
    r = dsp.rng(seed)
    d = 1.9
    n = n_of(d)
    rumble = dsp.lp(dsp.brown(n, r), 160, 2) * dsp.curve(n, [(0, 0), (d, 1)]) * 1.3
    rise = dsp.bubbles(n, r, rate_per_s=dsp.curve(n, [(0, 30), (d, 300)]).mean(), radius_mm=(8, 30)) * dsp.curve(n, [(0, 0.2), (d, 1)])
    creak = dsp.creak(n, r, base=70, rate=(10, 30)) * 0.5
    x = rumble + rise * 0.7 + creak
    x = room(x, r, 2.0, 0.3, 3000)
    return done("entity/kraken/ambush_warn", x, seed, d + 0.3, "Kraken: algo sobe do fundo (aviso da Emboscada).",
                "Ronco embaixo dos pés, bolhas subindo: saia de cima!", under=4000)


def ambush(k, seed):
    r = dsp.rng(seed)
    d = 2.4
    n = n_of(d)
    x = thump(n, r, 80, 26, 0.4) * 1.5 + splash(n, r, 1.8) * 1.1
    gravel = np.zeros(n)
    for _ in range(70):
        t0 = 0.05 + 1.2 * r.random() ** 1.6
        m = n_of(0.03)
        dsp.place(gravel, dsp.bp(dsp.white(m, r), 1500, 6000) * dsp.decay(m, 0.006, 0.0002) * r.uniform(0.1, 0.5), n_of(t0))
    x += gravel + dsp.bubbles(n, r, rate_per_s=250, radius_mm=(3, 20)) * dsp.decay(n, 0.8, 0.01) * 0.6
    x = room(x, r, 2.4, 0.25, 5000)
    return done(f"entity/kraken/ambush{k}", x, seed, d + 0.6, "Kraken: erupção do fundo (Emboscada).", "O fundo do mar explode para cima, com cascalho.", under=8000)


def shield_crack(k, seed):
    r = dsp.rng(seed)
    d = 1.4
    n = n_of(d)
    snap = dsp.hp(dsp.white(n, r), 2500) * dsp.decay(n, 0.004, 0.0001) * 1.5
    body = glass(n, r, r.uniform(300, 420), 0.5, 12) * 0.7
    thud = thump(n, r, 160, 90, 0.08) * 0.6
    x = snap + body + thud + shatter(n, r, 8, 0.3) * 0.5
    x = room(x, r, 2.2, 0.3, 9000)
    return done(f"entity/kraken/shield_crack{k}", x, seed, d + 0.6, "Kraken: a Carapaça racha.", "Vidro grosso trincando: progresso!", under=13000)


def shield_break(seed=2901):
    r = dsp.rng(seed)
    d = 3.8
    n = n_of(d)
    boom = thump(n, r, 110, 30, 0.5) * 1.4
    x = boom + glass(n, r, 260, 1.2, 16) * 0.8 + shatter(n, r, 90, 1.8) * 0.9
    x += dsp.hp(dsp.white(n, r), 2000) * dsp.decay(n, 0.12, 0.0003)
    scream = voice(1.6, [(0, 90), (0.2, 140), (1.6, 70)], [(0, 0), (0.05, 1), (1.6, 0)], [(0, "a"), (1.6, "o")], r,
                   scale=0.5, scream=1.0, churn=0.1) * 0.6
    dsp.place(x, scream, n_of(0.12))
    x = room(x, r, 3.0, 0.32, 9000)
    return done("entity/kraken/shield_break", x, seed, d + 0.8, "Kraken: a Carapaça se parte.",
                "Explosão de vidro grave e o grito: a janela de dano abriu.", under=13000)


def shield_regen(seed=2902):
    r = dsp.rng(seed)
    d = 1.8
    n = n_of(d)
    t = time(n)
    x = np.zeros(n)
    for k in range(9):
        f = 600 * (1.5 ** (k * 0.5)) * r.uniform(0.98, 1.02)
        x += np.sin(2 * np.pi * f * t * (1 + 0.04 * t)) * dsp.curve(n, [(0, 0), (0.2 + k * 0.12, 0), (0.4 + k * 0.12, 1), (d, 0)]) / (1 + k * 0.3)
    x *= 0.35
    x += dsp.hp(dsp.white(n, r), 6000) * dsp.curve(n, [(0, 0), (1.0, 0.15), (d, 0)])
    x = room(x, r, 2.5, 0.4, 10000)
    return done("entity/kraken/shield_regen", x, seed, d + 0.8, "Kraken: a Carapaça se refaz.", "Cristal crescendo, sobe e brilha.", under=14000)


# ================================================================ tentáculos, Crias e ovos

def tentacle_hurt(k, seed):
    r = dsp.rng(seed)
    d = 0.55
    n = n_of(d)
    x = squelch(n, r, 30, 300, 2000) * 0.9 + thump(n, r, 120, 70, 0.06) + dsp.bp(dsp.white(n, r), 800, 5000) * dsp.decay(n, 0.02, 0.0003) * 0.8
    x = room(x, r, 1.2, 0.18)
    return done(f"entity/kraken_tentacle/hurt{k}", x, seed, d + 0.3, "Tentáculo ferido.", "Acerto molhado e elástico.")


def tentacle_death(seed=3101):
    r = dsp.rng(seed)
    d = 1.6
    n = n_of(d)
    slice_ = whoosh(n, r, 5000, 1500, [(0, 1), (0.12, 0.6), (0.3, 0)], 2.0) * 1.2
    tear = squelch(n, r, 20, 200, 1400) * dsp.curve(n, [(0, 0), (0.08, 1), (0.6, 0.3), (d, 0)])
    x = slice_ + tear + splash(n, r, 0.8, start=0.55) * 0.9 + thump(n, r, 80, 40, 0.2, start=0.55)
    x = room(x, r, 1.8, 0.22, 6000)
    return done("entity/kraken_tentacle/death", x, seed, d + 0.4, "Tentáculo cortado.", "Corte limpo, rasgo molhado e o pedaço caindo n'água.", under=9000)


def young_ambient(k, seed):
    r = dsp.rng(seed)
    d = 0.7
    n = n_of(d)
    t = time(n)
    chirps = np.zeros(n)
    for j in range(int(r.integers(3, 6))):
        m = n_of(0.07)
        f = dsp.curve(m, [(0, r.uniform(900, 1300)), (0.07, r.uniform(1500, 2200))], "exp")
        c = dsp.formants(dsp.osc(f, m, "saw"), "i", 1.6) * dsp.adsr(m, 0.005, 0.03, 0.5, 0.02)
        dsp.place(chirps, c, n_of(0.05 + j * 0.12 + r.uniform(0, 0.03)))
    x = chirps + dsp.creak(n, r, base=600, rate=(60, 120)) * 0.2
    x = room(x, r, 1.0, 0.2, 10000)
    return done(f"entity/young_kraken/ambient{k}", x, seed, d + 0.3, "Cria do Kraken: chiado.", "Bichinho nojento e esperto.", under=12000)


def young_hurt(k, seed):
    r = dsp.rng(seed)
    d = 0.35
    n = n_of(d)
    f = dsp.curve(n, [(0, 1400), (0.08, 2200), (d, 900)], "exp")
    x = dsp.formants(dsp.osc(f, n, "saw"), "e", 1.5) * dsp.adsr(n, 0.004, 0.08, 0.6, 0.1) + squelch(n, r, 30) * 0.3
    x = room(x, r, 0.8, 0.15, 10000)
    return done(f"entity/young_kraken/hurt{k}", x, seed, d + 0.2, "Cria do Kraken: ferida.", "Guinchinho agudo.", under=12000)


def young_death(seed=3201):
    r = dsp.rng(seed)
    d = 0.8
    n = n_of(d)
    f = dsp.curve(n, [(0, 1800), (d, 400)], "exp")
    x = dsp.formants(dsp.osc(f, n, "saw"), "e", 1.5) * dsp.adsr(n, 0.004, 0.2, 0.6, 0.3) + squelch(n, r, 25, 200, 1500) * 0.5
    x = room(x, r, 1.0, 0.2, 9000)
    return done("entity/young_kraken/death", x, seed, d + 0.3, "Cria do Kraken: morre.", "Guincho que cai e um esmagado molhado.", under=12000)


def egg_hatch(seed=3301):
    r = dsp.rng(seed)
    d = 0.8
    n = n_of(d)
    crack = shatter(n, r, 10, 0.15) * 0.6 + dsp.hp(dsp.white(n, r), 2000) * dsp.decay(n, 0.01, 0.0002)
    x = crack + squelch(n, r, 25, 250, 1600) * dsp.curve(n, [(0, 0), (0.1, 1), (d, 0)]) * 0.8
    m = n_of(0.12)
    dsp.place(x, dsp.formants(dsp.osc(dsp.curve(m, [(0, 1500), (0.12, 2300)], "exp"), m, "saw"), "i", 1.6) * 0.4, n_of(0.45))
    x = room(x, r, 1.0, 0.2, 9000)
    return done("entity/kraken_egg/hatch", x, seed, d + 0.3, "Ovo do Kraken choca.", "Casca rachando, gosma e o primeiro chiado.", under=12000)


# ================================================================ tabela: eventos, arquivos e geração

SFX = {
    "entity.kraken.ambient": (["entity/kraken/ambient1", "entity/kraken/ambient2", "entity/kraken/ambient3"],
                              lambda: [ambient(k, 2000 + k) for k in (1, 2, 3)]),
    "entity.kraken.hurt": (["entity/kraken/hurt1", "entity/kraken/hurt2", "entity/kraken/hurt3"],
                           lambda: [hurt(k, 2010 + k) for k in (1, 2, 3)]),
    "entity.kraken.death": (["entity/kraken/death"], lambda: [death()]),
    "entity.kraken.roar": (["entity/kraken/roar1", "entity/kraken/roar2"], lambda: [roar2()]),
    "entity.kraken.emerge": (["entity/kraken/emerge"], lambda: [emerge()]),
    "entity.kraken.retreat": (["entity/kraken/retreat"], lambda: [retreat()]),
    "entity.kraken.slam_warn": (["entity/kraken/slam_warn1", "entity/kraken/slam_warn2"], lambda: [slam_warn(k, 2300 + k) for k in (1, 2)]),
    "entity.kraken.slam": (["entity/kraken/slam1", "entity/kraken/slam2", "entity/kraken/slam3"], lambda: [slam(k, 2310 + k) for k in (1, 2, 3)]),
    "entity.kraken.grab_warn": (["entity/kraken/grab_warn1", "entity/kraken/grab_warn2"], lambda: [grab_warn(k, 2320 + k) for k in (1, 2)]),
    "entity.kraken.grab": (["entity/kraken/grab1", "entity/kraken/grab2"], lambda: [grab(k, 2330 + k) for k in (1, 2)]),
    "entity.kraken.ink": (["entity/kraken/ink1", "entity/kraken/ink2"], lambda: [ink(k, 2340 + k) for k in (1, 2)]),
    "entity.kraken.whirlpool": (["entity/kraken/whirlpool"], lambda: [whirlpool()]),
    "entity.kraken.spawn_young": (["entity/kraken/spawn_young1", "entity/kraken/spawn_young2"], lambda: [spawn_young(k, 2410 + k) for k in (1, 2)]),
    "entity.kraken.water_jet_charge": (["entity/kraken/water_jet_charge"], lambda: [water_jet_charge()]),
    "entity.kraken.water_jet": (["entity/kraken/water_jet1", "entity/kraken/water_jet2"], lambda: [water_jet(k, 2510 + k) for k in (1, 2)]),
    "entity.kraken.roar_blast": (["entity/kraken/roar_blast"], lambda: [roar_blast()]),
    "entity.kraken.bolts_charge": (["entity/kraken/bolts_charge"], lambda: [bolts_charge()]),
    "entity.kraken.bolt": (["entity/kraken/bolt1", "entity/kraken/bolt2"], lambda: [bolt(k, 2710 + k) for k in (1, 2)]),
    "entity.kraken.ambush_warn": (["entity/kraken/ambush_warn"], lambda: [ambush_warn()]),
    "entity.kraken.ambush": (["entity/kraken/ambush1", "entity/kraken/ambush2"], lambda: [ambush(k, 2810 + k) for k in (1, 2)]),
    "entity.kraken.shield_crack": (["entity/kraken/shield_crack1", "entity/kraken/shield_crack2", "entity/kraken/shield_crack3"],
                                   lambda: [shield_crack(k, 2910 + k) for k in (1, 2, 3)]),
    "entity.kraken.shield_break": (["entity/kraken/shield_break"], lambda: [shield_break()]),
    "entity.kraken.shield_regen": (["entity/kraken/shield_regen"], lambda: [shield_regen()]),
    "entity.kraken_tentacle.hurt": (["entity/kraken_tentacle/hurt1", "entity/kraken_tentacle/hurt2"], lambda: [tentacle_hurt(k, 3100 + k) for k in (1, 2)]),
    "entity.kraken_tentacle.death": (["entity/kraken_tentacle/death"], lambda: [tentacle_death()]),
    "entity.young_kraken.ambient": (["entity/young_kraken/ambient1", "entity/young_kraken/ambient2"], lambda: [young_ambient(k, 3200 + k) for k in (1, 2)]),
    "entity.young_kraken.hurt": (["entity/young_kraken/hurt1", "entity/young_kraken/hurt2"], lambda: [young_hurt(k, 3210 + k) for k in (1, 2)]),
    "entity.young_kraken.death": (["entity/young_kraken/death"], lambda: [young_death()]),
    "entity.kraken_egg.hatch": (["entity/kraken_egg/hatch"], lambda: [egg_hatch()]),
}

# Música: as fases tocam pelo misturador de camadas do mod (som vazio da Fabric em fluxo); os stingers são arquivos.
MUSIC = {
    "music.kraken.tormenta": "fabric-sound-api-v1:empty",
    "music.kraken.furia": "fabric-sound-api-v1:empty",
    "music.kraken.abismo": "fabric-sound-api-v1:empty",
    "music.kraken.transition": "aquamarine:music/kraken/transition",
    "music.kraken.victory": "aquamarine:music/kraken/victory",
    "music.kraken.retreat": "aquamarine:music/kraken/retreat",
    "music.kraken.summon": "aquamarine:music/stinger_summon",
}


def write_sounds_json():
    data = {}
    for event, (files, _) in SFX.items():
        data[event] = {"subtitle": "subtitles.aquamarine." + event,
                       "sounds": [{"name": "aquamarine:" + f} for f in files]}
    for event, name in MUSIC.items():
        data[event] = {"sounds": [{"name": name, "stream": True}]}
    path = os.path.join(REPO, "src", "main", "resources", "assets", "aquamarine", "sounds.json")
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
        f.write("\n")
    print(path)


if __name__ == "__main__":
    for event in (sys.argv[1:] or SFX):
        SFX[event][1]()
    write_sounds_json()
