package com.mccritz.kpure.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.mccritz.kpure.kPure;

public class PlayerUtility
{
	
	public static double getHealth(Player p)
	{
		return ((Damageable) p).getHealth();
	}
	
	public static Player[] getOnlinePlayers()
	{
		return Bukkit.getOnlinePlayers();
	}
	
	public static boolean hasInventorySpace(Inventory inventory, org.bukkit.inventory.ItemStack is)
	{
		Inventory inv = Bukkit.createInventory(null, inventory.getSize());
		
		for (int i = 0; i < inv.getSize(); i++)
		{
			if (inventory.getItem(i) != null)
			{
				org.bukkit.inventory.ItemStack item = inventory.getItem(i).clone();
				inv.setItem(i, item);
			}
		}
		
		return inv.addItem(new org.bukkit.inventory.ItemStack[] { is.clone() }).size() <= 0;
	}
	
	public static void connectToServer(Player p, String channel)
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try
		{
			out.writeUTF("Connect");
			out.writeUTF(channel);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		p.sendPluginMessage(kPure.getInstance(), "BungeeCord", b.toByteArray());
	}
	
	public static List<String> toList(Player[] array)
	{
		List<String> list = new ArrayList<>();
		
		for (Player t : array)
		{
			list.add(t.getName());
		}
		
		return list;
	}
}
