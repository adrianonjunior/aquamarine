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
 * Minério de Aurium no mundo (Fase 6, 6D1): veios pequenos na ardósia sob o leito dos oceanos profundos, entre y −48
 * e 0 (data/aquamarine/worldgen/.../ore_aurium.json). Mais comum que o Neutrônio; é a fonte no sobrevivência, a mesma
 * solução proposta para o Minério Abissal (docs/resumo.md, "Plano", etapa 1).
 */
public final class AuriumOre {
	public static final ResourceKey<PlacedFeature> PLACED = ResourceKey.create(Registries.PLACED_FEATURE, Aquamarine.id("ore_aurium"));

	public static void register() {
		BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_DEEP_OCEAN), GenerationStep.Decoration.UNDERGROUND_ORES, PLACED);
	}

	private AuriumOre() {}
}
