package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;

public class CommandSlowChat extends BaseCommand {

    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    public CommandSlowChat() {
        super("slowchat", "pure.slowchat", CommandUsageBy.ANYONE);
        setUsage("&cImproper usage! /slowchat (seconds)");
        setMinArgs(0);
        setMaxArgs(1);

    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            pum.setChatSlowed(sender, 10);
        } else {
            try {
                pum.setChatSlowed(sender, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                MessageManager.sendMessage(sender, "&cYou must input a valid number.");
            }
        }
    }
}
