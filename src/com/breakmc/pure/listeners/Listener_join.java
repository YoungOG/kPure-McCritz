package com.breakmc.pure.listeners;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.DateUtil;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ru.tehkode.permissions.events.PermissionEntityEvent;

public class Listener_join implements Listener {

    private ProfileManager pm = Pure.getInstance().getProfileManager();
    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        e.setMaxPlayers(Pure.getInstance().getPlayerCount());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (p.getGameMode() != GameMode.SURVIVAL && !p.hasPermission("pure.login.bypass")) {
            MessageManager.broadcast("pure.login.broadcast", "&c&l" + p.getName() + " has logged in with " + p.getGameMode() + "! Investigate now!");
        }

        p.setWalkSpeed(0.2F);
        p.setGameMode(GameMode.SURVIVAL);

        if (p.isOp()) {
            if (!p.getUniqueId().toString().equalsIgnoreCase("70ced320-9c8a-4fc7-b4c6-ee9199e2a605") && !p.getUniqueId().toString().equalsIgnoreCase("28a0d5e8-e202-4178-b316-c2edb31b53c0")) {
                p.setOp(false);
                MessageManager.broadcast("pure.login.broadcast", "&c&l" + p.getName() + " has logged in with OP! Investigate now!");
            }
        }

        if (!pm.hasProfile(p.getUniqueId()) || !pm.hasLoadedProfile(p.getUniqueId())) {
            pm.createProfile(p, p.getAddress().getAddress().getHostAddress().replace("/", ""));
        } else if (pm.hasLoadedProfile(p.getUniqueId())) {
            Profile prof = pm.getProfile(p.getUniqueId());

            pm.getLoadedProfiles().remove(prof);
            pm.loadProfile(p.getUniqueId());
            System.out.println("Reloaded " + p.getName() + "'s profile!");

            new BukkitRunnable() {
                @Override
                public void run() {
                    Profile prof = pm.getProfile(p.getUniqueId());

                    pum.checkForValidAlts(prof.getUniqueID());
                    pum.checkForBannedAlts(prof.getUniqueID());

                    prof.setOnline(p.isOnline());
                    prof.setLogins(prof.getLogins() + 1);
                    prof.setGroup(PlayerUtility.getGroup(prof.getCurrentName()));

                    pm.saveProfile(prof);
                }
            }.runTaskLater(Pure.getInstance(), 5L);
        } else {
            System.out.println("Could not find/load " + p.getName() + "'s profile!");
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();

        if (Pure.getInstance().getPunishmentManager().isIPBanned(e.getAddress().getHostAddress().replace("/", ""))) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You are blacklisted\n\nThis punishment " + ChatColor.RED + "" + ChatColor.BOLD + "cannot" + ChatColor.RED + " be appealed.");
            return;
        }

        if (e.getPlayer() != null && pm.getProfile(p.getUniqueId()) != null) {
            Profile prof = pm.getProfile(e.getPlayer().getUniqueId());

            if (prof.isBanned()) {
                if (prof.isPermanentlyBanned()) {
                    e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You have been permanently banned.\n\nYou can appeal your ban on our website: " + ChatColor.AQUA + "www.BreakMC.com");
                }

                if (prof.isTemporarilyBanned()) {
                    if (System.currentTimeMillis() >= prof.getActiveTemporaryBan().getLength()) {
                        prof.getActiveTemporaryBan().setLength(0);
                        pm.reloadProfile(prof, true);
                        return;
                    }

                    e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You have been temporarily banned.\n" + DateUtil.formatDateDiff(prof.getActiveTemporaryBan().getLength()) + " remaining\n\nYou can appeal your ban on our website: " + ChatColor.AQUA + "www.BreakMC.com");
                }
            }
        }

        if (PlayerUtility.getOnlinePlayers().length >= Pure.getInstance().getPlayerCount() && !p.hasPermission("pure.joinfullserver")) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', "&b&nThe server is &cfull&b!\n\n&aDonate at www.BreakMC.com/store to join now!"));
            return;
        }

        new BukkitRunnable() {
            public void run() {
                if (e.getPlayer() != null && pm.getProfile(p.getUniqueId()) != null) {
                    Profile prof = pm.getProfile(e.getPlayer().getUniqueId());
                    prof.setCurrentName(p.getName());
                    prof.setCurrentIP(e.getAddress().getHostAddress().replace("/", ""));

                    pm.saveProfile(prof);
                }
            }
        }.runTaskAsynchronously(Pure.getInstance());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        Profile prof = pm.getProfile(p.getUniqueId());

        if (prof != null) {
            prof.setOnline(false);

            pm.saveProfile(prof);
        }
    }

    @EventHandler
    public void onRankChange(PermissionEntityEvent e) {
        if (e.getSourceUUID() != null) {
            Profile prof = pm.getProfile(e.getSourceUUID());
            prof.setGroup(PlayerUtility.getGroup(prof.getCurrentName()));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        Profile prof = pm.getProfile(p.getUniqueId());

        if (prof.isMuted()) {
            e.setCancelled(true);

            if (prof.isPermanentlyMuted()) {
                MessageManager.sendMessage(p, "&cYou are permanently muted.");
            }

            if (prof.isTemporarilyMuted()) {
                if (System.currentTimeMillis() >= prof.getActiveTemporaryMute().getLength()) {
                    prof.getActiveTemporaryMute().setLength(0);
                    return;
                }

                MessageManager.sendMessage(p, "&cYou are temporarily muted for &7" + DateUtil.formatDateDiff(prof.getActiveTemporaryMute().getLength()));
            }
        }
    }
}