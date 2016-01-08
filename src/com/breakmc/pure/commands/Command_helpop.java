package com.breakmc.pure.commands;

import com.breakmc.pure.utils.Cooldowns;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_helpop extends BaseCommand {

    public Command_helpop() {
        super("helpop", null, CommandUsageBy.PlAYER, "help", "?");
        setUsage("&cImproper Usage! /help (question)");
        setMinArgs(1);
        setMaxArgs(100);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (Cooldowns.tryCooldown(((Player) sender).getUniqueId(), "Report", 120000)) {
            MessageManager.broadcast("pure.alert", "&7(&aHelp&7) &a" + sender.getName() + " &7has requested assistance: " + StringUtils.join(args, " ", 0, args.length) + ".");
            MessageManager.sendMessage(sender, "&aYour question has been sent, a staff member will answer it shortly.");
        } else {
            MessageManager.sendMessage(sender, "&7It appears you have asked for help or have reported someone in the last &b2 &7minutes.\nPlease try again in &b" + (Cooldowns.getCooldown(((Player) sender).getUniqueId(), "Report") / 1000) + " &7seconds.");
        }
    }
}
