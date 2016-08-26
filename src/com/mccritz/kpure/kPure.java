package com.mccritz.kpure;

import com.mccritz.kpure.commands.*;
import com.mccritz.kpure.listeners.JoinListener;
import com.mccritz.kpure.listeners.PinListener;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.punishment.PunishmentManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mccritz.kpure.utils.command.Register;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

@Getter
public class kPure extends JavaPlugin {

    @Getter
    private static kPure instance;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private ProfileManager profileManager;
    private PunishmentManager punishmentManager;

    public static final ExecutorService SERVICE = Executors.newCachedThreadPool();

    @Override
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
            Profile result = profileManager.getProfile(p.getUniqueId());

            System.out.println("Loading " + p.getName() + "'s profile!");
            result.setLogins(result.getLogins() + 1);
            result.setGroup("disabled");
            profileManager.saveProfile(result);
        }
    }

    @Override
    public void onDisable() {
        mongoClient.close();
        saveConfig();
        SERVICE.shutdown();
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
            register.registerCommand("clearitems", new CommandClearItems());
            register.registerCommand("gamemodeclear", new CommandGMClear());
            register.registerCommand("register", new CommandRegister());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerListeners() {
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new PinListener(), this);
    }

    public void setupMongoConnection() {
        try {
            mongoClient = new MongoClient(getConfig().getString("database.host"));

            getLogger().log(Level.INFO, "Successfully connected to the MongoDB server.");

            mongoDatabase = mongoClient.getDatabase(getConfig().getString("database.database-name"));
        } catch (MongoException e) {
            e.printStackTrace();
            getLogger().log(Level.WARNING, "Failed to connect to the MongoDB server, disabling!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

}