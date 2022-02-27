package net.blay09.mods.gravelminer.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.event.client.ConnectedToServerEvent;
import net.blay09.mods.gravelminer.GravelMinerClientSetting;
import net.blay09.mods.gravelminer.GravelMinerConfig;
import net.blay09.mods.gravelminer.network.SetClientSettingMessage;

public class GravelMinerClient {
    public static void initialize() {
        ModKeyBindings.initialize(BalmClient.getKeyMappings());

        Balm.getEvents().onEvent(ConnectedToServerEvent.class, event -> {
            GravelMinerClientSetting setting = GravelMinerConfig.getActive().getClientSetting();
            Balm.getNetworking().sendToServer(new SetClientSettingMessage(setting));
        });
    }
}
