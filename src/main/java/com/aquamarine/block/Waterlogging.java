package com.aquamarine.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Água dentro dos blocos (0.6.1, 7A; docs/12-industria.md): bloco menor que o cubo posto numa fonte guarda a água,
 * como uma cerca; a água de dentro corre para os vizinhos livres e quebrar o bloco devolve a fonte. Quem usa
 * implementa {@link SimpleWaterloggedBlock} (balde e fluxo do jogo), junta {@link #WATERLOGGED} ao estado com valor
 * inicial {@code false} e chama {@link #placed}, {@link #fluid} e {@link #tick} na colocação, no {@code getFluidState}
 * e no {@code updateShape}. Mundos antigos abrem com {@code waterlogged=false}.
 */
public final class Waterlogging {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	/** Estado de colocação com água dentro se o lugar for uma fonte de água. */
	public static BlockState placed(BlockState state, BlockPlaceContext context) {
		return state == null ? null
			: state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	public static FluidState fluid(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
	}

	/** No {@code updateShape}: quando um vizinho muda, a água de dentro volta a correr. */
	public static void tick(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos) {
		if (state.getValue(WATERLOGGED)) {
			ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
	}

	private Waterlogging() {}
}
