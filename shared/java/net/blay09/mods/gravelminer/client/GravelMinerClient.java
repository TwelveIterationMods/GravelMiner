package net.blay09.mods.gravelminer.client;

import net.blay09.mods.balm.event.client.BalmClientEvents;
import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.gravelminer.GravelMinerConfig;
import net.blay09.mods.gravelminer.network.SetEnabledMessage;

public class GravelMinerClient {
    public static void initialize() {
        ModKeyBindings.initialize();

        BalmClientEvents.onConnectedToServer(it -> {
            BalmNetworking.sendToServer(new SetEnabledMessage(GravelMinerConfig.getActive().client.isEnabled));
        });
    }
}
