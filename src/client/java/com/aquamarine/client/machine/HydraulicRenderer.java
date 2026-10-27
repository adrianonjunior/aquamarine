package com.aquamarine.client.machine;

import com.aquamarine.industry.HydraulicBlock;
import com.aquamarine.industry.HydraulicBlockEntity;
import com.aquamarine.client.network.ConduitRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

/** O nível acompanha o estoque de cada bloco da coluna; o fluido usa o sprite vanilla animado. */
public final class HydraulicRenderer implements BlockEntityRenderer<HydraulicBlockEntity,HydraulicRenderer.State> {
    private final MachineRenderer<HydraulicBlockEntity> machine;
    public HydraulicRenderer() {
        var pump=MachineRenderer.Machine.of(TidePumpModel::create,TidePumpAnimations.CLIPS,TidePumpModel.GLASS,TidePumpModel.TEXTURE,TidePumpModel.GLOW);
        var tank=MachineRenderer.Machine.of(WaterTankModel::create,WaterTankAnimations.CLIPS,WaterTankModel.GLASS,WaterTankModel.TEXTURE,WaterTankModel.GLOW);
        machine=new MachineRenderer<>(be->be.kind()==HydraulicBlock.Kind.PUMP?pump:tank);
    }
    public static class State extends MachineRenderState { float fill,time; boolean tank,lava; int fluidColor; }
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(HydraulicBlockEntity be,State s,float partial,Vec3 camera,ModelFeatureRenderer.CrumblingOverlay breaking) {
        machine.extractRenderState(be,s,partial,camera,breaking);
        s.tank=be.kind()==HydraulicBlock.Kind.TANK; s.fill=be.fill(partial); s.lava=be.variant().isOf(Fluids.LAVA);
        s.time=be.getLevel()==null?0:(be.getLevel().getGameTime()%24000+partial)/20F;
        s.fluidColor=be.getLevel() instanceof BlockAndTintGetter tint ? 0xD0000000|FluidVariantRendering.getColor(be.variant(),tint,be.getBlockPos())&0xFFFFFF : 0xD03F76E4;
    }
    @Override public void submit(State s,PoseStack pose,SubmitNodeCollector out,CameraRenderState camera) {
        machine.submit(s,pose,out,camera);
        if(!s.tank || s.fill<.001F) return;
        var atlas=(TextureAtlas)Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
        var sprite=atlas.getSprite(Identifier.withDefaultNamespace(s.lava?"block/lava_still":"block/water_still"));
        pose.pushPose(); pose.translate(.5,0,.5); pose.rotateDegrees(Axis.YP,180-s.yRot); pose.translate(-.5,0,-.5);
        float top=Math.min(14F,2+12*s.fill+.06F*(float)Math.sin(s.time*2));
        out.submitCustomGeometry(pose,RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS),(p,v)-> {
            ConduitRenderer.box(v,p,new float[]{4,2,2.2F},new float[]{12,top,13.8F},-1,0,sprite,s.fluidColor,s.lava?0xF000F0:s.lightCoords);
            ConduitRenderer.box(v,p,new float[]{2.2F,2,4},new float[]{4,top,12},-1,0,sprite,s.fluidColor,s.lava?0xF000F0:s.lightCoords);
            ConduitRenderer.box(v,p,new float[]{12,2,4},new float[]{13.8F,top,12},-1,0,sprite,s.fluidColor,s.lava?0xF000F0:s.lightCoords);
        }); pose.popPose();
    }
}
