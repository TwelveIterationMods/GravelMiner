package net.blay09.mods.gravelminer;

import net.blay09.mods.gravelminer.net.MessageHello;
import net.blay09.mods.gravelminer.net.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addScheduledTask(Runnable runnable) {
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
	}

	public void receivedHello(EntityPlayer entityPlayer) {
		GravelMiner.enableFor(entityPlayer);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		NetworkHandler.instance.sendTo(new MessageHello(), (EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (GravelMiner.TEST_CLIENT_SIDE) {
			return;
		}
		if (event.getPlayer() instanceof FakePlayer || !GravelMiner.isEnabledFor(event.getPlayer())) {
			return;
		}
		if (GravelMiner.isGravelBlock(event.state)) {
			return;
		}
		final int maxCount = 256;
		for (int y = event.pos.getY() + 1; y < 256; y++) {
			if (y > event.pos.getY() + maxCount) {
				return;
			}
			BlockPos posAbove = new BlockPos(event.pos.getX(), y, event.pos.getZ());
			IBlockState stateAbove = event.world.getBlockState(posAbove);
			if (!GravelMiner.isGravelBlock(stateAbove)) {
				return;
			}
			BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(event.world, posAbove, stateAbove, event.getPlayer());
			MinecraftForge.EVENT_BUS.post(breakEvent);
			if (breakEvent.isCanceled()) {
				return;
			}
			event.world.playAuxSFXAtEntity(event.getPlayer(), 2001, posAbove, Block.getStateId(stateAbove));
			S28PacketEffect packet = new S28PacketEffect(2001, posAbove, Block.getStateId(stateAbove), false);
			final int range = 20;
			for (Object obj : event.world.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.fromBounds(event.pos.getX() - range, y - range, event.pos.getZ() - range, event.pos.getX() + range, y + range, event.pos.getZ() + range))) {
				((EntityPlayerMP) obj).playerNetServerHandler.sendPacket(packet);
			}
			stateAbove.getBlock().onBlockHarvested(event.world, posAbove, stateAbove, event.getPlayer());
			boolean removedByPlayer = stateAbove.getBlock().removedByPlayer(event.world, posAbove, event.getPlayer(), true);
			if (!removedByPlayer) {
				return;
			}
			if (!event.getPlayer().capabilities.isCreativeMode) {
				stateAbove.getBlock().onBlockDestroyedByPlayer(event.world, posAbove, stateAbove);
				stateAbove.getBlock().harvestBlock(event.world, event.getPlayer(), posAbove, stateAbove, event.world.getTileEntity(posAbove));
			}
		}
	}

}
