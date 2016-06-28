package com.mccritz.kpure.punishment.punishments;

import java.util.UUID;

import com.mccritz.kpure.punishment.Punishment;

public class PermanentMute extends Punishment
{
	
	public PermanentMute(UUID punisherUUID, String reason, String dateIssued, boolean active)
	{
		super();
		
		this.punisherUUID = punisherUUID;
		this.reason = reason;
		this.dateIssued = dateIssued;
		this.active = active;
	}
}
