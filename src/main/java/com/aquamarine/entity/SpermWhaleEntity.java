package com.aquamarine.entity;

import java.util.List;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Cachalote (docs/08-fase3.md, seção 6): neutro, 140 de vida, vive em fossas e tem cicatrizes de
 * tentáculo. Só reage se atacado: vira para o agressor e solta uma onda sonora que empurra e dá 6 de
 * dano num cone de 8 blocos à frente.
 */
public class SpermWhaleEntity extends AbstractWhaleEntity implements SpermWhaleView {
	public static final List<String> ANIMATION_NAMES = List.of("idle", "swim", "sonic", "death");
	public static final float MAX_HEALTH = 140.0F;
	public static final float SONIC_DAMAGE = 6.0F;
	public static final double SONIC_RANGE = 8.0;
	/** Meio ângulo do cone da onda sonora (cosseno de 35°). */
	public static final double SONIC_CONE_COS = Math.cos(Math.toRadians(35.0));
	public static final int SONIC_WARN = 20;
	public static final int SONIC_COOLDOWN = 80;
	/** Quanto tempo fica bravo depois de apanhar (20 s). */
	public static final int ANGER_TICKS = 400;
	public static final int XP = 20;

	private LivingEntity angryAt;
	private int angerTicks;
	private int sonicTicks = -1;
	private int cooldown;

	public SpermWhaleEntity(EntityType<? extends SpermWhaleEntity> type, Level level) {
		super(type, level, ANIMATION_NAMES);
		this.xpReward = XP;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return PathfinderMob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, MAX_HEALTH)
			.add(Attributes.MOVEMENT_SPEED, 0.6)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
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
	public AnimationState sonicAnimation() {
		return animations.state("sonic");
	}

	@Override
	public AnimationState deathAnimation() {
		return animations.state("death");
	}

	/** Ticks até poder soltar outra onda (maior que 0 logo depois de uma). */
	public int sonicCooldown() {
		return cooldown;
	}

	public LivingEntity angryAt() {
		return angerTicks > 0 ? angryAt : null;
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		boolean hurt = super.hurtServer(level, source, damage);
		if (hurt && source.getEntity() instanceof LivingEntity attacker && attacker != this) {
			angryAt = attacker;
			angerTicks = ANGER_TICKS;
		}
		return hurt;
	}

	@Override
	protected void serverBehavior(ServerLevel level) {
		if (cooldown > 0) {
			cooldown--;
		}
		if (sonicTicks >= 0) {
			tickSonic(level);
			return;
		}
		if (angerTicks > 0 && angryAt != null && angryAt.isAlive() && angryAt.distanceTo(this) < 32.0) {
			angerTicks--;
			Vec3 to = angryAt.getEyePosition().subtract(position().add(0, 2.0, 0));
			steer(to, to.length() > SONIC_RANGE - 2 ? 0.18 : 0.0, 6.0F);
			if (cooldown == 0 && to.length() <= SONIC_RANGE + 2) {
				startSonic();
			}
			return;
		}
		angerTicks = 0;
		wander(level, 0.1);
	}

	/** Começa a onda sonora (aviso de 1 s: a cabeça vibra e as bolhas crescem). */
	public void startSonic() {
		sonicTicks = 0;
		playAnimation("sonic");
		playSound(SoundEvents.WARDEN_SONIC_CHARGE, 2.0F, 0.6F);
	}

	private void tickSonic(ServerLevel level) {
		setDeltaMovement(getDeltaMovement().scale(0.5));
		if (angryAt != null) {
			face(angryAt.position().subtract(position()), 6.0F);
		}
		Vec3 front = front();
		Vec3 mouth = mouth();
		if (++sonicTicks < SONIC_WARN) {
			level.sendParticles(ParticleTypes.BUBBLE, mouth.x, mouth.y, mouth.z, 4 + sonicTicks / 2, 0.5, 0.5, 0.5, 0.05);
			return;
		}
		for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, new AABB(mouth, mouth).inflate(SONIC_RANGE),
			e -> e != this && e.isAlive() && inCone(mouth, front, e))) {
			target.hurtServer(level, damageSources().mobAttack(this), SONIC_DAMAGE);
			Vec3 push = target.position().subtract(mouth).normalize().scale(1.4);
			target.push(push.x, push.y * 0.5 + 0.2, push.z);
			target.needsSync = true;
		}
		for (int i = 1; i <= 8; i++) {
			Vec3 p = mouth.add(front.scale(i));
			level.sendParticles(ParticleTypes.SONIC_BOOM, p.x, p.y, p.z, 1, 0, 0, 0, 0);
		}
		level.playSound(null, mouth.x, mouth.y, mouth.z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL, 2.0F, 0.6F);
		sonicTicks = -1;
		cooldown = SONIC_COOLDOWN;
	}

	/** Carregando a onda sonora (aviso). */
	public boolean isCharging() {
		return sonicTicks >= 0;
	}

	public Vec3 front() {
		return Vec3.directionFromRotation(getXRot(), getYRot());
	}

	/** De onde sai a onda: a ponta da cabeça. */
	public Vec3 mouth() {
		return position().add(0, 2.5, 0).add(front().scale(5.0));
	}

	/** Dentro do cone de 8 blocos à frente da boca. */
	public static boolean inCone(Vec3 mouth, Vec3 front, LivingEntity e) {
		Vec3 to = e.position().add(0, e.getBbHeight() * 0.5, 0).subtract(mouth);
		double distance = to.length();
		return distance <= SONIC_RANGE && (distance < 1.0 || to.normalize().dot(front) >= SONIC_CONE_COS);
	}

	@Override
	public void die(DamageSource source) {
		playAnimation("death");
		super.die(source);
	}

	@Override
	protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean killedByPlayer) {
		super.dropCustomDeathLoot(level, source, killedByPlayer);
		spawnAtLocation(level, new ItemStack(Items.LEATHER, 2 + random.nextInt(3)));
		dropMeat(level, source);
	}

	@Override
	public float getVoicePitch() {
		return 0.35F;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.DOLPHIN_AMBIENT_WATER;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.DOLPHIN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.DOLPHIN_DEATH;
	}
}
