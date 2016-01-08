package com.breakmc.pure.utils.database;

import com.breakmc.pure.Pure;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bukkit.Bukkit;

import java.net.UnknownHostException;
import java.util.logging.Level;

public class DatabaseManager {

    private static DatabaseManager instance = new DatabaseManager();
    private Pure main = Pure.getInstance();
    private MongoClient client;
    private DB database;
    private DB registerDatabase;

    public void connect() {
        try {
            client = new MongoClient(new MongoClientURI("mongodb://" + main.getConfig().getString("database.host") + ":" + main.getConfig().getInt("database.port") + "/" + main.getConfig().getString("database.database")));
            main.getLogger().log(Level.INFO, "Connected to the MongoDB Server: " + client.getConnector().isOpen());
            database = client.getDB(main.getConfig().getString("database.database"));
            registerDatabase = client.getDB("breakmc");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            main.getLogger().log(Level.WARNING, "Failed to connect to the MongoDB server, disabling!");
            Bukkit.getPluginManager().disablePlugin(main);
        }
    }

    public MongoClient getClient() {
        return client;
    }

    public DB getDatabase() {
        return database;
    }

    public DB getRegisterDatabase() {
        return registerDatabase;
    }

    public DBCollection getCollection(String name) {
        return database.getCollection(name);
    }

    public boolean isConnected() {
        return database.getMongo().getConnector().isOpen();
    }

    public static DatabaseManager getInstance() {
        return instance;
    }
}
