package com.aquamarine.entity;

import com.aquamarine.entity.boss.AnimationSync;

import java.util.List;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Ovo do Kraken: aparece no aviso do ataque "Crias de Kraken" (pulsa), choca no golpe e some. Não
 * sofre dano. O modelo é da faixa 2D-M ({@link KrakenEggView}).
 */
public class KrakenEggEntity extends Entity implements KrakenEggView {
	public static final List<String> ANIMATION_NAMES = List.of("pulse", "hatch");
	/** Vida máxima do ovo sem ser chocado (limpeza se o Kraken sumir no meio do ataque). */
	public static final int MAX_AGE = 20 * 10;
	public static final int HATCH_TICKS = 12;
	private static final EntityDataAccessor<Integer> DATA_ANIM = SynchedEntityData.defineId(KrakenEggEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_ANIM_SEQ = SynchedEntityData.defineId(KrakenEggEntity.class, EntityDataSerializers.INT);

	private final AnimationSync animations = new AnimationSync(ANIMATION_NAMES);
	private int hatching = -1;

	public KrakenEggEntity(EntityType<? extends KrakenEggEntity> type, Level level) {
		super(type, level);
		this.setNoGravity(true);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_ANIM, 0);
		builder.define(DATA_ANIM_SEQ, 0);
	}

	public boolean playAnimation(String name) {
		int index = animations.indexOf(name);
		if (index < 0) {
			return false;
		}
		entityData.set(DATA_ANIM, index);
		entityData.set(DATA_ANIM_SEQ, entityData.get(DATA_ANIM_SEQ) + 1);
		return true;
	}

	/** Choca (anima) e some em {@link #HATCH_TICKS}. */
	public void hatch() {
		if (hatching < 0) {
			hatching = 0;
			playAnimation("hatch");
		}
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (key.equals(DATA_ANIM_SEQ) && level().isClientSide() && entityData.get(DATA_ANIM_SEQ) > 0) {
			animations.start(entityData.get(DATA_ANIM), tickCount, -1);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			if (entityData.get(DATA_ANIM_SEQ) == 0) {
				animations.state("pulse").startIfStopped(tickCount);
			}
		} else if (hatching >= 0 ? ++hatching >= HATCH_TICKS : tickCount >= MAX_AGE) {
			discard();
		}
	}

	@Override
	public AnimationState pulseAnimation() {
		return animations.state("pulse");
	}

	@Override
	public AnimationState hatchAnimation() {
		return animations.state("hatch");
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		return false;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
	}
}
