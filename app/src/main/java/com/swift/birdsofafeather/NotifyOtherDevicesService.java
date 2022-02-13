package com.swift.birdsofafeather;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.swift.birdsofafeather.model.db.AppDatabase;
import com.swift.birdsofafeather.model.db.Class;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotifyOtherDevicesService extends Service {
    private final ScheduledExecutorService executor;
    private AppDatabase db;
    private int frequency;

    public NotifyOtherDevicesService() {
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.db = AppDatabase.singleton(NotifyOtherDevicesService.this);
        this.frequency = Utils.getWriteFrequency();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executor.scheduleAtFixedRate(() -> {
            SharedPreferences preferences = Utils.getSharedPreferences(this.getApplicationContext());

            String studentUUIDString = preferences.getString("student_id", "default");
            String studentName = preferences.getString("first_name", "default");
            String photoURL = preferences.getString("image_url", "default");

            UUID studentUUID = UUID.fromString(studentUUIDString);
            List<Class> classes = db.classesDao().getForStudent(studentUUID);

            String encodedString = studentUUIDString + "," + studentName + "," + photoURL;

            for(Class c : classes) {
                encodedString += "," + c;
            }

            Message myStudentData = new Message(encodedString.getBytes(StandardCharsets.UTF_8));
            Nearby.getMessagesClient(this).publish(myStudentData);

        }, 0, frequency, TimeUnit.SECONDS);

        return super.onStartCommand(intent, flags, startId);
    }
}