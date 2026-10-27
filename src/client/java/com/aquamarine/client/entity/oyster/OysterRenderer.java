package com.aquamarine.client.entity.oyster;
import com.aquamarine.Aquamarine;
import com.aquamarine.farming.OysterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
public final class OysterRenderer extends MobRenderer<OysterEntity,OysterRenderState,OysterModel> {
    public OysterRenderer(EntityRendererProvider.Context ctx) {
        super(ctx,new OysterModel(ctx.bakeLayer(OysterModel.LAYER)),.25F);
        addLayer(new RenderLayer<OysterRenderState,OysterModel>(this) {
            @Override public void submit(PoseStack pose,SubmitNodeCollector out,int light,OysterRenderState s,float y,float x) {
                if(s.pearl) out.order(1).submitModel(getParentModel(),s,pose,
                    RenderTypes.eyes(Aquamarine.id("textures/entity/oyster/oyster_glow.png")),0xF000F0,OverlayTexture.NO_OVERLAY,-1);
            }
        });
    }
    @Override public OysterRenderState createRenderState() { return new OysterRenderState(); }
    @Override public Identifier getTextureLocation(OysterRenderState s) { return Aquamarine.id("textures/entity/oyster/oyster.png"); }
    @Override public void extractRenderState(OysterEntity e,OysterRenderState s,float partial) {
        super.extractRenderState(e,s,partial); s.openness=e.openness(partial); s.pearl=e.hasPearl();
    }
}
