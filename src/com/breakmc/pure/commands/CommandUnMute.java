package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandUnMute extends BaseCommand {

    private ProfileManager pm = Pure.getInstance().getProfileManager();
    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    public CommandUnMute() {
        super("unmute", "pure.unmute", CommandUsageBy.ANYONE);
        setUsage("&cImproper Usage! /unmute (player)");
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (pm.getProfile(args[0]) == null) {
            MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not be found.");
            return;
        }

        Profile prof = pm.getProfile(args[0]);
        pum.unmute(sender, prof.getUniqueID());
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
