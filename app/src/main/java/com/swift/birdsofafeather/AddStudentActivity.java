package com.swift.birdsofafeather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.ClassesDao;
import com.swift.birdsofafeather.model.db.SessionStudent;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentDao;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AddStudentActivity extends AppCompatActivity {
    private static final String TAG = "AddStudentActivity";
    private AppDatabase db;
    private ClassesDao classesDao;
    private StudentDao studentDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        db = AppDatabase.singleton(getApplicationContext());
        classesDao = db.classesDao();
        studentDao = db.studentDao();
    }

    public void onAddStudentClicked(View view) {
        TextView studentInfoTextView = findViewById(R.id.student_info);
        String studentInfo = studentInfoTextView.getText().toString();

        if(Utils.isEmpty(studentInfo)) return;

        studentInfoTextView.setText("");

        UUID studentId = UUID.randomUUID();
        List<String> studentRows = Arrays.asList(studentInfo.split("\n"));

        String studentName = "";

        String encodedString = "";

        for (int i = 0; i < studentRows.size(); i++) {
            List<String> studentRowData = Arrays.asList(studentRows.get(i).split("\\s*,\\s*"));
            if(i == 0){
                studentName = studentRowData.get(0);
            }
            if(i == 1){
                String pictureURL = studentRowData.get(0);
                ImageView pictureView = findViewById(R.id.imageView);
                Bitmap pictureBMap = Utils.urlToBitmap(this, pictureURL);

                Student student = new Student(studentId, studentName, pictureBMap);
                encodedString += student + "," + pictureURL;
                // studentDao.insert(student);
            }
            if(i >= 2) {
                String yearString = studentRowData.get(0).toString();
                String quarter = studentRowData.get(1).toString().toLowerCase();
                String subject = studentRowData.get(2).toString().toLowerCase();
                String courseNumber = studentRowData.get(3).toString().toLowerCase();

                if (validateInput(yearString, quarter, subject, courseNumber)) {
                    int year = Integer.parseInt(yearString);

                    if (db.classesDao().checkExist(studentId, year, quarter, subject, courseNumber)) {
                        Toast.makeText(getApplicationContext(), "Skipped duplicate class(es)", Toast.LENGTH_SHORT).show();
                        continue;
                    }

                    UUID newClassId = UUID.randomUUID();

                    Class newClass = new Class(newClassId, studentId, year, quarter, subject, courseNumber);
                    encodedString += "," + newClass;
                    //db.classesDao().insert(newClass);
                }
            }
        }

        MessageListener messageListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                String messageContent = new String(message.getContent());
                Log.d(TAG, messageContent);
                String[] decodedMessage = messageContent.split(",");

                UUID studentUUID = UUID.fromString(decodedMessage[0]);
                String name = decodedMessage[1];
                String pictureURL = decodedMessage[2];

                SharedPreferences preferences = Utils.getSharedPreferences(AddStudentActivity.this);
                String sessionUUIDString = preferences.getString("current_session_id", "");
                Log.d(TAG, sessionUUIDString);
                UUID currentSessionId = UUID.fromString(sessionUUIDString);

                Bitmap image = Utils.urlToBitmap(AddStudentActivity.this, pictureURL);

                Student classmate = new Student(studentUUID, name, image);
                db.studentDao().insert(classmate);

                SessionStudent studentInSession = new SessionStudent(currentSessionId, studentUUID);
                db.sessionStudentDao().insert(studentInSession);

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

        FakeMessageListener studentBeacon = new FakeMessageListener(messageListener, 3, encodedString);
        Toast.makeText(getApplicationContext(), "Added student with classes", Toast.LENGTH_SHORT).show();
    }

    public void onBackClicked(View view){
        finish();
    }

    protected boolean validateInput(String yearString, String quarter, String subject, String courseNumber){
        try {
            Integer.parseInt(yearString);
        } catch(Exception e) {
            Utils.showAlert(this, "Invalid year");
            return false;
        }

        if(Utils.isEmpty(quarter) || Utils.isEmpty(subject) || Utils.isEmpty(courseNumber)){
            Utils.showAlert(this, "No empty boxes allowed");
            return false;
        }

        return true;
    }
}