"""Biblioteca de síntese do Aquamarine (docs/09-audio.md): tudo original, gerado por código.

Sinais em float64 a 48 kHz; mono é um vetor (n,), estéreo é (2, n). Cada som sai de uma semente
fixa (rng), então rodar de novo dá o mesmo arquivo. Sem amostras de terceiros.

Blocos: osciladores sem aliasing (polyBLEP) com frequência variável, ruídos coloridos, filtros
estáticos e variáveis no tempo, formantes (vozes e coro), síntese modal (sinos, vidro, marimba,
casco de metal), Karplus-Strong (harpa), bolhas físicas (Minnaert), reverb por convolução com
resposta ao impulso gerada (banda a banda), saturação, compressor, limitador e laço sem emenda.
"""
import numpy as np
from scipy import signal

SR = 48000


def rng(seed):
    return np.random.default_rng(seed)


def n_of(seconds):
    return int(round(seconds * SR))


def time(n):
    return np.arange(n) / SR


def db(x):
    return 10.0 ** (x / 20.0)


# ---------------------------------------------------------------- envelopes e automação

def curve(n, points, shape="lin"):
    """Automação por pontos (tempo em s, valor). shape "exp" interpola em escala log (valores > 0)."""
    ts = np.array([p[0] for p in points], dtype=float)
    vs = np.array([p[1] for p in points], dtype=float)
    t = time(n)
    if shape == "exp":
        return np.exp(np.interp(t, ts, np.log(np.maximum(vs, 1e-9))))
    return np.interp(t, ts, vs)


def adsr(n, a=0.01, d=0.1, s=0.7, r=0.3, hold=None):
    """ADSR com curvas suaves; hold = tempo até soltar (padrão: n - r)."""
    t = time(n)
    total = n / SR
    hold = total - r if hold is None else hold
    env = np.empty(n)
    att = t < a
    env[att] = np.sin(0.5 * np.pi * t[att] / max(a, 1e-6)) ** 2
    dec = (t >= a) & (t < hold)
    env[dec] = s + (1 - s) * np.exp(-(t[dec] - a) / max(d, 1e-6) * 4)
    rel = t >= hold
    level = s + (1 - s) * np.exp(-(max(hold - a, 0)) / max(d, 1e-6) * 4)
    env[rel] = level * np.exp(-(t[rel] - hold) / max(r, 1e-6) * 5)
    return env


def decay(n, tau, attack=0.002):
    t = time(n)
    return (1 - np.exp(-t / max(attack, 1e-6))) * np.exp(-t / tau)


def smooth_noise(n, rate_hz, r, octaves=1):
    """Modulação lenta e orgânica (ruído interpolado), média 0 e amplitude ~1."""
    out = np.zeros(n)
    for o in range(octaves):
        rate = rate_hz * (2 ** o)
        k = max(int(n / SR * rate) + 3, 4)
        pts = r.standard_normal(k)
        xs = np.linspace(0, n, k)
        out += np.interp(np.arange(n), xs, pts) / (2 ** o)
    out = signal.sosfiltfilt(signal.butter(2, max(rate_hz * 2, 0.5), fs=SR, output="sos"), out)
    return out / (np.std(out) + 1e-12)


# ---------------------------------------------------------------- osciladores

def phase_of(freq, n):
    """Fase acumulada (em ciclos) para frequência constante ou variável."""
    f = np.broadcast_to(np.asarray(freq, dtype=float), (n,))
    return np.cumsum(f) / SR, f / SR


def _polyblep(p, dt):
    p = p % 1.0
    out = np.zeros_like(p)
    m = p < dt
    x = p[m] / dt[m]
    out[m] = x + x - x * x - 1.0
    m2 = p > 1.0 - dt
    x = (p[m2] - 1.0) / dt[m2]
    out[m2] = x * x + x + x + 1.0
    return out


def osc(freq, n, shape="sine", phase0=0.0, pulse=0.5):
    ph, dt = phase_of(freq, n)
    ph = ph + phase0
    if shape == "sine":
        return np.sin(2 * np.pi * ph)
    dt = np.maximum(dt, 1e-9)
    p = ph % 1.0
    if shape == "saw":
        return 2 * p - 1 - _polyblep(p, dt)
    if shape == "square":
        sq = np.where(p < pulse, 1.0, -1.0)
        return sq + _polyblep(p, dt) - _polyblep(p + (1 - pulse), dt)
    if shape == "tri":
        sq = np.where(p < 0.5, 1.0, -1.0) + _polyblep(p, dt) - _polyblep(p + 0.5, dt)
        tri = np.cumsum(sq) * 4 * dt
        return signal.sosfilt(signal.butter(1, 2.0, "high", fs=SR, output="sos"), tri)
    raise ValueError(shape)


def supersaw(freq, n, r, voices=7, detune_cents=14, mix_center=0.35):
    out = np.zeros(n)
    f = np.asarray(freq, dtype=float)
    for k in range(voices):
        c = (k - (voices - 1) / 2) / ((voices - 1) / 2 or 1) * detune_cents
        drift = 1 + 0.0015 * smooth_noise(n, 0.15, r)
        gain = mix_center if k == voices // 2 else (1 - mix_center) / (voices - 1) * 2
        out += gain * osc(f * 2 ** (c / 1200) * drift, n, "saw", phase0=r.random())
    return out / voices * 2


def fm(carrier, ratio, index, n, phase0=0.0):
    """FM de dois operadores; index pode variar no tempo (brilho)."""
    mod_ph, _ = phase_of(np.asarray(carrier, dtype=float) * ratio, n)
    car_ph, _ = phase_of(carrier, n)
    return np.sin(2 * np.pi * (car_ph + phase0) + np.asarray(index) * np.sin(2 * np.pi * mod_ph))


# ---------------------------------------------------------------- ruídos

def white(n, r):
    return r.standard_normal(n)


def pink(n, r):
    spec = np.fft.rfft(r.standard_normal(n))
    f = np.fft.rfftfreq(n, 1 / SR)
    f[0] = f[1]
    x = np.fft.irfft(spec / np.sqrt(f), n)
    return x / (np.std(x) + 1e-12)


def brown(n, r):
    spec = np.fft.rfft(r.standard_normal(n))
    f = np.fft.rfftfreq(n, 1 / SR)
    f[0] = f[1]
    x = np.fft.irfft(spec / np.maximum(f, 8.0), n)
    return x / (np.std(x) + 1e-12)


# ---------------------------------------------------------------- filtros

def lp(x, fc, order=4):
    return signal.sosfilt(signal.butter(order, min(fc, SR * 0.45), "low", fs=SR, output="sos"), x)


def hp(x, fc, order=4):
    return signal.sosfilt(signal.butter(order, fc, "high", fs=SR, output="sos"), x)


def bp(x, lo, hi, order=2):
    return signal.sosfilt(signal.butter(order, [lo, min(hi, SR * 0.45)], "band", fs=SR, output="sos"), x)


def _rbj(kind, f0, q, gain_db=0.0):
    w0 = 2 * np.pi * min(max(f0, 10.0), SR * 0.45) / SR
    alpha = np.sin(w0) / (2 * q)
    cw = np.cos(w0)
    A = 10 ** (gain_db / 40)
    if kind == "lp":
        b = [(1 - cw) / 2, 1 - cw, (1 - cw) / 2]
        a = [1 + alpha, -2 * cw, 1 - alpha]
    elif kind == "hp":
        b = [(1 + cw) / 2, -(1 + cw), (1 + cw) / 2]
        a = [1 + alpha, -2 * cw, 1 - alpha]
    elif kind == "bp":
        b = [alpha, 0, -alpha]
        a = [1 + alpha, -2 * cw, 1 - alpha]
    elif kind == "peak":
        b = [1 + alpha * A, -2 * cw, 1 - alpha * A]
        a = [1 + alpha / A, -2 * cw, 1 - alpha / A]
    elif kind == "lowshelf":
        sq = 2 * np.sqrt(A) * alpha
        b = [A * ((A + 1) - (A - 1) * cw + sq), 2 * A * ((A - 1) - (A + 1) * cw), A * ((A + 1) - (A - 1) * cw - sq)]
        a = [(A + 1) + (A - 1) * cw + sq, -2 * ((A - 1) + (A + 1) * cw), (A + 1) + (A - 1) * cw - sq]
    elif kind == "highshelf":
        sq = 2 * np.sqrt(A) * alpha
        b = [A * ((A + 1) + (A - 1) * cw + sq), -2 * A * ((A - 1) + (A + 1) * cw), A * ((A + 1) + (A - 1) * cw - sq)]
        a = [(A + 1) - (A - 1) * cw + sq, 2 * ((A - 1) - (A + 1) * cw), (A + 1) - (A - 1) * cw - sq]
    else:
        raise ValueError(kind)
    b = np.array(b) / a[0]
    a = np.array(a) / a[0]
    return b, a


def eq(x, kind, f0, q=0.707, gain_db=0.0):
    b, a = _rbj(kind, f0, q, gain_db)
    return signal.lfilter(b, a, x)


def sweep(x, kind, f0, q=0.707, block=64):
    """Filtro RBJ variável no tempo (f0 é um vetor por amostra), em blocos curtos com estado contínuo."""
    f0 = np.broadcast_to(np.asarray(f0, dtype=float), x.shape)
    q = np.broadcast_to(np.asarray(q, dtype=float), x.shape)
    out = np.empty_like(x)
    zi = np.zeros(2)
    for s in range(0, len(x), block):
        e = min(s + block, len(x))
        b, a = _rbj(kind, float(f0[s]), float(q[s]))
        out[s:e], zi = signal.lfilter(b, a, x[s:e], zi=zi)
    return out


# Formantes (F, largura de banda em Hz, ganho dB) de vogais cantadas; tenor/baixo e soprano.
VOWELS = {
    "a": [(650, 80, 0), (1080, 90, -6), (2650, 120, -7), (2900, 130, -8), (3250, 140, -22)],
    "o": [(400, 70, 0), (800, 80, -10), (2600, 100, -12), (2800, 130, -12), (3000, 135, -26)],
    "u": [(350, 40, 0), (600, 60, -20), (2400, 100, -32), (2675, 120, -28), (2950, 120, -36)],
    "e": [(400, 70, 0), (1700, 80, -9), (2600, 100, -8), (3200, 120, -10), (3580, 120, -14)],
    "i": [(290, 40, 0), (1870, 90, -15), (2800, 100, -18), (3250, 120, -20), (3540, 120, -30)],
}


def formants(x, vowel="a", scale=1.0, mix=None):
    """Banco de formantes em paralelo. scale < 1 = trato vocal maior (criatura enorme)."""
    out = np.zeros_like(x)
    table = VOWELS[vowel] if isinstance(vowel, str) else vowel
    for f, bw, g in table:
        fc = f * scale
        b, a = _rbj("bp", fc, max(fc / max(bw * scale, 1.0), 0.5))
        out += db(g) * signal.lfilter(b, a, x)
    return out


def morph_formants(x, vowels, scale=1.0):
    """Muda de vogal no tempo: vowels = [(tempo_s, "a"), (tempo_s, "o"), ...]."""
    n = len(x)
    t = time(n)
    ts = [v[0] for v in vowels]
    outs = [formants(x, v[1], scale) for v in vowels]
    weights = [np.interp(t, ts, [1.0 if j == k else 0.0 for j in range(len(vowels))]) for k in range(len(vowels))]
    return sum(w * o for w, o in zip(weights, outs))


# ---------------------------------------------------------------- síntese física

def modal(n, freqs, decays, amps, r, strike=0.002, detune=0.0):
    """Soma de modos amortecidos (sino, vidro, barra, chapa de metal), com toque de ruído."""
    t = time(n)
    out = np.zeros(n)
    for f, d, a in zip(freqs, decays, amps):
        if f >= SR * 0.45:
            continue
        f = f * (1 + detune * r.standard_normal())
        out += a * np.sin(2 * np.pi * f * t + r.random() * 2 * np.pi) * np.exp(-t / d)
    att = 1 - np.exp(-t / strike)
    tick = lp(r.standard_normal(n) * np.exp(-t / 0.004), 6000) * 0.08
    return out * att + tick


def karplus(freq, n, r, damping=0.996, bright=0.5):
    """Corda dedilhada (Karplus-Strong vetorizado por período)."""
    L = max(int(SR / freq), 2)
    frac = SR / freq - L
    y = np.zeros(n + L + 2)
    burst = r.uniform(-1, 1, L)
    burst = lp(burst, 1500 + 9000 * bright, order=1)
    y[:L] = burst
    k = L
    while k < n + L:
        e = min(k + L, n + L)
        prev = y[k - L:e - L]
        prev2 = y[k - L - 1:e - L - 1] if k - L - 1 >= 0 else np.concatenate([[0.0], y[k - L:e - L - 1]])
        mix = (1 - frac) * prev + frac * prev2
        y[k:e] = damping * (0.5 * mix + 0.5 * np.concatenate([[mix[0]], mix[:-1]]))
        k = e
    return y[L:L + n]


def bubble(n, radius_mm, r, depth=0.6):
    """Bolha de Minnaert: f0 = 3,26 / raio; o tom sobe enquanto ela se forma (van den Doel)."""
    t = time(n)
    f0 = 3.26 / (radius_mm / 1000.0)
    tau = 1.0 / (0.13 * f0 + 0.0072 * f0 ** 1.5)
    sigma = 0.1 * depth / tau
    f = f0 * (1 + sigma * t)
    env = np.exp(-t / tau)
    return np.sin(2 * np.pi * np.cumsum(f) / SR) * env


def bubbles(n, r, rate_per_s, radius_mm=(1.0, 6.0), rise=True, gain=1.0):
    """Nuvem de bolhas: tempos de Poisson, raios log-uniformes (as maiores são mais raras)."""
    out = np.zeros(n)
    count = r.poisson(rate_per_s * n / SR)
    lo, hi = np.log(radius_mm[0]), np.log(radius_mm[1])
    for _ in range(count):
        rad = float(np.exp(r.uniform(lo, hi)))
        start = int(r.uniform(0, n))
        dur = min(n - start, n_of(0.25))
        if dur < 32:
            continue
        b = bubble(dur, rad, r, depth=r.uniform(0.3, 1.0) if rise else 0.0)
        out[start:start + dur] += b * (rad / radius_mm[1]) ** 0.6 * r.uniform(0.4, 1.0)
    return out * gain


def creak(n, r, base=180.0, pitch_spread=0.3, rate=(20, 90)):
    """Rangido de casco: atrito stick-slip (pulsos de taxa variável) num banco de ressonâncias de chapa."""
    t = time(n)
    rate_curve = np.interp(t, np.linspace(0, n / SR, 6), r.uniform(*rate, 6))
    ph = np.cumsum(rate_curve) / SR
    clicks = np.diff(np.floor(ph), prepend=0.0)
    clicks *= r.uniform(0.5, 1.0, n)
    exc = signal.lfilter([1.0], [1.0, -0.6], clicks)
    ratios = [1.0, 1.59, 2.14, 2.65, 3.17, 4.07, 5.4]
    res = np.zeros(n)
    for k, ratio in enumerate(ratios):
        f = base * ratio * (1 + pitch_spread * 0.1 * r.standard_normal())
        b, a = _rbj("bp", f, 18 + 6 * k)
        res += signal.lfilter(b, a, exc) / (1 + 0.5 * k)
    env = np.sin(np.pi * np.clip(t / (n / SR), 0, 1)) ** 0.7
    return res * env


# ---------------------------------------------------------------- espaço: reverb por convolução

def impulse_response(seconds, r, rt60_low=4.0, rt60_high=1.2, predelay=0.02, early=12, dark=9000.0,
                     width=1.0):
    """IR estéreo gerada: reflexões iniciais esparsas + cauda de ruído decorrelacionado com
    decaimento por banda (graves longos, agudos curtos), como uma sala grande ou o fundo do mar."""
    n = n_of(seconds)
    t = time(n)
    bands = [(20, 250), (250, 1000), (1000, 4000), (4000, 16000)]
    rts = np.geomspace(rt60_low, rt60_high, len(bands))
    ir = np.zeros((2, n))
    for ch in range(2):
        noise = r.standard_normal(n)
        tail = np.zeros(n)
        for (lo, hi), rt in zip(bands, rts):
            tail += bp(noise, lo, min(hi, SR * 0.45), order=2) * np.exp(-6.9078 * t / rt)
        fade_in = 1 - np.exp(-t / 0.03)
        ir[ch] = tail * fade_in
        for _ in range(early):
            d = int(r.uniform(0.004, 0.08) * SR)
            if d < n:
                ir[ch, d] += r.uniform(-0.6, 0.6)
    mid = (ir[0] + ir[1]) / 2
    side = (ir[0] - ir[1]) / 2 * width
    ir = np.stack([mid + side, mid - side])
    ir = np.stack([lp(ch, dark, order=2) for ch in ir])
    pre = n_of(predelay)
    ir = np.concatenate([np.zeros((2, pre)), ir], axis=1)
    return ir / np.sqrt(np.sum(ir ** 2) / 2)


def reverb(x, ir, wet=0.3, dry=1.0):
    """Convolução; x mono vira estéreo. A cauda fica no fim do vetor (comprimento maior)."""
    if x.ndim == 1:
        x = np.stack([x, x])
    n = x.shape[1] + ir.shape[1] - 1
    out = np.zeros((2, n))
    for ch in range(2):
        out[ch] = signal.oaconvolve(x[ch], ir[ch]) * wet
    out[:, :x.shape[1]] += x * dry
    return out


def mono_reverb(x, ir, wet=0.3):
    """Para efeitos posicionais (mono): soma os dois canais da IR."""
    m = signal.oaconvolve(x, (ir[0] + ir[1]) / 2)
    m *= wet
    m[:len(x)] += x
    return m


# ---------------------------------------------------------------- estéreo e mixagem

def pan(x, position):
    """Pan de potência constante: -1 esquerda, 0 centro, 1 direita (position pode variar)."""
    a = (np.asarray(position) + 1) * np.pi / 4
    return np.stack([x * np.cos(a), x * np.sin(a)])


def haas(x, ms=12.0, side_gain=0.6, circular=False):
    """Alarga um mono com atraso curto no lado; circular=True para camadas em laço."""
    d = n_of(ms / 1000)
    l = x
    if circular:
        rr = np.roll(x, d)
    else:
        rr = np.concatenate([np.zeros(d), x[:-d]]) if d else x
    mid = (l + rr) / 2
    side = (l - rr) / 2 * side_gain
    return np.stack([mid + side, mid - side])


def fit(x, n):
    """Corta ou completa com zeros até n amostras (mono ou estéreo)."""
    if x.ndim == 1:
        return x[:n] if len(x) >= n else np.concatenate([x, np.zeros(n - len(x))])
    return x[:, :n] if x.shape[1] >= n else np.concatenate([x, np.zeros((x.shape[0], n - x.shape[1]))], axis=1)


def place(dst, src, start):
    """Soma src em dst a partir de start (amostras), cortando no fim. Mono ou estéreo."""
    if start >= dst.shape[-1]:
        return dst
    m = min(src.shape[-1], dst.shape[-1] - start)
    if dst.ndim == 1:
        dst[start:start + m] += src[:m] if src.ndim == 1 else src.mean(axis=0)[:m]
    else:
        s = src if src.ndim == 2 else np.stack([src, src])
        dst[:, start:start + m] += s[:, :m]
    return dst


def place_wrap(dst, src, start):
    """Como place, mas o que passa do fim volta ao começo (camadas em laço perfeito)."""
    n = dst.shape[-1]
    start %= n
    m = src.shape[-1]
    first = min(m, n - start)
    place(dst, src[..., :first], start)
    rest = src[..., first:]
    while rest.shape[-1] > 0:
        k = min(rest.shape[-1], n)
        place(dst, rest[..., :k], 0)
        rest = rest[..., k:]
    return dst


def loop_crossfade(x, n, xf):
    """Laço sem emenda: x tem n + xf amostras; o fim entra no começo em potência constante."""
    t = np.linspace(0, np.pi / 2, xf)
    out = x[..., :n].copy()
    out[..., :xf] = x[..., :xf] * np.sin(t) + x[..., n:n + xf] * np.cos(t)
    return out


# ---------------------------------------------------------------- laços circulares (emenda perfeita)
# Uma camada de N amostras é circular quando tudo nela é periódico em N: ruído feito por FFT,
# filtros em regime permanente, osciladores com número inteiro de ciclos, eventos com place_wrap
# e reverb por convolução circular. Aí o fim encaixa exatamente no começo, sem crossfade.

def periodic(freq, n):
    """Frequência mais próxima com número inteiro de ciclos em n amostras."""
    return max(round(freq * n / SR), 1) * SR / n


def periodic_lfo(n, cycles, phase=0.0):
    """Senoide lenta com número inteiro de ciclos em n (valores de -1 a 1)."""
    return np.sin(2 * np.pi * (cycles * np.arange(n) / n + phase))


def periodic_wander(n, r, max_cycles=6):
    """Modulação orgânica periódica: soma de senoides lentas com ciclos inteiros (média 0, ~±1)."""
    out = np.zeros(n)
    for c in range(1, max_cycles + 1):
        out += r.standard_normal() / c * periodic_lfo(n, c, r.random())
    return out / (np.abs(out).max() + 1e-12)


def circ(fn, x):
    """Aplica um filtro em regime permanente circular (processa duas voltas e fica com a segunda)."""
    n = x.shape[-1]
    return fn(np.concatenate([x, x], axis=-1))[..., n:]


def circ_sweep(x, kind, f0, q=0.707):
    f0 = np.broadcast_to(np.asarray(f0, dtype=float), x.shape)
    return circ(lambda y: sweep(y, kind, np.concatenate([f0, f0]), q), x)


def circ_reverb(x, ir, wet=0.3):
    """Reverb por convolução circular: a cauda do fim cai no começo (laço perfeito)."""
    if x.ndim == 1:
        x = np.stack([x, x])
    n = x.shape[-1]
    out = x.copy()
    for ch in range(2):
        out[ch] += wet * np.fft.irfft(np.fft.rfft(x[ch]) * np.fft.rfft(ir[ch], n), n)
    return out


# ---------------------------------------------------------------- dinâmica e cor

def saturate(x, drive=1.5):
    return np.tanh(x * drive) / np.tanh(drive)


def compress(x, threshold_db=-18.0, ratio=3.0, attack=0.005, release=0.15, makeup_db=0.0):
    mono = np.abs(x).max(axis=0) if x.ndim == 2 else np.abs(x)
    a_att = np.exp(-1 / (attack * SR))
    a_rel = np.exp(-1 / (release * SR))
    env = signal.lfilter([1 - a_rel], [1, -a_rel], mono)
    env = np.maximum(env, signal.lfilter([1 - a_att], [1, -a_att], mono))
    level = 20 * np.log10(env + 1e-9)
    over = np.maximum(level - threshold_db, 0)
    gain = db(-over * (1 - 1 / ratio) + makeup_db)
    return x * gain


def limit(x, ceiling_db=-1.0, lookahead=0.005, release=0.08):
    """Limitador com antecipação: ganho = mínimo móvel do teto/pico, suavizado."""
    ceiling = db(ceiling_db)
    peak = np.abs(x).max(axis=0) if x.ndim == 2 else np.abs(x)
    la = n_of(lookahead)
    need = np.minimum(1.0, ceiling / np.maximum(peak, 1e-9))
    pad = np.concatenate([need, np.ones(la)])
    from numpy.lib.stride_tricks import sliding_window_view
    gmin = sliding_window_view(pad, la + 1).min(axis=1)[:len(need)]
    a = np.exp(-1 / (release * SR))
    g = signal.lfilter([1 - a], [1, -a], gmin - 1) + 1
    g = np.minimum(g, gmin)
    return x * g


def normalize_peak(x, peak_db=-3.0):
    return x * db(peak_db) / (np.abs(x).max() + 1e-12)


def fade(x, fin=0.005, fout=0.05):
    n = x.shape[-1]
    env = np.ones(n)
    a, b = n_of(fin), n_of(fout)
    if a:
        env[:a] = np.sin(np.linspace(0, np.pi / 2, a)) ** 2
    if b:
        env[-b:] = np.cos(np.linspace(0, np.pi / 2, b)) ** 2
    return x * env


def underwater(x, cutoff=3500.0, depth=0.6):
    """Cor de debaixo d'água: corta agudos, realça graves e um leve chorus lento."""
    y = lp(x, cutoff, order=2) if x.ndim == 1 else np.stack([lp(c, cutoff, order=2) for c in x])
    y = eq(y, "lowshelf", 180, 0.7, 3.0 * depth) if y.ndim == 1 else np.stack([eq(c, "lowshelf", 180, 0.7, 3.0 * depth) for c in y])
    return y


# ---------------------------------------------------------------- música

NOTE = {"C": 0, "C#": 1, "Db": 1, "D": 2, "D#": 3, "Eb": 3, "E": 4, "F": 5, "F#": 6, "Gb": 6, "G": 7,
        "G#": 8, "Ab": 8, "A": 9, "A#": 10, "Bb": 10, "B": 11}


def hz(name):
    """'A4' -> 440; aceita sustenidos e bemóis ('F#3', 'Eb2')."""
    pitch, octave = name[:-1], int(name[-1])
    return 440.0 * 2 ** ((NOTE[pitch] + 12 * (octave + 1) - 69) / 12)
