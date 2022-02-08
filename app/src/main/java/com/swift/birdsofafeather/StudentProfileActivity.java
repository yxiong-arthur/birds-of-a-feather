package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

import java.util.Set;
import java.util.UUID;

public class StudentProfileActivity extends AppCompatActivity {
    private AppDatabase db;
    private UUID studentId;
    private StudentWithClasses myself;
    private Set<Class> myClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        Intent intent = getIntent();
        UUID classmateId = UUID.fromString(intent.getStringExtra("classmate_id"));

        db = AppDatabase.singleton(getApplicationContext());

        SharedPreferences preferences = Utils.getSharedPreferences(this);
        String UUIDString = preferences.getString("student_id", "");
        studentId = UUID.fromString(UUIDString);

        myself = db.studentWithClassesDao().getStudent(studentId);
        myClasses = myself.getClasses();

        StudentWithClasses classmate = db.studentWithClassesDao().getStudent(classmateId);
        Set<Class> similarClasses = getSimilarClasses(classmate);
    }

    private Set<Class> getSimilarClasses(StudentWithClasses classmate){
        Set<Class> mateClasses = classmate.getClasses();

        mateClasses.retainAll(myClasses);

        return mateClasses;
    }

    public void onGoBackHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}