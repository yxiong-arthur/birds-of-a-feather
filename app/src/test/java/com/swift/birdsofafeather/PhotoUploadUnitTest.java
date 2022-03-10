package com.swift.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Student;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class PhotoUploadUnitTest {
    private AppDatabase db;
    Context context = ApplicationProvider.getApplicationContext();

    @Before
    public void createDb() {
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).allowMainThreadQueries().build();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void photoDatabaseTest() {
        UUID currStudentId = UUID.randomUUID();
        Bitmap picture = Utils.urlToBitmap(context,"httpdfsklfjkldsj");
        Student student = new Student(currStudentId, "Test", picture);
        assert picture != null;
        String actual = Utils.bitmapToString(picture);
        String expected = Utils.bitmapToString(student.getPicture());
        assertEquals(expected, actual);
    }

    @Test
    public void photoLinkFailTest() {
        UUID currStudentId = UUID.randomUUID();
        Bitmap picture = Utils.urlToBitmap(context,"httpdfsklfjkldsj");
        Student student = new Student(currStudentId, "Test", picture);
        assert picture != null;
        String actual = Utils.bitmapToString(picture);
        Bitmap expectedB = Utils.urlToBitmap(context,"https://m.psecn.photoshelter.com/img-get2/I00006StPFKIH8hs/fit=1000x750/android-logo-200x200.jpg");
        assert expectedB != null;
        String expected = Utils.bitmapToString(expectedB);
        assertEquals(expected, actual);
    }
}