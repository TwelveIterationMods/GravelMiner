package net.blay09.mods.gravelminer;

import com.google.common.collect.Sets;
import net.blay09.mods.gravelminer.client.ClientEventHandler;
import net.blay09.mods.gravelminer.client.GravelMinerKeyBindings;
import net.blay09.mods.gravelminer.net.NetworkHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Set;
import java.util.UUID;

@Mod(GravelMiner.MOD_ID)
public class GravelMiner {

    public static final String MOD_ID = "gravelminer";
    public static final boolean TEST_CLIENT_SIDE = false;

    private static final Set<UUID> hasClientSide = Sets.newHashSet();
    private static final Set<UUID> hasEnabled = Sets.newHashSet();

    public static boolean isServerInstalled;

    public GravelMiner() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GravelMinerConfig.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GravelMinerConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, GravelMinerConfig.serverSpec);

        MinecraftForge.EVENT_BUS.register(new BlockBreakHandler());
    }

    private void setup(FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(NetworkHandler::init);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GravelMinerKeyBindings.register();

            MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        });
    }

    public static boolean isAvailableFor(EntityPlayer entityPlayer) {
        return !GravelMinerConfig.SERVER.isOptIn.get() || hasClientSide.contains(entityPlayer.getUniqueID());
    }

    public static boolean isEnabledFor(EntityPlayer entityPlayer) {
        return isAvailableFor(entityPlayer) && hasEnabled.contains(entityPlayer.getUniqueID());
    }

    public static void setHasClientSide(EntityPlayer entityPlayer) {
        hasClientSide.add(entityPlayer.getUniqueID());
        setHasEnabled(entityPlayer, true);
    }

    public static void setHasEnabled(EntityPlayer entityPlayer, boolean enabled) {
        if (enabled) {
            hasEnabled.add(entityPlayer.getUniqueID());
        } else {
            hasEnabled.remove(entityPlayer.getUniqueID());
        }
    }

    public static boolean isGravelBlock(IBlockState state) {
        return !(state == null || state.getBlock().getRegistryName() == null) && GravelMinerConfig.COMMON.gravelBlocks.get().contains(state.getBlock().getRegistryName().toString());
    }

    public static boolean isTorchBlock(IBlockState state) {
        return !(state == null || state.getBlock().getRegistryName() == null) && GravelMinerConfig.COMMON.torchItems.get().contains(state.getBlock().getRegistryName().toString());
    }

    public static boolean isTorchItem(ItemStack itemStack) {
        return !(itemStack == null || itemStack.getItem().getRegistryName() == null) && GravelMinerConfig.COMMON.torchItems.get().contains(itemStack.getItem().getRegistryName().toString());
    }

}
