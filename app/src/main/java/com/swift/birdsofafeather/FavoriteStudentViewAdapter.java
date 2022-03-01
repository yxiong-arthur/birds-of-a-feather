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

import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.FavoriteStudent;
import com.swift.birdsofafeather.model.db.Student;
import com.swift.birdsofafeather.model.db.StudentWithClasses;

import java.util.List;
import java.util.Set;

public class FavoriteStudentViewAdapter extends RecyclerView.Adapter<FavoriteStudentViewAdapter.ViewHolder> {
    private final List<FavoriteStudent> favoriteStudents;

    public FavoriteStudentViewAdapter(List<FavoriteStudent> favoriteStudents) {
        super();
        this.favoriteStudents = favoriteStudents;
    }

    @NonNull
    @Override
    public FavoriteStudentViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.display_favorite_students, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteStudentViewAdapter.ViewHolder holder, int position) {
        holder.setStudent(favoriteStudents.get(position));
    }

    @Override
    public int getItemCount() {
        return this.favoriteStudents.size();
    }

    public void addStudent(int index,FavoriteStudent student) {
        this.favoriteStudents.add(index,student);
        this.notifyItemInserted(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView studentNameView;
        private final ImageView thumbnail;
        private final TextView number;
        private FavoriteStudent favoriteStudent;
        private Student student;

        ViewHolder(View itemView) {
            super(itemView);
            this.studentNameView = itemView.findViewById(R.id.student_row_name);
            this.thumbnail = itemView.findViewById(R.id.thumbnail);
            this.number = itemView.findViewById(R.id.number_of_classes);

            itemView.setOnClickListener(this);
        }


        public void setStudent(FavoriteStudent favoriteStudent) {
            this.favoriteStudent = favoriteStudent;
            this.studentNameView.setText(student.getName());
            this.thumbnail.setImageBitmap(student.getPicture());
            this.number.setText(String.valueOf(this.student.getCount()));

            //set the student
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, FavStudentListActivity.class);
            intent.putExtra("session_id", this.favoriteStudent.getSessionId().toString());
            context.startActivity(intent);
        }
    }
}
