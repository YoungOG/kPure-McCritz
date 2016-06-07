package com.mccritz.kpure.punishment;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.UUID;

@Setter
@Getter
public abstract class Punishment {

    protected UUID punisherUUID;
    protected String reason;
    protected String dateIssued;
    protected boolean active;

    public String getPunisherName() {
        return ((punisherUUID != null) ? Bukkit.getOfflinePlayer(punisherUUID).getName() : "Console");
    }
}
