package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(GravelMiner.MOD_ID)
public class NeoForgeGravelMiner {
    public NeoForgeGravelMiner(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        Balm.initializeMod(GravelMiner.MOD_ID, context, GravelMiner::initialize);
    }

}
