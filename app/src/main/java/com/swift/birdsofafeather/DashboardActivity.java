package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.swift.birdsofafeather.model.db.AppDatabase;

public class DashboardActivity extends AppCompatActivity {
    private TextView answerbox;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        answerbox = findViewById(R.id.answerbox_textview);
        db = AppDatabase.singleton(this.getApplicationContext());
    }

    public void onLaunchNameClicked(View view) {
        Intent intent = new Intent(this, NameActivity.class);
        startActivity(intent);
    }

    public void onLaunchPhotoClicked(View view) {
        Intent intent = new Intent(this, PhotoUpload.class);
        startActivity(intent);
    }

    public void onLaunchAddClassesClicked(View view) {
        Intent intent = new Intent(this, AddClassesActivity.class);
        startActivity(intent);
    }

    public void onLaunchSearchStudentsClicked(View view) {
        Intent intent = new Intent(this, SearchStudentWithSimilarClasses.class);
        startActivity(intent);
    }

    public void onClearDatabaseClicked(View view) {
        Utils.showAlert(this, "Cleared Database");
        db.classesDao().nukeTable();
        db.sessionStudentDao().nukeTable();
        db.sessionDao().nukeTable();
        db.studentDao().nukeTable();
    }

    public void onDatabaseSizeClicked(View view) {
        int numClasses = db.classesDao().count();
        int numStudents = db.studentDao().count();
        String answer = "# of Classes in db: " + numClasses + "\n# of Students in db: " + numStudents;
        answerbox.setText(answer);
    }

    public void onGetNameClicked(View view) {
        SharedPreferences preferences = Utils.getSharedPreferences(this);
        String name = preferences.getString("first_name", "err not found");
        String answer = "Name retrieved is: \"" + name + "\"";
        answerbox.setText(answer);
    }

    public void onBTFunctClicked(View view) {
        Intent intent = new Intent(this, BluetoothCheckActivity.class);
        startActivity(intent);
    }

    public void onFavStudentClicked(View view) {
        Intent intent = new Intent(this, FavStudentListActivity.class);
        startActivity(intent);
    }

}

