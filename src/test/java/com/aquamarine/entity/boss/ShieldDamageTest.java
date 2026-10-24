package com.aquamarine.entity.boss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ShieldDamageTest {
	private static float amount(ShieldDamage.Source source, float damage, int shatter, boolean node, boolean exposed) {
		return ShieldDamage.amount(source, damage, 400.0F, shatter, node, exposed);
	}

	@Test
	void tableBySource() {
		assertEquals(2.5F, amount(ShieldDamage.Source.COMMON, 10, 0, false, false), 1e-5);
		assertEquals(6.0F, amount(ShieldDamage.Source.ORICHALCUM, 10, 0, false, false), 1e-5);
		assertEquals(30.0F, amount(ShieldDamage.Source.HARPOON, 10, 0, false, false), 1e-5);
		assertEquals(100.0F, amount(ShieldDamage.Source.PULSE, 0, 0, false, false), 1e-5);
		assertEquals(20.0F, amount(ShieldDamage.Source.PILLAR, 0, 0, false, false), 1e-5);
	}

	@Test
	void shellBreakerAddsTenPercentPerLevelUpToThree() {
		assertEquals(3.25F, amount(ShieldDamage.Source.COMMON, 10, 3, false, false), 1e-5);
		assertEquals(3.25F, amount(ShieldDamage.Source.COMMON, 10, 9, false, false), 1e-5);
		assertEquals(2.5F, amount(ShieldDamage.Source.COMMON, 10, -1, false, false), 1e-5);
	}

	@Test
	void nodeDoublesButNotPulseOrPillar() {
		assertEquals(5.0F, amount(ShieldDamage.Source.COMMON, 10, 0, true, false), 1e-5);
		assertEquals(100.0F, amount(ShieldDamage.Source.PULSE, 0, 0, true, false), 1e-5);
		assertEquals(20.0F, amount(ShieldDamage.Source.PILLAR, 0, 0, true, false), 1e-5);
	}

	@Test
	void exposedAddsQuarter() {
		assertEquals(3.125F, amount(ShieldDamage.Source.COMMON, 10, 0, false, true), 1e-5);
		assertEquals(125.0F, amount(ShieldDamage.Source.PULSE, 0, 0, false, true), 1e-5);
	}

	@Test
	void nodeCountFollowsPlayers() {
		assertEquals(4, TideNodes.count(1));
		assertEquals(4, TideNodes.count(4));
		assertEquals(4, TideNodes.count(5));
		assertEquals(5, TideNodes.count(6));
		assertEquals(8, TideNodes.count(12));
		assertEquals(8, TideNodes.count(40));
	}

	@Test
	void nodeLayoutsDifferByPhase() {
		for (int i = 0; i < 4; i++) {
			assertTrue(TideNodes.offset(1, i, 4).z() < 0, "fase 1: nas costas");
			assertEquals(i % 2 == 0 ? 4.2 : -4.2, TideNodes.offset(2, i, 4).x(), 1e-9);
			assertTrue(TideNodes.offset(3, i, 4).y() < 0, "fase 3: sob o corpo");
		}
		TideNodes.Offset turned = TideNodes.rotate(new TideNodes.Offset(1, 2, 0), 90.0F);
		assertEquals(0.0, turned.x(), 1e-9);
		assertEquals(1.0, turned.z(), 1e-9);
	}
}
