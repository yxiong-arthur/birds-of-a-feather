package com.swift.birdsofafeather.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "classes")
public class Class {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int classID;
}
