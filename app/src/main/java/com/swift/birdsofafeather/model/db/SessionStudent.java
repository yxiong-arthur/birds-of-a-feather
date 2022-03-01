package com.swift.birdsofafeather.model.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "sessions-students")
public class SessionStudent {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "session_id")
    public UUID sessionId;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "student_id")
    public UUID studentId;

    public SessionStudent(UUID sessionId, UUID studentId){
        this.sessionId = sessionId;
        this.studentId = studentId;
    }
}