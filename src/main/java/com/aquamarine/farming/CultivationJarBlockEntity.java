package com.aquamarine.farming;

import com.aquamarine.machine.MachineMotion;
import com.aquamarine.machine.MachineState;
import com.aquamarine.machine.MachineView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Cada garrafão guarda seu progresso e produto; não há estoque duplicado no controlador da linha. */
public final class CultivationJarBlockEntity extends BlockEntity implements MachineView {
    public static final int CYCLE = 20 * 60 * 8 * 10; // unidades de 0,1 tick: bônus exato sem arredondamento acumulado
    public static final int CAPACITY = 64;
    private int progress;
    private int pearls;
    private boolean working;
    private int speed = 10;
    private final MachineMotion motion = new MachineMotion();

    public CultivationJarBlockEntity(BlockPos pos, BlockState state) { super(ModCultivation.JAR_ENTITY, pos, state); }
    @Override public MachineMotion motion() { return motion; }
    public int pearls() { return pearls; }
    public boolean working() { return working; }
    public int progressPercent() { return progress * 100 / CYCLE; }
    public int progressUnits() { return progress; }
    public void setProgressUnits(int units) { progress = Mth.clamp(units, 0, CYCLE - 1); setChanged(); }

    public static boolean cleanWater(Level level, BlockPos pos) {
        if (!level.getFluidState(pos).is(FluidTags.WATER)) return false;
        for (Direction direction : Direction.values()) {
            BlockPos p = pos.relative(direction);
            if (level.getBlockState(p).is(Blocks.MAGMA_BLOCK) || level.getFluidState(p).is(FluidTags.LAVA)) return false;
            if (direction != Direction.DOWN && !level.getFluidState(p).is(FluidTags.WATER)) return false;
        }
        return true;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CultivationJarBlockEntity be) {
        if (level.isClientSide()) {
            be.motion.update(be.pearls > 0 ? MachineState.FULL : be.working ? MachineState.RUNNING : MachineState.IDLE,
                be.speed / 10F, level.getGameTime() / 20.0, 1.5);
            if (be.working && level.getGameTime() % 20 == 0) {
                level.addParticle(ParticleTypes.BUBBLE, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, 0, 0.02, 0);
            }
            return;
        }
        // Revalidação imediata impede um tique de produção depois de quebrar a corda ou retirar a água.
        CultivationLine line = CultivationLine.find(level, pos);
        boolean work = be.pearls < CAPACITY && line.anchored() && cleanWater(level, pos);
        int nextSpeed = 10 + Math.min(5, Math.max(0, line.jars().size() - 1));
        boolean changed = be.working != work || be.speed != nextSpeed;
        be.working = work;
        be.speed = nextSpeed;
        if (work) {
            be.progress += be.speed;
            if (be.progress >= CYCLE) { be.progress -= CYCLE; be.pearls++; changed = true; }
            be.setChanged();
        }
        if (changed) be.sync();
    }

    public int takePearls() {
        int out = pearls;
        if (out > 0) { pearls = 0; sync(); }
        return out;
    }
    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide()) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
    @Override public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (level != null && !level.isClientSide() && pearls > 0) {
            Block.popResource(level, pos, new ItemStack(ModCultivation.OYSTER_PEARL, pearls));
            pearls = 0;
        }
    }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("progress", progress);
        output.putInt("pearls", pearls);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        progress = Mth.clamp(input.getIntOr("progress", 0), 0, CYCLE - 1);
        pearls = Mth.clamp(input.getIntOr("pearls", 0), 0, CAPACITY);
        working = input.getBooleanOr("working", false);
        speed = Mth.clamp(input.getIntOr("speed", 10), 10, 15);
    }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("pearls", pearls);
        tag.putBoolean("working", working);
        tag.putInt("speed", speed);
        return tag;
    }
}

