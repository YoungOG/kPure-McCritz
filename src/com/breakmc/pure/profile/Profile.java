package com.breakmc.pure.profile;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.punishments.*;
import com.breakmc.pure.utils.DateUtil;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.database.DatabaseManager;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@Setter

public class Profile {

    private MongoCollection<Document> pCollection = DatabaseManager.getInstance().getMongoDatabase().getCollection("profiles");
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

    public Profile(String currentName) {
        this.currentName = currentName;
    }

    public Profile(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public void loadProfileData(@Nullable ProfileRequest<Profile> callback, boolean username) {
        pCollection.find(Filters.eq((username ? "currentName" : "uniqueID"), (username ?  Pattern.compile("^" + currentName + "$", Pattern.CASE_INSENSITIVE) : uniqueID.toString()))).first((document, t) -> {
            if (t != null) {
                t.printStackTrace();
                System.out.println("Failed to load " + (username ? currentName : uniqueID) + "'s profile.");

                if (callback != null)
                    callback.onComplete(null, t);

                return;
            }

            if (document == null) {
                System.out.println("Failed to load " + (username ? currentName : uniqueID) + "'s profile.");

                if (callback != null)
                    callback.onComplete(null, null);

                return;
            }

            HashSet<UUID> altList = new HashSet<>();
            List<String> alts = (List<String>) document.get("altList");
            for (String s : alts) {
                altList.add(UUID.fromString(s));
            }

            HashSet<String> nameList = new HashSet<>();
            List<String> names = (List<String>) document.get("nameList");
            for (String s : names) {
                nameList.add(s);
            }

            HashSet<String> ipList = new HashSet<>();
            List<String> ips = (List<String>) document.get("ipList");
            for (String s : ips) {
                ipList.add(s);
            }

            ArrayList<PermanentBan> permanentBans = new ArrayList<>();
            List<Document> docs1 = (List<Document>) document.get("permanent-bans");
            for (Document doc : docs1) {
                permanentBans.add(new PermanentBan(((doc.getString("punisherUUID") != null) ? UUID.fromString(doc.getString("punisherUUID")) : null), doc.getString("reason"), doc.getString("dateCreated"), doc.getBoolean("isActive")));
            }

            ArrayList<TemporaryBan> temporaryBans = new ArrayList<>();
            List<Document> docs2 = (List<Document>) document.get("temporary-bans");
            for (Document doc : docs2) {
                temporaryBans.add(new TemporaryBan(((doc.getString("punisherUUID") != null) ? UUID.fromString(doc.getString("punisherUUID")) : null), doc.getLong("length"), doc.getString("reason"), doc.getString("dateCreated")));
            }

            ArrayList<PermanentMute> permanentMutes = new ArrayList<>();
            List<Document> docs3 = (List<Document>) document.get("permanent-mutes");
            for (Document doc : docs3) {
                permanentMutes.add(new PermanentMute(((doc.getString("punisherUUID") != null) ? UUID.fromString(doc.getString("punisherUUID")) : null), doc.getString("reason"), doc.getString("dateCreated"), doc.getBoolean("isActive")));
            }

            ArrayList<TemporaryMute> temporaryMutes = new ArrayList<>();
            List<Document> docs4 = (List<Document>) document.get("temporary-mutes");
            for (Document doc : docs4) {
                temporaryMutes.add(new TemporaryMute(((doc.getString("punisherUUID") != null) ? UUID.fromString(doc.getString("punisherUUID")) : null), doc.getLong("length"), doc.getString("reason"), doc.getString("dateCreated")));
            }

            ArrayList<Warn> warns = new ArrayList<>();
            List<Document> docs5 = (List<Document>) document.get("warns");
            for (Document doc : docs5) {
                warns.add(new Warn(((doc.getString("punisherUUID") != null) ? UUID.fromString(doc.getString("punisherUUID")) : null), doc.getString("reason"), doc.getString("dateCreated")));
            }

            ArrayList<Note> notes = new ArrayList<>();
            List<Document> docs6 = (List<Document>) document.get("notes");
            for (Document doc : docs6) {
                notes.add(new Note(((doc.getString("punisherUUID") != null) ? UUID.fromString(doc.getString("punisherUUID")) : null), doc.getString("reason"), doc.getString("dateCreated")));
            }

            setUniqueID(UUID.fromString(document.getString("uniqueID")));
            setCurrentName(document.getString("currentName"));
            setCurrentIP(document.getString("currentIP"));
            setDateCreated(document.getString("dateCreated"));
            setGroup(PlayerUtility.getGroup(currentName));
            setOnline(document.getBoolean("isOnline"));
            setPlaytime(document.getLong("playtime"));
            setLogins(document.getInteger("logins"));
            setPin(document.getString("pin"));
            setAltList(altList);
            setNameList(nameList);
            setIpList(ipList);
            setPermanentBans(permanentBans);
            setTemporaryBans(temporaryBans);
            setPermanentMutes(permanentMutes);
            setTemporaryMutes(temporaryMutes);
            setWarns(warns);
            setNotes(notes);

            if (callback != null)
                callback.onComplete(this, null);
        });
    }

    public void saveProfileData() {
        Document doc = new Document("uniqueID", uniqueID.toString());
        doc.append("currentName", currentName);
        doc.append("currentIP", currentIP);
        doc.append("isOnline", isOnline);
        doc.append("playtime", playtime);
        doc.append("logins", logins);
        doc.append("pin", pin);
        doc.append("altList", altList.stream().map(UUID::toString).collect(Collectors.toList()));
        doc.append("nameList", nameList);
        doc.append("ipList", ipList);

        List<Document> docs1 = new ArrayList<>();
        for (PermanentBan b : permanentBans) {
            Document bDoc = new Document();

            if (b.getPunisherUUID() != null) {
                bDoc.append("punisherUUID", b.getPunisherUUID().toString());
            }

            bDoc.append("reason", b.getReason());
            bDoc.append("dateCreated", b.getDateIssued());
            bDoc.append("isActive", b.isActive());
            docs1.add(bDoc);
        }
        doc.append("permanent-bans", docs1);

        List<Document> docs2 = new ArrayList<>();
        for (TemporaryBan b : temporaryBans) {
            Document bDoc = new Document();
            if (System.currentTimeMillis() >= b.getLength()) {
                b.setActive(false);
            }

            if (b.getPunisherUUID() != null) {
                bDoc.append("punisherUUID", b.getPunisherUUID().toString());
            }

            bDoc.append("length", b.getLength());
            bDoc.append("reason", b.getReason());
            bDoc.append("dateCreated", b.getDateIssued());
            bDoc.append("isActive", b.isActive());
            docs2.add(bDoc);
        }
        doc.append("temporary-bans", docs2);

        List<Document> docs3 = new ArrayList<>();
        for (PermanentMute b : permanentMutes) {
            Document bDoc = new Document();

            if (b.getPunisherUUID() != null) {
                bDoc.append("punisherUUID", b.getPunisherUUID().toString());
            }

            bDoc.append("reason", b.getReason());
            bDoc.append("dateCreated", b.getDateIssued());
            bDoc.append("isActive", b.isActive());
            docs3.add(bDoc);
        }
        doc.append("permanent-mutes", docs3);

        List<Document> docs4 = new ArrayList<>();
        for (TemporaryMute b : temporaryMutes) {
            Document bDoc = new Document();
            if (System.currentTimeMillis() >= b.getLength()) {
                b.setActive(false);
            }

            if (b.getPunisherUUID() != null) {
                bDoc.append("punisherUUID", b.getPunisherUUID().toString());
            }

            bDoc.append("length", b.getLength());
            bDoc.append("reason", b.getReason());
            bDoc.append("dateCreated", b.getDateIssued());
            bDoc.append("isActive", b.isActive());
            docs4.add(bDoc);
        }
        doc.append("temporary-mutes", docs4);

        List<Document> docs5 = new ArrayList<>();
        for (Warn b : warns) {
            Document bDoc = new Document();

            if (b.getPunisherUUID() != null) {
                bDoc.append("punisherUUID", b.getPunisherUUID().toString());
            }

            bDoc.append("reason", b.getReason());
            bDoc.append("dateCreated", b.getDateIssued());
            docs5.add(bDoc);
        }
        doc.append("warns", docs5);

        List<Document> docs6 = new ArrayList<>();
        for (Note b : notes) {
            Document bDoc = new Document();

            if (b.getPunisherUUID() != null) {
                bDoc.append("punisherUUID", b.getPunisherUUID().toString());
            }

            bDoc.append("reason", b.getReason());
            bDoc.append("dateCreated", b.getDateIssued());
            docs6.add(bDoc);
        }
        doc.append("notes", docs6);

        pCollection.find(Filters.eq("uniqueID", uniqueID.toString())).first(new SingleResultCallback<Document>() {
            @Override
            public void onResult(Document document, Throwable throwable) {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    if (document != null) {
                        pCollection.replaceOne(Filters.eq("uniqueID", uniqueID.toString()), doc, new UpdateOptions().upsert(true), new SingleResultCallback<UpdateResult>() {
                            @Override
                            public void onResult(UpdateResult updateResult, Throwable t) {
                                if (t != null) {
                                    t.printStackTrace();
                                }
                            }
                        });
                    } else {
                        pCollection.insertOne(doc, new SingleResultCallback<Void>() {
                            @Override
                            public void onResult(Void result, Throwable t) {
                                if (t != null) {
                                    t.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        });
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
}
