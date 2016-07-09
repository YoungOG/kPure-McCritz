package com.mccritz.kpure.commands;

import org.bukkit.command.CommandSender;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.utils.IPUtils;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;

public class CommandLookup extends BaseCommand {

    private ProfileManager pm = kPure.getInstance().getProfileManager();

    public CommandLookup() {
	super("lookup", "kpure.lookup", CommandUsageBy.ANYONE, "profile", "seen", "info", "checkban", "cb");
	setUsage("&c/lookup <player/ip/info>");
	setMinArgs(1);
	setMaxArgs(1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
	if (args[0].equalsIgnoreCase("info")) {
	    long aLong = kPure.getInstance().getMongoDatabase().getCollection("profiles").count();
	    MessageManager.sendMessage(sender, "&aThere are a total of &b" + aLong + " &aprofiles on the network.");
	} else if (!IPUtils.isValidIP(args[0])) {
	    Profile result = pm.getProfile(args[0]);

	    if (result == null) {
		MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not found.");
		return;
	    }

	    result.lookup(sender);
	} else {
	    if (!sender.hasPermission("pure.lookup.admin")) {
		MessageManager.sendMessage(sender, "&cYou do not have permission to lookup IP Addresses.");
		return;
	    }

	    pm.lookup(sender, args[0]);
	}
    }

}