package com.aquamarine.client.entity.young_kraken;

import com.aquamarine.Aquamarine;
import com.aquamarine.entity.YoungKrakenView;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;

public final class YoungKrakenRenderer<T extends Mob> extends MobRenderer<T, YoungKrakenRenderState, YoungKrakenModel> {
    private static final Identifier TEXTURE = Aquamarine.id("textures/entity/young_kraken/young_kraken.png");
    private static final Identifier GLOW = Aquamarine.id("textures/entity/young_kraken/young_kraken_glow.png");
    public YoungKrakenRenderer(EntityRendererProvider.Context context) {
        super(context, new YoungKrakenModel(context.bakeLayer(YoungKrakenModel.LAYER)), .45F);
        this.addLayer(new com.aquamarine.client.entity.CreatureGlowLayer<>(this, GLOW));
    }
    @Override public YoungKrakenRenderState createRenderState() { return new YoungKrakenRenderState(); }
    @Override public void extractRenderState(T entity, YoungKrakenRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        if (entity instanceof YoungKrakenView view) {
            copy(view.idleAnimation(), state.idle); copy(view.swimAnimation(), state.swim);
            copy(view.stunnedAnimation(), state.stunned); copy(view.hatchAnimation(), state.hatch);
            copy(view.attackAnimation(), state.attack); copy(view.deathAnimation(), state.death);
        }
    }
    @Override public Identifier getTextureLocation(YoungKrakenRenderState state) { return TEXTURE; }
    private static void copy(AnimationState from, AnimationState to) { to.copyFrom(from); }
}
