"""Instrumentos da luta do Kraken (docs/09-audio.md, "Luta do Kraken"): bateria de break própria
(nada de amostras: o break é sintetizado e depois picotado como no breakcore), trombetas, trompas,
metais graves, coro, cordas, sinos, baixo, riff distorcido e efeitos (impacto, subida, prato invertido).

Cada voz devolve um vetor mono; `render` espalha notas num buffer estéreo em laço (place_wrap).
"""
import numpy as np

import dsp
from dsp import SR, n_of, time


# ================================================================ sequência

def parse(seq, vel=0.8):
    """'D5/2 Eb5/1! r/1 | C5/4.' -> [(início, duração, nota, vel)] em tempos.
    '!' acentua (1.0), '.' suaviza (0.55); 'r' é pausa; '|' só organiza a leitura."""
    out, t = [], 0.0
    for tok in seq.replace("|", " ").split():
        v = vel
        if tok.endswith("!"):
            v, tok = 1.0, tok[:-1]
        elif tok.endswith("."):
            v, tok = 0.55, tok[:-1]
        name, beats = tok.split("/")
        beats = float(beats)
        if name != "r":
            out.append((t, beats, name, v))
        t += beats
    return out


def shift(notes, beats):
    return [(s + beats, d, n, v) for s, d, n, v in notes]


def transpose(name, semis):
    """Transpõe o nome de uma nota ('D4', +12 -> 'D5')."""
    names = ["C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B"]
    pitch, octave = name[:-1], int(name[-1])
    k = dsp.NOTE[pitch] + 12 * octave + semis
    return names[k % 12] + str(k // 12)


def render(dst, notes, bpm, voice, r, pan=0.0, spread=0.0, players=1, detune=0.0, sloppy_ms=0.0, gain=1.0,
           length=1.0):
    """Toca notas [(início, duração, nota, vel)] (tempos) em dst estéreo com volta circular.
    players > 1 = naipe: cada músico desafinado em `detune` cents, atrasado até `sloppy_ms` e
    espalhado em `spread` no pan."""
    beat = 60.0 / bpm
    for p in range(players):
        cents = 0.0 if players == 1 else (p - (players - 1) / 2) / ((players - 1) / 2) * detune
        pos = pan + (0.0 if players == 1 else (p - (players - 1) / 2) / ((players - 1) / 2) * spread)
        for start, dur, name, vel in notes:
            f = dsp.hz(name) * 2 ** ((cents + r.normal(0, 1.5)) / 1200)
            x = voice(f, dur * beat * length, r, vel) * gain / np.sqrt(players)
            at = n_of(start * beat + abs(r.normal(0, sloppy_ms / 1000.0)))
            dsp.place_wrap(dst, dsp.pan(x, float(np.clip(pos, -1, 1))), at)
    return dst


# ================================================================ bateria de break (sintetizada)

def kick(r, vel=1.0, tone=1.0):
    n = n_of(0.5)
    f = dsp.curve(n, [(0, 165 * tone), (0.025, 78 * tone), (0.11, 50 * tone), (0.5, 43 * tone)], "exp")
    body = dsp.osc(f, n) * dsp.decay(n, 0.24, 0.0008)
    click = dsp.hp(dsp.white(n, r), 2200) * dsp.decay(n, 0.0035, 0.0002) * 0.55
    knock = dsp.bp(dsp.white(n, r), 300, 900) * dsp.decay(n, 0.012, 0.0005) * 0.35
    return dsp.saturate((body + click + knock) * 1.7, 2.0) * vel


def snare(r, vel=1.0, tone=1.0, length=1.0):
    n = n_of(0.55)
    t = time(n)
    drop = 1 + 0.22 * np.exp(-t / 0.012)
    body = (dsp.osc(188 * tone * drop, n) * dsp.decay(n, 0.075 * length, 0.0005)
            + 0.55 * dsp.osc(334 * tone * drop, n, phase0=0.25) * dsp.decay(n, 0.05 * length, 0.0005))
    wires = dsp.bp(dsp.white(n, r), 1500, 7000) * dsp.decay(n, 0.17 * length, 0.0012) * 0.8
    crack = dsp.hp(dsp.white(n, r), 4200) * dsp.decay(n, 0.006, 0.0002)
    x = 0.85 * body + 0.8 * wires + 0.45 * crack
    return dsp.saturate(x * 1.6, 1.7) * vel


HAT_PARTIALS = [205.3, 304.4, 369.6, 522.7, 540.0, 800.0]


def hat(r, vel=1.0, open_=False):
    n = n_of(0.7 if open_ else 0.14)
    metal = sum(dsp.osc(f * 1.72, n, "square", phase0=r.random()) for f in HAT_PARTIALS)
    x = dsp.bp(metal, 6800, 15000) * 0.5 + 0.55 * dsp.hp(dsp.white(n, r), 7500)
    return x * dsp.decay(n, 0.24 if open_ else 0.032, 0.0004) * vel * 0.45


def cymbal(r, vel=1.0, dur=2.8, bright=1.0):
    """Prato de ataque: parciais metálicos inarmônicos densos e chiado."""
    n = n_of(dur)
    k = 46
    freqs = np.geomspace(380, 9800 * bright, k) * r.uniform(0.96, 1.04, k)
    decays = np.geomspace(dur * 0.55, dur * 0.18, k)
    amps = r.uniform(0.3, 1.0, k) / np.sqrt(np.arange(1, k + 1)) ** 0.4
    metal = dsp.modal(n, freqs, decays, amps, r, strike=0.0008)
    hiss = dsp.bp(dsp.white(n, r), 3000, 11000) * dsp.decay(n, dur * 0.25, 0.0005) * 0.2
    return dsp.hp(metal * 0.18 + hiss, 280) * vel


def ride(r, vel=1.0):
    n = n_of(1.6)
    freqs = [3150, 3730, 4410, 5260, 6120, 7480, 8800]
    x = dsp.modal(n, freqs, [1.2, 1.0, 0.9, 0.7, 0.6, 0.5, 0.4], [1, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2], r, strike=0.0005)
    ping = dsp.bp(dsp.white(n, r), 5000, 12000) * dsp.decay(n, 0.05, 0.0003) * 0.6
    return (x * 0.25 + ping) * vel * 0.5


def tom(r, f, vel=1.0):
    n = n_of(0.7)
    fr = dsp.curve(n, [(0, f * 1.6), (0.04, f * 1.05), (0.7, f * 0.95)], "exp")
    body = dsp.osc(fr, n) * dsp.decay(n, 0.28, 0.0006)
    skin = dsp.bp(dsp.white(n, r), 400, 4000) * dsp.decay(n, 0.02, 0.0004) * 0.4
    return dsp.saturate((body + skin) * 1.3, 1.5) * vel


class Kit:
    """Variações prontas de cada peça (o break reaproveita os golpes)."""

    def __init__(self, r, tone=1.0):
        self.k = [kick(r, 1.0, tone * f) for f in (1.0, 0.97, 1.03)]
        self.s = [snare(r, 1.0, tone * f) for f in (1.0, 1.04, 0.97)]
        self.g = [snare(r, 1.0, tone * 1.05, length=0.55) for _ in range(3)]
        self.h = [hat(r) for _ in range(4)]
        self.o = [hat(r, open_=True) for _ in range(2)]
        self.rd = [ride(r) for _ in range(2)]

    def hit(self, sym, r):
        table = {"K": (self.k, 1.0), "k": (self.k, 0.6), "S": (self.s, 1.0), "s": (self.g, 0.32),
                 "H": (self.h, 0.5), "h": (self.h, 0.28), "O": (self.o, 0.5), "R": (self.rd, 0.6)}
        pool, vel = table[sym]
        return pool[r.integers(len(pool))] * vel * r.uniform(0.9, 1.05)


# Padrões próprios de break em 16 avos (2 compassos = 32 passos). Linhas por peça.
BREAKS = {
    "A": {"K": "K.........KK....|..K.......K.....", "S": "....S..s.s..S..s|.s..S..s....S.s.",
          "H": "H.H.H.H.H.H.H.H.|H.H.H.H.H.H.HOH."},
    "B": {"K": "K.....K...K..K..|K.....K.....K...", "S": "....S.s.....S..s|..s.S..s.s..S...",
          "H": "H.HhH.HhH.HhH.Hh|H.HhH.HhH.Hh..O."},
    "C": {"K": "K..K......K.K...|..K..K....K.....", "S": "....S..s.ss.S...|.s..S...s..sS.ss",
          "H": "H.H.H.H.H.H.H.H.|H.H.H.H.H.H.H.H.", "R": "R...............|R..............."},
}


def break_source(name, bpm, r, kit):
    """Renderiza um break de 2 compassos (estéreo) com a bateria sintetizada."""
    step = 60.0 / bpm / 4
    n = n_of(32 * step)
    out = np.zeros((2, n + n_of(0.8)))
    pans = {"K": 0.0, "k": 0.0, "S": 0.05, "s": 0.05, "H": 0.35, "h": 0.35, "O": 0.4, "R": -0.4}
    for row in BREAKS[name].values():
        steps = row.replace("|", "")
        for i, sym in enumerate(steps):
            if sym == ".":
                continue
            swing = n_of(step * 0.08) if i % 2 else 0
            dsp.place(out, dsp.pan(kit.hit(sym, r), pans[sym]), n_of(i * step) + swing)
    # o que vaza do fim volta ao começo: o break inteiro fica circular
    tail = out[:, n:]
    out = out[:, :n]
    out[:, :tail.shape[1]] += tail
    return out


# ---------------------------------------------------------------- cortes de breakcore

def _slice(src, idx, step_n, length_n):
    s = (idx % (src.shape[1] // step_n)) * step_n
    seg = src[:, s:s + length_n]
    if seg.shape[1] < length_n:
        seg = np.concatenate([seg, src[:, :length_n - seg.shape[1]]], axis=1)
    return seg.copy()


def _edge(seg, ms=1.2):
    k = min(n_of(ms / 1000), seg.shape[1] // 2)
    if k > 0:
        ramp = np.linspace(0, 1, k)
        seg[:, :k] *= ramp
        seg[:, -k:] *= ramp[::-1]
    return seg


def _resample(seg, ratio, out_n):
    """Toca o trecho `ratio` vezes mais rápido (tom sobe), cortado/estendido para out_n amostras."""
    pos = np.arange(out_n) * ratio
    pos = pos[pos < seg.shape[1] - 1]
    out = np.zeros((2, out_n))
    i = pos.astype(int)
    frac = pos - i
    for ch in range(2):
        out[ch, :len(pos)] = seg[ch, i] * (1 - frac) + seg[ch, i + 1] * frac
    return out


def chop_bar(src, bpm, plan):
    """Um compasso picotado: plan = 16 operações por 16 avos.
    ('n', i) normal · ('r', i) invertido · ('s', i, k) gagueira (k repetições) · ('p', i, razão) tom ·
    ('x',) silêncio · ('t', i) parada de fita (o tom cai até parar) · ('c', i) esmagado (bitcrush)."""
    step_n = n_of(60.0 / bpm / 4)
    bar = np.zeros((2, 16 * step_n))
    for k, op in enumerate(plan):
        kind = op[0]
        at = k * step_n
        if kind == "x":
            continue
        idx = op[1]
        if kind == "n":
            seg = _slice(src, idx, step_n, step_n)
        elif kind == "r":
            seg = _slice(src, idx, step_n, step_n)[:, ::-1]
        elif kind == "s":
            reps = op[2]
            piece = _edge(_slice(src, idx, step_n, max(step_n // reps, 32)), 0.6)
            seg = np.concatenate([piece * (0.75 + 0.25 * j / max(reps - 1, 1)) for j in range(reps)], axis=1)
        elif kind == "p":
            seg = _resample(_slice(src, idx, step_n, int(step_n * op[2]) + 2), op[2], step_n)
        elif kind == "t":
            raw = _slice(src, idx, step_n, step_n * 2)
            rate = np.linspace(1.0, 0.05, step_n)
            pos = np.cumsum(rate)
            pos = pos[pos < raw.shape[1] - 1]
            seg = np.zeros((2, step_n))
            i = pos.astype(int)
            seg[:, :len(pos)] = raw[:, i]
        elif kind == "c":
            seg = _slice(src, idx, step_n, step_n)
            hold = 6
            seg = np.repeat(seg[:, ::hold], hold, axis=1)[:, :step_n]
            seg = np.round(seg * 12) / 12
        else:
            raise ValueError(op)
        bar[:, at:at + step_n] += _edge(dsp.fit(seg, step_n))
    return bar


def chop_plan(r, intensity, fill=False, base=0):
    """Plano de cortes de um compasso. intensity 0 = break reto; 1 = breakcore pesado."""
    plan = [("n", base + k) for k in range(16)]
    for k in range(16):
        u = r.random()
        if u < 0.18 * intensity:
            plan[k] = ("n", base + int(r.integers(32)))
        elif u < 0.26 * intensity:
            plan[k] = ("s", base + int(r.choice([4, 12, 20, 28, 0, 16])), int(r.choice([2, 3, 4])))
        elif u < 0.31 * intensity:
            plan[k] = ("r", base + int(r.integers(32)))
        elif u < 0.35 * intensity:
            plan[k] = ("p", base + k, float(r.choice([0.5, 0.75, 1.5])))
    if fill:
        # Virada: rajada de caixa acelerando (8, 16, 32 avos) e uma parada de fita ou esmagado no fim.
        snare_steps = [4, 12, 20, 28]
        plan[8:12] = [("s", int(r.choice(snare_steps)), 2)] * 4
        plan[12:15] = [("s", int(r.choice(snare_steps)), 4), ("s", int(r.choice(snare_steps)), 6),
                       ("s", int(r.choice(snare_steps)), 8)]
        plan[15] = ("t", 12) if r.random() < 0.5 else ("c", 28)
    return plan


# ================================================================ metais

def _brass(f, dur, r, vel, dark_fc, bright_fc, attack, formant_lo, formant_hi, vib=0.006, scoop=0.03, drive=1.4):
    n = n_of(dur + 0.3)
    t = time(n)
    pitch = (1 - scoop * np.exp(-t / 0.025)) * (1 + vib * np.clip((t - 0.22) / 0.35, 0, 1) * np.sin(2 * np.pi * 5.3 * t + r.random() * 6))
    pitch *= 1 + 0.0012 * dsp.smooth_noise(n, 3, r)
    src = dsp.osc(f * pitch, n, "saw")
    env = dsp.adsr(n, a=attack, d=0.18, s=0.82, r=0.14, hold=dur)
    b = np.clip(env ** 1.6 * (0.45 + 0.55 * vel), 0, 1)
    x = dsp.lp(src, dark_fc, 2) * (1 - b) + dsp.lp(src, bright_fc, 2) * b
    x = x + 0.55 * dsp.bp(x, formant_lo[0], formant_lo[1]) + 0.25 * dsp.bp(x, formant_hi[0], formant_hi[1])
    x = dsp.saturate(x * drive * (0.6 + 0.4 * vel), drive)
    breath = dsp.bp(dsp.white(n, r), 1200, 5000) * dsp.adsr(n, 0.008, 0.05, 0.04, 0.05, hold=dur) * 0.05
    return (x * env + breath) * vel


def trumpet(f, dur, r, vel=0.9):
    """Trombeta: ataque rápido com o "bico" do lábio, brilho que abre com a força e vibrato tardio."""
    return _brass(f, dur, r, vel, 1300, 7500, 0.022, (1050, 1700), (2300, 3300))


def horn(f, dur, r, vel=0.8):
    """Trompa: mais escura e redonda, ataque macio."""
    return _brass(f, dur, r, vel, 700, 2600, 0.06, (450, 900), (1600, 2400), vib=0.004, scoop=0.015, drive=1.2)


def low_brass(f, dur, r, vel=0.9):
    """Trombones e tuba (o motivo do Kraken): rasgado quando forte."""
    return _brass(f, dur, r, vel, 500, 3200, 0.035, (380, 700), (1100, 1800), vib=0.003, scoop=0.02, drive=2.2)


# ================================================================ coro, cordas, baixo, riff

def choir(f, dur, r, vel=0.8, vowel="a", attack=0.28):
    n = n_of(dur + 1.2)
    t = time(n)
    fr = f * (1 + 0.0045 * np.sin(2 * np.pi * (4.8 + r.random()) * t + r.random() * 6)) * (1 + 0.002 * dsp.smooth_noise(n, 2, r))
    src = dsp.supersaw(fr, n, r, voices=5, detune_cents=12)
    scale = 1.0 if f < 330 else 1.12
    x = 0.8 * dsp.formants(src, vowel, scale) + 0.12 * dsp.lp(src, 900, 2)
    breath = dsp.formants(dsp.white(n, r), vowel, scale) * 0.035
    env = dsp.adsr(n, a=attack, d=0.6, s=0.88, r=0.9, hold=dur)
    return (x + breath) * env * vel


def chant(f, dur, r, vel=0.9):
    """Grito de coro curto ("HA!") para as batidas da Fúria."""
    return choir(f, dur, r, vel, "a", attack=0.02) * dsp.decay(n_of(dur + 1.2), max(dur * 0.6, 0.12), 0.001)


def strings_staccato(f, dur, r, vel=0.8):
    n = n_of(max(dur, 0.12) + 0.25)
    src = dsp.supersaw(f, n, r, voices=5, detune_cents=10)
    env = dsp.adsr(n, a=0.006, d=0.09, s=0.25, r=0.08, hold=max(dur * 0.7, 0.06))
    x = dsp.lp(src, 1800 + 2600 * vel, 2) + 0.3 * dsp.bp(src, 250, 600)
    bow = dsp.bp(dsp.white(n, r), 2000, 7000) * dsp.decay(n, 0.02, 0.001) * 0.08
    return (x * env + bow) * vel


def strings_long(f, dur, r, vel=0.7):
    n = n_of(dur + 1.5)
    t = time(n)
    fr = f * (1 + 0.003 * np.sin(2 * np.pi * 5.5 * t + r.random() * 6))
    src = dsp.supersaw(fr, n, r, voices=7, detune_cents=14)
    env = dsp.adsr(n, a=0.45, d=0.8, s=0.85, r=1.2, hold=dur)
    return (dsp.lp(src, 2600, 2) + 0.25 * dsp.bp(src, 300, 800)) * env * vel


def bass(f, dur, r, vel=0.9, grit=0.4):
    n = n_of(dur + 0.3)
    env = dsp.adsr(n, a=0.006, d=0.12, s=0.75, r=0.08, hold=dur)
    sub = dsp.osc(f, n)
    body = dsp.lp(dsp.osc(f, n, "saw"), 380 + 500 * vel, 2)
    return dsp.saturate((sub + 0.6 * body) * (1 + grit * 2), 1 + grit * 2) * env * vel


def riff(f, dur, r, vel=0.9, mute=False):
    """Riff grave distorcido (quinta justa como guitarra de metal, feita de dentes de serra)."""
    n = n_of(dur + 0.15)
    v = sum(dsp.osc(f * k * 2 ** (c / 1200), n, "saw", phase0=r.random()) for k, c in ((1, -6), (1, 6), (1.5, -4), (1.5, 4), (2, 0)))
    x = dsp.saturate(dsp.hp(v, 70, 2) * 6.0, 6.0)
    x = dsp.lp(x, 2600 if not mute else 1100, 2)
    x = dsp.eq(x, "peak", 1400, 0.8, 4.0)
    env = dsp.adsr(n, a=0.003, d=0.08 if mute else 0.3, s=0.35 if mute else 0.8, r=0.06, hold=dur)
    return x * env * vel * 0.5


# ================================================================ sinos e percussão grande

def church_bell(f, r, vel=1.0, dur=7.5):
    """Sino de catedral: zumbido, prima, terça menor, quinta, nominal, superquinta e oitava."""
    n = n_of(dur)
    ratios = [0.5, 1.0, 1.2, 1.5, 2.0, 2.5, 2.67, 3.0, 4.0, 5.33]
    decays = [dur * 0.9, dur * 0.65, dur * 0.5, dur * 0.4, dur * 0.35, dur * 0.22, dur * 0.2, dur * 0.16, dur * 0.1, dur * 0.06]
    amps = [0.55, 0.9, 0.75, 0.45, 1.0, 0.4, 0.3, 0.3, 0.2, 0.1]
    x = dsp.modal(n, [f * k for k in ratios], decays, amps, r, strike=0.002, detune=0.0015)
    clang = dsp.bp(dsp.white(n, r), 1500, 6000) * dsp.decay(n, 0.03, 0.0005) * 0.25
    return (x + clang) * vel


def tubular_bell(f, r, vel=1.0, dur=5.0):
    n = n_of(dur)
    ratios = [1.0, 2.76, 5.4, 8.93, 13.34]
    x = dsp.modal(n, [f * k for k in ratios], [dur * 0.7, dur * 0.4, dur * 0.2, dur * 0.1, dur * 0.05],
                  [1.0, 0.5, 0.3, 0.15, 0.08], r, strike=0.001)
    return x * vel


def glass_bell(f, dur, r, vel=0.6):
    """Sinos de vidro do Porto (celesta de cristal)."""
    n = n_of(max(dur, 0.5) + 2.5)
    x = dsp.modal(n, [f, f * 2.0, f * 3.0, f * 4.16], [2.2, 1.2, 0.6, 0.3], [1.0, 0.35, 0.12, 0.06], r, strike=0.0008)
    return dsp.lp(x, 9000, 2) * vel


def taiko(r, vel=1.0, f=1.0):
    n = n_of(1.8)
    body = dsp.osc(dsp.curve(n, [(0, 125 * f), (0.05, 70 * f), (0.5, 44 * f)], "exp"), n) * dsp.decay(n, 0.45, 0.001)
    skin = dsp.lp(dsp.white(n, r), 1600, 2) * dsp.decay(n, 0.05, 0.0008) * 0.6
    boom = dsp.osc(46 * f, n) * dsp.decay(n, 0.45, 0.01) * 0.2
    return dsp.saturate((body * 1.2 + skin + boom) * 1.3, 1.5) * vel


def timpani(f, r, vel=1.0, dur=3.0):
    n = n_of(dur)
    x = dsp.modal(n, [f * k for k in (1.0, 1.5, 1.99, 2.44, 2.89)], [dur * 0.5, dur * 0.3, dur * 0.22, dur * 0.15, dur * 0.1],
                  [1.0, 0.6, 0.4, 0.25, 0.15], r, strike=0.003)
    thump = dsp.lp(dsp.white(n, r), 500, 2) * dsp.decay(n, 0.03, 0.001) * 0.5
    return (x + thump) * vel


def timpani_roll(f, dur, r, vel=0.8):
    n = n_of(dur + 2.0)
    out = np.zeros(n)
    k, t = 0, 0.0
    while t < dur:
        g = vel * (0.35 + 0.65 * (t / dur)) * r.uniform(0.8, 1.0)
        dsp.place(out, timpani(f, r, g, 1.5), n_of(t))
        t += 0.055 + r.uniform(-0.008, 0.008)
        k += 1
    return out * 0.35


# ================================================================ efeitos

def impact(r, vel=1.0, dur=3.5):
    n = n_of(dur)
    sub = dsp.osc(dsp.curve(n, [(0, 70), (0.4, 32), (dur, 26)], "exp"), n) * dsp.decay(n, 0.9, 0.002)
    hit = taiko(r, 1.0, 0.9)
    crash = cymbal(r, 0.7, dur=min(dur, 3.0))
    out = 1.1 * sub
    dsp.place(out, hit, 0)
    dsp.place(out, crash, 0)
    return out * vel


def riser(dur, r, vel=0.6, lo=250, hi=7000):
    n = n_of(dur)
    env = dsp.curve(n, [(0, 0.001), (dur * 0.85, 1.0), (dur, 0.9)], "exp")
    x = dsp.sweep(dsp.white(n, r), "bp", dsp.curve(n, [(0, lo), (dur, hi)], "exp"), 1.4)
    tone = dsp.osc(dsp.curve(n, [(0, 110), (dur, 880)], "exp"), n, "saw")
    tone = dsp.lp(tone, 3000, 2) * 0.12
    return (x * 0.5 + tone) * env * vel


def reverse_cymbal(dur, r, vel=0.7):
    c = cymbal(r, 1.0, dur=dur + 0.3)[:n_of(dur)]
    return c[::-1] * dsp.curve(len(c), [(0, 0.2), (dur, 1.0)]) * vel


def sub_drop(r, vel=1.0, dur=2.5):
    n = n_of(dur)
    return dsp.osc(dsp.curve(n, [(0, 90), (dur, 28)], "exp"), n) * dsp.decay(n, dur * 0.4, 0.004) * vel
