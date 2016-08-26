package com.mccritz.kpure.commands;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.profile.Profile;
import com.mccritz.kpure.profile.ProfileManager;
import com.mccritz.kpure.utils.MessageManager;
import com.mccritz.kpure.utils.command.BaseCommand;
import com.mccritz.kpure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRegister extends BaseCommand {

    private ProfileManager pm = kPure.getInstance().getProfileManager();

    public CommandRegister() {
        super("register", "kpure.register", CommandUsageBy.PlAYER);
        setUsage("&c/register <password>");
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Profile profile = pm.getProfile(player.getUniqueId());

        if (args.length == 1) {
            if (args[0].length() < 3) {
                MessageManager.sendMessage(player, "&cYour password must be atleast 3 characters long.");
                return;
            }

            if (!args[0].matches("[A-Za-z0-9]+")) {
                MessageManager.sendMessage(player, "&cYour password must be alphanumerical.");
                return;
            }

            profile.setPassword(args[0]);
            pm.saveProfile(profile);

            MessageManager.sendMessage(player, "&7You have successfully set your password.");
        }
    }
}