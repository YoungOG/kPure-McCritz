package com.breakmc.pure.punishment.punishments;

import com.breakmc.pure.punishment.Punishment;

import java.util.UUID;

public class PermanentMute extends Punishment {

    public PermanentMute(UUID punishedUUID, UUID punisherUUID, String reason, String dateIssued, boolean active) {
        super();

        this.punishedUUID = punishedUUID;
        this.punisherUUID = punisherUUID;
        this.reason = reason;
        this.dateIssued = dateIssued;
        this.active = active;
    }
}
