package com.breakmc.pure.commands;

import com.breakmc.pure.utils.Cooldowns;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHub extends BaseCommand {

    public CommandHub() {
        super("hub", null, CommandUsageBy.PlAYER, "lobby");
        setMinArgs(0);
        setMaxArgs(0);
    }

    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;

        if (Cooldowns.tryCooldown(p.getUniqueId(), "Hub", 5000)) {
            MessageManager.sendMessage(p, "&7Sending you to the &bHub&7.");
            PlayerUtility.connectToServer(p, "Hub");
        } else {
            MessageManager.sendMessage(sender, "&7Please try again in &b" + (Cooldowns.getCooldown(((Player) sender).getUniqueId(), "Hub") / 1000) + " &7seconds.");
        }
    }
}
