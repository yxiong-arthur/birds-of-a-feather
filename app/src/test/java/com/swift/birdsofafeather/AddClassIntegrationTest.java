package com.swift.birdsofafeather;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.swift.birdsofafeather.model.db.AppDatabase;

@RunWith(AndroidJUnit4.class)
public class AddClassIntegrationTest {
    @Rule
    public ActivityScenarioRule<AddClassesActivity> scenarioRule = new ActivityScenarioRule<>(AddClassesActivity.class);

    @Test
    public void test_if_student_added() {
        ActivityScenario<AddClassesActivity> scenario = scenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            AppDatabase db = AppDatabase.singleton(activity.getApplicationContext());
            int count = db.studentDao().count();
            assertEquals(1, count);
        });
    }
}
