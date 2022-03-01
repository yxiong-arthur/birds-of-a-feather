package com.swift.birdsofafeather.model.db;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class SessionWithStudentWithClasses {
    @Embedded
    public Session session;

    @Relation(parentColumn="session_id",
                entityColumn="session_id",
                associateBy=@Junction(SessionStudent.class))
    public List<StudentWithClasses> students;

    public Session getSession() {
        return this.session;
    }

    public List<StudentWithClasses> getStudents(){
        return this.students;
    }
}
