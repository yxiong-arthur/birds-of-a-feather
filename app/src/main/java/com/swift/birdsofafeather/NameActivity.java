package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        String firstName = getUserDisplayName();
        TextView firstNameTextView = (TextView) findViewById(R.id.first_name_textview);
        if(!Utils.isEmpty(firstName)) firstNameTextView.setText(firstName);
    }

    protected void onConfirmClicked(View view){
        saveProfile();
        Intent pictureIntent = new Intent(this, PictureActivity.class);
        startActivity(pictureIntent);
    }

    public void saveProfile(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        TextView firstNameTextView = (TextView) findViewById(R.id.first_name_textview);

        editor.putString("first_name", firstNameTextView.getText().toString());

        editor.apply();
    }

    // unrequired method here for reference
//    public void loadProfile(){
//        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//
//        String name = preferences.getString("name", "Name");
//        String status = preferences.getString("status", "Status");
//
//        TextView nameTextView = (TextView) findViewById(R.id.name_textview);
//        TextView statusTextView = (TextView) findViewById(R.id.status_textview);
//
//        nameTextView.setText(name);
//        statusTextView.setText(status);
//    }

    // adapted from: https://gist.github.com/ohjongin/7986386
    @SuppressLint("Range")
    public String getUserDisplayName() {
        if (!Utils.hasPermission(this, "android.permission.READ_PROFILE")) return "";

        // Sets the columns to retrieve for the user profile
        String[] projections = new String[] {
                ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
                ContactsContract.Profile.IS_USER_PROFILE
        };

        // Retrieves the profile from the Contacts Provider
        Cursor c = this.getContentResolver().query(
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
                    name = c.getString(c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME));
                    break;
                }
            } while (c.moveToNext());
            c.close();
        }

        return name;
    }
}