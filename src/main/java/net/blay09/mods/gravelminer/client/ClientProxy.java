package net.blay09.mods.gravelminer.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.blay09.mods.gravelminer.BlockPos;
import net.blay09.mods.gravelminer.CommonProxy;
import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ClientProxy extends CommonProxy {

	public static class GravelKiller {
		public final BlockPos torchPos;
		public int placeTorchDelayTicks;
		public int gravelAboveTimeout;

		public GravelKiller(BlockPos torchPos) {
			this.torchPos = new BlockPos(torchPos);
			placeTorchDelayTicks = GravelMiner.getTorchDelay();
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

	private static final int HELLO_TIMEOUT = 20 * 10;
	private int helloTimeout;
	private boolean isServerSide;

	private BlockPos lastBreakingPos;
	private final Set<GravelKiller> gravelKillerList = Sets.newHashSet();

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void connectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		helloTimeout = HELLO_TIMEOUT;
		isServerSide = false;
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
		if (entityPlayer != null) {
			if (helloTimeout > 0) {
				helloTimeout--;
				if (helloTimeout <= 0 && !isServerSide) {
					entityPlayer.addChatMessage(new ChatComponentText("This server does not have GravelMiner installed. Using client-only implementation."));
				}
			}
			if(!isServerSide || GravelMiner.TEST_CLIENT_SIDE) {
				World world = Minecraft.getMinecraft().theWorld;
				if(lastBreakingPos != null && world.isAirBlock(lastBreakingPos.getX(), lastBreakingPos.getY(), lastBreakingPos.getZ()) && GravelMiner.isGravelBlock(world.getBlock(lastBreakingPos.getX(), lastBreakingPos.getY() + 1, lastBreakingPos.getZ()), world.getBlockMetadata(lastBreakingPos.getX(), lastBreakingPos.getY() + 1, lastBreakingPos.getZ()))) {
					gravelKillerList.add(new GravelKiller(lastBreakingPos));
					lastBreakingPos = null;
				}
				Iterator<GravelKiller> it = gravelKillerList.iterator();
				while(it.hasNext()) {
					GravelKiller gravelKiller = it.next();
					// Place the torch after a short delay to allow for the gravel to start falling
					if(gravelKiller.placeTorchDelayTicks > 0) {
						gravelKiller.placeTorchDelayTicks--;
						if(gravelKiller.placeTorchDelayTicks <= 0) {
							for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
								ItemStack hotbarStack = entityPlayer.inventory.mainInventory[i];
								if (GravelMiner.isTorchItem(hotbarStack)) {
									int old = entityPlayer.inventory.currentItem;
									entityPlayer.inventory.currentItem = i;
									Minecraft.getMinecraft().playerController.onPlayerRightClick(entityPlayer, world, hotbarStack, gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ(), ForgeDirection.UP.ordinal(), Vec3.createVectorHelper(0.5, 0.5, 0.5));
									entityPlayer.inventory.currentItem = old;
									break;
								}
							}
						}
					} else if(!GravelMiner.isGravelBlock(world.getBlock(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY() + 1, gravelKiller.torchPos.getZ()), world.getBlockMetadata(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY() + 1, gravelKiller.torchPos.getZ()))) {
						if(world.getEntitiesWithinAABB(EntityFallingBlock.class, AxisAlignedBB.getBoundingBox(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ(), gravelKiller.torchPos.getX() + 1, gravelKiller.torchPos.getY() + 2, gravelKiller.torchPos.getZ() + 1)).size() == 0) {
							// Looks like all gravel has fallen...
							if (GravelMiner.isTorchBlock(world.getBlock(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ()), world.getBlockMetadata(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ()))) {
								// ...so break the torch!
								Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(2, gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ(), ForgeDirection.UP.ordinal()));
							} else if (GravelMiner.isGravelBlock(world.getBlock(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ()), world.getBlockMetadata(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ()))) {
								// It seems the gravel fell before the place was torch, which means it was placed too late
								// Can't easily re-do in this case, but fix up the delay for next time
								GravelMiner.increaseTorchDelay(-1);
							}
							it.remove();
						}
					} else {
						gravelKiller.gravelAboveTimeout--;
						if(gravelKiller.gravelAboveTimeout <= 0) {
							// It looks like the gravel got stuck on top of the torch, which means it was placed too early
							GravelMiner.increaseTorchDelay(1);
							it.remove();
							// Break the torch and try again with new delay
							if (GravelMiner.isTorchBlock(world.getBlock(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ()), world.getBlockMetadata(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ()))) {
								Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(2, gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ(), ForgeDirection.UP.ordinal()));
								if(GravelMiner.isGravelBlock(world.getBlock(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY() + 1, gravelKiller.torchPos.getZ()), world.getBlockMetadata(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY() + 1, gravelKiller.torchPos.getZ()))) {
									gravelKillerList.add(new GravelKiller(gravelKiller.torchPos));
								}
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		lastBreakingPos = new BlockPos(event.x, event.y, event.z);
	}

	@Override
	public void receivedHello(EntityPlayer entityPlayer) {
		super.receivedHello(Minecraft.getMinecraft().thePlayer);
		helloTimeout = 0;
		isServerSide = true;
	}
}
