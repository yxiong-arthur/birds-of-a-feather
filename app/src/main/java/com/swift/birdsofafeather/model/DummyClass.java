package com.swift.birdsofafeather.model;

public class DummyClass extends IClass{
    private final int classId;
    private final int studentId;
    private final int year;
    private final String quarter;
    private final String subject;
    private final String courseNumber;

    public DummyClass(int classId, int studentId, int year, String quarter, String subject, String courseNumber){
        this.classId = classId;
        this.studentId = studentId;
        this.year = year;
        this.quarter = quarter.toLowerCase();
        this.subject = subject.toLowerCase();
        this.courseNumber = courseNumber.toLowerCase();
    }

    @Override
    public int getId() { return this.classId; }

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