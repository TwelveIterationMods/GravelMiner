package net.blay09.mods.gravelminer;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.config.BalmConfigData;
import net.blay09.mods.balm.api.config.Comment;
import net.blay09.mods.balm.api.config.Config;
import net.blay09.mods.balm.api.config.ExpectedType;

import java.util.List;

@Config(GravelMiner.MOD_ID)
public class GravelMinerConfigData implements BalmConfigData {

    public Common common = new Common();
    public Client client = new Client();

    public static class Common {
        @ExpectedType(String.class)
        @Comment("Blocks that will fall and break into items when hitting a non-solid block. Format: modid:name")
        public List<String> gravelBlocks = Lists.newArrayList("minecraft:gravel");

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
