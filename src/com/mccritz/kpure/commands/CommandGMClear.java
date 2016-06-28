package com.mccritz.kpure.commands;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;

public class CommandGMClear extends BaseCommand {

    public CommandGMClear() {
	super("gamemodeclear", "kpure.gamemodeclear", CommandUsageBy.ANYONE, "gclear");
	setUsage("&c/gamemodeclear");
	setMinArgs(0);
	setMaxArgs(0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
	for (Player all : PlayerUtility.getOnlinePlayers()) {
	    all.setGameMode(GameMode.SURVIVAL);
	}

	MessageManager.sendMessage(sender, "&7All users have been put into &csurvival &7mode.");
    }
}
