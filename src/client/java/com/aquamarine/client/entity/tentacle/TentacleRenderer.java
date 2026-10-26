package com.aquamarine.client.entity.tentacle;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.TentacleView;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;

public final class TentacleRenderer<T extends Mob> extends MobRenderer<T, TentacleRenderState, TentacleModel> {
    private static final Identifier TEXTURE = Aquamarine.id("textures/entity/tentacle/tentacle.png");
    private static final Identifier GLOW = Aquamarine.id("textures/entity/tentacle/tentacle_glow.png");
    public TentacleRenderer(EntityRendererProvider.Context context) {
        super(context, new TentacleModel(context.bakeLayer(TentacleModel.LAYER)), 1.3F);
        this.addLayer(new com.aquamarine.client.entity.CreatureGlowLayer<>(this, GLOW));
    }
    @Override public TentacleRenderState createRenderState() { return new TentacleRenderState(); }
    @Override public void extractRenderState(T entity, TentacleRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        if (entity instanceof TentacleView view) {
            copy(view.idleAnimation(), state.idle); copy(view.riseAnimation(), state.rise);
            copy(view.slamAnimation(), state.slam); copy(view.grabAnimation(), state.grab);
            copy(view.releaseAnimation(), state.release);
        }
    }
    @Override public Identifier getTextureLocation(TentacleRenderState state) { return TEXTURE; }
    private static void copy(AnimationState from, AnimationState to) { to.copyFrom(from); }
}
