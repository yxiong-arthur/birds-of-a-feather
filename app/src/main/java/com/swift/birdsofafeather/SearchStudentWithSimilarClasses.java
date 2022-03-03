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
import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Session;
import com.swift.birdsofafeather.model.db.SessionStudent;
import com.swift.birdsofafeather.model.db.SessionStudentDao;
import com.swift.birdsofafeather.model.db.SessionWithStudents;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

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

    private UUID currentSessionId;

    private UUID userId;
    private StudentWithClasses user;
    private Set<Class> userClasses;

    private RecyclerView studentsRecyclerView;
    private RecyclerView.LayoutManager studentsLayoutManager;
    private StudentViewAdapter studentsViewAdapter;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    private boolean searching = false;
    private boolean fromStartPage = false;
    private boolean stopSearch = false;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    EditText popup_year, popup_quarter, popup_subject, popup_number;
    Button save_popup, cancel_popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_with_similar_classes);

        db = AppDatabase.singleton(getApplicationContext());

        SharedPreferences preferences = Utils.getSharedPreferences(this);

        // get session and user id from preferences
        String sessionUUIDString = preferences.getString("current_session_id", "");
        currentSessionId = UUID.fromString(sessionUUIDString);

        String UUIDString = preferences.getString("student_id", "");
        userId = UUID.fromString(UUIDString);

        user = db.studentWithClassesDao().getStudent(userId);
        userClasses = user.getClasses();

        refreshRecycler();
        setUpNearby();
    }

    protected void refreshRecycler(){
        backgroundThreadExecutor.submit(() -> {
            List<Student> userClassmates = findPriorClassmates();

            runOnUiThread(() -> {
                // Set up the recycler view to show our database contents
                studentsRecyclerView = findViewById(R.id.persons_view);

                studentsLayoutManager = new LinearLayoutManager(this);
                studentsRecyclerView.setLayoutManager(studentsLayoutManager);

                studentsViewAdapter = new StudentViewAdapter(userClassmates);
                studentsRecyclerView.setAdapter(studentsViewAdapter);
            });
        });
    }

    protected List<Student> findPriorClassmates() {
        SessionWithStudents mySession = db.sessionWithStudentsDao().getSession(currentSessionId);
        List<Student> sessionStudents = mySession.getStudents();
        List<StudentWithClasses> studentList = new ArrayList<StudentWithClasses>();
        for(Student student : sessionStudents) {
            studentList.add(db.studentWithClassesDao().getStudent(student.getId()));
        }

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

        mateClasses.retainAll(userClasses);

        return mateClasses.size();
    }

    public int calculatePosition (Student classmate) {
        user = db.studentWithClassesDao().getStudent(userId);
        userClasses = user.getClasses();
        List<Student> userClassmates = findPriorClassmates();
        for(int i=0; i<userClassmates.size(); i++) {
            if(userClassmates.get(i).getId().equals(classmate.getId())){
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
        this.fromStartPage = true;
        Intent intent3 = new Intent(this, CourseDashboard.class);
        startActivity(intent3);
        // put start page code here
    }

    protected void onStopClicked(){
        Button toggle_button = findViewById(R.id.toggle_search_button);
        toggle_button.setText("Start Search");
        this.stopSearch = true;

        // put stop page code here

        dialogBuilder = new AlertDialog.Builder(this);
        final View saveNewClassView = getLayoutInflater().inflate(R.layout.popup_save_class, null);


        // set title
        dialogBuilder.setTitle("Save your class");
        dialogBuilder.setView(saveNewClassView);

        // set dialog message
        dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        dialog = dialogBuilder.create();

        // show it
        dialog.show();

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

                SessionStudent studentInSession = new SessionStudent(currentSessionId, studentUUID);
                db.sessionStudentDao().insert(studentInSession);

                // if student exists in database
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

        SharedPreferences preferences = Utils.getSharedPreferences(this);
        String sessionUUIDString = preferences.getString("current_session_id", "");
        currentSessionId = UUID.fromString(sessionUUIDString);

        if(this.fromStartPage){
            this.startNearby();
            this.fromStartPage = false;
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


    public void saveStudent(List<Student> s, String sessionName){
        UUID sessionId = UUID.randomUUID();
        Session newS = new Session(sessionId);
        newS.setName(sessionName);
        db.sessionDao().insert(newS);
        for(Student eachStudent : s){
            UUID studentId = eachStudent.getId();
            db.sessionStudentDao().insert(new SessionStudent(studentId, sessionId));
        }
    }
}