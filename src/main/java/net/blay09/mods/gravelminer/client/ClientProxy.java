package net.blay09.mods.gravelminer.client;

import com.google.common.collect.Sets;
import net.blay09.mods.gravelminer.CommonProxy;
import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Iterator;
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
	}

	@SubscribeEvent
	public void connectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		helloTimeout = HELLO_TIMEOUT;
		isServerSide = false;
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		EntityPlayerSP entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
		if (entityPlayer != null) {
			if (helloTimeout > 0) {
				helloTimeout--;
				if (helloTimeout <= 0 && !isServerSide) {
					entityPlayer.addChatMessage(new TextComponentString("This server does not have GravelMiner installed. Using client-only implementation."));
				}
			}
			if(!isServerSide || GravelMiner.TEST_CLIENT_SIDE) {
				WorldClient world = Minecraft.getMinecraft().theWorld;
				if(lastBreakingPos != null && world.isAirBlock(lastBreakingPos) && GravelMiner.isGravelBlock(world.getBlockState(lastBreakingPos.up()))) {
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
							if(GravelMiner.isTorchItem(entityPlayer.getHeldItemOffhand())) {
								Minecraft.getMinecraft().playerController.processRightClickBlock(entityPlayer, world, entityPlayer.getHeldItemOffhand(), gravelKiller.torchPos, EnumFacing.UP, new Vec3d(0.5, 0.5, 0.5), EnumHand.OFF_HAND);
							} else {
								for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
									ItemStack hotbarStack = entityPlayer.inventory.mainInventory[i];
									if (GravelMiner.isTorchItem(hotbarStack)) {
										int old = entityPlayer.inventory.currentItem;
										entityPlayer.inventory.currentItem = i;
										Minecraft.getMinecraft().playerController.processRightClickBlock(entityPlayer, world, hotbarStack, gravelKiller.torchPos, EnumFacing.UP, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
										entityPlayer.inventory.currentItem = old;
										break;
									}
								}
							}
						}
					} else if(!GravelMiner.isGravelBlock(world.getBlockState(gravelKiller.torchPos.up()))) {
						if(world.getEntitiesWithinAABB(EntityFallingBlock.class, new AxisAlignedBB(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ(), gravelKiller.torchPos.getX() + 1, gravelKiller.torchPos.getY() + 2, gravelKiller.torchPos.getZ() + 1)).size() == 0) {
							// Looks like all gravel has fallen...
							if (GravelMiner.isTorchBlock(world.getBlockState(gravelKiller.torchPos))) {
								// ...so break the torch!
								Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, gravelKiller.torchPos, EnumFacing.UP));
							} else if (GravelMiner.isGravelBlock(world.getBlockState(gravelKiller.torchPos))) {
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
							if (GravelMiner.isTorchBlock(world.getBlockState(gravelKiller.torchPos))) {
								Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, gravelKiller.torchPos, EnumFacing.UP));
								if(GravelMiner.isGravelBlock(world.getBlockState(gravelKiller.torchPos.up()))) {
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
		lastBreakingPos = event.getPos();
	}

	@Override
	public void receivedHello(EntityPlayer entityPlayer) {
		super.receivedHello(Minecraft.getMinecraft().thePlayer);
		helloTimeout = 0;
		isServerSide = true;
	}
}
