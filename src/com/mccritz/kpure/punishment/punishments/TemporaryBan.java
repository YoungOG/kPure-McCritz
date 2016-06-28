package com.mccritz.kpure.punishment.punishments;

import java.util.UUID;

import com.mccritz.kpure.punishment.Punishment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemporaryBan extends Punishment
{
	
	protected long length;
	
	public TemporaryBan(UUID punisherUUID, long length, String reason, String dateIssued)
	{
		super();
		
		this.punisherUUID = punisherUUID;
		this.length = length;
		this.reason = reason;
		this.dateIssued = dateIssued;
	}
	
	@Override
	public boolean isActive()
	{
		return active = System.currentTimeMillis() <= length;
	}
}
