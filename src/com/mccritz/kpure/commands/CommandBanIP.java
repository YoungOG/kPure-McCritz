package com.mccritz.kpure.commands;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.punishment.PunishmentManager;
import com.mccritz.kpure.utils.IPUtils;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

public class CommandBanIP extends BaseCommand {

    private ProfileManager pm = kPure.getInstance().getProfileManager();
    private PunishmentManager pum = kPure.getInstance().getPunishmentManager();

    public CommandBanIP() {
        super("banip", "kpure.banip", CommandUsageBy.ANYONE, "blacklist");
        setUsage("&c/banip <player/ip> <reason>");
        setMinArgs(2);
        setMaxArgs(100);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!IPUtils.isValidIP(args[0])) {
            Profile result = pm.getProfile(args[0]);

            if (result == null) {
                MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND(args[0]));
                return;
            }

            pum.banIP(sender, result.getCurrentIP(), StringUtils.join(args, " ", 1, args.length));
        } else {
            pum.banIP(sender, args[0], StringUtils.join(args, " ", 1, args.length));
        }
    }
}