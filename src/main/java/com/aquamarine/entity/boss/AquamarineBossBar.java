package com.aquamarine.entity.boss;

import java.util.Objects;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

/**
 * Barra de boss do mod (docs/02-kraken.md, "Barra de vida customizada"). O servidor usa um
 * {@link ServerBossEvent} comum para decidir quem vê a barra; quem tem o mod recebe também
 * {@link BossBarPayload} com escudo e fase, e o cliente desenha a barra própria no lugar da
 * vanilla. Uso: crie no boss, chame {@link #addPlayer}/{@link #removePlayer} em
 * startSeenByPlayer/stopSeenByPlayer e {@link #update} a cada tick.
 */
public class AquamarineBossBar {
	/** Reenvia o estado mesmo sem mudança, para cobrir pacotes perdidos e quem acabou de entrar. */
	private static final int RESEND_TICKS = 40;

	private final ServerBossEvent event;
	private BossBarPayload last;
	private int ticksSinceSend;

	public AquamarineBossBar(Component name) {
		this.event = new ServerBossEvent(java.util.UUID.randomUUID(), name, BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_6);
	}

	public static void registerPayloads() {
		PayloadTypeRegistry.clientboundPlay().register(BossBarPayload.TYPE, BossBarPayload.CODEC);
	}

	public ServerBossEvent event() {
		return event;
	}

	public void setName(Component name) {
		event.setName(name);
	}

	public void addPlayer(ServerPlayer player) {
		event.addPlayer(player);
		if (last != null) {
			send(player, last);
		}
	}

	public void removePlayer(ServerPlayer player) {
		event.removePlayer(player);
	}

	public void removeAllPlayers() {
		event.removeAllPlayers();
	}

	public void update(float health, float maxHealth, float shield, float maxShield, int phase, boolean shieldRegenerating) {
		event.setProgress(maxHealth > 0 ? health / maxHealth : 0);
		BossBarPayload payload = new BossBarPayload(event.getId(), health, maxHealth, shield, maxShield, phase, shieldRegenerating);
		ticksSinceSend++;
		if (!Objects.equals(payload, last) || ticksSinceSend >= RESEND_TICKS) {
			last = payload;
			ticksSinceSend = 0;
			for (ServerPlayer player : event.getPlayers()) {
				send(player, payload);
			}
		}
	}

	private static void send(ServerPlayer player, BossBarPayload payload) {
		if (ServerPlayNetworking.canSend(player, BossBarPayload.TYPE)) {
			ServerPlayNetworking.send(player, payload);
		}
	}
}
