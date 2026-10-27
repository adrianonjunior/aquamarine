package com.aquamarine.network;

import net.minecraft.network.chat.Component;

/**
 * Modo de um lado com conector (0.6.1, 7C): a Chave de Maré cicla sai, entra e desligado. "Sai" é a rede entregando à
 * máquina (o padrão), "entra" é a rede puxando dela, "desligado" corta a ligação. Entre dois canos só vale ligado ou
 * desligado.
 */
public enum PortMode {
	OUT,
	IN,
	OFF;

	public PortMode next(boolean port) {
		if (!port) {
			return this == OFF ? OUT : OFF;
		}
		return switch (this) {
			case OUT -> IN;
			case IN -> OFF;
			case OFF -> OUT;
		};
	}

	public Component label() {
		return Component.translatable("message.aquamarine.port." + name().toLowerCase(java.util.Locale.ROOT));
	}

	public static PortMode byId(int id) {
		PortMode[] all = values();
		return id >= 0 && id < all.length ? all[id] : OUT;
	}
}
