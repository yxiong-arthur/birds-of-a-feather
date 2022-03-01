package com.swift.birdsofafeather.model.db;

import androidx.room.TypeConverter;

import java.util.Date;

// adapted from stack overflow answer:
// https://stackoverflow.com/questions/50313525/room-using-date-field
public class DateConverter {
    @TypeConverter
    public static Date toDate(Long dateLong){
        return dateLong == null ? null: new Date(dateLong);
    }

    @TypeConverter
    public static Long fromDate(Date date){
        return date == null ? null : date.getTime();
    }
}