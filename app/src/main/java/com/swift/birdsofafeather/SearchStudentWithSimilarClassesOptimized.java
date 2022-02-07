package com.swift.birdsofafeather;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchStudentWithSimilarClassesOptimized extends AppCompatActivity {
    private AppDatabase db;
    private int studentId = 1;
    private StudentWithClasses myself;
    private Set<Class> myClasses;
    private RecyclerView studentsRecyclerView;
    private RecyclerView.LayoutManager studentsLayoutManager;
    private StudentViewAdapter studentsViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_with_similar_classes);

        db = AppDatabase.singleton(getApplicationContext());

        myself = db.studentWithClassesDao().getStudent(studentId);
        myClasses = myself.getClasses();

        List<Student> myClassmates = findPriorClassmates();

        // Set up the recycler view to show our database contents
        studentsRecyclerView = findViewById(R.id.persons_view);

        studentsLayoutManager = new LinearLayoutManager(this);
        studentsRecyclerView.setLayoutManager(studentsLayoutManager);

        studentsViewAdapter = new StudentViewAdapter(myClassmates);
        studentsRecyclerView.setAdapter(studentsViewAdapter);
    }

    private List<Student> findPriorClassmates() {
        List<StudentWithClasses> studentList = db.studentWithClassesDao().getAllStudents();
        studentList.remove(myself);

        List<Student> commonClassmates = new ArrayList<>();

        for (StudentWithClasses classmate : studentList) {
            if(countSimilarClasses(classmate) > 0) {
                commonClassmates.add(classmate.getStudent());
            }
        }

        return commonClassmates;
    }

    private int countSimilarClasses(StudentWithClasses classmate){
        Set<Class> mateClasses = classmate.getClasses();

        mateClasses.retainAll(myClasses);

        return mateClasses.size();
    }
}