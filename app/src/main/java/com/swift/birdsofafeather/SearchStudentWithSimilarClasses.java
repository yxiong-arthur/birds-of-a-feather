package com.swift.birdsofafeather;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SearchStudentWithSimilarClasses extends AppCompatActivity {
    private AppDatabase db;
    private UUID studentId;
    private StudentWithClasses myself;
    private Set<Class> myClasses;
    private RecyclerView studentsRecyclerView;
    private RecyclerView.LayoutManager studentsLayoutManager;
    private StudentViewAdapter studentsViewAdapter;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_with_similar_classes);

        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());

            SharedPreferences preferences = Utils.getSharedPreferences(this);
            String UUIDString = preferences.getString("student_id", "");
            studentId = UUID.fromString(UUIDString);

            myself = db.studentWithClassesDao().getStudent(studentId);
            myClasses = myself.getClasses();
            List<Student> myClassmates = findPriorClassmates();

            for(Student classmate:myClassmates){
                Set<Class> mateClasses = db.studentWithClassesDao().getStudent(classmate.studentId).getClasses();
                mateClasses.retainAll(myClasses);
                int count = mateClasses.size();
                classmate.setCount(count);
            }


            runOnUiThread(() -> {
                // Set up the recycler view to show our database contents
                studentsRecyclerView = findViewById(R.id.persons_view);

                studentsLayoutManager = new LinearLayoutManager(this);
                studentsRecyclerView.setLayoutManager(studentsLayoutManager);

                studentsViewAdapter = new StudentViewAdapter(myClassmates);
                studentsRecyclerView.setAdapter(studentsViewAdapter);
            });
        });
    }

    protected List<Student> findPriorClassmates() {
        List<StudentWithClasses> studentList = db.studentWithClassesDao().getAllStudents();
        studentList.remove(myself);

        List<Student> commonClassmates = new ArrayList<>();
        List<StudentWithClasses> tempCommon = new ArrayList<>();

        for (StudentWithClasses classmate : studentList) {

            if(countSimilarClasses(classmate) > 0) {
                if(commonClassmates.size() == 0) {
                    commonClassmates.add(classmate.getStudent());
                    tempCommon.add(classmate);
                } else if (countSimilarClasses(classmate) < countSimilarClasses(tempCommon.get(tempCommon.size()-1))) {
                    commonClassmates.add(classmate.getStudent());
                    tempCommon.add(classmate);
                } else{
                    for(int i=0; i<commonClassmates.size(); i++) {
                        if(countSimilarClasses(classmate) >= countSimilarClasses(tempCommon.get(i))) {
                            commonClassmates.add(i,classmate.getStudent());
                            tempCommon.add(i,classmate);
                            break;
                        }
                    }
                }
            }
        }

        return commonClassmates;
    }

    protected int countSimilarClasses(StudentWithClasses classmate){
        Set<Class> mateClasses = classmate.getClasses();

        mateClasses.retainAll(myClasses);

        return mateClasses.size();
    }

    public int calculatePosition (Student classmate) {
        myself = db.studentWithClassesDao().getStudent(studentId);
        myClasses = myself.getClasses();
        List<Student> myClassmates = findPriorClassmates();
        for(int i=0; i<myClassmates.size(); i++) {
            if(myClassmates.get(i).getId().equals(classmate.getId())){
                return i;
            }
        }
        return -1;
    }

    //for milestone2's turn-off button
    public void onToggle(View view) {
        this.future.cancel(true);
        //finish();
    }

    //public void
    public void onAddStudentsClicked(View view){
        Intent addStudentsIntent = new Intent(this, AddStudentActivity.class);
        startActivity(addStudentsIntent);
    }
}