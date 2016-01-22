package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetPin extends BaseCommand {

    private ProfileManager pm = Pure.getInstance().getProfileManager();

    public CommandSetPin() {
        super("setpin", "pure.setpin", CommandUsageBy.ANYONE);
        setUsage("&cImproper Usage! /setpin (pin)");
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        Profile profile = pm.getProfile(p.getUniqueId());

        if (!isFourDigitCode(args[0])) {
            MessageManager.sendMessage(sender, "&cYour PIN must be 4 numeric digits.");
            return;
        }

        profile.setPin(args[0]);

        MessageManager.sendMessage(p, "&7Your PIN has been set to " + args[0] + ".");
    }

    public boolean isFourDigitCode(String string) {
        String regex = "[0-9]+";

        return (string.length() == 4 && string.matches(regex));
    }
}
