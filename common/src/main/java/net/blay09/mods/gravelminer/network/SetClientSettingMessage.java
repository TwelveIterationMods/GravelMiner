package net.blay09.mods.gravelminer.network;

import net.blay09.mods.gravelminer.GravelMiner;
import net.blay09.mods.gravelminer.GravelMinerClientSetting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record SetClientSettingMessage(GravelMinerClientSetting setting) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetClientSettingMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            GravelMiner.MOD_ID,
            "set_client_setting"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetClientSettingMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(it -> GravelMinerClientSetting.values()[it], GravelMinerClientSetting::ordinal),
            SetClientSettingMessage::setting,
            SetClientSettingMessage::new
    );

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
