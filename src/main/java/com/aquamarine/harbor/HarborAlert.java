package com.aquamarine.harbor;

import com.aquamarine.config.AquamarineConfig;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.entity.ModVillagers;
import com.aquamarine.entity.MorayEntity;
import com.aquamarine.event.FalseWarning;
import com.aquamarine.item.ModItems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/**
 * Evento "Alerta de Moréia" do Porto dos Náufragos (docs/08-fase3.md, seção 8): em noite de tempestade, com
 * jogador no Porto e no máximo uma vez a cada 3 dias de jogo, o Sino toca e uma Moréia ataca a Cúpula. Quem
 * a ferir e estiver vivo quando ela morrer ganha reputação e 2 a 4 Escamas Elétricas, além do saque normal.
 * Se ninguém a vencer em 5 minutos, ela vai embora; falhar não destrói nada. Desliga em
 * {@code config/aquamarine.json} ({@code harborAlert}). A conta do gatilho é pura ({@link #decide}).
 */
public final class HarborAlert {
	public static final int MIN_DAYS_BETWEEN = 3;
	public static final long NEVER = -1000;
	public static final int REPUTATION = 50;
	public static final int MIN_SCALES = 2;
	public static final int MAX_SCALES = 4;
	/** 5 minutos para vencer a Moréia. */
	public static final int DURATION = 6000;
	/** Marca da Moréia do Alerta (fica salva na entidade). */
	public static final String TAG = "aquamarine_harbor_alert";
	private static final int CHECK_INTERVAL = 100;
	private static final int ANNOUNCE_RADIUS = 96;

	private static final Map<UUID, Alert> ACTIVE = new HashMap<>();

	private record Alert(long endTick, Set<UUID> defenders) {}

	public static boolean decide(boolean enabled, boolean stormyNight, boolean playerAtHarbor, long day, long lastAlertDay) {
		return enabled && stormyNight && playerAtHarbor && day - lastAlertDay >= MIN_DAYS_BETWEEN;
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(HarborAlert::tick);
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
			Alert alert = ACTIVE.get(entity.getUUID());
			if (alert != null && source.getEntity() instanceof ServerPlayer player) {
				alert.defenders().add(player.getUUID());
			}
		});
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			Alert alert = ACTIVE.remove(entity.getUUID());
			if (alert != null && entity.level() instanceof ServerLevel level) {
				// O golpe fatal não passa pelo AFTER_DAMAGE: quem mata também defendeu.
				if (source.getEntity() instanceof ServerPlayer player) {
					alert.defenders().add(player.getUUID());
				}
				reward(level, alert);
			}
		});
	}

	private static void tick(MinecraftServer server) {
		ServerLevel level = server.overworld();
		if (!ACTIVE.isEmpty()) {
			expire(level);
		}
		if (server.getTickCount() % CHECK_INTERVAL == 0) {
			check(level);
		}
	}

	/** Uma checagem: se der, começa o Alerta no Porto do primeiro jogador que estiver em um. */
	public static boolean check(ServerLevel level) {
		AquamarineConfig config = AquamarineConfig.get();
		boolean stormy = level.isThundering() && level.isDarkOutside();
		if (!config.harborAlert || !stormy) {
			return false;
		}
		ServerPlayer target = level.players().stream()
			.filter(p -> !p.isSpectator() && p.isAlive() && ModVillagers.isInHarbor(level, p.blockPosition()))
			.findFirst().orElse(null);
		HarborReputation data = HarborReputation.get(level.getServer());
		long day = FalseWarning.dayIndex(level.getOverworldClockTime());
		if (!decide(true, true, target != null, day, data.lastAlertDay())) {
			return false;
		}
		BlockPos bell = bellOf(level, target.blockPosition());
		if (bell == null) {
			return false;
		}
		data.setLastAlertDay(day);
		return start(level, bell) != null;
	}

	/** O Sino do Porto onde está {@code pos} (centro da peça principal da estrutura). */
	public static @Nullable BlockPos bellOf(ServerLevel level, BlockPos pos) {
		StructureStart start = level.structureManager().getStructureWithPieceAt(pos, ModVillagers.HARBORS);
		if (!start.isValid() || start.getPieces().isEmpty()) {
			return null;
		}
		BoundingBox box = start.getPieces().getFirst().getBoundingBox();
		return findBell(level, new BlockPos(box.getCenter().getX(), box.minY(), box.getCenter().getZ()), 4);
	}

	/** Procura um sino num raio em volta de {@code center}, da altura dele até 5 acima. */
	public static @Nullable BlockPos findBell(ServerLevel level, BlockPos center, int radius) {
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, 0, -radius), center.offset(radius, 5, radius))) {
			if (level.getBlockState(pos).is(Blocks.BELL)) {
				return pos.immutable();
			}
		}
		return null;
	}

	/** Toca o Sino e solta a Moréia na água em volta da Cúpula. Devolve a Moréia, ou null se não achou água. */
	public static @Nullable MorayEntity start(ServerLevel level, BlockPos bell) {
		BlockPos spawn = waterAround(level, bell);
		MorayEntity moray = spawn == null ? null : ModEntities.MORAY.create(level, EntitySpawnReason.EVENT);
		if (moray == null) {
			return null;
		}
		if (level.getBlockState(bell).getBlock() instanceof BellBlock block) {
			block.attemptToRing(level, bell, null);
		}
		float yaw = (float) (Mth.atan2(bell.getZ() - spawn.getZ(), bell.getX() - spawn.getX()) * Mth.RAD_TO_DEG) - 90.0F;
		moray.snapTo(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, yaw, 0.0F);
		moray.addTag(TAG);
		moray.setPersistenceRequired();
		level.addFreshEntity(moray);
		ACTIVE.put(moray.getUUID(), new Alert(level.getGameTime() + DURATION, new HashSet<>()));
		ServerPlayer nearest = level.getNearestPlayer(moray, MorayEntity.SCALING_RADIUS) instanceof ServerPlayer p && !p.isCreative()
			&& !p.isSpectator() ? p : null;
		if (nearest != null) {
			moray.setTarget(nearest);
		}
		announce(level, bell, Component.translatable("message.aquamarine.harbor.alert"));
		return moray;
	}

	/** Água aberta (3 x 3 x 3) num anel fora da Cúpula, de 18 a 30 blocos do Sino. */
	private static @Nullable BlockPos waterAround(ServerLevel level, BlockPos bell) {
		for (int radius = 18; radius <= 30; radius += 3) {
			for (int angle = 0; angle < 360; angle += 30) {
				double a = Math.toRadians(angle);
				for (int dy = 3; dy <= 9; dy += 3) {
					BlockPos pos = bell.offset((int) Math.round(radius * Math.cos(a)), dy, (int) Math.round(radius * Math.sin(a)));
					if (openWater(level, pos)) {
						return pos;
					}
				}
			}
		}
		return null;
	}

	private static boolean openWater(ServerLevel level, BlockPos center) {
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-1, -1, -1), center.offset(1, 1, 1))) {
			if (!level.getFluidState(pos).is(FluidTags.WATER)) {
				return false;
			}
		}
		return true;
	}

	private static void reward(ServerLevel level, Alert alert) {
		HarborReputation reputation = HarborReputation.get(level.getServer());
		for (UUID id : alert.defenders()) {
			ServerPlayer player = level.getServer().getPlayerList().getPlayer(id);
			if (player == null || !player.isAlive()) {
				continue;
			}
			reputation.add(id, REPUTATION);
			int count = MIN_SCALES + level.getRandom().nextInt(MAX_SCALES - MIN_SCALES + 1);
			ItemStack scales = new ItemStack(ModItems.ELECTRIC_SCALE, count);
			player.getInventory().placeItemBackInInventory(scales, Prediction.SERVER_ONLY);
			player.sendSystemMessage(Component.translatable("message.aquamarine.harbor.alert_won", count, REPUTATION));
		}
	}

	/** Fim do tempo: a Moréia vai embora e o Alerta termina sem castigo. */
	private static void expire(ServerLevel level) {
		long now = level.getGameTime();
		Iterator<Map.Entry<UUID, Alert>> it = ACTIVE.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Alert> entry = it.next();
			if (now < entry.getValue().endTick()) {
				continue;
			}
			it.remove();
			Entity moray = level.getEntity(entry.getKey());
			if (moray != null && moray.isAlive()) {
				announce(level, moray.blockPosition(), Component.translatable("message.aquamarine.harbor.alert_lost"));
				moray.discard();
			}
		}
	}

	private static void announce(ServerLevel level, BlockPos pos, Component message) {
		for (ServerPlayer player : level.players()) {
			if (player.blockPosition().closerThan(pos, ANNOUNCE_RADIUS)) {
				player.sendSystemMessage(message);
			}
		}
	}

	/** Alertas em andamento (para testes e para o comando). */
	public static int activeCount() {
		return ACTIVE.size();
	}

	private HarborAlert() {}
}
