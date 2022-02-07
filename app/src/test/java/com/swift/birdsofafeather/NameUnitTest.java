package com.swift.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NameUnitTest {
    @Rule
    public ActivityScenarioRule<NameActivity> scenarioRule = new ActivityScenarioRule<>(NameActivity.class);

    @Test
    public void test_saveName() {
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        String testFirstName = "Eugene";

        scenario.onActivity(activity -> {
            activity.saveName(testFirstName);

            SharedPreferences preferences = Utils.getSharedPreferences(activity);
            assertEquals(testFirstName, preferences.getString("first_name", null));
        });
    }

    @Test
    public void test_saveName_empty() {
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        String testFirstName = "";

        scenario.onActivity(activity -> {
            activity.saveName(testFirstName);

            SharedPreferences preferences = Utils.getSharedPreferences(activity);
            assertEquals(testFirstName, preferences.getString("first_name", null));
        });
    }
}
