package com.mccritz.kpure.profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.punishment.punishments.IPBan;
import com.mccritz.kpure.punishment.punishments.PermanentBan;
import com.mccritz.kpure.punishment.punishments.PermanentMute;
import com.mccritz.kpure.punishment.punishments.TemporaryBan;
import com.mccritz.kpure.punishment.punishments.TemporaryMute;
import com.mccritz.kpure.utils.DateUtil;
import com.mccritz.kpure.utils.MessageManager;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Profile {

    private UUID uniqueID;
    private String currentName;
    private String currentIP;
    private String dateCreated;
    private String group;
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

    public Profile(String currentName) {
	this.currentName = currentName;
    }

    public Profile(UUID uniqueID) {
	this.uniqueID = uniqueID;
    }

    public Profile(Document document) {
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
	    permanentBans.add(new PermanentBan(
		    doc.getString("punisherUUID") != null ? UUID.fromString(doc.getString("punisherUUID")) : null,
		    doc.getString("reason"), doc.getString("dateCreated"), doc.getBoolean("isActive")));
	}

	ArrayList<TemporaryBan> temporaryBans = new ArrayList<>();
	List<Document> docs2 = (List<Document>) document.get("temporary-bans");
	for (Document doc : docs2) {
	    temporaryBans.add(new TemporaryBan(
		    doc.getString("punisherUUID") != null ? UUID.fromString(doc.getString("punisherUUID")) : null,
		    doc.getLong("length"), doc.getString("reason"), doc.getString("dateCreated")));
	}

	ArrayList<PermanentMute> permanentMutes = new ArrayList<>();
	List<Document> docs3 = (List<Document>) document.get("permanent-mutes");
	for (Document doc : docs3) {
	    permanentMutes.add(new PermanentMute(
		    doc.getString("punisherUUID") != null ? UUID.fromString(doc.getString("punisherUUID")) : null,
		    doc.getString("reason"), doc.getString("dateCreated"), doc.getBoolean("isActive")));
	}

	ArrayList<TemporaryMute> temporaryMutes = new ArrayList<>();
	List<Document> docs4 = (List<Document>) document.get("temporary-mutes");
	for (Document doc : docs4) {
	    temporaryMutes.add(new TemporaryMute(
		    doc.getString("punisherUUID") != null ? UUID.fromString(doc.getString("punisherUUID")) : null,
		    doc.getLong("length"), doc.getString("reason"), doc.getString("dateCreated")));
	}

	setUniqueID(UUID.fromString(document.getString("uniqueID")));
	setCurrentName(document.getString("currentName"));
	setCurrentIP(document.getString("currentIP"));
	setDateCreated(document.getString("dateCreated"));
	setGroup(document.getString("group"));
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
    }

    public List<Player> getOnlineAlts() {
	return altList.stream().filter(id -> Bukkit.getPlayer(id) != null).map(Bukkit::getPlayer)
		.collect(Collectors.toList());
    }

    public List<String> getOnlineAltsNames() {
	return getOnlineAlts().stream().map(Player::getName).collect(Collectors.toList());
    }

    public List<String> getKnownAltsNames() {
	return getAltList().stream().map(id -> Bukkit.getOfflinePlayer(id).getName()).collect(Collectors.toList());
    }

    public boolean isBanned() {
	return isPermanentlyBanned() || isTemporarilyBanned()
		|| kPure.getInstance().getPunishmentManager().isIPBanned(getCurrentIP());
    }

    public boolean isPermanentlyBanned() {
	for (PermanentBan b : permanentBans) {
	    if (b.isActive())
		return true;
	}

	return false;
    }

    public PermanentBan getActivePermanentBan() {
	for (PermanentBan b : permanentBans) {
	    if (b.isActive())
		return b;
	}

	return null;
    }

    public boolean isTemporarilyBanned() {
	for (TemporaryBan b : temporaryBans) {
	    if (b.isActive())
		return true;
	}

	return false;
    }

    public TemporaryBan getActiveTemporaryBan() {
	for (TemporaryBan b : temporaryBans) {
	    if (b.isActive())
		return b;
	}

	return null;
    }

    public boolean isMuted() {
	return isPermanentlyMuted() || isTemporarilyMuted();
    }

    public boolean isPermanentlyMuted() {
	for (PermanentMute m : permanentMutes) {
	    if (m.isActive())
		return true;
	}

	return false;
    }

    public PermanentMute getActivePermanentMute() {
	for (PermanentMute m : permanentMutes) {
	    if (m.isActive())
		return m;
	}

	return null;
    }

    public boolean isTemporarilyMuted() {
	for (TemporaryMute m : temporaryMutes) {
	    if (m.isActive())
		return true;
	}

	return false;
    }

    public TemporaryMute getActiveTemporaryMute() {
	for (TemporaryMute m : temporaryMutes) {
	    if (m.isActive())
		return m;
	}

	return null;
    }

    public boolean hasPin() {
	return pin != null && !pin.isEmpty();
    }

    public void lookup(CommandSender sender) {
	boolean hasElevatedPermission = sender.hasPermission("kpure.lookup.admin");

	MessageManager.sendMessage(sender,
		"&c" + getCurrentName() + (hasElevatedPermission ? " &7(&c" + getCurrentIP() + "&7)" : ""));
	// MessageManager.sendMessage(sender, "&7Online: " + (isOnline ?
	// "&aTrue" : "&cFalse"));
	MessageManager.sendMessage(sender,
		"&7Online: " + (Bukkit.getPlayer(this.uniqueID) != null ? "&aTrue" : "&cFalse"));
	MessageManager.sendMessage(sender, "&7Playtime: &c" + DateUtil.readableTime(getPlaytime() * 1000));
	MessageManager.sendMessage(sender, "&7Rank: &c" + getGroup());

	if (hasElevatedPermission) {
	    MessageManager.sendMessage(sender, "&7Past IPs(&c" + getIpList().size() + "&7): &c"
		    + getIpList().toString().replace("[", "").replace("]", ""));
	}

	MessageManager.sendMessage(sender, "&7Past Names&7(&c" + getNameList().size() + "&7): &c"
		+ getNameList().toString().replace("[", "").replace("]", ""));

	if (getAltList().size() > 0) {
	    MessageManager.sendMessage(sender, "&7Known Alts&7(&c" + getAltList().size() + "&7): &c"
		    + getKnownAltsNames().toString().replace("[", "").replace("]", ""));

	    if (getOnlineAlts().size() > 0) {
		MessageManager.sendMessage(sender, "&7Online Alts&7(&c" + getOnlineAlts().size() + "&7): &c"
			+ getOnlineAltsNames().toString().replace("[", "").replace("]", ""));
	    }
	}

	MessageManager.sendMessage(sender, "&7Banned: &c" + isBanned());
	if (isBanned()) {
	    if (kPure.getInstance().getPunishmentManager().isIPBanned(getCurrentIP())) {
		IPBan b = kPure.getInstance().getPunishmentManager().getActiveIPBan(getCurrentIP());
		MessageManager.sendMessage(sender, "  &7Type: &cBlacklist");
		MessageManager.sendMessage(sender, "  &7Reason: &c" + b.getReason());
		MessageManager.sendMessage(sender, "  &7Date: &c" + b.getDateIssued());
		MessageManager.sendMessage(sender, "  &7By: &c" + b.getPunisherName());
	    }
	    if (isPermanentlyBanned()) {
		PermanentBan b = getActivePermanentBan();
		MessageManager.sendMessage(sender, "  &7Type: &cPermanent");
		MessageManager.sendMessage(sender, "  &7Reason: &c" + b.getReason());
		MessageManager.sendMessage(sender, "  &7Date: &c" + b.getDateIssued());
		MessageManager.sendMessage(sender, "  &7By: &c" + b.getPunisherName());
	    }

	    if (isTemporarilyBanned()) {
		TemporaryBan tb = getActiveTemporaryBan();
		MessageManager.sendMessage(sender, "  &7Type: &cTemporary: " + DateUtil.formatDateDiff(tb.getLength()));
		MessageManager.sendMessage(sender, "  &7Reason: &c" + tb.getReason());
		MessageManager.sendMessage(sender, "  &7Date: &c" + tb.getDateIssued());
		MessageManager.sendMessage(sender, "  &7By: &c" + tb.getPunisherName());
	    }
	}

	MessageManager.sendMessage(sender, "&7Muted: &c" + isMuted());
	if (isMuted()) {
	    if (isPermanentlyMuted()) {
		PermanentMute b = getActivePermanentMute();
		MessageManager.sendMessage(sender, "  &7Type: &cPermanent");
		MessageManager.sendMessage(sender, "  &7Reason: &c" + b.getReason());
		MessageManager.sendMessage(sender, "  &7Date: &c" + b.getDateIssued());
		MessageManager.sendMessage(sender, "  &7By: &c" + b.getPunisherName());
	    }

	    if (isTemporarilyMuted()) {
		TemporaryMute tb = getActiveTemporaryMute();
		MessageManager.sendMessage(sender, "  &7Type: &cTemporary: " + DateUtil.formatDateDiff(tb.getLength()));
		MessageManager.sendMessage(sender, "  &7Reason: &c" + tb.getReason());
		MessageManager.sendMessage(sender, "  &7Date: &c" + tb.getDateIssued());
		MessageManager.sendMessage(sender, "  &7By: &c" + tb.getPunisherName());
	    }
	}
    }
}
