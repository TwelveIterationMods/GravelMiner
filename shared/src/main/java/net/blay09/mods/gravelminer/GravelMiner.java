package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.BreakBlockEvent;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.gravelminer.network.HelloMessage;
import net.blay09.mods.gravelminer.network.ModNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class GravelMiner {

    public static final String MOD_ID = "gravelminer";

    private static final Set<UUID> hasClientSide = new HashSet<>();
    private static final Map<UUID, GravelMinerClientSetting> clientSettings = new HashMap<>();

    public static boolean isServerInstalled;

    public static void initialize() {
        GravelMinerConfig.initialize();
        ModNetworking.initialize(Balm.getNetworking());

        Balm.getEvents().onEvent(PlayerLoginEvent.class, event -> Balm.getNetworking().sendTo(event.getPlayer(), new HelloMessage()));

        Balm.getEvents().onEvent(BreakBlockEvent.class, BlockBreakHandler::blockBroken, EventPriority.Lowest);
    }

    public static boolean isAvailableFor(Player player) {
        return !GravelMinerConfig.getActive().common.isOptIn || hasClientSide.contains(player.getUUID());
    }

    public static boolean isEnabledFor(Player player) {
        return isAvailableFor(player) && clientSettings.getOrDefault(player.getUUID(), GravelMinerClientSetting.DISABLED).isEnabled(player);
    }

    public static void setHasClientSide(Player player) {
        hasClientSide.add(player.getUUID());
        setClientSetting(player, GravelMinerClientSetting.ENABLED);
    }

    public static void setClientSetting(Player player, GravelMinerClientSetting setting) {
        clientSettings.put(player.getUUID(), setting);
    }

    public static boolean isGravelBlock(BlockState state) {
        ResourceLocation registryName = state != null ? BuiltInRegistries.BLOCK.getKey(state.getBlock()) : null;
        return registryName != null && GravelMinerConfig.getActive().common.gravelBlocks.contains(registryName.toString());
    }

}
