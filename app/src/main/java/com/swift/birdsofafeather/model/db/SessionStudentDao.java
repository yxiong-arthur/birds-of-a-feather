package com.swift.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.UUID;

@Dao
public interface SessionStudentDao {
    @Transaction
    @Query("SELECT * FROM `sessions-students`")
    List<SessionStudent> getAllSessionStudentRelations();

    @Query("SELECT * FROM `sessions-students` WHERE student_id=:studentId")
    List<SessionStudent> getSessionsByStudent(UUID studentId);

    @Query("SELECT * FROM `sessions-students` WHERE session_id=:sessionId")
    List<SessionStudent> getStudentsBySession(UUID sessionId);

    @Insert
    void insert(SessionStudent sessionStudent);

    @Query("DELETE FROM `sessions-students`")
    void nukeTable();
}