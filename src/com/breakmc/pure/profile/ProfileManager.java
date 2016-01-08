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
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
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

        loadProfiles();
    }

    public void loadProfiles() {
        DBCursor dbc = pCollection.find();

        main.getLogger().log(Level.INFO, "Loading " + dbc.count() + " profiles.");

        while (dbc.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc.next();

            Profile prof = new Profile(UUID.fromString(dbo.getString("uuid")));

            prof.setCurrentName(dbo.getString("currentName"));
            prof.setCurrentIP(dbo.getString("currentIP"));
            prof.setDateCreated(dbo.getString("dateCreated"));
            prof.setGroup(PlayerUtility.getGroup(prof.getCurrentName()));
            prof.setOnline(dbo.getBoolean("isOnline"));
            prof.setPlaytime(dbo.getLong("playtime"));
            prof.setLogins(dbo.getInt("logins"));
            prof.setPin(dbo.getString("pin"));

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

            prof.setPermanentBans(permanentBans);
            prof.setTemporaryBans(temporaryBans);
            prof.setPermanentMutes(permanentMutes);
            prof.setTemporaryMutes(temporaryMutes);
            prof.setWarns(warns);
            prof.setNotes(notes);
            prof.setAltList(alts);
            prof.setNameList(names);
            prof.setIpList(ips);

            loadedProfiles.add(prof);
        }
    }

    public void saveProfiles() {
        main.getLogger().log(Level.INFO, "Saving " + getLoadedProfiles().size() + " profiles.");

        for (Profile prof : getLoadedProfiles()) {
            DBCursor dbc = pCollection.find(new BasicDBObject("uuid", prof.getUniqueID().toString()));

            BasicDBObject dbo = new BasicDBObject("uuid", prof.getUniqueID().toString());
            dbo.put("currentName", prof.getCurrentName());
            dbo.put("currentIP", prof.getCurrentIP());
            dbo.put("dateCreated", prof.getDateCreated());
            prof.setGroup(PlayerUtility.getGroup(prof.getCurrentName()));
            dbo.put("isOnline", prof.isOnline());
            dbo.put("playtime", prof.getPlaytime());
            dbo.put("logins", prof.getLogins());
            dbo.put("pin", prof.getPin());

            BasicDBList dbl1 = prof.getAltList().stream().map(UUID::toString).collect(Collectors.toCollection(BasicDBList::new));
            dbo.put("altList", dbl1);

            BasicDBList dbl2 = prof.getNameList().stream().collect(Collectors.toCollection(BasicDBList::new));
            dbo.put("nameList", dbl2);

            BasicDBList dbl3 = prof.getIpList().stream().collect(Collectors.toCollection(BasicDBList::new));
            dbo.put("ipList", dbl3);

            BasicDBList dbl4 = new BasicDBList();
            for (PermanentBan b : prof.getPermanentBans()) {
                BasicDBObject bdo = new BasicDBObject();
                bdo.append("punishedUUID", b.getPunishedUUID().toString());
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
            for (TemporaryBan b : prof.getTemporaryBans()) {
                if (System.currentTimeMillis() >= b.getLength()) {
                    b.setActive(false);
                }

                BasicDBObject bdo = new BasicDBObject();
                bdo.append("punishedUUID", b.getPunishedUUID().toString());
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
            for (PermanentMute m : prof.getPermanentMutes()) {
                BasicDBObject bdo = new BasicDBObject();
                bdo.append("punishedUUID", m.getPunishedUUID().toString());
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
            for (TemporaryMute m : prof.getTemporaryMutes()) {
                if (System.currentTimeMillis() >= m.getLength()) {
                    m.setActive(false);
                }

                BasicDBObject bdo = new BasicDBObject();
                bdo.append("punishedUUID", m.getPunishedUUID().toString());
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
            for (Warn w : prof.getWarns()) {
                BasicDBObject bdo = new BasicDBObject();
                bdo.append("punishedUUID", w.getPunishedUUID().toString());
                if (w.getPunisherUUID() != null) {
                    bdo.append("punisherUUID", w.getPunisherUUID().toString());
                }
                bdo.append("reason", w.getReason());
                bdo.append("dateCreated", w.getDateIssued());
                dbl8.add(bdo);
            }

            dbo.put("warns", dbl8);
            BasicDBList dbl9 = new BasicDBList();
            for (Note n : prof.getNotes()) {
                BasicDBObject bdo = new BasicDBObject();
                bdo.append("punishedUUID", n.getPunishedUUID().toString());
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
    }

    public void loadProfile(UUID id) {
        DBCursor dbc = pCollection.find(new BasicDBObject("uuid", id.toString()));

        if (dbc.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc.next();

            Profile prof = new Profile(UUID.fromString(dbo.getString("uuid")));

            prof.setCurrentName(dbo.getString("currentName"));
            prof.setCurrentIP(dbo.getString("currentIP"));
            prof.setDateCreated(dbo.getString("dateCreated"));
            prof.setGroup(PlayerUtility.getGroup(prof.getCurrentName()));
            prof.setOnline(dbo.getBoolean("isOnline"));
            prof.setPlaytime(dbo.getLong("playtime"));
            prof.setLogins(dbo.getInt("logins"));
            prof.setPin(dbo.getString("pin"));

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

            prof.setPermanentBans(permanentBans);
            prof.setTemporaryBans(temporaryBans);
            prof.setPermanentMutes(permanentMutes);
            prof.setTemporaryMutes(temporaryMutes);
            prof.setWarns(warns);
            prof.setNotes(notes);
            prof.setAltList(alts);
            prof.setNameList(names);
            prof.setIpList(ips);

            getLoadedProfiles().add(prof);
        }
    }

    public void saveProfile(Profile prof) {
        DBCursor dbc = pCollection.find(new BasicDBObject("uuid", prof.getUniqueID().toString()));

        BasicDBObject dbo = new BasicDBObject("uuid", prof.getUniqueID().toString());
        dbo.put("currentName", prof.getCurrentName());
        dbo.put("currentIP", prof.getCurrentIP());
        dbo.put("dateCreated", prof.getDateCreated());
        prof.setGroup(PlayerUtility.getGroup(prof.getCurrentName()));
        dbo.put("isOnline", prof.isOnline());
        dbo.put("playtime", prof.getPlaytime());
        dbo.put("logins", prof.getLogins());
        dbo.put("pin", prof.getPin());

        BasicDBList dbl1 = prof.getAltList().stream().map(UUID::toString).collect(Collectors.toCollection(BasicDBList::new));
        dbo.put("altList", dbl1);

        BasicDBList dbl2 = prof.getNameList().stream().collect(Collectors.toCollection(BasicDBList::new));
        dbo.put("nameList", dbl2);

        BasicDBList dbl3 = prof.getIpList().stream().collect(Collectors.toCollection(BasicDBList::new));
        dbo.put("ipList", dbl3);

        BasicDBList dbl4 = new BasicDBList();
        for (PermanentBan b : prof.getPermanentBans()) {
            BasicDBObject bdo = new BasicDBObject();
            bdo.append("punishedUUID", b.getPunishedUUID());
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
        for (TemporaryBan b : prof.getTemporaryBans()) {
            if (System.currentTimeMillis() >= b.getLength()) {
                b.setActive(false);
            }

            BasicDBObject bdo = new BasicDBObject();
            bdo.append("punishedUUID", b.getPunishedUUID());
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
        for (PermanentMute m : prof.getPermanentMutes()) {
            BasicDBObject bdo = new BasicDBObject();
            bdo.append("punishedUUID", m.getPunishedUUID());
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
        for (TemporaryMute m : prof.getTemporaryMutes()) {
            if (System.currentTimeMillis() >= m.getLength()) {
                m.setActive(false);
            }

            BasicDBObject bdo = new BasicDBObject();
            bdo.append("punishedUUID", m.getPunishedUUID());
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
        for (Warn w : prof.getWarns()) {
            BasicDBObject bdo = new BasicDBObject();
            bdo.append("punishedUUID", w.getPunishedUUID());
            if (w.getPunisherUUID() != null) {
                bdo.append("punisherUUID", w.getPunisherUUID().toString());
            }
            bdo.append("reason", w.getReason());
            bdo.append("dateCreated", w.getDateIssued());
            dbl8.add(bdo);
        }
        dbo.put("warns", dbl8);
        BasicDBList dbl9 = new BasicDBList();
        for (Note n : prof.getNotes()) {
            BasicDBObject bdo = new BasicDBObject();
            bdo.append("punishedUUID", n.getPunishedUUID());
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

    public void reloadProfile(Profile prof, boolean save) {
        if (getLoadedProfiles().contains(prof)) {
            getLoadedProfiles().remove(prof);
        }

        if (save)
            saveProfile(prof);

        loadProfile(prof.getUniqueID());
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

        saveProfile(prof);

        getLoadedProfiles().add(prof);
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
        return pCollection.find(new BasicDBObject("uuid", id.toString())).hasNext();
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

        return null;
    }

    public Profile getProfile(String name) {
        for (Profile prof : getLoadedProfiles()) {
            if (prof.getCurrentName().equalsIgnoreCase(name)) {
                return prof;
            }
        }

        return null;
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
