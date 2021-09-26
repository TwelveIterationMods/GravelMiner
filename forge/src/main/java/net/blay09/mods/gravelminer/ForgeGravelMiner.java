package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.gravelminer.client.GravelMinerClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(GravelMiner.MOD_ID)
public class ForgeGravelMiner {
    public ForgeGravelMiner() {
        GravelMiner.initialize();

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> GravelMinerClient::initialize);

        Balm.initialize(GravelMiner.MOD_ID);
    }

}
