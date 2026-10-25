package com.aquamarine.sound;

/**
 * Regras da música da luta (docs/09-audio.md, "Luta do Kraken"), sem nada do jogo para poder testar:
 * qual trilha cada fase toca e quanto de cada camada entra pelo estado da luta. Quem toca é o
 * {@code KrakenMusic} do cliente.
 *
 * <ul>
 *   <li><b>base</b> (orquestra, motivo do Kraken, sinos): sempre.</li>
 *   <li><b>breaks</b> (breakcore): sai no respiro, 12 s sem combate; na fase 3 nunca sai.</li>
 *   <li><b>glory</b> (trombetas e coro heroico): cresce com a fase, sobe quando o Kraken passa da metade
 *   da vida da fase, e vai ao máximo com a Carapaça quebrada (atordoado) ou com o jogador por um fio.</li>
 * </ul>
 */
public final class KrakenMusicPlan {
	public static final String[] TRACKS = {"tormenta", "furia", "abismo"};
	public static final String[] LAYERS = {"base", "breaks", "glory"};
	/** Ticks sem combate até o respiro (12 s). */
	public static final int QUIET_TICKS = 240;
	/** Do golpe da troca de fase até a trilha nova entrar (o stinger dura 3,6 s; a subida acaba em 3,4 s). */
	public static final int TRANSITION_TICKS = 68;
	/** Glória de cada fase antes do impulso. */
	public static final float[] GLORY_BY_PHASE = {0.35F, 0.6F, 0.85F};
	public static final float GLORY_HALF_PHASE_BOOST = 0.3F;
	public static final float LOW_PLAYER_HEALTH = 0.3F;

	public static String track(int phase) {
		return TRACKS[clampPhase(phase) - 1];
	}

	/** Vida dentro da fase: 1 no começo dela, 0 na fronteira da próxima (fases de um terço cada). */
	public static float phaseHealth(int phase, float healthFraction) {
		float floor = (3 - clampPhase(phase)) / 3.0F;
		return Math.max(0.0F, Math.min(1.0F, (healthFraction - floor) * 3.0F));
	}

	/** Ganhos de base, breaks e glória (0 a 1). */
	public static float[] gains(int phase, float phaseHealth, boolean stunned, int quietTicks, float playerHealth) {
		int p = clampPhase(phase);
		float breaks = p < 3 && quietTicks >= QUIET_TICKS ? 0.0F : 1.0F;
		float glory = GLORY_BY_PHASE[p - 1] + (phaseHealth < 0.5F ? GLORY_HALF_PHASE_BOOST : 0.0F);
		if (stunned || playerHealth < LOW_PLAYER_HEALTH) {
			glory = 1.0F;
		}
		return new float[] {1.0F, breaks, Math.min(1.0F, glory)};
	}

	private static int clampPhase(int phase) {
		return Math.max(1, Math.min(TRACKS.length, phase));
	}

	private KrakenMusicPlan() {}
}
