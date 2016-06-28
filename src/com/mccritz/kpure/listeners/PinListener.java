package com.mccritz.kpure.listeners;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.utils.MessageManager;

public class PinListener implements Listener {

    ProfileManager pm = kPure.getInstance().getProfileManager();
    private HashSet<UUID> logged = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e) {
	Player p = e.getPlayer();

	new BukkitRunnable() {
	    @Override
	    public void run() {
		if (p.hasPermission("kpure.pin")) {

		    if (pm.hasLoadedProfile(p.getUniqueId())) {
			Profile profile = pm.getProfile(p.getUniqueId());

			if (!profile.hasPin()) {
			    new BukkitRunnable() {
				@Override
				public void run() {
				    if (!profile.hasPin()) {
					MessageManager.sendMessage(p,
						"&cPlease setup your four digit PIN. /setpin ####");
				    }
				}
			    }.runTaskTimerAsynchronously(kPure.getInstance(), 0L, 5 * 20);
			} else {
			    logged.add(p.getUniqueId());

			    new BukkitRunnable() {
				@Override
				public void run() {
				    if (logged.contains(p.getUniqueId())) {
					MessageManager.sendMessage(p, "&7Please enter your PIN.");
				    }
				}
			    }.runTaskTimerAsynchronously(kPure.getInstance(), 5L, 5 * 20);
			}
		    }
		}
	    }
	}.runTaskLaterAsynchronously(kPure.getInstance(), 5L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
	Player p = e.getPlayer();

	if (p.hasPermission("kpure.pin")) {
	    if (pm.hasLoadedProfile(p.getUniqueId())) {
		Profile profile = pm.getProfile(p.getUniqueId());

		if (!profile.hasPin()) {
		    e.setTo(e.getFrom());
		}

		if (logged.contains(p.getUniqueId())) {
		    e.setTo(e.getFrom());
		}
	    }
	}
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
	Player p = e.getPlayer();

	if (p.hasPermission("kpure.pin")) {
	    if (pm.hasLoadedProfile(p.getUniqueId())) {
		Profile profile = pm.getProfile(p.getUniqueId());

		if (!profile.hasPin()) {
		    e.setCancelled(true);
		}

		if (logged.contains(p.getUniqueId())) {
		    e.setCancelled(true);
		}
	    }
	}
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
	if (e.getEntity() instanceof Player) {

	    Player p = (Player) e.getEntity();

	    if (p.hasPermission("kpure.pin")) {
		if (pm.hasLoadedProfile(p.getUniqueId())) {
		    Profile profile = pm.getProfile(p.getUniqueId());

		    if (!profile.hasPin()) {
			e.setCancelled(true);
		    }

		    if (logged.contains(p.getUniqueId())) {
			e.setCancelled(true);
		    }
		}
	    }
	}
    }

    @EventHandler
    public void onDamage2(EntityDamageByEntityEvent e) {
	if (e.getEntity() instanceof Player) {
	    Player p = (Player) e.getEntity();

	    if (p.hasPermission("kpure.pin")) {
		if (pm.hasLoadedProfile(p.getUniqueId())) {
		    Profile profile = pm.getProfile(p.getUniqueId());

		    if (!profile.hasPin()) {
			e.setCancelled(true);
		    }

		    if (logged.contains(p.getUniqueId())) {
			e.setCancelled(true);
		    }
		}
	    }
	}
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(PlayerChatEvent e) {
	Player p = e.getPlayer();

	if (p.hasPermission("kpure.pin")) {
	    if (pm.hasLoadedProfile(p.getUniqueId())) {
		Profile profile = pm.getProfile(p.getUniqueId());

		if (!profile.hasPin()) {
		    e.setCancelled(true);
		    MessageManager.sendMessage(p, "&7You cannot chat until you entered your PIN.");
		}

		if (logged.contains(p.getUniqueId())) {
		    e.setCancelled(true);

		    if (!isFourDigitCode(e.getMessage())) {
			MessageManager.sendMessage(p, "&4That PIN is incorrect. Please try again.");
			return;
		    }

		    if (!e.getMessage().equalsIgnoreCase(profile.getPin())) {
			MessageManager.sendMessage(p, "&4That PIN is incorrect. Please try again.");
			return;
		    }

		    logged.remove(p.getUniqueId());
		    MessageManager.sendMessage(p, "&7You have been successfully authenticated.");
		}
	    }
	}
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
	Player p = e.getPlayer();

	if (p.hasPermission("kpure.pin")) {
	    if (!e.getMessage().toLowerCase().contains("/setpin")) {
		if (pm.hasLoadedProfile(p.getUniqueId())) {
		    Profile profile = pm.getProfile(p.getUniqueId());

		    if (!profile.hasPin()) {
			e.setCancelled(true);
			MessageManager.sendMessage(p, "&7You cannot use commands until you have entered your PIN.");
		    }
		}
	    }

	    if (logged.contains(p.getUniqueId())) {
		e.setCancelled(true);
		MessageManager.sendMessage(p, "&7You cannot use commands until you have entered your PIN.");
	    }
	}
    }

    public boolean isFourDigitCode(String string) {
	String regex = "[0-9]+";

	return string.length() == 4 && string.matches(regex);
    }
}
