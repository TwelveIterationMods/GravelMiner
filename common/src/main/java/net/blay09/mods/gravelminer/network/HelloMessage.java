package net.blay09.mods.gravelminer.network;

import net.blay09.mods.gravelminer.GravelMiner;
import net.blay09.mods.gravelminer.GravelMinerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class HelloMessage implements CustomPacketPayload {

    public static final Type<HelloMessage> TYPE = new Type<>(new ResourceLocation(GravelMiner.MOD_ID, "hello"));

    public static void encode(FriendlyByteBuf buf, HelloMessage message) {
    }

    public static HelloMessage decode(FriendlyByteBuf buf) {
        return new HelloMessage();
    }

    public static void handle(Player player, HelloMessage message) {
        GravelMiner.isServerInstalled = true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
