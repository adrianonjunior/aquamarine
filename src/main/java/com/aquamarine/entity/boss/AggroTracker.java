package com.aquamarine.entity.boss;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Aggro do Kraken (docs/02-kraken.md, "IA"): o alvo é quem causou mais dano ao escudo nos
 * últimos 10 s. Cada golpe pesa só dentro da janela, então o aggro decai sozinho.
 */
public final class AggroTracker {
	public static final int WINDOW_TICKS = 200;

	private record Hit(UUID who, float amount, long tick) {}

	private final Deque<Hit> hits = new ArrayDeque<>();

	public void record(UUID who, float amount, long tick) {
		if (amount > 0) {
			hits.addLast(new Hit(who, amount, tick));
		}
	}

	/** Jogador com mais dano na janela, ou {@code null} se ninguém bateu. Empate: o mais antigo. */
	public UUID top(long tick) {
		while (!hits.isEmpty() && tick - hits.peekFirst().tick() > WINDOW_TICKS) {
			hits.removeFirst();
		}
		Map<UUID, Float> totals = new HashMap<>();
		UUID best = null;
		float bestTotal = 0;
		for (Hit hit : hits) {
			float total = totals.merge(hit.who(), hit.amount(), Float::sum);
			if (total > bestTotal) {
				bestTotal = total;
				best = hit.who();
			}
		}
		return best;
	}

	public void clear() {
		hits.clear();
	}
}
