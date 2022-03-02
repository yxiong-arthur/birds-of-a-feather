package com.swift.birdsofafeather.model.db;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "sessions")
public class Session {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "session_id")
    public UUID sessionId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "time_created")
    public Date timeCreated;

    public Session(UUID sessionId){
        this.sessionId = sessionId;
        this.timeCreated = new Date();
        this.name = this.timeCreated.toString();
    }

    public UUID getId() {
        return sessionId;
    }

    public String getName() {
        return name;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setName(String name) {
        this.name = name;
    }
}