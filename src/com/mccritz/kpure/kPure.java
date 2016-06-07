package com.mccritz.kpure;

import com.mccritz.kpure.commands.*;
import com.mccritz.kpure.listeners.JoinListener;
import com.mccritz.kpure.listeners.PinListener;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.punishment.PunishmentManager;
import com.mccritz.kpure.utils.Lag;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mccritz.kpure.utils.command.Register;
import com.mccritz.kpure.utils.database.DatabaseManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class kPure extends JavaPlugin {

    private static kPure instance;
    private ProfileManager profileManager;
    private PunishmentManager punishmentManager;
    private Permission permissions;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        DatabaseManager.getInstance().connect();

        setupPermissions();

        profileManager = new ProfileManager();
        punishmentManager = new PunishmentManager();

        registerCommands();
        registerListeners();
        registerChecks();

        for (Player p : PlayerUtility.getOnlinePlayers()) {
            profileManager.requestProfile(p.getUniqueId(), (result, throwable) -> {
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
                result.setGroup(permissions.getPrimaryGroup(p));
                result.saveProfileData();
            });
        }
    }

    public void onDisable() {
        profileManager.saveProfiles();
        punishmentManager.saveIPBans();

        DatabaseManager.getInstance().getMongoClient().close();

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
            register.registerCommand("setpin", new CommandSetPin());
            register.registerCommand("deletepin", new CommandResetPin());
            register.registerCommand("kickall", new CommandKickAll());
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

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permissions = rsp.getProvider();
        return permissions != null;
    }

    public void registerChecks() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
    }

    public static kPure getInstance() {
        return instance;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public Permission getPermissions() {
        return permissions;
    }
}
