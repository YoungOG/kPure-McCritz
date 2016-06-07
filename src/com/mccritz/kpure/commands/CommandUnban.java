package com.mccritz.kpure.commands;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.punishment.PunishmentManager;
import com.mccritz.kpure.utils.IPUtils;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;

public class CommandUnban extends BaseCommand {

    private ProfileManager pm = kPure.getInstance().getProfileManager();
    private PunishmentManager pum = kPure.getInstance().getPunishmentManager();

    public CommandUnban() {
        super("unban", "kpure.unban", CommandUsageBy.ANYONE);
        setUsage("&c/unban <name/ip>");
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!IPUtils.isValidIP(args[0])) {
            pm.requestProfile(args[0], (result, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND(args[0]));
                    return;
                }

                if (result == null) {
                    MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND(args[0]));
                    return;
                }

                pum.unban(sender, result);
            });
        } else {
            pum.unban(sender, args[0]);
        }
    }
}
