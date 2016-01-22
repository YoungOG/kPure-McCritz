package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
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

public class CommandDelPin extends BaseCommand {

    private ProfileManager pm = Pure.getInstance().getProfileManager();

    public CommandDelPin() {
        super("deletepin", "pure.delpin", CommandUsageBy.PlAYER, "delpin");
        setUsage("&cImproper Usage! /delpin (player)");
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;

        Profile prof = pm.getProfile(args[0]);

        if (prof == null) {
            MessageManager.sendMessage(p, "&cPlayer \"" + args[0] + "\" could not be found.");
            return;
        }

        prof.setPin("");

        MessageManager.sendMessage(p, "&eYou have deleted " + args[0] + "'s PIN.");
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
