package net.blay09.mods.gravelminer;

import com.google.common.collect.Sets;
import net.blay09.mods.gravelminer.net.MessageSetEnabled;
import net.blay09.mods.gravelminer.net.NetworkHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod(modid = GravelMiner.MOD_ID, name = "GravelMiner", acceptedMinecraftVersions = "[1.12]", guiFactory = "net.blay09.mods.gravelminer.client.GuiFactory")
public class GravelMiner {

	public static final String MOD_ID = "gravelminer";
	public static final boolean TEST_CLIENT_SIDE = false;

	@Mod.Instance
	public static GravelMiner instance;

	@SidedProxy(serverSide = "net.blay09.mods.gravelminer.CommonProxy", clientSide = "net.blay09.mods.gravelminer.client.ClientProxy")
	public static CommonProxy proxy;

	private static final Set<UUID> hasClientSide = Sets.newHashSet();
	private static final Set<UUID> hasEnabled = Sets.newHashSet();

	public static boolean isServerInstalled;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);

		MinecraftForge.EVENT_BUS.register(this);

		NetworkHandler.init();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent event) {
		if (event.getModID().equals(MOD_ID)) {
			ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
		}
	}

	@NetworkCheckHandler
	public boolean checkNetwork(Map<String, String> map, Side side) {
		if (side == Side.SERVER) {
			isServerInstalled = map.containsKey(GravelMiner.MOD_ID);
		}
		return true;
	}

	public static boolean isAvailableFor(EntityPlayer entityPlayer) {
		return !ModConfig.isOptIn || hasClientSide.contains(entityPlayer.getUniqueID());
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
		return !(state == null || state.getBlock().getRegistryName() == null) && ArrayUtils.contains(ModConfig.gravelBlocks, state.getBlock().getRegistryName().toString());
	}

	public static boolean isTorchBlock(IBlockState state) {
		return !(state == null || state.getBlock().getRegistryName() == null) && ArrayUtils.contains(ModConfig.torchItems, state.getBlock().getRegistryName().toString());
	}

	public static boolean isTorchItem(ItemStack itemStack) {
		return !(itemStack == null || itemStack.getItem().getRegistryName() == null) && ArrayUtils.contains(ModConfig.torchItems, itemStack.getItem().getRegistryName().toString());
	}

	public static void increaseTorchDelay(int value) {
		ModConfig.client.torchDelay = Math.max(1, Math.min(20, ModConfig.client.torchDelay + value));
		ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
	}

	public static void setEnabled(boolean enabled) {
		ModConfig.client.isEnabled = enabled;
		ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
		if (isServerInstalled) {
			NetworkHandler.instance.sendToServer(new MessageSetEnabled(enabled));
		}
	}

}