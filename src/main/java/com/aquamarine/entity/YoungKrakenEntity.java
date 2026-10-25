package com.aquamarine.entity;

import com.aquamarine.entity.boss.KrakenAttackType;

import com.aquamarine.entity.boss.AnimationSync;
import com.aquamarine.sound.ModSounds;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;

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
public class YoungKrakenEntity extends Monster implements YoungKrakenView {
	/** Nomes das animações do contrato ({@link YoungKrakenView}). */
	public static final List<String> ANIMATION_NAMES = List.of("idle", "swim", "stunned", "hatch", "attack", "death");
	private static final EntityDataAccessor<Integer> DATA_ANIM = SynchedEntityData.defineId(YoungKrakenEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_ANIM_SEQ = SynchedEntityData.defineId(YoungKrakenEntity.class, EntityDataSerializers.INT);

	private final AnimationSync animations = new AnimationSync(ANIMATION_NAMES);
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
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_ANIM, 0);
		builder.define(DATA_ANIM_SEQ, 0);
	}

	/** Dispara uma animação pelo nome do contrato (servidor; o cliente recebe pelos dados sincronizados). */
	public boolean playAnimation(String name) {
		int index = animations.indexOf(name);
		if (index < 0) {
			return false;
		}
		entityData.set(DATA_ANIM, index);
		entityData.set(DATA_ANIM_SEQ, entityData.get(DATA_ANIM_SEQ) + 1);
		return true;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (key.equals(DATA_ANIM_SEQ) && level().isClientSide() && entityData.get(DATA_ANIM_SEQ) > 0) {
			animations.start(entityData.get(DATA_ANIM), tickCount, 0);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			animations.state("idle").startIfStopped(tickCount);
			animations.state("swim").animateWhen(getDeltaMovement().horizontalDistanceSqr() > 1.0E-3, tickCount);
		} else if (tickCount == 1) {
			playAnimation("hatch");
		}
	}

	@Override
	public void die(DamageSource source) {
		playAnimation("death");
		super.die(source);
	}

	@Override
	public AnimationState idleAnimation() {
		return animations.state("idle");
	}

	@Override
	public AnimationState swimAnimation() {
		return animations.state("swim");
	}

	@Override
	public AnimationState stunnedAnimation() {
		return animations.state("stunned");
	}

	@Override
	public AnimationState hatchAnimation() {
		return animations.state("hatch");
	}

	@Override
	public AnimationState attackAnimation() {
		return animations.state("attack");
	}

	@Override
	public AnimationState deathAnimation() {
		return animations.state("death");
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
				playAnimation("attack");
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

	// --- Sons (docs/09-audio.md) ---

	@Override
	protected SoundEvent getAmbientSound() {
		return ModSounds.YOUNG_KRAKEN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ModSounds.YOUNG_KRAKEN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSounds.YOUNG_KRAKEN_DEATH;
	}
}
