package com.breakmc.pure;

import com.breakmc.pure.commands.*;
import com.breakmc.pure.listeners.*;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.Lag;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.command.Register;
import com.breakmc.pure.utils.database.DatabaseManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class Pure extends JavaPlugin {

    private static Pure instance;
    private ProfileManager profileManager;
    private PunishmentManager punishmentManager;
    private int playerCount;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        DatabaseManager.getInstance().connect();

        profileManager = new ProfileManager();
        punishmentManager = new PunishmentManager();

        registerCommands();
        registerListeners();
        registerChecks();
        registerPackets();

        playerCount = getConfig().getInt("player-count");

        for (Player p : PlayerUtility.getOnlinePlayers()) {
            Profile prof = profileManager.getProfile(p.getUniqueId());

            profileManager.getLoadedProfiles().remove(prof);
            profileManager.loadProfile(p.getUniqueId());
            System.out.println("Reloaded " + p.getName() + "'s profile!");

            new BukkitRunnable() {
                @Override
                public void run() {
                    Profile prof = profileManager.getProfile(p.getUniqueId());

                    prof.setOnline(p.isOnline());
                    prof.setLogins(prof.getLogins() + 1);
                    prof.setGroup(PlayerUtility.getGroup(prof.getCurrentName()));

                    profileManager.saveProfile(prof);
                }
            }.runTaskLater(Pure.getInstance(), 5L);
        }
    }

    public void onDisable() {
        for (Profile prof : profileManager.getLoadedProfiles()) {
            prof.setOnline(false);
            profileManager.saveProfile(prof);
        }

        profileManager.saveProfiles();
        punishmentManager.saveIPBans();

        DatabaseManager.getInstance().getClient().close();

        getConfig().set("player-count", playerCount);
        saveConfig();
    }

    public void registerCommands() {
        Register register = new Register();

        try {
            register.registerCommand("lookup", new Command_lookup());
            register.registerCommand("ban", new Command_ban());
            register.registerCommand("tempban", new Command_tempban());
            register.registerCommand("banip", new Command_banip());
            register.registerCommand("unban", new Command_unban());
            register.registerCommand("mute", new Command_mute());
            register.registerCommand("permmute", new Command_permmute());
            register.registerCommand("unmute", new Command_unmute());
            register.registerCommand("warn", new Command_warn());
            register.registerCommand("note", new Command_note());
            register.registerCommand("report", new Command_report());
            register.registerCommand("helpop", new Command_helpop());
            register.registerCommand("freeze", new Command_freeze());
            register.registerCommand("clearchat", new Command_clearchat());
            register.registerCommand("invsee", new Command_invsee());
            register.registerCommand("slowchat", new Command_slowchat());
            register.registerCommand("mutechat", new Command_mutechat());
            register.registerCommand("staffchat", new Command_staffchat());
            register.registerCommand("vanish", new Command_vanish());
            register.registerCommand("list", new Command_list());
            register.registerCommand("freezeall", new Command_freezeall());
            register.registerCommand("setslots", new Command_setslots());
            register.registerCommand("setpin", new Command_setpin());
            register.registerCommand("deletepin", new Command_deletepin());
            register.registerCommand("register", new Command_register());
            register.registerCommand("cleardb", new Command_cleardb());
            register.registerCommand("kickall", new Command_kickall());
            register.registerCommand("hub", new Command_hub());
            register.registerCommand("banlookup", new Command_banlookup());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerListeners() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getServer().getPluginManager().registerEvents(new Listener_join(), this);
        getServer().getPluginManager().registerEvents(new Listener_freeze(), this);
        getServer().getPluginManager().registerEvents(new Listener_chat(), this);
        getServer().getPluginManager().registerEvents(new Listener_vanish(), this);
        getServer().getPluginManager().registerEvents(new Listener_pin(), this);
        getServer().getPluginManager().registerEvents(new Listener_antispambot(), this);
    }

    public void registerChecks() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
    }

    public void registerPackets() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.TAB_COMPLETE) {
            public void onPacketReceiving(final PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
                    try {
                        if (event.getPlayer().hasPermission("pure.tabcomplete")) {
                            return;
                        }

                        PacketContainer packet = event.getPacket();
                        String message = ((String)packet.getSpecificModifier((Class)String.class).read(0)).toLowerCase();

                        if ((message.startsWith("/") && !message.contains(" ")) || (message.startsWith("/pex") && !message.contains("  ")) || (message.startsWith("/permissionsex") && !message.contains("  "))) {
                            event.setCancelled(true);
                        }
                    } catch (FieldAccessException e) {
                        getLogger().log(Level.SEVERE, "Couldn't access field.", e);
                    }
                }
            }
        });
    }

    public static Pure getInstance() {
        return instance;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
}
