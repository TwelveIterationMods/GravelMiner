package net.blay09.mods.gravelminer.fabric;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.blay09.mods.gravelminer.GravelMiner;
import net.fabricmc.api.ModInitializer;

public class FabricGravelMiner implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initializeMod(GravelMiner.MOD_ID, FabricLoadContext.INSTANCE, GravelMiner::initialize);
    }
}
