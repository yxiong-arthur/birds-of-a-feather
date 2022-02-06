package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Student;

public class AddClassesActivity extends AppCompatActivity {
    private AppDatabase db;
    private final int studentId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);

        // test works if spinners are removed
        // might need custom spinner item layout https://stackoverflow.com/questions/16694786/how-to-customize-a-spinner-in-android
        Spinner yearSpinner = (Spinner) findViewById(R.id.year_select);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.years_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        Spinner quarterSpinner = (Spinner) findViewById(R.id.quarter_select);
        ArrayAdapter<CharSequence> quarterAdapter = ArrayAdapter.createFromResource(this,
                R.array.quarters_array, android.R.layout.simple_spinner_item);
        quarterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quarterSpinner.setAdapter(quarterAdapter);

        initializeDatabase();
    }

    protected void onEnterClicked(View view){
        Spinner yearSpinner = findViewById(R.id.year_select);
        Spinner quarterSpinner = findViewById(R.id.quarter_select);
        TextView subjectTextView = (TextView) findViewById(R.id.subject_textview);
        TextView courseNumberTextView = (TextView) findViewById(R.id.courseNumber_textview);

        String yearString = yearSpinner.getSelectedItem().toString();
        String quarter = quarterSpinner.getSelectedItem().toString();
        String subject = subjectTextView.getText().toString();
        String courseNumber = courseNumberTextView.getText().toString();

        if(validateInput(yearString, quarter, subject, courseNumber)){
            int year = Integer.parseInt(yearString);

            if(db.classesDao().checkExist(year, quarter, subject, courseNumber) > 0){
                Utils.showAlert(this, "No duplicates allowed");
                return;
            }

            int newClassId = db.classesDao().count() + 1;

            Class newClass = new Class(newClassId, this.studentId, year, quarter, subject, courseNumber);
            db.classesDao().insert(newClass);
        }
    }

    protected void onDoneClicked(View view){
        // go to next activity
        // TODO: dev branch
    }

    protected void initializeDatabase(){
        this.db = AppDatabase.singleton(this.getApplicationContext());

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String name = preferences.getString("first_name", "");

        Student currentStudent = new Student(this.studentId, name);
        db.studentDao().insert(currentStudent);
    }

    protected boolean validateInput(String yearString, String quarter, String subject, String courseNumber){
        int year = 0;
        try {
            year = Integer.parseInt(yearString);
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