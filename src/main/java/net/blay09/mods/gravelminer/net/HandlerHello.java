package net.blay09.mods.gravelminer.net;

import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class HandlerHello implements IMessageHandler<MessageHello, IMessage> {
	@Override
	public IMessage onMessage(MessageHello message, final MessageContext ctx) {
		GravelMiner.proxy.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				GravelMiner.proxy.receivedHello(ctx.side == Side.SERVER ? ctx.getServerHandler().playerEntity : null);
				if(ctx.side == Side.CLIENT) {
					NetworkHandler.instance.sendToServer(new MessageHello());
				}
			}
		});
		return null;
	}
}
