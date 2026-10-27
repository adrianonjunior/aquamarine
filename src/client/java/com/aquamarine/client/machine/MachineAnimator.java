package com.aquamarine.client.machine;

import com.aquamarine.machine.MachineMotion;
import com.aquamarine.machine.MachineState;
import java.util.Map;
import net.minecraft.client.animation.AnimationDefinition;

/**
 * Escolhe e mistura os clipes de uma máquina pelo estado (0.6.1, 7B; docs/12-industria.md, "Animações (padrão)"):
 * parada toca {@code idle}; ao ligar, {@code start} e depois {@code run} na velocidade da produção; na sobrecarga,
 * {@code overload}; ao desligar, {@code stop} e volta ao {@code idle}. Cada troca passa por uma mistura de 0,3 s.
 * Também calcula as cores: o brilho fica baixo parado, pulsa a cada golpe funcionando e avermelha na sobrecarga.
 */
public final class MachineAnimator {
	private static final double FADE = 0.3;
	private static final float IDLE_GLOW = 0.45F;

	private MachineAnimator() {}

	public static void animate(MachineMotion motion, double now, Map<String, AnimationDefinition> clips, MachineRenderState out) {
		out.layers = 0;
		double el = now - motion.since();
		MachineState state = motion.state(), previous = motion.previous();
		double start = length(clips, "start"), stop = length(clips, "stop");
		float glow;
		float red = 0.0F;
		switch (state) {
			case RUNNING -> {
				if (previous == MachineState.IDLE && el < start) {
					add(out, clips, "start", el, 1.0F);
					glow = lerp(IDLE_GLOW, 1.0F, (float) (el / start));
				} else {
					float in = fade(previous == MachineState.OVERLOAD ? el : Double.POSITIVE_INFINITY);
					add(out, clips, "run", motion.runPhase(now), in);
					add(out, clips, "overload", el, 1.0F - in);
					red = 1.0F - in;
					glow = 1.0F;
				}
			}
			case OVERLOAD -> {
				float in = fade(el);
				add(out, clips, "overload", el, in);
				add(out, clips, previous == MachineState.IDLE ? "idle" : "run", previous == MachineState.IDLE ? now : motion.runPhase(now), 1.0F - in);
				red = in;
				glow = previous == MachineState.IDLE ? lerp(IDLE_GLOW, 1.0F, in) : 1.0F;
			}
			case FULL -> {
				float in = fade(el);
				add(out, clips, clips.containsKey("full") ? "full" : "idle", el, in);
				add(out, clips, "run", motion.runPhase(now), 1.0F - in);
				glow = lerp(1.0F, 0.8F, in);
			}
			default -> {
				boolean stopping = previous != MachineState.IDLE && el < stop;
				if (stopping) {
					float in = fade(el);
					add(out, clips, "stop", el, in);
					add(out, clips, previous == MachineState.OVERLOAD ? "overload" : "run",
						previous == MachineState.OVERLOAD ? el : motion.runPhase(now), 1.0F - in);
					red = previous == MachineState.OVERLOAD ? 1.0F - in : 0.0F;
					glow = lerp(1.0F, IDLE_GLOW, (float) (el / stop));
				} else {
					add(out, clips, "idle", previous != MachineState.IDLE && Double.isFinite(el) ? el - stop : now, 1.0F);
					glow = IDLE_GLOW;
				}
			}
		}
		if (state == MachineState.RUNNING && red == 0.0F && clips.containsKey("run")) {
			double run = clips.get("run").lengthInSeconds();
			glow *= 0.94F + 0.06F * (float) Math.cos(motion.runPhase(now) / run * Math.PI * 4.0);
		}
		float beat = 0.5F + 0.5F * (float) Math.sin(now * Math.PI * 8.0);
		out.glowColor = color(glow * (1.0F + 0.25F * red * beat), 1.0F - 0.55F * red, 1.0F - 0.7F * red);
		float flush = red * (0.55F + 0.45F * beat);
		out.bodyColor = color(1.0F, 1.0F - 0.18F * flush, 1.0F - 0.24F * flush);
	}

	private static double length(Map<String, AnimationDefinition> clips, String name) {
		AnimationDefinition clip = clips.get(name);
		return clip == null ? 0.0 : clip.lengthInSeconds();
	}

	/** Põe o clipe com o tempo já dentro do laço (em double: o relógio do jogo é grande demais para float). */
	private static void add(MachineRenderState out, Map<String, AnimationDefinition> clips, String name, double seconds, float weight) {
		AnimationDefinition clip = clips.get(name);
		if (clip == null || weight <= 0.001F) {
			return;
		}
		double length = clip.lengthInSeconds();
		double t = clip.looping() ? ((seconds % length) + length) % length : Math.max(0.0, Math.min(seconds, length));
		out.add(name, (float) t, weight);
	}

	/** Mistura suave de 0 a 1 nos primeiros 0,3 s depois da troca. */
	private static float fade(double elapsed) {
		if (!(elapsed < FADE)) {
			return 1.0F;
		}
		float f = (float) Math.max(0.0, elapsed / FADE);
		return f * f * (3.0F - 2.0F * f);
	}

	private static float lerp(float a, float b, float t) {
		return a + (b - a) * Math.max(0.0F, Math.min(1.0F, t));
	}

	private static int color(float brightness, float g, float b) {
		int rr = channel(brightness), gg = channel(brightness * g), bb = channel(brightness * b);
		return 0xFF000000 | rr << 16 | gg << 8 | bb;
	}

	private static int channel(float v) {
		return Math.max(0, Math.min(255, Math.round(v * 255.0F)));
	}
}
