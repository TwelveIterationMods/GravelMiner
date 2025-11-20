package net.blay09.mods.gravelminer.client;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.gravelminer.GravelMinerConfig;
import net.blay09.mods.gravelminer.network.SetClientSettingMessage;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static net.blay09.mods.gravelminer.GravelMiner.id;

public class ModKeyBindings {

    public static void initialize() {
        Kuma.createKeyMapping(id("toggle"))
                .handleWorldInput(event -> {
                    boolean newEnabled = !GravelMinerConfig.getActive().client.isEnabled;
                    GravelMinerConfig.setEnabled(newEnabled);
                    Balm.networking().sendToServer(new SetClientSettingMessage(GravelMinerConfig.getClientSetting()));

                    final var client = Minecraft.getInstance();
                    if (client.player != null) {
                        final MutableComponent message = Component.translatable("gravelminer.toggle" + (newEnabled ? "On" : "Off"));
                        client.player.displayClientMessage(message, true);
                    }
                    return true;
                })
                .build();
    }

}
