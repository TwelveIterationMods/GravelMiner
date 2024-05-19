package net.blay09.mods.gravelminer.fabric;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.gravelminer.GravelMiner;
import net.fabricmc.api.ModInitializer;

public class FabricGravelMiner implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initialize(GravelMiner.MOD_ID, EmptyLoadContext.INSTANCE, GravelMiner::initialize);
    }
}
