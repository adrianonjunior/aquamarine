package com.aquamarine.mixin;

import com.aquamarine.block.AquaticEnchantingTableBlock;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

/**
 * Mesa de Encantamento Aquática (Fase 6, 6F1): a mesma tela e as mesmas regras da mesa do jogo (lápis, estantes, níveis),
 * mas o sorteio sai só da tag {@code aquamarine:aquatic_enchantments} e o menu continua válido perto dela.
 */
@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {
	@Shadow
	@Final
	private ContainerLevelAccess access;

	@Unique
	private boolean aquamarine$aquatic() {
		return access.evaluate((level, pos) -> level.getBlockState(pos).getBlock() instanceof AquaticEnchantingTableBlock, false);
	}

	@ModifyArg(method = "getEnchantmentList", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/Registry;get(Lnet/minecraft/tags/TagKey;)Ljava/util/Optional;"))
	private TagKey<Enchantment> aquamarine$aquaticPool(TagKey<Enchantment> tag) {
		return aquamarine$aquatic() ? AquaticEnchantingTableBlock.AQUATIC_ENCHANTMENTS : tag;
	}

	@ModifyArg(method = "stillValid", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/inventory/EnchantmentMenu;stillValid(Lnet/minecraft/world/inventory/ContainerLevelAccess;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/Block;)Z"),
		index = 2)
	private Block aquamarine$aquaticTable(Block table) {
		return access.evaluate((level, pos) -> level.getBlockState(pos).getBlock(), table) instanceof AquaticEnchantingTableBlock aquatic
			? aquatic : table;
	}
}
