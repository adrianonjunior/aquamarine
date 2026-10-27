package com.aquamarine.entity;

import com.aquamarine.Aquamarine;
import com.aquamarine.ModTags;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

/** Tipos de entidade do mod. Renderizadores ficam em AquamarineClient. */
public final class ModEntities {
	private static final ResourceKey<EntityType<?>> BOSS_DUMMY_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("boss_dummy"));

	/** Só para testes (barra de vida); sem ovo e sem surgimento natural. */
	public static final EntityType<BossDummy> BOSS_DUMMY = Registry.register(BuiltInRegistries.ENTITY_TYPE, BOSS_DUMMY_KEY,
		EntityType.Builder.of(BossDummy::new, MobCategory.MISC).sized(2.0F, 2.0F).fireImmune().build(BOSS_DUMMY_KEY));

	private static final ResourceKey<EntityType<?>> KRAKEN_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("kraken"));

	/** O Kraken (docs/02-kraken.md). Caixa provisória; o tamanho final vem com o modelo. */
	public static final EntityType<KrakenEntity> KRAKEN = Registry.register(BuiltInRegistries.ENTITY_TYPE, KRAKEN_KEY,
		EntityType.Builder.of(KrakenEntity::new, MobCategory.MONSTER).sized(36.0F, 24.0F).fireImmune().clientTrackingRange(48).build(KRAKEN_KEY));
	private static final ResourceKey<EntityType<?>> KRAKEN_VAMPIRE_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("kraken_vampire"));
	private static final ResourceKey<EntityType<?>> KRAKEN_AMMONITE_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("kraken_ammonite"));
	/**
	 * Fase 6: os outros dois desenhos novos do Kraken, com a mesma luta (docs/11-fase6.md, 6A). O {@link #KRAKEN} é a
	 * v3a, Lula-colosso; estes são a v3b, Vampiro-abissal, e a v3c, Amonita-colosso, e só nascem pelo /summon.
	 */
	public static final EntityType<KrakenEntity> KRAKEN_VAMPIRE = Registry.register(BuiltInRegistries.ENTITY_TYPE, KRAKEN_VAMPIRE_KEY,
		EntityType.Builder.of(KrakenEntity::new, MobCategory.MONSTER).sized(36.0F, 24.0F).fireImmune().clientTrackingRange(48)
			.build(KRAKEN_VAMPIRE_KEY));
	public static final EntityType<KrakenEntity> KRAKEN_AMMONITE = Registry.register(BuiltInRegistries.ENTITY_TYPE, KRAKEN_AMMONITE_KEY,
		EntityType.Builder.of(KrakenEntity::new, MobCategory.MONSTER).sized(36.0F, 24.0F).fireImmune().clientTrackingRange(48)
			.build(KRAKEN_AMMONITE_KEY));

	private static final ResourceKey<EntityType<?>> TENTACLE_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("tentacle"));
	private static final ResourceKey<EntityType<?>> YOUNG_KRAKEN_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("young_kraken"));

	/** Tentáculo do Kraken (60 de vida). Caixa provisória até o modelo da 2D-M. */
	public static final EntityType<TentacleEntity> TENTACLE = Registry.register(BuiltInRegistries.ENTITY_TYPE, TENTACLE_KEY,
		EntityType.Builder.of(TentacleEntity::new, MobCategory.MONSTER).sized(2.0F, 9.0F).fireImmune().clientTrackingRange(16).build(TENTACLE_KEY));

	/** Cria de Kraken (8 de vida). Caixa provisória até o modelo da 2D-M. */
	public static final EntityType<YoungKrakenEntity> YOUNG_KRAKEN = Registry.register(BuiltInRegistries.ENTITY_TYPE, YOUNG_KRAKEN_KEY,
		EntityType.Builder.of(YoungKrakenEntity::new, MobCategory.MONSTER).sized(1.0F, 1.3F).fireImmune().clientTrackingRange(10).build(YOUNG_KRAKEN_KEY));

	private static final ResourceKey<EntityType<?>> KRAKEN_EGG_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("kraken_egg"));

	/** Ovo do Kraken (aviso do ataque Crias). */
	public static final EntityType<KrakenEggEntity> KRAKEN_EGG = Registry.register(BuiltInRegistries.ENTITY_TYPE, KRAKEN_EGG_KEY,
		EntityType.Builder.of(KrakenEggEntity::new, MobCategory.MISC).sized(0.8F, 1.0F).fireImmune().clientTrackingRange(10).build(KRAKEN_EGG_KEY));

	private static final ResourceKey<EntityType<?>> MORAY_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("moray"));
	private static final ResourceKey<EntityType<?>> MORAY_PART_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("moray_part"));
	private static final ResourceKey<EntityType<?>> WHALE_PART_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("whale_part"));

	/** Moréia Tempestuosa (docs/08-fase3.md): a caixa é a cabeça; o corpo são as partes abaixo. */
	public static final EntityType<MorayEntity> MORAY = Registry.register(BuiltInRegistries.ENTITY_TYPE, MORAY_KEY,
		EntityType.Builder.of(MorayEntity::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(1.6F, 1.6F).eyeHeight(0.8F)
			.clientTrackingRange(10).build(MORAY_KEY));

	/** Trecho do corpo da Moréia (caixa de colisão); não é salvo nem invocável. */
	public static final EntityType<MorayPartEntity> MORAY_PART = Registry.register(BuiltInRegistries.ENTITY_TYPE, MORAY_PART_KEY,
		EntityType.Builder.<MorayPartEntity>of(MorayPartEntity::new, MobCategory.MISC).sized(1.2F, 1.2F).noSave().noSummon()
			.clientTrackingRange(10).updateInterval(2).build(MORAY_PART_KEY));

	/** Cabeça, fim do corpo e rabo da Baleia-de-Nereia e do Cachalote (Fase 6, 6B1). */
	public static final EntityType<WhalePartEntity> WHALE_PART = Registry.register(BuiltInRegistries.ENTITY_TYPE, WHALE_PART_KEY,
		EntityType.Builder.<WhalePartEntity>of(WhalePartEntity::new, MobCategory.MISC).sized(4.0F, 4.0F).noSave().noSummon()
			.clientTrackingRange(10).updateInterval(2).build(WHALE_PART_KEY));

	private static final ResourceKey<EntityType<?>> WHALE_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("whale"));
	private static final ResourceKey<EntityType<?>> SPERM_WHALE_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("sperm_whale"));

	/** Baleia-de-Nereia (docs/08-fase3.md): montaria de 3 jogadores. A caixa cobre o tronco, não as nadadeiras. */
	public static final EntityType<WhaleEntity> WHALE = Registry.register(BuiltInRegistries.ENTITY_TYPE, WHALE_KEY,
		EntityType.Builder.of(WhaleEntity::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(6.0F, 5.0F).eyeHeight(3.0F)
			.clientTrackingRange(12).build(WHALE_KEY));

	/** Cachalote (docs/08-fase3.md): neutro, onda sonora quando atacado. */
	public static final EntityType<SpermWhaleEntity> SPERM_WHALE = Registry.register(BuiltInRegistries.ENTITY_TYPE, SPERM_WHALE_KEY,
		EntityType.Builder.of(SpermWhaleEntity::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(5.5F, 5.0F).eyeHeight(3.0F)
			.clientTrackingRange(12).build(SPERM_WHALE_KEY));

	private static final ResourceKey<EntityType<?>> CAVITATION_PULSE_KEY =
		ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("cavitation_pulse"));
	/** Pulso do Cavitador de Neutrônio (Fase 4G): sem modelo, só o rastro de partículas. */
	public static final EntityType<CavitationPulseEntity> CAVITATION_PULSE = Registry.register(BuiltInRegistries.ENTITY_TYPE,
		CAVITATION_PULSE_KEY, EntityType.Builder.<CavitationPulseEntity>of(CavitationPulseEntity::new, MobCategory.MISC).sized(0.5F, 0.5F)
			.clientTrackingRange(8).updateInterval(1).noSave().build(CAVITATION_PULSE_KEY));

	public static void register() {
		FabricDefaultAttributeRegistry.register(CRAB, CrabEntity.createAttributes());
		SpawnPlacements.register(CRAB, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CrabEntity::checkSpawn);
		BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_BEACH), MobCategory.CREATURE, CRAB, 3, 1, 2);
		FabricDefaultAttributeRegistry.register(KRAKEN, KrakenEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(KRAKEN_VAMPIRE, KrakenEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(KRAKEN_AMMONITE, KrakenEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(BOSS_DUMMY, BossDummy.createAttributes());
		FabricDefaultAttributeRegistry.register(TENTACLE, TentacleEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(YOUNG_KRAKEN, YoungKrakenEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(MORAY, MorayEntity.createAttributes());
		SpawnPlacements.register(MORAY, SpawnPlacementTypes.IN_WATER, Heightmap.Types.OCEAN_FLOOR, MorayEntity::checkSpawn);
		// Densidade do oceano (docs/00-decisoes.md, "Criaturas do oceano"): a regra de cada criatura limita quantas há.
		// Moréia e baleias usam a categoria das criaturas aquáticas profundas: vagas próprias (5 por jogador), quase
		// livres no oceano (a lula-brilhante só nasce bem fundo); assim não disputam com náutilos, lulas, golfinhos
		// nem com os monstros das cavernas embaixo do mar.
		BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_OCEAN), MobCategory.UNDERGROUND_WATER_CREATURE, MORAY, 6, 1, 1);
		FabricDefaultAttributeRegistry.register(WHALE, WhaleEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(SPERM_WHALE, SpermWhaleEntity.createAttributes());
		SpawnPlacements.register(WHALE, SpawnPlacementTypes.IN_WATER, Heightmap.Types.OCEAN_FLOOR, AbstractWhaleEntity::checkSpawn);
		SpawnPlacements.register(SPERM_WHALE, SpawnPlacementTypes.IN_WATER, Heightmap.Types.OCEAN_FLOOR, AbstractWhaleEntity::checkSpawn);
		BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_DEEP_OCEAN), MobCategory.UNDERGROUND_WATER_CREATURE, WHALE, 8, 1, 3);
		BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_OCEAN).and(BiomeSelectors.tag(BiomeTags.IS_DEEP_OCEAN).negate()),
			MobCategory.UNDERGROUND_WATER_CREATURE, WHALE, 3, 1, 2);
		BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.MORAY_TRENCHES), MobCategory.UNDERGROUND_WATER_CREATURE, SPERM_WHALE, 3, 1, 1);
	}

	private static final ResourceKey<EntityType<?>> CRAB_KEY = ResourceKey.create(Registries.ENTITY_TYPE, Aquamarine.id("crab"));
	public static final EntityType<CrabEntity> CRAB = Registry.register(BuiltInRegistries.ENTITY_TYPE, CRAB_KEY,
		EntityType.Builder.of(CrabEntity::new, MobCategory.CREATURE).sized(.85F, .45F).eyeHeight(.35F).clientTrackingRange(8).build(CRAB_KEY));

	private ModEntities() {}
}
