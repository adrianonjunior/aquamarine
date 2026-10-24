package com.aquamarine.client.mixin;

import com.aquamarine.client.SummonEffects;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Tremor de tela da invocação do Kraken: soma um desvio pequeno à rotação da câmera. Lógica em {@link SummonEffects}. */
@Mixin(Camera.class)
public abstract class CameraMixin {
	@Shadow
	protected abstract void setRotation(float yRot, float xRot);

	@Shadow
	public abstract float xRot();

	@Shadow
	public abstract float yRot();

	@Inject(method = "update", at = @At("TAIL"))
	private void aquamarine$shake(DeltaTracker deltaTracker, CallbackInfo ci) {
		if (SummonEffects.shaking()) {
			float[] offset = SummonEffects.shakeOffset(deltaTracker.getGameTimeDeltaPartialTick(false));
			setRotation(yRot() + offset[0], xRot() + offset[1]);
		}
	}
}
