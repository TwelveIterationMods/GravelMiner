package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.blay09.mods.gravelminer.client.GravelMinerClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;

@Mod(GravelMiner.MOD_ID)
public class NeoForgeGravelMiner {
    public NeoForgeGravelMiner(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        Balm.initialize(GravelMiner.MOD_ID, context, GravelMiner::initialize);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initialize(GravelMiner.MOD_ID, context, GravelMinerClient::initialize));
    }

}
