package net.blay09.mods.gravelminer.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.keybinds.BalmKeyMappings;
import net.blay09.mods.balm.client.keybinds.KeyConflictContext;
import net.blay09.mods.balm.client.keybinds.KeyModifier;
import net.blay09.mods.balm.event.client.BalmClientEvents;
import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.gravelminer.GravelMinerConfig;
import net.blay09.mods.gravelminer.network.SetEnabledMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;

public class ModKeyBindings extends BalmKeyMappings {

    public static KeyMapping keyToggleGravelMiner;

    public static void initialize() {
        keyToggleGravelMiner = registerKeyMapping("key.gravelminer.toggle", KeyConflictContext.INGAME, KeyModifier.NONE, InputConstants.UNKNOWN.getValue(), "key.categories.gravelminer");

        BalmClientEvents.onClientTicked(ModKeyBindings::clientTicked);
    }

    public static void clientTicked(Minecraft client) {
        while (isActiveAndWasPressed(keyToggleGravelMiner)) {
            boolean newEnabled = !GravelMinerConfig.getActive().client.isEnabled;
            GravelMinerConfig.setEnabled(newEnabled);
            BalmNetworking.sendToServer(new SetEnabledMessage(newEnabled));

            if (client.player != null) {
                final TranslatableComponent message = new TranslatableComponent("gravelminer.toggle" + (newEnabled ? "On" : "Off"));
                client.player.displayClientMessage(message, true);
            }
        }
    }
}
