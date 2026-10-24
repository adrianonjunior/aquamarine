package com.aquamarine.entity.boss;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Locomoção do Kraken no servidor: circula o centro da arena, avança contra o alvo de aggro e recua,
 * sempre preso a {@link #LEASH} blocos do ponto onde surgiu. Só desloca e vira o corpo: dano, escudo,
 * ataques e regras da luta ficam onde estão (docs/02-kraken.md). A posição e o giro chegam aos
 * clientes pelo rastreio normal da entidade.
 */
public final class KrakenMovement {
	/** Distância horizontal máxima do centro (blocos); com o corpo de 32 x 20 cabe na arena de 64. */
	public static final double LEASH = 12.0;
	/** Raio da volta em torno do centro quando está circulando. */
	public static final double ORBIT_RADIUS = 8.0;
	/** Giro máximo do corpo por tick (graus). */
	private static final float TURN = 2.5F;

	enum Mode { CIRCLE, APPROACH, RETREAT }

	private Vec3 home;
	private Mode mode = Mode.CIRCLE;
	private int modeTicks;
	private double orbitAngle;
	private int orbitDir = 1;
	private Vec3 velocity = Vec3.ZERO;

	/** Velocidade de cruzeiro (blocos por tick) por fase: a fase 3 é 30% mais rápida que a 2. */
	public static double speed(int phase) {
		return switch (phase) {
			case 1 -> 0.05;
			case 2 -> 0.075;
			default -> 0.0975;
		};
	}

	/** Altura em relação ao centro: submerso na fase 1, mais perto da superfície na 2, no fundo na 3. */
	public static double depthOffset(int phase) {
		return switch (phase) {
			case 1 -> 0.0;
			case 2 -> 3.0;
			default -> -4.0;
		};
	}

	/** Prende {@code point} a {@code leash} blocos de {@code home} no plano horizontal (y fica igual). */
	public static Vec3 clampToLeash(Vec3 home, Vec3 point, double leash) {
		double dx = point.x - home.x;
		double dz = point.z - home.z;
		double dist = Math.sqrt(dx * dx + dz * dz);
		if (dist <= leash || dist == 0.0) {
			return point;
		}
		double k = leash / dist;
		return new Vec3(home.x + dx * k, point.y, home.z + dz * k);
	}

	public Vec3 home() {
		return home;
	}

	public void setHome(Vec3 home) {
		this.home = home;
	}

	/**
	 * Um passo. {@code canMove} falso (atordoado, em transição, IA desligada) faz o corpo frear e parar;
	 * {@code attacking} deixa o deslocamento lento para o aviso não fugir do lugar.
	 */
	public void tick(Mob kraken, Player target, int phase, boolean canMove, boolean attacking, RandomSource random) {
		if (home == null) {
			home = kraken.position();
		}
		if (!canMove) {
			velocity = velocity.scale(0.8);
			kraken.setDeltaMovement(velocity);
			return;
		}
		double speed = speed(phase) * (attacking ? 0.35 : 1.0);
		if (--modeTicks <= 0) {
			pickMode(target, random);
		}
		Vec3 goal = goal(kraken, target, speed);
		goal = new Vec3(goal.x, home.y + depthOffset(phase) + Math.sin(kraken.tickCount * 0.035) * 1.2, goal.z);
		Vec3 to = goal.subtract(kraken.position());
		double dist = to.length();
		Vec3 want = dist < 0.2 ? Vec3.ZERO : to.scale(Math.min(speed, dist * 0.08) / dist);
		velocity = velocity.lerp(want, 0.08);
		kraken.setDeltaMovement(velocity);
		face(kraken, target);
	}

	private void pickMode(Player target, RandomSource random) {
		if (target == null) {
			mode = Mode.CIRCLE;
			modeTicks = 100;
		} else {
			float roll = random.nextFloat();
			mode = roll < 0.5F ? Mode.CIRCLE : roll < 0.8F ? Mode.APPROACH : Mode.RETREAT;
			modeTicks = 80 + random.nextInt(80);
		}
		if (random.nextFloat() < 0.3F) {
			orbitDir = -orbitDir;
		}
	}

	private Vec3 goal(Mob kraken, Player target, double speed) {
		if (target == null || mode == Mode.CIRCLE) {
			orbitAngle += orbitDir * speed / ORBIT_RADIUS;
			return home.add(Math.cos(orbitAngle) * ORBIT_RADIUS, 0, Math.sin(orbitAngle) * ORBIT_RADIUS);
		}
		Vec3 flat = new Vec3(target.getX() - home.x, 0, target.getZ() - home.z);
		if (mode == Mode.APPROACH) {
			return clampToLeash(home, home.add(flat), LEASH);
		}
		Vec3 away = flat.lengthSqr() < 1.0E-4 ? new Vec3(1, 0, 0) : flat.normalize().scale(-LEASH * 0.7);
		return home.add(away);
	}

	/** Vira o corpo para o alvo (ou para onde nada) aos poucos; o modelo é desenhado pelo giro do corpo. */
	private void face(Mob kraken, Player target) {
		double dx;
		double dz;
		if (target != null) {
			dx = target.getX() - kraken.getX();
			dz = target.getZ() - kraken.getZ();
		} else if (velocity.horizontalDistanceSqr() > 1.0E-4) {
			dx = velocity.x;
			dz = velocity.z;
		} else {
			return;
		}
		float want = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
		float yaw = Mth.approachDegrees(kraken.getYRot(), want, TURN);
		kraken.setYRot(yaw);
		kraken.setYBodyRot(yaw);
		kraken.setYHeadRot(yaw);
	}
}
