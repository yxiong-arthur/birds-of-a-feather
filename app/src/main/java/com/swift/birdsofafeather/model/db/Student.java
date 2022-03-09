package com.swift.birdsofafeather.model.db;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "students")
public class Student {
    @PrimaryKey
    @ColumnInfo(name = "student_id")
    public UUID studentId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "profile_picture")
    public Bitmap picture;

    @ColumnInfo(name = "class_score")
    public int classScore;

    @ColumnInfo(name = "recency_score")
    public int recencyScore;

    @ColumnInfo(name = "size_score")
    public double sizeScore;

    @ColumnInfo(name = "quarter_score")
    public int quarterScore;

    public Student (UUID studentId, String name, Bitmap picture){
        this.studentId = studentId;
        this.name = name;
        this.picture = picture;
        this.classScore = 0;
        this.recencyScore = 0;
        this.sizeScore = 0.0;
        this.quarterScore = 0;
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Student other = (Student) o;

        return this.studentId.equals(other.studentId) &&
                this.name.equals(other.name);
     }

    public UUID getId() { return studentId; }

    public String getName() { return name; }

    public Bitmap getPicture() { return picture; }

    @NonNull
    @Override
    public String toString() { return studentId + "," + name; }

    public int getClassScore() { return classScore; }

    public int getRecencyScore() { return recencyScore; }

    public double getSizeScore() { return sizeScore; }

    public int getQuarterScore() { return quarterScore; }

    public void setClassScore(int score) { this.classScore = score; }

    public void setRecencyScore(int score) { this.recencyScore = score; }

    public void setSizeScore(double score) { this.sizeScore = score; }
}
