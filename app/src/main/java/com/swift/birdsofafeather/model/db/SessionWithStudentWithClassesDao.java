package com.swift.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.UUID;

@Dao
public interface SessionWithStudentWithClassesDao {
    @Transaction
    @Query("SELECT * FROM sessions")
    List<SessionWithStudentWithClasses> getAllSessions();

    @Transaction
    @Query("SELECT * FROM sessions WHERE session_id=:sessionId")
    SessionWithStudentWithClasses getSession(UUID sessionId);

    @Query("SELECT COUNT(*) FROM sessions")
    int count();
}