package com.aquamarine.block;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Blocos de construção aquáticos e vidros rachados da Fase 4 (docs/10-fase4.md, §6 e §7).
 * GERADO por tools/gen_phase4_building.py: não edite à mão (mude a lista no gerador e rode de novo).
 */
public final class ModBuildingBlocks {
	private static final List<Block> BUILDING = new ArrayList<>();
	private static final List<Block> GLASS = new ArrayList<>();
	private static final Map<Block, Block> CRACKED = new HashMap<>();

	public static final Block TIDESTONE = add(BUILDING, "tidestone", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.WARPED_WART_BLOCK));
	public static final Block TIDESTONE_STAIRS = add(BUILDING, "tidestone_stairs", p -> new StairBlock(TIDESTONE.defaultBlockState(), p) {},
		BlockBehaviour.Properties.ofFullCopy(TIDESTONE));
	public static final Block TIDESTONE_SLAB = add(BUILDING, "tidestone_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(TIDESTONE));
	public static final Block TIDESTONE_WALL = add(BUILDING, "tidestone_wall", WallBlock::new,
		BlockBehaviour.Properties.ofFullCopy(TIDESTONE));
	public static final Block POLISHED_TIDESTONE = add(BUILDING, "polished_tidestone", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.WARPED_WART_BLOCK));
	public static final Block POLISHED_TIDESTONE_STAIRS = add(BUILDING, "polished_tidestone_stairs", p -> new StairBlock(POLISHED_TIDESTONE.defaultBlockState(), p) {},
		BlockBehaviour.Properties.ofFullCopy(POLISHED_TIDESTONE));
	public static final Block POLISHED_TIDESTONE_SLAB = add(BUILDING, "polished_tidestone_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(POLISHED_TIDESTONE));
	public static final Block POLISHED_TIDESTONE_WALL = add(BUILDING, "polished_tidestone_wall", WallBlock::new,
		BlockBehaviour.Properties.ofFullCopy(POLISHED_TIDESTONE));
	public static final Block TIDESTONE_BRICKS = add(BUILDING, "tidestone_bricks", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.WARPED_WART_BLOCK));
	public static final Block TIDESTONE_BRICK_STAIRS = add(BUILDING, "tidestone_brick_stairs", p -> new StairBlock(TIDESTONE_BRICKS.defaultBlockState(), p) {},
		BlockBehaviour.Properties.ofFullCopy(TIDESTONE_BRICKS));
	public static final Block TIDESTONE_BRICK_SLAB = add(BUILDING, "tidestone_brick_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(TIDESTONE_BRICKS));
	public static final Block TIDESTONE_BRICK_WALL = add(BUILDING, "tidestone_brick_wall", WallBlock::new,
		BlockBehaviour.Properties.ofFullCopy(TIDESTONE_BRICKS));
	public static final Block CRACKED_TIDESTONE_BRICKS = add(BUILDING, "cracked_tidestone_bricks", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.WARPED_WART_BLOCK));
	public static final Block CHISELED_TIDESTONE = add(BUILDING, "chiseled_tidestone", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.WARPED_WART_BLOCK));
	public static final Block TIDESTONE_PILLAR = add(BUILDING, "tidestone_pillar", RotatedPillarBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.WARPED_WART_BLOCK));
	public static final Block PETRIFIED_CORAL_BRICKS = add(BUILDING, "petrified_coral_bricks", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK));
	public static final Block PETRIFIED_CORAL_BRICK_STAIRS = add(BUILDING, "petrified_coral_brick_stairs", p -> new StairBlock(PETRIFIED_CORAL_BRICKS.defaultBlockState(), p) {},
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK));
	public static final Block PETRIFIED_CORAL_BRICK_SLAB = add(BUILDING, "petrified_coral_brick_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK));
	public static final Block PETRIFIED_CORAL_BRICK_WALL = add(BUILDING, "petrified_coral_brick_wall", WallBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK));
	public static final Block PETRIFIED_CORAL_TILES = add(BUILDING, "petrified_coral_tiles", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK));
	public static final Block PETRIFIED_CORAL_TILE_STAIRS = add(BUILDING, "petrified_coral_tile_stairs", p -> new StairBlock(PETRIFIED_CORAL_TILES.defaultBlockState(), p) {},
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK));
	public static final Block PETRIFIED_CORAL_TILE_SLAB = add(BUILDING, "petrified_coral_tile_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK));
	public static final Block SHELL_SHINGLES = add(BUILDING, "shell_shingles", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.TERRACOTTA_WHITE));
	public static final Block SHELL_SHINGLE_STAIRS = add(BUILDING, "shell_shingle_stairs", p -> new StairBlock(SHELL_SHINGLES.defaultBlockState(), p) {},
		BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.TERRACOTTA_WHITE));
	public static final Block SHELL_SHINGLE_SLAB = add(BUILDING, "shell_shingle_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.TERRACOTTA_WHITE));
	public static final Block ABYSSAL_TILES = add(BUILDING, "abyssal_tiles", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES));
	public static final Block ABYSSAL_TILE_STAIRS = add(BUILDING, "abyssal_tile_stairs", p -> new StairBlock(ABYSSAL_TILES.defaultBlockState(), p) {},
		BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES));
	public static final Block ABYSSAL_TILE_SLAB = add(BUILDING, "abyssal_tile_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES));
	public static final Block ABYSSAL_TILE_WALL = add(BUILDING, "abyssal_tile_wall", WallBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES));
	public static final Block CHISELED_ABYSSAL_TILES = add(BUILDING, "chiseled_abyssal_tiles", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES));
	public static final Block SEA_GLASS = add(BUILDING, "sea_glass", p -> new TransparentBlock(p) {},
		BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
	public static final Block SEA_GLASS_PANE = add(BUILDING, "sea_glass_pane", p -> new IronBarsBlock(p) {},
		BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));
	public static final Block JELLYFISH_LANTERN = add(BUILDING, "jellyfish_lantern", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.SEA_LANTERN).lightLevel(state -> 15).mapColor(MapColor.COLOR_PURPLE));
	public static final Block PEARL_LAMP = add(BUILDING, "pearl_lamp", LanternBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 15));
	public static final Block NAVAL_GRATE = add(BUILDING, "naval_grate", p -> new WaterloggedTransparentBlock(p) {},
		BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS));
	public static final Block CRACKED_GLASS = add(GLASS, "cracked_glass", p -> new TransparentBlock(p) {},
		BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
	public static final Block CRACKED_GLASS_PANE = add(GLASS, "cracked_glass_pane", p -> new IronBarsBlock(p) {},
		BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));
	public static final Block CRACKED_TINTED_GLASS = add(GLASS, "cracked_tinted_glass", TintedGlassBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.TINTED_GLASS));
	public static final Block CRACKED_WHITE_STAINED_GLASS = add(GLASS, "cracked_white_stained_glass", p -> new StainedGlassBlock(DyeColor.WHITE, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.WHITE)));
	public static final Block CRACKED_WHITE_STAINED_GLASS_PANE = add(GLASS, "cracked_white_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.WHITE, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.WHITE)));
	public static final Block CRACKED_ORANGE_STAINED_GLASS = add(GLASS, "cracked_orange_stained_glass", p -> new StainedGlassBlock(DyeColor.ORANGE, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.ORANGE)));
	public static final Block CRACKED_ORANGE_STAINED_GLASS_PANE = add(GLASS, "cracked_orange_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.ORANGE, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.ORANGE)));
	public static final Block CRACKED_MAGENTA_STAINED_GLASS = add(GLASS, "cracked_magenta_stained_glass", p -> new StainedGlassBlock(DyeColor.MAGENTA, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.MAGENTA)));
	public static final Block CRACKED_MAGENTA_STAINED_GLASS_PANE = add(GLASS, "cracked_magenta_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.MAGENTA, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.MAGENTA)));
	public static final Block CRACKED_LIGHT_BLUE_STAINED_GLASS = add(GLASS, "cracked_light_blue_stained_glass", p -> new StainedGlassBlock(DyeColor.LIGHT_BLUE, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.LIGHT_BLUE)));
	public static final Block CRACKED_LIGHT_BLUE_STAINED_GLASS_PANE = add(GLASS, "cracked_light_blue_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.LIGHT_BLUE, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.LIGHT_BLUE)));
	public static final Block CRACKED_YELLOW_STAINED_GLASS = add(GLASS, "cracked_yellow_stained_glass", p -> new StainedGlassBlock(DyeColor.YELLOW, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.YELLOW)));
	public static final Block CRACKED_YELLOW_STAINED_GLASS_PANE = add(GLASS, "cracked_yellow_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.YELLOW, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.YELLOW)));
	public static final Block CRACKED_LIME_STAINED_GLASS = add(GLASS, "cracked_lime_stained_glass", p -> new StainedGlassBlock(DyeColor.LIME, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.LIME)));
	public static final Block CRACKED_LIME_STAINED_GLASS_PANE = add(GLASS, "cracked_lime_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.LIME, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.LIME)));
	public static final Block CRACKED_PINK_STAINED_GLASS = add(GLASS, "cracked_pink_stained_glass", p -> new StainedGlassBlock(DyeColor.PINK, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.PINK)));
	public static final Block CRACKED_PINK_STAINED_GLASS_PANE = add(GLASS, "cracked_pink_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.PINK, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.PINK)));
	public static final Block CRACKED_GRAY_STAINED_GLASS = add(GLASS, "cracked_gray_stained_glass", p -> new StainedGlassBlock(DyeColor.GRAY, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.GRAY)));
	public static final Block CRACKED_GRAY_STAINED_GLASS_PANE = add(GLASS, "cracked_gray_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.GRAY, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.GRAY)));
	public static final Block CRACKED_LIGHT_GRAY_STAINED_GLASS = add(GLASS, "cracked_light_gray_stained_glass", p -> new StainedGlassBlock(DyeColor.LIGHT_GRAY, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.LIGHT_GRAY)));
	public static final Block CRACKED_LIGHT_GRAY_STAINED_GLASS_PANE = add(GLASS, "cracked_light_gray_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.LIGHT_GRAY, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.LIGHT_GRAY)));
	public static final Block CRACKED_CYAN_STAINED_GLASS = add(GLASS, "cracked_cyan_stained_glass", p -> new StainedGlassBlock(DyeColor.CYAN, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.CYAN)));
	public static final Block CRACKED_CYAN_STAINED_GLASS_PANE = add(GLASS, "cracked_cyan_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.CYAN, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.CYAN)));
	public static final Block CRACKED_PURPLE_STAINED_GLASS = add(GLASS, "cracked_purple_stained_glass", p -> new StainedGlassBlock(DyeColor.PURPLE, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.PURPLE)));
	public static final Block CRACKED_PURPLE_STAINED_GLASS_PANE = add(GLASS, "cracked_purple_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.PURPLE, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.PURPLE)));
	public static final Block CRACKED_BLUE_STAINED_GLASS = add(GLASS, "cracked_blue_stained_glass", p -> new StainedGlassBlock(DyeColor.BLUE, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.BLUE)));
	public static final Block CRACKED_BLUE_STAINED_GLASS_PANE = add(GLASS, "cracked_blue_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.BLUE, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.BLUE)));
	public static final Block CRACKED_BROWN_STAINED_GLASS = add(GLASS, "cracked_brown_stained_glass", p -> new StainedGlassBlock(DyeColor.BROWN, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.BROWN)));
	public static final Block CRACKED_BROWN_STAINED_GLASS_PANE = add(GLASS, "cracked_brown_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.BROWN, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.BROWN)));
	public static final Block CRACKED_GREEN_STAINED_GLASS = add(GLASS, "cracked_green_stained_glass", p -> new StainedGlassBlock(DyeColor.GREEN, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.GREEN)));
	public static final Block CRACKED_GREEN_STAINED_GLASS_PANE = add(GLASS, "cracked_green_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.GREEN, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.GREEN)));
	public static final Block CRACKED_RED_STAINED_GLASS = add(GLASS, "cracked_red_stained_glass", p -> new StainedGlassBlock(DyeColor.RED, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.RED)));
	public static final Block CRACKED_RED_STAINED_GLASS_PANE = add(GLASS, "cracked_red_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.RED, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.RED)));
	public static final Block CRACKED_BLACK_STAINED_GLASS = add(GLASS, "cracked_black_stained_glass", p -> new StainedGlassBlock(DyeColor.BLACK, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS.pick(DyeColor.BLACK)));
	public static final Block CRACKED_BLACK_STAINED_GLASS_PANE = add(GLASS, "cracked_black_stained_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.BLACK, p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STAINED_GLASS_PANE.pick(DyeColor.BLACK)));

	static {
		CRACKED.put(Blocks.GLASS, CRACKED_GLASS);
		CRACKED.put(Blocks.GLASS_PANE, CRACKED_GLASS_PANE);
		CRACKED.put(Blocks.TINTED_GLASS, CRACKED_TINTED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.WHITE), CRACKED_WHITE_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.WHITE), CRACKED_WHITE_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.ORANGE), CRACKED_ORANGE_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.ORANGE), CRACKED_ORANGE_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.MAGENTA), CRACKED_MAGENTA_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.MAGENTA), CRACKED_MAGENTA_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.LIGHT_BLUE), CRACKED_LIGHT_BLUE_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.LIGHT_BLUE), CRACKED_LIGHT_BLUE_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.YELLOW), CRACKED_YELLOW_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.YELLOW), CRACKED_YELLOW_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.LIME), CRACKED_LIME_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.LIME), CRACKED_LIME_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.PINK), CRACKED_PINK_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.PINK), CRACKED_PINK_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.GRAY), CRACKED_GRAY_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.GRAY), CRACKED_GRAY_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.LIGHT_GRAY), CRACKED_LIGHT_GRAY_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.LIGHT_GRAY), CRACKED_LIGHT_GRAY_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.CYAN), CRACKED_CYAN_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.CYAN), CRACKED_CYAN_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.PURPLE), CRACKED_PURPLE_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.PURPLE), CRACKED_PURPLE_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.BLUE), CRACKED_BLUE_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.BLUE), CRACKED_BLUE_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.BROWN), CRACKED_BROWN_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.BROWN), CRACKED_BROWN_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.GREEN), CRACKED_GREEN_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.GREEN), CRACKED_GREEN_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.RED), CRACKED_RED_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.RED), CRACKED_RED_STAINED_GLASS_PANE);
		CRACKED.put(Blocks.STAINED_GLASS.pick(DyeColor.BLACK), CRACKED_BLACK_STAINED_GLASS);
		CRACKED.put(Blocks.STAINED_GLASS_PANE.pick(DyeColor.BLACK), CRACKED_BLACK_STAINED_GLASS_PANE);
	}

	private static Block add(List<Block> group, String name, Function<BlockBehaviour.Properties, Block> factory,
		BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Aquamarine.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
		group.add(block);
		return block;
	}

	/** A versão rachada de um vidro ou vidraça do jogo, ou {@code null}. */
	public static Block cracked(Block glass) {
		return CRACKED.get(glass);
	}

	/** Blocos de construção, na ordem da aba criativa. */
	public static List<Block> building() {
		return Collections.unmodifiableList(BUILDING);
	}

	/** Vidros e vidraças rachados, na ordem da aba criativa. */
	public static List<Block> glass() {
		return Collections.unmodifiableList(GLASS);
	}

	public static void register() {
		// Os campos estáticos registram os blocos; aqui entram os itens, depois dos do ModItems.
		BUILDING.forEach(block -> ModItems.registerBlock(block, new Item.Properties()));
		GLASS.forEach(block -> ModItems.registerBlock(block, new Item.Properties()));
	}

	private ModBuildingBlocks() {}
}
