package com.swift.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.FavoriteStudent;
import com.swift.birdsofafeather.model.db.Student;

import java.util.List;
import java.util.UUID;

public class FavStudentListActivity extends AppCompatActivity {
    private AppDatabase db;
    private List<FavoriteStudent> favoriteStudentsList;
    private UUID sessionId;

    private RecyclerView favStudentsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_favorite_students);

        Intent intent = getIntent();
        UUID sessionId = UUID.fromString(intent.getStringExtra("session_id"));

        db = AppDatabase.singleton(this);
        favoriteStudentsList = db.favoriteStudentDao().getStudentsBySession(sessionId);
    }
}
