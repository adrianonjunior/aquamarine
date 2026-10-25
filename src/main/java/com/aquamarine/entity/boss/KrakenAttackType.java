package com.aquamarine.entity.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Os 10 ataques do Kraken (docs/02-kraken.md, "Ataques e avisos"): nome (igual ao da animação do
 * contrato), fase mínima, duração do aviso e quantos tentáculos usa. Só dados e contas, sem
 * dependência do jogo, para testar com JUnit.
 */
public enum KrakenAttackType {
	SLAM("slam", 1, 30, false, 1),
	GRAB("grab", 1, 30, false, 1),
	INK("ink", 1, 40, true, 0),
	WHIRLPOOL("whirlpool", 2, 40, true, 0),
	SPAWN_YOUNG("spawnYoung", 2, 40, false, 0),
	WATER_JET("waterJet", 2, 24, false, 0),
	ROAR("roar", 2, 30, true, 0),
	BOLTS("bolts", 3, 40, false, 0),
	AMBUSH("ambush", 3, 40, true, 0),
	DOUBLE_GRAB("doubleGrab", 3, 30, false, 2);

	/** Intervalo mínimo entre ataques do tipo "área" (8 s). */
	public static final int AREA_COOLDOWN_TICKS = 8 * 20;

	public static final float SLAM_DAMAGE = 12.0F;
	public static final float SLAM_RADIUS = 3.0F;
	public static final float GRAB_DPS = 6.0F;
	public static final float DOUBLE_GRAB_DPS = 10.0F;
	public static final int GRAB_TICKS = 5 * 20;
	public static final float INK_RADIUS = 24.0F;
	public static final int INK_BLIND_TICKS = 12 * 20;
	public static final float WHIRLPOOL_RADIUS = 24.0F;
	public static final int WHIRLPOOL_TICKS = 5 * 20;
	public static final float JET_DAMAGE = 10.0F;
	public static final float JET_LENGTH = 36.0F;
	public static final float ROAR_DAMAGE = 6.0F;
	public static final float ROAR_RADIUS = 18.0F;
	public static final float BOLT_DAMAGE = 14.0F;
	public static final float AMBUSH_DAMAGE = 10.0F;
	public static final float AMBUSH_RADIUS = 6.0F;
	public static final float YOUNG_DAMAGE = 4.0F;
	public static final float YOUNG_HEALTH = 8.0F;
	public static final int MAX_YOUNG = 16;
	public static final int TENTACLES = 8;
	public static final float TENTACLE_HEALTH = 60.0F;
	/** Tempo fora do ciclo de um tentáculo cortado (30 s). */
	public static final int TENTACLE_RESPAWN_TICKS = 30 * 20;
	/** Kraken exposto (8 tentáculos cortados): 10 s. */
	public static final int EXPOSED_TICKS = 10 * 20;

	private final String animation;
	private final int minPhase;
	private final int warnTicks;
	private final boolean area;
	private final int tentacles;

	KrakenAttackType(String animation, int minPhase, int warnTicks, boolean area, int tentacles) {
		this.animation = animation;
		this.minPhase = minPhase;
		this.warnTicks = warnTicks;
		this.area = area;
		this.tentacles = tentacles;
	}

	/** Nome do ataque e da animação do contrato ({@code slam}, {@code doubleGrab}...). */
	public String animation() {
		return animation;
	}

	public int minPhase() {
		return minPhase;
	}

	public int warnTicks() {
		return warnTicks;
	}

	public boolean isArea() {
		return area;
	}

	/** Tentáculos livres de que o ataque precisa para começar. */
	public int tentacles() {
		return tentacles;
	}

	public static List<String> names() {
		List<String> list = new ArrayList<>();
		for (KrakenAttackType type : values()) {
			list.add(type.animation);
		}
		return list;
	}

	public static KrakenAttackType byName(String name) {
		for (KrakenAttackType type : values()) {
			if (type.animation.equals(name) || type.name().toLowerCase(Locale.ROOT).equals(name.toLowerCase(Locale.ROOT))) {
				return type;
			}
		}
		return null;
	}

	/** Ataques simultâneos: {@code 2 + n ÷ 3}, no máximo 8 (n = jogadores na arena). */
	public static int maxSimultaneous(int players) {
		return Math.min(8, 2 + Math.max(0, players) / 3);
	}

	/** Crias por ataque: {@code 3 + n ÷ 2}. */
	public static int youngCount(int players) {
		return 3 + Math.max(0, players) / 2;
	}

	/** Ticks entre ataques novos, por fase (a dificuldade abissal acelera em 25%). */
	public static int attackInterval(int phase, boolean abyssal) {
		int base = phase >= 3 ? 45 : phase == 2 ? 60 : 80;
		return abyssal ? base * 3 / 4 : base;
	}

	/**
	 * Ataques que podem ser sorteados agora: liberados na fase, diferentes do último e dos que
	 * já estão ativos, sem área se ainda não passou o intervalo e só com tentáculos suficientes.
	 */
	public static List<KrakenAttackType> candidates(int phase, KrakenAttackType last, Set<KrakenAttackType> active,
		int sinceArea, int freeTentacles) {
		List<KrakenAttackType> list = new ArrayList<>();
		for (KrakenAttackType type : values()) {
			if (type.minPhase > phase || type == last || active.contains(type)) {
				continue;
			}
			if (type.area && sinceArea < AREA_COOLDOWN_TICKS) {
				continue;
			}
			if (type.tentacles > freeTentacles) {
				continue;
			}
			list.add(type);
		}
		return list;
	}
}
