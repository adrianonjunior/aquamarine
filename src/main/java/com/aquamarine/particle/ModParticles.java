package com.aquamarine.particle;

import com.aquamarine.Aquamarine;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

/** Partículas do Cavitador de Neutrônio (docs/10-fase4.md, §9). O desenho fica no cliente (AquamarineParticles). */
public final class ModParticles {
	/** Bolha de cavitação: rastro do pulso e a nuvem da implosão. */
	public static final SimpleParticleType CAVITATION_BUBBLE = register("cavitation_bubble");
	/** Centelha de estrela, com luz própria. */
	public static final SimpleParticleType NEUTRON_SPARK = register("neutron_spark");
	/** Anel de choque que se expande na implosão. */
	public static final SimpleParticleType SHOCK_RING = register("shock_ring");
	/** Ponto de luz que converge para a boca do cano enquanto a arma carrega. */
	public static final SimpleParticleType CHARGE_MOTE = register("charge_mote");

	private static SimpleParticleType register(String name) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Aquamarine.id(name), FabricParticleTypes.simple(true));
	}

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro.
	}

	private ModParticles() {}
}
