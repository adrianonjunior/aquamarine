package com.aquamarine.client.mixin;

import com.aquamarine.client.DivingGogglesVision;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.WaterFogEnvironment;

import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Óculos de Mergulho: névoa da água mais distante e mais clara. Lógica em {@link DivingGogglesVision}. */
@Mixin(WaterFogEnvironment.class)
public abstract class WaterFogEnvironmentMixin {
	@Inject(method = "setupFog", at = @At("TAIL"))
	private void aquamarine$gogglesFog(FogData fog, Camera camera, ClientLevel level, float renderDistance, DeltaTracker deltaTracker, CallbackInfo ci) {
		DivingGogglesVision.applyFog(fog, camera, deltaTracker);
	}

	@Inject(method = "getBaseColor", at = @At("RETURN"), cancellable = true)
	private void aquamarine$gogglesColor(ClientLevel level, Camera camera, int renderDistance, float partialTicks, CallbackInfoReturnable<Vector3fc> cir) {
		if (DivingGogglesVision.isActive(camera)) {
			cir.setReturnValue(DivingGogglesVision.clearerColor(cir.getReturnValue()));
		}
	}
}
