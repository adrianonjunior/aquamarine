package com.aquamarine.client.machine;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;

/**
 * Monta os ossos e cubos de uma máquina animada (0.6.1, 7B; docs/12-industria.md) com UV por face: cada face visível
 * aponta para o seu retângulo na folha de 128 px (32 px por face de bloco), no lugar do box-UV das criaturas. As classes
 * {@code *Model} geradas por tools/models/machines/ chamam isto; o resultado é um {@link ModelPart} comum do jogo, que
 * anima com {@code AnimationDefinition} e desenha a rachadura de quebrar como qualquer modelo.
 */
public final class MachineParts {
	/** Faces na ordem do kit (machine_kit.py, FACES): o topo visível é a face DOWN porque o y do modelo cresce para baixo. */
	private static final Direction[] DIRECTIONS = {Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

	private final float texWidth;
	private final float texHeight;
	private final Map<String, Def> parts = new LinkedHashMap<>();

	private record Def(String parent, PartPose pose, List<ModelPart.Cube> cubes) {}

	public MachineParts(int texWidth, int texHeight) {
		this.texWidth = texWidth;
		this.texHeight = texHeight;
	}

	/** Osso com pivô relativo ao pai (o pai vem antes). */
	public void part(String name, String parent, float x, float y, float z, float xRot, float yRot, float zRot) {
		if (parent != null && !parts.containsKey(parent)) {
			throw new IllegalArgumentException("pai desconhecido: " + parent);
		}
		parts.put(name, new Def(parent, PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot), new ArrayList<>()));
	}

	/** Cubo no osso; {@code faces} = [face, u, v, largura, altura, ...] em px da folha, só as faces visíveis. */
	public void box(String part, float x, float y, float z, float w, float h, float d, int[] faces) {
		EnumSet<Direction> visible = EnumSet.noneOf(Direction.class);
		for (int i = 0; i < faces.length; i += 5) {
			visible.add(DIRECTIONS[faces[i]]);
		}
		ModelPart.Cube cube = new ModelPart.Cube(0, 0, x, y, z, w, h, d, 0, 0, 0, false, texWidth, texHeight, visible);
		for (int p = 0; p < cube.polygons.length; p++) {
			ModelPart.Polygon polygon = cube.polygons[p];
			Vector3fc n = polygon.normal();
			Direction direction = Direction.getApproximateNearest(n.x(), n.y(), n.z());
			int i = rect(faces, direction);
			float u1 = faces[i + 1] / texWidth, v1 = faces[i + 2] / texHeight;
			float u2 = (faces[i + 1] + faces[i + 3]) / texWidth, v2 = (faces[i + 2] + faces[i + 4]) / texHeight;
			ModelPart.Vertex[] v = polygon.vertices();
			// Mesma ordem de cantos do jogo: a face fica de pé e sem espelho para quem a olha de fora.
			cube.polygons[p] = new ModelPart.Polygon(new ModelPart.Vertex[] {v[0].remap(u2, v1), v[1].remap(u1, v1), v[2].remap(u1, v2),
				v[3].remap(u2, v2)}, n);
		}
		parts.get(part).cubes().add(cube);
	}

	private static int rect(int[] faces, Direction direction) {
		for (int i = 0; i < faces.length; i += 5) {
			if (DIRECTIONS[faces[i]] == direction) {
				return i;
			}
		}
		throw new IllegalStateException("face sem UV: " + direction);
	}

	/** Raiz sem nome com os ossos de cima como filhos (o nome "root" fica livre para os clipes). */
	public ModelPart bake() {
		List<String> names = new ArrayList<>(parts.keySet());
		Map<String, Map<String, ModelPart>> children = new LinkedHashMap<>();
		Map<String, ModelPart> top = new LinkedHashMap<>();
		for (int i = names.size() - 1; i >= 0; i--) {
			String name = names.get(i);
			Def def = parts.get(name);
			Map<String, ModelPart> kids = children.getOrDefault(name, Map.of());
			Map<String, ModelPart> ordered = new LinkedHashMap<>();
			List<String> keys = new ArrayList<>(kids.keySet());
			for (int k = keys.size() - 1; k >= 0; k--) {
				ordered.put(keys.get(k), kids.get(keys.get(k)));
			}
			ModelPart part = new ModelPart(def.cubes(), ordered);
			part.setInitialPose(def.pose());
			part.loadPose(def.pose());
			(def.parent() == null ? top : children.computeIfAbsent(def.parent(), k -> new LinkedHashMap<>())).put(name, part);
		}
		return new ModelPart(List.of(), top);
	}
}
