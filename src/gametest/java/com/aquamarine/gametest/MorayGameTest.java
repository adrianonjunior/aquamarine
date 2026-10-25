package com.aquamarine.gametest;

import com.aquamarine.entity.ElectricDamage;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.entity.MorayEntity;
import com.aquamarine.entity.MorayPartEntity;
import com.aquamarine.item.ModItems;

import java.util.Locale;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

/**
 * Teste da faixa 3B-C (Moréia Tempestuosa): corpo segmentado, Descarga só em quem está na água e
 * isolada pelo Traje, choque que atordoa sem ferir, rabo ×1,5, Enrolar solto pelo rabo e as Escamas
 * pelo número de jogadores. Chamado por {@link AquamarineClientGameTest}; 2 screenshots.
 */
final class MorayGameTest {
	private static final int HALF = 14;
	private static final int HEIGHT = 16;

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(-300, 0, 0);
		int top = o.getY() + HEIGHT - 3;
		server.runCommand(String.format("forceload add %d %d %d %d", o.getX() - 24, o.getZ() - 24, o.getX() + 24, o.getZ() + 24));
		context.waitTicks(20);
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:glass hollow", o.getX() - HALF - 1, o.getY() - 1, o.getZ() - HALF - 1,
			o.getX() + HALF + 1, o.getY() + HEIGHT, o.getZ() + HALF + 1));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:water", o.getX() - HALF, o.getY(), o.getZ() - HALF,
			o.getX() + HALF, top, o.getZ() + HALF));
		// Pilar seco dentro do raio da Descarga: quem está em cima não leva o choque.
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:glass", o.getX() + 4, o.getY(), o.getZ(),
			o.getX() + 4, top + 1, o.getZ()));
		server.runCommand("gamemode survival @a");
		server.runCommand("effect give @a minecraft:water_breathing 600 0 true");
		server.runCommand("effect give @a minecraft:night_vision 600 0 true");

		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			MorayEntity moray = ModEntities.MORAY.create(level, EntitySpawnReason.COMMAND);
			moray.snapTo(o.getX() + 0.5, o.getY() + 6, o.getZ() + 6.5, 0.0F, 0.0F);
			moray.setAiFrozen(true);
			moray.setPersistenceRequired();
			level.addFreshEntity(moray);
		});
		context.waitTicks(10);
		check(server.computeOnServer(s -> moray(s).parts().size()) == MorayEntity.PART_OFFSETS.length, "Moréia: corpo sem as 7 partes");
		check(server.computeOnServer(s -> {
			MorayEntity m = moray(s);
			MorayPartEntity tail = m.parts().get(MorayEntity.TAIL_INDEX);
			return tail.position().distanceTo(m.position()) > 11.0;
		}), "Moréia: o rabo devia ficar ~12 blocos atrás da cabeça");

		// Foto 1: Moréia nadando vista de lado, de cima d'água rasa.
		server.runOnServer(s -> moray(s).playAnimation("swim"));
		tp(server, o.getX() + 11.5, o.getY() + 8, o.getZ() + 0.5, 90, 15);
		world.getConnection().waitForChunksRender();
		context.waitTicks(20);
		shot(context, "30_moreia_nadando");

		// Descarga: o jogador na água leva 8 e Lentidão.
		server.runOnServer(s -> {
			ServerPlayer p = player(s);
			p.setHealth(20.0F);
			p.removeEffect(MobEffects.SLOWNESS);
		});
		tp(server, o.getX() + 7.5, o.getY() + 7, o.getZ() + 0.5, 90, 10);
		context.waitTicks(5);
		server.runOnServer(s -> moray(s).start(s.overworld(), MorayEntity.Attack.CHAIN));
		context.waitTicks(20);
		shot(context, "31_moreia_descarga_carga");
		context.waitTicks(MorayEntity.CHAIN_WARN - 20 + 5);
		check(server.computeOnServer(s -> player(s).getHealth()) <= 12.5F, "Descarga: o jogador na água devia levar 8 de dano, vida "
			+ server.computeOnServer(s -> player(s).getHealth()));
		check(server.computeOnServer(s -> player(s).hasEffect(MobEffects.SLOWNESS)), "Descarga: devia dar Lentidão");

		// Fora da água (no pilar seco): nada.
		server.runOnServer(s -> player(s).setHealth(20.0F));
		tp(server, o.getX() + 4.5, top + 2, o.getZ() + 0.5, 90, 30);
		context.waitTicks(10);
		server.runOnServer(s -> moray(s).start(s.overworld(), MorayEntity.Attack.CHAIN));
		context.waitTicks(MorayEntity.CHAIN_WARN + 5);
		check(server.computeOnServer(s -> player(s).getHealth()) == 20.0F, "Descarga: fora da água não devia levar dano");

		// Traje completo na água: isolado (−40% por peça).
		server.runOnServer(s -> {
			ServerPlayer p = player(s);
			p.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.DEEP_DIVING_HELMET));
			p.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.DEEP_DIVING_CHESTPLATE));
			p.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.DEEP_DIVING_LEGGINGS));
			p.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.DEEP_DIVING_BOOTS));
			check(ElectricDamage.multiplier(p) == 0.0F, "Isolamento: o Traje completo devia anular o choque");
		});
		tp(server, o.getX() + 7.5, o.getY() + 7, o.getZ() + 0.5, 90, 10);
		context.waitTicks(5);
		server.runOnServer(s -> moray(s).start(s.overworld(), MorayEntity.Attack.CHAIN));
		context.waitTicks(MorayEntity.CHAIN_WARN + 5);
		check(server.computeOnServer(s -> player(s).getHealth()) == 20.0F, "Descarga: com o Traje completo não devia levar dano");
		server.runOnServer(s -> {
			for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
				player(s).setItemSlot(slot, ItemStack.EMPTY);
			}
		});

		// Choque: imune ao dano, mas fica atordoada.
		server.runOnServer(s -> {
			MorayEntity m = moray(s);
			float before = m.getHealth();
			m.hurtServer(s.overworld(), ElectricDamage.source(s.overworld(), player(s), player(s)), 20.0F);
			check(m.getHealth() == before, "Choque: a Moréia é imune a dano elétrico");
			check(m.isStunned(), "Choque: a Moréia devia ficar atordoada");
		});
		context.waitTicks(MorayEntity.STUN_TICKS + 5);
		check(!server.computeOnServer(s -> moray(s).isStunned()), "Choque: o atordoamento dura 3 s");

		// Rabo: dano ×1,5.
		server.runOnServer(s -> {
			MorayEntity m = moray(s);
			m.setInvulnerableTime(0);
			float before = m.getHealth();
			MorayPartEntity tail = m.parts().get(MorayEntity.TAIL_INDEX);
			tail.hurtServer(s.overworld(), s.overworld().damageSources().playerAttack(player(s)), 10.0F);
			check(Math.abs(before - m.getHealth() - 15.0F) < 0.01F, "Rabo: 10 de dano devia virar 15, foi " + (before - m.getHealth()));
		});

		// Enrolar: prende quem está perto; 30 de dano no rabo solta.
		tp(server, o.getX() + 0.5, o.getY() + 6, o.getZ() + 3.5, 0, 20);
		context.waitTicks(5);
		server.runOnServer(s -> {
			MorayEntity m = moray(s);
			m.setTarget(player(s));
			m.start(s.overworld(), MorayEntity.Attack.COIL);
		});
		context.waitTicks(MorayEntity.COIL_WARN + 5);
		check(server.computeOnServer(s -> moray(s).coiledTarget() == player(s)), "Enrolar: devia prender o jogador");
		server.runOnServer(s -> {
			MorayEntity m = moray(s);
			m.setInvulnerableTime(0);
			m.parts().get(MorayEntity.TAIL_INDEX).hurtServer(s.overworld(), s.overworld().damageSources().playerAttack(player(s)), 20.0F);
			check(m.coiledTarget() == null, "Enrolar: 30 de dano no rabo devia soltar o jogador");
		});

		// Drops: 1 jogador = 2 Escamas Elétricas (2 + n ÷ 2) e XP.
		check(MorayEntity.scaleDrops(1) == 2 && MorayEntity.scaleDrops(4) == 4 && MorayEntity.scaleDrops(12) == 6, "Drops: conta 2 + n ÷ 2 (máx. 6)");
		server.runOnServer(s -> {
			MorayEntity m = moray(s);
			m.setInvulnerableTime(0);
			m.hurtServer(s.overworld(), s.overworld().damageSources().playerAttack(player(s)), 9999.0F);
		});
		context.waitTicks(30);
		int scales = server.computeOnServer(s -> s.overworld().getEntitiesOfClass(ItemEntity.class,
			new AABB(o).inflate(HALF + 2, HEIGHT, HALF + 2), e -> e.getItem().is(ModItems.ELECTRIC_SCALE))
			.stream().mapToInt(e -> e.getItem().getCount()).sum());
		check(scales == 2, "Drops: 1 jogador devia render 2 Escamas, rendeu " + scales);
		check(server.computeOnServer(s -> s.overworld().getEntities(ModEntities.MORAY_PART, e -> true).isEmpty()),
			"Moréia: as partes do corpo deviam sumir com ela");

		server.runCommand("kill @e[type=item]");
		server.runCommand("effect clear @a");
		server.runOnServer(s -> player(s).setHealth(20.0F));
		server.runCommand("gamemode creative @a");
		tp(server, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, 0, 0);
		context.waitTicks(5);
	}

	private static MorayEntity moray(MinecraftServer server) {
		return server.overworld().getEntities(ModEntities.MORAY, e -> true).stream().findFirst()
			.orElseThrow(() -> new AssertionError("Moréia: sumiu do mundo"));
	}

	private static ServerPlayer player(MinecraftServer server) {
		return server.getPlayerList().getPlayers().getFirst();
	}

	private static void shot(ClientGameTestContext context, String name) {
		context.runOnClient(mc -> mc.gui.toastManager().clear());
		context.waitTick();
		context.takeScreenshot(name);
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private static void tp(TestServerContext server, double x, double y, double z, float yaw, float pitch) {
		server.runCommand(String.format(Locale.ROOT, "tp @p %.2f %.2f %.2f %.1f %.1f", x, y, z, yaw, pitch));
	}

	private MorayGameTest() {}
}
