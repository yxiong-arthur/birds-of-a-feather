package com.swift.birdsofafeather.model;

public class DummyClass implements IClass{
    private final int year;
    private final String quarter;
    private final String subject;
    private final String courseNumber;

    public DummyClass(int year, String quarter, String subject, String courseNumber){
        this.year = year;
        this.quarter = quarter;
        this.subject = subject;
        this.courseNumber = courseNumber;
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
