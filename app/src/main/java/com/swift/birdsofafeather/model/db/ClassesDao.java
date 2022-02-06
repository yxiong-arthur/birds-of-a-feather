package com.swift.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.swift.birdsofafeather.model.IClass;

import java.util.List;

@Dao
public interface ClassesDao {
    @Transaction
    @Query("SELECT * FROM classes")
    List<IClass> getAllClasses(int studentId);

    @Transaction
    @Query("SELECT * FROM classes WHERE student_id=:studentId")
    List<IClass> getForStudent(int studentId);

    @Query("SELECT * FROM classes WHERE id=:id")
    Class getClass(int id);

    @Query("SELECT COUNT(*) FROM classes")
    int count();

    @Insert
    void insert(Class toInsert);
}
