package com.swift.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
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
public class AppDatabaseUnitTest {
    private AppDatabase db;
    private StudentDao studentDao;
    private ClassesDao classesDao;
    private StudentWithClassesDao studentWithClassesDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
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
    public void insertStudentCheckExistence() throws Exception {
        UUID currStudentId = UUID.randomUUID();
        Student student = new Student(currStudentId, "Test");

        assertEquals(0, studentDao.count());

        studentDao.insert(student);

        assertEquals(1, studentDao.count());

        List<Student> allStudents = studentDao.getAllStudents();
        Student byId = studentDao.getStudent(currStudentId);

        assertEquals(student, allStudents.get(0));
        assertEquals(student, byId);
    }

    @Test
    public void insertStudentInsertClassCheckExistence() throws Exception {
        UUID currStudentId = UUID.randomUUID();
        UUID currClassId = UUID.randomUUID();
        Student student = new Student(currStudentId, "Test");
        studentDao.insert(student);

        assertEquals(0, classesDao.count());

        Class class1 = new Class(currClassId, currStudentId, 2022, "WI", "CSE", "110");
        classesDao.insert(class1);

        assertEquals(1, classesDao.count());

        List<Class> allClasses = classesDao.getAllClasses();
        Class byId = classesDao.getClass(currClassId);

        assertEquals(class1, allClasses.get(0));
        assertEquals(class1, byId);
    }
}
