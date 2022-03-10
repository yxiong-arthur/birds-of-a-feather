package com.swift.birdsofafeather.model.db;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        StudentWithClasses other = (StudentWithClasses) o;

        return this.student.equals(other.student);
    }

    public Set<Class> getClasses() {
        return new HashSet<>(this.classes);
    }

}
