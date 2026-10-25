package com.aquamarine.client.mixin;

import com.aquamarine.client.music.KrakenMusic;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** A música normal do jogo fica calada durante a luta do Kraken (docs/09-audio.md, "Luta do Kraken"). */
@Mixin(MusicManager.class)
public abstract class MusicManagerMixin {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void aquamarine$krakenFight(CallbackInfo ci) {
		if (KrakenMusic.silencesVanillaMusic()) {
			((MusicManager) (Object) this).stopPlaying();
			ci.cancel();
		}
	}
}
