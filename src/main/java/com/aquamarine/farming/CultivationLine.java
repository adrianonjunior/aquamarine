package com.aquamarine.farming;

import com.aquamarine.block.FacingShapeBlock;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Linha reta horizontal com corda entre peças. Nunca carrega chunks nem percorre mais de 64 blocos por ponta. */
public record CultivationLine(List<BlockPos> jars, boolean anchored) {
    public static Direction.Axis axis(BlockState state) {
        return state.getValue(FacingShapeBlock.FACING).getClockWise().getAxis();
    }

    public static CultivationLine find(Level level, BlockPos origin) {
        List<BlockPos> jars = new ArrayList<>();
        BlockState first = level.getBlockState(origin);
        if (!(first.getBlock() instanceof CultivationBlock)) return new CultivationLine(List.of(), false);
        Direction.Axis axis = axis(first);
        boolean anchored = first.is(ModCultivation.CULTIVATION_BUOY);
        if (first.is(ModCultivation.CULTIVATION_JAR)) jars.add(origin.immutable());
        for (Direction direction : axis == Direction.Axis.X
            ? new Direction[] {Direction.WEST, Direction.EAST} : new Direction[] {Direction.NORTH, Direction.SOUTH}) {
            boolean previousRope = first.is(ModCultivation.CULTIVATION_ROPE);
            for (int distance = 1; distance <= 64; distance++) {
                BlockPos pos = origin.relative(direction, distance);
                if (!level.hasChunkAt(pos)) break;
                BlockState state = level.getBlockState(pos);
                if (!(state.getBlock() instanceof CultivationBlock) || axis(state) != axis) break;
                boolean rope = state.is(ModCultivation.CULTIVATION_ROPE);
                if (!rope && !previousRope) break;
                if (state.is(ModCultivation.CULTIVATION_BUOY)) { anchored = true; break; }
                if (state.is(ModCultivation.CULTIVATION_JAR)) jars.add(pos.immutable());
                previousRope = rope;
            }
        }
        return new CultivationLine(List.copyOf(jars), anchored);
    }
}

