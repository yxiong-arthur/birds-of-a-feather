package com.swift.birdsofafeather.model;

import android.media.Image;

import java.util.Set;

public class DummyStudent implements IStudent {
    private final String name;
    private final Image picture;
    private final Set<IClass> classes;

    public DummyStudent(String name, Image picture, Set<IClass> classes){
        this.name = name;
        this.picture = picture;
        this.classes = classes;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Image getPicture() {
        return this.picture;
    }

    @Override
    public Set<IClass> getClasses() {
        return this.classes;
    }
}
