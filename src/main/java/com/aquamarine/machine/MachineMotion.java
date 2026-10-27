package com.aquamarine.machine;

/**
 * Relógio das animações de uma máquina no cliente (0.6.1, 7B): o estado atual, o anterior, quando trocou e a fase do
 * clipe {@code run}, que anda na velocidade da produção (0,5x a 2x) sem pular quando a velocidade muda. Só guarda dados:
 * quem escolhe e mistura os clipes é o renderizador ({@code com.aquamarine.client.machine.MachineAnimator}).
 */
public final class MachineMotion {
	private MachineState state = MachineState.IDLE;
	private MachineState previous = MachineState.IDLE;
	private double since = Double.NEGATIVE_INFINITY;
	private float speed = 1.0F;
	private double anchorTime;
	private double anchorPhase;
	private double phaseOffset;
	private boolean seen;

	/**
	 * Estado vindo do servidor; {@code now} em segundos de jogo. A primeira leitura (chunk carregando) não anima a troca.
	 * Ao ligar, a fase do run começa do zero no fim do clipe {@code start} ({@code startSeconds} depois).
	 */
	public void update(MachineState next, float nextSpeed, double now, double startSeconds) {
		if (!seen) {
			seen = true;
			state = previous = next;
			speed = nextSpeed;
			anchorTime = now;
			anchorPhase = 0;
			return;
		}
		if (next != state) {
			if (next == MachineState.RUNNING && state == MachineState.IDLE) {
				anchorTime = now + startSeconds;
				anchorPhase = 0;
				speed = nextSpeed;
			}
			previous = state;
			state = next;
			since = now;
		}
		if (nextSpeed != speed) {
			anchorPhase = runPhase(now);
			anchorTime = now;
			speed = nextSpeed;
		}
	}

	/** Segundos do clipe run (sem o módulo do laço). */
	public double runPhase(double now) {
		return anchorPhase + (now - anchorTime) * speed;
	}

	public MachineState state() {
		return state;
	}

	public MachineState previous() {
		return previous;
	}

	/** Segundos de jogo da última troca; menos infinito se nunca trocou desde que o chunk carregou. */
	public double since() {
		return since;
	}

	public float speed() {
		return speed;
	}

	/** Deslocamento determinístico do clipe em segundos; usado por módulos vizinhos para formar uma onda contínua. */
	public void setPhaseOffset(double seconds) {
		phaseOffset = seconds;
	}

	public double phaseOffset() {
		return phaseOffset;
	}
}
