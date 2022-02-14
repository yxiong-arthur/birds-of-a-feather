package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StudentProfileActivity extends AppCompatActivity {
    private AppDatabase db;
    private UUID studentId;
    private StudentWithClasses myself;
    private StudentWithClasses classmate;
    private Set<Class> myClasses;
    private RecyclerView ClassesRecyclerView;
    private RecyclerView.LayoutManager classesLayoutManager;
    private ClassViewAdapter classesViewAdapter;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future future;
    private ImageView portrait;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        portrait = findViewById(R.id.portrait);
        name = findViewById(R.id.name);

        Intent intent = getIntent();
        UUID classmateId = UUID.fromString(intent.getStringExtra("classmate_id"));
        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());

            SharedPreferences preferences = Utils.getSharedPreferences(this);
            String UUIDString = preferences.getString("student_id", "");
            studentId = UUID.fromString(UUIDString);

            myself = db.studentWithClassesDao().getStudent(studentId);
            myClasses = myself.getClasses();



            StudentWithClasses classmate = db.studentWithClassesDao().getStudent(classmateId);
            Set<Class> similarClasses = getSimilarClasses(classmate);

            Student classmateStudent = classmate.getStudent();
            this.portrait.setImageBitmap(classmateStudent.getPicture());
            this.name.setText(classmateStudent.getName());

            List<Class> similarC = new ArrayList<Class>(similarClasses);
            Collections.sort(similarC);

            runOnUiThread(() -> {
                // Set up the recycler view to show our database contents
                ClassesRecyclerView = findViewById(R.id.persons_view);

                classesLayoutManager = new LinearLayoutManager(this);
                ClassesRecyclerView.setLayoutManager(classesLayoutManager);

                classesViewAdapter = new ClassViewAdapter(similarC);
                ClassesRecyclerView.setAdapter(classesViewAdapter);
            });
        });
    }

    private Set<Class> getSimilarClasses(StudentWithClasses classmate){
        Set<Class> mateClasses = classmate.getClasses();

        mateClasses.retainAll(myClasses);

        return mateClasses;
    }

    public void onGoBackHome(View view) {
        finish();
    }
}