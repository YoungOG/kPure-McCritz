package com.breakmc.pure.listeners;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FreezeListener implements Listener {

    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (p.getWalkSpeed() != 0.2F) {
            p.setWalkSpeed(0.2F);
        }

        if (pum.getFrozen().contains(p.getUniqueId())) {
            p.setAllowFlight(false);
            p.setFlying(false);
            pum.getFrozen().remove(p.getUniqueId());
            MessageManager.broadcast("pure.freeze", "&7(&aFrozen&7) &a" + p.getName() + " &7has logged out while frozen.");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (pum.isServerFrozen()) {
            if (!p.hasPermission("pure.freeze")) {
                if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
                    e.setTo(e.getFrom());
                }
            } else {
                if (p.getWalkSpeed() != 0.2F) {
                    p.setWalkSpeed(0.2F);
                }
            }
        }

        if (pum.getFrozen().contains(p.getUniqueId())) {
            e.setTo(e.getFrom());
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        if (pum.getFrozen().contains(p.getUniqueId())) {
            e.setCancelled(true);
            MessageManager.sendMessage(e.getPlayer(), "&cYou cannot do that while frozen.");
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

        if (pum.getFrozen().contains(p.getUniqueId())) {
            e.setCancelled(true);
            MessageManager.sendMessage(e.getPlayer(), "&cYou cannot do that while frozen.");
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();

        if (pum.getFrozen().contains(p.getUniqueId())) {
            e.setCancelled(true);
            MessageManager.sendMessage(e.getPlayer(), "&cYou cannot do that while frozen.");
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player hit = (Player) e.getEntity();

            if (e.getDamager() instanceof Player) {
                Player hitter = (Player) e.getDamager();

                if (pum.getFrozen().contains(hit.getUniqueId())) {
                    e.setCancelled(true);
                    MessageManager.sendMessage(hitter, "&cYou cannot attack frozen players.");
                }

                if (pum.getFrozen().contains(hitter.getUniqueId())) {
                    e.setCancelled(true);
                    MessageManager.sendMessage(hitter, "&cYou cannot do that while frozen.");
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (e.getEntity() instanceof Player) {
                Player damaged = (Player) e.getEntity();

                if (pum.getFrozen().contains(damaged.getUniqueId())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player) {
            if (pum.getFrozen().contains(e.getTarget().getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        if (pum.getFrozen().contains(p.getUniqueId())) {
            e.setCancelled(true);
            MessageManager.sendMessage(p, "&cYou cannot use commands while frozen.");
        }
    }
}
