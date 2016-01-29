package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.profile.ProfileRequest;
import com.breakmc.pure.utils.Cooldowns;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandReport extends BaseCommand {

    private ProfileManager pm = Pure.getInstance().getProfileManager();

    public CommandReport() {
        super("report", null, CommandUsageBy.PlAYER);
        setUsage("&cImproper Usage! /report (player) (reason)");
        setMinArgs(2);
        setMaxArgs(100);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
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

                if (Cooldowns.tryCooldown(((Player) sender).getUniqueId(), "Report", 120000)) {
                    MessageManager.broadcast("pure.alert", "&7(&aReport&7) &a" + sender.getName() + " &7reported &a" + result.getCurrentName() + " &7(" + (result.isOnline() ? "&aOnline" : "&cOffline") + "&7) for " + StringUtils.join(args, " ", 1, args.length));
                    MessageManager.sendMessage(sender, "&bYour report has been successfully sent, a staff member will investigate shortly!");
                } else {
                    MessageManager.sendMessage(sender, "&7It appears you have asked for help or have reported someone in the last &b2 &7minutes\nPlease try again in &b" + (Cooldowns.getCooldown(((Player) sender).getUniqueId(), "Report") / 1000) + " &7seconds.");
                }
            }
        });
    }

    public List<String> tabComplete(String[] args, CommandSender sender) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                List<String> list2return = PlayerUtility.toList(PlayerUtility.getOnlinePlayers());
                Collections.sort(list2return);
                return list2return;
            }

            if (args.length == 1) {
                List<String> list2return = PlayerUtility.toList(PlayerUtility.getOnlinePlayers()).stream().filter(opt -> opt.toLowerCase().startsWith(args[0])).collect(Collectors.toList());
                Collections.sort(list2return);
                return list2return.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }
}
