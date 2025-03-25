package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.reflection.Comment;
import net.blay09.mods.balm.api.config.reflection.Config;
import net.blay09.mods.balm.api.config.reflection.NestedType;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

@Config(GravelMiner.MOD_ID)
public class GravelMinerConfig {

    public Common common = new Common();
    public Client client = new Client();

    public static GravelMinerConfig getActive() {
        return Balm.getConfig().getActiveConfig(GravelMinerConfig.class);
    }

    public static void initialize() {
        Balm.getConfig().registerConfig(GravelMinerConfig.class);
    }

    public static void setEnabled(boolean enabled) {
        Balm.getConfig().updateLocalConfig(GravelMinerConfig.class, config -> {
            config.client.isEnabled = enabled;
        });
    }

    public static GravelMinerClientSetting getClientSetting() {
        GravelMinerConfig config = getActive();
        if (config.client.isEnabled && config.client.activation == GravelMinerActivation.ALWAYS) {
            return GravelMinerClientSetting.ENABLED;
        } else if (config.client.isEnabled && config.client.activation == GravelMinerActivation.WHEN_SNEAKING) {
            return GravelMinerClientSetting.ONLY_WHEN_SNEAKING;
        } else if (config.client.isEnabled && config.client.activation == GravelMinerActivation.WHEN_NOT_SNEAKING) {
            return GravelMinerClientSetting.ONLY_WHEN_NOT_SNEAKING;
        }
        return GravelMinerClientSetting.DISABLED;
    }

    public static class Common {
        @NestedType(ResourceLocation.class)
        @Comment("Blocks that will fall and break into items when hitting a non-solid block. Format: modid:name")
        public Set<ResourceLocation> gravelBlocks = Set.of(ResourceLocation.withDefaultNamespace("gravel"));

        @Comment("If set to true, the mod will trigger when mining gravel as well, instead of only when mining a non-gravel block below gravel.")
        public boolean triggerOnGravel = true;

        @Comment("If set to true, GravelMiner will only be active for users who install the mod on their clients (for use in servers).")
        public boolean isOptIn = true;

        @Comment("If set to true, gravel mined via GravelMiner will have a chance of dropping as flint (server-side only).")
        public boolean rollFlintChance = true;
    }

    public static class Client {
        @Comment("Whether GravelMiner is currently enabled for this client (toggled via keybind)")
        public boolean isEnabled = true;

        @Comment("Defines when GravelMiner should activate. Either ALWAYS, WHEN_SNEAKING or WHEN_NOT_SNEAKING")
        public GravelMinerActivation activation = GravelMinerActivation.ALWAYS;
    }
}
