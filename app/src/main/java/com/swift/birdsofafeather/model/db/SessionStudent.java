package com.swift.birdsofafeather.model.db;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "sessions-students",
        foreignKeys = {
            @ForeignKey(entity = Student.class, parentColumns = "student_id",childColumns = "student_id"),
            @ForeignKey(entity = Session.class, parentColumns = "session_id",childColumns = "session_id")
        },
        primaryKeys = {
            "session_id", "student_id"
        },
        indices = {
            @Index("student_id")
        })
public class SessionStudent {
    @NonNull
    @ColumnInfo(name = "session_id")
    public UUID sessionId;

    @NonNull
    @ColumnInfo(name = "student_id")
    public UUID studentId;

    public SessionStudent(UUID sessionId, UUID studentId){
        this.sessionId = sessionId;
        this.studentId = studentId;
    }

    public UUID getStudentId(){
        return studentId;
    }
}