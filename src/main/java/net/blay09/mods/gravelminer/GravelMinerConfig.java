package net.blay09.mods.gravelminer;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Mod.EventBusSubscriber(modid = GravelMiner.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GravelMinerConfig {

    public static class Common {
        public final ForgeConfigSpec.ConfigValue<List<String>> gravelBlocks;
        public final ForgeConfigSpec.BooleanValue triggerOnGravel;
        public final ForgeConfigSpec.ConfigValue<List<String>> torchItems;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Common configuration settings").push("common");

            gravelBlocks = builder
                    .comment("Blocks that will fall and break into items when hitting a non-solid block. Format: modid:name")
                    .translation("gravelminer.config.gravelBlocks")
                    .define("gravelBlocks", Lists.newArrayList("minecraft:gravel"));

            triggerOnGravel = builder
                    .comment("If set to true, the mod will trigger when mining gravel as well, instead of only when mining a non-gravel block below gravel.")
                    .translation("gravelminer.config.triggerOnGravel")
                    .define("triggerOnGravel", true);

            torchItems = builder
                    .comment("Blocks that are non-solid and can be destroyed in a single hit. Format: modid:name (for use on clients)")
                    .translation("gravelminer.config.torchItems")
                    .define("torchItems", Lists.newArrayList("minecraft:torch"));
        }
    }

    public static class Server {
        public final ForgeConfigSpec.BooleanValue isOptIn;
        public final ForgeConfigSpec.BooleanValue rollFlintChance;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings").push("server");

            isOptIn = builder
                    .comment("If set to true, GravelMiner will only be active for users who install the mod on their clients (for use in servers).")
                    .translation("gravelminer.config.isOptIn")
                    .define("isOptIn", true);

            rollFlintChance = builder
                    .comment("If set to true, gravel mined via GravelMiner will have a chance of dropping as flint (server-side only).")
                    .translation("gravelminer.config.rollFlintChance")
                    .define("rollFlintChance", true);
        }
    }

    public static class Client {
        public final ForgeConfigSpec.BooleanValue isEnabled;
        public final ForgeConfigSpec.IntValue torchDelay;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings").push("client");

            isEnabled = builder
                    .comment("Whether GravelMiner is currently enabled for this client (toggled via keybind)")
                    .translation("gravelminer.config.isEnabled")
                    .define("isEnabled", true);

            torchDelay = builder
                    .comment("The delay in client ticks before the torch should be placed after breaking a block below gravel. Increase if torch is placed too early, decrease if torch is placed too late. (for use on clients)")
                    .translation("gravelminer.config.torchDelay")
                    .defineInRange("torchDelay", 8, 2, 20);
        }
    }

    static final ForgeConfigSpec commonSpec;
    public static final GravelMinerConfig.Common COMMON;

    static {
        final Pair<GravelMinerConfig.Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(GravelMinerConfig.Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    static final ForgeConfigSpec clientSpec;
    public static final GravelMinerConfig.Client CLIENT;

    static {
        final Pair<GravelMinerConfig.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(GravelMinerConfig.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    static final ForgeConfigSpec serverSpec;
    public static final GravelMinerConfig.Server SERVER;

    static {
        final Pair<GravelMinerConfig.Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(GravelMinerConfig.Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    private static ModConfig clientConfig;

    @SubscribeEvent
    public static void onConfigLoading(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
            clientConfig = event.getConfig();
        }
    }

    public static void increaseTorchDelay(int value) {
        int newTorchDelay = Math.max(1, Math.min(20, GravelMinerConfig.CLIENT.torchDelay.get() + value));
        clientConfig.getConfigData().set(GravelMinerConfig.CLIENT.torchDelay.getPath(), newTorchDelay);
        clientConfig.save();
    }

    public static void setEnabled(boolean enabled) {
        clientConfig.getConfigData().set(GravelMinerConfig.CLIENT.isEnabled.getPath(), enabled);
        clientConfig.save();
    }
}
