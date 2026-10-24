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
	/** Minério de Oricálcio. Não gera no mundo nesta versão: só a Cidade Perdida o terá. */
	public static final Block ORICHALCUM_ORE = register("orichalcum_ore",
		p -> new DropExperienceBlock(UniformInt.of(3, 7), p),
		BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops().strength(5.0F, 9.0F).sound(SoundType.DEEPSLATE));

	public static final Block ORICHALCUM_BLOCK = register("orichalcum_block", Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BELL)
			.requiresCorrectToolForDrops().strength(6.0F, 12.0F).sound(SoundType.METAL));

	/** Bancada das receitas de Oricálcio e dos itens da luta. Brilha de leve, como coral vivo. */
	public static final Block ABYSSAL_FORGE = register("abyssal_forge", AbyssalForgeBlock::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundType.DEEPSLATE_BRICKS).lightLevel(state -> 7));

	/** Altar do Abismo: onde o Chamariz Abissal invoca o Kraken. Só na Cidade Perdida (e no criativo). */
	public static final Block ABYSSAL_ALTAR = register("abyssal_altar", AbyssalAltarBlock::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM)
			.strength(-1.0F, 3600000.0F).sound(SoundType.DEEPSLATE_TILES).lightLevel(state -> 6).noOcclusion());

	/** Troféu do Kraken: decorativo, cabeça do Kraken sobre um pedestal. */
	public static final Block KRAKEN_TROPHY = register("kraken_trophy", Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops().strength(3.0F, 1200.0F).sound(SoundType.DEEPSLATE_TILES).lightLevel(state -> 4).noOcclusion());

	/** Troféu Dourado do Abismo: recompensa exclusiva do modo abissal. */
	public static final Block GOLDEN_ABYSS_TROPHY = register("golden_abyss_trophy", Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BELL)
			.requiresCorrectToolForDrops().strength(3.0F, 1200.0F).sound(SoundType.METAL).lightLevel(state -> 8).noOcclusion());

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Aquamarine.id(name));
		return Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
	}

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro.
	}

	private ModBlocks() {}
}
