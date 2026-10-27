package com.aquamarine.farming;

import com.aquamarine.machine.MachineMotion;
import com.aquamarine.machine.MachineState;
import com.aquamarine.machine.MachineView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

/** Sinal visual da linha; recalcula no máximo a cada 40 tiques e não persiste estado derivado. */
public final class CultivationBuoyBlockEntity extends BlockEntity implements MachineView {
    private final MachineMotion motion = new MachineMotion();
    private MachineState machine = MachineState.IDLE;

    public CultivationBuoyBlockEntity(BlockPos pos, BlockState state) {
        super(ModCultivation.BUOY_ENTITY, pos, state);
    }

    @Override public MachineMotion motion() { return motion; }
    public MachineState machineState() { return machine; }

    public static void tick(Level level, BlockPos pos, BlockState state, CultivationBuoyBlockEntity be) {
        if (level.isClientSide()) {
            be.motion.update(be.machine, 1.0F, level.getGameTime() / 20.0, 1.2);
        } else if (Math.floorMod(level.getGameTime() + pos.asLong(), 40L) == 0L) {
            be.refreshState();
        }
    }

    /** Chamado também logo depois de colher, para o clipe harvest começar sem esperar a próxima sondagem. */
    public void refreshState() {
        if (level == null || level.isClientSide()) return;
        CultivationLine line = CultivationLine.find(level, worldPosition);
        boolean full = false, running = false;
        for (BlockPos jarPos : line.jars()) {
            if (level.getBlockEntity(jarPos) instanceof CultivationJarBlockEntity jar) {
                full |= jar.pearls() > 0;
                running |= jar.working();
            }
        }
        MachineState next = full ? MachineState.FULL : running ? MachineState.RUNNING : MachineState.IDLE;
        if (next != machine) {
            machine = next;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        machine = MachineState.byId(input.getByteOr("machine", (byte) 0));
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putByte("machine", (byte) machine.ordinal());
        return tag;
    }
}

