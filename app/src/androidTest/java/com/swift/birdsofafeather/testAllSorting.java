package com.swift.birdsofafeather;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
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
public class testAllSorting {

    @Rule
    public ActivityTestRule<BluetoothCheckActivity> mActivityTestRule = new ActivityTestRule<>(BluetoothCheckActivity.class);

    @Test
    public void testOverallNew() {
        ViewInteraction materialButton10 = onView(
                allOf(withId(R.id.toggle_search_button), withText("Start Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton10.perform(click());

        ViewInteraction materialButton11 = onView(
                allOf(withId(android.R.id.button1), withText("New Session"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                0)));
        materialButton11.perform(scrollTo(), click());

        ViewInteraction materialButton12 = onView(
                allOf(withId(R.id.add_students_button), withText("Add Students"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton12.perform(click());

        ViewInteraction appCompatEditText17 = onView(
                allOf(withId(R.id.student_info),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText17.perform(scrollTo(), click());

        ViewInteraction appCompatEditText18 = onView(
                allOf(withId(R.id.student_info),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText18.perform(scrollTo(), replaceText("Darren,,,,\nhttps://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png,,,,\n2022,WI,CSE,110,Large\n2022,WI,CSE,105,Large\n2022,WI,CSE,101,large"), closeSoftKeyboard());

        ViewInteraction appCompatEditText19 = onView(
                allOf(withId(R.id.student_info), withText("Darren,,,,\nhttps://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png,,,,\n2022,WI,CSE,110,Large\n2022,WI,CSE,105,Large\n2022,WI,CSE,101,large"),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText19.perform(scrollTo(), click());

        ViewInteraction appCompatEditText20 = onView(
                allOf(withId(R.id.student_info), withText("Darren,,,,\nhttps://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png,,,,\n2022,WI,CSE,110,Large\n2022,WI,CSE,105,Large\n2022,WI,CSE,101,large"),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText20.perform(scrollTo(), click());

        ViewInteraction appCompatEditText21 = onView(
                allOf(withId(R.id.student_info), withText("Darren,,,,\nhttps://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png,,,,\n2022,WI,CSE,110,Large\n2022,WI,CSE,105,Large\n2022,WI,CSE,101,large"),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText21.perform(scrollTo(), replaceText("a4ca50b6-941b-11ec-b909-0242ac120002,,,,\nDarren,,,,\nhttps://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png,,,,\n2022,WI,CSE,110,Large\n2022,WI,CSE,105,Large\n2022,WI,CSE,101,large"));

        ViewInteraction appCompatEditText22 = onView(
                allOf(withId(R.id.student_info), withText("a4ca50b6-941b-11ec-b909-0242ac120002,,,,\nDarren,,,,\nhttps://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png,,,,\n2022,WI,CSE,110,Large\n2022,WI,CSE,105,Large\n2022,WI,CSE,101,large"),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText22.perform(closeSoftKeyboard());

        ViewInteraction materialButton13 = onView(
                allOf(withId(R.id.add_student_button), withText("Add Student"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton13.perform(click());

        ViewInteraction materialButton14 = onView(
                allOf(withId(R.id.back_button), withText("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton14.perform(click());

        ViewInteraction materialButton15 = onView(
                allOf(withId(R.id.add_students_button), withText("Add Students"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton15.perform(click());

        ViewInteraction appCompatEditText23 = onView(
                allOf(withId(R.id.student_info),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText23.perform(scrollTo(), replaceText("a5ca50bd-941b-11eb-b909-0242ac120002,,,,\nArthur,,,,\ns,,,,\n2022,WI,CSE,110,Large\n2021,WI,CSE,20,Medium"), closeSoftKeyboard());

        ViewInteraction materialButton16 = onView(
                allOf(withId(R.id.add_student_button), withText("Add Student"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton16.perform(click());

        ViewInteraction materialButton17 = onView(
                allOf(withId(R.id.back_button), withText("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton17.perform(click());

        ViewInteraction materialButton18 = onView(
                allOf(withId(R.id.add_students_button), withText("Add Students"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton18.perform(click());

        ViewInteraction appCompatEditText24 = onView(
                allOf(withId(R.id.student_info),
                        childAtPosition(
                                allOf(withId(R.id.scrollView2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0)));
        appCompatEditText24.perform(scrollTo(), replaceText("a6ca50b6-941b-11ec-b909-0242ac120002,,,,\nZiyan,,,,\ns,,,,\n2022,WI,CSE,110,Large\n2022,WI,CSE,105,Large"), closeSoftKeyboard());

        ViewInteraction materialButton19 = onView(
                allOf(withId(R.id.add_student_button), withText("Add Student"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton19.perform(click());

        ViewInteraction materialButton20 = onView(
                allOf(withId(R.id.back_button), withText("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton20.perform(click());

        ViewInteraction appCompatSpinner8 = onView(
                allOf(withId(R.id.filter_select),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatSpinner8.perform(click());

        DataInteraction appCompatCheckedTextView5 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView5.perform(click());

        ViewInteraction appCompatSpinner9 = onView(
                allOf(withId(R.id.filter_select),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatSpinner9.perform(click());

        DataInteraction appCompatCheckedTextView6 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatCheckedTextView6.perform(click());

        ViewInteraction appCompatSpinner10 = onView(
                allOf(withId(R.id.filter_select),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatSpinner10.perform(click());

        DataInteraction appCompatCheckedTextView7 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(3);
        appCompatCheckedTextView7.perform(click());

        ViewInteraction appCompatSpinner11 = onView(
                allOf(withId(R.id.filter_select),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatSpinner11.perform(click());

        DataInteraction appCompatCheckedTextView8 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatCheckedTextView8.perform(click());
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
