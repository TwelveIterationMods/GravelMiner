package net.blay09.mods.gravelminer;

import net.fabricmc.api.ModInitializer;

public class FabricGravelMiner implements ModInitializer {
    @Override
    public void onInitialize() {
        GravelMiner.initialize();
    }
}
