package com.mccritz.kpure.listeners;

import com.mccritz.kperms.kPerms;
import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.punishment.PunishmentManager;
import com.mccritz.kpure.utils.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class JoinListener implements Listener {

    private ProfileManager profileManager = kPure.getInstance().getProfileManager();
    private PunishmentManager punishmentManager = kPure.getInstance().getPunishmentManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (p.getGameMode() != GameMode.SURVIVAL && !p.hasPermission("kpure.gamemode.allow")) {
            MessageManager.broadcast("kpure.gamemode.alert", "&7&l[&4&lkPure&7&l] &c" + p.getName() + " &7&llogged in with creative. Cancelling and bringing back to &csurvival&7&l.");
            p.setGameMode(GameMode.SURVIVAL);
        }

        p.setGameMode(GameMode.SURVIVAL);

        Profile result = profileManager.getProfile(p.getUniqueId());
        com.mccritz.kperms.profiles.Profile pProfile = kPerms.getInstance().getProfileManager().getProfile(p.getUniqueId());

        if (result != null) {
            System.out.println("Loading " + p.getName() + "'s profile!");
            result.setLogins(result.getLogins() + 1);
            result.setGroup(pProfile != null ? pProfile.getRank().getName() : "None");
            result.setCurrentName(p.getName());

            profileManager.saveProfile(result);
        } else {
            System.out.println("Creating " + p.getName() + "'s profile!");
            profileManager.createProfile(p, p.getAddress().getAddress().getHostAddress().replace("/", ""));
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();

        if (punishmentManager.isForceBans()) {
            if (kPure.getInstance().getPunishmentManager().isIPBanned(e.getAddress().getHostAddress().replace("/", ""))) {
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Your account has been blacklisted from McCritZ.\nYou cannot purchase an unban while blacklisted.");
                return;
            }

            if (p != null) {
                Profile result = profileManager.getProfile(p.getUniqueId());

                if (result != null) {
                    if (result.isBanned()) {
                        e.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You have been banned from McCritZ.\nYou can purchase an unban at store.mccritz.com");
                        result.setCurrentName(p.getName());
                    } else {
                        result.setCurrentName(p.getName());
                        result.setCurrentIP(e.getAddress().getHostAddress().replace("/", ""));
                        result.setLastUsedIP(e.getAddress().getHostAddress().replace("/", ""));

                        result.getNameList().add(p.getName());
                        result.getIpList().add(e.getAddress().getHostAddress().replace("/", ""));

                        kPure.getInstance().getPunishmentManager().checkForValidAlts(result.getUniqueID());
                        kPure.getInstance().getPunishmentManager().checkForBannedAlts(result.getUniqueID());

                        profileManager.saveProfile(result);
                    }
                }
            }
        } else {
            if (p != null) {
                Profile result = profileManager.getProfile(p.getUniqueId());

                if (result != null) {
                    result.setCurrentName(p.getName());
                    result.setCurrentIP(e.getAddress().getHostAddress().replace("/", ""));
                    result.setLastUsedIP(e.getAddress().getHostAddress().replace("/", ""));

                    result.getNameList().add(p.getName());
                    result.getIpList().add(e.getAddress().getHostAddress().replace("/", ""));

                    kPure.getInstance().getPunishmentManager().checkForValidAlts(result.getUniqueID());
                    kPure.getInstance().getPunishmentManager().checkForBannedAlts(result.getUniqueID());

                    profileManager.saveProfile(result);
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        Profile prof = profileManager.getProfile(p.getUniqueId());

        if (prof.isMuted()) {
            e.setCancelled(true);

            if (prof.isPermanentlyMuted()) {
                MessageManager.sendMessage(p, "&7You are currently silenced.");
            }

            if (prof.isTemporarilyMuted()) {
                if (System.currentTimeMillis() >= prof.getActiveTemporaryMute().getLength()) {
                    prof.getActiveTemporaryMute().setLength(0);
                    profileManager.saveProfile(prof);
                    return;
                }

                MessageManager.sendMessage(p, "&7You are currently silenced.");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Profile profile = profileManager.getProfile(p.getUniqueId());

        profile.setLastUsedIP(profile.getCurrentIP());
        profileManager.saveProfile(profile);
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
        Player p = e.getPlayer();

        if ((e.getNewGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SURVIVAL)) && !p.hasPermission("kpure.gamemode.allow")) {
            MessageManager.broadcast("kpure.gamemode.alert", "&7&l[&4&lkPure&7&l] &c" + p.getName() + " &7&lwas put into creative. Cancelling and bringing back to &csurvival&7&l.");
            e.setCancelled(true);
        }
    }
}