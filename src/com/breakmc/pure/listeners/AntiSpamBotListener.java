package com.breakmc.pure.listeners;

import com.breakmc.pure.Pure;
import net.minecraft.util.gnu.trove.TCollections;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetAddress;

public class AntiSpamBotListener implements Listener {

    private Pure main = Pure.getInstance();
    private TObjectIntMap<InetAddress> addresses = TCollections.synchronizedMap(new TObjectIntHashMap<>());

    @EventHandler
    public void preLogin(PlayerPreLoginEvent e) {
        if (e.getResult() == PlayerPreLoginEvent.Result.ALLOWED) {
            if (addresses.get(e.getAddress()) >= main.getConfig().getInt("anti-spambot.account-limit")) {
                e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You already have " + main.getConfig().getInt("anti-spambot.account-limit") + " accounts using this IP Address.");
                e.setKickMessage(ChatColor.RED + "You already have " + main.getConfig().getInt("anti-spambot.account-limit") + " accounts using this IP Address.");
            }
        }
    }

    @EventHandler
    public void login(PlayerJoinEvent e) {
        if (e.getPlayer() != null ) {
            addresses.adjustOrPutValue(e.getPlayer().getAddress().getAddress(), 1, 1);
        }
    }

    @EventHandler
    public void disconnect(PlayerQuitEvent e) {
        InetAddress addr = e.getPlayer().getAddress().getAddress();
        addresses.adjustValue(addr, -1);

        if (addresses.get(addr) <= 0) {
            addresses.remove(addr);
        }
    }
}