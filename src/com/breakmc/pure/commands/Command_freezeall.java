package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;

public class Command_freezeall extends BaseCommand {

    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    public Command_freezeall() {
        super("freeze", "pure.freezeall", CommandUsageBy.ANYONE, "freezeserver");
        setUsage("&cImproper Usage! /freezeall");
        setMinArgs(0);
        setMaxArgs(0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        pum.setServerFrozen();
    }
}
