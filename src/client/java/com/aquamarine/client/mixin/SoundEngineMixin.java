package com.aquamarine.client.mixin;

import com.aquamarine.client.SummonEffects;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Silêncio do oceano na invocação do Kraken: barra os sons ambientes novos. Lógica em {@link SummonEffects}. */
@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {
	@Inject(method = "play", at = @At("HEAD"), cancellable = true)
	private void aquamarine$silenceOcean(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
		if (SummonEffects.silenced() && sound.getSource() == SoundSource.AMBIENT) {
			cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
		}
	}
}
