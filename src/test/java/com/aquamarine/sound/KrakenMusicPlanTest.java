package com.aquamarine.sound;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Regras da música da luta (docs/09-audio.md, "Luta do Kraken"). */
class KrakenMusicPlanTest {
	private static final float EPS = 1e-4F;

	@Test
	void eachPhaseHasItsTrack() {
		assertEquals("tormenta", KrakenMusicPlan.track(1));
		assertEquals("furia", KrakenMusicPlan.track(2));
		assertEquals("abismo", KrakenMusicPlan.track(3));
		assertEquals("tormenta", KrakenMusicPlan.track(0));
		assertEquals("abismo", KrakenMusicPlan.track(9));
	}

	@Test
	void phaseHealthGoesFromOneToZeroInsideEachThird() {
		assertEquals(1.0F, KrakenMusicPlan.phaseHealth(1, 1.0F), EPS);
		assertEquals(0.0F, KrakenMusicPlan.phaseHealth(1, 2 / 3F), EPS);
		assertEquals(0.5F, KrakenMusicPlan.phaseHealth(2, 0.5F), EPS);
		assertEquals(0.0F, KrakenMusicPlan.phaseHealth(3, 0.0F), EPS);
		assertEquals(1.0F, KrakenMusicPlan.phaseHealth(3, 0.9F), EPS);
	}

	@Test
	void breaksRestWhenTheFightIsQuietExceptInTheAbyss() {
		int quiet = KrakenMusicPlan.QUIET_TICKS;
		assertEquals(1.0F, KrakenMusicPlan.gains(1, 1.0F, false, quiet - 1, 1.0F)[1], EPS);
		assertEquals(0.0F, KrakenMusicPlan.gains(1, 1.0F, false, quiet, 1.0F)[1], EPS);
		assertEquals(0.0F, KrakenMusicPlan.gains(2, 1.0F, false, quiet, 1.0F)[1], EPS);
		assertEquals(1.0F, KrakenMusicPlan.gains(3, 1.0F, false, quiet * 10, 1.0F)[1], EPS);
	}

	@Test
	void gloryGrowsWithPhaseMomentumAndDanger() {
		assertArrayEquals(new float[] {1.0F, 1.0F, 0.35F}, KrakenMusicPlan.gains(1, 1.0F, false, 0, 1.0F), EPS);
		assertEquals(0.65F, KrakenMusicPlan.gains(1, 0.4F, false, 0, 1.0F)[2], EPS);
		assertEquals(0.6F, KrakenMusicPlan.gains(2, 0.9F, false, 0, 1.0F)[2], EPS);
		assertEquals(1.0F, KrakenMusicPlan.gains(3, 0.2F, false, 0, 1.0F)[2], EPS);
		assertEquals(1.0F, KrakenMusicPlan.gains(1, 1.0F, true, 0, 1.0F)[2], EPS);
		assertEquals(1.0F, KrakenMusicPlan.gains(1, 1.0F, false, 0, 0.2F)[2], EPS);
	}
}
