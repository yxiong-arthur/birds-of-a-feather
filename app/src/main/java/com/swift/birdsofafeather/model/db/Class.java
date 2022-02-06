package com.swift.birdsofafeather.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.swift.birdsofafeather.model.DummyClass;
import com.swift.birdsofafeather.model.IClass;

@Entity(tableName = "classes")
public class Class extends IClass {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int classId;

    @ColumnInfo(name = "student_id")
    public int studentId;

    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "quarter")
    public String quarter;

    @ColumnInfo(name = "subject")
    public String subject;

    @ColumnInfo(name = "course_number")
    public String courseNumber;

    public Class(int classId, int studentId, int year, String quarter, String subject, String courseNumber){
        this.classId = classId;
        this.studentId = studentId;
        this.year = year;
        this.quarter = quarter.toLowerCase();
        this.subject = subject.toLowerCase();
        this.courseNumber = courseNumber.toLowerCase();
    }

    @Override
    public int getId() {
        return this.classId;
    }

    @Override
    public int getYear() {
        return this.year;
    }

    @Override
    public String getQuarter() {
        return this.quarter;
    }

    @Override
    public String getSubject() {
        return this.subject;
    }

    @Override
    public String getCourseNumber() {
        return this.courseNumber;
    }
}
