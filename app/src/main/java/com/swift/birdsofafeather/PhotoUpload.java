package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;




public class PhotoUpload extends AppCompatActivity {

    EditText loadURL;
    Button saveButton, loadButton;
    ImageView imageResult;
    Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_photo_upload);

        saveButton = findViewById(R.id.savePhotoButton);
        loadButton = findViewById(R.id.loadPhotoButton);
        loadURL = (EditText) findViewById(R.id.photoUploadURL);
        imageResult = findViewById(R.id.uploadedPhoto);
        downloadButton = findViewById(R.id.downloadPhotoButton);
        ImageView downloadPhoto = findViewById(R.id.downloadPhoto);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageResult.buildDrawingCache();
                Bitmap bmap = imageResult.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();

                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                SharedPreferences shre = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor edit = shre.edit();
                edit.putString("image_data",encodedImage);
                edit.apply();
                downloadPhoto.setImageBitmap(null);
                downloadPhoto.setImageResource(0);
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
                }

                else {
                    Glide.with(PhotoUpload.this).load(URLLink).apply(new RequestOptions().centerCrop()).into(imageResult);
                }
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences shre = getPreferences(MODE_PRIVATE);
                String previouslyEncodedImage = shre.getString("image_data", "");

                if( !previouslyEncodedImage.equalsIgnoreCase("") ){
                    byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    downloadPhoto.setImageBitmap(bitmap);
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