package com.aquamarine.block;

import com.aquamarine.Aquamarine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Mesa de Encantamento Aquática (Fase 6, 6F1): abre a tela da mesa do jogo, com estantes e lápis, mas só sorteia os
 * encantamentos da água ({@code EnchantmentMenuMixin}): os do mod e os do jogo que são do mar. Modelo em
 * tools/gen_phase6_art.py.
 */
public class AquaticEnchantingTableBlock extends FacingShapeBlock {
	public static final TagKey<Enchantment> AQUATIC_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, Aquamarine.id("aquatic_enchantments"));
	private static final Component TITLE = Component.translatable("container.aquamarine.aquatic_enchanting_table");

	public AquaticEnchantingTableBlock(Properties properties) {
		super(properties, Block.box(0, 0, 0, 16, 12, 16));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide()) {
			player.openMenu(state.getMenuProvider(level, pos));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider((id, inventory, player) -> new EnchantmentMenu(id, inventory, ContainerLevelAccess.create(level, pos)),
			TITLE);
	}

	/** Bolhas e brilho subindo da concha, como as letras da mesa do jogo. */
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(3) == 0) {
			level.addParticle(random.nextBoolean() ? ParticleTypes.BUBBLE_POP : ParticleTypes.GLOW, pos.getX() + 0.3 + random.nextDouble() * 0.4,
				pos.getY() + 0.95, pos.getZ() + 0.3 + random.nextDouble() * 0.4, 0.0, 0.03, 0.0);
		}
	}
}
