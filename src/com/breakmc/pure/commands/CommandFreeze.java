package com.breakmc.pure.commands;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandFreeze extends BaseCommand {

    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    public CommandFreeze() {
        super("freeze", "pure.freeze", CommandUsageBy.ANYONE, "screenshare", "ss");
        setUsage("&cImproper Usage! /freeze (player)");
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (Bukkit.getPlayer(args[0]) == null) {
            MessageManager.sendMessage(sender, "&cPlayer \"" + args[0] + "\" could not be found.");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (!pum.getFrozen().contains(target.getUniqueId())) {
            pum.getFrozen().add(target.getUniqueId());
            target.setAllowFlight(true);
            target.setFlying(true);

            MessageManager.sendMessage(target, "&f\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
            MessageManager.sendMessage(target, "&f\u2588\u2588\u2588\u2588&c\u2588&f\u2588\u2588\u2588\u2588");
            MessageManager.sendMessage(target, "&f\u2588\u2588\u2588&c\u2588&0\u2588&c\u2588&f\u2588\u2588\u2588");
            MessageManager.sendMessage(target, "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588");
            MessageManager.sendMessage(target, "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588 &eYou have been frozen by a BreakMC staff member!");
            MessageManager.sendMessage(target, "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588 &eIf you disconnect you will be &4&lBANNED!");
            MessageManager.sendMessage(target, "&f\u2588&c\u2588&6\u2588\u2588\u2588&6\u2588\u2588&c\u2588&f\u2588 &ePlease connect to our TS: &cts.breakmc.com!");
            MessageManager.sendMessage(target, "&c\u2588&6\u2588\u2588\u2588&0\u2588&6\u2588\u2588\u2588&c\u2588");
            MessageManager.sendMessage(target, "&c\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
            MessageManager.sendMessage(target, "&f\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");

            MessageManager.broadcast("pure.freeze", "&b" + sender.getName() + " has frozen " + target.getName() + ".");
        } else {
            pum.getFrozen().remove(target.getUniqueId());
            target.setAllowFlight(false);
            target.setFlying(false);

            MessageManager.sendMessage(target, "&aYou have been unfrozen!");
            MessageManager.broadcast("pure.freeze", "&b" + sender.getName() + " has unfrozen " + target.getName() + ".");
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
