package com.aquamarine.mixin;

import com.aquamarine.item.HarpoonEffects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.EntityHitResult;

/** Ganchos do Arpão de Oricálcio no tridente do jogo: efeitos no acerto e a volta da Fisgada. */
@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin {
	@Inject(method = "onHitEntity", at = @At("TAIL"))
	private void aquamarine$harpoonHit(EntityHitResult hitResult, CallbackInfo ci) {
		HarpoonEffects.onHit((ThrownTrident) (Object) this, hitResult.getEntity());
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void aquamarine$harpoonReturn(CallbackInfo ci) {
		HarpoonEffects.tick((ThrownTrident) (Object) this);
	}
}
