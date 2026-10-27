package com.aquamarine.network;

import java.util.Arrays;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import team.reborn.energy.api.EnergyStorage;

/**
 * Um pedaço de rede no mundo (0.6.1, 7C): guarda o modo de cada lado e a sua parte do estoque (para salvar e refazer a
 * rede), expõe o estoque da rede às máquinas pela API da Fabric e da Team Reborn Energy e recebe do servidor, só quando
 * muda, o que o desenho mostra: o conteúdo, o nível, o sentido em cada lado e se está passando carga.
 */
public class ConduitBlockEntity extends BlockEntity {
	private final PortMode[] modes = new PortMode[6];
	private FluidVariant shareVariant = FluidVariant.blank();
	private long share;
	ConduitNetwork network;
	private FluidVariant shownVariant = FluidVariant.blank();
	private int shownLevel;
	private int shownFlow;
	private boolean shownActive;

	public ConduitBlockEntity(BlockPos pos, BlockState state) {
		super(ModNetworks.CONDUIT_ENTITY, pos, state);
		Arrays.fill(modes, PortMode.OUT);
	}

	private ConduitPiece piece() {
		return getBlockState().getBlock() instanceof ConduitPiece piece ? piece : null;
	}

	public ConduitKind kind() {
		ConduitPiece piece = piece();
		return piece == null ? ConduitKind.FLUID : piece.kind();
	}

	public long rate() {
		ConduitPiece piece = piece();
		return piece == null ? 0 : piece.rate();
	}

	public long capacity() {
		ConduitPiece piece = piece();
		return piece == null ? 0 : piece.capacity();
	}

	/** O pedaço liga por este lado (o modo desligado não conta aqui). */
	public boolean opens(Direction side) {
		ConduitPiece piece = piece();
		return piece != null && piece.opens(getBlockState(), side);
	}

	public PortMode mode(Direction side) {
		return modes[side.ordinal()];
	}

	/** Por este lado o estoque da rede aparece para quem está do lado de fora. */
	public boolean exposed(Direction side) {
		return side == null || opens(side) && mode(side) != PortMode.OFF;
	}

	/** Chave de Maré: cicla o lado (sai, entra, desligado num conector; ligado e desligado entre canos). */
	public PortMode cycle(Direction side) {
		boolean port = level != null && !(level.getBlockState(worldPosition.relative(side)).getBlock() instanceof ConduitPiece);
		modes[side.ordinal()] = modes[side.ordinal()].next(port);
		setChanged();
		invalidateNetwork();
		if (level != null && !level.isClientSide()) {
			BlockState state = getBlockState();
			BlockState linked = state.getBlock() instanceof ConduitBlock block ? block.linked(level, worldPosition, state) : state;
			level.setBlock(worldPosition, linked, Block.UPDATE_ALL);
			level.sendBlockUpdated(worldPosition, linked, linked, Block.UPDATE_CLIENTS);
		}
		return modes[side.ordinal()];
	}

	ConduitNetwork network() {
		if ((network == null || !network.valid) && level != null && !level.isClientSide() && !isRemoved()) {
			network = ConduitNetwork.build(level, this);
		}
		return network != null && network.valid ? network : null;
	}

	public void invalidateNetwork() {
		if (network != null) {
			network.invalidate();
			network = null;
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ConduitBlockEntity be) {
		ConduitNetwork network = be.network();
		if (network != null) {
			network.tick((ServerLevel) level);
		}
	}

	@Override
	public void setRemoved() {
		invalidateNetwork();
		super.setRemoved();
	}

	/** API de fluidos (Cano e Tubo de Ar): o estoque da rede, pelos lados ligados. */
	public Storage<FluidVariant> fluidStorage(Direction side) {
		if (kind() == ConduitKind.ENERGY || !exposed(side)) {
			return null;
		}
		ConduitNetwork network = network();
		return network == null ? null : network.fluid;
	}

	/** API de energia (Cabo): a carga da rede, pelos lados ligados. */
	public EnergyStorage energyStorage(Direction side) {
		if (kind() != ConduitKind.ENERGY || !exposed(side)) {
			return null;
		}
		ConduitNetwork network = network();
		return network == null ? null : network.energy;
	}

	/** Conteúdo e quantidade que este pedaço carrega (a parte dele no estoque da rede). */
	public FluidVariant shareVariant() {
		return network != null && network.valid ? network.variant() : shareVariant;
	}

	public long share() {
		return network != null && network.valid ? network.shareOf(this) : share;
	}

	void setShare(FluidVariant variant, long amount) {
		shareVariant = variant;
		share = amount;
	}

	/** Servidor: o que o cliente desenha; manda só se mudou. */
	void show(FluidVariant variant, int level, int flow, boolean active) {
		if (variant.equals(shownVariant) && level == shownLevel && flow == shownFlow && active == shownActive) {
			return;
		}
		shownVariant = variant;
		shownLevel = level;
		shownFlow = flow;
		shownActive = active;
		if (this.level != null) {
			BlockState state = getBlockState();
			this.level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
		}
	}

	public FluidVariant shownVariant() {
		return shownVariant;
	}

	/** Nível do conteúdo de 0 a 8. */
	public int shownLevel() {
		return shownLevel;
	}

	/** Sentido em cada lado: dois bits por {@link Direction#ordinal()}, 1 = sai por ali, 2 = entra por ali. */
	public int shownFlow() {
		return shownFlow;
	}

	public boolean shownActive() {
		return shownActive;
	}

	private int packedModes() {
		int packed = 0;
		for (int i = 0; i < 6; i++) {
			packed |= modes[i].ordinal() << (2 * i);
		}
		return packed;
	}

	private void unpackModes(int packed) {
		for (int i = 0; i < 6; i++) {
			modes[i] = PortMode.byId((packed >> (2 * i)) & 3);
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		unpackModes(input.getIntOr("modes", 0));
		share = input.getLongOr("share", 0L);
		shareVariant = input.read("variant", FluidVariant.CODEC).orElse(FluidVariant.blank());
		shownVariant = variantOf(input.getStringOr("shown", ""));
		shownLevel = input.getIntOr("level", 0);
		shownFlow = input.getIntOr("flow", 0);
		shownActive = input.getBooleanOr("active", false);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("modes", packedModes());
		output.putLong("share", share());
		FluidVariant variant = shareVariant();
		if (!variant.isBlank()) {
			output.store("variant", FluidVariant.CODEC, variant);
		}
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/** Só o que o desenho precisa: modos dos lados, conteúdo, nível, sentido e carga passando. */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("modes", packedModes());
		if (!shownVariant.isBlank()) {
			tag.putString("shown", BuiltInRegistries.FLUID.getKey(shownVariant.getFluid()).toString());
		}
		tag.putInt("level", shownLevel);
		tag.putInt("flow", shownFlow);
		tag.putBoolean("active", shownActive);
		return tag;
	}

	private static FluidVariant variantOf(String id) {
		Identifier key = id.isEmpty() ? null : Identifier.tryParse(id);
		Fluid fluid = key == null ? Fluids.EMPTY : BuiltInRegistries.FLUID.getValue(key);
		return fluid == Fluids.EMPTY ? FluidVariant.blank() : FluidVariant.of(fluid);
	}
}
