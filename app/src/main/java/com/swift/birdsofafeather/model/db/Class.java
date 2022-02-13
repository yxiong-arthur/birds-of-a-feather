package com.swift.birdsofafeather.model.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "classes")
public class Class {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public UUID classId;

    @NonNull
    @ColumnInfo(name = "student_id")
    public UUID studentId;

    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "quarter")
    public String quarter;

    @ColumnInfo(name = "subject")
    public String subject;

    @ColumnInfo(name = "course_number")
    public String courseNumber;

    public Class(UUID classId, UUID studentId, int year, String quarter, String subject, String courseNumber){
        this.classId = classId;
        this.studentId = studentId;
        this.year = year;
        this.quarter = quarter.toLowerCase();
        this.subject = subject.toLowerCase();
        this.courseNumber = courseNumber.toLowerCase();
    }

    public UUID getId() {
        return this.classId;
    }

    public int getYear() {
        return this.year;
    }

    public String getQuarter() {
        return this.quarter;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getCourseNumber() {
        return this.courseNumber;
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Class other = (Class) o;

        return this.year == other.year &&
                this.quarter.equals(other.quarter) &&
                this.subject.equals(other.subject) &&
                this.courseNumber.equals(other.courseNumber);
    }

    @Override
    public int hashCode(){
        String toHash = year + quarter + subject + courseNumber;
        return toHash.hashCode();
    }
}
