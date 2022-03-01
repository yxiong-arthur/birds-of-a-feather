package com.swift.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.UUID;

@Dao
public interface SessionDao {
    @Transaction
    @Query("SELECT * FROM sessions")
    List<Session> getAllSessions();

    @Query("SELECT * FROM sessions WHERE session_id=:sessionId")
    Student getSession(UUID sessionId);

    @Query("SELECT COUNT(*) FROM sessions")
    int count();

    @Insert
    void insert(Session session);

    @Query("UPDATE sessions SET name=:name WHERE session_id= :sessionId")
    void updateName(UUID sessionId, String name);

    @Query("DELETE FROM sessions")
    void nukeTable();
}