package net.blay09.mods.gravelminer.net;

import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerSetEnabled implements IMessageHandler<MessageSetEnabled, IMessage> {
	@Override
	public IMessage onMessage(final MessageSetEnabled message, final MessageContext ctx) {
		GravelMiner.proxy.addScheduledTask(() -> GravelMiner.setHasEnabled(ctx.getServerHandler().player, message.isEnabled()));
		return null;
	}
}
