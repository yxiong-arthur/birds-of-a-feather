package com.swift.birdsofafeather.model.db;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.UUID;

@Entity(tableName = "students")
public class Student {
    @PrimaryKey
    @NonNull
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

    @ColumnInfo(name = "favorite")
    public boolean favorited;

    @ColumnInfo(name = "waved_at")
    public boolean wavedAt;

    @ColumnInfo(name = "waved_back")
    public boolean wavedBack;

    @Ignore
    public List<Student> wavedBackList;

    public Student (UUID studentId, String name, Bitmap picture){
        this.studentId = studentId;
        this.name = name;
        this.picture = picture;
        this.classScore = 0;
        this.recencyScore = 0;
        this.sizeScore = 0.0;
        this.quarterScore = 0;
        this.favorited = false;
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
    public boolean isFavorited() { return favorited; }

    @Override
    public String toString() { return studentId + "," + name; }

    public int getClassScore() { return classScore; }

    public int getRecencyScore() { return recencyScore; }

    public double getSizeScore() { return sizeScore; }

    public int getQuarterScore() { return quarterScore; }

    public void toggleFavorited() {
        this.favorited = !this.favorited;
    }

    public void waveTo(Student student) { student.wavedAt = true; }

    public void waveBack(Student student) {
        if (!wavedBackList.contains(student) && student.wavedAt == true) {
            wavedBackList.add(student);
            student.wavedBack = true;
        }
    }
}
