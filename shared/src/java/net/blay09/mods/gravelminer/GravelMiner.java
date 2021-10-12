package net.blay09.mods.gravelminer;

import com.google.common.collect.Sets;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.BreakBlockEvent;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.gravelminer.network.HelloMessage;
import net.blay09.mods.gravelminer.network.ModNetworking;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.UUID;

public class GravelMiner {

    public static final String MOD_ID = "gravelminer";

    private static final Set<UUID> hasClientSide = Sets.newHashSet();
    private static final Set<UUID> hasEnabled = Sets.newHashSet();

    public static boolean isServerInstalled;

    public static void initialize() {
        GravelMinerConfig.initialize();
        ModNetworking.initialize(Balm.getNetworking());

        Balm.getEvents().onEvent(PlayerLoginEvent.class, event -> Balm.getNetworking().sendTo(event.getPlayer(), new HelloMessage()));

        Balm.getEvents().onEvent(BreakBlockEvent.Post.class, BlockBreakHandler::blockBroken);

        Balm.initialize(GravelMiner.MOD_ID);
    }

    public static boolean isAvailableFor(Player player) {
        return !GravelMinerConfig.getActive().common.isOptIn || hasClientSide.contains(player.getUUID());
    }

    public static boolean isEnabledFor(Player player) {
        return isAvailableFor(player) && hasEnabled.contains(player.getUUID());
    }

    public static void setHasClientSide(Player player) {
        hasClientSide.add(player.getUUID());
        setHasEnabled(player, true);
    }

    public static void setHasEnabled(Player player, boolean enabled) {
        if (enabled) {
            hasEnabled.add(player.getUUID());
        } else {
            hasEnabled.remove(player.getUUID());
        }
    }

    public static boolean isGravelBlock(BlockState state) {
        ResourceLocation registryName = state != null ? Registry.BLOCK.getKey(state.getBlock()) : null;
        return registryName != null && GravelMinerConfig.getActive().common.gravelBlocks.contains(registryName.toString());
    }

}
