package com.aquamarine.entity;

import com.aquamarine.entity.boss.AquamarineBossBar;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Entidade de teste oculta para a barra de vida customizada: invisível, parada, sem item de
 * criativo. {@code /summon aquamarine:boss_dummy} e {@code /damage @e[type=aquamarine:boss_dummy] 40}.
 * Tem um escudo simplificado (absorve todo o dano até quebrar e volta 10 s depois ou a cada
 * fase nova) só para exercitar a barra; o escudo de verdade é do Kraken (docs/02-kraken.md).
 */
public class BossDummy extends Mob {
	public static final float MAX_HEALTH = 300.0F;
	public static final float MAX_SHIELD = 120.0F;
	private static final int SHIELD_REGEN_DELAY = 200;
	private static final int SHIELD_REGEN_TICKS = 100;

	private final AquamarineBossBar bossBar = new AquamarineBossBar(this.getDisplayName());
	private float shield = MAX_SHIELD;
	private int shieldBrokenTicks = -1;
	private int lastPhase = 1;

	public BossDummy(EntityType<? extends BossDummy> type, Level level) {
		super(type, level);
		this.setNoAi(true);
		this.setPersistenceRequired();
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, MAX_HEALTH).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
	}

	public float shield() {
		return shield;
	}

	public int phase() {
		float f = getHealth() / getMaxHealth();
		return f > 2.0F / 3.0F ? 1 : f > 1.0F / 3.0F ? 2 : 3;
	}

	private boolean regenerating() {
		return shieldBrokenTicks >= SHIELD_REGEN_DELAY;
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		if (shield > 0 && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			shield = Math.max(0, shield - damage);
			if (shield == 0) {
				shieldBrokenTicks = 0;
				level.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.5F, 0.6F);
			}
			return true;
		}
		return super.hurtServer(level, source, damage);
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		int phase = phase();
		if (phase != lastPhase) {
			lastPhase = phase;
			if (shield < MAX_SHIELD && shieldBrokenTicks < SHIELD_REGEN_DELAY) {
				shieldBrokenTicks = SHIELD_REGEN_DELAY;
			}
		}
		if (shield < MAX_SHIELD && shieldBrokenTicks >= 0) {
			shieldBrokenTicks++;
			if (regenerating()) {
				shield = Math.min(MAX_SHIELD, shield + MAX_SHIELD / SHIELD_REGEN_TICKS);
				if (shield >= MAX_SHIELD) {
					shieldBrokenTicks = -1;
				}
			}
		}
		bossBar.update(getHealth(), getMaxHealth(), shield, MAX_SHIELD, phase, regenerating());
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		bossBar.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		bossBar.removePlayer(player);
	}

	@Override
	public void remove(RemovalReason reason) {
		bossBar.removeAllPlayers();
		super.remove(reason);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putFloat("Shield", shield);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		shield = input.getFloatOr("Shield", MAX_SHIELD);
		bossBar.setName(getDisplayName());
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isInvisible() {
		return true;
	}
}
