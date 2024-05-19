package net.blay09.mods.gravelminer.network;

import net.blay09.mods.gravelminer.GravelMiner;
import net.blay09.mods.gravelminer.GravelMinerClientSetting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SetClientSettingMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetClientSettingMessage> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(GravelMiner.MOD_ID,
            "set_client_setting"));

    private final GravelMinerClientSetting setting;

    public SetClientSettingMessage(GravelMinerClientSetting setting) {
        this.setting = setting;
    }

    public static void encode(FriendlyByteBuf buf, SetClientSettingMessage message) {
        buf.writeByte(message.setting.ordinal());
    }

    public static SetClientSettingMessage decode(FriendlyByteBuf buf) {
        GravelMinerClientSetting setting = GravelMinerClientSetting.values()[buf.readByte()];
        return new SetClientSettingMessage(setting);
    }

    public static void handle(ServerPlayer player, SetClientSettingMessage message) {
        if (player != null) {
            GravelMiner.setHasClientSide(player);
            GravelMiner.setClientSetting(player, message.setting);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
