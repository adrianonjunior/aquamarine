package com.aquamarine.client.machine;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/** Clipes de Coletor de Neutrônio. GERADO por tools/models/machines/neutronium_collector/build_model.py: não edite à mão. */
public final class NeutroniumCollectorAnimations {
	private static final AnimationChannel.Target ROT = AnimationChannel.Targets.ROTATION;
	private static final AnimationChannel.Target POS = AnimationChannel.Targets.POSITION;
	private static final AnimationChannel.Target SCALE = AnimationChannel.Targets.SCALE;
	private static final AnimationChannel.Interpolation SMOOTH = AnimationChannel.Interpolations.CATMULLROM;
	private static final AnimationChannel.Interpolation LINEAR = AnimationChannel.Interpolations.LINEAR;

	/** Clipes pelo nome do contrato (idle, start, run, stop, overload...). */
	public static final Map<String, AnimationDefinition> CLIPS = clips();

	private static Map<String, AnimationDefinition> clips() {
		Map<String, AnimationDefinition> clips = new LinkedHashMap<>();
		clips.put("idle", idle());
		clips.put("start", start());
		clips.put("run", run());
		clips.put("stop", stop());
		clips.put("full", full());
		clips.put("collect", collect());
		clips.put("progress", progress());
		clips.put("stock", stock());
		clips.put("invalid", invalid());
		return clips;
	}

	private static AnimationDefinition idle() {
		return AnimationDefinition.Builder.withLength(4F).looping()
			.addAnimation("core", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(1.1F, scale(1.04F, 1.04F, 1.04F), SMOOTH),
				k(2.2F, scale(0.98F, 0.98F, 0.98F), SMOOTH),
				k(3.2F, scale(1.03F, 1.03F, 1.03F), SMOOTH),
				k(4F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("orbit", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(4F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(1.4F, rot(0F, 0F, 1.5F), SMOOTH),
				k(2.8F, rot(0F, 0F, -1F), SMOOTH),
				k(4F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("drop", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("lamp_red", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), SMOOTH),
				k(4F, scale(0.01F, 0.01F, 0.01F), SMOOTH)))
			.addAnimation("lamp_amber", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(4F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("lamp_blue", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), SMOOTH),
				k(4F, scale(0.01F, 0.01F, 0.01F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition start() {
		return AnimationDefinition.Builder.withLength(1.6F)
			.addAnimation("core", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.45F, scale(1.03F, 1.03F, 1.03F), SMOOTH),
				k(1.1F, scale(0.98F, 0.98F, 0.98F), SMOOTH),
				k(1.6F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("ring_y", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.35F, rot(0F, 4F, 0F), SMOOTH),
				k(0.8F, rot(0F, 22F, 0F), SMOOTH),
				k(1.2F, rot(0F, 58F, 0F), SMOOTH),
				k(1.6F, rot(0F, 92F, 0F), SMOOTH)))
			.addAnimation("ring_x", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(0F, -3F, 0F), SMOOTH),
				k(1F, rot(0F, -25F, 0F), SMOOTH),
				k(1.6F, rot(0F, -86F, 0F), SMOOTH)))
			.addAnimation("ring_z", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.6F, rot(0F, 0F, 2F), SMOOTH),
				k(1.15F, rot(0F, 0F, 30F), SMOOTH),
				k(1.6F, rot(0F, 0F, 78F), SMOOTH)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.7F, rot(0F, 0F, 25F), SMOOTH),
				k(1.25F, rot(0F, 0F, 132F), SMOOTH),
				k(1.45F, rot(0F, 0F, 118F), SMOOTH),
				k(1.6F, rot(0F, 0F, 122F), SMOOTH)))
			.addAnimation("drop", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(1.6F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("lamp_red", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), SMOOTH),
				k(1.6F, scale(0.01F, 0.01F, 0.01F), SMOOTH)))
			.addAnimation("lamp_amber", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(1.6F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("lamp_blue", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(1.6F, scale(1F, 1F, 1F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition run() {
		return AnimationDefinition.Builder.withLength(2.4F).looping()
			.addAnimation("ring_y", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.3F, rot(0F, 47.8284F, 0F), LINEAR),
				k(0.6F, rot(0F, 94F, 0F), LINEAR),
				k(0.9F, rot(0F, 137.8284F, 0F), LINEAR),
				k(1.2F, rot(0F, 180F, 0F), LINEAR),
				k(1.5F, rot(0F, 222.1716F, 0F), LINEAR),
				k(1.8F, rot(0F, 266F, 0F), LINEAR),
				k(2.1F, rot(0F, 312.1716F, 0F), LINEAR),
				k(2.4F, rot(0F, 0F, 0F), LINEAR)))
			.addAnimation("ring_x", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.3F, rot(0F, -58.3713F, 0F), LINEAR),
				k(0.6F, rot(0F, -115.5F, 0F), LINEAR),
				k(0.9F, rot(0F, -170.8713F, 0F), LINEAR),
				k(1.2F, rot(0F, -225F, 0F), LINEAR),
				k(1.5F, rot(0F, -279.1287F, 0F), LINEAR),
				k(1.8F, rot(0F, -334.5F, 0F), LINEAR),
				k(2.1F, rot(0F, -391.6287F, 0F), LINEAR),
				k(2.4F, rot(0F, 0F, 0F), LINEAR)))
			.addAnimation("ring_z", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.3F, rot(0F, 0F, 35.1642F), LINEAR),
				k(0.6F, rot(0F, 0F, 69.5F), LINEAR),
				k(0.9F, rot(0F, 0F, 102.6642F), LINEAR),
				k(1.2F, rot(0F, 0F, 135F), LINEAR),
				k(1.5F, rot(0F, 0F, 167.3358F), LINEAR),
				k(1.8F, rot(0F, 0F, 200.5F), LINEAR),
				k(2.1F, rot(0F, 0F, 234.8358F), LINEAR),
				k(2.4F, rot(0F, 0F, 0F), LINEAR)))
			.addAnimation("orbit", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.3F, rot(0F, 45F, 0F), LINEAR),
				k(0.6F, rot(0F, 90F, 0F), LINEAR),
				k(0.9F, rot(0F, 135F, 0F), LINEAR),
				k(1.2F, rot(0F, 180F, 0F), LINEAR),
				k(1.5F, rot(0F, 225F, 0F), LINEAR),
				k(1.8F, rot(0F, 270F, 0F), LINEAR),
				k(2.1F, rot(0F, 315F, 0F), LINEAR),
				k(2.4F, rot(0F, 0F, 0F), LINEAR)))
			.addAnimation("core", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.3F, scale(1.05F, 1.05F, 1.05F), SMOOTH),
				k(0.6F, scale(0.98F, 0.98F, 0.98F), SMOOTH),
				k(0.9F, scale(1.04F, 1.04F, 1.04F), SMOOTH),
				k(1.2F, scale(1F, 1F, 1F), SMOOTH),
				k(1.5F, scale(1.06F, 1.06F, 1.06F), SMOOTH),
				k(1.8F, scale(0.99F, 0.99F, 0.99F), SMOOTH),
				k(2.1F, scale(1.03F, 1.03F, 1.03F), SMOOTH),
				k(2.4F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 120F), SMOOTH),
				k(0.4F, rot(0F, 0F, 128F), SMOOTH),
				k(0.8F, rot(0F, 0F, 116F), SMOOTH),
				k(1.2F, rot(0F, 0F, 126F), SMOOTH),
				k(1.6F, rot(0F, 0F, 119F), SMOOTH),
				k(2F, rot(0F, 0F, 127F), SMOOTH),
				k(2.4F, rot(0F, 0F, 120F), SMOOTH)))
			.addAnimation("arc_0", new AnimationChannel(SCALE,
				k(0F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(0.18F, scale(1.25F, 1.25F, 1.25F), SMOOTH),
				k(0.42F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(2.4F, scale(0.05F, 0.05F, 0.05F), SMOOTH)))
			.addAnimation("orbit_dust_0", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.6F, pos(0F, 0.5F, 0F), SMOOTH),
				k(1.2F, pos(0F, -0.25F, 0F), SMOOTH),
				k(1.8F, pos(0F, 0.4F, 0F), SMOOTH),
				k(2.4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arc_1", new AnimationChannel(SCALE,
				k(0F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(0.3F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(0.48F, scale(1.25F, 1.25F, 1.25F), SMOOTH),
				k(0.72F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(2.4F, scale(0.05F, 0.05F, 0.05F), SMOOTH)))
			.addAnimation("orbit_dust_1", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.6F, pos(0F, 0.5F, 0F), SMOOTH),
				k(1.2F, pos(0F, -0.25F, 0F), SMOOTH),
				k(1.8F, pos(0F, 0.4F, 0F), SMOOTH),
				k(2.4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arc_2", new AnimationChannel(SCALE,
				k(0F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(0.6F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(0.78F, scale(1.25F, 1.25F, 1.25F), SMOOTH),
				k(1.02F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(2.4F, scale(0.05F, 0.05F, 0.05F), SMOOTH)))
			.addAnimation("orbit_dust_2", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.6F, pos(0F, 0.5F, 0F), SMOOTH),
				k(1.2F, pos(0F, -0.25F, 0F), SMOOTH),
				k(1.8F, pos(0F, 0.4F, 0F), SMOOTH),
				k(2.4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arc_3", new AnimationChannel(SCALE,
				k(0F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(0.9F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(1.08F, scale(1.25F, 1.25F, 1.25F), SMOOTH),
				k(1.32F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(2.4F, scale(0.05F, 0.05F, 0.05F), SMOOTH)))
			.addAnimation("orbit_dust_3", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.6F, pos(0F, 0.5F, 0F), SMOOTH),
				k(1.2F, pos(0F, -0.25F, 0F), SMOOTH),
				k(1.8F, pos(0F, 0.4F, 0F), SMOOTH),
				k(2.4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arc_4", new AnimationChannel(SCALE,
				k(0F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(1.2F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(1.38F, scale(1.25F, 1.25F, 1.25F), SMOOTH),
				k(1.62F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(2.4F, scale(0.05F, 0.05F, 0.05F), SMOOTH)))
			.addAnimation("orbit_dust_4", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.6F, pos(0F, 0.5F, 0F), SMOOTH),
				k(1.2F, pos(0F, -0.25F, 0F), SMOOTH),
				k(1.8F, pos(0F, 0.4F, 0F), SMOOTH),
				k(2.4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arc_5", new AnimationChannel(SCALE,
				k(0F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(1.5F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(1.68F, scale(1.25F, 1.25F, 1.25F), SMOOTH),
				k(1.92F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(2.4F, scale(0.05F, 0.05F, 0.05F), SMOOTH)))
			.addAnimation("orbit_dust_5", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.6F, pos(0F, 0.5F, 0F), SMOOTH),
				k(1.2F, pos(0F, -0.25F, 0F), SMOOTH),
				k(1.8F, pos(0F, 0.4F, 0F), SMOOTH),
				k(2.4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arc_6", new AnimationChannel(SCALE,
				k(0F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(1.8F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(1.98F, scale(1.25F, 1.25F, 1.25F), SMOOTH),
				k(2.22F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(2.4F, scale(0.05F, 0.05F, 0.05F), SMOOTH)))
			.addAnimation("orbit_dust_6", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.6F, pos(0F, 0.5F, 0F), SMOOTH),
				k(1.2F, pos(0F, -0.25F, 0F), SMOOTH),
				k(1.8F, pos(0F, 0.4F, 0F), SMOOTH),
				k(2.4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("arc_7", new AnimationChannel(SCALE,
				k(0F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(0.12F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(2.1F, scale(0.05F, 0.05F, 0.05F), SMOOTH),
				k(2.28F, scale(1.25F, 1.25F, 1.25F), SMOOTH),
				k(2.4F, scale(0.05F, 0.05F, 0.05F), SMOOTH)))
			.addAnimation("orbit_dust_7", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.6F, pos(0F, 0.5F, 0F), SMOOTH),
				k(1.2F, pos(0F, -0.25F, 0F), SMOOTH),
				k(1.8F, pos(0F, 0.4F, 0F), SMOOTH),
				k(2.4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("drop", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(2.4F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("lamp_red", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), SMOOTH),
				k(2.4F, scale(0.01F, 0.01F, 0.01F), SMOOTH)))
			.addAnimation("lamp_amber", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), SMOOTH),
				k(2.4F, scale(0.01F, 0.01F, 0.01F), SMOOTH)))
			.addAnimation("lamp_blue", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(2.4F, scale(1F, 1F, 1F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition stop() {
		return AnimationDefinition.Builder.withLength(1.25F)
			.addAnimation("ring_y", new AnimationChannel(ROT,
				k(0F, rot(0F, 95F, 0F), SMOOTH),
				k(0.35F, rot(0F, 145F, 0F), SMOOTH),
				k(0.75F, rot(0F, 172F, 0F), SMOOTH),
				k(1.05F, rot(0F, 180F, 0F), SMOOTH),
				k(1.25F, rot(0F, 180F, 0F), SMOOTH)))
			.addAnimation("ring_x", new AnimationChannel(ROT,
				k(0F, rot(0F, -90F, 0F), SMOOTH),
				k(0.4F, rot(0F, -132F, 0F), SMOOTH),
				k(0.8F, rot(0F, -154F, 0F), SMOOTH),
				k(1.25F, rot(0F, -160F, 0F), SMOOTH)))
			.addAnimation("ring_z", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 80F), SMOOTH),
				k(0.35F, rot(0F, 0F, 116F), SMOOTH),
				k(0.8F, rot(0F, 0F, 130F), SMOOTH),
				k(1.25F, rot(0F, 0F, 132F), SMOOTH)))
			.addAnimation("core", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.25F, scale(1.08F, 1.08F, 1.08F), SMOOTH),
				k(0.65F, scale(0.98F, 0.98F, 0.98F), SMOOTH),
				k(1.25F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 120F), SMOOTH),
				k(0.45F, rot(0F, 0F, 78F), SMOOTH),
				k(0.95F, rot(0F, 0F, -5F), SMOOTH),
				k(1.25F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("drop", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(1.25F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("lamp_red", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), SMOOTH),
				k(1.25F, scale(0.01F, 0.01F, 0.01F), SMOOTH)))
			.addAnimation("lamp_amber", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(1.25F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("lamp_blue", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(1.25F, scale(1F, 1F, 1F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition full() {
		return AnimationDefinition.Builder.withLength(2F).looping()
			.addAnimation("ring_y", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.25F, rot(0F, 8.1F, 0F), LINEAR),
				k(0.5F, rot(0F, 16.2F, 0F), LINEAR),
				k(0.75F, rot(0F, 24.3F, 0F), LINEAR),
				k(1F, rot(0F, 32.4F, 0F), LINEAR),
				k(1.25F, rot(0F, 40.5F, 0F), LINEAR),
				k(1.5F, rot(0F, 48.6F, 0F), LINEAR),
				k(1.75F, rot(0F, 56.7F, 0F), LINEAR),
				k(2F, rot(0F, 0F, 0F), LINEAR)))
			.addAnimation("ring_x", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.25F, rot(0F, -6.3F, 0F), LINEAR),
				k(0.5F, rot(0F, -12.6F, 0F), LINEAR),
				k(0.75F, rot(0F, -18.9F, 0F), LINEAR),
				k(1F, rot(0F, -25.2F, 0F), LINEAR),
				k(1.25F, rot(0F, -31.5F, 0F), LINEAR),
				k(1.5F, rot(0F, -37.8F, 0F), LINEAR),
				k(1.75F, rot(0F, -44.1F, 0F), LINEAR),
				k(2F, rot(0F, 0F, 0F), LINEAR)))
			.addAnimation("ring_z", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), LINEAR),
				k(0.25F, rot(0F, 0F, 4.5F), LINEAR),
				k(0.5F, rot(0F, 0F, 9F), LINEAR),
				k(0.75F, rot(0F, 0F, 13.5F), LINEAR),
				k(1F, rot(0F, 0F, 18F), LINEAR),
				k(1.25F, rot(0F, 0F, 22.5F), LINEAR),
				k(1.5F, rot(0F, 0F, 27F), LINEAR),
				k(1.75F, rot(0F, 0F, 31.5F), LINEAR),
				k(2F, rot(0F, 0F, 0F), LINEAR)))
			.addAnimation("core", new AnimationChannel(SCALE,
				k(0F, scale(1.12F, 1.12F, 1.12F), SMOOTH),
				k(0.5F, scale(1.16F, 1.16F, 1.16F), SMOOTH),
				k(1F, scale(1.12F, 1.12F, 1.12F), SMOOTH),
				k(1.5F, scale(1.16F, 1.16F, 1.16F), SMOOTH),
				k(2F, scale(1.12F, 1.12F, 1.12F), SMOOTH)))
			.addAnimation("lamp_blue", new AnimationChannel(SCALE,
				k(0F, scale(0.35F, 0.35F, 0.35F), SMOOTH),
				k(0.2F, scale(1.2F, 1.2F, 1.2F), SMOOTH),
				k(0.65F, scale(0.55F, 0.55F, 0.55F), SMOOTH),
				k(1F, scale(1.2F, 1.2F, 1.2F), SMOOTH),
				k(1.45F, scale(0.35F, 0.35F, 0.35F), SMOOTH),
				k(2F, scale(0.35F, 0.35F, 0.35F), SMOOTH)))
			.addAnimation("lamp_red", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), SMOOTH),
				k(2F, scale(0.01F, 0.01F, 0.01F), SMOOTH)))
			.addAnimation("lamp_amber", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), SMOOTH),
				k(2F, scale(0.01F, 0.01F, 0.01F), SMOOTH)))
			.addAnimation("drop", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(2F, scale(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition collect() {
		return AnimationDefinition.Builder.withLength(1.2F)
			.addAnimation("core", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.18F, scale(1.5F, 1.5F, 1.5F), SMOOTH),
				k(0.36F, scale(0.78F, 0.78F, 0.78F), SMOOTH),
				k(0.62F, scale(1.12F, 1.12F, 1.12F), SMOOTH),
				k(1.2F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("ring_y", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.18F, rot(0F, 55F, 0F), SMOOTH),
				k(0.48F, rot(0F, 72F, 0F), SMOOTH),
				k(0.82F, rot(0F, 104F, 0F), SMOOTH),
				k(1.2F, rot(0F, 160F, 0F), SMOOTH)))
			.addAnimation("ring_x", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.18F, rot(0F, -48F, 0F), SMOOTH),
				k(0.5F, rot(0F, -64F, 0F), SMOOTH),
				k(1.2F, rot(0F, -145F, 0F), SMOOTH)))
			.addAnimation("ring_z", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.2F, rot(0F, 0F, 42F), SMOOTH),
				k(0.5F, rot(0F, 0F, 55F), SMOOTH),
				k(1.2F, rot(0F, 0F, 138F), SMOOTH)))
			.addAnimation("drop", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), SMOOTH),
				k(0.28F, scale(0F, 0F, 0F), SMOOTH),
				k(0.34F, scale(1F, 1F, 1F), SMOOTH),
				k(0.9F, scale(1F, 1F, 1F), SMOOTH),
				k(0.96F, scale(0F, 0F, 0F), SMOOTH),
				k(1.2F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("drop", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.34F, pos(0F, 0F, 0F), SMOOTH),
				k(0.58F, pos(0F, -7F, -2F), SMOOTH),
				k(0.82F, pos(0F, -13F, -5F), SMOOTH),
				k(0.96F, pos(0F, -15F, -7F), SMOOTH),
				k(1.2F, pos(0F, -15F, -7F), SMOOTH)))
			.addAnimation("lamp_red", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), SMOOTH),
				k(1.2F, scale(0.01F, 0.01F, 0.01F), SMOOTH)))
			.addAnimation("lamp_amber", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), SMOOTH),
				k(1.2F, scale(0.01F, 0.01F, 0.01F), SMOOTH)))
			.addAnimation("lamp_blue", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(1.2F, scale(1F, 1F, 1F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition progress() {
		return AnimationDefinition.Builder.withLength(1F)
			.addAnimation("charge", new AnimationChannel(SCALE,
				k(0F, scale(0.28F, 0.28F, 0.28F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("orbit_dust_0", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(1F, pos(0F, 0F, 0F), LINEAR)))
			.addAnimation("orbit_dust_1", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(1F, pos(0F, -0.04F, 0F), LINEAR)))
			.addAnimation("orbit_dust_2", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(1F, pos(0F, -0.08F, 0F), LINEAR)))
			.addAnimation("orbit_dust_3", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(1F, pos(0F, -0.12F, 0F), LINEAR)))
			.addAnimation("orbit_dust_4", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(1F, pos(0F, -0.16F, 0F), LINEAR)))
			.addAnimation("orbit_dust_5", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(1F, pos(0F, -0.2F, 0F), LINEAR)))
			.addAnimation("orbit_dust_6", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(1F, pos(0F, -0.24F, 0F), LINEAR)))
			.addAnimation("orbit_dust_7", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), LINEAR),
				k(1F, pos(0F, -0.28F, 0F), LINEAR)))
			.build();
	}

	private static AnimationDefinition stock() {
		return AnimationDefinition.Builder.withLength(1F)
			.addAnimation("tray_dust_0", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.0125F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.0625F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_1", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.075F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.125F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_2", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.1375F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.1875F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_3", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.2F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.25F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_4", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.2625F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.3125F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_5", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.325F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.375F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_6", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.3875F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.4375F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_7", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.45F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.5F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_8", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.5125F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.5625F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_9", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.575F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.625F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_10", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.6375F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.6875F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_11", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.7F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.75F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_12", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.7625F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.8125F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_13", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.825F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.875F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_14", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.8875F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.9375F, scale(1F, 1F, 1F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.addAnimation("tray_dust_15", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(0.95F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(1F, scale(1F, 1F, 1F), LINEAR)))
			.build();
	}

	private static AnimationDefinition invalid() {
		return AnimationDefinition.Builder.withLength(1F)
			.addAnimation("lamp_red", new AnimationChannel(SCALE,
				k(0F, scale(1.5F, 1.5F, 1.5F), LINEAR),
				k(0.15F, scale(2.2F, 2.2F, 2.2F), LINEAR),
				k(0.32F, scale(1.7F, 1.7F, 1.7F), LINEAR),
				k(0.5F, scale(2.4F, 2.4F, 2.4F), LINEAR),
				k(0.68F, scale(1.6F, 1.6F, 1.6F), LINEAR),
				k(0.85F, scale(2.1F, 2.1F, 2.1F), LINEAR),
				k(1F, scale(1.5F, 1.5F, 1.5F), LINEAR)))
			.addAnimation("lamp_amber", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(1F, scale(0.01F, 0.01F, 0.01F), LINEAR)))
			.addAnimation("lamp_blue", new AnimationChannel(SCALE,
				k(0F, scale(0.01F, 0.01F, 0.01F), LINEAR),
				k(1F, scale(0.01F, 0.01F, 0.01F), LINEAR)))
			.build();
	}

	private static Keyframe k(float time, Vector3f vector, AnimationChannel.Interpolation interpolation) {
		return new Keyframe(time, vector, interpolation);
	}

	private static Vector3f rot(float x, float y, float z) {
		return KeyframeAnimations.degreeVec(x, y, z);
	}

	private static Vector3f pos(float x, float y, float z) {
		return KeyframeAnimations.posVec(x, y, z);
	}

	private static Vector3f scale(float x, float y, float z) {
		return KeyframeAnimations.scaleVec(x, y, z);
	}

	private NeutroniumCollectorAnimations() {}
}
