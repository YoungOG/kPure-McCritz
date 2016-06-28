package com.mccritz.kpure.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;

public class CommandKick extends BaseCommand {

    public CommandKick() {
	super("kick", "kpure.kick", CommandUsageBy.ANYONE);
	setUsage("&c/kick <player> <reason>");
	setMinArgs(2);
	setMaxArgs(100);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
	Player target = Bukkit.getPlayer(args[0]);

	if (target == null) {
	    MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND(args[0]));
	    return;
	}

	MessageManager
		.broadcast("&c" + target.getPlayer().getName() + " &7has been kicked by &c" + sender.getName() + "&7.");
	target.kickPlayer(ChatColor.RED + "You have been kicked by " + sender.getName() + " for "
		+ StringUtils.join(args, " ", 1, args.length) + ".");
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
