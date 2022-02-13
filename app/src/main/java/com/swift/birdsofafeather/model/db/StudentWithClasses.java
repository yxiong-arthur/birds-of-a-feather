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

    public StudentWithClasses() {};

    public UUID getId(){
        return this.student.studentId;
    }

    public Student getStudent() {
        return this.student;
    }

    public String getName() {
        return this.student.name;
    }

    public Bitmap getPicture() {
        return this.student.picture;
    }

    public Set<Class> getClasses() {
        return new HashSet<>(this.classes);
    }
}
