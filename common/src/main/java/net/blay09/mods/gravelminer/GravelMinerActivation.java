package net.blay09.mods.gravelminer;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum GravelMinerActivation implements StringRepresentable {
    ALWAYS,
    WHEN_SNEAKING,
    WHEN_NOT_SNEAKING;

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
