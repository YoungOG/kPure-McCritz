package com.breakmc.pure.punishment;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.punishment.punishments.*;
import com.breakmc.pure.utils.DateUtil;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.SpecialPlayerInventory;
import com.breakmc.pure.utils.database.DatabaseManager;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PunishmentManager {

    private ProfileManager pm = Pure.getInstance().getProfileManager();
    private DBCollection bCollection = DatabaseManager.getInstance().getCollection("ipbans");
    private DBCollection pCollection = DatabaseManager.getInstance().getCollection("profiles");
    public final List<IPBan> ipBanList = new ArrayList<>();
    private List<UUID> frozen = new ArrayList<>();
    private HashSet<UUID> vanished = new HashSet<>();
    private final Map<UUID, SpecialPlayerInventory> inventories = new HashMap<>();
    private boolean isChatSlowed = false;
    private boolean isChatMuted = false;
    private boolean isServerFrozen = false;

    public PunishmentManager() {
        DBCursor dbc = bCollection.find();

        while (dbc.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc.next();

            ipBanList.add(new IPBan(dbo.getString("address"), (dbo.getString("punisherUUID") != null ? UUID.fromString(dbo.getString("punisherUUID")) : null), dbo.getString("reason"), dbo.getString("dateIssued"), dbo.getBoolean("isActive")));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID id : frozen) {
            MessageManager.sendMessage(id, "&f\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
            MessageManager.sendMessage(id, "&f\u2588\u2588\u2588\u2588&c\u2588&f\u2588\u2588\u2588\u2588");
            MessageManager.sendMessage(id, "&f\u2588\u2588\u2588&c\u2588&0\u2588&c\u2588&f\u2588\u2588\u2588");
            MessageManager.sendMessage(id, "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588");
            MessageManager.sendMessage(id, "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588 &eYou have been frozen by a BreakMC staff member!");
            MessageManager.sendMessage(id, "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588 &eIf you disconnect you will be &4&lBANNED!");
            MessageManager.sendMessage(id, "&f\u2588&c\u2588&6\u2588\u2588\u2588&6\u2588\u2588&c\u2588&f\u2588 &ePlease connect to our TS: &cts.breakmc.com!");
            MessageManager.sendMessage(id, "&c\u2588&6\u2588\u2588\u2588&0\u2588&6\u2588\u2588\u2588&c\u2588");
            MessageManager.sendMessage(id, "&c\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
            MessageManager.sendMessage(id, "&f\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
                }
            }
        }.runTaskTimerAsynchronously(Pure.getInstance(), 0, 10*20L);
    }

    public void permanentlyBan(CommandSender sender, UUID punished, String reason) {
        Profile prof = pm.getProfile(punished);
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (prof != null) {
            if (prof.isBanned()) {
                MessageManager.sendMessage(sender, "&cPlayer \"" + prof.getCurrentName() + "\" is already banned.");
                return;
            }

            prof.getPermanentBans().add(new PermanentBan(punisher, reason, DateUtil.getProperDate(new Date()), true));

            if (prof.isOnline()) {
                Bukkit.getPlayer(prof.getUniqueID()).kickPlayer(ChatColor.RED + "You have been permanently banned.n\nYou can appeal your ban on our website " + ChatColor.AQUA + "www.BreakMC.com");
            }

            pm.reloadProfile(prof, true);

            MessageManager.broadcast("&a" + prof.getCurrentName() + " has been permanently banned by " + sender.getName() + ".");
        }
    }

    public void temporarilyBan(CommandSender sender, UUID punished, long length, String reason) {
        Profile prof = pm.getProfile(punished);
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (prof != null) {
            if (prof.isBanned()) {
                MessageManager.sendMessage(sender, "&cPlayer \"" + prof.getCurrentName() + "\" is already banned.");
                return;
            }

            prof.getTemporaryBans().add(new TemporaryBan(punisher, length, reason, DateUtil.getProperDate(new Date())));

            if (prof.isOnline()) {
                Bukkit.getPlayer(prof.getUniqueID()).kickPlayer(ChatColor.RED + "You have been temporarily banned.\n" + DateUtil.formatDateDiff(prof.getActiveTemporaryBan().getLength()) + " remaining.\n\nYou can not appeal a temporary ban.");
            }

            pm.reloadProfile(prof, true);

            MessageManager.broadcast("&a" + prof.getCurrentName() + " has been temporarily banned by " + sender.getName() + ".");
        }
    }

    public void banIP(CommandSender sender, String address, String reason) {
        if (isIPBanned(address)) {
            MessageManager.sendMessage(sender, "&cIP \"" + address + "\" is already banned.");
            return;
        }

        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        ipBanList.add(new IPBan(address, punisher, reason, DateUtil.getProperDate(new Date()), true));

        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (all.getAddress().getAddress().getHostAddress().replace("[", "").replace("]", "").equalsIgnoreCase(address)) {
                all.kickPlayer(ChatColor.RED + "You are blacklisted.\n\nThis punishment " + ChatColor.RED + "" + ChatColor.BOLD + "cannot" + ChatColor.RED + " be appealed.");
            }
        }

        MessageManager.broadcast("pure.banip", "&a" + address + " has been blacklisted by " + sender.getName() + ".");
    }

    public void permanentlyMute(CommandSender sender, UUID punished, String reason) {
        Profile prof = pm.getProfile(punished);
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (prof != null) {
            if (prof.isBanned()) {
                MessageManager.sendMessage(sender, "&cPlayer \"" + prof.getCurrentName() + "\" is already muted.");
                return;
            }

            prof.getPermanentMutes().add(new PermanentMute(punisher, reason, DateUtil.getProperDate(new Date()), true));

            if (prof.isOnline()) {
                MessageManager.sendMessage(punished, "&cYou have been permanently muted.");
            }

            pm.reloadProfile(prof, true);

            MessageManager.broadcast("pure.permmute", "&a" + prof.getCurrentName() + " has been muted by " + sender.getName() + ".");
        }
    }

    public void temporarilyMute(CommandSender sender, UUID punished, long length, String reason) {
        Profile prof = pm.getProfile(punished);
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (prof != null) {
            if (prof.isMuted()) {
                MessageManager.sendMessage(sender, "&cPlayer \"" + prof.getCurrentName() + "\" is already muted.");
                return;
            }

            prof.getTemporaryMutes().add(new TemporaryMute(punisher, length, reason, DateUtil.getProperDate(new Date())));

            if (prof.isOnline()) {
                MessageManager.sendMessage(punished, "&cYou have been temporarily muted for " + DateUtil.formatDateDiff(length) + ".");
            }

            pm.reloadProfile(prof, true);

            MessageManager.broadcast("pure.tempmute", "&a" + prof.getCurrentName() + " has been muted by " + sender.getName() + " for " + DateUtil.formatDateDiff(length) + ".");
        }
    }

    public void warn(CommandSender sender, UUID punished, String reason) {
        Profile prof = pm.getProfile(punished);
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (prof != null) {
            prof.getWarns().add(new Warn(punisher, reason, DateUtil.getProperDate(new Date())));

            if (prof.isOnline()) {
                MessageManager.sendMessage(punished, "&cYou have been warned for " + reason);
            }

            pm.reloadProfile(prof, true);

            MessageManager.broadcast("pure.warn", "&a" + prof.getCurrentName() + " has been warned by " + sender.getName() + " for " + reason);
        }
    }

    public void note(CommandSender sender, UUID punished, String reason) {
        Profile prof = pm.getProfile(punished);
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (prof != null) {
            prof.getNotes().add(new Note(punisher, reason, DateUtil.getProperDate(new Date())));
            pm.reloadProfile(prof, true);

            MessageManager.sendMessage(sender, "&aNote has been added to " + prof.getCurrentName() + "'s profile.");
        }
    }

    public void unban(CommandSender sender, UUID punished) {
        Profile prof = pm.getProfile(punished);

        if (isIPBanned(prof.getCurrentIP())) {
            if (getActiveIPBan(prof.getCurrentIP()) != null) {
                getActiveIPBan(prof.getCurrentIP()).setActive(false);
            }
        } else if (prof.isPermanentlyBanned()) {
            if (prof.getActivePermanentBan() != null) {
                prof.getActivePermanentBan().setActive(false);
                pm.reloadProfile(prof, true);
            }
        } else if (prof.isTemporarilyBanned()) {
            if (prof.getActiveTemporaryBan() != null) {
                prof.getActiveTemporaryBan().setLength(0);
                pm.reloadProfile(prof, true);
            }
        } else {
            MessageManager.sendMessage(sender, "&c" + prof.getCurrentName() + " is not banned.");
            return;
        }

        MessageManager.broadcast("pure.unban", "&a" + prof.getCurrentName() + " has been unbanned by " + sender.getName() + ".");
    }

    public void unban(CommandSender sender, String address) {
        if (!isIPBanned(address)) {
            MessageManager.sendMessage(sender, "&cIP \"" + address + "\" is not banned.");
            return;
        }

        if (getActiveIPBan(address) != null) {
            getActiveIPBan(address).setActive(false);
        }

        MessageManager.broadcast("pure.unban", "&a" + address + " has been unbanned by " + sender.getName() + ".");
    }

    public void unmute(CommandSender sender, UUID punished) {
        Profile prof = pm.getProfile(punished);

        if (prof.isPermanentlyMuted()) {
            if (prof.getActivePermanentMute() != null) {
                prof.getActivePermanentMute().setActive(false);
                pm.reloadProfile(prof, true);
            }
        } else if (prof.isTemporarilyMuted()) {
            if (prof.getActiveTemporaryMute() != null) {
                prof.getActiveTemporaryMute().setLength(0);
                pm.reloadProfile(prof, true);
            }
        } else {
            MessageManager.sendMessage(sender, "&c" + prof.getCurrentName() + " is not muted.");
            return;
        }

        MessageManager.broadcast("pure.unban", "&a" + prof.getCurrentName() + " has been unmuted by " + sender.getName() + ".");
    }

    public void saveIPBans() {
        for (IPBan b : ipBanList) {
            DBCursor dbc = bCollection.find(new BasicDBObject("address", b.getAddress()));
            BasicDBObject dbo = new BasicDBObject();

            dbo.append("address", b.getAddress());
            if (b.getPunisherUUID() != null) {
                dbo.append("punisherUUID", b.getPunisherUUID().toString());
            }
            dbo.append("reason", b.getReason());
            dbo.append("dateIssued", b.getDateIssued());
            dbo.append("isActive", b.isActive());

            if (dbc.hasNext()) {
                bCollection.update(dbc.next(), dbo);
            } else {
                bCollection.insert(dbo);
            }
        }
    }

    public boolean isIPBanned(UUID id) {
        Profile prof = pm.getProfile(id);
        for (IPBan b : ipBanList) {
            if (b.getAddress().equalsIgnoreCase(prof.getCurrentIP())) {
                if (b.isActive()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isIPBanned(String address) {
        for (IPBan b : ipBanList) {
            if (b.getAddress().equalsIgnoreCase(address)) {
                if (b.isActive()) {
                    return true;
                }
            }
        }

        return false;
    }

    public IPBan getActiveIPBan(UUID id) {
        Profile prof = pm.getProfile(id);
        for (IPBan b : ipBanList) {
            if (b.getAddress().equalsIgnoreCase(prof.getCurrentIP())) {
                if (b.isActive()) {
                    return b;
                }
            }
        }

        return null;
    }

    public IPBan getActiveIPBan(String address) {
        for (IPBan b : ipBanList) {
            if (b.getAddress().equalsIgnoreCase(address)) {
                if (b.isActive()) {
                    return b;
                }
            }
        }

        return null;
    }

    public void checkForValidAlts(UUID id) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Profile prof = pm.getProfile(id);

                pm.getLoadedProfiles().stream().filter(profs -> profs.getIpList().contains(prof.getCurrentIP())).forEach(profs -> {
                    if (profs.getUniqueID() != prof.getUniqueID()) {
                        if (!profs.getAltList().contains(prof.getUniqueID())) {
                            profs.getAltList().add(prof.getUniqueID());
                        }

                        if (!prof.getAltList().contains(profs.getUniqueID())) {
                            prof.getAltList().add(profs.getUniqueID());
                        }
                    }
                });

                DBCursor dbc = pCollection.find();

                while (dbc.hasNext()) {
                    BasicDBObject dbo = (BasicDBObject) dbc.next();

                    Profile tprof = pm.getProfile(UUID.fromString(dbo.getString("uuid")));

                    if (tprof.getIpList().contains(prof.getCurrentIP())) {
                        if (!prof.getAltList().contains(tprof.getUniqueID())) {
                            if (tprof.getUniqueID() != prof.getUniqueID()) {
                                if (!tprof.getAltList().contains(prof.getUniqueID())) {
                                    tprof.getAltList().add(prof.getUniqueID());

                                    pm.saveProfile(tprof);
                                }

                                if (!prof.getAltList().contains(tprof.getUniqueID())) {
                                    prof.getAltList().add(tprof.getUniqueID());
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(Pure.getInstance());
    }

    public void checkForBannedAlts(UUID id) {
        List<String> associatedProfiles = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                Profile prof = pm.getProfile(id);
                DBCursor dbc = pCollection.find();

                while (dbc.hasNext()) {
                    BasicDBObject dbo = (BasicDBObject) dbc.next();

                    Profile tprof = pm.getProfile(UUID.fromString(dbo.getString("uuid")));

                    boolean alert = false;

                    if (tprof.isBanned()) {
                        for (String taddress : tprof.getIpList()) {
                            if (prof.getIpList().contains(taddress)) {
                                alert = true;
                            }
                        }

                        for (String address : prof.getIpList()) {
                            if (tprof.getIpList().contains(address)) {
                                alert = true;
                                associatedProfiles.add(tprof.getCurrentName());
                            }
                        }
                    }

                    if (alert)
                        MessageManager.broadcast("pure.alert", "&cAlert! &a" + prof.getCurrentName() + "&7's IP is associated with the banned account(s) &a" + associatedProfiles.toString().replace("&7(", "").replace("&7)", ""));
                }
            }
        }.runTaskAsynchronously(Pure.getInstance());
    }

    public void addVanisher(Player p) {
        vanished.add(p.getUniqueId());

        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (!all.hasPermission("pure.vanish")) {
                all.hidePlayer(p);
            }
        }

        MessageManager.sendMessage(p, "&aYou are now vanished.");
    }

    public void removeVanisher(Player p) {
        vanished.remove(p.getUniqueId());

        for (Player all : PlayerUtility.getOnlinePlayers()) {
            all.showPlayer(p);
        }

        MessageManager.sendMessage(p, "&aYou are now visible.");
    }

    public void setChatSlowed(CommandSender sender) {
        isChatSlowed = !isChatSlowed;

        MessageManager.broadcast("&c" + sender.getName() + " has " + (isChatSlowed ? "slowed" : "unslowed") + " the chat.");
    }

    public void setChatMuted(CommandSender sender) {
        isChatMuted = !isChatMuted;

        MessageManager.broadcast("&c" + sender.getName() + " has " + (isChatMuted ? "disabled" : "enabled") + " the chat.");
    }

    public void setServerFrozen() {
        isServerFrozen = !isServerFrozen;

        MessageManager.broadcast("&c&lThe server has been " + (isServerFrozen ? "frozen" : "unfrozen"));

        if (isServerFrozen) {
            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (!all.hasPermission("pure.freeze")) {
                    all.setWalkSpeed(0.0F);
                }
            }
        } else {
            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (!all.hasPermission("pure.freeze")) {
                    all.setWalkSpeed(0.2F);
                }
            }
        }
    }

    public boolean isVanished(Player p) {
        return vanished.contains(p.getUniqueId());
    }

    public Map<UUID, SpecialPlayerInventory> getInventories() {
        return inventories;
    }

    public boolean isChatSlowed() {
        return isChatSlowed;
    }

    public boolean isChatMuted() {
        return isChatMuted;
    }

    public boolean isServerFrozen() {
        return isServerFrozen;
    }

    public List<UUID> getFrozen() {
        return frozen;
    }
}
