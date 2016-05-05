package net.blay09.mods.gravelminer.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
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

public class ClientProxy extends CommonProxy {

	private static final int HELLO_TIMEOUT = 20 * 10;
	private int helloTimeout;
	private boolean isServerSide;

	private boolean isBreaking;
	private int breakingX;
	private int breakingY;
	private int breakingZ;

	private boolean isTorching;
	private int torchX;
	private int torchY;
	private int torchZ;

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
			if(!isServerSide) {
				World world = Minecraft.getMinecraft().theWorld;
				if(world.getEntitiesWithinAABB(EntityFallingBlock.class, AxisAlignedBB.getBoundingBox(breakingX, breakingY, breakingZ, breakingX + 1, breakingY + 2, breakingZ + 1)).size() > 0) {
					if(isBreaking) {
						for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
							ItemStack hotbarStack = entityPlayer.inventory.mainInventory[i];
							if (GravelMiner.isTorchItem(hotbarStack)) {
								int old = entityPlayer.inventory.currentItem;
								entityPlayer.inventory.currentItem = i;
								Minecraft.getMinecraft().playerController.onPlayerRightClick(entityPlayer, world, hotbarStack, breakingX, breakingY, breakingZ, ForgeDirection.UP.ordinal(), Vec3.createVectorHelper(0.5, 0.5, 0.5));
								entityPlayer.inventory.currentItem = old;
								isBreaking = false;
								isTorching = true;
								torchX = breakingX;
								torchY = breakingY;
								torchZ = breakingZ;
								break;
							}
						}
					}
				} else if(isTorching) {
					if(GravelMiner.isTorchBlock(world.getBlock(torchX, torchY, torchZ), world.getBlockMetadata(torchX, torchY, torchZ))) {
						Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(2, torchX, torchY, torchZ, ForgeDirection.UP.ordinal()));
					}
					isTorching = false;
				}
			}
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		isBreaking = true;
		breakingX = event.x;
		breakingY = event.y;
		breakingZ = event.z;
	}

	@Override
	public void receivedHello(EntityPlayer entityPlayer) {
		super.receivedHello(Minecraft.getMinecraft().thePlayer);
		helloTimeout = 0;
		isServerSide = true;
	}
}
