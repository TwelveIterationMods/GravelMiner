package net.blay09.mods.gravelminer.net;

import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

	public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(GravelMiner.MOD_ID);

	public static void init() {
		instance.registerMessage(HandlerHello.class, MessageHello.class, 0, Side.SERVER);
		instance.registerMessage(HandlerHello.class, MessageHello.class, 1, Side.CLIENT);
	}
}
