package com.aquamarine.item;

import com.aquamarine.ModTags;
import com.aquamarine.pressure.PressureManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Neutrônio solto no mundo (docs/10-fase4.md, §8): matéria densa demais para boiar, todo item da família afunda na
 * água. O Lingote de Neutrônio Instável se estabiliza (vira Lingote de Neutrônio) depois de {@link #TEMPER_TICKS}
 * submerso a {@link #TEMPER_DEPTH} blocos ou mais abaixo do nível do mar, onde a Pressão chega ao nível 3.
 * Chamado todo tique pelo {@code ItemEntityMixin}.
 */
public final class NeutroniumTempering {
	public static final int TEMPER_TICKS = 20 * 20;
	public static final double TEMPER_DEPTH = 70.0;
	private static final double SINK_SPEED = -0.08;

	/** @param progress tiques de têmpera acumulados; devolve o novo valor (0 quando não está temperando) */
	public static int tick(ItemEntity entity, int progress) {
		ItemStack stack = entity.getItem();
		if (!stack.is(ModTags.Items.NEUTRONIUM_DENSE)) {
			return 0;
		}
		if (entity.isInWater()) {
			Vec3 v = entity.getDeltaMovement();
			entity.setDeltaMovement(v.x * 0.8, Math.min(v.y, SINK_SPEED), v.z * 0.8);
		}
		if (!(entity.level() instanceof ServerLevel level) || !stack.is(ModItems.UNSTABLE_NEUTRONIUM_INGOT)) {
			return 0;
		}
		double depth = PressureManager.seaLevel(level) - entity.getY();
		if (!entity.isInWater() || depth < TEMPER_DEPTH) {
			return 0;
		}
		progress++;
		double x = entity.getX(), y = entity.getY() + 0.2, z = entity.getZ();
		if (progress % 5 == 0) {
			int amount = 1 + progress * 4 / TEMPER_TICKS;
			level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, amount, 0.15, 0.15, 0.15, 0.05);
			level.sendParticles(ParticleTypes.BUBBLE, x, y, z, 2, 0.1, 0.1, 0.1, 0.02);
		}
		if (progress < TEMPER_TICKS) {
			return progress;
		}
		entity.setItem(new ItemStack(ModItems.NEUTRONIUM_INGOT, stack.getCount()));
		level.sendParticles(ParticleTypes.GLOW, x, y, z, 30, 0.4, 0.4, 0.4, 0.1);
		level.sendParticles(ParticleTypes.END_ROD, x, y, z, 16, 0.2, 0.2, 0.2, 0.08);
		level.playSound(null, x, y, z, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0F, 1.6F);
		return 0;
	}

	private NeutroniumTempering() {}
}
