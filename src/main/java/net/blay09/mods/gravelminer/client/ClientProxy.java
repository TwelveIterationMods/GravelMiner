package net.blay09.mods.gravelminer.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.blay09.mods.gravelminer.CommonProxy;
import net.blay09.mods.gravelminer.GravelMiner;
import net.blay09.mods.gravelminer.net.MessageHello;
import net.blay09.mods.gravelminer.net.MessageSetEnabled;
import net.blay09.mods.gravelminer.net.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

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

	private boolean sentMissingMessage;

	private BlockPos lastBreakingPos;
	private final Set<GravelKiller> gravelKillerList = Sets.newHashSet();
	private final List<GravelKiller> tmpAddList = Lists.newArrayList();
	private final KeyBinding keyToggle = new KeyBinding("key.gravelminer.toggle", KeyConflictContext.IN_GAME, KeyModifier.NONE, 0, "key.categories.gravelminer");

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		ClientRegistry.registerKeyBinding(keyToggle);
	}

	@SubscribeEvent
	public void onClientJoin(EntityJoinWorldEvent event) {
		if(GravelMiner.isServerInstalled && event.getEntity() == Minecraft.getMinecraft().thePlayer) {
			NetworkHandler.instance.sendToServer(new MessageHello());
			NetworkHandler.instance.sendToServer(new MessageSetEnabled(GravelMiner.isEnabled()));
		}
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(Keyboard.getEventKeyState()) {
			if(keyToggle.isActiveAndMatches(Keyboard.getEventKey())) {
				boolean newEnabled = !GravelMiner.isEnabled();
				GravelMiner.setEnabled(newEnabled);
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentTranslation("gravelminer.toggle" + (newEnabled ? "On" : "Off")), 3);
			}
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		EntityPlayerSP entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
		if (entityPlayer != null) {
			if (!GravelMiner.isServerInstalled && !sentMissingMessage) {
				entityPlayer.addChatMessage(new TextComponentTranslation("gravelminer.serverNotInstalled"));
				sentMissingMessage = true;
			}
			if((!GravelMiner.isServerInstalled || GravelMiner.TEST_CLIENT_SIDE) && GravelMiner.isEnabled()) {
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
									tmpAddList.add(new GravelKiller(gravelKiller.torchPos));
								}
							}
						}
					}
				}
				if(!tmpAddList.isEmpty()) {
					gravelKillerList.addAll(tmpAddList);
					tmpAddList.clear();
				}
			}
		}
	}

	@Override
	public void addScheduledTask(Runnable runnable) {
		Minecraft.getMinecraft().addScheduledTask(runnable);
	}

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if(!GravelMiner.isGravelBlock(event.getState())) {
			lastBreakingPos = event.getPos();
		}
	}

}
