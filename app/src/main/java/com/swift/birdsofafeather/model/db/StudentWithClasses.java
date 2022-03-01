package com.swift.birdsofafeather.model.db;

import android.graphics.Bitmap;
import android.media.Image;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@DatabaseView
public class StudentWithClasses{
    @Embedded
    public Student student;

    @Relation(parentColumn = "student_id",
                entityColumn = "student_id",
                entity = Class.class)
    public List<Class> classes;

    public Student getStudent() {
        return this.student;
    }

    @Override
    public boolean equals(Object o){ return this.student.equals(o); }

    public Set<Class> getClasses() {
        return new HashSet<>(this.classes);
    }

}
