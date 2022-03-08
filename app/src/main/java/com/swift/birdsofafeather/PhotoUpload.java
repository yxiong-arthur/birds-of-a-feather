package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class PhotoUpload extends AppCompatActivity {
    private Bitmap picture;
    private String URLLink;

    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = Utils.getSharedPreferences(this);
        if(preferences.contains("image_data")){
            Intent intent = new Intent(this, SearchStudentWithSimilarClasses.class);
            startActivity(intent);
        }else{
            setContentView(R.layout.activity_photo_upload);
            submitButton = findViewById(R.id.submitPhotoButton);
            submitButton.setVisibility(View.INVISIBLE);
        }
    }

    public void onLoadClicked(View view) {
        EditText loadURL = (EditText) findViewById(R.id.photoUploadURL);;
        ImageView imageResult = findViewById(R.id.uploadedPhoto);


        URLLink = loadURL.getText().toString();
        imageResult.setImageBitmap(null);
        loadURL.setText("");

        if (URLLink.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter url", Toast.LENGTH_SHORT).show();
        } else {
            picture = Utils.urlToBitmap(this, URLLink);
            imageResult.setImageBitmap(picture);
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    public void onSubmitClicked(View view) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        SharedPreferences preferences = Utils.getSharedPreferences(PhotoUpload.this);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("image_data", encodedImage);
        edit.putString("image_url", URLLink);
        edit.apply();

        Intent addClassIntent = new Intent(PhotoUpload.this, AddClassesActivity.class);
        startActivity(addClassIntent);
    }
}