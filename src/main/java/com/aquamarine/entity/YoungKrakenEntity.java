package com.aquamarine.entity;

import com.aquamarine.entity.boss.KrakenAttackType;

import java.util.EnumSet;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Cria de Kraken (docs/02-kraken.md): 8 de vida, 4 de dano, nada direto até o jogador. O modelo
 * vem da faixa 2D-M (por enquanto o renderer é provisório).
 */
public class YoungKrakenEntity extends Monster {
	private static final int ATTACK_COOLDOWN = 20;

	private int attackCooldown;

	public YoungKrakenEntity(EntityType<? extends YoungKrakenEntity> type, Level level) {
		super(type, level);
		this.setNoGravity(true);
		this.xpReward = 0;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, KrakenAttackType.YOUNG_HEALTH)
			.add(Attributes.ATTACK_DAMAGE, KrakenAttackType.YOUNG_DAMAGE)
			.add(Attributes.MOVEMENT_SPEED, 0.3)
			.add(Attributes.FOLLOW_RANGE, 40.0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new SwimAtTargetGoal());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	/** Nada em linha reta até o alvo e morde ao encostar. */
	private final class SwimAtTargetGoal extends Goal {
		SwimAtTargetGoal() {
			setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			LivingEntity target = getTarget();
			return target != null && target.isAlive();
		}

		@Override
		public void tick() {
			LivingEntity target = getTarget();
			if (target == null) {
				return;
			}
			getLookControl().setLookAt(target, 30.0F, 30.0F);
			Vec3 to = target.getEyePosition().subtract(position());
			if (to.lengthSqr() > 1.0E-4) {
				Vec3 dir = to.normalize().scale(0.16);
				setDeltaMovement(getDeltaMovement().scale(0.6).add(dir.scale(0.5)));
			}
			if (attackCooldown > 0) {
				attackCooldown--;
			} else if (distanceToSqr(target) < 2.5 * 2.5 && level() instanceof ServerLevel level) {
				doHurtTarget(level, target);
				attackCooldown = ATTACK_COOLDOWN;
			}
		}
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

}
