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

public final class ModHydraulics {
    public static final Block TIDE_PUMP = block("tide_pump", HydraulicBlock.Kind.PUMP,
        Shapes.or(Block.box(1,0,1,15,3,15), Block.box(3,3,4,13,13,13), Block.box(1,4,1,15,15,5)));
    public static final Block WATER_TANK = block("water_tank", HydraulicBlock.Kind.TANK,
        Shapes.or(Block.box(1,0,1,15,2,15), Block.box(2,2,2,14,14,14), Block.box(1,14,1,15,16,15)));
    public static final BlockEntityType<HydraulicBlockEntity> ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Aquamarine.id("hydraulic"), new BlockEntityType<>(HydraulicBlockEntity::new, Set.of(TIDE_PUMP, WATER_TANK)));
    private static Block block(String id, HydraulicBlock.Kind kind, VoxelShape shape) {
        return Registry.register(BuiltInRegistries.BLOCK, Aquamarine.id(id), new HydraulicBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()
                .setId(ResourceKey.create(Registries.BLOCK, Aquamarine.id(id))), shape, kind));
    }
    public static void register() {
        ModItems.registerBlock(TIDE_PUMP, new Item.Properties());
        ModItems.registerBlock(WATER_TANK, new Item.Properties());
        FluidStorage.SIDED.registerForBlockEntity(HydraulicBlockEntity::fluidPort, ENTITY);
        EnergyStorage.SIDED.registerForBlockEntity((be, side) -> be.kind() == HydraulicBlock.Kind.PUMP && be.enabled(side) ? be.energy : null, ENTITY);
    }
    private ModHydraulics() {}
}

