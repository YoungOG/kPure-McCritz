package com.mccritz.kpure.punishment.punishments;

import java.util.UUID;

import com.mccritz.kpure.punishment.Punishment;

public class PermanentBan extends Punishment {

    public PermanentBan(UUID punisherUUID, String reason, String dateIssued, boolean active) {
	super(punisherUUID, reason, dateIssued, active);
    }
}