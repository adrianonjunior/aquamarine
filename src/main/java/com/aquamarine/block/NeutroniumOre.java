package com.aquamarine.block;

import com.aquamarine.Aquamarine;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Minério de Neutrônio no mundo (docs/10-fase4.md, §8): só sob o leito dos oceanos profundos, na ardósia entre
 * y −60 e −32, em veios pequenos que nunca ficam expostos ao ar (data/aquamarine/worldgen/.../ore_neutronium.json).
 */
public final class NeutroniumOre {
	public static final ResourceKey<PlacedFeature> PLACED = ResourceKey.create(Registries.PLACED_FEATURE, Aquamarine.id("ore_neutronium"));

	public static void register() {
		BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_DEEP_OCEAN), GenerationStep.Decoration.UNDERGROUND_ORES, PLACED);
	}

	private NeutroniumOre() {}
}
