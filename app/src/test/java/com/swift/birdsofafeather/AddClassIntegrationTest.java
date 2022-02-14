package com.swift.birdsofafeather;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.swift.birdsofafeather.model.db.AppDatabase;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class AddClassIntegrationTest {
    @Rule
    public ActivityScenarioRule<AddClassesActivity> scenarioRule = new ActivityScenarioRule<>(AddClassesActivity.class);

    @Before
    public void setup(){
        Context context = ApplicationProvider.getApplicationContext();

        SharedPreferences preferences = Utils.getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        Bitmap picture = Utils.createImage(4, 4, 4);

        editor.putString("first_name", "Bill");
        editor.putString("image_data", Utils.bitmapToString(picture));

        editor.apply();
    }

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
