package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;

public class Command_slowchat extends BaseCommand {

    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    public Command_slowchat() {
        super("slowchat", "pure.slowchat", CommandUsageBy.ANYONE);
        setUsage("&cImproper usage! /slowchat");
        setMinArgs(0);
        setMaxArgs(0);

    }

    public void execute(CommandSender sender, String[] args) {
        pum.setChatSlowed(sender);
    }
}
