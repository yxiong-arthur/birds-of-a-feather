package com.swift.birdsofafeather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Session;
import com.swift.birdsofafeather.model.db.SessionStudent;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CourseDashboard  extends AppCompatActivity {
    private static final String TAG = "CourseDashboard";
    private AppDatabase db;
    private UUID studentId;
    private StudentWithClasses myself;
    private Set<Class> myClasses;
    private RecyclerView courseRecyclerView;
    private RecyclerView.LayoutManager courseLayoutManager;
    private CourseViewAdapter courseViewAdapter;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());

            SharedPreferences preferences = Utils.getSharedPreferences(this);
            String UUIDString = preferences.getString("student_id", "");
            studentId = UUID.fromString(UUIDString);

            List<Session> mySessions = db.sessionDao().getAllSessions();

            runOnUiThread(() -> {
                // Set up the recycler view to show our database contents
                courseRecyclerView = findViewById(R.id.persons_view);

                courseLayoutManager = new LinearLayoutManager(this);
                courseRecyclerView.setLayoutManager(courseLayoutManager);

                courseViewAdapter = new CourseViewAdapter(mySessions);
                courseRecyclerView.setAdapter(courseViewAdapter);
            });
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Choose your session");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to continue from your existing session or start a new session?")
                .setCancelable(false)
                .setPositiveButton("New Session", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        UUID newSessionId = UUID.randomUUID();
                        Session newSession = new Session(newSessionId);
                        db.sessionDao().insert(newSession);

                        Log.d(TAG, "Session object name: " + newSession.getName());
                        Log.d(TAG, "Session database name: " + db.sessionDao().getName(newSessionId));

                        SessionStudent studentInSession = new SessionStudent(newSessionId, studentId);
                        db.sessionStudentDao().insert(studentInSession);

                        SharedPreferences preferences = Utils.getSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("current_session_id", newSessionId.toString());
                        editor.apply();

                        dialog.dismiss();

                        finish();
                    }
                }).setNegativeButton("Continue from existing session", null);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db.sessionDao().getAllSessions().size() == 0) {
                    Toast.makeText(getApplicationContext(), "You have no existing sessions!", Toast.LENGTH_SHORT).show();
                }
                else {
                    alertDialog.dismiss();
                }
            }
        });
    }
}
