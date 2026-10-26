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
			.strength(0.5F, 3600000.0F).sound(SoundType.WOOD).noLootTable());
	/** Troféu do Kraken: decorativo, cabeça do Kraken sobre um pedestal. */
	public static final Block KRAKEN_TROPHY = register("kraken_trophy", Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops().strength(3.0F, 1200.0F).sound(SoundType.DEEPSLATE_TILES).lightLevel(state -> 4).noOcclusion());

	/** Troféu Dourado do Abismo: recompensa exclusiva do modo abissal. */
	public static final Block GOLDEN_ABYSS_TROPHY = register("golden_abyss_trophy", Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BELL)
			.requiresCorrectToolForDrops().strength(3.0F, 1200.0F).sound(SoundType.METAL).lightLevel(state -> 8).noOcclusion());

	// Fase 3E: blocos de trabalho dos Mergulhadores do Porto dos Náufragos
	public static final Block TIDE_ANVIL = register("tide_anvil", Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).requiresCorrectToolForDrops().strength(5.0F, 1200.0F)
			.sound(SoundType.ANVIL));
	public static final Block CHART_TABLE = register("chart_table", Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD));
	public static final Block SHELL_ALTAR = register("shell_altar", Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).requiresCorrectToolForDrops().strength(3.0F)
			.lightLevel(state -> 7).sound(SoundType.CORAL_BLOCK));

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
