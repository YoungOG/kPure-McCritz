package com.mccritz.kpure.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.mccritz.kpure.kPure;

public class UUIDFetcher implements Callable<Entry<UUID, String>>
{
	
	/**
	 * UUID Fetcher v.2.0 by Max_Plays (02/14/2016)
	 * You may:
	 * - Use this class in your project
	 * - Share it only with your friends
	 * You may not:
	 * - Re-upload it on the internet
	 * - Pretend it belongs to you
	 * - Delete this note
	 * Modified by me. :) - SkillSam
	 */
	
	private String playerName;
	
	public UUIDFetcher(String playerName)
	{
		this.playerName = playerName;
	}
	
	public static Entry<UUID, String> getInformation(String playerName)
	{
		try
		{
			return kPure.SERVICE.submit(new UUIDFetcher(playerName)).get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			return null;
		}
	}
	
	@Override
	public Entry<UUID, String> call() throws Exception
	{
		URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName + "?");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = reader.readLine();
		
		String[] id = line.split(",");
		
		String uuidString = id[0].substring(7, 39);
		UUID uuid = UUID.fromString(uuidString.substring(0, 8) + "-" + uuidString.substring(8, 12) + "-" + uuidString.substring(12, 16)
				+ "-" + uuidString.substring(16, 20) + "-" + uuidString.substring(20, 32));
		String name = id[1].substring(8, id[1].lastIndexOf("\""));
		
		return new AbstractMap.SimpleEntry(uuid, name);
	}
	
}