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

	/** Nível máximo que a Pressão ambiental alcança sozinha (o 4 é exclusivo do Kraken). */
	public static final int AMBIENT_MAX_LEVEL = 3;

	/** Uma fonte da Pressão: o Kraken e a profundidade sobem e descem cada uma no seu ritmo. */
	private static final class Source {
		int level;
		float rise;
		int fall;

		void reset() {
			level = 0;
			rise = 0;
			fall = 0;
		}
	}

	private final Source kraken = new Source();
	private final Source ambient = new Source();

	/** Nível final: o maior das fontes (nunca a soma), já limitado pelo Tônico. */
	public int level() {
		return Math.max(kraken.level, ambient.level);
	}

	public int krakenLevel() {
		return kraken.level;
	}

	public int ambientLevel() {
		return ambient.level;
	}

	/**
	 * Nível ambiental máximo pela profundidade abaixo do nível do mar (docs/08-fase3.md, seção 1):
	 * 0 a 30 blocos = 0, 30 a 50 = 1, 50 a 70 = 2, 70 ou mais = 3.
	 */
	public static int ambientCap(double depth) {
		if (depth >= 70.0) {
			return 3;
		}
		if (depth >= 50.0) {
			return 2;
		}
		return depth >= 30.0 ? 1 : 0;
	}

	/** Máximo permitido: 4, ou 2 com o Tônico. */
	public static int maxLevel(boolean tonic) {
		return tonic ? MAX_LEVEL - TONIC_LEVELS : MAX_LEVEL;
	}

	/** Zera o nível (Boia de Âncora, morte). */
	public void reset() {
		kraken.reset();
		ambient.reset();
	}

	/**
	 * Avança 1 tick só com a fonte do Kraken (a luta da Entrega 2, sem fonte ambiental).
	 * {@code exposed}: submerso a menos de {@link #RANGE} blocos do Kraken.
	 * {@code reduction}: redução do ganho (0 a 1; a armadura dá 0,15 por peça).
	 * Devolve {@code true} se o nível final mudou.
	 */
	public boolean tick(boolean exposed, boolean tonic, float reduction, int krakenPhase) {
		return tick(exposed, 0, tonic, reduction, krakenPhase);
	}

	/**
	 * Avança 1 tick com as duas fontes. {@code ambientTarget}: nível ambiental que a profundidade e
	 * as contramedidas permitem agora (0 a {@value #AMBIENT_MAX_LEVEL}); a fonte sobe até ele e
	 * desce quando passa dele, com as mesmas regras de tempo do Kraken.
	 */
	public boolean tick(boolean exposed, int ambientTarget, boolean tonic, float reduction, int krakenPhase) {
		int before = level();
		int max = maxLevel(tonic);
		tickKraken(exposed, max, reduction, krakenPhase);
		tickAmbient(Math.min(Math.min(ambientTarget, AMBIENT_MAX_LEVEL), max), max, reduction);
		return level() != before;
	}

	private void tickKraken(boolean exposed, int max, float reduction, int krakenPhase) {
		Source s = kraken;
		if (s.level > max) {
			s.level = max;
		}
		if (exposed) {
			s.fall = 0;
			if (s.level < max) {
				s.rise += Math.max(0.0F, 1.0F - reduction);
				if (s.rise >= (krakenPhase >= 3 ? RISE_TICKS_PHASE3 : RISE_TICKS)) {
					s.rise = 0;
					s.level++;
				}
			} else {
				s.rise = 0;
			}
		} else {
			s.rise = 0;
			if (s.level > 0 && ++s.fall >= FALL_TICKS) {
				s.fall = 0;
				s.level--;
			}
		}
	}

	private void tickAmbient(int target, int max, float reduction) {
		Source s = ambient;
		if (s.level > max) {
			s.level = max;
		}
		if (s.level < target) {
			s.fall = 0;
			s.rise += Math.max(0.0F, 1.0F - reduction);
			if (s.rise >= RISE_TICKS) {
				s.rise = 0;
				s.level++;
			}
		} else if (s.level > target) {
			s.rise = 0;
			if (++s.fall >= FALL_TICKS) {
				s.fall = 0;
				s.level--;
			}
		} else {
			s.rise = 0;
			s.fall = 0;
		}
	}

	/** Gravidade extra (atributo, base 0,08) por nível: 1 afunda 20% mais rápido; 4 é puxado ao fundo. */
	public static double gravityBonus(int level) {
		return switch (level) {
			case 1 -> 0.016;
			case 2 -> 0.024;
			case 3 -> 0.08;
			case 4 -> 0.3;
			default -> 0.0;
		};
	}

	/** Ticks entre os danos do nível 4 (1 coração = 2 pontos). */
	public static final int DAMAGE_INTERVAL = 40;
	public static final float DAMAGE = 2.0F;
}
