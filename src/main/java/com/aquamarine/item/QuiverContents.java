package com.aquamarine.item;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Conteúdo da Aljava de Arpões: quantas pontas de cada tipo ({@link HarpoonTip}, na ordem do enum) e
 * qual está escolhida. Imutável, como todo componente de item.
 */
public record QuiverContents(List<Integer> counts, int selected) {
	/** Até 64 pontas de cada tipo. */
	public static final int MAX_PER_TIP = 64;
	public static final QuiverContents EMPTY = new QuiverContents(List.of(0, 0, 0, 0), 0);

	public static final Codec<QuiverContents> CODEC = RecordCodecBuilder.create(i -> i.group(
		Codec.INT.listOf().fieldOf("counts").forGetter(QuiverContents::counts),
		Codec.INT.fieldOf("selected").forGetter(QuiverContents::selected)
	).apply(i, QuiverContents::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, QuiverContents> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), QuiverContents::counts,
		ByteBufCodecs.VAR_INT, QuiverContents::selected,
		QuiverContents::new);

	public int count(HarpoonTip tip) {
		return tip.ordinal() < counts.size() ? counts.get(tip.ordinal()) : 0;
	}

	public HarpoonTip selectedTip() {
		HarpoonTip[] tips = HarpoonTip.values();
		return tips[Math.floorMod(selected, tips.length)];
	}

	public int total() {
		return counts.stream().mapToInt(Integer::intValue).sum();
	}

	public QuiverContents with(HarpoonTip tip, int count) {
		List<Integer> next = new ArrayList<>(counts);
		while (next.size() < HarpoonTip.values().length) {
			next.add(0);
		}
		next.set(tip.ordinal(), Math.max(0, Math.min(MAX_PER_TIP, count)));
		return new QuiverContents(List.copyOf(next), selected);
	}

	/** Próxima ponta com estoque (ou só a próxima, se a Aljava estiver vazia). */
	public QuiverContents cycle() {
		int n = HarpoonTip.values().length;
		for (int step = 1; step <= n; step++) {
			int index = Math.floorMod(selected + step, n);
			if (count(HarpoonTip.values()[index]) > 0) {
				return new QuiverContents(counts, index);
			}
		}
		return new QuiverContents(counts, Math.floorMod(selected + 1, n));
	}
}
