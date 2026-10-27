package com.aquamarine.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Ar Comprimido (0.6.1, 7C): o "vento" do Tubo de Ar. É um fluido só para a API de transferência da Fabric: sem bloco,
 * sem balde e sem estado no mundo (técnica confirmada no 26.3: um {@link Fluid} registrado vira {@code FluidVariant}; o
 * nome e o "mais leve que o ar" vêm do {@code FluidVariantAttributes}). O estado padrão conta como fonte porque a Fabric só
 * aceita fluido parado num {@code FluidVariant}.
 */
public final class CompressedAirFluid extends Fluid {
	@Override
	public Item getBucket() {
		return Items.AIR;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
		return true;
	}

	@Override
	protected Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState state) {
		return Vec3.ZERO;
	}

	@Override
	public int getTickDelay(LevelReader level) {
		return 0;
	}

	@Override
	protected float getExplosionResistance() {
		return 0.0F;
	}

	@Override
	public float getHeight(FluidState state, BlockGetter level, BlockPos pos) {
		return 0.0F;
	}

	@Override
	public float getOwnHeight(FluidState state) {
		return 0.0F;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public boolean isSource(FluidState state) {
		return true;
	}

	@Override
	public int getAmount(FluidState state) {
		return 8;
	}

	@Override
	public VoxelShape getShape(FluidState state, BlockGetter level, BlockPos pos) {
		return Shapes.empty();
	}
}
