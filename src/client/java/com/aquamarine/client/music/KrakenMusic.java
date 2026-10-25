package com.aquamarine.client.music;

import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.sound.KrakenMusicPlan;
import com.aquamarine.sound.ModSounds;
import java.util.Comparator;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;

/**
 * A música da luta do Kraken, só no cliente (docs/09-audio.md, "Luta do Kraken"):
 * <ul>
 *   <li>começa quando um Kraken vivo está a até {@link #START_RANGE} blocos (a invocação toca o stinger dela);</li>
 *   <li>cada fase tem a sua trilha (Tormenta, Fúria, Abismo); na troca, a trilha cai, o golpe de transição
 *   toca e a fase nova entra quando ele acaba;</li>
 *   <li>as camadas (base, breaks, glória) seguem a luta pelo {@link KrakenMusicPlan};</li>
 *   <li>morte do Kraken: fanfarra de vitória; Kraken que some vivo por perto: o stinger do recuo; jogador morto
 *   ou longe ({@link #STOP_RANGE}): a música some devagar;</li>
 *   <li>a música normal do jogo fica calada durante a luta e um pouco depois.</li>
 * </ul>
 * Também avisa o {@link KrakenSky} (céu por fase).
 */
public final class KrakenMusic {
	public static final double START_RANGE = 72.0;
	public static final double STOP_RANGE = 112.0;
	/** O céu muda antes da música, de mais longe. */
	public static final double SKY_RANGE = 128.0;
	private static final int FADE_OUT_TICKS = 60;
	private static final int QUICK_FADE_TICKS = 10;
	private static final int AFTER_FIGHT_SILENCE = 25 * 20;
	private static final int SUMMON_SILENCE = 14 * 20;
	private static final int RESTART_COOLDOWN = 200;

	private static LayeredMusicInstance current;
	private static int currentPhase;
	private static int krakenId = -1;
	private static Vec3 lastSeen;
	private static int pendingTicks;
	private static int quietTicks;
	private static float lastShield = -1.0F;
	private static int lastAnimation = -1;
	private static int vanillaSilence;
	private static int restartCooldown;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(KrakenMusic::tick);
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> reset());
	}

	/** A música normal do jogo deve ficar calada (luta, troca de fase, stinger ou logo depois da luta). */
	public static boolean silencesVanillaMusic() {
		return current != null || pendingTicks > 0 || vanillaSilence > 0;
	}

	/** Luta tocando (para testes). */
	public static boolean playing() {
		return current != null || pendingTicks > 0;
	}

	public static int currentPhase() {
		return playing() ? currentPhase : 0;
	}

	public static float[] currentGains() {
		return current == null ? new float[3] : current.layerGains();
	}

	/** A invocação começou (silêncio do oceano): toca o stinger dela. */
	public static void onSummon() {
		play(ModSounds.MUSIC_KRAKEN_SUMMON);
		vanillaSilence = Math.max(vanillaSilence, SUMMON_SILENCE);
	}

	private static void tick(Minecraft mc) {
		if (vanillaSilence > 0) {
			vanillaSilence--;
		}
		if (restartCooldown > 0) {
			restartCooldown--;
		}
		LocalPlayer player = mc.player;
		if (mc.level == null || player == null) {
			reset();
			return;
		}
		KrakenEntity sky = nearest(mc, player, SKY_RANGE);
		KrakenSky.update(sky);

		KrakenEntity kraken = krakenId >= 0 && mc.level.getEntity(krakenId) instanceof KrakenEntity k && !k.isRemoved() ? k : null;
		if (krakenId >= 0 && kraken == null) {
			// Sumiu: vivo e perto é o recuo ("retorna às profundezas"); longe, só saiu do alcance.
			boolean near = lastSeen != null && player.position().distanceTo(lastSeen) < STOP_RANGE;
			endFight(near ? ModSounds.MUSIC_KRAKEN_RETREAT : null, near ? QUICK_FADE_TICKS * 2 : FADE_OUT_TICKS);
			return;
		}
		if (kraken == null) {
			kraken = player.isAlive() ? nearest(mc, player, START_RANGE) : null;
			if (kraken == null) {
				return;
			}
			startFight(kraken);
		}
		lastSeen = kraken.position();
		if (kraken.isDeadOrDying()) {
			endFight(ModSounds.MUSIC_KRAKEN_VICTORY, QUICK_FADE_TICKS);
			vanillaSilence = AFTER_FIGHT_SILENCE;
			KrakenSky.victory();
			return;
		}
		if (!player.isAlive() || player.position().distanceTo(kraken.position()) > STOP_RANGE) {
			endFight(null, FADE_OUT_TICKS);
			return;
		}

		int phase = kraken.phase();
		if (pendingTicks > 0) {
			if (--pendingTicks == 0) {
				startPhase(mc, currentPhase);
			}
		} else if (phase != currentPhase) {
			// Troca de fase: a trilha cai, o golpe toca, a fase nova entra no fim da subida.
			if (current != null) {
				current.fadeOutAndStop(QUICK_FADE_TICKS);
				current = null;
			}
			play(ModSounds.MUSIC_KRAKEN_TRANSITION);
			currentPhase = phase;
			pendingTicks = KrakenMusicPlan.TRANSITION_TICKS;
		}

		trackActivity(kraken, player);
		if (current != null) {
			if (!mc.getSoundManager().isActive(current) && restartCooldown == 0) {
				// O fluxo parou sem a gente pedir (recarga de recursos, travada longa): volta a fase.
				startPhase(mc, currentPhase);
			}
			float health = kraken.realMaxHealth() > 0 ? kraken.realHealth() / kraken.realMaxHealth() : 1.0F;
			current.setLayerGains(KrakenMusicPlan.gains(phase, KrakenMusicPlan.phaseHealth(phase, health), kraken.isStunned(),
				quietTicks, player.getHealth() / player.getMaxHealth()));
		}
	}

	private static void startFight(KrakenEntity kraken) {
		krakenId = kraken.getId();
		currentPhase = kraken.phase();
		quietTicks = 0;
		lastShield = kraken.shield();
		lastAnimation = kraken.lastAnimation();
		startPhase(Minecraft.getInstance(), currentPhase);
	}

	private static void startPhase(Minecraft mc, int phase) {
		if (current != null) {
			current.fadeOutAndStop(QUICK_FADE_TICKS);
		}
		String track = KrakenMusicPlan.track(phase);
		current = new LayeredMusicInstance(event(phase), LayeredMusicInstance.layers(track, KrakenMusicPlan.LAYERS),
			KrakenMusicPlan.gains(phase, 1.0F, false, 0, 1.0F));
		mc.getSoundManager().play(current);
		restartCooldown = RESTART_COOLDOWN;
	}

	private static SoundEvent event(int phase) {
		return switch (phase) {
			case 2 -> ModSounds.MUSIC_KRAKEN_FURIA;
			case 3 -> ModSounds.MUSIC_KRAKEN_ABISMO;
			default -> ModSounds.MUSIC_KRAKEN_TORMENTA;
		};
	}

	/** Combate acontecendo: o Kraken tomou dano, perdeu escudo ou atacou, ou o jogador se feriu. */
	private static void trackActivity(KrakenEntity kraken, LocalPlayer player) {
		boolean action = kraken.hurtTime > 0 || player.hurtTime > 0 || kraken.shield() < lastShield - 0.01F
			|| kraken.lastAnimation() != lastAnimation;
		lastShield = kraken.shield();
		lastAnimation = kraken.lastAnimation();
		quietTicks = action ? 0 : quietTicks + 1;
	}

	private static void endFight(SoundEvent stinger, int fadeTicks) {
		if (current != null) {
			current.fadeOutAndStop(fadeTicks);
			current = null;
		}
		pendingTicks = 0;
		krakenId = -1;
		lastSeen = null;
		if (stinger != null) {
			play(stinger);
			vanillaSilence = Math.max(vanillaSilence, AFTER_FIGHT_SILENCE / 2);
		}
	}

	private static void play(SoundEvent event) {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forMusic(event));
	}

	private static KrakenEntity nearest(Minecraft mc, LocalPlayer player, double range) {
		return mc.level.getEntitiesOfClass(KrakenEntity.class, player.getBoundingBox().inflate(range),
				k -> k.isAlive() && !k.isDeadOrDying() && k.position().distanceTo(player.position()) <= range)
			.stream().min(Comparator.comparingDouble(k -> k.distanceToSqr(player))).orElse(null);
	}

	private static void reset() {
		if (current != null) {
			Minecraft.getInstance().getSoundManager().stop(current);
		}
		current = null;
		krakenId = -1;
		lastSeen = null;
		pendingTicks = 0;
		vanillaSilence = 0;
		KrakenSky.reset();
	}

	private KrakenMusic() {}
}
