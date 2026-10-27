package com.aquamarine.client.manual;

import com.aquamarine.guide.ManualItem;
import com.aquamarine.guide.OpenManualPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

/** Liga o Manual no cliente: o item abre a tela e o {@code /aquamarine help} também. */
public final class ManualClient {
	public static void register() {
		ManualItem.opener = () -> Minecraft.getInstance().gui.setScreen(new ManualScreen());
		ClientPlayNetworking.registerGlobalReceiver(OpenManualPayload.TYPE, (payload, context) -> context.client().gui.setScreen(new ManualScreen()));
	}

	private ManualClient() {}
}
