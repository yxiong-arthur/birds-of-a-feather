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
    @ColumnInfo(name = "id")
    public UUID studentId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "profile_picture")
    public Bitmap picture;

    @Ignore
    public int count;

    public Student (UUID studentId, String name, Bitmap picture){
        this.studentId = studentId;
        this.name = name;
        this.picture = picture;
        this.count = 0;
    }

    public boolean equals(Object o){
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Student other = (Student) o;

        return this.studentId.equals(other.studentId) &&
                this.name.equals(other.name);
     }

    public String getName() {
        return name;
      }

    public UUID getId() {
        return studentId;
    }

    public Bitmap getPicture() { return picture; }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
