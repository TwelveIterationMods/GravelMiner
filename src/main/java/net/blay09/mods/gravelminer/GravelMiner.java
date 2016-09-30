package net.blay09.mods.gravelminer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.blay09.mods.gravelminer.net.MessageSetEnabled;
import net.blay09.mods.gravelminer.net.NetworkHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod(modid = GravelMiner.MOD_ID, name = "GravelMiner", acceptedMinecraftVersions = "[1.10]",
		guiFactory = "net.blay09.mods.gravelminer.client.GuiFactory",
		updateJSON = "http://balyware.com/new/forge_update.php?modid=gravelminer")
public class GravelMiner {

	public static final String MOD_ID = "gravelminer";
	public static final boolean TEST_CLIENT_SIDE = false;

	@Mod.Instance
	public static GravelMiner instance;

	@SidedProxy(serverSide = "net.blay09.mods.gravelminer.CommonProxy", clientSide = "net.blay09.mods.gravelminer.client.ClientProxy")
	public static CommonProxy proxy;

	private static final String TORCH_DELAY_COMMENT = "The delay in client ticks before the torch should be placed after breaking a block below gravel. Increase if torch is placed too early, decrease if torch is placed too late. (for use on clients)";
	private static final String IS_ENABLED_COMMENT = "Whether GravelMiner is currently enabled for this client (toggled via keybind)";

	private static final Set<UUID> hasClientSide = Sets.newHashSet();
	private static final Set<UUID> hasEnabled = Sets.newHashSet();

	public static Configuration config;
	private static boolean isOptIn;
	private static List<String> gravelBlocks;
	private static List<String> torchItems;
	private static int torchDelay;
	private static boolean isEnabled;

	public static boolean isServerInstalled;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);

		MinecraftForge.EVENT_BUS.register(this);

		config = new Configuration(event.getSuggestedConfigurationFile());
		reloadConfig();

		NetworkHandler.init();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	private void reloadConfig() {
		isOptIn = config.getBoolean("Is Opt In", "general", true, "If set to true, GravelMiner will only be active for users who install the mod on their clients. (for use in servers)");
		gravelBlocks = Lists.newArrayList(config.getStringList("Gravel Blocks", "general", new String[]{"minecraft:gravel"}, "Blocks that will fall and break into items when hitting a non-solid block. Format: modid:name"));
		torchItems = Lists.newArrayList(config.getStringList("Torch Items", "general", new String[]{"minecraft:torch"}, "Blocks that are non-solid and can be destroyed in a single hit. Format: modid:name (for use on clients)"));
		torchDelay = config.getInt("Torch Delay", "client", 8, 2, 20, TORCH_DELAY_COMMENT);
		isEnabled = config.getBoolean("Is Enabled", "client", true, IS_ENABLED_COMMENT);
		if (config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent event) {
		if (event.getModID().equals(MOD_ID)) {
			reloadConfig();
		}
	}

	@NetworkCheckHandler
	public boolean checkNetwork(Map<String, String> map, Side side) {
		if(side == Side.SERVER) {
			isServerInstalled = map.containsKey(GravelMiner.MOD_ID);
		}
		return true;
	}

	public static boolean isAvailableFor(EntityPlayer entityPlayer) {
		return !isOptIn || hasClientSide.contains(entityPlayer.getUniqueID());
	}

	public static boolean isEnabledFor(EntityPlayer entityPlayer) {
		return isAvailableFor(entityPlayer) && hasEnabled.contains(entityPlayer.getUniqueID());
	}

	public static void setHasClientSide(EntityPlayer entityPlayer) {
		hasClientSide.add(entityPlayer.getUniqueID());
		setHasEnabled(entityPlayer, true);
	}

	public static void setHasEnabled(EntityPlayer entityPlayer, boolean enabled) {
		if(enabled) {
			hasEnabled.add(entityPlayer.getUniqueID());
		} else {
			hasEnabled.remove(entityPlayer.getUniqueID());
		}
	}

	public static boolean isGravelBlock(IBlockState state) {
		return !(state == null || state.getBlock().getRegistryName() == null) && gravelBlocks.contains(state.getBlock().getRegistryName().toString());
	}

	public static boolean isTorchBlock(IBlockState state) {
		return !(state == null || state.getBlock().getRegistryName() == null) && torchItems.contains(state.getBlock().getRegistryName().toString());
	}

	public static boolean isTorchItem(ItemStack itemStack) {
		return !(itemStack == null || itemStack.getItem().getRegistryName() == null) && torchItems.contains(itemStack.getItem().getRegistryName().toString());
	}

	public static int getTorchDelay() {
		return torchDelay;
	}

	public static void increaseTorchDelay(int value) {
		torchDelay = Math.max(1, Math.min(20, torchDelay + value));
		config.get("client", "Torch Delay", 8, TORCH_DELAY_COMMENT, 2, 20).set(torchDelay);
		if (config.hasChanged()) {
			config.save();
		}
	}

	public static void setEnabled(boolean enabled) {
		isEnabled = enabled;
		config.get("client", "Is Enabled", true, IS_ENABLED_COMMENT).set(enabled);
		if (config.hasChanged()) {
			config.save();
		}
		if(isServerInstalled) {
			NetworkHandler.instance.sendToServer(new MessageSetEnabled(enabled));
		}
	}

	public static boolean isEnabled() {
		return isEnabled;
	}
}
