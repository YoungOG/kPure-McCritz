package com.mccritz.kpure.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.utils.MessageManager;

public class JoinListener implements Listener {

    private ProfileManager pm = kPure.getInstance().getProfileManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
	Player p = e.getPlayer();

	if (p.getGameMode() != GameMode.SURVIVAL && !p.hasPermission("kpure.gamemode.allow")) {
	    MessageManager.broadcast("kpure.gamemode.alert", "&7&l[&4&lkPure&7&l] &c" + p.getName()
		    + " &7&llogged in with creative. Cancelling and bringing back to &csurvival&7&l.");
	    p.setGameMode(GameMode.SURVIVAL);
	}

	p.setGameMode(GameMode.SURVIVAL);

	Profile result = pm.getProfile(p.getUniqueId());

	if (result != null) {
	    System.out.println("Loading " + p.getName() + "'s profile!");
	    result.setLogins(result.getLogins() + 1);
	    result.setGroup("disabled");
	    pm.saveProfile(result);
	} else {
	    System.out.println("Creating " + p.getName() + "'s profile!");
	    pm.createProfile(p, p.getAddress().getAddress().getHostAddress().replace("/", ""));
	}
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
	Player p = e.getPlayer();

	if (kPure.getInstance().getPunishmentManager().isIPBanned(e.getAddress().getHostAddress().replace("/", ""))) {
	    e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED
		    + "Your account has been blacklisted from McCritZ.\nYou cannot purchase an unban while blacklisted.");
	    return;
	}

	if (p != null) {
	    Profile result = pm.getProfile(p.getUniqueId());

	    if (result != null) {
		if (result.isBanned()) {
		    e.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED
			    + "You have been banned from McCritZ.\nYou can purchase an unban at store.mccritz.com");
		} else {
		    result.setCurrentName(p.getName());
		    result.getNameList().add(p.getName());

		    result.setCurrentIP(e.getAddress().getHostAddress().replace("/", ""));
		    result.getIpList().add(e.getAddress().getHostAddress().replace("/", ""));
		    pm.saveProfile(result);
		}
	    }
	}
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
	Player p = e.getPlayer();
	Profile prof = pm.getProfile(p.getUniqueId());

	if (prof.isMuted()) {
	    e.setCancelled(true);

	    if (prof.isPermanentlyMuted()) {
		MessageManager.sendMessage(p, "&7You are currently silenced.");
	    }

	    if (prof.isTemporarilyMuted()) {
		if (System.currentTimeMillis() >= prof.getActiveTemporaryMute().getLength()) {
		    prof.getActiveTemporaryMute().setLength(0);
		    pm.saveProfile(prof);
		    return;
		}

		MessageManager.sendMessage(p, "&7You are currently silenced.");
	    }
	}
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
	Player p = e.getPlayer();

	if ((e.getNewGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SURVIVAL))
		&& !p.hasPermission("kpure.gamemode.allow")) {
	    MessageManager.broadcast("kpure.gamemode.alert", "&7&l[&4&lkPure&7&l] &c" + p.getName()
		    + " &7&lwas put into creative. Cancelling and bringing back to &csurvival&7&l.");
	    e.setCancelled(true);
	}
    }
}