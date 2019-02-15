package net.blay09.mods.gravelminer.net;

import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSetEnabled {

    private final boolean enabled;

    public MessageSetEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static void encode(MessageSetEnabled message, PacketBuffer buf) {
        buf.writeBoolean(message.enabled);
    }

    public static MessageSetEnabled decode(PacketBuffer buf) {
        boolean enabled = buf.readBoolean();
        return new MessageSetEnabled(enabled);
    }

    public static void handle(MessageSetEnabled message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            EntityPlayer player = context.getSender();
            if (player != null) {
                GravelMiner.setHasEnabled(player, message.enabled);
            }
        });
    }
}
