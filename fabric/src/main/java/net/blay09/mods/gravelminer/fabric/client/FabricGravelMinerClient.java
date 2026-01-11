package net.blay09.mods.gravelminer.fabric.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.blay09.mods.gravelminer.GravelMiner;
import net.blay09.mods.gravelminer.client.GravelMinerClient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class FabricGravelMinerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initializeMod(GravelMiner.MOD_ID, FabricLoadContext.INSTANCE, GravelMinerClient::initialize);

        KeyBindingHelper.registerKeyBinding(new KeyMapping("", 0, null));
    }
}
