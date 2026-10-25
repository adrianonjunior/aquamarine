package com.aquamarine.harbor;

import com.aquamarine.Aquamarine;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

/**
 * Função de saque {@code aquamarine:biome_map}: como o {@code exploration_map} do jogo, mas aponta o bioma
 * mais próximo de uma tag em vez de uma estrutura. Usada no Mapa de Fossas de Moréia do Cartógrafo Náutico
 * (tag {@code #aquamarine_fossa_de_nereia}). Sem bioma no raio, o mapa sai vazio e a troca não aparece.
 */
public class BiomeMapFunction extends LootItemConditionalFunction {
	public static final MapCodec<BiomeMapFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> commonFields(i).and(i.group(
		TagKey.hashedCodec(Registries.BIOME).fieldOf("destination").forGetter(f -> f.destination),
		MapDecorationType.CODEC.optionalFieldOf("decoration", MapDecorationTypes.RED_X).forGetter(f -> f.decoration),
		Codec.BYTE.optionalFieldOf("zoom", (byte) 2).forGetter(f -> f.zoom),
		Codec.INT.optionalFieldOf("search_radius", 3200).forGetter(f -> f.searchRadius)
	)).apply(i, BiomeMapFunction::new));

	private final TagKey<Biome> destination;
	private final Holder<MapDecorationType> decoration;
	private final byte zoom;
	private final int searchRadius;

	private BiomeMapFunction(Optional<Holder<LootItemCondition>> condition, TagKey<Biome> destination,
		Holder<MapDecorationType> decoration, byte zoom, int searchRadius) {
		super(condition);
		this.destination = destination;
		this.decoration = decoration;
		this.zoom = zoom;
		this.searchRadius = searchRadius;
	}

	public static void register() {
		Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, Aquamarine.id("biome_map"), MAP_CODEC);
	}

	@Override
	public MapCodec<BiomeMapFunction> codec() {
		return MAP_CODEC;
	}

	@Override
	public Set<ContextKey<?>> getReferencedContextParams() {
		return Set.of(LootContextParams.ORIGIN);
	}

	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		Vec3 origin = context.getOptional(LootContextParams.ORIGIN);
		if (stack.isEmpty() || origin == null) {
			return stack;
		}
		ServerLevel level = context.getLevel();
		Pair<BlockPos, Holder<Biome>> found = level.findClosestBiome3d(biome -> biome.is(destination), BlockPos.containing(origin),
			searchRadius, 32, 64);
		if (found == null) {
			return stack;
		}
		BlockPos target = found.getFirst();
		MapItem.applyNewSavedData(level, stack, target.getX(), target.getZ(), zoom, true, true);
		MapItem.renderBiomePreviewMap(level, stack);
		MapItemSavedData.addTargetDecoration(stack, target, "+", decoration);
		return stack;
	}
}
