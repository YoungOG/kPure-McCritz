package com.mccritz.kpure.punishment.punishments;

import java.util.UUID;

import com.mccritz.kpure.punishment.Punishment;

import lombok.Getter;

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
