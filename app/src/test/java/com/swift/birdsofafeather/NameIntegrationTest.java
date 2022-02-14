package com.swift.birdsofafeather;
import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class NameIntegrationTest {
    @Rule
    public ActivityScenarioRule<NameActivity> scenarioRule = new ActivityScenarioRule<>(NameActivity.class);

    @Test
    public void test_save_profile() {
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        String testFirstName = "Eugene";

        scenario.onActivity(activity -> {
            TextView firstNameTextView = (TextView) activity.findViewById(R.id.first_name_textview);
            firstNameTextView.setText(testFirstName);

            Button confirmButton = activity.findViewById(R.id.submit_name_button);
            confirmButton.performClick();

            SharedPreferences preferences = Utils.getSharedPreferences(activity);
            assertEquals(testFirstName, preferences.getString("first_name", null));
        });
    }

    @Test
    public void test_save_profile_empty_string() {
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        String testFirstName = "";

        scenario.onActivity(activity -> {
            TextView firstNameTextView = (TextView) activity.findViewById(R.id.first_name_textview);
            firstNameTextView.setText(testFirstName);

            Button confirmButton = activity.findViewById(R.id.submit_name_button);
            confirmButton.performClick();

            SharedPreferences preferences = Utils.getSharedPreferences(activity);
            assertEquals(null, preferences.getString("first_name", null));
        });
    }
}