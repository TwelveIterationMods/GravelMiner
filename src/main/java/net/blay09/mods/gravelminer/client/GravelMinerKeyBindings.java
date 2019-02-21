package net.blay09.mods.gravelminer.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class GravelMinerKeyBindings {

    public static final KeyBinding keyToggle = new KeyBinding("key.gravelminer.toggle", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputMappings.INPUT_INVALID, "key.categories.gravelminer");

    public static void register() {
        ClientRegistry.registerKeyBinding(keyToggle);
    }

}
