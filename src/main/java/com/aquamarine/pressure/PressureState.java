package com.aquamarine.pressure;

/**
 * Contas da Pressão de um jogador (docs/02-kraken.md, "Pressão"), sem dependência do jogo para
 * testar com JUnit. Chame {@link #tick} uma vez por tick do servidor.
 */
public final class PressureState {
	public static final int MAX_LEVEL = 4;
	/** Distância ao Kraken, em blocos, a partir da qual a Pressão sobe. */
	public static final double RANGE = 24.0;
	/** Ticks submerso perto do Kraken para subir 1 nível (10 s; 6 s na fase 3). */
	public static final int RISE_TICKS = 200;
	public static final int RISE_TICKS_PHASE3 = 120;
	/** Ticks fora da condição para descer 1 nível (5 s). */
	public static final int FALL_TICKS = 100;
	/** Níveis a menos no máximo com o Tônico de Pressão. */
	public static final int TONIC_LEVELS = 2;
	public static final int TONIC_TICKS = 5 * 60 * 20;

	private int level;
	private float rise;
	private int fall;

	public int level() {
		return level;
	}

	/** Máximo permitido: 4, ou 2 com o Tônico. */
	public static int maxLevel(boolean tonic) {
		return tonic ? MAX_LEVEL - TONIC_LEVELS : MAX_LEVEL;
	}

	/** Zera o nível (Boia de Âncora, morte). */
	public void reset() {
		level = 0;
		rise = 0;
		fall = 0;
	}

	/**
	 * Avança 1 tick. {@code exposed}: submerso a menos de {@link #RANGE} blocos do Kraken.
	 * {@code reduction}: redução do ganho (0 a 1; a armadura dá 0,15 por peça).
	 * Devolve {@code true} se o nível mudou.
	 */
	public boolean tick(boolean exposed, boolean tonic, float reduction, int krakenPhase) {
		int before = level;
		int max = maxLevel(tonic);
		if (level > max) {
			level = max;
		}
		if (exposed) {
			fall = 0;
			if (level < max) {
				rise += Math.max(0.0F, 1.0F - reduction);
				if (rise >= (krakenPhase >= 3 ? RISE_TICKS_PHASE3 : RISE_TICKS)) {
					rise = 0;
					level++;
				}
			} else {
				rise = 0;
			}
		} else {
			rise = 0;
			if (level > 0 && ++fall >= FALL_TICKS) {
				fall = 0;
				level--;
			}
		}
		return level != before;
	}

	/** Empuxo extra para baixo por tick de quem está na água, por nível (nível 4: puxado ao fundo). */
	public static double sinkForce(int level) {
		return switch (level) {
			case 1 -> 0.004;
			case 2 -> 0.006;
			case 3 -> 0.02;
			case 4 -> 0.07;
			default -> 0.0;
		};
	}

	/** Ticks entre os danos do nível 4 (1 coração = 2 pontos). */
	public static final int DAMAGE_INTERVAL = 40;
	public static final float DAMAGE = 2.0F;
}
