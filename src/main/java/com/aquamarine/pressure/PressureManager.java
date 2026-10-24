package com.aquamarine.pressure;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.AnchorBuoyBlock;
import com.aquamarine.effect.ModEffects;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.item.OrichalcumEffects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Pressão (docs/02-kraken.md): nível 0 a 4 por jogador, calculado no servidor. Sobe submerso a
 * menos de {@link PressureState#RANGE} blocos de um {@link KrakenEntity}; a armadura de Oricálcio
 * reduz o ganho, o Tônico limita o máximo e a Boia de Âncora zera. O nível vai ao próprio jogador
 * por {@link PressurePayload} (vinheta e som no cliente).
 */
public final class PressureManager {
	private static final Identifier SLOW_ID = Aquamarine.id("pressure_slow");
	private static final Identifier ATTACK_ID = Aquamarine.id("pressure_attack");
	/** Nado lento (nível 2+): tira da eficiência de movimento na água. */
	private static final double SLOW_SWIM = -0.4;
	/** Velocidade de ataque perdida no nível 3+. */
	private static final double SLOW_ATTACK = -0.3;
	private static final int EXPOSURE_CHECK_TICKS = 10;

	private static final Map<UUID, Entry> STATES = new HashMap<>();

	private static final class Entry {
		final PressureState state = new PressureState();
		boolean exposed;
		int damageTimer;
		int lastSent = -1;
	}

	public static void register() {
		PayloadTypeRegistry.clientboundPlay().register(PressurePayload.TYPE, PressurePayload.CODEC);
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				tickPlayer(player);
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> STATES.remove(handler.getPlayer().getUUID()));
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> reset(newPlayer));
	}

	/** Nível atual da Pressão do jogador (0 se nunca ficou exposto). */
	public static int level(ServerPlayer player) {
		Entry entry = STATES.get(player.getUUID());
		return entry == null ? 0 : entry.state.level();
	}

	/** Zera a Pressão do jogador (Boia de Âncora, morte). */
	public static void reset(ServerPlayer player) {
		Entry entry = STATES.get(player.getUUID());
		if (entry != null) {
			entry.state.reset();
			entry.damageTimer = 0;
			apply(player, entry);
		}
	}

	private static void tickPlayer(ServerPlayer player) {
		if (player.isSpectator() || player.isCreative()) {
			if (STATES.containsKey(player.getUUID())) {
				reset(player);
			}
			return;
		}
		Entry entry = STATES.get(player.getUUID());
		ServerLevel level = player.level();
		if (entry == null && (player.tickCount % EXPOSURE_CHECK_TICKS != 0 || nearestKraken(level, player) == null)) {
			return;
		}
		if (entry == null) {
			entry = new Entry();
			STATES.put(player.getUUID(), entry);
		}
		if (AnchorBuoyBlock.isStandingOn(player)) {
			if (entry.state.level() > 0) {
				entry.state.reset();
			}
			entry.exposed = false;
		} else if (player.tickCount % EXPOSURE_CHECK_TICKS == 0) {
			entry.exposed = player.isUnderWater() && nearestKraken(level, player) != null;
		}
		KrakenEntity kraken = entry.exposed ? nearestKraken(level, player) : null;
		int phase = kraken == null ? 1 : kraken.phase();
		entry.state.tick(entry.exposed && kraken != null, ModEffects.hasTonic(player),
			OrichalcumEffects.pressureReduction(player), phase);
		apply(player, entry);

		int lvl = entry.state.level();
		if (lvl > 0 && player.isInWater()) {
			// Empuxo para baixo; o jogador é movido pelo cliente, então o servidor reenvia a velocidade.
			player.addDeltaMovement(new net.minecraft.world.phys.Vec3(0, -PressureState.sinkForce(lvl), 0));
			player.needsSync = true;
		}
		if (lvl >= PressureState.MAX_LEVEL) {
			if (++entry.damageTimer >= PressureState.DAMAGE_INTERVAL) {
				entry.damageTimer = 0;
				player.hurtServer(level, level.damageSources().magic(), PressureState.DAMAGE);
			}
		} else {
			entry.damageTimer = 0;
		}
		if (lvl == 0 && !entry.exposed && entry.lastSent == 0) {
			STATES.remove(player.getUUID());
		}
	}

	private static void apply(ServerPlayer player, Entry entry) {
		int lvl = entry.state.level();
		setModifier(player.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY), SLOW_ID, lvl >= 2, SLOW_SWIM,
			AttributeModifier.Operation.ADD_VALUE);
		setModifier(player.getAttribute(Attributes.ATTACK_SPEED), ATTACK_ID, lvl >= 3, SLOW_ATTACK,
			AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		if (entry.lastSent != lvl) {
			entry.lastSent = lvl;
			ServerPlayNetworking.send(player, new PressurePayload(lvl));
		}
	}

	private static void setModifier(AttributeInstance attribute, Identifier id, boolean on, double amount, AttributeModifier.Operation op) {
		if (attribute == null) {
			return;
		}
		if (on && !attribute.hasModifier(id)) {
			attribute.addTransientModifier(new AttributeModifier(id, amount, op));
		} else if (!on && attribute.hasModifier(id)) {
			attribute.removeModifier(id);
		}
	}

	private static KrakenEntity nearestKraken(ServerLevel level, ServerPlayer player) {
		List<KrakenEntity> krakens = level.getEntitiesOfClass(KrakenEntity.class, player.getBoundingBox().inflate(PressureState.RANGE));
		KrakenEntity best = null;
		double bestDist = PressureState.RANGE * PressureState.RANGE;
		for (KrakenEntity kraken : krakens) {
			double d = kraken.distanceToSqr(player);
			if (d <= bestDist) {
				bestDist = d;
				best = kraken;
			}
		}
		return best;
	}

	private PressureManager() {}
}
