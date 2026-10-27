package com.aquamarine.guide;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Manual do Aquamarine (pedido de 27/10/2026): o botão direito abre a tela do livro no cliente. A tela é do cliente, por
 * isso quem abre é o {@link #opener} que o cliente instala (o servidor nem sabe que ela existe).
 */
public class ManualItem extends Item {
	/** Instalado pelo cliente (GuideClient); no servidor dedicado fica sem fazer nada. */
	public static Runnable opener = () -> {};

	public ManualItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide()) {
			opener.run();
		}
		return InteractionResult.SUCCESS;
	}
}
