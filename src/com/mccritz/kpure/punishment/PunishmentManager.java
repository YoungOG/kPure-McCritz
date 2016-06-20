package com.mccritz.kpure.punishment;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.profile.ProfileRequest;
import com.mccritz.kpure.punishment.punishments.*;
import com.mccritz.kpure.utils.DateUtil;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PunishmentManager {

    private kPure main = kPure.getInstance();
    private ProfileManager pm = kPure.getInstance().getProfileManager();
    private MongoCollection<Document> bCollection = main.getMongoDatabase().getCollection("ipbans");
    private MongoCollection<Document> pCollection = main.getMongoDatabase().getCollection("profiles");
    private final List<IPBan> ipBanList = new ArrayList<>();

    public PunishmentManager() {
        bCollection.find().into(new ArrayList<>(), new SingleResultCallback<ArrayList<Document>>() {
            @Override
            public void onResult(ArrayList<Document> documents, Throwable throwable) {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    for (Document doc : documents) {
                        ipBanList.add(new IPBan(doc.getString("address"), (doc.getString("punisherUUID") != null ? UUID.fromString(doc.getString("punisherUUID")) : null), doc.getString("reason"), doc.getString("dateIssued"), doc.getBoolean("isActive")));
                    }

                    System.out.println("Loaded " + ipBanList.size() + " ip-bans into the blacklist.");
                }
            }
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                saveIPBans();
            }
        }.runTaskTimer(kPure.getInstance(), 0L, 300 * 20);
    }

    public void permanentlyBan(CommandSender sender, Profile profile, String reason) {
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (profile != null) {
            if (profile.isBanned()) {
                MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is already banned.");
                return;
            }

            profile.getPermanentBans().add(new PermanentBan(punisher, reason, DateUtil.getProperDate(new Date()), true));
            profile.saveProfileData();

            if (Bukkit.getPlayer(profile.getUniqueID()) != null) {
                Bukkit.getPlayer(profile.getUniqueID()).kickPlayer(ChatColor.RED + "You have been banned from McCritZ.\nYou can purchase an unban at store.mccritz.com");
            }

            MessageManager.broadcast("&c" + profile.getCurrentName() + " &7has been banned by &c" + sender.getName() + "&7.");
        }
    }

    public void temporarilyBan(CommandSender sender, Profile profile, long length, String reason) {
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (profile != null) {
            if (profile.isBanned()) {
                MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is already banned.");
                return;
            }

            profile.getTemporaryBans().add(new TemporaryBan(punisher, length, reason, DateUtil.getProperDate(new Date())));
            profile.saveProfileData();

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

        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        ipBanList.add(new IPBan(address, punisher, reason, DateUtil.getProperDate(new Date()), true));

        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (all.getAddress().getAddress().getHostAddress().replace("[", "").replace("]", "").equalsIgnoreCase(address)) {
                all.kickPlayer(ChatColor.RED + "You have been blacklisted from McCritZ.\nYou cannot purchase an unban.");
            }
        }

        MessageManager.broadcast("kpure.banip", "&c" + address + " &7has been blacklisted by &c" + sender.getName() + "&7.");
    }

    public void permanentlyMute(CommandSender sender, Profile profile, String reason) {
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (profile != null) {
            if (profile.isBanned()) {
                MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is already muted.");
                return;
            }

            profile.getPermanentMutes().add(new PermanentMute(punisher, reason, DateUtil.getProperDate(new Date()), true));
            profile.saveProfileData();

            if (profile.isOnline()) {
                MessageManager.sendMessage(profile.getUniqueID(), "&cYou have been permanently muted.");
            }

            MessageManager.broadcast("&c" + profile.getCurrentName() + " &7has been muted by &c" + sender.getName() + "&7.");
        }
    }

    public void temporarilyMute(CommandSender sender, Profile profile, long length, String reason) {
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (profile != null) {
            if (profile.isMuted()) {
                MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is already muted.");
                return;
            }

            profile.getTemporaryMutes().add(new TemporaryMute(punisher, length, reason, DateUtil.getProperDate(new Date())));
            profile.saveProfileData();

            if (profile.isOnline()) {
                MessageManager.sendMessage(profile.getUniqueID(), "&cYou have been temporarily muted for " + DateUtil.formatDateDiff(length) + ".");
            }

            MessageManager.broadcast("&c" + profile.getCurrentName() + " &7has been muted by &c" + sender.getName() + "&7.");
        }
    }

    public void unban(CommandSender sender, Profile profile) {
        if (isIPBanned(profile.getCurrentIP())) {
            if (getActiveIPBan(profile.getCurrentIP()) != null) {
                getActiveIPBan(profile.getCurrentIP()).setActive(false);
            }
        } else if (profile.isPermanentlyBanned()) {
            if (profile.getActivePermanentBan() != null) {
                profile.getActivePermanentBan().setActive(false);
                profile.saveProfileData();
            }
        } else if (profile.isTemporarilyBanned()) {
            if (profile.getActiveTemporaryBan() != null) {
                profile.getActiveTemporaryBan().setLength(0);
                profile.saveProfileData();
            }
        } else {
            MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is not banned.");
            return;
        }

        MessageManager.broadcast("&c" + profile.getCurrentName() + " &7has been unbanned by &c" + sender.getName() + "&7.");
    }

    public void unban(CommandSender sender, String address) {
        if (!isIPBanned(address)) {
            MessageManager.sendMessage(sender, "&c" + address + " &7is not banned.");
            return;
        }

        if (getActiveIPBan(address) != null) {
            getActiveIPBan(address).setActive(false);
        }

        MessageManager.broadcast("kpure.unban", "&c" + address + " &7has been unbanned by &c" + sender.getName() + "&7.");
    }

    public void unmute(CommandSender sender, Profile profile) {
        if (profile.isPermanentlyMuted()) {
            if (profile.getActivePermanentMute() != null) {
                profile.getActivePermanentMute().setActive(false);
                profile.saveProfileData();
            }
        } else if (profile.isTemporarilyMuted()) {
            if (profile.getActiveTemporaryMute() != null) {
                profile.getActiveTemporaryMute().setLength(0);
                profile.saveProfileData();
            }
        } else {
            MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " &7is not muted.");
            return;
        }

        MessageManager.broadcast("kpure.unban", "&c" + profile.getCurrentName() + " &7has been unmuted by &c" + sender.getName() + "&7.");
    }

    public void saveIPBans() {
        for (IPBan b : ipBanList) {
            Document doc = new Document("address", b.getAddress());

            if (b.getPunisherUUID() != null)
                doc.append("punisherUUID", b.getPunisherUUID().toString());

            doc.append("reason", b.getReason());
            doc.append("dateIssued", b.getDateIssued());
            doc.append("isActive", b.isActive());

            bCollection.find(Filters.eq("address", b.getAddress())).first(new SingleResultCallback<Document>() {
                @Override
                public void onResult(Document document, Throwable throwable) {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else {
                        if (document != null) {
                            bCollection.replaceOne(Filters.eq("address", b.getAddress()), doc, new UpdateOptions().upsert(true), new SingleResultCallback<UpdateResult>() {
                                @Override
                                public void onResult(UpdateResult updateResult, Throwable t) {
                                    if (t != null) {
                                        t.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            bCollection.insertOne(doc, new SingleResultCallback<Void>() {
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
        Profile profile = pm.getProfile(id);

        pm.getLoadedProfiles().stream().filter(loadedProfile -> loadedProfile.getIpList().contains(profile.getCurrentIP())).forEach(loadedProfile -> {
            if (loadedProfile.getUniqueID() != profile.getUniqueID()) {
                if (!loadedProfile.getAltList().contains(profile.getUniqueID())) {
                    loadedProfile.getAltList().add(profile.getUniqueID());
                }

                if (!profile.getAltList().contains(loadedProfile.getUniqueID())) {
                    profile.getAltList().add(loadedProfile.getUniqueID());
                }
            }
        });

        pCollection.find().into(new ArrayList<>(), new SingleResultCallback<ArrayList<Document>>() {
            @Override
            public void onResult(ArrayList<Document> documents, Throwable throwable) {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    for (Document doc : documents) {
                        pm.requestProfile(UUID.fromString(doc.getString("uniqueID")), new ProfileRequest<Profile>() {
                            @Override
                            public void onComplete(Profile result, Throwable throwable) {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                } else {
                                    if (result.getIpList().contains(profile.getCurrentIP())) {
                                        if (!profile.getAltList().contains(result.getUniqueID())) {
                                            if (result.getUniqueID() != profile.getUniqueID()) {
                                                if (!result.getAltList().contains(profile.getUniqueID())) {
                                                    result.getAltList().add(profile.getUniqueID());
                                                    result.saveProfileData();
                                                }

                                                if (!profile.getAltList().contains(result.getUniqueID())) {
                                                    profile.getAltList().add(result.getUniqueID());
                                                    profile.saveProfileData();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void checkForBannedAlts(UUID id) {
        HashSet<String> associatedProfiles = new HashSet<>();

        Profile profile = pm.getProfile(id);

        pCollection.find().into(new ArrayList<>(), new SingleResultCallback<ArrayList<Document>>() {
            @Override
            public void onResult(ArrayList<Document> documents, Throwable throwable) {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    for (Document doc : documents) {
                        pm.requestProfile(UUID.fromString(doc.getString("uniqueID")), new ProfileRequest<Profile>() {
                            @Override
                            public void onComplete(Profile result, Throwable throwable) {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                } else {
                                    boolean alert = false;

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

                                    if (alert) {
//                                        MessageManager.broadcast("pure.alert", "&cAlert! &a" + profile.getCurrentName() + "&7's IP is associated with the banned account(s) &a" + associatedProfiles.toString().replace("&7(", "").replace("&7)", ""));
                                        MessageManager.broadcast("kpure.alert", "&7Searching database for alts associated with &c" + profile.getCurrentName() + "&7(&c" + associatedProfiles.size() + "&7).");
                                        MessageManager.broadcast("kpure.alert", "" + associatedProfiles.toString().replace("[", "").replace("]", ""));
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void staffRollback(UUID id, long length) {
        pCollection.find().into(new ArrayList<>(), new SingleResultCallback<ArrayList<Document>>() {
            @Override
            public void onResult(ArrayList<Document> documents, Throwable throwable) {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    for (Document doc : documents) {
                        pm.requestProfile(UUID.fromString(doc.getString("uniqueID")), new ProfileRequest<Profile>() {
                            @Override
                            public void onComplete(Profile result, Throwable throwable) {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                } else {
                                    if (result.isBanned()) {
                                        if (result.isPermanentlyBanned()) {
//                                            if (result.getActivePermanentBan().getPunisherUUID().equals(id) && result.getD) {
//
//                                            }
                                        }
                                    } else if (result.isMuted()) {

                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
