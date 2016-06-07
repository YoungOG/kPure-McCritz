package com.mccritz.kpure.profile;

import java.util.UUID;

public class BasicProfileLoader implements ProfileLoader {

    private ProfileRequest<Profile> callback;

    public BasicProfileLoader(UUID id, ProfileRequest<Profile> callback) {
        this.callback = callback;

        new Profile(id).loadProfileData(callback, false);
    }

    public BasicProfileLoader(String name, ProfileRequest<Profile> callback) {
        this.callback = callback;

        new Profile(name).loadProfileData(callback, true);
    }

    public BasicProfileLoader(Profile provided, ProfileRequest<Profile> callback) {
        this.callback = callback;

        callback.onComplete(provided, null);
    }

    @Override
    public ProfileLoader onComplete(ProfileRequest<Profile> callback) {
        this.callback = callback;
        return this;
    }
}
