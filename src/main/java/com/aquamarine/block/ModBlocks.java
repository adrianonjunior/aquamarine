package com.aquamarine.block;

import com.aquamarine.Aquamarine;

import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Blocos do mod. Os itens de bloco são registrados em {@link com.aquamarine.item.ModItems}.
 */
public final class ModBlocks {
	/** Minério Abissal. Não gera no mundo nesta versão: só a Cidade Perdida o terá. */
	public static final Block ABYSSAL_ORE = register("abyssal_ore",
		p -> new DropExperienceBlock(UniformInt.of(3, 7), p),
		BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops().strength(5.0F, 9.0F).sound(SoundType.DEEPSLATE));

	public static final Block ABYSSAL_BLOCK = register("abyssal_block", Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BELL)
			.requiresCorrectToolForDrops().strength(6.0F, 12.0F).sound(SoundType.METAL));

	/** Bancada das receitas Abissais e dos itens da luta. Brilha de leve, como coral vivo. */
	public static final Block ABYSSAL_FORGE = register("abyssal_forge", AbyssalForgeBlock::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundType.DEEPSLATE_BRICKS).lightLevel(state -> 7));

	/** Altar do Abismo: onde o Chamariz Abissal invoca o Kraken. Só na Cidade Perdida (e no criativo). */
	public static final Block ABYSSAL_ALTAR = register("abyssal_altar", AbyssalAltarBlock::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM)
			.strength(-1.0F, 3600000.0F).sound(SoundType.DEEPSLATE_TILES).lightLevel(state -> 6).noOcclusion());

	/** Plataforma da Boia de Âncora: dura 90 s e zera a Pressão de quem pisa (docs/05-itens.md). */
	public static final Block ANCHOR_BUOY = register("anchor_buoy", AnchorBuoyBlock::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASS)
			.strength(0.5F, 3600000.0F).sound(SoundType.WOOD).noLootTable().noOcclusion()
			.lightLevel(state -> state.getValue(AnchorBuoyBlock.CENTER) ? 10 : 0));
	// Formatos dos modelos 3D da Fase 4 (frente para o norte; FacingShapeBlock gira para as outras direções).
	private static final VoxelShape TROPHY_SHAPE = Shapes.or(Block.box(1, 0, 1, 15, 2, 15), Block.box(3, 2, 3, 13, 4, 13),
		Block.box(4.5, 4, 4.5, 11.5, 15, 11.5));
	private static final VoxelShape ANVIL_SHAPE = Shapes.or(Block.box(2, 0, 2, 14, 4, 14), Block.box(4, 4, 3, 12, 5, 13),
		Block.box(6, 5, 4, 10, 10, 12), Block.box(3, 10, 0, 13, 16, 16));
	private static final VoxelShape TABLE_SHAPE = Block.box(0, 0, 0, 16, 14, 16);
	private static final VoxelShape ALTAR_SHAPE = Shapes.or(Block.box(1, 0, 1, 15, 3, 15), Block.box(3, 3, 3, 13, 9, 13),
		Block.box(2, 9, 2, 14, 10.5, 14), Block.box(4, 10.5, 5, 12, 14, 12));

	/** Troféu do Kraken: estatueta do Kraken com os tentáculos enrolados num pedestal. */
	public static final Block KRAKEN_TROPHY = register("kraken_trophy", p -> new FacingShapeBlock(p, TROPHY_SHAPE),
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops().strength(3.0F, 1200.0F).sound(SoundType.DEEPSLATE_TILES).lightLevel(state -> 4).noOcclusion());

	/** Troféu Dourado do Abismo: a mesma estatueta em ouro, com cristais; recompensa exclusiva do modo abissal. */
	public static final Block GOLDEN_ABYSS_TROPHY = register("golden_abyss_trophy", p -> new FacingShapeBlock(p, TROPHY_SHAPE),
		BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BELL)
			.requiresCorrectToolForDrops().strength(3.0F, 1200.0F).sound(SoundType.METAL).lightLevel(state -> 8).noOcclusion());

	// Fase 3E: blocos de trabalho dos Mergulhadores do Porto dos Náufragos
	public static final Block TIDE_ANVIL = register("tide_anvil", p -> new FacingShapeBlock(p, ANVIL_SHAPE),
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).requiresCorrectToolForDrops().strength(5.0F, 1200.0F)
			.sound(SoundType.ANVIL).noOcclusion());
	public static final Block CHART_TABLE = register("chart_table", p -> new FacingShapeBlock(p, TABLE_SHAPE),
		BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD).noOcclusion());
	public static final Block SHELL_ALTAR = register("shell_altar", p -> new FacingShapeBlock(p, ALTAR_SHAPE),
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).requiresCorrectToolForDrops().strength(3.0F)
			.lightLevel(state -> 7).sound(SoundType.CORAL_BLOCK).noOcclusion());

	// Fase 4F: Neutrônio (docs/10-fase4.md, §8)
	/** Minério de Neutrônio: sob o leito dos oceanos profundos; dureza 50, pede picareta Abissal ou de netherite. */
	public static final Block NEUTRONIUM_ORE = register("neutronium_ore", Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
			.strength(50.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS).lightLevel(state -> 3));
	/** Bloco de Neutrônio: nove lingotes; base de sinalizador. */
	public static final Block NEUTRONIUM_BLOCK = register("neutronium_block", Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).requiresCorrectToolForDrops().strength(50.0F, 1200.0F)
			.sound(SoundType.NETHERITE_BLOCK).lightLevel(state -> 6));

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Aquamarine.id(name));
		return Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
	}

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro. Minério e bloco se chamavam orichalcum_* até a
		// Fase 4: o ID antigo vira apelido e os blocos já colocados nos mundos continuam lá.
		for (String name : new String[] {"ore", "block"}) {
			BuiltInRegistries.BLOCK.addAlias(Aquamarine.id("orichalcum_" + name), Aquamarine.id("abyssal_" + name));
		}
	}

	private ModBlocks() {}
}
