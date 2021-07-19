package net.blay09.mods.gravelminer.network;

import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.resources.ResourceLocation;

public class ModNetworking extends BalmNetworking {

    public static void initialize() {
        registerClientboundPacket(id("hello"), HelloMessage.class, HelloMessage::encode, HelloMessage::decode, HelloMessage::handle);
        registerServerboundPacket(id("set_enabled"), SetEnabledMessage.class, SetEnabledMessage::encode, SetEnabledMessage::decode, SetEnabledMessage::handle);
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(GravelMiner.MOD_ID, name);
    }
}
