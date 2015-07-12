package net.moddedminecraft.mmcessentials;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

	@SuppressWarnings("unused")
	private static MMCEssentials plugin;

	public Config(MMCEssentials instance) {
		plugin = instance;
	}

	static File configFile;

	private static final int confVersion = 1; // Tracking config version

	public static int configVersion = 0;
	public static boolean hideplugins = true;

	static void loadConfiguration(MMCEssentials plugin) {

		final FileConfiguration config = plugin.getConfig();
		config.options().copyDefaults(true);
		final int ver = config.getInt("version", 0);
		
		if (!plugin.plugins.isEmpty()) {
			plugin.plugins.clear();
		}

		if (ver != Config.confVersion) {
			plugin.getLogger().info("Attempting to update your configuration. Check to make sure it's ok");
			if (ver < 1) {
				config.set("hideplugins.enabled", true);
				config.set("hideplugins.list", "There, is, nothing, to, see, here");
				config.set("version", Config.confVersion);
			}
		}

		Config.configVersion = plugin.getConfig().getInt("version", 0);

		Config.hideplugins = plugin.getConfig().getBoolean("hideplugins.enabled", true);

		for (String s : plugin.getConfig().getString("hideplugins.list").split(", ")) {
			plugin.plugins.add(s);
		}
	}

}
