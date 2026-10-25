package com.aquamarine.entity.boss;

/**
 * Números da luta do Kraken (docs/02-kraken.md: "Fases", "Escala por número de jogadores" e
 * "Dificuldade"). Só contas, sem dependência do jogo, para testar com JUnit.
 */
public final class KrakenStats {
	/** Vida base de cada fase; a vida total é a soma (1.500). */
	public static final float HEALTH_PER_PHASE = 500.0F;
	public static final int PHASES = 3;
	/** Escudo base por fase (índice 0 = fase 1). */
	public static final float[] SHIELD_BY_PHASE = {400.0F, 600.0F, 800.0F};
	public static final float ABYSSAL_MULTIPLIER = 1.5F;
	/**
	 * Tamanho (docs/00-decisoes.md, "Tamanho do Kraken"): escala do modelo sobre os pixels do KrakenModel.
	 * Na 8, com os braços de 7 juntas, o corpo tem ~41 blocos de largura e ~25 de altura.
	 */
	public static final float MODEL_SCALE = 8.0F;
	/**
	 * Quanto o modelo desce (blocos) para a ponta dos braços em repouso ficar nos pés da entidade: a hitbox
	 * começa ali, e o Kraken que nasce 24 blocos abaixo da superfície fica com a coroa rente à água.
	 */
	public static final float MODEL_DROP = 5.4F;

	/** {@code f = 1 + 0,3 × (n − 1)}, com {@code n} limitado a {@code cap} e no mínimo 1. */
	public static float scale(int players, int cap) {
		int n = Math.max(1, Math.min(players, Math.max(1, cap)));
		return 1.0F + 0.3F * (n - 1);
	}

	public static float difficultyMultiplier(String difficulty) {
		return "abissal".equals(difficulty) ? ABYSSAL_MULTIPLIER : 1.0F;
	}

	public static float maxHealth(float scale, String difficulty) {
		return HEALTH_PER_PHASE * PHASES * scale * difficultyMultiplier(difficulty);
	}

	public static float maxShield(int phase, float scale, String difficulty) {
		return SHIELD_BY_PHASE[Math.max(1, Math.min(PHASES, phase)) - 1] * scale * difficultyMultiplier(difficulty);
	}

	/** Fase (1 a 3) para uma fração de vida: 100%–66% é a 1, 66%–33% a 2, abaixo disso a 3. */
	public static int phaseFor(float healthFraction) {
		return healthFraction > 2.0F / 3.0F ? 1 : healthFraction > 1.0F / 3.0F ? 2 : 3;
	}

	/** Fração de vida abaixo da qual a fase termina (0 na fase 3). */
	public static float phaseFloorFraction(int phase) {
		return (PHASES - phase) / (float) PHASES;
	}

	private KrakenStats() {}
}
