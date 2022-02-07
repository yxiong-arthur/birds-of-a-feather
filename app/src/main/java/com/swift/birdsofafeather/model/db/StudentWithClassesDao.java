package com.swift.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface StudentWithClassesDao {
    @Transaction
    @Query("SELECT * FROM students")
    List<StudentWithClasses> getAllStudents();

    @Transaction
    @Query("SELECT * FROM students WHERE id=:id")
    StudentWithClasses getStudent(int id);

    @Query("SELECT COUNT(*) FROM students")
    int count();
}
