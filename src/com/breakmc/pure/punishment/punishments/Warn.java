package com.breakmc.pure.punishment.punishments;

import com.breakmc.pure.punishment.Punishment;

import java.util.UUID;

public class Warn extends Punishment {

    public Warn(UUID punisherUUID, String reason, String dateIssued) {
        super();

        this.punisherUUID = punisherUUID;
        this.reason = reason;
        this.dateIssued = dateIssued;
    }
}