package com.aquamarine.gametest;

import com.aquamarine.Aquamarine;
import com.aquamarine.block.Waterlogging;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 0.6.1, 7A (docs/12-industria.md): todo bloco do mod menor que o cubo, colocado numa fonte de água, nasce com a água
 * dentro; a água corre para o vizinho que se abre e quebrar o bloco devolve a fonte. Cada bloco fica numa bacia de
 * vidro de duas casas: a do bloco (a fonte) e a do lado, fechada até o bloco estar no lugar.
 */
final class WaterlogGameTest {
	private static final int PER_ROW = 10;

	private WaterlogGameTest() {}

	static void run(ClientGameTestContext context, TestSingleplayerContext world, TestServerContext server, BlockPos base) {
		BlockPos o = base.offset(-150, 0, 150);
		List<Block> blocks = server.computeOnServer(s -> BuiltInRegistries.BLOCK.stream()
			.filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(Aquamarine.MOD_ID))
			.filter(b -> b.asItem() != Items.AIR)
			.filter(b -> !Block.isShapeFullBlock(b.defaultBlockState().getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)))
			.toList());
		check(blocks.size() >= 20, "Água nos blocos: achei só " + blocks.size() + " blocos menores que o cubo");
		int rows = (blocks.size() + PER_ROW - 1) / PER_ROW;
		server.runCommand(String.format("forceload add %d %d %d %d", o.getX() - 2, o.getZ() - 2, o.getX() + 3 * PER_ROW, o.getZ() + 4 * rows));
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			ServerPlayer player = s.getPlayerList().getPlayers().get(0);
			for (int i = 0; i < blocks.size(); i++) {
				Block block = blocks.get(i);
				BlockPos cell = cell(o, i), side = cell.south();
				check(block.defaultBlockState().hasProperty(Waterlogging.WATERLOGGED),
					"Água nos blocos: " + name(block) + " não guarda água");
				check(!block.defaultBlockState().getValue(Waterlogging.WATERLOGGED), "Água nos blocos: " + name(block) + " nasce cheio");
				for (BlockPos p : new BlockPos[] {cell.below(), side.below(), cell.west(), cell.east(), side.west(), side.east(), cell.north(),
					side.south(), side}) {
					level.setBlockAndUpdate(p, Blocks.GLASS.defaultBlockState());
				}
				level.setBlockAndUpdate(cell, Blocks.WATER.defaultBlockState());
				BlockPlaceContext place = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, new ItemStack(block),
					new BlockHitResult(Vec3.atCenterOf(cell), Direction.UP, cell, false));
				BlockState state = block.getStateForPlacement(place);
				check(state != null, "Água nos blocos: " + name(block) + " não pôde ser colocado na água");
				check(state.getValue(Waterlogging.WATERLOGGED), "Água nos blocos: " + name(block) + " posto na fonte não ficou com água");
				level.setBlockAndUpdate(cell, state);
				check(level.getFluidState(cell).isSource(), "Água nos blocos: " + name(block) + " apagou a fonte");
			}
			// Abre a casa do lado: a água de dentro do bloco tem de correr para ela.
			for (int i = 0; i < blocks.size(); i++) {
				level.setBlockAndUpdate(cell(o, i).south(), Blocks.AIR.defaultBlockState());
			}
		});
		context.waitTicks(20);
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			for (int i = 0; i < blocks.size(); i++) {
				check(level.getFluidState(cell(o, i).south()).is(FluidTags.WATER),
					"Água nos blocos: a água de dentro de " + name(blocks.get(i)) + " não correu para o lado");
			}
		});
		BlockPos view = cell(o, Math.min(4, blocks.size() - 1));
		server.runCommand(String.format(java.util.Locale.ROOT, "tp @p %.2f %.2f %.2f 0 35", view.getX() + 0.5, view.getY() + 3.0, view.getZ() - 4.5));
		world.getConnection().waitForChunksRender();
		context.waitTicks(10);
		context.runOnClient(mc -> mc.gui.toastManager().clear());
		context.waitTick();
		context.takeScreenshot("agua_00_blocos");
		server.runOnServer(s -> {
			ServerLevel level = s.overworld();
			for (int i = 0; i < blocks.size(); i++) {
				BlockPos cell = cell(o, i);
				level.destroyBlock(cell, false);
				check(level.getFluidState(cell).isSource(), "Água nos blocos: quebrar " + name(blocks.get(i)) + " não devolveu a fonte");
			}
		});
	}

	private static BlockPos cell(BlockPos o, int i) {
		return o.offset(3 * (i % PER_ROW), 0, 4 * (i / PER_ROW));
	}

	private static String name(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block).getPath();
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
