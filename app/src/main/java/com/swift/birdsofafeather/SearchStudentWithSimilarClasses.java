package com.swift.birdsofafeather;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private static final String currentYear = "2022";
    private static final String currentQuarter = "wi";

    private MessageListener realListener;
    private Message myStudentData;

    private Spinner filterSpinner;
    private Spinner thisYearSpinner;
    private Spinner thisQuarterSpinner;
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

        thisYearSpinner = findViewById(R.id.year_select);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.years_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        thisYearSpinner.setAdapter(yearAdapter);

        thisQuarterSpinner = findViewById(R.id.quarter_select);
        ArrayAdapter<CharSequence> quarterAdapter = ArrayAdapter.createFromResource(this,
                R.array.quarters_array, android.R.layout.simple_spinner_item);
        quarterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        thisQuarterSpinner.setAdapter(quarterAdapter);

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

    protected List<Student> findPriorClassmates() {
        SessionWithStudents mySession = db.sessionWithStudentsDao().getSession(currentSessionId);
        List<Student> sessionStudents = mySession.getStudents();
        sessionStudents.remove(user.getStudent());

        List<StudentWithClasses> studentList = new ArrayList<>();
        for(Student student : sessionStudents) {
            studentList.add(db.studentWithClassesDao().getStudent(student.getId()));
        }



        List<Student> commonClassmates = new ArrayList<>();

        PriorityQueue<StudentWithClasses> pq;
        String filterString = filterSpinner.getSelectedItem().toString();
        String thisYearString = thisYearSpinner.getSelectedItem().toString();
        String thisQuarterString = thisQuarterSpinner.getSelectedItem().toString().toLowerCase();

        switch (filterString) {
            case "prioritize recent":
                pq = new PriorityQueue<>(1000, new StudentClassRecencyComparator());
                break;
            case "prioritize small classes":
                pq = new PriorityQueue<>(1000, new StudentClassSizeComparator());
                break;
            case "this quarter only":
                pq = new PriorityQueue<>(1000, new StudentThisQuarterComparator());
                break;
            default:
                pq = new PriorityQueue<>(1000, new StudentComparator());
        }

        for (StudentWithClasses classmate : studentList) {
            if (countSimilarClasses(classmate) > 0) {
                pq.add(classmate);
            }
        }
        while (!pq.isEmpty()) {
            commonClassmates.add(Objects.requireNonNull(pq.poll()).getStudent());
        }
        return commonClassmates;
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
        for(int i=0; i<userClassmates.size(); i++) {
            if(userClassmates.get(i).getId().equals(classmate.getId())){
                return i;
            }
        }
        return -1;
    }

    public void onToggleClicked(View view) {
        if(searching)
            this.onStopClicked();
        else
            this.onStartClicked();
        searching = !searching;
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

                String thisYearString = thisYearSpinner.getSelectedItem().toString();
                String thisQuarterString = thisQuarterSpinner.getSelectedItem().toString().toLowerCase();

                userClasses = user.getClasses();
                for (Class course : userClasses) {

                    if (course.getYear() == Integer.parseInt(thisYearString) && course.getQuarter().equals(thisQuarterString)) {
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
        this.realListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
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

                StudentWithClasses studentWithClasses = db.studentWithClassesDao().getStudent(studentUUID);
                setAllScore(studentWithClasses);
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
        Log.d(TAG, "Started Nearby Searching");
    }

    protected void stopNearby(){
        Nearby.getMessagesClient(this).unsubscribe(realListener);
        Nearby.getMessagesClient(this).unpublish(myStudentData);
        Log.d(TAG, "Stopped Nearby Searching");
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
            this.fromStartPage = false;
        }

        refreshRecycler();
    }

    public void onAddStudentsClicked(View view){
        Intent addStudentsIntent = new Intent(this, AddStudentActivity.class);
        startActivity(addStudentsIntent);
    }

    public ArrayList<Class> sortClasses(Set<Class> list, PriorityQueue<Class> pq) {
        ArrayList<Class> classList = new ArrayList<>();
        pq.addAll(list);
        while (!pq.isEmpty()) {
            classList.add(pq.poll());
        }
        return classList;
    }

    class StudentComparator implements Comparator<StudentWithClasses> {
        @Override
        public int compare(StudentWithClasses student1, StudentWithClasses student2) {
            return countSimilarClasses(student1) - countSimilarClasses(student2);
        }
    }


    class StudentClassSizeComparator implements Comparator<StudentWithClasses> {
        public int compare (StudentWithClasses student1, StudentWithClasses student2) {
            if (student1.getStudent().getSizeScore() > student2.getStudent().getSizeScore()) {
                return -1;
            }
            else {
                return 1;
            }
        }
    }


    class StudentClassRecencyComparator implements Comparator<StudentWithClasses> {
        @Override
        public int compare(StudentWithClasses student1, StudentWithClasses student2) {
            if (student1.getStudent().getRecencyScore() > student2.getStudent().getRecencyScore()) {
                return -1;
            }
            else {
                return 1;
            }
//
//            PriorityQueue<Class> pq = new PriorityQueue<>(1000, new ClassRecencyComparator());
//            ArrayList<Class> s1_classes_sorted = sortClasses(getSimilarClasses(student1), pq);
//            ArrayList<Class> s2_classes_sorted = sortClasses(getSimilarClasses(student2), pq);
//
//            int index = 0;
//            int s1_num_classes = s1_classes_sorted.size();
//            int s2_num_classes = s2_classes_sorted.size();
//
//            while (index < s1_num_classes && index < s2_num_classes) {
//                int compareIndicator = (s1_classes_sorted.get(index)).compareTo(s2_classes_sorted.get(index));
//
//                if (compareIndicator > 0) {
//                    return -1;
//                }
//                else if (compareIndicator < 0) {
//                    return 1;
//                }
//                index++;
//            }
//
//            if (s1_num_classes > s2_num_classes) {
//                return -1;
//            }
//            else {
//                return 1;
//            }
        }
    }

    class StudentThisQuarterComparator implements Comparator<StudentWithClasses> {
        @Override
        public int compare(StudentWithClasses student1, StudentWithClasses student2) {
            if (student1.getStudent().getQuarterScore() > student2.getStudent().getQuarterScore()) {
                return -1;
            }
            else {
                return 1;
            }

            /*
            Set<Class> s1_classes = getSimilarClasses(student1);
            Set<Class> s2_classes = getSimilarClasses(student2);

            String thisYearString = thisYearSpinner.getSelectedItem().toString();
            String thisQuarterString = thisQuarterSpinner.getSelectedItem().toString().toLowerCase();

            int s1_count = 0;

            for (Class course : s1_classes) {
                if (course.getYear() == Integer.parseInt(thisYearString) && course.getQuarter().equals(thisQuarterString)) {
                    s1_count++;
                }
            }

            int s2_count = 0;

            for (Class course : s2_classes) {
                if (course.getYear() == Integer.parseInt(thisYearString) && course.getQuarter().equals(thisQuarterString)) {
                    s2_count++;
                }
            }

            if (s1_count > s2_count) {
                return -1;
            }
            else {
                return 1;
            }

             */
        }
    }

    public void onAddClassesClicked(View view){
        Intent addClassesIntent = new Intent(this, AddClassesActivity.class);
        startActivity(addClassesIntent);
    }

    public void setAllScore(StudentWithClasses student) {
        setSizeScore(student);
        setRecencyScore(student);
        setQuarterScore(student);
    }

    public void setSizeScore(StudentWithClasses student) {
        Set<Class> classes = getSimilarClasses(student);
        double score = 0;
        for (Class course : classes) {
            score += Utils.getClassSizeScore(course.getCourseSize());
        }
        student.getStudent().setSizeScore(score);
    }

    public void setRecencyScore(StudentWithClasses student) {
        int thisYear = Integer.parseInt(currentYear);
        int thisQuarterScore = Utils.getRecencyScore(currentQuarter);

        Set<Class> classes = getSimilarClasses(student);
        int totalScore = 0;
        for (Class course : classes) {
            int year = course.getYear();
            int quarter = Utils.getRecencyScore(course.getQuarter());
            int score = (thisYear - year) * 4 + (thisQuarterScore - quarter);
            switch(score) {
                case 0:
                    totalScore += 5;
                    break;
                case 1:
                    totalScore += 4;
                    break;
                case 2:
                    totalScore += 3;
                    break;
                case 3:
                    totalScore += 2;
                    break;
                case 4:
                    totalScore += 1;
                    break;
                default:
                    totalScore += 1;
                    break;
            }

        }
        student.getStudent().setRecencyScore(totalScore);
    }

    public void setQuarterScore(StudentWithClasses student) {
        Set<Class> classes = getSimilarClasses(student);
        int score = 0;
        for (Class course : classes) {
            if (course.getYear() == Integer.parseInt(currentYear) && course.getQuarter().equals(currentQuarter)) {
                score++;
            }
        }
        student.getStudent().setQuarterScore(score);
    }
}