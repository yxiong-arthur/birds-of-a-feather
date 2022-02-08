package com.swift.birdsofafeather.model.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
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

    public Student (UUID studentId, String name){
        this.studentId = studentId;
        this.name = name;
    }

    public boolean equals(Object o){
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Student other = (Student) o;

        return this.studentId == other.studentId &&
                this.name.equals(other.name);
     }

    public String getName() {
        return name;
      }

    public UUID getId() {
        return studentId;
    }
}
