package com.aquamarine.client.machine;

import com.aquamarine.industry.PneumaticBlock;
import com.aquamarine.industry.PneumaticBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;

/**
 * Compressor de Ar e Pulmão de Ferro (0.6.1, 7F). O Pulmão soma aos clipes do estado o clipe {@code level} parado no
 * tempo igual ao nível de ar (0 a 1 s): o fole abre e a boia do visor sobe conforme enche.
 */
public final class PneumaticRenderer implements BlockEntityRenderer<PneumaticBlockEntity, MachineRenderState> {
	private final MachineRenderer<PneumaticBlockEntity> machine;

	public PneumaticRenderer() {
		var compressor = MachineRenderer.Machine.of(AirCompressorModel::create, AirCompressorAnimations.CLIPS, AirCompressorModel.GLASS,
			AirCompressorModel.TEXTURE, AirCompressorModel.GLOW);
		var lung = MachineRenderer.Machine.of(IronLungModel::create, IronLungAnimations.CLIPS, IronLungModel.GLASS, IronLungModel.TEXTURE,
			IronLungModel.GLOW);
		machine = new MachineRenderer<>(be -> be.kind() == PneumaticBlock.Kind.COMPRESSOR ? compressor : lung);
	}

	@Override
	public MachineRenderState createRenderState() {
		return machine.createRenderState();
	}

	@Override
	public void extractRenderState(PneumaticBlockEntity be, MachineRenderState state, float partialTick, Vec3 cameraPos,
		ModelFeatureRenderer.CrumblingOverlay breakProgress) {
		machine.extractRenderState(be, state, partialTick, cameraPos, breakProgress);
		if (be.kind() == PneumaticBlock.Kind.LUNG) {
			state.add("level", Math.clamp(be.fill(partialTick), 0.0F, 1.0F), 1.0F);
		}
	}

	@Override
	public void submit(MachineRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		machine.submit(state, poseStack, collector, camera);
	}
}
