package com.swift.birdsofafeather.model.db;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class SessionWithStudents {
    @Embedded
    public Session session;

    @Relation(parentColumn="session_id",
                entityColumn="student_id",
                associateBy=@Junction(SessionStudent.class))
    public List<Student> students;

    public Session getSession() {
        return this.session;
    }

    public List<Student> getStudents(){
        return this.students;
    }
}
