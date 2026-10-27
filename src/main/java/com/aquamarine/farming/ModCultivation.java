package com.aquamarine.farming;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.ModItems;
import java.util.Set;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.shapes.Shapes;

/** Ostra e linha de cultivo (0.6.1, 7G). IDs públicos usados pelas receitas das máquinas. */
public final class ModCultivation {
    public static final Item OYSTER_PEARL = ModItems.register("oyster_pearl", Item::new, new Item.Properties());
    private static final ResourceKey<EntityType<?>> OYSTER_KEY = ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("oyster"));
    public static final EntityType<OysterEntity> OYSTER = Registry.register(BuiltInRegistries.ENTITY_TYPE, OYSTER_KEY,
        EntityType.Builder.of(OysterEntity::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).eyeHeight(0.2F)
            .clientTrackingRange(8).build(OYSTER_KEY));

    public static final Block CULTIVATION_JAR = block("cultivation_jar", CultivationBlock.Kind.JAR,
        Shapes.or(Block.box(3, 0, 3, 13, 2, 13), Block.box(2, 2, 2, 14, 11, 14),
            Block.box(4, 11, 4, 12, 14, 12), Block.box(5, 14, 5, 11, 16, 11)));
    public static final Block CULTIVATION_ROPE = block("cultivation_rope", CultivationBlock.Kind.ROPE, Block.box(0, 11, 7, 16, 13, 9));
    public static final Block CULTIVATION_BUOY = block("cultivation_buoy", CultivationBlock.Kind.BUOY,
        Shapes.or(Block.box(2, 1, 2, 14, 8, 14), Block.box(7, 8, 7, 9, 16, 9)));
    public static final BlockEntityType<CultivationJarBlockEntity> JAR_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Aquamarine.id("cultivation_jar"), new BlockEntityType<>(CultivationJarBlockEntity::new, Set.of(CULTIVATION_JAR)));

    private static Block block(String id, CultivationBlock.Kind kind, net.minecraft.world.phys.shapes.VoxelShape shape) {
        var key = ResourceKey.create(Registries.BLOCK, Aquamarine.id(id));
        return Registry.register(BuiltInRegistries.BLOCK, key, new CultivationBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).strength(1.5F).noOcclusion().setId(key), shape, kind));
    }

    public static void register() {
        ModItems.registerBlock(CULTIVATION_JAR, new Item.Properties());
        ModItems.registerBlock(CULTIVATION_ROPE, new Item.Properties());
        ModItems.registerBlock(CULTIVATION_BUOY, new Item.Properties());
        FabricDefaultAttributeRegistry.register(OYSTER, OysterEntity.createAttributes());
        SpawnPlacements.register(OYSTER, SpawnPlacementTypes.IN_WATER, Heightmap.Types.OCEAN_FLOOR, OysterEntity::checkSpawn);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN),
            MobCategory.WATER_AMBIENT, OYSTER, 5, 2, 4);
    }

    private ModCultivation() {}
}

