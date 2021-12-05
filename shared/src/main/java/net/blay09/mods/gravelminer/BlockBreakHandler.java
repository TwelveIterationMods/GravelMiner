package net.blay09.mods.gravelminer;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.BreakBlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockBreakHandler {

    public static void blockBroken(BreakBlockEvent event) {
        Player player = event.getPlayer();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        // Do not handle this event for fake players and players who do not have GravelMiner enabled.
        if (Balm.getHooks().isFakePlayer(player) || !GravelMiner.isEnabledFor(player)) {
            return;
        }

        // Do not handle this event for gravel blocks themselves, unless it's been enabled.
        if (!GravelMinerConfig.getActive().common.triggerOnGravel && GravelMiner.isGravelBlock(state)) {
            return;
        }

        // Iterate through blocks upwards as long as gravel is found
        final int maxCount = 256;
        final int startY = pos.getY() + 1;
        for (int y = startY; y <= startY + maxCount; y++) {
            // Retrieve the block above the current position
            BlockPos posAbove = new BlockPos(pos.getX(), y, pos.getZ());
            BlockState stateAbove = level.getBlockState(posAbove);

            // If the block at this position is not gravel, abort here
            if (!GravelMiner.isGravelBlock(stateAbove)) {
                return;
            }

            playBreakBlockEffects(level, posAbove, stateAbove);

            if (!breakBlock(player, level, posAbove, stateAbove)) {
                return;
            }
        }
    }

    private static void playBreakBlockEffects(Level level, BlockPos pos, BlockState state) {
        final int blockBreakEvent = 2001;
        level.levelEvent(null, blockBreakEvent, pos, Block.getId(state));
    }

    private static boolean breakBlock(Player player, Level level, BlockPos pos, BlockState state) {
        FluidState fluidState = level.getFluidState(pos);
        state.getBlock().playerWillDestroy(level, pos, state, player);
        boolean removedByPlayer = level.setBlock(pos, fluidState.createLegacyBlock(), level.isClientSide ? 11 : 3);
        if (!removedByPlayer) {
            return false;
        }

        if (!player.getAbilities().instabuild) {
            state.getBlock().destroy(level, pos, state);
            if (GravelMinerConfig.getActive().common.rollFlintChance || state.getBlock() != Blocks.GRAVEL) {
                state.getBlock().playerDestroy(level, player, pos, state, level.getBlockEntity(pos), ItemStack.EMPTY);
            } else {
                Block.popResource(level, pos, new ItemStack(Blocks.GRAVEL, 1));
            }
        }

        return true;
    }

}
