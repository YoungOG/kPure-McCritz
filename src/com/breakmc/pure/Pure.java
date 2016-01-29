package com.breakmc.pure;

import com.breakmc.pure.commands.*;
import com.breakmc.pure.listeners.*;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.profile.ProfileRequest;
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
            profileManager.requestProfile(p.getUniqueId(), new ProfileRequest<Profile>() {
                @Override
                public void onComplete(Profile result, Throwable throwable) {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        return;
                    }

                    if (result == null) {
                        return;
                    }

                    System.out.println("Loading " + p.getName() + "'s profile!");
                    profileManager.loadProfile(result, true);

                    result.setOnline(p.isOnline());
                    result.setLogins(result.getLogins() + 1);
                    result.setGroup(PlayerUtility.getGroup(result.getCurrentName()));
                    result.saveProfileData();
                }
            });
        }
    }

    public void onDisable() {
        profileManager.saveProfiles();
        punishmentManager.saveIPBans();

        DatabaseManager.getInstance().getMongoClient().close();

        getConfig().set("player-count", playerCount);
        saveConfig();
    }

    public void registerCommands() {
        Register register = new Register();

        try {
            register.registerCommand("lookup", new CommandLookup());
            register.registerCommand("ban", new CommandBan());
            register.registerCommand("tempban", new CommandTempBan());
            register.registerCommand("banip", new CommandBanIP());
            register.registerCommand("unban", new CommandUnban());
            register.registerCommand("mute", new CommandMute());
            register.registerCommand("permmute", new CommandPermMute());
            register.registerCommand("unmute", new CommandUnMute());
            register.registerCommand("warn", new CommandWarn());
            register.registerCommand("note", new CommandNote());
            register.registerCommand("report", new CommandReport());
            register.registerCommand("helpop", new CommandHelpOP());
            register.registerCommand("freeze", new CommandFreeze());
            register.registerCommand("clearchat", new CommandClearChat());
            register.registerCommand("invsee", new CommandInvsee());
            register.registerCommand("slowchat", new CommandSlowChat());
            register.registerCommand("mutechat", new CommandMuteChat());
            register.registerCommand("staffchat", new CommandStaffChat());
            register.registerCommand("vanish", new CommandVanish());
            register.registerCommand("list", new CommandList());
            register.registerCommand("freezeall", new CommandFreezeAll());
            register.registerCommand("setslots", new CommandSetSlots());
            register.registerCommand("setpin", new CommandSetPin());
            register.registerCommand("deletepin", new CommandDelpin());
            register.registerCommand("register", new CommandRegister());
            register.registerCommand("kickall", new CommandKickAll());
            register.registerCommand("hub", new CommandHub());
            register.registerCommand("clearmobs", new CommandClearMobs());
            register.registerCommand("clearitems", new CommandClearItems());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerListeners() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new FreezeListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new VanishListener(), this);
        getServer().getPluginManager().registerEvents(new PinListener(), this);
        getServer().getPluginManager().registerEvents(new AntiSpamBotListener(), this);
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
