package net.blay09.mods.gravelminer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.blay09.mods.gravelminer.net.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mod(modid = GravelMiner.MOD_ID, name = "GravelMiner", acceptableRemoteVersions = "*", guiFactory = "net.blay09.mods.gravelminer.client.GuiFactory")
public class GravelMiner {

	public static final String MOD_ID = "gravelminer";
	public static final boolean TEST_CLIENT_SIDE = false;

	@Mod.Instance
	public static GravelMiner instance;

	@SidedProxy(serverSide = "net.blay09.mods.gravelminer.CommonProxy", clientSide = "net.blay09.mods.gravelminer.client.ClientProxy")
	public static CommonProxy proxy;

	private static final String TORCH_DELAY_COMMENT = "The delay in client ticks before the torch should be placed after breaking a block below gravel. Increase if torch is placed too early, decrease if torch is placed too late. (for use on clients)";

	private static final Set<UUID> enabledUsers = Sets.newHashSet();

	public static Configuration config;
	private static boolean isOptIn;
	private static List<String> gravelBlocks;
	private static List<String> torchItems;
	private static int torchDelay;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);

		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		config = new Configuration(event.getSuggestedConfigurationFile());
		reloadConfig();

		NetworkHandler.init();
	}

	private void reloadConfig() {
		isOptIn = config.getBoolean("Is Opt In", "general", true, "If set to true, GravelMiner will only be active for users who install the mod on their clients. (for use in servers)");
		gravelBlocks = Lists.newArrayList(config.getStringList("Gravel Blocks", "general", new String[] { "minecraft:gravel" }, "Blocks that will fall and break into items when hitting a non-solid block. Format: modid:name"));
		torchItems = Lists.newArrayList(config.getStringList("Torch Items", "general", new String[] { "minecraft:torch" }, "Blocks that are non-solid and can be destroyed in a single hit. Format: modid:name (for use on clients)"));
		torchDelay = config.getInt("Torch Delay", "general", 8, 2, 20, TORCH_DELAY_COMMENT);
		if(config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent event) {
		if(event.modID.equals(MOD_ID)) {
			reloadConfig();
		}
	}

	public static boolean isEnabledFor(EntityPlayer entityPlayer) {
		return !isOptIn || enabledUsers.contains(entityPlayer.getUniqueID());
	}

	public static void enableFor(EntityPlayer entityPlayer) {
		enabledUsers.add(entityPlayer.getUniqueID());
	}

	public static boolean isGravelBlock(Block block, int metadata) {
		GameRegistry.UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(block);
		return gravelBlocks.contains(identifier.toString());
	}

	public static boolean isTorchBlock(Block block, int metadata) {
		GameRegistry.UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(block);
		return torchItems.contains(identifier.toString());
	}

	public static int getTorchDelay() {
		return torchDelay;
	}

	public static void increaseTorchDelay(int value) {
		torchDelay = Math.max(1, Math.min(20, torchDelay + value));
		config.get("Torch Delay", "general", 8, TORCH_DELAY_COMMENT, 2, 20).set(torchDelay);
		if(config.hasChanged()) {
			config.save();
		}
	}

	public static boolean isTorchItem(ItemStack itemStack) {
		return itemStack != null && isTorchBlock(Block.getBlockFromItem(itemStack.getItem()), itemStack.getItemDamage());
	}
}
