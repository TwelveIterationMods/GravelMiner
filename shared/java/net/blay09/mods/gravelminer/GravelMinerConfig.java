package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.api.Balm;

public class GravelMinerConfig {
    public static GravelMinerConfigData getActive() {
        return Balm.getConfig().getActive(GravelMinerConfigData.class);
    }

    public static void initialize() {
        Balm.getConfig().registerConfig(GravelMinerConfigData.class, null);
    }

    public static void setEnabled(boolean enabled) {
        Balm.getConfig().updateConfig(GravelMinerConfigData.class, config -> {
            config.client.isEnabled = enabled;
        });
    }
}
