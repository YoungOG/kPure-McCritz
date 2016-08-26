package com.mccritz.kpure.listeners;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

public class PinListener implements Listener {

    private ProfileManager pm = kPure.getInstance().getProfileManager();
    private HashSet<UUID> logged = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (p.hasPermission("kpure.pin")) {
                    if (!pm.getProfile(p.getUniqueId()).hasPin()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Profile profile = pm.getProfile(p.getUniqueId());

                                if (!profile.hasPin()) {
                                    MessageManager.sendMessage(p, "&cPlease setup your four digit PIN. /setpin ####");
                                } else {
                                    this.cancel();
                                }
                            }

                        }.runTaskTimer(kPure.getInstance(), 0, 5 * 20);
                    } else {
                        Profile profile = pm.getProfile(p.getUniqueId());

                        if (profile.getLastUsedIP() != null && profile.getLastUsedIP().equalsIgnoreCase(profile.getCurrentIP())) {
                            return;
                        }

                        logged.add(p.getUniqueId());

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (logged.contains(p.getUniqueId())) {
                                    MessageManager.sendMessage(p, "&7Please enter your PIN.");
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(kPure.getInstance(), 0, 5 * 20);
                    }
                }
            }
        }.runTaskLater(kPure.getInstance(), 5L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (p.hasPermission("kpure.pin")) {
            Profile profile = pm.getProfile(p.getUniqueId());

            if (!profile.hasPin() || logged.contains(p.getUniqueId())) {
                e.setTo(e.getFrom());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (p.hasPermission("kpure.pin")) {
            Profile profile = pm.getProfile(p.getUniqueId());

            if (!profile.hasPin() || logged.contains(p.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {

            Player p = (Player) e.getEntity();

            if (p.hasPermission("kpure.pin")) {
                Profile profile = pm.getProfile(p.getUniqueId());

                if (!profile.hasPin() || logged.contains(p.getUniqueId())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamage2(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();

            if (p.hasPermission("kpure.pin")) {
                Profile profile = pm.getProfile(p.getUniqueId());

                if (!profile.hasPin() || logged.contains(p.getUniqueId())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (p.hasPermission("kpure.pin")) {
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

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (p.hasPermission("kpure.pin")) {
                    if (!e.getMessage().toLowerCase().contains("/setpin")) {
                        Profile profile = pm.getProfile(p.getUniqueId());

                        if (!profile.hasPin()) {
                            e.setCancelled(true);
                            MessageManager.sendMessage(p, "&7You cannot use commands until you have entered your PIN.");
                        }
                    }

                    if (logged.contains(p.getUniqueId())) {
                        e.setCancelled(true);
                        MessageManager.sendMessage(p, "&7You cannot use commands until you have entered your PIN.");
                    }
                }
            }
        }.runTaskAsynchronously(kPure.getInstance());
    }

    public boolean isFourDigitCode(String string) {
        String regex = "[0-9]+";

        return string.length() == 4 && string.matches(regex);
    }
}
