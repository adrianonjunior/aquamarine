package com.aquamarine.sound;

import com.aquamarine.Aquamarine;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

/**
 * Eventos de som do mod (docs/09-audio.md). Os arquivos e as variações estão em
 * {@code assets/aquamarine/sounds.json}; as legendas em {@code lang/*.json} ({@code subtitles.aquamarine.*}).
 * Os sons do Kraken saem do servidor pelo mecanismo do jogo; a música é só do cliente.
 */
public final class ModSounds {
	// --- Kraken (o corpo e os ataques)
	public static final SoundEvent KRAKEN_AMBIENT = register("entity.kraken.ambient");
	public static final SoundEvent KRAKEN_HURT = register("entity.kraken.hurt");
	public static final SoundEvent KRAKEN_DEATH = register("entity.kraken.death");
	public static final SoundEvent KRAKEN_ROAR = register("entity.kraken.roar");
	public static final SoundEvent KRAKEN_EMERGE = register("entity.kraken.emerge");
	public static final SoundEvent KRAKEN_RETREAT = register("entity.kraken.retreat");
	public static final SoundEvent KRAKEN_SLAM_WARN = register("entity.kraken.slam_warn");
	public static final SoundEvent KRAKEN_SLAM = register("entity.kraken.slam");
	public static final SoundEvent KRAKEN_GRAB_WARN = register("entity.kraken.grab_warn");
	public static final SoundEvent KRAKEN_GRAB = register("entity.kraken.grab");
	public static final SoundEvent KRAKEN_INK = register("entity.kraken.ink");
	public static final SoundEvent KRAKEN_WHIRLPOOL = register("entity.kraken.whirlpool");
	public static final SoundEvent KRAKEN_SPAWN_YOUNG = register("entity.kraken.spawn_young");
	public static final SoundEvent KRAKEN_WATER_JET_CHARGE = register("entity.kraken.water_jet_charge");
	public static final SoundEvent KRAKEN_WATER_JET = register("entity.kraken.water_jet");
	public static final SoundEvent KRAKEN_ROAR_BLAST = register("entity.kraken.roar_blast");
	public static final SoundEvent KRAKEN_BOLTS_CHARGE = register("entity.kraken.bolts_charge");
	public static final SoundEvent KRAKEN_BOLT = register("entity.kraken.bolt");
	public static final SoundEvent KRAKEN_AMBUSH_WARN = register("entity.kraken.ambush_warn");
	public static final SoundEvent KRAKEN_AMBUSH = register("entity.kraken.ambush");
	public static final SoundEvent KRAKEN_SHIELD_CRACK = register("entity.kraken.shield_crack");
	public static final SoundEvent KRAKEN_SHIELD_BREAK = register("entity.kraken.shield_break");
	public static final SoundEvent KRAKEN_SHIELD_REGEN = register("entity.kraken.shield_regen");

	// --- Tentáculos, Crias e ovos
	public static final SoundEvent TENTACLE_HURT = register("entity.kraken_tentacle.hurt");
	public static final SoundEvent TENTACLE_DEATH = register("entity.kraken_tentacle.death");
	public static final SoundEvent YOUNG_KRAKEN_AMBIENT = register("entity.young_kraken.ambient");
	public static final SoundEvent YOUNG_KRAKEN_HURT = register("entity.young_kraken.hurt");
	public static final SoundEvent YOUNG_KRAKEN_DEATH = register("entity.young_kraken.death");
	public static final SoundEvent KRAKEN_EGG_HATCH = register("entity.kraken_egg.hatch");

	// --- Música da luta (só o cliente toca; as fases são tocadas pelo misturador de camadas)
	public static final SoundEvent MUSIC_KRAKEN_TORMENTA = register("music.kraken.tormenta");
	public static final SoundEvent MUSIC_KRAKEN_FURIA = register("music.kraken.furia");
	public static final SoundEvent MUSIC_KRAKEN_ABISMO = register("music.kraken.abismo");
	public static final SoundEvent MUSIC_KRAKEN_TRANSITION = register("music.kraken.transition");
	public static final SoundEvent MUSIC_KRAKEN_VICTORY = register("music.kraken.victory");
	public static final SoundEvent MUSIC_KRAKEN_RETREAT = register("music.kraken.retreat");
	public static final SoundEvent MUSIC_KRAKEN_SUMMON = register("music.kraken.summon");

	private static SoundEvent register(String name) {
		return Registry.register(BuiltInRegistries.SOUND_EVENT, Aquamarine.id(name), SoundEvent.createVariableRangeEvent(Aquamarine.id(name)));
	}

	public static void register() {
		// Carrega a classe; os campos estáticos fazem o registro.
	}

	private ModSounds() {}
}
