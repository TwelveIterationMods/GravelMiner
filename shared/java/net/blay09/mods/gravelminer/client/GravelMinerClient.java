package net.blay09.mods.gravelminer.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.event.client.ConnectedToServerEvent;
import net.blay09.mods.gravelminer.GravelMinerConfig;
import net.blay09.mods.gravelminer.network.SetEnabledMessage;

public class GravelMinerClient {
    public static void initialize() {
        ModKeyBindings.initialize(BalmClient.getKeyMappings());

        Balm.getEvents().onEvent(ConnectedToServerEvent.class, event -> Balm.getNetworking().sendToServer(new SetEnabledMessage(GravelMinerConfig.getActive().client.isEnabled)));
    }
}
