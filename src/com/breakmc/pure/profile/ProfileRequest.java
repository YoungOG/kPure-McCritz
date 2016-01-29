package com.breakmc.pure.profile;

public interface ProfileRequest<R> {

    void onComplete(R result, Throwable throwable);
}
