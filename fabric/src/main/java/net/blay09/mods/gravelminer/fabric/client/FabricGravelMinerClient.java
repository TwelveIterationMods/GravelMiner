package net.blay09.mods.gravelminer.fabric.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.gravelminer.GravelMiner;
import net.blay09.mods.gravelminer.client.GravelMinerClient;
import net.fabricmc.api.ClientModInitializer;

public class FabricGravelMinerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initialize(GravelMiner.MOD_ID, GravelMinerClient::initialize);
    }
}
