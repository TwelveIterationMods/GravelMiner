package net.blay09.mods.gravelminer.net;

import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageHello {
    public static void handle(MessageHello message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            PlayerEntity player = context.getSender();
            if (player != null) {
                GravelMiner.setHasClientSide(player);
            }
        });
    }
}
