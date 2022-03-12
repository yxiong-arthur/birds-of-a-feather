package com.swift.birdsofafeather;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.SessionStudent;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AddStudentActivity extends AppCompatActivity {
    private static final String TAG = "AddStudentActivity";
    private static final int currentYear = 2022;
    private static final String currentQuarter = "wi";

    private AppDatabase db;

    private UUID userId;
    private StudentWithClasses user;
    private Set<Class> userClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        db = AppDatabase.singleton(getApplicationContext());

        SharedPreferences preferences = Utils.getSharedPreferences(this);
        String UUIDString = preferences.getString("student_id", "");

        userId = UUID.fromString(UUIDString);
        user = db.studentWithClassesDao().getStudent(userId);
        userClasses = user.getClasses();
    }

    public void onAddStudentClicked(View view) {
        TextView studentInfoTextView = findViewById(R.id.student_info);
        String studentInfo = studentInfoTextView.getText().toString();

        if(Utils.isEmpty(studentInfo)) return;

        studentInfoTextView.setText("");

        List<String> studentRows = Arrays.asList(studentInfo.split("\n"));

        UUID studentId = UUID.randomUUID();
        String studentName = "";

        boolean waveFlag = false;

        StringBuilder encodedString = new StringBuilder();

        for (int i = 0; i < studentRows.size(); i++) {
            List<String> studentRowData = Arrays.asList(studentRows.get(i).split("\\s*,\\s*"));
            if(i == 0){
                studentId = UUID.fromString(studentRowData.get(0));
            }
            if(i == 1){
                studentName = studentRowData.get(0);
            }
            if(i == 2){
                String pictureURL = studentRowData.get(0);
                Bitmap pictureBMap = Utils.urlToBitmap(this, pictureURL);

                Student student = new Student(studentId, studentName, pictureBMap);
                encodedString.append(student).append(",").append(pictureURL);
            }
            if(i >= 3) {
                String yearString = studentRowData.get(0);
                if(yearString.length() > 4){
                    waveFlag = true;
                    break;
                }
                String quarter = studentRowData.get(1).toLowerCase();
                String subject = studentRowData.get(2).toLowerCase();
                String courseNumber = studentRowData.get(3).toLowerCase();
                String courseSize = studentRowData.get(4).toLowerCase();

                if (validateInput(yearString, quarter, subject, courseNumber, courseSize)) {
                    int year = Integer.parseInt(yearString);

                    if (db.classesDao().checkExist(studentId, year, quarter, subject, courseNumber, courseSize)) {
                        Toast.makeText(getApplicationContext(), "Skipped duplicate class(es)", Toast.LENGTH_SHORT).show();
                        continue;
                    }

                    UUID newClassId = UUID.randomUUID();

                    Class newClass = new Class(newClassId, studentId, year, quarter, subject, courseNumber, courseSize);
                    encodedString.append(",").append(newClass);
                }
            }
        }

        boolean finalWaveFlag = waveFlag;
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

                if(finalWaveFlag){
                    db.studentDao().updateWavedFrom(studentUUID, true);
                }

                SessionStudent studentInSession = new SessionStudent(currentSessionId, studentUUID);
                db.sessionStudentDao().insert(studentInSession);

                for(int i = 3; i < decodedMessage.length; i+=6) {
                    UUID classId = UUID.fromString(decodedMessage[i]);
                    int year = Integer.parseInt(decodedMessage[i + 1]);
                    String quarter = decodedMessage[i + 2];
                    String subject = decodedMessage[i + 3];
                    String courseNumber = decodedMessage[i + 4];
                    String courseSize = decodedMessage[i + 5];

                    Class newClass = new Class(classId, studentUUID, year, quarter, subject, courseNumber, courseSize);
                    db.classesDao().insert(newClass);
                }

                StudentWithClasses studentWithClasses = db.studentWithClassesDao().getStudent(studentUUID);
                setAllScore(studentWithClasses);
            }

            @Override
            public void onLost(@NonNull Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
            }
        };

        FakeMessageListener studentBeacon = new FakeMessageListener(messageListener, 3, encodedString.toString());
        Toast.makeText(getApplicationContext(), "Added student with classes", Toast.LENGTH_SHORT).show();
    }

    public void onBackClicked(View view){
        finish();
    }

    protected boolean validateInput(String yearString, String quarter, String subject, String courseNumber, String courseSize){
        try {
            Integer.parseInt(yearString);
        } catch(Exception e) {
            Utils.showAlert(this, "Invalid year");
            return false;
        }

        if(Utils.isEmpty(quarter) || Utils.isEmpty(subject) || Utils.isEmpty(courseNumber) || Utils.isEmpty(courseSize)){
            Utils.showAlert(this, "No empty boxes allowed");
            return false;
        }

        return true;
    }

    public void setAllScore(StudentWithClasses student) {
        setClassScore(student);
        setSizeScore(student);
        setRecencyScore(student);
        setQuarterScore(student);
    }

    public void setClassScore(StudentWithClasses student){
        int classScore = countSimilarClasses(student);
        Log.d(TAG, "Class score is: " + classScore);
        db.studentDao().updateClassScore(student.getStudent().getId(), classScore);
    }

    public void setSizeScore(StudentWithClasses student) {
        Set<Class> classes = getSimilarClasses(student);
        double sizeScore = 0;

        for (Class course : classes) {
            sizeScore += Utils.getClassSizeScore(course.getCourseSize());
        }

        Log.d(TAG, "Size score is: " + sizeScore);
        db.studentDao().updateSizeScore(student.getStudent().getId(), sizeScore);
    }

    public void setRecencyScore(StudentWithClasses student) {
        int thisQuarterScore = Utils.getRecencyScore(currentQuarter);

        Set<Class> classes = getSimilarClasses(student);
        int recencyScore = 0;
        for (Class course : classes) {
            int year = course.getYear();
            int quarter = Utils.getRecencyScore(course.getQuarter());
            int score = (currentYear - year) * 4 + (thisQuarterScore - quarter);
            recencyScore += score > 4 ? 1 : 5 - score;
        }

        Log.d(TAG, "Recency score is: " + recencyScore);
        db.studentDao().updateRecencyScore(student.getStudent().getId(), recencyScore);
    }

    public void setQuarterScore(StudentWithClasses student) {
        Set<Class> classes = getSimilarClasses(student);
        int quarterScore = 0;
        for (Class course : classes) {
            if ((course.getYear() == currentYear) && (course.getQuarter().equals(currentQuarter))) {
                quarterScore++;
            }
        }

        Log.d(TAG, "Quarter score is: " + quarterScore);
        db.studentDao().updateQuarterScore(student.getStudent().getId(), quarterScore);
    }

    protected int countSimilarClasses(StudentWithClasses classmate){
        Set<Class> mateClasses = classmate.getClasses();

        mateClasses.retainAll(userClasses);

        return mateClasses.size();
    }

    protected Set<Class> getSimilarClasses(StudentWithClasses classmate) {
        Set<Class> mateClasses = classmate.getClasses();
        mateClasses.retainAll(userClasses);
        return mateClasses;
    }
}