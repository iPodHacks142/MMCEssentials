package net.moddedminecraft.mmcessentials;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import net.moddedminecraft.mmcessentials.commands.MMCECommand;
import net.moddedminecraft.mmcessentials.listeners.CommandListener;
import net.moddedminecraft.mmcessentials.utils.ShutdownTask;
import net.moddedminecraft.mmcessentials.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.util.BlockIterator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import static org.bukkit.Bukkit.*;

public class MMCEssentials extends JavaPlugin {
	
	public Logger log = Logger.getLogger("Minecraft");

	MMCEssentials plugin = this;
	
	public static int usingReason = 0;
	public static int voteCancel = 0;
	public int rebootConfirm = 0;
	public static int cdTimer = 0;
	public static boolean voteStarted = false;
	public static int yesVotes = 0;
	public static ArrayList<Player> hasVoted = new ArrayList<Player>();
	public ArrayList<String> plugins = new ArrayList<String>();

	public static int playersOnline;
	public static int voteSeconds;

	public boolean justStarted = true;
	public boolean isRestarting = false;
	public String reason;

	public String version;
	public String name;

	// Timers
	public static ArrayList<Timer> warningTimers = new ArrayList<Timer>();
	public Timer rebootTimer;
	public Timer justStartedTimer;

	public static long startTimestamp;

	public void onEnable() {
		PluginManager pm = getPluginManager();
		version = getDescription().getVersion();
		name = getDescription().getName();
		final List<String> hiddenCommands = new ArrayList<String>();
		hiddenCommands.add("all");

		if(pm.getPlugin("ProtocolLib") != null) {
			preventTabComplete();
		} else {
			log.info("[MMCEssentials] ProtocalLib not found. Disabling support.");
		}

		Config.loadConfiguration(this);
		getCommand("blockinfo").setExecutor(new MMCECommand(plugin));
		getCommand("mmce").setExecutor(new MMCECommand(plugin));
		pm.registerEvents(new CommandListener(this), this);
	}
	
	public void onDisable() {
		cancelTasks();
		log.info("[MMCEssentials] plugin disabled");
	}
	
	public void preventTabComplete() {
		final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(this, new PacketType[] { PacketType.Play.Client.TAB_COMPLETE })
		{
			@SuppressWarnings("rawtypes")
			public void onPacketReceiving(PacketEvent event) {
				if ((event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) 
						&& (!event.getPlayer().hasPermission("mmcessentials.bypass")) 
						&& (((String)event.getPacket().getStrings().read(0)).startsWith("/")) 
						&& (((String)event.getPacket().getStrings().read(0)).split(" ").length == 1)) {

					event.setCancelled(true);

					List<?> list = new ArrayList();
					List<?> extra = new ArrayList();

					String[] tabList = new String[list.size() + extra.size()];

					for (int index = 0; index < list.size(); index++) {
						tabList[index] = ((String)list.get(index));
					}

					for (int index = 0; index < extra.size(); index++) {
						tabList[(index + list.size())] = ('/' + (String)extra.get(index));
					}
					PacketContainer tabComplete = manager.createPacket(PacketType.Play.Server.TAB_COMPLETE);
					tabComplete.getStringArrays().write(0, tabList);

					try {
						manager.sendServerPacket(event.getPlayer(), tabComplete);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public boolean stopServer() {
		log.info("[MMCEssentials] Restarting...");
		isRestarting = false;
		Util.broadcastMessage("&cServer is restarting, we'll be right back!");
		try {
			File file = new File(this.getDataFolder().getAbsolutePath() + File.separator + "restart.txt");
			log.info("[MMCEssentials] Touching restart.txt at: " + file.getAbsolutePath());
			if (file.exists()) {
				file.setLastModified(System.currentTimeMillis());
			} else {
				file.createNewFile();
			}
		} catch (Exception e) {
			log.info("[MMCEssentials] Something went wrong while touching restart.txt!");
			return false;
		}
		try {
			this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "save-all");
			this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "stop");
		} catch (Exception e) {
			log.info("[MMCEssentials] Something went wrong while saving & stoping!");
			return false;
		}
		return true;
	}
	
	public void ReloadCfg() {
		reloadConfig();
		plugin.getConfig().getDefaults();
		Config.loadConfiguration(this);
	}
	
	public void cancelTasks() {
	}
	
	public static int getTimeLeftInSeconds() {
		return voteSeconds;
	}
	
	
	public void logToFile(String message) {
        try {
            File dataFolder = this.getDataFolder();
            if(!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            File saveTo = new File(plugin.getDataFolder(), "Reboot-log.txt");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }
            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            Date dt = new Date();
            SimpleDateFormat df = new SimpleDateFormat("[dd-MM-yyyy] [HH:mm:ss]");
            String time = df.format(dt);
            pw.println(Util.stripColours(time + ": " + message));
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public Block getTargetNonAirBlock(Player player, int maxDistance) throws IllegalStateException
    {
        BlockIterator iterator = new BlockIterator(player.getLocation(), player.getEyeHeight(), maxDistance);
        Block result = player.getLocation().getBlock().getRelative(BlockFace.UP);
        while (iterator.hasNext())
        {
            result = iterator.next();
            if(result.getType() != Material.AIR) return result;
        }
        
        return result;
    }

}
