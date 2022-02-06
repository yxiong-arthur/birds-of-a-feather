package com.swift.birdsofafeather.model;

import com.swift.birdsofafeather.model.db.Class;

public abstract class IClass {
    private int classId;
    private int studentId;
    private int year;
    private String quarter;
    private String subject;
    private String courseNumber;

    public abstract int getId();
    public abstract int getYear();
    public abstract String getQuarter();
    public abstract String getSubject();
    public abstract String getCourseNumber();

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        IClass other = (IClass) o;

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
