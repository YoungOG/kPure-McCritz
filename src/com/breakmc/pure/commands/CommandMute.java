package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.profile.ProfileRequest;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.DateUtil;
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

public class CommandMute extends BaseCommand {

    private ProfileManager pm = Pure.getInstance().getProfileManager();
    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    public CommandMute() {
        super("mute", "pure.mute", CommandUsageBy.ANYONE);
        setUsage("&cImproper usage! /mute (name) (time) (reason)");
        setMinArgs(3);
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

                try {
                    pum.temporarilyMute(sender, result, DateUtil.parseDateDiff(args[1], true), StringUtils.join(args, " ", 2, args.length));
                } catch (Exception ignored) {
                    MessageManager.sendMessage(sender, "&cImproper time format!");
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
