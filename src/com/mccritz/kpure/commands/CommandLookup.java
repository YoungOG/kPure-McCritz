package com.mccritz.kpure.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.profile.ProfileRequest;
import com.mccritz.kpure.utils.IPUtils;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.PlayerUtility;
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
	    MessageManager.sendMessage(sender, "&aThere are currently &b" + pm.getLoadedProfiles().size()
		    + " &aloaded profiles on this instance.");
	    long aLong = kPure.getInstance().getMongoDatabase().getCollection("profiles").count();
	    MessageManager.sendMessage(sender, "&aThere are a total of &b" + aLong + " &aprofiles on the network.");
	} else if (!IPUtils.isValidIP(args[0])) {
	    pm.requestProfile(args[0], (ProfileRequest<Profile>) (result, throwable) -> {
		if (throwable != null) {
		    throwable.printStackTrace();
		    MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not found.");
		    return;
		}

		if (result == null) {
		    MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not found.");
		    return;
		}

		result.lookup(sender);
	    });
	} else {
	    if (!sender.hasPermission("pure.lookup.admin")) {
		MessageManager.sendMessage(sender, "&cYou do not have permission to lookup IP Addresses.");
		return;
	    }

	    pm.lookup(sender, args[0]);
	}
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {
	if (sender instanceof Player) {
	    if (args.length == 0) {
		List<String> list2return = PlayerUtility.toList(PlayerUtility.getOnlinePlayers());
		Collections.sort(list2return);
		return list2return;
	    }

	    if (args.length == 1) {
		List<String> list2return = PlayerUtility.toList(PlayerUtility.getOnlinePlayers()).stream()
			.filter(opt -> opt.toLowerCase().startsWith(args[0])).collect(Collectors.toList());
		Collections.sort(list2return);
		return list2return.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
			.collect(Collectors.toList());
	    }
	}

	return new ArrayList<>();
    }
}
