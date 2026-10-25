"""Áudio v1, luta do Kraken (docs/09-audio.md, "Luta do Kraken"): as três trilhas, uma por fase, cada
uma em três camadas sincronizadas que o jogo mistura ao vivo, e os stingers.

- Camadas: `base` (orquestra, metais graves com o motivo do Kraken, sinos, baixo, coro), `breaks`
  (breakcore com a bateria sintetizada) e `glory` (trombetas, trompas e coro heroico).
- Tormenta (fase 1, 160 bpm), Fúria (fase 2, 172 bpm), Abismo (fase 3, 180 bpm); tudo em ré menor
  com o segundo grau frígio (mi bemol) do Kraken; o Abismo vira ré maior e cita o tema do Porto.
- Stingers: transição de fase, vitória e recuo (a invocação é o do lote A0).

Uso (na raiz do repositório):
    tools/audio/.venv/Scripts/python tools/audio/kraken_music.py [tormenta furia abismo transition victory retreat]
"""
import sys

import numpy as np

import dsp
import instruments as I
from dsp import n_of
from master import export, export_stems

SCRIPT = "tools/audio/kraken_music.py"

# baixo e voicing médio (cordas, coro, trompas) de cada acorde
CHORDS = {
    "Dm": ("D2", ["D3", "F3", "A3", "D4"]),
    "Eb": ("Eb2", ["Eb3", "G3", "Bb3", "Eb4"]),
    "C": ("C2", ["C3", "E3", "G3", "C4"]),
    "Bb": ("Bb1", ["Bb2", "D3", "F3", "Bb3"]),
    "Gm": ("G2", ["G2", "Bb2", "D3", "G3"]),
    "A": ("A1", ["A2", "C#3", "E3", "A3"]),
    "F": ("F2", ["F2", "A2", "C3", "F3"]),
    "D": ("D2", ["D3", "F#3", "A3", "D4"]),
    "G": ("G2", ["G2", "B2", "D3", "G3"]),
}


class Song:
    """Um laço de `bars` compassos com as três camadas; tudo entra com volta circular."""

    def __init__(self, bpm, bars, seed):
        self.bpm, self.bars = bpm, bars
        self.beat = 60.0 / bpm
        self.N = n_of(bars * 4 * self.beat)
        self.r = dsp.rng(seed)
        self.stems = {k: np.zeros((2, self.N)) for k in ("base", "breaks", "glory")}
        self.prog = [None] * bars
        self.kit = I.Kit(self.r)

    def at(self, bar, beat=0.0):
        return n_of((bar * 4 + beat) * self.beat)

    def chords(self, start, names, bars_each=2):
        for k, name in enumerate(names):
            for b in range(bars_each):
                self.prog[start + k * bars_each + b] = name

    def play(self, stem, notes, bar, voice, **kw):
        I.render(self.stems[stem], I.shift(notes, bar * 4), self.bpm, voice, self.r, **kw)

    def hit(self, stem, x, bar, beat=0.0, pan=0.0, gain=1.0):
        src = dsp.pan(x, pan) if x.ndim == 1 else x
        dsp.place_wrap(self.stems[stem], src * gain, self.at(bar, beat))

    # ---------------------------------------------------------------- partes que seguem os acordes

    def ostinato(self, bars, pattern, sub=2, vel=0.7, octave=0, gain=1.0, stem="base"):
        """Cordas em staccato: pattern indexa [fundamental, quinta, oitava, terça] do acorde do compasso."""
        step = 1.0 / sub
        for bar in bars:
            _, v = CHORDS[self.prog[bar]]
            tones = [I.transpose(v[0], 12 * octave), I.transpose(v[2], 12 * octave), I.transpose(v[3], 12 * octave),
                     I.transpose(v[1], 12 * octave)]
            notes = []
            for k in range(int(4 * sub)):
                idx = pattern[k % len(pattern)]
                accent = 1.0 if k % sub == 0 else 0.75
                notes.append((k * step, step * 0.9, tones[idx], vel * accent * self.r.uniform(0.9, 1.05)))
            self.play(stem, notes, bar, I.strings_staccato, pan=0.0, spread=0.5, players=2, detune=6, sloppy_ms=4, gain=gain)

    def bassline(self, bars, rhythm, vel=0.9, grit=0.4, gain=0.55, octaves=False):
        """rhythm = [(tempo, duração)] por compasso; octaves=True alterna a oitava (graves duplos)."""
        for bar in bars:
            root, _ = CHORDS[self.prog[bar]]
            notes = []
            for k, (beat, dur) in enumerate(rhythm):
                name = root if not (octaves and k % 2) else I.transpose(root, 12)
                notes.append((beat, dur, name, vel))
            self.play("base", notes, bar, lambda f, d, r, v: I.bass(f, d, r, v, grit), gain=gain)

    def pads(self, bars, voice, vel=0.6, gain=1.0, stem="base", octave=0, spread=0.6, every=2, **kw):
        for bar in bars:
            if (bar - bars[0]) % every:
                continue
            _, v = CHORDS[self.prog[bar]]
            for k, name in enumerate(v):
                self.play(stem, [(0.0, 4.0 * every, I.transpose(name, 12 * octave), vel)], bar, voice,
                          pan=(k - 1.5) / 1.5 * spread, gain=gain / 2, **kw)

    def stabs(self, bars, beats, dur=0.5, vel=0.85, gain=1.0):
        """Golpes de trombeta nos tempos dados, com as duas notas de cima do acorde (oitava 4-5)."""
        for bar in bars:
            _, v = CHORDS[self.prog[bar]]
            top = [I.transpose(v[1], 12), I.transpose(v[3], 12)]
            notes = [(b, dur, name, vel) for b in beats for name in top]
            self.play("glory", notes, bar, I.trumpet, players=3, detune=7, spread=0.5, sloppy_ms=6, gain=gain)

    def taiko_pattern(self, bars, beats, vel=0.8, gain=0.6):
        for bar in bars:
            for k, b in enumerate(beats):
                self.hit("base", I.taiko(self.r, vel * (1.0 if k == 0 else 0.75), 1.0 + 0.04 * (k % 2)), bar, b, gain=gain)

    def glass_arps(self, bars, vel=0.35, gain=1.0, octave=2, direction=1):
        for bar in bars:
            _, v = CHORDS[self.prog[bar]]
            tones = [I.transpose(n, 12 * octave) for n in v][::direction]
            notes = [(k * 0.5, 0.5, tones[k % 4], vel * (1.0 if k % 2 == 0 else 0.7)) for k in range(8)]
            self.play("base", notes, bar, I.glass_bell, pan=0.35, gain=gain)

    def breaks(self, bars, source, intensity, fills=(), gain=1.0, halftime=False):
        src = I.break_source(source, self.bpm, self.r, self.kit)
        step_n = n_of(self.beat / 4)
        for bar in bars:
            if halftime:
                seg = I._resample(src[:, (bar % 2) * 16 * step_n:][:, :8 * step_n + 2], 0.5, 16 * step_n)
                if self.r.random() < 0.5:
                    k = int(self.r.integers(8, 15))
                    seg[:, k * step_n:(k + 1) * step_n] = seg[:, k * step_n:(k + 1) * step_n][:, ::-1]
                barx = I._edge(seg, 2.0)
            else:
                plan = I.chop_plan(self.r, intensity, fill=bar in fills, base=(bar % 2) * 16)
                barx = I.chop_bar(src, self.bpm, plan)
            self.hit("breaks", barx, bar, gain=gain)

    def crash(self, bars, vel=0.8, gain=1.0):
        for bar in bars:
            self.hit("breaks", I.cymbal(self.r, vel), bar, pan=-0.2, gain=gain)

    def bed(self, levels, note="D", gain=1.0):
        """Drone e tempestade (vento, mar) com volume por trecho; circular. levels = [(compasso, nível)]."""
        N, r = self.N, self.r
        pts = [(self.at(b) / dsp.SR, lv) for b, lv in levels]
        pts.append((N / dsp.SR, levels[0][1]))
        level = dsp.curve(N, pts)
        sub = 0.15 * dsp.osc(dsp.periodic(dsp.hz(note + "1"), N), N) + 0.3 * dsp.osc(dsp.periodic(dsp.hz(note + "2"), N), N, phase0=0.2)
        wind = dsp.circ_sweep(dsp.pink(N, r), "bp", 300 + 250 * dsp.periodic_wander(N, r, 9) ** 2 * 4, 1.2)
        waves = dsp.circ(lambda x: dsp.lp(x, 500, 2), dsp.brown(N, r)) * (0.5 + 0.5 * dsp.periodic_lfo(N, max(1, int(N / dsp.SR / 6)), 0.3))
        drone = dsp.haas(sub * 0.4, 9, 0.4, circular=True) + np.stack([wind, np.roll(wind, n_of(0.21))]) * 0.12 + \
            np.stack([waves, np.roll(waves, n_of(0.33))]) * 0.18
        self.stems["base"] += drone * level * gain

    # ---------------------------------------------------------------- mixagem das camadas

    def finish(self, name, title, listen):
        r = self.r
        hall = dsp.impulse_response(3.6, r, rt60_low=3.0, rt60_high=1.1, predelay=0.03, dark=9000)
        room = dsp.impulse_response(0.9, r, rt60_low=0.8, rt60_high=0.35, predelay=0.008, dark=12000)
        base = dsp.circ_reverb(self.stems["base"], hall, wet=0.32)
        base = np.stack([dsp.circ(lambda x: dsp.hp(dsp.eq(dsp.eq(x, "lowshelf", 160, 0.7, -8.0), "peak", 260, 0.9, -3.5), 35, 2), ch)
                         for ch in base])
        base = dsp.circ(lambda x: dsp.compress(x, -16, 2.0, 0.015, 0.25), base)
        brk = dsp.circ_reverb(self.stems["breaks"], room, wet=0.14)
        brk = np.stack([dsp.circ(lambda x: dsp.hp(dsp.eq(dsp.eq(x, "lowshelf", 100, 0.7, -4.5), "highshelf", 6000, 0.7, -4.5), 32, 2), ch)
                        for ch in brk])
        brk = dsp.circ(lambda x: dsp.compress(x * 1.3, -12, 4.0, 0.002, 0.08), brk)
        brk = dsp.saturate(brk * 1.2, 1.3)
        glory = dsp.circ_reverb(self.stems["glory"], hall, wet=0.38)
        glory = np.stack([dsp.circ(lambda x: dsp.hp(dsp.eq(dsp.eq(x, "peak", 2400, 0.9, 2.5), "peak", 1100, 0.8, 1.5), 120, 2), ch)
                          for ch in glory])
        glory = dsp.circ(lambda x: dsp.compress(x * 1.4, -18, 2.0, 0.02, 0.3), glory)
        return export_stems(name, {"base": base, "breaks": brk, "glory": glory}, SCRIPT, int(self.r.integers(1 << 30)),
                            title, listen, self.bpm, self.bars)


# ================================================================ motivos

# Motivo do Kraken (metais graves, meio tempo): desce em ré frígio e volta ao mi bemol.
KRAKEN_MOTIF = I.parse("D3/6! Eb3/2 | D3/4 C3/4 | Bb2/6 C3/2 | A2/8 | D3/6! Eb3/2 | F3/4 Eb3/4 | D3/4 C3/4 | D3/8", 0.9)


def tormenta(seed=1101):
    """Fase 1: marcial e crescente. Tambores surdos, ostinato de cordas, o motivo do Kraken nos metais
    graves; o breakcore entra abafado e abre; as trombetas chamam e depois tocam o tema heroico."""
    s = Song(160, 56, seed)
    s.chords(0, ["Dm", "Eb", "Dm", "C"])
    s.chords(8, ["Dm", "Eb", "Dm", "C", "Bb", "C", "Dm", "A"])
    s.chords(24, ["Bb", "C", "Dm", "Dm", "Bb", "C", "A", "A"])
    s.chords(40, ["Dm", "Bb", "C", "Dm", "Bb", "A"])
    s.chords(52, ["Eb", "A"])
    r = s.r

    # ---- base
    s.bed([(0, 1.0), (8, 0.6), (24, 0.45), (40, 0.25), (52, 0.6)])
    s.hit("base", I.impact(r, 1.0), 0)
    s.hit("base", I.church_bell(dsp.hz("D3"), r, 0.9), 0, pan=0.3)
    s.hit("base", I.church_bell(dsp.hz("D2"), r, 0.6), 0, pan=-0.3)
    s.taiko_pattern(range(0, 24), [0, 2.5, 3], vel=0.75)
    s.taiko_pattern(range(24, 52), [0, 1.5, 2, 3], vel=0.7, gain=0.8)
    s.ostinato(range(4, 56), [0, 0, 1, 0, 2, 0, 1, 3], sub=2, vel=0.6, gain=0.5, octave=1)
    s.play("base", KRAKEN_MOTIF, 8, I.low_brass, players=3, detune=6, spread=0.4, sloppy_ms=8, gain=0.55)
    s.play("base", KRAKEN_MOTIF, 16, I.low_brass, players=3, detune=6, spread=0.4, sloppy_ms=8, gain=0.6)
    s.play("base", [(st, d, I.transpose(n, 12), v * 0.7) for st, d, n, v in KRAKEN_MOTIF], 16, I.horn, players=2,
           detune=5, spread=0.6, gain=0.35)
    s.pads(range(24, 52), I.strings_long, vel=0.5, gain=0.5, octave=1)
    s.pads(range(24, 40), lambda f, d, rr, v: I.choir(f, d, rr, v, "o"), vel=0.45, gain=0.45, octave=0)
    s.bassline(range(0, 8), [(0, 3.8)], vel=0.8)
    s.bassline(range(8, 40), [(0, 1.8), (2, 1.8)], vel=0.85)
    s.bassline(range(40, 52), [(0, 0.9), (1, 0.9), (2, 0.9), (3, 0.9)], vel=0.9)
    s.bassline(range(52, 56), [(k * 0.5, 0.45) for k in range(8)], vel=0.9)
    for bar in (24, 28, 32, 36):
        root, _ = CHORDS[s.prog[bar]]
        s.hit("base", I.tubular_bell(dsp.hz(I.transpose(root, 24)), r, 0.5), bar, pan=0.4)
    s.glass_arps(range(24, 40), vel=0.3, gain=0.6)
    s.hit("base", I.timpani_roll(dsp.hz("A1"), 2 * 4 * s.beat, r, 0.9), 38)
    s.hit("base", I.timpani_roll(dsp.hz("D2"), 2 * 4 * s.beat, r, 0.9), 54)
    for bar in range(40, 52, 2):
        root, _ = CHORDS[s.prog[bar]]
        s.hit("base", I.timpani(dsp.hz(root), r, 0.8), bar)
    s.hit("base", I.impact(r, 0.9), 40)
    s.hit("base", I.church_bell(dsp.hz("D3"), r, 0.8), 40, pan=-0.3)
    s.hit("base", I.reverse_cymbal(2 * 4 * s.beat, r, 0.5), 22)
    s.hit("base", I.reverse_cymbal(2 * 4 * s.beat, r, 0.6), 38)
    s.hit("base", I.riser(2 * 4 * s.beat, r, 0.5), 54)

    # ---- breaks
    s.crash([0], 0.9)
    s.breaks(range(1, 8), "C", 0.1, gain=0.55)
    lo = s.at(1)
    hi = s.at(8)
    seg = s.stems["breaks"][:, lo:hi]
    cutoff = dsp.curve(hi - lo, [(0, 450), ((hi - lo) / dsp.SR, 3200)], "exp")
    s.stems["breaks"][:, lo:hi] = np.stack([dsp.sweep(ch, "lp", cutoff, 0.9) for ch in seg])
    s.breaks(range(8, 24), "A", 0.25, fills=(15, 23), gain=0.8)
    s.breaks(range(24, 40), "B", 0.35, fills=(31, 39), gain=0.85)
    s.breaks(range(40, 52), "A", 0.5, fills=(43, 47, 51), gain=0.95)
    s.breaks(range(52, 56), "C", 0.6, fills=(53, 55), gain=0.95)
    s.crash([8, 24, 40, 44, 48], 0.75)

    # ---- glória
    for bar in range(8, 24, 2):
        _, v = CHORDS[s.prog[bar]]
        s.play("glory", [(0, 7.5, I.transpose(v[1], 12), 0.45), (0, 7.5, I.transpose(v[2], 12), 0.45)], bar, I.horn,
               players=2, detune=5, spread=0.5, gain=0.4)
    calls = ("F4/1 Bb4/1 D5/6 | r/2 E5/1 G5/1 C6/4 | A4/1 D5/1 F5/2 A5/4 | G5/2 F5/2 E5/2 D5/2 | "
             "F5/1 Bb5/1 D6/6 | r/2 E5/1 G5/1 C6/4 | C#5/2 E5/2 A5/4 | G5/2 F5/2 E5/4")
    s.play("glory", I.parse(calls, 0.85), 24, I.trumpet, players=3, detune=7, spread=0.45, sloppy_ms=7, gain=0.7)
    hero = I.parse("A4/2 D5/2 F5/3 E5/1 | D5/3 C5/1 Bb4/2 D5/2 | E5/2 G5/2 C6/3 Bb5/1 | A5/6! F5/2 | "
                   "D6/3! C6/1 Bb5/2 A5/2 | C#6/3! B5/1 A5/4", 0.95)
    s.play("glory", hero, 40, I.trumpet, players=3, detune=7, spread=0.45, sloppy_ms=6, gain=0.85)
    s.play("glory", [(st, d, I.transpose(n, -12), v * 0.8) for st, d, n, v in hero], 40, I.horn, players=3, detune=6,
           spread=0.6, gain=0.5)
    s.pads(range(40, 52), lambda f, d, rr, v: I.choir(f, d, rr, v, "a"), vel=0.55, gain=0.55, stem="glory", octave=1)
    s.play("glory", I.parse("Bb4/2 Eb5/2 G5/4 | A4/2 C#5/2 E5/2 G5/2", 0.9), 52, I.trumpet, players=3, detune=7,
           spread=0.45, gain=0.75)
    return s.finish("music/kraken/tormenta", "Kraken: Tormenta (fase 1; ré menor, 160 bpm, 56 compassos em laço)",
                    "Marcha surda que cresce; o motivo do Kraken nos metais graves; o breakcore abre o filtro; "
                    "as trombetas chamam e o tema heroico explode no fim; a volta do laço cai no impacto.")


def furia(seed=1201):
    """Fase 2: graves duplos, coro gritando, breakcore inteiro, trombetas em golpes e o tema heroico."""
    s = Song(172, 64, seed)
    s.chords(0, ["Dm", "Bb", "Gm", "A"])
    s.chords(8, ["Dm", "Bb", "Gm", "A", "Dm", "Bb", "Eb", "A"])
    s.chords(24, ["Bb", "Bb", "Gm", "A"])
    s.chords(32, ["Dm", "Bb", "F", "C", "Gm", "Bb", "A", "A"])
    s.chords(48, ["Dm", "Eb", "Dm", "Eb", "Bb", "A"])
    s.chords(60, ["Gm", "A"])
    r = s.r

    # ---- base
    s.bed([(0, 0.5), (8, 0.3), (24, 0.8), (32, 0.25), (48, 0.35), (60, 0.5)])
    s.hit("base", I.impact(r, 1.0), 0)
    s.hit("base", I.church_bell(dsp.hz("D3"), r, 0.8), 0, pan=0.3)
    eighths = [(k * 0.5, 0.45) for k in range(8)]
    s.bassline(range(0, 24), eighths, vel=0.85, octaves=True, grit=0.6)
    s.bassline(range(32, 64), eighths, vel=0.9, octaves=True, grit=0.6)
    s.bassline(range(24, 32), [(0, 7.6)], vel=0.7)
    s.ostinato(range(8, 24), [0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 3, 2, 1, 0], sub=4, vel=0.55, gain=0.45, octave=1)
    s.ostinato(range(32, 60), [0, 1, 2, 1, 0, 1, 2, 3, 0, 1, 2, 1, 3, 2, 1, 2], sub=4, vel=0.6, gain=0.45, octave=1)
    motif2 = I.parse("D3/4! Eb3/2 D3/2 | D3/4 C3/2 Bb2/2 | Bb2/4 A2/2 G2/2 | A2/4 Bb2/2 A2/2 | "
                     "D3/4! Eb3/2 F3/2 | F3/4 Eb3/2 D3/2 | G3/4 F3/2 Eb3/2 | D3/2 C#3/2 A2/4", 0.95)
    s.play("base", motif2, 8, I.low_brass, players=3, detune=6, spread=0.4, sloppy_ms=6, gain=0.6)
    for bar in range(8, 24):
        root, v = CHORDS[s.prog[bar]]
        for b in (0, 2.5):
            for name in (v[0], v[2]):
                s.play("base", [(b, 0.4, I.transpose(name, 12), 0.9)], bar, I.chant, gain=0.28, pan=0.0, spread=0.5,
                       players=2)
    s.pads(range(24, 32), lambda f, d, rr, v: I.choir(f, d, rr, v, "o"), vel=0.55, gain=0.55)
    for bar in range(24, 32, 2):
        s.hit("base", I.church_bell(dsp.hz("D3"), r, 0.7), bar, pan=-0.35)
        s.hit("base", I.sub_drop(r, 0.6), bar)
    s.glass_arps(range(24, 32), vel=0.3, gain=0.7, direction=-1)
    s.pads(range(32, 48), I.strings_long, vel=0.5, gain=0.45, octave=1)
    s.taiko_pattern(range(0, 8), [0, 1.5, 3], vel=0.8)
    s.taiko_pattern(range(32, 48), [0, 2], vel=0.7, gain=0.7)
    for bar, note in ((30, "A1"), (62, "D2")):
        s.hit("base", I.timpani_roll(dsp.hz(note), 2 * 4 * s.beat, r, 0.9), bar)
        s.hit("base", I.riser(2 * 4 * s.beat, r, 0.55), bar)
    s.hit("base", I.impact(r, 0.95), 32)
    s.hit("base", I.church_bell(dsp.hz("D3"), r, 0.8), 32, pan=0.3)
    s.hit("base", I.reverse_cymbal(4 * s.beat, r, 0.6), 23)
    s.play("base", I.parse("D3/4! r/4 | Eb3/4! r/4 | D3/4! r/4 | Eb3/4! r/4 | Bb2/4! r/4 | A2/4! r/4", 1.0), 48,
           I.low_brass, players=3, detune=6, spread=0.4, gain=0.65)

    # ---- breaks
    s.crash([0, 8, 16, 32, 36, 40, 44, 48, 52, 56], 0.75)
    s.breaks(range(0, 8), "A", 0.5, fills=(7,), gain=0.9)
    s.breaks(range(8, 24), "B", 0.6, fills=(11, 15, 19, 23), gain=0.9)
    s.breaks(range(24, 32), "C", 0.0, gain=0.8, halftime=True)
    s.breaks(range(32, 48), "A", 0.7, fills=(35, 39, 43, 47), gain=0.95)
    s.breaks(range(48, 60), "C", 0.9, fills=(49, 51, 53, 55, 57, 59), gain=0.95)
    s.breaks(range(60, 64), "B", 0.9, fills=(60, 61, 62, 63), gain=0.95)

    # ---- glória
    s.stabs(range(8, 24), [1.5, 3.0], dur=0.45, vel=0.85, gain=0.55)
    hero = I.parse("D5/2 F5/2 A5/4! | Bb5/3 A5/1 F5/2 D5/2 | C5/2 F5/2 A5/3 G5/1 | G5/4 E5/2 C5/2 | "
                   "D5/2 G5/2 Bb5/3! A5/1 | F5/3 G5/1 F5/2 D5/2 | E5/2 A5/2 C#6/4! | D6/2 C#6/2 A5/4", 0.95)
    s.play("glory", hero, 32, I.trumpet, players=3, detune=7, spread=0.45, sloppy_ms=6, gain=0.85)
    s.play("glory", [(st, d, I.transpose(n, -12), v * 0.8) for st, d, n, v in hero], 32, I.horn, players=3, detune=6,
           spread=0.6, gain=0.5)
    s.pads(range(40, 48), lambda f, d, rr, v: I.choir(f, d, rr, v, "a"), vel=0.6, gain=0.55, stem="glory", octave=1)
    answers = I.parse("r/4 A4/1 D5/1 F5/2 | r/4 Bb4/1 Eb5/1 G5/2 | r/4 D5/1 F5/1 A5/2 | r/4 Eb5/1 G5/1 Bb5/2 | "
                      "r/4 D5/1 F5/1 Bb5/2 | r/4 C#5/1 E5/1 A5/2", 0.95)
    s.play("glory", answers, 48, I.trumpet, players=3, detune=7, spread=0.45, sloppy_ms=5, gain=0.8)
    s.play("glory", I.parse("D5/1 E5/1 F5/1 G5/1 | A5/4 | C#5/1 D5/1 E5/1 F5/1 | G5/2 A5/2 | "
                            "Bb5/2 A5/2 | G5/2 F5/2 | E5/4 | C#6/4!", 0.9), 60, I.trumpet, players=3, detune=7,
           spread=0.45, gain=0.75)
    return s.finish("music/kraken/furia", "Kraken: Fúria (fase 2; ré menor, 172 bpm, 64 compassos em laço)",
                    "Graves duplos e coro gritando com o motivo; breakcore picotado de verdade; quebra com sinos e "
                    "o break em meio tempo; o tema heroico das trombetas; a pergunta e resposta no fim.")


def abismo(seed=1301):
    """Fase 3: riff distorcido, sinos de catedral, breakcore no máximo; vira ré maior com o tema do Porto
    nas trombetas (a esperança), e volta ao duelo do motivo do Kraken contra as trombetas."""
    s = Song(180, 72, seed)
    s.chords(0, ["Dm", "Eb", "Dm", "Eb"])
    s.chords(8, ["Dm", "Eb", "Bb", "A", "Dm", "Eb", "Gm", "A"])
    s.chords(24, ["Bb", "A", "Bb", "A"])
    s.chords(32, ["Bb", "C", "D", "D", "Bb", "C", "D", "D", "G", "A", "D", "D"])
    s.chords(56, ["Dm", "Eb", "Bb", "A", "Dm", "A"])
    s.chords(68, ["Bb", "A"])
    r = s.r

    # ---- base
    s.bed([(0, 0.5), (8, 0.3), (24, 0.9), (32, 0.15), (56, 0.35), (68, 0.5)])
    for bar in (0, 32, 56):
        s.hit("base", I.impact(r, 1.0), bar)
    for bar in (0, 4, 56, 60):
        s.hit("base", I.church_bell(dsp.hz("D3"), r, 0.85), bar, pan=0.3)
        s.hit("base", I.church_bell(dsp.hz("A2"), r, 0.6), bar, 2, pan=-0.3)
    chug = [(k * 0.25, 0.22 if k % 3 else 0.24, k % 3 == 0 or k in (6, 14)) for k in range(16)]
    for bar in list(range(0, 24)) + list(range(56, 72)):
        root, _ = CHORDS[s.prog[bar]]
        f = I.transpose(root, 12 if dsp.hz(root) < dsp.hz("C2") else 0)
        notes = [(b, d, f, 0.95 if acc else 0.7) for b, d, acc in chug]
        s.play("base", notes, bar, lambda ff, dd, rr, vv: I.riff(ff, dd, rr, vv, mute=vv < 0.9), gain=0.38, pan=0.0)
    s.bassline(list(range(0, 24)) + list(range(32, 72)), [(k * 0.5, 0.45) for k in range(8)], vel=0.9, grit=0.7)
    s.bassline(range(24, 32), [(0, 7.6)], vel=0.75)
    motif3 = I.parse("D3/6! Eb3/2 | Eb3/6 D3/2 | D3/4 C3/4 | C#3/4 A2/4 | D3/6! Eb3/2 | G3/4 F3/2 Eb3/2 | D3/4 Bb2/4 | A2/8",
                     1.0)
    s.play("base", motif3, 8, I.low_brass, players=4, detune=7, spread=0.5, sloppy_ms=6, gain=0.6)
    s.pads(range(8, 24), lambda f, d, rr, v: I.choir(f, d, rr, v, "a"), vel=0.55, gain=0.5)
    s.pads(range(24, 32), lambda f, d, rr, v: I.choir(f, d, rr, v, "o"), vel=0.6, gain=0.6)
    s.glass_arps(range(24, 32), vel=0.32, gain=0.7, direction=-1)
    for bar in range(24, 32, 2):
        s.hit("base", I.sub_drop(r, 0.7), bar)
        s.hit("base", I.church_bell(dsp.hz("D2"), r, 0.6), bar, pan=0.2)
    s.pads(range(32, 56), I.strings_long, vel=0.55, gain=0.5, octave=1)
    s.ostinato(range(32, 56), [0, 1, 2, 1, 0, 1, 2, 3, 0, 1, 2, 1, 3, 2, 1, 2], sub=4, vel=0.55, gain=0.4, octave=1)
    rounds = ["D4", "C#4", "B3", "A3", "G3", "F#3", "E3", "D3"]
    for bar in range(32, 56, 2):
        for k, name in enumerate(rounds):
            s.hit("base", I.church_bell(dsp.hz(name), r, 0.32, dur=4.0), bar, k * 0.5, pan=-0.6 + 1.2 * k / 7)
    s.taiko_pattern(range(32, 56, 2), [0, 3], vel=0.75, gain=0.7)
    for bar, note in ((30, "A1"), (54, "D2"), (70, "A1")):
        s.hit("base", I.timpani_roll(dsp.hz(note), 2 * 4 * s.beat, r, 0.95), bar)
        s.hit("base", I.riser(2 * 4 * s.beat, r, 0.55), bar)
    s.play("base", I.parse("D3/6! Eb3/2 | Eb3/6 D3/2 | D3/4 C3/4 | C#3/4 A2/4 | D3/6! F3/2 | A2/8", 1.0), 56,
           I.low_brass, players=4, detune=7, spread=0.5, gain=0.65)

    # ---- breaks
    s.crash([0, 4, 8, 16, 32, 34, 36, 40, 44, 48, 52, 56, 60, 64], 0.8)
    s.breaks(range(0, 8), "A", 0.85, fills=(3, 7), gain=0.95)
    s.breaks(range(8, 24), "C", 0.9, fills=(11, 15, 19, 23), gain=0.95)
    s.breaks(range(24, 32), "B", 0.0, gain=0.85, halftime=True)
    s.breaks(range(32, 56), "A", 0.8, fills=(35, 39, 43, 47, 51, 55), gain=0.95)
    s.breaks(range(56, 68), "C", 1.0, fills=(57, 59, 61, 63, 65, 67), gain=1.0)
    s.breaks(range(68, 72), "B", 1.0, fills=(68, 69, 70, 71), gain=1.0)

    # ---- glória
    fanfare = I.parse("r/4 A4/0.5 A4/0.5 D5/1 F5/2 | r/4 Bb4/0.5 Bb4/0.5 Eb5/1 G5/2 | "
                      "r/4 D5/0.5 D5/0.5 F5/1 A5/2 | r/4 Eb5/0.5 Eb5/0.5 G5/1 Bb5/2", 0.95)
    s.play("glory", fanfare, 0, I.trumpet, players=3, detune=7, spread=0.45, sloppy_ms=5, gain=0.8)
    counter = I.parse("A5/4 F5/4 | G5/4 Bb5/4 | D5/2 F5/2 Bb5/4 | A5/2 G5/2 E5/4 | D5/2 F5/2 A5/4 | "
                      "Bb5/3 G5/1 Eb5/4 | D5/2 G5/2 Bb5/4 | C#6/4! A5/4", 0.9)
    s.play("glory", counter, 8, I.trumpet, players=3, detune=7, spread=0.45, sloppy_ms=6, gain=0.75)
    porto = I.parse("D5/2 F5/2 Bb5/4 | E5/2 G5/2 C6/4 | F#5/4! A5/2 G#5/2 | E5/6 F#5/2 | "
                    "D5/2 F5/2 Bb5/4 | E5/2 G5/2 C6/4 | A5/4! B5/2 A5/2 | F#5/8 | "
                    "G5/2 B5/2 D6/4 | E5/2 A5/2 C#6/4 | D6/8! | D6/4 A5/2 F#5/2", 0.95)
    s.play("glory", porto, 32, I.trumpet, players=3, detune=7, spread=0.45, sloppy_ms=6, gain=0.9)
    s.play("glory", [(st, d, I.transpose(n, -12), v * 0.8) for st, d, n, v in porto], 32, I.horn, players=3, detune=6,
           spread=0.6, gain=0.5)
    s.pads(range(32, 56), lambda f, d, rr, v: I.choir(f, d, rr, v, "a"), vel=0.6, gain=0.5, stem="glory", octave=1)
    duel = I.parse("A5/2 F5/2 D5/4 | Bb5/2 G5/2 Eb5/4 | F5/2 Bb5/2 D6/4! | E5/2 A5/2 C#6/4 | D6/4! A5/4 | "
                   "C#6/3 B5/1 A5/4", 0.95)
    s.play("glory", duel, 56, I.trumpet, players=3, detune=7, spread=0.45, sloppy_ms=5, gain=0.85)
    s.play("glory", I.parse("D5/2 F5/2 Bb5/4 | A5/4 C#6/4!", 0.95), 68, I.trumpet, players=3, detune=7, spread=0.45,
           gain=0.8)
    return s.finish("music/kraken/abismo", "Kraken: Abismo (fase 3; ré menor e ré maior, 180 bpm, 72 compassos em laço)",
                    "Riff distorcido e sinos de catedral; breakcore no máximo; a virada para ré maior com o tema do "
                    "Porto nas trombetas e os sinos repicando; o duelo final do motivo contra as trombetas.")


# ================================================================ stingers

def transition(seed=1401):
    """~3,6 s: o golpe da troca de fase (impacto, sino grave, cluster de metais e coro gritando) e uma
    subida que acaba no silêncio, onde a trilha da fase nova entra."""
    r = dsp.rng(seed)
    total = n_of(3.6)
    mix = np.zeros((2, total))
    dsp.place(mix, dsp.haas(I.impact(r, 1.0, 3.6), 7, 0.4), 0)
    dsp.place(mix, dsp.pan(I.church_bell(dsp.hz("D2"), r, 0.8, 3.6), -0.2), 0)
    for name in ("D2", "Eb2", "A2", "D3", "Eb3"):
        dsp.place(mix, dsp.pan(I.low_brass(dsp.hz(name), 1.2, r, 1.0), r.uniform(-0.5, 0.5)) * 0.35, 0)
    for name in ("D4", "Eb4", "A4"):
        dsp.place(mix, dsp.pan(I.choir(dsp.hz(name), 1.0, r, 0.9, "a", attack=0.03), r.uniform(-0.6, 0.6)) * 0.3, 0)
    dsp.place(mix, dsp.haas(I.riser(2.2, r, 0.7), 10, 0.6), n_of(1.2))
    dsp.place(mix, dsp.haas(I.reverse_cymbal(2.2, r, 0.6), 12, 0.7), n_of(1.2))
    ir = dsp.impulse_response(3.0, r, rt60_low=2.6, rt60_high=1.0, predelay=0.02, dark=8000)
    mix = dsp.fit(dsp.reverb(mix, ir, wet=0.3), total)
    mix = dsp.fade(mix, 0.002, 0.08)
    return export("music/kraken/transition", mix, "music", SCRIPT, seed,
                  "Stinger: troca de fase do Kraken (~3,6 s).",
                  "O golpe da troca de fase e a subida que acaba seca, onde a trilha nova entra.")


def victory(seed=1501):
    """~14 s: fanfarra de trombetas em ré maior, o tema do Porto, sinos repicando, coro e tímpanos."""
    r = dsp.rng(seed)
    bpm = 96.0
    beat = 60.0 / bpm
    total = n_of(15.0)
    mix = np.zeros((2, total))
    fanfare = I.parse("D5/0.33 D5/0.33 D5/0.34 F#5/1.5! A5/0.5 D6/3! | F#5/1 A5/0.5 G#5/0.5 E5/1.5 r/0.5 "
                      "D5/0.5 E5/0.5 F#5/1 C#5/1 | D5/6!", 0.95)
    I.render(mix, fanfare, bpm, I.trumpet, r, players=3, detune=7, spread=0.45, sloppy_ms=5, gain=0.9)
    I.render(mix, [(st, d, I.transpose(n, -12), v * 0.8) for st, d, n, v in fanfare], bpm, I.horn, r, players=3,
             detune=6, spread=0.6, gain=0.5)
    for k, chord in enumerate([["D3", "F#3", "A3", "D4"], ["E3", "G#3", "B3", "E4"], ["D3", "F#3", "A3", "D4"]]):
        start = [0, 5.5, 8.5][k]
        dur = [5.5, 3.0, 6.0][k]
        for j, name in enumerate(chord):
            for shift in (0, 12):
                x = I.choir(dsp.hz(name) * 2 ** (shift / 12), dur * beat, r, 0.55, "a")
                dsp.place(mix, dsp.pan(x, (j - 1.5) / 2), n_of(start * beat))
            x = I.strings_long(dsp.hz(name), dur * beat, r, 0.5)
            dsp.place(mix, dsp.pan(x, (j - 1.5) / 2.5) * 0.6, n_of(start * beat))
    dsp.place(mix, I.timpani_roll(dsp.hz("A1"), 1.3, r, 0.9), 0)
    for t, name in ((1.3, "D2"), (8.5 * beat, "D2"), (8.5 * beat + 0.3, "A1")):
        dsp.place(mix, I.timpani(dsp.hz(name), r, 1.0), n_of(t))
    peal = ["D5", "A4", "F#4", "D4", "E4", "C#4", "A3", "D3"]
    for k in range(24):
        name = peal[k % len(peal)]
        dsp.place(mix, dsp.pan(I.church_bell(dsp.hz(name), r, 0.35, 5.0), -0.7 + 1.4 * (k % 8) / 7), n_of(1.4 + k * 0.42))
    dsp.place(mix, dsp.haas(I.impact(r, 0.8, 3.0), 7, 0.4), n_of(8.5 * beat))
    I.render(mix, [(8.5 + k * 0.25, 0.25, n, 0.4) for k, n in enumerate(["D6", "A5", "F#5", "D5", "A5", "F#5", "D5", "A4"])],
             bpm, I.glass_bell, r, pan=0.4)
    ir = dsp.impulse_response(4.5, r, rt60_low=3.5, rt60_high=1.4, predelay=0.03, dark=10000)
    mix = dsp.fit(dsp.reverb(mix, ir, wet=0.35), total)
    mix = dsp.fade(mix, 0.002, 2.5)
    mix = np.stack([dsp.hp(ch, 30, 2) for ch in mix])
    return export("music/kraken/victory", mix, "music", SCRIPT, seed,
                  "Stinger: vitória sobre o Kraken (~15 s, ré maior).",
                  "Glória: trombetas com o tema do Porto, sinos repicando, coro e tímpanos; alívio no fim.")


def retreat(seed=1601):
    """~7 s: o Kraken volta às profundezas. Drone frio, sinos de vidro descendo e o motivo bem lento."""
    r = dsp.rng(seed)
    total = n_of(7.5)
    mix = np.zeros((2, total))
    n = total
    drone = dsp.osc(dsp.hz("D1"), n) * 0.5 + dsp.lp(dsp.supersaw(dsp.hz("D2"), n, r, 5, 10), 500, 2) * 0.5
    drone *= dsp.curve(n, [(0, 0), (0.8, 1), (5.0, 0.8), (7.5, 0)])
    dsp.place(mix, dsp.haas(drone * 0.5, 10, 0.5), 0)
    I.render(mix, I.parse("D3/3 Eb3/1.5 D3/3", 0.8), 60, I.low_brass, r, players=3, detune=6, spread=0.4, gain=0.45)
    I.render(mix, [(0.5 + k * 0.7, 1.5, n_, 0.4) for k, n_ in enumerate(["A5", "F5", "D5", "Eb5", "D5", "A4"])], 60,
             I.glass_bell, r, pan=0.3)
    wind = dsp.bp(dsp.pink(n, r), 200, 900) * dsp.curve(n, [(0, 0), (1.5, 0.3), (7.5, 0)])
    dsp.place(mix, dsp.haas(wind * 0.25, 13, 0.7), 0)
    ir = dsp.impulse_response(5.0, r, rt60_low=4.5, rt60_high=1.3, predelay=0.04, dark=6000)
    mix = dsp.fit(dsp.reverb(mix, ir, wet=0.5), total)
    mix = dsp.fade(mix, 0.05, 2.0)
    return export("music/kraken/retreat", mix, "music", SCRIPT, seed,
                  "Stinger: o Kraken retorna às profundezas (~7,5 s).",
                  "Frio e sem resolução: o motivo do Kraken some no fundo.")


SOUNDS = {"tormenta": tormenta, "furia": furia, "abismo": abismo, "transition": transition, "victory": victory,
          "retreat": retreat}

if __name__ == "__main__":
    for key in (sys.argv[1:] or SOUNDS):
        SOUNDS[key]()
