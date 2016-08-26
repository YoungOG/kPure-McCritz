package com.mccritz.kpure.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUtility {

    public static Player[] getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

}