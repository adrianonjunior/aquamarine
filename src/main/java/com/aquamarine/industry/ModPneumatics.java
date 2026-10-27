package com.aquamarine.industry;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.ModItems;
import java.util.Set;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
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

/** Registro do Compressor de Ar e do Pulmão de Ferro (0.6.1, 7F). */
public final class ModPneumatics {
	public static final Block AIR_COMPRESSOR = block("air_compressor", PneumaticBlock.Kind.COMPRESSOR,
		Shapes.or(Block.box(1, 0, 1, 15, 2, 15), Block.box(1, 2, 2.5, 15, 16, 15.5)));
	public static final Block IRON_LUNG = block("iron_lung", PneumaticBlock.Kind.LUNG,
		Shapes.or(Block.box(1, 0, 1, 15, 4, 15), Block.box(0.5, 4, 2.5, 16, 12, 13.5), Block.box(4.5, 12, 6.5, 7.5, 15, 9.5)));
	public static final BlockEntityType<PneumaticBlockEntity> ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Aquamarine.id("pneumatic"), new BlockEntityType<>(PneumaticBlockEntity::new, Set.of(AIR_COMPRESSOR, IRON_LUNG)));

	private static Block block(String id, PneumaticBlock.Kind kind, VoxelShape shape) {
		return Registry.register(BuiltInRegistries.BLOCK, Aquamarine.id(id), new PneumaticBlock(
			BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()
				.setId(ResourceKey.create(Registries.BLOCK, Aquamarine.id(id))), shape, kind));
	}

	public static void register() {
		ModItems.registerBlock(AIR_COMPRESSOR, new Item.Properties());
		ModItems.registerBlock(IRON_LUNG, new Item.Properties());
		FluidStorage.SIDED.registerForBlockEntity(PneumaticBlockEntity::fluidPort, ENTITY);
		EnergyStorage.SIDED.registerForBlockEntity(PneumaticBlockEntity::energyPort, ENTITY);
	}

	private ModPneumatics() {}
}
