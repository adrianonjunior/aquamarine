package com.aquamarine.gametest;

import static com.aquamarine.gametest.IndustryWave3GameTest.check;
import static com.aquamarine.gametest.IndustryWave3GameTest.place;
import static com.aquamarine.gametest.IndustryWave3GameTest.reload;

import com.aquamarine.energy.ModEnergy;
import com.aquamarine.energy.NeutroniumCollectorBlockEntity;
import com.aquamarine.energy.WaterEnergy;
import com.aquamarine.energy.WaterGeneratorBlockEntity;
import com.aquamarine.industry.HydraulicBlockEntity;
import com.aquamarine.industry.ModHydraulics;
import com.aquamarine.industry.ModPneumatics;
import com.aquamarine.industry.PneumaticBlockEntity;
import com.aquamarine.machine.MachineState;
import com.aquamarine.network.ConduitKind;
import com.aquamarine.network.ConduitNetwork;
import com.aquamarine.network.ModNetworks;
import com.aquamarine.network.PortMode;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.material.Fluids;

/**
 * Onda 4 da 0.6.1 (7E e 7F; docs/12-industria.md): a Fenda gera em terra firme com água e lava vindas de Tanques pelo
 * Cano, o Coletor segue recusando a terra, o Compressor enche o Pulmão pelo Tubo de Ar e o Pulmão regula a Fenda (+25%),
 * esvaziando; os lados seguem as regras do Cano e do Tubo; tudo volta igual do save; o Pulmão cheio só alivia.
 */
final class IndustryWave4GameTest {
	private static final long B = FluidConstants.BUCKET;

	private IndustryWave4GameTest() {}

	private static WaterGeneratorBlockEntity generator(ServerLevel level, BlockPos pos) {
		return (WaterGeneratorBlockEntity) level.getBlockEntity(pos);
	}

	private static PneumaticBlockEntity pneumatic(ServerLevel level, BlockPos pos) {
		return (PneumaticBlockEntity) level.getBlockEntity(pos);
	}

	private static void facing(ServerLevel level, BlockPos pos, Block block, Direction front) {
		level.setBlockAndUpdate(pos, block.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, front));
	}

	private static void fill(ServerLevel level, BlockPos tank, FluidVariant variant, long amount) {
		try (Transaction tx = Transaction.openOuter()) {
			check(((HydraulicBlockEntity) level.getBlockEntity(tank)).inventory().insert(variant, amount, tx) == amount,
				"Tanque não aceitou " + variant);
			tx.commit();
		}
	}

	private static long insertAir(PneumaticBlockEntity lung, long amount) {
		try (Transaction tx = Transaction.openOuter()) {
			long moved = lung.fluidPort(null).insert(FluidVariant.of(ModNetworks.COMPRESSED_AIR), amount, tx);
			tx.commit();
			return moved;
		}
	}

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(-130, 0, 40);
		BlockPos vent = o.offset(4, 0, 4), waterTank = o.offset(2, 1, 4), lavaTank = o.offset(6, 1, 4), collector = o.offset(10, 0, 4);
		BlockPos compressor = o.offset(4, 0, 10), lung = o.offset(7, 0, 10), regulated = o.offset(7, 0, 13), plain = o.offset(10, 0, 13);
		BlockPos fullLung = o.offset(12, 0, 10);
		long[] lungBefore = new long[1];
		server.runCommand(String.format("forceload add %d %d %d %d", o.getX() - 4, o.getZ() - 4, o.getX() + 18, o.getZ() + 18));
		server.runOnServer(s -> {
			ServerLevel l = s.overworld();
			s.getPlayerList().getPlayers().getFirst().setGameMode(GameType.SPECTATOR);
			for (BlockPos p : BlockPos.betweenClosed(o.offset(-4, -1, -4), o.offset(18, -1, 18))) {
				place(l, p, Blocks.SMOOTH_STONE);
			}
			for (BlockPos p : BlockPos.betweenClosed(o.offset(-4, 0, -4), o.offset(18, 5, 18))) {
				place(l, p, Blocks.AIR);
			}
			// 7E: Fenda em terra firme (pedra embaixo, ar em cima) com um Tanque de água e um de lava, cada um pelo seu Cano.
			facing(l, vent, ModEnergy.VENT_GENERATOR, Direction.NORTH);
			check(WaterGeneratorBlockEntity.rate(l, vent, generator(l, vent).kind()) == 0, "A Fenda em terra não pode ter o modo mundo");
			for (BlockPos p : new BlockPos[] {vent.west(), vent.west(2), vent.east(), vent.east(2)}) {
				place(l, p, ModNetworks.FLUID_PIPE);
			}
			place(l, waterTank, ModHydraulics.WATER_TANK);
			place(l, lavaTank, ModHydraulics.WATER_TANK);
			fill(l, waterTank, FluidVariant.of(Fluids.WATER), 8 * B);
			fill(l, lavaTank, FluidVariant.of(Fluids.LAVA), 8 * B);
			check(ConduitNetwork.isPort(l, vent, Direction.WEST, ConduitKind.FLUID), "O Cano deve ligar no lado da Fenda");
			check(!ConduitNetwork.isPort(l, vent, Direction.NORTH, ConduitKind.FLUID), "O Cano não liga na frente da Fenda");
			check(ConduitNetwork.isPort(l, vent, Direction.SOUTH, ConduitKind.GAS), "O Tubo de Ar deve ligar atrás da Fenda");
			check(!ConduitNetwork.isPort(l, vent, Direction.NORTH, ConduitKind.GAS), "O Tubo de Ar não liga na frente da Fenda");
			// Coletor ao lado do mesmo Tanque: segue recusando a terra e não tem entrada de fluido.
			place(l, collector, ModEnergy.NEUTRONIUM_COLLECTOR);
			place(l, collector.west(), ModHydraulics.WATER_TANK);
			fill(l, collector.west(), FluidVariant.of(Fluids.WATER), 8 * B);
			((NeutroniumCollectorBlockEntity) l.getBlockEntity(collector)).energy.amount = WaterEnergy.COLLECTOR_CAPACITY;
			check(!NeutroniumCollectorBlockEntity.validSpot(l, collector), "O Coletor não pode aceitar terra firme");
			check(FluidStorage.SIDED.find(l, collector, Direction.WEST) == null, "O Coletor não recebe fluido");
			// 7F: Compressor -> Tubo de Ar -> Pulmão (o lado de trás do Pulmão fica livre: ele só enche).
			facing(l, compressor, ModPneumatics.AIR_COMPRESSOR, Direction.NORTH);
			pneumatic(l, compressor).energy.amount = 16_000;
			place(l, compressor.east(), ModNetworks.AIR_TUBE);
			place(l, compressor.east(2), ModNetworks.AIR_TUBE);
			facing(l, lung, ModPneumatics.IRON_LUNG, Direction.NORTH);
			check(!ConduitNetwork.isPort(l, compressor, Direction.EAST, ConduitKind.FLUID), "O Cano não liga no Compressor");
			check(ConduitNetwork.isPort(l, compressor, Direction.EAST, ConduitKind.GAS), "O Tubo de Ar liga no Compressor");
			check(pneumatic(l, lung).port(Direction.SOUTH) == PortMode.OUT
				&& pneumatic(l, lung).port(Direction.WEST) == PortMode.IN, "Pulmão: entra pelos lados, sai por trás");
		});
		context.waitTicks(80);
		server.runOnServer(s -> {
			ServerLevel l = s.overworld();
			WaterGeneratorBlockEntity v = generator(l, vent);
			check(v.lastRate() == 32 && v.energy.amount > 0, "Fenda em terra com Tanques deveria gerar 32 E/t, gerou " + v.lastRate());
			check(v.water() > 0 && v.lava() > 0 && !v.regulated(), "Fenda: tanques de água e lava deveriam ter recebido pelo Cano");
			NeutroniumCollectorBlockEntity c = (NeutroniumCollectorBlockEntity) l.getBlockEntity(collector);
			check(c.progress() == 0 && c.dust() == 0, "O Coletor trabalhou em terra firme");
			PneumaticBlockEntity comp = pneumatic(l, compressor), lu = pneumatic(l, lung);
			check(comp.energy.amount < 16_000, "O Compressor não gastou energia");
			check(lu.amount() > 0, "O Pulmão não encheu pelo Tubo de Ar");
			// Para o Compressor, põe 10 baldes no Pulmão e liga a saída de trás numa Fenda regulada (Tubo de Ar).
			comp.energy.amount = 0;
			check(insertAir(lu, 10 * B) == 10 * B, "Pulmão deveria aceitar 10 baldes");
			lungBefore[0] = lu.amount();
			place(l, lung.south(), ModNetworks.AIR_TUBE);
			place(l, lung.south(2), ModNetworks.AIR_TUBE);
			facing(l, regulated, ModEnergy.VENT_GENERATOR, Direction.EAST);
			facing(l, plain, ModEnergy.VENT_GENERATOR, Direction.EAST);
			WaterGeneratorBlockEntity.FORCED.add(regulated.immutable());
			WaterGeneratorBlockEntity.FORCED.add(plain.immutable());
			// Pulmão cheio: aceita só a capacidade e nunca some nem quebra nada.
			facing(l, fullLung, ModPneumatics.IRON_LUNG, Direction.NORTH);
			check(insertAir(pneumatic(l, fullLung), 300 * B) == PneumaticBlockEntity.LUNG_CAPACITY, "Pulmão: capacidade de 256 baldes");
		});
		context.waitTicks(60);
		long[] airBefore = new long[1];
		server.runOnServer(s -> {
			ServerLevel l = s.overworld();
			WaterGeneratorBlockEntity r = generator(l, regulated), p = generator(l, plain);
			check(r.regulated() && r.lastRate() == 40, "A Fenda com ar do Pulmão deveria render 40 E/t, rendeu " + r.lastRate());
			check(!p.regulated() && p.lastRate() == 32, "A Fenda sem ar segue em 32 E/t");
			check(pneumatic(l, lung).amount() < lungBefore[0], "O Pulmão não esvaziou pelo Tubo de Ar");
			check(pneumatic(l, fullLung).machineState() == MachineState.FULL && l.getBlockState(fullLung).is(ModPneumatics.IRON_LUNG),
				"Pulmão cheio deveria ficar em alívio, inteiro");
			// Corta o Tubo: a Fenda segue regulada gastando o ar que guardou.
			place(l, lung.south(2), Blocks.AIR);
			r.energy.amount = 0;
			airBefore[0] = r.air();
		});
		context.waitTicks(20);
		server.runOnServer(s -> {
			ServerLevel l = s.overworld();
			WaterGeneratorBlockEntity r = generator(l, regulated);
			check(r.air() < airBefore[0] && r.regulated(), "A Fenda regulada deveria gastar o ar guardado");
			// Persistência: tanques da Fenda, ar do Pulmão e lados voltam iguais.
			WaterGeneratorBlockEntity v = generator(l, vent);
			long water = v.water(), lava = v.lava();
			reload(l, vent);
			check(generator(l, vent).water() == water && generator(l, vent).lava() == lava, "Save perdeu os tanques da Fenda");
			long air = pneumatic(l, fullLung).amount();
			pneumatic(l, fullLung).cyclePort(Direction.WEST);
			reload(l, fullLung);
			check(pneumatic(l, fullLung).amount() == air, "Save perdeu o ar do Pulmão");
			check(pneumatic(l, fullLung).port(Direction.WEST) == PortMode.OFF
				&& pneumatic(l, fullLung).fluidPort(Direction.WEST) == null, "Lado desligado do Pulmão deveria sumir e salvar");
			s.getPlayerList().getPlayers().getFirst().setGameMode(GameType.SPECTATOR);
			// Fenda de vitrine ligada a tudo para as capturas: volta a ter ar e o Compressor volta a girar.
			pneumatic(l, compressor).energy.amount = 16_000;
		});
		IndustryWave3GameTest.shotAt(context, world, server, "onda4_fenda_terra",
			vent.getX() + 0.5, vent.getY() + 2.6, vent.getZ() - 3.2, vent.getX() + 0.5, vent.getY() + 0.5, vent.getZ() + 0.5);
		IndustryWave3GameTest.shotAt(context, world, server, "onda4_compressor",
			compressor.getX() - 1.3, compressor.getY() + 1.7, compressor.getZ() - 1.6, compressor.getX() + 0.5, compressor.getY() + 0.5,
			compressor.getZ() + 0.5);
		IndustryWave3GameTest.shotAt(context, world, server, "onda4_pulmao_cheio",
			fullLung.getX() + 2.1, fullLung.getY() + 1.6, fullLung.getZ() - 1.4, fullLung.getX() + 0.5, fullLung.getY() + 0.5,
			fullLung.getZ() + 0.5);
		IndustryWave3GameTest.shotAt(context, world, server, "onda4_linha_de_ar",
			lung.getX() - 3.5, lung.getY() + 4.5, lung.getZ() - 2.5, lung.getX() + 0.5, lung.getY(), lung.getZ() + 1.5);
		server.runOnServer(s -> {
			WaterGeneratorBlockEntity.FORCED.remove(regulated);
			WaterGeneratorBlockEntity.FORCED.remove(plain);
		});
	}
}
