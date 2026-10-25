package com.aquamarine.client;

import com.aquamarine.client.entity.kraken.KrakenAnimations;
import com.aquamarine.client.entity.kraken.KrakenRenderer;
import com.aquamarine.entity.KrakenEntity;
import com.aquamarine.entity.boss.KrakenStats;
import com.aquamarine.sound.ModSounds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Partículas e sons do corpo do Kraken, desenhados por cada cliente a partir do estado que o servidor
 * sincroniza (animação em curso, fase e escudo): nada depende de evento local, então todos os
 * jogadores veem o mesmo. Os marcadores no mundo (círculo da Pancada, linha do Jato, som de aviso)
 * são do servidor, em KrakenAttacks (2D-C); aqui fica o que sai do próprio corpo.
 *
 * <p>Posições em pixels do modelo (KrakenModel; y para baixo, pés em y = 24, frente em +z) convertidas
 * pela escala da entidade vezes {@link KrakenRenderer#MODEL_SCALE}: crescem junto com o Kraken.
 */
public final class KrakenEffects {
	private static final DustParticleOptions PURPLE = new DustParticleOptions(0xA060FF, 1.6F);
	private static final DustParticleOptions INK_DUST = new DustParticleOptions(0x150A26, 2.2F);
	private static final DustParticleOptions CRYSTAL = new DustParticleOptions(0xC4FFF0, 1.2F);
	private static final DustParticleOptions EGG = new DustParticleOptions(0xFFE07A, 1.8F);
	private static final DustParticleOptions TEAL_LIGHT = new DustParticleOptions(0x6FDCC4, 0.5F);
	private static final BlockParticleOption SHARD = new BlockParticleOption(ParticleTypes.BLOCK,
		Blocks.SEA_LANTERN.defaultBlockState());
	private static final float[] CRACKS = {0.75F, 0.50F, 0.25F};
	/** Comprimento do braço em pixels do modelo, da raiz à ponta (7 juntas). */
	private static final double ARM_LENGTH = 39.0;

	private static final Map<Integer, Track> TRACKS = new HashMap<>();
	/** Partículas emitidas por animação desde o início do cliente (o teste de cliente confere). */
	private static final Map<String, Integer> EMITTED = new HashMap<>();

	/** O que cada Kraken tinha no tick anterior, para disparar efeitos uma vez só. */
	private static final class Track {
		final float[] elapsed = new float[KrakenEntity.ANIMATION_NAMES.size()];
		float shield = -1.0F;
		int phase;
	}

	public static void register() {
		ClientTickEvents.END_LEVEL_TICK.register(KrakenEffects::tick);
	}

	public static int emitted(String animation) {
		return EMITTED.getOrDefault(animation, 0);
	}

	private static void tick(ClientLevel level) {
		Set<Integer> seen = new HashSet<>();
		for (Entity entity : level.entitiesForRendering()) {
			if (entity instanceof KrakenEntity kraken && !kraken.isRemoved()) {
				seen.add(kraken.getId());
				tick(level, kraken, TRACKS.computeIfAbsent(kraken.getId(), id -> new Track()));
			}
		}
		TRACKS.keySet().retainAll(seen);
	}

	private static void tick(ClientLevel level, KrakenEntity kraken, Track track) {
		Body body = new Body(level, kraken);
		AnimationState[] states = states(kraken);
		for (int i = 0; i < states.length; i++) {
			String name = KrakenEntity.ANIMATION_NAMES.get(i);
			float length = KrakenAnimations.lengthSeconds(name);
			float now = states[i].isStarted() ? states[i].getTimeInMillis(kraken.tickCount) / 1000.0F : -1.0F;
			boolean looping = name.equals("idle") || name.equals("swim") || name.equals("stunned") || name.equals("whirlpool");
			if (now >= 0.0F && (looping || now <= length)) {
				body.anim = name;
				animation(body, name, now, track.elapsed[i]);
			}
			track.elapsed[i] = now;
		}
		body.anim = "trail";
		trail(body, kraken);
		body.anim = "shield";
		shield(body, kraken, track);
		track.phase = kraken.phase();
	}

	private static AnimationState[] states(KrakenEntity k) {
		return new AnimationState[] {k.idleAnimation(), k.swimAnimation(), k.emergeAnimation(), k.stunnedAnimation(),
			k.phaseChangeAnimation(), k.deathAnimation(), k.slamAnimation(), k.grabAnimation(), k.inkAnimation(),
			k.whirlpoolAnimation(), k.spawnYoungAnimation(), k.waterJetAnimation(), k.roarAnimation(), k.boltsAnimation(),
			k.ambushAnimation(), k.doubleGrabAnimation()};
	}

	/** {@code t}: segundos desde o início do clipe; {@code before}: valor no tick anterior (-1 se parado). */
	private static void animation(Body b, String name, float t, float before) {
		switch (name) {
			case "idle" -> idle(b);
			case "stunned" -> stunned(b, t);
			case "emerge" -> emerge(b, t, before);
			case "phaseChange" -> phaseChange(b, t, before);
			case "death" -> death(b, t, before);
			case "slam" -> slam(b, t, before);
			case "grab" -> grab(b, t, before, new int[] {6}, KrakenAnimations.GRAB_IMPACT);
			case "doubleGrab" -> grab(b, t, before, new int[] {1, 7}, KrakenAnimations.DOUBLE_GRAB_IMPACT);
			case "ink" -> ink(b, t, before);
			case "whirlpool" -> whirlpool(b, t);
			case "spawnYoung" -> spawnYoung(b, t, before);
			case "waterJet" -> waterJet(b, t, before);
			case "roar" -> roar(b, t, before);
			case "bolts" -> bolts(b, t, before);
			case "ambush" -> ambush(b, t, before);
			default -> { }
		}
	}

	private static boolean crossed(float before, float t, float mark) {
		return before < mark && t >= mark;
	}

	// ------------------------------------------------------------------ estados

	private static void idle(Body b) {
		// Bolhas soltas que sobem do manto: dá para achar o Kraken na água escura.
		if (b.every(6)) {
			b.emit(ParticleTypes.BUBBLE, b.at(b.rand(-8, 8), -30, b.rand(-8, 8)), 0, 0.06, 0);
		}
	}

	private static void stunned(Body b, float t) {
		// Atordoado: anel de faíscas girando sobre a cabeça.
		if (b.every(2)) {
			double a = t * 6.0;
			for (int k = 0; k < 2; k++) {
				double ang = a + k * Math.PI;
				b.emit(ParticleTypes.ELECTRIC_SPARK, b.at(Math.cos(ang) * 9, -40, Math.sin(ang) * 9), 0, 0, 0);
			}
		}
	}

	private static void emerge(Body b, float t, float before) {
		if (t < 1.3F && b.every(2)) {
			b.burst(ParticleTypes.BUBBLE_COLUMN_UP, b.at(0, -10, 0), 14, 6, 0.0, 0.25, 0.0);
		}
		if (crossed(before, t, 1.3F)) {
			b.burst(ParticleTypes.SPLASH, b.at(0, -30, 0), 16, 60, 0.0, 0.4, 0.0);
			b.burst(ParticleTypes.BUBBLE_POP, b.at(0, -24, 0), 14, 30, 0.0, 0.1, 0.0);
			b.sound(ModSounds.KRAKEN_EMERGE, 5.0F, 1.0F);
		}
	}

	private static void phaseChange(Body b, float t, float before) {
		boolean abyss = b.kraken.phase() >= 3;
		if (t < 1.1F && b.every(2)) {
			// Contração: partículas puxadas para o corpo, de todos os lados.
			for (int k = 0; k < 4; k++) {
				double ang = b.random.nextDouble() * Math.PI * 2;
				Vec3 from = b.at(Math.cos(ang) * 40, -20 + b.rand(-10, 10), Math.sin(ang) * 40);
				Vec3 to = b.at(0, -24, 0);
				Vec3 v = to.subtract(from).scale(0.08);
				b.emit(abyss ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.GLOW, from, v.x, v.y, v.z);
			}
		}
		if (crossed(before, t, 1.2F)) {
			// Explosão da nova fase: tinta para a 2 (Fúria), almas e roxo para a 3 (Abismo).
			b.burst(abyss ? ParticleTypes.SCULK_SOUL : ParticleTypes.SQUID_INK, b.at(0, -24, 0), 18, 50, 0.0, 0.05, 0.0);
			b.burst(abyss ? PURPLE : CRYSTAL, b.at(0, -24, 0), 22, 40, 0.0, 0.05, 0.0);
			b.ring(ParticleTypes.BUBBLE_POP, b.at(0, -16, 0), 30, 24, 0.25);
			b.ring(abyss ? PURPLE : CRYSTAL, b.at(0, -24, 0), 26, 32, 0.5);
		}
	}

	private static void death(Body b, float t, float before) {
		if (t < 1.6F && b.every(3)) {
			b.burst(ParticleTypes.SQUID_INK, b.at(0, -20, 0), 14, 6, 0.0, 0.02, 0.0);
		}
		if (crossed(before, t, 1.6F)) {
			b.burst(ParticleTypes.GLOW_SQUID_INK, b.at(0, -24, 0), 20, 50, 0.0, 0.05, 0.0);
			b.burst(SHARD, b.at(0, -30, 0), 14, 40, 0.0, 0.2, 0.0);
		}
		if (t > 1.6F && b.every(4)) {
			b.burst(ParticleTypes.BUBBLE, b.at(0, -20, 0), 12, 4, 0.0, 0.15, 0.0);
		}
	}

	// ------------------------------------------------------------------ ataques

	private static void slam(Body b, float t, float before) {
		float hit = KrakenAnimations.SLAM_IMPACT;
		if (t < hit && b.every(2)) {
			// O tentáculo da direita sobe: água escorre da ponta enquanto ele se ergue.
			float lift = Mth.clamp(t / (hit - 0.35F), 0.0F, 1.0F) * 70.0F;
			b.emit(ParticleTypes.FALLING_WATER, b.arm(2, 1.0F, lift), 0, 0, 0);
			b.emit(ParticleTypes.SPLASH, b.arm(2, 0.8F, lift), 0, 0.05, 0);
		}
		if (crossed(before, t, hit + 0.1F)) {
			b.burst(ParticleTypes.SPLASH, b.arm(2, 1.0F, -30), 8, 40, 0.0, 0.4, 0.0);
			b.ring(ParticleTypes.BUBBLE_POP, b.arm(2, 1.0F, -30), 16, 16, 0.2);
		}
	}

	private static void grab(Body b, float t, float before, int[] arms, float hit) {
		if (t < hit && b.every(1)) {
			// Brilho roxo correndo pelo tentáculo, cada vez mais forte até o bote.
			float charge = t / hit;
			for (int arm : arms) {
				float f = (float) b.random.nextDouble();
				b.emit(PURPLE, b.arm(arm, f, 20), 0, 0.01, 0);
				if (b.random.nextFloat() < charge) {
					b.emit(ParticleTypes.WITCH, b.arm(arm, 1.0F, 20), 0, 0.02, 0);
				}
			}
		}
		if (crossed(before, t, hit + 0.12F)) {
			for (int arm : arms) {
				b.burst(ParticleTypes.REVERSE_PORTAL, b.arm(arm, 1.0F, 0), 6, 24, 0.0, 0.05, 0.0);
			}
		}
	}

	private static void ink(Body b, float t, float before) {
		float hit = KrakenAnimations.INK_IMPACT;
		if (t < hit && b.every(1)) {
			// Água escurece em espiral, apertando em volta do corpo.
			double radius = 40 - 26 * (t / hit);
			for (int k = 0; k < 2; k++) {
				double ang = t * 5.0 + k * Math.PI;
				b.emit(ParticleTypes.SQUID_INK, b.at(Math.cos(ang) * radius, -20 + Math.sin(t * 3) * 4, Math.sin(ang) * radius),
					0, 0, 0);
			}
		}
		if (crossed(before, t, hit + 0.08F)) {
			b.burst(ParticleTypes.SQUID_INK, b.at(0, -20, 0), 30, 90, 0.0, 0.08, 0.0);
			b.burst(INK_DUST, b.at(0, -20, 0), 40, 60, 0.0, 0.0, 0.0);
		}
	}

	private static void whirlpool(Body b, float t) {
		if (b.every(1)) {
			// Espiral de bolhas e correnteza girando ao redor e afundando no centro.
			for (int k = 0; k < 3; k++) {
				double ang = t * 4.0 + k * Math.PI * 2 / 3;
				double r = 60 - (t * 20 % 40);
				Vec3 p = b.at(Math.cos(ang) * r, -16 - (r - 20) * 0.4, Math.sin(ang) * r);
				b.emit(ParticleTypes.BUBBLE, p, 0, -0.02, 0);
				b.emit(k == 0 ? TEAL_LIGHT : ParticleTypes.NAUTILUS, p, 0, 0, 0);
			}
			b.emit(ParticleTypes.CURRENT_DOWN, b.at(b.rand(-6, 6), -40, b.rand(-6, 6)), 0, 0, 0);
		}
	}

	private static void spawnYoung(Body b, float t, float before) {
		float hit = KrakenAnimations.SPAWN_YOUNG_IMPACT;
		if (t < hit && b.every(2)) {
			// Ovos brilhando por baixo do corpo, pulsando cada vez mais rápido.
			for (int k = 0; k < 3; k++) {
				double ang = k * Math.PI * 2 / 3 + t;
				Vec3 egg = b.at(Math.cos(ang) * 10, -2, Math.sin(ang) * 10 - 8);
				b.emit(EGG, egg, 0, 0, 0);
				if (b.random.nextFloat() < t / hit) {
					b.emit(ParticleTypes.GLOW, egg, 0, 0.03, 0);
				}
			}
		}
		if (crossed(before, t, hit + 0.08F)) {
			b.burst(ParticleTypes.EGG_CRACK, b.at(0, -2, -8), 14, 20, 0.0, 0.1, 0.0);
			b.burst(ParticleTypes.BUBBLE_POP, b.at(0, -2, -8), 14, 20, 0.0, 0.05, 0.0);
		}
	}

	private static void waterJet(Body b, float t, float before) {
		float hit = KrakenAnimations.WATER_JET_IMPACT;
		Vec3 mouth = b.at(0, -9, 16);
		if (t < hit && b.every(1)) {
			// Bolhas sugadas para o bico (a água entrando antes do disparo).
			Vec3 from = b.at(b.rand(-20, 20), -9 + b.rand(-12, 12), 40 + b.rand(0, 16));
			Vec3 v = mouth.subtract(from).scale(0.1);
			b.emit(ParticleTypes.BUBBLE, from, v.x, v.y, v.z);
			b.emit(t > hit * 0.5F ? TEAL_LIGHT : ParticleTypes.BUBBLE, b.at(b.rand(-3, 3), -9 + b.rand(-2, 2), 17), 0, 0, 0);
		}
		if (crossed(before, t, hit + 0.06F)) {
			Vec3 forward = b.at(0, -9, 17).subtract(b.at(0, -9, 16)).normalize();
			for (int k = 0; k < 24; k++) {
				double speed = 0.6 + k * 0.05;
				b.emit(ParticleTypes.BUBBLE, mouth, forward.x * speed, forward.y * speed + 0.02, forward.z * speed);
			}
			b.burst(ParticleTypes.SPLASH, mouth, 4, 20, 0.0, 0.2, 0.0);
		}
	}

	private static void roar(Body b, float t, float before) {
		float hit = KrakenAnimations.ROAR_IMPACT;
		if (t < hit && b.every(1)) {
			// Corpo se contrai: a água em volta é sugada para ele (e ele treme).
			for (int k = 0; k < 2; k++) {
				double ang = b.random.nextDouble() * Math.PI * 2;
				Vec3 from = b.at(Math.cos(ang) * 36, -18 + b.rand(-8, 8), Math.sin(ang) * 36);
				Vec3 v = b.at(0, -22, 0).subtract(from).scale(0.09);
				b.emit(ParticleTypes.BUBBLE, from, v.x, v.y, v.z);
			}
			if (t > hit - 0.5F) {
				b.emit(ParticleTypes.SCULK_CHARGE_POP, b.at(b.rand(-10, 10), -26, b.rand(-10, 10)), 0, 0.02, 0);
			}
		}
		if (crossed(before, t, hit + 0.08F)) {
			// Onda de choque: anel que se abre a partir do corpo.
			b.ring(TEAL_LIGHT, b.at(0, -22, 0), 14, 28, 0.7);
			b.ring(ParticleTypes.CLOUD, b.at(0, -14, 0), 20, 32, 0.6);
			b.ring(ParticleTypes.BUBBLE_POP, b.at(0, -14, 0), 24, 32, 0.45);
		}
	}

	private static void bolts(Body b, float t, float before) {
		float hit = KrakenAnimations.BOLTS_IMPACT;
		if (t > 0.6F && t < hit && b.every(2)) {
			// Quatro braços em antena: faíscas nas pontas e um brilho azul subindo.
			for (int arm = 0; arm < 8; arm += 2) {
				Vec3 tip = b.arm(arm, 1.0F, 80);
				b.emit(ParticleTypes.ELECTRIC_SPARK, tip, b.rand(-0.1, 0.1), 0.1, b.rand(-0.1, 0.1));
				if (b.random.nextFloat() < 0.4F) {
					b.emit(ParticleTypes.END_ROD, tip, 0, 0.12, 0);
				}
			}
		}
		if (crossed(before, t, hit + 0.05F)) {
			for (int arm = 0; arm < 8; arm += 2) {
				b.burst(ParticleTypes.ELECTRIC_SPARK, b.arm(arm, 1.0F, 80), 4, 20, 0.0, 0.3, 0.0);
			}
			b.burst(ParticleTypes.FIREWORK, b.at(0, -40, 0), 8, 16, 0.0, 0.2, 0.0);
		}
	}

	private static void ambush(Body b, float t, float before) {
		float hit = KrakenAnimations.AMBUSH_IMPACT;
		if (t > 0.4F && t < hit && b.every(2)) {
			// Braços cavando o fundo: bolhas sobem do chão em volta do corpo.
			for (int k = 0; k < 3; k++) {
				double ang = b.random.nextDouble() * Math.PI * 2;
				double r = 20 + b.random.nextDouble() * 30;
				b.emit(ParticleTypes.BUBBLE_COLUMN_UP, b.at(Math.cos(ang) * r, 22, Math.sin(ang) * r), 0, 0.2, 0);
			}
		}
		if (crossed(before, t, hit + 0.1F)) {
			b.ring(ParticleTypes.BUBBLE_POP, b.at(0, 22, 0), 40, 28, 0.15);
			b.burst(ParticleTypes.BUBBLE_COLUMN_UP, b.at(0, 22, 0), 30, 30, 0.0, 0.3, 0.0);
		}
	}

	/** Rastro ao nadar: bolhas soltas pelas pontas dos tentáculos e atrás do manto, mais quanto mais rápido. */
	private static void trail(Body b, KrakenEntity kraken) {
		double speed = Math.sqrt(Mth.square(kraken.getX() - kraken.xo) + Mth.square(kraken.getZ() - kraken.zo));
		if (speed < 0.02) {
			return;
		}
		int count = speed > 0.07 ? 3 : speed > 0.04 ? 2 : 1;
		for (int k = 0; k < count; k++) {
			b.emit(ParticleTypes.BUBBLE, b.arm(b.random.nextInt(8), 1.0F, -20), 0, 0.04, 0);
		}
		if (b.every(3)) {
			b.emit(ParticleTypes.BUBBLE_COLUMN_UP, b.at(b.rand(-6, 6), -26, -14), 0, 0.05, 0);
		}
	}

	// ------------------------------------------------------------------ escudo e rachaduras

	private static void shield(Body b, KrakenEntity kraken, Track track) {
		float now = kraken.shieldFraction();
		float before = track.shield;
		if (before >= 0.0F && kraken.phase() == track.phase) {
			for (float mark : CRACKS) {
				if (before > mark && now <= mark) {
					// Nova rachadura: estilhaços de cristal e estalo grave.
					b.burst(SHARD, b.at(0, -36, 0), 16, 24, 0.0, 0.15, 0.0);
					b.burst(CRYSTAL, b.at(0, -36, 0), 16, 16, 0.0, 0.05, 0.0);
					b.sound(ModSounds.KRAKEN_SHIELD_CRACK, 4.0F, 0.85F + mark * 0.3F);
				}
			}
			if (before > 0.0F && now <= 0.0F) {
				// A carapaça explode (o servidor toca o vidro e o grito).
				b.burst(SHARD, b.at(0, -24, 0), 26, 80, 0.0, 0.25, 0.0);
				b.burst(CRYSTAL, b.at(0, -24, 0), 26, 50, 0.0, 0.1, 0.0);
			}
			if (now - before > 0.15F) {
				// Escudo se refaz: brilho subindo em volta do manto.
				b.ring(ParticleTypes.END_ROD, b.at(0, -10, 0), 18, 24, 0.0);
				b.sound(ModSounds.KRAKEN_SHIELD_REGEN, 3.0F, 1.0F);
			}
		}
		// Rachaduras abertas soltam brilho; quanto menos escudo, mais.
		float open = now <= 0.25F ? 3 : now <= 0.5F ? 2 : now <= 0.75F ? 1 : 0;
		if (open > 0 && b.random.nextFloat() < open * 0.12F) {
			b.emit(ParticleTypes.GLOW, b.at(b.rand(-9, 9), -34, b.rand(-10, 10)), 0, 0.04, 0);
		}
		if (kraken.phase() >= 3 && b.every(5)) {
			b.emit(PURPLE, b.at(b.rand(-18, 18), b.rand(-30, 0), b.rand(-18, 18)), 0, 0.02, 0);
		}
		track.shield = now;
	}

	/** Pontos do corpo no mundo e emissão, na escala atual do Kraken. */
	private static final class Body {
		final ClientLevel level;
		final KrakenEntity kraken;
		final RandomSource random;
		final double scale;
		final Vec3 origin;
		final Vec3 forward;
		final Vec3 right;
		String anim = "";

		Body(ClientLevel level, KrakenEntity kraken) {
			this.level = level;
			this.kraken = kraken;
			this.random = kraken.getRandom();
			this.scale = kraken.getScale() * KrakenRenderer.MODEL_SCALE / 16.0;
			this.origin = kraken.position();
			float yaw = kraken.yBodyRot * Mth.DEG_TO_RAD;
			this.forward = new Vec3(-Mth.sin(yaw), 0, Mth.cos(yaw));
			this.right = new Vec3(-Mth.cos(yaw), 0, -Mth.sin(yaw));
		}

		/** Ponto do modelo (pixels; y para baixo, pés em 24, +z para a frente). */
		Vec3 at(double x, double y, double z) {
			return origin.add(right.scale(x * scale)).add(0, (24 - y) * scale - KrakenStats.MODEL_DROP, 0).add(forward.scale(z * scale));
		}

		/** Ponto ao longo do braço {@code arm} ({@code f} 0 a 1), erguido {@code lift} graus acima do horizontal. */
		Vec3 arm(int arm, float f, float lift) {
			double angle = Math.PI * 2 * arm / 8.0;
			// Braço da v2: 5 juntas de 6 px e as pontas de 6 e 4,5 px.
			double reach = 4 + f * ARM_LENGTH * Math.cos(Math.toRadians(lift));
			double up = f * ARM_LENGTH * Math.sin(Math.toRadians(lift));
			return at(Math.sin(angle) * (8 + reach), -10 - up, Math.cos(angle) * (7 + reach));
		}

		boolean every(int ticks) {
			return (kraken.tickCount + kraken.getId()) % ticks == 0;
		}

		double rand(double min, double max) {
			return min + random.nextDouble() * (max - min);
		}

		void emit(ParticleOptions type, Vec3 p, double vx, double vy, double vz) {
			level.addParticle(type, p.x, p.y, p.z, vx, vy, vz);
			EMITTED.merge(anim, 1, Integer::sum);
		}

		/** {@code count} partículas espalhadas num raio de {@code spread} pixels do modelo. */
		void burst(ParticleOptions type, Vec3 p, double spread, int count, double vx, double vy, double vz) {
			double s = spread * scale;
			for (int i = 0; i < count; i++) {
				emit(type, p.add(rand(-s, s), rand(-s, s) * 0.5, rand(-s, s)),
					vx + rand(-0.1, 0.1), vy + rand(0, 0.1), vz + rand(-0.1, 0.1));
			}
		}

		/** Anel de raio {@code radius} pixels, com velocidade para fora. */
		void ring(ParticleOptions type, Vec3 center, double radius, int points, double outward) {
			for (int i = 0; i < points; i++) {
				double ang = Math.PI * 2 * i / points;
				Vec3 dir = right.scale(Math.cos(ang)).add(forward.scale(Math.sin(ang)));
				emit(type, center.add(dir.scale(radius * scale)), dir.x * outward, 0.02, dir.z * outward);
			}
		}

		void sound(SoundEvent sound, float volume, float pitch) {
			level.playLocalSound(origin.x, origin.y + 2, origin.z, sound, SoundSource.HOSTILE, volume, pitch, false);
		}
	}

	private KrakenEffects() {}
}
