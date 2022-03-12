package com.swift.birdsofafeather.model.db;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.swift.birdsofafeather.Utils;

import java.util.UUID;

@Entity(tableName = "classes", foreignKeys = {
            @ForeignKey(onDelete = CASCADE, entity = Student.class,
                    parentColumns = "student_id", childColumns = "student_id")
        },
        indices = {
                @Index("student_id")
        })
public class Class implements Comparable{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "class_id")
    public UUID classId;

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

    @ColumnInfo(name = "course_size")
    public String courseSize;

    public Class(@NonNull UUID classId, UUID studentId, int year, String quarter, String subject, String courseNumber, String courseSize){
        this.classId = classId;
        this.studentId = studentId;
        this.year = year;
        this.quarter = quarter.toLowerCase();
        this.subject = subject.toLowerCase();
        this.courseNumber = courseNumber.toLowerCase();
        this.courseSize = courseSize.toLowerCase();
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

    public String getCourseSize() {
        return this.courseSize;
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
                this.courseNumber.equals(other.courseNumber) &&
                this.courseSize.equals(other.courseSize);
    }

    @Override
    public int hashCode(){
        String toHash = this.year + this.quarter + this.subject + this.courseNumber + this.courseSize;
        return toHash.hashCode();
    }

    @NonNull
    @Override
    public String toString(){
        return this.classId + "," + this.year + "," + this.quarter + "," + this.subject
                + "," + this.courseNumber + "," + this.courseSize;
    }

    public String toDisplayString(){
        return this.year + " " + this.quarter + " " + this.subject + " " + this.courseNumber + " "
                + this.courseSize;
    }

    @Override
    public int compareTo(Object o) {
        Class other = (Class) o;

        return (this.year*4 + Utils.getRecencyScore(this.quarter)) - (other.year*4 + Utils.getRecencyScore(other.quarter));
    }
}
