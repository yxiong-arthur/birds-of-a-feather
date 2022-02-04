package com.swift.birdsofafeather.model;

public class DummyClass implements IClass{
    private final int year;
    private final String quarter;
    private final String subject;
    private final String courseNumber;

    public DummyClass(int year, String quarter, String subject, String courseNumber){
        this.year = year;
        this.quarter = quarter.toLowerCase();
        this.subject = subject.toLowerCase();
        this.courseNumber = courseNumber.toLowerCase();
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

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DummyClass other = (DummyClass) o;

        return this.year == other.year &&
                this.quarter.equals(other.quarter) &&
                this.subject.equals(other.subject) &&
                this.courseNumber.equals(other.courseNumber);
    }

    @Override
    public int hashCode(){
        String toHash = Integer.toString(year) + quarter + subject + courseNumber;
        return toHash.hashCode();
    }
}
