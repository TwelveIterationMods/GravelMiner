package net.blay09.mods.gravelminer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void init(FMLInitializationEvent event) {}

	public void addScheduledTask(Runnable runnable) {
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (GravelMiner.TEST_CLIENT_SIDE) {
			return;
		}
		if (event.getPlayer() instanceof FakePlayer || !GravelMiner.isEnabledFor(event.getPlayer())) {
			return;
		}
		if (GravelMiner.isGravelBlock(event.getState())) {
			return;
		}
		final int maxCount = 256;
		for (int y = event.getPos().getY() + 1; y < 256; y++) {
			if (y > event.getPos().getY() + maxCount) {
				return;
			}
			BlockPos posAbove = new BlockPos(event.getPos().getX(), y, event.getPos().getZ());
			IBlockState stateAbove = event.getWorld().getBlockState(posAbove);
			if (!GravelMiner.isGravelBlock(stateAbove)) {
				return;
			}
			BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(event.getWorld(), posAbove, stateAbove, event.getPlayer());
			MinecraftForge.EVENT_BUS.post(breakEvent);
			if (breakEvent.isCanceled()) {
				return;
			}
			event.getWorld().playEvent(event.getPlayer(), 2001, posAbove, Block.getStateId(stateAbove));
			SPacketEffect packet = new SPacketEffect(2001, posAbove, Block.getStateId(stateAbove), false);
			final int range = 20;
			for (Entity entity : event.getWorld().getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(event.getPos().getX() - range, y - range, event.getPos().getZ() - range, event.getPos().getX() + range, y + range, event.getPos().getZ() + range))) {
				((EntityPlayerMP) entity).connection.sendPacket(packet);
			}
			stateAbove.getBlock().onBlockHarvested(event.getWorld(), posAbove, stateAbove, event.getPlayer());
			boolean removedByPlayer = stateAbove.getBlock().removedByPlayer(stateAbove, event.getWorld(), posAbove, event.getPlayer(), true);
			if (!removedByPlayer) {
				return;
			}
			if (!event.getPlayer().capabilities.isCreativeMode) {
				stateAbove.getBlock().onBlockDestroyedByPlayer(event.getWorld(), posAbove, stateAbove);
				if(GravelMiner.isRollFlintChance() || event.getState().getBlock() != Blocks.GRAVEL) {
					stateAbove.getBlock().harvestBlock(event.getWorld(), event.getPlayer(), posAbove, stateAbove, event.getWorld().getTileEntity(posAbove), null);
				} else {
					Block.spawnAsEntity(event.getWorld(), event.getPos(), new ItemStack(Blocks.GRAVEL, 1));
				}
			}
		}
	}

}
