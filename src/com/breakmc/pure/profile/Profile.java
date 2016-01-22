package com.breakmc.pure.profile;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.punishments.*;
import com.breakmc.pure.utils.DateUtil;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.database.DatabaseManager;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
@Setter

public class Profile {

    private DBCollection pCollection = DatabaseManager.getInstance().getCollection("profiles");
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
    }

    public Profile(String currentName) {
        this.currentName = currentName;
    }

    public boolean loadProfileData(boolean username) {
        DBCursor dbc;

        if (username) {
            dbc = pCollection.find(new BasicDBObject("currentName", currentName));
        } else {
            dbc = pCollection.find(new BasicDBObject("uniqueID", uniqueID.toString()));
        }

        if (dbc.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc.next();

            this.uniqueID = UUID.fromString(dbo.getString("uniqueID"));
            this.currentName = dbo.getString("currentName");
            this.currentIP = dbo.getString("currentIP");
            this.dateCreated = dbo.getString("dateCreated");
            this.group = PlayerUtility.getGroup(currentName);
            this.isOnline = dbo.getBoolean("isOnline");
            this.playtime = dbo.getLong("playtime");
            this.logins = dbo.getInt("logins");
            this.pin = dbo.getString("pin");

            BasicDBList dbol1 = (BasicDBList) dbo.get("altList");
            HashSet<UUID> altList = new HashSet<>();
            if (dbol1 != null) {
                altList.addAll(dbol1.stream().map(obj -> UUID.fromString((String) obj)).collect(Collectors.toList()));
            }

            BasicDBList dbol2 = (BasicDBList) dbo.get("nameList");
            HashSet<String> nameList = new HashSet<>();
            if (dbol2 != null) {
                nameList.addAll(dbol2.stream().map(obj -> (String) obj).collect(Collectors.toList()));
            }

            BasicDBList dbol3 = (BasicDBList) dbo.get("ipList");
            HashSet<String> ipList = new HashSet<>();
            if (dbol3 != null) {
                ipList.addAll(dbol3.stream().map(obj -> (String) obj).collect(Collectors.toList()));
            }

            BasicDBList dbol4 = (BasicDBList) dbo.get("permanent-bans");
            ArrayList<PermanentBan> permanentBans = new ArrayList<>();
            if (dbol4 != null) {
                for (Object obj : dbol4) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    permanentBans.add(new PermanentBan(((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getString("reason"), bdo.getString("dateCreated"), bdo.getBoolean("isActive")));
                }
            }

            BasicDBList dbol5 = (BasicDBList) dbo.get("temporary-bans");
            ArrayList<TemporaryBan> temporaryBans = new ArrayList<>();
            if (dbol5 != null) {
                for (Object obj : dbol5) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    temporaryBans.add(new TemporaryBan(((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getLong("length"), bdo.getString("reason"), bdo.getString("dateCreated")));
                }
            }

            BasicDBList dbol6 = (BasicDBList) dbo.get("permanent-mutes");
            ArrayList<PermanentMute> permanentMutes = new ArrayList<>();
            if (dbol6 != null) {
                for (Object obj : dbol6) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    permanentMutes.add(new PermanentMute(((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getString("reason"), bdo.getString("dateCreated"), bdo.getBoolean("isActive")));
                }
            }

            BasicDBList dbol7 = (BasicDBList) dbo.get("temporary-mutes");
            ArrayList<TemporaryMute> temporaryMutes = new ArrayList<>();
            if (dbol7 != null) {
                for (Object obj : dbol7) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    temporaryMutes.add(new TemporaryMute(((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getLong("length"), bdo.getString("reason"), bdo.getString("dateCreated")));
                }
            }

            BasicDBList dbol8 = (BasicDBList) dbo.get("warns");
            ArrayList<Warn> warns = new ArrayList<>();
            if (dbol8 != null) {
                for (Object obj : dbol8) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    warns.add(new Warn(((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getString("reason"), bdo.getString("dateCreated")));
                }
            }

            BasicDBList dbol9 = (BasicDBList) dbo.get("notes");
            ArrayList<Note> notes = new ArrayList<>();
            if (dbol9 != null) {
                for (Object obj : dbol9) {
                    BasicDBObject bdo = (BasicDBObject) obj;
                    notes.add(new Note(((bdo.getString("punisherUUID") != null) ? UUID.fromString(bdo.getString("punisherUUID")) : null), bdo.getString("reason"), bdo.getString("dateCreated")));
                }
            }

            this.permanentBans = permanentBans;
            this.temporaryBans = temporaryBans;
            this.permanentMutes = permanentMutes;
            this.temporaryMutes = temporaryMutes;
            this.warns = warns;
            this.notes = notes;
            this.altList = altList;
            this.nameList = nameList;
            this.ipList = ipList;

            return true;
        } else {
            Pure.getInstance().getLogger().log(Level.WARNING, "Could not load profile data for " + uniqueID);
            return false;
        }
    }

    public void saveProfileData() {
        DBCursor dbc = pCollection.find(new BasicDBObject("uniqueID", uniqueID.toString()));

        BasicDBObject dbo = new BasicDBObject("uniqueID", uniqueID.toString());
        dbo.put("currentName", currentName);
        dbo.put("currentIP", currentIP);
        dbo.put("dateCreated", dateCreated);
        dbo.put("isOnline", isOnline);
        dbo.put("playtime", playtime);
        dbo.put("logins", logins);
        dbo.put("pin", pin);

        BasicDBList dbl1 = altList.stream().map(UUID::toString).collect(Collectors.toCollection(BasicDBList::new));
        dbo.put("altList", dbl1);

        BasicDBList dbl2 = nameList.stream().collect(Collectors.toCollection(BasicDBList::new));
        dbo.put("nameList", dbl2);

        BasicDBList dbl3 = ipList.stream().collect(Collectors.toCollection(BasicDBList::new));
        dbo.put("ipList", dbl3);

        BasicDBList dbl4 = new BasicDBList();
        for (PermanentBan b : permanentBans) {
            BasicDBObject bdo = new BasicDBObject();
            if (b.getPunisherUUID() != null) {
                bdo.append("punisherUUID", b.getPunisherUUID().toString());
            }
            bdo.append("reason", b.getReason());
            bdo.append("dateCreated", b.getDateIssued());
            bdo.append("isActive", b.isActive());
            dbl4.add(bdo);
        }
        dbo.put("permanent-bans", dbl4);

        BasicDBList dbl5 = new BasicDBList();
        for (TemporaryBan b : temporaryBans) {
            if (System.currentTimeMillis() >= b.getLength()) {
                b.setActive(false);
            }

            BasicDBObject bdo = new BasicDBObject();
            if (b.getPunisherUUID() != null) {
                bdo.append("punisherUUID", b.getPunisherUUID().toString());
            }
            bdo.append("length", b.getLength());
            bdo.append("reason", b.getReason());
            bdo.append("dateCreated", b.getDateIssued());
            dbl5.add(bdo);
        }
        dbo.put("temporary-bans", dbl5);

        BasicDBList dbl6 = new BasicDBList();
        for (PermanentMute m : permanentMutes) {
            BasicDBObject bdo = new BasicDBObject();
            if (m.getPunisherUUID() != null) {
                bdo.append("punisherUUID", m.getPunisherUUID().toString());
            }
            bdo.append("reason", m.getReason());
            bdo.append("dateCreated", m.getDateIssued());
            bdo.append("isActive", m.isActive());
            dbl6.add(bdo);
        }
        dbo.put("permanent-mutes", dbl6);

        BasicDBList dbl7 = new BasicDBList();
        for (TemporaryMute m : temporaryMutes) {
            if (System.currentTimeMillis() >= m.getLength()) {
                m.setActive(false);
            }

            BasicDBObject bdo = new BasicDBObject();
            if (m.getPunisherUUID() != null) {
                bdo.append("punisherUUID", m.getPunisherUUID().toString());
            }
            bdo.append("length", m.getLength());
            bdo.append("reason", m.getReason());
            bdo.append("dateCreated", m.getDateIssued());
            dbl7.add(bdo);
        }
        dbo.put("temporary-mutes", dbl7);

        BasicDBList dbl8 = new BasicDBList();
        for (Warn w : warns) {
            BasicDBObject bdo = new BasicDBObject();
            if (w.getPunisherUUID() != null) {
                bdo.append("punisherUUID", w.getPunisherUUID().toString());
            }
            bdo.append("reason", w.getReason());
            bdo.append("dateCreated", w.getDateIssued());
            dbl8.add(bdo);
        }

        dbo.put("warns", dbl8);
        BasicDBList dbl9 = new BasicDBList();
        for (Note n : notes) {
            BasicDBObject bdo = new BasicDBObject();
            if (n.getPunisherUUID() != null) {
                bdo.append("punisherUUID", n.getPunisherUUID().toString());
            }
            bdo.append("reason", n.getReason());
            bdo.append("dateCreated", n.getDateIssued());
            dbl9.add(bdo);
        }
        dbo.put("notes", dbl9);

        if (dbc.hasNext()) {
            pCollection.update(dbc.next(), dbo);
        } else {
            pCollection.insert(dbo);
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
        return pin != null && !pin.equalsIgnoreCase("");
    }

    public void lookup(CommandSender sender) {
        boolean hasElevatedPermission = sender.hasPermission("pure.lookup.admin");

        MessageManager.sendMessage(sender, "&b&l" + getCurrentName() + (hasElevatedPermission ? " &7(&a" + getCurrentIP() + "&7)" : ""));
        MessageManager.sendMessage(sender, "&7Online: " + (isOnline ? "&aTrue" : "&cFalse"));
        MessageManager.sendMessage(sender, "&7Playtime: &a" + DateUtil.readableTime(getPlaytime() * 1000));
        MessageManager.sendMessage(sender, "&7Rank: &a" + getGroup());
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
}
