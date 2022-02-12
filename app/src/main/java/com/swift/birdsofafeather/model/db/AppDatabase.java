package com.swift.birdsofafeather.model.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Student.class, Class.class}, version = 2)
@TypeConverters({UUIDConverter.class, BitmapConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase singletonInstance;

    public static AppDatabase singleton(Context context){
        if(singletonInstance == null){
            singletonInstance = Room.databaseBuilder(context, AppDatabase.class, "students.db")
                                .allowMainThreadQueries() // change this from main thread
                                .build();
        }

        return singletonInstance;
    }

    public abstract StudentWithClassesDao studentWithClassesDao();
    public abstract ClassesDao classesDao();
    public abstract StudentDao studentDao();
}
