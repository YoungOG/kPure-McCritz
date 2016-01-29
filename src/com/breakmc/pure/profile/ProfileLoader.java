package com.breakmc.pure.profile;

public interface ProfileLoader {

    ProfileLoader onComplete(ProfileRequest<Profile> callback);
}
