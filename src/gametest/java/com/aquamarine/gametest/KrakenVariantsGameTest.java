package com.aquamarine.gametest;

import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.ModEntities;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

/**
 * Fase 6 (6A, escolha do Adriano em 26/10/2026): os três Krakens novos nascem pelo /summon com o desenho certo:
 * {@code aquamarine:kraken} (v3a, Lula-colosso), {@code kraken_vampire} (v3b) e {@code kraken_ammonite} (v3c).
 */
final class KrakenVariantsGameTest {
	private KrakenVariantsGameTest() {}

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(0, 0, -140);
		server.runCommand(String.format("forceload add %d %d %d %d", o.getX() - 48, o.getZ() - 64, o.getX() + 48, o.getZ() + 32));
		List<EntityType<KrakenEntity>> types = List.of(ModEntities.KRAKEN, ModEntities.KRAKEN_VAMPIRE, ModEntities.KRAKEN_AMMONITE);
		String[] names = {"kraken", "kraken_vampire", "kraken_ammonite"};
		for (int i = 0; i < names.length; i++) {
			EntityType<KrakenEntity> type = types.get(i);
			server.runCommand(String.format(Locale.ROOT, "summon aquamarine:%s %d %d %d {NoAI:1b,Rotation:[180f,0f]}", names[i],
				o.getX(), o.getY() + 2, o.getZ()));
			context.waitTicks(40);
			check(server.computeOnServer(s -> s.overworld().getEntities(EntityTypeTest.forClass(KrakenEntity.class),
				k -> k.getType() == type && k.isAlive()).size()) == 1, "/summon aquamarine:" + names[i] + " não criou o Kraken certo");
			tp(server, o.getX() + 0.5, o.getY() + 12.0, o.getZ() - 52.0, 0.0F, 6.0F);
			world.getConnection().waitForChunksRender();
			context.waitTicks(20);
			context.runOnClient(mc -> mc.gui.toastManager().clear());
			context.waitTick();
			context.takeScreenshot("kraken_v3_" + names[i]);
			server.runOnServer(s -> {
				s.overworld().getEntities(EntityTypeTest.forClass(KrakenEntity.class), k -> true).forEach(k -> k.discard());
				s.overworld().getEntitiesOfClass(net.minecraft.world.entity.Entity.class, new AABB(o).inflate(80),
					e -> e.getType() == ModEntities.TENTACLE).forEach(e -> e.discard());
			});
			context.waitTicks(5);
		}
	}

	private static void tp(TestServerContext server, double x, double y, double z, float yaw, float pitch) {
		server.runCommand(String.format(Locale.ROOT, "tp @p %.2f %.2f %.2f %.1f %.1f", x, y, z, yaw, pitch));
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
