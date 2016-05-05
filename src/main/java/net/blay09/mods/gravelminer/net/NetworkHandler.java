package net.blay09.mods.gravelminer.net;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.blay09.mods.gravelminer.GravelMiner;

public class NetworkHandler {

	public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(GravelMiner.MOD_ID);

	public static void init() {
		instance.registerMessage(HandlerHello.class, MessageHello.class, 0, Side.SERVER);
		instance.registerMessage(HandlerHello.class, MessageHello.class, 1, Side.CLIENT);
	}
}
