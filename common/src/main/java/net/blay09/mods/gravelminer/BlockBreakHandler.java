package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.EventHandling;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class BlockBreakHandler {

    public static EventHandling blockBroken(LevelAccessor level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Player player) {
        // Do not handle this event for fake players and players who do not have GravelMiner enabled.
        if (Balm.hooks().isFakePlayer(player) || !GravelMiner.isEnabledFor(player)) {
            return EventHandling.RESUME;
        }

        // Do not handle this event for gravel blocks themselves, unless it's been enabled.
        if (!GravelMinerConfig.getActive().common.triggerOnGravel && GravelMiner.isGravelBlock(state)) {
            return EventHandling.RESUME;
        }

        // Iterate through blocks upwards as long as gravel is found
        final int maxCount = 256;
        final int startY = pos.getY() + 1;
        final var tool = player.getMainHandItem();
        for (int y = startY; y <= startY + maxCount; y++) {
            // Retrieve the block above the current position
            BlockPos posAbove = new BlockPos(pos.getX(), y, pos.getZ());
            BlockState stateAbove = level.getBlockState(posAbove);

            // If the block at this position is not gravel, abort here
            if (!GravelMiner.isGravelBlock(stateAbove)) {
                return EventHandling.RESUME;
            }

            playBreakBlockEffects(level, posAbove, stateAbove);

            if (!breakBlock(player, level, posAbove, stateAbove, tool)) {
                return EventHandling.RESUME;
            }
        }

        return EventHandling.RESUME;
    }

    private static void playBreakBlockEffects(LevelAccessor level, BlockPos pos, BlockState state) {
        final int blockBreakEvent = 2001;
        level.levelEvent(null, blockBreakEvent, pos, Block.getId(state));
    }

    private static boolean breakBlock(Player player, LevelAccessor levelAccessor, BlockPos pos, BlockState state, ItemStack tool) {
        FluidState fluidState = levelAccessor.getFluidState(pos);
        if (levelAccessor instanceof Level level) {
            state.getBlock().playerWillDestroy(level, pos, state, player);
        }
        boolean removedByPlayer = levelAccessor.setBlock(pos, fluidState.createLegacyBlock(), levelAccessor.isClientSide() ? 11 : 3);
        if (!removedByPlayer) {
            return false;
        }

        if (!player.getAbilities().instabuild) {
            state.getBlock().destroy(levelAccessor, pos, state);
            if (levelAccessor instanceof Level level) {
                state.getBlock().playerDestroy(level, player, pos, state, levelAccessor.getBlockEntity(pos), tool);
            }
        }

        return true;
    }

}
