package com.breakmc.pure.commands;

import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_clearchat extends BaseCommand {

    public Command_clearchat() {
        super("clearchat", "pure.clearchat", CommandUsageBy.ANYONE, "cc");
        setUsage("&cImproper usage! /clearchat");
        setMinArgs(0);
        setMaxArgs(0);
    }

    public void execute(CommandSender sender, String[] args) {
        for (int i = 0; i <= 101; i++) {
            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (!all.hasPermission("pure.clearchat")) {
                    MessageManager.sendMessage(all, " ");
                }
            }
        }

        MessageManager.broadcast("&bChat has been cleared by " + sender.getName() + "!");
    }
}
