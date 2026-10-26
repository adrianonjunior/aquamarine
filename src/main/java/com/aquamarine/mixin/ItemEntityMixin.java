package com.aquamarine.mixin;

import com.aquamarine.item.NeutroniumTempering;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Neutrônio solto: afunda na água e o Lingote Instável se tempera no fundo ({@link NeutroniumTempering}). */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Unique
	private int aquamarine$temper;

	@Inject(method = "tick", at = @At("TAIL"))
	private void aquamarine$neutronium(CallbackInfo ci) {
		aquamarine$temper = NeutroniumTempering.tick((ItemEntity) (Object) this, aquamarine$temper);
	}
}
