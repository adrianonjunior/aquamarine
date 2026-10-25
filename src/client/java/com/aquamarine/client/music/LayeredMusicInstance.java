package com.aquamarine.client.music;

import com.aquamarine.Aquamarine;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Util;

/**
 * Uma trilha em camadas tocando (categoria Música, sem posição). O evento da {@code sounds.json} aponta
 * para o som vazio da Fabric com {@code "stream": true}; o áudio de verdade vem do
 * {@link LayeredMusicStream}, criado quando o jogo pede o fluxo. O volume faz os fades imediatos.
 */
public final class LayeredMusicInstance extends AbstractTickableSoundInstance {
	private final List<Identifier> files;
	private float[] gains;
	private volatile LayeredMusicStream stream;
	private float targetVolume = 1.0F;
	private float volumeStep = 1.0F;
	private boolean stopWhenSilent;

	public LayeredMusicInstance(SoundEvent event, List<Identifier> files, float[] gains) {
		super(event, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
		this.files = files;
		this.gains = gains.clone();
		this.looping = false;
		this.delay = 0;
		this.volume = 1.0F;
		this.relative = true;
		this.attenuation = SoundInstance.Attenuation.NONE;
	}

	/** As camadas da trilha {@code name}: {@code sounds/music/kraken/<name>_<camada>.ogg}. */
	public static List<Identifier> layers(String name, String... layerNames) {
		return java.util.Arrays.stream(layerNames)
			.map(layer -> Aquamarine.id("sounds/music/kraken/" + name + "_" + layer + ".ogg")).toList();
	}

	@Override
	public CompletableFuture<AudioStream> getAudioStream(SoundBufferLibrary loader, Identifier id, boolean repeatInstantly) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				LayeredMusicStream opened = LayeredMusicStream.open(Minecraft.getInstance().getResourceManager(), files, gains);
				stream = opened;
				return opened;
			} catch (IOException e) {
				throw new CompletionException(e);
			}
		}, Util.nonCriticalIoPool());
	}

	/** Ganho alvo de cada camada (base, breaks, glória); o misturador faz a rampa. */
	public void setLayerGains(float[] values) {
		gains = values.clone();
		LayeredMusicStream s = stream;
		if (s != null) {
			s.setTargets(values);
		}
	}

	public float[] layerGains() {
		return gains.clone();
	}

	/** Fade de volume em {@code ticks} (imediato no tick seguinte, diferente das camadas). */
	public void fadeTo(float volume, int ticks) {
		targetVolume = volume;
		volumeStep = Math.abs(volume - this.volume) / Math.max(1, ticks);
		stopWhenSilent = false;
	}

	public void fadeOutAndStop(int ticks) {
		fadeTo(0.0F, ticks);
		stopWhenSilent = true;
	}

	public boolean isFadingOut() {
		return stopWhenSilent;
	}

	@Override
	public void tick() {
		if (volume < targetVolume) {
			volume = Math.min(targetVolume, volume + volumeStep);
		} else if (volume > targetVolume) {
			volume = Math.max(targetVolume, volume - volumeStep);
		}
		if (stopWhenSilent && volume <= 0.0F) {
			stop();
		}
	}

	@Override
	public boolean canStartSilent() {
		return true;
	}
}
