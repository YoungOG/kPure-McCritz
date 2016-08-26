package com.mccritz.kpure.commands;

import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.Collection;

public class CommandClearItems extends BaseCommand {

    public CommandClearItems() {
        super("clearitems", "kpure.clearitems", CommandUsageBy.ANYONE);
        setUsage("&c/clearitems");
        setMinArgs(0);
        setMaxArgs(0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (World w : Bukkit.getWorlds()) {
            Collection<Item> entities = w.getEntitiesByClass(Item.class);

            MessageManager.sendMessage(sender, "&7World " + (w.getEnvironment() == World.Environment.NORMAL ? "&a" + w.getName() : "&c" + w.getName()) + " &7removed &c" + entities.size() + " &7items");

            for (Entity e : entities) {
                e.remove();
            }
        }
    }
}
