package com.swift.birdsofafeather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
            TextView firstNameTextView = activity.findViewById(R.id.first_name_textview);
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
            TextView firstNameTextView = activity.findViewById(R.id.first_name_textview);
            firstNameTextView.setText(testFirstName);

            Button confirmButton = activity.findViewById(R.id.submit_name_button);
            confirmButton.performClick();

            SharedPreferences preferences = Utils.getSharedPreferences(activity);
            assertNull(preferences.getString("first_name", null));
        });
    }
}