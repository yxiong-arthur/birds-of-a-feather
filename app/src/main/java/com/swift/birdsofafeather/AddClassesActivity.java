package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.swift.birdsofafeather.model.DummyClass;

public class AddClassesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);

        Spinner yearSpinner = findViewById(R.id.year_select);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.years_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        Spinner quarterSpinner = findViewById(R.id.quarter_select);
        ArrayAdapter<CharSequence> quarterAdapter = ArrayAdapter.createFromResource(this,
                R.array.quarters_array, android.R.layout.simple_spinner_item);
        quarterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quarterSpinner.setAdapter(quarterAdapter);

    }

    protected void onEnterClicked(View view){
        Spinner yearSpinner = findViewById(R.id.year_select);
        Spinner quarterSpinner = findViewById(R.id.quarter_select);
        TextView subjectTextView = (TextView) findViewById(R.id.subject_textview);
        TextView courseNumberTextView = (TextView) findViewById(R.id.courseNumber_textview);

        int id = 15;
        String yearString = yearSpinner.getSelectedItem().toString();
        int year = Integer.parseInt(yearString);
        String quarter = quarterSpinner.getSelectedItem().toString();
        String subject = subjectTextView.getText().toString();
        String courseNumber = courseNumberTextView.getText().toString();

        DummyClass dummyClass = new DummyClass(id, year, quarter, subject, courseNumber);
    }
}