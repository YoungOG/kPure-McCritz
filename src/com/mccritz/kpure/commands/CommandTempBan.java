package com.mccritz.kpure.commands;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.profile.ProfileRequest;
import com.mccritz.kpure.punishment.PunishmentManager;
import com.mccritz.kpure.utils.DateUtil;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.PlayerUtility;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandTempBan extends BaseCommand {

    private ProfileManager pm = kPure.getInstance().getProfileManager();
    private PunishmentManager pum = kPure.getInstance().getPunishmentManager();

    public CommandTempBan() {
        super("tempban", "kpure.tempban", CommandUsageBy.ANYONE);
        setUsage("&c/tempban <name> <time> <reason>");
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
                    MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND(args[0]));
                    return;
                }

                if (result == null) {
                    MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND(args[0]));
                    return;
                }

                try {
                    pum.temporarilyBan(sender, result, DateUtil.parseDateDiff(args[1], true), StringUtils.join(args, " ", 2, args.length));
                } catch (Exception ignored) {
                    MessageManager.sendMessage(sender, "&7Improper time format. &7/tempban Young 1y2m3d2h15m Spam");
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
