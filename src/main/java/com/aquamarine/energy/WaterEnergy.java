package com.aquamarine.energy;

/**
 * Números da energia aquática (Fase 6, 6E; docs/11-fase6.md). A unidade é a da Team Reborn Energy (E), para conversar
 * com outros mods de tecnologia: geradores, cabos e o Coletor expõem {@code EnergyStorage.SIDED}.
 */
public final class WaterEnergy {
	/** Turbina de Correnteza: por lado com água corrente (máximo 6 lados). */
	public static final long TURBINE_PER_SIDE = 4;
	/** Gerador de Fenda: magma embaixo e água em cima. */
	public static final long VENT_RATE = 32;
	/** Boia de Ondas: céu aberto em cima e água embaixo; dobra na chuva e quadruplica na tempestade. */
	public static final long BUOY_RATE = 4;
	public static final long GENERATOR_CAPACITY = 20_000;
	public static final long GENERATOR_PUSH = 128;
	/** Cabo Aquático: guarda pouco e iguala com os vizinhos. */
	public static final long CABLE_CAPACITY = 4_000;
	public static final long CABLE_RATE = 256;
	/** Coletor de Neutrônio: gasto por tick trabalhando e tempo de cada Pó Estelar (5 min). */
	public static final long COLLECTOR_CAPACITY = 100_000;
	public static final long COLLECTOR_INSERT = 512;
	public static final long COLLECTOR_USE = 64;
	public static final int COLLECTOR_TICKS = 6_000;
	public static final int COLLECTOR_MAX_DUST = 16;
	/** O Coletor só trabalha bem fundo: 20 blocos abaixo do nível do mar ou mais. */
	public static final int COLLECTOR_MIN_DEPTH = 20;

	private WaterEnergy() {}
}
