package net.blay09.mods.gravelminer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockBreakHandler {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        // Do not handle this event server-side if the test flag for client-side handling is enabled.
        if (GravelMiner.TEST_CLIENT_SIDE) {
            return;
        }

        // Do not handle this event for fake players and players who do not have GravelMiner enabled.
        if (event.getPlayer() instanceof FakePlayer || !GravelMiner.isEnabledFor(event.getPlayer())) {
            return;
        }

        // Do not handle this event for gravel blocks themselves, unless it's been enabled.
        if (!GravelMinerConfig.COMMON.triggerOnGravel.get() && GravelMiner.isGravelBlock(event.getState())) {
            return;
        }

        // Iterate through blocks upwards as long as gravel is found
        final int maxCount = 256;
        final int startY = event.getPos().getY() + 1;
        for (int y = startY; y <= startY + maxCount; y++) {
            // Retrieve the block above the current position
            BlockPos posAbove = new BlockPos(event.getPos().getX(), y, event.getPos().getZ());
            BlockState stateAbove = event.getWorld().getBlockState(posAbove);

            // If the block at this position is not gravel, abort here
            if (!GravelMiner.isGravelBlock(stateAbove)) {
                return;
            }

            World world = (World) event.getWorld();

            // Fire block break event to see if we're allowed to break this block.
            BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, posAbove, stateAbove, event.getPlayer());
            MinecraftForge.EVENT_BUS.post(breakEvent);
            if (breakEvent.isCanceled()) {
                return;
            }

            playBreakBlockEffects(event.getPlayer(), world, posAbove, stateAbove);
            if (!breakBlock(event.getPlayer(), world, posAbove, stateAbove)) {
                return;
            }
        }
    }

    private void playBreakBlockEffects(PlayerEntity player, World world, BlockPos pos, BlockState state) {
        final int blockBreakEvent = 2001;
        world.playEvent(null, blockBreakEvent, pos, Block.getStateId(state));
    }

    private boolean breakBlock(PlayerEntity player, World world, BlockPos pos, BlockState state) {
        FluidState fluidState = world.getFluidState(pos);
        boolean removedByPlayer = state.getBlock().removedByPlayer(state, world, pos, player, true, fluidState);
        if (!removedByPlayer) {
            return false;
        }

        if (!player.abilities.isCreativeMode) {
            state.getBlock().onPlayerDestroy(world, pos, state);
            if (GravelMinerConfig.SERVER.rollFlintChance.get() || state.getBlock() != Blocks.GRAVEL) {
                state.getBlock().harvestBlock(world, player, pos, state, world.getTileEntity(pos), ItemStack.EMPTY);
            } else {
                Block.spawnAsEntity(world, pos, new ItemStack(Blocks.GRAVEL, 1));
            }
        }

        return true;
    }

}
