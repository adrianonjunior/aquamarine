package com.aquamarine.entity;

import com.aquamarine.ModTags;
import com.aquamarine.config.AquamarineConfig;
import com.aquamarine.item.ModItems;

import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Baleia-de-Nereia (docs/08-fase3.md, seção 6): passiva, 200 de vida, bioluminescente. Canta de
 * longe; domada com 4 a 6 peixes crus, com a Sela de Baleia carrega 3 jogadores (só o dono pilota,
 * configurável), sobe sozinha para respirar a cada 3 minutos e dá Respiração Aquática a quem monta.
 */
public class WhaleEntity extends AbstractWhaleEntity implements WhaleView {
	public static final List<String> ANIMATION_NAMES = List.of("idle", "swim", "sing", "surface", "death");
	public static final float MAX_HEALTH = 200.0F;
	public static final int MAX_RIDERS = 3;
	/** ~9 blocos por segundo pilotada. */
	public static final double RIDE_SPEED = 0.45;
	/** Sobe para respirar a cada 3 minutos debaixo d'água. */
	public static final int BREATH_TICKS = 3 * 60 * 20;
	public static final int MIN_FOOD = 4;
	public static final int MAX_FOOD = 6;
	public static final int XP = 20;
	/** Lugares na sela, em blocos (x, altura, frente) relativos à baleia: piloto na frente. */
	private static final Vec3[] SEATS = {new Vec3(0, 5.3, 0.6), new Vec3(0, 5.2, -1.2), new Vec3(0, 5.0, -3.0)};

	private static final EntityDataAccessor<Boolean> DATA_SADDLED = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DATA_TAMED = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DATA_SURFACING = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.BOOLEAN);
	/**
	 * Quem pilota precisa ser conhecido dos dois lados: no 26.3 a montaria anda no cliente do piloto (como o cavalo),
	 * e com o dono só no servidor nenhum dos dois movia a baleia. Dono como texto ("" = sem dono) e a regra
	 * {@code whaleOwnerOnlyPilots} da config do servidor.
	 */
	private static final EntityDataAccessor<String> DATA_OWNER = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Boolean> DATA_OWNER_PILOTS = SynchedEntityData.defineId(WhaleEntity.class, EntityDataSerializers.BOOLEAN);

	private UUID owner;
	private int fed;
	private int foodNeeded;
	private int breath;
	private int songCooldown = 200;
	private BlockPos cityHint;
	private int cityHintAge = Integer.MAX_VALUE;

	public WhaleEntity(EntityType<? extends WhaleEntity> type, Level level) {
		super(type, level, ANIMATION_NAMES);
		this.xpReward = XP;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return PathfinderMob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, MAX_HEALTH)
			.add(Attributes.MOVEMENT_SPEED, 0.6)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
			.add(Attributes.STEP_HEIGHT, 1.0);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SADDLED, false);
		builder.define(DATA_TAMED, false);
		builder.define(DATA_SURFACING, false);
		builder.define(DATA_OWNER, "");
		builder.define(DATA_OWNER_PILOTS, true);
	}

	// ---- Contrato WhaleView ----

	@Override
	public AnimationState idleAnimation() {
		return animations.state("idle");
	}

	@Override
	public AnimationState swimAnimation() {
		return animations.state("swim");
	}

	@Override
	public AnimationState singAnimation() {
		return animations.state("sing");
	}

	@Override
	public AnimationState surfaceAnimation() {
		return animations.state("surface");
	}

	@Override
	public AnimationState deathAnimation() {
		return animations.state("death");
	}

	@Override
	public boolean saddled() {
		return entityData.get(DATA_SADDLED);
	}

	// ---- Domar e selar ----

	public boolean isTamed() {
		return entityData.get(DATA_TAMED);
	}

	public UUID owner() {
		return owner;
	}

	private void setOwner(UUID owner) {
		this.owner = owner;
		entityData.set(DATA_OWNER, owner == null ? "" : owner.toString());
	}

	public boolean isSurfacing() {
		return entityData.get(DATA_SURFACING);
	}

	/** Doma direto (comandos e testes). */
	public void tame(Player player) {
		setOwner(player.getUUID());
		entityData.set(DATA_TAMED, true);
		setPersistenceRequired();
		level().broadcastEntityEvent(this, (byte) 7);
	}

	public void setSaddled(boolean saddled) {
		entityData.set(DATA_SADDLED, saddled);
	}

	public int fed() {
		return fed;
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!isTamed()) {
			if (stack.is(Items.COD) || stack.is(Items.SALMON)) {
				if (level() instanceof ServerLevel) {
					stack.consume(1, player);
					feed(player);
				}
				return InteractionResult.SUCCESS;
			}
			return super.mobInteract(player, hand);
		}
		if (!saddled() && stack.is(ModItems.WHALE_SADDLE)) {
			if (level() instanceof ServerLevel) {
				stack.consume(1, player);
				setSaddled(true);
				playSound(SoundEvents.HORSE_SADDLE.value(), 1.0F, 0.6F);
			}
			return InteractionResult.SUCCESS;
		}
		if (saddled() && !player.isSecondaryUseActive() && canAddPassenger(player)) {
			if (level() instanceof ServerLevel) {
				player.startRiding(this);
			}
			return InteractionResult.SUCCESS;
		}
		return super.mobInteract(player, hand);
	}

	/** Um peixe cru: de 4 a 6 (sorteado no primeiro) a baleia confia em quem a alimentou. */
	private void feed(Player player) {
		if (foodNeeded == 0) {
			foodNeeded = MIN_FOOD + random.nextInt(MAX_FOOD - MIN_FOOD + 1);
		}
		fed++;
		playSound(SoundEvents.DOLPHIN_EAT, 1.0F, 0.5F);
		if (fed >= foodNeeded) {
			tame(player);
		} else {
			level().broadcastEntityEvent(this, (byte) 6);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 7 || id == 6) {
			for (int i = 0; i < 14; i++) {
				level().addParticle(id == 7 ? ParticleTypes.HEART : ParticleTypes.HAPPY_VILLAGER,
					getRandomX(1.0), getY() + 3.0 + random.nextDouble() * 2.0, getRandomZ(1.0), 0, 0.05, 0);
			}
		} else {
			super.handleEntityEvent(id);
		}
	}

	// ---- Montaria ----

	/** Só o dono pilota (config {@code whaleOwnerOnlyPilots} do servidor); qualquer um pode ser passageiro. */
	public boolean canPilot(Player player) {
		return !entityData.get(DATA_OWNER_PILOTS) || player.getUUID().toString().equals(entityData.get(DATA_OWNER));
	}

	@Override
	public LivingEntity getControllingPassenger() {
		if (!saddled()) {
			return null;
		}
		for (Entity passenger : getPassengers()) {
			if (passenger instanceof Player player && canPilot(player)) {
				return player;
			}
		}
		return null;
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return getPassengers().size() < MAX_RIDERS;
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
		int index = Math.max(0, getPassengers().indexOf(passenger));
		LivingEntity pilot = getControllingPassenger();
		// O piloto fica no lugar da frente, os outros atrás, na ordem em que subiram.
		if (pilot != null && passenger != pilot) {
			index = 1 + (int) getPassengers().stream().filter(p -> p != pilot).toList().indexOf(passenger);
		} else if (pilot != null) {
			index = 0;
		}
		Vec3 seat = SEATS[Math.min(index, SEATS.length - 1)];
		return seat.yRot(-getYRot() * Mth.DEG_TO_RAD);
	}

	@Override
	protected Vec3 getRiddenInput(Player controller, Vec3 selfInput) {
		return new Vec3(controller.xxa, 0.0, controller.zza);
	}

	@Override
	protected void tickRidden(Player controller, Vec3 riddenInput) {
		super.tickRidden(controller, riddenInput);
		setYRot(Mth.approachDegrees(getYRot(), controller.getYRot(), 5.0F));
		setXRot(Mth.approach(getXRot(), Mth.clamp(controller.getXRot() * 0.6F, -30.0F, 30.0F), 3.0F));
		yBodyRot = getYRot();
		yHeadRot = getYRot();
	}

	/** Pilotada: segue o olhar do piloto; na hora de respirar, sobe sozinha; nunca entra a 48 blocos do Kraken. */
	@Override
	public void travel(Vec3 input) {
		if (getControllingPassenger() instanceof Player rider && isInWater()) {
			Vec3 target;
			if (isSurfacing()) {
				target = new Vec3(0, RIDE_SPEED * 0.6, 0);
			} else {
				Vec3 look = rider.getLookAngle();
				target = input.z > 0 ? look.scale(RIDE_SPEED) : input.z < 0 ? look.scale(-RIDE_SPEED * 0.3) : Vec3.ZERO;
				KrakenEntity kraken = nearestKraken();
				if (kraken != null) {
					Vec3 away = position().subtract(kraken.position()).normalize();
					double toward = target.dot(away.scale(-1));
					if (toward > 0) {
						target = target.add(away.scale(toward));
					}
					target = target.add(away.scale(0.1));
				}
			}
			setDeltaMovement(getDeltaMovement().lerp(target, 0.12));
			move(MoverType.SELF, getDeltaMovement());
			return;
		}
		super.travel(input);
	}

	// ---- Comportamento no servidor ----

	@Override
	public void tick() {
		super.tick();
		if (level() instanceof ServerLevel level) {
			boolean ownerPilots = AquamarineConfig.get().whaleOwnerOnlyPilots;
			if (entityData.get(DATA_OWNER_PILOTS) != ownerPilots) {
				entityData.set(DATA_OWNER_PILOTS, ownerPilots);
			}
			tickBreath(level);
			if (isVehicle() && tickCount % 20 == 0) {
				// Bolha de ar: quem monta respira debaixo d'água (a Pressão cai 1 nível no PressureManager).
				for (Entity passenger : getPassengers()) {
					if (passenger instanceof LivingEntity living) {
						living.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false, true));
					}
				}
			}
			if (--songCooldown <= 0) {
				sing(level);
			}
		}
	}

	/** Conta o fôlego; aos 3 minutos sobe, bufa um esguicho na superfície e volta. */
	private void tickBreath(ServerLevel level) {
		boolean submerged = isEyeInFluid(FluidTags.WATER);
		if (isSurfacing()) {
			if (!submerged) {
				entityData.set(DATA_SURFACING, false);
				breath = 0;
				Vec3 spout = position().add(0, 6.0, 0).add(Vec3.directionFromRotation(0, getYRot()).scale(3.0));
				level.sendParticles(ParticleTypes.SPLASH, spout.x, spout.y, spout.z, 60, 0.4, 1.2, 0.4, 0.3);
				level.playSound(null, spout.x, spout.y, spout.z, SoundEvents.GENERIC_SPLASH, SoundSource.NEUTRAL, 2.0F, 0.5F);
			}
		} else if (submerged && ++breath >= BREATH_TICKS) {
			entityData.set(DATA_SURFACING, true);
			playAnimation("surface");
		}
	}

	/** Canto: som que se ouve de longe e um rastro de luz apontando a Cidade Perdida mais próxima. */
	public void sing(ServerLevel level) {
		songCooldown = 900 + random.nextInt(600);
		playAnimation("sing");
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.ELDER_GUARDIAN_AMBIENT, SoundSource.NEUTRAL, 8.0F, 0.45F);
		if (cityHintAge > 6000) {
			cityHint = level.findNearestMapStructure(ModTags.Structures.LOST_CITY, blockPosition(), 64, false);
			cityHintAge = 0;
		}
		cityHintAge += songCooldown;
		Vec3 head = position().add(0, 4.0, 0);
		Vec3 dir = cityHint != null ? Vec3.atCenterOf(cityHint).subtract(head).multiply(1, 0, 1).normalize() : Vec3.ZERO;
		for (int i = 0; i < 24; i++) {
			Vec3 p = head.add(dir.scale(4.0 + i * 0.8));
			level.sendParticles(ParticleTypes.GLOW, p.x, p.y + random.nextGaussian() * 0.3, p.z, 2, 0.4, 0.4, 0.4, 0.0);
		}
	}

	@Override
	protected void serverBehavior(ServerLevel level) {
		if (isSurfacing()) {
			steer(new Vec3(0, 1, 0), 0.25, 2.0F);
			return;
		}
		LivingEntity attacker = getLastHurtByMob();
		if (attacker != null && tickCount - getLastHurtByMobTimestamp() < 100) {
			steer(position().subtract(attacker.position()), 0.3, 4.0F);
			return;
		}
		wander(level, 0.12);
	}

	// ---- Morte, persistência e sons ----

	@Override
	public void die(DamageSource source) {
		playAnimation("death");
		super.die(source);
	}

	@Override
	protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean killedByPlayer) {
		super.dropCustomDeathLoot(level, source, killedByPlayer);
		spawnAtLocation(level, new ItemStack(Items.LEATHER, 2 + random.nextInt(3)));
		if (!isTamed()) {
			dropMeat(level, source);
		}
		if (saddled()) {
			spawnAtLocation(level, new ItemStack(ModItems.WHALE_SADDLE));
		}
	}

	@Override
	public boolean removeWhenFarAway(double distSqr) {
		return !isTamed() && super.removeWhenFarAway(distSqr);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putBoolean("Tamed", isTamed());
		output.putBoolean("Saddled", saddled());
		if (owner != null) {
			output.putString("Owner", owner.toString());
		}
		output.putInt("Fed", fed);
		output.putInt("FoodNeeded", foodNeeded);
		output.putInt("Breath", breath);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		entityData.set(DATA_TAMED, input.getBooleanOr("Tamed", false));
		setSaddled(input.getBooleanOr("Saddled", false));
		String id = input.getStringOr("Owner", "");
		setOwner(id.isEmpty() ? null : UUID.fromString(id));
		fed = input.getIntOr("Fed", 0);
		foodNeeded = input.getIntOr("FoodNeeded", 0);
		breath = input.getIntOr("Breath", 0);
	}

	@Override
	public float getVoicePitch() {
		return 0.45F;
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
