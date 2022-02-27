package net.blay09.mods.gravelminer;

import net.minecraft.world.entity.player.Player;

public enum GravelMinerClientSetting {
    DISABLED,
    ENABLED,
    ONLY_WHEN_SNEAKING,
    ONLY_WHEN_NOT_SNEAKING;

    public boolean isEnabled(Player player) {
        return this == ENABLED
                || (this == ONLY_WHEN_SNEAKING && player.isShiftKeyDown())
                || (this == ONLY_WHEN_NOT_SNEAKING && !player.isShiftKeyDown());
    }
}
