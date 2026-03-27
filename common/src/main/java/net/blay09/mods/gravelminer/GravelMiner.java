package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.blay09.mods.balm.platform.event.EventPhases;
import net.blay09.mods.balm.platform.event.callback.BlockCallback;
import net.blay09.mods.balm.platform.event.callback.ServerPlayerCallback;
import net.blay09.mods.gravelminer.network.HelloMessage;
import net.blay09.mods.gravelminer.network.ModNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class GravelMiner {

    public static final String MOD_ID = "gravelminer";

    private static final Set<UUID> hasClientSide = new HashSet<>();
    private static final Map<UUID, GravelMinerClientSetting> clientSettings = new HashMap<>();

    public static boolean isServerInstalled;

    public static void initialize(BalmRegistrars registrars) {
        GravelMinerConfig.initialize();
        ModNetworking.initialize(Balm.networking());

        ServerPlayerCallback.Join.EVENT.register(player -> Balm.networking().sendTo(player, HelloMessage.INSTANCE));
        BlockCallback.Break.Before.EVENT.register(EventPhases.LOWEST, BlockBreakHandler::blockBroken);
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

    public static boolean isGravelBlock(@Nullable BlockState state) {
        Identifier registryName = state != null ? BuiltInRegistries.BLOCK.getKey(state.getBlock()) : null;
        return registryName != null && GravelMinerConfig.getActive().common.gravelBlocks.contains(registryName);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
