package com.aquamarine.mixin;

import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Quem negocia num menu de troca aberto (para saber se é um Mergulhador do Porto). */
@Mixin(MerchantMenu.class)
public interface MerchantMenuAccessor {
	@Accessor("trader")
	Merchant aquamarine$trader();
}
