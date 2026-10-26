package com.aquamarine.client.particle;

import com.aquamarine.particle.ModParticles;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * Partículas do Cavitador de Neutrônio (docs/10-fase4.md, §9), todas com luz própria: a bolha de cavitação cresce e
 * some, a centelha encolhe, o anel de choque se abre e o ponto de carga corre até a boca do cano.
 */
public final class AquamarineParticles {
	enum Kind { BUBBLE, SPARK, RING, MOTE }

	public static void register() {
		ParticleProviderRegistry registry = ParticleProviderRegistry.getInstance();
		registry.register(ModParticles.CAVITATION_BUBBLE, sprites -> provider(sprites, Kind.BUBBLE));
		registry.register(ModParticles.NEUTRON_SPARK, sprites -> provider(sprites, Kind.SPARK));
		registry.register(ModParticles.SHOCK_RING, sprites -> provider(sprites, Kind.RING));
		registry.register(ModParticles.CHARGE_MOTE, sprites -> provider(sprites, Kind.MOTE));
	}

	private static net.minecraft.client.particle.ParticleProvider<SimpleParticleType> provider(SpriteSet sprites, Kind kind) {
		return (type, level, x, y, z, dx, dy, dz, random) -> new Glow(level, x, y, z, dx, dy, dz, sprites, kind, random);
	}

	static final class Glow extends SingleQuadParticle {
		private final SpriteSet sprites;
		private final Kind kind;
		private final float startSize;

		Glow(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites, Kind kind, RandomSource random) {
			super(level, x, y, z, dx, dy, dz, sprites.first());
			this.sprites = sprites;
			this.kind = kind;
			this.xd = dx;
			this.yd = dy;
			this.zd = dz;
			this.hasPhysics = false;
			this.gravity = 0.0F;
			switch (kind) {
				case BUBBLE -> {
					lifetime = 10 + random.nextInt(8);
					quadSize = 0.06F + random.nextFloat() * 0.08F;
					friction = 0.86F;
					yd += 0.008;
					setColor(0.8F, 1.0F, 1.0F);
				}
				case SPARK -> {
					lifetime = 8 + random.nextInt(8);
					quadSize = 0.06F + random.nextFloat() * 0.07F;
					friction = 0.88F;
					setColor(0.75F, 1.0F, 1.0F);
				}
				case RING -> {
					lifetime = 12;
					quadSize = 0.4F;
					xd = yd = zd = 0.0;
				}
				case MOTE -> {
					lifetime = 8;
					quadSize = 0.045F;
					friction = 1.0F;
					setColor(0.5F, 0.95F, 1.0F);
				}
			}
			this.startSize = quadSize;
			setSpriteFromAge(sprites);
		}

		@Override
		protected SingleQuadParticle.Layer getLayer() {
			return SingleQuadParticle.Layer.TRANSLUCENT;
		}

		@Override
		public int getLightCoords(float partialTick) {
			return 0xF000F0;
		}

		@Override
		public void tick() {
			super.tick();
			if (removed) {
				return;
			}
			float life = (float) age / lifetime;
			setSpriteFromAge(sprites);
			switch (kind) {
				case BUBBLE -> {
					quadSize = startSize * (1.0F + life * 1.6F);
					alpha = 1.0F - life * life;
				}
				case SPARK -> {
					quadSize = startSize * (1.0F - life * 0.8F);
					alpha = 1.0F - life * 0.4F;
				}
				case RING -> {
					quadSize = startSize + life * 3.6F;
					alpha = 1.0F - life;
				}
				case MOTE -> alpha = 0.55F + 0.45F * life;
			}
		}
	}

	private AquamarineParticles() {}
}
