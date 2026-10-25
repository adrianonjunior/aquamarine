package com.aquamarine.effect;

import com.aquamarine.Aquamarine;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/** Efeitos de status do mod. */
public final class ModEffects {
	/** Tônico de Pressão: máximo da Pressão −2 e soltura de agarrões 50% mais rápida (5 min). */
	public static final Holder<MobEffect> PRESSURE_TONIC = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
		Aquamarine.id("pressure_tonic"), new MobEffect(MobEffectCategory.BENEFICIAL, 0x2FB7B0) {});

	/**
	 * Chama Abissal (ponta de Fogo do Arpão): 2 de dano por segundo, também debaixo d'água, com
	 * faíscas de fogo azul. Não acende o alvo; a Resistência ao Fogo protege.
	 */
	public static final Holder<MobEffect> ABYSSAL_FLAME = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
		Aquamarine.id("abyssal_flame"), new MobEffect(MobEffectCategory.HARMFUL, 0x3FA8FF, ParticleTypes.SOUL_FIRE_FLAME) {
			@Override
			public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
				return tickCount % 20 == 0;
			}

			@Override
			public boolean applyEffectTick(ServerLevel level, LivingEntity mob, int amplification) {
				if (!mob.hasEffect(MobEffects.FIRE_RESISTANCE)) {
					mob.hurtServer(level, level.damageSources().onFire(), ABYSSAL_FLAME_DAMAGE * (amplification + 1));
				}
				return true;
			}
		});
	public static final float ABYSSAL_FLAME_DAMAGE = 2.0F;

	public static boolean hasTonic(LivingEntity entity) {
		return entity.hasEffect(PRESSURE_TONIC);
	}

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro.
	}

	private ModEffects() {}
}
