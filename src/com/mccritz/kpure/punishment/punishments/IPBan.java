package com.mccritz.kpure.punishment.punishments;

import java.util.UUID;

import com.mccritz.kpure.punishment.Punishment;

import lombok.Getter;

@Getter
public class IPBan extends Punishment {

    private String address;

    public IPBan(String address, UUID punisherUUID, String reason, String dateIssued, boolean active) {
	super(punisherUUID, reason, dateIssued, active);
	this.address = address;
    }
}