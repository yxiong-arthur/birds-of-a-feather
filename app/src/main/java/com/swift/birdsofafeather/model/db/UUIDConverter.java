package com.swift.birdsofafeather.model.db;

import androidx.room.TypeConverter;

import java.util.UUID;

// adapted from stack overflow answer:
// https://stackoverflow.com/questions/59572749/using-uuid-for-primary-key-using-room-with-android
public class UUIDConverter {
    @TypeConverter
    public static String fromUUID(UUID uuid) {
        return uuid.toString();
    }

    @TypeConverter
    public static UUID uuidFromString(String string) {
        return UUID.fromString(string);
    }
}
