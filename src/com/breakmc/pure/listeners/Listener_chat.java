package com.breakmc.pure.listeners;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.Cooldowns;
import com.breakmc.pure.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Listener_chat implements Listener {

    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (pum.isChatSlowed()) {
            if (!p.hasPermission("pure.slowchat") && !Cooldowns.tryCooldown(p.getUniqueId(), "SlowedChatCooldown", 10000)) {
                e.setCancelled(true);
                MessageManager.sendMessage(p, "&7Chat is currently &bslowed&7, you may only speak once every &b10 &7seconds.");
            }
        }

        if (pum.isChatMuted()) {
            if (!p.hasPermission("pure.mutechat")) {
                e.setCancelled(true);
                MessageManager.sendMessage(p, "&cChat is currently muted, you may not speak.");
            }
        }
    }
}
