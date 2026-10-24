package com.aquamarine.entity.boss;

/**
 * Posições dos Nós de Maré, os pontos fracos do escudo (docs/02-kraken.md): fase 1 nas costas,
 * fase 2 nos flancos, fase 3 sob o corpo. Coordenadas locais ao Kraken (x à direita, y acima
 * dos pés, z à frente). Só contas, para testar com JUnit.
 */
public final class TideNodes {
	public static final int BASE_COUNT = 4;
	public static final int MAX_COUNT = 8;
	/** Lado da hitbox cúbica de cada nó, já na escala do Kraken final. */
	public static final double SIZE = 1.5 * 4.0;
	/** Os offsets abaixo foram desenhados para 8 x 6 blocos; o Kraken final (32 x 20) é 4 vezes maior. */
	public static final double WORLD_SCALE = 4.0;

	public record Offset(double x, double y, double z) {}

	/** 4 nós até 4 jogadores; depois 1 nó a cada 2 jogadores, no máximo 8. */
	public static int count(int players) {
		return Math.min(MAX_COUNT, BASE_COUNT + Math.max(0, players - BASE_COUNT) / 2);
	}

	public static Offset offset(int phase, int index, int count) {
		return switch (Math.max(1, Math.min(KrakenStats.PHASES, phase))) {
			case 1 -> {
				double angle = Math.PI + (index - (count - 1) / 2.0) * 0.5;
				yield new Offset(Math.sin(angle) * 4.2, 3.5, Math.cos(angle) * 4.2);
			}
			case 2 -> {
				double side = index % 2 == 0 ? 1.0 : -1.0;
				int rows = (count + 1) / 2;
				yield new Offset(side * 4.2, 3.0, (index / 2 - (rows - 1) / 2.0) * 2.2);
			}
			default -> {
				double angle = Math.PI * 2.0 * index / count;
				yield new Offset(Math.sin(angle) * 2.5, -0.5, Math.cos(angle) * 2.5);
			}
		};
	}

	/** Gira um deslocamento local pela rotação {@code yawDegrees} do Kraken (convenção do jogo). */
	public static Offset rotate(Offset local, float yawDegrees) {
		double rad = Math.toRadians(yawDegrees);
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);
		return new Offset(local.x() * cos - local.z() * sin, local.y(), local.x() * sin + local.z() * cos);
	}

	private TideNodes() {}
}
