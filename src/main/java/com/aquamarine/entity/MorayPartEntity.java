package com.aquamarine.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Caixa de colisão de um trecho do corpo da Moréia (técnica do Ender Dragon, mas como entidade
 * comum para o cliente poder mirar nela). Não é salva: a Moréia recria as partes ao carregar.
 * O dano vai para a dona ({@link MorayEntity#hurtPart}); o último trecho é o rabo.
 */
public class MorayPartEntity extends Entity {
	private static final EntityDataAccessor<Integer> DATA_OWNER = SynchedEntityData.defineId(MorayPartEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_INDEX = SynchedEntityData.defineId(MorayPartEntity.class, EntityDataSerializers.INT);
	/** Ticks sem dona até sumir (a dona saiu do alcance ou morreu). */
	private static final int ORPHAN_TICKS = 40;

	private int orphan;

	public MorayPartEntity(EntityType<? extends MorayPartEntity> type, Level level) {
		super(type, level);
		this.noPhysics = true;
		this.setNoGravity(true);
	}

	MorayPartEntity(Level level, MorayEntity owner, int index) {
		this(ModEntities.MORAY_PART, level);
		entityData.set(DATA_OWNER, owner.getId());
		entityData.set(DATA_INDEX, index);
		refreshDimensions();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_OWNER, -1);
		builder.define(DATA_INDEX, 0);
	}

	public int index() {
		return entityData.get(DATA_INDEX);
	}

	public MorayEntity owner() {
		return level().getEntity(entityData.get(DATA_OWNER)) instanceof MorayEntity moray ? moray : null;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (key.equals(DATA_INDEX)) {
			refreshDimensions();
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		float size = MorayEntity.PART_SIZES[Math.min(index(), MorayEntity.PART_SIZES.length - 1)];
		return EntityDimensions.fixed(size, size);
	}

	@Override
	public void tick() {
		super.tick();
		MorayEntity owner = owner();
		if (owner == null || !owner.isAlive()) {
			if (!level().isClientSide() && ++orphan > ORPHAN_TICKS) {
				discard();
			}
			return;
		}
		orphan = 0;
		// Cliente e servidor calculam a mesma posição a partir da dona: a mira segue o corpo sem atraso.
		owner.placePart(this);
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		MorayEntity owner = owner();
		return owner != null && owner.hurtPart(level, this, source, damage);
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean is(Entity other) {
		return this == other || owner() == other;
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {}
}
