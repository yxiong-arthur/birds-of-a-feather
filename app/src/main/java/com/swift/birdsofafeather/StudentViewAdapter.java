package com.swift.birdsofafeather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Student;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

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
        private final ImageButton favButton;
        private final ImageView waveIndicator;
        private Student student;
        private Context context;
        private AppDatabase db;

        ViewHolder(View itemView) {
            super(itemView);
            this.studentNameView = itemView.findViewById(R.id.student_row_name);
            this.thumbnail = itemView.findViewById(R.id.thumbnail);
            this.number = itemView.findViewById(R.id.number_of_classes);
            this.context = itemView.getContext();
            this.favButton = itemView.findViewById(R.id.favButton);
            this.waveIndicator = itemView.findViewById(R.id.waveIndicator);

            db = AppDatabase.singleton(context);

            itemView.setOnClickListener(this);
            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    student.toggleFavorited();
                    db.studentDao().updateFavorited(student.getId(), student.isFavorited());

                    if(student.isFavorited()) {
                        favButton.setBackgroundResource(R.drawable.ic_baseline_star_24);
                        Toast.makeText(context, "Favorited " + student.getName(), Toast.LENGTH_SHORT).show();
                    } else {
                        favButton.setBackgroundResource(R.drawable.ic_baseline_star_gray_24);
                        Toast.makeText(context, "Unfavorited " + student.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

//            //TODO idk what we should do with the button (should it disappear? turn solid? Also when should we make it show up?
//            //TODO we don't want to add it to the list if it already exists (so make it unclickable)
//            //TODO DELETE EVERYTHIGN BELOW IM CONFUSED IF THE OTHER STUDENT NEEDS TO KNOW IF THEY GOT WAVED BACK
//            //TODO Darren: it shouldn't *do* anything
//            waveButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    SharedPreferences preferences = Utils.getSharedPreferences(context);
//                    String studentUUIDString = preferences.getString("student_id", "default");
//                    UUID studentUUID = UUID.fromString(studentUUIDString);
//                    Student myStudent = db.studentDao().getStudent(studentUUID);
//                    myStudent.classmatesWavedToList.add(student);
//                    waveButton.setEnabled(false);
//                    waveButton.setVisibility(View.GONE);
//                }
//            });
        }

        public void setStudent(Student student) {
            this.student = student;
            this.studentNameView.setText(student.getName());
            this.thumbnail.setImageBitmap(student.getPicture());
            this.number.setText(String.valueOf(this.student.getClassScore()));

            if(student.isFavorited()) {
                this.favButton.setBackgroundResource(R.drawable.ic_baseline_star_24);
            }
            if(student.isWavedFrom()) {
                this.waveIndicator.setVisibility(View.VISIBLE);
            }
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
