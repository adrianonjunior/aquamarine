package com.aquamarine.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Caixa de colisão da cabeça, do fim do corpo ou do rabo de uma baleia (Fase 6, 6B1), no molde de
 * {@link MorayPartEntity}: a caixa da própria baleia é o meio do corpo. Não é salva; a baleia recria as partes. O dano
 * vai inteiro para a dona; a cabeça é sólida para quem não está montado nela. Clicar numa parte é clicar na baleia
 * (Sela e montaria).
 */
public class WhalePartEntity extends Entity {
	private static final EntityDataAccessor<Integer> DATA_OWNER = SynchedEntityData.defineId(WhalePartEntity.class, EntityDataSerializers.INT);
	/** Espécie × {@link AbstractWhaleEntity#PARTS} + parte: escolhe o tamanho em {@link AbstractWhaleEntity#PART_SHAPES}. */
	private static final EntityDataAccessor<Integer> DATA_SHAPE = SynchedEntityData.defineId(WhalePartEntity.class, EntityDataSerializers.INT);
	private static final int ORPHAN_TICKS = 40;

	private int orphan;

	public WhalePartEntity(EntityType<? extends WhalePartEntity> type, Level level) {
		super(type, level);
		this.noPhysics = true;
		this.setNoGravity(true);
	}

	WhalePartEntity(Level level, AbstractWhaleEntity owner, int shape) {
		this(ModEntities.WHALE_PART, level);
		entityData.set(DATA_OWNER, owner.getId());
		entityData.set(DATA_SHAPE, shape);
		refreshDimensions();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_OWNER, -1);
		builder.define(DATA_SHAPE, 0);
	}

	public int shape() {
		return Math.floorMod(entityData.get(DATA_SHAPE), AbstractWhaleEntity.PART_SHAPES.length);
	}

	public int index() {
		return shape() % AbstractWhaleEntity.PARTS;
	}

	public boolean isHead() {
		return index() == AbstractWhaleEntity.HEAD;
	}

	public AbstractWhaleEntity owner() {
		return level().getEntity(entityData.get(DATA_OWNER)) instanceof AbstractWhaleEntity whale ? whale : null;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (key.equals(DATA_SHAPE)) {
			refreshDimensions();
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		float[] s = AbstractWhaleEntity.PART_SHAPES[shape()];
		return EntityDimensions.fixed(s[2], s[3]);
	}

	@Override
	public void tick() {
		super.tick();
		AbstractWhaleEntity owner = owner();
		if (owner == null || !owner.isAlive()) {
			if (!level().isClientSide() && ++orphan > ORPHAN_TICKS) {
				discard();
			}
			return;
		}
		orphan = 0;
		// Cliente e servidor calculam a mesma posição a partir da dona: a mira segue o corpo sem atraso.
		owner.placePart(this);
		if (isHead() && level() instanceof ServerLevel level) {
			owner.pushOutOfHead(level, this);
		}
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		AbstractWhaleEntity owner = owner();
		return owner != null && owner.hurtPart(level, source, damage);
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean canBeCollidedWith(Entity other) {
		AbstractWhaleEntity owner = owner();
		return isHead() && owner != null && other != null && other != owner && !owner.hasPassenger(other)
			&& !(other instanceof WhalePartEntity);
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
		AbstractWhaleEntity owner = owner();
		return owner != null ? owner.interact(player, hand, location) : InteractionResult.PASS;
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
