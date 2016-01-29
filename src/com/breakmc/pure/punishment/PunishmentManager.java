package com.breakmc.pure.punishment;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.profile.ProfileRequest;
import com.breakmc.pure.punishment.punishments.*;
import com.breakmc.pure.utils.DateUtil;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.SpecialPlayerInventory;
import com.breakmc.pure.utils.database.DatabaseManager;
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

    private ProfileManager pm = Pure.getInstance().getProfileManager();
    private MongoCollection<Document> bCollection = DatabaseManager.getInstance().getMongoDatabase().getCollection("ipbans");
    private MongoCollection<Document> pCollection = DatabaseManager.getInstance().getMongoDatabase().getCollection("profiles");
    private final List<IPBan> ipBanList = new ArrayList<>();
    private List<UUID> frozen = new ArrayList<>();
    private HashSet<UUID> vanished = new HashSet<>();
    private final Map<UUID, SpecialPlayerInventory> inventories = new HashMap<>();
    private int chatSlowSeconds = 0;
    private boolean isChatSlowed = false;
    private boolean isChatMuted = false;
    private boolean isServerFrozen = false;

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
        }.runTaskTimer(Pure.getInstance(), 0L, 300*20);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID id : frozen) {
                    MessageManager.sendMessage(id, "&f\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
                    MessageManager.sendMessage(id, "&f\u2588\u2588\u2588\u2588&c\u2588&f\u2588\u2588\u2588\u2588");
                    MessageManager.sendMessage(id, "&f\u2588\u2588\u2588&c\u2588&0\u2588&c\u2588&f\u2588\u2588\u2588");
                    MessageManager.sendMessage(id, "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588");
                    MessageManager.sendMessage(id, "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588 &eYou have been frozen by a BreakMC staff member!");
                    MessageManager.sendMessage(id, "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588 &eIf you disconnect you will be &4&lBANNED!");
                    MessageManager.sendMessage(id, "&f\u2588&c\u2588&6\u2588\u2588\u2588&6\u2588\u2588&c\u2588&f\u2588 &ePlease connect to our TS: &cts.breakmc.com!");
                    MessageManager.sendMessage(id, "&c\u2588&6\u2588\u2588\u2588&0\u2588&6\u2588\u2588\u2588&c\u2588");
                    MessageManager.sendMessage(id, "&c\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
                    MessageManager.sendMessage(id, "&f\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
                }
            }
        }.runTaskTimerAsynchronously(Pure.getInstance(), 0, 10 * 20L);
    }

    public void permanentlyBan(CommandSender sender, Profile profile, String reason) {
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (profile != null) {
            if (profile.isBanned()) {
                MessageManager.sendMessage(sender, "&cPlayer \"" + profile.getCurrentName() + "\" is already banned.");
                return;
            }

            profile.getPermanentBans().add(new PermanentBan(punisher, reason, DateUtil.getProperDate(new Date()), true));
            profile.saveProfileData();

            if (Bukkit.getPlayer(profile.getUniqueID()) != null) {
                Bukkit.getPlayer(profile.getUniqueID()).kickPlayer(ChatColor.RED + "You have been permanently banned.\n\nYou can appeal your ban on our website " + ChatColor.AQUA + "www.BreakMC.com");
            }

            MessageManager.broadcast("&a" + profile.getCurrentName() + " has been permanently banned by " + sender.getName() + ".");
        }
    }

    public void temporarilyBan(CommandSender sender, Profile profile, long length, String reason) {
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (profile != null) {
            if (profile.isBanned()) {
                MessageManager.sendMessage(sender, "&cPlayer \"" + profile.getCurrentName() + "\" is already banned.");
                return;
            }

            profile.getTemporaryBans().add(new TemporaryBan(punisher, length, reason, DateUtil.getProperDate(new Date())));
            profile.saveProfileData();

            if (Bukkit.getPlayer(profile.getUniqueID()) != null) {
                Bukkit.getPlayer(profile.getUniqueID()).kickPlayer(ChatColor.RED + "You have been temporarily banned.\n" + DateUtil.formatDateDiff(profile.getActiveTemporaryBan().getLength()) + " remaining.\n\nYou can not appeal a temporary ban.");
            }

            MessageManager.broadcast("&a" + profile.getCurrentName() + " has been temporarily banned by " + sender.getName() + ".");
        }
    }

    public void banIP(CommandSender sender, String address, String reason) {
        if (isIPBanned(address)) {
            MessageManager.sendMessage(sender, "&cIP \"" + address + "\" is already banned.");
            return;
        }

        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        ipBanList.add(new IPBan(address, punisher, reason, DateUtil.getProperDate(new Date()), true));

        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (all.getAddress().getAddress().getHostAddress().replace("[", "").replace("]", "").equalsIgnoreCase(address)) {
                all.kickPlayer(ChatColor.RED + "You are blacklisted.\n\nThis punishment " + ChatColor.RED + "" + ChatColor.BOLD + "cannot" + ChatColor.RED + " be appealed.");
            }
        }

        MessageManager.broadcast("pure.banip", "&a" + address + " has been blacklisted by " + sender.getName() + ".");
    }

    public void permanentlyMute(CommandSender sender, Profile profile, String reason) {
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (profile != null) {
            if (profile.isBanned()) {
                MessageManager.sendMessage(sender, "&cPlayer \"" + profile.getCurrentName() + "\" is already muted.");
                return;
            }

            profile.getPermanentMutes().add(new PermanentMute(punisher, reason, DateUtil.getProperDate(new Date()), true));
            profile.saveProfileData();

            if (profile.isOnline()) {
                MessageManager.sendMessage(profile.getUniqueID(), "&cYou have been permanently muted.");
            }

            MessageManager.broadcast("&a" + profile.getCurrentName() + " has been muted by " + sender.getName() + ".");
        }
    }

    public void temporarilyMute(CommandSender sender, Profile profile, long length, String reason) {
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (profile != null) {
            if (profile.isMuted()) {
                MessageManager.sendMessage(sender, "&cPlayer \"" + profile.getCurrentName() + "\" is already muted.");
                return;
            }

            profile.getTemporaryMutes().add(new TemporaryMute(punisher, length, reason, DateUtil.getProperDate(new Date())));
            profile.saveProfileData();

            if (profile.isOnline()) {
                MessageManager.sendMessage(profile.getUniqueID(), "&cYou have been temporarily muted for " + DateUtil.formatDateDiff(length) + ".");
            }

            MessageManager.broadcast("&a" + profile.getCurrentName() + " has been muted by " + sender.getName() + " for " + DateUtil.formatDateDiff(length) + ".");
        }
    }

    public void warn(CommandSender sender, Profile profile, String reason) {
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (profile != null) {
            profile.getWarns().add(new Warn(punisher, reason, DateUtil.getProperDate(new Date())));
            profile.saveProfileData();

            if (profile.isOnline()) {
                MessageManager.sendMessage(profile.getUniqueID(), "&cYou have been warned for " + reason);
            }

            MessageManager.broadcast("&a" + profile.getCurrentName() + " has been warned by " + sender.getName() + " for " + reason);
        }
    }

    public void note(CommandSender sender, Profile profile, String reason) {
        UUID punisher = ((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);

        if (profile != null) {
            profile.getNotes().add(new Note(punisher, reason, DateUtil.getProperDate(new Date())));
            profile.saveProfileData();

            MessageManager.sendMessage(sender, "&aNote has been added to " + profile.getCurrentName() + "'s profile.");
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
            MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " is not banned.");
            return;
        }

        MessageManager.broadcast("&a" + profile.getCurrentName() + " has been unbanned by " + sender.getName() + ".");
    }

    public void unban(CommandSender sender, String address) {
        if (!isIPBanned(address)) {
            MessageManager.sendMessage(sender, "&cIP \"" + address + "\" is not banned.");
            return;
        }

        if (getActiveIPBan(address) != null) {
            getActiveIPBan(address).setActive(false);
        }

        MessageManager.broadcast("pure.unban", "&a" + address + " has been unbanned by " + sender.getName() + ".");
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
            MessageManager.sendMessage(sender, "&c" + profile.getCurrentName() + " is not muted.");
            return;
        }

        MessageManager.broadcast("pure.unban", "&a" + profile.getCurrentName() + " has been unmuted by " + sender.getName() + ".");
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
                                                associatedProfiles.add(result.getCurrentName());
                                            }
                                        }

                                        for (String address : profile.getIpList()) {
                                            if (result.getIpList().contains(address) && !profile.getUniqueID().equals(result.getUniqueID())) {
                                                alert = true;
                                                associatedProfiles.add(result.getCurrentName());
                                            }
                                        }
                                    }

                                    if (alert)
                                        MessageManager.broadcast("pure.alert", "&cAlert! &a" + profile.getCurrentName() + "&7's IP is associated with the banned account(s) &a" + associatedProfiles.toString().replace("&7(", "").replace("&7)", ""));
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void addVanisher(Player p) {
        vanished.add(p.getUniqueId());

        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (!all.hasPermission("pure.vanish")) {
                all.hidePlayer(p);
            }
        }

        MessageManager.sendMessage(p, "&aYou are now vanished.");
    }

    public void removeVanisher(Player p) {
        vanished.remove(p.getUniqueId());

        for (Player all : PlayerUtility.getOnlinePlayers()) {
            all.showPlayer(p);
        }

        MessageManager.sendMessage(p, "&aYou are now visible.");
    }

    public void setChatSlowed(CommandSender sender, int chatSlowSeconds) {
        isChatSlowed = !isChatSlowed;

        MessageManager.broadcast("&c" + sender.getName() + " has " + (isChatSlowed ? "slowed" : "unslowed") + " the chat.");

        if (isChatSlowed)
            this.chatSlowSeconds = chatSlowSeconds;
    }

    public void setChatMuted(CommandSender sender) {
        isChatMuted = !isChatMuted;

        MessageManager.broadcast("&c" + sender.getName() + " has " + (isChatMuted ? "disabled" : "enabled") + " the chat.");
    }

    public void setServerFrozen() {
        isServerFrozen = !isServerFrozen;

        MessageManager.broadcast("&c&lThe server has been " + (isServerFrozen ? "frozen" : "unfrozen"));
    }

    public boolean isVanished(Player p) {
        return vanished.contains(p.getUniqueId());
    }

    public Map<UUID, SpecialPlayerInventory> getInventories() {
        return inventories;
    }

    public boolean isChatSlowed() {
        return isChatSlowed;
    }

    public boolean isChatMuted() {
        return isChatMuted;
    }

    public boolean isServerFrozen() {
        return isServerFrozen;
    }

    public List<UUID> getFrozen() {
        return frozen;
    }

    public int getChatSlowSeconds() {
        return chatSlowSeconds;
    }
}
