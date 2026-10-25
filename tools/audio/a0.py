"""Áudio v1, lote A0: as 6 amostras da prova de qualidade (docs/09-audio.md, "Portões de audição").

Uso (na raiz do repositório):
    tools/audio/.venv/Scripts/python tools/audio/a0.py [nome ...]

Tudo original, sintetizado por código com semente fixa. Grava os .ogg em
assets/aquamarine/sounds/, os masters e espectrogramas em build/audio/ e atualiza o manifesto.
"""
import sys

import numpy as np

import dsp
from dsp import SR, n_of, time
from master import export

SCRIPT = "tools/audio/a0.py"


# ============================================================== 1. Ambiência: Profundeza (nível 2)

def depth_2(seed=201):
    """Laço de 48 s, estéreo e circular: drone grave em ré (frígio, com o mi bemol da tensão),
    respiração da Pressão, massa d'água, rangidos de casco, gemidos longínquos e bolhas."""
    r = dsp.rng(seed)
    N = n_of(48.0)
    t = time(N)
    mix = np.zeros((2, N))

    # Drone: sub em ré, corpo de supersaw com filtro que respira, e o mi bemol que aparece e some.
    sub = 0.55 * dsp.osc(dsp.periodic(dsp.hz("D1"), N), N) + 0.30 * dsp.osc(dsp.periodic(dsp.hz("D2"), N), N, phase0=0.3)
    body = np.zeros(N)
    for note, gain in (("D2", 0.5), ("A2", 0.35), ("D3", 0.18)):
        for k in range(5):
            cents = (k - 2) * 6.0
            f = dsp.periodic(dsp.hz(note) * 2 ** (cents / 1200), N)
            vib = 1 + 0.0012 * dsp.periodic_lfo(N, 3 + k, r.random())
            body += gain / 5 * dsp.osc(f * vib, N, "saw", phase0=r.random())
    cutoff = 260 + 140 * dsp.periodic_lfo(N, 3, 0.1) + 60 * dsp.periodic_wander(N, r, 5)
    body = dsp.circ_sweep(body, "lp", cutoff, 0.9)
    tension_env = np.clip(dsp.periodic_lfo(N, 1, 0.65), 0, 1) ** 2
    tension = tension_env * (0.20 * dsp.osc(dsp.periodic(dsp.hz("Eb3"), N), N) + 0.10 * dsp.osc(dsp.periodic(dsp.hz("Eb2"), N), N))
    drone = sub + 0.9 * body + tension
    mix += dsp.haas(drone * 0.55, 9.0, 0.5, circular=True)

    # Respiração da Pressão: ruído marrom grave que incha e esvazia a cada 8 s.
    breath = dsp.circ(lambda x: dsp.lp(x, 240, 2), dsp.brown(N, r))
    breath *= 0.55 + 0.45 * dsp.periodic_lfo(N, 6, 0.75)
    mix += np.stack([breath, np.roll(breath, n_of(0.37))]) * 0.22

    # Massa d'água: ruído rosa em banda, com correnteza lenta de um lado ao outro.
    water = dsp.circ(lambda x: dsp.bp(x, 70, 520, 2), dsp.pink(N, r))
    sway = 0.6 * dsp.periodic_lfo(N, 2, 0.2)
    mix += dsp.pan(water * (0.7 + 0.3 * dsp.periodic_wander(N, r, 4)), sway) * 0.16

    # Eventos (com volta circular): rangidos, gemidos distantes, bolhas.
    events = np.zeros((2, N))
    for k in range(7):
        dur = r.uniform(0.9, 2.6)
        c = dsp.creak(n_of(dur), r, base=r.uniform(110, 320), rate=(15, 70))
        c = dsp.hp(c, 90) * r.uniform(0.6, 1.2)
        dsp.place_wrap(events, dsp.pan(c, r.uniform(-0.85, 0.85)), int(r.uniform(0, N)))
    for k in range(3):
        dur = r.uniform(3.5, 6.0)
        n = n_of(dur)
        f = dsp.curve(n, [(0, r.uniform(80, 110)), (dur * 0.5, r.uniform(120, 170)), (dur, r.uniform(70, 95))], "exp")
        moan = dsp.osc(f, n) + 0.35 * dsp.osc(f * 2, n) + 0.12 * dsp.osc(f * 3, n)
        moan = dsp.formants(moan, "u", 0.6) * dsp.adsr(n, 1.2, 1.0, 0.8, 1.6)
        moan = dsp.lp(moan, 650, 2) * 0.5
        dsp.place_wrap(events, dsp.pan(moan, r.uniform(-0.6, 0.6)), int(r.uniform(0, N)))
    for k in range(5):
        dur = r.uniform(1.0, 2.2)
        cloud = dsp.bubbles(n_of(dur), r, rate_per_s=r.uniform(20, 45), radius_mm=(0.8, 4.0))
        cloud = dsp.hp(cloud, 400) * 0.06
        dsp.place_wrap(events, dsp.pan(cloud, r.uniform(-0.9, 0.9)), int(r.uniform(0, N)))
    ir = dsp.impulse_response(7.0, r, rt60_low=6.5, rt60_high=1.6, predelay=0.05, dark=4500)
    events = dsp.circ_reverb(events, ir, wet=0.9)
    mix += events * 0.9

    mix = np.stack([dsp.circ(lambda x: dsp.eq(x, "highshelf", 4000, 0.7, -9), ch) for ch in mix])
    mix = np.stack([dsp.circ(lambda x: dsp.hp(x, 24, 2), ch) for ch in mix])
    return export("ambient/depth_2", mix, "ambience", SCRIPT, seed,
                  "Ambiência: Profundeza (nível 2 da Pressão). Laço de 48 s.",
                  "Peso no peito sem cansar; rangidos de casco espaçados; a emenda do laço (deixe repetir).",
                  loop=True)


# ============================================================== 2. Criatura: canto da Baleia-de-Nereia

def _whale_voice(n, f, r, grit=0.0):
    v = dsp.osc(f, n) + 0.45 * dsp.osc(f * 2, n, phase0=0.2) + 0.2 * dsp.osc(f * 3, n, phase0=0.5) + 0.08 * dsp.osc(f * 4, n)
    v += 0.25 * dsp.fm(f, 1.0, 0.8 + 0.6 * dsp.smooth_noise(n, 0.8, r) ** 2, n)
    if grit:
        v *= 1 + grit * dsp.osc(31.0, n, "tri")
    return v


def whale_song(seed=301):
    """Frase de ~9 s: lamento que sobe, um "uup" rápido e um lamento que desce com aspereza no fim."""
    r = dsp.rng(seed)
    total = n_of(9.0)
    out = np.zeros(total)
    phrases = [
        (0.0, 2.8, [(0, 140), (0.9, 205), (2.0, 215), (2.8, 185)], 0.0, "u"),
        (3.1, 1.1, [(0, 300), (0.55, 760), (1.1, 880)], 0.0, "o"),
        (4.5, 4.0, [(0, 255), (1.4, 205), (2.9, 150), (4.0, 118)], 0.35, "o"),
    ]
    for start, dur, contour, grit, vowel in phrases:
        n = n_of(dur)
        f = dsp.curve(n, contour, "exp") * (1 + 0.006 * dsp.osc(4.6, n))
        voice = _whale_voice(n, f, r, grit)
        voice = 0.6 * dsp.formants(voice, vowel, 0.75) + 0.4 * voice
        voice *= dsp.adsr(n, a=min(0.35, dur * 0.3), d=0.6, s=0.85, r=min(0.7, dur * 0.35))
        breath = dsp.bp(dsp.pink(n, r), 300, 2200) * dsp.adsr(n, 0.2, 0.5, 0.5, 0.4) * 0.04
        dsp.place(out, voice + breath, n_of(start))
    out = dsp.hp(dsp.underwater(out, 4200, 0.3), 75, 2)
    ir = dsp.impulse_response(6.0, r, rt60_low=4.0, rt60_high=1.8, predelay=0.06, dark=3800)
    out = dsp.hp(dsp.mono_reverb(out, ir, wet=0.55), 60, 2)
    out = dsp.fade(dsp.fit(out, n_of(12.0)), 0.01, 1.5)
    return export("entity/whale/song1", out, "sfx", SCRIPT, seed,
                  "Criatura: canto da Baleia-de-Nereia (posicional, mono).",
                  "Beleza e distância: tem que soar como um animal enorme e calmo, não como sintetizador.")


# ============================================================== 3. Kraken: rugido

def kraken_roar(seed=401):
    """Rugido de ~4,5 s: voz de trato vocal enorme (formantes escalados), sub, água revolvida e grão
    metálico. Contorno: sobe, segura, cai."""
    r = dsp.rng(seed)
    dur = 4.6
    n = n_of(dur)
    t = time(n)
    f0 = dsp.curve(n, [(0, 46), (0.25, 70), (0.9, 78), (1.9, 74), (3.4, 52), (4.6, 40)], "exp")
    f0 = f0 * (1 + 0.025 * dsp.smooth_noise(n, 28, r))
    rough = 1 + 0.55 * dsp.osc(dsp.curve(n, [(0, 22), (2, 34), (4.6, 18)]), n, "tri")
    src = dsp.osc(f0, n, "saw") + 0.5 * dsp.osc(f0 / 2, n, "saw", phase0=0.3) + 0.2 * dsp.osc(f0 * 1.5, n, "saw")
    src *= rough
    env = dsp.curve(n, [(0, 0), (0.12, 0.9), (0.5, 1.0), (2.4, 0.95), (3.8, 0.55), (4.6, 0)])
    voice = dsp.morph_formants(src, [(0, "o"), (0.6, "a"), (2.2, "a"), (3.5, "o"), (4.6, "u")], scale=0.42)
    voice = dsp.saturate(voice * 2.2, 2.5) * env
    throat = dsp.morph_formants(dsp.pink(n, r), [(0, "o"), (0.8, "a"), (3.0, "o"), (4.6, "u")], scale=0.5) * env * 0.35
    sub = (0.8 * dsp.osc(f0 / 2, n) + 0.4 * dsp.osc(31.0, n)) * env
    thump_n = n_of(0.5)
    thump = dsp.osc(dsp.curve(thump_n, [(0, 75), (0.4, 28)], "exp"), thump_n) * dsp.decay(thump_n, 0.18, 0.004)
    churn = dsp.bubbles(n, r, rate_per_s=90, radius_mm=(8, 26)) * dsp.curve(n, [(0, 0), (0.3, 1), (3.0, 0.8), (4.6, 0.1)])
    rush = dsp.lp(dsp.pink(n, r), 900, 2) * env * 0.25
    grit = dsp.bp(dsp.fm(f0 * 3, 1.41, 3.0, n), 600, 2600) * env * 0.12
    # Grito: a mesma voz quatro vezes mais aguda num trato menor, só nos médios; é o que faz o
    # rugido "falar" em vez de virar ronco.
    scream_src = dsp.osc(f0 * 4, n, "saw") * (1 + 0.6 * dsp.osc(dsp.curve(n, [(0, 26), (4.6, 20)]), n, "tri"))
    scream = dsp.morph_formants(scream_src, [(0, "o"), (0.5, "a"), (2.4, "a"), (3.6, "o")], scale=0.8)
    scream = dsp.bp(dsp.saturate(scream * 2.0, 2.0), 350, 3200) * env ** 1.3 * 0.55
    churn *= dsp.curve(n, [(0, 1), (3.4, 1), (4.0, 0)])
    mix = 1.0 * voice + scream + throat + 0.6 * sub + 0.45 * churn + rush + grit
    dsp.place(mix, thump * 1.4, 0)
    mix = dsp.hp(mix, 32, 2)
    mix = dsp.eq(mix, "peak", 130, 1.0, -5.0)
    mix = dsp.eq(mix, "peak", 1300, 0.9, 3.5)
    mix = dsp.compress(mix, -16, 3.0, 0.004, 0.2)
    mix = dsp.underwater(mix, 6000, 0.4)
    ir = dsp.impulse_response(5.0, r, rt60_low=5.0, rt60_high=1.2, predelay=0.03, dark=3500)
    mix = dsp.mono_reverb(mix, ir, wet=0.32)
    mix = dsp.fade(dsp.fit(mix, n_of(7.0)), 0.002, 1.2)
    return export("entity/kraken/roar1", mix, "sfx", SCRIPT, seed,
                  "Kraken: rugido (intensidade média). Posicional, mono.",
                  "Tamanho e ameaça; nada de \"voz de robô\"; o grave tem que encher sem embolar.")


# ============================================================== 4. Item: disparo do Propulsor de Maré

def thruster_fire(seed=501):
    """~1,6 s: carga elétrica, golpe de água que varre de grave a agudo, rajada de bolhas e baque."""
    r = dsp.rng(seed)
    n = n_of(1.8)
    out = np.zeros(n)
    zn = n_of(0.22)
    zap = dsp.fm(dsp.curve(zn, [(0, 380), (0.2, 2600)], "exp"), 2.01, dsp.curve(zn, [(0, 4), (0.22, 1)]), zn)
    crackle = dsp.bp((r.random(zn) > 0.985) * r.uniform(-1, 1, zn), 2500, 9000) * 3
    dsp.place(out, (zap * 0.35 + crackle * 0.4) * dsp.adsr(zn, 0.01, 0.05, 0.7, 0.06), 0)
    wn = n_of(1.3)
    sweep_f = dsp.curve(wn, [(0, 300), (0.18, 2600), (0.6, 1100), (1.3, 450)], "exp")
    whoosh = dsp.sweep(dsp.pink(wn, r), "bp", sweep_f, 1.1) * dsp.curve(wn, [(0, 0), (0.06, 1), (0.35, 0.7), (1.3, 0)]) * 1.6
    dsp.place(out, whoosh, n_of(0.12))
    bn = n_of(1.1)
    rush = dsp.bubbles(bn, r, rate_per_s=420, radius_mm=(0.6, 3.2)) * dsp.curve(bn, [(0, 1), (0.4, 0.6), (1.1, 0)])
    dsp.place(out, rush * 0.35, n_of(0.16))
    tn = n_of(0.35)
    thump = dsp.osc(dsp.curve(tn, [(0, 95), (0.3, 38)], "exp"), tn) * dsp.decay(tn, 0.09, 0.003)
    dsp.place(out, thump * 0.9, n_of(0.13))
    out = dsp.underwater(out, 7500, 0.5)
    ir = dsp.impulse_response(1.6, r, rt60_low=1.4, rt60_high=0.5, predelay=0.01, dark=6000)
    out = dsp.mono_reverb(out, ir, wet=0.18)
    out = dsp.fade(dsp.fit(out, n_of(2.6)), 0.001, 0.5)
    return export("item/thruster/fire1", out, "sfx", SCRIPT, seed,
                  "Item: disparo do Propulsor de Maré (posicional, mono).",
                  "Impulso e força debaixo d'água; a faísca no começo avisa a Bobina; curto e satisfatório.")


# ============================================================== 5. Música: Porto dos Náufragos

BPM = 76.0
BEAT = 60.0 / BPM
BAR = 4 * BEAT

# Ré lídio: 2 compassos por acorde, 32 compassos (o fim volta ao começo).
CHORDS = [
    ("D2", ["D3", "F#3", "A3", "C#4", "E4"]), ("D2", ["D3", "E3", "G#3", "B3", "E4"]),
    ("B1", ["D3", "F#3", "A3", "B3", "E4"]), ("G1", ["D3", "F#3", "B3", "C#4", "G4"]),
    ("D2", ["D3", "F#3", "A3", "C#4", "E4"]), ("D2", ["D3", "E3", "G#3", "B3", "E4"]),
    ("F#1", ["C#3", "E3", "A3", "F#4", "E4"]), ("G1", ["D3", "F#3", "A3", "B3", "G4"]),
    ("B1", ["D3", "F#3", "A3", "C#4", "B4"]), ("G1", ["D3", "F#3", "B3", "C#4", "G4"]),
    ("F#1", ["C#3", "D3", "A3", "E4", "F#4"]), ("E2", ["D3", "F#3", "G3", "B3", "E4"]),
    ("G1", ["D3", "F#3", "B3", "C#4", "G4"]), ("A1", ["D3", "E3", "G3", "B3", "A4"]),
    ("G1", ["D3", "F#3", "A3", "B3", "G4"]), ("A1", ["D3", "E3", "A3", "B3", "E4"]),
]

# Melodia da harpa de vidro: (compasso, tempo, duração em tempos, nota). Entra no compasso 5.
MELODY = [
    (4, 0, 2, "F#5"), (4, 2, 1, "A5"), (4, 3, 1, "G#5"), (5, 0, 3, "E5"),
    (6, 0, 1, "D5"), (6, 1, 1, "E5"), (6, 2, 2, "F#5"), (7, 0, 4, "C#5"),
    (8, 0, 2, "A5"), (8, 2, 1, "B5"), (8, 3, 1, "A5"), (9, 0, 4, "F#5"),
    (10, 0, 2, "G#5"), (10, 2, 1, "F#5"), (10, 3, 1, "E5"), (11, 0, 4, "B4"),
    (12, 0, 2, "C#5"), (12, 2, 1, "E5"), (12, 3, 1, "F#5"), (13, 0, 4, "A5"),
    (14, 0, 2, "B5"), (14, 2, 1, "A5"), (14, 3, 1, "F#5"), (15, 0, 4, "D5"),
    (16, 0, 1, "F#5"), (16, 1, 1, "A5"), (16, 2, 2, "C#6"), (17, 0, 4, "B5"),
    (18, 0, 1, "A5"), (18, 1, 1, "B5"), (18, 2, 1, "C#6"), (18, 3, 1, "D6"), (19, 0, 3, "C#6"), (19, 3, 1, "B5"),
    (20, 0, 4, "A5"), (21, 0, 2, "F#5"), (21, 2, 2, "E5"),
    (22, 0, 2, "G5"), (22, 2, 1, "F#5"), (22, 3, 1, "E5"), (23, 0, 4, "B4"),
    (24, 0, 6, "C#6"), (25, 2, 2, "B5"), (26, 0, 8, "A5"),
    (28, 0, 4, "F#5"), (29, 0, 4, "A5"), (30, 0, 8, "E5"),
]


def glass_harp(f, dur, r, vel):
    """Taça friccionada: senoide quase pura com batimento lento, parcial 2,32 e chiado do dedo."""
    n = n_of(dur + 1.6)
    t = time(n)
    env = dsp.adsr(n, a=0.16, d=0.5, s=0.82, r=1.2, hold=dur)
    tone = (np.sin(2 * np.pi * f * t) + 0.55 * np.sin(2 * np.pi * f * 1.0028 * t + 1.1)
            + 0.10 * np.sin(2 * np.pi * f * 2.32 * t) * np.exp(-t / 1.2) + 0.03 * np.sin(2 * np.pi * f * 4.25 * t))
    tone *= 1 + 0.05 * np.sin(2 * np.pi * 5.2 * t)
    rub = dsp.bp(dsp.pink(n, r), f * 0.85, f * 1.2, 1) * 0.04
    return (tone + rub) * env * vel


def marimba(f, r, vel):
    """Barra de marimba: modos 1 : 3,93 : 9,24 com decaimento curto nos agudos, e o baque da baqueta."""
    n = n_of(1.8)
    d = 1.1 * (220 / f) ** 0.45
    v = dsp.modal(n, [f, f * 3.93, f * 9.24, f * 2.0], [d, d * 0.28, d * 0.08, d * 0.5], [1.0, 0.33, 0.1, 0.06], r, strike=0.0015)
    return dsp.lp(v, 5500, 2) * vel


def harbor_bell(f, r):
    """Sino do Porto: parciais de sino de terça menor, longo."""
    n = n_of(7.0)
    ratios = [0.5, 1.0, 1.183, 1.506, 2.0, 2.514, 2.662, 3.011, 4.166]
    decays = [5.5, 4.0, 3.2, 2.6, 2.1, 1.5, 1.3, 1.0, 0.7]
    amps = [0.6, 1.0, 0.7, 0.45, 0.4, 0.25, 0.2, 0.15, 0.08]
    return dsp.modal(n, [f * k for k in ratios], decays, amps, r, strike=0.003)


def pad(notes, dur, r):
    n = n_of(dur + 2.5)
    out = np.zeros(n)
    for name in notes:
        out += dsp.supersaw(dsp.hz(name), n, r, voices=5, detune_cents=9)
    out = dsp.lp(out, 1500, 2)
    out = 0.75 * out + 0.25 * dsp.formants(out, "o", 1.0)
    return out * dsp.adsr(n, a=1.4, d=1.0, s=0.85, r=2.2, hold=dur) / len(notes)


def bass(name, dur, r):
    n = n_of(dur + 1.0)
    f = dsp.hz(name)
    v = dsp.osc(f, n) + 0.22 * dsp.osc(f, n, "tri") + 0.08 * dsp.osc(f * 2, n)
    return dsp.lp(v, 420, 2) * dsp.adsr(n, a=0.04, d=0.6, s=0.7, r=0.6, hold=dur)


def harbor_theme(seed=601):
    """Tema do Porto dos Náufragos: caloroso e seguro. Ré lídio, 76 bpm, 32 compassos em laço."""
    r = dsp.rng(seed)
    N = n_of(32 * BAR)
    mix = np.zeros((2, N))
    hum = lambda: r.normal(0, 0.008)
    pads, marimbas, melody, plucks, low = (np.zeros((2, N)) for _ in range(5))
    for i, (root, voicing) in enumerate(CHORDS):
        start = i * 2 * BAR
        dsp.place_wrap(pads, dsp.haas(pad(voicing, 2 * BAR, r), 14, 0.7), n_of(start))
        for b in (0, 1):
            bar_start = start + b * BAR
            # Baixo na oitava 2 (73 a 123 Hz): abaixo disso embola em caixas pequenas.
            low_root = root if dsp.hz(root) >= dsp.hz("D2") else root[:-1] + str(int(root[-1]) + 1)
            dsp.place_wrap(low, bass(low_root, BEAT * 2.6, r) * 0.9, n_of(bar_start))
            dsp.place_wrap(low, bass(low_root, BEAT * 1.2, r) * 0.55, n_of(bar_start + 2.5 * BEAT))
            tones = sorted({dsp.hz(v) * 2 for v in voicing if dsp.hz(v) * 2 < 900})
            pattern = [0, 2, 1, 3, 2, 4, 3, 1]
            for k, idx in enumerate(pattern):
                f = tones[idx % len(tones)]
                vel = (0.75 if k % 2 == 0 else 0.5) * r.uniform(0.85, 1.05) * (0.6 if i < 2 else 1.0)
                hit = marimba(f, r, vel)
                dsp.place_wrap(marimbas, dsp.pan(hit, 0.25 * np.sin(k)), n_of(bar_start + k * BEAT / 2 + hum()))
            if 4 <= i < 12 or 12 <= i < 14:
                for beat in (1, 3):
                    f = dsp.hz(voicing[(beat + i) % len(voicing)]) * 4
                    pl = dsp.karplus(f, n_of(2.0), r, damping=0.9965, bright=0.35) * 0.28 * r.uniform(0.8, 1.0)
                    dsp.place_wrap(plucks, dsp.pan(pl, r.uniform(-0.7, 0.7)), n_of(bar_start + beat * BEAT + hum()))
    for bar, beat, beats, note in MELODY:
        g = glass_harp(dsp.hz(note), beats * BEAT * 0.95, r, 0.55)
        dsp.place_wrap(melody, dsp.pan(g, -0.12), n_of(bar * BAR + beat * BEAT + hum()))
    bells = np.zeros((2, N))
    for bar in (0, 16):
        dsp.place_wrap(bells, dsp.pan(harbor_bell(dsp.hz("D4"), r), 0.45), n_of(bar * BAR))
    bub = np.zeros((2, N))
    for k in range(10):
        cloud = dsp.hp(dsp.bubbles(n_of(1.5), r, rate_per_s=25, radius_mm=(1.0, 4.0)), 500) * 0.05
        dsp.place_wrap(bub, dsp.pan(cloud, r.uniform(-0.9, 0.9)), int(r.uniform(0, N)))
    mix = (0.30 * pads + 0.42 * marimbas + 0.62 * melody + 0.32 * plucks + 0.40 * low + 0.12 * bells + 0.5 * bub)
    ir = dsp.impulse_response(4.5, r, rt60_low=3.4, rt60_high=1.5, predelay=0.03, dark=10000)
    mix = dsp.circ_reverb(mix, ir, wet=0.42)
    mix = np.stack([dsp.circ(lambda x: dsp.hp(dsp.eq(x, "lowshelf", 120, 0.7, 1.5), 30, 2), ch) for ch in mix])
    mix = dsp.circ(lambda x: dsp.compress(x, -20, 1.8, 0.02, 0.3), mix)
    return export("music/harbor", mix, "music", SCRIPT, seed,
                  "Música: Porto dos Náufragos (ré lídio, 76 bpm, 32 compassos em laço, ~1 min 41 s).",
                  "Calor e segurança; a harpa de vidro canta por cima; o Sino do Porto ao fundo; a volta do laço.",
                  loop=True)


# ============================================================== 6. Stinger: Invocação

def summon_stinger(seed=701):
    """~11 s: quase silêncio, coro grave em ré frígio que cresce (ooh → aah), metais em FM que
    abrem, pulsos que aceleram, ruído que sobe e o impacto (taiko, sub e gongo) com a cauda."""
    r = dsp.rng(seed)
    total = n_of(12.0)
    mix = np.zeros((2, total))
    hit = 8.4
    sn = n_of(hit + 0.6)
    swell = dsp.curve(sn, [(0, 0.001), (0.9, 0.004), (1.6, 0.05), (4.5, 0.2), (hit, 1.0), (hit + 0.6, 0.0001)], "exp")
    choir = np.zeros((2, sn))
    for k, name in enumerate(["D2", "A2", "D3", "Eb3", "A3", "D4"]):
        f = dsp.hz(name) * (1 + 0.004 * dsp.osc(5.0 + 0.3 * k, sn))
        voice = dsp.supersaw(f, sn, r, voices=5, detune_cents=11)
        voice = dsp.morph_formants(voice, [(0, "u"), (4.0, "o"), (hit, "a")], scale=1.0 if k < 4 else 1.15)
        choir += dsp.pan(voice, (k - 2.5) / 3.5)
    choir *= swell * 0.5
    brass = np.zeros(sn)
    for name in ("D3", "A3"):
        brass += dsp.fm(dsp.hz(name), 1.0, dsp.curve(sn, [(0, 0.3), (hit, 4.5)]), sn)
    brass = dsp.lp(brass, 3500, 2) * swell ** 1.4 * 0.35
    sub = dsp.osc(dsp.curve(sn, [(0, 36.7), (hit, 41.2)], "exp"), sn) * swell * 0.7
    riser = dsp.sweep(dsp.white(sn, r), "bp", dsp.curve(sn, [(0, 300), (hit, 6000)], "exp"), 1.5) * swell ** 2 * 0.25
    pulses = np.zeros(sn)
    times = [4.0, 5.1, 6.0, 6.7, 7.25, 7.7, 8.05]
    for k, pt in enumerate(times):
        pn = n_of(0.5)
        drum = dsp.osc(dsp.curve(pn, [(0, 90), (0.25, 48)], "exp"), pn) * dsp.decay(pn, 0.16, 0.003)
        dsp.place(pulses, drum * (0.3 + 0.1 * k), n_of(pt))
    pre = choir + dsp.pan(brass, 0.0) + dsp.haas(sub + pulses + riser, 8, 0.4)
    dsp.place(mix, pre, 0)
    imp_n = n_of(3.6)
    taiko = dsp.osc(dsp.curve(imp_n, [(0, 120), (0.4, 42)], "exp"), imp_n) * dsp.decay(imp_n, 0.55, 0.002)
    skin = dsp.lp(dsp.white(imp_n, r), 1800, 2) * dsp.decay(imp_n, 0.06, 0.001) * 0.6
    gong_freqs = [dsp.hz("D3") * k for k in (1, 1.47, 1.97, 2.43, 2.92, 3.6, 4.4, 5.8)]
    gong = dsp.modal(imp_n, gong_freqs, [3.2, 2.6, 2.2, 1.8, 1.5, 1.1, 0.8, 0.5], [1, 0.7, 0.6, 0.45, 0.35, 0.25, 0.2, 0.1], r, strike=0.01)
    impact = 1.3 * taiko + skin + 0.35 * gong
    tail_choir = np.zeros((2, imp_n))
    for k, name in enumerate(["D2", "A2", "D3", "Eb3", "A3"]):
        v = dsp.formants(dsp.supersaw(dsp.hz(name), imp_n, r, voices=5, detune_cents=11), "a")
        tail_choir += dsp.pan(v, (k - 2) / 3)
    tail_choir *= dsp.curve(imp_n, [(0, 0.5), (3.6, 0)]) * 0.5
    dsp.place(mix, dsp.haas(impact, 6, 0.3) + tail_choir, n_of(hit))
    ir = dsp.impulse_response(6.0, r, rt60_low=5.0, rt60_high=1.6, predelay=0.04, dark=7000)
    mix = dsp.reverb(mix, ir, wet=0.45)
    mix = dsp.fade(dsp.fit(mix, total), 0.01, 1.0)
    mix = np.stack([dsp.hp(ch, 25, 2) for ch in mix])
    return export("music/stinger_summon", mix, "music", SCRIPT, seed,
                  "Stinger: Invocação do Kraken (~11 s, estéreo).",
                  "Silêncio que pesa, crescimento de pavor e um impacto que dá frio na espinha; sem cortar seco.")


SOUNDS = {"depth_2": depth_2, "whale": whale_song, "roar": kraken_roar, "thruster": thruster_fire,
          "harbor": harbor_theme, "summon": summon_stinger}

if __name__ == "__main__":
    for key in (sys.argv[1:] or SOUNDS):
        SOUNDS[key]()
