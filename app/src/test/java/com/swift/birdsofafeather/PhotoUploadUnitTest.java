package com.swift.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.ClassesDao;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentDao;
import com.swift.birdsofafeather.model.db.StudentWithClassesDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class PhotoUploadUnitTest {
    private AppDatabase db;
    private StudentDao studentDao;
    private ClassesDao classesDao;
    private StudentWithClassesDao studentWithClassesDao;
    Context context = ApplicationProvider.getApplicationContext();

    @Before
    public void createDb() {
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).allowMainThreadQueries().build();
        studentDao = db.studentDao();
        classesDao = db.classesDao();
        studentWithClassesDao = db.studentWithClassesDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void photoDatabaseTest() throws Exception {
        UUID currStudentId = UUID.randomUUID();
        Bitmap picture = Utils.urlToBitmap(context,"httpdfsklfjkldsj");
        Student student = new Student(currStudentId, "Test", picture);
        String actual = Utils.bitmapToString(picture);
        String expected = Utils.bitmapToString(student.getPicture());
        assertEquals(expected, actual);
    }

    @Test
    public void photoLinkFailTest() throws Exception {
        UUID currStudentId = UUID.randomUUID();
        Bitmap picture = Utils.urlToBitmap(context,"httpdfsklfjkldsj");
        Student student = new Student(currStudentId, "Test", picture);
        String actual = Utils.bitmapToString(picture);
        Bitmap expectedB = Utils.urlToBitmap(context,"https://m.psecn.photoshelter.com/img-get2/I00006StPFKIH8hs/fit=1000x750/android-logo-200x200.jpg");
        String expected = Utils.bitmapToString(expectedB);
        assertEquals(expected, actual);
    }
}