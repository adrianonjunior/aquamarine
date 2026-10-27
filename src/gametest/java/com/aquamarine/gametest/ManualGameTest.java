package com.aquamarine.gametest;

import static com.aquamarine.gametest.IndustryWave3GameTest.check;
import static com.aquamarine.gametest.IndustryWave3GameTest.place;

import com.aquamarine.block.Waterlogging;
import com.aquamarine.client.manual.ManualScreen;
import com.aquamarine.energy.ModEnergy;
import com.aquamarine.energy.WaterGeneratorBlockEntity;
import com.aquamarine.entity.CrabEntity;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.farming.ModCultivation;
import com.aquamarine.farming.OysterEntity;
import com.aquamarine.guide.ModManual;
import com.aquamarine.industry.HydraulicBlockEntity;
import com.aquamarine.industry.ModHydraulics;
import com.aquamarine.industry.ModPneumatics;
import com.aquamarine.industry.PneumaticBlockEntity;
import com.aquamarine.network.ConduitBlockEntity;
import com.aquamarine.network.ModNetworks;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * Manual do Aquamarine (pedido de 27/10/2026). Parte {@code manualshots}: monta as cenas do passo a passo e tira os prints
 * que tools/gen_manual_shots.py recorta para textures/gui/manual/. Parte {@code manual}: o {@code /aquamarine help}
 * entrega e abre o livro, e a tela é fotografada na capa e em capítulos (para conferir o desenho).
 */
final class ManualGameTest {
	private static final long B = FluidConstants.BUCKET;

	private ManualGameTest() {}

	private static void facing(ServerLevel l, BlockPos pos, Block block, Direction front) {
		l.setBlockAndUpdate(pos, block.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, front));
	}

	private static void fill(ServerLevel l, BlockPos tank, Fluid fluid, long buckets) {
		try (Transaction tx = Transaction.openOuter()) {
			((HydraulicBlockEntity) l.getBlockEntity(tank)).inventory().insert(FluidVariant.of(fluid), buckets * B, tx);
			tx.commit();
		}
	}

	private static void box(ServerLevel l, BlockPos a, BlockPos b, Block block) {
		for (BlockPos p : BlockPos.betweenClosed(a, b)) {
			place(l, p, block);
		}
	}

	/** Terreno de uma cena: grama em volta, ar em cima. */
	private static void ground(ServerLevel l, BlockPos o) {
		box(l, o.offset(-8, -7, -8), o.offset(8, -2, 8), Blocks.STONE);
		box(l, o.offset(-8, -1, -8), o.offset(8, -1, 8), Blocks.GRASS_BLOCK);
		box(l, o.offset(-8, 0, -8), o.offset(8, 7, 8), Blocks.AIR);
	}

	/** Aquário com fundo de areia: água de {@code y0} a {@code y1} (relativos), paredes de prismarinho escuro. O mundo plano de
	 * teste começa logo abaixo da grama, por isso a água fica acima do chão. */
	private static void pool(ServerLevel l, BlockPos o, int x0, int z0, int x1, int z1, int y0, int y1) {
		box(l, o.offset(x0 - 1, y0 - 1, z0 - 1), o.offset(x1 + 1, y1, z1 + 1), Blocks.DARK_PRISMARINE);
		box(l, o.offset(x0, y0 - 1, z0), o.offset(x1, y0 - 1, z1), Blocks.SAND);
		box(l, o.offset(x0, y0, z0), o.offset(x1, y1, z1), Blocks.WATER);
	}

	static void shots(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos s = base.offset(-200, 0, 140);
		List<BlockPos> forced = new ArrayList<>();
		BlockPos wrench = s, vent = s.offset(18, 0, 0), pump = s.offset(36, 0, 0), tanks = s.offset(54, 0, 0);
		BlockPos fluid = s.offset(72, 0, 0), air = s.offset(90, 0, 0), sea = s.offset(108, 0, 0), beach = s.offset(126, 0, 0);
		server.runCommand(String.format("forceload add %d %d %d %d", s.getX() - 10, s.getZ() - 10, s.getX() + 136, s.getZ() + 10));
		server.runCommand("time set noon");
		server.runCommand("weather clear");
		int[] fov = new int[1];
		context.runOnClient(mc -> {
			if (!mc.gui.hud.isHidden()) {
				mc.gui.hud.toggle();
			}
			// Lente mais fechada: as máquinas enchem o quadro sem a câmera entrar nelas.
			fov[0] = mc.options.fov().get();
			mc.options.fov().set(40);
		});
		server.runOnServer(srv -> {
			ServerLevel l = srv.overworld();
			srv.getPlayerList().getPlayers().getFirst().setGameMode(GameType.SPECTATOR);
			for (BlockPos o : new BlockPos[] {wrench, vent, pump, tanks, fluid, air, sea, beach}) {
				ground(l, o);
			}
			// Chave: três canos num Tanque, com as marcas sai (padrão), entra e desligado na ponta de cada um.
			place(l, wrench, ModHydraulics.WATER_TANK);
			fill(l, wrench, Fluids.WATER, 40);
			for (Direction d : new Direction[] {Direction.WEST, Direction.NORTH, Direction.EAST}) {
				place(l, wrench.relative(d), ModNetworks.FLUID_PIPE);
				place(l, wrench.relative(d, 2), ModNetworks.FLUID_PIPE);
			}
			((ConduitBlockEntity) l.getBlockEntity(wrench.north())).cycle(Direction.SOUTH);
			((ConduitBlockEntity) l.getBlockEntity(wrench.east())).cycle(Direction.WEST);
			((ConduitBlockEntity) l.getBlockEntity(wrench.east())).cycle(Direction.WEST);
			// Fenda no modo mundo: magma no fundo de areia, água em cima, capim marinho em volta.
			pool(l, vent, -3, -3, 3, 3, 0, 3);
			place(l, vent, Blocks.MAGMA_BLOCK);
			facing(l, vent.above(), ModEnergy.VENT_GENERATOR, Direction.SOUTH);
			for (BlockPos p : new BlockPos[] {vent.offset(-2, 0, 1), vent.offset(2, 0, -1), vent.offset(1, 0, 2), vent.offset(-1, 0, -2)}) {
				place(l, p, Blocks.SEAGRASS);
			}
			// Lago atrás da Bomba, cercado de arenito; a Bomba fica na borda da frente, encostada na água.
			box(l, pump.offset(-4, -1, -5), pump.offset(1, 0, 0), Blocks.SMOOTH_SANDSTONE);
			box(l, pump.offset(-3, -1, -4), pump.offset(0, -1, -1), Blocks.SAND);
			box(l, pump.offset(-3, 0, -4), pump.offset(0, 0, -1), Blocks.WATER);
			place(l, pump.east().below(), Blocks.GRASS_BLOCK);
			place(l, pump.east(), Blocks.AIR);
			facing(l, pump, ModHydraulics.TIDE_PUMP, Direction.SOUTH);
			// Coluna de três Tanques com água e um de lava ao lado.
			for (int i = 0; i < 3; i++) {
				place(l, tanks.above(i), ModHydraulics.WATER_TANK);
			}
			fill(l, tanks, Fluids.WATER, 150);
			place(l, tanks.east(2), ModHydraulics.WATER_TANK);
			fill(l, tanks.east(2), Fluids.LAVA, 40);
			// Fenda no modo fluido em terra: cada Tanque em cima do seu Cano.
			facing(l, fluid, ModEnergy.VENT_GENERATOR, Direction.SOUTH);
			for (int dx : new int[] {-2, -1, 1, 2}) {
				place(l, fluid.offset(dx, 0, 0), ModNetworks.FLUID_PIPE);
			}
			place(l, fluid.offset(-2, 1, 0), ModHydraulics.WATER_TANK);
			place(l, fluid.offset(2, 1, 0), ModHydraulics.WATER_TANK);
			fill(l, fluid.offset(-2, 1, 0), Fluids.LAVA, 30);
			fill(l, fluid.offset(2, 1, 0), Fluids.WATER, 30);
			// Linha de ar, passo 1: Fenda -> Cabo -> Compressor -> Tubo de Ar -> Pulmão.
			facing(l, air.offset(-4, 0, 0), ModEnergy.VENT_GENERATOR, Direction.SOUTH);
			forced.add(air.offset(-4, 0, 0).immutable());
			place(l, air.offset(-3, 0, 0), ModNetworks.AQUATIC_CABLE);
			place(l, air.offset(-2, 0, 0), ModNetworks.AQUATIC_CABLE);
			facing(l, air.offset(-1, 0, 0), ModPneumatics.AIR_COMPRESSOR, Direction.SOUTH);
			place(l, air, ModNetworks.AIR_TUBE);
			place(l, air.east(), ModNetworks.AIR_TUBE);
			facing(l, air.east(2), ModPneumatics.IRON_LUNG, Direction.SOUTH);
			try (Transaction tx = Transaction.openOuter()) {
				((PneumaticBlockEntity) l.getBlockEntity(air.east(2))).fluidPort(null)
					.insert(FluidVariant.of(ModNetworks.COMPRESSED_AIR), 120 * B, tx);
				tx.commit();
			}
			// Fundo do mar: ostras de um lado, linha de cultivo do outro.
			pool(l, sea, -5, -3, 5, 5, 0, 3);
			for (BlockPos p : new BlockPos[] {sea.offset(-4, 0, -1), sea.offset(-3, 0, 1), sea.offset(-2, 0, -1)}) {
				OysterEntity oyster = ModCultivation.OYSTER.create(l, EntitySpawnReason.COMMAND);
				oyster.setPos(p.getX() + 0.5, p.getY(), p.getZ() + 0.5);
				oyster.setYRot(160 + p.getX() % 3 * 15);
				oyster.setPersistenceRequired();
				l.addFreshEntity(oyster);
			}
			place(l, sea.offset(-5, 0, 2), Blocks.SEAGRASS);
			place(l, sea.offset(-1, 0, -2), Blocks.SEAGRASS);
			place(l, sea.offset(5, 0, -2), Blocks.SEAGRASS);
			Block[] line = {ModCultivation.CULTIVATION_JAR, ModCultivation.CULTIVATION_ROPE, ModCultivation.CULTIVATION_JAR,
				ModCultivation.CULTIVATION_ROPE, ModCultivation.CULTIVATION_BUOY};
			for (int i = 0; i < line.length; i++) {
				l.setBlockAndUpdate(sea.offset(i, 0, 1), line[i].defaultBlockState().setValue(Waterlogging.WATERLOGGED, true));
			}
			// Praia: areia, mar ao fundo e um caranguejo parado de frente.
			box(l, beach.offset(-8, -1, -8), beach.offset(8, -1, 8), Blocks.SAND);
			box(l, beach.offset(-8, -1, -8), beach.offset(8, -1, -4), Blocks.WATER);
			box(l, beach.offset(-8, -2, -8), beach.offset(8, -2, -4), Blocks.SAND);
			CrabEntity crab = ModEntities.CRAB.create(l, EntitySpawnReason.COMMAND);
			crab.setPos(beach.getX() + 0.5, beach.getY(), beach.getZ() + 0.5);
			crab.setYRot(20);
			crab.setYHeadRot(20);
			crab.yBodyRot = 20;
			crab.setNoAi(true);
			crab.setPersistenceRequired();
			l.addFreshEntity(crab);
			forced.forEach(WaterGeneratorBlockEntity.FORCED::add);
		});
		context.waitTicks(40);
		IndustryWave3GameTest.shotAt(context, world, server, "manual_chave_lados",
			wrench.getX() - 0.3, wrench.getY() + 1.75, wrench.getZ() + 1.55, wrench.getX() - 0.05, wrench.getY() + 0.6, wrench.getZ() + 0.45);
		IndustryWave3GameTest.shotAt(context, world, server, "manual_energia_fenda",
			vent.getX() + 3.3, vent.getY() + 2.5, vent.getZ() + 3.4, vent.getX() + 0.5, vent.getY() + 1.2, vent.getZ() + 0.5);
		IndustryWave3GameTest.shotAt(context, world, server, "manual_bomba_1",
			pump.getX() + 2.0, pump.getY() + 2.1, pump.getZ() + 3.4, pump.getX() - 0.8, pump.getY() + 0.2, pump.getZ() - 1.0);
		server.runOnServer(srv -> {
			ServerLevel l = srv.overworld();
			// Passo 2: energia por cabo, de uma Fenda a 4 blocos.
			facing(l, pump.east(4), ModEnergy.VENT_GENERATOR, Direction.SOUTH);
			WaterGeneratorBlockEntity.FORCED.add(pump.east(4).immutable());
			forced.add(pump.east(4).immutable());
			for (int i = 1; i <= 3; i++) {
				place(l, pump.east(i), ModNetworks.AQUATIC_CABLE);
			}
		});
		context.waitTicks(50);
		IndustryWave3GameTest.shotAt(context, world, server, "manual_bomba_2",
			pump.getX() + 3.0, pump.getY() + 2.5, pump.getZ() + 4.6, pump.getX() + 1.8, pump.getY() + 0.3, pump.getZ() - 0.4);
		server.runOnServer(srv -> {
			ServerLevel l = srv.overworld();
			// Passo 3: Cano pela borda até um Tanque (canos e Tanque seguram a água como a borda).
			place(l, pump.west(), ModNetworks.FLUID_PIPE);
			place(l, pump.west(2), ModNetworks.FLUID_PIPE);
			place(l, pump.west(3), ModHydraulics.WATER_TANK);
			fill(l, pump.west(3), Fluids.WATER, 20);
			// Linha de ar, passo 2: saída de trás do Pulmão até uma Fenda regulada.
			place(l, air.offset(2, 0, -1), ModNetworks.AIR_TUBE);
			place(l, air.offset(2, 0, -2), ModNetworks.AIR_TUBE);
			facing(l, air.offset(2, 0, -3), ModEnergy.VENT_GENERATOR, Direction.EAST);
			WaterGeneratorBlockEntity.FORCED.add(air.offset(2, 0, -3).immutable());
			forced.add(air.offset(2, 0, -3).immutable());
		});
		context.waitTicks(60);
		IndustryWave3GameTest.shotAt(context, world, server, "manual_bomba_3",
			pump.getX() + 0.6, pump.getY() + 3.2, pump.getZ() + 5.0, pump.getX() - 1.0, pump.getY() + 0.3, pump.getZ() - 0.8);
		IndustryWave3GameTest.shotAt(context, world, server, "manual_tanque_coluna",
			tanks.getX() + 2.4, tanks.getY() + 2.3, tanks.getZ() + 5.8, tanks.getX() + 1.3, tanks.getY() + 1.5, tanks.getZ() + 0.5);
		IndustryWave3GameTest.shotAt(context, world, server, "manual_fenda_fluido",
			fluid.getX() + 0.5, fluid.getY() + 2.7, fluid.getZ() + 4.8, fluid.getX() + 0.5, fluid.getY() + 0.8, fluid.getZ() + 0.4);
		IndustryWave3GameTest.shotAt(context, world, server, "manual_ar_2",
			air.getX() + 1.8, air.getY() + 4.2, air.getZ() + 5.4, air.getX() + 0.3, air.getY() + 0.3, air.getZ() - 1.0);
		server.runOnServer(srv -> {
			// Para o print do passo 1, a saída do Pulmão sai de cena por um instante.
			ServerLevel l = srv.overworld();
			for (int i = 1; i <= 3; i++) {
				place(l, air.offset(2, 0, -i), Blocks.AIR);
			}
		});
		IndustryWave3GameTest.shotAt(context, world, server, "manual_ar_1",
			air.getX() - 0.5, air.getY() + 3.0, air.getZ() + 5.8, air.getX() - 0.5, air.getY() + 0.4, air.getZ() + 0.4);
		IndustryWave3GameTest.shotAt(context, world, server, "manual_ostra",
			sea.getX() - 1.4, sea.getY() + 1.9, sea.getZ() + 3.1, sea.getX() - 2.9, sea.getY() + 0.3, sea.getZ() - 0.1);
		IndustryWave3GameTest.shotAt(context, world, server, "manual_cultivo",
			sea.getX() + 2.5, sea.getY() + 2.3, sea.getZ() + 5.4, sea.getX() + 2.5, sea.getY() + 0.4, sea.getZ() + 1.5);
		IndustryWave3GameTest.shotAt(context, world, server, "manual_caranguejo",
			beach.getX() + 1.3, beach.getY() + 1.1, beach.getZ() + 3.0, beach.getX() + 0.5, beach.getY() + 0.25, beach.getZ() + 0.4);
		server.runOnServer(srv -> forced.forEach(WaterGeneratorBlockEntity.FORCED::remove));
		context.runOnClient(mc -> {
			if (mc.gui.hud.isHidden()) {
				mc.gui.hud.toggle();
			}
			mc.options.fov().set(fov[0]);
		});
	}

	/** {@code /aquamarine help} como jogador: o Manual entra no inventário e abre; fotos da capa e de capítulos. */
	static void book(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server) {
		server.runOnServer(srv -> srv.getPlayerList().getPlayers().getFirst().getInventory().clearContent());
		server.runCommand("execute as @p run aquamarine help");
		context.waitTicks(10);
		check(server.computeOnServer(srv -> srv.getPlayerList().getPlayers().getFirst().getInventory()
			.contains(stack -> stack.is(ModManual.MANUAL))), "/aquamarine help deveria entregar o Manual");
		check(context.computeOnClient(mc -> mc.gui.screen() instanceof ManualScreen), "/aquamarine help deveria abrir o Manual");
		context.runOnClient(mc -> ((ManualScreen) mc.gui.screen()).openChapter(0));
		context.waitTicks(5);
		context.takeScreenshot("livro_capa");
		int chapters = context.computeOnClient(mc -> ((ManualScreen) mc.gui.screen()).chapterCount());
		check(chapters == 7, "O Manual deveria ter 7 capítulos, tem " + chapters);
		for (int chapter : new int[] {1, 3, 5}) {
			context.runOnClient(mc -> ((ManualScreen) mc.gui.screen()).openChapter(chapter));
			context.waitTicks(5);
			context.takeScreenshot("livro_capitulo_" + chapter);
		}
		// Uma página com print (Bomba, passo 1) e o mesmo print ampliado.
		context.runOnClient(mc -> ((ManualScreen) mc.gui.screen()).showAfter(3, 1, false));
		context.waitTicks(5);
		context.takeScreenshot("livro_print");
		context.runOnClient(mc -> ((ManualScreen) mc.gui.screen()).showAfter(3, 1, true));
		context.waitTicks(5);
		context.takeScreenshot("livro_zoom");
		// Em português (o idioma do Adriano): a capa e um capítulo cabem nas páginas.
		context.runOnClient(mc -> {
			mc.getLanguageManager().setSelected("pt_br");
			mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
			mc.gui.setScreen(new ManualScreen());
			((ManualScreen) mc.gui.screen()).openChapter(0);
		});
		context.waitTicks(5);
		context.takeScreenshot("livro_pt_capa");
		context.runOnClient(mc -> ((ManualScreen) mc.gui.screen()).showAfter(1, 1, false));
		context.waitTicks(5);
		context.takeScreenshot("livro_pt_chave");
		context.runOnClient(mc -> {
			mc.getLanguageManager().setSelected("en_us");
			mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
		});
		// De novo com o livro no inventário: não duplica.
		context.setScreen(() -> null);
		server.runCommand("execute as @p run aquamarine help");
		context.waitTicks(10);
		int count = server.computeOnServer(srv -> srv.getPlayerList().getPlayers().getFirst().getInventory().countItem(ModManual.MANUAL));
		check(count == 1, "O segundo /aquamarine help duplicou o Manual: " + count);
		context.setScreen(() -> null);
	}
}
