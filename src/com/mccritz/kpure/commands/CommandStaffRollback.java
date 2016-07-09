package com.mccritz.kpure.commands;

import org.bukkit.command.CommandSender;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;

public class CommandStaffRollback extends BaseCommand {

    private ProfileManager pm = kPure.getInstance().getProfileManager();

    public CommandStaffRollback() {
	super("staffrollback", "kpure.staffrollback", CommandUsageBy.ANYONE);
	setUsage("&c/staffrollback <player> <time>");
	setMinArgs(2);
	setMaxArgs(100);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
	Profile result = pm.getProfile(args[0]);

	if (result == null) {
	    MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND(args[0]));
	    return;
	}

	// TODO: stuff.
    }

}