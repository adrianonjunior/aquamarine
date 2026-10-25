package com.aquamarine.harbor;

import com.aquamarine.entity.ModVillagers;

import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * Elevadores de bolhas do Cais do Porto dos Náufragos (docs/08-fase3.md, seção 8): areia das almas sobe,
 * magma desce, do fundo até a superfície. A geração do mundo não garante as colunas (a decoração do oceano
 * põe algas em cima, e às vezes a coluna nem começa); por isso, a cada 5 s, com jogador em um Porto, as
 * colunas que faltam são refeitas, tirando as plantas do caminho.
 */
public final class HarborElevators {
	/** Bases dos elevadores na peça {@code porto.nbt} (as de {@code ELEVATORS} em {@code tools/gen_harbor.py}). */
	private static final List<BlockPos> BASES = List.of(new BlockPos(74, 0, 45), new BlockPos(74, 0, 47),
		new BlockPos(6, 0, 35), new BlockPos(6, 0, 37));
	private static final int INTERVAL = 100;

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % INTERVAL == 0) {
				ServerLevel level = server.overworld();
				for (ServerPlayer player : level.players()) {
					refresh(level, player.blockPosition());
				}
			}
		});
	}

	/** Refaz as colunas do Porto onde está {@code pos}. Devolve quantas refez. */
	public static int refresh(ServerLevel level, BlockPos pos) {
		StructureStart start = level.structureManager().getStructureWithPieceAt(pos, ModVillagers.HARBORS);
		if (!start.isValid() || !(start.getPieces().getFirst() instanceof PoolElementStructurePiece piece)) {
			return 0;
		}
		int built = 0;
		for (BlockPos local : BASES) {
			BlockPos base = piece.getPosition().offset(StructureTemplate.transform(local, Mirror.NONE, piece.getRotation(), BlockPos.ZERO));
			if (!level.isLoaded(base)) {
				continue;
			}
			BlockState state = level.getBlockState(base);
			if (!state.is(BlockTags.ENABLES_BUBBLE_COLUMN_PUSH_UP) && !state.is(BlockTags.ENABLES_BUBBLE_COLUMN_DRAG_DOWN)
				|| level.getBlockState(base.above()).is(Blocks.BUBBLE_COLUMN)) {
				continue;
			}
			for (BlockPos plant = base.above(); isSeaPlant(level.getBlockState(plant)); plant = plant.above()) {
				level.setBlock(plant, Blocks.WATER.defaultBlockState(), Block.UPDATE_CLIENTS);
			}
			BubbleColumnBlock.updateColumn(Blocks.BUBBLE_COLUMN, level, base.above(), state);
			built++;
		}
		return built;
	}

	private static boolean isSeaPlant(BlockState state) {
		return state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT) || state.is(Blocks.SEAGRASS) || state.is(Blocks.TALL_SEAGRASS);
	}

	private HarborElevators() {}
}
