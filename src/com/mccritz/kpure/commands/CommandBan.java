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

public class CommandBan extends BaseCommand {

    private ProfileManager pm = kPure.getInstance().getProfileManager();
    private PunishmentManager pum = kPure.getInstance().getPunishmentManager();

    public CommandBan() {
	super("ban", "kpure.ban", CommandUsageBy.ANYONE);
	setUsage("&c/ban <player> <reason>");
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

	pum.permanentlyBan(sender, result, StringUtils.join(args, " ", 1, args.length));
    }
}