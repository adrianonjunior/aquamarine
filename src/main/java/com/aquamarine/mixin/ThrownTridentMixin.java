package com.aquamarine.mixin;

import com.aquamarine.item.HarpoonEffects;
import com.aquamarine.item.HarpoonStyleHolder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Ganchos do Arpão Abissal no tridente do jogo: efeitos no acerto, a volta da Fisgada e (6D3) o desenho do arpão,
 * sincronizado com o cliente para o renderer e o rastro.
 */
@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin implements HarpoonStyleHolder {
	@Unique
	private static final EntityDataAccessor<Integer> AQUAMARINE$STYLE = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.INT);

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	private void aquamarine$defineStyle(SynchedEntityData.Builder builder, CallbackInfo ci) {
		builder.define(AQUAMARINE$STYLE, TRIDENT);
	}

	@Inject(method = "onHitEntity", at = @At("TAIL"))
	private void aquamarine$harpoonHit(EntityHitResult hitResult, CallbackInfo ci) {
		HarpoonEffects.onHit((ThrownTrident) (Object) this, hitResult.getEntity());
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void aquamarine$harpoonReturn(CallbackInfo ci) {
		HarpoonEffects.tick((ThrownTrident) (Object) this);
		HarpoonEffects.trail((ThrownTrident) (Object) this, aquamarine$harpoonStyle());
	}

	@Override
	public int aquamarine$harpoonStyle() {
		return ((ThrownTrident) (Object) this).getEntityData().get(AQUAMARINE$STYLE);
	}

	@Override
	public void aquamarine$setHarpoonStyle(int style) {
		((ThrownTrident) (Object) this).getEntityData().set(AQUAMARINE$STYLE, style);
	}
}
