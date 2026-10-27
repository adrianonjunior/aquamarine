package com.aquamarine.energy;

import com.aquamarine.item.ModItems;
import com.aquamarine.machine.MachineMotion;
import com.aquamarine.machine.MachineState;
import com.aquamarine.machine.MachineView;
import com.aquamarine.menu.NeutroniumCollectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import team.reborn.energy.api.base.SimpleEnergyStorage;

/** Controlador único do Coletor 3×2×3: energia, progresso, estoque, menu e relógio do BER. */
public final class NeutroniumCollectorBlockEntity extends BlockEntity implements MachineView, MenuProvider {
	public static final int SPOT_OK = 0;
	public static final int SPOT_NO_DEEP_OCEAN = 1;
	public static final int SPOT_NO_WATER_ABOVE = 2;
	public static final int SPOT_TOO_SHALLOW = 3;
	private static final int COLLECT_ANIMATION_TICKS = 24;

	public final SimpleEnergyStorage energy = new SimpleEnergyStorage(WaterEnergy.COLLECTOR_CAPACITY, WaterEnergy.COLLECTOR_INSERT, 0) {
		@Override protected void onFinalCommit() { setChanged(); }
	};
	private final MachineMotion motion = new MachineMotion();
	private int progress;
	private int dust;
	private int spotReason = SPOT_NO_DEEP_OCEAN;
	private long spotCheckedAt = -100;
	private int collectTicks;
	private boolean assembled;
	private MachineState machine = MachineState.IDLE;
	private float clientProgress;

	public NeutroniumCollectorBlockEntity(BlockPos pos, BlockState state) {
		super(ModEnergy.COLLECTOR_ENTITY, pos, state);
	}

	@Override public MachineMotion motion() { return motion; }
	@Override public float visualProgress() { return Mth.clamp(clientProgress / WaterEnergy.COLLECTOR_TICKS, 0.0F, 1.0F); }
	@Override public float visualStock() { return dust / (float) WaterEnergy.COLLECTOR_MAX_DUST; }
	@Override public boolean visualInvalid() { return spotReason != SPOT_OK; }
	public int progress() { return progress; }
	public int dust() { return dust; }
	public int spotReason() { return spotReason; }
	public boolean assembled() { return assembled; }
	void markAssembled() { assembled = true; setChanged(); }

	/** Para testes: adianta o trabalho sem mudar as regras de produção. */
	public void setProgress(int ticks) {
		progress = Mth.clamp(ticks, 0, WaterEnergy.COLLECTOR_TICKS - 1);
		clientProgress = progress;
		setChanged();
	}

	public static boolean validSpot(Level level, BlockPos pos) { return spotReason(level, pos) == SPOT_OK; }

	public static int spotReason(Level level, BlockPos pos) {
		if (forceValidSpot) return SPOT_OK;
		if (!level.getBiome(pos).is(BiomeTags.IS_DEEP_OCEAN)) return SPOT_NO_DEEP_OCEAN;
		if (!level.getFluidState(pos.above()).is(FluidTags.WATER)) return SPOT_NO_WATER_ABOVE;
		return pos.getY() <= level.getSeaLevel() - WaterEnergy.COLLECTOR_MIN_DEPTH ? SPOT_OK : SPOT_TOO_SHALLOW;
	}

	/** Só para GameTest: o mundo plano não tem oceano profundo. */
	public static boolean forceValidSpot;

	private int cachedSpotReason(Level level, BlockPos pos) {
		if (level.getGameTime() - spotCheckedAt >= 20) {
			spotReason = spotReason(level, pos);
			spotCheckedAt = level.getGameTime();
		}
		return spotReason;
	}

	public static void tick(Level level, BlockPos pos, BlockState state, NeutroniumCollectorBlockEntity be) {
		if (level.isClientSide()) {
			clientTick(level, pos, be);
			return;
		}
		if (!(level instanceof ServerLevel)) return;
		if (!be.assembled) {
			if (!NeutroniumCollectorBlock.assemble(level, pos, state, be)) {
				NeutroniumCollectorBlock.dismantle(level, pos, true);
				return;
			}
		}

		int oldReason = be.spotReason;
		int reason = be.cachedSpotReason(level, pos);
		boolean working = be.dust < WaterEnergy.COLLECTOR_MAX_DUST && be.energy.amount >= WaterEnergy.COLLECTOR_USE
			&& reason == SPOT_OK;
		boolean produced = false;
		if (working) {
			be.energy.amount -= WaterEnergy.COLLECTOR_USE;
			if (++be.progress >= WaterEnergy.COLLECTOR_TICKS) {
				be.progress = 0;
				be.dust++;
				be.collectTicks = COLLECT_ANIMATION_TICKS;
				produced = true;
			}
			be.setChanged();
		}
		if (be.collectTicks > 0) be.collectTicks--;
		MachineState next = be.dust >= WaterEnergy.COLLECTOR_MAX_DUST ? MachineState.FULL
			: be.collectTicks > 0 ? MachineState.COLLECTING : working ? MachineState.RUNNING : MachineState.IDLE;
		if (next != be.machine || oldReason != reason || produced) {
			be.machine = next;
			be.sync();
		}
		NeutroniumCollectorBlock.setActive(level, pos, state, working);
	}

	private static void clientTick(Level level, BlockPos pos, NeutroniumCollectorBlockEntity be) {
		double now = level.getGameTime() / 20.0;
		be.motion.update(be.machine, 1.0F, now, 1.6);
		if (be.machine == MachineState.RUNNING) be.clientProgress = Math.min(WaterEnergy.COLLECTOR_TICKS, be.clientProgress + 1);
		if ((be.machine == MachineState.RUNNING || be.machine == MachineState.COLLECTING) && level.getGameTime() % 4 == 0) {
			double x = pos.getX() + .5 + (level.getRandom().nextDouble() - .5) * .65;
			double z = pos.getZ() + .5 + (level.getRandom().nextDouble() - .5) * .65;
			level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 1.25, z, 0, .01, 0);
			level.addParticle(ParticleTypes.ENCHANT, x, pos.getY() + .85, z, 0, .045, 0);
		}
	}

	public ItemStack takeDust(int maximum) {
		int out = Math.min(dust, maximum);
		if (out > 0) {
			dust -= out;
			sync();
		}
		return dustStack(out);
	}

	public ItemStack dustStack(int count) { return new ItemStack(ModItems.NEUTRONIUM_STARDUST, count); }

	/** Item único da estrutura, carregando o pó no componente de dados do block entity. */
	public ItemStack machineStack() {
		ItemStack stack = new ItemStack(ModEnergy.NEUTRONIUM_COLLECTOR);
		if (dust > 0) {
			CompoundTag tag = new CompoundTag();
			tag.putInt("dust", dust);
			stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(ModEnergy.COLLECTOR_ENTITY, tag));
		}
		return stack;
	}

	private void sync() {
		setChanged();
		if (level != null && !level.isClientSide()) {
			BlockState state = getBlockState();
			level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
		}
	}

	@Override public Component getDisplayName() { return Component.translatable("container.aquamarine.neutronium_collector"); }
	@Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new NeutroniumCollectorMenu(id, inventory, this); }

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		super.preRemoveSideEffects(pos, state);
		if (level != null && !level.isClientSide()) NeutroniumCollectorBlock.dismantle(level, pos, true);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		energy.amount = Math.clamp(input.getLongOr("energy", 0L), 0, WaterEnergy.COLLECTOR_CAPACITY);
		progress = Mth.clamp(input.getIntOr("progress", 0), 0, WaterEnergy.COLLECTOR_TICKS - 1);
		dust = Mth.clamp(input.getIntOr("dust", 0), 0, WaterEnergy.COLLECTOR_MAX_DUST);
		assembled = input.getBooleanOr("assembled", false);
		machine = MachineState.byId(input.getByteOr("machine", (byte) 0));
		spotReason = input.getIntOr("spot_reason", SPOT_NO_DEEP_OCEAN);
		clientProgress = progress;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putLong("energy", energy.amount);
		output.putInt("progress", progress);
		output.putInt("dust", dust);
		output.putBoolean("assembled", assembled);
		output.putByte("machine", (byte) machine.ordinal());
		output.putInt("spot_reason", spotReason);
	}

	@Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
	@Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		tag.putByte("machine", (byte) machine.ordinal());
		tag.putInt("progress", progress);
		tag.putInt("dust", dust);
		tag.putInt("spot_reason", spotReason);
		return tag;
	}
}
