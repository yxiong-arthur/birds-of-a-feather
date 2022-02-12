package com.swift.birdsofafeather;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.swift.birdsofafeather.model.db.Student;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class NearbyStudentsActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothActivity";
    private MessageListener messageListener;
    private Message myStudentData;
    private Message myStudentData2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MessageListener realListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                Log.d(TAG, "Found message: " + new String (message.getContent()));
            }

            @Override
            public void onLost(@NonNull Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
            }
        };

        SharedPreferences preferences = Utils.getSharedPreferences(NearbyStudentsActivity.this);
        String photoString = preferences.getString("image_data", "default");

        // student object for testing
        Student student = new Student(UUID.randomUUID(),"travis", null);

        // TODO: delete after testing
        String encodedString = student.toString() + "," + photoString;

        myStudentData = new Message(encodedString.getBytes(StandardCharsets.UTF_8));
        //myStudentData2 = new Message("hello world".getBytes(StandardCharsets.UTF_8));

        this.messageListener = new FakedMessageListener(realListener, 5, encodedString);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Nearby.getMessagesClient(this).subscribe(messageListener);
        Nearby.getMessagesClient(this).publish(myStudentData);
        //Nearby.getMessagesClient(this).publish(myStudentData2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Nearby.getMessagesClient(this).unsubscribe(messageListener);
        Nearby.getMessagesClient(this).unpublish(myStudentData);
        //Nearby.getMessagesClient(this).unpublish(myStudentData2);
    }
}

