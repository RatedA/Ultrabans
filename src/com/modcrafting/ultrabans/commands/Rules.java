package com.modcrafting.ultrabans.commands;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.modcrafting.ultrabans.UltraBan;

public class Rules implements CommandExecutor{
	public static final Logger log = Logger.getLogger("Minecraft");
	UltraBan plugin;
	
	public Rules(UltraBan ultraBan) {
		this.plugin = ultraBan;
	}
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
		if(!plugin.useRules) return true;
		boolean auth = false;
		boolean set = false;
		Player player = null;
		if (sender instanceof Player){
			player = (Player)sender;
			if (plugin.setupPermissions()){
				if (plugin.permission.has(player, "ultraban.rules")) auth = true;
				if (plugin.permission.has(player, "ultraban.rules.set")) set = true;
			
			}else{
			 if (player.isOp()){
				 auth = true;
				 set = true;
			 }
			}
		}else{
			auth = true;
			set = true;
		}
			if(args.length < 1){
				if(auth){
					String ruleMsg = config.getString("messages.ruleMsg", "The Servers Rules Are As Follows!");
					sender.sendMessage(formatMessage(ruleMsg));
					String rule1 = config.getString("rules.one");
					if (rule1 != null) sender.sendMessage(formatMessage("1. " + rule1));
					String rule2 = config.getString("rules.two");
					if (rule2 != null) sender.sendMessage(formatMessage("2. " + rule2));
					String rule3 = config.getString("rules.three");
					if (rule3 != null) sender.sendMessage(formatMessage("3. " + rule3));
					String rule4 = config.getString("rules.four");
					if (rule4 != null) sender.sendMessage(formatMessage("4. " + rule4));
					String rule5 = config.getString("rules.five");
					if (rule5 != null) sender.sendMessage(formatMessage("5. " + rule5));
					String rule6 = config.getString("rules.six");
					if (rule6 != null) sender.sendMessage(formatMessage("6. " + rule6));
					String rule7 = config.getString("rules.seven");
					if (rule7 != null) sender.sendMessage(formatMessage("7. " + rule7));
					String rule8 = config.getString("rules.eight");
					if (rule8 != null) sender.sendMessage(formatMessage("8. " + rule8));
					String rule9 = config.getString("rules.nine");
					if (rule9 != null) sender.sendMessage(formatMessage("9. " + rule9));
					String rule10 = config.getString("rules.ten");
					if (rule10 != null) sender.sendMessage(formatMessage("10. " + rule10));
					return true;
				}
			}
			if(set){
				if(args[0].equalsIgnoreCase("help")){
					this.helpString(sender);
					return true;
				}
				if(args[0].equalsIgnoreCase("set")){
					if(args.length < 2){
						this.helpString(sender);
						return true;
					}
					if(args[1].equalsIgnoreCase("one")){
						String rule1 = combineSplit(2, args, " ");
						config.set("rules.one", (String) rule1);
					}
					if(args[1].equalsIgnoreCase("two")){
						String rule2 = combineSplit(2, args, " ");
						config.set("rules.two", (String) rule2);
					}
					if(args[1].equalsIgnoreCase("three")){
						String rule3 = combineSplit(2, args, " ");
						config.set("rules.three", (String) rule3);
					}
					if(args[1].equalsIgnoreCase("four")){
						String rule4 = combineSplit(2, args, " ");
						config.set("rules.four", (String) rule4);
					}
					if(args[1].equalsIgnoreCase("five")){
						String rule5 = combineSplit(2, args, " ");
						config.set("rules.five", (String) rule5);
					}
					if(args[1].equalsIgnoreCase("six")){
						String rule6 = combineSplit(2, args, " ");
						config.set("rules.six", (String) rule6);
					}
					if(args[1].equalsIgnoreCase("seven")){
						String rule7 = combineSplit(2, args, " ");
						config.set("rules.seven", (String) rule7);
					}
					if(args[1].equalsIgnoreCase("eight")){
						String rule8 = combineSplit(2, args, " ");
						config.set("rules.eight", (String) rule8);
					}
					if(args[1].equalsIgnoreCase("nine")){
						String rule9 = combineSplit(2, args, " ");
						config.set("rules.nine", (String) rule9);
					}
					if(args[1].equalsIgnoreCase("ten")){
						String rule10 = combineSplit(2, args, " ");
						config.set("rules.ten", (String) rule10);
					}
					sender.sendMessage(ChatColor.GRAY + "Rule Set.");
			        plugin.saveConfig();
			        return true;
				}
			}
		return false;
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
	public void helpString(CommandSender sender){
		sender.sendMessage(ChatColor.GRAY + "Rules and Setting Rules Help");
		sender.sendMessage(ChatColor.BLUE + "Required Info {}");
		sender.sendMessage(ChatColor.GRAY + "/rules - Displays rules.");
		sender.sendMessage(ChatColor.GRAY + "/rules set {one,two,three,four,... ten} {Message}");
		sender.sendMessage(ChatColor.GRAY + "ex: /rules set one No Grief.");
		sender.sendMessage(ChatColor.GRAY + "ex: /rules set seven No Swearing.");
		sender.sendMessage(ChatColor.GRAY + "displays: 1. No Grief.");
		sender.sendMessage(ChatColor.GRAY + "displays: 7. No Swearing.");
		sender.sendMessage(ChatColor.GRAY + "/rules help - Displays this help.");
	}

	public String formatMessage(String str){
		String funnyChar = new Character((char) 167).toString();
		str = str.replaceAll("&", funnyChar);
		return str;
	}
}
