package com.breakmc.pure.listeners;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.Cooldowns;
import com.breakmc.pure.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class ChatListener implements Listener {

    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();
    private HashMap<UUID, Long> chatCooldowns = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (pum.isChatSlowed()) {
            if (!p.hasPermission("pure.slowchat") && !Cooldowns.tryCooldown(p.getUniqueId(), "SlowedChatCooldown", (pum.getChatSlowSeconds() * 1000))) {
                e.setCancelled(true);
                MessageManager.sendMessage(p, "&7Chat is currently &bslowed&7, you may only speak once every &b" + pum.getChatSlowSeconds() + " &7seconds.");
            }
        }

        if (pum.isChatMuted()) {
            if (!p.hasPermission("pure.mutechat")) {
                e.setCancelled(true);
                MessageManager.sendMessage(p, "&cChat is currently muted, you may not speak.");
            }
        }
    }

    @EventHandler
    public void onSpam(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) {
            return;
        }

        Player p = e.getPlayer();

        if (p.hasPermission("pure.antispam")) {
            return;
        }

        Long time = System.currentTimeMillis();

        Long lastUse = chatCooldowns.get(p.getUniqueId());

        if (lastUse == null) {
            lastUse = 0L;
        }

        if (lastUse + 1000L > time) {
            MessageManager.sendMessage(p, "&cPlease avoid spamming the chat.");
            e.setCancelled(true);
        }

        chatCooldowns.remove(p.getUniqueId());
        chatCooldowns.put(p.getUniqueId(), time);
    }
}