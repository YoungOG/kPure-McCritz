package com.mccritz.kpure.punishment;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.UUID;

@Setter
@Getter
public abstract class Punishment {

    private UUID punisherUUID;
    private String reason;
    private String dateIssued;
    private boolean active;

    public Punishment(UUID punisherUUID, String reason, String dateIssued, boolean active) {
        this.punisherUUID = punisherUUID;
        this.reason = reason;
        this.dateIssued = dateIssued;
        this.active = active;
    }

    public String getPunisherName() {
        return punisherUUID != null ? Bukkit.getOfflinePlayer(punisherUUID).getName() : "Console";
    }
}
