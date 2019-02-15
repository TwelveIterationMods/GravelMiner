package net.blay09.mods.gravelminer.net;

import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageHello {
    public static void handle(MessageHello message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            EntityPlayer player = context.getSender();
            if (player != null) {
                GravelMiner.setHasClientSide(player);
            }
        });
    }
}
