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
        MessageManager.sendMessage(sender, "&4Owner &7- &bDev &7- &c&oHead Admin &7- &cSr. Admin &7- &cAdmin &7- &3G. Staff &7- &5Mod &7- &bSupreme &7- &eEnhanced &7- &aMember &7- &dYT &7- Normal");

        List<String> owners = new ArrayList<>();
        List<String> devs = new ArrayList<>();
        List<String> headadmins  = new ArrayList<>();
        List<String> sradmins = new ArrayList<>();
        List<String> admins = new ArrayList<>();
        List<String> gstaffs = new ArrayList<>();
        List<String> mods = new ArrayList<>();
        List<String> supremes = new ArrayList<>();
        List<String> enhanced = new ArrayList<>();
        List<String> members = new ArrayList<>();
        List<String> youtubers = new ArrayList<>();
        List<String> normals = new ArrayList<>();

        pm.getLoadedProfiles().stream().filter(Profile::isOnline).forEach(prof -> {
            if (prof.getGroup().equalsIgnoreCase("Owner")) {
                owners.add("&4" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Dev")) {
                devs.add("&b" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("HeadAdmin")) {
                headadmins.add("&c&o" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("SrAdmin")) {
                sradmins.add("&c" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Admin")) {
                admins.add("&c" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("GStaff")) {
                gstaffs.add("&3" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Mod")) {
                mods.add("&5" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("supreme")) {
                supremes.add("&b" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("enhanced")) {
                enhanced.add("&e" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("member")) {
                members.add("&a" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Youtube+") || prof.getGroup().equalsIgnoreCase("Youtube")) {
                youtubers.add("&d" + prof.getCurrentName());
            } else if (prof.getGroup().equalsIgnoreCase("Normal")) {
                normals.add("&7" + prof.getCurrentName());
            }
        });

        Collections.sort(owners, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(devs, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(headadmins, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(sradmins, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(admins, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(gstaffs, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(mods, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(supremes, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(enhanced, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(members, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(youtubers, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(normals, String.CASE_INSENSITIVE_ORDER);

        owners.addAll(devs);
        owners.addAll(headadmins);
        owners.addAll(sradmins);
        owners.addAll(admins);
        owners.addAll(gstaffs);
        owners.addAll(mods);
        owners.addAll(supremes);
        owners.addAll(enhanced);
        owners.addAll(members);
        owners.addAll(youtubers);
        owners.addAll(normals);

        StringBuilder finishedList = new StringBuilder("&7(&b" + PlayerUtility.getOnlinePlayers().length + "&7/&b" + Pure.getInstance().getPlayerCount() + "&7) ");

        for (int i = 0; i < owners.size(); i++) {
            finishedList.append(owners.get(i));

            if (i != owners.size() - 1) {
                finishedList.append("&7, &r");
            }
        }

        MessageManager.sendMessage(sender, finishedList.toString());
    }
}
