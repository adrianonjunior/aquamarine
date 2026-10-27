package com.aquamarine.network;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Um pedaço de rede (0.6.1, 7C): Cano, Cabo e Tubo de Ar ({@link ConduitBlock}) ou Válvula e Visor ({@link InlineConduitBlock}).
 * Vazões (por tick, em cada conector) e quanto cada pedaço guarda são valores iniciais a testar no jogo.
 */
public interface ConduitPiece {
	/** Cabo: 256 E/t; Reforçado (Aurium): 2048 E/t. */
	long CABLE_RATE = 256;
	long REINFORCED_CABLE_RATE = 2048;
	/** Cano: 1 balde por segundo; Reforçado: 4 baldes por segundo. */
	long PIPE_RATE = FluidConstants.BUCKET / 20;
	long REINFORCED_PIPE_RATE = FluidConstants.BUCKET / 5;
	/** Tubo de Ar: 2 baldes de ar por segundo. */
	long TUBE_RATE = FluidConstants.BUCKET / 10;

	ConduitKind kind();

	boolean reinforced();

	/** O pedaço liga pelo lado {@code side}: o cano por todos; válvula e visor só no eixo (e a válvula só aberta). */
	boolean opens(BlockState state, Direction side);

	default long rate() {
		return switch (kind()) {
			case ENERGY -> reinforced() ? REINFORCED_CABLE_RATE : CABLE_RATE;
			case FLUID -> reinforced() ? REINFORCED_PIPE_RATE : PIPE_RATE;
			case GAS -> TUBE_RATE * (reinforced() ? 2 : 1);
		};
	}

	/** Quanto cada pedaço soma ao estoque da rede: energia, 4 ticks de vazão; fluido e gás, 20 ticks (1 balde no Cano). */
	default long capacity() {
		return rate() * (kind() == ConduitKind.ENERGY ? 4 : 20);
	}
}
