package com.aquamarine.entity.boss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class KrakenStatsTest {
	@Test
	void scaleFollowsFormulaAndCap() {
		assertEquals(1.0F, KrakenStats.scale(1, 12), 1e-6);
		assertEquals(1.0F, KrakenStats.scale(0, 12), 1e-6);
		assertEquals(2.2F, KrakenStats.scale(5, 12), 1e-6);
		assertEquals(4.3F, KrakenStats.scale(12, 12), 1e-6);
		assertEquals(4.3F, KrakenStats.scale(40, 12), 1e-6);
		assertEquals(1.9F, KrakenStats.scale(40, 4), 1e-6);
	}

	@Test
	void healthAndShieldPerPhase() {
		assertEquals(1500F, KrakenStats.maxHealth(1, "normal"), 1e-3);
		assertEquals(1500F * 2.2F * 1.5F, KrakenStats.maxHealth(2.2F, "abissal"), 1e-2);
		assertEquals(400F, KrakenStats.maxShield(1, 1, "normal"), 1e-3);
		assertEquals(600F * 2.2F, KrakenStats.maxShield(2, 2.2F, "normal"), 1e-3);
		assertEquals(800F * 1.5F, KrakenStats.maxShield(3, 1, "abissal"), 1e-3);
	}

	@Test
	void phasesByHealthFraction() {
		assertEquals(1, KrakenStats.phaseFor(1.0F));
		assertEquals(1, KrakenStats.phaseFor(0.67F));
		assertEquals(2, KrakenStats.phaseFor(0.66F));
		assertEquals(2, KrakenStats.phaseFor(0.34F));
		assertEquals(3, KrakenStats.phaseFor(0.33F));
		assertEquals(0F, KrakenStats.phaseFloorFraction(3), 1e-6);
		assertEquals(2F / 3F, KrakenStats.phaseFloorFraction(1), 1e-6);
	}

	@Test
	void aggroPicksTopDamageInWindowAndDecays() {
		AggroTracker aggro = new AggroTracker();
		UUID a = UUID.randomUUID();
		UUID b = UUID.randomUUID();
		assertNull(aggro.top(0));
		aggro.record(a, 50, 0);
		aggro.record(b, 30, 10);
		aggro.record(b, 30, 20);
		assertEquals(b, aggro.top(30));
		assertEquals(b, aggro.top(205));
		assertNull(aggro.top(500));
	}
}
