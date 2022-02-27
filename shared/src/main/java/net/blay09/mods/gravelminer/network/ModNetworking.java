package net.blay09.mods.gravelminer.network;

import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.resources.ResourceLocation;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerClientboundPacket(id("hello"), HelloMessage.class, HelloMessage::encode, HelloMessage::decode, HelloMessage::handle);
        networking.registerServerboundPacket(id("set_enabled"), SetClientSettingMessage.class, SetClientSettingMessage::encode, SetClientSettingMessage::decode, SetClientSettingMessage::handle);
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(GravelMiner.MOD_ID, name);
    }
}
