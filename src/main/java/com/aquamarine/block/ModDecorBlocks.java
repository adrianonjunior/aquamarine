package com.aquamarine.block;

import com.aquamarine.Aquamarine;
import com.aquamarine.item.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Construção e decoração da Fase 6 (6H, docs/11-fase6.md): família do Aurium e peças de navio, naufrágio e aquário.
 * GERADO por tools/gen_phase6_building.py: não edite à mão (mude a lista no gerador e rode de novo).
 */
public final class ModDecorBlocks {
	private static final List<Block> BUILDING = new ArrayList<>();
	private static final List<Block> DECOR = new ArrayList<>();

	public static final Block AURIUM_TILES = add(BUILDING, "aurium_tiles", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES).mapColor(MapColor.QUARTZ));
	public static final Block AURIUM_TILE_STAIRS = add(BUILDING, "aurium_tile_stairs", p -> new StairBlock(AURIUM_TILES.defaultBlockState(), p) {},
		BlockBehaviour.Properties.ofFullCopy(AURIUM_TILES));
	public static final Block AURIUM_TILE_SLAB = add(BUILDING, "aurium_tile_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(AURIUM_TILES));
	public static final Block AURIUM_TILE_WALL = add(BUILDING, "aurium_tile_wall", WallBlock::new,
		BlockBehaviour.Properties.ofFullCopy(AURIUM_TILES));
	public static final Block CHISELED_AURIUM_TILES = add(BUILDING, "chiseled_aurium_tiles", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES).mapColor(MapColor.QUARTZ));
	public static final Block AURIUM_LAMP = add(BUILDING, "aurium_lamp", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.SEA_LANTERN).mapColor(MapColor.QUARTZ).lightLevel(state -> 15));
	public static final Block SHIP_LANTERN = add(DECOR, "ship_lantern", p -> new FacingShapeBlock(p, Block.box(4, 0, 4, 12, 15, 12)),
		BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 15).noOcclusion());
	public static final Block SHIP_WHEEL = add(DECOR, "ship_wheel", p -> new FacingShapeBlock(p, Block.box(0, 0, 12.5, 16, 16, 16)),
		BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion());
	public static final Block DECORATIVE_ANCHOR = add(DECOR, "decorative_anchor", p -> new FacingShapeBlock(p, Block.box(1, 0, 6, 15, 16, 10)),
		BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS).noOcclusion());
	public static final Block ROPE_COIL = add(DECOR, "rope_coil", p -> new FacingShapeBlock(p, Block.box(3, 0, 3, 13, 6, 13)),
		BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).noOcclusion());
	public static final Block CARGO_BARREL = add(DECOR, "cargo_barrel", p -> new FacingShapeBlock(p, Block.box(2, 0, 2, 14, 14, 14)),
		BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).noOcclusion());
	public static final Block FISHING_NET = add(DECOR, "fishing_net", p -> new FacingShapeBlock(p, Block.box(0, 0, 0, 16, 1, 16)),
		BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_CARPET).noCollision().noOcclusion());
	public static final Block AQUARIUM = add(DECOR, "aquarium", p -> new FacingShapeBlock(p, Block.box(0, 0, 0, 16, 15, 16)),
		BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).lightLevel(state -> 8).noOcclusion());
	public static final Block KRAKEN_STATUE = add(DECOR, "kraken_statue", p -> new FacingShapeBlock(p, Block.box(1, 0, 1, 15, 16, 15)),
		BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).lightLevel(state -> 4).noOcclusion());

	private static Block add(List<Block> group, String name, Function<BlockBehaviour.Properties, Block> factory,
		BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Aquamarine.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
		group.add(block);
		return block;
	}

	/** Blocos de construção (aba de construção), na ordem da aba. */
	public static List<Block> building() {
		return Collections.unmodifiableList(BUILDING);
	}

	/** Peças de decoração (aba de blocos funcionais), na ordem da aba. */
	public static List<Block> decor() {
		return Collections.unmodifiableList(DECOR);
	}

	public static void register() {
		// Os campos estáticos registram os blocos; aqui entram os itens, depois dos do ModItems.
		BUILDING.forEach(block -> ModItems.registerBlock(block, new Item.Properties()));
		DECOR.forEach(block -> ModItems.registerBlock(block, new Item.Properties()));
	}

	private ModDecorBlocks() {}
}
