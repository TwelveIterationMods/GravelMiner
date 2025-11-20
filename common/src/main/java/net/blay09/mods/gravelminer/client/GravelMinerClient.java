package net.blay09.mods.gravelminer.client;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.platform.event.callback.ConfigCallback;
import net.blay09.mods.gravelminer.GravelMinerClientSetting;
import net.blay09.mods.gravelminer.GravelMinerConfig;
import net.blay09.mods.gravelminer.network.SetClientSettingMessage;

public class GravelMinerClient {
    public static void initialize(BalmClientRegistrars registrars) {
        ModKeyBindings.initialize();

        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            GravelMinerClientSetting setting = GravelMinerConfig.getClientSetting();
            Balm.networking().sendToServer(new SetClientSettingMessage(setting));
        });

        ConfigCallback.Reloaded.EVENT.register(schema -> {
            GravelMinerClientSetting setting = GravelMinerConfig.getClientSetting();
            Balm.networking().sendToServer(new SetClientSettingMessage(setting));
        });
    }
}
