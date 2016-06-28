package com.mccritz.kpure.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;

public class CommandKickAll extends BaseCommand
{
	
	public CommandKickAll()
	{
		super("kickall", "kpure.kickall", CommandUsageBy.ANYONE);
		setUsage("&c/kickall");
		setMinArgs(0);
		setMaxArgs(0);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player p = (Player) sender;
			
			for (Player all : PlayerUtility.getOnlinePlayers())
			{
				if (all.getUniqueId() != p.getUniqueId())
				{
					all.kickPlayer(ChatColor.RED + "All players have been kicked from the server.");
				}
			}
		}
		else
		{
			for (Player all : PlayerUtility.getOnlinePlayers())
			{
				all.kickPlayer(ChatColor.RED + "All players have been kicked from the server.");
			}
		}
		
		MessageManager.sendMessage(sender, "&7All users have been kicked.");
	}
}
