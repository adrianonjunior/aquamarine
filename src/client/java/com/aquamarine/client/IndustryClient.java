package com.aquamarine.client;
import com.aquamarine.client.machine.*;
import com.aquamarine.client.entity.crab.*;
import com.aquamarine.client.entity.oyster.*;
import com.aquamarine.entity.ModEntities;
import com.aquamarine.industry.ModHydraulics;
import com.aquamarine.industry.ModPneumatics;
import com.aquamarine.farming.ModCultivation;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
public final class IndustryClient {
    public static void register() {
        ModelLayerRegistry.registerModelLayer(CrabModel.LAYER,CrabModel::createBodyLayer);
        EntityRendererRegistry.register(ModEntities.CRAB,CrabRenderer::new);
        ModelLayerRegistry.registerModelLayer(OysterModel.LAYER,OysterModel::createBodyLayer);
        EntityRendererRegistry.register(ModCultivation.OYSTER,OysterRenderer::new);
        BlockEntityRendererRegistry.register(ModHydraulics.ENTITY,ctx->new HydraulicRenderer());
        BlockEntityRendererRegistry.register(ModPneumatics.ENTITY,ctx->new PneumaticRenderer());
        BlockEntityRendererRegistry.register(ModCultivation.JAR_ENTITY,ctx->{
            var jar=MachineRenderer.Machine.of(CultivationJarModel::create,CultivationJarAnimations.CLIPS,
                CultivationJarModel.GLASS,CultivationJarModel.TEXTURE,CultivationJarModel.GLOW);
            return new MachineRenderer<>(be->jar);
        });
        BlockEntityRendererRegistry.register(ModCultivation.ROPE_ENTITY,ctx->{
            var rope=MachineRenderer.Machine.of(CultivationRopeModel::create,CultivationRopeAnimations.CLIPS,
                CultivationRopeModel.GLASS,CultivationRopeModel.TEXTURE,CultivationRopeModel.GLOW);
            return new MachineRenderer<>(be->rope);
        });
        BlockEntityRendererRegistry.register(ModCultivation.BUOY_ENTITY,ctx->{
            var buoy=MachineRenderer.Machine.of(CultivationBuoyModel::create,CultivationBuoyAnimations.CLIPS,
                CultivationBuoyModel.GLASS,CultivationBuoyModel.TEXTURE,CultivationBuoyModel.GLOW);
            return new MachineRenderer<>(be->buoy);
        });
    }
    private IndustryClient() {}
}
