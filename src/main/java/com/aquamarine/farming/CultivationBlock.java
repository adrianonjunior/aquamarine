package com.aquamarine.farming;

import com.aquamarine.block.FacingShapeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CultivationBlock extends FacingShapeBlock implements EntityBlock {
    public enum Kind { JAR, ROPE, BUOY }
    private final Kind kind;
    public CultivationBlock(Properties properties, VoxelShape shape, Kind kind) {
        super(properties, shape);
        this.kind = kind;
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return switch (kind) {
            case JAR -> new CultivationJarBlockEntity(pos, state);
            case ROPE -> new CultivationRopeBlockEntity(pos, state);
            case BUOY -> new CultivationBuoyBlockEntity(pos, state);
        };
    }
    @Override protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
    @Override @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == ModCultivation.JAR_ENTITY)
            return (BlockEntityTicker<T>) (BlockEntityTicker<CultivationJarBlockEntity>) CultivationJarBlockEntity::tick;
        if (type == ModCultivation.ROPE_ENTITY && level.isClientSide())
            return (BlockEntityTicker<T>) (BlockEntityTicker<CultivationRopeBlockEntity>) CultivationRopeBlockEntity::clientTick;
        if (type == ModCultivation.BUOY_ENTITY)
            return (BlockEntityTicker<T>) (BlockEntityTicker<CultivationBuoyBlockEntity>) CultivationBuoyBlockEntity::tick;
        return null;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (kind == Kind.ROPE) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            CultivationLine line = CultivationLine.find(level, pos);
            int pearls = 0, progress = 0, active = 0;
            for (BlockPos jarPos : kind == Kind.BUOY ? line.jars() : java.util.List.of(pos)) {
                if (level.getBlockEntity(jarPos) instanceof CultivationJarBlockEntity jar) {
                    progress = Math.max(progress, jar.progressPercent());
                    if (jar.working()) active++;
                    if (kind == Kind.BUOY || player.isShiftKeyDown()) {
                        int count = jar.takePearls();
                        pearls += count;
                        ItemStack stack = new ItemStack(ModCultivation.OYSTER_PEARL, count);
                        if (!stack.isEmpty() && !player.getInventory().add(stack)) Block.popResource(level, pos.above(), stack);
                    } else pearls += jar.pearls();
                }
            }
            if (kind == Kind.BUOY && level.getBlockEntity(pos) instanceof CultivationBuoyBlockEntity buoy) buoy.refreshState();
            player.sendOverlayMessage(Component.translatable("message.aquamarine.cultivation", line.jars().size(), active, pearls, progress,
                Component.translatable(line.anchored() ? "message.aquamarine.cultivation.connected" : "message.aquamarine.cultivation.disconnected")));
        }
        return InteractionResult.SUCCESS;
    }
}

