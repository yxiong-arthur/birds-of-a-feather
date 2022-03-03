package com.swift.birdsofafeather;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Session;
import com.swift.birdsofafeather.model.db.SessionStudent;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;
import com.swift.birdsofafeather.model.db.UUIDConverter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private boolean searching = false;
    private boolean startSearch = false;
    private boolean stopSearch = false;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    EditText popup_class;

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

        refreshRecycler();
        setUpNearby();
    }

    protected void refreshRecycler(){
        backgroundThreadExecutor.submit(() -> {
            //List<Student> myClassmates = findPriorClassmates();


            //new
            Intent intent = getIntent();
            UUID sessionId = UUIDConverter.uuidFromString(intent.getStringExtra("Session_id"));
            List<SessionStudent> mySession = db.sessionStudentDao().getStudentsBySession(sessionId);
            List<Student> inputStudent = new ArrayList<>();
            for(int i=0; i<mySession.size(); i++){
                Student temp = db.studentDao().getStudent(mySession.get(i).getStudentId());
                inputStudent.add(temp);
            }


            runOnUiThread(() -> {
                // Set up the recycler view to show our database contents
                studentsRecyclerView = findViewById(R.id.persons_view);

                studentsLayoutManager = new LinearLayoutManager(this);
                studentsRecyclerView.setLayoutManager(studentsLayoutManager);

                studentsViewAdapter = new StudentViewAdapter(inputStudent);
                studentsRecyclerView.setAdapter(studentsViewAdapter);
            });
        });
    }

    protected List<Student> findPriorClassmates() {
        List<StudentWithClasses> studentList = db.studentWithClassesDao().getAllStudentsExceptFor(studentId);

        List<Student> commonClassmates = new ArrayList<>();

        PriorityQueue<Student> pq = new PriorityQueue<>(100, new StudentComparator());

        for (StudentWithClasses classmate : studentList) {
            int count = countSimilarClasses(classmate);

            Student student = classmate.getStudent();
            student.setScore(count);

            if (count > 0) {
                pq.add(student);
            }
        }
        while (!pq.isEmpty()) {
            commonClassmates.add(pq.poll());
        }
        return commonClassmates;
    }

    class StudentComparator implements Comparator<Student> {
        public int compare(Student s1, Student s2) {
            if (s1.getScore() > s2.getScore()) {
                return -1;
            }
            else {
                return 1;
            }
        }
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
        if(searching) {
            this.onStopClicked();
        }
        else {
            this.onStartClicked();
        }
        searching = !searching;
    }

    protected void onStartClicked(){
        Button toggle_button = findViewById(R.id.toggle_search_button);
        toggle_button.setText("Stop Search");
        this.startSearch = true;
        Intent intent3 = new Intent(this, CourseDashboard.class);
        startActivity(intent3);
        // put start page code here
    }

    protected void onStopClicked(){
        Button toggle_button = findViewById(R.id.toggle_search_button);
        toggle_button.setText("Start Search");
        this.stopSearch = true;

        // put stop page code here
        popup_class = (EditText)findViewById(R.id.save_class);



        dialogBuilder = new AlertDialog.Builder(
                this);

        dialogBuilder = new AlertDialog.Builder(this);
        final View saveNewClassView = getLayoutInflater().inflate(R.layout.popup_save_class, null);


        dialogBuilder.setView(saveNewClassView);
        // set title
        dialogBuilder.setTitle("Save this class");


        // set dialog message
        dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        /*
                        String className = popup_class.getText().toString();
                        UUID sessionId = UUID.randomUUID();
                        Session newS = new Session(sessionId);
                        newS.setName(className);
                        db.sessionDao().insert(newS);


                        List<Student> myClassmates = findPriorClassmates();
                        for (Student student : myClassmates) {
                            db.sessionStudentDao().insert(new SessionStudent(sessionId, student.getId()));
                        }

                         */


                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                })
                .setView(saveNewClassView);


        // create alert dialog
        AlertDialog alertDialog = dialogBuilder.create();

        // show it
        alertDialog.show();

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

    @Override
    protected void onResume() {
        super.onResume();
        if(this.startSearch){
            this.startNearby();
            this.startSearch = false;
        }
        if(this.stopSearch){
            this.stopNearby();
            this.stopSearch = false;
        }
        refreshRecycler();
    }

    //public void
    public void onAddStudentsClicked(View view){
        Intent addStudentsIntent = new Intent(this, AddStudentActivity.class);
        startActivity(addStudentsIntent);
    }

    public void onRefresh(View view){
        refreshRecycler();
    }
}