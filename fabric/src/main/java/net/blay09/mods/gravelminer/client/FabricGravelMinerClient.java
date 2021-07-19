package net.blay09.mods.gravelminer.client;

import net.fabricmc.api.ClientModInitializer;

public class FabricGravelMinerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GravelMinerClient.initialize();
    }
}
