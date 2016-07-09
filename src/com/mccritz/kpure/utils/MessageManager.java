package com.mccritz.kpure.utils;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageManager {

    public static void sendMessage(Player p, String message) {
	p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMessage(UUID id, String message) {
	Player p = Bukkit.getPlayer(id);

	if (p != null) {
	    p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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

    public static String PLAYER_NOT_FOUND(String name) {
	return "&c" + name + " &7could not be found.";
    }
}
