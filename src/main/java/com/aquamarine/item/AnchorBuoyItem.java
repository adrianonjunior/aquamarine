package com.aquamarine.item;

import com.aquamarine.block.AnchorBuoyBlock;
import com.aquamarine.block.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Boia de Âncora (docs/05-itens.md): ao usar mirando a superfície da água, forma uma plataforma
 * 3x3 que dura 90 s ({@link AnchorBuoyBlock}). Recarga de 30 s por jogador.
 */
public class AnchorBuoyItem extends BlockItem {
	public static final int COOLDOWN_TICKS = 30 * 20;

	public AnchorBuoyItem(Properties properties) {
		super(ModBlocks.ANCHOR_BUOY, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		if (hit.getType() != HitResult.Type.BLOCK) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		if (!level.getFluidState(hit.getBlockPos()).isSource() || !level.getBlockState(hit.getBlockPos()).is(Blocks.WATER)
			|| !level.getBlockState(hit.getBlockPos().above()).isAir()) {
			return InteractionResult.PASS;
		}
		if (place(server, hit.getBlockPos()) == 0) {
			return InteractionResult.PASS;
		}
		player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
		stack.consume(1, player);
		return InteractionResult.SUCCESS_SERVER;
	}

	/** Forma a plataforma 3x3 centrada em {@code center} (superfície da água). Devolve quantos blocos pôs. */
	public static int place(ServerLevel level, BlockPos center) {
		int placed = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				BlockPos pos = center.offset(dx, 0, dz);
				if (level.getBlockState(pos).is(Blocks.WATER) && level.getFluidState(pos).isSource()) {
					level.setBlockAndUpdate(pos, ModBlocks.ANCHOR_BUOY.defaultBlockState());
					level.scheduleTick(pos, ModBlocks.ANCHOR_BUOY, AnchorBuoyBlock.LIFETIME_TICKS);
					placed++;
				}
			}
		}
		if (placed > 0) {
			level.playSound(null, center, SoundEvents.LILY_PAD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
		}
		return placed;
	}
}
