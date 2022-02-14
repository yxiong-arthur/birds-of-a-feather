package com.swift.birdsofafeather;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SearchStudentWithSimilarClasses extends AppCompatActivity {
    private static final String TAG = "BluetoothActivity";
    private MessageListener realListener;
    private Message myStudentData;

    private AppDatabase db;

    private UUID studentId;
    private StudentWithClasses myself;
    private Set<Class> myClasses;

    private RecyclerView studentsRecyclerView;
    private RecyclerView.LayoutManager studentsLayoutManager;
    private StudentViewAdapter studentsViewAdapter;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future future;

    private boolean searching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_with_similar_classes);

        db = AppDatabase.singleton(getApplicationContext());

        SharedPreferences preferences = Utils.getSharedPreferences(this);
        String UUIDString = preferences.getString("student_id", "");
        studentId = UUID.fromString(UUIDString);

        myself = db.studentWithClassesDao().getStudent(studentId);
        myClasses = myself.getClasses();

        this.future = backgroundThreadExecutor.submit(() -> {
            List<Student> myClassmates = findPriorClassmates();

            for(Student classmate:myClassmates){
                Set<Class> mateClasses = db.studentWithClassesDao().getStudent(classmate.studentId).getClasses();
                mateClasses.retainAll(myClasses);
                int count = mateClasses.size();
                classmate.setCount(count);
            }

            runOnUiThread(() -> {
                // Set up the recycler view to show our database contents
                studentsRecyclerView = findViewById(R.id.persons_view);

                studentsLayoutManager = new LinearLayoutManager(this);
                studentsRecyclerView.setLayoutManager(studentsLayoutManager);

                studentsViewAdapter = new StudentViewAdapter(myClassmates);
                studentsRecyclerView.setAdapter(studentsViewAdapter);
            });
        });

        setUpNearby();
    }

    protected List<Student> findPriorClassmates() {
        List<StudentWithClasses> studentList = db.studentWithClassesDao().getAllStudentsExceptFor(studentId);

        List<Student> commonClassmates = new ArrayList<>();
        List<StudentWithClasses> tempCommon = new ArrayList<>();

        for (StudentWithClasses classmate : studentList) {

            if(countSimilarClasses(classmate) > 0) {
                if(commonClassmates.size() == 0) {
                    commonClassmates.add(classmate.getStudent());
                    tempCommon.add(classmate);
                } else if (countSimilarClasses(classmate) < countSimilarClasses(tempCommon.get(tempCommon.size()-1))) {
                    commonClassmates.add(classmate.getStudent());
                    tempCommon.add(classmate);
                } else{
                    for(int i=0; i<commonClassmates.size(); i++) {
                        if(countSimilarClasses(classmate) >= countSimilarClasses(tempCommon.get(i))) {
                            commonClassmates.add(i,classmate.getStudent());
                            tempCommon.add(i,classmate);
                            break;
                        }
                    }
                }
            }
        }

        return commonClassmates;
    }

    protected int countSimilarClasses(StudentWithClasses classmate){
        Set<Class> mateClasses = classmate.getClasses();

        mateClasses.retainAll(myClasses);

        return mateClasses.size();
    }

    public int calculatePosition (Student classmate) {
        myself = db.studentWithClassesDao().getStudent(studentId);
        myClasses = myself.getClasses();
        List<Student> myClassmates = findPriorClassmates();
        for(int i=0; i<myClassmates.size(); i++) {
            if(myClassmates.get(i).getId().equals(classmate.getId())){
                return i;
            }
        }
        return -1;
    }

    //for milestone2's turn-off button
    public void onToggleClicked(View view) {
        Button toggle_button = findViewById(R.id.toggle_search_button);

        if(searching) {
            this.stopNearby();
            toggle_button.setText("Start Search");
        }
        else {
            this.startNearby();
            toggle_button.setText("Stop Search");
        }

        searching = !searching;
    }

    protected void setUpNearby(){
        this.realListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                String messageContent = new String(message.getContent());
                String[] decodedMessage = messageContent.split(",");

                UUID studentUUID = UUID.fromString(decodedMessage[0]);
                String name = decodedMessage[1];
                String pictureURL = decodedMessage[2];

                if(db.studentDao().checkExists(studentUUID)) return;

                Bitmap image = Utils.urlToBitmap(SearchStudentWithSimilarClasses.this, pictureURL);

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

                int listPosition = calculatePosition(classmate);
                studentsViewAdapter.addStudent(listPosition, classmate);
            }

            @Override
            public void onLost(@NonNull Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
            }
        };

        SharedPreferences preferences = Utils.getSharedPreferences(this);

        String studentUUIDString = preferences.getString("student_id", "default");
        UUID studentUUID = UUID.fromString(studentUUIDString);
        List<Class> classes = db.classesDao().getForStudent(studentUUID);

        String encodedString = Utils.encodeStudent(this) + "," + Utils.encodeClasses(classes);
        myStudentData = new Message(encodedString.getBytes(StandardCharsets.UTF_8));

        startNearby();
    }

    protected void startNearby(){
        Nearby.getMessagesClient(this).subscribe(realListener);
        Nearby.getMessagesClient(this).publish(myStudentData);
    }

    protected void stopNearby(){
        Nearby.getMessagesClient(this).unsubscribe(realListener);
        Nearby.getMessagesClient(this).unpublish(myStudentData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopNearby();
    }

    //public void
    public void onAddStudentsClicked(View view){
        Intent addStudentsIntent = new Intent(this, AddStudentActivity.class);
        startActivity(addStudentsIntent);
    }
}