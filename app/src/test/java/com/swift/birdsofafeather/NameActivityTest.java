package com.swift.birdsofafeather;
import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


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
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

@RunWith(AndroidJUnit4.class)
public class NameActivityTest {
    @Rule
    public ActivityScenarioRule<NameActivity> scenarioRule = new ActivityScenarioRule<>(NameActivity.class);

    @Test
    public void test_save_profile() {
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            TextView firstNameTextView = (TextView) activity.findViewById(R.id.first_name_textview);
            firstNameTextView.setText("Eugene");
            Button confirmButton = activity.findViewById(R.id.submit_name_button);
            confirmButton.performClick();

            SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
            assertEquals("Eugene", preferences.getString("first_name", null));
        });
    }

    @Test
    public void test_save_profile_empty_string() {
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            TextView firstNameTextView = (TextView) activity.findViewById(R.id.first_name_textview);
            firstNameTextView.setText("");
            Button confirmButton = activity.findViewById(R.id.submit_name_button);
            confirmButton.performClick();

            SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
            assertNull(preferences.getString("first_name", null));
        });
    }
}



/*
public class ExampleRobolectricTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void test_starts_with_no_result() {
        // Create a "scenario" to move through the activity lifecycle.
        // https://developer.android.com/guide/components/activities/activity-lifecycle
        ActivityScenario<MainActivity> scenario = scenarioRule.getScenario();

        // Make sure the activity is in the created state (so onCreated is called).
        scenario.moveToState(Lifecycle.State.CREATED);

        // When it's ready, we're ready to test inside this lambda (anonymous inline function).
        scenario.onActivity(activity -> {
            // No calculations have been run yet, so there shouldn't be a result!
            assertFalse(activity.hasResult());
        });
    }

    @Test
    public void test_adds_numbers_and_not_something_else() {
        // This is an INTEGRATION test, as we're testing multiple units!
        // This test SHOULD fail. You need to fix it as an exercise!

        ActivityScenario<MainActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            EditText num1View = activity.findViewById(R.id.number1);
            EditText num2View = activity.findViewById(R.id.number2);
            Button equalsButton = activity.findViewById(R.id.equals_button);
            TextView resultView = activity.findViewById(R.id.result);

            num1View.setText("13");
            num2View.setText("42");
            equalsButton.performClick();
            int result = Utils.toIntNullsafe(resultView.getText().toString());
            assertEquals(55, result);
        });
    }
}
 */