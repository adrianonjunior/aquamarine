package com.aquamarine.item;

import java.util.Locale;

import net.minecraft.world.item.Item;

/**
 * Pontas do Arpão Abissal (docs/08-fase3.md, seção 5), na ordem da Aljava. {@code krakenBonus}:
 * fração do dano base somada no escudo do Kraken (o triplo do Arpão continua valendo);
 * {@code affectsKraken}: veneno e wither não pegam no Kraken.
 */
public enum HarpoonTip {
	POISON(0.0F, false),
	FIRE(0.10F, true),
	WITHER(0.0F, false),
	SHOCK(0.40F, true);

	/** Veneno I por 10 s. */
	public static final int POISON_TICKS = 10 * 20;
	/** Chama Abissal por 5 s (2 de dano por segundo, também debaixo d'água). */
	public static final int FIRE_TICKS = 5 * 20;
	/** Wither I por 8 s. */
	public static final int WITHER_TICKS = 8 * 20;
	/** Descarga do Choque: alvos molhados a 6 blocos, 50% do dano. */
	public static final double SHOCK_RADIUS = 6.0;
	public static final float SHOCK_FRACTION = 0.5F;

	public final float krakenBonus;
	public final boolean affectsKraken;

	HarpoonTip(float krakenBonus, boolean affectsKraken) {
		this.krakenBonus = krakenBonus;
		this.affectsKraken = affectsKraken;
	}

	public String id() {
		return name().toLowerCase(Locale.ROOT);
	}

	public Item item() {
		return switch (this) {
			case POISON -> ModItems.POISON_HARPOON_TIP;
			case FIRE -> ModItems.FIRE_HARPOON_TIP;
			case WITHER -> ModItems.WITHER_HARPOON_TIP;
			case SHOCK -> ModItems.SHOCK_HARPOON_TIP;
		};
	}

	public static HarpoonTip of(Item item) {
		for (HarpoonTip tip : values()) {
			if (tip.item() == item) {
				return tip;
			}
		}
		return null;
	}
}
