package com.swift.birdsofafeather.model.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

// adapted from stack overflow answer:
// https://stackoverflow.com/questions/59572749/using-uuid-for-primary-key-using-room-with-android
public class BitmapConverter {
    @TypeConverter
    public static String fromBitmap(Bitmap bmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    @TypeConverter
    public static Bitmap bitmapFromString(String bmap_string){
        byte[] b = Base64.decode(bmap_string, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }
}
