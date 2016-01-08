package com.breakmc.pure.utils;

import com.breakmc.pure.Pure;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerUtility {

    public static double getHealth(Player p) {
        return p.getHealth();
    }

    public static Player[] getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public static boolean hasInventorySpace(Inventory inventory, org.bukkit.inventory.ItemStack is) {
        Inventory inv = Bukkit.createInventory(null, inventory.getSize());

        for (int i = 0; i < inv.getSize(); i++) {
            if (inventory.getItem(i) != null) {
                org.bukkit.inventory.ItemStack item = inventory.getItem(i).clone();
                inv.setItem(i, item);
            }
        }

        return inv.addItem(new org.bukkit.inventory.ItemStack[]{is.clone()}).size() <= 0;
    }

    public static String getGroup(String name) {
        if (!Bukkit.getPluginManager().isPluginEnabled(PermissionsEx.getPlugin())) {
            return "";
        }

        PermissionUser user = PermissionsEx.getUser(name);
        if (user == null)
            return "";

        PermissionGroup[] groups = user.getGroups();

        if (groups.length == 0)
            return "";

        return groups[0].getName();
    }

    public static List<String> toList(Player[] array) {
        List<String> list = new ArrayList<>();
        for (Player t : array) {
            list.add(t.getName());
        }
        return list;
    }

    public static void connectToServer(Player p, String channel) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        p.sendPluginMessage(Pure.getInstance(), "BungeeCord", b.toByteArray());
    }
}
