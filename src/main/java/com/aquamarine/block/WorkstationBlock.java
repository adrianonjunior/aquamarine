package com.aquamarine.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Mesas de trabalho dos Mergulhadores com função (Fase 6, 6C2; docs/11-fase6.md), usando as telas do jogo:
 * Bigorna de Maré (bigorna: reparar e renomear; no equipamento do mod o custo não cresce e ela não se desgasta),
 * Mesa de Cartas Náuticas (cartografia: copiar, ampliar e travar mapas) e Altar de Concha (rebolo: desencantar).
 * Os ajustes nas telas estão em {@code WorkstationMenusMixin} e {@code AnvilMenuMixin}.
 */
public class WorkstationBlock extends FacingShapeBlock {
	public enum Kind { ANVIL, CHART, ALTAR }

	private final Kind kind;

	public WorkstationBlock(Properties properties, VoxelShape northShape, Kind kind) {
		super(properties, northShape);
		this.kind = kind;
	}

	public Kind kind() {
		return kind;
	}

	/** A mesa de função {@code kind} na posição do menu, se for ela. */
	public static boolean is(ContainerLevelAccess access, Kind kind) {
		return access.evaluate((level, pos) -> level.getBlockState(pos).getBlock() instanceof WorkstationBlock w && w.kind == kind, false);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide()) {
			player.openMenu(state.getMenuProvider(level, pos));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		ContainerLevelAccess access = ContainerLevelAccess.create(level, pos);
		return switch (kind) {
			case ANVIL -> new SimpleMenuProvider((id, inv, p) -> new AnvilMenu(id, inv, access),
				Component.translatable("container.aquamarine.tide_anvil"));
			case CHART -> new SimpleMenuProvider((id, inv, p) -> new CartographyTableMenu(id, inv, access),
				Component.translatable("container.aquamarine.chart_table"));
			case ALTAR -> new SimpleMenuProvider((id, inv, p) -> new GrindstoneMenu(id, inv, access),
				Component.translatable("container.aquamarine.shell_altar"));
		};
	}
}
