package com.breakmc.pure.commands;

import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

public class CommandClearItems extends BaseCommand {

    public CommandClearItems() {
        super("clearitems", "pure.clearitems", CommandUsageBy.ANYONE);
        setUsage("&cImproper Usage! /clearitems");
        setMinArgs(0);
        setMaxArgs(0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (World w : Bukkit.getWorlds()) {
            MessageManager.sendMessage(sender, "&7- " + (w.getEnvironment() == World.Environment.NORMAL ? "&a" + w.getName() : "&c" + w.getName()) + " &7removed &b" + w.getEntitiesByClass(Item.class).size() + " &7items.");

            for (Entity ent : w.getEntitiesByClass(Item.class)) {
                ent.remove();
            }
        }
    }
}
