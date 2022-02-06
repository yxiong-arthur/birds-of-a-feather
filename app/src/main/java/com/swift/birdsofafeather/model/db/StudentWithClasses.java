package com.swift.birdsofafeather.model.db;

import android.media.Image;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.swift.birdsofafeather.model.IClass;
import com.swift.birdsofafeather.model.IStudent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentWithClasses implements IStudent {
    @Embedded
    public Student student;

    @Relation(parentColumn = "id",
                entityColumn = "student_id",
                entity = Class.class)
    public List<Class> classes;

    @Override
    public int getId(){
        return this.student.studentId;
    }

    @Override
    public String getName() {
        return this.student.name;
    }

    @Override
    public Image getPicture() {
        return null;
    }

    @Override
    public Set<IClass> getClasses() {
        return new HashSet<IClass>(this.classes);
    }

    @Override
    public boolean addClass(IClass classToAdd) {
        return false;
    }
}
