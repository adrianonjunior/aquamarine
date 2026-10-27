package com.aquamarine.client.network;

import com.aquamarine.Aquamarine;
import com.aquamarine.network.ConduitBlock;
import com.aquamarine.network.ConduitBlockEntity;
import com.aquamarine.network.ConduitKind;
import com.aquamarine.network.InlineConduitBlock;
import com.aquamarine.network.PortMode;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

/**
 * O que passa dentro do Cabo, do Cano e do Tubo de Ar (0.6.1, 7C; docs/12-industria.md): o líquido com a textura do
 * próprio fluido (água tingida pelo bioma, lava acesa), no nível da rede e correndo no sentido do fluxo; fiapos de ar
 * rolando no Tubo; arcos elétricos no Cabo enquanto passa carga; e, nos conectores, a marca do modo (sai, entra) ou a
 * tampa de desligado. A carcaça é o modelo JSON do bloco; aqui só o conteúdo, mandado pelo servidor quando muda.
 */
public final class ConduitRenderer implements BlockEntityRenderer<ConduitBlockEntity, ConduitRenderer.State> {
	private static final Identifier ARC = Aquamarine.id("textures/block/conduit/arc.png");
	private static final Identifier WISP = Aquamarine.id("textures/block/conduit/air_wisp.png");
	private static final Identifier MARKS = Aquamarine.id("textures/block/conduit/port_marks.png");
	private static final int FULL_BRIGHT = 0xF000F0;

	public static final class State extends BlockEntityRenderState {
		ConduitKind kind = ConduitKind.FLUID;
		/** Lados com conteúdo (0 = nada, 1 = braço até a borda, 2 = braço até o anel do conector). */
		final int[] arms = new int[6];
		final PortMode[] modes = new PortMode[6];
		boolean inline;
		boolean window;
		FluidVariant variant = FluidVariant.blank();
		int level;
		int flow;
		boolean active;
		float time;
		int color = -1;
		int seed;
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(ConduitBlockEntity be, State state, float partialTick, Vec3 cameraPos,
		ModelFeatureRenderer.CrumblingOverlay breakProgress) {
		BlockEntityRenderState.extractBase(be, state, breakProgress);
		BlockState block = be.getBlockState();
		state.kind = be.kind();
		state.inline = block.getBlock() instanceof InlineConduitBlock;
		state.window = state.inline && !((InlineConduitBlock) block.getBlock()).valve();
		boolean open = !state.inline || state.window || block.getValue(InlineConduitBlock.OPEN);
		for (Direction d : Direction.values()) {
			int i = d.ordinal();
			state.modes[i] = be.mode(d);
			if (block.getBlock() instanceof ConduitBlock) {
				ConduitBlock.Link link = block.getValue(ConduitBlock.SIDES.get(d));
				state.arms[i] = link == ConduitBlock.Link.NONE ? 0 : link == ConduitBlock.Link.PIPE ? 1 : 2;
			} else {
				state.arms[i] = open && d.getAxis() == block.getValue(InlineConduitBlock.AXIS) ? 1 : 0;
			}
		}
		state.variant = be.shownVariant();
		state.level = be.shownLevel();
		state.flow = be.shownFlow();
		state.active = be.shownActive();
		state.time = be.getLevel() == null ? 0 : ((be.getLevel().getGameTime() % 24000L) + partialTick) / 20.0F;
		state.seed = (int) (be.getBlockPos().asLong() * 31 % 4);
		state.color = state.kind == ConduitKind.FLUID && !state.variant.isBlank() && be.getLevel() instanceof BlockAndTintGetter tint
			? 0xFF000000 | FluidVariantRendering.getColor(state.variant, tint, be.getBlockPos()) : -1;
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.kind == ConduitKind.FLUID && state.level > 0 && !state.variant.isBlank()) {
			boolean lava = state.variant.isOf(Fluids.LAVA);
			int light = lava ? FULL_BRIGHT : state.lightCoords;
			TextureAtlasSprite still = sprite(lava ? "block/lava_still" : "block/water_still");
			TextureAtlasSprite flowing = sprite(lava ? "block/lava_flow" : "block/water_flow");
			collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS),
				(pose, out) -> liquid(state, pose, out, still, flowing, light));
		}
		if (state.kind == ConduitKind.GAS && state.level > 0) {
			collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(WISP), (pose, out) -> wisps(state, pose, out));
		}
		if (state.kind == ConduitKind.ENERGY && state.active) {
			collector.submitCustomGeometry(poseStack, RenderTypes.eyes(ARC), (pose, out) -> arcs(state, pose, out));
		}
		if (!state.inline) {
			collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(MARKS), (pose, out) -> marks(state, pose, out));
		}
	}

	private static TextureAtlasSprite sprite(String path) {
		TextureAtlas atlas = (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
		return atlas.getSprite(Identifier.withDefaultNamespace(path));
	}

	/** Líquido: um bloco no miolo e um por braço, no nível da rede; nos braços com fluxo, a textura corre no sentido dele. */
	private static void liquid(State s, PoseStack.Pose pose, VertexConsumer out, TextureAtlasSprite still, TextureAtlasSprite flowing, int light) {
		float r = s.window ? 3.9F : 2.6F;
		float fill = s.level / 8.0F;
		float lo = 8 - r, top = lo + 2 * r * Math.max(0.12F, fill);
		box(out, pose, new float[] {8 - r, lo, 8 - r}, new float[] {8 + r, top, 8 + r}, -1, 0, still, s.color, light);
		for (Direction d : Direction.values()) {
			int i = d.ordinal();
			if (s.arms[i] == 0) {
				continue;
			}
			int bits = (s.flow >> (2 * i)) & 3;
			int sign = bits == 1 ? d.getAxisDirection().getStep() : bits == 2 ? -d.getAxisDirection().getStep() : 0;
			float edge = s.arms[i] == 2 ? 1 : 0;
			float[] a = {8 - r, lo, 8 - r}, b = {8 + r, d.getAxis() == Direction.Axis.Y ? 8 + r : top, 8 + r};
			int axis = d.getAxis().ordinal();
			if (d.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
				a[axis] = 8 + r;
				b[axis] = 16 - edge;
			} else {
				a[axis] = edge;
				b[axis] = 8 - r;
			}
			box(out, pose, a, b, sign == 0 ? -1 : axis, sign, sign == 0 ? still : flowing, s.color, light);
		}
	}

	/** Fiapos de ar: duas fitas cruzadas por braço, rolando no sentido do fluxo (mais rápido com ar passando). */
	private static void wisps(State s, PoseStack.Pose pose, VertexConsumer out) {
		int alpha = (int) (110 + 145 * s.level / 8.0F);
		for (Direction d : Direction.values()) {
			int i = d.ordinal();
			if (s.arms[i] == 0) {
				continue;
			}
			int bits = (s.flow >> (2 * i)) & 3;
			float speed = bits == 0 ? 0.08F : bits == 1 ? 0.7F : -0.7F;
			ribbon(out, pose, d, s.arms[i] == 2 ? 1 : 0, 2.4F, s.time * speed, alpha, s.lightCoords);
		}
	}

	/** Arcos: quadro do arco trocado a cada 2 ticks, desencontrado de bloco para bloco. */
	private static void arcs(State s, PoseStack.Pose pose, VertexConsumer out) {
		int frame = ((int) (s.time * 10) + s.seed) & 3;
		for (Direction d : Direction.values()) {
			int i = d.ordinal();
			if (s.arms[i] != 0) {
				arcRibbon(out, pose, d, s.arms[i] == 2 ? 1 : 0, s.window ? 3.8F : 2.2F, frame);
			}
		}
	}

	/** Marca do modo no anel de cada conector; tampa vermelha no miolo nos lados desligados. */
	private static void marks(State s, PoseStack.Pose pose, VertexConsumer out) {
		for (Direction d : Direction.values()) {
			int i = d.ordinal();
			if (s.arms[i] == 2) {
				int icon = s.modes[i] == PortMode.IN ? 1 : 0;
				face(out, pose, d, -0.02F, 2.2F, icon, s.lightCoords);
				// A de cima do braço é a que se vê de pé: a seta aponta para a máquina (sai) ou para o cano (entra).
				if (d.getAxis().isHorizontal()) {
					top(out, pose, d, s.kind == ConduitKind.ENERGY ? 2.5F : 3.0F, icon, s.lightCoords);
				}
			} else if (s.modes[i] == PortMode.OFF) {
				face(out, pose, d, 4.48F, 2.0F, 2, s.lightCoords);
			}
		}
	}

	/** Marca deitada em cima do braço {@code d}, entre o anel e o miolo; o alto da textura fica do lado da máquina. */
	private static void top(VertexConsumer out, PoseStack.Pose pose, Direction d, float sheath, int icon, int light) {
		int along = d.getAxis() == Direction.Axis.X ? 0 : 2, across = 2 - along;
		boolean positive = d.getAxisDirection() == Direction.AxisDirection.POSITIVE;
		float outer = positive ? 15.0F : 1.0F, inner = positive ? 11.6F : 4.4F, y = 8 + sheath + 0.03F;
		float[][] c = new float[4][], uv = new float[4][];
		float[] side = {6.3F, 9.7F, 9.7F, 6.3F}, end = {outer, outer, inner, inner};
		for (int k = 0; k < 4; k++) {
			float[] v = new float[3];
			v[1] = y;
			v[along] = end[k];
			v[across] = side[k];
			c[k] = v;
			uv[k] = new float[] {(icon + (k == 1 || k == 2 ? 1 : 0)) / 3.0F, k >= 2 ? 1 : 0};
		}
		quad(out, pose, c, uv, Direction.UP, -1, light);
	}

	// ================================================================ geometria

	/** Caixa em pixels do bloco; a textura repete a cada 16 px e, com fluxo, o v anda no eixo do fluxo (sinal = sentido). */
	public static void box(VertexConsumer out, PoseStack.Pose pose, float[] a, float[] b, int flowAxis, int sign,
		TextureAtlasSprite sprite, int color, int light) {
		for (Direction face : Direction.values()) {
			int n = face.getAxis().ordinal();
			int p = (n + 1) % 3, q = (n + 2) % 3;
			if (q != flowAxis && p == flowAxis) {
				int t = p;
				p = q;
				q = t;
			}
			float plane = face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? b[n] : a[n];
			float[][] corners = new float[4][];
			float[][] uv = new float[4][];
			float[] ps = {a[p], b[p], b[p], a[p]}, qs = {a[q], a[q], b[q], b[q]};
			for (int k = 0; k < 4; k++) {
				float[] c = new float[3];
				c[n] = plane;
				c[p] = ps[k];
				c[q] = qs[k];
				corners[k] = c;
				float v = (qs[k] - a[q]) / 16.0F;
				uv[k] = new float[] {sprite.getU((ps[k] - a[p]) / 16.0F), sprite.getV(sign < 0 && q == flowAxis ? (b[q] - a[q]) / 16.0F - v : v)};
			}
			quad(out, pose, corners, uv, face, color, light);
		}
	}

	private static void ribbon(VertexConsumer out, PoseStack.Pose pose, Direction d, float edge, float half, float scroll, int alpha, int light) {
		int axis = d.getAxis().ordinal();
		boolean positive = d.getAxisDirection() == Direction.AxisDirection.POSITIVE;
		float from = positive ? 8 : edge, to = positive ? 16 - edge : 8;
		float off = scroll - (float) Math.floor(scroll);
		for (int plane = 0; plane < 2; plane++) {
			int p = plane == 0 ? (axis + 1) % 3 : (axis + 2) % 3;
			// Divide a fita onde a textura dá a volta, para rolar sem depender de repetição de textura.
			float span = (to - from) / 16.0F;
			float cut = from + (1 - off) * 16.0F;
			float[][] parts = cut < to ? new float[][] {{from, cut, off, 1}, {cut, to, 0, off + span - 1}} : new float[][] {{from, to, off, off + span}};
			for (float[] part : parts) {
				float[][] c = new float[4][];
				float[] lows = {part[0], part[1], part[1], part[0]}, wides = {8 - half, 8 - half, 8 + half, 8 + half};
				float[][] uv = new float[4][];
				for (int k = 0; k < 4; k++) {
					float[] v = {8, 8, 8};
					v[axis] = lows[k];
					v[p] = wides[k];
					c[k] = v;
					uv[k] = new float[] {k < 2 ? 0 : 1, k == 0 || k == 3 ? part[2] : part[3]};
				}
				int argb = (alpha << 24) | 0xE8F4FF;
				quad(out, pose, c, uv, null, argb, light);
				quad(out, pose, new float[][] {c[3], c[2], c[1], c[0]}, new float[][] {uv[3], uv[2], uv[1], uv[0]}, null, argb, light);
			}
		}
	}

	private static void arcRibbon(VertexConsumer out, PoseStack.Pose pose, Direction d, float edge, float half, int frame) {
		int axis = d.getAxis().ordinal();
		boolean positive = d.getAxisDirection() == Direction.AxisDirection.POSITIVE;
		float from = positive ? 8 : edge, to = positive ? 16 - edge : 8;
		float v0 = frame / 4.0F, v1 = (frame + 1) / 4.0F;
		for (int plane = 0; plane < 2; plane++) {
			int p = plane == 0 ? (axis + 1) % 3 : (axis + 2) % 3;
			float[][] c = new float[4][];
			float[] along = {from, to, to, from}, across = {8 - half, 8 - half, 8 + half, 8 + half};
			for (int k = 0; k < 4; k++) {
				float[] v = {8, 8, 8};
				v[axis] = along[k];
				v[p] = across[k];
				c[k] = v;
			}
			float[][] uv = {{0, v0}, {0, v1}, {1, v1}, {1, v0}};
			quad(out, pose, c, uv, null, -1, FULL_BRIGHT);
			quad(out, pose, new float[][] {c[3], c[2], c[1], c[0]}, new float[][] {uv[3], uv[2], uv[1], uv[0]}, null, -1, FULL_BRIGHT);
		}
	}

	/** Quadrado de marca na face {@code d}, a {@code depth} px da borda do bloco; {@code icon} 0 sai, 1 entra, 2 desligado. */
	private static void face(VertexConsumer out, PoseStack.Pose pose, Direction d, float depth, float half, int icon, int light) {
		int n = d.getAxis().ordinal(), p = (n + 1) % 3, q = (n + 2) % 3;
		float plane = d.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 16 - depth : depth;
		float[] ps = {8 - half, 8 + half, 8 + half, 8 - half}, qs = {8 - half, 8 - half, 8 + half, 8 + half};
		float[][] c = new float[4][], uv = new float[4][];
		for (int k = 0; k < 4; k++) {
			float[] v = new float[3];
			v[n] = plane;
			v[p] = ps[k];
			v[q] = qs[k];
			c[k] = v;
			uv[k] = new float[] {(icon + (k == 1 || k == 2 ? 1 : 0)) / 3.0F, k >= 2 ? 1 : 0};
		}
		quad(out, pose, c, uv, d, -1, light);
	}

	/** Um quadrilátero em pixels do bloco; com {@code face}, a ordem dos cantos é acertada para ficar de frente para fora. */
	private static void quad(VertexConsumer out, PoseStack.Pose pose, float[][] c, float[][] uv, Direction face, int color, int light) {
		float nx, ny, nz;
		if (face != null) {
			nx = face.getStepX();
			ny = face.getStepY();
			nz = face.getStepZ();
			float[] e1 = {c[1][0] - c[0][0], c[1][1] - c[0][1], c[1][2] - c[0][2]}, e2 = {c[2][0] - c[0][0], c[2][1] - c[0][1], c[2][2] - c[0][2]};
			float dot = (e1[1] * e2[2] - e1[2] * e2[1]) * nx + (e1[2] * e2[0] - e1[0] * e2[2]) * ny + (e1[0] * e2[1] - e1[1] * e2[0]) * nz;
			if (dot < 0) {
				c = new float[][] {c[3], c[2], c[1], c[0]};
				uv = new float[][] {uv[3], uv[2], uv[1], uv[0]};
			}
		} else {
			float[] e1 = {c[1][0] - c[0][0], c[1][1] - c[0][1], c[1][2] - c[0][2]}, e2 = {c[2][0] - c[0][0], c[2][1] - c[0][1], c[2][2] - c[0][2]};
			nx = e1[1] * e2[2] - e1[2] * e2[1];
			ny = e1[2] * e2[0] - e1[0] * e2[2];
			nz = e1[0] * e2[1] - e1[1] * e2[0];
			float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
			if (len > 0) {
				nx /= len;
				ny /= len;
				nz /= len;
			}
		}
		for (int k = 0; k < 4; k++) {
			out.addVertex(pose, c[k][0] / 16.0F, c[k][1] / 16.0F, c[k][2] / 16.0F).setColor(color).setUv(uv[k][0], uv[k][1])
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, nx, ny, nz);
		}
	}
}
