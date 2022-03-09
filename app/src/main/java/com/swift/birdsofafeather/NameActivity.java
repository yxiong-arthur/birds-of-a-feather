package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class NameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        SharedPreferences preferences = Utils.getSharedPreferences(this);
        if(preferences.contains("first_name")){
            Intent intent = new Intent(this, PhotoUpload.class);
            startActivity(intent);
        }else{
            setContentView(R.layout.activity_name);
            String firstName = getUserDisplayName(this);
            TextView firstNameTextView = findViewById(R.id.first_name_textview);
            if(!Utils.isEmpty(firstName)) firstNameTextView.setText(firstName);
        }
    }

    public void onConfirmClicked(View view){
        TextView firstNameTextView = findViewById(R.id.first_name_textview);
        String enteredName = firstNameTextView.getText().toString();

        if(Utils.isEmpty(enteredName)) {
           Utils.showAlert(this, "Name can't be empty");
           return;
        }

        saveName(enteredName);

        Intent photoIntent = new Intent(this, PhotoUpload.class);
        startActivity(photoIntent);
    }

    protected void saveName(String enteredName){
        SharedPreferences preferences = Utils.getSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("first_name", enteredName);
        editor.apply();
    }

    // adapted from: https://gist.github.com/ohjongin/7986386
    @SuppressLint("Range")
    protected String getUserDisplayName(Context context) {
        if (!Utils.hasPermission(context, "android.permission.READ_PROFILE")) return "";

        // Sets the columns to retrieve for the user profile
        String[] projections = new String[] {
                ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
                ContactsContract.Profile.IS_USER_PROFILE
        };

        // Retrieves the profile from the Contacts Provider
        Cursor c = context.getContentResolver().query(
                ContactsContract.Profile.CONTENT_URI,
                projections,
                null,
                null,
                null
        );

        String name = "";

        if (c != null) {
            int count = c.getCount();
            c.moveToFirst();
            do {
                int is_user_profile = c.getInt(c.getColumnIndex(ContactsContract.Profile.IS_USER_PROFILE));
                if (count == 1 || (count > 1 && is_user_profile == 1)) {
                    name = c.getString(c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME_PRIMARY));
                    break;
                }
            } while (c.moveToNext());
            c.close();
        }

        return name;
    }

    public void onGoBackHome(View view) {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}