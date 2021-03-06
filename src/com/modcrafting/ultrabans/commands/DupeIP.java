package com.modcrafting.ultrabans.commands;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.modcrafting.ultrabans.UltraBan;

public class DupeIP implements CommandExecutor{
	public static final Logger log = Logger.getLogger("Minecraft");
	UltraBan plugin;
	
	public DupeIP(UltraBan ultraBan) {
		this.plugin = ultraBan;
	
	}
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
		boolean auth = false;
		Player player = null;
		if (sender instanceof Player){
			player = (Player)sender;
			if (plugin.setupPermissions()){
				if (plugin.permission.has(player, "ultraban.checkip")) auth = true;
			}else{
			 if (player.isOp()) auth = true; 
			 }
		}else{
			auth = true; //if sender is not a player - Console
		}
		if (!auth){
			sender.sendMessage(ChatColor.RED + "You do not have the required permissions.");
			return true;
		}else{
		if (args.length < 1) return false;
		String p = args[0];

		if(p.isEmpty()){
			sender.sendMessage(ChatColor.RED + "Unable to view ip for " + p + " !");
			return true;
		}
		p = expandName(p); 
		String ip = plugin.getServer().getPlayer(p.toLowerCase()).getAddress().getAddress().getHostAddress();
		if(ip.isEmpty()){
			sender.sendMessage(ChatColor.RED + "Unable to view ip for " + p + " !");
			return true;
		}
		Player[] pl = plugin.getServer().getOnlinePlayers();
		sender.sendMessage(ChatColor.AQUA + "Scanning Current IP of " + p + ": " + ip + " !");
		for (int i=0; i<pl.length; i++){
			String name = pl[i].getAddress().getAddress().getHostAddress();
	        if (name == ip){
	        	sender.sendMessage(ChatColor.GRAY + "Player: " + pl[i] + " duplicates player: " + p + "!");
	        }
		}
		return true;
		}
	}
}
