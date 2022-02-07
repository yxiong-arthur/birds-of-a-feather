package com.swift.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface StudentDao {
    @Transaction
    @Query("SELECT * FROM students")
    List<Student> getAllStudents();

    @Query("SELECT * FROM students WHERE id=:id")
    Student getStudent(int id);

    @Query("SELECT COUNT(*) FROM students")
    int count();

    @Insert
    void insert(Student student);

    @Query("DELETE FROM students")
    void nukeTable();
}
