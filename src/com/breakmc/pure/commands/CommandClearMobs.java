package com.breakmc.pure.commands;

import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

public class CommandClearMobs extends BaseCommand {

    public CommandClearMobs() {
        super("clearmobs", "pure.clearmobs", CommandUsageBy.ANYONE);
        setUsage("&cImproper Usage! /clearmobs");
        setMinArgs(0);
        setMaxArgs(0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (World w : Bukkit.getWorlds()) {
            MessageManager.sendMessage(sender, "&7- " + (w.getEnvironment() == World.Environment.NORMAL ? "&a" + w.getName() : "&c" + w.getName()) + " &7removed &b" + w.getEntitiesByClass(Monster.class).size() + " &7monsters.");

            for (Entity ent : w.getEntitiesByClass(Monster.class)) {
                ent.remove();
            }
        }
    }
}
