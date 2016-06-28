package com.mccritz.kpure.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;

public class CommandClearMobs extends BaseCommand {

    public CommandClearMobs() {
	super("clearmobs", "kpure.clearmobs", CommandUsageBy.ANYONE);
	setUsage("&c/clearmobs");
	setMinArgs(0);
	setMaxArgs(0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
	for (World w : Bukkit.getWorlds()) {
	    MessageManager
		    .sendMessage(sender,
			    "&7World "
				    + (w.getEnvironment() == World.Environment.NORMAL ? "&a" + w.getName()
					    : "&c" + w.getName())
				    + " &7removed &c" + w.getEntitiesByClass(Monster.class).size() + " &7monsters.");

	    for (Entity ent : w.getEntitiesByClass(Monster.class)) {
		ent.remove();
	    }
	}
    }
}
