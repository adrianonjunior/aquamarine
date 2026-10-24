package com.aquamarine.entity.boss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class KrakenAttackTypeTest {
	@Test
	void simultaneosSaoDoisMaisUmTercoDosJogadores() {
		assertEquals(2, KrakenAttackType.maxSimultaneous(1));
		assertEquals(3, KrakenAttackType.maxSimultaneous(3));
		assertEquals(4, KrakenAttackType.maxSimultaneous(6));
		assertEquals(8, KrakenAttackType.maxSimultaneous(30));
	}

	@Test
	void criasSaoTresMaisMetadeDosJogadores() {
		assertEquals(3, KrakenAttackType.youngCount(1));
		assertEquals(4, KrakenAttackType.youngCount(2));
		assertEquals(7, KrakenAttackType.youngCount(8));
	}

	@Test
	void ataquesLiberadosPorFase() {
		Set<KrakenAttackType> none = EnumSet.noneOf(KrakenAttackType.class);
		List<KrakenAttackType> phase1 = KrakenAttackType.candidates(1, null, none, 1000, 8);
		assertEquals(Set.of(KrakenAttackType.SLAM, KrakenAttackType.GRAB, KrakenAttackType.INK), Set.copyOf(phase1));
		assertEquals(7, KrakenAttackType.candidates(2, null, none, 1000, 8).size());
		assertEquals(10, KrakenAttackType.candidates(3, null, none, 1000, 8).size());
	}

	@Test
	void naoRepeteOUltimoNemOsAtivos() {
		Set<KrakenAttackType> running = EnumSet.of(KrakenAttackType.GRAB);
		List<KrakenAttackType> list = KrakenAttackType.candidates(1, KrakenAttackType.SLAM, running, 1000, 8);
		assertFalse(list.contains(KrakenAttackType.SLAM));
		assertFalse(list.contains(KrakenAttackType.GRAB));
		assertTrue(list.contains(KrakenAttackType.INK));
	}

	@Test
	void areaTemIntervaloMinimoDe8Segundos() {
		Set<KrakenAttackType> none = EnumSet.noneOf(KrakenAttackType.class);
		List<KrakenAttackType> list = KrakenAttackType.candidates(3, null, none, 100, 8);
		assertFalse(list.contains(KrakenAttackType.INK));
		assertFalse(list.contains(KrakenAttackType.ROAR));
		assertTrue(list.contains(KrakenAttackType.SLAM));
		assertTrue(KrakenAttackType.candidates(3, null, none, 160, 8).contains(KrakenAttackType.INK));
	}

	@Test
	void ataquesDeTentaculoPrecisamDeTentaculosLivres() {
		Set<KrakenAttackType> none = EnumSet.noneOf(KrakenAttackType.class);
		List<KrakenAttackType> list = KrakenAttackType.candidates(3, null, none, 1000, 1);
		assertFalse(list.contains(KrakenAttackType.DOUBLE_GRAB));
		assertTrue(list.contains(KrakenAttackType.GRAB));
		assertFalse(KrakenAttackType.candidates(3, null, none, 1000, 0).contains(KrakenAttackType.SLAM));
	}

	@Test
	void nomesDeAtaqueEAnimacaoCoincidem() {
		assertEquals(KrakenAttackType.DOUBLE_GRAB, KrakenAttackType.byName("doubleGrab"));
		assertEquals(KrakenAttackType.WATER_JET, KrakenAttackType.byName("water_jet"));
		assertNull(KrakenAttackType.byName("nada"));
		assertEquals(10, KrakenAttackType.names().size());
	}

	@Test
	void intervaloCaiComAFaseEAbissalAcelera() {
		assertTrue(KrakenAttackType.attackInterval(1, false) > KrakenAttackType.attackInterval(3, false));
		assertTrue(KrakenAttackType.attackInterval(2, true) < KrakenAttackType.attackInterval(2, false));
	}
}
