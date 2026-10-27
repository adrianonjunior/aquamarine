package com.aquamarine.client.machine;

import com.aquamarine.machine.MachineView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Renderizador das máquinas animadas (0.6.1, 7B; docs/12-industria.md): desenha o modelo inteiro do bloco (como o baú),
 * virado para a frente do bloco, nas três passadas de {@link MachineModel}, com os clipes que o {@link MachineAnimator}
 * escolheu pelo estado. Um tipo de block entity pode servir a vários blocos: a função devolve a máquina de cada um, ou
 * {@code null} quando o bloco ainda usa o modelo JSON.
 */
public final class MachineRenderer<T extends BlockEntity & MachineView> implements BlockEntityRenderer<T, MachineRenderState> {
	private static final int FULL_BRIGHT = 0xF000F0;
	private final Function<T, Machine> machines;

	public MachineRenderer(Function<T, Machine> machines) {
		this.machines = machines;
	}

	/** Uma máquina pronta para desenhar: três cópias do esqueleto (uma por passada), clipes e texturas. */
	public record Machine(MachineModel body, MachineModel glass, MachineModel glow, Map<String, AnimationDefinition> clips,
		Identifier texture, Identifier glowTexture) {
		public static Machine of(Supplier<ModelPart> skeleton, Map<String, AnimationDefinition> clips, Set<String> glassParts,
			Identifier texture, Identifier glowTexture) {
			return new Machine(new MachineModel(skeleton.get(), clips, glassParts, MachineModel.Pass.BODY),
				new MachineModel(skeleton.get(), clips, glassParts, MachineModel.Pass.GLASS),
				new MachineModel(skeleton.get(), clips, glassParts, MachineModel.Pass.GLOW), clips, texture, glowTexture);
		}
	}

	@Override
	public MachineRenderState createRenderState() {
		return new MachineRenderState();
	}

	@Override
	public void extractRenderState(T be, MachineRenderState state, float partialTick, Vec3 cameraPos,
		ModelFeatureRenderer.CrumblingOverlay breakProgress) {
		BlockEntityRenderState.extractBase(be, state, breakProgress);
		state.machine = machines.apply(be);
		if (state.machine == null || be.getLevel() == null) {
			return;
		}
		BlockState block = be.getBlockState();
		state.yRot = block.hasProperty(HorizontalDirectionalBlock.FACING) ? block.getValue(HorizontalDirectionalBlock.FACING).toYRot() : 0.0F;
		double now = (be.getLevel().getGameTime() + partialTick) / 20.0;
		MachineAnimator.animate(be.motion(), now, state.machine.clips(), state);
	}

	@Override
	public void submit(MachineRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		Machine machine = state.machine;
		if (machine == null) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0.5F, 0.0F, 0.5F);
		poseStack.rotateDegrees(Axis.YP, 180.0F - state.yRot);
		poseStack.translate(0.0F, 1.5F, 0.0F);
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		collector.submitModel(machine.body(), state, poseStack, RenderTypes.entityCutout(machine.texture()), state.lightCoords,
			OverlayTexture.NO_OVERLAY, state.bodyColor, null, 0);
		// O vidro vai depois de tudo o que fica dentro dele (o líquido do Tanque é desenhado à parte, na ordem 0).
		collector.order(1).submitModel(machine.glass(), state, poseStack, RenderTypes.entityTranslucent(machine.texture()), state.lightCoords,
			OverlayTexture.NO_OVERLAY, state.bodyColor, null, 0);
		collector.order(2).submitModel(machine.glow(), state, poseStack, RenderTypes.eyes(machine.glowTexture()), FULL_BRIGHT,
			OverlayTexture.NO_OVERLAY, state.glowColor, null, 0);
		if (state.breakProgress != null) {
			collector.order(3).submitCrumblingOverlay(machine.body(), state, poseStack, RenderTypes.entityCutout(machine.texture()),
				state.lightCoords, OverlayTexture.NO_OVERLAY, -1, state.breakProgress);
		}
		poseStack.popPose();
	}
}
