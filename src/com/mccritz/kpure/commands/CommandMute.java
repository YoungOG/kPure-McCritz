package com.mccritz.kpure.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.punishment.PunishmentManager;
import com.mccritz.kpure.utils.DateUtil;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;

public class CommandMute extends BaseCommand {

    private ProfileManager pm = kPure.getInstance().getProfileManager();
    private PunishmentManager pum = kPure.getInstance().getPunishmentManager();

    public CommandMute() {
	super("mute", "kpure.mute", CommandUsageBy.ANYONE);
	setUsage("&c/mute <name> <time> <reason>");
	setMinArgs(3);
	setMaxArgs(100);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
	Profile result = pm.getProfile(args[0]);

	if (result == null) {
	    MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not found.");
	    return;
	}

	try {
	    pum.temporarilyMute(sender, result, DateUtil.parseDateDiff(args[1], true),
		    StringUtils.join(args, " ", 2, args.length));
	} catch (Exception ignored) {
	    MessageManager.sendMessage(sender, "&cImproper time format!");
	}
    }

}