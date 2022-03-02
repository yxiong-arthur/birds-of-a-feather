package com.swift.birdsofafeather.model.db;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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

    @ColumnInfo(name = "favorite")
    public boolean favorited;

    @Ignore
    public int score;

    public Student (UUID studentId, String name, Bitmap picture){
        this.studentId = studentId;
        this.name = name;
        this.picture = picture;
        this.score = 0;
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

    public UUID getId() {
        return studentId;
    }

    public String getName() {
        return name;
      }

    public Bitmap getPicture() { return picture; }

    public boolean isFavorited() { return favorited; }

    @Override
    public String toString() {
        return studentId + "," + name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void toggleFavorited() {
        this.favorited = !this.favorited;
    }
}
