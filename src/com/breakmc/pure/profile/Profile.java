package com.breakmc.pure.profile;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.punishments.*;
import com.breakmc.pure.utils.DateUtil;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.database.DatabaseManager;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter

public class Profile {

    private UUID uniqueID;
    private String currentName;
    private String currentIP;
    private String dateCreated;
    private String group;
    private boolean isOnline;
    private long playtime;
    private int logins;
    private String pin;
    private HashSet<UUID> altList = new HashSet<>();
    private HashSet<String> nameList = new HashSet<>();
    private HashSet<String> ipList = new HashSet<>();
    private ArrayList<PermanentBan> permanentBans = new ArrayList<>();
    private ArrayList<TemporaryBan> temporaryBans = new ArrayList<>();
    private ArrayList<PermanentMute> permanentMutes = new ArrayList<>();
    private ArrayList<TemporaryMute> temporaryMutes = new ArrayList<>();
    private ArrayList<Warn> warns = new ArrayList<>();
    private ArrayList<Note> notes = new ArrayList<>();

    public Profile(UUID uniqueID) {
        this.uniqueID = uniqueID;

        altList = new HashSet<>();
        nameList = new HashSet<>();
        ipList = new HashSet<>();
        permanentBans = new ArrayList<>();
        temporaryBans = new ArrayList<>();
        permanentMutes = new ArrayList<>();
        temporaryMutes = new ArrayList<>();
        warns = new ArrayList<>();
        notes = new ArrayList<>();
    }

    public void loadProfileData() {
        DBCursor dbc = DatabaseManager.getInstance().getCollection("profiles").find(new BasicDBObject("uuid", uniqueID));

        if (dbc.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc.next();

            this.currentName = dbo.getString("currentName");
            this.currentIP = dbo.getString("currentIP");
            this.dateCreated = dbo.getString("dateCreated");
            this.group = PlayerUtility.getGroup(currentName);
            this.isOnline = dbo.getBoolean("isOnline");
            this.playtime = dbo.getLong("playtime");
            this.logins = dbo.getInt("logins");
            this.pin = dbo.getString("pin");

            BasicDBList dbol1 = (BasicDBList) dbo.get("altList");
            HashSet<UUID> alts = new HashSet<>();
            if (dbol1 != null) {
                alts.addAll(dbol1.stream().map(obj -> UUID.fromString((String) obj)).collect(Collectors.toList()));
            }

            BasicDBList dbol2 = (BasicDBList) dbo.get("nameList");
            HashSet<String> names = new HashSet<>();
            if (dbol2 != null) {
                names.addAll(dbol2.stream().map(obj -> (String) obj).collect(Collectors.toList()));
            }

            BasicDBList dbol3 = (BasicDBList) dbo.get("ipList");
            HashSet<String> ips = new HashSet<>();
            if (dbol3 != null) {
                ips.addAll(dbol3.stream().map(obj -> (String) obj).collect(Collectors.toList()));
            }

            BasicDBList dbol4 = (BasicDBList) dbo.get("permanent-bans");
            ArrayList<PermanentBan> permanentBans = new ArrayList<>();
            if (dbol4 != null) {
                for (Object obj : dbol4) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    permanentBans.add(new PermanentBan(UUID.fromString(bdo.getString("punishedUUID")), ((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getString("reason"), bdo.getString("dateCreated"), bdo.getBoolean("isActive")));
                }
            }

            BasicDBList dbol5 = (BasicDBList) dbo.get("temporary-bans");
            ArrayList<TemporaryBan> temporaryBans = new ArrayList<>();
            if (dbol5 != null) {
                for (Object obj : dbol5) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    temporaryBans.add(new TemporaryBan(UUID.fromString(bdo.getString("punishedUUID")), ((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getLong("length"), bdo.getString("reason"), bdo.getString("dateCreated")));
                }
            }

            BasicDBList dbol6 = (BasicDBList) dbo.get("permanent-mutes");
            ArrayList<PermanentMute> permanentMutes = new ArrayList<>();
            if (dbol6 != null) {
                for (Object obj : dbol6) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    permanentMutes.add(new PermanentMute(UUID.fromString(bdo.getString("punishedUUID")), ((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getString("reason"), bdo.getString("dateCreated"), bdo.getBoolean("isActive")));
                }
            }

            BasicDBList dbol7 = (BasicDBList) dbo.get("temporary-mutes");
            ArrayList<TemporaryMute> temporaryMutes = new ArrayList<>();
            if (dbol7 != null) {
                for (Object obj : dbol7) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    temporaryMutes.add(new TemporaryMute(UUID.fromString(bdo.getString("punishedUUID")), ((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getLong("length"), bdo.getString("reason"), bdo.getString("dateCreated")));
                }
            }

            BasicDBList dbol8 = (BasicDBList) dbo.get("warns");
            ArrayList<Warn> warns = new ArrayList<>();
            if (dbol8 != null) {
                for (Object obj : dbol8) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    warns.add(new Warn(UUID.fromString(bdo.getString("punishedUUID")), ((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getString("reason"), bdo.getString("dateCreated")));
                }
            }

            BasicDBList dbol9 = (BasicDBList) dbo.get("notes");
            ArrayList<Note> notes = new ArrayList<>();
            if (dbol9 != null) {
                for (Object obj : dbol9) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    notes.add(new Note(UUID.fromString(bdo.getString("punishedUUID")), ((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getString("reason"), bdo.getString("dateCreated")));
                }
            }

            this.permanentBans = permanentBans;
            this.temporaryBans = temporaryBans;
            this.permanentMutes = permanentMutes;
            this.temporaryMutes = temporaryMutes;
            this.warns = warns;
            this.setNotes(notes);
            this.setAltList(alts);
            this.setNameList(names);
            this.setIpList(ips);

            checkForValidAlts();
            checkForBannedAlts();
        }
    }

    public List<Player> getOnlineAlts() {
        return altList.stream().filter(id -> Bukkit.getPlayer(id) != null).map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public List<String> getOnlineAltsNames() {
        return getOnlineAlts().stream().map(Player::getName).collect(Collectors.toList());
    }

    public List<String> getKnownAltsNames() {
        return getAltList().stream().map(id -> Bukkit.getOfflinePlayer(id).getName()).collect(Collectors.toList());
    }

    public boolean isBanned() {
        return isPermanentlyBanned() || isTemporarilyBanned() || Pure.getInstance().getPunishmentManager().isIPBanned(getCurrentIP());
    }

    public boolean isPermanentlyBanned() {
        for (PermanentBan b : permanentBans) {
            if (b.isActive()) {
                return true;
            }
        }

        return false;
    }

    public PermanentBan getActivePermanentBan() {
        for (PermanentBan b : permanentBans) {
            if (b.isActive()) {
                return b;
            }
        }

        return null;
    }

    public boolean isTemporarilyBanned() {
        for (TemporaryBan b : temporaryBans) {
            if (b.isActive()) {
                return true;
            }
        }

        return false;
    }

    public TemporaryBan getActiveTemporaryBan() {
        for (TemporaryBan b : temporaryBans) {
            if (b.isActive()) {
                return b;
            }
        }

        return null;
    }

    public boolean isMuted() {
        return isPermanentlyMuted() || isTemporarilyMuted();
    }

    public boolean isPermanentlyMuted() {
        for (PermanentMute m : permanentMutes) {
            if (m.isActive()) {
                return true;
            }
        }

        return false;
    }

    public PermanentMute getActivePermanentMute() {
        for (PermanentMute m : permanentMutes) {
            if (m.isActive()) {
                return m;
            }
        }

        return null;
    }

    public boolean isTemporarilyMuted() {
        for (TemporaryMute m : temporaryMutes) {
            if (m.isActive()) {
                return true;
            }
        }

        return false;
    }

    public TemporaryMute getActiveTemporaryMute() {
        for (TemporaryMute m : temporaryMutes) {
            if (m.isActive()) {
                return m;
            }
        }

        return null;
    }

    public boolean hasPin() {
        return !pin.equalsIgnoreCase("") || !pin.isEmpty() || !(pin.length() < 4);
    }

    public void lookup(CommandSender sender) {
        boolean hasElevatedPermission = sender.hasPermission("pure.lookup.admin");

        MessageManager.sendMessage(sender, "&b&l" + getCurrentName() + (hasElevatedPermission ? " &7(&a" + getCurrentIP() + "&7)" : ""));
        MessageManager.sendMessage(sender, "&7Online: " + (isOnline ? "&aTrue" : "&cFalse"));
        MessageManager.sendMessage(sender, "&7Playtime: &a" + DateUtil.readableTime(getPlaytime() * 1000));
        MessageManager.sendMessage(sender, "&7Rank: &a" + getGroup());

        if (hasElevatedPermission)
            MessageManager.sendMessage(sender, "&7Past IPs(&a" + getIpList().size() + "&7): &a" + getIpList().toString().replace("[", "").replace("]", ""));

        MessageManager.sendMessage(sender, "&7Past Names&7(&a" + getNameList().size() + "&7): &a" + getNameList().toString().replace("[", "").replace("]", ""));

        if (getAltList().size() > 0) {
            MessageManager.sendMessage(sender, "&7Known Alts&7(&a" + getAltList().size() + "&7): &a" + getKnownAltsNames().toString().replace("[", "").replace("]", ""));

            if (getOnlineAlts().size() > 0) {
                MessageManager.sendMessage(sender, "&7Online Alts&7(&a" + getOnlineAlts().size() + "&7): &a" + getOnlineAltsNames().toString().replace("[", "").replace("]", ""));
            }
        }

        MessageManager.sendMessage(sender, "&7Banned: &a" + isBanned());
        if (isBanned()) {
            if (Pure.getInstance().getPunishmentManager().isIPBanned(getCurrentIP())) {
                IPBan b = Pure.getInstance().getPunishmentManager().getActiveIPBan(getCurrentIP());
                MessageManager.sendMessage(sender, "  &7Type: &aBlacklist");
                MessageManager.sendMessage(sender, "  &7Reason: &a" + b.getReason());
                MessageManager.sendMessage(sender, "  &7Date: &a" + b.getDateIssued());
                MessageManager.sendMessage(sender, "  &7By: &a" + b.getPunisherName());
            }
            if (isPermanentlyBanned()) {
                PermanentBan b = getActivePermanentBan();
                MessageManager.sendMessage(sender, "  &7Type: &aPermanent");
                MessageManager.sendMessage(sender, "  &7Reason: &a" + b.getReason());
                MessageManager.sendMessage(sender, "  &7Date: &a" + b.getDateIssued());
                MessageManager.sendMessage(sender, "  &7By: &a" + b.getPunisherName());
            }

            if (isTemporarilyBanned()) {
                TemporaryBan tb = getActiveTemporaryBan();
                MessageManager.sendMessage(sender, "  &7Type: &aTemporary: " + DateUtil.formatDateDiff(tb.getLength()));
                MessageManager.sendMessage(sender, "  &7Reason: &a" + tb.getReason());
                MessageManager.sendMessage(sender, "  &7Date: &a" + tb.getDateIssued());
                MessageManager.sendMessage(sender, "  &7By: &a" + tb.getPunisherName());
            }
        }

        MessageManager.sendMessage(sender, "&7Muted: &a" + isMuted());
        if (isMuted()) {
            if (isPermanentlyMuted()) {
                PermanentMute b = getActivePermanentMute();
                MessageManager.sendMessage(sender, "  &7Type: &aPermanent");
                MessageManager.sendMessage(sender, "  &7Reason: &a" + b.getReason());
                MessageManager.sendMessage(sender, "  &7Date: &a" + b.getDateIssued());
                MessageManager.sendMessage(sender, "  &7By: &a" + b.getPunisherName());
            }

            if (isTemporarilyMuted()) {
                TemporaryMute tb = getActiveTemporaryMute();
                MessageManager.sendMessage(sender, "  &7Type: &aTemporary: " + DateUtil.formatDateDiff(tb.getLength()));
                MessageManager.sendMessage(sender, "  &7Reason: &a" + tb.getReason());
                MessageManager.sendMessage(sender, "  &7Date: &a" + tb.getDateIssued());
                MessageManager.sendMessage(sender, "  &7By: &a" + tb.getPunisherName());
            }
        }

        if (getWarns().size() > 0) {
            MessageManager.sendMessage(sender, "&7Warns&7(&a" + getWarns().size() + "&7):");

            for (Warn w : getWarns()) {
                MessageManager.sendMessage(sender, "  &7- &a" + w.getReason() + " &7(&a" + w.getPunisherName() + "&7)");
            }
        }

        if (getNotes().size() > 0) {
            MessageManager.sendMessage(sender, "&7Notes&7(&a" + getNotes().size() + "&7):");

            for (Note n : getNotes()) {
                MessageManager.sendMessage(sender, "  &7- &a" + n.getReason() + " &7(&a" + n.getPunisherName() + "&7)");
            }
        }
    }

    public void checkForValidAlts() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Pure.getInstance().getProfileManager().getLoadedProfiles().stream().filter(profs -> profs.getIpList().contains(getCurrentIP())).forEach(profs -> {
                    if (profs.getUniqueID() != getUniqueID()) {
                        if (!profs.getAltList().contains(getUniqueID())) {
                            profs.getAltList().add(getUniqueID());
                        }

                        if (!getAltList().contains(profs.getUniqueID())) {
                            getAltList().add(profs.getUniqueID());
                        }
                    }
                });

                DBCursor dbc = DatabaseManager.getInstance().getCollection("profiles").find();

                while (dbc.hasNext()) {
                    BasicDBObject dbo = (BasicDBObject) dbc.next();

                    Profile tprof = Pure.getInstance().getProfileManager().getProfile(UUID.fromString(dbo.getString("uuid")));

                    if (tprof.getIpList().contains(getCurrentIP())) {
                        if (!getAltList().contains(tprof.getUniqueID())) {
                            if (tprof.getUniqueID() != getUniqueID()) {
                                if (!tprof.getAltList().contains(getUniqueID())) {
                                    tprof.getAltList().add(getUniqueID());

                                    Pure.getInstance().getProfileManager().saveProfile(tprof);
                                }

                                if (!getAltList().contains(tprof.getUniqueID())) {
                                    getAltList().add(tprof.getUniqueID());
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(Pure.getInstance());
    }

    public void checkForBannedAlts() {
        List<String> associatedProfiles = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                DBCursor dbc = DatabaseManager.getInstance().getCollection("profiles").find();

                while (dbc.hasNext()) {
                    BasicDBObject dbo = (BasicDBObject) dbc.next();

                    Profile tprof = Pure.getInstance().getProfileManager().getProfile(UUID.fromString(dbo.getString("uuid")));

                    boolean alert = false;

                    if (tprof.isBanned()) {
                        for (String taddress : tprof.getIpList()) {
                            if (getIpList().contains(taddress)) {
                                alert = true;
                            }
                        }

                        for (String address : getIpList()) {
                            if (tprof.getIpList().contains(address)) {
                                alert = true;
                                associatedProfiles.add(tprof.getCurrentName());
                            }
                        }
                    }

                    if (alert)
                        MessageManager.broadcast("pure.alert", "&cAlert! &a" + getCurrentName() + "&7's IP is associated with the banned account(s) &a" + associatedProfiles.toString().replace("&7(", "").replace("&7)", ""));
                }
            }
        }.runTaskAsynchronously(Pure.getInstance());
    }
}
