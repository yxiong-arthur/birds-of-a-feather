package com.swift.birdsofafeather.model.db;

import android.media.Image;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentWithClasses {
    @Embedded
    public Student student;

    @Relation(parentColumn = "id",
                entityColumn = "student_id",
                entity = Class.class)
    public List<Class> classes;

    public int getId(){
        return this.student.studentId;
    }

    public Student getStudent() {
        return this.student;
    }

    public String getName() {
        return this.student.name;
    }

    public Image getPicture() {
        return null;
    }

    public Set<Class> getClasses() {
        return new HashSet<Class>(this.classes);
    }

    public boolean addClass(Class classToAdd) {
        return false;
    }
}
