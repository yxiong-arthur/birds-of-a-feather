package com.swift.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FavStudentListActivity extends AppCompatActivity {
    private AppDatabase db;
    private List<Student> favoriteStudentList;
    private UUID sessionId;
    private Future future;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private RecyclerView favoriteStudentRecyclerView;
    private RecyclerView.LayoutManager favoriteStudentLayoutManager;
    private StudentViewAdapter studentViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_favorite_students);
        Intent intent = getIntent();

        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());
            favoriteStudentList = db.studentDao().getAllFavoritedStudents();

            runOnUiThread(() -> {
                favoriteStudentRecyclerView = findViewById(R.id.persons_view);

                favoriteStudentLayoutManager = new LinearLayoutManager(this);
                favoriteStudentRecyclerView.setLayoutManager(favoriteStudentLayoutManager);

                studentViewAdapter = new StudentViewAdapter(favoriteStudentList);
                favoriteStudentRecyclerView.setAdapter(studentViewAdapter);
            });
        });
    }

    public void onBackClicked(View view) {
        finish();
    }
}
