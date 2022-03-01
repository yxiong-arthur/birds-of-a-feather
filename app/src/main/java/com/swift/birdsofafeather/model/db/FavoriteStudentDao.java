package com.swift.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.UUID;

@Dao
public interface FavoriteStudentDao {
    @Transaction
    @Query("SELECT * FROM `favorite-students`")
    List<FavoriteStudent> getAllFavoriteStudentRelations();

    @Query("SELECT * FROM `favorite-students` WHERE session_id=:sessionId")
    List<FavoriteStudent> getStudentsBySession(UUID sessionId);

    @Insert
    void insert(FavoriteStudent favoriteStudent);

    @Query("DELETE FROM `favorite-students`")
    void nukeTable();
}