package net.blay09.mods.gravelminer.client;

import net.blay09.mods.gravelminer.CommonProxy;
import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ClientProxy extends CommonProxy {

	private static final int HELLO_TIMEOUT = 20 * 10;
	private int helloTimeout;
	private boolean isServerSide;

	private boolean isBreaking;
	private BlockPos breakingPos;
	private BlockPos torchPos;

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
					entityPlayer.addChatMessage(new ChatComponentText("This server does not have GravelMiner installed. Using client-only implementation."));
				}
			}
			if(!isServerSide) {
				WorldClient world = Minecraft.getMinecraft().theWorld;
				if(breakingPos != null) {
					if (world.getEntitiesWithinAABB(EntityFallingBlock.class, AxisAlignedBB.fromBounds(breakingPos.getX(), breakingPos.getY(), breakingPos.getZ(), breakingPos.getX() + 1, breakingPos.getY() + 2, breakingPos.getZ() + 1)).size() > 0) {
						if (isBreaking) {
							for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
								ItemStack hotbarStack = entityPlayer.inventory.mainInventory[i];
								if (GravelMiner.isTorchItem(hotbarStack)) {
									int old = entityPlayer.inventory.currentItem;
									entityPlayer.inventory.currentItem = i;
									Minecraft.getMinecraft().playerController.onPlayerRightClick(entityPlayer, world, hotbarStack, breakingPos, EnumFacing.UP, new Vec3(0.5, 0.5, 0.5));
									entityPlayer.inventory.currentItem = old;
									isBreaking = false;
									torchPos = new BlockPos(breakingPos);
									break;
								}
							}
						}
					} else if (torchPos != null) {
						if (GravelMiner.isTorchBlock(world.getBlockState(torchPos))) {
							Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, torchPos, EnumFacing.UP));
						}
						torchPos = null;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		isBreaking = true;
		breakingPos = new BlockPos(event.pos);
	}

	@Override
	public void receivedHello(EntityPlayer entityPlayer) {
		super.receivedHello(Minecraft.getMinecraft().thePlayer);
		helloTimeout = 0;
		isServerSide = true;
	}
}
