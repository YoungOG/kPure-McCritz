package com.mccritz.kpure.punishment.punishments;

import com.mccritz.kpure.punishment.Punishment;

import java.util.UUID;

public class PermanentBan extends Punishment {

    public PermanentBan(UUID punisherUUID, String reason, String dateIssued, boolean active) {
        super();

        this.punisherUUID = punisherUUID;
        this.reason = reason;
        this.dateIssued = dateIssued;
        this.active = active;
    }
}