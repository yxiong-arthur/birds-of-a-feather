package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.swift.birdsofafeather.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.InputStream;

public class PhotoUpload extends AppCompatActivity {

    EditText loadURL;
    Button saveButton, loadButton;
    ImageView imageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_photo_upload);

        saveButton = findViewById(R.id.savePhotoButton);
        loadButton = findViewById(R.id.loadPhotoButton);
        loadURL = (EditText) findViewById(R.id.photoUploadURL);
        imageResult = findViewById(R.id.uploadedPhoto);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String URLlink = loadURL.getText().toString();
                if (URLlink.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter url", Toast.LENGTH_SHORT).show();
                }
                else {
                    LoadImage image = new LoadImage(imageResult);
                }
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String URLLink = loadURL.getText().toString();
                loadURL.setText("");
                imageResult.setImageBitmap(null);

                if(URLLink.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter url", Toast.LENGTH_SHORT).show();
                }else {
                    Glide.with(PhotoUpload.this).load(URLLink).into(imageResult );
                }
            }
        });
    }


    private class LoadImage extends AsyncTask<String,Void, Bitmap> {
        ImageView imageView;
        public LoadImage(ImageView imageView){
            this.imageView = imageView;
        }
        
        @Override
        protected Bitmap doInBackground(String... urls) {
            String link = urls[0]; //??
            Bitmap bitmap = null;
            try {
                InputStream inputS = new java.net.URL(link).openStream();
                bitmap = BitmapFactory.decodeStream(inputS);
            } catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}