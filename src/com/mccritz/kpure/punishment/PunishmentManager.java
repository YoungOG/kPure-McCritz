package com.mccritz.kpure.punishment;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.punishment.punishments.*;
import com.mccritz.kpure.utils.DateUtil;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

public class PunishmentManager {

    private kPure main = kPure.getInstance();
    private ProfileManager pm = kPure.getInstance().getProfileManager();
    private MongoCollection<Document> bCollection = main.getMongoDatabase().getCollection("ipbans");
    private MongoCollection<Document> pCollection = main.getMongoDatabase().getCollection("profiles");
    private boolean forceBans;

    public PunishmentManager() {
        bCollection.createIndex(new Document("address", 1), new IndexOptions().unique(true));

        forceBans = main.getConfig().getBoolean("general.force-bans");
    }

    public void permanentlyBan(CommandSender sender, Profile profile, String reason) {
        UUID punisher = sender instanceof Player ? ((Player) sender).getUniqueId() : null;

        if (profile != null) {
            if (profile.isBanned()) {
                MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is already banned.");
                return;
            }

            profile.getPermanentBans().add(new PermanentBan(punisher, reason, DateUtil.getProperDate(new Date()), true));
            pm.saveProfile(profile);

            if (Bukkit.getPlayer(profile.getUniqueID()) != null) {
                Bukkit.getPlayer(profile.getUniqueID()).kickPlayer(ChatColor.RED + "You have been banned from McCritZ.\nYou can purchase an unban at store.mccritz.com");
            }

            MessageManager.broadcast("&c" + profile.getCurrentName() + " &7has been banned by &c" + sender.getName() + "&7.");
        }
    }

    public void temporarilyBan(CommandSender sender, Profile profile, long length, String reason) {
        UUID punisher = sender instanceof Player ? ((Player) sender).getUniqueId() : null;

        if (profile != null) {
            if (profile.isBanned()) {
                MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is already banned.");
                return;
            }

            profile.getTemporaryBans().add(new TemporaryBan(punisher, length, reason, DateUtil.getProperDate(new Date())));
            pm.saveProfile(profile);

            if (Bukkit.getPlayer(profile.getUniqueID()) != null) {
                Bukkit.getPlayer(profile.getUniqueID()).kickPlayer(ChatColor.RED + "You have been temporarily banned from McCritZ.\nThis ban expires in " + DateUtil.formatDateDiff(length) + "\nYou can purchase an unban at store.mccritz.com");
            }

            MessageManager.broadcast("&c" + profile.getCurrentName() + " &7has been temporarily banned by &c" + sender.getName() + "&7.");
        }
    }

    public void banIP(CommandSender sender, String address, String reason) {
        if (isIPBanned(address)) {
            MessageManager.sendMessage(sender, "&c" + address + " &7is already banned.");
            return;
        }

        UUID punisher = sender instanceof Player ? ((Player) sender).getUniqueId() : null;

        // ipBanList.add(new IPBan(address, punisher, reason,
        // DateUtil.getProperDate(new Date()), true));
        IPBan b = new IPBan(address, punisher, reason, DateUtil.getProperDate(new Date()), true);
        saveIPBan(b);

        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (all.getAddress().getAddress().getHostAddress().replace("[", "").replace("]", "").equalsIgnoreCase(address)) {
                all.kickPlayer(ChatColor.RED + "You have been blacklisted from McCritZ.\nYou cannot purchase an unban.");
            }
        }

        MessageManager.broadcast("kpure.banip", "&c" + address + " &7has been blacklisted by &c" + sender.getName() + "&7.");
    }

    public void permanentlyMute(CommandSender sender, Profile profile, String reason) {
        UUID punisher = sender instanceof Player ? ((Player) sender).getUniqueId() : null;

        if (profile != null) {
            if (profile.isBanned()) {
                MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is already muted.");
                return;
            }

            profile.getPermanentMutes().add(new PermanentMute(punisher, reason, DateUtil.getProperDate(new Date()), true));
            pm.saveProfile(profile);

            MessageManager.sendMessage(profile.getUniqueID(), "&cYou have been permanently muted.");

            MessageManager.broadcast("&c" + profile.getCurrentName() + " &7has been muted by &c" + sender.getName() + "&7.");
        }
    }

    public void temporarilyMute(CommandSender sender, Profile profile, long length, String reason) {
        UUID punisher = sender instanceof Player ? ((Player) sender).getUniqueId() : null;

        if (profile != null) {
            if (profile.isMuted()) {
                MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is already muted.");
                return;
            }

            profile.getTemporaryMutes().add(new TemporaryMute(punisher, length, reason, DateUtil.getProperDate(new Date())));
            pm.saveProfile(profile);

            MessageManager.sendMessage(profile.getUniqueID(), "&cYou have been temporarily muted for " + DateUtil.formatDateDiff(length) + ".");

            MessageManager.broadcast("&c" + profile.getCurrentName() + " &7has been muted by &c" + sender.getName() + "&7.");
        }
    }

    public void unban(CommandSender sender, Profile profile) {
        boolean unbanned = false;

        if (isIPBanned(profile.getCurrentIP())) {
            IPBan b = this.getActiveIPBan(profile.getCurrentIP());

            if (b != null) {
                b.setActive(false);
                this.saveIPBan(b);
                unbanned = true;
            }
        }

        if (profile.isPermanentlyBanned()) {
            PermanentBan b = profile.getActivePermanentBan();

            if (b != null) {
                b.setActive(false);
                pm.saveProfile(profile);
                unbanned = true;
            }
        }

        if (profile.isTemporarilyBanned()) {
            TemporaryBan b = profile.getActiveTemporaryBan();

            if (b != null) {
                b.setLength(0);
                pm.saveProfile(profile);
                unbanned = true;
            }
        }

        if (unbanned) {
            MessageManager.broadcast("&c" + profile.getCurrentName() + " &7has been unbanned by &c" + sender.getName() + "&7.");
        } else {
            MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is not banned.");
        }
    }

    public void unban(CommandSender sender, String address) {
        if (!isIPBanned(address)) {
            MessageManager.sendMessage(sender, "&c" + address + " &7is not banned.");
            return;
        }

        IPBan b = this.getActiveIPBan(address);

        if (b != null) {
            b.setActive(false);
            this.saveIPBan(b);
        }

        MessageManager.broadcast("kpure.unban", "&c" + address + " &7has been unbanned by &c" + sender.getName() + "&7.");
    }

    public void unmute(CommandSender sender, Profile profile) {
        if (profile.isPermanentlyMuted()) {
            if (profile.getActivePermanentMute() != null) {
                profile.getActivePermanentMute().setActive(false);
                pm.saveProfile(profile);
            }
        } else if (profile.isTemporarilyMuted()) {
            if (profile.getActiveTemporaryMute() != null) {
                profile.getActiveTemporaryMute().setLength(0);
                pm.saveProfile(profile);
            }
        } else {
            MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is not muted.");
            return;
        }

        MessageManager.broadcast("kpure.unban", "&c" + profile.getCurrentName() + " &7has been unmuted by &c" + sender.getName() + "&7.");
    }

    public void saveIPBan(IPBan b) {
        Document doc = new Document("address", b.getAddress());

        if (b.getPunisherUUID() != null) {
            doc.append("punisherUUID", b.getPunisherUUID().toString());
        }

        doc.append("reason", b.getReason());
        doc.append("dateIssued", b.getDateIssued());
        doc.append("isActive", b.isActive());

        Document document = bCollection.find(Filters.eq("address", b.getAddress())).first();

        if (document != null) {
            bCollection.replaceOne(Filters.eq("address", b.getAddress()), doc, new UpdateOptions().upsert(true));
        } else {
            bCollection.insertOne(doc);
        }
    }

    public boolean isIPBanned(String address) {
        IPBan b = this.getActiveIPBan(address);

        if (b != null)
            return b.isActive();

        return false;
    }

    public IPBan getActiveIPBan(String address) {
        Document doc = bCollection.find(Filters.eq("address", address)).first();

        if (doc != null)
            return new IPBan(doc.getString("address"), doc.getString("punisherUUID") != null ? UUID.fromString(doc.getString("punisherUUID")) : null, doc.getString("reason"), doc.getString("dateIssued"), doc.getBoolean("isActive"));

        return null;
    }

    public void checkForValidAlts(UUID id) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Profile profile = pm.getProfile(id);

                ArrayList<Document> documents = pCollection.find().into(new ArrayList<>());

                for (Document doc : documents) {
                    Profile result = pm.getProfile(UUID.fromString(doc.getString("uniqueID")));

                    if (result != null) {
                        if (result.getIpList().contains(profile.getCurrentIP())) {
                            if (!profile.getAltList().contains(result.getUniqueID())) {
                                if (result.getUniqueID() != profile.getUniqueID()) {
                                    if (!result.getAltList().contains(profile.getUniqueID())) {
                                        result.getAltList().add(profile.getUniqueID());
                                        pm.saveProfile(result);
                                    }

                                    if (!profile.getAltList().contains(result.getUniqueID())) {
                                        profile.getAltList().add(result.getUniqueID());
                                        pm.saveProfile(profile);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(main);
    }

    public void checkForBannedAlts(UUID id) {
        new BukkitRunnable() {
            @Override
            public void run() {
                HashSet<String> associatedProfiles = new HashSet<>();

                Profile profile = pm.getProfile(id);

                ArrayList<Document> documents = pCollection.find().into(new ArrayList<>());

                for (Document doc : documents) {
                    Profile result = pm.getProfile(UUID.fromString(doc.getString("uniqueID")));

                    boolean alert = false;

                    if (result != null) {
                        if (result.isBanned()) {
                            for (String taddress : result.getIpList()) {
                                if (profile.getIpList().contains(taddress) && !profile.getUniqueID().equals(result.getUniqueID())) {
                                    alert = true;
                                    associatedProfiles.add("&c" + result.getCurrentName());
                                }
                            }

                            for (String address : profile.getIpList()) {
                                if (result.getIpList().contains(address) && !profile.getUniqueID().equals(result.getUniqueID())) {
                                    alert = true;
                                    associatedProfiles.add("&c" + result.getCurrentName());
                                }
                            }
                        }
                    }

                    if (alert) {
                        MessageManager.broadcast("kpure.alert", "&7Searching database for alts associated with &c" + profile.getCurrentName() + "&7(&c" + associatedProfiles.size() + "&7).");
                        MessageManager.broadcast("kpure.alert", "" + associatedProfiles.toString().replace("[", "").replace("]", ""));
                    }
                }
            }
        }.runTaskAsynchronously(main);
    }

    public void staffRollback(UUID id, long length) {
        ArrayList<Document> documents = pCollection.find().into(new ArrayList<>());

        for (Document doc : documents) {
            Profile result = pm.getProfile(UUID.fromString(doc.getString("uniqueID")));

            if (result.isBanned()) {
                if (result.isPermanentlyBanned()) {
                    // if
                    // (result.getActivePermanentBan().getPunisherUUID().equals(id)
                    // && result.getD) {
                    //
                    // }
                }
            } else if (result.isMuted()) {

            }
        }
    }

    public boolean isForceBans() {
        return forceBans;
    }
}
