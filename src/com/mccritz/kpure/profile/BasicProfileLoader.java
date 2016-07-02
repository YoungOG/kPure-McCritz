package com.mccritz.kpure.profile;

import java.util.Map.Entry;
import java.util.UUID;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.utils.UUIDFetcher;

public class BasicProfileLoader implements ProfileLoader {

    private ProfileManager pm = kPure.getInstance().getProfileManager();
    
    public BasicProfileLoader(UUID id, ProfileRequest<Profile> callback) {
	new Profile(id).loadProfileData(callback, false);
    }

    public BasicProfileLoader(String name, ProfileRequest<Profile> callback) {

	// new BukkitRunnable() {
	// @Override
	// public void run() {
	// UUID id = UUID.fromString(UUIDFetcher.getUUID(name));
	//
	// if (kPure.getInstance().getProfileManager().getp)
	// }
	// }.runTaskAsynchronously(kPure.getInstance());
	
	Entry<UUID, String> info = UUIDFetcher.getInformation(name);
	
	if (info != null)
	{
	    callback.onComplete(pm.createSimpleProfile(info.getKey(), info.getValue()), null);
	}
	else
	{
	    new Profile(name).loadProfileData(callback, true);
	}

//	new Profile(name).loadProfileData(callback, true);
    }

    public BasicProfileLoader(Profile provided, ProfileRequest<Profile> callback) {
	callback.onComplete(provided, null);
    }

    @Override
    public ProfileLoader onComplete(ProfileRequest<Profile> callback) {
	return this;
    }
}
