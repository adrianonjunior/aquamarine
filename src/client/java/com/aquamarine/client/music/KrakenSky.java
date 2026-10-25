package com.aquamarine.client.music;

import com.aquamarine.entity.KrakenEntity;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * O céu da luta do Kraken (docs/02-kraken.md, "A luta muda o mundo"), só no cliente: perto de um Kraken
 * vivo, céu, névoa, água, nuvens e luz do céu puxam para a cor da fase (Tormenta: ardósia esverdeada;
 * Fúria: violeta machucado; Abismo: carmim abissal). Na vitória o céu dá um clarão dourado e volta ao normal.
 * Aplicado em {@code EnvironmentAttributeProbeMixin}.
 */
public final class KrakenSky {
	/** Por fase: céu, névoa, névoa da água, nuvem, luz do céu (RGB 0 a 1). */
	private static final float[][][] PHASES = {
		{{0.23F, 0.33F, 0.38F}, {0.16F, 0.24F, 0.27F}, {0.05F, 0.16F, 0.20F}, {0.30F, 0.36F, 0.40F}, {0.55F, 0.65F, 0.72F}},
		{{0.29F, 0.16F, 0.39F}, {0.18F, 0.10F, 0.24F}, {0.10F, 0.06F, 0.17F}, {0.32F, 0.22F, 0.40F}, {0.62F, 0.50F, 0.78F}},
		{{0.35F, 0.05F, 0.11F}, {0.20F, 0.03F, 0.07F}, {0.14F, 0.02F, 0.05F}, {0.36F, 0.10F, 0.14F}, {0.80F, 0.42F, 0.42F}},
	};
	private static final float[][] GOLD = {{0.95F, 0.78F, 0.42F}, {0.85F, 0.68F, 0.40F}, {0.30F, 0.42F, 0.38F}, {0.98F, 0.86F, 0.60F},
		{1.0F, 0.92F, 0.75F}};
	/** Quanto cada atributo puxa para a cor (na intensidade máxima). */
	private static final float[] STRENGTH = {0.85F, 0.75F, 0.6F, 0.6F, 0.35F};
	private static final float FADE_IN = 0.02F;
	private static final float FADE_OUT = 0.01F;
	private static final float COLOR_LERP = 0.03F;
	private static final int GOLD_TICKS = 140;

	private static float intensity;
	private static final float[][] color = copy(PHASES[0]);
	private static int goldTicks;

	/** A cada tick do cliente, com o Kraken vivo mais perto (ou nenhum). */
	static void update(KrakenEntity kraken) {
		float target = kraken != null ? 1.0F : 0.0F;
		intensity += Math.max(-FADE_OUT, Math.min(FADE_IN, target - intensity));
		if (kraken != null) {
			float[][] goal = PHASES[Math.max(1, Math.min(PHASES.length, kraken.phase())) - 1];
			for (int a = 0; a < color.length; a++) {
				for (int c = 0; c < 3; c++) {
					color[a][c] += (goal[a][c] - color[a][c]) * COLOR_LERP;
				}
			}
		}
		if (goldTicks > 0) {
			goldTicks--;
		}
	}

	static void victory() {
		goldTicks = GOLD_TICKS;
	}

	static void reset() {
		intensity = 0.0F;
		goldTicks = 0;
	}

	/** Intensidade atual (0 a 1), para testes. */
	public static float intensity() {
		return intensity;
	}

	/** Valor tingido para o atributo, ou {@code null} para deixar como está. */
	public static Object tint(EnvironmentAttribute<?> attribute, Object value) {
		float gold = goldTicks / (float) GOLD_TICKS;
		if (intensity <= 0.001F && gold <= 0.001F) {
			return null;
		}
		int index = attribute == EnvironmentAttributes.SKY_COLOR ? 0
			: attribute == EnvironmentAttributes.FOG_COLOR ? 1
			: attribute == EnvironmentAttributes.WATER_FOG_COLOR ? 2
			: attribute == EnvironmentAttributes.CLOUD_COLOR ? 3
			: attribute == EnvironmentAttributes.SKY_LIGHT_COLOR ? 4 : -1;
		if (index < 0) {
			return null;
		}
		float k = STRENGTH[index] * intensity;
		float g = STRENGTH[index] * gold * gold;
		if (value instanceof Vector3fc v) {
			Vector3f out = new Vector3f(v);
			blend(out, color[index], k);
			blend(out, GOLD[index], g);
			return out;
		}
		if (value instanceof Vector4fc v) {
			Vector3f rgb = new Vector3f(v.x(), v.y(), v.z());
			blend(rgb, color[index], k);
			blend(rgb, GOLD[index], g);
			return new Vector4f(rgb, v.w());
		}
		return null;
	}

	private static void blend(Vector3f out, float[] to, float k) {
		if (k > 0.0F) {
			out.lerp(new Vector3f(to[0], to[1], to[2]), Math.min(1.0F, k));
		}
	}

	private static float[][] copy(float[][] src) {
		float[][] out = new float[src.length][];
		for (int i = 0; i < src.length; i++) {
			out[i] = src[i].clone();
		}
		return out;
	}

	private KrakenSky() {}
}
