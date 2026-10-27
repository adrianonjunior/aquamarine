package com.aquamarine.energy;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import team.reborn.energy.api.EnergyStorage;

/**
 * Energia aquática (Fase 6, 6E; docs/11-fase6.md): três geradores, o Cabo Aquático e o Coletor de Neutrônio, com a
 * energia da Team Reborn Energy. Modelos, texturas e dados saem de tools/gen_phase6_energy.py.
 */
public final class ModEnergy {
	private static final List<Block> BLOCKS = new ArrayList<>();
	/** Gerador de Fenda animado (0.6.1, 7B), frente ao norte: base, câmara, pistões, balancim, chaminé, mostrador e válvula. */
	private static final VoxelShape VENT_SHAPE = Shapes.or(Block.box(0.5, 0, 0.5, 15.5, 6, 15.5), Block.box(3.5, 6, 2.5, 12.5, 13.5, 11.5),
		Block.box(12, 6, 5, 15.5, 14.5, 8.5), Block.box(0.5, 6, 5, 4, 14.5, 8.5), Block.box(1.5, 12.5, 6, 14.5, 15.5, 7.5),
		Block.box(12, 6, 11, 15.5, 15.5, 14.5), Block.box(10.75, 6, 0.75, 15.75, 12, 1.75), Block.box(0, 6, 0, 4, 8, 4));

	public static final Block CURRENT_TURBINE = add("current_turbine",
		p -> new WaterGeneratorBlock(p, Block.box(1, 0, 1, 15, 16, 15), WaterGeneratorBlock.Kind.TURBINE),
		BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion());
	public static final Block VENT_GENERATOR = add("vent_generator",
		p -> new WaterGeneratorBlock(p, VENT_SHAPE, WaterGeneratorBlock.Kind.VENT),
		BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE).noOcclusion()
			.lightLevel(state -> state.getValue(WaterGeneratorBlock.ACTIVE) ? 10 : 0));
	public static final Block WAVE_BUOY = add("wave_buoy",
		p -> new WaterGeneratorBlock(p, Block.box(3, 0, 3, 13, 16, 13), WaterGeneratorBlock.Kind.BUOY),
		BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()
			.lightLevel(state -> state.getValue(WaterGeneratorBlock.ACTIVE) ? 8 : 0));
	public static final Block AQUATIC_CABLE = add("aquatic_cable", AquaticCableBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS).strength(1.0F, 6.0F).noOcclusion());
	public static final Block NEUTRONIUM_COLLECTOR = add("neutronium_collector",
		p -> new NeutroniumCollectorBlock(p, Block.box(0, 0, 0, 16, 16, 16)),
		BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK).noOcclusion()
			.lightLevel(state -> state.getValue(WaterGeneratorBlock.ACTIVE) ? 12 : 3));

	public static final BlockEntityType<WaterGeneratorBlockEntity> WATER_GENERATOR_ENTITY = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE, Aquamarine.id("water_generator"),
		new BlockEntityType<>(WaterGeneratorBlockEntity::new, Set.of(CURRENT_TURBINE, VENT_GENERATOR, WAVE_BUOY)));
	public static final BlockEntityType<AquaticCableBlockEntity> CABLE_ENTITY = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE, Aquamarine.id("aquatic_cable"),
		new BlockEntityType<>(AquaticCableBlockEntity::new, Set.of(AQUATIC_CABLE)));
	public static final BlockEntityType<NeutroniumCollectorBlockEntity> COLLECTOR_ENTITY = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE, Aquamarine.id("neutronium_collector"),
		new BlockEntityType<>(NeutroniumCollectorBlockEntity::new, Set.of(NEUTRONIUM_COLLECTOR)));

	private static Block add(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Aquamarine.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
		BLOCKS.add(block);
		return block;
	}

	/** Os blocos de energia, na ordem da aba criativa. */
	public static List<Block> blocks() {
		return Collections.unmodifiableList(BLOCKS);
	}

	public static void register() {
		BLOCKS.forEach(block -> ModItems.registerBlock(block, new Item.Properties()));
		EnergyStorage.SIDED.registerForBlockEntity((be, side) -> be.energy, WATER_GENERATOR_ENTITY);
		EnergyStorage.SIDED.registerForBlockEntity((be, side) -> be.energy, CABLE_ENTITY);
		EnergyStorage.SIDED.registerForBlockEntity((be, side) -> be.energy, COLLECTOR_ENTITY);
	}

	private ModEnergy() {}
}
