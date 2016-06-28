package com.mccritz.kpure.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;

public class CommandSetPin extends BaseCommand
{
	
	private ProfileManager pm = kPure.getInstance().getProfileManager();
	
	public CommandSetPin()
	{
		super("setpin", "kpure.setpin", CommandUsageBy.ANYONE);
		setUsage("&c/setpin <pin>");
		setMinArgs(1);
		setMaxArgs(1);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args)
	{
		Player p = (Player) sender;
		Profile profile = pm.getProfile(p.getUniqueId());
		
		if (!isFourDigitCode(args[0]))
		{
			MessageManager.sendMessage(sender, "&7Your PIN must be &c4 &7numeric digits.");
			return;
		}
		
		profile.setPin(args[0]);
		profile.saveProfileData();
		
		MessageManager.sendMessage(p, "&7Your PIN has been set to &c" + args[0] + "&7.");
	}
	
	public boolean isFourDigitCode(String string)
	{
		String regex = "[0-9]+";
		
		return string.length() == 4 && string.matches(regex);
	}
}
