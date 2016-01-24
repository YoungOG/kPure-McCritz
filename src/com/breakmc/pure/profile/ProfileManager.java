package com.breakmc.pure.profile;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.punishments.*;
import com.breakmc.pure.utils.DateUtil;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.database.DatabaseManager;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ProfileManager {

    private Pure main = Pure.getInstance();
    private List<Profile> loadedProfiles = new ArrayList<>();
    private DBCollection pCollection = DatabaseManager.getInstance().getCollection("profiles");

    public ProfileManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                getLoadedProfiles().stream().filter(Profile::isOnline).forEach(prof -> prof.setPlaytime(prof.getPlaytime() + 1));
            }
        }.runTaskTimerAsynchronously(main, 0L, 20);
    }

    public void saveProfiles() {
        main.getLogger().log(Level.INFO, "Saving " + getLoadedProfiles().size() + " profiles.");

        for (Profile prof : getLoadedProfiles()) {
            prof.setOnline(false);
            prof.saveProfileData();
        }

        main.getLogger().log(Level.INFO, "Saved " + getLoadedProfiles().size() + " profiles.");
    }

    public void loadProfile(UUID id, boolean check) {
        Profile profile = new Profile(id);
        profile.loadProfileData(false);

        getLoadedProfiles().add(profile);

        if (check) {
            Bukkit.getLogger().log(Level.INFO, "Performing check for " + profile.getCurrentName() + ".");

            new BukkitRunnable() {
                @Override
                public void run() {
                    Pure.getInstance().getPunishmentManager().checkForValidAlts(profile.getUniqueID());
                    Pure.getInstance().getPunishmentManager().checkForBannedAlts(profile.getUniqueID());
                }
            }.runTaskAsynchronously(Pure.getInstance());
        }
    }

    public Profile loadProfile(UUID id) {
        Profile profile = new Profile(id);
        profile.loadProfileData(false);
        return profile;
    }

    public Profile loadProfile(String name) {
        Profile profile = new Profile(name);
        if (profile.loadProfileData(true))
            return profile;

        return null;
    }

    public void createProfile(Player p, String ip) {
        Profile prof = new Profile(p.getUniqueId());

        prof.setCurrentIP(ip);
        prof.setCurrentName(p.getName());
        prof.setDateCreated(DateUtil.getProperDate(new Date()));
        prof.setGroup(PlayerUtility.getGroup(p.getName()));
        prof.setOnline(p.isOnline());
        prof.setPlaytime(0);
        prof.setLogins(0);
        prof.setPin("");
        prof.getIpList().add(prof.getCurrentIP());
        prof.getNameList().add(prof.getCurrentName());
        prof.saveProfileData();

        getLoadedProfiles().add(prof);

        Bukkit.getLogger().log(Level.INFO, "Performing check for " + prof.getCurrentName() + ".");

        new BukkitRunnable() {
            @Override
            public void run() {
                Pure.getInstance().getPunishmentManager().checkForValidAlts(prof.getUniqueID());
                Pure.getInstance().getPunishmentManager().checkForBannedAlts(prof.getUniqueID());
            }
        }.runTaskAsynchronously(Pure.getInstance());
    }

    public void lookup(CommandSender sender, String address) {
        List<Profile> foundProfiles = loadedProfiles.stream().filter(prof -> prof.getIpList().contains(address) || prof.getCurrentIP().equalsIgnoreCase(address)).collect(Collectors.toList());

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

    public boolean hasProfile(UUID id) {
        return pCollection.find(new BasicDBObject("uniqueID", id.toString())).hasNext();
    }

    public boolean hasLoadedProfile(UUID id) {
        for (Profile prof : getLoadedProfiles()) {
            if (prof.getUniqueID().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public Profile getProfile(UUID id) {
        for (Profile prof : getLoadedProfiles()) {
            if (prof.getUniqueID().equals(id)) {
                return prof;
            }
        }

        return loadProfile(id);
    }

    public Profile getProfile(String name) {
        for (Profile prof : getLoadedProfiles()) {
            if (prof.getCurrentName().equalsIgnoreCase(name)) {
                return prof;
            }
        }

        return loadProfile(name);
    }

    public List<Profile> getLoadedProfiles() {
        return loadedProfiles;
    }

    public List<String> getProfileInformation(Profile prof) {
        List<String> lines = new ArrayList<>();

        lines.add("&b&l" + prof.getCurrentName() + " &7(&a" + prof.getCurrentIP() + "&7)");
        lines.add("&7Online: " + (prof.isOnline() ? "&aTrue" : "&cFalse"));
        lines.add("&7Playtime: &a" + DateUtil.readableTime(prof.getPlaytime() * 1000));
        lines.add("&7Rank: &a" + prof.getGroup());
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
            if (Pure.getInstance().getPunishmentManager().isIPBanned(prof.getCurrentIP())) {
                IPBan b = Pure.getInstance().getPunishmentManager().getActiveIPBan(prof.getCurrentIP());
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

        if (prof.getWarns().size() > 0) {
            lines.add("&7Warns&7(&a" + prof.getWarns().size() + "&7):");
            lines.addAll(prof.getWarns().stream().map(w -> "  &7- &a" + w.getReason() + " &7(&a" + w.getPunisherName() + "&7)").collect(Collectors.toList()));
        }

        if (prof.getNotes().size() > 0) {
            lines.add("&7Notes&7(&a" + prof.getNotes().size() + "&7):");
            lines.addAll(prof.getNotes().stream().map(n -> "  &7- &a" + n.getReason() + " &7(&a" + n.getPunisherName() + "&7)").collect(Collectors.toList()));
        }

        return lines;
    }

    public List<String> translatedColors(List<String> list) {
        return list.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toCollection(ArrayList::new));
    }
}
