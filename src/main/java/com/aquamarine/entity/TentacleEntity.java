package com.aquamarine.entity;

import com.aquamarine.entity.boss.KrakenAttackType;

import com.aquamarine.entity.boss.AnimationSync;
import com.aquamarine.sound.ModSounds;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.AnimationState;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Tentáculo do Kraken (docs/02-kraken.md, "Tentáculos"): 60 de vida, independente do corpo e com
 * referência ao dono. Quem decide o que ele faz é o {@link com.aquamarine.entity.boss.KrakenAttacks}
 * do Kraken; aqui só há o estado. O modelo vem da faixa 2D-M (por enquanto o renderer é provisório).
 */
public class TentacleEntity extends Mob implements TentacleView {
	/** Nomes das animações do contrato ({@link TentacleView}). */
	public static final List<String> ANIMATION_NAMES = List.of("idle", "rise", "slam", "grab", "release");
	private static final EntityDataAccessor<Integer> DATA_ANIM = SynchedEntityData.defineId(TentacleEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_ANIM_SEQ = SynchedEntityData.defineId(TentacleEntity.class, EntityDataSerializers.INT);

	private final AnimationSync animations = new AnimationSync(ANIMATION_NAMES);
	private UUID owner;
	private int slot;
	/** Em um ataque (não pode ser escolhido para outro). */
	private boolean busy;
	private int pinnedTicks;

	public TentacleEntity(EntityType<? extends TentacleEntity> type, Level level) {
		super(type, level);
		this.setNoGravity(true);
		this.setPersistenceRequired();
		this.xpReward = 0;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, KrakenAttackType.TENTACLE_HEALTH)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
			.add(Attributes.MOVEMENT_SPEED, 0.0);
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
		}
	}

	@Override
	public AnimationState idleAnimation() {
		return animations.state("idle");
	}

	@Override
	public AnimationState riseAnimation() {
		return animations.state("rise");
	}

	@Override
	public AnimationState slamAnimation() {
		return animations.state("slam");
	}

	@Override
	public AnimationState grabAnimation() {
		return animations.state("grab");
	}

	@Override
	public AnimationState releaseAnimation() {
		return animations.state("release");
	}

	public void bind(UUID owner, int slot) {
		this.owner = owner;
		this.slot = slot;
	}

	public UUID owner() {
		return owner;
	}

	public int slot() {
		return slot;
	}

	/** Ocupado num ataque ou preso pelo Arpão (não ataca nem acompanha o corpo). */
	public boolean isBusy() {
		return busy || pinnedTicks > 0;
	}

	/** O Arpão prende o tentáculo (docs/02-kraken.md): fica parado por {@code ticks}. */
	public void pin(int ticks) {
		pinnedTicks = Math.max(pinnedTicks, ticks);
		setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
	}

	public boolean isPinned() {
		return pinnedTicks > 0;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		if (pinnedTicks > 0) {
			pinnedTicks--;
			setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
		}
		if (owner != null && (!(level.getEntity(owner) instanceof KrakenEntity kraken) || !kraken.isAlive())) {
			discard();
		}
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		// Escudo e dano de afogamento não contam: o tentáculo só cai por golpes.
		if (source.is(net.minecraft.tags.DamageTypeTags.IS_DROWNING)) {
			return false;
		}
		return super.hurtServer(level, source, damage);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putInt("Slot", slot);
		if (owner != null) {
			output.putString("Owner", owner.toString());
		}
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		slot = input.getIntOr("Slot", 0);
		owner = input.getString("Owner").map(UUID::fromString).orElse(null);
	}

	// --- Sons (docs/09-audio.md) ---

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ModSounds.TENTACLE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSounds.TENTACLE_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 2.0F;
	}
}
