package net.blay09.mods.gravelminer.client;

import net.blay09.mods.gravelminer.GravelMiner;
import net.blay09.mods.gravelminer.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;
import java.util.Set;

public class GuiFactory implements IModGuiFactory {
	@Override
	public void initialize(Minecraft minecraftInstance) {
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new ConfigGUI(parentScreen);
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	public static class ConfigGUI extends GuiConfig {
		public ConfigGUI(GuiScreen parentScreen) {
			super(parentScreen, getConfigElements(), GravelMiner.MOD_ID, GravelMiner.MOD_ID, false, false, "GravelMiner Settings");
		}

		private static List<IConfigElement> getConfigElements() {
			List<IConfigElement> elements = ConfigElement.from(ModConfig.class).getChildElements();
			elements.removeIf(p -> p.getName().equals("client"));
			return elements;
		}
	}
}