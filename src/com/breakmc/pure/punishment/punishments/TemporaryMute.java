package com.breakmc.pure.punishment.punishments;

import com.breakmc.pure.punishment.Punishment;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TemporaryMute extends Punishment {

    protected long length;

    public TemporaryMute(UUID punishedUUID, UUID punisherUUID, long length, String reason, String dateIssued) {
        super();

        this.punishedUUID = punishedUUID;
        this.punisherUUID = punisherUUID;
        this.length = length;
        this.reason = reason;
        this.dateIssued = dateIssued;
    }

    @Override
    public boolean isActive() {
        return active = System.currentTimeMillis() <= length;
    }
}
