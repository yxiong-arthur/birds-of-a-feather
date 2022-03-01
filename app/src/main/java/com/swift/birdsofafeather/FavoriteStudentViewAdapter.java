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
    private final List<Student> favoriteStudents;


    public FavoriteStudentViewAdapter(List<Student> favoriteStudents) {
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

    public void addStudent(int index, Student student) {
        this.favoriteStudents.add(index,student);
        this.notifyItemInserted(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView studentNameView;
        private final ImageView thumbnail;
        private Student favoriteStudent;

        ViewHolder(View itemView) {
            super(itemView);
            this.studentNameView = itemView.findViewById(R.id.student_row_name);
            this.thumbnail = itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(this);
        }


        public void setStudent(Student favoriteStudent) {
            this.favoriteStudent = favoriteStudent;
            this.studentNameView.setText(favoriteStudent.getName());
            this.thumbnail.setImageBitmap(favoriteStudent.getPicture());

        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, FavStudentListActivity.class);
            //intent.putExtra("session_id", this.favoriteStudent.getSessionId().toString());
            context.startActivity(intent);
        }
    }
}
