package net.blay09.mods.gravelminer.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.blay09.mods.gravelminer.GravelMiner;

public class HandlerHello implements IMessageHandler<MessageHello, IMessage> {
	@Override
	public IMessage onMessage(MessageHello message, final MessageContext ctx) {
		GravelMiner.proxy.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				GravelMiner.proxy.receivedHello(ctx.side == Side.SERVER ? ctx.getServerHandler().playerEntity : null);
			}
		});
		return ctx.side == Side.CLIENT ? new MessageHello() : null;
	}
}
