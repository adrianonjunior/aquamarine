package com.aquamarine.entity;

import com.aquamarine.config.AquamarineConfig;
import com.aquamarine.entity.boss.AnimationSync;
import com.aquamarine.entity.boss.KrakenStats;
import com.aquamarine.item.ModItems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Moréia Tempestuosa (docs/08-fase3.md, seção 3): hostil, ~14 blocos, 150 de vida escalada por
 * jogadores, três ataques com aviso (Bote, Descarga em Cadeia, Enrolar), rabo fraco (×1,5), imune a
 * dano elétrico e atordoada 3 s por ele. A posição da entidade é a cabeça; o corpo são
 * {@link MorayPartEntity}. Toda a lógica roda no servidor; o cliente só anima.
 */
public class MorayEntity extends Monster implements MorayView {
	public static final List<String> ANIMATION_NAMES = List.of("idle", "swim", "strike", "chain", "coil", "stunned", "death");
	public static final float BASE_HEALTH = 150.0F;
	public static final float STRIKE_DAMAGE = 10.0F;
	public static final float CHAIN_DAMAGE = 8.0F;
	public static final double CHAIN_RADIUS = 12.0;
	public static final float COIL_DAMAGE = 3.0F;
	public static final float TAIL_MULTIPLIER = 1.5F;
	/** Dano no rabo que solta o alvo enrolado (a "vida" da cauda). */
	public static final float TAIL_GRIP = 30.0F;
	public static final double SCALING_RADIUS = 48.0;
	public static final int STRIKE_WARN = 20;
	public static final int CHAIN_WARN = 30;
	public static final int COIL_WARN = 20;
	public static final int COIL_HOLD = 80;
	public static final int STUN_TICKS = 60;
	public static final int XP = 40;
	public static final int MAX_SCALES = 6;
	/** Profundidade mínima abaixo do nível do mar para surgir sozinha. */
	public static final int SPAWN_DEPTH = 25;

	/** Centros das caixas do corpo, em blocos atrás do centro da cabeça (segmentos do modelo); o último é o rabo. */
	public static final double[] PART_OFFSETS = {2.2, 4.0, 5.8, 7.6, 9.4, 11.0, 12.6};
	public static final float[] PART_SIZES = {1.4F, 1.3F, 1.3F, 1.2F, 1.1F, 1.0F, 1.2F};
	public static final int TAIL_INDEX = PART_OFFSETS.length - 1;
	/** Altura da linha central do corpo acima dos pés da cabeça (a do modelo). */
	private static final double CENTER_Y = 0.8;

	private static final EntityDataAccessor<Integer> DATA_ANIM = SynchedEntityData.defineId(MorayEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_ANIM_SEQ = SynchedEntityData.defineId(MorayEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> DATA_CHARGE = SynchedEntityData.defineId(MorayEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> DATA_STUNNED = SynchedEntityData.defineId(MorayEntity.class, EntityDataSerializers.BOOLEAN);

	public enum Attack { NONE, STRIKE, CHAIN, COIL }

	private final AnimationSync animations = new AnimationSync(ANIMATION_NAMES);
	private final List<MorayPartEntity> parts = new ArrayList<>();
	private final Set<UUID> struck = new HashSet<>();
	private Attack attack = Attack.NONE;
	private int attackTicks;
	private int cooldown = 40;
	private int stunTicks;
	private int stunImmunity;
	private LivingEntity coiled;
	private float tailDamage;
	private Vec3 lunge = Vec3.ZERO;
	private Vec3 wanderTarget;
	private int playersNearby = 1;
	/** Desliga ataques e movimento (comando de teste). */
	private boolean aiFrozen;

	public MorayEntity(EntityType<? extends MorayEntity> type, Level level) {
		super(type, level);
		this.setNoGravity(true);
		this.xpReward = XP;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, BASE_HEALTH)
			.add(Attributes.ATTACK_DAMAGE, STRIKE_DAMAGE)
			.add(Attributes.MOVEMENT_SPEED, 0.3)
			.add(Attributes.FOLLOW_RANGE, 32.0)
			.add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
	}

	/** Regra de surgimento natural: na água, a {@link #SPAWN_DEPTH} blocos ou mais, em fossas ou sob tempestade. */
	public static boolean checkSpawn(EntityType<MorayEntity> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
		if (reason != EntitySpawnReason.NATURAL && reason != EntitySpawnReason.CHUNK_GENERATION) {
			return true;
		}
		if (!level.getFluidState(pos).is(FluidTags.WATER) || pos.getY() > level.getLevel().getSeaLevel() - SPAWN_DEPTH) {
			return false;
		}
		boolean trench = level.getBiome(pos).is(com.aquamarine.ModTags.Biomes.MORAY_TRENCHES);
		return (trench || level.getLevel().isThundering()) && random.nextInt(trench ? 6 : 3) == 0
			&& level.getEntitiesOfClass(MorayEntity.class, new AABB(pos).inflate(64.0)).isEmpty();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_ANIM, 0);
		builder.define(DATA_ANIM_SEQ, 0);
		builder.define(DATA_CHARGE, 0.0F);
		builder.define(DATA_STUNNED, false);
	}

	@Override
	protected void registerGoals() {
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	// ---- Animações (contrato MorayView) ----

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
	public AnimationState idleAnimation() {
		return animations.state("idle");
	}

	@Override
	public AnimationState swimAnimation() {
		return animations.state("swim");
	}

	@Override
	public AnimationState strikeAnimation() {
		return animations.state("strike");
	}

	@Override
	public AnimationState chainAnimation() {
		return animations.state("chain");
	}

	@Override
	public AnimationState coilAnimation() {
		return animations.state("coil");
	}

	@Override
	public AnimationState stunnedAnimation() {
		return animations.state("stunned");
	}

	@Override
	public AnimationState deathAnimation() {
		return animations.state("death");
	}

	@Override
	public float chargeFraction() {
		return entityData.get(DATA_CHARGE);
	}

	public boolean isStunned() {
		return entityData.get(DATA_STUNNED);
	}

	public Attack currentAttack() {
		return attack;
	}

	public LivingEntity coiledTarget() {
		return coiled;
	}

	public void setAiFrozen(boolean frozen) {
		this.aiFrozen = frozen;
	}

	// ---- Corpo segmentado ----

	/** Direção da cabeça para o rabo (oposta ao olhar), pela rotação do corpo. */
	private Vec3 backward() {
		return Vec3.directionFromRotation(getXRot(), yBodyRot).scale(-1.0);
	}

	/** Centro do trecho {@code index} do corpo no mundo. */
	public Vec3 partCenter(int index) {
		return position().add(0, CENTER_Y, 0).add(backward().scale(PART_OFFSETS[index]));
	}

	void placePart(MorayPartEntity part) {
		Vec3 c = partCenter(part.index());
		part.setPos(c.x, c.y - part.getBbHeight() / 2.0, c.z);
	}

	public List<MorayPartEntity> parts() {
		return parts;
	}

	private void ensureParts(ServerLevel level) {
		parts.removeIf(p -> p.isRemoved());
		if (parts.size() == PART_OFFSETS.length) {
			return;
		}
		parts.forEach(Entity::discard);
		parts.clear();
		for (int i = 0; i < PART_OFFSETS.length; i++) {
			MorayPartEntity part = new MorayPartEntity(level, this, i);
			placePart(part);
			level.addFreshEntity(part);
			parts.add(part);
		}
	}

	@Override
	public void remove(RemovalReason reason) {
		super.remove(reason);
		parts.forEach(Entity::discard);
		parts.clear();
	}

	/** Golpe numa parte do corpo: o rabo leva ×1,5 e, durante o Enrolar, conta para soltar o alvo. */
	boolean hurtPart(ServerLevel level, MorayPartEntity part, DamageSource source, float damage) {
		boolean tail = part.index() == TAIL_INDEX;
		float amount = tail ? damage * TAIL_MULTIPLIER : damage;
		if (tail && coiled != null && !source.is(ElectricDamage.TYPE)) {
			tailDamage += amount;
			if (tailDamage >= TAIL_GRIP) {
				releaseCoil();
				level.sendParticles(ParticleTypes.CRIT, getX(), getY() + 1, getZ(), 20, 0.6, 0.6, 0.6, 0.2);
			}
		}
		return hurtServer(level, source, amount);
	}

	// ---- Dano, atordoamento e escala ----

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		if (source.is(ElectricDamage.TYPE)) {
			// Imune ao próprio elemento, mas o choque a atordoa (docs/08-fase3.md, Fraquezas).
			stun(level);
			return false;
		}
		return super.hurtServer(level, source, damage);
	}

	public void stun(ServerLevel level) {
		if (stunImmunity > 0 || isDeadOrDying()) {
			return;
		}
		cancelAttack();
		stunTicks = STUN_TICKS;
		stunImmunity = STUN_TICKS + 100;
		entityData.set(DATA_STUNNED, true);
		playAnimation("stunned");
		level.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 1, getZ(), 30, 1.0, 0.6, 1.0, 0.2);
	}

	/** Jogadores vivos a {@link #SCALING_RADIUS} blocos (o {@code n} da escala). */
	private int countPlayers(ServerLevel level) {
		return Math.max(1, level.getEntitiesOfClass(Player.class, getBoundingBox().inflate(SCALING_RADIUS),
			p -> p.isAlive() && !p.isSpectator()).size());
	}

	/** {@code f = 1 + 0,3 × (n − 1)} na vida, mantendo a fração atual; o dano não escala. */
	private void rescale(ServerLevel level) {
		playersNearby = countPlayers(level);
		float max = BASE_HEALTH * KrakenStats.scale(playersNearby, AquamarineConfig.get().scalingCap);
		var attribute = getAttribute(Attributes.MAX_HEALTH);
		if (attribute != null && Math.abs(attribute.getBaseValue() - max) > 0.01) {
			float fraction = getHealth() / getMaxHealth();
			attribute.setBaseValue(max);
			setHealth(fraction * max);
		}
	}

	// ---- Tick e IA ----

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			animations.state("idle").startIfStopped(tickCount);
			animations.state("swim").animateWhen(!isStunned() && getDeltaMovement().lengthSqr() > 4.0E-3, tickCount);
			clientEffects();
		}
	}

	/** Faíscas azuis crescentes na carga (aviso da Descarga) e no rabo (aviso do Enrolar). */
	private void clientEffects() {
		float charge = chargeFraction();
		if (charge <= 0.0F || random.nextFloat() > charge) {
			return;
		}
		int index = random.nextInt(PART_OFFSETS.length);
		Vec3 c = partCenter(index);
		level().addParticle(ParticleTypes.ELECTRIC_SPARK, c.x + random.nextGaussian() * 0.5, c.y + random.nextGaussian() * 0.5,
			c.z + random.nextGaussian() * 0.5, 0, 0.05, 0);
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		ensureParts(level);
		setNoGravity(isInWater());
		if (tickCount % 100 == 1) {
			rescale(level);
		}
		if (stunImmunity > 0) {
			stunImmunity--;
		}
		if (stunTicks > 0) {
			setDeltaMovement(getDeltaMovement().scale(0.7));
			if (--stunTicks == 0) {
				entityData.set(DATA_STUNNED, false);
				animations.state("stunned").stop();
				playAnimation("idle");
			}
			return;
		}
		LivingEntity target = getTarget();
		if (attack != Attack.NONE) {
			// Um ataque já começado (também por comando) termina mesmo com a IA desligada.
			tickAttack(level, target);
		} else if (aiFrozen) {
			setDeltaMovement(Vec3.ZERO);
		} else if (target != null && target.isAlive()) {
			chase(level, target);
		} else {
			wander(level);
		}
	}

	/** Vira o corpo para a direção do movimento (yaw e inclinação suaves). */
	private void face(Vec3 dir, float maxTurn) {
		if (dir.lengthSqr() < 1.0E-6) {
			return;
		}
		float yaw = (float) (Mth.atan2(dir.z, dir.x) * Mth.RAD_TO_DEG) - 90.0F;
		float pitch = (float) -(Mth.atan2(dir.y, dir.horizontalDistance()) * Mth.RAD_TO_DEG);
		setYRot(Mth.approachDegrees(getYRot(), yaw, maxTurn));
		setXRot(Mth.approach(getXRot(), Mth.clamp(pitch, -60.0F, 60.0F), maxTurn * 0.5F));
		yBodyRot = getYRot();
		yHeadRot = getYRot();
	}

	private void steer(Vec3 to, double speed, float turn) {
		Vec3 dir = to.lengthSqr() > 1.0E-4 ? to.normalize() : Vec3.ZERO;
		if (!isInWater()) {
			dir = new Vec3(dir.x, Math.min(dir.y, 0.0), dir.z);
		}
		setDeltaMovement(getDeltaMovement().scale(0.6).add(dir.scale(speed)));
		face(dir, turn);
	}

	private void chase(ServerLevel level, LivingEntity target) {
		Vec3 to = target.getEyePosition().subtract(position().add(0, CENTER_Y, 0));
		double distance = to.length();
		steer(to, distance > 3.0 ? 0.11 : 0.02, 8.0F);
		if (cooldown > 0) {
			cooldown--;
			return;
		}
		Attack next = choose(distance, target);
		if (next != Attack.NONE) {
			start(level, next);
		}
	}

	/** Escolhe um ataque pela distância: perto enrola, meia distância dá o bote, longe descarrega na água. */
	private Attack choose(double distance, LivingEntity target) {
		boolean wet = ElectricDamage.isWet(target);
		if (distance <= 4.0 && random.nextInt(3) == 0) {
			return Attack.COIL;
		}
		if (distance <= 9.0 && (random.nextInt(3) != 0 || !wet)) {
			return Attack.STRIKE;
		}
		return wet && distance <= CHAIN_RADIUS ? Attack.CHAIN : Attack.NONE;
	}

	/** Começa um ataque pelo aviso (para testes e comandos também). */
	public void start(ServerLevel level, Attack next) {
		attack = next;
		attackTicks = 0;
		struck.clear();
		tailDamage = 0.0F;
		switch (next) {
			case STRIKE -> {
				playAnimation("strike");
				playSound(SoundEvents.GUARDIAN_ATTACK, 1.5F, 0.7F);
			}
			case CHAIN -> {
				playAnimation("chain");
				level.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 2.0F, 1.8F);
			}
			case COIL -> {
				playAnimation("coil");
				playSound(SoundEvents.ELDER_GUARDIAN_CURSE, 0.8F, 1.6F);
			}
			default -> {}
		}
	}

	private void tickAttack(ServerLevel level, LivingEntity target) {
		attackTicks++;
		switch (attack) {
			case STRIKE -> tickStrike(level, target);
			case CHAIN -> tickChain(level);
			case COIL -> tickCoil(level, target);
			default -> cancelAttack();
		}
	}

	/** Bote: recua 1 s olhando o alvo, avança ~7 blocos em linha reta e morde o que encostar na cabeça. */
	private void tickStrike(ServerLevel level, LivingEntity target) {
		if (attackTicks <= STRIKE_WARN) {
			Vec3 to = target != null ? target.getEyePosition().subtract(position().add(0, CENTER_Y, 0)) : Vec3.directionFromRotation(getXRot(), getYRot());
			face(to, 12.0F);
			setDeltaMovement(to.normalize().scale(-0.04));
			lunge = to.normalize();
			return;
		}
		if (attackTicks <= STRIKE_WARN + 8) {
			setDeltaMovement(lunge.scale(0.9));
			face(lunge, 4.0F);
			for (LivingEntity hit : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.9),
				e -> e != this && e.isAlive() && !(e instanceof MorayEntity) && !struck.contains(e.getUUID()))) {
				struck.add(hit.getUUID());
				hit.hurtServer(level, damageSources().mobAttack(this), STRIKE_DAMAGE);
			}
			return;
		}
		finishAttack(40);
	}

	/** Descarga em Cadeia: 1,5 s de carga visível, depois 8 de dano e Lentidão em todos na água a 12 blocos. */
	private void tickChain(ServerLevel level) {
		setDeltaMovement(getDeltaMovement().scale(0.5));
		if (attackTicks < CHAIN_WARN) {
			entityData.set(DATA_CHARGE, attackTicks / (float) CHAIN_WARN);
			return;
		}
		Vec3 center = partCenter(2);
		List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(CHAIN_RADIUS),
			e -> e.isAlive() && !(e instanceof MorayEntity) && e.position().distanceTo(center) <= CHAIN_RADIUS && e.isInWater()
				&& ElectricDamage.isWet(e));
		for (LivingEntity target : targets) {
			if (ElectricDamage.hurt(level, target, this, this, CHAIN_DAMAGE) > 0.0F) {
				target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1), this);
			}
			ElectricDamage.line(level, center, target.position().add(0, target.getBbHeight() * 0.5, 0));
		}
		level.playSound(null, center.x, center.y, center.z, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 2.0F, 1.6F);
		level.sendParticles(ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, 80, 4.0, 2.0, 4.0, 0.3);
		finishAttack(70);
	}

	/** Enrolar: a cauda brilha 1 s; quem estiver a 5 blocos é preso 4 s (3 de dano por segundo) até acertarem o rabo. */
	private void tickCoil(ServerLevel level, LivingEntity target) {
		if (attackTicks < COIL_WARN) {
			entityData.set(DATA_CHARGE, attackTicks / (float) COIL_WARN);
			if (target != null) {
				steer(target.getEyePosition().subtract(position()), 0.05, 8.0F);
			}
			return;
		}
		if (attackTicks == COIL_WARN) {
			entityData.set(DATA_CHARGE, 0.0F);
			if (target == null || !target.isAlive() || target.distanceTo(this) > 5.0) {
				finishAttack(30);
				return;
			}
			coiled = target;
			playSound(SoundEvents.ELDER_GUARDIAN_HURT, 1.0F, 0.6F);
		}
		if (coiled == null || !coiled.isAlive() || coiled.distanceTo(this) > 10.0 || attackTicks > COIL_WARN + COIL_HOLD) {
			releaseCoil();
			finishAttack(80);
			return;
		}
		setDeltaMovement(Vec3.ZERO);
		Vec3 hold = partCenter(2).subtract(0, coiled.getBbHeight() * 0.5, 0);
		coiled.setDeltaMovement(hold.subtract(coiled.position()).scale(0.5));
		coiled.needsSync = true;
		if ((attackTicks - COIL_WARN) % 20 == 0) {
			coiled.hurtServer(level, damageSources().mobAttack(this), COIL_DAMAGE);
		}
	}

	private void releaseCoil() {
		coiled = null;
		if (attack == Attack.COIL) {
			animations.state("coil").stop();
			finishAttack(80);
		}
	}

	private void finishAttack(int nextCooldown) {
		attack = Attack.NONE;
		attackTicks = 0;
		cooldown = nextCooldown + random.nextInt(20);
		entityData.set(DATA_CHARGE, 0.0F);
	}

	private void cancelAttack() {
		coiled = null;
		finishAttack(40);
	}

	/** Sem alvo: nada devagar entre pontos de água a até 12 blocos. */
	private void wander(ServerLevel level) {
		if (wanderTarget == null || position().distanceToSqr(wanderTarget) < 4.0 || random.nextInt(200) == 0) {
			BlockPos pos = blockPosition().offset(random.nextInt(25) - 12, random.nextInt(9) - 4, random.nextInt(25) - 12);
			wanderTarget = level.getFluidState(pos).is(FluidTags.WATER) ? Vec3.atCenterOf(pos) : null;
		}
		if (wanderTarget != null) {
			steer(wanderTarget.subtract(position()), 0.04, 4.0F);
		} else {
			setDeltaMovement(getDeltaMovement().scale(0.8));
		}
	}

	// ---- Morte e drops ----

	@Override
	public void die(DamageSource source) {
		playAnimation("death");
		super.die(source);
	}

	/** Escamas pelos jogadores na luta: 2 + n ÷ 2 (máximo 6). */
	public static int scaleDrops(int players) {
		return Math.min(MAX_SCALES, 2 + Math.max(1, players) / 2);
	}

	@Override
	protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean killedByPlayer) {
		super.dropCustomDeathLoot(level, source, killedByPlayer);
		int count = scaleDrops(countPlayers(level));
		if (source.getEntity() instanceof LivingEntity killer) {
			int looting = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.LOOTING)
				.map(holder -> EnchantmentHelper.getEnchantmentLevel(holder, killer)).orElse(0);
			count += looting > 0 ? random.nextInt(looting + 1) : 0;
		}
		spawnAtLocation(level, new ItemStack(ModItems.ELECTRIC_SCALE, count));
	}

	// ---- Água e sons ----

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.GUARDIAN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.GUARDIAN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ELDER_GUARDIAN_DEATH;
	}
}
