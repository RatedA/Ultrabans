package com.modcrafting.ultrabans.commands;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.modcrafting.ultrabans.UltraBan;

public class Warn implements CommandExecutor{
	public static final Logger log = Logger.getLogger("Minecraft");
	UltraBan plugin;
	
	public Warn(UltraBan ultraBan) {
		this.plugin = ultraBan;
	}
	public boolean autoComplete;
	public String expandName(String p) {
		int m = 0;
		String Result = "";
		for (int n = 0; n < plugin.getServer().getOnlinePlayers().length; n++) {
			String str = plugin.getServer().getOnlinePlayers()[n].getName();
			if (str.matches("(?i).*" + p + ".*")) {
				m++;
				Result = str;
				if(m==2) {
					return null;
				}
			}
			if (str.equalsIgnoreCase(p))
				return str;
		}
		if (m == 1)
			return Result;
		if (m > 1) {
			return null;
		}
		if (m < 1) {
			return p;
		}
		return p;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
		if(!plugin.useWarn) return true;
		boolean auth = false;
		Player player = null;
		String admin = config.getString("defAdminName", "server");
		if (sender instanceof Player){
			player = (Player)sender;
			if (plugin.setupPermissions()){
				if (plugin.permission.has(player, "ultraban.warn")) auth = true;
			}else{
			 if (player.isOp()) auth = true; //defaulting to Op if no vault doesn't take or node
			}
			admin = player.getName();
		}else{
			auth = true; //if sender is not a player - Console
		}
		if (!auth){
			sender.sendMessage(ChatColor.RED + "You do not have the required permissions.");
			return true;
		}
		// Has enough arguments?
		if (args.length < 1) return false;

		String p = args[0]; // Get the victim's name from the command
		if(autoComplete)
			p = expandName(p); //If the admin has chosen to do so, autocomplete the name!
		Player victim = plugin.getServer().getPlayer(p); // What player is really the victim?
		// Reason stuff
		String reason = config.getString("defReason", "not sure");
		
		boolean broadcast = true;
		if(args.length > 1){
			if(args[1].equalsIgnoreCase("-s")){
				broadcast = false;
				reason = combineSplit(2, args, " ");
			}else
				reason = combineSplit(1, args, " ");
			
		}
		if(victim != null){
			plugin.db.addPlayer(victim.getName(), reason, admin, 0, 2);
			log.log(Level.INFO, "[UltraBan] " + admin + " warned player " + victim.getName() + ".");
			if(broadcast){ 
				String warnMsgBroadcast = config.getString("messages.warnMsgBroadcast", "%victim% was warned by %admin%. Reason: %reason%");
				warnMsgBroadcast = warnMsgBroadcast.replaceAll("%admin%", admin);
				warnMsgBroadcast = warnMsgBroadcast.replaceAll("%reason%", reason);
				warnMsgBroadcast = warnMsgBroadcast.replaceAll("%victim%", victim.getName());
				plugin.getServer().broadcastMessage(formatMessage(warnMsgBroadcast));
				return true;
			}else{
					String warnMsgVictim = config.getString("messages.warnMsgVictim", "You have been warned by %admin%. Reason: %reason%");
					warnMsgVictim = warnMsgVictim.replaceAll("%admin%", admin);
					warnMsgVictim = warnMsgVictim.replaceAll("%reason%", reason);
					String warnMsgBroadcast = config.getString("messages.warnMsgBroadcast", "%victim% was warned by %admin%. Reason: %reason%");
					warnMsgBroadcast = warnMsgBroadcast.replaceAll("%admin%", admin);
					warnMsgBroadcast = warnMsgBroadcast.replaceAll("%reason%", reason);
					warnMsgBroadcast = warnMsgBroadcast.replaceAll("%victim%", victim.getName());
					sender.sendMessage(formatMessage(":S:" + warnMsgBroadcast));
				return true;
				
			}	
		}else{
			plugin.db.addPlayer(p, reason, admin, 0, 2);
			log.log(Level.INFO, "[UltraBan] " + admin + " warned player " + p + ".");
			if(broadcast){ 
				String warnMsgBroadcast = config.getString("messages.warnMsgBroadcast", "%victim% was warned by %admin%. Reason: %reason%");
				warnMsgBroadcast = warnMsgBroadcast.replaceAll("%admin%", admin);
				warnMsgBroadcast = warnMsgBroadcast.replaceAll("%reason%", reason);
				warnMsgBroadcast = warnMsgBroadcast.replaceAll("%victim%", p);
				plugin.getServer().broadcastMessage(formatMessage(warnMsgBroadcast));
				return true;
			}else{
					String warnMsgVictim = config.getString("messages.warnMsgVictim", "You have been warned by %admin%. Reason: %reason%");
					warnMsgVictim = warnMsgVictim.replaceAll("%admin%", admin);
					warnMsgVictim = warnMsgVictim.replaceAll("%reason%", reason);
					String warnMsgBroadcast = config.getString("messages.warnMsgBroadcast", "%victim% was warned by %admin%. Reason: %reason%");
					warnMsgBroadcast = warnMsgBroadcast.replaceAll("%admin%", admin);
					warnMsgBroadcast = warnMsgBroadcast.replaceAll("%reason%", reason);
					warnMsgBroadcast = warnMsgBroadcast.replaceAll("%victim%", p);
					sender.sendMessage(formatMessage(":S:" + warnMsgBroadcast));
				return true;
			}
		}
	}
	
	public String combineSplit(int startIndex, String[] string, String seperator) {
		StringBuilder builder = new StringBuilder();

		for (int i = startIndex; i < string.length; i++) {
			builder.append(string[i]);
			builder.append(seperator);
		}

		builder.deleteCharAt(builder.length() - seperator.length()); // remove
		return builder.toString();
	}
	public String formatMessage(String str){
		String funnyChar = new Character((char) 167).toString();
		str = str.replaceAll("&", funnyChar);
		return str;
	}
}
