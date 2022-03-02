package com.swift.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.UUID;

@Dao
public interface StudentDao {
    @Transaction
    @Query("SELECT * FROM students")
    List<Student> getAllStudents();

    @Transaction
    @Query("SELECT * FROM students WHERE favorite=1")
    List<Student> getAllFavoritedStudents();

    @Query("SELECT * FROM students WHERE student_id=:studentId")
    Student getStudent(UUID studentId);

    @Query("SELECT COUNT(*) FROM students")
    int count();

    @Query("SELECT EXISTS(SELECT * FROM students WHERE student_id=:studentId)")
    boolean checkExists(UUID studentId);

    @Query("UPDATE students SET favorite=:favorited WHERE student_id=:id")
    void updateFavorited(UUID id, boolean favorited);

    @Insert
    void insert(Student student);

    @Query("DELETE FROM students WHERE NOT student_id=:studentId")
    void deleteExcept(UUID studentId);

    @Query("DELETE FROM students")
    void nukeTable();
}
