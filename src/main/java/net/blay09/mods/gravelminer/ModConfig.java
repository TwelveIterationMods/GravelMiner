package net.blay09.mods.gravelminer;

import net.minecraftforge.common.config.Config;

@Config(modid = GravelMiner.MOD_ID)
public class ModConfig {

	@Config.Name("Is Opt In")
	@Config.Comment("If set to true, GravelMiner will only be active for users who install the mod on their clients (for use in servers).")
	public static boolean isOptIn = true;

	@Config.Name("Roll Flint Chance")
	@Config.Comment("If set to true, gravel mined via GravelMiner will have a chance of dropping as flint (server-side only).")
	public static boolean rollFlintChance = true;

	@Config.Name("Gravel Blocks")
	@Config.Comment("Blocks that will fall and break into items when hitting a non-solid block. Format: modid:name")
	public static String[] gravelBlocks = new String[]{"minecraft:gravel"};

	@Config.Name("Trigger on Gravel")
	@Config.Comment("If set to true, the mod will trigger when mining gravel as well, instead of only when mining a non-gravel block below gravel.")
	public static boolean triggerOnGravel = true;

	@Config.Name("Torch Items")
	@Config.Comment("Blocks that are non-solid and can be destroyed in a single hit. Format: modid:name (for use on clients)")
	public static String[] torchItems = new String[]{"minecraft:torch"};

	public static Client client = new Client();

	public static class Client {
		@Config.Name("Is Enabled")
		@Config.Comment("Whether GravelMiner is currently enabled for this client (toggled via keybind)")
		public boolean isEnabled = true;

		@Config.Name("Torch Delay")
		@Config.Comment("The delay in client ticks before the torch should be placed after breaking a block below gravel. Increase if torch is placed too early, decrease if torch is placed too late. (for use on clients)")
		@Config.RangeInt(min = 2, max = 20)
		public int torchDelay = 8;

		@Config.Name("Missing on Server Message")
		@Config.Comment("Whether GravelMiner should notify the client if the server does not have GravelMiner installed")
		public boolean missingOnServerMessage = true;
	}
}
