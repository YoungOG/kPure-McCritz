package com.breakmc.pure.listeners;

import com.breakmc.pure.Pure;
import com.breakmc.pure.punishment.PunishmentManager;
import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.PlayerUtility;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class Listener_vanish implements Listener {

    private PunishmentManager pum = Pure.getInstance().getPunishmentManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        HumanEntity player = e.getWhoClicked();

        if (Pure.getInstance().getPunishmentManager().getInventories().values().contains(inventory)) {
            if (!player.hasPermission("pure.invsee.edit")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (pum.isVanished(all)) {
                if (!e.getPlayer().hasPermission("pure.vanish")) {
                    e.getPlayer().hidePlayer(all);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (pum.isVanished(e.getPlayer())) {
            pum.removeVanisher(e.getPlayer());
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (pum.isVanished(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (pum.isVanished(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (pum.isVanished(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!p.isSneaking() && (e.getAction() == Action.RIGHT_CLICK_BLOCK) && pum.isVanished(p)) {
            Block b = e.getClickedBlock();
            Inventory inv = null;
            BlockState blockState = b.getState();

            switch (b.getType()) {
                case TRAPPED_CHEST:
                case CHEST:
                    Chest chest = (Chest) blockState;
                    inv = Pure.getInstance().getServer().createInventory(p, chest.getInventory().getSize());
                    inv.setContents(chest.getInventory().getContents());
                    break;
                case ENDER_CHEST:
                    inv = p.getEnderChest();
                    break;
                case DISPENSER:
                    inv = ((Dispenser) blockState).getInventory();
                    break;
                case HOPPER:
                    inv = ((Hopper) blockState).getInventory();
                    break;
                case DROPPER:
                    inv = ((Dropper) blockState).getInventory();
                    break;
                case FURNACE:
                    inv = ((Furnace) blockState).getInventory();
                    break;
                case BREWING_STAND:
                    inv = ((BrewingStand) blockState).getInventory();
                    break;
                case BEACON:
                    inv = ((Beacon) blockState).getInventory();
                    break;
            }

            e.setCancelled(true);

            if (inv != null) {
                MessageManager.sendMessage(p, "&7Container opened silently.");
                p.openInventory(inv);
                return;
            }
        }

        if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.SOIL) {
            if (pum.isVanished(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player) {
            if (pum.isVanished(((Player) e.getTarget()))) {
                e.setCancelled(true);
            }
        }
    }
}
