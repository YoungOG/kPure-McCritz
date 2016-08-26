package com.mccritz.kpure.profile;

import com.mccritz.kperms.kPerms;
import com.mccritz.kpure.kPure;
import com.mccritz.kpure.punishment.punishments.*;
import com.mccritz.kpure.utils.DateUtil;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import mkremins.fanciful.FancyMessage;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ProfileManager {

    private kPure main = kPure.getInstance();
    private MongoCollection pCollection = main.getMongoDatabase().getCollection("profiles");

    public ProfileManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player all : PlayerUtility.getOnlinePlayers()) {
                    if (getProfile(all.getUniqueId()) != null) {
                        getProfile(all.getUniqueId()).setPlaytime(getProfile(all.getUniqueId()).getPlaytime() + 1);
                    }
                }
            }
        }.runTaskTimerAsynchronously(main, 0L, 20);
    }

    public void createProfile(Player p, String ip) {
        Profile prof = new Profile(p.getUniqueId());
        com.mccritz.kperms.profiles.Profile pProf = kPerms.getInstance().getProfileManager().getProfile(p.getUniqueId());

        prof.setCurrentIP(ip);
        prof.setCurrentName(p.getName());
        prof.setDateCreated(DateUtil.getProperDate(new Date()));
        prof.setGroup(pProf != null ? pProf.getRank().getName() : "None");
        prof.setLastUsedIP(ip);
        prof.setPlaytime(0);
        prof.setLogins(1);
        prof.setPin("");
        prof.getIpList().add(prof.getCurrentIP());
        prof.getNameList().add(prof.getCurrentName());
        saveProfile(prof);

        main.getPunishmentManager().checkForValidAlts(prof.getUniqueID());
        main.getPunishmentManager().checkForBannedAlts(prof.getUniqueID());
    }

    public void lookup(CommandSender sender, String address) {
        new BukkitRunnable() {
            @Override
            public void run() {
                FindIterable<Document> foundDocuments = pCollection.find(Filters.eq("currentIP", address));
                List<Profile> foundProfiles = new ArrayList<>();

                for (Document doc : foundDocuments) {
                    foundProfiles.add(kPure.getInstance().getProfileManager().getProfile(UUID.fromString(doc.getString("uniqueID"))));
                }

                if (foundProfiles.size() <= 0) {
                    MessageManager.sendMessage(sender, "&cCould not find any accounts associated with the IP \"" + address + "\"");
                }

                MessageManager.sendMessage(sender, "&7Found (&a" + foundProfiles.size() + "&7) accounts associated with the IP (&a" + address + "&7):");
                MessageManager.sendMessage(sender, "&cNote: Hover over for more information, click to preform lookup command.");

                for (Profile prof : foundProfiles) {
                    FancyMessage fm = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&7- &b&l" + prof.getCurrentName() + " &7(&a" + prof.getCurrentIP() + "&7)"));
                    fm.command("/lookup " + prof.getCurrentName());
                    fm.tooltip(translatedColors(getProfileInformation(prof)));
                    fm.send(sender);
                }
            }
        }.runTaskAsynchronously(main);
    }

    public Profile getProfile(UUID id) {
        try {
            return kPure.SERVICE.submit(new ProfileLoadCallable(id)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Profile getProfile(String name) {
        try {
            return kPure.SERVICE.submit(new ProfileLoadCallable(name)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveProfile(Profile profile) {
        kPure.SERVICE.submit(new ProfileSaveCallable(profile));
    }

    public List<String> getProfileInformation(Profile prof) {
        List<String> lines = new ArrayList<>();

        lines.add("&b&l" + prof.getCurrentName() + " &7(&a" + prof.getCurrentIP() + "&7)");
        lines.add("&7Online: " + (Bukkit.getPlayer(prof.getUniqueID()) != null ? "&aTrue" : "&cFalse"));
        lines.add("&7Playtime: &a" + DateUtil.readableTime(prof.getPlaytime() * 1000));
        lines.add("&7Rank: &a" + prof.getGroup());
        lines.add("&7Last Used IP: &c" + prof.getLastUsedIP());
        lines.add("&7Past IPs(&a" + prof.getIpList().size() + "&7): &a" + prof.getIpList().toString().replace("[", "").replace("]", ""));
        lines.add("&7Past Names&7(&a" + prof.getNameList().size() + "&7): &a" + prof.getNameList().toString().replace("[", "").replace("]", ""));

        if (prof.getAltList().size() > 0) {
            lines.add("&7Known Alts&7(&a" + prof.getAltList().size() + "&7): &a" + prof.getKnownAltsNames().toString().replace("[", "").replace("]", ""));

            if (prof.getOnlineAlts().size() > 0) {
                lines.add("&7Online Alts&7(&a" + prof.getOnlineAlts().size() + "&7): &a" + prof.getOnlineAltsNames().toString().replace("[", "").replace("]", ""));
            }
        }

        lines.add("&7Banned: &a" + prof.isBanned());
        if (prof.isBanned()) {
            if (kPure.getInstance().getPunishmentManager().isIPBanned(prof.getCurrentIP())) {
                IPBan b = kPure.getInstance().getPunishmentManager().getActiveIPBan(prof.getCurrentIP());
                lines.add("  &7Type: &aBlacklist");
                lines.add("  &7Reason: &a" + b.getReason());
                lines.add("  &7Date: &a" + b.getDateIssued());
                lines.add("  &7By: &a" + b.getPunisherName());
            }
            if (prof.isPermanentlyBanned()) {
                PermanentBan b = prof.getActivePermanentBan();
                lines.add("  &7Type: &aPermanent");
                lines.add("  &7Reason: &a" + b.getReason());
                lines.add("  &7Date: &a" + b.getDateIssued());
                lines.add("  &7By: &a" + b.getPunisherName());
            }

            if (prof.isTemporarilyBanned()) {
                TemporaryBan tb = prof.getActiveTemporaryBan();
                lines.add("  &7Type: &aTemporary: " + DateUtil.formatDateDiff(tb.getLength()));
                lines.add("  &7Reason: &a" + tb.getReason());
                lines.add("  &7Date: &a" + tb.getDateIssued());
                lines.add("  &7By: &a" + tb.getPunisherName());
            }
        }

        lines.add("&7Muted: &a" + prof.isMuted());
        if (prof.isMuted()) {
            if (prof.isPermanentlyMuted()) {
                PermanentMute b = prof.getActivePermanentMute();
                lines.add("  &7Type: &aPermanent");
                lines.add("  &7Reason: &a" + b.getReason());
                lines.add("  &7Date: &a" + b.getDateIssued());
                lines.add("  &7By: &a" + b.getPunisherName());
            }

            if (prof.isTemporarilyMuted()) {
                TemporaryMute tb = prof.getActiveTemporaryMute();
                lines.add("  &7Type: &aTemporary: " + DateUtil.formatDateDiff(tb.getLength()));
                lines.add("  &7Reason: &a" + tb.getReason());
                lines.add("  &7Date: &a" + tb.getDateIssued());
                lines.add("  &7By: &a" + tb.getPunisherName());
            }
        }

        return lines;
    }

    public List<String> translatedColors(List<String> list) {
        return list.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
