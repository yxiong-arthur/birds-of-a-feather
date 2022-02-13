package com.swift.birdsofafeather.model.db;

import android.graphics.Bitmap;
import android.media.Image;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class StudentWithClasses {
    @Embedded
    public Student student;

    @Relation(parentColumn = "id",
                entityColumn = "student_id",
                entity = Class.class)
    public List<Class> classes;

    public Student getStudent() {
        return this.student;
    }

    @Override
    public boolean equals(Object o){ return this.student.equals(o); }

    public UUID getId(){
        return this.student.studentId;
    }

    public String getName() {
        return this.student.name;
    }

    public Bitmap getPicture() {
        return this.student.picture;
    }

    public int getCount() { return this.student.count; }

    public void setCount(int count) { this.student.count = count; }

    public Set<Class> getClasses() {
        return new HashSet<>(this.classes);
    }
}
