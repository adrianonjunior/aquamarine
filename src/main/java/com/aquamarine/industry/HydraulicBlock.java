package com.aquamarine.industry;

import com.aquamarine.block.FacingShapeBlock;
import com.aquamarine.network.ModNetworks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Bomba e Tanque (7D). Baldes usam a API transacional; a Chave continua recebendo seu clique. */
public class HydraulicBlock extends FacingShapeBlock implements EntityBlock {
    public enum Kind { PUMP, TANK }
    private final Kind kind;
    public HydraulicBlock(Properties properties, VoxelShape shape, Kind kind) {
        super(properties, shape); this.kind = kind;
    }
    public Kind kind() { return kind; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HydraulicBlockEntity(pos, state);
    }
    @Override @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModHydraulics.ENTITY ? (BlockEntityTicker<T>)(BlockEntityTicker<HydraulicBlockEntity>) HydraulicBlockEntity::tick : null;
    }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(ModNetworks.TIDE_WRENCH)) return InteractionResult.PASS;
        if (level.getBlockEntity(pos) instanceof HydraulicBlockEntity be && kind == Kind.TANK) {
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            if (FluidStorageUtil.interactWithFluidStorage(be.inventory(), player, hand)) return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof HydraulicBlockEntity be) {
            player.sendOverlayMessage(Component.translatable("message.aquamarine.hydraulic.contents",
                be.totalAmount() / 81000.0, be.totalCapacity() / 81000, be.energy.amount));
        }
        return InteractionResult.SUCCESS;
    }
}

