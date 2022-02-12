package com.swift.birdsofafeather;

import android.app.Person;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;
import com.swift.birdsofafeather.model.db.Class;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class NearbyStudentsActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothActivity";
    private MessageListener messageListener;
    private Message myStudentData;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.singleton(this);

        MessageListener realListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                String messageContent = new String(message.getContent());
                String[] decodedMessage = messageContent.split(",");
                String name = decodedMessage[0];
                String pictureURL = decodedMessage[1];
                UUID uuid = UUID.randomUUID();
                List<Class> classes = new ArrayList<Class>();

                //TODO set to default
                Bitmap image = null;


                for(int i = 2; i < decodedMessage.length; i+=5) {
                    UUID classId = UUID.fromString(decodedMessage[i]);
                    int year = Integer.parseInt(decodedMessage[i + 1]);
                    String quarter = decodedMessage[i + 2];
                    String subject = decodedMessage[i + 3];
                    String courseNumber = decodedMessage[i + 4];
                    Class newClass = new Class(classId, uuid, year, quarter, subject, courseNumber);
                    classes.add(newClass);
                }

                try {
                    URL url =  new URL(pictureURL);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    //TODO log
                }

                StudentWithClasses classmate = new StudentWithClasses(uuid, name, image, classes);
                db.studentWithClassesDao().insert(classmate);
            }

            @Override
            public void onLost(@NonNull Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
            }
        };

        SharedPreferences preferences = Utils.getSharedPreferences(NearbyStudentsActivity.this);
        String photoString = preferences.getString("image_data", "default");

        // TODO: delete after testing
        // student object for testing
        String studentUUIDString = preferences.getString("student_id", null);
        UUID studentUUID = studentUUIDString != null ? UUID.fromString(studentUUIDString) : UUID.randomUUID();
        String studentName = "travis";
        List<Class> classes = db.classesDao().getForStudent(studentUUID);
        String encodedString = studentName + "," + photoString;

        // TODO serialize? but how do we combine multiple serialized objects?
        for(Class c : classes) {
            encodedString += "," + c.getId().toString() + "," + c.getYear() + "," + c.getQuarter() + "," +
                    c.getSubject() + "," + c.getCourseNumber();
        }

        myStudentData = new Message(encodedString.getBytes(StandardCharsets.UTF_8));
        this.messageListener = new FakedMessageListener(realListener, 5, encodedString);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Nearby.getMessagesClient(this).subscribe(messageListener);
        Nearby.getMessagesClient(this).publish(myStudentData);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Nearby.getMessagesClient(this).unsubscribe(messageListener);
        Nearby.getMessagesClient(this).unpublish(myStudentData);
    }
}

