package com.swift.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.UUID;

@Dao
public interface StudentWithClassesDao {
    @Transaction
    @Query("SELECT * FROM students")
    List<StudentWithClasses> getAllStudents();

    @Transaction
    @Query("SELECT * FROM students WHERE NOT id=:id")
    List<StudentWithClasses> getAllStudentsExceptFor(UUID id);

    @Transaction
    @Query("SELECT * FROM students WHERE id=:id")
    StudentWithClasses getStudent(UUID id);

    @Query("SELECT COUNT(*) FROM students")
    int count();
}
