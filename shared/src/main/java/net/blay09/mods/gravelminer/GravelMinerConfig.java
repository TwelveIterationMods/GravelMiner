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

    public static GravelMinerClientSetting getClientSetting() {
        GravelMinerConfigData config = getActive();
        if (config.client.isEnabled && config.client.activation == GravelMinerActivation.ALWAYS) {
            return GravelMinerClientSetting.ENABLED;
        } else if (config.client.isEnabled && config.client.activation == GravelMinerActivation.WHEN_SNEAKING) {
            return GravelMinerClientSetting.ONLY_WHEN_SNEAKING;
        } else if (config.client.isEnabled && config.client.activation == GravelMinerActivation.WHEN_NOT_SNEAKING) {
            return GravelMinerClientSetting.ONLY_WHEN_NOT_SNEAKING;
        }
        return GravelMinerClientSetting.DISABLED;
    }
}
