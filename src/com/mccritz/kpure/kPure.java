package com.mccritz.kpure;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.mccritz.kpure.commands.CommandBan;
import com.mccritz.kpure.commands.CommandBanIP;
import com.mccritz.kpure.commands.CommandClearItems;
import com.mccritz.kpure.commands.CommandGMClear;
import com.mccritz.kpure.commands.CommandKick;
import com.mccritz.kpure.commands.CommandKickAll;
import com.mccritz.kpure.commands.CommandLookup;
import com.mccritz.kpure.commands.CommandMute;
import com.mccritz.kpure.commands.CommandPermMute;
import com.mccritz.kpure.commands.CommandResetPin;
import com.mccritz.kpure.commands.CommandSetPin;
import com.mccritz.kpure.commands.CommandTempBan;
import com.mccritz.kpure.commands.CommandUnMute;
import com.mccritz.kpure.commands.CommandUnban;
import com.mccritz.kpure.listeners.JoinListener;
import com.mccritz.kpure.listeners.PinListener;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.punishment.PunishmentManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mccritz.kpure.utils.command.Register;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;

import lombok.Getter;

@Getter
public class kPure extends JavaPlugin {

    @Getter
    private static kPure instance;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private ProfileManager profileManager;
    private PunishmentManager punishmentManager;

    public static final Gson GSON = new Gson();
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
	    profileManager.requestProfile(p.getUniqueId(), (result, throwable) -> {
		if (throwable != null) {
		    throwable.printStackTrace();
		    return;
		}

		if (result == null)
		    return;

		System.out.println("Loading " + p.getName() + "'s profile!");
		// profileManager.loadProfile(result, true);

		// result.setOnline(p.isOnline());
		result.setLogins(result.getLogins() + 1);
		result.setGroup("disabled");
		result.saveProfileData();
	    });
	}
    }

    @Override
    public void onDisable() {
	// profileManager.saveProfiles();
	// punishmentManager.saveIPBans();

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
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void registerListeners() {
	getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

	getServer().getPluginManager().registerEvents(new JoinListener(), this);
	getServer().getPluginManager().registerEvents(new PinListener(), this);
//	getServer().getPluginManager().registerEvents(new UUIDVerifierListener(), this);
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