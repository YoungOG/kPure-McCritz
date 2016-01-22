package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;

public class CommandMuteChat extends BaseCommand {

    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    public CommandMuteChat() {
        super("mutechat", "pure.mutechat", CommandUsageBy.ANYONE, "mc");
        setUsage("&cImproper usage! /mutechat");
        setMinArgs(0);
        setMaxArgs(0);

    }

    public void execute(CommandSender sender, String[] args) {
        pum.setChatMuted(sender);
    }
}
