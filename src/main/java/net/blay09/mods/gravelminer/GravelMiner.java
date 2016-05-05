package net.blay09.mods.gravelminer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.blay09.mods.gravelminer.net.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mod(modid = GravelMiner.MOD_ID, name = "GravelMiner", acceptableRemoteVersions = "*")
public class GravelMiner {

	public static final String MOD_ID = "gravelminer";

	@Mod.Instance
	public static GravelMiner instance;

	@SidedProxy(serverSide = "net.blay09.mods.gravelminer.CommonProxy", clientSide = "net.blay09.mods.gravelminer.client.ClientProxy")
	public static CommonProxy proxy;

	private static final Set<UUID> enabledUsers = Sets.newHashSet();

	private static boolean isOptIn;
	private static List<String> gravelBlocks;
	private static List<String> torchItems;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		isOptIn = config.getBoolean("Is Opt In", "general", true, "If set to true, GravelMiner will only be active for users who install the mod on their clients. (for use in servers)");
		gravelBlocks = Lists.newArrayList(config.getStringList("Gravel Blocks", "general", new String[] { "minecraft:gravel" }, "Blocks that will fall and break into items when hitting a non-solid block. Format: modid:name"));
		torchItems = Lists.newArrayList(config.getStringList("Torch Items", "general", new String[] { "minecraft:torch" }, "Blocks that are non-solid and can be destroyed in a single hit. Format: modid:name (for use on clients)"));
		if(config.hasChanged()) {
			config.save();
		}

		NetworkHandler.init();
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

	public static boolean isTorchItem(ItemStack itemStack) {
		return itemStack != null && isTorchBlock(Block.getBlockFromItem(itemStack.getItem()), itemStack.getItemDamage());
	}
}
