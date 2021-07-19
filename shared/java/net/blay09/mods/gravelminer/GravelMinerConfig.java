package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.config.BalmConfigHolder;

public class GravelMinerConfig {
    public static GravelMinerConfigData getActive() {
        return BalmConfigHolder.getActive(GravelMinerConfigData.class);
    }

    public static void initialize() {
        BalmConfigHolder.registerConfig(GravelMinerConfigData.class, null);
    }

    public static void setEnabled(boolean enabled) {
        BalmConfigHolder.updateConfig(GravelMinerConfigData.class, config -> {
            config.client.isEnabled = enabled;
        });
    }
}
