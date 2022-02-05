package com.swift.birdsofafeather.model;

import android.media.Image;

import java.util.Set;

public interface IStudent {
    String getName();
    Image getPicture();
    Set<IClass> getClasses();
    boolean addClass(IClass classToAdd);
}
