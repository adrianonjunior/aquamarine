package com.aquamarine.network;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import team.reborn.energy.api.EnergyStorage;

/**
 * Redes da indústria (0.6.1, 7C; docs/12-industria.md): Cabo e Cabo Reforçado (energia), Cano e Cano Reforçado (água e
 * lava), Tubo de Ar (Ar Comprimido), a Válvula e o Visor de cada rede, a Chave de Maré e o Lingote de Latão. Modelos,
 * texturas e dados saem de tools/gen_industry_networks.py.
 */
public final class ModNetworks {
	private static final List<Block> BLOCKS = new ArrayList<>();

	/** Ar Comprimido: fluido só da API de transferência (sem bloco nem balde). */
	public static final Fluid COMPRESSED_AIR = Registry.register(BuiltInRegistries.FLUID, Aquamarine.id("compressed_air"), new CompressedAirFluid());

	public static final Block AQUATIC_CABLE = add("aquatic_cable", p -> new ConduitBlock(p, ConduitKind.ENERGY, false), cable());
	public static final Block REINFORCED_AQUATIC_CABLE = add("reinforced_aquatic_cable", p -> new ConduitBlock(p, ConduitKind.ENERGY, true), cable());
	public static final Block CABLE_SWITCH = add("cable_switch", p -> new InlineConduitBlock(p, ConduitKind.ENERGY, true), cable());
	public static final Block CABLE_WINDOW = add("cable_window", p -> new InlineConduitBlock(p, ConduitKind.ENERGY, false), cable());
	public static final Block FLUID_PIPE = add("fluid_pipe", p -> new ConduitBlock(p, ConduitKind.FLUID, false), pipe());
	public static final Block REINFORCED_FLUID_PIPE = add("reinforced_fluid_pipe", p -> new ConduitBlock(p, ConduitKind.FLUID, true), pipe());
	public static final Block PIPE_VALVE = add("pipe_valve", p -> new InlineConduitBlock(p, ConduitKind.FLUID, true), pipe());
	public static final Block PIPE_WINDOW = add("pipe_window", p -> new InlineConduitBlock(p, ConduitKind.FLUID, false), pipe());
	public static final Block AIR_TUBE = add("air_tube", p -> new ConduitBlock(p, ConduitKind.GAS, false), pipe());
	public static final Block AIR_TUBE_VALVE = add("air_tube_valve", p -> new InlineConduitBlock(p, ConduitKind.GAS, true), pipe());
	public static final Block AIR_TUBE_WINDOW = add("air_tube_window", p -> new InlineConduitBlock(p, ConduitKind.GAS, false), pipe());

	public static final BlockEntityType<ConduitBlockEntity> CONDUIT_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Aquamarine.id("conduit"), new BlockEntityType<>(ConduitBlockEntity::new, Set.copyOf(BLOCKS)));

	public static final Item BRASS_INGOT = ModItems.register("brass_ingot", Item::new, new Item.Properties());
	public static final Item TIDE_WRENCH = ModItems.register("tide_wrench", TideWrenchItem::new, new Item.Properties().stacksTo(1));

	private static BlockBehaviour.Properties cable() {
		return BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS).strength(1.0F, 6.0F).noOcclusion();
	}

	private static BlockBehaviour.Properties pipe() {
		return BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS).strength(1.5F, 6.0F).sound(SoundType.COPPER).noOcclusion();
	}

	private static Block add(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Aquamarine.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
		BLOCKS.add(block);
		return block;
	}

	/** Os pedaços de rede, na ordem da aba criativa. */
	public static List<Block> blocks() {
		return Collections.unmodifiableList(BLOCKS);
	}

	public static void register() {
		BLOCKS.forEach(block -> ModItems.registerBlock(block, new Item.Properties()));
		FluidStorage.SIDED.registerForBlockEntity(ConduitBlockEntity::fluidStorage, CONDUIT_ENTITY);
		EnergyStorage.SIDED.registerForBlockEntity(ConduitBlockEntity::energyStorage, CONDUIT_ENTITY);
		FluidVariantAttributes.register(COMPRESSED_AIR, new FluidVariantAttributeHandler() {
			@Override
			public Component getName(FluidVariant variant) {
				return Component.translatable("fluid.aquamarine.compressed_air");
			}

			@Override
			public boolean isLighterThanAir(FluidVariant variant) {
				return true;
			}
		});
	}

	private ModNetworks() {}
}
