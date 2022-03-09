package com.swift.birdsofafeather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Student;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AddClassesActivity extends AppCompatActivity {
    private AppDatabase db;
    private UUID studentId;
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future future;

    private Spinner yearSpinner;
    private Spinner quarterSpinner;
    private Spinner courseSizeSpinner;

    private final String[] courseSizes = getResources().getStringArray(R.array.courseSize_array_actual);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);

        this.future = backgroundThreadExecutor.submit(() -> {
            yearSpinner = findViewById(R.id.year_select);
            ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                    R.array.years_array, android.R.layout.simple_spinner_item);
            yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            yearSpinner.setAdapter(yearAdapter);

            quarterSpinner = findViewById(R.id.quarter_select);
            ArrayAdapter<CharSequence> quarterAdapter = ArrayAdapter.createFromResource(this,
                    R.array.quarters_array, android.R.layout.simple_spinner_item);
            quarterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            quarterSpinner.setAdapter(quarterAdapter);

            courseSizeSpinner = findViewById(R.id.courseSize_select);
            ArrayAdapter<CharSequence> courseSizeAdapter = ArrayAdapter.createFromResource(this,
                    R.array.courseSize_array_display, android.R.layout.simple_spinner_item);
            quarterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            courseSizeSpinner.setAdapter(courseSizeAdapter);

            initializeDatabase();
        });
    }

    public void onEnterClicked(View view){
        TextView subjectTextView = findViewById(R.id.subject_textview);
        TextView courseNumberTextView = findViewById(R.id.courseNumber_textview);

        String yearString = yearSpinner.getSelectedItem().toString();
        String quarter = quarterSpinner.getSelectedItem().toString().toLowerCase();
        String subject = subjectTextView.getText().toString().toLowerCase();
        String courseNumber = courseNumberTextView.getText().toString().toLowerCase();
        String courseSize = courseSizes[courseSizeSpinner.getSelectedItemPosition()].toLowerCase();

        if(!validateInput(yearString, quarter, subject, courseNumber, courseSize)) return;

        int year = Integer.parseInt(yearString);

        if(db.classesDao().checkExist(year, quarter, subject, courseNumber, courseSize)) {
            Utils.showAlert(this, "No duplicates allowed");
            return;
        }

        UUID newClassId = UUID.randomUUID();

        Class newClass = new Class(newClassId, studentId, year, quarter, subject, courseNumber, courseSize);
        db.classesDao().insert(newClass);

        Toast.makeText(getApplicationContext(), "Added new class", Toast.LENGTH_SHORT).show();
    }

    public void onDoneClicked(View view){
        this.future.cancel(true);

        Intent searchStudentIntent = new Intent(this, SearchStudentWithSimilarClasses.class);
        startActivity(searchStudentIntent);
    }

    protected void initializeDatabase(){
        db = AppDatabase.singleton(this.getApplicationContext());
        SharedPreferences preferences = Utils.getSharedPreferences(this);

        if(db.studentDao().count() > 0){
            String UUIDString = preferences.getString("student_id", "");
            studentId = UUID.fromString(UUIDString);
            return;
        }

        studentId = UUID.randomUUID();

        String name = preferences.getString("first_name", "");
        String pictureData = preferences.getString("image_data","");

        Bitmap pictureBMap = Utils.stringToBitmap(pictureData);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("student_id", studentId.toString());
        editor.apply();

        Student currentStudent = new Student(studentId, name, pictureBMap);
        db.studentDao().insert(currentStudent);
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

    public void onGoBackHome(View view) {
        this.future.cancel(true);

        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}