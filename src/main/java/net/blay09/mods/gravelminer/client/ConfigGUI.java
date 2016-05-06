package net.blay09.mods.gravelminer.client;

import cpw.mods.fml.client.config.GuiConfig;
import net.blay09.mods.gravelminer.GravelMiner;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

public class ConfigGUI extends GuiConfig {
	public ConfigGUI(GuiScreen parentScreen) {
		super(parentScreen, new ConfigElement(GravelMiner.config.getCategory("general")).getChildElements(), GravelMiner.MOD_ID, GravelMiner.MOD_ID, false, false, "GravelMiner Settings");
	}
}
