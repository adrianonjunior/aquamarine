package com.aquamarine.gametest;

import com.aquamarine.energy.ModEnergy;
import com.aquamarine.energy.NeutroniumCollectorBlockEntity;
import com.aquamarine.energy.WaterGeneratorBlockEntity;
import com.aquamarine.network.ConduitBlock;
import com.aquamarine.network.ConduitBlockEntity;
import com.aquamarine.network.InlineConduitBlock;
import com.aquamarine.network.ModNetworks;
import com.aquamarine.network.PortMode;
import java.util.Locale;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

/**
 * 0.6.1, 7C (docs/12-industria.md): as redes. Água anda e se reparte num T até dois caldeirões, passando por Visor e
 * Válvula; lava anda no Cano Reforçado; Ar Comprimido enche o Tubo; a Fenda manda energia pelo Cabo até o Coletor. Confere
 * que nada se mistura, que Cano e Tubo não se ligam, a Chave de Maré (sai, entra, desligado), a Válvula fechada e o Cano de
 * água quebrado soltando a fonte; tira as capturas com conteúdo correndo.
 */
final class NetworkGameTest {
	private NetworkGameTest() {}

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(-90, 0, -60);
		int x = o.getX(), y = o.getY(), z = o.getZ();
		server.runCommand(String.format("forceload add %d %d %d %d", x - 6, z - 8, x + 14, z + 14));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:smooth_stone", x - 2, y - 1, z - 3, x + 11, y - 1, z + 12));
		server.runCommand(String.format("fill %d %d %d %d %d %d minecraft:air", x - 2, y, z - 3, x + 11, y + 4, z + 12));
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			// Água: Cano em x+1..x+4, Visor x+5, Válvula x+6, Cano x+7 e caldeirão em x+8; ramo do T em x+3 até outro caldeirão.
			for (int i = 1; i <= 4; i++) {
				place(level, new BlockPos(x + i, y, z), ModNetworks.FLUID_PIPE.defaultBlockState());
			}
			place(level, new BlockPos(x + 5, y, z), inline(ModNetworks.PIPE_WINDOW, Direction.Axis.X));
			place(level, new BlockPos(x + 6, y, z), inline(ModNetworks.PIPE_VALVE, Direction.Axis.X));
			place(level, new BlockPos(x + 7, y, z), ModNetworks.FLUID_PIPE.defaultBlockState());
			place(level, new BlockPos(x + 8, y, z), Blocks.CAULDRON.defaultBlockState());
			place(level, new BlockPos(x + 3, y, z + 1), ModNetworks.FLUID_PIPE.defaultBlockState());
			place(level, new BlockPos(x + 3, y, z + 2), Blocks.CAULDRON.defaultBlockState());
			// Tubo de Ar encostado no Cano: os dois não se ligam.
			place(level, new BlockPos(x + 2, y, z - 1), ModNetworks.AIR_TUBE.defaultBlockState());
			// Lava: Cano x+1..x+4, Cano Reforçado x+5, caldeirão x+6.
			for (int i = 1; i <= 4; i++) {
				place(level, new BlockPos(x + i, y, z + 4), ModNetworks.FLUID_PIPE.defaultBlockState());
			}
			place(level, new BlockPos(x + 5, y, z + 4), ModNetworks.REINFORCED_FLUID_PIPE.defaultBlockState());
			place(level, new BlockPos(x + 6, y, z + 4), Blocks.CAULDRON.defaultBlockState());
			// Ar: Tubo x+1..x+5 com uma subida, Visor x+6, Válvula x+7.
			for (int i = 1; i <= 5; i++) {
				place(level, new BlockPos(x + i, y, z + 7), ModNetworks.AIR_TUBE.defaultBlockState());
			}
			place(level, new BlockPos(x + 3, y + 1, z + 7), ModNetworks.AIR_TUBE.defaultBlockState());
			place(level, new BlockPos(x + 6, y, z + 7), inline(ModNetworks.AIR_TUBE_WINDOW, Direction.Axis.X));
			place(level, new BlockPos(x + 7, y, z + 7), inline(ModNetworks.AIR_TUBE_VALVE, Direction.Axis.X));
			// Energia: Fenda (produção forçada) em x, Cabo x+1..x+3, Visor x+4, Reforçado x+5, Interruptor x+6, Coletor x+7.
			BlockPos vent = new BlockPos(x, y, z + 10);
			place(level, vent, ModEnergy.VENT_GENERATOR.defaultBlockState());
			WaterGeneratorBlockEntity.FORCED.add(vent);
			for (int i = 1; i <= 3; i++) {
				place(level, new BlockPos(x + i, y, z + 10), ModNetworks.AQUATIC_CABLE.defaultBlockState());
			}
			place(level, new BlockPos(x + 4, y, z + 10), inline(ModNetworks.CABLE_WINDOW, Direction.Axis.X));
			place(level, new BlockPos(x + 5, y, z + 10), ModNetworks.REINFORCED_AQUATIC_CABLE.defaultBlockState());
			place(level, new BlockPos(x + 6, y, z + 10), inline(ModNetworks.CABLE_SWITCH, Direction.Axis.X));
			place(level, new BlockPos(x + 7, y, z + 10), ModEnergy.NEUTRONIUM_COLLECTOR.defaultBlockState());
		});
		look(server, x + 4.5, y + 3.2, z - 3.5, x + 4.5, y + 0.5, z + 2.0);
		context.waitTicks(5);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			BlockState pipe = level.getBlockState(new BlockPos(x + 2, y, z));
			check(pipe.getValue(ConduitBlock.SIDES.get(Direction.EAST)) == ConduitBlock.Link.PIPE, "Cano: devia ligar no vizinho");
			check(pipe.getValue(ConduitBlock.SIDES.get(Direction.NORTH)) == ConduitBlock.Link.NONE, "Cano e Tubo de Ar não deviam se ligar");
			check(level.getBlockState(new BlockPos(x + 7, y, z)).getValue(ConduitBlock.SIDES.get(Direction.EAST)) == ConduitBlock.Link.PORT,
				"Cano: o caldeirão devia virar conector");
			check(insert(level, new BlockPos(x + 1, y, z), FluidVariant.of(Fluids.WATER), 2 * FluidConstants.BUCKET) == 2 * FluidConstants.BUCKET,
				"Cano: devia aceitar 2 baldes de água");
			check(insert(level, new BlockPos(x + 1, y, z), FluidVariant.of(Fluids.LAVA), FluidConstants.BUCKET) == 0, "Cano: lava não devia misturar com água");
			check(insert(level, new BlockPos(x + 1, y, z), FluidVariant.of(ModNetworks.COMPRESSED_AIR), 1000) == 0, "Cano: não devia aceitar gás");
			check(insert(level, new BlockPos(x + 1, y, z + 7), FluidVariant.of(ModNetworks.COMPRESSED_AIR), FluidConstants.BUCKET) == FluidConstants.BUCKET,
				"Tubo de Ar: devia aceitar Ar Comprimido");
			check(insert(level, new BlockPos(x + 1, y, z + 7), FluidVariant.of(Fluids.WATER), 1000) == 0, "Tubo de Ar: não devia aceitar água");
			insert(level, new BlockPos(x + 1, y, z + 4), FluidVariant.of(Fluids.LAVA), 2 * FluidConstants.BUCKET);
		});
		feed(context, server, x, y, z, 50, false);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			check(level.getBlockState(new BlockPos(x + 8, y, z)).is(Blocks.WATER_CAULDRON), "Cano: a água devia chegar ao caldeirão do fim");
			check(level.getBlockState(new BlockPos(x + 3, y, z + 2)).is(Blocks.WATER_CAULDRON), "Cano: o ramo do T também devia receber");
			check(level.getBlockState(new BlockPos(x + 6, y, z + 4)).is(Blocks.LAVA_CAULDRON), "Cano: a lava devia chegar ao caldeirão");
			NeutroniumCollectorBlockEntity collector = (NeutroniumCollectorBlockEntity) level.getBlockEntity(new BlockPos(x + 7, y, z + 10));
			check(collector != null && collector.energy.amount > 0, "Cabo: a energia da Fenda devia chegar ao Coletor");
			// Chave de Maré no conector do caldeirão: sai, entra, desligado (sem ligação) e de volta a sai.
			ConduitBlockEntity end = (ConduitBlockEntity) level.getBlockEntity(new BlockPos(x + 7, y, z));
			check(end.cycle(Direction.EAST) == PortMode.IN, "Chave: sai devia virar entra");
			check(end.cycle(Direction.EAST) == PortMode.OFF, "Chave: entra devia virar desligado");
			check(level.getBlockState(new BlockPos(x + 7, y, z)).getValue(ConduitBlock.SIDES.get(Direction.EAST)) == ConduitBlock.Link.NONE,
				"Chave: desligado devia cortar o conector");
			check(end.cycle(Direction.EAST) == PortMode.OUT, "Chave: desligado devia voltar a sai");
			// Válvula fechada: o caldeirão do fim não recebe mais.
			BlockPos valve = new BlockPos(x + 6, y, z);
			level.setBlock(valve, level.getBlockState(valve).setValue(InlineConduitBlock.OPEN, false), Block.UPDATE_ALL);
			com.aquamarine.network.ConduitNetwork.invalidateAround(level, valve);
			level.setBlockAndUpdate(new BlockPos(x + 8, y, z), Blocks.CAULDRON.defaultBlockState());
		});
		// A água que já tinha passado da válvula sai uma vez; depois disso nada mais chega.
		feed(context, server, x, y, z, 30, false);
		server.runOnServer(s -> s.overworld().setBlockAndUpdate(new BlockPos(x + 8, y, z), Blocks.CAULDRON.defaultBlockState()));
		feed(context, server, x, y, z, 40, false);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			check(level.getBlockState(new BlockPos(x + 8, y, z)).is(Blocks.CAULDRON), "Válvula fechada: a água não devia passar");
			BlockPos valve = new BlockPos(x + 6, y, z);
			level.setBlock(valve, level.getBlockState(valve).setValue(InlineConduitBlock.OPEN, true), Block.UPDATE_ALL);
			com.aquamarine.network.ConduitNetwork.invalidateAround(level, valve);
		});
		// Capturas com o conteúdo correndo (o teste repõe a água e a lava e esvazia os caldeirões a cada tick).
		feed(context, server, x, y, z, 30, true);
		context.runOnClient(mc -> {
			if (!mc.gui.hud.isHidden()) {
				mc.gui.hud.toggle();
			}
		});
		server.runCommand("gamemode spectator @p");
		shot(context, world, server, x, y, z, "redes_00_visao", x + 4.5, y + 3.4, z - 3.2, x + 4.5, y + 0.3, z + 5.0);
		shot(context, world, server, x, y, z, "redes_01_agua", x + 4.6, y + 1.35, z - 1.6, x + 5.0, y + 0.5, z + 0.5);
		shot(context, world, server, x, y, z, "redes_02_t_caldeirao", x + 1.4, y + 1.6, z + 0.2, x + 3.5, y + 0.4, z + 1.5);
		shot(context, world, server, x, y, z, "redes_03_lava", x + 3.0, y + 1.3, z + 2.6, x + 4.5, y + 0.5, z + 4.5);
		shot(context, world, server, x, y, z, "redes_04_ar", x + 4.0, y + 1.6, z + 5.3, x + 5.0, y + 0.6, z + 7.5);
		shot(context, world, server, x, y, z, "redes_05_cabo", x + 3.5, y + 1.4, z + 8.4, x + 4.5, y + 0.5, z + 10.5);
		context.runOnClient(mc -> {
			if (mc.gui.hud.isHidden()) {
				mc.gui.hud.toggle();
			}
		});
		server.runCommand("gamemode creative @p");
		// Cano de água quebrado por um jogador (sobrevivência) solta uma fonte no lugar.
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			ServerPlayer player = s.getPlayerList().getPlayers().get(0);
			BlockPos pipe = new BlockPos(x + 2, y, z);
			insert(level, pipe, FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET);
			player.setGameMode(GameType.SURVIVAL);
			player.gameMode.destroyBlock(pipe);
			player.setGameMode(GameType.CREATIVE);
			check(level.getFluidState(pipe).is(Fluids.WATER), "Cano de água quebrado devia soltar uma fonte");
			WaterGeneratorBlockEntity.FORCED.remove(new BlockPos(x, y, z + 10));
		});
	}

	/** Espera {@code ticks} ticks repondo água, lava e ar; com {@code drain}, esvazia os caldeirões para a água seguir correndo. */
	private static void feed(ClientGameTestContext context, TestServerContext server, int x, int y, int z, int ticks, boolean drain) {
		for (int t = 0; t < ticks; t++) {
			server.runOnServer(s -> {
				ServerLevel level = s.overworld();
				insert(level, new BlockPos(x + 1, y, z), FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET / 10);
				insert(level, new BlockPos(x + 1, y, z + 4), FluidVariant.of(Fluids.LAVA), FluidConstants.BUCKET / 10);
				insert(level, new BlockPos(x + 1, y, z + 7), FluidVariant.of(ModNetworks.COMPRESSED_AIR), FluidConstants.BUCKET / 20);
				if (drain) {
					for (BlockPos p : new BlockPos[] {new BlockPos(x + 8, y, z), new BlockPos(x + 3, y, z + 2), new BlockPos(x + 6, y, z + 4)}) {
						if (!level.getBlockState(p).is(Blocks.CAULDRON)) {
							level.setBlockAndUpdate(p, Blocks.CAULDRON.defaultBlockState());
						}
					}
				}
			});
			context.waitTick();
		}
	}

	private static long insert(ServerLevel level, BlockPos pos, FluidVariant variant, long amount) {
		Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, Direction.UP);
		if (storage == null) {
			return 0;
		}
		try (Transaction tx = Transaction.openOuter()) {
			long moved = storage.insert(variant, amount, tx);
			tx.commit();
			return moved;
		}
	}

	private static void place(ServerLevel level, BlockPos pos, BlockState state) {
		level.setBlockAndUpdate(pos, state);
	}

	private static BlockState inline(Block block, Direction.Axis axis) {
		return block.defaultBlockState().setValue(InlineConduitBlock.AXIS, axis);
	}

	private static void shot(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, int x, int y, int z,
		String name, double ex, double ey, double ez, double tx, double ty, double tz) {
		look(server, ex, ey, ez, tx, ty, tz);
		world.getConnection().waitForChunksRender();
		feed(context, server, x, y, z, 14, true);
		context.runOnClient(mc -> mc.gui.hud.getChat().clearMessages(false));
		context.takeScreenshot(name);
	}

	private static void look(TestServerContext server, double ex, double ey, double ez, double tx, double ty, double tz) {
		double dx = tx - ex, dy = ty - ey, dz = tz - ez;
		float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
		float pitch = (float) Math.toDegrees(-Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
		server.runCommand(String.format(Locale.ROOT, "tp @p %.2f %.2f %.2f %.1f %.1f", ex, ey - 1.62, ez, yaw, pitch));
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
