package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.forge.platform.runtime.ForgeLoadContext;
import net.blay09.mods.gravelminer.client.GravelMinerClient;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(GravelMiner.MOD_ID)
public class ForgeGravelMiner {
    public ForgeGravelMiner(FMLJavaModLoadingContext context) {
        final var loadContext = new ForgeLoadContext(context.getModBusGroup());
        Balm.initializeMod(GravelMiner.MOD_ID, loadContext, GravelMiner::initialize);
        if (FMLEnvironment.dist.isClient()) {
            BalmClient.initializeMod(GravelMiner.MOD_ID, loadContext, GravelMinerClient::initialize);
        }
    }

}
