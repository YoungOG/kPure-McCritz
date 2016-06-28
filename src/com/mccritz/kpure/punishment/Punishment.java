package com.mccritz.kpure.punishment;

import java.util.UUID;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Punishment
{
	
	protected UUID		punisherUUID;
	protected String	reason;
	protected String	dateIssued;
	protected boolean	active;
	
	public String getPunisherName()
	{
		return punisherUUID != null ? Bukkit.getOfflinePlayer(punisherUUID).getName() : "Console";
	}
}
