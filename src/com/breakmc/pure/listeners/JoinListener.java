package com.breakmc.pure.listeners;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.profile.ProfileRequest;
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
import ru.tehkode.permissions.events.PermissionEntityEvent;

public class JoinListener implements Listener {

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

        pm.requestProfile(p.getUniqueId(), new ProfileRequest<Profile>() {
            @Override
            public void onComplete(Profile result, Throwable throwable) {
                if (throwable != null || result == null) {
                    if (throwable != null)
                        throwable.printStackTrace();

                    System.out.println("Creating " + p.getName() + "'s profile!");
                    pm.createProfile(p, p.getAddress().getAddress().getHostAddress().replace("/", ""));
                } else {
                    System.out.println("Loading " + p.getName() + "'s profile!");
                    pm.loadProfile(result, true);

                    result.setOnline(p.isOnline());
                    result.setLogins(result.getLogins() + 1);
                    result.setGroup(PlayerUtility.getGroup(result.getCurrentName()));
                    result.saveProfileData();
                }
            }
        });
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();

        if (Pure.getInstance().getPunishmentManager().isIPBanned(e.getAddress().getHostAddress().replace("/", ""))) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You are blacklisted\n\nThis punishment " + ChatColor.RED + "" + ChatColor.BOLD + "cannot" + ChatColor.RED + " be appealed.");
            return;
        }

        if (PlayerUtility.getOnlinePlayers().length >= Pure.getInstance().getPlayerCount() && !p.hasPermission("pure.joinfullserver")) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', "&b&nThe server is &cfull&b!\n\n&aDonate at www.BreakMC.com/store to join now!"));
        }

        if (e.getPlayer() != null) {
            pm.requestProfile(e.getPlayer().getUniqueId(), new ProfileRequest<Profile>() {
                @Override
                public void onComplete(Profile result, Throwable throwable) {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else {
                        if (result != null) {
                            result.setCurrentName(p.getName());
                            result.setCurrentIP(e.getAddress().getHostAddress().replace("/", ""));
                            result.saveProfileData();
                        }
                    }
                }
            });
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        Profile prof = pm.getProfile(p.getUniqueId());

        if (prof != null) {
            prof.setOnline(false);
            prof.saveProfileData();
        }

        pm.getLoadedProfiles().remove(prof);
    }

    @EventHandler
    public void onRankChange(PermissionEntityEvent e) {
        if (e.getSourceUUID() != null) {
            Profile prof = pm.getProfile(e.getSourceUUID());
            prof.setGroup(PlayerUtility.getGroup(prof.getCurrentName()));
            prof.saveProfileData();
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        Profile prof = pm.getProfile(p.getUniqueId());
        prof.saveProfileData();

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