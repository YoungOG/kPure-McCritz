package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import com.breakmc.pure.utils.database.DatabaseManager;
import com.mongodb.DB;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.stream.Collectors;

public class Command_cleardb extends BaseCommand {

    public Command_cleardb() {
        super("cleardb", "pure.cleardb", CommandUsageBy.CONSOLE);
        setUsage("&cImproper Usage! /cleardb");
        setMinArgs(0);
        setMaxArgs(0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        HashSet<DB> databases = DatabaseManager.getInstance().getClient().getDatabaseNames().stream().map(s -> DatabaseManager.getInstance().getClient().getDB(s)).collect(Collectors.toCollection(HashSet::new));

        for (Plugin pl : Bukkit.getServer().getPluginManager().getPlugins()) {
            if (pl != Pure.getInstance()) {
                Bukkit.getServer().getPluginManager().disablePlugin(pl);
            }
        }

        MessageManager.sendMessage(sender, "Preparing to drop " + databases.size() + " databases!");

        for (DB db : databases) {
            MessageManager.sendMessage(sender, "Dropping: " + db.getName() + " with " + db.getCollectionNames().size() + " collections!");
            db.dropDatabase();
        }

        DatabaseManager.getInstance().getClient().getConnector().close();
        DatabaseManager.getInstance().getClient().close();

        MessageManager.sendMessage(sender, "Successfully dropped " + databases.size() + " databases!");

        Bukkit.getServer().getPluginManager().disablePlugin(Pure.getInstance());
    }
}
