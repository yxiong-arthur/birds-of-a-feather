package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Session;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ViewSessionActivity extends AppCompatActivity {
    private AppDatabase db;
    private UUID studentId;
    private RecyclerView sessionRecyclerView;
    private RecyclerView.LayoutManager sessionLayoutManager;
    private SessionViewAdapter sessionViewAdapter;
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_session);

        db = AppDatabase.singleton(getApplicationContext());

        SharedPreferences preferences = Utils.getSharedPreferences(this);
        String UUIDString = preferences.getString("student_id", "");
        studentId = UUID.fromString(UUIDString);

        List<Session> mySessions = db.sessionDao().getAllSessions();

        this.future = backgroundThreadExecutor.submit(() -> runOnUiThread(() -> {
            // Set up the recycler view to show our database contents
            sessionRecyclerView = findViewById(R.id.persons_view);

            sessionLayoutManager = new LinearLayoutManager(this);
            sessionRecyclerView.setLayoutManager(sessionLayoutManager);

            sessionViewAdapter = new SessionViewAdapter(mySessions);
            sessionRecyclerView.setAdapter(sessionViewAdapter);

        }));
    }

    public void onGoBackClicked(View view) {
        finish();
    }
}