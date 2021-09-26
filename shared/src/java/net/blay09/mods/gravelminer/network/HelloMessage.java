package net.blay09.mods.gravelminer.network;

import net.blay09.mods.gravelminer.GravelMiner;
import net.blay09.mods.gravelminer.GravelMinerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class HelloMessage {

    private int dummy;

    public static void encode(HelloMessage message, FriendlyByteBuf buf) {
    }

    public static HelloMessage decode(FriendlyByteBuf buf) {
        return new HelloMessage();
    }

    public static void handle(Player player, HelloMessage message) {
        GravelMiner.isServerInstalled = true;
    }
}
