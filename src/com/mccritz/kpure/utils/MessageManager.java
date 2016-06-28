package com.mccritz.kpure.utils;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mccritz.kpure.kPure;

public class MessageManager {

    public static void sendMessage(Player p, String message) {
	p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMessage(UUID id, String message) {
	if (Bukkit.getPlayer(id) != null) {
	    Bukkit.getPlayer(id).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
    }

    public static void sendMessage(CommandSender s, String message) {
	s.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void broadcast(String message) {
	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void broadcast(String permission, String message) {
	Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', message), permission);
    }

    public static void broadcast(List<UUID> list, String message) {
	for (UUID id : list) {
	    sendMessage(id, message);
	}
    }

    public static void sendStaffMessage(CommandSender sender, String message) {
	for (Player all : PlayerUtility.getOnlinePlayers()) {
	    if (all.hasPermission("pure.staffchat")) {
		all.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d" + sender.getName() + ": ") + message);
	    }
	}

	kPure.getInstance().getLogger().log(Level.INFO, "[Staff Message]: " + sender.getName() + ": " + message);
    }

    public static String PLAYER_NOT_FOUND(String name) {
	return "&c" + name + " &7could not be found.";
    }
}
