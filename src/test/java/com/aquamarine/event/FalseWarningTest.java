package com.aquamarine.event;

import static com.aquamarine.event.FalseWarning.Outcome.COOLDOWN;
import static com.aquamarine.event.FalseWarning.Outcome.MISSED;
import static com.aquamarine.event.FalseWarning.Outcome.NOT_ELIGIBLE;
import static com.aquamarine.event.FalseWarning.Outcome.TRIGGERED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FalseWarningTest {
	private static final long NIGHT_0 = 14000;
	private static final long NIGHT_1 = 24000 + 14000;

	@Test
	void triggersOnStormyDeepOceanNightWithLuckyRoll() {
		assertEquals(TRIGGERED, FalseWarning.decide(true, true, true, NIGHT_0, -1, FalseWarning.NEVER, 0.05));
	}

	@Test
	void missesWhenRollIsAboveChance() {
		assertEquals(MISSED, FalseWarning.decide(true, true, true, NIGHT_0, -1, FalseWarning.NEVER, 0.08));
		assertEquals(MISSED, FalseWarning.decide(true, true, true, NIGHT_0, -1, FalseWarning.NEVER, 0.9));
	}

	@Test
	void needsEveryCondition() {
		assertEquals(NOT_ELIGIBLE, FalseWarning.decide(false, true, true, NIGHT_0, -1, FalseWarning.NEVER, 0.0));
		assertEquals(NOT_ELIGIBLE, FalseWarning.decide(true, false, true, NIGHT_0, -1, FalseWarning.NEVER, 0.0));
		assertEquals(NOT_ELIGIBLE, FalseWarning.decide(true, true, false, NIGHT_0, -1, FalseWarning.NEVER, 0.0));
	}

	@Test
	void rollsOnlyOncePerNight() {
		long checked = FalseWarning.nightIndex(NIGHT_0);
		assertEquals(NOT_ELIGIBLE, FalseWarning.decide(true, true, true, NIGHT_0 + 3000, checked, FalseWarning.NEVER, 0.0));
		assertEquals(TRIGGERED, FalseWarning.decide(true, true, true, NIGHT_1, checked, FalseWarning.NEVER, 0.0));
	}

	@Test
	void waitsThreeDaysBetweenWarnings() {
		long day = FalseWarning.dayIndex(NIGHT_1);
		assertEquals(COOLDOWN, FalseWarning.decide(true, true, true, NIGHT_1, 0, day - 2, 0.0));
		assertEquals(TRIGGERED, FalseWarning.decide(true, true, true, NIGHT_1, 0, day - 3, 0.0));
	}

	@Test
	void nightSpansTheDarkHoursOfOneDay() {
		assertEquals(0, FalseWarning.nightIndex(13000));
		assertEquals(0, FalseWarning.nightIndex(23000));
		assertEquals(0, FalseWarning.nightIndex(24000 + 5000)); // madrugada ainda é a mesma noite
		assertEquals(1, FalseWarning.nightIndex(24000 + 13000));
	}
}
