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
    @Query("SELECT * FROM students WHERE NOT student_id=:studentId")
    List<StudentWithClasses> getAllStudentsExceptFor(UUID studentId);

    @Transaction
    @Query("SELECT * FROM students WHERE student_id=:studentId")
    StudentWithClasses getStudent(UUID studentId);

    @Query("SELECT COUNT(*) FROM students")
    int count();
}
