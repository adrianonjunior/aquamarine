package com.aquamarine.entity.boss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.world.phys.Vec3;

import org.junit.jupiter.api.Test;

class KrakenMovementTest {
	@Test
	void speedGrowsWithPhaseAndPhaseThreeIsThirtyPercentFaster() {
		assertTrue(KrakenMovement.speed(1) < KrakenMovement.speed(2));
		assertEquals(KrakenMovement.speed(2) * 1.3, KrakenMovement.speed(3), 1e-9);
	}

	@Test
	void leashKeepsTheBodyNearHome() {
		Vec3 home = new Vec3(100, 40, -50);
		Vec3 inside = new Vec3(105, 41, -50);
		assertEquals(inside, KrakenMovement.clampToLeash(home, inside, 12.0));
		Vec3 far = KrakenMovement.clampToLeash(home, new Vec3(100, 30, -10), 12.0);
		assertEquals(12.0, Math.sqrt(Math.pow(far.x - home.x, 2) + Math.pow(far.z - home.z, 2)), 1e-9);
		assertEquals(30.0, far.y, 1e-9);
	}

	@Test
	void phaseDepthMatchesTheFight() {
		// fase 2 sobe (emerge), fase 3 desce ao fundo da Cratera
		assertTrue(KrakenMovement.depthOffset(2) > KrakenMovement.depthOffset(1));
		assertTrue(KrakenMovement.depthOffset(3) < KrakenMovement.depthOffset(1));
	}
}
