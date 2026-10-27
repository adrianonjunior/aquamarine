package com.aquamarine.farming;

import com.aquamarine.machine.MachineMotion;
import com.aquamarine.machine.MachineState;
import com.aquamarine.machine.MachineView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Relógio puramente visual da corda: não salva nem manda dados pela rede. */
public final class CultivationRopeBlockEntity extends BlockEntity implements MachineView {
    private final MachineMotion motion = new MachineMotion();

    public CultivationRopeBlockEntity(BlockPos pos, BlockState state) {
        super(ModCultivation.ROPE_ENTITY, pos, state);
    }

    @Override public MachineMotion motion() { return motion; }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CultivationRopeBlockEntity be) {
        Direction.Axis axis = CultivationLine.axis(state);
        int along = axis == Direction.Axis.X ? pos.getX() : pos.getZ();
        be.motion.setPhaseOffset(along * 0.3);
        be.motion.update(MachineState.IDLE, 1.0F, level.getGameTime() / 20.0, 0.0);
    }
}

