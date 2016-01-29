package com.breakmc.pure.listeners;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

public class PinListener implements Listener {

    ProfileManager pm = Pure.getInstance().getProfileManager();
    private HashSet<UUID> logged = new HashSet<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (p.hasPermission("pure.pin")) {
            if (pm.hasLoadedProfile(p.getUniqueId())) {
                Profile profile = pm.getProfile(p.getUniqueId());

                if (!profile.hasPin()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!profile.hasPin()) {
                                MessageManager.sendMessage(p, "&cPlease setup a 4 digit PIN. /setpin ####");
                            }
                        }
                    }.runTaskTimerAsynchronously(Pure.getInstance(), 0L, 5 * 20);
                } else {
                    logged.add(p.getUniqueId());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (logged.contains(p.getUniqueId())) {
                                MessageManager.sendMessage(p, "&cPlease enter your 4 digit PIN.");
                            }
                        }
                    }.runTaskTimerAsynchronously(Pure.getInstance(), 0L, 5 * 20);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (p.hasPermission("pure.pin")) {
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

        if (p.hasPermission("pure.pin")) {
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

            if (p.hasPermission("pure.pin")) {
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

            if (p.hasPermission("pure.pin")) {
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

        if (p.hasPermission("pure.pin")) {
            if (pm.hasLoadedProfile(p.getUniqueId())) {
                Profile profile = pm.getProfile(p.getUniqueId());

                if (!profile.hasPin()) {
                    e.setCancelled(true);
                    MessageManager.sendMessage(p, "&cYou cannot use chat until you set your PIN!");
                }

                if (logged.contains(p.getUniqueId())) {
                    e.setCancelled(true);

                    if (!isFourDigitCode(e.getMessage())) {
                        MessageManager.sendMessage(p, "&cThat PIN is invalid!");
                        return;
                    }

                    if (!e.getMessage().equalsIgnoreCase(profile.getPin())) {
                        MessageManager.sendMessage(p, "&cThat PIN is invalid!");
                        return;
                    }

                    logged.remove(p.getUniqueId());
                    MessageManager.sendMessage(p, "&aPIN confirmed! Thank you.");
                }
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        if (p.hasPermission("pure.pin")) {
            if (!e.getMessage().toLowerCase().contains("/setpin")) {
                if (pm.hasLoadedProfile(p.getUniqueId())) {
                    Profile profile = pm.getProfile(p.getUniqueId());

                    if (!profile.hasPin()) {
                        e.setCancelled(true);
                        MessageManager.sendMessage(p, "&cYou cannot use commands until you set your PIN!");
                    }
                }
            }

            if (logged.contains(p.getUniqueId())) {
                e.setCancelled(true);
                MessageManager.sendMessage(p, "&cYou cannot use commands until you confirm your PIN!");
            }
        }
    }

    public boolean isFourDigitCode(String string) {
        String regex = "[0-9]+";

        return (string.length() == 4 && string.matches(regex));
    }
}
