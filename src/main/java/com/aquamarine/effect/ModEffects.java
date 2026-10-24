package com.aquamarine.effect;

import com.aquamarine.Aquamarine;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/** Efeitos de status do mod. */
public final class ModEffects {
	/** Tônico de Pressão: máximo da Pressão −2 e soltura de agarrões 50% mais rápida (5 min). */
	public static final Holder<MobEffect> PRESSURE_TONIC = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
		Aquamarine.id("pressure_tonic"), new MobEffect(MobEffectCategory.BENEFICIAL, 0x2FB7B0) {});

	public static boolean hasTonic(LivingEntity entity) {
		return entity.hasEffect(PRESSURE_TONIC);
	}

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro.
	}

	private ModEffects() {}
}
