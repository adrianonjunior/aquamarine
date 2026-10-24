package com.aquamarine.entity;

import com.aquamarine.entity.boss.KrakenAttackType;

import java.util.UUID;

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
public class TentacleEntity extends Mob {
	private UUID owner;
	private int slot;
	/** Em um ataque (não pode ser escolhido para outro). */
	private boolean busy;

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

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		if (owner == null || !(level.getEntity(owner) instanceof KrakenEntity kraken) || !kraken.isAlive()) {
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
}
