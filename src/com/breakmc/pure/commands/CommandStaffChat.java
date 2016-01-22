package com.breakmc.pure.commands;

import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

public class CommandStaffChat extends BaseCommand {

    public CommandStaffChat() {
        super("staffchat", "pure.staffchat", CommandUsageBy.ANYONE, "sc");
        setUsage("&cImproper usage! /staffchat (message)");
        setMinArgs(1);
        setMaxArgs(100);
    }

    public void execute(CommandSender sender, String[] args) {
        MessageManager.sendStaffMessage(sender, StringUtils.join(args, " ", 0, args.length));
    }
}
