package com.mccritz.kpure.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;

public class CommandStaffRollback extends BaseCommand
{
	
	private ProfileManager		pm	= kPure.getInstance().getProfileManager();
	
	public CommandStaffRollback()
	{
		super("staffrollback", "kpure.staffrollback", CommandUsageBy.ANYONE);
		setUsage("&c/staffrollback <player> <time>");
		setMinArgs(2);
		setMaxArgs(100);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args)
	{
		pm.requestProfile(args[0], (result, throwable) ->
		{
			if (throwable != null)
			{
				throwable.printStackTrace();
				MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND(args[0]));
				return;
			}
			
			if (result == null)
			{
				MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND(args[0]));
				return;
			}
			
		});
	}
	
	@Override
	public List<String> tabComplete(String[] args, CommandSender sender)
	{
		if (sender instanceof Player)
		{
			if (args.length == 0)
			{
				List<String> list2return = PlayerUtility.toList(PlayerUtility.getOnlinePlayers());
				Collections.sort(list2return);
				return list2return;
			}
			
			if (args.length == 1)
			{
				List<String> list2return = PlayerUtility.toList(PlayerUtility.getOnlinePlayers()).stream()
						.filter(opt -> opt.toLowerCase().startsWith(args[0])).collect(Collectors.toList());
				Collections.sort(list2return);
				return list2return.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
			}
		}
		
		return new ArrayList<>();
	}
}
