package net.blay09.mods.gravelminer.network;

import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SetEnabledMessage {

    private final boolean enabled;

    public SetEnabledMessage(boolean enabled) {
        this.enabled = enabled;
    }

    public static void encode(SetEnabledMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.enabled);
    }

    public static SetEnabledMessage decode(FriendlyByteBuf buf) {
        boolean enabled = buf.readBoolean();
        return new SetEnabledMessage(enabled);
    }

    public static void handle(ServerPlayer player, SetEnabledMessage message) {
        if (player != null) {
            GravelMiner.setHasClientSide(player);
            GravelMiner.setHasEnabled(player, message.enabled);
        }
    }
}
