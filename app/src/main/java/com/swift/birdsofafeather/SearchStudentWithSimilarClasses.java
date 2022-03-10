package com.swift.birdsofafeather;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.SessionStudent;
import com.swift.birdsofafeather.model.db.SessionWithStudents;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchStudentWithSimilarClasses extends AppCompatActivity {
    private static final String TAG = "BluetoothActivity";
    private static final int currentYear = 2022;
    private static final String currentQuarter = "wi";

    private MessageListener studentInfoListener;
    private Message myStudentData;

    private Spinner filterSpinner;
//    private Spinner thisYearSpinner;
//    private Spinner thisQuarterSpinner;
    private AppDatabase db;

    private UUID currentSessionId;

    private UUID userId;
    private StudentWithClasses user;
    private Set<Class> userClasses;

    private RecyclerView studentsRecyclerView;
    private RecyclerView.LayoutManager studentsLayoutManager;
    private StudentViewAdapter studentsViewAdapter;
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    private boolean searching = false;
    private boolean fromStartPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_with_similar_classes);

        filterSpinner = findViewById(R.id.filter_select);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.filters_array, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                refreshRecycler();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

//        thisYearSpinner = findViewById(R.id.year_select);
//        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
//                R.array.years_array, android.R.layout.simple_spinner_item);
//        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        thisYearSpinner.setAdapter(yearAdapter);
//
//        thisQuarterSpinner = findViewById(R.id.quarter_select);
//        ArrayAdapter<CharSequence> quarterAdapter = ArrayAdapter.createFromResource(this,
//                R.array.quarters_array, android.R.layout.simple_spinner_item);
//        quarterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        thisQuarterSpinner.setAdapter(quarterAdapter);

        db = AppDatabase.singleton(getApplicationContext());

        SharedPreferences preferences = Utils.getSharedPreferences(this);

        // get session and user id from preferences
        if(preferences.contains("current_session_id")) {
            String sessionUUIDString = preferences.getString("current_session_id", "");
            currentSessionId = UUID.fromString(sessionUUIDString);
        }

        String UUIDString = preferences.getString("student_id", "");

        userId = UUID.fromString(UUIDString);
        user = db.studentWithClassesDao().getStudent(userId);
        userClasses = user.getClasses();

        clearRecycler();
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

    protected void clearRecycler(){
        backgroundThreadExecutor.submit(() -> runOnUiThread(() -> {
            // Set up the recycler view to show our database contents
            studentsRecyclerView = findViewById(R.id.persons_view);

            studentsLayoutManager = new LinearLayoutManager(this);
            studentsRecyclerView.setLayoutManager(studentsLayoutManager);

            studentsViewAdapter = new StudentViewAdapter(new ArrayList<>());
            studentsRecyclerView.setAdapter(studentsViewAdapter);
        }));
    }

    public void onToggleClicked(View view) {
        if(searching)
            this.onStopClicked();
        else
            this.onStartClicked();
        searching = !searching;
    }

    protected void startNearby(){
        Nearby.getMessagesClient(this).subscribe(studentInfoListener);
        Nearby.getMessagesClient(this).publish(myStudentData);
        Log.d(TAG, "Started Nearby Subscribing");
        Log.d(TAG, "Started Nearby Publishing");
    }

    protected void stopNearby(){
        Nearby.getMessagesClient(this).unsubscribe(studentInfoListener);
        Nearby.getMessagesClient(this).unpublish(myStudentData);
        Log.d(TAG, "Stopped Nearby Subscribing");
        Log.d(TAG, "Stopped Nearby Publishing");
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
        if(preferences.contains("current_session_id")) {
            String sessionUUIDString = preferences.getString("current_session_id", "");
            currentSessionId = UUID.fromString(sessionUUIDString);
        }

        if(this.fromStartPage){
            this.startNearby();
            Toast.makeText(getApplicationContext(), "Starting search...", Toast.LENGTH_SHORT).show();
            this.fromStartPage = false;
        }

        refreshRecycler();
    }

    public void onAddStudentsClicked(View view){
        Intent addStudentsIntent = new Intent(this, AddStudentActivity.class);
        startActivity(addStudentsIntent);
    }

    protected void onStartClicked(){
        Button toggle_button = findViewById(R.id.toggle_search_button);
        toggle_button.setText("Stop Search");
        this.fromStartPage = true;
        Intent intent3 = new Intent(this, StartSearchPage.class);
        startActivity(intent3);
    }

    protected void onStopClicked(){
        Button toggle_button = findViewById(R.id.toggle_search_button);
        toggle_button.setText("Start Search");
        this.stopNearby();
        Toast.makeText(getApplicationContext(), "Stopping search...", Toast.LENGTH_SHORT).show();

        if (!(db.sessionDao().checkNamed(currentSessionId))) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set title
            alertDialogBuilder.setTitle("Save your class");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Name it as a class you take this quarter or give it a new name")
                    .setCancelable(false)
                    .setPositiveButton("Choose a class", null)
                    .setNegativeButton("Give it a name", (dialog, id) -> {
                        AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(SearchStudentWithSimilarClasses.this);

                        LinearLayout layout = new LinearLayout(SearchStudentWithSimilarClasses.this);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        final EditText editTextCourse = new EditText(SearchStudentWithSimilarClasses.this);
                        editTextCourse.setHint("Subject + Course Number");
                        layout.addView(editTextCourse);


                        // set title
                        alertDialogBuilder1.setTitle("Save your class");
                        alertDialogBuilder1.setView(layout);

                        // set dialog message
                        alertDialogBuilder1
                                .setCancelable(false)
                                .setPositiveButton("Save", null);

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder1.create();

                        // show it
                        alertDialog.show();

                        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(view -> {

                            String className = editTextCourse.getText().toString().toLowerCase();
                            if (className.length() > 0) {
                                db.sessionDao().updateName(currentSessionId, className);
                                Log.d(TAG, "Named session to " + db.sessionDao().getName(currentSessionId));
                                Toast.makeText(SearchStudentWithSimilarClasses.this, "save as new session", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Please enter subject and course number!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {


                AlertDialog.Builder alertDialogBuilder12 = new AlertDialog.Builder(SearchStudentWithSimilarClasses.this);

                final Spinner classSpinner = new Spinner(SearchStudentWithSimilarClasses.this);

                List<String> spinnerArray = new ArrayList<>();

                userClasses = user.getClasses();
                for (Class course : userClasses) {

                    if (course.getYear() == currentYear && course.getQuarter().equals(currentQuarter)) {
                        String courseString = course.getSubject() + " " + course.getCourseNumber();
                        spinnerArray.add(courseString);
                    }
                }

                if (!spinnerArray.isEmpty()) {
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                            (SearchStudentWithSimilarClasses.this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                            .simple_spinner_dropdown_item);
                    classSpinner.setAdapter(spinnerArrayAdapter);

                    // set title
                    alertDialogBuilder12.setTitle("Choose a class");
                    alertDialogBuilder12.setView(classSpinner);

                    alertDialogBuilder12
                            .setCancelable(false)
                            .setPositiveButton("Enter", (dialogInterface, i) -> {
                                String className = classSpinner.getSelectedItem().toString().toLowerCase();
                                db.sessionDao().updateName(currentSessionId, className);
                                Log.d(TAG, "Named session to " + db.sessionDao().getName(currentSessionId));
                                Toast.makeText(SearchStudentWithSimilarClasses.this, "save as a this quarter's session", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            });

                    // create alert dialog
                    AlertDialog alertDialog1 = alertDialogBuilder12.create();

                    // show it
                    alertDialog1.show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "You don't have class in this quarter!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(SearchStudentWithSimilarClasses.this, "save to existing session", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences preferences = Utils.getSharedPreferences(this);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove("current_session_id");
        edit.apply();
        clearRecycler();
    }

    protected void setUpNearby(){
        this.studentInfoListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                Toast.makeText(getApplicationContext(), "Found Nearby Message...", Toast.LENGTH_SHORT).show();
                String messageContent = new String(message.getContent());
                Log.d(TAG, messageContent);
                String[] decodedMessage = messageContent.split(",");

                UUID studentUUID = UUID.fromString(decodedMessage[0]);
                String name = decodedMessage[1];
                String pictureURL = decodedMessage[2];

                // if student exists in database
                if(db.studentDao().checkExists(studentUUID)){
                    SessionStudent studentInSession = new SessionStudent(currentSessionId, studentUUID);
                    db.sessionStudentDao().insert(studentInSession);
                    return;
                }

                Bitmap image = Utils.urlToBitmap(SearchStudentWithSimilarClasses.this, pictureURL);

                Student classmate = new Student(studentUUID, name, image);
                db.studentDao().insert(classmate);

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

                int listPosition = calculatePosition(classmate);

                // Set all score for this new student
                StudentWithClasses studentWithClasses = db.studentWithClassesDao().getStudent(studentUUID);
                setAllScore(studentWithClasses);

                studentsViewAdapter.addStudent(listPosition, studentWithClasses.getStudent());
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

    protected List<Student> findPriorClassmates() {
        SessionWithStudents mySession = db.sessionWithStudentsDao().getSession(currentSessionId);
        List<Student> sessionStudents = mySession.getStudents();
        sessionStudents.remove(user.getStudent());


        List<StudentWithClasses> studentList = new ArrayList<>();
        for(Student student : sessionStudents) {
            studentList.add(db.studentWithClassesDao().getStudent(student.getId()));
        }

        List<Student> commonClassmates = new ArrayList<>();

        PriorityQueue<Student> pq;
        String filterString = filterSpinner.getSelectedItem().toString();
//        String thisYearString = thisYearSpinner.getSelectedItem().toString();
//        String thisQuarterString = thisQuarterSpinner.getSelectedItem().toString().toLowerCase();

        switch (filterString) {
            case "prioritize recent":
                pq = new PriorityQueue<>(1000, new StudentClassRecencyComparator());
                break;
            case "prioritize small classes":
                pq = new PriorityQueue<>(1000, new StudentClassSizeComparator());
                break;
            case "this quarter only":
                return findStudentsThisQuarter(studentList);
            default:
                pq = new PriorityQueue<>(1000, new StudentClassComparator());
        }

        for (StudentWithClasses classmate : studentList) {
            if (classmate.getStudent().getClassScore() > 0) {
                pq.add(classmate.getStudent());
            }
        }
        while (!pq.isEmpty()) {
            Student student = Objects.requireNonNull(pq.poll());
            commonClassmates.add(student);
        }
        return commonClassmates;
    }

    public List<Student> findStudentsThisQuarter(List<StudentWithClasses> studentList) {
        PriorityQueue<Student> pq = new PriorityQueue<>(1000, new StudentThisQuarterComparator());
        List<Student> commonClassmates = new ArrayList<>();
        for(StudentWithClasses classmate : studentList) {
            Student student = classmate.getStudent();
            if (student.getClassScore() > 0 && student.getQuarterScore() > 1) {
                pq.add(student);
            }
        }
        while (!pq.isEmpty()) {
            commonClassmates.add(Objects.requireNonNull(pq.poll()));
        }

        return commonClassmates;
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

    public int calculatePosition (Student classmate) {
        user = db.studentWithClassesDao().getStudent(userId);
        userClasses = user.getClasses();

        List<Student> userClassmates = findPriorClassmates();
        for(int i = 0; i < userClassmates.size(); i++) {
            if(userClassmates.get(i).getId().equals(classmate.getId())){
                return i;
            }
        }
        return -1;
    }
}

class StudentClassComparator implements Comparator<Student> {
    @Override
    public int compare(Student student1, Student student2) {
        return - (student1.getClassScore() - student2.getClassScore());
    }
}


class StudentClassSizeComparator implements Comparator<Student> {
    @Override
    public int compare(Student student1, Student student2) {
        if (student1.getSizeScore() > student2.getSizeScore()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}


class StudentClassRecencyComparator implements Comparator<Student> {
    @Override
    public int compare(Student student1, Student student2) {
        return - (student1.getRecencyScore() - student2.getRecencyScore());
    }
}

class StudentThisQuarterComparator implements Comparator<Student> {
    @Override
    public int compare(Student student1, Student student2) {
        return - (student1.getQuarterScore() - student2.getQuarterScore());
    }
}