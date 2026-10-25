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
			org.junit.jupiter.api.Assertions.assertTrue(PressureState.gravityBonus(i) < PressureState.gravityBonus(i + 1));
		}
		assertEquals(0.0, PressureState.gravityBonus(0));
	}

	private static void runAmbient(PressureState s, int ticks, boolean krakenExposed, int ambientTarget, boolean tonic) {
		for (int i = 0; i < ticks; i++) {
			s.tick(krakenExposed, ambientTarget, tonic, 0, 1);
		}
	}

	@Test
	void niveisAmbientaisPorProfundidade() {
		assertEquals(0, PressureState.ambientCap(0));
		assertEquals(0, PressureState.ambientCap(29.9));
		assertEquals(1, PressureState.ambientCap(30));
		assertEquals(1, PressureState.ambientCap(49.9));
		assertEquals(2, PressureState.ambientCap(50));
		assertEquals(2, PressureState.ambientCap(69.9));
		assertEquals(3, PressureState.ambientCap(70));
		assertEquals(3, PressureState.ambientCap(500));
	}

	@Test
	void fonteAmbientalSobeNasMesmasRegrasEPara3() {
		PressureState s = new PressureState();
		runAmbient(s, 199, false, 3, false);
		assertEquals(0, s.level());
		runAmbient(s, 1, false, 3, false);
		assertEquals(1, s.level());
		runAmbient(s, 5000, false, 3, false);
		assertEquals(3, s.level(), "a Pressão ambiental nunca chega a 4 sozinha");
		assertEquals(0, s.krakenLevel());
	}

	@Test
	void fonteAmbientalDesceQuandoAlvoCai() {
		PressureState s = new PressureState();
		runAmbient(s, 700, false, 3, false);
		assertEquals(3, s.level());
		runAmbient(s, 100, false, 1, false);
		assertEquals(2, s.level());
		runAmbient(s, 100, false, 1, false);
		assertEquals(1, s.level());
		runAmbient(s, 400, false, 1, false);
		assertEquals(1, s.level(), "no alvo, o nível fica");
		runAmbient(s, 100, false, 0, false);
		assertEquals(0, s.level());
	}

	@Test
	void nivelFinalEOMaiorDasFontesENaoASoma() {
		PressureState s = new PressureState();
		runAmbient(s, 700, false, 3, false);
		assertEquals(3, s.level());
		runAmbient(s, 400, true, 3, false);
		assertEquals(3, s.level(), "Kraken em 2 e ambiental em 3: vale 3, não 5");
		runAmbient(s, 400, true, 3, false);
		assertEquals(4, s.level());
		assertEquals(3, s.ambientLevel());
		assertEquals(4, s.krakenLevel());
		// saindo do alcance do Kraken, volta ao ambiental
		runAmbient(s, 100, false, 3, false);
		assertEquals(3, s.level());
	}

	@Test
	void tonicoLimitaAsDuasFontes() {
		PressureState s = new PressureState();
		runAmbient(s, 900, true, 3, false);
		assertEquals(4, s.level());
		runAmbient(s, 1, true, 3, true);
		assertEquals(2, s.level());
		assertEquals(2, s.ambientLevel());
	}

	@Test
	void alturaSemKrakenNaoMudaALuta() {
		// sem fonte ambiental (alvo 0) o comportamento é exatamente o da Entrega 2
		PressureState s = new PressureState();
		PressureState old = new PressureState();
		for (int i = 0; i < 900; i++) {
			s.tick(true, 0, false, 0, 1);
			old.tick(true, false, 0, 1);
			assertEquals(old.level(), s.level());
		}
	}
}
