package net.blay09.mods.gravelminer.net;

import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(GravelMiner.MOD_ID, "network"), () -> "1.0", it -> {
        GravelMiner.isServerInstalled = it.equals("1.0");
        return true;
    }, it -> true);

    public static void init() {
        channel.registerMessage(0, MessageHello.class, (message, buf) -> {
        }, it -> new MessageHello(), MessageHello::handle);
        channel.registerMessage(1, MessageSetEnabled.class, MessageSetEnabled::encode, MessageSetEnabled::decode, MessageSetEnabled::handle);
    }
}
