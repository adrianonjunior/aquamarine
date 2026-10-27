package com.aquamarine.client.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Modelo de uma máquina animada (0.6.1, 7B): o mesmo esqueleto em três passadas, cada uma com a sua cópia dos ossos.
 * {@link Pass#BODY} desenha o que é opaco (recorte), {@link Pass#GLASS} só o vidro (translúcido, depois) e
 * {@link Pass#GLOW} tudo com a textura {@code _glow} e luz própria. Os clipes se somam com peso (é assim que um passa
 * para o outro sem pulo).
 */
public final class MachineModel extends Model<MachineRenderState> {
	public enum Pass { BODY, GLASS, GLOW }

	private final Map<String, KeyframeAnimation> clips = new HashMap<>();
	private final List<ModelPart> glass = new ArrayList<>();
	private final List<ModelPart> solid = new ArrayList<>();
	private final Pass pass;

	public MachineModel(ModelPart root, Map<String, AnimationDefinition> definitions, Set<String> glassParts, Pass pass) {
		super(root, RenderTypes::entityCutout);
		this.pass = pass;
		definitions.forEach((name, definition) -> clips.put(name, definition.bake(root)));
		List<ModelPart> glassList = new ArrayList<>();
		var lookup = root.createPartLookup();
		for (String name : glassParts) {
			ModelPart part = lookup.apply(name);
			if (part != null) {
				glassList.add(part);
			}
		}
		glass.addAll(glassList);
		for (ModelPart part : root.getAllParts()) {
			if (!glassList.contains(part)) {
				solid.add(part);
			}
		}
	}

	@Override
	public void setupAnim(MachineRenderState state) {
		resetPose();
		for (int i = 0; i < state.layers; i++) {
			KeyframeAnimation clip = clips.get(state.clip[i]);
			if (clip != null) {
				clip.apply((long) (state.time[i] * 1000.0F), state.weight[i]);
			}
		}
		for (ModelPart part : glass) {
			part.visible = pass != Pass.BODY;
		}
		for (ModelPart part : solid) {
			part.skipDraw = pass == Pass.GLASS;
		}
	}
}
