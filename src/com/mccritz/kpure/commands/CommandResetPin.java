package com.mccritz.kpure.commands;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandResetPin extends BaseCommand {

    private ProfileManager pm = kPure.getInstance().getProfileManager();

    public CommandResetPin() {
        super("resetpin", "kpure.delpin", CommandUsageBy.PlAYER, "delpin", "deletepin");
        setUsage("&c/resetpin <player>");
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        Profile result = pm.getProfile(args[0]);

        if (result == null) {
            MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND(args[0]));
            return;
        }

        result.setPin("");
        pm.saveProfile(result);

        MessageManager.sendMessage(p, "&7You have reset the pin of &c" + result.getCurrentName() + "&7.");
    }
}