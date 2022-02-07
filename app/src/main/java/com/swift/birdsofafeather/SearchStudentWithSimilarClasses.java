package com.swift.birdsofafeather;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.swift.birdsofafeather.model.db.Class;

import android.os.Bundle;

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchStudentWithSimilarClasses extends AppCompatActivity {
    protected RecyclerView studentsRecyclerView;
    protected RecyclerView.LayoutManager studentsLayoutManager;
    protected StudentViewAdapter studentsViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_with_similar_classes);

        AppDatabase db = AppDatabase.singleton(getApplicationContext());
        List<Student> myClassmates = findPriorClassmates(db);

        // Set up the recycler view to show our database contents
        studentsRecyclerView = findViewById(R.id.persons_view);

        studentsLayoutManager = new LinearLayoutManager(this);
        studentsRecyclerView.setLayoutManager(studentsLayoutManager);

        studentsViewAdapter = new StudentViewAdapter(myClassmates);
        studentsRecyclerView.setAdapter(studentsViewAdapter);



    }

    private List<Student> findPriorClassmates(AppDatabase db) {
        List<StudentWithClasses> studentList = db.studentWithClassesDao().getAllStudents();
        StudentWithClasses myself = db.studentWithClassesDao().getStudent(1);
        studentList.remove(myself);

        Set<Class> myClasses = myself.getClasses();

        List<Student> commonClassmates = new ArrayList<>();

        for (StudentWithClasses classmate : studentList) {
            Set<Class> classList = classmate.getClasses();
            boolean flag = false;

            for (Class mateClass : classList) {
                if (flag) {
                    break;
                }
                for (Class myClass : myClasses) {
                    if (
                            myClass.getYear() == mateClass.getYear() &&
                                    myClass.getQuarter().equals(mateClass.getQuarter()) &&
                                    myClass.getSubject().equals(mateClass.getSubject()) &&
                                    myClass.getCourseNumber().equals(mateClass.getCourseNumber())
                    ) {
                        commonClassmates.add(classmate.getStudent());
                        flag = true;
                        break;
                    }
                }
            }
        }
        return commonClassmates;
    }
}