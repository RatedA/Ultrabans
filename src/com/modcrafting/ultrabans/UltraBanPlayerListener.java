
package com.modcrafting.ultrabans;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class UltraBanPlayerListener implements Listener{
	public static final Logger log = Logger.getLogger("Minecraft");
	UltraBan plugin;
	public UltraBanPlayerListener(UltraBan ultraBans) {
		this.plugin = ultraBans;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event){
		YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
		Player player = event.getPlayer();
		if(plugin.bannedPlayers.contains(player.getName().toLowerCase())){
			System.out.println("banned player joined");
			String reason = plugin.db.getBanReason(player.getName().toLowerCase());
			String adminMsg = "You've been banned for: " + reason;
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, adminMsg);
		}
		if(plugin.tempBans.get(player.getName().toLowerCase()) != null){
			long tempTime = plugin.tempBans.get(player.getName().toLowerCase());
			long now = System.currentTimeMillis()/1000;
			long diff = tempTime - now;
			if(diff <= 0){
				plugin.tempBans.remove(player.getName().toLowerCase());
				plugin.bannedPlayers.remove(player.getName().toLowerCase());
				plugin.db.removeFromBanlist(player.getName().toLowerCase());
				return;
			}
			Date date = new Date();
			date.setTime(tempTime*1000);
			String dateStr = date.toString();
			String reason = plugin.db.getBanReason(player.getName());
			String adminMsg = "You've been tempbanned for " + reason + " Remaining:" + dateStr;
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, adminMsg);
			return;
		}
		boolean lock = config.getBoolean("lockdown", false);
		if(lock){
			boolean auth = false;
			String lockMsgLogin = config.getString("messages.lockMsgLogin", "Server is under a lockdown, Try again later!");
			if (plugin.setupPermissions()){
				if (plugin.permission.has(player, "ultraban.lockdown.override")) auth = true;
			}else{
			 if (player.isOp()) auth = true; //defaulting to Op if no vault doesn't take or node
			}
			
			if (!auth) event.disallow(PlayerLoginEvent.Result.KICK_OTHER, lockMsgLogin);
			UltraBan.log.log(Level.INFO,"[UltraBan] " + player.getName() + " attempted to join during lockdown.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event){
		YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
		Player player = event.getPlayer();
		String ip = player.getAddress().getAddress().getHostAddress();
		plugin.db.setAddress(player.getName().toLowerCase(), ip);
		System.out.println("[UltraBan] Logged " + player.getName() + " connecting from ip:" + ip);
		
		
		if(plugin.bannedIPs.contains(ip)){
			System.out.println("[UltraBan] Banned player attempted Login!");
			event.setJoinMessage(null);
			String adminMsg = config.getString("messages.LoginIPBan", "Your IP is banned!");
			player.kickPlayer(adminMsg);
		}
		boolean logon = config.getBoolean("LogonInfo", true);
		if(logon){
		//Validate IP / Proxy Check
		//Thanks Havoc :)
			try {
				InetAddress InetP = InetAddress.getByName(ip);
				String hostAddress = InetP.getHostAddress();
				String hostName = InetP.getHostName();
				Player[] pll = plugin.getServer().getOnlinePlayers();
				for (int iii=0; iii<pll.length; iii++){
					if(pll.length > 0){
    					if (plugin.setupPermissions()){
    						if (plugin.permission.has(pll[iii], "ultraban.admin")){
    							pll[iii].sendMessage(ChatColor.BLUE + "[UltraBans]: " + ChatColor.GRAY + player.getName() + ChatColor.DARK_GRAY + " Connected.");
    							pll[iii].sendMessage(ChatColor.DARK_GRAY + "Host Address: " + ChatColor.GRAY + hostAddress);
    							pll[iii].sendMessage(ChatColor.DARK_GRAY + "Host Name: " + ChatColor.GRAY + hostName);
    						}
    					}
					}	
    			}	
    		//may cause lag when verifying ip
    		//will autoboot when unreachable.
				Boolean pingCheck = config.getBoolean("PingCheck", false);
				if (pingCheck){
					try {
						Boolean host = InetP.isReachable(config.getInt("HostTimeOut", 1800));
						if (!host){
							player.kickPlayer("Destination Host Is Unreachable, Host Timed Out.");
							for (int iiii=0; iiii<pll.length; iiii++){
								if(pll.length > 0){
									if (plugin.setupPermissions()){
										if (plugin.permission.has(pll[iiii], "ultraban.admin")){
											pll[iiii].sendMessage(ChatColor.GOLD + player.getName() + " attempted to join from proxy.");
											pll[iiii].sendMessage(ChatColor.YELLOW + "Host Address: " + hostAddress);
											pll[iiii].sendMessage(ChatColor.YELLOW + "Host Name: " + hostName);
											pll[iiii].sendMessage(ChatColor.YELLOW + "IP Address: " + ip);
										}
									}
								}
							}
						}
    			
					} catch (IOException e) {
						UltraBan.log.log(Level.INFO, "Host Name Doesn't Exist. Removed Player: " + player.getName());
						player.kickPlayer("Destination Host Is Unreachable, IP handshake failed.");
						e.printStackTrace();
					}
				}	
			} catch (UnknownHostException e) {
				UltraBan.log.log(Level.INFO, "Host Name Doesn't Exist. Removed Player: " + player.getName());
				player.kickPlayer("Destination Host Is Unreachable, IP handshake failed.");
			}
		}
		//Duplicate IP check and display
		Player[] pl = plugin.getServer().getOnlinePlayers();
		for (int i=0; i<pl.length; i++){
			String name = pl[i].getAddress().getAddress().getHostAddress();
	        if (name == ip){
	    		Player[] pll = plugin.getServer().getOnlinePlayers();
	    		for (int ii=0; ii<pll.length; ii++){
	    			if (plugin.setupPermissions()){
	    				if (plugin.permission.has(pll[ii], "ultraban.checkip")){
	    					pll[ii].sendMessage(ChatColor.YELLOW + "Duplicate IP Login detected: " + ChatColor.GOLD + pl[i] + ChatColor.YELLOW + " and " + ChatColor.GOLD + player.getName());
	    					pll[ii].sendMessage(ChatColor.YELLOW + "IP Address: " + name);
	    				}
	    			}
	    		}
	    		boolean dupePolicy = config.getBoolean("dupePolicy", false);
	    		if(dupePolicy){
	    			String adminMsg = config.getString("messages.DupeIPKick", "Duplicate IP addresses disabled.");
	    			player.kickPlayer(adminMsg);
	    		}
	    	}
	    		
	    }
		
		if(!plugin.db.matchAddress(player.getName(), ip)){
			plugin.db.updateAddress(player.getName(), ip);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
		Player player = event.getPlayer();
			if(plugin.jailed.contains(player.getName().toLowerCase())){
				String adminMsg = config.getString("messages.jailCmdMsg", "You cannot use commands while Jailed!");
				player.sendMessage(ChatColor.GRAY + adminMsg);
				event.setCancelled(true);
			 }
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(PlayerChatEvent event){
		YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
		 Player player = event.getPlayer();
		 	if(plugin.muted.contains(player.getName().toLowerCase())){
				String adminMsg = config.getString("messages.muteChatMsg", "Your cry falls on deaf ears.");
		 		player.sendMessage(ChatColor.GRAY + adminMsg);
		 		event.setCancelled(true);
		 	}
		 	if(plugin.jailed.contains(player.getName().toLowerCase())){
				String adminMsg = config.getString("messages.jailChatMsg", "Your cry falls on deaf ears.");
		 		player.sendMessage(ChatColor.GRAY + adminMsg);
		 		event.setCancelled(true);
		 	}
	}
		 
}
