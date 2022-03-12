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

    private MessageListener messageListener;
    private Message myStudentData;
    private Message wavedToData;

    private List<Student> classmatesWavedToList;

    private Spinner filterSpinner;
    private AppDatabase db;

    private UUID currentSessionId;

    private UUID userId;
    private StudentWithClasses user;
    private Set<Class> userClasses;

    private List<Student> classmates;

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
                refreshRecycler(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

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

        classmatesWavedToList = new ArrayList<>();

        refreshRecycler(true);
        setUpNearby();
    }

    protected void refreshRecycler(boolean clear){
        backgroundThreadExecutor.submit(() -> {
            classmates = new ArrayList<>();

            // if not clear then build list
            if(!clear) {
                rebuildClassmates();
            }

            runOnUiThread(() -> {
                // Set up the recycler view to show our database contents
                studentsRecyclerView = findViewById(R.id.persons_view);

                studentsLayoutManager = new LinearLayoutManager(this);
                studentsRecyclerView.setLayoutManager(studentsLayoutManager);

                studentsViewAdapter = new StudentViewAdapter(classmates);
                studentsRecyclerView.setAdapter(studentsViewAdapter);
            });
        });
    }

    protected void rebuildClassmates(){
        List<Student> sessionStudents = db.sessionWithStudentsDao()
                .getSession(currentSessionId)
                .getStudents();

        sessionStudents.remove(user.getStudent());

        List<Student> favoritedAndWavedFromStudents = db.studentDao().getAllFavoritedAndWavedFromStudents();
        favoritedAndWavedFromStudents.retainAll(sessionStudents);
        classmates.addAll(findPriorClassmates(favoritedAndWavedFromStudents));

        List<Student> favoritedOnlyStudents = db.studentDao().getAllFavoritedOnlyStudents();
        favoritedOnlyStudents.retainAll(sessionStudents);
        classmates.addAll(findPriorClassmates(favoritedOnlyStudents));

        List<Student> wavedFromOnlyStudents = db.studentDao().getAllWavedFromOnlyStudents();
        wavedFromOnlyStudents.retainAll(sessionStudents);
        classmates.addAll(findPriorClassmates(wavedFromOnlyStudents));

        List<Student> regularStudents = db.studentDao().getAllRegularStudents();
        regularStudents.retainAll(sessionStudents);
        classmates.addAll(findPriorClassmates(regularStudents));
    }

    protected List<Student> findPriorClassmates(List<Student> classmatesToOrder) {
        List<StudentWithClasses> studentList = new ArrayList<>();
        for(Student student : classmatesToOrder) {
            studentList.add(db.studentWithClassesDao().getStudent(student.getId()));
        }

        List<Student> commonClassmates = new ArrayList<>();

        PriorityQueue<Student> pq;
        String filterString = filterSpinner.getSelectedItem().toString();

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
            commonClassmates.add(Objects.requireNonNull(pq.poll()));
        }
        return commonClassmates;
    }

    public List<Student> findStudentsThisQuarter(List<StudentWithClasses> studentList) {
        PriorityQueue<Student> pq = new PriorityQueue<>(1000, new StudentThisQuarterComparator());
        List<Student> commonClassmates = new ArrayList<>();
        for(StudentWithClasses classmate : studentList) {
            Student student = classmate.getStudent();
            if (student.getClassScore() > 0 && student.getQuarterScore() > 0) {
                pq.add(student);
            }
        }
        while (!pq.isEmpty()) {
            commonClassmates.add(Objects.requireNonNull(pq.poll()));
        }

        return commonClassmates;
    }

    public void onViewSessionClicked(View view) {
        if (!searching) {
            Intent viewSessionIntent = new Intent(this, StartSearchPage.class);
            viewSessionIntent.putExtra("viewing", true);
            startActivity(viewSessionIntent);
        }
        else {
            Toast.makeText(getApplicationContext(), "Can't view when you are searching", Toast.LENGTH_SHORT).show();
        }
    }

    public void onToggleClicked(View view) {
        if(searching) {
            this.onStopClicked();
            searching = false;
        }
        else{
            this.onStartClicked();
            searching = true;
        }
    }

    protected void startNearby(){
        Nearby.getMessagesClient(this).subscribe(messageListener);
        Nearby.getMessagesClient(this).publish(myStudentData);
        if(wavedToData != null) Nearby.getMessagesClient(this).publish(wavedToData);
        Log.d(TAG, "Started Nearby Subscribing");
        Log.d(TAG, "Started Nearby Publishing");
    }

    protected void stopNearby(){
        Nearby.getMessagesClient(this).unsubscribe(messageListener);
        Nearby.getMessagesClient(this).unpublish(myStudentData);
        if(wavedToData != null) Nearby.getMessagesClient(this).unpublish(wavedToData);
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

        // retrieve session id if existing
        if(preferences.contains("current_session_id")) {
            String sessionUUIDString = preferences.getString("current_session_id", "");
            currentSessionId = UUID.fromString(sessionUUIDString);
        }

        // from start page
        if(this.fromStartPage){
            this.startNearby();
            Toast.makeText(getApplicationContext(), "Starting search...", Toast.LENGTH_SHORT).show();
            this.fromStartPage = false;
        }

        // Publishing the wavedTo Message
        List<Student> allWavedToStudents = db.studentDao().getAllWavedToStudents();

        if(allWavedToStudents.size() != this.classmatesWavedToList.size()) {
            this.classmatesWavedToList = allWavedToStudents;
            if(wavedToData != null) Nearby.getMessagesClient(this).unpublish(this.wavedToData);
            Log.d(TAG, "Unpublished WaveTo Message...");

            updateWavedToData(allWavedToStudents);

            Nearby.getMessagesClient(this).publish(this.wavedToData);
            Log.d(TAG, "Published WaveTo Message...");
        }

        refreshRecycler(false);
    }

    private void updateWavedToData(List<Student> classmatesWavedTo) {
        StringBuilder message = new StringBuilder(this.userId.toString());

        for(Student classmate : classmatesWavedTo) {
            String classmateIdString = classmate.getId().toString();
            message.append(",").append(classmateIdString);
        }

        this.wavedToData = new Message(message.toString().getBytes(StandardCharsets.UTF_8), Utils.WAVE_INFO);
    }

    public void onAddStudentsClicked(View view){
        if (searching) {
            Intent addStudentsIntent = new Intent(this, AddStudentActivity.class);
            startActivity(addStudentsIntent);
        }
        else {
            Toast.makeText(getApplicationContext(), "Can't add if you are not searching", Toast.LENGTH_SHORT).show();
        }
    }

    public void onViewFavClicked(View view){
        Intent viewFavoritesIntent = new Intent(this, FavStudentListActivity.class);
        startActivity(viewFavoritesIntent);
    }

    protected void onStartClicked(){
        Button toggle_button = findViewById(R.id.toggle_search_button);
        toggle_button.setText("Stop Search");
        this.fromStartPage = true;
        Intent startSearchIntent = new Intent(this, StartSearchPage.class);
        startActivity(startSearchIntent);
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
        refreshRecycler(true);
    }

    protected void setUpNearby(){
        SharedPreferences preferences = Utils.getSharedPreferences(this);

        String studentUUIDString = preferences.getString("student_id", "default");
        UUID studentUUID = UUID.fromString(studentUUIDString);
        List<Class> classes = db.classesDao().getForStudent(studentUUID);

        String encodedString = Utils.encodeStudent(this) + "," + Utils.encodeClasses(classes);
        myStudentData = new Message(encodedString.getBytes(StandardCharsets.UTF_8), Utils.STUDENT_INFO);

        this.messageListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                Toast.makeText(getApplicationContext(), "Found Nearby Message...", Toast.LENGTH_SHORT).show();
                String contentType = message.getType();
                String messageContent = new String(message.getContent());

                if(contentType.equals(Utils.STUDENT_INFO)) {
                    handleStudentInfo(messageContent);
                } else {
                    handleWaveInfo(messageContent, studentUUID);
                }
                refreshRecycler(false);
            }

            @Override
            public void onLost(@NonNull Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
            }
        };
    }

    private void handleWaveInfo(String messageContent, UUID thisStudentId) {
        String[] decodedMessage = messageContent.split(",");
        String myId = thisStudentId.toString();
        UUID classmateUUID = UUID.fromString(decodedMessage[0]);

        for(int i = 1; i < decodedMessage.length; i++) {
            if(decodedMessage[i].equals(myId)) {
                if(db.studentDao().checkExists(classmateUUID)) {
                    db.studentDao().updateWavedFrom(classmateUUID, true);
                }
                break;
            }
        }
    }

    private void handleStudentInfo(String messageContent) {
        Log.d(TAG, messageContent);
        String[] decodedMessage = messageContent.split(",");

        UUID studentUUID = UUID.fromString(decodedMessage[0]);
        String name = decodedMessage[1];
        String pictureURL = decodedMessage[2];

        // if student exists in database
        if(db.studentDao().checkExists(studentUUID)){
            if(!db.sessionStudentDao().checkStudentInSession(currentSessionId, studentUUID)) {
                SessionStudent studentInSession = new SessionStudent(currentSessionId, studentUUID);
                db.sessionStudentDao().insert(studentInSession);
            }
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
        // Set all score for this new student
        StudentWithClasses studentWithClasses = db.studentWithClassesDao().getStudent(studentUUID);
        setAllScore(studentWithClasses);
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