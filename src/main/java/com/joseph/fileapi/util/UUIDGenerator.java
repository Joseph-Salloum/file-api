package com.joseph.fileapi.util;

import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;


public final class UUIDGenerator {
    public static UUID generateUUIDv7() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
