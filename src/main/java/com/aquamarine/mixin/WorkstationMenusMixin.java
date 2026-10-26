package com.aquamarine.mixin;

import com.aquamarine.block.WorkstationBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.level.block.Block;

/**
 * A tela de cartografia e a do rebolo continuam abertas perto da Mesa de Cartas Náuticas e do Altar de Concha (6C2):
 * o menu do jogo só aceitava o próprio bloco.
 */
@Mixin({CartographyTableMenu.class, GrindstoneMenu.class})
public abstract class WorkstationMenusMixin {
	@ModifyArg(method = "stillValid", at = @At(value = "INVOKE",
		target = "stillValid(Lnet/minecraft/world/inventory/ContainerLevelAccess;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/Block;)Z"),
		index = 2)
	private Block aquamarine$workstation(ContainerLevelAccess access, net.minecraft.world.entity.player.Player player, Block block) {
		return access.evaluate((level, pos) -> level.getBlockState(pos).getBlock(), block) instanceof WorkstationBlock station
			? station : block;
	}
}
