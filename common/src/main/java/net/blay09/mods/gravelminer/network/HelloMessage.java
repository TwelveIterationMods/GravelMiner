package net.blay09.mods.gravelminer.network;

import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class HelloMessage implements CustomPacketPayload {

    public static final HelloMessage INSTANCE = new HelloMessage();
    public static final Type<HelloMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(GravelMiner.MOD_ID, "hello"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HelloMessage> STREAM_CODEC = StreamCodec.unit(HelloMessage.INSTANCE);

    private HelloMessage() {
    }

    public static void handle(Player player, HelloMessage message) {
        GravelMiner.isServerInstalled = true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
