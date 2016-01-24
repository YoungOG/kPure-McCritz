package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.DateUtil;
import com.breakmc.pure.utils.IPUtils;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import com.breakmc.pure.utils.database.DatabaseManager;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandLookup extends BaseCommand {

    private ProfileManager pm = Pure.getInstance().getProfileManager();
    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    public CommandLookup() {
        super("lookup", "pure.lookup", CommandUsageBy.ANYONE, "history", "seen", "info");
        setUsage("&cImproper Usage! /lookup (player/address/info)");
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("info")) {
            MessageManager.sendMessage(sender, "&aThere are currently &b" + pm.getLoadedProfiles().size() + " &aloaded profiles on this instance.");
            MessageManager.sendMessage(sender, "&aThere are a total of &b" + DatabaseManager.getInstance().getCollection("profiles").count() + " &aprofiles on the network.");

            long playtime = 0L;
            DBCursor dbc = DatabaseManager.getInstance().getCollection("profiles").find();
            while (dbc.hasNext()) {
                BasicDBObject dbo = (BasicDBObject) dbc.next();

                if (dbo.getLong("playtime") > 0L) {
                    playtime += dbo.getLong("playtime");
                }
            }

            MessageManager.sendMessage(sender, "&aThere is a total playtime of &b" + DateUtil.readableTime(playtime * 1000));
        } else if (!IPUtils.isValidIP(args[0])) {
            if (pm.getProfile(args[0]) == null) {
                MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not be found.");
                return;
            }

            Profile prof = pm.getProfile(args[0]);
            prof.lookup(sender);
        } else {
            if (!sender.hasPermission("pure.lookup.admin")) {
                MessageManager.sendMessage(sender, "&cYou do not have permission to lookup addresses.");
                return;
            }

            pm.lookup(sender, args[0]);
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
