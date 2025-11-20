package net.blay09.mods.gravelminer.network;

import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.gravelminer.GravelMiner;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.allowServerOnly(GravelMiner.MOD_ID);

        networking.registerClientboundPacket(HelloMessage.TYPE, HelloMessage.class, HelloMessage.STREAM_CODEC, HelloMessage::handle);
        networking.registerServerboundPacket(SetClientSettingMessage.TYPE, SetClientSettingMessage.class, SetClientSettingMessage.STREAM_CODEC, SetClientSettingMessage::handle);
    }

}
