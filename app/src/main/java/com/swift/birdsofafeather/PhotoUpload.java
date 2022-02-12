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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.io.ByteArrayOutputStream;


public class PhotoUpload extends AppCompatActivity {

    EditText loadURL;
    Button loadButton;
    Button SubmitButton;
    ImageView imageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        SubmitButton = findViewById(R.id.submitPhotoButton);
        loadButton = findViewById(R.id.loadPhotoButton);

        loadURL = (EditText) findViewById(R.id.photoUploadURL);
        imageResult = findViewById(R.id.uploadedPhoto);
    }

    public void onLoadClicked(View view) {
        String URLLink = loadURL.getText().toString();
        imageResult.setImageBitmap(null);
        loadURL.setText("");

        if (URLLink.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter url", Toast.LENGTH_SHORT).show();
        } else {
            Glide
                    .with(PhotoUpload.this)
                    .load(URLLink)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.logo)
                            .override(200, 200)
                            .centerCrop())
                    .into(imageResult);
        }
    }

    public void onSubmitClicked(View view) {
        imageResult.buildDrawingCache();
        Bitmap bmap = imageResult.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        SharedPreferences preferences = Utils.getSharedPreferences(PhotoUpload.this);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("image_data", encodedImage);
        edit.apply();

        Intent addClassIntent = new Intent(PhotoUpload.this, AddClassesActivity.class);
        startActivity(addClassIntent);

    }
}