package com.breakmc.pure.utils.database;

import com.breakmc.pure.Pure;
import com.mongodb.MongoException;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class DatabaseManager {

    private static DatabaseManager instance = new DatabaseManager();
    private Pure main = Pure.getInstance();
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoDatabase mongoRegisterDatabase;

    public void connect() {
        try {
            mongoClient = MongoClients.create("mongodb://" + main.getConfig().getString("database.host") + ":" + main.getConfig().getInt("database.port"));
            main.getLogger().log(Level.INFO, "Successfully connected to the MongoDB server.");

            mongoDatabase = mongoClient.getDatabase(main.getConfig().getString("database.database"));
            mongoRegisterDatabase = mongoClient.getDatabase("breakmcdev");
        } catch (MongoException e) {
            e.printStackTrace();
            main.getLogger().log(Level.WARNING, "Failed to connect to the MongoDB server, disabling!");
            Bukkit.getPluginManager().disablePlugin(main);
        }
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public MongoDatabase getMongoRegisterDatabase() {
        return mongoRegisterDatabase;
    }

    public static DatabaseManager getInstance() {
        return instance;
    }
}
