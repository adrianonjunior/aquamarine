package com.aquamarine.entity;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.DiveGear;

import java.util.List;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Dano elétrico da Fase 3 (Descarga da Moréia, corrente do Chicote, ponta de Choque): um tipo de
 * dano próprio ({@code aquamarine:electric}) e as defesas de docs/08-fase3.md (Traje −40% por peça,
 * Isolamento −15% por nível). Tudo no servidor.
 */
public final class ElectricDamage {
	public static final ResourceKey<DamageType> TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, Aquamarine.id("electric"));
	public static final ResourceKey<Enchantment> ISOLATION = ResourceKey.create(Registries.ENCHANTMENT, Aquamarine.id("isolation"));
	public static final float SUIT_PER_PIECE = 0.4F;
	public static final float ISOLATION_PER_LEVEL = 0.15F;

	private static final EquipmentSlot[] ARMOR = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	/** Fração do dano elétrico que passa (0 = imune), pelas peças do Traje e os níveis de Isolamento. */
	public static float multiplier(LivingEntity target) {
		return multiplier(DiveGear.suitPieces(target), isolationLevels(target));
	}

	/** Conta pura (JUnit): cada defesa tira uma fração; nunca passa de 100%. */
	public static float multiplier(int suitPieces, int isolationLevels) {
		return Math.max(0.0F, 1.0F - SUIT_PER_PIECE * suitPieces - ISOLATION_PER_LEVEL * isolationLevels);
	}

	/** Soma dos níveis de Isolamento nas quatro peças de armadura. */
	public static int isolationLevels(LivingEntity target) {
		return target.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(ISOLATION).map(holder -> {
			int levels = 0;
			for (EquipmentSlot slot : ARMOR) {
				levels += EnchantmentHelper.getItemEnchantmentLevel(holder, target.getItemBySlot(slot));
			}
			return levels;
		}).orElse(0);
	}

	public static DamageSource source(ServerLevel level, Entity direct, Entity attacker) {
		return level.damageSources().source(TYPE, direct, attacker);
	}

	/** Molhado o bastante para a corrente: na água ou na chuva, fora de barco. */
	public static boolean isWet(LivingEntity target) {
		return target.isInWaterOrRain() && !(target.getVehicle() instanceof AbstractBoat);
	}

	/** Dá dano elétrico com as defesas. Devolve a fração que passou (0 = o alvo está isolado). */
	public static float hurt(ServerLevel level, LivingEntity target, Entity direct, Entity attacker, float amount) {
		float fraction = multiplier(target);
		if (fraction > 0.0F) {
			target.hurtServer(level, source(level, direct, attacker), amount * fraction);
			Vec3 at = target.position().add(0, target.getBbHeight() * 0.6, 0);
			level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y, at.z, 8, 0.3, 0.4, 0.3, 0.1);
		}
		return fraction;
	}

	/**
	 * Corrente em cadeia: os alvos molhados a {@code radius} blocos de {@code center} (menos o atacante
	 * e {@code except}) levam {@code amount} de dano elétrico. Devolve quantos foram atingidos.
	 */
	public static int chain(ServerLevel level, Entity attacker, Vec3 center, double radius, float amount, Entity except) {
		List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(radius),
			e -> e.isAlive() && e != attacker && e != except && isWet(e) && e.position().distanceTo(center) <= radius);
		for (LivingEntity target : targets) {
			hurt(level, target, attacker, attacker, amount);
			line(level, center, target.position().add(0, target.getBbHeight() * 0.5, 0));
		}
		return targets.size();
	}

	/** Raio visível entre dois pontos (faíscas azuis). */
	public static void line(ServerLevel level, Vec3 from, Vec3 to) {
		Vec3 step = to.subtract(from);
		int points = Math.max(2, (int) (step.length() * 2));
		for (int i = 0; i <= points; i++) {
			Vec3 p = from.add(step.scale(i / (double) points));
			level.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0.0);
		}
	}

	private ElectricDamage() {}
}
