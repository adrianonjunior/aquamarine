package com.aquamarine.harbor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HarborTest {
	@Test
	void reputationOpensTiersAtVanillaThresholds() {
		assertEquals(1, HarborReputation.tier(0));
		assertEquals(1, HarborReputation.tier(9));
		assertEquals(2, HarborReputation.tier(10));
		assertEquals(2, HarborReputation.tier(69));
		assertEquals(3, HarborReputation.tier(70));
		assertEquals(4, HarborReputation.tier(150));
		assertEquals(5, HarborReputation.tier(250));
		assertEquals(5, HarborReputation.tier(100_000));
	}

	@Test
	void defendingOnceOpensTheSecondTier() {
		assertEquals(2, HarborReputation.tier(HarborAlert.REPUTATION));
	}

	@Test
	void alertNeedsEveryCondition() {
		assertTrue(HarborAlert.decide(true, true, true, 10, HarborAlert.NEVER));
		assertFalse(HarborAlert.decide(false, true, true, 10, HarborAlert.NEVER));
		assertFalse(HarborAlert.decide(true, false, true, 10, HarborAlert.NEVER));
		assertFalse(HarborAlert.decide(true, true, false, 10, HarborAlert.NEVER));
	}

	@Test
	void alertWaitsThreeDays() {
		assertFalse(HarborAlert.decide(true, true, true, 12, 10));
		assertTrue(HarborAlert.decide(true, true, true, 13, 10));
	}
}
