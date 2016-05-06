package net.blay09.mods.gravelminer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.blay09.mods.gravelminer.net.MessageHello;
import net.blay09.mods.gravelminer.net.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	public void addScheduledTask(Runnable runnable) {
		runnable.run();
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
		if(GravelMiner.TEST_CLIENT_SIDE) {
			return;
		}
		if (event.getPlayer() instanceof FakePlayer || !GravelMiner.isEnabledFor(event.getPlayer())) {
			return;
		}
		if (GravelMiner.isGravelBlock(event.block, event.blockMetadata)) {
			return;
		}
		final int maxCount = 256;
		for (int y = event.y + 1; y < 256; y++) {
			if(y > event.y + maxCount) {
				return;
			}
			Block blockAbove = event.world.getBlock(event.x, y, event.z);
			int metadataAbove = event.world.getBlockMetadata(event.x, y, event.z);
			if (!GravelMiner.isGravelBlock(blockAbove, metadataAbove)) {
				return;
			}
			BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(event.x, y, event.z, event.world, blockAbove, metadataAbove, event.getPlayer());
			MinecraftForge.EVENT_BUS.post(breakEvent);
			if(breakEvent.isCanceled()) {
				return;
			}
			event.world.playAuxSFXAtEntity(event.getPlayer(), 2001, event.x, y, event.z, Block.getIdFromBlock(blockAbove) + (metadataAbove << 12));
			S28PacketEffect packet = new S28PacketEffect(2001, event.x, y, event.z, Block.getIdFromBlock(blockAbove) + (metadataAbove << 12), false);
			final int range = 20;
			for(Object obj : event.world.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox(event.x - range, y - range, event.z - range, event.x + range, y + range, event.z + range))) {
				((EntityPlayerMP) obj).playerNetServerHandler.sendPacket(packet);
			}
			blockAbove.onBlockHarvested(event.world, event.x, y, event.z, metadataAbove, event.getPlayer());
			boolean removedByPlayer = blockAbove.removedByPlayer(event.world, event.getPlayer(), event.x, y, event.z, true);
			if (!removedByPlayer) {
				return;
			}
			if(!event.getPlayer().capabilities.isCreativeMode) {
				blockAbove.onBlockDestroyedByPlayer(event.world, event.x, y, event.z, metadataAbove);
				blockAbove.harvestBlock(event.world, event.getPlayer(), event.x, y, event.z, metadataAbove);
			}
		}
	}

}
