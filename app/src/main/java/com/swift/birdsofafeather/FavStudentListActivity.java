package com.swift.birdsofafeather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.FavoriteStudent;
import com.swift.birdsofafeather.model.db.FavoriteStudentDao;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentDao;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FavStudentListActivity extends AppCompatActivity {
    private AppDatabase db;
    private List<FavoriteStudent> favoriteStudentsList;
    private List<Student> studentList;
    private UUID sessionId;
    private Future future;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private RecyclerView favoriteStudentRecyclerView;
    private RecyclerView.LayoutManager favoriteStudentLayoutManager;
    private FavoriteStudentViewAdapter favStudentViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_favorite_students);

        Intent intent = getIntent();
        studentList = new ArrayList<Student>();
        sessionId = UUID.fromString(intent.getStringExtra("session_id"));

        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());
            favoriteStudentsList = db.favoriteStudentDao().getStudentsBySession(sessionId);

            for(FavoriteStudent s : favoriteStudentsList) {
                UUID favoriteStudentId = s.getStudentId();
                Student student = db.studentDao().getStudent(favoriteStudentId);
                studentList.add(student);
            }

            runOnUiThread(() -> {
                favoriteStudentRecyclerView = findViewById(R.id.persons_view);

                favoriteStudentLayoutManager = new LinearLayoutManager(this);
                favoriteStudentRecyclerView.setLayoutManager(favoriteStudentLayoutManager);

                favStudentViewAdapter = new FavoriteStudentViewAdapter(studentList);
                favoriteStudentRecyclerView.setAdapter(favStudentViewAdapter);
            });
        });
    }
}
