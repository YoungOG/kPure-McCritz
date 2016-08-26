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
        super(punisherUUID, reason, dateIssued, System.currentTimeMillis() <= length);
        this.length = length;
    }

    @Override
    public boolean isActive() {
        boolean active = System.currentTimeMillis() <= length;
        super.setActive(active);
        return active;
    }
}