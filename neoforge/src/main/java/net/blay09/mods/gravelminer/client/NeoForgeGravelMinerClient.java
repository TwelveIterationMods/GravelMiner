package net.blay09.mods.gravelminer.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.blay09.mods.gravelminer.GravelMiner;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = GravelMiner.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeGravelMinerClient {
    public NeoForgeGravelMinerClient(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        BalmClient.initialize(GravelMiner.MOD_ID, context, GravelMinerClient::initialize);
    }

}
