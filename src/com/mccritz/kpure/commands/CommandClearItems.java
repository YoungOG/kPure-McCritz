package com.mccritz.kpure.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;

public class CommandClearItems extends BaseCommand
{
	
	public CommandClearItems()
	{
		super("clearitems", "kpure.clearitems", CommandUsageBy.ANYONE);
		setUsage("&c/clearitems");
		setMinArgs(0);
		setMaxArgs(0);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args)
	{
		for (World w : Bukkit.getWorlds())
		{
			MessageManager.sendMessage(sender,
					"&7World " + (w.getEnvironment() == World.Environment.NORMAL ? "&a" + w.getName() : "&c" + w.getName())
							+ " &7removed &c" + w.getEntitiesByClass(Item.class).size() + " &7items");
			
			for (Entity ent : w.getEntitiesByClass(Item.class))
			{
				ent.remove();
			}
		}
	}
}
