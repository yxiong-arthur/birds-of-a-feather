package com.swift.birdsofafeather;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swift.birdsofafeather.model.db.Student;

import java.util.List;

public class StudentViewAdapter extends RecyclerView.Adapter<StudentViewAdapter.ViewHolder> {
    private final List<Student> students;

    public StudentViewAdapter(List<Student> students) {
        super();
        this.students = students;
    }

    @NonNull
    @Override
    public StudentViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.display_classmates, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewAdapter.ViewHolder holder, int position) {
        holder.setStudent(students.get(position));
    }

    @Override
    public int getItemCount() {
        return this.students.size();
    }

    public void addStudent(int index,Student student) {
        this.students.add(index,student);
        this.notifyItemInserted(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView studentNameView;
        private final ImageView thumbnail;
        private final TextView number;
        private Student student;

        ViewHolder(View itemView) {
            super(itemView);
            this.studentNameView = itemView.findViewById(R.id.student_row_name);
            this.thumbnail = itemView.findViewById(R.id.thumbnail);
            this.number = itemView.findViewById(R.id.number_of_classes);

            itemView.setOnClickListener(this);
        }

        public void setStudent(Student student) {
            this.student = student;
            this.studentNameView.setText(student.getName());
            this.thumbnail.setImageBitmap(student.getPicture());
            this.number.setText(String.valueOf(this.student.getClassScore()));
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, StudentProfileActivity.class);
            intent.putExtra("classmate_id", this.student.getId().toString());
            context.startActivity(intent);
        }
    }
}
