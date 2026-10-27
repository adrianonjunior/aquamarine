package com.aquamarine.client.machine;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/** Clipes de Gerador de Fenda. GERADO por tools/models/machines/vent_generator/build_model.py: não edite à mão. */
public final class VentGeneratorAnimations {
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
		clips.put("overload", overload());
		return clips;
	}

	private static AnimationDefinition idle() {
		return AnimationDefinition.Builder.withLength(4F).looping()
			.addAnimation("magma", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(2F, scale(1.01F, 1.04F, 1.01F), SMOOTH),
				k(4F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("blob_1", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), LINEAR),
				k(4F, scale(0F, 0F, 0F), LINEAR)))
			.addAnimation("blob_3", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), LINEAR),
				k(4F, scale(0F, 0F, 0F), LINEAR)))
			.addAnimation("blob_2", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), LINEAR),
				k(1.4F, scale(0F, 0F, 0F), LINEAR),
				k(2.6F, scale(0.8F, 0.8F, 0.8F), SMOOTH),
				k(3F, scale(1.15F, 1.15F, 1.15F), SMOOTH),
				k(3.05F, scale(0F, 0F, 0F), LINEAR),
				k(4F, scale(0F, 0F, 0F), LINEAR)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(1.3F, rot(0F, 0F, 0.8F), SMOOTH),
				k(2.4F, rot(0F, 0F, -0.6F), SMOOTH),
				k(4F, rot(0F, 0F, 0F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition start() {
		return AnimationDefinition.Builder.withLength(1.6F)
			.addAnimation("valve", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.15F, rot(0F, 20F, 0F), SMOOTH),
				k(0.7F, rot(0F, 250F, 0F), SMOOTH),
				k(0.85F, rot(0F, 270F, 0F), SMOOTH),
				k(1.6F, rot(0F, 270F, 0F), SMOOTH)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.45F, rot(0F, 0F, 10F), SMOOTH),
				k(1.05F, rot(0F, 0F, 142F), SMOOTH),
				k(1.35F, rot(0F, 0F, 127F), SMOOTH),
				k(1.6F, rot(0F, 0F, 130F), SMOOTH)))
			.addAnimation("piston_l", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0F, 0F), SMOOTH),
				k(0.88F, pos(0F, -0.55F, 0F), SMOOTH),
				k(1.22F, pos(0F, 0.25F, 0F), SMOOTH),
				k(1.6F, pos(0F, 1F, 0F), SMOOTH)))
			.addAnimation("piston_r", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0F, 0F), SMOOTH),
				k(0.88F, pos(0F, 0.55F, 0F), SMOOTH),
				k(1.22F, pos(0F, -0.25F, 0F), SMOOTH),
				k(1.6F, pos(0F, -1F, 0F), SMOOTH)))
			.addAnimation("beam", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.5F, rot(0F, 0F, 0F), SMOOTH),
				k(0.88F, rot(0F, 0F, -5.4262F), SMOOTH),
				k(1.22F, rot(0F, 0F, 2.4665F), SMOOTH),
				k(1.6F, rot(0F, 0F, 9.8658F), SMOOTH)))
			.addAnimation("magma", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.6F, scale(1.01F, 1.05F, 1.01F), SMOOTH),
				k(1.6F, scale(1.03F, 1.0787F, 1.03F), SMOOTH)))
			.addAnimation("chimney_cap", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(1.2F, rot(0F, 0F, 0F), SMOOTH),
				k(1.36F, rot(-26F, 0F, 0F), SMOOTH),
				k(1.6F, rot(-4F, 0F, 0F), SMOOTH)))
			.addAnimation("blob_1", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), LINEAR),
				k(0.8F, scale(0F, 0F, 0F), LINEAR),
				k(1.6F, scale(0F, 0F, 0F), SMOOTH)))
			.addAnimation("blob_2", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), LINEAR),
				k(0.8F, scale(0F, 0F, 0F), LINEAR),
				k(1.6F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("blob_3", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), LINEAR),
				k(0.8F, scale(0F, 0F, 0F), LINEAR),
				k(1.6F, scale(1F, 1F, 1F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition run() {
		return AnimationDefinition.Builder.withLength(1.2F).looping()
			.addAnimation("piston_l", new AnimationChannel(POS,
				k(0F, pos(0F, 1F, 0F), SMOOTH),
				k(0.15F, pos(0F, 0.7071F, 0F), SMOOTH),
				k(0.3F, pos(0F, 0F, 0F), SMOOTH),
				k(0.45F, pos(0F, -0.7071F, 0F), SMOOTH),
				k(0.6F, pos(0F, -1F, 0F), SMOOTH),
				k(0.75F, pos(0F, -0.7071F, 0F), SMOOTH),
				k(0.9F, pos(0F, 0F, 0F), SMOOTH),
				k(1.05F, pos(0F, 0.7071F, 0F), SMOOTH),
				k(1.2F, pos(0F, 1F, 0F), SMOOTH)))
			.addAnimation("piston_r", new AnimationChannel(POS,
				k(0F, pos(0F, -1F, 0F), SMOOTH),
				k(0.15F, pos(0F, -0.7071F, 0F), SMOOTH),
				k(0.3F, pos(0F, 0F, 0F), SMOOTH),
				k(0.45F, pos(0F, 0.7071F, 0F), SMOOTH),
				k(0.6F, pos(0F, 1F, 0F), SMOOTH),
				k(0.75F, pos(0F, 0.7071F, 0F), SMOOTH),
				k(0.9F, pos(0F, 0F, 0F), SMOOTH),
				k(1.05F, pos(0F, -0.7071F, 0F), SMOOTH),
				k(1.2F, pos(0F, -1F, 0F), SMOOTH)))
			.addAnimation("beam", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 9.8658F), SMOOTH),
				k(0.15F, rot(0F, 0F, 6.9762F), SMOOTH),
				k(0.3F, rot(0F, 0F, 0F), SMOOTH),
				k(0.45F, rot(0F, 0F, -6.9762F), SMOOTH),
				k(0.6F, rot(0F, 0F, -9.8658F), SMOOTH),
				k(0.75F, rot(0F, 0F, -6.9762F), SMOOTH),
				k(0.9F, rot(0F, 0F, 0F), SMOOTH),
				k(1.05F, rot(0F, 0F, 6.9762F), SMOOTH),
				k(1.2F, rot(0F, 0F, 9.8658F), SMOOTH)))
			.addAnimation("magma", new AnimationChannel(SCALE,
				k(0F, scale(1.03F, 1.0787F, 1.03F), SMOOTH),
				k(0.15F, scale(1.03F, 1.0709F, 1.03F), SMOOTH),
				k(0.3F, scale(1.03F, 1.0918F, 1.03F), SMOOTH),
				k(0.45F, scale(1.03F, 1.1292F, 1.03F), SMOOTH),
				k(0.6F, scale(1.03F, 1.1613F, 1.03F), SMOOTH),
				k(0.75F, scale(1.03F, 1.1691F, 1.03F), SMOOTH),
				k(0.9F, scale(1.03F, 1.1482F, 1.03F), SMOOTH),
				k(1.05F, scale(1.03F, 1.1108F, 1.03F), SMOOTH),
				k(1.2F, scale(1.03F, 1.0787F, 1.03F), SMOOTH)))
			.addAnimation("chimney_cap", new AnimationChannel(ROT,
				k(0F, rot(-4F, 0F, 0F), SMOOTH),
				k(0.12F, rot(-24F, 0F, 0F), SMOOTH),
				k(0.32F, rot(-7F, 0F, 0F), SMOOTH),
				k(0.6F, rot(-4F, 0F, 0F), SMOOTH),
				k(0.72F, rot(-24F, 0F, 0F), SMOOTH),
				k(0.92F, rot(-7F, 0F, 0F), SMOOTH),
				k(1.2F, rot(-4F, 0F, 0F), SMOOTH)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 130F), SMOOTH),
				k(0.3F, rot(0F, 0F, 133F), SMOOTH),
				k(0.6F, rot(0F, 0F, 128F), SMOOTH),
				k(0.9F, rot(0F, 0F, 132F), SMOOTH),
				k(1.2F, rot(0F, 0F, 130F), SMOOTH)))
			.addAnimation("valve", new AnimationChannel(ROT,
				k(0F, rot(0F, 270F, 0F), SMOOTH),
				k(1.2F, rot(0F, 270F, 0F), SMOOTH)))
			.addAnimation("blob_1", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), LINEAR),
				k(0.3375F, scale(1F, 1F, 1F), LINEAR),
				k(0.45F, scale(1.25F, 1.25F, 1.25F), SMOOTH),
				k(0.5F, scale(0F, 0F, 0F), LINEAR),
				k(1.2F, scale(0F, 0F, 0F), LINEAR)))
			.addAnimation("blob_2", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.42F, scale(0F, 0F, 0F), LINEAR),
				k(0.705F, scale(1F, 1F, 1F), LINEAR),
				k(0.8F, scale(1.1F, 1.1F, 1.1F), SMOOTH),
				k(0.85F, scale(0F, 0F, 0F), LINEAR),
				k(1.2F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("blob_3", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.8F, scale(0F, 0F, 0F), LINEAR),
				k(1.04F, scale(1F, 1F, 1F), LINEAR),
				k(1.12F, scale(1.3F, 1.3F, 1.3F), SMOOTH),
				k(1.17F, scale(0F, 0F, 0F), LINEAR),
				k(1.2F, scale(1F, 1F, 1F), SMOOTH)))
			.build();
	}

	private static AnimationDefinition stop() {
		return AnimationDefinition.Builder.withLength(1.4F)
			.addAnimation("piston_l", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.35F, pos(0F, 0.55F, 0F), SMOOTH),
				k(0.75F, pos(0F, -0.25F, 0F), SMOOTH),
				k(1.1F, pos(0F, 0.06F, 0F), SMOOTH),
				k(1.4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("piston_r", new AnimationChannel(POS,
				k(0F, pos(0F, 0F, 0F), SMOOTH),
				k(0.35F, pos(0F, -0.55F, 0F), SMOOTH),
				k(0.75F, pos(0F, 0.25F, 0F), SMOOTH),
				k(1.1F, pos(0F, -0.06F, 0F), SMOOTH),
				k(1.4F, pos(0F, 0F, 0F), SMOOTH)))
			.addAnimation("beam", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 0F), SMOOTH),
				k(0.35F, rot(0F, 0F, 5.4262F), SMOOTH),
				k(0.75F, rot(0F, 0F, -2.4665F), SMOOTH),
				k(1.1F, rot(0F, 0F, 0.5919F), SMOOTH),
				k(1.4F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("valve", new AnimationChannel(ROT,
				k(0F, rot(0F, 270F, 0F), SMOOTH),
				k(0.2F, rot(0F, 255F, 0F), SMOOTH),
				k(0.95F, rot(0F, 10F, 0F), SMOOTH),
				k(1.1F, rot(0F, 0F, 0F), SMOOTH),
				k(1.4F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 130F), SMOOTH),
				k(0.3F, rot(0F, 0F, 120F), SMOOTH),
				k(0.95F, rot(0F, 0F, -8F), SMOOTH),
				k(1.15F, rot(0F, 0F, 3F), SMOOTH),
				k(1.4F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("magma", new AnimationChannel(SCALE,
				k(0F, scale(1.03F, 1.12F, 1.03F), SMOOTH),
				k(1.4F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("chimney_cap", new AnimationChannel(ROT,
				k(0F, rot(-4F, 0F, 0F), SMOOTH),
				k(0.2F, rot(-26F, 0F, 0F), SMOOTH),
				k(0.55F, rot(-3F, 0F, 0F), SMOOTH),
				k(0.7F, rot(0F, 0F, 0F), SMOOTH),
				k(1.4F, rot(0F, 0F, 0F), SMOOTH)))
			.addAnimation("blob_1", new AnimationChannel(SCALE,
				k(0F, scale(0.6F, 0.6F, 0.6F), SMOOTH),
				k(0.3F, scale(0F, 0F, 0F), LINEAR),
				k(1.4F, scale(0F, 0F, 0F), LINEAR)))
			.addAnimation("blob_2", new AnimationChannel(SCALE,
				k(0F, scale(0.6F, 0.6F, 0.6F), SMOOTH),
				k(0.3F, scale(0F, 0F, 0F), LINEAR),
				k(1.4F, scale(0F, 0F, 0F), LINEAR)))
			.addAnimation("blob_3", new AnimationChannel(SCALE,
				k(0F, scale(0.6F, 0.6F, 0.6F), SMOOTH),
				k(0.3F, scale(0F, 0F, 0F), LINEAR),
				k(1.4F, scale(0F, 0F, 0F), LINEAR)))
			.build();
	}

	private static AnimationDefinition overload() {
		return AnimationDefinition.Builder.withLength(0.5F).looping()
			.addAnimation("works", new AnimationChannel(POS,
				k(0F, pos(0.12F, 0.05F, 0.1F), LINEAR),
				k(0.05F, pos(-0.12F, -0.05F, -0.1F), LINEAR),
				k(0.1F, pos(0.12F, 0.05F, 0.1F), LINEAR),
				k(0.15F, pos(-0.12F, -0.05F, -0.1F), LINEAR),
				k(0.2F, pos(0.12F, 0.05F, 0.1F), LINEAR),
				k(0.25F, pos(-0.12F, -0.05F, -0.1F), LINEAR),
				k(0.3F, pos(0.12F, 0.05F, 0.1F), LINEAR),
				k(0.35F, pos(-0.12F, -0.05F, -0.1F), LINEAR),
				k(0.4F, pos(0.12F, 0.05F, 0.1F), LINEAR),
				k(0.45F, pos(-0.12F, -0.05F, -0.1F), LINEAR),
				k(0.5F, pos(0.12F, 0.05F, 0.1F), LINEAR)))
			.addAnimation("piston_l", new AnimationChannel(POS,
				k(0F, pos(0F, 0.7F, 0F), SMOOTH),
				k(0.0625F, pos(0F, 0.495F, 0F), SMOOTH),
				k(0.125F, pos(0F, 0F, 0F), SMOOTH),
				k(0.1875F, pos(0F, -0.495F, 0F), SMOOTH),
				k(0.25F, pos(0F, -0.7F, 0F), SMOOTH),
				k(0.3125F, pos(0F, -0.495F, 0F), SMOOTH),
				k(0.375F, pos(0F, 0F, 0F), SMOOTH),
				k(0.4375F, pos(0F, 0.495F, 0F), SMOOTH),
				k(0.5F, pos(0F, 0.7F, 0F), SMOOTH)))
			.addAnimation("piston_r", new AnimationChannel(POS,
				k(0F, pos(0F, -0.7F, 0F), SMOOTH),
				k(0.0625F, pos(0F, -0.495F, 0F), SMOOTH),
				k(0.125F, pos(0F, 0F, 0F), SMOOTH),
				k(0.1875F, pos(0F, 0.495F, 0F), SMOOTH),
				k(0.25F, pos(0F, 0.7F, 0F), SMOOTH),
				k(0.3125F, pos(0F, 0.495F, 0F), SMOOTH),
				k(0.375F, pos(0F, 0F, 0F), SMOOTH),
				k(0.4375F, pos(0F, -0.495F, 0F), SMOOTH),
				k(0.5F, pos(0F, -0.7F, 0F), SMOOTH)))
			.addAnimation("beam", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 6.9061F), SMOOTH),
				k(0.0625F, rot(0F, 0F, 4.8833F), SMOOTH),
				k(0.125F, rot(0F, 0F, 0F), SMOOTH),
				k(0.1875F, rot(0F, 0F, -4.8833F), SMOOTH),
				k(0.25F, rot(0F, 0F, -6.9061F), SMOOTH),
				k(0.3125F, rot(0F, 0F, -4.8833F), SMOOTH),
				k(0.375F, rot(0F, 0F, 0F), SMOOTH),
				k(0.4375F, rot(0F, 0F, 4.8833F), SMOOTH),
				k(0.5F, rot(0F, 0F, 6.9061F), SMOOTH)))
			.addAnimation("needle", new AnimationChannel(ROT,
				k(0F, rot(0F, 0F, 209F), LINEAR),
				k(0.05F, rot(0F, 0F, 201F), LINEAR),
				k(0.1F, rot(0F, 0F, 209F), LINEAR),
				k(0.15F, rot(0F, 0F, 201F), LINEAR),
				k(0.2F, rot(0F, 0F, 209F), LINEAR),
				k(0.25F, rot(0F, 0F, 201F), LINEAR),
				k(0.3F, rot(0F, 0F, 209F), LINEAR),
				k(0.35F, rot(0F, 0F, 201F), LINEAR),
				k(0.4F, rot(0F, 0F, 209F), LINEAR),
				k(0.45F, rot(0F, 0F, 201F), LINEAR),
				k(0.5F, rot(0F, 0F, 209F), LINEAR)))
			.addAnimation("valve", new AnimationChannel(ROT,
				k(0F, rot(0F, 272F, 0F), LINEAR),
				k(0.05F, rot(0F, 268F, 0F), LINEAR),
				k(0.1F, rot(0F, 272F, 0F), LINEAR),
				k(0.15F, rot(0F, 268F, 0F), LINEAR),
				k(0.2F, rot(0F, 272F, 0F), LINEAR),
				k(0.25F, rot(0F, 268F, 0F), LINEAR),
				k(0.3F, rot(0F, 272F, 0F), LINEAR),
				k(0.35F, rot(0F, 268F, 0F), LINEAR),
				k(0.4F, rot(0F, 272F, 0F), LINEAR),
				k(0.45F, rot(0F, 268F, 0F), LINEAR),
				k(0.5F, rot(0F, 272F, 0F), LINEAR)))
			.addAnimation("magma", new AnimationChannel(SCALE,
				k(0F, scale(1.04F, 1.2F, 1.04F), SMOOTH),
				k(0.12F, scale(1.06F, 1.28F, 1.06F), SMOOTH),
				k(0.25F, scale(1.03F, 1.16F, 1.03F), SMOOTH),
				k(0.37F, scale(1.06F, 1.27F, 1.06F), SMOOTH),
				k(0.5F, scale(1.04F, 1.2F, 1.04F), SMOOTH)))
			.addAnimation("chimney_cap", new AnimationChannel(ROT,
				k(0F, rot(-30F, 0F, 0F), SMOOTH),
				k(0.12F, rot(-38F, 0F, 0F), SMOOTH),
				k(0.25F, rot(-24F, 0F, 0F), SMOOTH),
				k(0.37F, rot(-36F, 0F, 0F), SMOOTH),
				k(0.5F, rot(-30F, 0F, 0F), SMOOTH)))
			.addAnimation("blob_1", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), LINEAR),
				k(0.12F, scale(1F, 1F, 1F), LINEAR),
				k(0.16F, scale(1.3F, 1.3F, 1.3F), SMOOTH),
				k(0.21F, scale(0F, 0F, 0F), LINEAR),
				k(0.5F, scale(0F, 0F, 0F), LINEAR)))
			.addAnimation("blob_2", new AnimationChannel(SCALE,
				k(0F, scale(1F, 1F, 1F), SMOOTH),
				k(0.17F, scale(0F, 0F, 0F), LINEAR),
				k(0.275F, scale(1F, 1F, 1F), LINEAR),
				k(0.31F, scale(1.2F, 1.2F, 1.2F), SMOOTH),
				k(0.36F, scale(0F, 0F, 0F), LINEAR),
				k(0.5F, scale(1F, 1F, 1F), SMOOTH)))
			.addAnimation("blob_3", new AnimationChannel(SCALE,
				k(0F, scale(0F, 0F, 0F), LINEAR),
				k(0.33F, scale(0F, 0F, 0F), LINEAR),
				k(0.42F, scale(1F, 1F, 1F), LINEAR),
				k(0.45F, scale(1.4F, 1.4F, 1.4F), SMOOTH),
				k(0.5F, scale(0F, 0F, 0F), LINEAR)))
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

	private VentGeneratorAnimations() {}
}
