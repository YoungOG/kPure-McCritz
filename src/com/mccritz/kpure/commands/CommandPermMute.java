package com.mccritz.kpure.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.punishment.PunishmentManager;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;

public class CommandPermMute extends BaseCommand {

    private ProfileManager pm = kPure.getInstance().getProfileManager();
    private PunishmentManager pum = kPure.getInstance().getPunishmentManager();

    public CommandPermMute() {
	super("permmute", "kpure.permmute", CommandUsageBy.ANYONE);
	setUsage("&c/permmute <name> <reason>");
	setMinArgs(2);
	setMaxArgs(100);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
	Profile result = pm.getProfile(args[0]);

	if (result == null) {
	    MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not found.");
	    return;
	}

	pum.permanentlyMute(sender, result, StringUtils.join(args, " ", 1, args.length));
    }
}