package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_setslots extends BaseCommand {

    private Pure main = Pure.getInstance();

    public Command_setslots() {
        super("setslots", "pure.setslots", CommandUsageBy.ANYONE, "setslots");
        setUsage("/<command> (number)");
        setMinArgs(1);
        setMaxArgs(1);
    }

    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        try {
            Integer.parseInt(args[0]);
        } catch (NumberFormatException ignored) {
            MessageManager.sendMessage(player, "&cPlease enter a valid number.");
            return;
        }

        if (Integer.parseInt(args[0]) > Bukkit.getServer().getMaxPlayers()) {
            MessageManager.sendMessage(player, "&cYou cannot set the playercount higher than what it is set to in the server properties.");
            return;
        }

        main.setPlayerCount(Integer.parseInt(args[0]));
        main.getConfig().set("player-count", Integer.parseInt(args[0]));
    }
}
