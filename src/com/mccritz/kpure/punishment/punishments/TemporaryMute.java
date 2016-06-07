package com.mccritz.kpure.punishment.punishments;

import com.mccritz.kpure.punishment.Punishment;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TemporaryMute extends Punishment {

    protected long length;

    public TemporaryMute(UUID punisherUUID, long length, String reason, String dateIssued) {
        super();

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
