package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandVanish extends BaseCommand {

    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();
    private ProfileManager pm = Pure.getInstance().getProfileManager();

    public CommandVanish() {
        super("vanish", "pure.vanish", CommandUsageBy.ANYONE, "v", "ev");
        setUsage("&cImproper usage! /vanish (player)");
        setMinArgs(0);
        setMaxArgs(1);
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player p = (Player) sender;

                if (pum.isVanished(p)) {
                    pum.removeVanisher(p);
                } else {
                    pum.addVanisher(p);
                }
            }
        }

        if (args.length == 1) {
            Profile prof = pm.getProfile(args[0]);

            if (prof == null) {
                MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not be found.");
                return;
            }

            if (Bukkit.getPlayer(prof.getUniqueID()) == null) {
                MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not be found.");
                return;
            }

            Player t = Bukkit.getPlayer(prof.getUniqueID());

            if (pum.isVanished(t)) {
                pum.removeVanisher(t);
                MessageManager.sendMessage(sender, "&aVanish disabled for " + prof.getCurrentName());
            } else {
                pum.addVanisher(t);
                MessageManager.sendMessage(sender, "&aVanish enabled for " + prof.getCurrentName());
            }
        }
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
