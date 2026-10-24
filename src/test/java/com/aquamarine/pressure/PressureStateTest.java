package com.aquamarine.pressure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PressureStateTest {
	private static void run(PressureState s, int ticks, boolean exposed, boolean tonic, float reduction, int phase) {
		for (int i = 0; i < ticks; i++) {
			s.tick(exposed, tonic, reduction, phase);
		}
	}

	@Test
	void sobeUmNivelACada10Segundos() {
		PressureState s = new PressureState();
		run(s, 199, true, false, 0, 1);
		assertEquals(0, s.level());
		run(s, 1, true, false, 0, 1);
		assertEquals(1, s.level());
		run(s, 600, true, false, 0, 1);
		assertEquals(PressureState.MAX_LEVEL, s.level());
	}

	@Test
	void naFase3SobeACada6Segundos() {
		PressureState s = new PressureState();
		run(s, 120, true, false, 0, 3);
		assertEquals(1, s.level());
	}

	@Test
	void desceUmNivelACada5SegundosForaDaCondicao() {
		PressureState s = new PressureState();
		run(s, 800, true, false, 0, 1);
		assertEquals(4, s.level());
		run(s, 99, false, false, 0, 1);
		assertEquals(4, s.level());
		run(s, 1, false, false, 0, 1);
		assertEquals(3, s.level());
		run(s, 300, false, false, 0, 1);
		assertEquals(0, s.level());
	}

	@Test
	void armaduraCompletaReduzOGanhoEm60Porcento() {
		PressureState s = new PressureState();
		float full = 0.15F * 4;
		run(s, 499, true, false, full, 1); // 200 / 0,4 = 500 ticks por nivel
		assertEquals(0, s.level());
		run(s, 3, true, false, full, 1); // folga do arredondamento de float
		assertEquals(1, s.level());
	}

	@Test
	void tonicoLimitaOMaximoEmDoisNiveis() {
		PressureState s = new PressureState();
		run(s, 2000, true, true, 0, 1);
		assertEquals(2, s.level());
		// o Tônico vencendo com a Pressão acima do novo máximo derruba o nível
		PressureState t = new PressureState();
		run(t, 2000, true, false, 0, 1);
		assertEquals(4, t.level());
		run(t, 1, true, true, 0, 1);
		assertEquals(2, t.level());
	}

	@Test
	void resetZeraOEstado() {
		PressureState s = new PressureState();
		run(s, 450, true, false, 0, 1);
		assertEquals(2, s.level());
		s.reset();
		assertEquals(0, s.level());
	}

	@Test
	void empuxoCresceComONivel() {
		for (int i = 1; i < PressureState.MAX_LEVEL; i++) {
			org.junit.jupiter.api.Assertions.assertTrue(PressureState.sinkForce(i) < PressureState.sinkForce(i + 1));
		}
		assertEquals(0.0, PressureState.sinkForce(0));
	}
}
