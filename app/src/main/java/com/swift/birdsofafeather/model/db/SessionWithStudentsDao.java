package com.swift.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.UUID;

@Dao
public interface SessionWithStudentsDao {
    @Transaction
    @Query("SELECT * FROM sessions")
    List<SessionWithStudents> getAllSessions();

    @Transaction
    @Query("SELECT * FROM sessions WHERE session_id=:sessionId")
    SessionWithStudents getSession(UUID sessionId);

    @Query("SELECT COUNT(*) FROM sessions")
    int count();
}