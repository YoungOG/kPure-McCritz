package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.profile.ProfileRequest;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.IPUtils;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;

public class CommandUnban extends BaseCommand {

    private ProfileManager pm = Pure.getInstance().getProfileManager();
    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    public CommandUnban() {
        super("unban", "pure.unban", CommandUsageBy.ANYONE);
        setUsage("&cImproper Usage! /unban (name/ip)");
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!IPUtils.isValidIP(args[0])) {
            pm.requestProfile(args[0], new ProfileRequest<Profile>() {
                @Override
                public void onComplete(Profile result, Throwable throwable) {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not found.");
                        return;
                    }

                    if (result == null) {
                        MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not found.");
                        return;
                    }

                    pum.unban(sender, result);
                }
            });
        } else {
            pum.unban(sender, args[0]);
        }
    }
}
