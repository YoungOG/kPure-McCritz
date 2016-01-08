package com.breakmc.pure.punishment.punishments;

import com.breakmc.pure.punishment.Punishment;
import lombok.Getter;

import java.util.UUID;

@Getter
public class IPBan extends Punishment {

    protected String address;

    public IPBan(String address, UUID punisherUUID, String reason, String dateIssued, boolean active) {
        super();

        this.address = address;
        this.punisherUUID = punisherUUID;
        this.reason = reason;
        this.dateIssued = dateIssued;
        this.active = active;
    }
}
