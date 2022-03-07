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
    Session getSession(UUID sessionId);

    @Query("SELECT name FROM sessions WHERE session_id=:sessionId")
    String getName(UUID sessionId);

    @Query("SELECT COUNT(*) FROM sessions")
    int count();

    @Query("SELECT EXISTS(SELECT * FROM sessions WHERE session_id=:sessionId)")
    boolean checkExists(UUID sessionId);

    @Query("SELECT named FROM sessions WHERE session_id=:sessionId")
    boolean checkNamed(UUID sessionId);

    @Insert
    void insert(Session session);

    @Query("UPDATE sessions SET name=:name, named='true' WHERE session_id= :sessionId")
    void updateName(UUID sessionId, String name);

    @Query("DELETE FROM sessions")
    void nukeTable();
}