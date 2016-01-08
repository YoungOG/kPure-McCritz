package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.utils.Cooldowns;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_kickall extends BaseCommand {

    private ProfileManager pm = Pure.getInstance().getProfileManager();

    public Command_kickall() {
        super("kickall", "pure.kickall", CommandUsageBy.ANYONE);
        setUsage("&cImproper Usage! /kickall");
        setMinArgs(0);
        setMaxArgs(0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (all.getUniqueId() != p.getUniqueId()) {
                    all.kickPlayer(ChatColor.RED + "All players have been kicked from the server.\nYou may rejoin at BreakMC.com");
                }
            }
        } else {
            for (Player all : PlayerUtility.getOnlinePlayers()) {
                all.kickPlayer(ChatColor.RED + "All players have been kicked from the server.\nYou may rejoin at BreakMC.com");
            }
        }

        MessageManager.sendMessage(sender, "&aAll players have been kicked.");
    }
}
