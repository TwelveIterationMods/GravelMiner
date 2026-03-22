package net.blay09.mods.gravelminer.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.blay09.mods.gravelminer.GravelMiner;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = GravelMiner.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeGravelMinerClient {
    public NeoForgeGravelMinerClient(ModContainer modContainer, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modContainer, modEventBus);
        BalmClient.initializeMod(GravelMiner.MOD_ID, context, GravelMinerClient::initialize);
    }

}
