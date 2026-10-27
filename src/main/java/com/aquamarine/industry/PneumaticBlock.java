package com.aquamarine.industry;

import com.aquamarine.block.FacingShapeBlock;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Compressor de Ar e Pulmão de Ferro (0.6.1, 7F; docs/12-industria.md): máquinas de gás animadas, ligadas pelo Tubo de
 * Ar. A Chave de Maré muda os lados (o clique dela passa direto para a Chave); a mão vazia mostra o que guardam.
 */
public class PneumaticBlock extends FacingShapeBlock implements EntityBlock {
	public enum Kind { COMPRESSOR, LUNG }

	private final Kind kind;

	public PneumaticBlock(Properties properties, VoxelShape shape, Kind kind) {
		super(properties, shape);
		this.kind = kind;
	}

	public Kind kind() {
		return kind;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PneumaticBlockEntity(pos, state);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (type != ModPneumatics.ENTITY) {
			return null;
		}
		BlockEntityTicker<PneumaticBlockEntity> ticker = level.isClientSide() ? PneumaticBlockEntity::clientTick : PneumaticBlockEntity::tick;
		return (BlockEntityTicker<T>) ticker;
	}

	/** Máquina animada: quem desenha é o renderizador do block entity. */
	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PneumaticBlockEntity be) {
			String air = String.format(Locale.ROOT, "%.1f", be.amount() / 81000.0);
			long capacity = be.capacity() / 81000;
			player.sendOverlayMessage(kind == Kind.COMPRESSOR
				? Component.translatable("message.aquamarine.pneumatic.compressor", air, capacity, be.energy.amount)
				: Component.translatable("message.aquamarine.pneumatic.lung", air, capacity));
		}
		return InteractionResult.SUCCESS;
	}
}
