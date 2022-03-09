package com.swift.birdsofafeather;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swift.birdsofafeather.model.db.Class;

import java.util.List;

public class ClassViewAdapter extends RecyclerView.Adapter<ClassViewAdapter.ViewHolder> {
    private final List<Class> commonC;

    public ClassViewAdapter(List<Class> classes) {
        super();
        this.commonC = classes;
    }

    @NonNull
    @Override
    public ClassViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.display_student_commonclasses, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewAdapter.ViewHolder holder, int position) {
        holder.setClass(commonC.get(position));
    }

    @Override
    public int getItemCount() {
        return this.commonC.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView classView;
        private Class aClass;

        ViewHolder(View itemView) {
            super(itemView);
            this.classView = itemView.findViewById(R.id.student_class);
            //itemView.setOnClickListener(this);
        }

        public void setClass(Class aClass) {
            this.aClass = aClass;
            String result = aClass.toString().replaceAll(",", " ");
            this.classView.setText(result);
        }
    }
}

