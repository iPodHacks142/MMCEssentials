package net.moddedminecraft.mmcessentials.listeners;

import net.moddedminecraft.mmcessentials.Config;
import net.moddedminecraft.mmcessentials.MMCEssentials;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
	
	private static MMCEssentials plugin;

	public CommandListener(MMCEssentials instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		boolean plugins = event.getMessage().startsWith("/plugins");
		boolean pl = event.getMessage().equalsIgnoreCase("/pl");
		boolean pl2 = event.getMessage().startsWith("/pl ");
		boolean plugman = event.getMessage().startsWith("/plugman list");
		boolean gc = event.getMessage().equalsIgnoreCase("/gc");
		boolean icanhasbukkit = event.getMessage().startsWith("/icanhasbukkit");
		boolean unknown = event.getMessage().startsWith("/?");
		boolean version = event.getMessage().startsWith("/version");
		boolean ver = event.getMessage().startsWith("/ver");
		boolean bukkitplugin = event.getMessage().startsWith("/bukkit:plugins");
		boolean bukkitpl = event.getMessage().startsWith("/bukkit:pl");
		boolean bukkitunknown = event.getMessage().startsWith("/bukkit:?");
		boolean about = event.getMessage().startsWith("/about");
		boolean a = event.getMessage().equalsIgnoreCase("/a");
		boolean bukkitabout = event.getMessage().startsWith("/bukkit:about");
		boolean bukkita = event.getMessage().startsWith("/bukkit:a");
		boolean bukkitversion = event.getMessage().startsWith("/bukkit:version");
		boolean bukkitver = event.getMessage().startsWith("/bukkit:ver");
		boolean bukkithelp = event.getMessage().startsWith("/bukkit:help");


		Player player = event.getPlayer();

		if (Config.hideplugins = true) {
			if ((plugins) || (pl) || (pl2) || (plugman) || (bukkitunknown) ||  (unknown) ||  (bukkitplugin) ||  (bukkitpl)) {
				if(!player.hasPermission("mmcessentials.hideplugins.bypass")){
					event.setCancelled(true);
					String defaultMessage = "§a";
					for (String plugin : plugin.plugins) {
						defaultMessage = defaultMessage + plugin + ", ";
					}
					defaultMessage = defaultMessage.substring(0, defaultMessage.lastIndexOf(", "));
					player.sendMessage(ChatColor.WHITE + "Plugins (" + plugin.plugins.size() + "): " + ChatColor.GREEN + defaultMessage.replaceAll(", ", new StringBuilder().append(ChatColor.WHITE).append(", ").append(ChatColor.GREEN).toString()));
				}
			}


			if ((version) || (ver) ||  (gc) ||  (icanhasbukkit) ||  (a) ||  (about) ||  (bukkitversion) ||  (bukkitver)||  (bukkitabout)  ||  (bukkita) ||  (bukkithelp)) {
				if(!player.hasPermission("mmcessentials.hideplugins.bypass")){
					Player p = event.getPlayer();
					event.setCancelled(true);
					p.sendMessage(ChatColor.RED + "You do not have the permission to use that!");
				}
			}
		}

		boolean cofh1 = event.getMessage().equalsIgnoreCase("/cofh killall");
		boolean cofh2 = event.getMessage().equalsIgnoreCase("/cofh tps");
		boolean cofh3 = event.getMessage().equalsIgnoreCase("/cofh help");
		boolean cofh4 = event.getMessage().equalsIgnoreCase("/cofh cape");
		boolean cofh5 = event.getMessage().equalsIgnoreCase("/cofh skin");
		boolean cofh6 = event.getMessage().startsWith("/cofh killall");
		boolean cofh7 = event.getMessage().startsWith("/cofh tps");
		boolean cofh8 = event.getMessage().startsWith("/cofh help");
		boolean cofh9 = event.getMessage().startsWith("/cofh cape");
		boolean cofh10 = event.getMessage().startsWith("/cofh skin");
		boolean cofh11 = event.getMessage().startsWith("/cofh friend");

		if ((cofh1) || (cofh2) || (cofh3) || (cofh5) || (cofh6) || (cofh7) || (cofh8) || (cofh9) || (cofh10)) {
			if (!player.hasPermission("cofh.staff")) {
				Player p = event.getPlayer();
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You do not have the permission to use that!");
			}	
		}

		if ((cofh11)) {
			if (!player.hasPermission("cofh.friend")) {
				Player p = event.getPlayer();
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You do not have the permission to use that! Register on the website and become member to gain access to this command. Type '/register' for more information.");
			}	
		}
	}
}
