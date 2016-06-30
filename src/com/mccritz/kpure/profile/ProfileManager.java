package com.mccritz.kpure.profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.punishment.punishments.IPBan;
import com.mccritz.kpure.punishment.punishments.PermanentBan;
import com.mccritz.kpure.punishment.punishments.PermanentMute;
import com.mccritz.kpure.punishment.punishments.TemporaryBan;
import com.mccritz.kpure.punishment.punishments.TemporaryMute;
import com.mccritz.kpure.utils.DateUtil;
import com.mccritz.kpure.utils.MessageManager;

import mkremins.fanciful.FancyMessage;

public class ProfileManager {

    private kPure main = kPure.getInstance();
    private List<Profile> loadedProfiles = new ArrayList<>();

    public ProfileManager() {
	new BukkitRunnable() {
	    @Override
	    public void run() {
		getLoadedProfiles().stream().filter(Profile::isOnline)
			.forEach(prof -> prof.setPlaytime(prof.getPlaytime() + 1));
	    }
	}.runTaskTimerAsynchronously(main, 0L, 20);
    }

    public void saveProfiles() {
	main.getLogger().log(Level.INFO, "Saving " + getLoadedProfiles().size() + " profiles.");

	int count = 0;

	for (Profile prof : getLoadedProfiles()) {
	    count++;
	    prof.setOnline(false);
	    prof.saveProfileData();
	}

	getLoadedProfiles().clear();

	main.getLogger().log(Level.INFO, "Saved " + count + " profiles.");
    }

    public void loadProfile(Profile profile, boolean check) {
	getLoadedProfiles().add(profile);

	if (check) {
	    Bukkit.getLogger().log(Level.INFO, "Performing check for " + profile.getCurrentName() + ".");

	    kPure.getInstance().getPunishmentManager().checkForValidAlts(profile.getUniqueID());
	    kPure.getInstance().getPunishmentManager().checkForBannedAlts(profile.getUniqueID());
	}
    }

    public ProfileLoader requestProfile(String name, ProfileRequest<Profile> callback) {
	Profile profile = getProfile(name);

	if (profile != null)
	    return new BasicProfileLoader(profile, callback);

	return new BasicProfileLoader(name, callback);
    }

    public ProfileLoader requestProfile(UUID id, ProfileRequest<Profile> callback) {
	Profile profile = getProfile(id);

	if (profile != null)
	    return new BasicProfileLoader(profile, callback);

	return new BasicProfileLoader(id, callback);
    }

    public void createProfile(Player p, String ip) {
	Profile prof = new Profile(p.getUniqueId());

	prof.setCurrentIP(ip);
	prof.setCurrentName(p.getName());
	prof.setDateCreated(DateUtil.getProperDate(new Date()));
	prof.setGroup("disabled");
	prof.setOnline(p.isOnline());
	prof.setPlaytime(0);
	prof.setLogins(1);
	prof.setPin("");
	prof.getIpList().add(prof.getCurrentIP());
	prof.getNameList().add(prof.getCurrentName());
	prof.saveProfileData();

	getLoadedProfiles().add(prof);

	Bukkit.getLogger().log(Level.INFO, "Performing check for " + prof.getCurrentName() + ".");

	kPure.getInstance().getPunishmentManager().checkForValidAlts(prof.getUniqueID());
	kPure.getInstance().getPunishmentManager().checkForBannedAlts(prof.getUniqueID());
    }

    public void createSimpleProfile(UUID id, String name) {
	Profile profile = new Profile(id);
	profile.setCurrentName(name);
	profile.setCurrentIP("0.0.0.0");
	profile.setDateCreated(DateUtil.getProperDate(new Date()));
	profile.setGroup("none");
	profile.setOnline(false);
	profile.setPlaytime(0);
	profile.setLogins(0);
	profile.setPin("");
	profile.getNameList().add(name);
	profile.saveProfileData();

	getLoadedProfiles().add(profile);

	Bukkit.getLogger().log(Level.INFO, "Performing check for " + profile.getCurrentName() + ".");

	kPure.getInstance().getPunishmentManager().checkForValidAlts(id);
	kPure.getInstance().getPunishmentManager().checkForBannedAlts(id);
    }

    public void lookup(CommandSender sender, String address) {
	List<Profile> foundProfiles = loadedProfiles.stream()
		.filter(prof -> prof.getIpList().contains(address) || prof.getCurrentIP().equalsIgnoreCase(address))
		.collect(Collectors.toList());

	if (foundProfiles.size() <= 0) {
	    MessageManager.sendMessage(sender,
		    "&cCould not find any accounts associated with the IP \"" + address + "\"");
	}

	MessageManager.sendMessage(sender,
		"&7Found (&a" + foundProfiles.size() + "&7) accounts associated with the IP (&a" + address + "&7):");
	MessageManager.sendMessage(sender, "&cNote: Hover over for more information, click to preform lookup command.");

	for (Profile prof : foundProfiles) {
	    FancyMessage fm = new FancyMessage(ChatColor.translateAlternateColorCodes('&',
		    "&7- &b&l" + prof.getCurrentName() + " &7(&a" + prof.getCurrentIP() + "&7)"));
	    fm.command("/lookup " + prof.getCurrentName());
	    fm.tooltip(translatedColors(getProfileInformation(prof)));
	    fm.send(sender);
	}
    }

    public boolean hasLoadedProfile(UUID id) {
	for (Profile prof : getLoadedProfiles()) {
	    if (prof.getUniqueID().equals(id))
		return true;
	}

	return false;
    }

    public Profile getProfile(UUID id) {
	for (Profile prof : getLoadedProfiles()) {
	    if (prof.getUniqueID().equals(id))
		return prof;
	}

	return null;
    }

    public Profile getProfile(String name) {
	for (Profile prof : getLoadedProfiles()) {
	    if (prof.getCurrentName().equalsIgnoreCase(name))
		return prof;
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
	lines.add("&7Past IPs(&a" + prof.getIpList().size() + "&7): &a"
		+ prof.getIpList().toString().replace("[", "").replace("]", ""));
	lines.add("&7Past Names&7(&a" + prof.getNameList().size() + "&7): &a"
		+ prof.getNameList().toString().replace("[", "").replace("]", ""));

	if (prof.getAltList().size() > 0) {
	    lines.add("&7Known Alts&7(&a" + prof.getAltList().size() + "&7): &a"
		    + prof.getKnownAltsNames().toString().replace("[", "").replace("]", ""));

	    if (prof.getOnlineAlts().size() > 0) {
		lines.add("&7Online Alts&7(&a" + prof.getOnlineAlts().size() + "&7): &a"
			+ prof.getOnlineAltsNames().toString().replace("[", "").replace("]", ""));
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
