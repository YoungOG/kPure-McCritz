package com.mccritz.kpure.profile;

public interface ProfileRequest<R> {

    void onComplete(R result, Throwable throwable);
}
