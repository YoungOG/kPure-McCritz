package com.mccritz.kpure.profile;

public interface ProfileLoader {

    ProfileLoader onComplete(ProfileRequest<Profile> callback);
}
