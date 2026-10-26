package com.aquamarine.entity.boss;

/**
 * Tabela de dano no escudo "Carapaça Abissal" por fonte (docs/02-kraken.md, "Dano no escudo
 * por fonte"). Só contas, sem dependência do jogo, para testar com JUnit.
 */
public final class ShieldDamage {
	public enum Source {
		/** Espada, machado, arco, besta, tridente e o que não tem item especial. */
		COMMON(0.25F, true),
		/** Ferramentas e armas da tag {@code aquamarine:shield_breakers}. */
		ABYSSAL(0.60F, true),
		/** Ferramentas de Neutrônio (tag {@code aquamarine:neutronium_tools}, Fase 4). */
		NEUTRONIUM(0.80F, true),
		/** Arpão Abissal (tag {@code aquamarine:harpoons}). */
		HARPOON(3.0F, true),
		/** Carga de Pulso: dano fixo, em área. */
		PULSE(0.0F, false),
		/** Pilar Ressonante: fração do escudo máximo. */
		PILLAR(0.0F, false);

		final float multiplier;
		final boolean nodeApplies;

		Source(float multiplier, boolean nodeApplies) {
			this.multiplier = multiplier;
			this.nodeApplies = nodeApplies;
		}
	}

	public static final float PULSE_DAMAGE = 100.0F;
	public static final float PILLAR_FRACTION = 0.05F;
	public static final float NODE_MULTIPLIER = 2.0F;
	/** Kraken exposto (8 tentáculos cortados): +25% de dano no escudo. */
	public static final float EXPOSED_MULTIPLIER = 1.25F;
	public static final float SHATTER_PER_LEVEL = 0.10F;
	public static final int MAX_SHATTER_LEVEL = 3;

	/**
	 * Dano efetivo no escudo.
	 *
	 * @param damage dano bruto do golpe (ignorado por {@link Source#PULSE} e {@link Source#PILLAR})
	 * @param maxShield escudo máximo atual (base do Pilar Ressonante)
	 * @param shatterLevel nível de Quebra-Carapaça da arma (0 a 3)
	 * @param node o golpe acertou um Nó de Maré (vale 2x; não vale para Pulso e Pilar)
	 * @param exposed o Kraken está exposto
	 */
	public static float amount(Source source, float damage, float maxShield, int shatterLevel, boolean node, boolean exposed) {
		float base = switch (source) {
			case PULSE -> PULSE_DAMAGE;
			case PILLAR -> maxShield * PILLAR_FRACTION;
			default -> damage * source.multiplier;
		};
		int level = Math.max(0, Math.min(MAX_SHATTER_LEVEL, shatterLevel));
		float result = base * (1.0F + SHATTER_PER_LEVEL * level);
		if (node && source.nodeApplies) {
			result *= NODE_MULTIPLIER;
		}
		if (exposed) {
			result *= EXPOSED_MULTIPLIER;
		}
		return Math.max(0.0F, result);
	}

	private ShieldDamage() {}
}
