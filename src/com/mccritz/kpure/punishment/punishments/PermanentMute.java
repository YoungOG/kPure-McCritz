package com.mccritz.kpure.punishment.punishments;

import com.mccritz.kpure.punishment.Punishment;

import java.util.UUID;

public class PermanentMute extends Punishment {

    public PermanentMute(UUID punisherUUID, String reason, String dateIssued, boolean active) {
        super(punisherUUID, reason, dateIssued, active);
    }
}
