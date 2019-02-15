package net.blay09.mods.gravelminer.client;

import net.blay09.mods.gravelminer.GravelMinerConfig;
import net.minecraft.util.math.BlockPos;

public class GravelKiller {
    public final BlockPos torchPos;
    public int placeTorchDelayTicks;
    public int gravelAboveTimeout;

    public GravelKiller(BlockPos torchPos) {
        this.torchPos = new BlockPos(torchPos);
        placeTorchDelayTicks = GravelMinerConfig.CLIENT.torchDelay.get();
        gravelAboveTimeout = 20;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GravelKiller that = (GravelKiller) o;
        return torchPos.equals(that.torchPos);
    }

    @Override
    public int hashCode() {
        return torchPos.hashCode();
    }
}
