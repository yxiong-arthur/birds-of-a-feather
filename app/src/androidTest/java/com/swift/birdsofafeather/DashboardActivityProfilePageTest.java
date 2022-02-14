package com.swift.birdsofafeather;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DashboardActivityProfilePageTest {

    @Rule
    public ActivityTestRule<BluetoothCheckActivity> mActivityTestRule = new ActivityTestRule<>(BluetoothCheckActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.BLUETOOTH_SCAN",
                    "android.permission.BLUETOOTH_CONNECT");

    @Test
    public void dashboardActivityProfilePageTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.first_name_textview),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.first_name_textview),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Eugene"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.submit_name_button), withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.photoUploadURL),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText3.perform(longClick());

        ViewInteraction linearLayout = onView(
                allOf(withContentDescription("Paste"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                0),
                        isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.photoUploadURL),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("https://images.unsplash.com/photo-1604675223954-b1aabd668078?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8b3JhbmdlJTIwY2F0fGVufDB8fDB8fA%3D%3D&w=1000&q=80"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.loadPhotoButton), withText("Load"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.submitPhotoButton), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.subject_textview),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("cse"), closeSoftKeyboard());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.courseNumber_textview),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("110"), closeSoftKeyboard());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.enter_button), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.courseNumber_textview), withText("110"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("105"));

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.courseNumber_textview), withText("105"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText8.perform(closeSoftKeyboard());

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.enter_button), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.courseNumber_textview), withText("105"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText9.perform(replaceText("101"));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.courseNumber_textview), withText("101"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText10.perform(closeSoftKeyboard());

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.enter_button), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction materialButton7 = onView(
                allOf(withId(R.id.done_button), withText("done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton7.perform(click());

        ViewInteraction materialButton8 = onView(
                allOf(withId(R.id.add_students_button), withText("Add Students"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton8.perform(click());

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.student_info),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText11.perform(scrollTo(), replaceText("Tom,,,\n"), closeSoftKeyboard());

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.student_info), withText("Tom,,,\n"),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText12.perform(scrollTo(), longClick());

        ViewInteraction linearLayout2 = onView(
                allOf(withContentDescription("Paste"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                0),
                        isDisplayed()));
        linearLayout2.perform(click());

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.student_info), withText("Tom,,,\n"),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText13.perform(scrollTo(), replaceText("Tom,,,\nhttps://images.unsplash.com/photo-1604675223954-b1aabd668078?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8b3JhbmdlJTIwY2F0fGVufDB8fDB8fA%3D%3D&w=1000&q=80,,,\n2022,FA,CSE,110\n2022,FA,CSE,105"));

        ViewInteraction appCompatEditText14 = onView(
                allOf(withId(R.id.student_info), withText("Tom,,,\nhttps://images.unsplash.com/photo-1604675223954-b1aabd668078?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8b3JhbmdlJTIwY2F0fGVufDB8fDB8fA%3D%3D&w=1000&q=80,,,\n2022,FA,CSE,110\n2022,FA,CSE,105"),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText14.perform(closeSoftKeyboard());

        ViewInteraction materialButton9 = onView(
                allOf(withId(R.id.add_student_button), withText("Add Student"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton9.perform(click());

        ViewInteraction materialButton10 = onView(
                allOf(withId(R.id.back_button), withText("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton10.perform(click());

        ViewInteraction materialButton11 = onView(
                allOf(withId(R.id.add_students_button), withText("Add Students"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton11.perform(click());

        ViewInteraction appCompatEditText15 = onView(
                allOf(withId(R.id.student_info),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText15.perform(scrollTo(), replaceText("Jerry,,,\n"), closeSoftKeyboard());

        ViewInteraction appCompatEditText16 = onView(
                allOf(withId(R.id.student_info), withText("Jerry,,,\n"),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText16.perform(scrollTo(), longClick());

        ViewInteraction linearLayout3 = onView(
                allOf(withContentDescription("Paste"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                0),
                        isDisplayed()));
        linearLayout3.perform(click());

        ViewInteraction appCompatEditText17 = onView(
                allOf(withId(R.id.student_info), withText("Jerry,,,\n"),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText17.perform(scrollTo(), replaceText("Jerry,,,\nhttps://t3.ftcdn.net/jpg/03/04/27/12/360_F_304271210_ikONTzlfio9cnS3K9dJtKSGBrajGFpZo.jpg,,,\n2022,FA,CSE,110\n2022,FA,CSE,101\n2022,FA,CSE,105"));

        ViewInteraction appCompatEditText18 = onView(
                allOf(withId(R.id.student_info), withText("Jerry,,,\nhttps://t3.ftcdn.net/jpg/03/04/27/12/360_F_304271210_ikONTzlfio9cnS3K9dJtKSGBrajGFpZo.jpg,,,\n2022,FA,CSE,110\n2022,FA,CSE,101\n2022,FA,CSE,105"),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText18.perform(closeSoftKeyboard());

        ViewInteraction materialButton12 = onView(
                allOf(withId(R.id.add_student_button), withText("Add Student"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton12.perform(click());

        ViewInteraction materialButton13 = onView(
                allOf(withId(R.id.back_button), withText("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton13.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.persons_view),
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction materialButton14 = onView(
                allOf(withId(R.id.backhome), withText("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton14.perform(click());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.persons_view),
                        childAtPosition(
                                withClassName(is("android.widget.ScrollView")),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(1, click()));

        ViewInteraction materialButton15 = onView(
                allOf(withId(R.id.backhome), withText("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        materialButton15.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
