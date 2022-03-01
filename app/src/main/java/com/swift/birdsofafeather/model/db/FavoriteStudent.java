package com.swift.birdsofafeather.model.db;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "favorite-students")
public class FavoriteStudent {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "session_id")
    public UUID sessionId;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "student_id")
    public UUID studentId;

    public FavoriteStudent(UUID sessionId, UUID studentId){
        this.sessionId = sessionId;
        this.studentId = studentId;
    }

    public UUID getSessionId() {
        return sessionId;
    }
    public UUID getStudentId() { return studentId; }

}