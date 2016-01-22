package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.profile.Profile;
import com.breakmc.pure.profile.ProfileManager;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandList extends BaseCommand {

    private ProfileManager pm = Pure.getInstance().getProfileManager();

    public CommandList() {
        super("list", null, CommandUsageBy.ANYONE, "who");
        setMinArgs(0);
        setMaxArgs(0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageManager.sendMessage(sender, "&4Owner &7- &bDev &7- &cSrAdmin &7- &cAdmin &7- &5Mod &7- &6&lExtreme &7- &b&lAdvanced &7- &dInfamous &7- &eMaster &7- &aLegend &7- &6Pro &7- &2Vip &7- &9Premium &7- &7Member");

        List<String> owners = new ArrayList<>();
        List<String> devs = new ArrayList<>();
        List<String> sradmins = new ArrayList<>();
        List<String> admins = new ArrayList<>();
        List<String> mods = new ArrayList<>();
        List<String> extremes = new ArrayList<>();
        List<String> advances = new ArrayList<>();
        List<String> infamouses = new ArrayList<>();
        List<String> masters = new ArrayList<>();
        List<String> legends = new ArrayList<>();
        List<String> pros = new ArrayList<>();
        List<String> vips = new ArrayList<>();
        List<String> premiums = new ArrayList<>();
        List<String> youtubersplus = new ArrayList<>();
        List<String> youtubers = new ArrayList<>();
        List<String> members = new ArrayList<>();

        pm.getLoadedProfiles().stream().filter(Profile::isOnline).forEach(prof -> {
            if (prof.getGroup().equalsIgnoreCase("Owner")) {
                owners.add("&4" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Dev")) {
                devs.add("&b" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("SrAdmin")) {
                sradmins.add("&c&o" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Admin")) {
                admins.add("&c" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Mod")) {
                mods.add("&5" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Extreme")) {
                extremes.add("&6&l" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Advanced")) {
                advances.add("&b&l" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Infamous")) {
                infamouses.add("&d&l" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Master")) {
                masters.add("&e" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Legend")) {
                legends.add("&a" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Pro")) {
                pros.add("&6" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Vip")) {
                vips.add("&2" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Premium")) {
                premiums.add("&9" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Youtube+")) {
                youtubersplus.add("&7" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Youtube")) {
                youtubers.add("&7" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Member")) {
                members.add("&7" + prof.getCurrentName());
            }
        });

        Collections.sort(owners, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(devs, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(sradmins, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(admins, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(mods, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(extremes, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(advances, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(infamouses, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(masters, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(legends, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(pros, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(vips, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(premiums, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(youtubersplus, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(youtubers, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(members, String.CASE_INSENSITIVE_ORDER);

        owners.addAll(devs);
        owners.addAll(sradmins);
        owners.addAll(admins);
        owners.addAll(mods);
        owners.addAll(extremes);
        owners.addAll(advances);
        owners.addAll(infamouses);
        owners.addAll(masters);
        owners.addAll(legends);
        owners.addAll(pros);
        owners.addAll(vips);
        owners.addAll(premiums);
        owners.addAll(youtubersplus);
        owners.addAll(youtubers);
        owners.addAll(members);

        StringBuilder finishedList = new StringBuilder("&7(&b" + PlayerUtility.getOnlinePlayers().length + "&7/&b120&7)");

        for (int i = 0; i < owners.size(); i++) {
            finishedList.append(owners.get(i));

            if (i != owners.size() - 1) {
                finishedList.append("&7, &r");
            }
        }

        MessageManager.sendMessage(sender, finishedList.toString());
    }
}
