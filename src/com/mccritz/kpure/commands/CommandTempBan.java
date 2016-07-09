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

public class CommandTempBan extends BaseCommand {

    private ProfileManager pm = kPure.getInstance().getProfileManager();
    private PunishmentManager pum = kPure.getInstance().getPunishmentManager();

    public CommandTempBan() {
	super("tempban", "kpure.tempban", CommandUsageBy.ANYONE);
	setUsage("&c/tempban <name> <time> <reason>");
	setMinArgs(3);
	setMaxArgs(100);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
	Profile result = pm.getProfile(args[0]);

	if (result == null) {
	    MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND(args[0]));
	    return;
	}

	try {
	    pum.temporarilyBan(sender, result, DateUtil.parseDateDiff(args[1], true),
		    StringUtils.join(args, " ", 2, args.length));
	} catch (Exception ignored) {
	    MessageManager.sendMessage(sender, "&7Improper time format. &7/tempban Young 1y2m3d2h15m Spam");
	}
    }
}