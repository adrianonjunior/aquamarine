package com.aquamarine.client.mixin;

import com.aquamarine.client.music.KrakenSky;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeProbe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** O céu, a névoa, a água, as nuvens e a luz do céu tomam a cor da fase do Kraken (docs/02-kraken.md). */
@Mixin(EnvironmentAttributeProbe.class)
public abstract class EnvironmentAttributeProbeMixin {
	@Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
	private void aquamarine$krakenSky(EnvironmentAttribute<?> attribute, float partialTicks, CallbackInfoReturnable<Object> cir) {
		Object tinted = KrakenSky.tint(attribute, cir.getReturnValue());
		if (tinted != null) {
			cir.setReturnValue(tinted);
		}
	}
}
