package net.blay09.mods.gravelminer.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.blay09.mods.gravelminer.GravelMiner;
import net.blay09.mods.gravelminer.GravelMinerConfig;
import net.blay09.mods.gravelminer.net.MessageHello;
import net.blay09.mods.gravelminer.net.MessageSetEnabled;
import net.blay09.mods.gravelminer.net.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ClientEventHandler {

    private BlockPos lastBreakingPos;
    private final Set<GravelKiller> gravelKillerList = Sets.newHashSet();
    private final List<GravelKiller> tmpAddList = Lists.newArrayList();

    @SubscribeEvent
    public void onClientJoin(EntityJoinWorldEvent event) {
        if (GravelMiner.isServerInstalled && event.getEntity() == Minecraft.getInstance().player) {
            NetworkHandler.channel.sendToServer(new MessageHello());
            NetworkHandler.channel.sendToServer(new MessageSetEnabled(GravelMinerConfig.CLIENT.isEnabled.get()));
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            if (GravelMinerKeyBindings.keyToggle.isActiveAndMatches(InputMappings.getInputByCode(event.getKey(), event.getScanCode()))) {
                boolean newEnabled = !GravelMinerConfig.CLIENT.isEnabled.get();
                GravelMinerConfig.setEnabled(newEnabled);
                if (GravelMiner.isServerInstalled) {
                    NetworkHandler.channel.sendToServer(new MessageSetEnabled(newEnabled));
                }
                Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TranslationTextComponent("gravelminer.toggle" + (newEnabled ? "On" : "Off")), 3);
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        ClientPlayerEntity entityPlayer = Minecraft.getInstance().player;
        if (entityPlayer != null) {
            if ((!GravelMiner.isServerInstalled || GravelMiner.TEST_CLIENT_SIDE) && GravelMinerConfig.CLIENT.isEnabled.get()) {
                ClientWorld world = Minecraft.getInstance().world;
                if (lastBreakingPos != null && world.isAirBlock(lastBreakingPos) && GravelMiner.isGravelBlock(world.getBlockState(lastBreakingPos.up()))) {
                    gravelKillerList.add(new GravelKiller(lastBreakingPos));
                    lastBreakingPos = null;
                }
                Iterator<GravelKiller> it = gravelKillerList.iterator();
                while (it.hasNext()) {
                    GravelKiller gravelKiller = it.next();
                    // Place the torch after a short delay to allow for the gravel to start falling
                    if (gravelKiller.placeTorchDelayTicks > 0) {
                        gravelKiller.placeTorchDelayTicks--;
                        if (gravelKiller.placeTorchDelayTicks <= 0) {
                            BlockRayTraceResult rayTraceResult = new BlockRayTraceResult(new Vector3d(0.5, 0.5, 0.5), Direction.UP, gravelKiller.torchPos, false);
                            if (GravelMiner.isTorchItem(entityPlayer.getHeldItemOffhand())) {
                                Minecraft.getInstance().playerController.func_217292_a(entityPlayer, world, Hand.OFF_HAND, rayTraceResult); // playerController.processRightClickBlock
                            } else {
                                for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
                                    ItemStack hotbarStack = entityPlayer.inventory.mainInventory.get(i);
                                    if (GravelMiner.isTorchItem(hotbarStack)) {
                                        int old = entityPlayer.inventory.currentItem;
                                        entityPlayer.inventory.currentItem = i;
                                        Minecraft.getInstance().playerController.func_217292_a(entityPlayer, world, Hand.MAIN_HAND, rayTraceResult); // playerController.processRightClickBlock
                                        entityPlayer.inventory.currentItem = old;
                                        break;
                                    }
                                }
                            }
                        }
                    } else if (!GravelMiner.isGravelBlock(world.getBlockState(gravelKiller.torchPos.up()))) {
                        if (world.getEntitiesWithinAABB(FallingBlockEntity.class, new AxisAlignedBB(gravelKiller.torchPos.getX(), gravelKiller.torchPos.getY(), gravelKiller.torchPos.getZ(), gravelKiller.torchPos.getX() + 1, gravelKiller.torchPos.getY() + 2, gravelKiller.torchPos.getZ() + 1)).size() == 0) {
                            // Looks like all gravel has fallen...
                            if (GravelMiner.isTorchBlock(world.getBlockState(gravelKiller.torchPos))) {
                                // ...so break the torch!
                                ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
                                if (connection != null) {
                                    connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, gravelKiller.torchPos, Direction.UP));
                                }
                            } else if (GravelMiner.isGravelBlock(world.getBlockState(gravelKiller.torchPos))) {
                                // It seems the gravel fell before the place was torch, which means it was placed too late
                                // Can't easily re-do in this case, but fix up the delay for next time
                                GravelMinerConfig.increaseTorchDelay(-1);
                            }
                            it.remove();
                        }
                    } else {
                        gravelKiller.gravelAboveTimeout--;
                        if (gravelKiller.gravelAboveTimeout <= 0) {
                            // It looks like the gravel got stuck on top of the torch, which means it was placed too early
                            GravelMinerConfig.increaseTorchDelay(1);
                            it.remove();
                            // Break the torch and try again with new delay
                            if (GravelMiner.isTorchBlock(world.getBlockState(gravelKiller.torchPos))) {
                                ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
                                if (connection != null) {
                                    connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, gravelKiller.torchPos, Direction.UP));
                                }
                                if (GravelMiner.isGravelBlock(world.getBlockState(gravelKiller.torchPos.up()))) {
                                    tmpAddList.add(new GravelKiller(gravelKiller.torchPos));
                                }
                            }
                        }
                    }
                }
                if (!tmpAddList.isEmpty()) {
                    gravelKillerList.addAll(tmpAddList);
                    tmpAddList.clear();
                }
            }
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (GravelMinerConfig.COMMON.triggerOnGravel.get() || !GravelMiner.isGravelBlock(event.getState())) {
            lastBreakingPos = event.getPos();
        }
    }
}
