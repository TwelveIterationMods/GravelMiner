package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.api.Balm;
import net.fabricmc.api.ModInitializer;

public class FabricGravelMiner implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initialize(GravelMiner.MOD_ID, GravelMiner::initialize);
    }
}
