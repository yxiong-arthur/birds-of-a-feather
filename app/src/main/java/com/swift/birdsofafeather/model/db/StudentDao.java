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
    @Query("SELECT * FROM students WHERE favorite=1 AND waved_from=1")
    List<Student> getAllFavoritedAndWavedFromStudents();

    @Transaction
    @Query("SELECT * FROM students WHERE favorite=0 AND waved_from=1")
    List<Student> getAllWavedFromOnlyStudents();

    @Transaction
    @Query("SELECT * FROM students WHERE favorite=1 AND waved_from=0")
    List<Student> getAllFavoritedOnlyStudents();

    @Transaction
    @Query("SELECT * FROM students WHERE favorite=0 AND waved_from=0")
    List<Student> getAllRegularStudents();

    @Transaction
    @Query("SELECT * FROM students WHERE favorite=1")
    List<Student> getAllFavoritedStudents();

    @Transaction
    @Query("SELECT * FROM students WHERE waved_to=1")
    List<Student> getAllWavedToStudents();

    @Query("SELECT * FROM students WHERE student_id=:studentId")
    Student getStudent(UUID studentId);

    @Query("SELECT COUNT(*) FROM students")
    int count();

    @Query("SELECT EXISTS(SELECT * FROM students WHERE student_id=:studentId)")
    boolean checkExists(UUID studentId);

    @Query("SELECT waved_to FROM students WHERE student_id=:studentId")
    boolean hasWavedTo(UUID studentId);

    @Insert
    void insert(Student student);

    @Query("UPDATE students SET class_score=:classScore WHERE student_id= :studentId")
    void updateClassScore(UUID studentId, int classScore);

    @Query("UPDATE students SET recency_score=:recencyScore WHERE student_id= :studentId")
    void updateRecencyScore(UUID studentId, int recencyScore);

    @Query("UPDATE students SET size_score=:sizeScore WHERE student_id= :studentId")
    void updateSizeScore(UUID studentId, double sizeScore);

    @Query("UPDATE students SET quarter_score=:quarterScore WHERE student_id= :studentId")
    void updateQuarterScore(UUID studentId, int quarterScore);

    @Query("UPDATE students SET favorite=:favorited WHERE student_id=:id")
    void updateFavorited(UUID id, boolean favorited);

    @Query("UPDATE students SET waved_to=:wavedTo WHERE student_id= :studentId")
    void updateWavedTo(UUID studentId, boolean wavedTo);

    @Query("UPDATE students SET waved_from=:wavedFrom WHERE student_id= :studentId")
    void updateWavedFrom(UUID studentId, boolean wavedFrom);

    @Query("DELETE FROM students")
    void nukeTable();
}
