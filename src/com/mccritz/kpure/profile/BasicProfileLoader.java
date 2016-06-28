package com.mccritz.kpure.profile;

import java.util.UUID;

public class BasicProfileLoader implements ProfileLoader
{
	
	public BasicProfileLoader(UUID id, ProfileRequest<Profile> callback)
	{
		new Profile(id).loadProfileData(callback, false);
	}
	
	public BasicProfileLoader(String name, ProfileRequest<Profile> callback)
	{
		
		// new BukkitRunnable() {
		// @Override
		// public void run() {
		// UUID id = UUID.fromString(UUIDFetcher.getUUID(name));
		//
		// if (kPure.getInstance().getProfileManager().getp)
		// }
		// }.runTaskAsynchronously(kPure.getInstance());
		
		new Profile(name).loadProfileData(callback, true);
	}
	
	public BasicProfileLoader(Profile provided, ProfileRequest<Profile> callback)
	{
		callback.onComplete(provided, null);
	}
	
	@Override
	public ProfileLoader onComplete(ProfileRequest<Profile> callback)
	{
		return this;
	}
}
