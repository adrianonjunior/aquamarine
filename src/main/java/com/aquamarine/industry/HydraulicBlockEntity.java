package com.aquamarine.industry;

import com.aquamarine.machine.MachineMotion;
import com.aquamarine.machine.MachineState;
import com.aquamarine.machine.MachineView;
import com.aquamarine.network.ConduitNetwork;
import com.aquamarine.network.PortConfigurable;
import com.aquamarine.network.PortMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import team.reborn.energy.api.base.SimpleEnergyStorage;

/** 7D: cada tanque salva sua parcela; agregar uma coluna nunca cria um segundo estoque. */
public class HydraulicBlockEntity extends BlockEntity implements MachineView, PortConfigurable {
    public static final long BUCKET = FluidConstants.BUCKET;
    public static final long TANK_CAPACITY = 64 * BUCKET;
    public static final long PUMP_COST = 160;
    private final PortMode[] modes = new PortMode[6];
    private final MachineMotion motion = new MachineMotion();
    private MachineState machine = MachineState.IDLE;
    private boolean dirty = true;
    private long activeUntil;
    private long shownAmount;
    private float smoothFill;
    private final SingleVariantStorage<FluidVariant> contents = new SingleVariantStorage<>() {
        protected FluidVariant getBlankVariant() { return FluidVariant.blank(); }
        protected long getCapacity(FluidVariant variant) { return capacity(); }
        protected boolean canInsert(FluidVariant variant) {
            return variant.isOf(Fluids.WATER) || kind() == HydraulicBlock.Kind.TANK && variant.isOf(Fluids.LAVA);
        }
        protected void onFinalCommit() { changed(); }
    };
    public final SimpleEnergyStorage energy = new SimpleEnergyStorage(16000, 256, 0) {
        protected void onFinalCommit() { setChanged(); dirty = true; }
    };
    public HydraulicBlockEntity(BlockPos pos, BlockState state) {
        super(ModHydraulics.ENTITY, pos, state);
        Arrays.fill(modes, kind() == HydraulicBlock.Kind.PUMP ? PortMode.OUT : PortMode.IN);
        modes[Direction.DOWN.ordinal()] = PortMode.OUT;
    }
    public HydraulicBlock.Kind kind() { return ((HydraulicBlock)getBlockState().getBlock()).kind(); }
    public long capacity() { return kind() == HydraulicBlock.Kind.TANK ? TANK_CAPACITY : 4 * BUCKET; }
    public long amount() { return contents.amount; }
    public FluidVariant variant() { return contents.variant; }
    public float fill(float partial) { return smoothFill + (shownAmount / (float)capacity() - smoothFill) * partial * .2F; }
    @Override public MachineMotion motion() { return motion; }
    @Override public PortMode port(Direction side) { return modes[side.ordinal()]; }
    public boolean enabled(Direction side) { return !isRemoved() && (side == null || port(side) != PortMode.OFF); }
    @Override public void cyclePort(Direction side) {
        modes[side.ordinal()] = port(side).next(true); changed();
        if (level != null) {
            ConduitNetwork.invalidateAround(level, worldPosition);
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    }
    private void changed() {
        setChanged(); dirty = true;
        if (level != null) activeUntil = level.getGameTime() + 24;
    }
    /** Coluna carregada: não força chunks. Campos locais são a única fonte persistida. */
    private List<SingleVariantStorage<FluidVariant>> column() {
        List<SingleVariantStorage<FluidVariant>> tanks = new ArrayList<>();
        if (kind() != HydraulicBlock.Kind.TANK || level == null) { tanks.add(contents); return tanks; }
        BlockPos bottom = worldPosition;
        while (level.isLoaded(bottom.below()) && level.getBlockEntity(bottom.below()) instanceof HydraulicBlockEntity b
                && b.kind() == HydraulicBlock.Kind.TANK && !b.isRemoved()) bottom = bottom.below();
        for (BlockPos p = bottom; level.isLoaded(p) && level.getBlockEntity(p) instanceof HydraulicBlockEntity b
                && b.kind() == HydraulicBlock.Kind.TANK && !b.isRemoved(); p = p.above()) tanks.add(b.contents);
        return tanks;
    }
    public Storage<FluidVariant> inventory() {
        List<SingleVariantStorage<FluidVariant>> tanks = column();
        return new CombinedStorage<>(tanks) {
            @Override public long insert(FluidVariant v, long max, TransactionContext tx) {
                if (tanks.stream().anyMatch(t -> t.amount > 0 && !t.variant.equals(v))) return 0;
                return super.insert(v, max, tx);
            }
        };
    }
    public long totalAmount() { return column().stream().mapToLong(t -> t.amount).sum(); }
    public long totalCapacity() { return column().size() * capacity(); }
    public Storage<FluidVariant> fluidPort(Direction side) {
        if (!enabled(side)) return null;
        return new SidedFluidStorage(this::inventory,
            () -> enabled(side) && kind() == HydraulicBlock.Kind.TANK && (side == null || port(side) == PortMode.IN),
            () -> enabled(side) && (side == null || port(side) == PortMode.OUT));
    }
    private void push() {
        long left = BUCKET / 20;
        for (Direction d : Direction.values()) {
            if (port(d) != PortMode.OUT || left <= 0) continue;
            BlockPos target = worldPosition.relative(d);
            if (!level.isLoaded(target)) continue;
            if (level.getBlockEntity(target) instanceof HydraulicBlockEntity b && b.kind() == HydraulicBlock.Kind.TANK
                    && kind() == HydraulicBlock.Kind.TANK && d.getAxis() == Direction.Axis.Y) continue;
            Storage<FluidVariant> to = FluidStorage.SIDED.find(level, target, d.getOpposite());
            if (to != null) try (Transaction tx = Transaction.openOuter()) {
                left -= StorageUtil.move(inventory(), to, v -> true, left, tx); tx.commit();
            }
        }
    }
    /** Captação em lotes: fonte renovável permanece; uma fonte isolada fornece exatamente um balde. */
    private void pump() {
        if (energy.amount < PUMP_COST || contents.amount + BUCKET > capacity()) return;
        for (Direction d : Direction.values()) {
            BlockPos source = worldPosition.relative(d);
            if (!level.isLoaded(source) || !level.getBlockState(source).is(Blocks.WATER)
                    || !level.getFluidState(source).isSource()) continue;
            int neighbors = 0;
            for (Direction h : Direction.Plane.HORIZONTAL)
                if (level.getFluidState(source.relative(h)).isSourceOfType(Fluids.WATER)) neighbors++;
            boolean renewable = neighbors >= 2 && (level.getBlockState(source.below()).isSolid()
                || level.getFluidState(source.below()).isSourceOfType(Fluids.WATER));
            try (Transaction tx = Transaction.openOuter()) {
                if (contents.insert(FluidVariant.of(Fluids.WATER), BUCKET, tx) != BUCKET) return;
                // A energia pertence à mesma transação; saída externa nunca pode extrair do reservatório da Bomba.
                energy.amount -= PUMP_COST;
                tx.commit();
            }
            if (!renewable) level.setBlockAndUpdate(source, Blocks.AIR.defaultBlockState());
            changed(); return;
        }
    }
    private void settle() {
        if (contents.amount == 0 || !level.isLoaded(worldPosition.below())) return;
        if (level.getBlockEntity(worldPosition.below()) instanceof HydraulicBlockEntity b && b.kind() == HydraulicBlock.Kind.TANK) {
            try (Transaction tx = Transaction.openOuter()) {
                StorageUtil.move(contents, b.contents, v -> true, 4 * BUCKET, tx); tx.commit();
            }
        }
    }
    public static void tick(Level level, BlockPos pos, BlockState state, HydraulicBlockEntity be) {
        if (level.isClientSide()) {
            be.motion.update(be.machine, 1, level.getGameTime()/20.0, 1.2);
            be.smoothFill += (be.shownAmount/(float)be.capacity()-be.smoothFill)*.2F;
            if (be.kind() == HydraulicBlock.Kind.PUMP && be.machine == MachineState.RUNNING && level.getGameTime()%10==0)
                level.addParticle(ParticleTypes.BUBBLE, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, 0,.03,0);
            return;
        }
        if (be.kind() == HydraulicBlock.Kind.PUMP && level.getGameTime()%20==0) be.pump();
        if (be.kind() == HydraulicBlock.Kind.TANK) be.settle();
        be.push();
        MachineState next = be.contents.amount == be.capacity() ? MachineState.FULL
            : level.getGameTime()<be.activeUntil ? MachineState.RUNNING : MachineState.IDLE;
        if (next != be.machine) { be.machine = next; be.dirty = true; }
        if (be.dirty && level.getGameTime()%5==0) {
            be.dirty = false; level.sendBlockUpdated(pos,state,state,Block.UPDATE_CLIENTS);
        }
    }
    private int packedModes() {
        int result=0;
        for(int i=0;i<6;i++) result |= modes[i].ordinal() << (i*2);
        return result;
    }
    @Override protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putLong("amount",contents.amount); out.store("variant",FluidVariant.CODEC,contents.variant);
        out.putLong("energy",energy.amount); out.putInt("modes",packedModes());
    }
    @Override protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        contents.variant=in.read("variant",FluidVariant.CODEC).orElse(FluidVariant.blank());
        contents.amount=Math.clamp(in.getLongOr("amount",0),0,capacity());
        if (contents.variant.isBlank()) contents.amount=0;
        energy.amount=Math.clamp(in.getLongOr("energy",0),0,energy.capacity);
        int packed=in.getIntOr("modes",packedModes());
        for(int i=0;i<6;i++) modes[i]=PortMode.byId((packed>>(i*2))&3);
        machine=MachineState.byId(in.getByteOr("machine",(byte)0)); shownAmount=contents.amount;
    }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag=saveWithoutMetadata(registries); tag.putByte("machine",(byte)machine.ordinal()); return tag;
    }
}

