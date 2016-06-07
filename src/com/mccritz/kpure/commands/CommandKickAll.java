package com.mccritz.kpure.commands;

import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKickAll extends BaseCommand {

    public CommandKickAll() {
        super("kickall", "pure.kickall", CommandUsageBy.ANYONE);
        setUsage("&cImproper Usage! /kickall");
        setMinArgs(0);
        setMaxArgs(0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (all.getUniqueId() != p.getUniqueId()) {
                    all.kickPlayer(ChatColor.RED + "All players have been kicked from the server.\nYou may rejoin at BreakMC.com");
                }
            }
        } else {
            for (Player all : PlayerUtility.getOnlinePlayers()) {
                all.kickPlayer(ChatColor.RED + "All players have been kicked from the server.\nYou may rejoin at BreakMC.com");
            }
        }

        MessageManager.sendMessage(sender, "&aAll players have been kicked.");
    }
}
