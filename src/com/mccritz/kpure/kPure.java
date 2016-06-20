package com.mccritz.kpure;

import com.mccritz.kpure.commands.*;
import com.mccritz.kpure.listeners.JoinListener;
import com.mccritz.kpure.listeners.PinListener;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.punishment.PunishmentManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mccritz.kpure.utils.command.Register;
import com.mongodb.MongoException;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class kPure extends JavaPlugin {

    private static kPure instance;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private ProfileManager profileManager;
    private PunishmentManager punishmentManager;

    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        setupMongoConnection();

        profileManager = new ProfileManager();
        punishmentManager = new PunishmentManager();

        registerCommands();
        registerListeners();

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
                result.setGroup("disabled");
                result.saveProfileData();
            });
        }
    }

    public void onDisable() {
        profileManager.saveProfiles();
        punishmentManager.saveIPBans();

        saveConfig();
    }

    public void registerCommands() {
        Register register = new Register();

        try {
            register.registerCommand("lookup", new CommandLookup());
            register.registerCommand("kick", new CommandKick());
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
            register.registerCommand("gamemodeclear", new CommandGMClear());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerListeners() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new PinListener(), this);
    }

    public void setupMongoConnection() {
        try {
            mongoClient = MongoClients.create("mongodb://" + getConfig().getString("database.host"));

            getLogger().log(Level.INFO, "Successfully connected to the MongoDB server.");

            mongoDatabase = mongoClient.getDatabase(getConfig().getString("database.database-name"));
        } catch (MongoException e) {
            e.printStackTrace();
            getLogger().log(Level.WARNING, "Failed to connect to the MongoDB server, disabling!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
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
}
