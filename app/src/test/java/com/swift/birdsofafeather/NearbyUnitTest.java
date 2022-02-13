package com.swift.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.RemoteInput;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Student;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class NearbyUnitTest {
    private AppDatabase db;
    private MessageListener realListener;
    private FakeMessageListener fakeListener;
    private String testMessage;
    private Message myStudentData;
    Context context;

    @Before
    public void init() {
        context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        realListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                String messageContent = new String(message.getContent());
                String[] decodedMessage = messageContent.split(",");

                UUID studentUUID = UUID.fromString(decodedMessage[0]);
                String name = decodedMessage[1];
                String pictureURL = decodedMessage[2];
                Bitmap image = Utils.urlToBitmap(context, pictureURL);


                Student classmate = new Student(studentUUID, name, image);
                db.studentDao().insert(classmate);

                for(int i = 3; i < decodedMessage.length; i+=5) {
                    UUID classId = UUID.fromString(decodedMessage[i]);
                    int year = Integer.parseInt(decodedMessage[i + 1]);
                    String quarter = decodedMessage[i + 2];
                    String subject = decodedMessage[i + 3];
                    String courseNumber = decodedMessage[i + 4];

                    Class newClass = new Class(classId, studentUUID, year, quarter, subject, courseNumber);
                    db.classesDao().insert(newClass);
                }
            }

            @Override
            public void onLost(@NonNull Message message) {
                //Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
            }
        };

        testMessage = getTestMessage(context);
        fakeListener = new FakeMessageListener(realListener, 3, testMessage);
        Message myStudentData = new Message(testMessage.getBytes(StandardCharsets.UTF_8));
        Nearby.getMessagesClient(context).subscribe(fakeListener);
        Nearby.getMessagesClient(context).publish(myStudentData);

    }

    @Test
    public void test_entries_database() {
        try {
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            System.out.println("Exception");
        }

        List<Student> students = db.studentDao().getAllStudents();
        List<Class> classes = db.classesDao().getAllClasses();

        Student first = students.get(0);
        Class c1 = classes.get(0).getQuarter().equals("winter") ? classes.get(0) : classes.get(1);
        Class c2 = classes.get(0).getQuarter().equals("winter") ? classes.get(1) : classes.get(0);

        assertEquals(1, students.size());
        assertEquals("Travis", first.getName());
        assertEquals("winter", c1.getQuarter());
        assertEquals("130", c1.getCourseNumber());
        assertEquals(2, classes.size());


    }

    private String getTestMessage(Context context) {
        UUID randomUUID = UUID.randomUUID();
        String testName = "Travis";
        String pictureURL = "https://riverlegacy.org/wp-content/uploads/2021/07/blank-profile-photo.jpeg";
        Bitmap testImage = Utils.urlToBitmap(context, pictureURL);

        Student testStudent = new Student(randomUUID, testName, testImage);

        UUID id1 = UUID.randomUUID();
        String quarter = "fall";
        int year = 2007;
        String courseNumber = "110";
        String subject = "cse";
        Class class1 = new Class(id1, randomUUID, year, quarter, subject, courseNumber);

        UUID id2 = UUID.randomUUID();
        String quarter2 = "winter";
        int year2 = 2007;
        String courseNumber2 = "130";
        String subject2 = "cse";
        Class class2 = new Class(id2, randomUUID, year2, quarter2, subject2, courseNumber2);

        String encodeMessage = randomUUID.toString() + "," + testName + "," + pictureURL
                + testStudent.toString() + "," + class1.toString() + "," + class2.toString();
        return encodeMessage;
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }
}
